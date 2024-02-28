package com.czc.Config.annotation;

import java.lang.annotation.*;

/**
 * 该注解用于标记一个方法，在运行时记录其执行时间
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CostTime {

}

