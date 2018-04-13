package org.ipph.xml;

public interface XmlElement {
	//根元素标签
	public static final String rootNames="tables";
	//表标签
	public static final String table="table";
	public static final String subTable="subTable";
	//字段属性标签
	public static final String field="field";
	//字段类型
	public static final String type="type";
	//指定format标签
	public static final String format="format";
	//format_class标签下的format_class_name标签
	public static final String format_class_name="format_class_name";
	//format_class_method标签下的format_class_arg标签
	public static final String format_class_arg="format_class_arg";
	//字段条件字段
	public static final String field_condition="condition";
	
	public static final String fieldSeparator="fieldSeparator";
	public static final String separator_class_name="separator_class_name";
	public static final String separator_class_arg="separator_class_arg";
	
	public static final String gencode="gencode";
}
