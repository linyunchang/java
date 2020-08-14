package pers.lyc.mybatis.core.sql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import pers.lyc.mybatis.core.cache.ColumnCache;
import pers.lyc.mybatis.core.cache.RelatedCache;
import pers.lyc.mybatis.core.cache.TableCache;

/**
 * sql where语句
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public abstract class SqlWhere {
	
	/** 存放where and语句的Set */
	protected Set<String> andSet = new HashSet<>();
	
	/** 存放where or语句的Set */
	protected Set<String> orSet = new HashSet<>();
	
	/** 存放参数数据的map */
	protected Map<String, Object> paramMap = new HashMap<>();
	
	/**
	 * 获取表缓存信息，由具体子类实现
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @return 
	 */
	protected abstract TableCache getTableCache();
	
	/**
	 * 添加关联关系
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param relatedField 关联属性
	 * @return 
	 */
	protected abstract void joinRelated(String relatedField);
	
	/**
	 * 添加and条件，属性前后需要有空格
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 条件语句
	 * @param field 条件语句中涉及到的属性名称
	 * @example 
	 * 	sql： birthday is null； field：birthday<br>
	 * 	sql： birthday is not null； field：birthday<br>
	 */
	public SqlWhere and(String sql, String field) {
		and(sql, field, null);
		
		return this;
	}
	
	/**
	 * 添加and条件，属性前后需要有空格
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 条件语句
	 * @param field 条件语句中涉及到的属性名称
	 * @param value 条件语句中涉及到的属性的值
	 * @example 
	 * 	sql： id = #{id}； field：id； value：1<br>
	 * 	sql： name like concat('%', #{name},'%')； field：name； value："林运昌"<br>
	 * 	sql： type in &lt;foreach>type&lt;/foreach>； field：type； value：Integer[] or List<Integer><br>
	 * 	sql： birthday is null； field：birthday； value：null<br>
	 * 	sql： birthday is not null； field：birthday； value：null<br>
	 */
	public SqlWhere and(String sql, String field, Object value) {
		Assert.hasLength(sql, "sql不能为空");
		Assert.hasLength(field, "属性名称不能为空");
		
		// 渲染sql
		sql = renderField(sql, field, true);
		// 添加sql
		andSet.add("(" + sql + ")");
		// 添加参数
		if ( null!=value )
			this.paramMap.put(field, value);
		
		return this;
	}
	
	/** 
	 * 添加and条件，属性前后需要有空格
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 条件语句
	 * @param fields 条件语句中涉及到的属性名称数组
	 * @param paramMap 条件语句中涉及到的参数值map
	 * @example 
	 * 	sql： id = #{id}； fields：[id]； paramMap：{id:1}<br>
	 *  sql： name like concat('%', #{name},'%')； fields：[name]； paramMap：{name:"林运昌"}<br>
	 * 	sql： type in &lt;foreach>type&lt;/foreach>； fields：[type]； paramMap：{type:[1,2,3]}<br>
	 * 	sql： birthday is null； fields：[birthday]； paramMap：{}<br>
	 * 	sql： birthday is not null； fields：[birthday]； paramMap：{}<br>
	 */
	public SqlWhere and(String sql, String[] fields, Map<String, Object> paramMap) {
		Assert.hasLength(sql, "sql不能为空");
		Assert.notNull(fields, "属性名称数组不能为空");
		
		// 渲染sql
		for ( String field : fields )
			sql = renderField(sql, field, true);
		// 添加到and语句列表中
		andSet.add("(" + sql + ")");
		// 如果参数不为空，则添加到参数map中
		if ( !CollectionUtils.isEmpty(paramMap) )
			this.paramMap.putAll(paramMap);
		
		return this;
	}
	
	/**
	 * 添加or条件，属性前后需要有空格
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 条件语句
	 * @param field field
	 * @example 
	 * 	sql： birthday is null； field：birthday<br>
	 * 	sql： birthday is not null； field：birthday<br>
	 */
	public SqlWhere or(String sql, String field) {
		or(sql, field, null);
		
		return this;
	}
	
	/**
	 * 添加or条件，属性前后需要有空格
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 条件语句
	 * @param field field
	 * @param value value
	 * @example 
	 * 	sql： id = #{id}； field：id； value：1<br>
	 * 	sql： name like concat('%', #{name},'%')； field：name； value："you name"<br>
	 * 	sql： type in &lt;foreach>type&lt;/foreach>； field：type； value：Integer[] or List<Integer><br>
	 * 	sql： birthday is null； field：birthday； value：null<br>
	 * 	sql： birthday is not null； field：birthday； value：null<br>
	 */
	public SqlWhere or(String sql, String field, Object value) {
		Assert.hasLength(sql, "sql不能为空");
		Assert.hasLength(field, "属性名称不能为空");
		
		// 渲染sql
		sql = renderField(sql, field, true);
		// 添加sql
		orSet.add("(" + sql + ")");
		// 添加参数
		if ( null!=value )
			paramMap.put(field, value);
		
		return this;
	}
	
	/** 
	 * 添加or条件，属性前后需要有空格
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 条件语句
	 * @param fields 条件语句中涉及到的属性名称数组
	 * @param paramMap 条件语句中涉及到的参数值map
	 * @example 
	 * 	id = #{id} <br>
	 * 	name like concat('%', #{name},'%') <br>
	 * 	type in &lt;foreach>type&lt;/foreach> <br>
	 * 	birthday is null <br>
	 * 	birthday is not null <br>
	 */
	public SqlWhere or(String sql, String[] fields, Map<String, Object> paramMap) {
		Assert.hasLength(sql, "sql不能为空");
		Assert.notNull(fields, "属性名称数组不能为空");

		// 渲染sql
		for ( String field : fields )
			sql = renderField(sql, field, true);
		// 添加到or语句列表中
		orSet.add("(" + sql + ")");
		// 如果参数不为空，则添加到参数map中
		if ( !CollectionUtils.isEmpty(paramMap) )
			this.paramMap.putAll(paramMap);
		
		return this;
	}
	
	/** 
	 * 限制 field=value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere andEqualTo(String field, Object value) {
		// 生成sql
		String sql = " "+field+" = #{"+field+"}";
		// 添加and条件语句
		and(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 限制 field!=value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere andNotEqualTo(String field, Object value) {
		// 生成sql
		String sql = " "+field+" != #{"+field+"}";
		// 添加and条件语句
		and(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 限制 field like %value%
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere andLike(String field, Object value) {
		// 生成sql
		String sql = " "+field+" like concat('%', #{"+field+"}, '%')";
		// 添加and条件语句
		and(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 限制 field not like %value%
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere andNotLike(String field, Object value) {
		// 生成sql
		String sql = " "+field+" not like concat('%', #{"+field+"}, '%')";
		// 添加and条件语句
		and(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 限制 field in value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值（数组或list）
	 */
	public SqlWhere andIn(String field, Object value) {
		// 生成sql
		String sql = " "+field+" in <foreach>"+field+"</foreach>";
		// 添加and条件语句
		and(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 限制 field not in value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值（数组或list）
	 */
	public SqlWhere andNotIn(String field, Object value) {
		// 生成sql
		String sql = " "+field+" not in <foreach>"+field+"</foreach>";
		// 添加and条件语句
		and(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 限制 field &lt; value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere andLessThan(String field, Object value) {
		// 生成sql
		String sql = " "+field+" &lt; #{"+field+"}";
		// 添加and条件语句
		and(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 限制 field <= value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere andLessEqualTo(String field, Object value) {
		// 生成sql
		String sql = " "+field+" &lt;= #{"+field+"}";
		// 添加and条件语句
		and(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 限制 field > value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere andGreaterThan(String field, Object value) {
		// 生成sql
		String sql = " "+field+" > #{"+field+"}";
		// 添加and条件语句
		and(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 限制 field >= value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere andGreaterEqualTo(String field, Object value) {
		// 生成sql
		String sql = " "+field+" >= #{"+field+"}";
		// 添加and条件语句
		and(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 限制 field在startValue和endValue之间
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param startValue 开始值
	 * @param endValue 结束值
	 */
	public SqlWhere andBetween(String field, Object startValue, Object endValue) {
		// 生成sql
		String sql = " "+field+" between #{"+field+"Start} and #{" + field + "End}";
		// 添加and条件语句
		and(sql, field);
		// 添加参数
		paramMap.put(field + "Start", startValue);
		paramMap.put(field + "End", endValue);
		
		return this;
	}
	
	/** 
	 * 限制 field不在startValue和endValue之间
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param startValue 开始值
	 * @param endValue 结束值
	 */
	public SqlWhere andNotBetween(String field, Object startValue, Object endValue) {
		// 生成sql
		String sql = " "+field+" not between #{"+field+"Start} and #{" + field + "End}";
		// 添加and条件语句
		and(sql, field);
		// 添加参数
		paramMap.put(field + "Start", startValue);
		paramMap.put(field + "End", endValue);
		
		return this;
	}
	
	/** 
	 * 限制属性为空
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 */
	public SqlWhere andIsNull(String field) {
		// 生成sql
		String sql = " "+field+" is null";
		// 添加and条件语句
		and(sql, field);
		
		return this;
	}
	
	/** 
	 * 限制属性为空或空字符串
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 */
	public SqlWhere andIsNullOrEmpty(String field) {
		// 生成sql
		String sql = " "+field+" is null or "+field+" = ''";
		// 添加and条件语句
		and(sql, field);
		
		return this;
	}
	
	/** 
	 * 限制属性不为空
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 */
	public SqlWhere andIsNotNull(String field) {
		// 生成sql
		String sql = " "+field+" is not null";
		// 添加and条件语句
		and(sql, field);
		
		return this;
	}
	
	/** 
	 * 限制属性不为空值和空字符串
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 */
	public SqlWhere andIsNotNullAndNotEmpty(String field) {
		// 生成sql
		String sql = " "+field+" is not null and "+field+" != ''";
		// 添加and条件语句
		and(sql, field);
		
		return this;
	}
	
	/** 
	 * 或者 field = value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere orEqualTo(String field, Object value) {
		// 生成sql
		String sql = " "+field+" = #{"+field+"}";
		// 添加and条件语句
		or(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 或者 field != value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere orNoEqualTo(String field, Object value) {
		// 生成sql
		String sql = " "+field+" != #{"+field+"}";
		// 添加and条件语句
		or(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 或者 field like %value% 
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere orLike(String field, Object value) {
		// 生成sql
		String sql = " "+field+" like concat('%', #{"+field+"}, '%')";
		// 添加and条件语句
		or(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 或者 field not like %value% 
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere orNotLike(String field, Object value) {
		// 生成sql
		String sql = " "+field+" not like concat('%', #{"+field+"}, '%')";
		// 添加and条件语句
		or(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 或者 field in value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值（数组或list）
	 */
	public SqlWhere orIn(String field, Object value) {
		// 生成sql
		String sql = " "+field+" in <foreach>"+field+"</foreach>";
		// 添加and条件语句
		or(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 或者 field not in value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值（数组或list）
	 */
	public SqlWhere orNotIn(String field, Object value) {
		// 生成sql
		String sql = " "+field+" not in <foreach>"+field+"</foreach>";
		// 添加and条件语句
		or(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 或者 field &lt; value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere orLessThan(String field, Object value) {
		// 生成sql
		String sql = " "+field+" &lt; #{"+field+"}";
		// 添加and条件语句
		or(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 或者 field <= value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere orLessEqualTo(String field, Object value) {
		// 生成sql
		String sql = " "+field+" &lt;= #{"+field+"}";
		// 添加and条件语句
		or(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 或者 field > value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere orGreaterThan(String field, Object value) {
		// 生成sql
		String sql = " "+field+" > #{"+field+"}";
		// 添加and条件语句
		or(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 或者 field >= value
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param value	值
	 */
	public SqlWhere orGreaterEqualTo(String field, Object value) {
		// 生成sql
		String sql = " "+field+" >= #{"+field+"}";
		// 添加and条件语句
		or(sql, field, value);
		
		return this;
	}
	
	/** 
	 * 或者 field在startValue和startValue之间
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param startValue 开始值
	 * @param endValue 结束值
	 */
	public SqlWhere orBetween(String field, Object startValue, Object endValue) {
		// 生成sql
		String sql = " "+field+" between #{"+field+"Start} and #{" + field + "End}";
		// 添加and条件语句
		or(sql, field);
		// 添加参数
		paramMap.put(field + "Start", startValue);
		paramMap.put(field + "End", endValue);
		
		return this;
	}
	
	/** 
	 * 或者 field不在startValue和startValue之间
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 * @param startValue 开始值
	 * @param endValue 结束值
	 */
	public SqlWhere orNotBetween(String field, Object startValue, Object endValue) {
		// 生成sql
		String sql = " "+field+" not between #{"+field+"Start} and #{" + field + "End}";
		// 添加and条件语句
		or(sql, field);
		// 添加参数
		paramMap.put(field + "Start", startValue);
		paramMap.put(field + "End", endValue);
		
		return this;
	}
	
	/** 
	 * 或者field为空
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 */
	public SqlWhere orIsNull(String field) {
		// 生成sql
		String sql = " "+field+" is null";
		// 添加and条件语句
		or(sql, field);
		
		return this;
	}
	
	/** 
	 * 或者 field为空或空字符串
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 */
	public SqlWhere orIsNullOrEmpty(String field) {
		// 生成sql
		String sql = " "+field+" is null or "+field+" = ''";
		// 添加and条件语句
		or(sql, field);
		
		return this;
	}
	
	/** 
	 * 或者field不为空
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 */
	public SqlWhere orIsNotNull(String field) {
		// 生成sql
		String sql = " "+field+" is not null";
		// 添加and条件语句
		or(sql, field);
		
		return this;
	}
	
	/** 
	 * 或者 field不为空和空字符串
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param field	表实体的属性名称
	 */
	public SqlWhere orIsNotNullAndNotEmpty(String field) {
		// 生成sql
		String sql = " "+field+" is not null and "+field+" != ''";
		// 添加and条件语句
		or(sql, field);
		
		return this;
	}
	
	/**
	 * 渲染属性
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 待渲染field的sql
	 * @param isFullName 是否添加表别名前缀
	 * @return 
	 */
	protected String renderField(String sql, boolean isFullName) {
		Assert.hasLength(sql, "sql 不能为空");
		
		// 获取表缓存信息
		TableCache tableCache = getTableCache();
		// 获取字段缓存信息
		LinkedHashMap<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
		// 获取关系缓存信息
		LinkedHashMap<String, RelatedCache> relatedCacheMap = tableCache.getRelatedCacheMap();

		// 渲染属性
		for ( Entry<String, ColumnCache> entry : columnCacheMap.entrySet() ) {
			// 获取列字段信息
			ColumnCache columnCache = entry.getValue();
			// 渲染sql
			sql = renderField(sql, columnCache, isFullName);
		}
		// 渲染关系属性
		for ( Entry<String, RelatedCache> entry : relatedCacheMap.entrySet() ) {
			// 获取列字段信息
			RelatedCache relatedCache = entry.getValue();
			// 渲染sql
			sql = renderField(sql, relatedCache, isFullName);
		}
		
		// 返回结果
		return sql;
	}
	
	/**
	 * 渲染属性
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 待渲染field的sql
	 * @param field 要渲染的属性名
	 * @param isFullName 是否添加表别名前缀
	 * @return 
	 */
	protected String renderField(String sql, String field, boolean isFullName) {
		Assert.hasLength(sql, "sql 不能为空");
		
		// 获取表缓存信息
		TableCache tableCache = getTableCache();
		// 获取字段缓存信息
		LinkedHashMap<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
		// 获取关系缓存信息
		LinkedHashMap<String, RelatedCache> relatedCacheMap = tableCache.getRelatedCacheMap();
		
		// 渲染属性
		if ( columnCacheMap.containsKey(field) ) {
			ColumnCache columnCache = columnCacheMap.get(field);
			sql = renderField(sql, columnCache, isFullName);
		} else if ( relatedCacheMap.containsKey(field) ) {
			RelatedCache relatedCache = relatedCacheMap.get(field);
			sql = renderField(sql, relatedCache, isFullName);
		} else {
			throw new IllegalArgumentException("属性"+field+"不存在");
		}
		
		// 返回结果
		return sql;
	}
	
	/**
	 * 渲染属性
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 待渲染field的sql
	 * @param columnCache 字段缓存信息
	 * @param isFullName 是否添加表别名前缀
	 * @return 
	 */
	protected String renderField(String sql, ColumnCache columnCache, boolean isFullName) {
		Assert.hasLength(sql, "sql 不能为空");
		Assert.notNull(columnCache, "字段缓存信息不能为空");
		
		// 如果sql不包含该字段，则不渲染
		if ( sql.indexOf(" "+columnCache.getFieldName()+" ")==-1 )
			return sql;
		
		// 替换值名称
		if ( isFullName ) {
			sql = sql.replaceAll(" "+columnCache.getFieldName()+" ", " "+columnCache.getFullName()+" ");
		} else {
			sql = sql.replaceAll(" "+columnCache.getFieldName()+" ", " "+columnCache.getColumnName()+" ");
		}
		// 渲染参数
		sql = renderParam(sql);
		
		// 返回结果
		return sql;
	}
	
	/**
	 * 渲染属性
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 待渲染field的sql
	 * @param relatedCache 关系字段缓存信息
	 * @param isFullName 是否添加表别名前缀
	 * @return 
	 */
	protected String renderField(String sql, RelatedCache relatedCache, boolean isFullName) {
		Assert.hasLength(sql, "sql 不能为空");
		Assert.notNull(relatedCache, "关系字段缓存信息不能为空");
		
		// 如果sql不包含该字段，则不渲染
		if ( sql.indexOf(" "+relatedCache.getFieldName()+" ")==-1 )
			return sql;
		
		// 替换值名称
		if ( isFullName ) {
			sql = sql.replaceAll(" "+relatedCache.getFieldName()+" ", " "+relatedCache.getFullName()+" ");
			
			// 添加关联关系
			joinRelated(relatedCache.getFieldName());
		} else {
			sql = sql.replaceAll(" "+relatedCache.getFieldName()+" ", " "+relatedCache.getColumnName()+" ");
		}
		// 渲染参数
		sql = renderParam(sql);
		
		// 返回结果
		return sql;
	}
	
	/**
	 * 渲染参数
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 待渲染的sql语句
	 * @return 渲染完毕的sql语句
	 */
	protected String renderParam(String sql) {
		Assert.hasLength(sql, "sql 不能为空");
		
		return sql.replaceAll("#\\{", "#{paramMap.").replaceAll("\\$\\{", "\\${paramMap.")
				.replaceAll("<foreach>", "<foreach collection=\"paramMap.").replaceAll("</foreach>", "\" item=\"item\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach>");
	}
	
}
