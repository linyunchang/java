package pers.lyc.mybatis.provider.mysql;

import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.util.Assert;

import pers.lyc.mybatis.core.sql.mysql.MySqlUpdate;

/**
 * MySql数据更新SqlProvider
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public class MySqlUpdateSqlProvider implements ProviderMethodResolver {
	
	/**
	 * 数据更新
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param context ProviderContext
	 * @param sql 数据更新语句
	 * @return 
	 */
	public static String update(ProviderContext context, MySqlUpdate<?> sql) {
		Assert.notNull(sql, "数据更新语句不能为空");
		
		return "<script>" + sql.toString() + "</script>";
	}
	
}
