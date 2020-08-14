package pers.lyc.mybatis.generate.provider.mysql;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.Assert;

import pers.lyc.mybatis.core.cache.ColumnCache;
import pers.lyc.mybatis.core.cache.TableCache;
import pers.lyc.mybatis.generate.provider.TableSqlProvider;
import pers.lyc.mybatis.util.TableCacheUtil;

/**
 * mysql数据库表操作provider
 * @author 林运昌（linyunchang）
 * @since 2020年6月22日
 */
public class MySqlTableSqlProvider extends TableSqlProvider {
	
	/**
	 * 查询表字段名称
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param context ProviderContext
	 * @param dbName 数据库名称
	 * @param tableName 表名称
	 * @return 
	 */
	public static String selectTableColumn(ProviderContext context, @Param("dbName") String dbName, @Param("tableName") String tableName) {
		Assert.hasLength(dbName, "数据库名不能为空");
		Assert.hasLength(tableName, "表名不能为空");
		
		// 创建SQL
		SQL sql = new SQL();
		sql.SELECT("column_name");
		sql.FROM("information_schema.columns");
		sql.WHERE("table_schema = #{dbName} and table_name = #{tableName}");
		sql.ORDER_BY("ordinal_position");
		
		System.out.println(sql.toString());
		
		return sql.toString();
	}
	
	/**
	 * 查询表索引
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param context ProviderContext
	 * @param dbName 数据库名称
	 * @param tableName 表名称
	 * @return 
	 */
	public static String selectTableIndex(ProviderContext context, @Param("dbName") String dbName, @Param("tableName") String tableName) {
		Assert.hasLength(dbName, "数据库名不能为空");
		Assert.hasLength(tableName, "表名不能为空");
		
		// 创建SQL
		SQL sql = new SQL();
		sql.SELECT("distinct stat_description");
		sql.FROM("mysql.innodb_index_stats");
		sql.WHERE("database_name = #{dbName} and table_name = #{tableName} and sample_size > 0");
		sql.ORDER_BY("last_update");
		
		return sql.toString();
	}
	
	/**
	 * 创建数据表
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param context ProviderContext
	 * @param tableCls 表对象Class
	 * @return 
	 */
	public static String createTable(ProviderContext context, @Param("tableCls") Class<?> tableCls) {
		Assert.notNull(tableCls, "表对象不能为空");
		
		// 获取表缓存信息
		TableCache tableCache = TableCacheUtil.getTableCache(tableCls);
		Map<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
		
		// 创建SQL
		StringBuilder sql = new StringBuilder();
		sql.append("create table ");
		sql.append(tableCache.getTableName());
		sql.append(" (");
		columnCacheMap.forEach((k, v) -> {
			// 列名
			sql.append(v.getColumnName());
			sql.append(" ");
			// 列类型
			sql.append(v.getColumnType());
			if ( v.getLength()!=0 ) {
				sql.append("(");
				sql.append(v.getLength());
				if ( "decimal".equalsIgnoreCase(v.getColumnType()) )
					sql.append(",2");
				sql.append(")");
			}
			// 是否无符号
			if ( "Integer".equals(v.getFieldType()) && v.getUnsigned() )
				sql.append(" unsigned ");
			// 是否自增
			if ( v.isAutoIncrement() )
				sql.append(" auto_increment ");
			// 非主键设置默认值
			if ( null!=v.getDefaultValue() && !k.equalsIgnoreCase(tableCache.getPrimaryKey()) && !v.isAutoIncrement() ) {
				sql.append(" default ");
				if ( "varchar".equalsIgnoreCase(v.getColumnType()) ) {
					sql.append("'");
					sql.append(v.getDefaultValue());
					sql.append("'");
				} else if ( "datetime".equalsIgnoreCase(v.getColumnType()) ) {
					sql.append("now()");
				} else {
					sql.append(v.getDefaultValue());
				}
			}
			// 非主键判断是否非空
			if ( v.getColumn().required() && !k.equalsIgnoreCase(tableCache.getPrimaryKey()) && !v.isAutoIncrement() )
				sql.append(" not null ");
			// 列注释
			sql.append(" comment '");
			sql.append(v.getComment());
			sql.append("',");
		});
		// 获取主键
		String[] primaryKeys = tableCache.getPrimaryKey().split(",");
		// 声明主键
		sql.append("primary key(");
		for ( int i=0; i<primaryKeys.length; i++ ) {
			if ( i>0 )
				sql.append(",");
			// 拼接主键
			String primaryKey = primaryKeys[i];
			sql.append(columnCacheMap.get(primaryKey).getColumnName());
		}
		sql.append(")");
		// 建立唯一键
		tableCache.getUniqueKeyList().forEach(uniqueKey -> {
			sql.append(",");
			sql.append("unique unique_" + uniqueKey.replaceAll(",", "_") + "(" + uniqueKey + ")");
		});
		// 建立索引
		tableCache.getIndexKeyList().forEach(indexKey -> {
			sql.append(",");
			sql.append("index index_" + indexKey.replaceAll(",", "_") + "(" + indexKey + ")");
		});
		sql.append(") engine=InnoDB default charset=utf8 comment='");
		sql.append(tableCache.getComment());
		sql.append("';");
		
		return sql.toString();
	}
	
