package pers.lyc.mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表注解
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
	/** 表名称，未指定则自动转换类名成表名 */
	public String enName() default "";
	
	/** 表中文名称，可用于前端代码生成展示 */
	public String cnName();
	
	/** 表主键 */
	public String primaryKey() default "id";
	
	/** 表唯一键：{"name"}或{"account", "name,phone"} */
	public String[] uniqueKeys() default {};
	
	/** 表索引：{"name"}或{"name", "name,phone"} */
	public String[] indexKeys() default {};
	
	/** 是否级联 */
	public boolean isCascade() default false;
	
	/** 表注释 */
	public String comment() default "";
}
