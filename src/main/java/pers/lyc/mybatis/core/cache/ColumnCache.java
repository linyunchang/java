package pers.lyc.mybatis.core.cache;

import java.io.Serializable;

import pers.lyc.mybatis.core.annotation.Column;

/**
 * 字段缓存
 * @author 林运昌（linyunchang）
 * @date 2020年6月19日
 */
public class ColumnCache implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** @Column */
	private Column column;
	
	/** 字段所属表别名 */
	private String tableAliasName;
	
	/** 字段列名 */
	private String columnName;
	
	/** 字段属性名 */
	private String fieldName;
	
	/** 表对象名（短横线分隔） */
	private String kebabName;
	
	/** 字段完整名称（表别名.列名） */
	private String fullName;
	
	/** 字段别名 */
	private String aliasName;
	
	/** 字段中文名 */
	private String cnName;
	
	/** 属性类型 */
	private String fieldType;
	
	/** 字段类型 */
	private String columnType;
	
	/** 字段长度 */
	private Integer length;

	/** 是否无符号 */
	private Boolean unsigned;

	/** 是否必填 */
	private boolean isRequired;
	
	/** 字段默认值 */
	private Object defaultValue;

	/** 是否自增（Integer或Long类型主键默认自增） */
	private boolean autoIncrement;
	
	/** 字段注释 */
	private String comment;
	
	/** 是否枚举 */
	private boolean isEnum;
	
	/** 枚举对象类型 */
	private Class<?> enumClass;
	
	/** 枚举对象名称 */
	private String enumName;
	
	/** 枚举对象简称 */
	private String enumSimpleName;
	
	/** 枚举对象取值属性 */
	private String enumValueField;
	
	/** 是否数据字典 */
	private boolean isDict;
	
	/** 数据字典-字典组名称 */
	private String dictGroupName;
	
	/** 关联表 */
	private JoinCache joinCache;
	
	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
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

	public String getKebabName() {
		return kebabName;
	}

	public void setKebabName(String kebabName) {
		this.kebabName = kebabName;
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

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Boolean getUnsigned() {
		return unsigned;
	}

	public void setUnsigned(Boolean unsigned) {
		this.unsigned = unsigned;
	}

	public boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public String getEnumValueField() {
		return enumValueField;
	}

	public void setEnumValueField(String enumValueField) {
		this.enumValueField = enumValueField;
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

	public JoinCache getJoinCache() {
		return joinCache;
	}

	public void setJoinCache(JoinCache joinCache) {
		this.joinCache = joinCache;
	}
	
}
