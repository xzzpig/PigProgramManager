package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.plugin.script.ScriptPluginLoader;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class ScriptJE implements JsonExecutor {

    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("type"))
            return false;
        if (!jsonObject.has("url"))
            return false;
        String type = jsonObject.optString("type");
        String url_s = jsonObject.optString("url");
        URL url;
        try {
            url = new URL(url_s);
        } catch (MalformedURLException e) {
            API.verbException(e);
            throw new JsonExecuteException("ScriptExecuteException", "url(" + url_s + ")不合法");
        }
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager(ScriptPluginLoader.Classloader4ScriptManager);
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(type);
        if (scriptEngine == null)
            throw new JsonExecuteException("ScriptExecuteException", "没有ScriptEngine名为" + type);
        try (InputStream inputStream = url.openStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            scriptEngine.eval(bufferedReader);
        } catch (Exception e) {
            API.verbException(e);
            throw new JsonExecuteException("ScriptExecuteException", e.getLocalizedMessage());
        }
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "Script";
    }

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"type", "String", "脚本类型"}, {"url", "String", "脚本的URL"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"AnyException", "ScriptExecuteException"};
    }
}
