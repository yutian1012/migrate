package org.ipph.model;

import java.util.ArrayList;
import java.util.List;

public class WhereModel implements Cloneable{
	private List<FieldModel> fieldList=new ArrayList<>();
	public List<FieldModel> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<FieldModel> fieldList) {
		this.fieldList = fieldList;
	}
	
	public WhereModel copyWhereModel(){
		try {
			return (WhereModel) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
