package pers.lyc.mybatis.core.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import pers.lyc.mybatis.core.annotation.Table;

/**
 * 表结构缓存
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public class TableCache implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** Table注解对象 */
	private Table table;
	
	/** 表对象 */
	private Class<?> tableCls;
	
	/** 表名 */
	private String tableName;
	
	/** 表对象名 */
	private String className;
	
	/** 表对象名（简写） */
	private String classSimpleName;
	
	/** 表对象名（短横线分隔） */
	private String kebabName;
	
	/** 表别名 */
	private String aliasName;
	
	/** 表中文名 */
	private String cnName;
	
	/** 表主键 */
	private String primaryKey;
	
	/** 表主键类型 */
	private String primaryKeyType;
	
	/** 表注释 */
	public String comment;
	
	/** 字段信息 */
	private LinkedHashMap<String, ColumnCache> columnCacheMap = new LinkedHashMap<>();
	
	/** 关联信息 */
	private LinkedHashMap<String, JoinCache> joinCacheMap = new LinkedHashMap<>();
	
	/** 字段关系 */
	private LinkedHashMap<String, RelatedCache> relatedCacheMap = new LinkedHashMap<>();
	
	/** 唯一键列表 */
	private List<String> uniqueKeyList = new ArrayList<>();
	
	/** 索引列表 */
	private List<String> indexKeyList = new ArrayList<>();

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public Class<?> getTableCls() {
		return tableCls;
	}

	public void setTableCls(Class<?> tableCls) {
		this.tableCls = tableCls;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassSimpleName() {
		return classSimpleName;
	}

	public void setClassSimpleName(String classSimpleName) {
		this.classSimpleName = classSimpleName;
	}

	public String getKebabName() {
		return kebabName;
	}

	public void setKebabName(String kebabName) {
		this.kebabName = kebabName;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getPrimaryKeyType() {
		return primaryKeyType;
	}

	public void setPrimaryKeyType(String primaryKeyType) {
		this.primaryKeyType = primaryKeyType;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LinkedHashMap<String, ColumnCache> getColumnCacheMap() {
		return columnCacheMap;
	}

	public void setColumnCacheMap(LinkedHashMap<String, ColumnCache> columnCacheMap) {
		this.columnCacheMap = columnCacheMap;
	}

	public LinkedHashMap<String, JoinCache> getJoinCacheMap() {
		return joinCacheMap;
	}

	public void setJoinCacheMap(LinkedHashMap<String, JoinCache> joinCacheMap) {
		this.joinCacheMap = joinCacheMap;
	}

	public LinkedHashMap<String, RelatedCache> getRelatedCacheMap() {
		return relatedCacheMap;
	}

	public void setRelatedCacheMap(LinkedHashMap<String, RelatedCache> relatedCacheMap) {
		this.relatedCacheMap = relatedCacheMap;
	}

	public List<String> getUniqueKeyList() {
		return uniqueKeyList;
	}

	public void setUniqueKeyList(List<String> uniqueKeyList) {
		this.uniqueKeyList = uniqueKeyList;
	}

	public List<String> getIndexKeyList() {
		return indexKeyList;
	}

	public void setIndexKeyList(List<String> indexKeyList) {
		this.indexKeyList = indexKeyList;
	}
	
}
