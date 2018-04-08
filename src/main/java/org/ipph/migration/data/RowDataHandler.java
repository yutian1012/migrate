package org.ipph.migration.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ipph.condition.ConditionContext;
import org.ipph.exception.FormatException;
import org.ipph.format.FormaterContext;
import org.ipph.model.FieldModel;
import org.ipph.model.FieldRestrictEnum;
import org.ipph.model.TableModel;
import org.springframework.stereotype.Component;

@Component
public class RowDataHandler {
	@Resource
	private FormaterContext formaterContext;
	@Resource
	private ConditionContext conditionContext;
	/**
	 * 处理唯一键行数据并返回数值数组
	 * @param row
	 * @param table
	 * @return
	 * @throws Exception 
	 */
	public Object[] handleUniqueRowData(Map<String,Object> row,TableModel table) throws FormatException{
		List<FieldModel> fieldList=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(field.getRestrict()!=FieldRestrictEnum.UNIQUE) continue;
			
			fieldList.add(field);
		}
		
		if(fieldList.size()>0){
			return handleRowData(fieldList, row);
		}
		return null;
	}
	/**
	 * 获取待更新字段的参数值
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException
	 */
	public Object[] handle2UpdRowData(Map<String,Object> row,TableModel table)throws FormatException{
		List<FieldModel> fieldList=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			if(null==field.getFrom()||"".equals(field.getFrom())){
				continue;
			}
			
			fieldList.add(field);
		}
		
		if(fieldList.size()>0){
			return handleRowData(fieldList, row);
		}
		return null;
	}
	/**
	 * 获取待更新字段的参数值
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException
	 */
	public Object[] handle2MigrateRowData(Map<String,Object> row,TableModel table)throws FormatException{
		List<FieldModel> fieldList=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			fieldList.add(field);
		}
		
		if(fieldList.size()>0){
			return handleRowData(fieldList, row);
		}
		return null;
	}
	
	/**
	 * 获取待更新字段的参数值
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException
	 */
	public Object[] handle2ErrorRowData(Map<String,Object> row,TableModel table)throws FormatException{
		List<FieldModel> fieldList=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())){
				continue;
			}
			fieldList.add(field);
		}
		
		if(fieldList.size()>0){
			return handleRowData(fieldList, row);
		}
		return null;
	}
	
	
	/**
	 * 处理查询条件值
	 * @param table
	 * @return
	 */
	public Object[] handleFieldCondition(TableModel table){
		List<Object> result=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())) continue;
			
			if(null!=field.getCondition()&&null!=field.getCondition().getValue()){
				
				if(conditionContext.isValueSkip(field)){
					continue;
				}
				
				Object obj=conditionContext.getConditionParamValue(field);
				if(null!=obj){
					if(obj instanceof Object[]){
						for(Object o:(Object[])obj){
							result.add(o);
						}
					}
				}
				result.add(obj);
			}
		}
		
		return result.toArray();
	}
	
	/**
	 * 处理字段值
	 * @param row
	 * @param field
	 * @return
	 * @throws Exception 
	 */
	private Object processFieldValue(Map<String,Object> row,FieldModel field) throws FormatException{
		Object value=null;
		if(null!=field.getFrom()&&!"".equals(field.getFrom())){
			value=formaterContext.getFormatedValue(field.getFormat(),row.get(field.getFrom()));
		}
		//处理默认值
		if(null==value||"".equals(value)){
			if(null!=field.getDefaultValue()&&!"".equals(field.getDefaultValue())){
				value=field.getDefaultValue().trim();
			}
		}
		return value;
	}
	/**
	 * 处理数据映射，返回对象数组
	 * @param fieldList
	 * @param row
	 * @return
	 * @throws FormatException 
	 */
	private Object[] handleRowData(List<FieldModel> fieldList,Map<String,Object> row) throws FormatException{
		List<Object> result=new ArrayList<>();
		
		for(FieldModel field:fieldList){
			result.add(processFieldValue(row, field));
		}
		
		return result.toArray();
	}
}
