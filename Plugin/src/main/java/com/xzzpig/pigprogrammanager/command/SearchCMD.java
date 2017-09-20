package com.xzzpig.pigprogrammanager.command;

import com.github.xzzpig.pigutils.database.Table;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.CommandInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("search", "搜索软件", new String[]{"<str>"}, null, null);

    @Override public String execute(Command cmd) {
        if (cmd.commands.size() < 2)
            return "<str>不可为空";
        String str = cmd.commands.get(1);
        try {
            Table table_all = API.database.getTable("all_");
            API.echo("搜索结果:");
            ResultSet resultSet = table_all.select().setColums("name").setWhere("name like \"%" + str + "%\"").select();
            while (resultSet.next()) API.echo('\t', resultSet.getString("name"));
        } catch (SQLException e) {
            API.verbException(e);
            return "数据库打开/操作失败";
        }
        API.echo("搜索完成");
        return null;
    }

    @Override public CommandInfo info() {
        return COMMAND_INFO;
    }
}
