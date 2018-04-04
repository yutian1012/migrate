package org.ipph.thread;


import org.ipph.migration.sql.SqlOperation;
import org.ipph.model.TableModel;

public class MigrationTask implements Runnable{

	private SqlOperation sqlOperation;
	
	private TableModel table;
	
	public MigrationTask(SqlOperation sqlOperation,TableModel table){
		this.sqlOperation=sqlOperation;
		this.table=table;
	}
	
	@Override
	public void run() {
		sqlOperation.migrateTable(table);
	}
	
}
