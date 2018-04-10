package org.ipph.migration.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.ipph.exception.DataExistsException;
import org.ipph.exception.DataNotFoundException;
import org.ipph.exception.FormatException;
import org.ipph.exception.SeparatorException;
import org.ipph.migration.data.RowDataHandler;
import org.ipph.migration.sql.SqlBuilder;
import org.ipph.model.TableModel;
import org.ipph.util.MapUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MigrateExceptionHandler {
	@Resource
	private JdbcTemplate errorJdbcTemplate;
	@Resource
	private RowDataHandler rowDataHandler;
	@Resource
	private SqlBuilder sqlBuilder;
	
	private ConcurrentMap<String, Set<Long>> concurrentMap=new ConcurrentHashMap<>();
	
	private Logger log=Logger.getLogger(MigrateExceptionHandler.class);
	/**
	 * 处理消息格式化异常
	 * @param e
	 * @param sql
	 * @param row
	 * @param table
	 */
	public void formatExceptionHandler(FormatException e,Map<String,Object> row,TableModel table,boolean isUpdate){
		log.error("格式化数据错误"+e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		insertErrorInof(row, table, isUpdate);
	}
	/**
	 * 数据对象拆分异常
	 * @param e
	 * @param row
	 * @param table
	 */
	public void separatorExceptionHandler(SeparatorException e,Map<String,Object> row,TableModel table){
		log.error("数据字段拆分错误"+e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		insertErrorInof(row, table, false);
		/*String sql=sqlBuilder.getErrorInsertSql(table);
		try {
			if(null!=sql){
				errorJdbcTemplate.update(sql,rowDataHandler.handle2ErrorRowData(row,table));
			}
		} catch (FormatException e1) {
			e1.printStackTrace();
		}*/
	}
	
	/**
	 * 更新数据时未找到记录
	 * @param e
	 * @param sql
	 * @param row
	 * @param table
	 */
	public void dataNotFoundExceptionHandler(DataNotFoundException e,Map<String,Object> row,TableModel table){
		log.error(e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		insertErrorInof(row, table, false);
	}
	/**
	 * 处理异常错误数据
	 * @param e
	 * @param sql
	 * @param row
	 * @param table
	 */
	public void sqlExceptionHandler(Exception e,Map<String,Object> row,TableModel table,boolean isUpdate){
		log.error("语句执行错误"+e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		insertErrorInof(row, table, isUpdate);
		/*if(isUpdate){
			return;
		}
		String sql=sqlBuilder.getErrorInsertSql(table);
		try {
			if(null!=sql){
				errorJdbcTemplate.update(sql,rowDataHandler.handle2ErrorRowData(row,table));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}*/
	}
	/**
	 * 处理错误数据
	 * @param e
	 * @param row
	 * @param table
	 */
	public void dataExistsExceptionHandler(DataExistsException e,Map<String, Object> row, TableModel table) {
		log.error(e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		
		insertErrorInof(row, table, false);
		
		/*String sql=sqlBuilder.getErrorInsertSql(table);
		try {
			if(null!=sql){
				errorJdbcTemplate.update(sql,rowDataHandler.handle2ErrorRowData(row,table));
			}
		} catch (FormatException e1) {
			e1.printStackTrace();
		}*/
	}
	
	private void insertErrorInof(Map<String,Object> row,TableModel table,boolean isUpdate){
		if(isUpdate){
			return;
		}
		if(row.containsKey("id")&&null!=row.get("id")){
			if(put(table.getFrom(),(Long) row.get("id"))){
				String sql=sqlBuilder.getErrorInsertSql(table);
				try {
					if(null!=sql){
						errorJdbcTemplate.update(sql,rowDataHandler.handle2ErrorRowData(row,table));
					}
				} catch (FormatException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private synchronized boolean put(String tableName,Long value){
		if(!concurrentMap.containsKey(tableName)){
			concurrentMap.put(tableName, new HashSet<Long>());
		}
		return concurrentMap.get(tableName).add(value);
	}
}
