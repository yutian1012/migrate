package org.ipph.condition;

import org.ipph.model.FieldConditionModel;

public class ConditionIsImpl implements Condition{

	@Override
	public String getConditionParam(FieldConditionModel fieldConditionModel) {
		if(null==fieldConditionModel||null==fieldConditionModel.getConditionType())
			return null;
		
		return fieldConditionModel.getConditionType().getName();
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
