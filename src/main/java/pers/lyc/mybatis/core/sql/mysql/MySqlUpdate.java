package pers.lyc.mybatis.core.sql.mysql;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.Assert;

import cn.hutool.core.util.ReflectUtil;
import pers.lyc.mybatis.core.annotation.Column;
import pers.lyc.mybatis.core.annotation.Table;
import pers.lyc.mybatis.core.cache.ColumnCache;
import pers.lyc.mybatis.core.cache.JoinCache;
import pers.lyc.mybatis.core.cache.RelatedCache;
import pers.lyc.mybatis.core.cache.TableCache;
import pers.lyc.mybatis.core.sql.SqlWhere;
import pers.lyc.mybatis.util.TableCacheUtil;

/**
 * mysql更新语句
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public class MySqlUpdate<T> extends SqlWhere {
	/** 表实体类型 */
	private Class<?> tableCls;
	/** 自定义要操作的表（对应数据库的表名），表结构需要和表对象类型tableCls对应的数据表一致 */
	private String tableName;
	/** 表达式map */
	private Map<String, String> expressionMap = new HashMap<>();
	/** 待更新的值 */
	private Map<String, Object> valueMap = new HashMap<>();
	/** 生效的关联信息 */
	private LinkedHashMap<String, JoinCache> joinCacheMap = new LinkedHashMap<>();
	
	/**
	 * 初始化
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 */
	public MySqlUpdate() {
		// 判断类型
		Class<?> cls = getClass();
		if ( cls==MySqlUpdate.class ) {
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
	 * @since 2020年6月19日
	 * @param tableCls 表实体类型
	 */
	public MySqlUpdate(Class<T> tableCls) {
		Assert.notNull(tableCls, "表实体类型不能为空");
		
		this.tableCls = tableCls;
	}
	
	/**
	 * 自定义要更新数据的表（对应数据库的表名）
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param tableName 自定义要更新数据的表名（对应数据库的表名）
	 * @return 
	 */
	public MySqlUpdate<T> update(String tableName) {
		Assert.hasLength(tableName, "自定义要更新数据的表不能为空");
		
		this.tableName = tableName;
		
		return this;
	}
	
	/**
	 * 设置最新数据对象
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param data 最新数据对象
	 */
	public MySqlUpdate<T> data(T data) {
		return data(data, false);
	}
	
	/**
	 * 设置最新数据对象
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param data 最新数据对象
	 * @param setNull 是否设置空值（true：设置空值；false：不设置空值）
	 */
	public MySqlUpdate<T> data(T data, boolean setNull) {
		Assert.notNull(data, "最新数据对象不能为空");
		
		// 获取@Table注解
		Table table = tableCls.getAnnotation(Table.class);
		// 获取主键
		String primaryKey = table.primaryKey();
		Assert.hasLength(primaryKey, "主键不能为空");
		// 获取所有属性
		Field[] fields = ReflectUtil.getFields(tableCls);
		// 设置待更新数据
		for ( Field field : fields ) {
			// 获取属性名
			String fieldName = field.getName();
			// 如果是主键或序列号则判断下一个属性
			if ( primaryKey.equalsIgnoreCase(fieldName) || fieldName.equalsIgnoreCase("serialVersionUID") )
				continue;
			// 如果属性没有添加@Column注解则判断下一个属性
			if ( null==field.getAnnotation(Column.class) )
				continue;
			// 获取值
			Object fieldValue = ReflectUtil.getFieldValue(data, field);
			// 值未设置则判断下一个属性
			if ( !setNull && fieldValue==null )
				continue;
			// 设置待更新数据
			this.set(fieldName, fieldValue);
		}
		// 获取主键值
		Object fieldValue = ReflectUtil.getFieldValue(data, primaryKey);
		if ( fieldValue==null )
			throw new IllegalArgumentException(primaryKey + " 不能为空");
		// 设置主键参数
		this.andEqualTo(primaryKey, fieldValue);
		
		return this;
	}
	
	/**
	 * 设置待更新值
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param fieldName 属性名称
	 * @param value 值
	 */
	public MySqlUpdate<T> set(String fieldName, Object value) {
		Assert.hasLength(fieldName, "属性名称不能为空");
		
		// 添加关联关系
		joinRelated(fieldName);
		
		// 设置表达式和数据值
		this.expressionMap.put(fieldName, "#{valueMap."+fieldName+"}");
		if ( value!=null )
			this.valueMap.put(fieldName, value);
		
		return this;
	}
	
	/**
	 * 设置待更新值
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param fieldName 属性名称
	 * @param expression 表达式，如：<br>
	 * 	&nbsp;&nbsp;&nbsp;&nbsp; version + 1
	 * 	&nbsp;&nbsp;&nbsp;&nbsp; price + #{price}
	 * @param value 值
	 */
	public MySqlUpdate<T> set(String fieldName, String expression, Object value) {
		Assert.hasLength(fieldName, "属性名称不能为空");
		Assert.hasLength(expression, "表达式不能为空");
		
		// 添加关联关系
		joinRelated(fieldName);
		
		// 处理表达式
		expression = super.renderField(expression, true);
		expression = expression.replaceAll("#\\{paramMap.", "#{valueMap.");
		
		this.expressionMap.put(fieldName, expression);
		if ( value!=null )
			this.valueMap.put(fieldName, value);
		
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
		// 获取字段缓存信息
		LinkedHashMap<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
		LinkedHashMap<String, RelatedCache> relatedCacheMap = tableCache.getRelatedCacheMap();
		// 字段字符串
		StringBuilder sets = new StringBuilder();
		// 条件字符串
		StringBuilder where = new StringBuilder();
		// 生成sql语句
		StringBuilder sql = new StringBuilder("update ");
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
		// 添加要修改的数据
		for ( String columnKey : columnCacheMap.keySet() ) {
			// 主键不添加
			if ( columnKey.equalsIgnoreCase(tableCache.getPrimaryKey()) )
				continue;
			// 表达式未设置则不更新
			if ( !expressionMap.containsKey(columnKey) )
				continue;
			// 获取列信息
			ColumnCache columnCache = columnCacheMap.get(columnKey);
			// 添加set
			if ( sets.length()>0 ) 
				sets.append(",");
			else
				sets.append(" set ");
			sets.append(columnCache.getTableAliasName() + "." + columnCache.getColumnName() + "=" + expressionMap.get(columnKey));
		}
		// 获取关系信息
		for ( String relatedKey : relatedCacheMap.keySet() ) {
			// 表达式未设置则不更新
			if ( !expressionMap.containsKey(relatedKey) )
				continue;
			// 获取列信息
			RelatedCache relatedCache = relatedCacheMap.get(relatedKey);
			// 添加set
			if ( sets.length()>0 ) 
				sets.append(",");
			else
				sets.append(" set ");
			sets.append(relatedCache.getTableAliasName() + "." + relatedCache.getColumnName() + "=" + expressionMap.get(relatedKey));
		}
		sql.append(sets.toString());
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
	
	public Map<String, String> getExpressionMap() {
		return expressionMap;
	}
	
	public Map<String, Object> getValueMap() {
		return valueMap;
	}
	
}
