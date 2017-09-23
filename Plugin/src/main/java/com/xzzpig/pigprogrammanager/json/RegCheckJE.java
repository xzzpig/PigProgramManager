package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

public class RegCheckJE implements JsonExecutor {
    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("str"))
            return false;
        if (!jsonObject.has("failErr"))
            return false;
        if (!jsonObject.has("failMsg"))
            return false;
        if (!jsonObject.has("reg"))
            return false;
        String str = jsonObject.optString("str");
        String reg = jsonObject.optString("reg");
        String failErr = jsonObject.optString("failErr");
        String failMsg = jsonObject.optString("failMsg");
        if (!str.matches(reg))
            throw new JsonExecuteException(failErr, failMsg);
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "RegCheck";
    }

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"str", "String", "待检验内容"}, {"reg", "String", "正则"}, {"failErr", "String", "确认失败的Error的name"}, {"failMsg", "String", "确认失败的Error的msg"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"AnyException"};
    }
}
