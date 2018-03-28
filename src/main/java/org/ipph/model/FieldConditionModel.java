package org.ipph.model;

public class FieldConditionModel implements Cloneable{
	private String value;
	private FieldConditionTypeEnum condition;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public FieldConditionTypeEnum getCondition() {
		return condition;
	}
	public void setCondition(FieldConditionTypeEnum condition) {
		this.condition = condition;
	}
	public FieldConditionModel copyFieldConditionModel(){
		try {
			return (FieldConditionModel) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
