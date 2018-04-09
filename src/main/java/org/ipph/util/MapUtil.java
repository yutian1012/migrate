package org.ipph.util;

import java.util.Map;

public class MapUtil {
	/**
	 * 输出map集合数据
	 * @param map
	 * @return
	 */
	public static String outMapData(Map<String,Object> map){
		StringBuilder sbuilder=new StringBuilder();
		sbuilder.append("data[");
		for(String key:map.keySet()){
			sbuilder.append(key).append("==>").append("'").append(map.get(key)).append("'");
		}
		
		sbuilder.append("]");
		return sbuilder.toString();
	}
}
