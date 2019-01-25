package com.lsl.multidatasource.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.lsl.multidatasource.comm.DynamicDataSourceHolder;
import com.lsl.multidatasource.comm.TargetDataSource;

/**
 * 切面获取注解配置的数据源
 * 
 * @author liusonglin
 * @date 2018年6月14日
 */

@Aspect
@Order(-1)
@Configuration
public class MultiDataSourceAspect {

	private static final Logger logger = LoggerFactory.getLogger(MultiDataSourceAspect.class);

	@Autowired
	private Environment env;

	@Pointcut("execution(* *..*.*(..))")
	public void pointCut() {
	}

	/**
	 * 执行方法前更换数据源
	 *
	 * @param joinPoint
	 *            切点
	 * @param targetDataSource
	 *            动态数据源
	 */
	@Before("@annotation(targetDataSource)")
	public void doBefore(JoinPoint joinPoint, TargetDataSource targetDataSource) {
		String isEnable = env.getProperty("multi.datasource.enable-dynamic");
		if (StringUtils.isEmpty(isEnable) || !Boolean.parseBoolean(isEnable)) {// 未开启多数据源
			return;
		}

		if (!StringUtils.isEmpty(targetDataSource.customDataSourceKey())) {
			String property = env.getProperty("multi.datasource.custom");
			if (!StringUtils.isEmpty(property)
					&& Arrays.asList(property.split(",")).contains(targetDataSource.customDataSourceKey())) {
				DynamicDataSourceHolder.set(targetDataSource.customDataSourceKey());
				logger.info("the current datasource is: " + targetDataSource.customDataSourceKey());
			} else {
				throw new RuntimeException(
						"please confirm config this DataSource：" + targetDataSource.customDataSourceKey());
			}
		} else {
			DynamicDataSourceHolder.set(targetDataSource.dataSourceKey().toString().toLowerCase());
			logger.info("the current datasource is: " + targetDataSource.dataSourceKey().toString().toLowerCase());
		}
	}

	/**
	 * 执行方法后清除数据源设置
	 *
	 * @param joinPoint
	 *            切点
	 * @param targetDataSource
	 *            动态数据源
	 */
	@After("@annotation(targetDataSource)")
	public void doAfter(JoinPoint joinPoint, TargetDataSource targetDataSource) {
		String dataSourceKey = DynamicDataSourceHolder.get();
		if (!StringUtils.isEmpty(dataSourceKey)) {
			DynamicDataSourceHolder.clear();
			logger.info("Current data sources have been cleared: " + dataSourceKey);
		}
	}
}
