package org.ipph.migration.core;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.ipph.migration.sql.SqlOperation;
import org.ipph.model.TableModel;
import org.ipph.util.XmlUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xml.sax.SAXException;

public class MigrateMain {
	public static void main(String[] args) {
		ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext.xml");
		
		String xmlFile=MigrateMain.class.getClassLoader().getResource("tables/User.xml").getPath();
		SqlOperation operation=context.getBean(SqlOperation.class);
		
		List<TableModel> tableList;
		try {
			tableList = XmlUtil.parseBySax(xmlFile);
			if(null!=tableList){
				for(TableModel table:tableList){
					operation.transferTable(table);
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
}
