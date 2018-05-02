package org.ipph.migration.sql;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ipph.condition.ConditionContext;
import org.ipph.exception.ConditionException;
import org.ipph.model.FieldModel;
import org.ipph.model.WhereModel;

public class BaseSqlBuilder {
	
	@Resource
	private ConditionContext conditionContext;
	/**
	 * 获取查询统计数量语句
	 * @param tableName
	 * @return
	 */
	protected String getCountSql(String tableName){
		StringBuilder sbuilder=new StringBuilder();
		
		sbuilder.append("select count(1) num from ").append(tableName);
		
		return sbuilder.toString();
	}
	/**
	 * 获取查询语句
	 * @param tableName
	 * @param fieldList
	 * @return
	 */
	protected String getSelectSql(String tableName,List<String> selectFieldList){
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("select ");
		for(String field:selectFieldList){
			sbuilder.append(field).append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);//去掉末尾的逗号
		sbuilder.append(" from ").append(tableName);
		
		return sbuilder.toString();
	}
	
	
	/**
	 * 获取插入语句
	 * @param tableName
	 * @param fieldList
	 * @return
	 */
	protected String getInsertSql(String tableName,List<String> insertFieldList){
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("insert into ").append(tableName).append(" (");
		for(String field:insertFieldList){
			sbuilder.append(field).append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);
		sbuilder.append(" )").append(" VALUES (");
		for(String field:insertFieldList){
			sbuilder.append("?").append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);
		sbuilder.append(")");
		return sbuilder.toString();
	}
	/**
	 * 获取更新语句
	 * @param tableName
	 * @param updateFieldList
	 * @return
	 */
	protected String getUpdateSql(String tableName,List<String> updateFieldList){
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("update ").append(tableName).append(" set ");
		
		//to有值，from无值作为待更新的字段
		for(String field:updateFieldList){
			sbuilder.append(field).append(" = ? ").append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);

		return sbuilder.toString();
	}
	
	/**
	 * 获取查询的where条件
	 * @param whereModel
	 * @return
	 */
	protected String getFromCondition(WhereModel whereModel){
		List<FieldModel> conditionList=new ArrayList<>();
		if(null!=whereModel){
			for(FieldModel field:whereModel.getFieldList()){
				if(null==field.getFrom()||"".equals(field.getFrom())){
					continue;
				}
				
				if(null!=field.getCondition()
						&&null!=field.getCondition().getValue()
						&&!"".equals(field.getCondition().getValue())){
					conditionList.add(field);
				}
			}
		}
		return getWhereByConditionField(conditionList);
	}
	/**
	 * 获取目标表的where条件
	 * @param whereModel
	 * @return
	 */
	protected String getTargetCondition(WhereModel whereModel){
		List<FieldModel> conditionList=new ArrayList<>();
		if(null!=whereModel){
			for(FieldModel field:whereModel.getFieldList()){
				if(null==field.getTo()||"".equals(field.getTo())){
					continue;
				}
				
				conditionList.add(field);
			}
		}
		return getWhereByConditionField(conditionList);
	}
	
	
	/**
	 * 获取sql语句的where条件
	 * 使用Condition字段获取条件
	 * @param conditionList
	 * @return
	 * @throws ConditionException 
	 */
	protected String getWhereByConditionField(List<FieldModel> conditionList){
		StringBuilder sbuilder=new StringBuilder();
		//where条件构造
		if(null!=conditionList&&conditionList.size()>0){
			
			sbuilder.append(" where 1=1 ");
			
			for(FieldModel field:conditionList){
				
				String condition=conditionContext.getConditionParam(field);
				
				if(null!=condition){
					sbuilder.append(" and ").append(field.getFrom()).append(" ").append(condition);
				}
			}
		}
		return sbuilder.toString();
	}
	/**
	 * 获取sql语句的where条件
	 * 使用field字段获取条件
	 * @param conditionList
	 * @return
	 */
	protected String getWhereByField(List<String> conditionList){
		StringBuilder sbuilder=new StringBuilder();
		//where条件构造
		if(null!=conditionList&&conditionList.size()>0){
			
			sbuilder.append(" where 1=1 ");
			
			for(String field:conditionList){
				
				
				if(null!=field&&!"".equals(field)){
					sbuilder.append(" and ").append(field).append(" = ? ");
				}
			}
		}
		return sbuilder.toString();
	}
}
