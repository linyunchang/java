package pers.lyc.mybatis.provider.mysql;

import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.util.Assert;

import pers.lyc.mybatis.core.sql.mysql.MySqlInsert;

/**
 * MySql数据新增SqlProvider
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public class MySqlInsertSqlProvider implements ProviderMethodResolver {
	
	/**
	 * 数据新增
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param context ProviderContext
	 * @param sql 数据新增语句
	 * @return 
	 */
	public static String insert(ProviderContext context, MySqlInsert<?> sql) {
		Assert.notNull(sql, "数据新增语句不能为空");
		
		return "<script>" + sql.toString() + "</script>";
	}
	
}
