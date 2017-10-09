package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import java.io.IOException;

public class CMDJE implements JsonExecutor {
    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("cmd"))
            return false;
        String cmd = jsonObject.optString("cmd");
        cmd = API.solveVars(cmd);
        boolean need_err = jsonObject.optBoolean("err", false);
        boolean need_out = jsonObject.optBoolean("out", true);
        if (!API.needConfirm("执行命令:" + cmd)) {
            throw new JsonExecuteException("CMDExecuteFailedException", "ConfirmCanceled");
        }
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            if (need_out)
                new Thread(()->{
                    int i;
                    try {
                        while (process.isAlive() && (i = process.getInputStream().read()) != -1)
                            System.out.write(i);
                    } catch (IOException e) {
                        API.verbException(e);
                    }
                }).start();
            if (need_err)
                new Thread(()->{
                    int i;
                    try {
                        while (process.isAlive() && (i = process.getInputStream().read()) != -1)
                            System.err.write(i);
                    } catch (IOException e) {
                        API.verbException(e);
                    }
                }).start();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
            }
        } catch (IOException e) {
            API.verbException(e);
            throw new JsonExecuteException("CMDExecuteFailedException", e.getLocalizedMessage());
        }
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
        return new String[][]{{"cmd", "String", "执行的命令"}, {"out", "boolean", "是否需要显示输出"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"CMDExecuteFailedException"};
    }
}
