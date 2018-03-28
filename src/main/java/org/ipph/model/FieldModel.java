package org.ipph.model;
/**
 * 该类对象xml的field标签
 */
public class FieldModel implements Cloneable{
	private String from;
	private String to;
	private String type;
	private String defaultValue;
	private FieldRestrictEnum restrict;//键值的约束问题
	private FieldFormatModel format;
	private FieldConditionModel condition;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public FieldFormatModel getFormat() {
		return format;
	}
	public void setFormat(FieldFormatModel format) {
		this.format = format;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public FieldRestrictEnum getRestrict() {
		return restrict;
	}
	public void setRestrict(FieldRestrictEnum restrict) {
		this.restrict = restrict;
	}
	public FieldConditionModel getCondition() {
		return condition;
	}
	public void setCondition(FieldConditionModel condition) {
		this.condition = condition;
	}
	public FieldModel copyFieldModel(){
		try {
			return (FieldModel) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
