package pers.lyc.mybatis.generate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.util.Assert;

import cn.hutool.core.date.DateUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import pers.lyc.mybatis.core.cache.ColumnCache;
import pers.lyc.mybatis.core.cache.RelatedCache;
import pers.lyc.mybatis.core.cache.TableCache;
import pers.lyc.mybatis.generate.buildr.TableMapper;
import pers.lyc.mybatis.generate.factory.DataSourceFactory;
import pers.lyc.mybatis.util.TableCacheUtil;

/**
 * mybatis代码自动生成，请使用3.5.4以上版本的mybatis
 * @author 林运昌（linyunchang）
 * @date 2020年6月22日
 */
public class MybatisGenerate {
	
	/**
	 * 创建文件，如果文件已存在，则不创建
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月22日
	 * @param author 作者信息
	 * @param ftlFileName 模板文件名
	 * @param generatePath 文件生成路径
	 * @param fileName 生成的文件名
	 * @param cls 表数据对象类型
	 * @param cfg freemarker配置信息
	 */
	public static boolean createFile(String author, String ftlFileName, String generatePath, String fileName, Class<?> cls, Configuration cfg) {
		Assert.notNull(cls, "表数据对象类型不能为空");
		
		// 获取表缓存信息
		TableCache tableCache = getTableCache(cls);
		
		return createFile(author, ftlFileName, generatePath, fileName, tableCache, cfg);
	}
	
	/**
	 * 创建文件，如果文件已存在，则不创建
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月22日
	 * @param author 作者信息
	 * @param ftlFileName 模板文件名
	 * @param generatePath 文件生成路径
	 * @param fileName 生成的文件名
	 * @param tableCache 表缓存信息
	 * @param cfg freemarker配置信息
	 */
	public static boolean createFile(String author, String ftlFileName, String generatePath, String fileName, TableCache tableCache, Configuration cfg) {
		return createFile(author, ftlFileName, generatePath, fileName, tableCache, cfg, null);
	}
	
	/**
	 * 创建文件，如果文件已存在，则不创建
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月22日
	 * @param author 作者信息
	 * @param ftlFileName 模板文件名
	 * @param generatePath 文件生成路径
	 * @param fileName 生成的文件名
	 * @param cls 表数据对象类型
	 * @param cfg freemarker配置信息
	 * @param ftlInfo 用户自定义信息
	 */
	public static boolean createFile(String author, String ftlFileName, String generatePath, String fileName, Class<?> cls, Configuration cfg, Object ftlInfo) {
		Assert.notNull(cls, "表数据对象类型不能为空");
		
		// 获取表缓存信息
		TableCache tableCache = getTableCache(cls);
		
		return createFile(author, ftlFileName, generatePath, fileName, tableCache, cfg, ftlInfo);
	}
	
