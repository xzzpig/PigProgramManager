package com.xzzpig.pigprogrammanager.api;

import java.lang.annotation.*;

/**
 * 表示该对象/方法在执行命令才可以使用
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.FIELD,ElementType.TYPE})
public @interface InCommand {
    String[] value() default {"*"};
}
