package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import java.io.File;

public class MoveJE implements JsonExecutor {
    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("from"))
            return false;
        if (!jsonObject.has("to"))
            return false;
        File from = new File(jsonObject.optString("from"));
        File to = new File(jsonObject.optString("to"));
        if (to.exists())
            API.deleteDir(to);
        try {
            to.getParentFile().mkdirs();
        } catch (Exception e) {}
        if (!API.needConfirm("移动文件", from.getAbsolutePath(), "到", to.getAbsolutePath())) {
            throw new JsonExecuteException("MoveFailedException", "ConfirmCanceled");
        }
        try {
            if (!new File(from.getAbsolutePath()).renameTo(new File(to.getAbsolutePath())))
                throw new RuntimeException(from + " copy to " + to + " failed");
        } catch (Exception e) {
            API.verbException(e);
            throw new JsonExecuteException("MoveFailedException", e.getLocalizedMessage());
        }
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "Move";
    }

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"from", "String", "文件路径"}, {"to", "String", "目标路径"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"MoveFailedException"};
    }
}
