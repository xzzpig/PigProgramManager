package com.xzzpig.pigprogrammanager.command;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.json.JSONTokener;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.CommandInfo;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MakeCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("make", "生成软件库列表文件", new String[]{"<source>", "<target>"}, null, null);

    @Override public String execute(Command cmd) {
        if (cmd.commands.size() < 3)
            return "<source> <target> 不能为空";
        File source = new File(cmd.commands.get(1)), target = new File(cmd.commands.get(2));
        if (!source.exists())
            return "文件" + source.getAbsolutePath() + " 不存在";
        try (FileReader fileReader = new FileReader(source); FileWriter fileWriter = new FileWriter(target, false); Scanner scanner = new Scanner(fileReader)) {
            String url_s;
            URL url = null;
            while (scanner.hasNextLine()) {
                url_s = scanner.nextLine();
                if (url_s.equals(""))
                    continue;
                try {
                    url = new URL(url_s);
                } catch (MalformedURLException e) {
                    API.echo("!" + url_s + " 不是有效的URL(已跳过)");
                }
                if (url == null)
                    continue;
                StringBuffer stringBuffer = new StringBuffer();
                try (InputStream inputStream = url.openStream()) {
                    JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
                    jsonObject = API.digestSetupJson(jsonObject);
                    jsonObject.put("url", url_s);
                    String string = jsonObject.toString();
                    stringBuffer.append('!').append("已添加:").append(string);
                    fileWriter.write(string + "\n");
                    API.echo(stringBuffer);
                    stringBuffer.delete(0, stringBuffer.length() - 1);
                } catch (Exception e) {
                    API.verbException(e);
                    API.echo("处理", url_s, "发生错误:", e.getMessage());
                }
                url = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "文件" + source.getAbsolutePath() + " 不存在";
        } catch (IOException e) {
            e.printStackTrace();
            return "IO发生错误";
        }
        API.echo("处理完成");
        return null;
    }

    @Override public CommandInfo info() {
        return COMMAND_INFO;
    }
}
