package pers.lyc.mybatis.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import cn.hutool.core.util.ReflectUtil;
import pers.lyc.mybatis.core.annotation.Column;
import pers.lyc.mybatis.core.annotation.DictGroup;
import pers.lyc.mybatis.core.annotation.EnumClass;
import pers.lyc.mybatis.core.annotation.Join;
import pers.lyc.mybatis.core.annotation.Related;
import pers.lyc.mybatis.core.annotation.Table;
import pers.lyc.mybatis.core.cache.ColumnCache;
import pers.lyc.mybatis.core.cache.JoinCache;
import pers.lyc.mybatis.core.cache.RelatedCache;
import pers.lyc.mybatis.core.cache.TableCache;

/**
 * 表缓存工具类
 * @author 林运昌（linyunchang）
 * @since 2020年6月18日
 */
public class TableCacheUtil {
	
	/** 缓存表信息 */
	private static Map<Class<?>, TableCache> tableCacheMap = new HashMap<>();
	
	/**
	 * 获取表信息
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param cls 表对象类型
	 * @return 
	 */
	public static TableCache getTableCache(Class<?> cls) {
		Assert.notNull(cls, "表对象类型不能为空");
		
		// 表信息未缓存则添加表信息缓存
		if (!tableCacheMap.containsKey(cls)) {
			synchronized (TableCacheUtil.class) {
				if ( !tableCacheMap.containsKey(cls) ) {
					// 生成表信息
					TableCache tableCache = generateTableCache(cls);
					// 保存表信息
					tableCacheMap.put(cls, tableCache);
				}
			}
		}
		// 获取表缓存信息
		TableCache tableCache = tableCacheMap.get(cls);
		
		return tableCache;
	}
	
	/**
	 * 生成TableCache
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param cls 表对象类型
	 * @return 
	 */
	private static TableCache generateTableCache(Class<?> cls) {
		Assert.notNull(cls, "Table Class不能为空");

		// 获取@Table注解
		Table table = cls.getAnnotation(Table.class);
		Assert.notNull(table, cls.getName() + " 不是一个数据库表对象");
		
		// 创建TableCache
		TableCache tableCache = new TableCache();
		tableCache.setTable(table);
		tableCache.setTableCls(cls);
		tableCache.setCnName(table.cnName());
		tableCache.setClassName(cls.getName());
		tableCache.setClassSimpleName(cls.getSimpleName());
		tableCache.setKebabName(MybatisUtil.camelToKebab(cls.getSimpleName()));
		tableCache.setPrimaryKey(table.primaryKey());
		tableCache.setComment(StringUtils.isNotBlank(table.comment()) ? table.comment() : table.cnName());
		// 设置表名
		String tableName = MybatisUtil.getTableName(cls, table);
		tableCache.setTableName(tableName);
		// 设置表别名
		String aliasName = MybatisUtil.getAliasName(tableName);
		tableCache.setAliasName(aliasName);
		
		// 字段信息
		Map<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
		// 关联信息
		Map<String, JoinCache> joinCacheMap = tableCache.getJoinCacheMap();
		// 字段关系
		Map<String, RelatedCache> relatedCacheMap = tableCache.getRelatedCacheMap();
		// 唯一键列表
		List<String> uniqueKeyList = tableCache.getUniqueKeyList();
		// 索引列表
		List<String> indexKeyList = tableCache.getIndexKeyList();
		
		// 关系字段列表
		List<Field> relatedFieldList = new ArrayList<>();
		// 获取类属性并解析
		Field[] fields = cls.getDeclaredFields();
		for ( Field field : fields ) {
			// 属性为serialVersionUID则不处理
			if ( "serialVersionUID".equalsIgnoreCase(field.getName()) )
				continue;
			// 生成字段信息
			ColumnCache columnCache = generateColumnCache(aliasName, tableCache.getPrimaryKey(), field);
			if ( null!=columnCache ) {
				// 判断是否主键，如果是主键则设置主键类型
				if ( tableCache.getPrimaryKey().equals(columnCache.getFieldName()) )
					tableCache.setPrimaryKeyType(columnCache.getFieldType());
				// 添加字段信息
				columnCacheMap.put(field.getName(), columnCache);
				// 修正关联表别名，添加关联信息
				JoinCache joinCache = columnCache.getJoinCache();
				if ( null!=joinCache ) {
					String joinTableAliasName = joinCache.getJoinTableAliasName();
					String newJoinTableAliasName = joinTableAliasName + joinCacheMap.size();
					joinCache.setJoinTableAliasName(newJoinTableAliasName);
					joinCache.setJoinOn(joinCache.getJoinOn().replaceAll(joinTableAliasName + ".", newJoinTableAliasName + "."));
					joinCacheMap.put(field.getName(), joinCache);
				}
			} else {
				relatedFieldList.add(field);
			}
		}
		// 处理关系字段
		for ( Field relatedField : relatedFieldList ) {
			RelatedCache relatedCache = generateRelatedCache(tableCache, relatedField);
			if ( null!=relatedCache ) {
				relatedCacheMap.put(relatedField.getName(), relatedCache);
			}
		}
		
		// 处理唯一键
		String[] uniqueKeys = table.uniqueKeys();
		for ( String uniqueKey : uniqueKeys ) {
			// 添加唯一键到唯一键列表
			uniqueKeyList.add(fieldsToColumns(columnCacheMap, uniqueKey));
		}
		// 处理索引
		String[] indexKeys = table.indexKeys();
		for ( String indexKey : indexKeys ) {
			// 添加索引到索引列表
			indexKeyList.add(fieldsToColumns(columnCacheMap, indexKey));
		}
		
		return tableCache;
	}

