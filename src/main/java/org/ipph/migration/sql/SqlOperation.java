package org.ipph.migration.sql;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.ipph.migration.data.RowDataHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class SqlOperation {
	@Resource
	private SqlBuilder sqlBuilder;
	@Resource
	private JdbcTemplate fromJdbcTemplate;
	@Resource
	private JdbcTemplate toJdbcTemplate;
	
	@Resource
	private RowDataHandler rowDataHandler;
	
	@Resource
	private TransactionTemplate transactionTemplate;
	
	//private int batch=50;
	
	/*private ExecutorService threadPool = Executors.newFixedThreadPool(10, new NamedThreadFactory("migration"));*/
	
	private Logger log=Logger.getLogger(SqlOperation.class);
	
	/**
	 * 获取数据
	 * @param select
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> getMigrateData(String select,Object[] params){
		return fromJdbcTemplate.queryForList(select,params);
	}
	/**
	 * 判断记录是否存在
	 * @param toUpdSelect
	 * @param params
	 * @return
	 */
	public boolean isExists(String toUpdSelect,Object[] params){
		if(toUpdSelect!=null){
			Map<String, Object> count=toJdbcTemplate.queryForMap(toUpdSelect,params);
			long total=0;
			
			if(null!=count.get("num")&&(Long)count.get("num")>0L){//唯一键是否重复
				total=(Long)count.get("num");
			}
		
			return total>0L;
		}
		return false;
	}
	
	public long getTotal(String selectCount,Object[] params){
		
		Map<String, Object> count=fromJdbcTemplate.queryForMap(selectCount,params);
		long total=0;
		
		if(null!=count.get("num")&&(Long)count.get("num")>0L){//唯一键是否重复
			total=(Long)count.get("num");
		}
	
		return total;
	}
	/**
	 * 执行操作
	 * @param update
	 * @param params
	 * @return
	 */
	public int execute(String update,Object[] params){
		return toJdbcTemplate.update(update, params);
	}
	
	public Map<String,Object> queryForMap(String limitSql,Object[] params){
		return fromJdbcTemplate.queryForMap(limitSql,params);
	}
	
	/**
	 * 使用编程式事务控制批量数据处理
	 * @param insert
	 * @param batchDataList
	 * @return
	 */
	public int[] migrate(final String insert,final List<Object[]> batchDataList){
		return transactionTemplate.execute(new TransactionCallback<int[]>() {
			@Override
			public int[] doInTransaction(TransactionStatus arg0) {
				int[] ins=toJdbcTemplate.batchUpdate(insert,batchDataList);
				return ins;
			}
		});
	}
	
}
