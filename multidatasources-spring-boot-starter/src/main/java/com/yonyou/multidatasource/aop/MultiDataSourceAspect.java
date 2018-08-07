package com.yonyou.multidatasource.aop;

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

import com.yonyou.multidatasource.comm.DataSourceKey;
import com.yonyou.multidatasource.comm.DynamicDataSourceHolder;
import com.yonyou.multidatasource.comm.TargetDataSource;


/**
* 切面获取注解配置的数据源
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
		if(!StringUtils.isEmpty(targetDataSource.customDataSourceKey())) {
			DynamicDataSourceHolder.set(targetDataSource.customDataSourceKey());
			logger.info("设置数据源：" + targetDataSource.customDataSourceKey() + "为当前数据源");
		}else {
			DynamicDataSourceHolder.set(targetDataSource.dataSourceKey().toString());
			logger.info("设置数据源：" + targetDataSource.dataSourceKey() + "为当前数据源");
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
		DynamicDataSourceHolder.clear();
		logger.info("已清除当前数据源" + dataSourceKey);
	}
}
