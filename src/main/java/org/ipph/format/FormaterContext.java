package org.ipph.format;

import java.util.List;

import org.ipph.model.FieldFormatModel;
import org.springframework.stereotype.Component;

@Component
public class FormaterContext {
	
	private List<Formater> formaterList;
	
	/**
	 * 格式化数据
	 * @param fieldFormatModel
	 * @param value
	 * @return
	 */
	public Object getFormatedValue(FieldFormatModel fieldFormatModel,Object value ) {
		if(null==fieldFormatModel){
			return value;
		}
		
		Formater formater=getFormater(fieldFormatModel.getClassName());
		if(null!=formater){
			formater.format(fieldFormatModel.getMethodArgs(), value);
		}
		
		return value;
	}
	/**
	 * 获取格式化处理类
	 * @param formater
	 * @return
	 */
	private Formater getFormater(String formater){
		if(null==formaterList) return null;

		if(null==formater||"".equals(formater)) return null;
		
		for(Formater f:formaterList){
			if(f.getClass().toString().equals(formater)){
				return f;
			}
		}
		return null;
	}
	
}
