package com.yonyou.multidatasource.comm;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 获取当前数据源
 * 
 * @author liusonglin
 * @date 2018年6月14日
 */

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return DynamicDataSourceHolder.get();
	}
}