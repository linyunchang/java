package pers.lyc.mybatis.generate.factory;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * DataSource工厂类
 * @author 林运昌（linyunchang）
 * @since 2020年6月22日
 */
public class DataSourceFactory {
	
	private final static String driverClassName = "com.mysql.cj.jdbc.Driver";

	/**
	 * 创建数据源
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param url 数据库地址
	 * @param userName 数据库账号
	 * @param password 数据库密码
	 * @return 
	 */
	public static DataSource createBasicDataSource(String url, String userName, String password) {
		return createBasicDataSource(DataSourceFactory.driverClassName, url, userName, password);
	}
	
	/**
	 * 创建数据源
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param driverClassName 数据源驱动类名
	 * @param url 数据库地址
	 * @param userName 数据库账号
	 * @param password 数据库密码
	 * @return 
	 */
	public static DataSource createBasicDataSource(String driverClassName, String url, String userName, String password) {
		// 创建数据源
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		
		return dataSource;
	}
	
}
