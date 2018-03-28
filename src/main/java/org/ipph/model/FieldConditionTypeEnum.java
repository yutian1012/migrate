package org.ipph.model;

public enum FieldConditionTypeEnum {
	LIKE("like"),LLike("left like"),RLIKE("right like"),EQUAL("equal"),NEQUAL("not equal"),IN("in");
	private String name;
	private FieldConditionTypeEnum(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
}
