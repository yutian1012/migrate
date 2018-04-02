package org.ipph.separator;

import java.util.ArrayList;
import java.util.List;

import org.ipph.exception.SeparatorException;
import org.springframework.stereotype.Component;
@Component
public class CharacterSeparator implements Separator{

	@Override
	public List<Object> separate(String args, Object value) throws SeparatorException {
		List<Object> result=new ArrayList<>();
		
		if(null!=args&&!"".equals(args.trim())){
			String temp=(String) value;
			String separators=args.trim();
			String[] arr=null;
			for(int i=0;i<separators.length();i++){
				String s=separators.substring(i,i+1);
				if(temp.indexOf(s)==-1)	continue;
				
				arr=temp.split(s);
				break;
			}
			if(arr!=null){
				for(String s:arr){
					result.add(s);
				}
			}
		}
		
		if(result.size()==0){
			result.add(value);
		}
		return result;
	}

}
