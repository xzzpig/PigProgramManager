package com.xzzpig.pigprogrammanager.api;

import com.github.xzzpig.pigutils.annoiation.API;
import com.github.xzzpig.pigutils.annoiation.ArraySize;
import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

import java.util.Arrays;

@API
public class CommandInfo {
    @NotNull
    public final String cmd;
    @NotNull
    public final String describe;
    @NotNull
    public final String[][] signs;
    @NotNull
    public final String[][] signMap;
    @NotNull
    public final String[] args;

    /**
     * @param cmd      命令名称
     * @param describe 命令说明
     * @param args     软件参数(<>为必须,[]为非必须)
     * @param signs    无参数命令标志说明(不含'-'),[*][0]为标志,[*][1]为标志说明
     * @param signMap  带参数命令标志说明(不含'-'),[*][0]为标志,[*][1]为参数名称,[*][2]为标志说明
     */
    public CommandInfo(@NotNull String cmd, @Nullable String describe, @Nullable String[] args, @Nullable @ArraySize({-1, 2}) String[][] signs, @Nullable @ArraySize({-1, 3}) String[][] signMap) {
        this.cmd = cmd;
        this.describe = describe == null ? "" : describe;
        this.args = args == null ? new String[0] : args;
        this.signs = signs == null ? new String[0][2] : signs;
        this.signMap = signMap == null ? new String[0][3] : signMap;
    }

    @Override
    @NotNull
    public String toString() {
        StringBuffer sb = new StringBuffer("\t");
        sb.append(cmd);
        for (String arg : args)
            sb.append(' ').append(arg);
        sb.append('\t').append(describe);
        if (signs.length > 0) {
            sb.append('\n').append('\t').append('\t').append("无参数标志:");
            Arrays.asList(signs).forEach(strs->sb.append('\n').append('\t').append('\t').append('-').append(strs[0]).append('\t').append(strs[1]));
        }
        if (signMap.length > 0) {
            sb.append('\n').append('\t').append('\t').append("有参数标志:");
            Arrays.asList(signMap).forEach(strs->sb.append('\n').append('\t').append('\t').append('-').append(strs[0]).append(':').append(strs[1]).append('\t').append(strs[2]));
        }
        return sb.toString();
    }
}
