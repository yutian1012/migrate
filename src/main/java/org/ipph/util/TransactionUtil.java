package org.ipph.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionUtil {
	public static void beginTransaction(Connection cnn){
		if(cnn!=null){
			try {
				if(cnn.getAutoCommit()){
					cnn.setAutoCommit(false);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 提交事务
	 * @param cnn
	 */
	public static void commitTransaction(Connection cnn){
		if(cnn!=null){
			try {
				if(!cnn.getAutoCommit()){
					cnn.commit();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 回滚事务
	 * @param cnn
	 */
	public static void rollBackTransaction(Connection cnn){
		if(cnn!=null){
			try {
				if(!cnn.getAutoCommit()){
					cnn.rollback();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
