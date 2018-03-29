package org.ipph.format;

import java.util.List;

import org.ipph.exception.FormatException;
import org.ipph.model.FieldFormatModel;


public class FormaterContext {
	
	private List<Formater> formaterList;
	
	public List<Formater> getFormaterList() {
		return formaterList;
	}
	public void setFormaterList(List<Formater> formaterList) {
		this.formaterList = formaterList;
	}
	/**
	 * 格式化数据
	 * @param fieldFormatModel
	 * @param value
	 * @return
	 * @throws Exception 
	 */
	public Object getFormatedValue(FieldFormatModel fieldFormatModel,Object value ) throws FormatException {
		if(null==fieldFormatModel){
			return value;
		}
		
		Formater formater=getFormater(fieldFormatModel.getClassName());
		if(null!=formater){
			value=formater.format(fieldFormatModel.getMethodArgs(), value);
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
			if(f.getClass().getName().equals(formater)){
				return f;
			}
		}
		return null;
	}
	
}
