package org.ipph.model;

public class SubtableModel extends TableModel {
	public SubtableModel copySubtableModel(){
		try {
			return (SubtableModel) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
