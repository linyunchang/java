package pers.lyc.mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段注解，自动生成表时该注解会在表中生成对应的列，可配合@Join使用
 * @author 林运昌（linyunchang）
 * @date 2020年6月19日
 * @notice 该注解不能和@Related一起使用，否则会使@Related失效
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	/** 字段名称，未指定则自动转换属性名成字段名 */
	public String enName() default "";
	
	/** 字段中文名称，可用于前端代码生成展示 */
	public String cnName();
	
	/** 字段类型 */
	public String type() default "";
	
	/** 字段长度 */
	public int length() default 0;
	
	/** 字段默认值 */
	public String value() default "";
	
	/** 是否必填 */
	public boolean required() default true;
	
	/** 是否无符号 */
	public boolean unsigned() default true;
	
	/** 字段注释 */
	public String comment() default "";
}
