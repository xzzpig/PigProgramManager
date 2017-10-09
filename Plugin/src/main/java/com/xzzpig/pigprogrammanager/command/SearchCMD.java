package com.xzzpig.pigprogrammanager.command;

import com.github.xzzpig.pigutils.database.Table;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.CommandInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("search", "搜索软件", new String[]{"<str>"}, new String[][]{{"d", "深度搜索"}}, null);

    @Override public String execute(Command cmd) {
        if (cmd.commands.size() < 2)
            return "<str>不可为空";
        String str = cmd.commands.get(1);
        if (cmd.hasSign("d")) {
            StringBuilder sb = new StringBuilder();
            for (char c : str.toCharArray())
                sb.append(c).append('%');
            str = sb.toString();
        }
        try {
            Table table_aliasmap = API.database.getTable("aliasmap");
            API.echo("搜索结果:");
            int i = 0;
            ResultSet resultSet = table_aliasmap.select().setColums("alias").setWhere("alias like \"%" + str + "%\"").select();
            while (resultSet.next()) API.echo(++i, '\t', resultSet.getString("alias"));
            if (i == 0) {
                API.echo("\t无");
                API.echo("未找到结果?试试 ppm search -d", str);
            }
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
