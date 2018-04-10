package org.ipph.exception;

public class DataExistsException extends Exception{
	private static final long serialVersionUID = 1L;

	public DataExistsException(String message){
		super(message);
	}
}