	/**
	 * 生成ColumnCache
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param tableAliasName 表别名
	 * @param primaryKey 表主键
	 * @param field 表字段对应的属性
	 * @return 
	 */
	private static ColumnCache generateColumnCache(String tableAliasName, String primaryKey, Field field) {
		Assert.hasLength(tableAliasName, "表别名不能为空");
		Assert.hasLength(primaryKey, "表主键不能为空");
		Assert.notNull(field, "Column Field不能为空");
		
		// 获取列注解，列注解不存在则返回空
		Column column = field.getAnnotation(Column.class);
		if ( null==column )
			return null;
		
		// 创建columnCache
		ColumnCache columnCache = new ColumnCache();
		columnCache.setColumn(column);
		columnCache.setTableAliasName(tableAliasName);
		columnCache.setColumnName(MybatisUtil.getColumnName(field, column));
		columnCache.setFieldName(field.getName());
		columnCache.setKebabName(MybatisUtil.camelToKebab(field.getName()));
		columnCache.setAliasName(field.getName());
		columnCache.setCnName(column.cnName());
		columnCache.setUnsigned(column.unsigned());
		columnCache.setIsRequired(column.required());
		columnCache.setAutoIncrement(false);
		columnCache.setComment(StringUtils.isNotBlank(column.comment()) ? column.comment() : column.cnName());
		
		// 设置字段属性类型
		String fieldType = field.getType().getSimpleName();
		columnCache.setFieldType(fieldType);

		// 获取枚举注解
		EnumClass enumClass = field.getAnnotation(EnumClass.class);
		// 设置枚举信息
		if ( null!=enumClass ) {
			columnCache.setEnum(true);
			columnCache.setEnumClass(enumClass.cls());
			columnCache.setEnumName(enumClass.cls().getName());
			columnCache.setEnumSimpleName(enumClass.cls().getSimpleName());
			columnCache.setEnumValueField(enumClass.valueField());
		}
		// 获取数据字典注解
		DictGroup dictGroup = field.getAnnotation(DictGroup.class);
		// 设置数据字典信息
		if ( null!=dictGroup ) {
			columnCache.setDict(true);
			columnCache.setDictGroupName(dictGroup.name());
		}
		
		// 设置字段类型、字段长度、字段默认值
		String type = column.type();
		Integer length = column.length();
		Object defaultValue = null;
		if ( "Integer".equals(fieldType) ) {
			// 设置类型
			if ( StringUtils.isBlank(type) ) {
				// 如果存在枚举或数据字典注解，则设置tinyint，否则int
				if ( null!=enumClass || null!=dictGroup ) 
					type = "tinyint";
				else 
					type = "int";
			}
			// 设置默认值
			if ( StringUtils.isNotBlank(column.value()) )
				defaultValue = Integer.valueOf(column.value());
			
		} else if ( "Long".equals(fieldType) ) {
			// 设置类型
			if ( StringUtils.isBlank(type) ) 
				type = "bigint";
			// 设置默认值
			if ( StringUtils.isNotBlank(column.value()) )
				if ( !"now".equalsIgnoreCase(column.value()) && !"now()".equalsIgnoreCase(column.value()) && !"CURRENT_TIMESTAMP".equalsIgnoreCase(column.value()) ) 
					defaultValue = Long.valueOf(column.value());
			
		} else if ( "Boolean".equals(fieldType) ) {
			// 设置类型
			type = "tinyint";
			// 设置长度
			length = 1;
			// 设置默认值
			if ( StringUtils.isNotBlank(column.value()) )
				defaultValue = Boolean.valueOf(column.value());
			
		} else if ( "BigDecimal".equals(fieldType) ) {
			// 设置类型
			if ( StringUtils.isBlank(type) ) 
				type = "decimal";
			// 设置长度
			if ( length==0 ) 
				length = 12;
			// 设置默认值
			if ( StringUtils.isNotBlank(column.value()) )
				defaultValue = new BigDecimal(column.value());
			
		} else if ( "String".equals(fieldType) ) {
			// 设置类型
			if ( StringUtils.isBlank(type) ) 
				type = "varchar";
			// 设置字符串长度
			if ( "varchar".equalsIgnoreCase(type) && length==0 ) 
				length = 255;
			// 设置默认值
			if ( !"TEXT".equalsIgnoreCase(type) && !"BLOB".equalsIgnoreCase(type) && !"GEOMETRY".equalsIgnoreCase(type) && !"JSON".equalsIgnoreCase(type) ) 
				defaultValue = column.value();
			
		} else if ( "Date".equals(fieldType) ) {
			// 设置类型
			if ( StringUtils.isBlank(type) ) 
				type = "datetime";
			
		} else {
			throw new IllegalArgumentException("设置默认值失败：未知的数据类型 " + field.getType() + " ，请联系 林运昌 添加该类型的处理逻辑");
		}
		columnCache.setColumnType(type);
		columnCache.setLength(length);
		columnCache.setDefaultValue(defaultValue);
		
		// 主键判断
		if ( primaryKey.equalsIgnoreCase(columnCache.getFieldName()) ) {
			if ( ("Integer".equalsIgnoreCase(fieldType) || "Long".equalsIgnoreCase(fieldType)) ) {
				columnCache.setAutoIncrement(true);
			} else if ( "String".equalsIgnoreCase(fieldType) ) {
				if ( length==0 || length==255 ) {
					length = 32;
					columnCache.setLength(length);
				}
			}
		}
		
		// 设置Join关系
		JoinCache joinCache = generateJoinCache(tableAliasName, column, field);
		columnCache.setJoinCache(joinCache);
		// 存在Join关系且字段类型为Integer或Long，自动设置无符号
		if ( null!=joinCache ) {
			columnCache.setLength(joinCache.getJoinFieldLength());
		}
		
		return columnCache;
	}

