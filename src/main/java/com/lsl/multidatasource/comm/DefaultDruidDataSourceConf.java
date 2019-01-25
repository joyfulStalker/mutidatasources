package com.lsl.multidatasource.comm;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.config.ConfigFilter;
import com.alibaba.druid.filter.encoding.EncodingConvertFilter;
import com.alibaba.druid.filter.logging.CommonsLogFilter;
import com.alibaba.druid.filter.logging.Log4j2Filter;
import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.ExceptionSorter;
import com.alibaba.druid.wall.WallFilter;

/**
 * druid 连接池参数默认配置
 * 
 * @author liusonglin
 * @date 2018年8月23日
 */
@ConfigurationProperties(prefix = "multi.datasource.default-config")
public class DefaultDruidDataSourceConf {

	// ------------------------下面是Filter配置,可进行扩展-----------
	@Autowired(required = false)
	public void addStatFilter(StatFilter statFilter) {
		this.proxyFilters.add(statFilter);
	}
	@Autowired(required = false)
    public void addConfigFilter(ConfigFilter configFilter) {
        this.proxyFilters.add(configFilter);
    }

    @Autowired(required = false)
    public void addEncodingConvertFilter(EncodingConvertFilter encodingConvertFilter) {
        this.proxyFilters.add(encodingConvertFilter);
    }

    @Autowired(required = false)
    public void addSlf4jLogFilter(Slf4jLogFilter slf4jLogFilter) {
        this.proxyFilters.add(slf4jLogFilter);
    }

    @Autowired(required = false)
    public void addLog4jFilter(Log4jFilter log4jFilter) {
        this.proxyFilters.add(log4jFilter);
    }

    @Autowired(required = false)
    public void addLog4j2Filter(Log4j2Filter log4j2Filter) {
        this.proxyFilters.add(log4j2Filter);
    }

    @Autowired(required = false)
    public void addCommonsLogFilter(CommonsLogFilter commonsLogFilter) {
        this.proxyFilters.add(commonsLogFilter);
    }

    @Autowired(required = false)
    public void addWallFilter(WallFilter wallFilter) {
        this.proxyFilters.add(wallFilter);
    }
	// ------------------------上面是Filter配置-----------

	/**
	 * 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
	 */
	private int initialSize = 0;

	/**
	 * 最大连接池数量
	 */
	private int maxActive = 50;

	/**
	 * 最小连接池数量
	 */
	private int minIdle = 5;

	/**
	 * 默认设置5s
	 * 获取连接时最大等待时间，单位毫秒,配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁
	 */
	private long maxWait = 5000;

	/**
	 * 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。
	 */
	private boolean poolPreparedStatements = false;

	/**
	 * 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100
	 */
	private int maxPoolPreparedStatementPerConnectionSize = 10;

	/**
	 * 用来检测连接是否有效的sql，要求是一个查询语句。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用。
	 */
	private String validationQuery = "select 1";

	/**
	 * 单位：秒，检测连接是否有效的超时时间。底层调用jdbc Statement对象的void setQueryTimeout(int seconds)方法
	 */
	private int validationQueryTimeout = 5000;

	/**
	 * 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
	 */
	private boolean testOnBorrow = false;

	/**
	 * 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
	 */
	private boolean testOnReturn = false;

	/**
	 * 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
	 */
	private boolean testWhileIdle = true;

	/**
	 * 连接池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作。
	 */
	private boolean keepAlive = false;

	/**
	 * 有两个含义： 1) Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接。
	 * 2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明
	 */
	private long timeBetweenEvictionRunsMilli = 60 * 1000L;

	/**
	 * 连接保持空闲而不被驱逐的最小时间
	 */
	private long minEvictableIdleTimeMillis = 600000L;
	/**
	 * 连接保持空闲而不被驱逐的最大时间
	 */
	private long maxEvictableIdleTimeMillis = 900000L;

	/**
	 * 物理连接初始化的时候执行的sql
	 * 
	 */
	private List<String> connectionInitSqls;

	/**
	 * 当数据库抛出一些不可恢复的异常时，抛弃连接.根据dbType自动识别
	 */
	private ExceptionSorter exceptionSorter = null;

	/**
	 * 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有： 监控统计用的filter:stat 日志用的filter:log4j
	 * 防御sql注入的filter:wall
	 */
//	private List<Filter> filters = new CopyOnWriteArrayList<Filter>();

	/**
	 * 类型是List<com.alibaba.druid.filter.Filter>，如果同时配置了filters和proxyFilters，是组合关系，并非替换关系
	 */
	private List<Filter> proxyFilters = new CopyOnWriteArrayList<Filter>();

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public int getValidationQueryTimeout() {
		return validationQueryTimeout;
	}

	public void setValidationQueryTimeout(int validationQueryTimeout) {
		this.validationQueryTimeout = validationQueryTimeout;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public boolean isPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	public void setPoolPreparedStatements(boolean poolPreparedStatements) {
		this.poolPreparedStatements = poolPreparedStatements;
	}

	public int getMaxPoolPreparedStatementPerConnectionSize() {
		return maxPoolPreparedStatementPerConnectionSize;
	}

	public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
		this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public long getTimeBetweenEvictionRunsMilli() {
		return timeBetweenEvictionRunsMilli;
	}

	public void setTimeBetweenEvictionRunsMilli(long timeBetweenEvictionRunsMilli) {
		this.timeBetweenEvictionRunsMilli = timeBetweenEvictionRunsMilli;
	}

	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public List<String> getConnectionInitSqls() {
		return connectionInitSqls;
	}

	public void setConnectionInitSqls(List<String> connectionInitSqls) {
		this.connectionInitSqls = connectionInitSqls;
	}

	public ExceptionSorter getExceptionSorter() {
		return exceptionSorter;
	}

	public void setExceptionSorter(ExceptionSorter exceptionSorter) {
		this.exceptionSorter = exceptionSorter;
	}

	public List<Filter> getProxyFilters() {
		return proxyFilters;
	}

	public void setProxyFilters(List<Filter> proxyFilters) {
		this.proxyFilters = proxyFilters;
	}

	public long getMaxEvictableIdleTimeMillis() {
		return maxEvictableIdleTimeMillis;
	}

	public void setMaxEvictableIdleTimeMillis(long maxEvictableIdleTimeMillis) {
		this.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
	}

//	public List<Filter> getFilters() {
//		return filters;
//	}
//
//	public void setFilters(List<Filter> filters) {
//		this.filters = filters;
//	}

}
