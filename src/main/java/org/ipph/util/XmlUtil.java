package org.ipph.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.ipph.model.TableModel;
import org.ipph.xml.TransferTableHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XmlUtil {
	/**
	 * 解析xml
	 * @param path
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static List<TableModel> parseBySax(String path) throws ParserConfigurationException, SAXException, IOException{
		
		if(! validateXmlByXSD(path)) return null;
		
		// 创建解析工厂  
        SAXParserFactory factory = SAXParserFactory.newInstance();  
        // 创建解析器  
        SAXParser parser = factory.newSAXParser();  
        // 得到读取器  
        XMLReader reader = parser.getXMLReader();
        //设置内容处理器
        TransferTableHandler handler=new TransferTableHandler();
        reader.setContentHandler(handler);
        //读取xml文档
        reader.parse(path);
        
        return handler.getResult();
	}
	
	/**
	 * 判断xml是否符合规范
	 * @param xmlPath
	 * @return
	 */
	public static boolean validateXmlByXSD(String xmlPath){
		FileInputStream fis=null;
		boolean result=false;
        try {
        	SchemaFactory schemaFactory=SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        	Schema schema=schemaFactory.newSchema(new StreamSource(XmlUtil.class.getResourceAsStream("/tables/tables.xsd")));
        	Validator validator=schema.newValidator();
        	fis=new FileInputStream(xmlPath);
			validator.validate(new StreamSource(fis));
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
			result=false;
		}finally{
			if(null!=fis){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return result;
	}
}