	/**
	 * 添加表字段
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param context ProviderContext
	 * @param tableCls 表对象
	 * @param fieldName 字段对应的属性名
	 * @return 
	 */
	public static String addColumn(ProviderContext context, @Param("tableCls") Class<?> tableCls, @Param("fieldName") String fieldName) {
		Assert.notNull(tableCls, "表对象不能为空");
		Assert.hasLength(fieldName, "字段对应的属性名不能为空");
		
		// 获取表缓存信息
		TableCache tableCache = TableCacheUtil.getTableCache(tableCls);
		Map<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
		if ( !columnCacheMap.containsKey(fieldName) ) 
			throw new IllegalArgumentException("字段对应的属性名 " + fieldName + " 在表对象 " + tableCls.getName() + " 中不存在");
		ColumnCache columnCache = columnCacheMap.get(fieldName);
		
		// 创建SQL
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		sql.append(tableCache.getTableName());
		sql.append(" add ");
		// 列名
		sql.append(columnCache.getColumnName());
		sql.append(" ");
		// 列类型
		sql.append(columnCache.getColumnType());
		if ( columnCache.getLength()!=0 ) {
			sql.append("(");
			sql.append(columnCache.getLength());
			if ( "decimal".equalsIgnoreCase(columnCache.getColumnType()) )
				sql.append(",2");
			sql.append(")");
		}
		// 是否无符号
		if ( "Integer".equals(columnCache.getFieldType()) && columnCache.getUnsigned() )
			sql.append(" unsigned ");
		// 是否主键
		if ( fieldName.equalsIgnoreCase(tableCache.getPrimaryKey()) )
			sql.append(" primary key ");
		// 是否自增
		if ( columnCache.isAutoIncrement() )
			sql.append(" auto_increment ");
		// 非主键设置默认值
		if ( null!=columnCache.getDefaultValue() && !fieldName.equalsIgnoreCase(tableCache.getPrimaryKey()) ) {
			sql.append(" default ");
			if ( "varchar".equalsIgnoreCase(columnCache.getColumnType()) ) {
				sql.append("'");
				sql.append(columnCache.getDefaultValue());
				sql.append("'");
			} else if ( "datetime".equalsIgnoreCase(columnCache.getColumnType()) ) {
				sql.append("now()");
			} else {
				sql.append(columnCache.getDefaultValue());
			}
		}
		// 非主键判断是否非空
//		if ( columnCache.getColumn().required() && !fieldName.equalsIgnoreCase(tableCache.getPrimaryKey()) )
//			sql.append(" not null ");
		// 列注释
		sql.append(" comment '");
		sql.append(columnCache.getComment());
		sql.append("';");
		
		return sql.toString();
	}
	
	/**
	 * 添加表唯一键
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param context ProviderContext
	 * @param tableCls 表对象
	 * @param uniqueKey 唯一键
	 * @return 
	 */
	public static String addUniqueKey(ProviderContext context, @Param("tableCls") Class<?> tableCls, @Param("uniqueKey") String uniqueKey) {
		Assert.notNull(tableCls, "表对象不能为空");
		Assert.hasLength(uniqueKey, "表唯一键不能为空");
		
		// 获取表缓存信息
		TableCache tableCache = TableCacheUtil.getTableCache(tableCls);
		
		// 创建SQL
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		sql.append(tableCache.getTableName());
		sql.append(" add unique unique_");
		sql.append(uniqueKey.replaceAll(",", "_"));
		sql.append("(");
		sql.append(uniqueKey);
		sql.append(");");
		
		return sql.toString();
	}
	
	/**
	 * 添加表索引
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param context ProviderContext
	 * @param tableCls 表对象
	 * @param indexKey 索引
	 * @return 
	 */
	public static String addIndexKey(ProviderContext context, @Param("tableCls") Class<?> tableCls, @Param("indexKey") String indexKey) {
		Assert.notNull(tableCls, "表对象不能为空");
		Assert.hasLength(indexKey, "表索引不能为空");
		
		// 获取表缓存信息
		TableCache tableCache = TableCacheUtil.getTableCache(tableCls);
		
		// 创建SQL
		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		sql.append(tableCache.getTableName());
		sql.append(" add index index_");
		sql.append(indexKey.replaceAll(",", "_"));
		sql.append("(");
		sql.append(indexKey);
		sql.append(");");
		
		return sql.toString();
	}

}
