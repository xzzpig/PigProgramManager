package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

public class ConfirmJE implements JsonExecutor {
    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("msgs"))
            return false;
        if (!jsonObject.has("failErr"))
            return false;
        if (!jsonObject.has("failMsg"))
            return false;
        JSONArray msgs_arr = jsonObject.optJSONArray("msgs");
        if (msgs_arr == null)
            msgs_arr = new JSONArray();
        String failErr = jsonObject.optString("failErr");
        String failMsg = jsonObject.optString("failMsg");
        String[] msgs = new String[msgs_arr.length()];
        for (int i = 0; i < msgs.length; i++)
            msgs[i] = msgs_arr.getString(i);
        if (!API.needConfirm(msgs))
            throw new JsonExecuteException(failErr, failMsg);
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "Confirm";
    }

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"msgs", "StringArray", "确认的信息"}, {"failErr", "String", "确认失败的Error的name"}, {"failMsg", "String", "确认失败的Error的msg"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"AnyException"};
    }
}
