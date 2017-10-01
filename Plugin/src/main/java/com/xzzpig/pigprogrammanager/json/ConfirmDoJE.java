package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

public class ConfirmDoJE implements JsonExecutor {
    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("msg"))
            return false;
        if (!jsonObject.has("actions"))
            return false;
        boolean care = jsonObject.optBoolean("careFail", false);
        JSONArray actions = jsonObject.optJSONArray("actions");
        String msg = jsonObject.optString("msg");
        if (API.needConfirm(msg))
            if (!API.executeJsonObjects(actions) && care)
                throw new JsonExecuteException("ConfirmDoFailedException", "");
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "ConfirmDo";
    }

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"actions", "JsonArray", "同意后执行的流程"}, {"msg", "String", "确认的信息"}, {"careFail", "boolean", "是否关注执行失败"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"ConfirmDoFailedException"};
    }
}
