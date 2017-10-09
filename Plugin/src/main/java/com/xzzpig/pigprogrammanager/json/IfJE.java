package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

public class IfJE implements JsonExecutor {
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        JSONObject condition = jsonObject.optJSONObject("condition");
        if (condition == null)
            return false;
        try {
            API.executeJsonObject(condition.optString("name"), jsonObject);
            if (jsonObject.has("success"))
                if (!API.executeJsonObjects(jsonObject.optJSONArray("success")))
                    throw new JsonExecuteException("IfExecuteFailedException", "successJsonExecuteFailed");
        } catch (JsonExecuteException e) {
            if (jsonObject.has(e.name)) {
                if (!API.executeJsonObjects(jsonObject.optJSONArray(e.name)))
                    throw new JsonExecuteException("IfExecuteFailedException", e.msg);
            } else
                throw e;
        } finally {
            if (jsonObject.has("finally"))
                if (!API.executeJsonObjects(jsonObject.optJSONArray("finally")))
                    throw new JsonExecuteException("IfExecuteFailedException", "finallyJsonExecuteFailed");
        }
        return true;
    }

    @Override public String name() {
        return "If";
    }

    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"condition", "JsonObject", "判断的条件"}, {"success", "JsonArray<JsonObject>", "若无错误则执行"}, {"finally", "JsonArray<JsonObject>", "finally必定执行"}, {"???", "JsonArray<JsonObject>", "???为抛出的错误的name"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"AnyException"};
    }
}
