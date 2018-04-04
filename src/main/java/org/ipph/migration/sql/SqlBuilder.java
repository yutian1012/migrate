package org.ipph.migration.sql;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ipph.condition.ConditionContext;
import org.ipph.model.FieldModel;
import org.ipph.model.FieldRestrictEnum;
import org.ipph.model.TableModel;
import org.springframework.stereotype.Component;

@Component
public class SqlBuilder {
	
	@Resource
	private ConditionContext conditionContext;
	
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
		List<FieldModel> conditionFieldList=new ArrayList<>();
		
		//select查询字段
		sbuilder.append("select ");
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())) continue;
			
			sbuilder.append(field.getFrom()).append(",");
			
			if(null!=field.getCondition()&&null!=field.getCondition().getValue()){
				conditionFieldList.add(field);
			}
		}
		sbuilder.setLength(sbuilder.length()-1);//去掉末尾的逗号
		sbuilder.append(" from ").append(tableModel.getFrom());
		
		//where条件构造
		if(conditionFieldList.size()>0){
			sbuilder.append(" where 1=1 ");
			for(FieldModel field:conditionFieldList){
				String condition=conditionContext.getConditionParam(field);
				if(null!=condition){
					sbuilder.append("and ").append(field.getFrom()).append(" ").append(condition);
				}
			}
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
		List<FieldModel> conditionFieldList=new ArrayList<>();
		
		//select查询字段
		sbuilder.append("select count(1) num ");
		sbuilder.append(" from ").append(tableModel.getFrom());
		
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())) continue;
			
			if(null!=field.getCondition()&&null!=field.getCondition().getValue()){
				conditionFieldList.add(field);
			}
		}
		//where条件构造
		if(conditionFieldList.size()>0){
			sbuilder.append(" where 1=1 ");
			for(FieldModel field:conditionFieldList){
				String condition=conditionContext.getConditionParam(field);
				if(null!=condition){
					sbuilder.append("and ").append(field.getFrom()).append(" ").append(condition);
				}
			}
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
		
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("insert into ").append(tableModel.getTo()).append(" (");
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			sbuilder.append(field.getTo()).append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);
		sbuilder.append(" )").append(" VALUES (");
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			sbuilder.append("?").append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);
		sbuilder.append(")");
		return sbuilder.toString();
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
		
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("insert into ").append(tableModel.getFrom()).append(" (");
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())){
				continue;
			}
			sbuilder.append(field.getFrom()).append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);
		sbuilder.append(" )").append(" VALUES (");
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())){
				continue;
			}
			sbuilder.append("?").append(",");
		}
		sbuilder.setLength(sbuilder.length()-1);
		sbuilder.append(")");
		return sbuilder.toString();
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
		sbuilder.append("update ").append(tableModel.getTo()).append(" set ");
		
		//to有值，from无值作为待更新的字段
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			if(null==field.getFrom()||"".equals(field.getFrom())){
				sbuilder.append(field.getTo()).append(" = ? ").append(",");
			}
		}
		sbuilder.setLength(sbuilder.length()-1);

		sbuilder.append(" where 1=1 ");
		
		//from和to两个都有值作为关联条件
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			if(null==field.getFrom()||"".equals(field.getFrom())){
				continue;
			}
			sbuilder.append(" and ").append(field.getTo()).append(" = ? ");
		}
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
			return getCountSql(tableModel.getTo(), uniqueFieldList);
		}
		return null;
		
	}
	/**
	 * 获取待更新记录信息是否存在的判断语句
	 * @param tableModel
	 * @return
	 */
	public String get2UpdFieldSelect(TableModel tableModel){
		
		List<FieldModel> toUpdFieldList=new ArrayList<>();
		
		if(tableModel.getFiledList().size()==0){
			return null;
		}
		//from和to两个都有值作为关联条件
		for(FieldModel field:tableModel.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			if(null==field.getFrom()||"".equals(field.getFrom())){
				continue;
			}
			toUpdFieldList.add(field);
		}
		
		if(toUpdFieldList.size()>0){
			return getCountSql(tableModel.getTo(), toUpdFieldList);
		}
		return null;
		
	}
	
	private String getSelectSql(String tableName,List<String> fieldList,List<FieldModel> conditionList){
		StringBuilder sbuilder=new StringBuilder();
		
		//select查询字段
		sbuilder.append("select ");
		
		for(String field:fieldList){
			
			sbuilder.append(field).append(",");
			
		}
		
		sbuilder.setLength(sbuilder.length()-1);//去掉末尾的逗号
		
		sbuilder.append(" from ").append(tableName);
		
		return sbuilder.toString();
	}
	
	/**
	 * 获取统计数量查询语句
	 * @param tableName
	 * @param fieldList
	 * @return
	 */
	private String getCountSql(String tableName,List<FieldModel> fieldList){
		if(fieldList.size()>0){
			
			List<String> fieldNameList=new ArrayList<>();
			
			for(FieldModel field:fieldList){
				if(null==field.getTo()||"".equals(field.getTo())){
					continue;
				}
				
				
				
				fieldNameList.add(field.getTo());
			}
			
			if(fieldNameList.size()>0){
				//return getCountSqlByField(tableName, fieldNameList);
			}
		}
		return null;
	}
	
	/**
	 * 获取统计数量查询语句
	 * @param tableName
	 * @param fieldNameList
	 * @return
	 */
	private String getCountSqlByField(String tableName,List<FieldModel> conditionList){
		
		StringBuilder sbuilder=new StringBuilder();
		
		sbuilder.append("select count(1) num from ").append(tableName);
		
		String condition=getWhereCondition(conditionList);
		if(null!=condition){
			sbuilder.append(condition);
		}
		return sbuilder.toString();
	}
	
	/**
	 * 获取sql语句的where条件
	 * @param conditionList
	 * @return
	 */
	private String getWhereCondition(List<FieldModel> conditionList){
		StringBuilder sbuilder=new StringBuilder();
		//where条件构造
		if(null!=conditionList&&conditionList.size()>0){
			
			sbuilder.append(" where 1=1 ");
			
			for(FieldModel field:conditionList){
				
				String condition=conditionContext.getConditionParam(field);
				
				if(null!=condition){
					sbuilder.append("and ").append(field.getFrom()).append(" ").append(condition);
				}
			}
		}
		return sbuilder.toString();
	}
	
}
