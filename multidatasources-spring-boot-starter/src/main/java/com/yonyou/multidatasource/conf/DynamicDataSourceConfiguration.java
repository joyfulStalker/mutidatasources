package com.yonyou.multidatasource.conf;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import com.yonyou.multidatasource.comm.DataSourceKey;
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
	private DataSourceProperties dataSourceProperties;

	@Bean
	@ConfigurationProperties(prefix = "multi.datasource.default")
	public DataSource dbDefault() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(dataSourceProperties.determineDriverClassName());
		dataSource.setPassword(dataSourceProperties.determinePassword());
		dataSource.setUrl(dataSourceProperties.determineUrl());
		dataSource.setUsername(dataSourceProperties.determineUsername());
		return dataSource;
	}

	@Bean
	@ConfigurationProperties(prefix = "multi.datasource.two")
	public DataSource dbTwo() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(dataSourceProperties.determineDriverClassName());
		dataSource.setPassword(dataSourceProperties.determinePassword());
		dataSource.setUrl(dataSourceProperties.determineUrl());
		dataSource.setUsername(dataSourceProperties.determineUsername());
		return dataSource;
	}
	@Bean
	@ConfigurationProperties(prefix = "multi.datasource.three")
	public DataSource dbThree() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(dataSourceProperties.determineDriverClassName());
		dataSource.setPassword(dataSourceProperties.determinePassword());
		dataSource.setUrl(dataSourceProperties.determineUrl());
		dataSource.setUsername(dataSourceProperties.determineUsername());
		return dataSource;
	}
	@Bean
	@ConfigurationProperties(prefix = "multi.datasource.four")
	public DataSource dbFour() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(dataSourceProperties.determineDriverClassName());
		dataSource.setPassword(dataSourceProperties.determinePassword());
		dataSource.setUrl(dataSourceProperties.determineUrl());
		dataSource.setUsername(dataSourceProperties.determineUsername());
		return dataSource;
	}
	@Bean
	@ConfigurationProperties(prefix = "multi.datasource.five")
	public DataSource dbFive() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(dataSourceProperties.determineDriverClassName());
		dataSource.setPassword(dataSourceProperties.determinePassword());
		dataSource.setUrl(dataSourceProperties.determineUrl());
		dataSource.setUsername(dataSourceProperties.determineUsername());
		return dataSource;
	}

	@Bean
	public DataSource dynamicDataSource() {
		DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
		dataSource.setDefaultTargetDataSource(dbDefault());
		Map<Object, Object> dataSourceMap = new HashMap<>(4);
		
//		DynamicDataSourceConfiguration.class.getDeclaredAnnotation(annotationClass)
		
		dataSourceMap.put(DataSourceKey.DB_DEFAULT, dbDefault());
		dataSourceMap.put(DataSourceKey.DB_TWO, dbTwo());
		dataSourceMap.put(DataSourceKey.DB_THREE, dbThree());
		dataSourceMap.put(DataSourceKey.DB_FOUR, dbFour());
		dataSourceMap.put(DataSourceKey.DB_FIVE, dbFive());
		dataSource.setTargetDataSources(dataSourceMap);
		return dataSource;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dynamicDataSource());
		// 此处设置为了解决找不到mapper文件的问题
//		sqlSessionFactoryBean
//				.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
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