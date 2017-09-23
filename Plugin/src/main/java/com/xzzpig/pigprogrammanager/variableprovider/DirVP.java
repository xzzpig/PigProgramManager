package com.xzzpig.pigprogrammanager.variableprovider;

import com.github.xzzpig.pigutils.database.Table;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.VariableProvider;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DirVP implements VariableProvider {
    @Override public String name() {
        return "Dir";
    }

    @Override public String provide(String... args) {
        if (args.length < 1)
            return null;
        switch (args[0]) {
            case "Programs":
                if (API.getConfig() == null)
                    return API.getPPMDir() + "/Programs/";
                return API.getConfig().optString("programFiles", null);
            case "Temps":
                return API.getConfig().optString("tmpDir", null);
            case "Bin":
                return API.getConfig().optString("bin", null);
            case "PPM":
                return API.getPPMDir().getAbsolutePath();
            case "Home":
                return System.getenv("USERPROFILE");
            case "Program":
                if (API.getCommand() != null && API.getCommand().hasSign("d"))
                    return API.getCommand().signMap.getOrDefault("d", new File("./").getAbsolutePath()).replace('\\', '/');
                else return null;
            case "Temp":
                if (API.getConfig().has("tmpDir") && API.getCommand() != null && API.getCommand().signMap.containsKey("program"))
                    return API.getConfig().optString("tmpDir") + "/" + API.getCommand().signMap.get("program");
                else return null;
            case "of":
                if (args.length < 2 || API.getCommand() == null)
                    return null;
                String program = args[1];
                Table table_installed = API.database.getTable("installed");
                try {
                    ResultSet resultSet = table_installed.select().setColums("name", "loc").setWhere("name = \"" + program + "\"").select();
                    if (resultSet.next()) {
                        return resultSet.getString("loc");
                    } else
                        return null;
                } catch (SQLException e) {
                    API.verbException(e);
                    return null;
                }
            default:
                return null;
        }
    }
}
