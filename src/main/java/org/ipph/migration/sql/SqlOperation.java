package org.ipph.migration.sql;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ipph.format.FormaterContext;
import org.ipph.model.FieldModel;
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
		
		List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select+" limit 0,100");
		
		if(null!=result){
			for(Map<String,Object> row:result){
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
			result[i]=formaterContext.getFormatedValue(field.getFormat(),row.get(field.getFrom()));
		}
		
		return result;
	}
}
