package pers.lyc.mybatis.core.sql.mysql;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import pers.lyc.mybatis.core.cache.ColumnCache;
import pers.lyc.mybatis.core.cache.JoinCache;
import pers.lyc.mybatis.core.cache.RelatedCache;
import pers.lyc.mybatis.core.cache.TableCache;
import pers.lyc.mybatis.core.enums.JoinType;
import pers.lyc.mybatis.core.sql.SqlWhere;
import pers.lyc.mybatis.util.TableCacheUtil;

/**
 * mysql查询语句
 * @author 林运昌（linyunchang）
 * @date 2020年6月19日
 */
public class MySqlSelect<T> extends SqlWhere {
	/** 表实体类型 */
	private Class<?> tableCls;
	/** 自定义要操作的表（对应数据库的表名），表结构需要和表对象类型tableCls对应的数据表一致 */
	private String tableName;
	/** 是否去重 */
	private Boolean distinct = false;
	/** 要查询的属性数组 */
	private String[] fields;
	/** 关联类型map */
	private Map<String, JoinType> joinTypeMap = new HashMap<String, JoinType>();
	/** 生效的关联信息 */
	private LinkedHashMap<String, JoinCache> joinCacheMap = new LinkedHashMap<>();
	/** 排序 */
	private StringBuilder orderBy = new StringBuilder();
	/** 数据起始行位置 */
	private int offset = -1;
	/** 查询数量 */
	private int limit = -1;
	
