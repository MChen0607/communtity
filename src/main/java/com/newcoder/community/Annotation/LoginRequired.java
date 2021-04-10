package com.newcoder.community.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //作用的范围
@Retention(RetentionPolicy.RUNTIME) //有效的时长
public @interface LoginRequired {

}
