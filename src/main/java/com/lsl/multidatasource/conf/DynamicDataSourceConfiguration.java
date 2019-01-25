package com.lsl.multidatasource.conf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.lsl.multidatasource.comm.DefaultDruidDataSourceConf;
import com.lsl.multidatasource.comm.DynamicRoutingDataSource;
import com.lsl.multidatasource.comm.MultiDruidDataSourceWrapper;

/**
 * 动态设置数据源
 * 
 * @author liusonglin
 * @date 2018年6月14日
 */

@Configuration
@EnableConfigurationProperties({ DataSourceProperties.class, DefaultDruidDataSourceConf.class })
public class DynamicDataSourceConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceConfiguration.class);

	@Autowired
	DefaultDruidDataSourceConf defaultDruidDataSourceConf;

	@Autowired
	private Environment env;

	@Bean
	public DataSource dynamicDataSource() {

		// 多数据源开关
		String isEnable = env.getProperty("multi.datasource.enable-dynamic");
		if (StringUtils.isEmpty(isEnable) || !Boolean.parseBoolean(isEnable)) {// 未开启多数据源
			// 由于德鲁伊的DruidDataSourceWrapper类是受保护的，不能引用，故参照DruidDataSourceWrapper定义MultiDruidDataSourceWrapper
			MultiDruidDataSourceWrapper druidDataSourceWrapper = new MultiDruidDataSourceWrapper();
			druidDataSourceWrapper.setDriverClassName(env.getProperty("spring.datasource.driverClassName"));
			druidDataSourceWrapper.setPassword(env.getProperty("spring.datasource.password"));
			druidDataSourceWrapper.setUrl(env.getProperty("spring.datasource.url"));
			druidDataSourceWrapper.setUsername(env.getProperty("spring.datasource.username"));
			return druidDataSourceWrapper;
		}

		DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
		Map<Object, Object> dataSourceMap = new HashMap<>();// 存放自定义数据源和约定数据源
		// 约定的配置
		String[] appoint = { "default", "master", "slave" };
		for (String name : appoint) {
			String property = "multi.datasource." + name;
			String driverclass = env.getProperty(property + ".driverclass");
			String url = env.getProperty(property + ".url");
			String username = env.getProperty(property + ".username");
			String password = env.getProperty(property + ".password");
			if (driverclass != null && url != null && username != null) {
				dealDruidConf(dataSourceMap, defaultDruidDataSourceConf, name, driverclass, url, username, password);
			} else {
				if ("default".equals(name)) {
					throw new RuntimeException("if you had enabled dynamic datasource,please config the default ...");
				}
			}
		}

		// 自定义扩展的配置
		String customNames = env.getProperty("multi.datasource.custom");
		if (!StringUtils.isEmpty(customNames)) {
			String[] names = customNames.split(",");
			logger.info("自定义数据源：" + Arrays.toString(names));

			for (String name : names) {
				String property = "multi.datasource." + name;
				dealDruidConf(dataSourceMap, defaultDruidDataSourceConf, name,
						env.getProperty(property + ".driverclass"), env.getProperty(property + ".url"),
						env.getProperty(property + ".username"), env.getProperty(property + ".password"));
			}
		}
		// 把datasource 以键值对存放到放入目标源
		dataSource.setTargetDataSources(dataSourceMap);
		return dataSource;
	}

	private void dealDruidConf(Map<Object, Object> dataSourceMap, DefaultDruidDataSourceConf defaultDruidDataSourceConf,
			String name, String driverclass, String url, String username, String password) {
		DruidDataSource druidDataSource = new DruidDataSource();
		// 先改变下MinEvictableIdleTimeMillis，druid设置MinEvictableIdleTimeMillis和MaxEvictableIdleTimeMillis有点问题
		// 导致设置MaxEvictableIdleTimeMillis时候报maxEvictableIdleTimeMillis must be grater
		// than minEvictableIdleTimeMillis错误
		// 详情参考https://www.cnblogs.com/wangiqngpei557/p/7136455.html?utm_source=itdadao&utm_medium=referral
		druidDataSource.setMinEvictableIdleTimeMillis(defaultDruidDataSourceConf.getMinEvictableIdleTimeMillis());
		BeanUtils.copyProperties(defaultDruidDataSourceConf, druidDataSource);
		druidDataSource.setName(name);// 如果存在多个数据源，监控的时候可以通过名字来区分开来
		druidDataSource.setDriverClassName(driverclass);
		druidDataSource.setPassword(password);
		druidDataSource.setUrl(url);
		druidDataSource.setUsername(username);
		dataSourceMap.put(name, druidDataSource);
	}

}