	/**
	 * 初始化
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 */
	public MySqlSelect() {
		// 判断类型
		Class<?> cls = getClass();
		if ( cls==MySqlSelect.class ) {
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
	public MySqlSelect(Class<T> tableCls) {
		Assert.notNull(tableCls, "表实体类型不能为空");
		
		this.tableCls = tableCls;
	}
	
	/**
	 * 设置是否去重
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param distinct 是否去重
	 * @return
	 */
	public MySqlSelect<T> distinct(Boolean distinct) {
		Assert.notNull(distinct, "是否去重不能为空");
		
		this.distinct = distinct;
		
		return this;
	}
	
	/**
	 * 设置要查询的属性
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param field 要查询的属性
	 * @return
	 */
	public MySqlSelect<T> field(String field) {
		// 设置字段
		if ( StringUtils.isNotBlank(field) ) {
			this.fields = new String[] {field};
			
			// 添加关联关系
			joinRelated(field);
		} else {
			this.fields = new String[] {};
		}
		
		return this;
	}
	
	/**
	 * 设置要查询的属性数组
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param fields 要查询的属性数组
	 * @return
	 */
	public MySqlSelect<T> fields(String[] fields) {
		// 设置字段
		if ( fields!=null && fields.length>0 ) {
			this.fields = fields;
			
			// 添加关联关系
			for ( String field : fields ) {
				joinRelated(field);
			}
		} else {
			this.fields = new String[] {};
		}
		
		return this;
	}
	
	/**
	 * 自定义要查询数据的表（对应数据库的表名）
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param tableName 自定义要查询数据的表名（对应数据库的表名）
	 * @return
	 */
	public MySqlSelect<T> from(String tableName) {
		Assert.hasLength(tableName, "自定义要查询数据的表不能为空");
		
		this.tableName = tableName;
		
		return this;
	}
	
	/**
	 * 声明关联类型
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param joinCacheKey 关联关系缓存关键字
	 * @param joinType 关联类型
	 */
	public MySqlSelect<T> joinType(String joinCacheKey, JoinType joinType) {
		Assert.hasLength(joinCacheKey, "关联关系缓存关键字不能为空");
		Assert.notNull(joinType, "关联关系类型不能为空");
		
		// 未声明过关联类型则设置关联类型
		if ( !joinTypeMap.containsKey(joinCacheKey) ) {
			joinTypeMap.put(joinCacheKey, joinType);
		}
		
		return this;
	}
	
	/**
	 * 设置排序
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param field 字段属性
	 * @return
	 */
	public MySqlSelect<T> orderBy(String field) {
		return orderBy(field, false);
	}
	
	/**
	 * 设置排序
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param field 字段属性
	 * @param desc 是否倒序
	 * @return
	 */
	public MySqlSelect<T> orderBy(String field, boolean desc) {
		Assert.hasLength(field, "字段属性不能为空");

		// 获取表缓存信息
		TableCache tableCache = getTableCache();
		// 获取字段缓存信息
		LinkedHashMap<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
		LinkedHashMap<String, RelatedCache> relatedCacheMap = tableCache.getRelatedCacheMap();
		// 添加排序
		if ( columnCacheMap.containsKey(field) ) {
			if ( orderBy.length()>0 )
				orderBy.append(",");
			// 添加排序
			orderBy.append(columnCacheMap.get(field).getFullName());
			// 判断是否倒序
			if ( desc )
				orderBy.append(" desc");
		} else if ( relatedCacheMap.containsKey(field) ) {
			if ( orderBy.length()>0 )
				orderBy.append(",");
			// 添加排序
			orderBy.append(relatedCacheMap.get(field).getFullName());
			// 判断是否倒序
			if ( desc )
				orderBy.append(" desc");
		} else {
			throw new IllegalArgumentException("属性"+field+"不存在");
		}
		
		return this;
	}
	
	/**
	 * 设置查询数据数量
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月19日
	 * @param offset 数据偏移量
	 * @param limit 查询数据条数
	 * @return
	 */
	public MySqlSelect<T> limit(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
		
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
				// 声明关联类型
				joinType(joinCacheKey, joinCache.getJoinType());
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
		StringBuilder columns = new StringBuilder();
		// 条件字符串
		StringBuilder where = new StringBuilder();
		// 生成sql语句
		StringBuilder sql = new StringBuilder("select ");
		// 是否添加distinct
		if ( this.distinct ) 
			sql.append("distinct ");
		// 添加列字段
		if ( fields!=null && fields.length>0 ) {
			// 遍历属性，添加属性对应的字段
			for ( String field : fields ) {
				// 如果属性存在则添加
				if ( columnCacheMap.containsKey(field) ) {
					// 获取字段缓存信息
					ColumnCache columnCache = columnCacheMap.get(field);
					// 添加列字段
					if ( columns.length()>0 )
						columns.append(",");
					columns.append(columnCache.getFullName());
					columns.append(" ");
					columns.append(columnCache.getAliasName());
				} else if ( relatedCacheMap.containsKey(field) ) {
					// 获取字段缓存信息
					RelatedCache relatedCache = relatedCacheMap.get(field);
					// 添加列字段
					if ( columns.length()>0 )
						columns.append(",");
					columns.append(relatedCache.getFullName());
					columns.append(" ");
					columns.append(relatedCache.getAliasName());
				} else {
					// 添加列字段
					if ( columns.length()>0 )
						columns.append(",");
					columns.append(field);
				}
			}
			// 添加字段到sql语句
			sql.append(columns.toString());
		} else {
			// 添加字段
			for ( Entry<String, ColumnCache> entry : columnCacheMap.entrySet() ) {
				// 获取字段缓存信息
				ColumnCache columnCache = entry.getValue();
				// 添加字段
				if ( columns.length()>0 )
					columns.append(",");
				columns.append(columnCache.getFullName());
				columns.append(" ");
				columns.append(columnCache.getAliasName());
			}
			// 添加关系字段
			for ( Entry<String, RelatedCache> entry : relatedCacheMap.entrySet() ) {
				// 获取字段缓存信息
				RelatedCache relatedCache = entry.getValue();
				// 添加字段
				if ( columns.length()>0 )
					columns.append(",");
				columns.append(relatedCache.getFullName());
				columns.append(" ");
				columns.append(relatedCache.getAliasName());
			}
			// 添加字段到sql语句
			sql.append(columns.toString());
		}
		// 添加from
		sql.append(" from ");
		if ( tableName==null )
			sql.append(tableCache.getTableName());
		else
			sql.append(tableName);
		sql.append(" ");
		sql.append(tableCache.getAliasName());
		// 关联关系缓存处理
		if ( fields==null || fields.length==0 ) {
			joinCacheMap = tableCache.getJoinCacheMap();
		} 
		// 添加关联关系
		for ( Entry<String, JoinCache> entry : joinCacheMap.entrySet() ) {
			// 获取关联关系
			JoinCache joinCache = entry.getValue();
			// 关联类型
			JoinType joinType = joinCache.getJoinType();
			// 关联类型处理
			if ( joinTypeMap.containsKey(entry.getKey()) ) {
				joinType = joinTypeMap.get(entry.getKey());
			}
			// 添加关联关系
			sql.append(" ");
			sql.append(joinType.getEn());
			sql.append(" ");
			sql.append(joinCache.getJoinTableName());
			sql.append(" ");
			sql.append(joinCache.getJoinTableAliasName());
			// 交叉连接
			if ( joinType!=JoinType.CROSS ) {
				sql.append(" on ");
				sql.append(joinCache.getJoinOn());
			} else {
				if ( where.length()==0 )
					where.append(" where ");
				else 
					where.append(" and ");
				where.append(joinCache.getJoinOn());
			}
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
		// 排序
		if ( orderBy.length()>0 ) {
			sql.append(" order by ");
			sql.append(orderBy.toString());
		}
		// 限制查询数量
		if ( offset>=0 && limit>0 ) {
			sql.append(" limit ");
			sql.append(offset);
			sql.append(",");
			sql.append(limit);
		}

		System.out.println(sql.toString());
		return sql.toString();
	}

	public Class<?> getTableCls() {
		return tableCls;
	}

	public String getTableName() {
		return tableName;
	}

	public Boolean getDistinct() {
		return distinct;
	}

	public String[] getFields() {
		return fields;
	}

	public StringBuilder getOrderBy() {
		return orderBy;
	}

	public int getOffset() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}
	
}
