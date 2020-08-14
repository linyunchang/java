package pers.lyc.mybatis.generate.buildr;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import pers.lyc.mybatis.generate.provider.mysql.MySqlTableSqlProvider;

/**
 * 数据库表操作mapper，请使用3.5.4以上版本的mybatis
 * @author 林运昌（linyunchang）
 * @since 2020年6月22日
 */
public interface TableMapper {

	/**
	 * 查询表字段名称
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param dbName 数据库名称
	 * @param tableName 表名称
	 * @return 
	 */
	@SelectProvider(type = MySqlTableSqlProvider.class)
	public List<String> selectTableColumn(@Param("dbName") String dbName, @Param("tableName") String tableName);
	
	/**
	 * 查询表索引
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param dbName 数据库名称
	 * @param tableName 表名称
	 * @return 
	 */
	@SelectProvider(type = MySqlTableSqlProvider.class)
	public List<String> selectTableIndex(@Param("dbName") String dbName, @Param("tableName") String tableName);
	
	/**
	 * 创建数据表
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param tableCls 表对象Class
	 * @return 
	 */
	@UpdateProvider(type = MySqlTableSqlProvider.class)
	public int createTable(@Param("tableCls") Class<?> tableCls);
	
	/**
	 * 添加表字段
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param tableCls 表对象
	 * @param fieldName 字段对应的属性名
	 * @return 
	 */
	@UpdateProvider(type = MySqlTableSqlProvider.class)
	public int addColumn(@Param("tableCls") Class<?> tableCls, @Param("fieldName") String fieldName);

	/**
	 * 添加表唯一键
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param tableCls 表对象
	 * @param uniqueKey 唯一键
	 * @return 
	 */
	@UpdateProvider(type = MySqlTableSqlProvider.class)
	public int addUniqueKey(@Param("tableCls") Class<?> tableCls, @Param("uniqueKey") String uniqueKey);
	
	/**
	 * 添加表索引
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月22日
	 * @param tableCls 表对象
	 * @param indexKey 索引
	 * @return 
	 */
	@UpdateProvider(type = MySqlTableSqlProvider.class)
	public int addIndexKey(@Param("tableCls") Class<?> tableCls, @Param("indexKey") String indexKey);
	
}
