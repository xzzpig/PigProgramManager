package com.xzzpig.pigprogrammanager.plugin.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;
import com.xzzpig.pigprogrammanager.plugin.WinAPI;

import java.io.File;
import java.io.IOException;

public class AddPathJE implements JsonExecutor {
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
        if (!API.needConfirm("將" + path + "加入到用戶的环境变量path中")) {
            if (!ignoreFailed) {throw new JsonExecuteException("AddPathCanceledException", "拒绝将" + path + "加入到path中");}
            return true;
        }
        try {
            WinAPI.addPath(path);
        } catch (IOException e) {
            API.verbException(e);
            if (!ignoreFailed) {
                throw new JsonExecuteException("AddPathFailedException", "将" + path + "加入到用戶的环境变量path中失敗");
            } else
                API.echo("將" + path + "加入到用戶的环境变量path中失敗");
        }
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "AddPath";
    }

    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"path", "String", "添加的Path"}, {"ignoreFailed", "boolean", "是否忽略错误"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"AddPathCanceledException", "AddPathFailedException"};
    }
}
