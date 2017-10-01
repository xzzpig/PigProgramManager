package com.xzzpig.pigprogrammanager.command;

import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.CommandInfo;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InstallCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("install", "安装软件", new String[]{"<program>"}, new String[][]{{"d", "安装在当前目录"}, {"y", "同意危险操作"}}, new String[][]{{"d", "[Dir]", "安装在指定目录"}});

    @Override public String execute(Command cmd) {
        if (cmd.commands.size() < 2)
            return "<program>不可为空";
        String program = cmd.commands.get(1);
        try {//确实软件未安装
            if (API.isInstalled(program))
                return program + " 已安装";
        } catch (SQLException e) {
            return "数据库操作失败";
        }

        if (cmd.hasSign("d"))
            cmd.signMap.put("d", cmd.signMap.getOrDefault("d", new File("./").getAbsolutePath()));
        else
            cmd.signMap.put("d", API.solveVars("${Dir:Programs}/") + program);
        cmd.signMap.put("program", program);
        cmd.signMap.put("d", new File(cmd.signMap.get("d")).getAbsolutePath());
        JSONObject jsonObject;
        try {
            System.out.print("开始下载软件安装配置文件");
            jsonObject = API.downloadProgramJson(program);
            System.out.println("(完成)");
        } catch (Exception e) {
            API.verbException(e);
            return "Program安装说明文件下载失败";
        }
        cmd.signMap.put("URL_JSON", jsonObject.optString("jsonURL"));
        if (jsonObject.has("depends")) {//处理依赖
            JSONArray dependsArray = jsonObject.getJSONArray("depends");
            for (int i = 0; i < dependsArray.length(); i++) {
                String program_depend = dependsArray.optString(i);
                try {
                    if (API.isInstalled(program_depend))
                        continue;
                } catch (SQLException e) {
                    API.verbException(e);
                }
                API.echo("开始安装", program, "的依赖:", program_depend);
                Command cmd_depend = API.solveArgs("install", program_depend);
                if (cmd.hasSign("y"))
                    cmd_depend.signs.add("y");
                cmd_depend.signs.add("soft");
                if (!API.executeCommand(cmd_depend)) {
                    return "软件依赖(" + program_depend + ")安装失败";
                }
            }
        }
        jsonObject = new JSONObject(API.solveVars(jsonObject.toString()));
        API.echo("开始安装", program, "(", jsonObject.optString("version"), ")");
        if (jsonObject.has("license")) {
            JSONObject license = jsonObject.optJSONObject("license");
            if (!(API.needConfirm("该软件协议:" + license.optString("name") + "(" + license.optString("url") + ")")))
                return "不接受软件协议无法安装";
        }
        JSONObject size = jsonObject.optJSONObject("size");
        if (size == null)
            size = new JSONObject();
        if (!API.needConfirm("安装将需要下载:" + size.optString("download", "unknown") + ",占用:" + size.optString("install", "unknown")))
            return "";
        JSONObject steps = jsonObject.optJSONObject("steps");
        JSONArray step;
        if (steps != null) {
            step = steps.optJSONArray("install");
            if (step != null) {
                API.echo("开始执行安装步骤");
                if (!API.executeJsonObjects(step))
                    return "Json执行失败";
            }
            step = steps.optJSONArray("setup");
            if (step != null) {
                API.echo("开始执行配置步骤");
                if (!API.executeJsonObjects(step))
                    return "Json执行失败";
            }
        }
        JSONObject jsonObject_digest = API.digestSetupJson(jsonObject);
        Map<String, Object> map = new HashMap<>();
        map.put("name", program);
        String[] versions = jsonObject_digest.optString("version").split("\\.");
        map.put("version_0", Integer.parseInt(versions[0]));
        map.put("version_1", Integer.parseInt(versions[1]));
        map.put("version_2", Integer.parseInt(versions[2]));
        map.put("json", jsonObject.toString());
        map.put("loc", cmd.signMap.get("d"));
        map.put("depended", cmd.hasSign("depended") ? 1 : 0);
        map.put("soft", cmd.hasSign("soft") ? 1 : 0);
        try {
            API.database.getTable("installed").insert(map);
        } catch (SQLException e) {
            API.verbException(e);
            API.echo("数据库插入失败");
        }
        JSONArray depends = jsonObject.optJSONArray("depends");
        if (depends != null)
            for (int i = 0; i < depends.length(); i++)
                try {
                    API.addDependedNum(depends.optString(i), 1);
                } catch (SQLException e) {
                    return "依赖自增失败";
                }
        File tmpDir = new File(API.solveVars("${Dir:Temp}"));
        if (!API.deleteDir(tmpDir))
            API.echo("临时文件删除失败,请手动删除:" + tmpDir.getAbsolutePath());
        API.echo(program, "已安装完成!");
        return null;
    }

    @Override public CommandInfo info() {
        return COMMAND_INFO;
    }
}
