package org.ipph.migration.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.ipph.exception.DataExistsException;
import org.ipph.exception.DataNotFoundException;
import org.ipph.exception.FormatException;
import org.ipph.exception.SeparatorException;
import org.ipph.migration.core.MigrateBatchExceptionHandler;
import org.ipph.migration.core.MigrateExceptionHandler;
import org.ipph.migration.data.RowDataHandler;
import org.ipph.migration.sql.SqlBuilder;
import org.ipph.migration.sql.SqlOperation;
import org.ipph.model.FieldModel;
import org.ipph.model.SubtableModel;
import org.ipph.model.TableModel;
import org.ipph.separator.SeparatorContext;
import org.ipph.thread.MigrationTask;
import org.ipph.thread.MigrationUpdateTask;
import org.ipph.thread.ThreadPool;
import org.ipph.util.MapUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class MigrateDao {
	@Resource
	private SqlOperation sqlOperation;
	@Resource
	private SqlBuilder sqlBuilder;
	@Resource
	private RowDataHandler rowDataHandler;
	@Resource
	private SeparatorContext separatorContext;
	@Resource
	private MigrateExceptionHandler migrateExceptionHandler;
	@Resource
	private MigrateBatchExceptionHandler migrateBatchExceptionHandler;
	@Resource
	private ThreadPool threadPool;
	
	private int size=3000;
	
	private Logger log=Logger.getLogger(MigrateDao.class);
	/**
	 * 迁移主表数据
	 * @param table
	 */
	public void migrateMasterTable(TableModel table) {
		//migrateTable(table, false);
		batchMigrateTable(table, 1,false);
	}
	/**
	 * 迁移子表数据
	 * @param table
	 */
	public void migrateSubTable(TableModel table) {
		List<SubtableModel> subTablelist=table.getSubTableList();
		if(null!=subTablelist&&subTablelist.size()>0){
			for(SubtableModel subTable:subTablelist){
				if(null==subTable||subTable.isSkip()==true) continue;
				batchMigrateTable(subTable,1,true);
			}
		}
	}
	
	/**
	 * 迁移主表数据
	 * @param table
	 */
	public void batchMigrateMasterTable(TableModel table, int batch) {
		batchMigrateTable(table,batch,false);
	}
	/**
	 * 迁移子表数据
	 * @param table
	 */
	public void batchMigrateSubTable(TableModel table, int batch) {
		List<SubtableModel> subTablelist=table.getSubTableList();
		if(null!=subTablelist&&subTablelist.size()>0){
			for(SubtableModel subTable:subTablelist){
				if(null==subTable||subTable.isSkip()==true) continue;
				batchMigrateTable(subTable,batch,true);
			}
		}
	}
	
	
	/**
	 * 批次迁移
	 * @param table
	 * @param i
	 * @param b
	 */
	private void batchMigrateTable(TableModel table, int batch,boolean isSubTable) {
		if(null==table|| table.isSkip()) return;
		
		long total=getTotal(table);
		/*if(!isSubTable){
			total=5;
		}*/
		for(int index=0;index<total;index+=size){
			threadPool.addTask(new MigrationTask(this, table, isSubTable,index, size,batch));
			
		}
		//threadPool.addTask(new MigrationTask(this, table, isSubTable,0, total,batch));
		//batchMigrateTable(table, batch, 0,total,isSubTable);
	}
	/**
	 * 更新目标数据集
	 * @param table
	 */
	public void updateTable(TableModel table) {
		if(null==table|| table.isSkip()) return;
		
		batchUpdateTable(table, 1);
	}
	/**
	 * 批次更新目标数据集
	 * @param table
	 * @param batch
	 */
	public void batchUpdateTable(TableModel table, int batch) {
		if(null==table|| table.isSkip()) return;
		
		long total=getTotal(table);
		
		//threadPool.addTask(new MigrationUpdateTask(this, table, 0, total,batch));
		for(int index=0;index<total;index+=size){
			threadPool.addTask(new MigrationUpdateTask(this, table, index, size,batch));
		}
	}
	/**
	 * 逐条迁移操作
	 * 包含错误记录和唯一性校验
	 * @param table
	 * @param batch
	 * @param isSubTable
	 */
	public  void migrateTable(TableModel table,long start,long size,boolean isSubTable){
		if(null==table|| table.isSkip()) return;
		
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		//判断唯一键是否重复
		String isExistsSql=sqlBuilder.isUniqueFieldExistsSelectSql(table);
		
		List<Map<String,Object>> result=sqlOperation.getMigrateData(select+" limit "+start+","+size,rowDataHandler.handleFieldCondition(table));
		
		List<Object[]> paramDataList=new ArrayList<>();
		
		if(null!=result){
			
			for(Map<String,Object> row:result){
				try{
					//判断唯一键或主键是否已经存在
					if(sqlOperation.isExists(isExistsSql, rowDataHandler.handleUniqueRowData(row,table))){
						throw new DataExistsException("记录已经存在!");
					}
					if(log.isDebugEnabled()){
						log.debug("执行插入语句："+insert);
					}
					int ins=0;
					if(isSubTable){
						paramDataList.clear();
						migrateSubTableRow(row, table, paramDataList);
						if(paramDataList.size()>0){
							sqlOperation.migrate(insert, paramDataList);
						}
					}else{
						ins=sqlOperation.execute(insert, rowDataHandler.handle2MigrateRowData(row,table));
					}
					if(ins>0){
						if(log.isDebugEnabled()){
							log.debug("insert the data success!");
						}
					}
				}catch(FormatException e){
					migrateExceptionHandler.formatExceptionHandler(e, row, table,isSubTable);
				}catch (DataExistsException e) {
					migrateExceptionHandler.dataExistsExceptionHandler(e, row, table);
				}catch (Exception e) {
					migrateExceptionHandler.sqlExceptionHandler(e, row, table,isSubTable);
				}
			}
		}
	}
	
	/**
	 * 批量的迁移操作
	 * @param table
	 * @param batch
	 * @param isSubTable
	 */
	public void batchMigrateTable(TableModel table,int batch,long start,long total,boolean isSubTable){
		
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String insert=sqlBuilder.getInsertSql(table);
		
		if(null==insert) return;
		
		List<Object[]> batchDataList=new ArrayList<>();
		List<Map<String,Object>> result=null;
		
		//判断唯一键是否重复
		String isExistsSql=sqlBuilder.isUniqueFieldExistsSelectSql(table);
		
		for(long index=start;index<total;index+=batch){
			String limitSql=select+" limit "+index+","+batch;
			
			result=sqlOperation.getMigrateData(limitSql,rowDataHandler.handleFieldCondition(table));
			
			if(null!=result){
				for(Map<String,Object> row:result){
					try{
						//判断唯一键或主键是否已经存在
						if(sqlOperation.isExists(isExistsSql, rowDataHandler.handleUniqueRowData(row,table))){
							throw new DataExistsException("记录已经存在!");
							//continue;
						}
						if(!isSubTable){
							//处理批次数据
							batchDataList.add(rowDataHandler.handle2MigrateRowData(row,table));
						}else{
							migrateSubTableRow(row, table, batchDataList);
						}
					}catch(FormatException e){
						migrateExceptionHandler.formatExceptionHandler(e,row, table,false);
					}catch (SeparatorException e) {
						migrateExceptionHandler.separatorExceptionHandler(e, row, table);
					} catch (DataExistsException e) {
						migrateExceptionHandler.dataExistsExceptionHandler(e, row, table);
					}
				}
				try{
					if(batchDataList.size()>0){
						sqlOperation.migrate(insert, batchDataList);
					}
				}catch (Exception e) {
					handleBatchException(table, isSubTable, index, batch);
				}
			}
			
			batchDataList.clear();
		}
	}
	/**
	 * 逐条更新目标数据库
	 * @param table
	 * @param start
	 * @param size
	 */
	public void updateTable(TableModel table,long start,long size) {
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
		
		List<Map<String,Object>> result=sqlOperation.getMigrateData(select+" limit "+start+","+size,rowDataHandler.handleFieldCondition(table));
		
		if(result!=null){
			for(Map<String,Object> row:result){
				try{
					if(null!=toUpdSelect){
						if(!sqlOperation.isExists(toUpdSelect, rowDataHandler.handle2UpdRowData(row,table))){
							throw new DataNotFoundException("未找到更新记录");
						}
					}
					//更新数据
					int upd=sqlOperation.execute(update, rowDataHandler.handle2MigrateRowData(row,table));
					if(upd>0){
						if(log.isDebugEnabled()){
							log.debug("update the data success!");
						}
					}else{
						log.error("更新失败"+MapUtil.outMapData(row));
					}
				}catch(FormatException e){
					migrateExceptionHandler.formatExceptionHandler(e, row, table,true);
				}catch(DataNotFoundException e){
					migrateExceptionHandler.dataNotFoundExceptionHandler(e,row, table);
				}catch(Exception e){
					migrateExceptionHandler.sqlExceptionHandler(e, row, table,true);
				}
				
			}
		}
	}
	
	/**
	 * 批量的迁移操作
	 * @param table
	 * @param batch
	 * @param isSubTable
	 */
	public void batchUpdateTable(TableModel table,int batch,long start,long total){
		
		String select=sqlBuilder.getSelectSql(table);
		
		if(null==select) return;
		
		String update=sqlBuilder.getUpdateSql(table);
		
		//update语句必须要有条件，否则直接返回
		if(null==update||update.indexOf("?")==-1) return;
		
		String toUpdSelect=sqlBuilder.isExistSelectSql(table);
		
		if(log.isDebugEnabled()){
			log.debug("执行update语句："+update);
		}
		
		List<Object[]> batchDataList=new ArrayList<>();
		
		for(long index=start;index<total;index+=batch){
			String limitSql=select+" limit "+index+","+batch;
			List<Map<String,Object>> result=sqlOperation.getMigrateData(limitSql,rowDataHandler.handleFieldCondition(table));
		
			if(result!=null){
				for(Map<String,Object> row:result){
					try{
						if(null!=toUpdSelect){
							if(!sqlOperation.isExists(toUpdSelect, rowDataHandler.handle2UpdRowData(row,table))){
								throw new DataNotFoundException("未找到更新记录");
							}
						}
						batchDataList.add(rowDataHandler.handle2MigrateRowData(row,table));
					}catch(FormatException e){
						migrateExceptionHandler.formatExceptionHandler(e, row, table,true);
					}catch(DataNotFoundException e){
						migrateExceptionHandler.dataNotFoundExceptionHandler(e,row, table);
					}
				}
				try{
					if(batchDataList.size()>0){
						sqlOperation.migrate(update, batchDataList);
					}
				}catch (Exception e) {
					handleBatchUpdateException(table, index, batch);
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
		return sqlOperation.getTotal(selectCount, rowDataHandler.handleFieldCondition(table));
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
	private void migrateSubTableRow(Map<String,Object> row,TableModel table,List<Object[]> paramDataList) throws FormatException, SeparatorException{
		
		List<FieldModel> separatorFieldList=new ArrayList<>();
		for(FieldModel field:table.getFiledList()){
			if(null!=field.getFieldSeparatorModel()){
				separatorFieldList.add(field);
			}
		}
		
		if(separatorFieldList.size()==0){
			paramDataList.add(rowDataHandler.handle2MigrateRowData(row,table));
		}
		
		//处理需要拆分的数据
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
		for(Map<String,Object> data:dataList){
			paramDataList.add(rowDataHandler.handle2MigrateRowData(data,table));
		}
	}
	/**
	 * 处理批次的异常
	 * @param table
	 * @param isSubTable
	 * @param index
	 * @param total
	 * @param batch
	 */
	private void handleBatchException(TableModel table,boolean isSubTable,long index,long size){
		threadPool.addTask(new MigrationTask(this, table,  isSubTable,index, size,1));
	}
	/**
	 * 处理批次的异常
	 * @param table
	 * @param isSubTable
	 * @param index
	 * @param total
	 * @param batch
	 */
	private void handleBatchUpdateException(TableModel table,long index,long size){
		threadPool.addTask(new MigrationUpdateTask(this, table,  index, size,1));
	}
}
