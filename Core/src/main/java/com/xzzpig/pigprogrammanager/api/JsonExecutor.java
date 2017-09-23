package com.xzzpig.pigprogrammanager.api;

import com.github.xzzpig.pigutils.annoiation.API;
import com.github.xzzpig.pigutils.annoiation.ArraySize;
import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.json.JSONObject;

@API
public interface JsonExecutor {

    /**
     * @return 是否执行了此jsonObject
     * @throws JsonExecuteException json执行错误
     */
    boolean execute(JSONObject jsonObject) throws JsonExecuteException;

    /**
     * @return 名称
     */
    String name();

    /**
     * ret[*][0]: jsonObject的key<br/>
     * ret[*][1]: key 的类型<br/>
     * ret[*][2]: key 的说明
     *
     * @return 可执行的jsonObject的说明
     */
    @ArraySize({-1, 3})
    @NotNull
    String[][] getJsonObjectHelp();

    /**
     * @return 此Executor可能抛出的异常名称
     */
    default @NotNull
    String[] getJsonExecuteExceptions() {
        return new String[0];
    }

    /**
     * @return 该 JsonExecutor执行等级,值越小，在同名的JsonExecutor中执行等级越高
     */
    default int getExecuteLevel() {return 10;}
}
