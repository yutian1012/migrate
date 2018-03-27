package org.ipph.migration.sql;

import org.ipph.model.FieldModel;
import org.ipph.model.TableModel;
import org.springframework.stereotype.Component;

@Component
public class SqlBuilder {
	/**
	 * 获取查询语句
	 * @param tableModel
	 * @return
	 */
	public String getSelectSql(TableModel tableModel){

		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("select ");
		int index=1;
		for(FieldModel field:tableModel.getFiledList()){
			sbuilder.append(field.getFrom());
			if(tableModel.getFiledList().size()>index++){
				sbuilder.append(",");
			}
		}
		sbuilder.append(" from ").append(tableModel.getFrom());
		return sbuilder.toString();
	}
	/**
	 * 获取insert语句
	 * @param tableModel
	 * @return
	 */
	public String getInsertSql(TableModel tableModel){
		
		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("insert into ").append(tableModel.getTo()).append(" (");
		int index=1;
		for(FieldModel field:tableModel.getFiledList()){
			sbuilder.append(field.getTo());
			if(tableModel.getFiledList().size()>index++){
				sbuilder.append(",");
			}
		}
		sbuilder.append(" )");
		sbuilder.append(" VALUES (");
		for(index=0;index<tableModel.getFiledList().size();index++){
			sbuilder.append("?");
			if(tableModel.getFiledList().size()-1!=index){
				sbuilder.append(",");
			}
		}
		sbuilder.append(")");
		return sbuilder.toString();
	}
}
