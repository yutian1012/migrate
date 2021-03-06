package org.ipph.xml;

import java.util.ArrayList;
import java.util.List;

import org.ipph.model.FieldConditionModel;
import org.ipph.model.FieldConditionTypeEnum;
import org.ipph.model.FieldDataTypeEnum;
import org.ipph.model.FieldFormatModel;
import org.ipph.model.FieldModel;
import org.ipph.model.FieldRestrictEnum;
import org.ipph.model.FieldSeparatorModel;
import org.ipph.model.SubtableModel;
import org.ipph.model.TableModel;
import org.ipph.model.TableOperationEnum;
import org.ipph.model.WhereModel;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <tables>
	<table from="fee" to="fee" type="MIGRATE">
		<fields>
			<field from="caseNo" to="">
				<type></type>
				<format>
					<format_class_name>org.ipph.format.JsonMethodFormat</format_class_name>
					<format_class_method>
						<format_class_method_name>execute</format_class_method_name>
						<format_class_method_arg>{'SQF':'申请费','SSF':'实审费','FSF':'复审费','ZLSQF':'授权费','DLF':'代理费','QTF':'其他费'}</format_class_method_arg>
					</format_class_method>
				</format>
			</field>
		</fields>
	 </table>
	</tables>
 *
 */
public class TransferTableHandler extends DefaultHandler {
	List<TableModel> tableList=null;
    private TableModel table=null;
    private FieldModel fieldModel=null;
    private FieldFormatModel fieldFormatModel=null;
    private FieldConditionModel fieldConditionModel=null;
    private SubtableModel subTableModel=null;
    private FieldSeparatorModel fieldSeparatorModel=null;
    private WhereModel whereModel=null;
    private StringBuffer temp=new StringBuffer();
    
    @Override
    public void startDocument () {  
        //开始解析文档  
    	tableList=new ArrayList<>();
    }  
    @Override
    public void endDocument () {
        //文档解析结束  
    	table=null;
    	fieldModel=null;
    	fieldFormatModel=null;
    	fieldConditionModel=null;
    	subTableModel=null;
    	fieldSeparatorModel=null;
    	whereModel=null;
    }
    
