package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import java.io.File;

public class DeleteJE implements JsonExecutor {
    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("file"))
            return false;
        File file = new File(jsonObject.optString("file"));
        if (!file.exists())
            return true;
        API.needConfirm("将删除文件:" + file.getAbsolutePath());
        if (!API.deleteDir(file))
            throw new JsonExecuteException("DeleteFailedException", "file(" + file.getAbsolutePath() + ") delete failed");
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "Delete";
    }

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"file", "String", "文件路径"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"DeleteFailedException"};
    }
}
