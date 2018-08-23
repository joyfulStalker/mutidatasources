package com.yonyou.multidatasource.conf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.yonyou.multidatasource.comm.DefaultDruidDataSourceConf;
import com.yonyou.multidatasource.comm.DynamicRoutingDataSource;

/**
 * 动态设置数据源
 * 
 * @author liusonglin
 * @date 2018年6月14日
 */

@Configuration
public class DynamicDataSourceConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceConfiguration.class);

	@Autowired
	private Environment env;

	@Bean
	public DataSource dynamicDataSource() {
		DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
		Map<Object, Object> dataSourceMap = new HashMap<>();// 存放自定义数据源和约定数据源
		DefaultDruidDataSourceConf defaultDruidDataSourceConf = new DefaultDruidDataSourceConf();
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
					throw new RuntimeException("默认数据源必配");
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
		BeanUtils.copyProperties(defaultDruidDataSourceConf, druidDataSource);
		druidDataSource.setName(name);// 如果存在多个数据源，监控的时候可以通过名字来区分开来
		druidDataSource.setDriverClassName(driverclass);
		druidDataSource.setPassword(password);
		druidDataSource.setUrl(url);
		druidDataSource.setUsername(username);
		dataSourceMap.put(name, druidDataSource);
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dynamicDataSource());
		// 多数据源设置会使mybatis的mybatis.mapper-locations配置失效，此处设置为了解决找该问题
		String mapperLocationsProperty = env.getProperty("mybatis.mapper-locations");
		if (!StringUtils.isEmpty(mapperLocationsProperty)) {
			sqlSessionFactoryBean.setMapperLocations(
					new PathMatchingResourcePatternResolver().getResources(mapperLocationsProperty));
		}

		return sqlSessionFactoryBean.getObject();
	}

	@Bean
	public SqlSessionTemplate sqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory());
	}

	/**
	 * 事务管理
	 *
	 * @return 事务管理实例
	 */
	@Bean
	public PlatformTransactionManager platformTransactionManager() {
		return new DataSourceTransactionManager(dynamicDataSource());
	}
}