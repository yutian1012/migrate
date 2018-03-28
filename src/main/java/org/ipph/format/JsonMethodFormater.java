package org.ipph.format;

import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * 参数提供一个类似于Map的集合，map中只接受字符串类型的数据Map<String,String>
 * 如：{'1':'值1'，'2':'值2'}
 */
public class JsonMethodFormater implements Formater{
	
	@SuppressWarnings("unchecked")
	public Object format(String args,Object value){
		if(null==value) return null;
		
		String val= value.toString();
		if(null!=args&&!"".equals(args)){
			Map<String,Object> jsonMap = JSON.parseObject(args, Map.class);
			if(null!=jsonMap){
				return jsonMap.get(val);
			}
		}
		return null;
	}
}
