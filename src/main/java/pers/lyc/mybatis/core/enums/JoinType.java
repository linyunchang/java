package pers.lyc.mybatis.core.enums;

/**
 * 连接类型
 * @author 林运昌（linyunchang）
 * @since 2020年6月19日
 */
public enum JoinType {
	LEFT("left join", "左连接"), INNER("inner join", "内连接"), RIGHT("right join", "右连接"), CROSS("cross join", "交叉连接");

	private Integer code; // 数字代码
	private String en; // 英文值
	private String cn; // 中文值
	private boolean valid; // 是否有效

	/* custom */
	/** 根据数字代码获取枚举类型 */
	public static JoinType byCode(Integer code) {
		if (code == null)
			return null;

		// 遍历查询code对应的枚举对象
		for (JoinType value : JoinType.values()) {
			if (code == value.getCode().intValue()) {
				// 返回查询结果
				return value;
			}
		}
		return null;
	}

	/** 根据英文值获取枚举类型 */
	public static JoinType byEn(String en) {
		if (en == null || "".equals(en))
			return null;

		// 遍历查询en对应的枚举对象
		for (JoinType value : JoinType.values()) {
			if (value.getEn().equals(en)) {
				// 返回查询结果
				return value;
			}
		}
		return null;
	}

	/* constructor */
	private JoinType(Integer code, String cn) {
		this.code = code;
		this.en = this.toString();
		this.cn = cn;
		this.valid = true;
	}

	private JoinType(Integer code, String cn, boolean valid) {
		this.code = code;
		this.en = this.toString();
		this.cn = cn;
		this.valid = valid;
	}

	private JoinType(String en, String cn) {
		this.code = ordinal();
		this.en = en;
		this.cn = cn;
		this.valid = true;
	}

	private JoinType(String en, String cn, boolean valid) {
		this.code = ordinal();
		this.en = en;
		this.cn = cn;
		this.valid = valid;
	}

	/* getter */
	public Integer getCode() {
		return code;
	}

	public String getEn() {
		return en;
	}

	public String getCn() {
		return cn;
	}

	public boolean isValid() {
		return valid;
	}
}
