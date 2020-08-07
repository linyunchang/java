package pers.lyc.mybatis.provider.mysql;

import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.util.Assert;

import pers.lyc.mybatis.core.sql.mysql.MySqlSelect;

/**
 * MySql数据查询SqlProvider
 * @author 林运昌（linyunchang）
 * @date 2020年6月19日
 */
public class MySqlSelectSqlProvider implements ProviderMethodResolver {
	
	/**
	 * 单条数据查询
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param context ProviderContext
	 * @param sql 数据查询语句
	 * @return
	 */
	public static String selectOne(ProviderContext context, MySqlSelect<?> sql) {
		Assert.notNull(sql, "查询语句不能为空");
		
		return "<script>" + sql.toString() + "</script>";
	}
	
	/**
	 * 单字段数据查询
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param context ProviderContext
	 * @param sql 数据查询语句
	 * @return
	 */
	public static String selectColumn(ProviderContext context, MySqlSelect<?> sql) {
		Assert.notNull(sql, "查询语句不能为空");
		if ( sql.getFields()==null ) 
			throw new IllegalArgumentException("请设置要查询的字段");
		if ( sql.getFields().length!=1 ) 
			throw new IllegalArgumentException("只能设置一个要查询的字段");
		
		return "<script>" + sql.toString() + "</script>";
	}
	
	/**
	 * 数据数量统计
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param context ProviderContext
	 * @param sql 数据查询语句
	 * @return
	 */
	public static String count(ProviderContext context, MySqlSelect<?> sql) {
		Assert.notNull(sql, "统计语句不能为空");
		
		// 数据缓存
		boolean distinct = sql.getDistinct();
		String[] fields = sql.getFields();
		int offset = sql.getOffset();
		int limit = sql.getLimit();
		// 生成执行语句
		String returnSql = sql.distinct(false).field("count(1)").limit(-1, -1).toString();
		// 恢复数据
		sql.distinct(distinct);
		sql.fields(fields);
		sql.limit(offset, limit);
		
		return "<script>" + returnSql + "</script>";
	}
	
	/**
	 * 数据列表查询
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param context ProviderContext
	 * @param sql 数据查询语句
	 * @return
	 */
	public static String select(ProviderContext context, MySqlSelect<?> sql) {
		Assert.notNull(sql, "查询语句不能为空");
		
		return "<script>" + sql.toString() + "</script>";
	}
	
}
