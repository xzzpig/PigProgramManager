package com.xzzpig.pigprogrammanager.command;

import com.github.xzzpig.pigutils.database.Table;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.CommandInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AutoRemoveCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("autoremove", "移除为被依赖的软件", null, new String[][]{{"y", "同意危险操作"}}, null);

    @Override public String execute(Command cmd) {
        Table table_installed = API.database.getTable("installed");
        ResultSet resultSet;
        try {
            resultSet = table_installed.select().setColums("name").setWhere("depended <= 0 and soft = 1").select();
        } catch (SQLException e) {
            API.verbException(e);
            return "数据库查询失败";
        }
        try {
            String program;
            while (resultSet.next()) {
                program = resultSet.getString("name");
                Command cmd2 = API.solveArgs("remove", program);
                if (cmd.hasSign("y"))
                    cmd2.signs.add("y");
                if (!API.executeCommand(cmd2))
                    API.echo(program + "自动移除失败");
                else
                    API.echo(program + "自动移除成功");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override public CommandInfo info() {
        return COMMAND_INFO;
    }
}
