package com.xzzpig.pigprogrammanager.plugin.json;

import com.github.xzzpig.pigutils.core.RegistryHelper;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import java.io.IOException;

public class RegistryJE implements JsonExecutor {
    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("action"))
            return false;
        if (!jsonObject.has("dir"))
            return false;
        if (!jsonObject.has("key"))
            return false;
        String action = jsonObject.optString("action");
        String dir = jsonObject.optString("dir");
        String key = jsonObject.optString("key");
        String type = jsonObject.optString("type");
        String value = jsonObject.optString("value");
        boolean ignoreFailed = jsonObject.optBoolean("ignoreFailed", true);
        try {
            if (action.equalsIgnoreCase("SET")) {
                if (type == null || value == null) {
                    throw new JsonExecuteException("RegistryFailedException", "type或者value不能为空");
                }
                if (!API.needConfirm("將设置注冊表", dir + "目录下", key + "键", "的值为" + type + "类型的", value))
                    throw new JsonExecuteException("RegistryFailedException", "已拒绝");
                try {
                    RegistryHelper.setValue(dir, key, type, value);
                } catch (IOException e) {
                    API.verbException(e);
                    throw new JsonExecuteException("RegistryFailedException", e.getLocalizedMessage());
                }
            } else if (action.equalsIgnoreCase("DELETE")) {
                if (!API.needConfirm("將刪除注冊表", dir + "目录下的", key + "键"))
                    throw new JsonExecuteException("RegistryFailedException", "已拒绝");
                try {
                    RegistryHelper.deleteValue(dir, key);
                } catch (IOException e) {
                    API.verbException(e);
                    throw new JsonExecuteException("RegistryFailedException", e.getLocalizedMessage());
                }
            }
        } catch (JsonExecuteException e) {
            if (!ignoreFailed)
                throw e;
            else
                API.verbException(e);
        }
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "Registry";
    }

    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"action", "String", "添加/设置:SET|删除:DELETE"}, {"dir", "String", ""}, {"key", "String", ""}, {"type", "String", ""}, {"value", "String", ""}, {"ignoreFailed", "boolean", ""}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"RegistryFailedException"};
    }
}
