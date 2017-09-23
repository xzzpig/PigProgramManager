package com.xzzpig.pigprogrammanager.api;

import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;

/**
 * 提供变量内容
 */
public interface VariableProvider {

    /**
     * 与 {@link Variable#name} 对应
     *
     * @return 提供变量的名称
     */
    @NotNull
    String name();

    /**
     * @param args 与{@link Variable#args} 对于
     * @return 变量内容(null是表示此Provider不无法提供此变量内容)
     */
    @Nullable
    String provide(@NotNull String... args);
}
