package com.xzzpig.pigprogrammanager;

import com.github.xzzpig.pigutils.database.Database;
import com.github.xzzpig.pigutils.plugin.PluginManager;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.plugin.PPMPlugin;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Command command = API.solveArgs(args);
        API.verbose = command.hasSign("v");
        API.loadPlugins();
        API.loadConfig();
        API.initDatabase();
        PluginManager.DefaultPluginManager.getPluginStream().filter(PPMPlugin.class::isInstance).map(PPMPlugin.class::cast).forEach(PPMPlugin::onBeforeCommand);
        try (Database database = API.openDatabase()) {
            API.database = database;
            API.executeCommand(command);
        }
    }
}
