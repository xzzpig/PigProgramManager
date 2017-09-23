package com.xzzpig.pigprogrammanager.api;

import com.github.xzzpig.pigutils.annoiation.NotNull;

import java.util.Arrays;

public class Variable {
    @NotNull
    public final String raw;
    @NotNull
    public final String name;
    @NotNull
    public final String[] args;

    Variable(String content) {
        raw = "${" + content + "}";
        String[] strs = content.split(":", 2);
        name = strs[0];
        if (strs.length > 1)
            args = strs[1].split(",");
        else
            args = new String[0];
    }

    @Override
    public String toString() {
        return "Variable{" + name + "(" + Arrays.toString(args) + ")}";
    }
}
