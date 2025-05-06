package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义一个注解，用于标识某个方法需要自动字段填充处理
 */
@Target(ElementType.METHOD)//指定注解只能添加到方法上
@Retention(RetentionPolicy.RUNTIME)//在运行时保留，可以通过反射获取
public @interface AutoFill {
    //指定数据库操作类型
    OperationType value();//可以通过反射读取,因为OperationType只有INSERT和UPDATE两种格式，可以在使用时得到操作的类型，因为这两个类型的自动插入操作也不同
}
