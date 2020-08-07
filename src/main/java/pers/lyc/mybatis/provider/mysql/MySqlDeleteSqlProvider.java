package pers.lyc.mybatis.provider.mysql;

import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.util.Assert;

import pers.lyc.mybatis.core.sql.mysql.MySqlDelete;

/**
 * MySql数据删除SqlProvider
 * @author 林运昌（linyunchang）
 * @date 2020年6月19日
 */
public class MySqlDeleteSqlProvider implements ProviderMethodResolver {
	
	/**
	 * 数据删除
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param context ProviderContext
	 * @param sql 数据删除语句
	 * @return
	 */
	public static String delete(ProviderContext context, MySqlDelete<?> sql) {
		Assert.notNull(sql, "数据删除语句不能为空");
		
		return "<script>" + sql.toString() + "</script>";
	}
	
}
