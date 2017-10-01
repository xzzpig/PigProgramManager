package com.xzzpig.pigprogrammanager.plugin.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;
import com.xzzpig.pigprogrammanager.plugin.WinAPI;

import java.io.File;
import java.io.IOException;

public class RemovePathJE implements JsonExecutor {
    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("path"))
            return false;
        String path = jsonObject.optString("path");
        path = new File(path).getAbsolutePath();
        boolean ignoreFailed = jsonObject.optBoolean("ignoreFailed", true);
        if (!API.needConfirm("將" + path + "从到用戶的环境变量path中移除")) {
            if (!ignoreFailed) {
                throw new JsonExecuteException("RemovePathCanceledException", "拒绝将" + path + "从path中移除");
            }
            return true;
        }
        try {
            WinAPI.removePath(path);
        } catch (IOException e) {
            API.verbException(e);
            if (!ignoreFailed) {
                throw new JsonExecuteException("RemovePathFailedException", "将" + path + "从用戶的环境变量path中移除失敗");
            } else
                API.echo("將" + path + "从用戶的环境变量path中移除失敗");
        }
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "RemovePath";
    }

    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"path", "String", "移除的Path"}, {"ignoreFailed", "boolean", "是否忽略错误"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"RemovePathCanceledException", "RemovePathFailedException"};
    }
}
