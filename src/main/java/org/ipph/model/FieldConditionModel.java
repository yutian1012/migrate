package org.ipph.model;

public class FieldConditionModel implements Cloneable{
	private String value;
	private FieldConditionTypeEnum conditionType;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public FieldConditionTypeEnum getConditionType() {
		return conditionType;
	}
	public void setConditionType(FieldConditionTypeEnum conditionType) {
		this.conditionType = conditionType;
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
