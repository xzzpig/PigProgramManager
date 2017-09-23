package com.xzzpig.pigprogrammanager.command;

import com.github.xzzpig.pigutils.json.JSONObject;
import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Command;
import com.xzzpig.pigprogrammanager.api.CommandExecutor;
import com.xzzpig.pigprogrammanager.api.CommandInfo;

public class ShowCMD implements CommandExecutor {
    private static final CommandInfo COMMAND_INFO = new CommandInfo("show", "显示软件信息", new String[]{"<program>"}, null, null);

    @Override public String execute(Command cmd) {
        if (cmd.commands.size() < 2)
            return "<program>不可为空";
        String program = cmd.commands.get(1);
        cmd.signMap.put("program", program);
        JSONObject jsonObject;
        try {
            jsonObject = API.downloadProgramJson(program);
        } catch (Exception e) {
            API.verbException(e);
            return "Program安装说明文件下载失败";
        }
        API.echo(program, "软件信息:");
        API.echo('\t', "软件别名:" + jsonObject.optString("alias", "null"));
        API.echo('\t', "官网:" + jsonObject.optString("homepage", "null"));
        API.echo('\t', "最新版本:" + jsonObject.optString("version", "null"));
        API.echo('\t', "软件介绍:" + jsonObject.optString("introduce", "null"));
        JSONObject license = jsonObject.optJSONObject("license");
        if (license != null)
            API.echo('\t', "软件协议:" + license.optString("name", "null"));
        JSONObject size = jsonObject.optJSONObject("size");
        if (size != null) {
            API.echo('\t', "软件大小:");
            API.echo('\t', '\t', "下载:" + size.optString("download", "unknown"));
            API.echo('\t', '\t', "安装:" + size.optString("install", "unknown"));
        }
        API.echo('\t', "软件依赖:" + jsonObject.optString("depends", "null"));
        return null;
    }

    @Override public CommandInfo info() {
        return COMMAND_INFO;
    }
}
