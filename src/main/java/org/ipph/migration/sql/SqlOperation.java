package org.ipph.migration.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.ipph.condition.ConditionContext;
import org.ipph.exception.FormatException;
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
	@Resource
	private ConditionContext conditionContext;
	
	private int batch=100;
	
	private Logger log=Logger.getLogger(SqlOperation.class);
	
	public void transferTable(TableModel tableModel){
		switch (tableModel.getType()) {
		case MIGRATE:
			migrateTable(tableModel);
			break;
		case UPDATE:
			updateTable(tableModel);
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
		if(null==table|| table.isSkip()) return;
		
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		String uniqueSelect=sqlBuilder.getUniqueFieldSelect(table);
		
		if(log.isDebugEnabled()){
			log.debug("执行插入语句："+insert);
		}
		
		//List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select+" limit 0,100");
		List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select,handleFieldCondition(table));
		
		if(null!=result){
			for(Map<String,Object> row:result){
				try{
					if(null!=uniqueSelect){
						Map<String,Object> res=toJdbcTemplate.queryForMap(uniqueSelect,handleUniqueRowData(row,table));
						if(null!=res.get("num")&&(Long)res.get("num")>0L){//唯一键是否重复
							continue;
						}
					}
					toJdbcTemplate.update(insert, handleRowData(row,table));
				}catch(FormatException e){
					e.printStackTrace();
					log.error("格式化数据错误"+e.getCause().getMessage());
				}
			}
		}
	}
	/**
	 * 更新目标表数据
	 * @param table
	 */
	public void updateTable(TableModel table){
		if(null==table|| table.isSkip()) return;
		
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String update=sqlBuilder.getUpdateSql(table);
		
		//update语句必须要有条件，否则直接返回
		if(null==update||update.indexOf("?")==-1) return;
		
		String toUpdSelect=sqlBuilder.get2UpdFieldSelect(table);
		
		if(log.isDebugEnabled()){
			log.debug("执行update语句："+update);
		}
		
		List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select,handleFieldCondition(table));
		
		if(result!=null){
			for(Map<String,Object> row:result){
				try{
					if(null!=toUpdSelect){
						Object[] o=handle2UpdWereRowData(row,table);
						Map<String,Object> res=toJdbcTemplate.queryForMap(toUpdSelect,handle2UpdWereRowData(row,table));
						if(null!=res.get("num")&&(Long)res.get("num")==0L){//唯一键是否重复
							log.error("数据记录不存在："+row);
							continue;
						}
					}
					Object[] o=handle2UpdRowData(row,table);
					toJdbcTemplate.update(update, handle2UpdRowData(row,table));
				}catch(FormatException e){
					e.printStackTrace();
					log.error("格式化数据错误"+e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 处理行数据并返回数值数组
	 * @param row
	 * @param table
	 * @return
	 * @throws Exception 
	 */
	private Object[] handleRowData(Map<String,Object> row,TableModel table) throws FormatException{
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
	 * @throws Exception 
	 */
	private Object[] handleUniqueRowData(Map<String,Object> row,TableModel table) throws FormatException{
		List<Object> result=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(field.getRestrict()!=FieldRestrictEnum.UNIQUE) continue;
			
			result.add(processFieldValue(row, field));
		}
		
		return result.toArray();
	}
	/**
	 * 获取待更新字段的参数值
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException
	 */
	private Object[] handle2UpdWereRowData(Map<String,Object> row,TableModel table)throws FormatException{
		List<Object> result=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			if(null==field.getFrom()||"".equals(field.getFrom())){
				continue;
			}
			
			result.add(processFieldValue(row, field));
		}
		
		return result.toArray();
	}
	/**
	 * 获取待更新字段的参数值
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException
	 */
	private Object[] handle2UpdRowData(Map<String,Object> row,TableModel table)throws FormatException{
		List<Object> result=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			result.add(processFieldValue(row, field));
		}
		
		return result.toArray();
	}
	/**
	 * 处理字段值
	 * @param row
	 * @param field
	 * @return
	 * @throws Exception 
	 */
	private Object processFieldValue(Map<String,Object> row,FieldModel field) throws FormatException{
		Object value=null;
		if(null!=field.getFrom()&&!"".equals(field.getFrom())){
			value=formaterContext.getFormatedValue(field.getFormat(),row.get(field.getFrom()));
		}
		//处理默认值
		if(null==value&&null!=field.getDefaultValue()&&!"".equals(field.getDefaultValue())){
			value=field.getDefaultValue().trim();
		}
		return value;
	}
	/**
	 * 处理查询条件值
	 * @param table
	 * @return
	 */
	private Object[] handleFieldCondition(TableModel table){
		List<Object> result=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())) continue;
			
			if(null!=field.getCondition()&&null!=field.getCondition().getValue()){
				Object obj=conditionContext.getConditionParamValue(field);
				if(null!=obj){
					if(obj instanceof Object[]){
						for(Object o:(Object[])obj){
							result.add(o);
						}
					}
				}
				result.add(obj);
			}
		}
		
		return result.toArray();
	}
}
