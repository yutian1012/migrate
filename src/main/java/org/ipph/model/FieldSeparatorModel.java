package org.ipph.model;

public class FieldSeparatorModel implements Cloneable{
	private String className;
	private String methodArgs;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodArgs() {
		return methodArgs;
	}
	public void setMethodArgs(String methodArgs) {
		this.methodArgs = methodArgs;
	}
	public FieldSeparatorModel copyFieldSeparatorModel(){
		try {
			return (FieldSeparatorModel) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
