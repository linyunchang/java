package pers.lyc.mybatis.core.sql.mysql;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.springframework.util.Assert;

import cn.hutool.core.util.ReflectUtil;
import pers.lyc.mybatis.core.annotation.Column;
import pers.lyc.mybatis.core.cache.ColumnCache;
import pers.lyc.mybatis.core.cache.TableCache;
import pers.lyc.mybatis.util.TableCacheUtil;

/**
 * mysql新增语句
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public class MySqlInsert<T> {
	
	/** 表实体类型 */
	private Class<?> tableCls;
	/** 自定义要操作的表（对应数据库的表名），表结构需要和表对象类型tableCls对应的数据表一致 */
	private String tableName;
	/** 数据列表 */
	private List<T> dataList = new ArrayList<>();
	
	/**
	 * 初始化
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 */
	public MySqlInsert() {
		// 判断类型
		Class<?> cls = getClass();
		if ( cls==MySqlInsert.class ) {
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
	 * @param cls 表实体类型
	 */
	public MySqlInsert(Class<T> tableCls) {
		Assert.notNull(tableCls, "表实体类型不能为空");
		
		this.tableCls = tableCls;
	}

	/**
	 * 自定义要新增数据的表（对应数据库的表名）
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param tableName 自定义要新增数据的表名（对应数据库的表名）
	 * @return 
	 */
	public MySqlInsert<T> insertInto(String tableName) {
		Assert.hasLength(tableName, "自定义要新增数据的表不能为空");
		
		this.tableName = tableName;
		
		return this;
	}
	
	/**
	 * 添加数据
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param data 数据对象
	 * @return 
	 */
	public MySqlInsert<T> value(T data) {
		return value(data, false);
	}
	
	/**
	 * 设置数据
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param data 数据对象
	 * @param clear 是否清除旧数据列表
	 * @return 
	 */
	public MySqlInsert<T> value(T data, boolean clear) {
		Assert.notNull(data, "数据不能为空");
		
		// 判断数据类型是否正确
		if ( tableCls!=data.getClass() && !tableCls.isAssignableFrom(data.getClass()) ) 
			throw new IllegalArgumentException("数据类型不正确");
		
		// 设置默认值
		setDefault(data);
		
		// 清除旧数据列表
		if ( clear ) {
			dataList.clear();
		}
		// 添加到数据列表
		dataList.add(data);
		
		return this;
	}
	
	/**
	 * 添加数据列表
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param dataList 数据对象列表
	 * @return 
	 */
	public MySqlInsert<T> values(List<T> dataList) {
		return values(dataList, false);
	}
	
	/**
	 * 设置数据列表
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param dataList 数据对象列表
	 * @param clear 是否清除旧数据列表
	 * @return 
	 */
	public MySqlInsert<T> values(List<T> dataList, boolean clear) {
		Assert.notEmpty(dataList, "数据列表不能为空");

		// 清除旧数据列表
		if ( clear ) {
			this.dataList.clear();
		}
		// 添加到数据列表
		for ( T data : dataList ) {
			this.value(data);
		}
		
		return this;
	}
	
	/**
	 * 设置默认值
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param data 数据对象
	 * @return 
	 */
	private Object setDefault(T data) {
		// 当前时间
		Date nowDate = new Date();
		// 获取表缓存信息
		TableCache tableCache = TableCacheUtil.getTableCache(tableCls);
		// 获取字段缓存信息，判断是否设置默认值
		Map<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
		for ( String columnKey : columnCacheMap.keySet() ) {
			// 获取字段缓存信息
			ColumnCache columnCache = columnCacheMap.get(columnKey);
			// 自增字段不设置默认值
			if ( columnCache.isAutoIncrement() )
				continue;
			
			// 获取字段默认值
			Object defaultValue = columnCache.getDefaultValue();
			// 如果有默认值，且该属性未设置值，则设置默认值 
			if (defaultValue!=null ) {
				// 获取属性值
				Object fieldValue = ReflectUtil.getFieldValue(data, columnKey);
				// 属性未设置值，则设置默认值
				if ( null==fieldValue ) {
					ReflectUtil.setFieldValue(data, columnKey, defaultValue);
				}
			} else { // 如果没有默认值，判断是否时间类型，如果是标注了值为当前时间的时间类型则设置当前时间
				String fieldType = columnCache.getFieldType();
				Column column = columnCache.getColumn();
				if ( "Date".equals(fieldType) ) {
					if ( "now".equalsIgnoreCase(column.value()) || "now()".equalsIgnoreCase(column.value()) ) 
						ReflectUtil.setFieldValue(data, columnKey, nowDate);
				} else if ( "Long".equals(fieldType) ) {
					if ( "now".equalsIgnoreCase(column.value()) || "now()".equalsIgnoreCase(column.value()) ) 
						ReflectUtil.setFieldValue(data, columnKey, nowDate.getTime());
				}
			}
			
			// 如果是字符串主键，且未设置值，则自动生成uuid
			if (columnKey.equals(tableCache.getPrimaryKey())) {
				// 获取属性
				Object fieldValue = ReflectUtil.getFieldValue(data, columnKey);
				// 属性未设置值，则设置默认值
				if ( null==fieldValue || StringUtils.isBlank(String.valueOf(fieldValue)) ) {
					ReflectUtil.setFieldValue(data, columnKey, UuidUtil.getTimeBasedUuid().toString().replaceAll("-", ""));
				}
			}
		}
		
		return data;
	}
	
	/**
	 * 获取字段字符串
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @return 
	 */
	private String getColums() {
		// 获取表缓存信息
		TableCache tableCache = TableCacheUtil.getTableCache(tableCls);
		// 字段
		StringBuilder columnsSb = new StringBuilder();
		// 获取字段缓存map
		Map<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
		// 遍历字段，拼接字段信息
		for ( Entry<String, ColumnCache> entry : columnCacheMap.entrySet() ) {
			// 获取字段缓存信息
			ColumnCache columnCache = entry.getValue();
			// 自增字段是否添加判断
			if ( columnCache.isAutoIncrement() )
				continue;
			// 是否拼接,
			if ( columnsSb.length()>0 ) 
				columnsSb.append(",");
			// 添加字段
			columnsSb.append(columnCache.getColumnName());
		}
		
		return columnsSb.toString();
	}
	
	/**
	 * 获取属性参数字符串
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @return 
	 */
	private String getParams() {
		// 获取表缓存信息
		TableCache tableCache = TableCacheUtil.getTableCache(tableCls);
		// 字段
		StringBuilder paramsSb = new StringBuilder();
		// 获取字段缓存map
		Map<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
		// 遍历字段，拼接字段信息
		for ( Entry<String, ColumnCache> entry : columnCacheMap.entrySet() ) {
			// 获取字段缓存信息
			ColumnCache columnCache = entry.getValue();
			// 自增字段是否添加判断
			if ( columnCache.isAutoIncrement() )
				continue;
			// 是否拼接,
			if ( paramsSb.length()>0 ) 
				paramsSb.append(",");
			// 添加字段
			paramsSb.append("#{data." + columnCache.getFieldName() + "}");
		}
		
		return paramsSb.toString();
	}
	
	@Override
	public String toString() {
		// 获取表缓存信息
		TableCache tableCache = TableCacheUtil.getTableCache(tableCls);
		// 生成sql语句
		StringBuilder sql = new StringBuilder("insert into ");
		if ( tableName==null )
			sql.append(tableCache.getTableName());
		else 
			sql.append(tableName);
		sql.append("(");
		sql.append(getColums());
		sql.append(")");
		sql.append(" values ");
		sql.append("<foreach collection=\"dataList\" item=\"data\" separator=\",\">");
		sql.append("(");
		sql.append(getParams());
		sql.append(")");
		sql.append("</foreach>");

		System.out.println(sql.toString());
		return sql.toString();
	}
	
	public Class<?> getTableCls() {
		return tableCls;
	}
	
	public List<T> getDataList() {
		return dataList;
	}
	
}
