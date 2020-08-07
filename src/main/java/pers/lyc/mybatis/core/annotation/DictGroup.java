package pers.lyc.mybatis.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据字典-字典组，用于自动生成代码
 * @author 林运昌（linyunchang）
 * @date 2020年6月19日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DictGroup {
	/** 字典组名称 */
	public String name();
}
