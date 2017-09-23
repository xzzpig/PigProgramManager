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

public class RemoveCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("remove", "移除软件", new String[]{"<program>"}, new String[][]{{"y", "同意危险操作"}}, null);

    @Override public String execute(Command cmd) {
        if (cmd.commands.size() < 2)
            return "<program>不可为空";
        String program = cmd.commands.get(1);
        try {//确实软件已安装
            if (!API.isInstalled(program))
                return program + " 未安装";
        } catch (SQLException e) {
            return "数据库操作失败";
        }
        cmd.signMap.put("program", program);
        String loc;
        JSONObject jsonObject;
        int depended;
        Table table_installed = API.database.getTable("installed");
        try {
            ResultSet resultSet = table_installed.select().setColums("*").setWhere("name = \"" + program + "\"").select();
            if (!resultSet.next()) {return "未在数据表(installed)中找到该软件:" + program;}
            loc = resultSet.getString("loc");
            jsonObject = new JSONObject(resultSet.getString("json"));
            depended = resultSet.getInt("depended");
        } catch (SQLException e) {
            API.verbException(e);
            return "数据库查询失败";
        }
        cmd.signMap.put("d", loc);

        if (depended != 0) {//软件被依赖
            if (!cmd.hasSign("f"))//软件没强制卸载参数
                if (!API.needConfirm("有软件将" + program + "作为依赖安装,移除此软件可能会导致其他软件无法使用"))
                    return "手动停止";
        }

        API.echo("开始移除", program, "(", jsonObject.optString("version"), ")");
        JSONObject steps = jsonObject.optJSONObject("steps");
        JSONArray step;
        if (steps != null) {
            step = steps.optJSONArray("uninstall");
            if (step != null) {
                API.echo("开始执行卸载步骤");
                if (!API.executeJsonObjects(step))
                    return "Json执行失败";
            }
            step = steps.optJSONArray("clean");
            if (step != null) {
                API.echo("开始执行清理步骤");
                if (!API.executeJsonObjects(step))
                    return "Json执行失败";
            }
        }
        File tmpDir = new File(API.solveVars("${Dir:Temp}"));
        if (!API.deleteDir(tmpDir))
            API.echo("临时文件删除失败,请手动删除:" + tmpDir.getAbsolutePath());
        try {
            table_installed.delete("name = \"" + program + "\"");
        } catch (SQLException e) {
            API.verbException(e);
            return "软件安装信息从数据库中删除失败";
        }
        if (jsonObject.has("depends")) {//处理依赖
            JSONArray dependsArray = jsonObject.getJSONArray("depends");
            for (int i = 0; i < dependsArray.length(); i++) {
                String program_depend = dependsArray.optString(i);
                try {
                    if (!API.isInstalled(program_depend))
                        continue;
                } catch (SQLException e) {
                    API.verbException(e);
                }
                try {
                    API.addDependedNum(program_depend, -1);
                } catch (SQLException e) {
                    API.verbException(e);
                    return "数据库操作失败(依赖自增)";
                }
            }
        }
        API.echo(program, "已移除完成!");
        return null;
    }

    @Override public CommandInfo info() {
        return COMMAND_INFO;
    }
}
