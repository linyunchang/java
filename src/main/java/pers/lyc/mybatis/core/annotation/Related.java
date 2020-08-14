package pers.lyc.mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段引用关系注解，自动生成表时该注解不会在表中生成对应的列，不能和@Column一起使用，否则无效
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Related {
	/** 字段引用关系，被引用的字段需要添加Join注解声明关联表对象 */
	public String value();
	
	/** 字段名，可用于前端代码生成展示 */
	public String name();
}
