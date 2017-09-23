package com.xzzpig.pigprogrammanager.variableprovider;

import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.InCommand;
import com.xzzpig.pigprogrammanager.api.Variable;
import com.xzzpig.pigprogrammanager.api.VariableProvider;

import java.sql.ResultSet;
import java.sql.SQLException;

@InCommand
public class VersionVP implements VariableProvider {
    /**
     * 与 {@link Variable#name} 对应
     *
     * @return 提供变量的名称
     */
    @Override public String name() { return "Version"; }

    /**
     * @param args 与{@link Variable#args} 对于
     * @return 变量内容(null是表示此Provider不无法提供此变量内容)
     */
    @Override
    public String provide(String... args) {
        if (args.length < 2)
            return null;
        String name = args[0];
        String version = args[1];
        try {
            ResultSet resultSet = API.getInstalledProgramResult(name);
            if (!resultSet.next())
                return null;
            return resultSet.getString("version_" + version);
        } catch (SQLException e) {
            return null;
        }
    }
}
