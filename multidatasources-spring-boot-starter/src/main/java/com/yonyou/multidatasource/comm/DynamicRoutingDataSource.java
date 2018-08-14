package com.yonyou.multidatasource.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.yonyou.multidatasource.conf.DynamicDataSourceConfiguration;

/**
 * 获取当前数据源
 * 
 * @author liusonglin
 * @date 2018年6月14日
 */

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
	private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceConfiguration.class);

	@Override
	protected Object determineCurrentLookupKey() {
		
		if(null == DynamicDataSourceHolder.get()) {
			logger.info("當前數據源為默認數據源");
			return "default";
		}else {
			return DynamicDataSourceHolder.get();
		} 
	}
}