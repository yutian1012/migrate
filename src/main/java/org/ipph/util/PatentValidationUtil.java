package org.ipph.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class PatentValidationUtil {
	/**
	 * 判断是否是合法的申请号
	 * @param appNumber
	 * @return
	 */
	public static boolean isValidAppNumber(String appNumber){
		try{
			String[] zlNumber=getZlNumber(appNumber);
			if(null!=zlNumber&&StringUtils.isNotEmpty(zlNumber[0])){
				return validation(zlNumber[0],zlNumber[1]);
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	private static String[] getZlNumber(String appNumber){
		String[] zlNumber=new String[2];//第1个位置存放专利号，第二个wiz存放校验位
		if(StringUtils.isNotEmpty(appNumber)){
			appNumber=appNumber.trim();
			Pattern p=Pattern.compile("^([a-zA-Z]{2})?+\\d{8}(\\d{4})?+(\\.[\\d|X|x])?+");
			Matcher m=p.matcher(appNumber);
			if(m.matches()){
				p=Pattern.compile("^[a-zA-Z]{2}(\\d+?)(\\.[\\d|X|x])?");
				m=p.matcher(appNumber);
				if(m.matches()){
					zlNumber[0]=m.group(1);
					if(appNumber.indexOf(".")!=-1){
						zlNumber[1]=appNumber.substring(appNumber.lastIndexOf(".")+1);
					}
				}
				else if(appNumber.indexOf(".")!=-1){
					zlNumber[0]=appNumber.substring(0,appNumber.lastIndexOf("."));
					zlNumber[1]=appNumber.substring(appNumber.lastIndexOf(".")+1);
				}
				else{
					zlNumber[0]=appNumber;
				}
			}
		}
		return zlNumber;
	}
	/**
	 * 根据校验位校验专利是否正确
	 * @param zlNumber
	 * @param validation
	 * @return
	 */
	private static boolean validation(String zlNumber,String validation){
		Pattern p=Pattern.compile("^(85|86|87|88)(1|2|3|8|9)\\d+");
		Matcher m=p.matcher(zlNumber);
		if("".equals(validation)){
			if(m.matches())
				return true;
		}
		if(zlNumber.length()==8){
			//p=Pattern.compile("^(89|90|91|92|93|94|95|96|97|98|99|00|01|02|03)(1|2|3|8|9)\\d+");
			p=Pattern.compile("^(85|86|87|88|89|90|91|92|93|94|95|96|97|98|99|00|01|02|03)(1|2|3|8|9)\\d+");
			m=p.matcher(zlNumber);
			if(!m.matches())
				return false;
		}
		int[] numbers=new int[zlNumber.length()];
		
		int sum=0;
		for(int i=0, mul=2;i<numbers.length;i++,mul++){
			if(mul>9) mul=2;
			sum+=Integer.parseInt(zlNumber.substring(i,i+1))*mul;
		}
		int flag=sum%11;
		if(flag==10&&"x".equalsIgnoreCase(validation)){
			return true;
		}
		else{
			return (flag+"").equalsIgnoreCase(validation);
		}
	}
	
	public static void main(String[] args) {
		String appNumber="CN201620150382";
		System.out.println(PatentValidationUtil.isValidAppNumber(appNumber));
	}
}
