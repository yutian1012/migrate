package org.ipph.format;

import org.ipph.exception.FormatException;
import org.ipph.exception.PatentFormatException;
import org.ipph.util.PatentValidationUtil;

public class PctAuthNoFormater implements Formater{

	@Override
	public Object format(String args, Object value) throws FormatException {
		if(null==value||"".equals(value)) return null;
		
		String result=null;
		
		String pctNo="PCT/CN%1$s/%2$s";
		//历史数据的专利号：PCT/CN2014/089443,pct/cn2016/096304,PCTCN2015082600	
		String appNumber=(String) value;
		//去掉所有的字母和/以及空格，只保留数字
		appNumber=appNumber.replaceAll("[^\\d]", "");
		//截取数据成2部分，前一部分是4为的年，后一部分为流水号
		if(appNumber.length()==10){
			Object[] temp=new String[2];
			temp[0]=appNumber.substring(0,4);
			temp[1]=appNumber.substring(4);
			result=String.format(pctNo, temp);
		}
		
		if(!PatentValidationUtil.isValidPctNo(result)){
			throw new PatentFormatException("PCT专利号格式化异常："+value);
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		PctNoFormater pctNoFormater=new PctNoFormater();
		try {
			System.out.println(pctNoFormater.format(null, "PCT-CN2015-077044"));
		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
