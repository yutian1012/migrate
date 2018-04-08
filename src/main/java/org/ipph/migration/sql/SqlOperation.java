package org.ipph.migration.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.ipph.exception.DataNotFoundException;
import org.ipph.exception.FormatException;
import org.ipph.exception.SeparatorException;
import org.ipph.migration.core.MigrateExceptionHandler;
import org.ipph.migration.data.RowDataHandler;
import org.ipph.model.FieldModel;
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
	private SeparatorContext separatorContext;
	@Resource
	private RowDataHandler rowDataHandler;
	@Resource
	private MigrateExceptionHandler migrateExceptionHandler;
	
	//private int batch=50;
	
	private ExecutorService threadPool = Executors.newFixedThreadPool(10, new NamedThreadFactory("migration"));
	
	private Logger log=Logger.getLogger(SqlOperation.class);
	
	/**
	 * 迁移主表数据
	 * @param table
	 */
	public void migrateMasterTable(TableModel table){
		migrateTable(table, false);
	}
	
	/**
	 * 处理子表集合
	 * @param table
	 */
	public void migrateSubTable(TableModel table){
		List<SubtableModel> subTablelist=table.getSubTableList();
		if(null!=subTablelist&&subTablelist.size()>0){
			for(SubtableModel subTable:subTablelist){
				if(null==subTable||subTable.isSkip()==true) continue;
				migrateTable(subTable,true);
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
		
		String toUpdSelect=sqlBuilder.isExistSelectSql(table);
		
		if(log.isDebugEnabled()){
			log.debug("执行update语句："+update);
		}
		
		List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select,rowDataHandler.handleFieldCondition(table));
		
		if(result!=null){
			for(Map<String,Object> row:result){
				try{
					if(null!=toUpdSelect){
						Map<String,Object> res=toJdbcTemplate.queryForMap(toUpdSelect,rowDataHandler.handle2UpdRowData(row,table));
						if(null!=res.get("num")&&(Long)res.get("num")==0L){
							throw new DataNotFoundException("未找到更新记录");
						}
					}
					int upd=toJdbcTemplate.update(update, rowDataHandler.handle2MigrateRowData(row,table));
					if(upd>0){
						if(log.isDebugEnabled()){
							log.debug("update the data success!");
						}
					}
				}catch(FormatException e){
					migrateExceptionHandler.formatExceptionHandler(e, null, row, table);
				}catch(DataNotFoundException e){
					migrateExceptionHandler.dataNotFoundExceptionHandler(e,null, row, table);
				}catch(Exception e){
					migrateExceptionHandler.sqlExceptionHandler(e, null, row, table);
				}
			}
		}
	}
	/**
	 * 迁移操作
	 * @param table
	 * @param batch
	 * @param isSubTable
	 */
	private void migrateTable(TableModel table,boolean isSubTable){
		if(null==table|| table.isSkip()) return;
		
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		//判断唯一键是否重复
		String isExistsSql=sqlBuilder.isUniqueFieldExistsSelectSql(table);
		
		String errorInsert=sqlBuilder.getErrorInsertSql(table);
		
		long total=getTotal(table);
		
		for(int index=0;index<total;index++){
			String limitSql=select+" limit "+index+",1";
			
			Map<String,Object> row=fromJdbcTemplate.queryForMap(limitSql,rowDataHandler.handleFieldCondition(table));
			
			try{
				//判断唯一键或主键是否已经存在
				if(isExists(isExistsSql, row, table)){
					continue;
				}
				
				if(log.isDebugEnabled()){
					log.debug("执行插入语句："+insert);
				}
				int ins=0;
				if(isSubTable){
					ins=migrateSubTableRow(insert, row, table);
				}else{
					ins=toJdbcTemplate.update(insert, rowDataHandler.handle2MigrateRowData(row,table));
				}
				if(ins>0){
					if(log.isDebugEnabled()){
						log.debug("insert the data success!");
					}
				}
			}catch(FormatException e){
				migrateExceptionHandler.formatExceptionHandler(e, errorInsert, row, table);
			}catch (Exception e) {
				migrateExceptionHandler.sqlExceptionHandler(e, errorInsert, row, table);
			}
		}
	}
	
	/**
	 * 批次迁移
	 * @param table
	 * @param batch
	 * @param isSubTable
	 */
	public void batchMigrateTable(TableModel table,int batch,boolean isSubTable){
		if(null==table|| table.isSkip()) return;
		
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		String errorInsert=sqlBuilder.getErrorInsertSql(table);
		
		long total=getTotal(table);
		
		List<Object[]> batchDataList=new ArrayList<>();
		List<Map<String,Object>> result=null;
		
		for(int index=0;index<total;index+=batch){
			String limitSql=select+" limit "+index+","+batch;
			
			result=fromJdbcTemplate.queryForList(limitSql,rowDataHandler.handleFieldCondition(table));
			
			if(null!=result){
				
				for(Map<String,Object> row:result){
					try{
						
						batchDataList.add(rowDataHandler.handle2MigrateRowData(row,table));
					}catch(FormatException e){
						migrateExceptionHandler.formatExceptionHandler(e, errorInsert, row, table);
					}
				}
				try{
					if(batchDataList.size()>0){
						int[] ins=toJdbcTemplate.batchUpdate(insert,batchDataList );
					}
				}catch (Exception e) {
					e.printStackTrace();
					//migrateExceptionHandler.sqlExceptionHandler(e, errorInsert, row, table);
				}
			}
			
			batchDataList.clear();
		}
		
	}
	
	/**
	 * 获取待迁移的记录数据量
	 * @param table
	 * @return
	 */
	private long getTotal(TableModel table){
		//获取记录数量
		String selectCount=sqlBuilder.getSelectCountSql(table);
		Map<String, Object> count=fromJdbcTemplate.queryForMap(selectCount,rowDataHandler.handleFieldCondition(table));
		
		long total=0;
		
		if(null!=count.get("num")&&(Long)count.get("num")>0L){//唯一键是否重复
			total=(Long)count.get("num");
		}
	
		return total;
	}
	
	/**
	 * 判断是否已经存在
	 * @param sql
	 * @param row
	 * @param table
	 * @return
	 * @throws FormatException 
	 * @throws DataAccessException 
	 */
	private boolean isExists(String sql,Map<String,Object> row,TableModel table) throws DataAccessException, FormatException{
		if(null!=sql){
			Map<String,Object> res=toJdbcTemplate.queryForMap(sql,rowDataHandler.handleUniqueRowData(row,table));
			if(null!=res.get("num")&&(Long)res.get("num")>0L){//唯一键是否重复
				return true;
			}
		}
		return false;
	}
	
	
	/*private void migrateMasterTableBatchThreadPool(TableModel table){
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		String uniqueSelect=sqlBuilder.isUniqueFieldExistsSelectSql(table);
		
		if(log.isDebugEnabled()){
			log.debug("执行插入语句："+insert);
		}
		
		//获取记录数量
		String selectCount=sqlBuilder.getSelectCountSql(table);
		
		Map<String, Object> count=fromJdbcTemplate.queryForMap(selectCount,rowDataHandler.handleFieldCondition(table));
		
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
			result=fromJdbcTemplate.queryForList(limitSql,rowDataHandler.handleFieldCondition(table));
			
			if(null!=result){
				
				for(Map<String,Object> row:result){
					try{
						if(null!=uniqueSelect){
							Map<String,Object> res=toJdbcTemplate.queryForMap(uniqueSelect,rowDataHandler.handleUniqueRowData(row,table));
							if(null!=res.get("num")&&(Long)res.get("num")>0L){//唯一键是否重复
								continue;
							}
						}
						batchDataList.add(rowDataHandler.handle2MigrateRowData(row,table));
					}catch(FormatException e){
						e.printStackTrace();
						log.error("格式化数据错误"+e.getMessage());
					}
				}
				try{	
					if(batchDataList.size()>0){
						int[] ins=toJdbcTemplate.batchUpdate(insert,batchDataList );
						if(ins>0){
							if(log.isDebugEnabled()){
								log.debug("insert the data success!");
							}
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
					log.error("语句执行错误"+e.getMessage());
				}
			}
			
			batchDataList.clear();
		}
		
		//List<Map<String,Object>> result=fromJdbcTemplate.queryForList(select+" limit 0,100");
		
	}*/
	
	/**
	 * 迁移主表数据
	 * @param table
	 */
	/*private void migrateMasterTableBatch(TableModel table){
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		String uniqueSelect=sqlBuilder.isUniqueFieldExistsSelectSql(table);
		
		if(log.isDebugEnabled()){
			log.debug("执行插入语句："+insert);
		}
		
		//获取记录数量
		String selectCount=sqlBuilder.getSelectCountSql(table);
		
		Map<String, Object> count=fromJdbcTemplate.queryForMap(selectCount,rowDataHandler.handleFieldCondition(table));
		
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
			result=fromJdbcTemplate.queryForList(limitSql,rowDataHandler.handleFieldCondition(table));
			
			if(null!=result){
				
				for(Map<String,Object> row:result){
					try{
						if(null!=uniqueSelect){
							Map<String,Object> res=toJdbcTemplate.queryForMap(uniqueSelect,rowDataHandler.handleUniqueRowData(row,table));
							if(null!=res.get("num")&&(Long)res.get("num")>0L){//唯一键是否重复
								continue;
							}
						}
						batchDataList.add(rowDataHandler.handle2MigrateRowData(row,table));
					}catch(FormatException e){
						e.printStackTrace();
						log.error("格式化数据错误"+e.getMessage());
					}
				}
				try{	
					if(batchDataList.size()>0){
						int[] ins=toJdbcTemplate.batchUpdate(insert,batchDataList );
						if(ins>0){
							if(log.isDebugEnabled()){
								log.debug("insert the data success!");
							}
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
					log.error("语句执行错误"+e.getMessage());
				}
			}
			
			batchDataList.clear();
		}
		
	}*/
	
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
	private int migrateSubTableRow(String insert,Map<String,Object> row,TableModel table) throws DataAccessException, FormatException, SeparatorException{
		List<FieldModel> separatorFieldList=new ArrayList<>();
		for(FieldModel field:table.getFiledList()){
			if(null!=field.getFieldSeparatorModel()){
				separatorFieldList.add(field);
			}
		}
		
		if(separatorFieldList.size()==0){
			return toJdbcTemplate.update(insert, rowDataHandler.handle2MigrateRowData(row,table));
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
			num+=toJdbcTemplate.update(insert, rowDataHandler.handle2MigrateRowData(data,table));
		}
		
		return num;
		
	}
	
	
}
