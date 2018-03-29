package org.ipph.exception;
/**
 * 专利格式化错误
 */
public class PatentFormatException extends FormatException{
	private static final long serialVersionUID = 1L;

	public PatentFormatException(String message){
		super(message);
	}
}
