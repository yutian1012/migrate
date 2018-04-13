package org.ipph.format;

import javax.annotation.Resource;

import org.ipph.exception.FormatException;
import org.ipph.relation.UserDataRelation;

public class UserDataFormater implements Formater{
	
	@Resource
	private UserDataRelation userDataRelation;

	@Override
	public Object format(String args, Object value) throws FormatException {
		if(null==value||"".equals(value)) return null;
		
		return userDataRelation.getUserIdByAccount((String)value);
	}
	
}
