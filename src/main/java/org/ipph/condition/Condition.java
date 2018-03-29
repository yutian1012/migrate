package org.ipph.condition;

import org.ipph.model.FieldConditionModel;

public interface Condition {

	public String getConditionParam(FieldConditionModel fieldConditionModel);
	
	public Object getConditionParamValue(FieldConditionModel fieldConditionModel);
}
