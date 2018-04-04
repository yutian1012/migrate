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
	 * 针对in的情况还需要解析设置的参数，从而确定出参数的个数，可能会有多个逗号作为占位符
	 * @param field
	 * @return
	 */
	public String getConditionParam(FieldModel field){
		return (String) getConditionParamInfo(field,true);
	}
	/**
	 * 获取条件的数据，替换sql中的占位符
	 * @param field
	 * @return
	 */
	public Object getConditionParamValue(FieldModel field){
		return getConditionParamInfo(field, false);
	}
	/**
	 * 获取参数信息，
	 * @param field
	 * @param isParam true表示参数值，false表示参数占位符
	 * @return
	 */
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
	/**
	 * 获取condition的处理类
	 * 有新的处理类需要在此进行配置
	 * @param conditionType
	 * @return
	 */
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
	/**
	 * 获取处理类实例
	 * @param clazz
	 * @return
	 */
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
