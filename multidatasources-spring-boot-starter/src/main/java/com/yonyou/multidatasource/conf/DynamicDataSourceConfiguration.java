package com.yonyou.multidatasource.conf;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import com.yonyou.multidatasource.comm.DynamicRoutingDataSource;

/**
 * 动态设置数据源
 * 
 * @author liusonglin
 * @date 2018年6月14日
 */

@Configuration
@SuppressWarnings(value = { "all" })
public class DynamicDataSourceConfiguration {

	@Autowired
	private Environment env;

	@Autowired
	private DataSourceProperties dataSourceProperties;

	@Bean
	public DataSource dynamicDataSource() {
		DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
		Map<Object, Object> dataSourceMap = new HashMap<>();
		// 约定的配置
		String[] appoint = { "default", "two", "three" };
		for (String name : appoint) {
			String property = "multi.datasource." + name;
			String driverclass = env.getProperty(property + ".driverclass");
			String password = env.getProperty(property + ".password");
			String url = env.getProperty(property + ".url");
			String username = env.getProperty(property + ".username");
			if(driverclass != null && url != null && username != null) {
				DriverManagerDataSource dataSource2 = new DriverManagerDataSource();
				dataSource2.setDriverClassName(driverclass);
				dataSource2.setPassword(password);
				dataSource2.setUrl(url);
				dataSource2.setUsername(username);
				dataSourceMap.put(name, dataSource2);
			}
		}

		// 自定义扩展的配置
		String customNames = env.getProperty("multi.datasource.custom");
		System.out.println(customNames);
		String[] names = customNames.split(",");
		DriverManagerDataSource dataSource2 = new DriverManagerDataSource();
		for (String name : names) {
			String property = "multi.datasource." + name;
			String password = env.getProperty(property + ".password");
			dataSource2.setDriverClassName(env.getProperty(property + ".driverclass"));
			dataSource2.setPassword(password);
			dataSource2.setUrl(env.getProperty(property + ".url"));
			dataSource2.setUsername(env.getProperty(property + ".username"));
			dataSourceMap.put(name, dataSource2);
		}
		// 把datasource 以键值对存放到放入目标源
		dataSource.setTargetDataSources(dataSourceMap);
		return dataSource;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dynamicDataSource());
		// 此处设置为了解决找不到mapper文件的问题
		// sqlSessionFactoryBean
		// .setMapperLocations(new
		// PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
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