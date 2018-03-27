package org.ipph.format;

public interface Formater {
	/**
	 * 格式化方法，输出格式化后的数据值
	 * @param args
	 * @param value
	 * @return
	 */
	public Object format(String args,Object value);
}
