package org.ipph.migration.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.ipph.format.FormaterContext;
import org.ipph.model.FieldModel;
import org.ipph.model.FieldRestrictEnum;
import org.ipph.model.TableModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SqlOperation {
	@Resource
	private SqlBuilder sqlBuilder;
	@Resource
	private JdbcTemplate fromJdbcTemplate;
	@Resource
	private JdbcTemplate toJdbcTemplate;
	@Resource
	private FormaterContext formaterContext;
	
	private int batch=100;
	
	private Logger log=Logger.getLogger(SqlOperation.class);
	
	public void transferTable(TableModel tableModel){
		switch (tableModel.getType()) {
		case MIGRATE:
			migrateTable(tableModel);
			break;
		case UPDATE:

		default:
			break;
		}
	}
	/**
	 * 数据库迁移操作
	 * @param fromTable
	 * @param targetTable
	 */
	public void migrateTable(TableModel table){
		if(null==table) return;
		
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		String uniqueSelect=sqlBuilder.getUniqueFieldSelect(table);
		
		if(log.isDebugEnabled()){
			log.debug(select);
		}
		
		//List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select+" limit 0,100");
		List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select);
		
		if(null!=result){
			for(Map<String,Object> row:result){
				
				if(null!=uniqueSelect){
					Map<String,Object> res=toJdbcTemplate.queryForMap(uniqueSelect,handleUniqueRowData(row,table));
					if(null!=res.get("num")&&(Long)res.get("num")>0L){//唯一键是否重复
						continue;
					}
				}
				
				toJdbcTemplate.update(insert, handleRowData(row,table));
			}
		}
	}
	/**
	 * 处理行数据并返回数值数组
	 * @param row
	 * @param table
	 * @return
	 */
	private Object[] handleRowData(Map<String,Object> row,TableModel table){
		Object[] result=new Object[table.getFiledList().size()];
		
		for(int i=0;i<table.getFiledList().size();i++){
			FieldModel field=table.getFiledList().get(i);
			result[i]=processFieldValue(row, field);
		}
		
		return result;
	}
	
	/**
	 * 处理唯一键行数据并返回数值数组
	 * @param row
	 * @param table
	 * @return
	 */
	private Object[] handleUniqueRowData(Map<String,Object> row,TableModel table){
		List<Object> result=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(field.getRestrict()!=FieldRestrictEnum.UNIQUE) continue;
			
			result.add(processFieldValue(row, field));
		}
		
		return result.toArray();
	}
	/**
	 * 处理字段值
	 * @param row
	 * @param field
	 * @return
	 */
	private Object processFieldValue(Map<String,Object> row,FieldModel field){
		Object value=null;
		if(null!=field.getFrom()&&!"".equals(field.getFrom())){
			value=formaterContext.getFormatedValue(field.getFormat(),row.get(field.getFrom()));
			
		}
		if(null==value&&null!=field.getDefaultValue()&&!"".equals(field.getDefaultValue())){
			value=field.getDefaultValue();
		}
		return value;
	}
	
}
