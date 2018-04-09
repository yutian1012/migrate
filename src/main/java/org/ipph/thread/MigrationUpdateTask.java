package org.ipph.thread;

import org.ipph.migration.dao.MigrateDao;
import org.ipph.model.TableModel;

public class MigrationUpdateTask implements Runnable{

	private MigrateDao migrateDao;
	
	private TableModel table;
	
	private long start;
	
	private long size;
	
	private int batch;
	
	public MigrationUpdateTask(MigrateDao migrateDao,TableModel table,long start,long size,int batch){
		this.migrateDao=migrateDao;
		this.table=table;
		this.start=start;
		this.size=size;
		this.batch=batch;
	}
	
	@Override
	public void run() {
		if(batch>1){
			//批次操作时传递总量
			migrateDao.batchUpdateTable(table, batch, start, start+size);
		}else{
			//单价操作时传递数量
			migrateDao.updateTable(table, start, size);
		}
	}
	
}
