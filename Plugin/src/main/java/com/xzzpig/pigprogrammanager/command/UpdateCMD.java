package com.xzzpig.pigprogrammanager.command;

import com.github.xzzpig.pigutils.database.Database;
import com.github.xzzpig.pigutils.database.Table;
import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONException;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.CommandInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UpdateCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("update", "更新软件索引", null, null, null);

    @Override public String execute(Command cmd) {
        API.echo("开始更新软件索引");
        JSONArray repertory = API.getConfig().optJSONArray("repertory");
        if (repertory == null) {
            API.echo("!Repertory配置项为空");
            return null;
        }
        try {
            API.database.getTable("all_").drop();
            API.database.createTable("all_", API.TABLE_CONSTRUCT_ALL);
            API.database.getTable("aliasmap").drop();
            API.database.createTable("aliasmap", API.TABLE_CONSTRUCT_ALIASMAP);
            repertory.toList().stream().map(obj->obj + "").forEach(this::updateURL);
        } catch (SQLException e) {
            return "数据库打开/操作失败";
        }
        API.echo("软件索引更新完成");
        return null;
    }

    private void updateURL(String str) {
        API.echo("加载Repertory:", str);
        URL url;
        try {
            url = new URL(str);
        } catch (MalformedURLException e) {
            API.echo("读取Repertory", str, "失败");
            return;
        }
        Database db = API.database;
        Table table_all = db.getTable("all_");
        Table table_aliasmap = db.getTable("aliasmap");
        Map<String, Object> map_all = new HashMap<>();
        Map<String, Object> map_alias = new HashMap<>();
        try (InputStream inputStream = url.openStream(); Scanner scanner = new Scanner(inputStream)) {
            String s;
            String os_name = System.getProperty("os.name").toLowerCase();
            while (scanner.hasNextLine()) {
                s = scanner.nextLine();
                map_all.clear();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (!os_name.matches(jsonObject.optString("os", "").toLowerCase()))
                        continue;
                    String name = jsonObject.optString("name");
                    map_all.put("name", name);
                    String[] versions = jsonObject.optString("version").split("\\.");
                    map_all.put("version_0", Integer.parseInt(versions[0]));
                    map_all.put("version_1", Integer.parseInt(versions[1]));
                    map_all.put("version_2", Integer.parseInt(versions[2]));
                    map_all.put("depends", jsonObject.optString("depends", "[]"));
                    map_all.put("url", jsonObject.optString("url"));
                    table_all.insert(map_all);

                    map_alias.put("name", name);
                    map_alias.put("alias", name);

                    table_aliasmap.insert(map_alias);

                    if (jsonObject.has("alias")) {
                        JSONArray jsonArray = jsonObject.optJSONArray("alias");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String alias = jsonArray.optString(i);
                            //                            map_all.put("name", alias);
                            //                            table_all.insert(map_all);
                            map_alias.put("alias", alias);
                            table_aliasmap.insert(map_alias);
                        }
                    }
                } catch (JSONException e) {
                    API.echo("!无法格式化Json:" + s);
                } catch (SQLException e) {
                    API.verbException(e);
                    API.echo("数据库操作失败");
                }
            }
        } catch (IOException e) {
            API.verbException(e);
            API.echo("读取Repertory", str, "失败");
        }
    }

    @Override public CommandInfo info() {
        return COMMAND_INFO;
    }
}
