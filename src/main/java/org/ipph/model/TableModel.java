package org.ipph.model;

import java.util.List;

/**
 * 该类对象fromTable和toTable标签，表示一张表信息
 */
public class TableModel implements Cloneable{
	private TableOperationEnum type;
	private List<FieldModel> filedList;
	private List<SubtableModel> subTableList;
	private WhereModel whereModel;
	private String from;
	private String to;
	private boolean skip;
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
	public boolean isSkip() {
		return skip;
	}
	public void setSkip(boolean skip) {
		this.skip = skip;
	}
	public List<SubtableModel> getSubTableList() {
		return subTableList;
	}
	public void setSubTableList(List<SubtableModel> subTableList) {
		this.subTableList = subTableList;
	}
	public WhereModel getWhereModel() {
		return whereModel;
	}
	public void setWhereModel(WhereModel whereModel) {
		this.whereModel = whereModel;
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
