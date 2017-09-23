package com.xzzpig.pigprogrammanager.variableprovider;

import com.xzzpig.pigprogrammanager.api.API;
import com.xzzpig.pigprogrammanager.api.Variable;
import com.xzzpig.pigprogrammanager.api.VariableProvider;

public class VarVP implements VariableProvider {
    /**
     * 与 {@link Variable#name} 对应
     *
     * @return 提供变量的名称
     */
    @Override public String name() { return "Var"; }

    /**
     * @param args 与{@link Variable#args} 对于
     * @return 变量内容(null是表示此Provider不无法提供此变量内容)
     */
    @Override
    public String provide(String... args) {
        if (args.length < 1)
            return null;
        String key = args[0];
        String value = API.getCommand().signMap.get(key);
        if (value == null)
            value = API.getCommand().hasSign(key) + "";
        return value;
    }
}
