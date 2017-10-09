package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import java.io.File;
import java.util.Arrays;

public class ExistInPathJE implements JsonExecutor {
    private static boolean hasFileInDir(File dir, String fileReg) {
        return Arrays.asList(dir.listFiles()).stream().map(File::getName).anyMatch(str->str.matches(fileReg));
    }

    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        jsonObject.put("name", "Exists");
        JSONArray path = new JSONArray(Arrays.asList(API.getPath()));
        jsonObject.put("dirs", path);
        try {
            API.executeJsonObject("Exists", jsonObject);
        } catch (JsonExecuteException e) {
            if ("NoJsonExecutorExecuted".equalsIgnoreCase(e.name))
                return false;
            throw e;
        }
        return true;
    }

    @Override public String name() {
        return "ExistInPath";
    }

    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"fileReg", "Reg", "要判断的文件名称的正则"}, {"failError", "String", ""}, {"failMsg", "String", ""}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"AnyException"};
    }
}
