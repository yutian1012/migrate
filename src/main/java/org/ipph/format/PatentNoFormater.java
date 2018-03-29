package org.ipph.format;

import org.ipph.exception.FormatException;
import org.ipph.exception.PatentFormatException;
import org.ipph.util.PatentValidationUtil;

public class PatentNoFormater implements Formater{

	@Override
	public Object format(String args, Object value) throws FormatException {
		
		if(null==value||"".equals(value)) return null;
		
		//历史数据的专利号：201029092038.0,ZL 2008 2 0009767.0,2010101463727,ZL200820132792.8,无
		String appNumber=(String) value;
		//转大写
		appNumber=appNumber.trim().toUpperCase();
		//去掉空白
		appNumber=appNumber.replaceAll("[\\s\\t]+", "");
		//添加CN标记
		if(!appNumber.startsWith("CN")){
			appNumber=appNumber.replaceFirst("^[A-Z]{2}", "");
			appNumber="CN"+appNumber;
		}
		//校验位的小数点省略的数据，专利的校验数据包括13位和9位，其中包括校验位
		if(appNumber.indexOf(".")==-1){
			if(appNumber.length()==15||appNumber.length()==11){
				appNumber=appNumber.substring(0,appNumber.length()-1)+"."+appNumber.substring(appNumber.length()-1);
			}
		}
		
		if(!PatentValidationUtil.isValidAppNumber(appNumber)){
			throw new PatentFormatException("专利号错误，无法进行格式化："+value);
		}
		
		return appNumber;
	}
	
}
