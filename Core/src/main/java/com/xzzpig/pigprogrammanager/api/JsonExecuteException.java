package com.xzzpig.pigprogrammanager.api;

public class JsonExecuteException extends Exception {
    public final String name;
    public final String msg;

    public JsonExecuteException(String name, String msg) {
        this.name = name;
        this.msg = msg;
    }
}
