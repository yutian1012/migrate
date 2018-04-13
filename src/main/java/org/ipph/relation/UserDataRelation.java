package org.ipph.relation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class UserDataRelation {
	
	@Resource
	private JdbcTemplate fromJdbcTemplate;
	
	private Map<String,Long> userAccountMap=null;
	
	@PostConstruct
	public void init(){
		
		String sql="select id,loginname from twxuser;";
		
		userAccountMap=fromJdbcTemplate.query(sql, new ResultSetExtractor<Map<String,Long>>(){
			
			Map<String,Long> resultMap=new HashMap<String,Long>();
			@Override
			public Map<String, Long> extractData(ResultSet rs)throws SQLException, DataAccessException {
				while(rs.next()){
					if(null!=rs.getString("loginname")&&!"".equals(rs.getString("loginname").trim())){
						resultMap.put(rs.getString("loginname").trim(), rs.getLong("id"));
					}
				}
				return resultMap;
			}});
	}
	
	public Long getUserIdByAccount(String account){
		if(userAccountMap==null) return null;
		
		return userAccountMap.get(account);
	}
}
