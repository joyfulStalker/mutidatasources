package com.yonyou.multidatasource.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.yonyou.multidatasource.conf.DynamicDataSourceConfiguration;

/**
 * 获取当前数据源
 * 
 * @author liusonglin
 * @date 2018年6月14日
 */

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
	
	private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceConfiguration.class);
	
	@Autowired
	private Environment env;

	@Override
	protected Object determineCurrentLookupKey() {

		String isEnable = env.getProperty("multi.datasource.enable-dynamic");
		if (StringUtils.isEmpty(isEnable) || !Boolean.parseBoolean(isEnable)) {// 未开启多数据源
			return TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();// 默认实现
		} else {
			if (null == DynamicDataSourceHolder.get()) {
				logger.info("当前为默认数据源");
				return "default";
			} else {
				return DynamicDataSourceHolder.get();
			}
		}
	}
}