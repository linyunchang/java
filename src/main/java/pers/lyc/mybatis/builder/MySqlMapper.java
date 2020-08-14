package pers.lyc.mybatis.builder;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import pers.lyc.mybatis.core.sql.mysql.MySqlDelete;
import pers.lyc.mybatis.core.sql.mysql.MySqlInsert;
import pers.lyc.mybatis.core.sql.mysql.MySqlSelect;
import pers.lyc.mybatis.core.sql.mysql.MySqlUpdate;
import pers.lyc.mybatis.provider.mysql.MySqlDeleteSqlProvider;
import pers.lyc.mybatis.provider.mysql.MySqlInsertSqlProvider;
import pers.lyc.mybatis.provider.mysql.MySqlSelectSqlProvider;
import pers.lyc.mybatis.provider.mysql.MySqlUpdateSqlProvider;

/**
 * MySqlMapper，建议项目中创建一个BaseMapper继承此mapper，其他mapper继承BaseMapper。以后如果切换项目使用的数据库，直接修改BaseMapper继承的mapper即可。请使用3.5.4以上版本的mybatis。
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public interface MySqlMapper<T> extends SqlMapper {
	
	/**
	 * 单条数据查询
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 数据查询语句
	 * @return 
	 */
	@SelectProvider(type = MySqlSelectSqlProvider.class)
	public T selectOne(MySqlSelect<T> sql);
	
	/**
	 * 单字段查询
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 数据查询语句
	 * @return 
	 */
	@SelectProvider(type = MySqlSelectSqlProvider.class)
	public <C> List<C> selectColumn(MySqlSelect<T> sql);
	
	/**
	 * 数据数量统计
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 数据查询语句
	 * @return 
	 */
	@SelectProvider(type = MySqlSelectSqlProvider.class)
	public int count(MySqlSelect<T> sql);
	
	/**
	 * 数据列表查询
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 数据查询语句
	 * @return 
	 */
	@SelectProvider(type = MySqlSelectSqlProvider.class)
	public List<T> select(MySqlSelect<T> sql);
	
	/**
	 * 新增数据
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 数据新增语句
	 * @return 
	 */
	@InsertProvider(type = MySqlInsertSqlProvider.class)
	public int insert(MySqlInsert<T> sql);
	
	/**
	 * 数据更新
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 数据更新语句
	 * @return 
	 */
	@UpdateProvider(type = MySqlUpdateSqlProvider.class)
	public int update(MySqlUpdate<T> sql);
	
	/**
	 * 数据删除
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月19日
	 * @param sql 数据删除语句
	 * @return 
	 */
	@DeleteProvider(type = MySqlDeleteSqlProvider.class)
	public int delete(MySqlDelete<T> sql);
	
}
