package org.ipph.condition;

import org.ipph.model.FieldConditionModel;

public class ConditionDefaultImpl implements Condition{

	@Override
	public String getConditionParam(FieldConditionModel fieldConditionModel) {
		if(null==fieldConditionModel||null==fieldConditionModel.getConditionType())
			return null;
		
		return fieldConditionModel.getConditionType().getName()+" ? ";
	}

	@Override
	public Object getConditionParamValue(FieldConditionModel fieldConditionModel) {
		if(null==fieldConditionModel||null==fieldConditionModel.getConditionType())
			return null;
		
		return fieldConditionModel.getValue();
		/*String value=fieldConditionModel.getValue();
		if("null".equals(value)||"NULL".equals(value)){
			return null;
		}else{
			return value;
		}*/
		
	}

}
