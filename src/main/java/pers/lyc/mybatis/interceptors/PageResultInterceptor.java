package pers.lyc.mybatis.interceptors;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import pers.lyc.mybatis.core.pojo.Page;

import cn.hutool.core.util.ReflectUtil;

/**
 * mybatis分页拦截器（将查询结果记录集保存到page中）
 * @author 林运昌（linyunchang）
 * @since 2020年6月20日
 */
@Intercepts({ @Signature( type=ResultSetHandler.class, method="handleResultSets", args={Statement.class} ) })
public class PageResultInterceptor implements Interceptor {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object intercept(Invocation invocation) throws Throwable {
		Object obj = invocation.proceed();
		
		DefaultResultSetHandler resultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
		//获取BoundSql
		BoundSql boundSql = (BoundSql) ReflectUtil.getFieldValue(resultSetHandler, "boundSql");
		//获取mapper接口的参数
		Object parameterObj = boundSql.getParameterObject();
		
		//判断参数是否是com.lyc.app.core.vo.Page类型，如果是，则执行分页查询业务逻辑
		if ( parameterObj instanceof Page ) {
			Page page = (Page) parameterObj;
			//返回结果类型是ArrayList，则设置分页查询结果
			if ( obj instanceof ArrayList ) {
				List dataList = (ArrayList) obj;
				page.setDataList(dataList);
			}
		}
		
		return obj;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		
	}

}
