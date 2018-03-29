package org.ipph.condition;

import java.util.List;

import org.ipph.model.FieldConditionTypeEnum;
import org.ipph.model.FieldModel;

public class ConditionContext {
	
	private List<Condition> conditionList;
	
	
	public List<Condition> getConditionList() {
		return conditionList;
	}

	public void setConditionList(List<Condition> conditionList) {
		this.conditionList = conditionList;
	}

	/**
	 * 获取条件参数
	 * @param field
	 * @return
	 */
	public String getConditionParam(FieldModel field){
		return (String) getConditionParamInfo(field,true);
	}
	/**
	 * 获取条件数据
	 * @param field
	 * @return
	 */
	public Object getConditionParamValue(FieldModel field){
		return getConditionParamInfo(field, false);
	}
	
	private Object getConditionParamInfo(FieldModel field,boolean isParam){
		if(null==field.getCondition()) return null;
		
		FieldConditionTypeEnum conditionType=field.getCondition().getConditionType();
		
		String result=null;
		
		Condition condition=getCondition(conditionType);
		
		if(null!=condition){
			if(isParam){
				return condition.getConditionParam(field.getCondition());
			}else{
				return condition.getConditionParamValue(field.getCondition());
			}
		}
		
		return result;
	}
	
	private Condition getCondition(FieldConditionTypeEnum conditionType){
		Condition condition=null;
		switch (conditionType) {
		case IN:
			condition=getCondition(InConditionImpl.class);
			break;
		default:
			condition=getCondition(ConditionDefaultImpl.class);
			break;
		}
		return condition;
	}
	
	private Condition getCondition(Class<?> clazz){
		if(null==conditionList) return null;

		for(Condition c:conditionList){
			if(c.getClass().getName().equals(clazz.getName())){
				return c;
			}
		}
		return null;
	}
}