	/**
	 * 生成JoinCache
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param tableAliasName 表别名
	 * @param column 表字段注解
	 * @param field 表字段对应的属性
	 * @return 
	 */
	private static JoinCache generateJoinCache(String tableAliasName, Column column, Field field) {
		Assert.hasLength(tableAliasName, "表别名不能为空");
		Assert.notNull(column, "@Column 不能为空");
		Assert.notNull(field, "Column Field不能为空");

		// 获取@Join注解，注解不存在则返回空
		Join join = field.getAnnotation(Join.class);
		if ( null==join ) 
			return null;
		
		// 获取关联表
		Class<?> joinTableCls = join.table();
		// 获取关联表@Table注解
		Table joinTable = joinTableCls.getAnnotation(Table.class);
		Assert.notNull(joinTable, joinTableCls.getName() + " 不是一个数据库表对象 ");
		
		// 创建JoinCache
		JoinCache joinCache = new JoinCache();
		joinCache.setJoin(join);
		joinCache.setJoinTable(joinTable);
		joinCache.setJoinTableCls(joinTableCls);
		joinCache.setJoinType(join.joinType());
		
		// 设置关联表名
		String joinTableName = MybatisUtil.getTableName(joinTableCls, joinTable);
		joinCache.setJoinTableName(joinTableName);
		// 设置关联表别名
		String joinTableAliasName = tableAliasName + "_" + MybatisUtil.getAliasName(joinTableName);
		joinCache.setJoinTableAliasName(joinTableAliasName);
		
		// 设置关联表属性
		Field joinField = null;
		if ( StringUtils.isBlank(join.field()) ) {
			joinField = ReflectUtil.getField(joinTableCls, joinTable.primaryKey());
			Assert.notNull(joinField, joinTableCls.getName() + " 不存在主键 " + joinTable.primaryKey());
		} else {
			joinField = ReflectUtil.getField(joinTableCls, join.field());
			Assert.notNull(joinField, joinTableCls.getName() + " 不存在属性 " + join.field());
		}
		// 获取关联属性@Column注解
		Column joinColumn = joinField.getAnnotation(Column.class);
		Assert.notNull(joinColumn, joinField.getName() + " 不是表 " + joinTableName + " 的列 ");
		// 设置长度
		Field joinFieldJoinField = joinField;
		Column joinFieldJoinColumn = joinColumn;
		Join joinFieldJoin = joinField.getAnnotation(Join.class);;
		while ( joinFieldJoin!=null ) {
			// 获取关联表
			Class<?> joinFieldJoinTableCls = joinFieldJoin.table();
			// 获取关联表@Table注解
			Table joinFieldJoinTable = joinFieldJoinTableCls.getAnnotation(Table.class);
			Assert.notNull(joinFieldJoinTable, joinFieldJoinTableCls.getName() + " 不是一个数据库表对象 ");
			// 获取关联属性
			if ( StringUtils.isBlank(joinFieldJoin.field()) ) {
				joinFieldJoinField = ReflectUtil.getField(joinFieldJoinTableCls, joinFieldJoinTable.primaryKey());
				Assert.notNull(joinFieldJoinField, joinFieldJoinTableCls.getName() + " 不存在主键 " + joinFieldJoinTable.primaryKey());
			} else {
				joinFieldJoinField = ReflectUtil.getField(joinFieldJoinTableCls, joinFieldJoin.field());
				Assert.notNull(joinFieldJoinField, joinFieldJoinTableCls.getName() + " 不存在属性 " + joinFieldJoin.field());
			}
			// 获取注解
			joinFieldJoinColumn = joinFieldJoinField.getAnnotation(Column.class);
			joinFieldJoin = joinFieldJoinField.getAnnotation(Join.class);
		}
		String joinFieldType = joinFieldJoinField.getType().getSimpleName();
		int joinFieldLength = joinFieldJoinColumn.length();
		if ( "String".equalsIgnoreCase(joinFieldType) ) {
			if ( joinFieldLength==0 ) {
				joinFieldLength = 32;
			}
		}
		joinCache.setJoinFieldLength(joinFieldLength);
		
		// 设置关联条件
		String joinOn = joinTableAliasName + "." + MybatisUtil.getColumnName(joinField, joinColumn) + " = " + tableAliasName + "." + MybatisUtil.getColumnName(field, column);
		joinCache.setJoinOn(joinOn);
		
		return joinCache;
	}

