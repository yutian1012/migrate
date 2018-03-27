package org.ipph.model;

public enum TableOperationEnum {
	MIGRATE("迁移"),UPDATE("更新");
	private TableOperationEnum(String name){
		this.name=name;
	}
	private String name;

	public String getName() {
		return name;
	}
}
