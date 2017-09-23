package com.xzzpig.pigprogrammanager.api;

import com.github.xzzpig.pigutils.annoiation.API;
import com.github.xzzpig.pigutils.annoiation.Const;
import com.github.xzzpig.pigutils.annoiation.NotNull;

@API
public interface CommandExecutor {
    /**
     * @return execute result,null if success,else error msg
     */
    String execute(Command cmd);

    @NotNull
    @Const(constField = true)
    CommandInfo info();
}
