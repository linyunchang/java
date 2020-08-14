package pers.lyc.mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 枚举对象，用于自动生成代码
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumClass {
	/** 枚举对象 */
	public Class<?> cls();
	
	/** 取值属性，默认从code获取值 */
	public String valueField() default "code";
}
