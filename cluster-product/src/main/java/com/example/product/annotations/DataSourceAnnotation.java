package com.example.product.annotations;

import com.example.common.constant.DataSourceType;

import java.lang.annotation.*;

/**
 * 接口可以被继承/实现
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceAnnotation {
    /**
     * 切换数据源名称
     */
    DataSourceType value() default DataSourceType.MASTER;
}
