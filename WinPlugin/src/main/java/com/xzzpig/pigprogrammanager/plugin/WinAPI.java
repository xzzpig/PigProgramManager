package com.xzzpig.pigprogrammanager.plugin;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.core.RegistryHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WinAPI {
    public static List<String> getPaths() throws IOException {
        return new ArrayList<>(Arrays.asList(RegistryHelper.getUserPath().split(";")));
    }

    public static boolean hasPath(@NotNull String path) throws IOException {
        return getPaths().contains(path);
    }

    public static void addPath(@NotNull String path) throws IOException {
        if (hasPath(path))
            return;
        RegistryHelper.addUserPath(path);
    }

    public static void removePath(@NotNull String path) throws IOException {
        List<String> paths = getPaths();
        if (!path.contains(path))
            return;
        paths.remove(path);
        StringBuilder stringBuilder = new StringBuilder();
        paths.forEach(p->stringBuilder.append(p).append(';'));
        RegistryHelper.setValue("HKEY_CURRENT_USER\\Environment", "Path", null, stringBuilder.toString());
    }
}
