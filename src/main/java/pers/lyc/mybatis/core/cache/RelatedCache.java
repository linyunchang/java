package pers.lyc.mybatis.core.cache;

import java.io.Serializable;

import pers.lyc.mybatis.core.annotation.Related;

/**
 * 字段关系缓存
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public class RelatedCache implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** @Related */
	private Related related;
	
	/** 关系表别名 */
	private String tableAliasName;
	
	/** 字段列名 */
	private String columnName;
	
	/** 字段属性名 */
	private String fieldName;
	
	/** 字段完整名称 */
	private String fullName;
	
	/** 字段别名 */
	private String aliasName;
	
	/** 字段中文名 */
	private String cnName;
	
	/** 属性类型 */
	private String fieldType;
	
	/** 是否枚举 */
	private boolean isEnum;
	
	/** 枚举对象类型 */
	private Class<?> enumClass;
	
	/** 枚举对象名称 */
	private String enumName;
	
	/** 枚举对象简称 */
	private String enumSimpleName;
	
	/** 是否数据字典 */
	private boolean isDict;
	
	/** 数据字典-字典组名称 */
	private String dictGroupName;

	public Related getRelated() {
		return related;
	}

	public void setRelated(Related related) {
		this.related = related;
	}

	public String getTableAliasName() {
		return tableAliasName;
	}

	public void setTableAliasName(String tableAliasName) {
		this.tableAliasName = tableAliasName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFullName() {
		if ( null==fullName ) {
			fullName = tableAliasName + "." + columnName;
		}
		
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public boolean getIsEnum() {
		return isEnum;
	}

	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	public Class<?> getEnumClass() {
		return enumClass;
	}

	public void setEnumClass(Class<?> enumClass) {
		this.enumClass = enumClass;
	}

	public String getEnumName() {
		return enumName;
	}

	public void setEnumName(String enumName) {
		this.enumName = enumName;
	}

	public String getEnumSimpleName() {
		return enumSimpleName;
	}

	public void setEnumSimpleName(String enumSimpleName) {
		this.enumSimpleName = enumSimpleName;
	}

	public boolean getIsDict() {
		return isDict;
	}

	public void setDict(boolean isDict) {
		this.isDict = isDict;
	}

	public String getDictGroupName() {
		return dictGroupName;
	}

	public void setDictGroupName(String dictGroupName) {
		this.dictGroupName = dictGroupName;
	}
	
}
