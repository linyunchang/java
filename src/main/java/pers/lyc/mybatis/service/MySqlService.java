package pers.lyc.mybatis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import pers.lyc.mybatis.builder.MySqlMapper;
import pers.lyc.mybatis.core.pojo.Page;
import pers.lyc.mybatis.core.sql.mysql.MySqlDelete;
import pers.lyc.mybatis.core.sql.mysql.MySqlInsert;
import pers.lyc.mybatis.core.sql.mysql.MySqlSelect;
import pers.lyc.mybatis.core.sql.mysql.MySqlUpdate;

/**
 * MySqlService，建议项目中创建一个BaseService继承此service，其他service继承BaseService。以后如果切换项目使用的数据库，直接修改BaseService继承的service即可
 * @author 林运昌（linyunchang）
 * @since 2020年6月20日
 */
public class MySqlService<T> {
	
	@Autowired
	private MySqlMapper<T> mysqlMapper;
	
	/**
	 * 单条数据查询
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param sql 数据查询语句
	 * @return 
	 */
	public T selectOne(MySqlSelect<T> sql) {
		Assert.notNull(sql, "查询语句不能为空");
		
		// 数据查询
		T data = mysqlMapper.selectOne(sql);
		
		return data;
	}
	
	/**
	 * 单字段查询
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param sql 数据查询语句
	 * @return 
	 */
	public <C> List<C> selectColumn(MySqlSelect<T> sql) {
		Assert.notNull(sql, "查询语句不能为空");
		
		// 数据查询
		List<C> dataList = mysqlMapper.selectColumn(sql);
		
		return dataList;
	}
	
	/**
	 * 数据数量统计
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param sql 数据查询语句
	 * @return 
	 */
	public int count(MySqlSelect<T> sql) {
		Assert.notNull(sql, "查询语句不能为空");
		
		// 数据统计
		int count = mysqlMapper.count(sql);
		
		return count;
	}
	
	/**
	 * 数据列表查询
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param sql 数据查询语句
	 * @return 
	 */
	public List<T> select(MySqlSelect<T> sql) {
		Assert.notNull(sql, "查询语句不能为空");
		
		// 数据查询
		List<T> dataList = mysqlMapper.select(sql);
		
		return dataList;
	}
	
	/**
	 * 分页查询
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param sql 数据查询语句
	 * @param pageNo 页码
	 * @param limit 查询数量
	 * @return 
	 */
	public Page<T> selectPage(MySqlSelect<T> sql, Integer pageNo, Integer limit) {
		Assert.notNull(sql, "查询语句不能为空");
		
		// 查询数据总数
		int count = this.count(sql);
		// 创建分页查询数据
		Page<T> page = new Page<T>(count, pageNo, limit);
		// 设置分页数据
		sql.limit(page.getOffset(), page.getLimit());
		// 查询数据列表
		List<T> dataList = this.select(sql);
		// 设置数据结果
		page.setDataList(dataList);
		
		return page;
	}
	
	/**
	 * 批量新增数据
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param sql 新增语句
	 * @return 
	 */
	@Transactional
	public int insert(MySqlInsert<T> sql) {
		return insert(sql, 2000);
	}
	
	/**
	 * 新增数据
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param sql 数据新增语句
	 * @param maxCount 每次最大新增数据数量
	 * @return 
	 */
	@Transactional
	public int insert(MySqlInsert<T> sql, int maxCount) {
		Assert.notNull(sql, "新增语句不能为空");
		Assert.isTrue(maxCount>0, "每次最大新增数据数量需要大于0");
		
		// 获取新增数据列表
		List<T> dataList = sql.getDataList();
		if ( CollectionUtils.isEmpty(dataList) )
			return 0;
		// 批量新增成功条数
		int count = 0;
		// 待批量新增数据条数
		int listSize = dataList.size();
		// 如果待批量新增数据条数小于等于maxCount，则直接插入
		if ( listSize<=maxCount ) {
			// 数据新增
			count += mysqlMapper.insert(sql);
		} else { // 如果待批量新增数据条数大于maxCount，则拆分多次插入
			// 分批次插入数据，每次插入maxCount条
			int currentIndex = 0;
			// 循环拆分数据并新增
			List<T> dataListPart = null;
			while ( currentIndex+maxCount<listSize ) {
				// 拆分数据
				dataListPart = dataList.subList(currentIndex, currentIndex+maxCount);
				// 设置数据
				sql.values(dataListPart, true);
				// 数据新增
				count += mysqlMapper.insert(sql);
				// 索引处理
				currentIndex += maxCount;
			}
			// 拆分剩余的数据
			dataListPart = dataList.subList(currentIndex, listSize);
			// 设置数据
			sql.values(dataListPart, true);
			// 数据新增
			count += mysqlMapper.insert(sql);
		}
		
		return count;
	}
	
	/**
	 * 数据更新
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param sql 数据更新语句
	 * @return 
	 */
	@Transactional
	public int update(MySqlUpdate<T> sql) {
		Assert.notNull(sql, "更新语句不能为空");
		
		// 数据更新
		int count = mysqlMapper.update(sql);
		
		return count;
	}
	
	/**
	 * 数据删除
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月20日
	 * @param sql 数据删除语句
	 * @return 
	 */
	@Transactional
	public int delete(MySqlDelete<T> sql) {
		Assert.notNull(sql, "删除语句不能为空");
		
		// 数据删除
		int count = mysqlMapper.delete(sql);
		
		return count;
	}
	
}
