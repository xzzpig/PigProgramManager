package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadJE implements JsonExecutor {
    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("url"))
            return false;
        if (!jsonObject.has("save"))
            return false;
        URL url;
        try {
            url = new URL(jsonObject.optString("url"));
        } catch (MalformedURLException e) {
            throw new JsonExecuteException("MalformedURLException", e.getMessage());
        }
        File saveFile = new File(jsonObject.optString("save"));
        if (saveFile.isDirectory())
            throw new JsonExecuteException("IllegalArgumentException", "参数save应该为文件路径");
        File par = saveFile.getParentFile();
        if (par != null) par.mkdirs();
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
            }
        }
        API.echo("准备下载...", "!" + jsonObject.getString("url"));
        URLConnection connection = null;
        for (int i = 0; i < 5; i++) {
            try {
                connection = url.openConnection();
                break;
            } catch (IOException e) {
                if (i < 4) {
                    API.echo("连接失败,正在重试");
                    continue;
                }
                API.verbException(e);
                throw new JsonExecuteException("URLConnectionOpenException", e.getLocalizedMessage());
            }
        }
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        try (InputStream in = new BufferedInputStream(connection.getInputStream()); OutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile))) {
            long size_all = connection.getContentLengthLong();
            API.needConfirm("从" + url, "将下载" + (size_all / 1024) + "KB", "到" + saveFile.getAbsolutePath());
            int size_per, len, process = 0;
            if (size_all == -1) size_per = 1024;
            else size_per = (int) (size_all / 99);
            byte[] bytes = new byte[size_per];
            System.out.print('[');
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
                process++;
                if (process % 10 == 0)
                    System.out.print('*');
            }
            out.flush();
            System.out.println(']');
        } catch (IOException e) {
            throw new JsonExecuteException("DownloadException", e.getLocalizedMessage());
        }
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "Download";
    }

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"url", "String", "下载地址"}, {"save", "String", "保存路径"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"MalformedURLException", "IllegalArgumentException", "DownloadException", "URLConnectionOpenException"};
    }
}
