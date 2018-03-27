package org.ipph.exception;
/**
 * 反射异常类
 */
public class ReflectionException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	public ReflectionException() {
        super();
    }
	public ReflectionException(String s) {
        super(s);
    }
	public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public ReflectionException(Throwable cause) {
        super(cause);
    }
}
