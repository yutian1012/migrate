package org.ipph.model;

public enum FieldDataTypeEnum {
	VARCHAR("varchar"),DATE("date"),NUMERIC("numeric"),BLOB("blob"),TEXT("text");
	private String name;
	private FieldDataTypeEnum(String name){
		this.name=name;
	}
	public String getName(){
		return this.name;
	}
}
