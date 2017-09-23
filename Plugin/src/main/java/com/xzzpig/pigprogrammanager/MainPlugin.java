package com.xzzpig.pigprogrammanager;

import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;
import com.xzzpig.pigprogrammanager.api.VariableProvider;
import com.xzzpig.pigprogrammanager.command.*;
import com.xzzpig.pigprogrammanager.json.*;
import com.xzzpig.pigprogrammanager.plugin.PPMPlugin;
import com.xzzpig.pigprogrammanager.variableprovider.DirVP;
import com.xzzpig.pigprogrammanager.variableprovider.URLVP;
import com.xzzpig.pigprogrammanager.variableprovider.VarVP;
import com.xzzpig.pigprogrammanager.variableprovider.VersionVP;

public class MainPlugin extends PPMPlugin {

    private static final VariableProvider[] VARIABLE_PROVIDERS = new VariableProvider[]{new VersionVP(), new DirVP(), new VarVP(), new URLVP()};
    private static final CommandExecutor[] COMMAND_EXECUTORS = new CommandExecutor[]{new SetupCMD(), new AutoRemoveCMD(), new NoUpgradeCMD(), new UpgradeCMD(), new RemoveCMD(), new ShowCMD(), new MakeCMD(), new UpdateCMD(), new SearchCMD(), new InstallCMD()};
    private static final JsonExecutor[] JSON_EXECUTORS = new JsonExecutor[]{new ConfirmJE(), new RegCheckJE(), new CMDJE(), new DeleteJE(), new MoveJE(), new UnzipJE(), new DownloadJE()};

    @Override public void onEnable() {
        super.onEnable();
        API.echo("!" + getName() + " loaded");
    }

    /**
     * @return 插件提供的CommandExecutor(将被自动注册)
     */
    @Override public CommandExecutor[] getCommandExecutors() {
        return COMMAND_EXECUTORS;
    }

    /**
     * @return 插件提供的JsonExecutor(将被自动注册)
     */
    @Override public JsonExecutor[] getJsonExecutors() {
        return JSON_EXECUTORS;
    }

    /**
     * @return VariableProvider(将被自动注册)
     */
    @Override public VariableProvider[] getVariableProviders() {
        return VARIABLE_PROVIDERS;
    }

    @Override public void onDisable() {
    }
}
