package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.core.MD5;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import java.io.File;
import java.io.FileNotFoundException;

public class CheckMD5JE implements JsonExecutor {
    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        String file = jsonObject.optString("file");
        String md5 = jsonObject.optString("md5");
        String failError = jsonObject.optString("failError", "MD5CheckException");
        String failMsg = jsonObject.optString("failMsg", "MD5CheckFailed");
        boolean gc = jsonObject.optBoolean("gc", true);
        if (file == null || md5 == null)
            return false;
        File file1 = new File(file);
        if (!file1.exists() || !file1.isFile())
            throw new JsonExecuteException(failError, failMsg);
        try {
            if (!MD5.GetMD5Code(file1).equalsIgnoreCase(md5))
                throw new JsonExecuteException(failError, failMsg);
        } catch (FileNotFoundException e) {
            API.verbException(e);
            throw new JsonExecuteException("MD5CheckException", e.getLocalizedMessage());
        }
        if (gc)
            System.gc();
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "CMD";
    }

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"file", "String", "校验的文件路径"}, {"md5", "String", "文件的md5"}, {"failError", "String", "校验失败时抛出的错误"}, {"failMsg", "String", "错误内容"}, {"gc", "boolean", "是否需要GC"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"AnyException"};
    }
}
