package com.xzzpig.pigprogrammanager.json;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.JsonExecuteException;
import com.xzzpig.pigprogrammanager.api.JsonExecutor;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipJE implements JsonExecutor {
    /**
     * 解压缩zip包
     *
     * @param zipFilePath   zip文件的全路径
     * @param unzipFilePath 解压后的文件保存的路径
     * @param noname        解压后的文件保存的路径是否包含压缩文件的文件名。true-不包含；false-包含
     */
    public static void unzip(String zipFilePath, String unzipFilePath, boolean noname) throws JsonExecuteException {
        int count = -1;
        int buffer = 1024;
        File file = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        unzipFilePath += "/";
        if (!noname) {
            String fileName = new File(zipFilePath).getName();
            if (!fileName.equals("")) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            unzipFilePath = unzipFilePath + fileName + "/";
        }
        //savepath = path.substring(0, path.lastIndexOf(".")) + File.separator; //保存解压文件目录
        File unzipFile2 = new File(unzipFilePath);
        if (!unzipFile2.exists() && !unzipFile2.mkdirs()) //创建保存目录
            throw new JsonExecuteException("UnzipFailedException", "无法创建目标文件夹:" + unzipFilePath);
        if (!API.needConfirm("将文件" + new File(zipFilePath).getAbsolutePath(), "解压到" + unzipFile2.getAbsolutePath())) {
            throw new JsonExecuteException("UnzipFailedException", "ConfirmCanceled");
        }
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFilePath, Charset.defaultCharset());//解决中文乱码问题
            Enumeration<?> entries = zipFile.entries();//.getEntries();
            while (entries.hasMoreElements()) {
                byte buf[] = new byte[buffer];
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String filename = entry.getName();
                boolean mkdir = false;
                if (filename.lastIndexOf("/") != -1) { //检查此文件是否带有文件夹
                    mkdir = true;
                }
                filename = unzipFilePath + filename;
                if (entry.isDirectory()) { //如果是文件夹先创建
                    file = new File(filename);
                    file.mkdirs();
                    continue;
                }
                file = new File(filename);
                if (!file.exists()) { //如果是目录先创建
                    if (mkdir) {
                        new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); //目录先创建
                    }
                }
                file.createNewFile(); //创建文件
                is = zipFile.getInputStream(entry);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, buffer);
                while ((count = is.read(buf)) > -1) {
                    bos.write(buf, 0, count);
                }
                bos.flush();
                bos.close();
                fos.close();
                is.close();
            }
            zipFile.close();

        } catch (IOException ioe) {
            API.verbException(ioe);
            throw new JsonExecuteException("UnzipFailedException", "解压" + unzipFilePath + "失败");
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    @Override public boolean execute(JSONObject jsonObject) throws JsonExecuteException {
        if (!jsonObject.has("file"))
            return false;
        if (!jsonObject.has("to"))
            return false;
        try {
            unzip(jsonObject.optString("file"), jsonObject.optString("to"), jsonObject.optBoolean("noname", true));
        } catch (JsonExecuteException e) {
            throw e;
        }
        return true;
    }

    /**
     * @return 名称
     */
    @Override public String name() {
        return "Unzip";
    }

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @Override public String[][] getJsonObjectHelp() {
        return new String[][]{{"file", "String", "文件路径"}, {"to", "String", "目标路径"}, {"noname", "boolean", "是否包含压缩文件的文件名"}};
    }

    @Override public String[] getJsonExecuteExceptions() {
        return new String[]{"UnzipFailedException"};
    }
}
