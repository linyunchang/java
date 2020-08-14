package pers.lyc.mybatis.interceptors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.springframework.util.Assert;

import pers.lyc.mybatis.core.pojo.Page;

import cn.hutool.core.util.ReflectUtil;

/**
 * mybatis分页拦截器（查询数据总数并保存到page中）
 * @author 林运昌（linyunchang）
 * @since 2020年6月20日
 */
@Intercepts({ @Signature( type=StatementHandler.class, method="prepare", args={Connection.class, Integer.class} ) })
public class PagePrepareInterceptor implements Interceptor {

	/** 数据库类型，不同的数据库有不同的分页方法 */
	private String databaseType;
	
	public Object intercept(Invocation invocation) throws Throwable {
		//获取handler
		RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
		//获取delegate
		StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
		//获取BoundSql，等同于BoundSql boundSql = handler.getBoundSql()
		BoundSql boundSql = delegate.getBoundSql();
		//获取mapper接口的参数
		Object parameterObj = boundSql.getParameterObject();
		
		//判断参数是否是com.lyc.app.core.vo.Page类型，如果是，则执行分页查询业务逻辑
		if ( parameterObj instanceof Page ) {
			Page<?> page = (Page<?>) parameterObj;
			//拦截到的prepare方法参数是一个Connection对象
			Connection connection = (Connection) invocation.getArgs()[0];
			//通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
			
			MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
			//数据总数查询业务逻辑
			setTotalRecord(connection, mappedStatement, page);
			//分页查询处理业务逻辑
			handleBoundSql(boundSql, page);
		}

		Object obj = invocation.proceed();
		return obj;
	}
	
	/**
	 * 执行数据总数查询业务逻辑
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param connection
	 * @param mappedStatement
	 * @param page
	 */
	public void setTotalRecord(Connection connection, MappedStatement mappedStatement, Page<?> page) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//获取BoundSql，等同于BoundSql boundSql = delegate.getBoundSql()
			BoundSql boundSql = mappedStatement.getBoundSql(page);
			//获取数据总数统计的sql语句
			String countSql = getCountSql(boundSql.getSql());
			//获取参数列表
			List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
			
			//数据总数统计查询业务逻辑处理
			BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, parameterMappings, page);
			ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, page, countBoundSql);
			ps = connection.prepareStatement(countSql);
			parameterHandler.setParameters(ps);
			rs = ps.executeQuery();
			
			//设置查询出来的数据总数
			if ( rs.next() ) {
				page.setTotalRecord( rs.getInt(1) );
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close();} 
			catch (SQLException e) { e.printStackTrace(); }
			
			try { if (ps != null) ps.close(); } 
			catch (SQLException e) { e.printStackTrace(); }
		}
	}
	
	/**
	 * 获取分页查询语句
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param sql
	 * @return 
	 */
	public String getCountSql(String sql) {
		// 创建新的sql语句
		StringBuilder newSql = new StringBuilder();
		// 获取第一个from位置
		Integer fromIndex = sql.indexOf("from");
		Assert.notNull(fromIndex, "sql语句异常");
		// 获取group by位置
		Integer groupByIndex = sql.indexOf("group by");
		// sql语句处理
		if ( groupByIndex==-1 ) {
			newSql.append("select count(1) ");
			newSql.append(sql.substring(fromIndex, sql.length()));
		} else {
			newSql.append("select count(1) from (select 1 ");
			newSql.append(sql.substring(fromIndex, sql.length()));
			newSql.append(") temp");
		}
		
		return newSql.toString();
	}
	
	/**
	 * 分页语句处理
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param boundSql
	 * @param page
	 */
	public void handleBoundSql(BoundSql boundSql, Page<?> page) {
		if ( "mysql".equalsIgnoreCase(databaseType) ) {
			handleBoundSqlMySql(boundSql, page);
		}
	}
	
	/**
	 * mysql分页语句处理
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param boundSql
	 * @param page
	 */
	public void handleBoundSqlMySql(BoundSql boundSql, Page<?> page) {
		String sql = boundSql.getSql();
		StringBuilder sqlsb = new StringBuilder(sql);
		sqlsb.append(" limit ").append(page.getOffset()).append(",").append(page.getLimit());
		ReflectUtil.setFieldValue(boundSql, "sql", sqlsb.toString());
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
//		this.databaseType = properties.getProperty("databaseType");
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

}
