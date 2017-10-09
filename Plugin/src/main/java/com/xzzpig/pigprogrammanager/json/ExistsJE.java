package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import java.io.File;
import java.util.Arrays;

public class ExistsJE implements JsonExecutor {
    private static boolean hasFileInDir(File dir, String fileReg) {
        return Arrays.asList(dir.listFiles()).stream().map(File::getName).anyMatch(str->str.matches(fileReg));
    }

    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        String fileReg = jsonObject.optString("fileReg");
        JSONArray dirs = jsonObject.optJSONArray("dirs");
        if (fileReg == null || dirs == null)
            return false;
        if (!dirs.toList().stream().map(obj->obj + "").map(File::new).filter(File::exists).filter(File::isDirectory).anyMatch(file->hasFileInDir(file, fileReg))) {
            String failError = jsonObject.optString("failError");
            String failMsg = jsonObject.optString("failMsg");
            throw new JsonExecuteException(failError, failMsg);
        }
        return true;
    }

    @Override public String name() {
        return "Exists";
    }

    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"fileReg", "Reg", "要判断的文件名称的正则"}, {"dirs", "JsonArray<String>", "查找的路径"}, {"failError", "String", ""}, {"failMsg", "String", ""}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"AnyException"};
    }
}
