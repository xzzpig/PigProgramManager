package com.xzzpig.pigprogrammanager.command;

import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.CommandInfo;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UpgradeCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("upgrade", "更新软件", new String[]{"[program]"}, new String[][]{{"y", "同意危险操作"}, {"f", "强制更新到最新版本,包括更新号改变"}}, null);

    @Override public String execute(Command cmd) {
        if (cmd.data.containsKey("result")) {
            ResultSet resultSet = (ResultSet) cmd.data.get("result");
            try {
                String program = resultSet.getString("name"), loc = resultSet.getString("loc");
                cmd.signMap.put("program", program);
                cmd.signMap.put("d", loc);
                API.echo("开始更新软件:", program);
                JSONObject jsonObject = new JSONObject(resultSet.getString("json"));
                JSONObject steps = jsonObject.optJSONObject("steps");
                JSONArray step;
                if (steps != null) {
                    step = steps.optJSONArray("uninstall");
                    if (step != null) {
                        API.echo("开始执行卸载步骤");
                        if (!API.executeJsonObjects(step))
                            return "Json执行失败";
                    }
                }
                jsonObject = API.downloadProgramJson(program);
                steps = jsonObject.optJSONObject("steps");
                if (steps != null) {
                    step = steps.optJSONArray("install");
                    if (step != null) {
                        API.echo("开始执行安装步骤");
                        if (!API.executeJsonObjects(step))
                            return "Json执行失败";
                    }
                }
                JSONObject jsonObject_digest = API.digestSetupJson(jsonObject);
                Map<String, Object> map = new HashMap<>();
                String[] versions = jsonObject_digest.optString("version").split("\\.");
                API.database.getConnection().createStatement().executeUpdate("UPDATE \"installed\" SET version_0 = " + versions[0] + ", version_1 = " + versions[1] + ", version_2 = " + versions[2] + " WHERE \"name\" = \"" + program + "\"");
                API.echo("软件(", program, ")更新完成");
            } catch (SQLException e) {
                API.verbException(e);
                return "数据库查询/更新失败";
            } catch (IOException e) {
                API.verbException(e);
                return "Program安装说明文件下载失败";
            }
            return null;
        }

        String program;
        if (cmd.commands.size() < 2) program = "*";
        else program = cmd.commands.get(1);
        try {//确实软件已安装
            if (!program.equals("*") && !API.isInstalled(program))
                return program + " 未安装";
        } catch (SQLException e) {
            return "数据库操作失败";
        }
        if (program.equals("*")) {
            String sql;
            if (cmd.hasSign("f")) sql =
                    "select \n" +
                            "    json,url,loc,\n" +
                            "    all_.name as name,all_.version_0 as v0,all_.version_1 as v1,all_.version_2 as v2,\n" +
                            "    installed.version_0 as v_0,installed.version_1 as v_1,installed.version_2 as v_2\n" +
                            "from all_,installed \n" +
                            "where\n" +
                            "    all_.name = installed.name and\n" +
                            "    ((all_.version_0 > installed.version_0) or (all_.version_1 > installed.version_1) or (all_.version_2 > installed.version_2)) and \n" +
                            "    disableUpgrade = 0";
            else sql =
                    "select \n" +
                            "    all_.name as name,json,url,loc,\n" +
                            "    all_.version_0 as v0,all_.version_1 as v1,all_.version_2 as v2,\n" +
                            "    installed.version_0 as v_0,installed.version_1 as v_1,installed.version_2 as v_2\n" +
                            "from all_,installed \n" +
                            "where\n" +
                            "    all_.name = installed.name and\n" +
                            "    ((all_.version_0 > installed.version_0) or (all_.version_1 > installed.version_1)) and \n" +
                            "    disableUpgrade = 0";
            try {
                ResultSet resultSet = API.database.getConnection().createStatement().executeQuery(sql);
                String[] args;
                if (cmd.hasSign("y")) args = new String[]{"upgrade -y"};
                else args = new String[]{"upgrade"};
                while (resultSet.next()) {
                    Command command = API.solveArgs(args);
                    command.data.put("result", resultSet);
                    if (!API.executeCommand(command))
                        API.echo("更新失败");
                }
                API.echo("全部軟件更新完成");
            } catch (SQLException e) {
                API.verbException(e);
                return "数据库查询失敗";
            }
        } else {
            String sql;
            if (cmd.hasSign("f")) sql =
                    "select \n" +
                            "select \n" +
                            "    all_.name as name,json,url,loc,\n" +
                            "    all_.version_0 as v0,all_.version_1 as v1,all_.version_2 as v2,\n" +
                            "    installed.version_0 as v_0,installed.version_1 as v_1,installed.version_2 as v_2\n" +
                            "from all_,installed \n" +
                            "where\n" +
                            "    all_.name = installed.name and\n" +
                            "    ((all_.version_0 > installed.version_0) or (all_.version_1 > installed.version_1)) and \n" +
                            "    disableUpgrade = 0 and" +
                            "    all_.name = \"" + program + "\"";
            else sql =
                    "select \n" +
                            "select \n" +
                            "    all_.name as name,json,url,loc,\n" +
                            "    all_.version_0 as v0,all_.version_1 as v1,all_.version_2 as v2,\n" +
                            "    installed.version_0 as v_0,installed.version_1 as v_1,installed.version_2 as v_2\n" +
                            "from all_,installed \n" +
                            "where\n" +
                            "    all_.name = installed.name and\n" +
                            "    ((all_.version_0 > installed.version_0) or (all_.version_1 > installed.version_1)) and \n" +
                            "    disableUpgrade = 0 and" +
                            "    all_.name = \"" + program + "\"";
            try {
                ResultSet resultSet = API.database.getConnection().prepareCall(sql).getResultSet();
                String[] args;
                if (cmd.hasSign("y")) args = new String[]{"upgrade -y"};
                else args = new String[]{"upgrade"};
                if (resultSet.next()) {
                    Command command = API.solveArgs(args);
                    command.data.put("result", resultSet);
                    if (!API.executeCommand(command))
                        return null;
                } else {
                    return "此程序(" + program + ")不需要更新";
                }
            } catch (SQLException e) {
                API.verbException(e);
                return "数据库查询失敗";
            }
        }
        return null;
    }

    @Override public CommandInfo info() {
        return COMMAND_INFO;
    }
}
