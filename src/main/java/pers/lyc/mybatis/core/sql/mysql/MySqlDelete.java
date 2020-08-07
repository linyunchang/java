package pers.lyc.mybatis.core.sql.mysql;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.springframework.util.Assert;

import pers.lyc.mybatis.core.cache.JoinCache;
import pers.lyc.mybatis.core.cache.RelatedCache;
import pers.lyc.mybatis.core.cache.TableCache;
import pers.lyc.mybatis.core.sql.SqlWhere;
import pers.lyc.mybatis.util.TableCacheUtil;

/**
 * mysql删除语句
 * @author 林运昌（linyunchang）
 * @date 2020年6月19日
 */
public class MySqlDelete<T> extends SqlWhere {
	/** 表实体类型 */
	private Class<?> tableCls;
	/** 自定义要操作的表（对应数据库的表名），表结构需要和表对象类型tableCls对应的数据表一致 */
	private String tableName;
	/** 生效的关联信息 */
	private LinkedHashMap<String, JoinCache> joinCacheMap = new LinkedHashMap<>();
	
	/**
	 * 初始化
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 */
	public MySqlDelete() {
		// 判断类型
		Class<?> cls = getClass();
		if ( cls==MySqlDelete.class ) {
			throw new IllegalArgumentException("请传入表实体类型");
		}
		// 获取表实体类型
		Type type = cls.getGenericSuperclass();
		if ( type instanceof ParameterizedType ) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			this.tableCls = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		} else {
			throw new IllegalArgumentException("请传入表实体类型");
		}
	}
	
	/**
	 * 初始化
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param cls 表实体类型
	 */
	public MySqlDelete(Class<T> tableCls) {
		Assert.notNull(tableCls, "表实体类型不能为空");
		
		this.tableCls = tableCls;
	}
	
	/**
	 * 自定义要删除数据的表（对应数据库的表名）
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param tableName 自定义要删除数据的表名（对应数据库的表名）
	 * @return
	 */
	public MySqlDelete<T> from(String tableName) {
		Assert.hasLength(tableName, "自定义要删除数据的表不能为空");
		
		this.tableName = tableName;
		
		return this;
	}
	
	@Override
	protected TableCache getTableCache() {
		TableCache tableCache = TableCacheUtil.getTableCache(tableCls);
		return tableCache;
	}

	@Override
	protected void joinRelated(String relatedField) {
		Assert.hasLength(relatedField, "关联属性不能为空");
		
		// 获取表缓存信息
		TableCache tableCache = getTableCache();
		
		// 获取关系map
		LinkedHashMap<String, RelatedCache> relatedCacheMap = tableCache.getRelatedCacheMap();
		// 判断是否关系字段
		if ( !relatedCacheMap.containsKey(relatedField) )
			return;
		
		// 获取关联关系缓存
		RelatedCache relatedCache = relatedCacheMap.get(relatedField);
		// 获取关联关系
		String relatedValue = relatedCache.getRelated().value();
		// 如果关联关系已存在，则不添加
		if ( joinCacheMap.containsKey(relatedValue.substring(0, relatedValue.lastIndexOf("."))) )
			return;
		
		// 获取关联关系缓存
		LinkedHashMap<String, JoinCache> allJoinCacheMap = tableCache.getJoinCacheMap();
		// 拆分关系解析
		int index = relatedValue.indexOf(".");
		while (index!=-1) {
			// 关联信息key
			String joinCacheKey = relatedValue.substring(0, index);
			// 下一个索引位置
			index = relatedValue.indexOf(".", index+1);
			
			// 未添加关联信息则添加
			if ( !joinCacheMap.containsKey(joinCacheKey) ) {
				// 获取关联缓存信息
				JoinCache joinCache = allJoinCacheMap.get(joinCacheKey);
				// 添加关联缓存
				joinCacheMap.put(joinCacheKey, joinCache);
			}
			
			continue;
		}
	}

	@Override
	public String toString() {
		// 获取表缓存信息
		TableCache tableCache = TableCacheUtil.getTableCache(tableCls);
		// 条件字符串
		StringBuilder where = new StringBuilder();
		// 生成sql语句
		StringBuilder sql = new StringBuilder("delete ");
		sql.append(tableCache.getAliasName());
		// 添加from
		sql.append(" from ");
		if ( tableName==null )
			sql.append(tableCache.getTableName());
		else
			sql.append(tableName);
		sql.append(" ");
		sql.append(tableCache.getAliasName());
		// 添加关联表
		for ( Entry<String, JoinCache> entry : joinCacheMap.entrySet() ) {
			// 获取关联关系
			JoinCache joinCache = entry.getValue();
			// 添加关联关系
			sql.append(",");
			sql.append(joinCache.getJoinTableName());
			sql.append(" ");
			sql.append(joinCache.getJoinTableAliasName());
		}
		// 添加关联关系
		for ( Entry<String, JoinCache> entry : joinCacheMap.entrySet() ) {
			// 获取关联关系
			JoinCache joinCache = entry.getValue();
			// 添加关系
			if ( where.length()==0 )
				where.append(" where ");
			else 
				where.append(" and ");
			where.append(joinCache.getJoinOn());
		}
		// 条件语句处理
		for ( String and : andSet ) {
			if ( where.length()==0 )
				where.append(" where ");
			else 
				where.append(" and ");
			where.append(and);
		}
		for ( String or : orSet ) {
			if ( where.length()==0 )
				where.append(" where ");
			else 
				where.append(" or ");
			where.append(or);
		}
		sql.append(where.toString());
		
		System.out.println(sql.toString());
		return sql.toString();
	}
	
}
