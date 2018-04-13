package org.ipph.condition;

import org.ipph.model.FieldConditionModel;

public class ConditionNotInImpl implements Condition{

	@Override
	public String getConditionParam(FieldConditionModel fieldConditionModel){
		if(null==fieldConditionModel||null==fieldConditionModel.getConditionType())
			return null;
		
		String value=fieldConditionModel.getValue();
		
		if(null==value||"".equals(value)){
			//throw new ConditionException("in 条件参数设置问题");
			return null;
		}
		
		String[] values=value.split(",");
		
		if(null!=values&&values.length>0){
			
			StringBuilder condition=new StringBuilder();
			
			condition.append(fieldConditionModel.getConditionType().getName()).append(" (");
			
			for(String v:values){
				condition.append("'").append(v).append("'").append(",");
			}
			
			condition.setLength(condition.length()-1);
			
			condition.append(")");
			
			return condition.toString();
		}
		
		return null;
		
	}

	@Override
	public Object getConditionParamValue(FieldConditionModel fieldConditionModel) {
		return null;
	}

	@Override
	public boolean isValueSkip() {
		return true;
	}

}
