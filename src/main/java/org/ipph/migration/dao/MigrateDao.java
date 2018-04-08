package org.ipph.migration.dao;

import javax.annotation.Resource;

import org.ipph.migration.sql.SqlOperation;
import org.ipph.model.TableModel;
import org.springframework.stereotype.Repository;

@Repository
public class MigrateDao {
	@Resource
	private SqlOperation sqlOperation;
	/**
	 * 逐条操作
	 * @param tableModel
	 */
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
	 * 批次操作
	 * @param tableModel
	 */
	public void batchTransferTable(TableModel tableModel){
		switch (tableModel.getType()) {
		case MIGRATE:
			batchMigrateTable(tableModel);
			break;
		case UPDATE:
			batchUpdateTable(tableModel);
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
		sqlOperation.migrateMasterTable(table);
		//迁移子表
		sqlOperation.migrateSubTable(table);
	}
	/**
	 * 数据库迁移操作
	 * @param fromTable
	 * @param targetTable
	 */
	public void batchMigrateTable(TableModel table){
		if(null==table|| table.isSkip()) return;
		//迁移主表
		sqlOperation.batchMigrateTable(table,50,false);
		//迁移子表
		//sqlOperation.migrateSubTable(table,50);
	}
	/**
	 * 更新数据库操作
	 * @param table
	 */
	public void updateTable(TableModel table){
		if(null==table|| table.isSkip()) return;
		
		sqlOperation.updateTable(table);
	}
	
	/**
	 * 更新数据库操作
	 * @param table
	 */
	public void batchUpdateTable(TableModel table){
		if(null==table|| table.isSkip()) return;
	}
}
