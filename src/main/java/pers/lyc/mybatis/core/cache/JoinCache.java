package pers.lyc.mybatis.core.cache;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import pers.lyc.mybatis.core.annotation.Join;
import pers.lyc.mybatis.core.annotation.Table;
import pers.lyc.mybatis.core.enums.JoinType;

/**
 * 关联缓存
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public class JoinCache implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** Join注解对象 */
	private Join join;
	
	/** 关联表@Table */
	private Table joinTable;
	
	/** 关联表实体类 */
	private Class<?> joinTableCls;
	
	/** 关联表名 */
	private String joinTableName;
	
	/** 关联表别名 */
	private String joinTableAliasName;
	
	/** 关联表属性长度 */
	private Integer joinFieldLength;
	
	/** 关联条件 */
	private String joinOn;
	
	/** 关联类型 */
	private JoinType joinType;
	
	@Override
	public String toString() {
		if ( StringUtils.isBlank(joinTableName) )
			throw new IllegalArgumentException("joinTableName 不能为空");
		if ( StringUtils.isBlank(joinTableAliasName) )
			throw new IllegalArgumentException("joinTableAliasName 不能为空");
		if ( StringUtils.isBlank(joinOn) )
			throw new IllegalArgumentException("joinOn 不能为空");
		
		return joinTableName + " " + joinTableAliasName + " on " + joinOn;
	}

	public Join getJoin() {
		return join;
	}

	public void setJoin(Join join) {
		this.join = join;
	}

	public Table getJoinTable() {
		return joinTable;
	}

	public void setJoinTable(Table joinTable) {
		this.joinTable = joinTable;
	}

	public Class<?> getJoinTableCls() {
		return joinTableCls;
	}

	public void setJoinTableCls(Class<?> joinTableCls) {
		this.joinTableCls = joinTableCls;
	}

	public String getJoinTableName() {
		return joinTableName;
	}

	public void setJoinTableName(String joinTableName) {
		this.joinTableName = joinTableName;
	}

	public String getJoinTableAliasName() {
		return joinTableAliasName;
	}

	public void setJoinTableAliasName(String joinTableAliasName) {
		this.joinTableAliasName = joinTableAliasName;
	}

	public Integer getJoinFieldLength() {
		return joinFieldLength;
	}

	public void setJoinFieldLength(Integer joinFieldLength) {
		this.joinFieldLength = joinFieldLength;
	}

	public String getJoinOn() {
		return joinOn;
	}

	public void setJoinOn(String joinOn) {
		this.joinOn = joinOn;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}

}