    /**
     * 标签开始前初始化对象
     */
    @Override
    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
    	//开始解析节点，设置当前解析节点的标签名称
    	if(XmlElement.table.equals(qName)) {//表
    		table=new TableModel();
        	table.setType(TableOperationEnum.valueOf(attributes.getValue("type")));
        	table.setFrom(attributes.getValue("from"));
        	table.setTo(attributes.getValue("to"));
        	if(null!=attributes.getValue("skip")&&!"".equals(attributes.getValue("skip"))){
        		table.setSkip(Boolean.parseBoolean(attributes.getValue("skip").trim()));
        	}
        	table.setFiledList(new ArrayList<FieldModel>());
        	table.setSubTableList(new ArrayList<SubtableModel>());
        }else if(XmlElement.subTable.equals(qName)){//子表
        	subTableModel=new SubtableModel();
        	if(null!=table){
        		subTableModel.setFrom(table.getFrom());
        	}
        	subTableModel.setTo(attributes.getValue("name"));
        	if(null!=attributes.getValue("skip")&&!"".equals(attributes.getValue("skip"))){
        		subTableModel.setSkip(Boolean.parseBoolean(attributes.getValue("skip").trim()));
        	}
        	subTableModel.setFiledList(new ArrayList<FieldModel>());
        }else if(XmlElement.field.equals(qName)){//字段
        	fieldModel=new FieldModel();
        	fieldModel.setFrom(attributes.getValue("from"));
        	//fieldModel.setTo(null==attributes.getValue("to")?attributes.getValue("from"):attributes.getValue("to"));
        	fieldModel.setTo(attributes.getValue("to"));
        	fieldModel.setDefaultValue(null!=attributes.getValue("field_default")?attributes.getValue("field_default"):"");
        	if(null!=attributes.getValue("field_restrict")&&!"".equals(attributes.getValue("field_restrict"))){
        		fieldModel.setRestrict(FieldRestrictEnum.valueOf(attributes.getValue("field_restrict")));
        	}
        }else if(XmlElement.where.equals(qName)){//where条件
        	whereModel=new WhereModel();
        	whereModel.setFieldList(new ArrayList<FieldModel>());
        }else if(XmlElement.format.equals(qName)){//格式化
        	fieldFormatModel=new FieldFormatModel();
        }else if(XmlElement.fieldSeparator.equals(qName)){//字段拆分
        	fieldSeparatorModel=new FieldSeparatorModel();
        }else if(XmlElement.field_condition.equals(qName)){//条件
        	fieldConditionModel=new FieldConditionModel();
        	/*String type=attributes.getValue("type");
        	FieldConditionTypeEnum[] arr=FieldConditionTypeEnum.values();*/
        	fieldConditionModel.setConditionType(FieldConditionTypeEnum.valueOf(attributes.getValue("type")));
        }
    };
    /**
     * 获取标签体中的值
     */
    @Override  
    public void characters(char[] ch, int start, int length)throws SAXException {
    	if(length<=0) return;
    	temp.append(new String(ch,start,length));
    }  
    /**
     * 标签结束时赋值对象并重置数据
     */
    @Override  
    public void endElement(String uri, String localName, String qName)throws SAXException { 
    	String s=temp.toString().replaceAll("\\s*|\t|\r|\n", "");
    	//解析Field内的节点内容
    	if(XmlElement.type.equals(qName)){
    		if(null!=fieldModel){
    			if(null!=s&&!"".equals(s)){
    				fieldModel.setFieldType(FieldDataTypeEnum.valueOf(s));
    			}
    		}
    	}else if(XmlElement.format_class_name.equals(qName)) {
    		if(null!=fieldFormatModel){
    			fieldFormatModel.setClassName(s);
    		}
    	}else if(XmlElement.format_class_arg.equals(qName)) {  
    		if(null!=fieldFormatModel){
    			fieldFormatModel.setMethodArgs(s);
    		}
    	}else if(XmlElement.format.equals(qName)){
    		if(null!=fieldModel&&fieldFormatModel!=null){
    			fieldModel.setFormat(fieldFormatModel.copyFieldFormatModel());//克隆对象
    		}
    		fieldFormatModel=null;
    	}else if(XmlElement.gencode.equals(qName)){
    		if(null!=fieldModel){
    			fieldModel.setGencode(true);
    		}
    		fieldFormatModel=null;
    	}else if(XmlElement.separator_class_name.equals(qName)) {
    		if(null!=fieldSeparatorModel){
    			fieldSeparatorModel.setClassName(s);
    		}
    	}else if(XmlElement.separator_class_arg.equals(qName)) {  
    		if(null!=fieldSeparatorModel){
    			fieldSeparatorModel.setMethodArgs(s);
    		}
    	}else if(XmlElement.fieldSeparator.equals(qName)){
    		if(null!=fieldModel&&fieldSeparatorModel!=null){
    			fieldModel.setFieldSeparatorModel(fieldSeparatorModel.copyFieldSeparatorModel());//克隆对象
    		}
    		fieldSeparatorModel=null;
    	}else if(XmlElement.field_condition.equals(qName)){
    		if(null!=fieldModel&&fieldConditionModel!=null){
    			fieldConditionModel.setValue(s);
    			fieldModel.setCondition(fieldConditionModel.copyFieldConditionModel());//克隆对象
    		}
    		fieldFormatModel=null;
    	}else if(XmlElement.field.equals(qName)){
    		if(null!=fieldModel&&null!=table){
    			if(whereModel!=null){
    				whereModel.getFieldList().add(fieldModel.copyFieldModel());
    			}
    			else if(subTableModel!=null){
    				subTableModel.getFiledList().add(fieldModel.copyFieldModel());
    			}else{
    				table.getFiledList().add(fieldModel.copyFieldModel());
    			}
    		}
    		fieldModel=null;
    	}else if(XmlElement.where.equals(qName)){
    		if(null!=whereModel){
    			if(null!=subTableModel){
    				subTableModel.setWhereModel(whereModel.copyWhereModel());
    			}else if(null!=table){
    				table.setWhereModel(whereModel);
    			}
    		}
    		whereModel=null;
    	}else if(XmlElement.subTable.equals(qName)){
    		if(null!=subTableModel&&null!=table){
    			table.getSubTableList().add(subTableModel.copySubtableModel());
    		}
    		subTableModel=null;
    	}else if(XmlElement.table.equals(qName)){
    		if(null!=table){
    			tableList.add(table.copyTableModel());
    		}
    		table=null;
    	}
    	
    	temp.setLength(0);
    }  
  
    public List<TableModel> getResult(){
    	return this.tableList;
    }
}