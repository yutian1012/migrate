package org.ipph.migration.core;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.ipph.exception.DataNotFoundException;
import org.ipph.exception.FormatException;
import org.ipph.migration.data.RowDataHandler;
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
	
	private Logger log=Logger.getLogger(MigrateExceptionHandler.class);
	/**
	 * 处理消息格式化异常
	 * @param e
	 * @param sql
	 * @param row
	 * @param table
	 */
	public void formatExceptionHandler(FormatException e,String sql,Map<String,Object> row,TableModel table){
		log.error("格式化数据错误"+e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		try {
			if(null!=sql){
				errorJdbcTemplate.update(sql,rowDataHandler.handle2ErrorRowData(row,table));
			}
		} catch (FormatException e1) {
			e1.printStackTrace();
		}
	}
	/**
	 * 处理消息格式化异常
	 * @param e
	 * @param sql
	 * @param row
	 * @param table
	 */
	public void dataNotFoundExceptionHandler(DataNotFoundException e,String sql,Map<String,Object> row,TableModel table){
		log.error(e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		try {
			if(null!=sql){
				errorJdbcTemplate.update(sql,rowDataHandler.handle2ErrorRowData(row,table));
			}
		} catch (FormatException e1) {
			e1.printStackTrace();
		}
	}
	/**
	 * 处理异常错误数据
	 * @param e
	 * @param sql
	 * @param row
	 * @param table
	 */
	public void sqlExceptionHandler(Exception e,String sql,Map<String,Object> row,TableModel table){
		log.error("语句执行错误"+e.getMessage());
		log.error("数据对象"+MapUtil.outMapData(row));
		try {
			if(null!=sql){
				errorJdbcTemplate.update(sql,rowDataHandler.handle2ErrorRowData(row,table));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
