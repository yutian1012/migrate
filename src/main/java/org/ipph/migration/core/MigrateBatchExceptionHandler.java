package org.ipph.migration.core;

import org.apache.log4j.Logger;
import org.ipph.migration.sql.SqlOperation;
import org.springframework.stereotype.Component;

@Component
public class MigrateBatchExceptionHandler {
	
	private Logger log=Logger.getLogger(SqlOperation.class);
	
	public void handlerBatchException(String sql){
		log.error("批次处理错误："+sql);
		
	}
}
