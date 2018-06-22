package com.yonyou.multidatasource.comm;

/**
 * 从注解获取数据源名并存放到当前线程中
 * 
 * @author liusonglin
 * @date 2018年6月14日
 */

public class DynamicDataSourceHolder {

	private static final ThreadLocal<DataSourceKey> currentDatesource = new ThreadLocal<>();

	/**
	 * 清除当前数据源
	 */
	public static void clear() {
		currentDatesource.remove();
	}

	/**
	 * 获取当前使用的数据源
	 *
	 * @return 当前使用数据源的ID
	 */
	public static DataSourceKey get() {
		return currentDatesource.get();
	}

	/**
	 * 设置当前使用的数据源
	 *
	 * @param value
	 *            需要设置的数据源ID
	 */
	public static void set(DataSourceKey value) {
		currentDatesource.set(value);
	}

}