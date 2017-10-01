package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import java.io.File;

public class LinkJE implements JsonExecutor {
    public static void link_win(String from, String to, boolean isDir) throws JsonExecuteException {
        String cmd;
        if (isDir)
            cmd = "cmd /c mklink /J" + to + " " + from;
        else
            cmd = "cmd /c mklink " + to + " " + from;
        JSONObject json = new JSONObject();
        json.put("name", "CMD");
        json.put("cmd", cmd);
        API.executeJsonObject("CMD", json);
    }

    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        try {

            if (!jsonObject.has("from"))
                return false;
            if (!jsonObject.has("to"))
                return false;
            String from = jsonObject.optString("from");
            String to = jsonObject.optString("to");
            boolean ignoreFailed = jsonObject.optBoolean("ignoreFailed", true);
            File fromFile = new File(from);
            if (!fromFile.exists())
                throw new JsonExecuteException("LinkFailedException", "文件" + fromFile.getAbsolutePath() + "不存在");
            from = fromFile.getAbsolutePath();
            boolean dir = fromFile.isDirectory();
            if (!dir) {
                if (!to.matches(".+\\.\\w+"))
                    to = to + "/" + fromFile.getName();
            }
            File toFile = new File(to);
            to = toFile.getAbsolutePath();
            if (!API.needConfirm("将链接文件" + (dir ? "夹" : ""), "把" + from, "链接到" + to))
                throw new JsonExecuteException("LinkFailedException", "链接未同意");
            if (toFile.exists()) {API.deleteDir(toFile);}
            String os_name = System.getProperty("os.name").toLowerCase();
            if (os_name.matches(".*windows.*"))
                link_win(from, to, dir);
            else
                return false;
            if (!toFile.exists())
                throw new JsonExecuteException("LinkFailedException", "未知错误(可能权限不够)");
            return true;
        } catch (JsonExecuteException e) {
            throw e;
        }
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "Link";
    }

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"from", "String", "被链接的文件(夹)"}, {"to", "String", "目标文件(夹)"}, {"ignoreFailed", "boolean", "是否忽略失败"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"CMDExecuteFailedException", "LinkFailedException"};
    }
}
