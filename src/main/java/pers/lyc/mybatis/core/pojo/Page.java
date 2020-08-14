package pers.lyc.mybatis.core.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页数据
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public class Page<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** 默认每页显示记录数 */
	public static final Integer DEFAULTLIMIT = 10;
	
	/** 当前页码 */
	private Integer pageNo;
	/** 查询偏移量 */
	private Integer offset;
	/** 每页显示数据条数 */
	private Integer limit;
	/** 总页数 */
	private int totalPage;
	/** 数据总数 */
	private int totalRecord;
	/** 数据列表 */
	private List<T> dataList;
	/** 每页显示记录数列表 */
	private List<Integer> limitList;
	/** 查询参数 */
	private Map<String, Object> params;
	/** 排序字段 */
	private String orderBy;
	/** 是否倒序 */
	private boolean desc;
	
	public Page() {
		super();
		this.pageNo = 1;
		this.limit = Page.DEFAULTLIMIT;
		this.totalPage = 0;
		this.totalRecord = 0;
		this.limitList = getDefaultLimitList();
		this.dataList = new ArrayList<>();
		this.params = new HashMap<String, Object>();
	}
	
	public Page(Integer pageNo, Integer limit) {
		super();
		this.setPageNo(pageNo);
		this.setLimit(limit);
		this.totalPage = 0;
		this.totalRecord = 0;
		this.limitList = getDefaultLimitList();
		this.dataList = new ArrayList<>();
		this.params = new HashMap<String, Object>();
	}
	
	public Page(int totalRecord, Integer pageNo, Integer limit) {
		super();
		this.setPageNo(pageNo);
		this.setLimit(limit);
		this.totalPage = 0;
		this.totalRecord = totalRecord;
		this.limitList = getDefaultLimitList();
		this.dataList = new ArrayList<>();
		this.params = new HashMap<String, Object>();
	}
	
	public Page(int totalRecord, List<T> dataList, Integer pageNo, Integer limit) {
		super();
		this.setPageNo(pageNo);
		this.setLimit(limit);
		this.totalPage = 0;
		this.totalRecord = totalRecord;
		this.limitList = getDefaultLimitList();
		this.dataList = dataList;
		this.params = new HashMap<String, Object>();
	}
	
	/** 默认每页数据量下拉列表 */
	private static List<Integer> getDefaultLimitList() {
		List<Integer> limitList = new ArrayList<Integer>();
		limitList.add(5);
		limitList.add(10);
		limitList.add(20);
		limitList.add(30);
		limitList.add(50);
		limitList.add(80);
		limitList.add(100);
		return limitList;
	}
	
	public int getPageNo() {
		this.pageNo = this.pageNo < 1 ? 1 : this.pageNo > getTotalPage() ? getTotalPage() : this.pageNo;
		return pageNo;
	}
	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo==null ? 1 : pageNo;
	}
	public int getOffset() {
		this.offset = (getPageNo()-1) * getLimit();
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit==null || limit<1 ? Page.DEFAULTLIMIT : limit;
	}
	public int getTotalPage() {
		this.totalPage = (getTotalRecord() - 1) / getLimit() + 1;
		if ( this.totalPage<1 ) 
			this.totalPage = 1;
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
	public List<Integer> getLimitList() {
		return limitList;
	}
	public void setLimitList(List<Integer> limitList) {
		this.limitList = limitList;
	}
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public boolean isDesc() {
		return desc;
	}
	public void setDesc(boolean desc) {
		this.desc = desc;
	}
}
