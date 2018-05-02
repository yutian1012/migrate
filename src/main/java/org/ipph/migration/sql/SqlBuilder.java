package org.ipph.migration.sql;

import java.util.ArrayList;
import java.util.List;

import org.ipph.exception.ConditionException;
import org.ipph.model.FieldModel;
import org.ipph.model.FieldRestrictEnum;
import org.ipph.model.TableModel;
import org.springframework.stereotype.Component;

@Component
public class SqlBuilder extends BaseSqlBuilder{
	
	/**
	 * 获取查询语句
	 * @param tableModel
	 * @return
	 * @throws ConditionException 
	 */
	public String getSelectSql(TableModel tableModel){

		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		List<String> fieldList=new ArrayList<>();
		
		//select查询字段
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())) continue;
			
			fieldList.add(field.getFrom());
		}
		
		String sql=getSelectSql(tableModel.getFrom(), fieldList);
		
		if(null!=sql){
			sbuilder.append(sql);
		}
		
		String condition=getFromCondition(tableModel.getWhereModel());
		
		if(null!=condition){
			sbuilder.append(condition);
		}
		
		return sbuilder.toString();
	}
	/**
	 * 获取查询语句
	 * @param tableModel
	 * @return
	 */
	public String getSelectCountSql(TableModel tableModel){

		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		//List<FieldModel> conditionList=new ArrayList<>();
		
		
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())) continue;
			
			/*if(null!=field.getCondition()&&null!=field.getCondition().getValue()){
				conditionList.add(field);
			}*/
		}
		String sql=getCountSql(tableModel.getFrom());
		
		if(null!=sql){
			sbuilder.append(sql);
		}
		
		String condition=null;
		
		/*if(null!=tableModel.getWhereModel()){
			for(FieldModel field:tableModel.getWhereModel().getFieldList()){
				if("".equals(field.getTo())||null==field.getTo()){
					conditionList.add(field);
				}
			}
			condition=getWhereByConditionField(conditionList);
		}*/
		condition=getFromCondition(tableModel.getWhereModel());
		
		if(null!=condition){
			sbuilder.append(condition);
		}
		
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
		
		List<String> fieldList=new ArrayList<>();
		
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			fieldList.add(field.getTo());
		}
		
		if(fieldList.size()>0){
			return getInsertSql(tableModel.getTo(),fieldList);
		}
		return null;
	}
	/**
	 * 错误记录插入语句
	 * @param tableModel
	 * @return
	 */
	public String getErrorInsertSql(TableModel tableModel){
		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		List<String> fieldList=new ArrayList<>();
		
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())){
				continue;
			}
			fieldList.add(field.getFrom());
		}
		
		if(fieldList.size()>0){
			return getInsertSql(tableModel.getFrom(),fieldList);
		}
		return null;
	}
	
	
	
	/**
	 * 获取update语句
	 * @param tableModel
	 * @return
	 */
	public String getUpdateSql(TableModel tableModel){
		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		List<String> fieldList=new ArrayList<>();
		
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			fieldList.add(field.getTo());
		}
		
		if(fieldList.size()==0) return null;
		
		String sql=getUpdateSql(tableModel.getTo(), fieldList);
		
		if(null!=sql){
			sbuilder.append(sql);
		}
		
		String condition=getTargetCondition(tableModel.getWhereModel());
		
		if(null!=condition){
			sbuilder.append(condition);
		}
		
		return sbuilder.toString();
	}
	
	/**
	 * 获取唯一键的查询语句判断语句
	 * @param tableModel
	 * @return
	 */
	public String isUniqueFieldExistsSelectSql(TableModel tableModel){
		List<String> uniqueFieldList=new ArrayList<>();
		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		
		for(FieldModel field:tableModel.getFiledList()){
			if(null!=field.getRestrict()&&field.getRestrict()==FieldRestrictEnum.UNIQUE){
				if(null!=field.getTo())
					uniqueFieldList.add(field.getTo());
			}
		}
		String sql=getCountSql(tableModel.getTo());
		
		if(null!=sql){
			sbuilder.append(sql);
		}
		
		String condition=getWhereByField(uniqueFieldList);
		
		if(null!=condition&&!"".equals(condition)){
			sbuilder.append(condition);
			return sbuilder.toString();
		}
		return null;
		
	}
	/**
	 * 获取待更新记录信息是否存在的判断语句
	 * 更新数据时调用
	 * @param tableModel
	 * @return
	 */
	public String isExistSelectSql(TableModel tableModel){
		
		if(tableModel.getFiledList().size()==0){
			return null;
		}
		
		StringBuilder sbuilder=new StringBuilder();
		//List<String> fieldList=new ArrayList<>();
		
		/*for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			if(null==field.getFrom()||"".equals(field.getFrom())){
				continue;
			}
			//from和to两个都有值作为关联条件
			fieldList.add(field.getTo());
		}*/
		
		String sql=getCountSql(tableModel.getTo());
		
		if(null!=sql){
			sbuilder.append(sql);
		}
		
		String condition=getTargetCondition(tableModel.getWhereModel());
		
		if(null!=condition){
			sbuilder.append(condition);
		}
		return sbuilder.toString();
		
	}
}
