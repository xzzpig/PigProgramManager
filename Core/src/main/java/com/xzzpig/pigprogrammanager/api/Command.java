package com.xzzpig.pigprogrammanager.api;

import com.github.xzzpig.pigutils.annoiation.Const;
import com.github.xzzpig.pigutils.annoiation.NotNull;

import java.util.*;

public class Command {
    @NotNull
    public final List<String> commands, signs;
    @NotNull
    public final Map<String, String> signMap;

    @NotNull
    public final Map<String, Object> data = new Hashtable<>();
    @NotNull
    @Const(constReference = true)
    public String cmd;


    /**
     * @throws IllegalArgumentException 当args中没有命令时抛出
     */
    Command(String[] args) {
        commands = new ArrayList<>();
        this.signs = new ArrayList<>();
        signMap = new HashMap<>();
        String sign = null;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                String[] sign2 = arg.replaceFirst("-", "").split(":", 2);
                if (sign2.length == 1)
                    signs.add(sign2[0]);
                else
                    signMap.put(sign2[0], sign2[1]);
            } else {
                if (cmd == null)
                    cmd = arg;
                commands.add(arg);
            }
        }
        if (cmd == null)
            throw new IllegalArgumentException("no Command in these signs");
    }

    public boolean hasSign(String sign) {
        return signs.contains(sign) || signMap.containsKey(sign);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        signs.forEach(sign->sb.append("-").append(sign).append(' '));
        signMap.forEach((key, value)->sb.append("-").append(key).append(':').append(value).append(' '));
        commands.forEach(cmd->sb.append(cmd).append(' '));
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Command && obj.toString().equals(this.toString());
    }
}
