package pers.lyc.mybatis.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import cn.hutool.core.util.ReflectUtil;
import pers.lyc.mybatis.core.annotation.Column;
import pers.lyc.mybatis.core.annotation.Table;

/**
 * mybatis工具类
 * @author 林运昌（linyunchang）
 * @since 2020年6月18日
 */
public class MybatisUtil {
	
	/**
	 * 获取表名
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param cls 表对象类型
	 * @return 
	 */
	public static String getTableName(Class<?> cls) {
		Assert.notNull(cls, "Class 不能为空");
		// 获取关联表@Table注解
		Table table = cls.getAnnotation(Table.class);
		Assert.notNull(table, cls.getName() + " 不是一个数据库表对象");
		
		return getTableName(cls, table);
	}
	
	/**
	 * 获取表名
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param cls 表对象类型
	 * @param table 表注解
	 * @return 
	 */
	public static String getTableName(Class<?> cls, Table table) {
		Assert.notNull(cls, "Class 不能为空");
		Assert.notNull(table, "@Table 不能为空");
		
		// 获取表名
		String tableName = table.enName();
		if ( StringUtils.isBlank(tableName) ) {
			String className = cls.getSimpleName();
			tableName = MybatisUtil.camelToUnder(className);
			if ( tableName.startsWith("_") )
				tableName = tableName.substring(1, tableName.length());
		}
		
		return tableName;
	}
	
	/**
	 * 根据Field获取列名
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param field 属性
	 * @return 
	 */
	public static String getColumnName(Field field) {
		Assert.notNull(field, "Field 不能为空");
		// 获取关联表@Table注解
		Column column = field.getAnnotation(Column.class);
		Assert.notNull(column, field.getName() + " 不是一个数据库列属性");
		
		return getColumnName(field, column);
	}
	
	/**
	 * 根据Field获取列名
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param field 属性
	 * @param column 列注释
	 * @return 
	 */
	public static String getColumnName(Field field, Column column) {
		Assert.notNull(column, "@Column 不能为空");
		Assert.notNull(field, "Field 不能为空");
		
		// 获取表名
		String columnName = column.enName();
		if ( StringUtils.isBlank(columnName) ) {
			String fieldName = field.getName();
			columnName = camelToUnder(fieldName);
			if ( columnName.startsWith("_") )
				columnName = columnName.substring(1, columnName.length());
		}
		
		return columnName;
	}
	
	/**
	 * 根据名称获取别名
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param name 要获取别名的名称
	 * @return 
	 */
	public static String getAliasName(String name) {
		Assert.hasLength(name, "名称不能为空");
		
		return Arrays.asList(name.replaceAll("\\d+","").split("_")).stream().map(f -> String.valueOf(f.charAt(0))).collect(Collectors.joining(""));
	}
	
	/**
	 * 驼峰转下划线
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param str 要进行转换的字符
	 * @return 
	 */
	public static String camelToUnder(String str) {
		Assert.hasLength(str, "字符串不能为空");
		
		return camelToSeparator(str, '_');
	}
	
	/**
	 * 下划线转驼峰
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param str 要进行转换的字符
	 * @return 
	 */
	public static String underToCamel(String str) {
		Assert.hasLength(str, "字符串不能为空");
		
		return separatorToCamel(str, '_');
	}
	
	/**
	 * 驼峰转短横线分隔
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param str 要进行转换的字符
	 * @return 
	 */
	public static String camelToKebab(String str) {
		Assert.hasLength(str, "字符串不能为空");
		
		return camelToSeparator(str, '-');
	}
	
	/**
	 * 短横线分隔转驼峰
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param str 要进行转换的字符
	 * @return 
	 */
	public static String kebabToCamel(String str) {
		Assert.hasLength(str, "字符串不能为空");
		
		return separatorToCamel(str, '-');
	}
	
