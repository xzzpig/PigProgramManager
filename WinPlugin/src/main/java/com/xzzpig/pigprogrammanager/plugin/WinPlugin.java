package com.xzzpig.pigprogrammanager.plugin;

import com.github.xzzpig.pigutils.core.RegistryHelper;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;
import com.xzzpig.pigprogrammanager.api.VariableProvider;
import com.xzzpig.pigprogrammanager.plugin.json.AddPathJE;
import com.xzzpig.pigprogrammanager.plugin.json.RegistryJE;
import com.xzzpig.pigprogrammanager.plugin.json.RemovePathJE;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WinPlugin extends PPMPlugin {

    private static final VariableProvider[] VARIABLE_PROVIDERS = new VariableProvider[]{};
    private static final CommandExecutor[] COMMAND_EXECUTORS = new CommandExecutor[]{};
    private static final JsonExecutor[] JSON_EXECUTORS = new JsonExecutor[]{new RegistryJE(), new RemovePathJE(), new AddPathJE()};

    @Override public void onEnable() {
        String os_name = System.getProperty("os.name").toLowerCase();
        if (os_name.matches(".*windows.*"))
            super.onEnable();
        else
            API.echo("!当前系统（" + os_name + ")不是Windows系统," + getName() + "停止加载");
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

    @Override public void onBeforeCommand() {
        try {
            checkPath();
        } catch (IOException e) {
            API.verbException(e);
            API.echo("Path检查失败");
        }
    }

    private void checkPath() throws IOException {
        List<String> paths = WinAPI.getPaths();
        String path = API.getConfig().optString("bin");
        File pathFile = new File(path);
        path = pathFile.getAbsolutePath();
        if (!pathFile.exists())
            pathFile.mkdirs();
        if (paths.contains(path))
            return;
        if (!API.needConfirm("未在用户Path中找到", path, "是否添加到用户Path中"))
            return;
        RegistryHelper.addUserPath(path);
    }
}
