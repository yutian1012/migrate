package org.ipph.xml;

import java.util.ArrayList;
import java.util.List;

import org.ipph.model.FieldConditionModel;
import org.ipph.model.FieldConditionTypeEnum;
import org.ipph.model.FieldDataTypeEnum;
import org.ipph.model.FieldFormatModel;
import org.ipph.model.FieldModel;
import org.ipph.model.FieldRestrictEnum;
import org.ipph.model.TableModel;
import org.ipph.model.TableOperationEnum;
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
    }
    
    /**
     * 标签开始前初始化对象
     */
    @Override
    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
    	//开始解析节点，设置当前解析节点的标签名称
    	if(XmlElement.table.equals(qName)) {
    		table=new TableModel();
        	table.setType(TableOperationEnum.valueOf(attributes.getValue("type")));
        	table.setFrom(attributes.getValue("from"));
        	table.setTo(attributes.getValue("to"));
        	if(null!=attributes.getValue("skip")&&!"".equals(attributes.getValue("skip"))){
        		table.setSkip(Boolean.parseBoolean(attributes.getValue("skip").trim()));
        	}
        	table.setFiledList(new ArrayList<FieldModel>());
        }else if(XmlElement.field.equals(qName)){
        	fieldModel=new FieldModel();
        	fieldModel.setFrom(attributes.getValue("from"));
        	//fieldModel.setTo(null==attributes.getValue("to")?attributes.getValue("from"):attributes.getValue("to"));
        	fieldModel.setTo(attributes.getValue("to"));
        	fieldModel.setDefaultValue(null!=attributes.getValue("field_default")?attributes.getValue("field_default"):"");
        	if(null!=attributes.getValue("field_restrict")&&!"".equals(attributes.getValue("field_restrict"))){
        		fieldModel.setRestrict(FieldRestrictEnum.valueOf(attributes.getValue("field_restrict")));
        	}
        }else if(XmlElement.format.equals(qName)){
        	fieldFormatModel=new FieldFormatModel();
        }else if(XmlElement.field_condition.equals(qName)){
        	fieldConditionModel=new FieldConditionModel();
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
    	}else if(XmlElement.field_condition.equals(qName)){
    		if(null!=fieldModel&&fieldConditionModel!=null){
    			fieldConditionModel.setValue(s);
    			fieldModel.setCondition(fieldConditionModel.copyFieldConditionModel());//克隆对象
    		}
    		fieldFormatModel=null;
    	}else if(XmlElement.field.equals(qName)){
    		if(null!=fieldModel&&null!=table){
    			table.getFiledList().add(fieldModel.copyFieldModel());
    		}
    		fieldModel=null;
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