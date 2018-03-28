package org.ipph.migration.sql;

import java.util.ArrayList;
import java.util.List;

import org.ipph.model.FieldModel;
import org.ipph.model.FieldRestrictEnum;
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
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())) continue;
			sbuilder.append(field.getFrom()).append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);//去掉末尾的逗号
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
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getTo()||!"".equals(field.getTo())){
				continue;
			}
			sbuilder.append(field.getTo()).append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);
		sbuilder.append(" )").append(" VALUES (");
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getTo()||!"".equals(field.getTo())){
				continue;
			}
			sbuilder.append("?").append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);
		sbuilder.append(")");
		return sbuilder.toString();
	}
	/**
	 * 获取唯一键的查询语句判断语句
	 * @param tableModel
	 * @return
	 */
	public String getUniqueFieldSelect(TableModel tableModel){
		List<FieldModel> uniqueFieldList=new ArrayList<>();
		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		for(FieldModel field:tableModel.getFiledList()){
			if(null!=field.getRestrict()&&field.getRestrict()==FieldRestrictEnum.UNIQUE){
				uniqueFieldList.add(field);
			}
		}
		
		if(uniqueFieldList.size()>0){
			StringBuilder sbuilder=new StringBuilder();
			
			sbuilder.append("select count(1) num from ").append(tableModel.getTo()).append(" where 1=1 ");
			
			for(FieldModel field:uniqueFieldList){
				if(null==field.getTo()||!"".equals(field.getTo())){
					continue;
				}
				sbuilder.append(" and ").append(field.getTo()).append("=?");
			}
			
			return sbuilder.toString();
		}
		
		return null;
	}
}
