package pers.lyc.mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pers.lyc.mybatis.core.enums.JoinType;

/**
 * 列-表关联注解，只能配合@Column使用
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Join {
	/** 关联表对象，关联后关系字段可使用@Related注解引用该表的字段信息 */
	public Class<?> table();
	
	/** 关联类型 */
	public JoinType joinType() default JoinType.LEFT;
	
	/** 关联属性，未指定默认关联该表的主键 */
	public String field() default "";
	
	/** 是否创建外键 */
	public boolean foreignKey() default false;
}
