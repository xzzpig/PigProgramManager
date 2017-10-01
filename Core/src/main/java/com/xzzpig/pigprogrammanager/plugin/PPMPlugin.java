package com.xzzpig.pigprogrammanager.plugin;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.plugin.java.JavaPlugin;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;
import com.xzzpig.pigprogrammanager.api.VariableProvider;

public abstract class PPMPlugin extends JavaPlugin {
    /**
     * @return 插件提供的CommandExecutor(将被自动注册)
     */
    public abstract @NotNull CommandExecutor[] getCommandExecutors();

    /**
     * @return 插件提供的JsonExecutor(将被自动注册)
     */
    public abstract @NotNull JsonExecutor[] getJsonExecutors();

    /**
     * @return VariableProvider(将被自动注册)
     */
    public abstract @NotNull VariableProvider[] getVariableProviders();

    @Override
    public void onEnable() {
        for (CommandExecutor commandExecutor : getCommandExecutors())
            API.registerCommandExecutor(commandExecutor);
        for (JsonExecutor jsonExecutor : getJsonExecutors())
            API.registerJsonExecutor(jsonExecutor);
        for (VariableProvider variableProvider : getVariableProviders())
            API.registerValuableProvider(variableProvider);
    }

    /**
     * 在准备开始执行命令前执行(仅一次)
     */
    public void onBeforeCommand() {}

    public @NotNull String getDescribe() {
        return getInfos().getOrDefault("describe", "");
    }
}
