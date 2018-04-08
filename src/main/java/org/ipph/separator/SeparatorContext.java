package org.ipph.separator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.ipph.exception.SeparatorException;
import org.ipph.model.FieldSeparatorModel;
import org.springframework.stereotype.Component;

@Component
public class SeparatorContext {
	@Resource
	private CharacterSeparator separator;
	
	public List<Object> getSperateValue(FieldSeparatorModel fieldSeparatorModel,Object value ) throws SeparatorException {
		if(null==value){
			throw new SeparatorException("字段值为空");
		}
		
		if(null==fieldSeparatorModel) {
			List<Object> result=new ArrayList<>();
			result.add(value);
			return result;
		}
		
		return separator.separate(fieldSeparatorModel.getMethodArgs(), value);
	}
}
