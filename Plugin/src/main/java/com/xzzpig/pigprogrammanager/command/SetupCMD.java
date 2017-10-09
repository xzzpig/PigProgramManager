package com.xzzpig.pigprogrammanager.command;

import com.github.xzzpig.pigutils.database.Table;
import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.CommandInfo;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SetupCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("setup", "重新配置软件", new String[]{"<program>"}, new String[][]{{"y", "同意危险操作"}}, null);

    @Override public String execute(Command cmd) {
        if (cmd.commands.size() < 2)
            return "<program>不可为空";
        String program = cmd.commands.get(1);
        try {
            program = API.getRawName(program);
        } catch (SQLException e) {
            return "数据库操作失败";
        }
        try {//确实软件已安装
            if (!API.isInstalled(program))
                return program + " 未安装";
        } catch (SQLException e) {
            return "数据库操作失败";
        }
        cmd.signMap.put("program", program);
        String loc;
        JSONObject jsonObject;
        Table table_installed = API.database.getTable("installed");
        try {
            ResultSet resultSet = table_installed.select().setColums("*").setWhere("name = \"" + program + "\"").select();
            if (!resultSet.next()) return "未在数据表(installed)中找到该软件:" + program;
            loc = resultSet.getString("loc");
            jsonObject = new JSONObject(resultSet.getString("json"));
        } catch (SQLException e) {
            API.verbException(e);
            return "数据库查询失败";
        }
        cmd.signMap.put("d", loc);
        API.echo("开始重新配置", program, "(", jsonObject.optString("version"), ")");
        JSONObject steps = jsonObject.optJSONObject("steps");
        JSONArray step;
        if (steps != null) {
            step = steps.optJSONArray("clean");
            if (step != null) {
                API.echo("开始执行清理步骤");
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
        File tmpDir = new File(API.solveVars("${Dir:Temp}"));
        if (!API.deleteDir(tmpDir))
            API.echo("临时文件删除失败,请手动删除:" + tmpDir.getAbsolutePath());
        API.echo(program, "已重新配置完成!");
        return null;
    }

    @Override public CommandInfo info() {
        return COMMAND_INFO;
    }
}
