package org.ipph.thread;

import org.ipph.migration.dao.MigrateDao;
import org.ipph.model.TableModel;

public class MigrationTask implements Runnable{
	
	//private Logger log=Logger.getLogger(MigrationTask.class);

	private MigrateDao migrateDao;
	
	private TableModel table;
	
	private boolean isSubTable;
	
	private long start;
	
	private long size;
	
	private int batch;
	
	public MigrationTask(MigrateDao migrateDao,TableModel table,boolean isSubTable,long start,long size,int batch){
		this.migrateDao=migrateDao;
		this.table=table;
		this.isSubTable=isSubTable;
		this.start=start;
		this.size=size;
		this.batch=batch;
	}
	
	@Override
	public void run() {
		if(batch>1){
			//批次操作时传递总量
			migrateDao.batchMigrateTable(table, batch, start, start+size, isSubTable);
		}else{
			//单价操作时传递数量
			migrateDao.migrateTable(table, start, size, isSubTable);
		}
	}
	
}