	/**
	 * 驼峰转分隔符
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param str 要进行转换的字符
	 * @param separator 分隔符
	 * @return 
	 */
	public static String camelToSeparator(String str, char separator) {
		Assert.hasLength(str, "字符串不能为空");

		// 返回的下划线字符串
		StringBuilder sb = new StringBuilder();
		// 获取字符数组
		char[] chars = str.toCharArray();
		// 遍历字符串，判断每个字母大小写
		for ( char nowChar : chars ) {
			// 如果字符是大写字符，则将字符转换成小写，并在字符前面添加下划线
			if ( Character.isUpperCase(nowChar) ) {
				nowChar = Character.toLowerCase(nowChar);
				if ( sb.length()>0 ) 
					sb.append(separator);
			}
			// 添加字符
			sb.append(nowChar);
		}
		
		return sb.toString();
	}
	
	/**
	 * 分隔符转驼峰
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param str 要进行转换的字符
	 * @param separator 分隔符
	 * @return 
	 */
	public static String separatorToCamel(String str, char separator) {
		Assert.hasLength(str, "字符串不能为空");
		
		// 返回的驼峰字符串
		StringBuilder sb = new StringBuilder();
		// 获取字符数组
		char[] chars = str.toCharArray();
		// 遍历字符数组，判断是否存在分隔符
		for ( int i=0; i<chars.length; i++ ) {
			char nowChar = chars[i];
			// 如果当前字符不是分隔符，则转换成小写并添加到字符串中
			if ( nowChar!=separator ) {
				// 如果字符是大写字符，则将字符转换成小写
				if ( Character.isUpperCase(nowChar) ) 
					nowChar = Character.toLowerCase(nowChar); 
				// 添加字符
				sb.append(nowChar);
			} else { // 如果当前字符是分隔符，则跳过当前字符，下一个字符转换成大写
				// 坐标自增
				i++;
				// 如果坐标超过数组长度，则终止执行
				if ( i>=chars.length ) {
					break;
				} 
				// 获取下一个字符
				char nextChar = chars[i];
				// 如果下一个字符是小写，则转换成大写
				if ( Character.isLowerCase(nextChar) ) 
					nextChar = Character.toUpperCase(nextChar); 
				// 添加字符
				sb.append(nextChar);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 字符串首字母小写
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param str 要转换成首字母小写的字符串
	 * @return 
	 */
	public static String firstCharToLower(String str) {
		Assert.hasLength(str, "待转换字符串不能为空");
		
		// 获取字符数组
		char[] chars = str.toCharArray();
		// 如果第一个字符是小写英文字母，则转换成大写英文字母
		if ( Character.isUpperCase(chars[0]) )
			chars[0] = Character.toLowerCase(chars[0]);
		
		return String.valueOf(chars);
	}
	
	/**
	 * 字符串首字母大写
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param str 要转换成首字母大写的字符串
	 * @return 
	 */
	public static String firstCharToUpper(String str) {
		Assert.hasLength(str, "待转换字符串不能为空");
		
		// 获取字符数组
		char[] chars = str.toCharArray();
		// 如果第一个字符是小写英文字母，则转换成大写英文字母
		if ( Character.isLowerCase(chars[0]) )
			chars[0] = Character.toUpperCase(chars[0]);
		
		return String.valueOf(chars);
	}
	
	/**
	 * javaBean转换成map
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param value 进行转换的对象
	 * @return 
	 */
	public static Map<String, Object> toMap(Object value) {
		Assert.notNull(value, "进行转换的对象不能为空");
		
		return toMap(value, false);
	}
	
	/**
	 * javaBean转换成map
	 * @author 林运昌（linyunchang）
	 * @since 2020年6月18日
	 * @param valueObj 进行转换的对象
	 * @param hasNull 是否拥有空值
	 * @return 
	 */
	public static Map<String, Object> toMap(Object valueObj, boolean hasNull) {
		Assert.notNull(valueObj, "进行转换的对象不能为空");
		
		// 存放结果的map
		Map<String, Object> valueMap = new HashMap<>();
		
		// 获取类型
		Class<?> valueCls = valueObj.getClass();
		
		// 获取属性
		Field[] fields = ReflectUtil.getFields(valueCls);
		for ( Field field : fields ) {
			Object fieldValue = ReflectUtil.getFieldValue(valueObj, field);
			if ( null==fieldValue && !hasNull ) 
				continue;
			valueMap.put(field.getName(), fieldValue);
		}
		
		return valueMap;
	}
	
}
