package com.xzzpig.pigprogrammanager.variableprovider;

import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.VariableProvider;

import java.net.URL;

public class URLVP implements VariableProvider {
    @Override public String name() {
        return "URL";
    }

    @Override public String provide(String... args) {
        if (args.length < 1)
            return null;
        switch (args[0]) {
            case "JSON":
                if (API.getCommand() != null && API.getCommand().signMap.containsKey("URL_JSON"))
                    return API.getCommand().signMap.get("URL_JSON");
                else return null;
            default:
                return null;
        }
    }
}
