package com.lsl.multidatasource.comm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解设置所使用的数据库
 * 
 * @author liusonglin
 * @date 2018年6月14日
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetDataSource {
	
	/**
	 * 使用约定的dataSourceKey
	 */
	DataSourceKey dataSourceKey() default DataSourceKey.DEFAULT;
	/**
	 * 使用自定义的dataSourceKey
	 */
	String customDataSourceKey() default "";
}
