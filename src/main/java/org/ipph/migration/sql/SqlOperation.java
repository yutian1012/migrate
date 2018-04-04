package org.ipph.migration.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.ipph.condition.ConditionContext;
import org.ipph.exception.FormatException;
import org.ipph.exception.SeparatorException;
import org.ipph.format.FormaterContext;
import org.ipph.model.FieldModel;
import org.ipph.model.FieldRestrictEnum;
import org.ipph.model.SubtableModel;
import org.ipph.model.TableModel;
import org.ipph.separator.SeparatorContext;
import org.ipph.thread.NamedThreadFactory;
import org.springframework.dao.DataAccessException;
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
	private JdbcTemplate errorJdbcTemplate;
	@Resource
	private FormaterContext formaterContext;
	@Resource
	private ConditionContext conditionContext;
	@Resource
	private SeparatorContext separatorContext;
	
	private int batch=50;
	
	private ExecutorService threadPool = Executors.newFixedThreadPool(10, new NamedThreadFactory("migration"));
	
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
		//迁移主表
		//migrateMasterTable(table);
		migrateMasterTableBatch(table);
		//迁移子表
		migrateSubTableList(table);
	}
	
	private void migrateMasterTableBatchThreadPool(TableModel table){
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		String uniqueSelect=sqlBuilder.getUniqueFieldSelect(table);
		
		if(log.isDebugEnabled()){
			log.debug("执行插入语句："+insert);
		}
		
		//获取记录数量
		String selectCount=sqlBuilder.getSelectCountSql(table);
		
		Map<String, Object> count=fromJdbcTemplate.queryForMap(selectCount,handleFieldCondition(table));
		
		long total=0;
		
		if(null!=count.get("num")&&(Long)count.get("num")>0L){//唯一键是否重复
			total=(Long)count.get("num");
		}
		
		List<Object[]> batchDataList=new ArrayList<>();
		List<Map<String,Object>> result=null;
		for(int index=0;index<total;index+=batch){
			String limitSql=null;
			if(index+batch>total){
				limitSql=select+" limit "+index+","+total;
			}else{
				limitSql=select+" limit "+index+","+batch;
			}
			System.out.println(limitSql);
			result=fromJdbcTemplate.queryForList(limitSql,handleFieldCondition(table));
			
			if(null!=result){
				
				for(Map<String,Object> row:result){
					try{
						if(null!=uniqueSelect){
							Map<String,Object> res=toJdbcTemplate.queryForMap(uniqueSelect,handleUniqueRowData(row,table));
							if(null!=res.get("num")&&(Long)res.get("num")>0L){//唯一键是否重复
								continue;
							}
						}
						batchDataList.add(handle2UpdRowData(row,table));
					}catch(FormatException e){
						e.printStackTrace();
						log.error("格式化数据错误"+e.getMessage());
					}
				}
				try{	
					if(batchDataList.size()>0){
						int[] ins=toJdbcTemplate.batchUpdate(insert,batchDataList );
						/*if(ins>0){
							if(log.isDebugEnabled()){
								log.debug("insert the data success!");
							}
						}*/
					}
				}catch (Exception e) {
					e.printStackTrace();
					log.error("语句执行错误"+e.getMessage());
				}
			}
			
			batchDataList.clear();
		}
		
		//List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select+" limit 0,100");
		
	}
	
	/**
	 * 迁移主表数据
	 * @param table
	 */
	private void migrateMasterTableBatch(TableModel table){
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		String uniqueSelect=sqlBuilder.getUniqueFieldSelect(table);
		
		if(log.isDebugEnabled()){
			log.debug("执行插入语句："+insert);
		}
		
		//获取记录数量
		String selectCount=sqlBuilder.getSelectCountSql(table);
		
		Map<String, Object> count=fromJdbcTemplate.queryForMap(selectCount,handleFieldCondition(table));
		
		long total=0;
		
		if(null!=count.get("num")&&(Long)count.get("num")>0L){//唯一键是否重复
			total=(Long)count.get("num");
		}
		
		//批次处理
		
		List<Object[]> batchDataList=new ArrayList<>();
		List<Map<String,Object>> result=null;
		for(int index=0;index<total;index+=batch){
			String limitSql=null;
			if(index+batch>total){
				limitSql=select+" limit "+index+","+total;
			}else{
				limitSql=select+" limit "+index+","+batch;
			}
			System.out.println(limitSql);
			result=fromJdbcTemplate.queryForList(limitSql,handleFieldCondition(table));
			
			if(null!=result){
				
				for(Map<String,Object> row:result){
					try{
						if(null!=uniqueSelect){
							Map<String,Object> res=toJdbcTemplate.queryForMap(uniqueSelect,handleUniqueRowData(row,table));
							if(null!=res.get("num")&&(Long)res.get("num")>0L){//唯一键是否重复
								continue;
							}
						}
						batchDataList.add(handle2UpdRowData(row,table));
					}catch(FormatException e){
						e.printStackTrace();
						log.error("格式化数据错误"+e.getMessage());
					}
				}
				try{	
					if(batchDataList.size()>0){
						int[] ins=toJdbcTemplate.batchUpdate(insert,batchDataList );
						/*if(ins>0){
							if(log.isDebugEnabled()){
								log.debug("insert the data success!");
							}
						}*/
					}
				}catch (Exception e) {
					e.printStackTrace();
					log.error("语句执行错误"+e.getMessage());
				}
			}
			
			batchDataList.clear();
		}
		
		//List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select+" limit 0,100");
		
	}
	
	/**
	 * 迁移主表数据
	 * @param table
	 */
	private void migrateMasterTable(TableModel table){
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		String uniqueSelect=sqlBuilder.getUniqueFieldSelect(table);
		
		if(log.isDebugEnabled()){
			log.debug("执行插入语句："+insert);
		}
		
		String errorInsert=sqlBuilder.getErrorInsertSql(table);
		
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
					int ins=toJdbcTemplate.update(insert, handle2UpdRowData(row,table));
					if(ins>0){
						if(log.isDebugEnabled()){
							log.debug("insert the data success!");
						}
					}
				}catch(FormatException e){
					e.printStackTrace();
					log.error("格式化数据错误"+e.getMessage());
					try {
						errorJdbcTemplate.update(errorInsert,handle2ErrorRowData(row,table));
					} catch (Exception e1) {
						
					}
				}catch (Exception e) {
					e.printStackTrace();
					log.error("语句执行错误"+e.getMessage());
					try {
						errorJdbcTemplate.update(errorInsert,handle2ErrorRowData(row,table));
					} catch (Exception e1) {
						
					}
				}
			}
		}
	}
	/**
	 * 处理子表集合
	 * @param table
	 */
	private void migrateSubTableList(TableModel table){
		List<SubtableModel> subTablelist=table.getSubTableList();
		if(null!=subTablelist&&subTablelist.size()>0){
			for(SubtableModel subTable:subTablelist){
				if(null==subTable||subTable.isSkip()==true) continue;
				
				migrateSubTable(subTable);
			}
		}
	}
	
	/**
	 * 数据库迁移操作--处理子表
	 * @param fromTable
	 * @param targetTable
	 */
	private void migrateSubTable(SubtableModel table){
		
		if(null==table|| table.isSkip()) return;
		
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		if(log.isDebugEnabled()){
			log.debug("执行插入语句："+insert);
		}
		
		//List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select+" limit 0,100");
		List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select,handleFieldCondition(table));
		
		if(null!=result){
			for(Map<String,Object> row:result){
				try{
					//int ins=toJdbcTemplate.update(insert, handle2UpdRowData(row,table));
					int ins=migrateSubTableRow(insert, row, table);
					if(ins>0){
						if(log.isDebugEnabled()){
							log.debug("insert the data success!");
						}
					}
				}catch(FormatException e){
					e.printStackTrace();
					log.error("格式化数据错误"+e.getMessage());
				}catch(SeparatorException e){
					e.printStackTrace();
					log.error("数据字段拆分错误"+e.getMessage());
				}catch(Exception e){
					e.printStackTrace();
					log.error("语句执行错误"+e.getMessage());
				}
			}
		}
	}
	/**
	 * 处理将拆分的每一行数据
	 * @param insert
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException 
	 * @throws DataAccessException 
	 * @throws SeparatorException 
	 */
	private int migrateSubTableRow(String insert,Map<String,Object> row,SubtableModel table) throws DataAccessException, FormatException, SeparatorException{
		List<FieldModel> separatorFieldList=new ArrayList<>();
		for(FieldModel field:table.getFiledList()){
			if(null!=field.getFieldSeparatorModel()){
				separatorFieldList.add(field);
			}
		}
		
		if(separatorFieldList.size()==0){
			return toJdbcTemplate.update(insert, handle2UpdRowData(row,table));
		}
		Map<String,List<Object>> separatorDataMap=new HashMap<>();
		for(FieldModel field:separatorFieldList){
			separatorDataMap.put(field.getFrom(), separatorContext.getSperateValue(field.getFieldSeparatorModel(),row.get(field.getFrom())));
		}
		
		boolean flag=true;
		int size=0;
		for(FieldModel field:separatorFieldList){
			if(size==0){
				size=separatorDataMap.get(field.getFrom()).size();
			}else if(size!=separatorDataMap.get(field.getFrom()).size()){//判断所有集合长度是否相同，如果不同，则证明字段拆分的有问题，数量不匹配
				flag=false;
				break;
			}
		}
		
		if(!flag){
			throw new SeparatorException("字段拆分数量不匹配：");
		}
		
		List<Map<String,Object>> dataList=new ArrayList<>();
		for(int i=0;i<size;i++){
			Map<String,Object> data=new HashMap<>();
			for(FieldModel field:table.getFiledList()){
				Object value=null;
				if(null!=field.getFieldSeparatorModel()){
					value=separatorDataMap.get(field.getFrom()).get(i);
				}else{
					value=row.get(field.getFrom());
				}
				
				data.put(field.getFrom(), value);
			}
			dataList.add(data);
		}
		
		//执行sql语句
		int num=0;
		for(Map<String,Object> data:dataList){
			num+=toJdbcTemplate.update(insert, handle2UpdRowData(data,table));
		}
		
		return num;
		
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
						Map<String,Object> res=toJdbcTemplate.queryForMap(toUpdSelect,handle2UpdWhereRowData(row,table));
						if(null!=res.get("num")&&(Long)res.get("num")==0L){//唯一键是否重复
							log.error("数据记录不存在："+row);
							continue;
						}
					}
					//Object[] o=handle2UpdRowData(row,table);
					int upd=toJdbcTemplate.update(update, handle2UpdRowData(row,table));
					if(upd>0){
						if(log.isDebugEnabled()){
							log.debug("update the data success!");
						}
					}else{
						log.error("not found the data!");
					}
					
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
	/*private Object[] handleRowData(Map<String,Object> row,TableModel table) throws FormatException{
		
		List<Object> result=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(null==field.getTo()||"".equals(field.getTo())){
				continue;
			}
			
			result.add(processFieldValue(row, field));
		}
		
		return result.toArray();
		
	}*/
	
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
	private Object[] handle2UpdWhereRowData(Map<String,Object> row,TableModel table)throws FormatException{
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
	 * 获取待更新字段的参数值
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException
	 */
	private Object[] handle2ErrorRowData(Map<String,Object> row,TableModel table)throws FormatException{
		List<Object> result=new ArrayList<>();
		
		for(FieldModel field:table.getFiledList()){
			if(null==field.getFrom()||"".equals(field.getFrom())){
				continue;
			}
			result.add(row.get(field.getFrom()));
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
		if(null==value||"".equals(value)){
			if(null!=field.getDefaultValue()&&!"".equals(field.getDefaultValue())){
				value=field.getDefaultValue().trim();
			}
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
