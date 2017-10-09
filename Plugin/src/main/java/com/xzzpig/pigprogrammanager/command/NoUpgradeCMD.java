package com.xzzpig.pigprogrammanager.command;

import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.CommandInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NoUpgradeCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("noupgrade", "禁止/允许软件软件", new String[]{"<program>"}, null, null);

    @Override public String execute(Command cmd) {
        if (cmd.commands.size() < 2)
            return "<program>不可为空";
        String program = cmd.commands.get(1);
        try {
            program = API.getRawName(program);
        } catch (SQLException e) {
            return "数据库操作失败";
        }
        try {//确实软件未安装
            if (!API.isInstalled(program))
                return program + " 未安装";
        } catch (SQLException e) {
            return "数据库操作失败";
        }
        boolean enabled;
        try {
            ResultSet resultSet = API.getInstalledProgramResult(program);
            enabled = resultSet.getInt("disableUpgrade") == 0;
            API.database.getConnection().createStatement().executeUpdate("UPDATE \"installed\" SET disableUpgrade = " + (enabled ? 1 : 0) + " WHERE \"name\" = \"" + program + "\"");
        } catch (SQLException e) {
            API.verbException(e);
            return "数据库更新操作失败";
        }
        API.echo("已" + (enabled ? "禁止" : "允许") + program + "的更新");
        return null;
    }

    @Override public CommandInfo info() {
        return COMMAND_INFO;
    }
}