	/**
	 * 创建文件，如果文件已存在，则不创建
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月22日
	 * @param author 作者信息
	 * @param ftlFileName 模板文件名
	 * @param generatePath 文件生成路径
	 * @param fileName 生成的文件名
	 * @param tableCache 表缓存信息
	 * @param cfg freemarker配置信息
	 * @param ftlInfo 用户自定义信息
	 */
	public static boolean createFile(String author, String ftlFileName, String generatePath, String fileName, TableCache tableCache, Configuration cfg, Object ftlInfo) {
		Assert.notNull(cfg, "freemarker配置信息不存在");
		Assert.notNull(tableCache, "表缓存信息不能为空");
		Assert.hasLength(author, "作者信息不能为空");
		
		// 模板文件名或文件生成路径不存在则不生成直接返回
		if ( StringUtils.isBlank(ftlFileName) || StringUtils.isBlank(generatePath) ) 
			return false;
		
		// 文件生成路径处理
		if ( !generatePath.endsWith("/") ) 
			generatePath += "/";
		
		// ftl模板数据
		Map<String, Object> ftlMap = new HashMap<>();
		ftlMap.put("author", author);
		ftlMap.put("nowDate", DateUtil.format(new Date(), "yyyy年MM月dd日"));
		ftlMap.put("tableCache", tableCache);
		ftlMap.put("columnList", new ArrayList<ColumnCache>(tableCache.getColumnCacheMap().values()));
		ftlMap.put("relatedList", new ArrayList<RelatedCache>(tableCache.getRelatedCacheMap().values()));
		ftlMap.put("ftlInfo", ftlInfo);
		
		// 文件实例
		File outputFile = new File(generatePath + fileName);
		// 如果文件存在，则不执行操作直接返回
		if ( outputFile.exists() ) 
			return false;
		// 创建文件目录
		File parentFile = outputFile.getParentFile();
		if ( parentFile!=null && !parentFile.exists() ) 
			parentFile.mkdirs();
		// 获取模板并输出文件
		Writer out = null;
		try {
			// 创建文件输出流
			out = new FileWriter(outputFile);
			// 获取模板
			Template temp = cfg.getTemplate(ftlFileName);
			// 输出文件
			temp.process(ftlMap, out);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			// 关闭文件输出流
			try {
				if (null!=out) 
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
	/**
	 * 创建ftl配置信息
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月22日
	 * @param ftlDirectory ftl文件保存路径
	 * @return
	 */
	public static Configuration newFtlConfig(String ftlDirectory) {
		// ftl文件路径为空则返回空
		if ( StringUtils.isBlank(ftlDirectory) ) {
			System.out.println("ftl文件目录不存在，不生成文件");
			return null;
		}
		
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
		try {
			cfg.setDirectoryForTemplateLoading(new File(ftlDirectory));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        cfg.setDefaultEncoding("utf-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		return cfg;
	}
	
	/**
	 * 创建数据库表
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月22日
	 * @param dbName 数据库名称
	 * @param dbUrl 数据库地址
	 * @param dbUserName 数据库用户名称
	 * @param dbPassword 数据库用户密码
	 * @param clsArray 表对象类型数组
	 */
	public static void createTable(String dbName, String dbUrl, String dbUserName, String dbPassword, Class<?>[] clsArray) {
		Assert.hasText(dbName, "数据库名称不能为空");
		Assert.hasText(dbUrl, "数据库地址不能为空");
		Assert.hasText(dbUserName, "数据库用户名称不能为空");
		Assert.notEmpty(clsArray, "表缓存信息不能为空");
		
		// 连接数据库，获取SqlSession
		SqlSession session = openSession(dbUrl, dbUserName, dbPassword);
		// 获取TableMapper
		TableMapper tableMapper = session.getMapper(TableMapper.class);
		
		// 循环创建所有需要创立的表
		for ( Class<?> cls : clsArray ) {
			// 获取表缓存信息
			TableCache tableCache = getTableCache(cls);
			// 获取表名
			String tableName = tableCache.getTableName();
			// 查询表信息
			List<String> columnList = tableMapper.selectTableColumn(dbName, tableCache.getTableName()); // 表字段信息
			List<String> indexList = tableMapper.selectTableIndex(dbName, tableCache.getTableName()); // 表索引信息
			// 如果字段长度为空，则创建表
			if ( columnList.size()==0 ) {
				System.out.println();
				System.out.println("开始创建表：" + tableName + "...");
				// 创建表
				tableMapper.createTable(tableCache.getTableCls());
				System.out.println("表 " + tableName + " 创建完毕");
			} else { //字段存在，说明表存在，只增添新建字段
				System.out.println();
				System.out.println("开始检测 " + tableName + " 是否需要添加字段...");
				// 获取字段缓存信息
				Map<String, ColumnCache> columnCacheMap = tableCache.getColumnCacheMap();
				// 遍历字段缓存信息，如果字段不存在，则添加字段
				columnCacheMap.forEach((k, v) -> {
					// 获取字段名
					String columnName = v.getColumnName();
					// 字段不存在，则添加字段
					if ( !columnList.contains(columnName) ) {
						System.out.println();
						System.out.println("字段 " + columnName + " 不存在，开始添加...");
						tableMapper.addColumn(tableCache.getTableCls(), v.getFieldName());
						System.out.println("字段 " + columnName + " 添加完毕");
					}
				});
				System.out.println("表 " + tableName + " 字段检测完毕");
				System.out.println();
				System.out.println("表 " + tableName + " 已存在，不进行创建，开始检测是否需要添加索引...");
				// 获取唯一键
				List<String> uniqueKeyList = tableCache.getUniqueKeyList();
				// 判断是否需要添加唯一键
				uniqueKeyList.forEach(uniqueKey -> {
					if ( !indexList.contains(uniqueKey) ) {
						System.out.println();
						System.out.println("唯一键 " + uniqueKey + " 不存在，开始添加...");
						tableMapper.addUniqueKey(tableCache.getTableCls(), uniqueKey);
						System.out.println("唯一键 " + uniqueKey + " 添加完毕");
						System.out.println();
					}
				});
				// 获取索引
				List<String> indexKeyList = tableCache.getIndexKeyList();
				// 判断是否需要添加索引
				indexKeyList.forEach(indexKey -> {
					if ( !indexList.contains(indexKey) ) {
						System.out.println();
						System.out.println("索引 " + indexKey + " 不存在，开始添加...");
						tableMapper.addIndexKey(tableCache.getTableCls(), indexKey);
						System.out.println("索引 " + indexKey + " 添加完毕");
						System.out.println();
					}
				});
				System.out.println("表 " + tableName + " 索引检测完毕");
			}
		}
		
		// 提交事务
		session.commit();
		// 关闭数据库连接
		session.close();
	}
	
	/**
	 * 开启新的数据库连接，返回SqlSession
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月22日
	 * @param url 数据库连接地址
	 * @param userName 数据库用户名
	 * @param password 数据库用户密码
	 * @return
	 */
	public static SqlSession openSession(String url, String userName, String password) {
		Assert.hasLength(url, "数据库连接地址不能为空");
		Assert.hasLength(userName, "数据库用户名不能为空");
		
		DataSource dataSource = DataSourceFactory.createBasicDataSource(url, userName, password);
		TransactionFactory transactionFactory = new JdbcTransactionFactory();
		Environment environment = new Environment("development", transactionFactory, dataSource);
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(environment);
		configuration.setMapUnderscoreToCamelCase(true);
		configuration.addMapper(TableMapper.class);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		SqlSession session = sqlSessionFactory.openSession();
		
		return session;
	}
	
	/**
	 * 获取表缓存信息
	 * @author 林运昌（linyunchang）
	 * @date 2020年6月22日
	 * @param cls 表对象类型
	 * @return
	 */
	public static TableCache getTableCache(Class<?> cls) {
		return TableCacheUtil.getTableCache(cls);
	}
	
}
