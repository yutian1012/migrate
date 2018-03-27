package org.ipph.model;

import java.util.List;

/**
 * 该类对象fromTable和toTable标签，表示一张表信息
 */
public class TableModel implements Cloneable{
	private TableOperationEnum type;
	private List<FieldModel> filedList;
	private String from;
	private String to;
	public TableOperationEnum getType() {
		return type;
	}
	public void setType(TableOperationEnum type) {
		this.type = type;
	}
	public List<FieldModel> getFiledList() {
		return filedList;
	}
	public void setFiledList(List<FieldModel> filedList) {
		this.filedList = filedList;
	}
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
	public TableModel copyTableModel(){
		try {
			return (TableModel) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
