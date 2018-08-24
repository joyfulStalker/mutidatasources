package com.lsl.multidatasource.comm;


/**
* druid 连接池参数默认配置
* @author liusonglin
* @date 2018年8月23日
*/
	
public class DefaultDruidDataSourceConf {

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
	 * 配置useUnfairLock属性为true使用非公平锁
	 */
	private Boolean useUnfairLock = true;

	/**
	 * 用来检测连接是否有效的sql，要求是一个查询语句。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用。
	 */
	private String validationQuery = "select 1";

	/**
	 * 单位：秒，检测连接是否有效的超时时间。底层调用jdbc Statement对象的void setQueryTimeout(int seconds)方法
	 */
	private int validationQueryTimeout = 5000;

	/**
	 * 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
	 */
	private boolean testWhileIdle = true;

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

	public Boolean getUseUnfairLock() {
		return useUnfairLock;
	}

	public void setUseUnfairLock(Boolean useUnfairLock) {
		this.useUnfairLock = useUnfairLock;
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

}
