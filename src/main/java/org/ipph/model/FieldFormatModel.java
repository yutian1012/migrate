package org.ipph.model;

/**
 * 对象xml的format信息，记录格式化信息设置
 * 一般出现在迁移的目标Table设置中，放到源table无效
 */
public class FieldFormatModel implements Cloneable{
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
	public FieldFormatModel copyFieldFormatModel(){
		try {
			return (FieldFormatModel) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
