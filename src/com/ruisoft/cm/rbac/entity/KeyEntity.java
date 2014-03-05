package com.ruisoft.cm.rbac.entity;

public class KeyEntity {
	/** 生成规则 */
	private String rule = null;
	
	/** 序号增长步长 */
	private int step = 1;
	
	/** 固定位长序号补位字符 */
	private String fillChar = "0";
	
	/**
	 * 序号计数周期 ，当新的周期开始时序号最大值将归0<br/>
	 * year:按年<br/>
	 * month:按月<br/>
	 * day:按天<br/>
	 * def:按自定义组<br/>
	 * none:无
	 */
	private String circle = "none";
	
	/** 分组计数  */
	private boolean group = false;
	
	/** 初始计数值 */
	private int init = 1;
	
	private String description = "";
	
	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getFillChar() {
		return fillChar;
	}

	public void setFillChar(String fillChar) {
		this.fillChar = fillChar;
	}

	public String getCircle() {
		return circle;
	}

	public void setCircle(String circle) {
		this.circle = circle;
	}

	public boolean isGroup() {
		return group;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	public int getInit() {
		return init;
	}

	public void setInit(int init) {
		this.init = init;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}