package org.ipph.xml;

public interface XmlElement {
	//根元素标签
	public static final String rootNames="tables";
	//表标签
	public static final String table="table";
	//字段属性标签
	public static final String field="field";
	//字段类型
	public static final String type="type";
	//指定format标签
	public static final String format="format";
	//format_class标签下的format_class_name标签
	public static final String format_class_name="format_class_name";
	//format_class_method标签下的format_class_arg标签
	public static final String format_class_arg="format_class_method_arg";
}