	/**
	 * 生成RelatedCache
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param tableCache 表缓存信息
	 * @param relatedField 表关联字段对应的属性
	 * @return 
	 */
	private static RelatedCache generateRelatedCache(TableCache tableCache, Field relatedField) {
		Assert.notNull(tableCache, "TableCache 不能为空");
		Assert.notNull(relatedField, "Related Field不能为空");

		// 获取@Related注解，注解不存在则返回空
		Related related = relatedField.getAnnotation(Related.class);
		if ( null==related ) 
			return null;
		
		// 关系格式验证
		String relatedValue = related.value();
		if ( relatedValue.indexOf(".")==-1 ) {
			throw new IllegalArgumentException("@Related 关系格式不正确");
		}
		
		// 创建JoinCache
		RelatedCache relatedCache = new RelatedCache();
		relatedCache.setRelated(related);
		relatedCache.setAliasName(relatedField.getName());
		relatedCache.setCnName(related.name());
		relatedCache.setFieldName(relatedField.getName());
		relatedCache.setFieldType(relatedField.getType().getSimpleName());

		// 获取枚举注解
		EnumClass enumClass = relatedField.getAnnotation(EnumClass.class);
		// 设置枚举信息
		if ( null!=enumClass ) {
			relatedCache.setEnum(true);
			relatedCache.setEnumClass(enumClass.cls());
			relatedCache.setEnumName(enumClass.cls().getName());
			relatedCache.setEnumSimpleName(enumClass.cls().getSimpleName());
		}
		// 获取数据字典注解
		DictGroup dictGroup = relatedField.getAnnotation(DictGroup.class);
		// 设置数据字典信息
		if ( null!=dictGroup ) {
			relatedCache.setDict(true);
			relatedCache.setDictGroupName(dictGroup.name());
		}
		
		// 关联信息
		Map<String, JoinCache> joinCacheMap = tableCache.getJoinCacheMap();
		
		// 拆分关系解析
		int index = relatedValue.indexOf(".");
		while (index!=-1) {
			// 关联信息key
			String joinCacheKey = relatedValue.substring(0, index);
			
			// 获取当前关联信息
			if ( !joinCacheMap.containsKey(joinCacheKey) ) 
				throw new IllegalArgumentException("关联信息 " + joinCacheKey + " 不存在");
			JoinCache joinCache = joinCacheMap.get(joinCacheKey);
			// 关联信息未结束，则添加关联关系
			int nextIndex = relatedValue.indexOf(".", index+1);
			if ( nextIndex!=-1 ) {
				// 判断下一级关联关系是否存在
				String nextJoinCacheKey = relatedValue.substring(0, nextIndex);
				if ( joinCacheMap.containsKey(nextJoinCacheKey) ) {
					index = nextIndex;
					continue;
				}
				
				// 获取当前关联对象Class
				Class<?> joinTableCls = joinCache.getJoinTableCls();
				// 获取当前关联对象的属性名称，用于生成下次关联信息
				String nextRelatedFieldName = relatedValue.substring(index+1, nextIndex);
				// 获取当前关联对象的属性，用于生成下次关联信息
				Field nextRelatedField = ReflectUtil.getField(joinTableCls, nextRelatedFieldName);
				Assert.notNull(nextRelatedField, joinTableCls.getName() + " 不存在属性 " + nextRelatedFieldName);
				// 判断该属性是否有@Column注解
				Column nextRelatedColumn = nextRelatedField.getAnnotation(Column.class);
				Assert.notNull(nextRelatedColumn, joinTableCls.getName() + " 的属性 " + nextRelatedFieldName + " 需要添加@Column注解");
				// 判断该属性是否有@Join注解
				Join nextRelatedJoin = nextRelatedField.getAnnotation(Join.class);
				Assert.notNull(nextRelatedJoin, joinTableCls.getName() + " 的属性 " + nextRelatedFieldName + " 需要添加@Join注解");
				// 生成JoinCache
				JoinCache nextJoinCache = generateJoinCache(joinCache.getJoinTableAliasName(), nextRelatedColumn, nextRelatedField);
				// 修正表别名
				String nextJoinTableAliasName = nextJoinCache.getJoinTableAliasName();
				String newNextJoinTableAliasName = nextJoinTableAliasName + joinCacheMap.size();
				nextJoinCache.setJoinTableAliasName(newNextJoinTableAliasName);
				nextJoinCache.setJoinOn(nextJoinCache.getJoinOn().replaceAll(nextJoinTableAliasName + ".", newNextJoinTableAliasName + "."));
				
				joinCacheMap.put(nextJoinCacheKey, nextJoinCache);
			}
			
			index = nextIndex;
		}
		
		// 获取最后一级关联关系
		String lastJoinCacheKey = relatedValue.substring(0, relatedValue.lastIndexOf("."));
		if ( !joinCacheMap.containsKey(lastJoinCacheKey) ) 
			throw new IllegalArgumentException("关联信息 " + lastJoinCacheKey + " 不存在");
		JoinCache lastJoinCache = joinCacheMap.get(lastJoinCacheKey);
		Class<?> lastJoinTableCls = lastJoinCache.getJoinTableCls();
		
		// 设置关系表别名
		relatedCache.setTableAliasName(lastJoinCache.getJoinTableAliasName());
		
		// 获取最后关联对象的属性，用于生成关联信息
		String lastFieldName = relatedValue.substring(relatedValue.lastIndexOf(".")+1, relatedValue.length());
		Field lastField = ReflectUtil.getField(lastJoinTableCls, lastFieldName);
		Assert.notNull(lastField, lastJoinTableCls.getName() + " 不存在属性 " + lastFieldName);
		// 判断该属性是否有@Column注解
		Column lastColumn = lastField.getAnnotation(Column.class);
		Assert.notNull(lastColumn, lastJoinTableCls.getName() + " 的属性 " + lastFieldName + " 需要添加@Column注解");
		
		// 设置关系字段列名
		relatedCache.setColumnName(MybatisUtil.getColumnName(lastField, lastColumn));
		
		return relatedCache;
	}
	
	/**
	 * 属性字符串转换成列字符串（逗号隔开）
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param columnCacheMap 列缓存信息
	 * @param fieldStr 属性字符串
	 * @return 
	 */
	private static String fieldsToColumns(Map<String, ColumnCache> columnCacheMap, String fieldStr) {
		// 属性属性组合
		String[] fields = fieldStr.split(",");
		// 列字符串
		StringBuilder columnSb = new StringBuilder();
		// 遍历属性组合信息，拼接列字符串
		for ( String field : fields ) {
			field = field.trim();
			// 获取属性对应的列信息
			ColumnCache columnCache = columnCacheMap.get(field);
			Assert.notNull(columnCache, field + " 对应的字段信息不存在");
			// 拼接列名称
			if ( columnSb.length()>0 )
				columnSb.append(",");
			columnSb.append(columnCache.getColumnName());
		}
		
		return columnSb.toString();
	}
	
}
