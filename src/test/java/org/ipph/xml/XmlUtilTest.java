package org.ipph.xml;

import java.util.List;
import org.ipph.model.TableModel;
import org.ipph.util.XmlUtil;
import org.junit.Assert;
import org.junit.Test;

public class XmlUtilTest {
	
	@Test
	public void testValid(){
		//String xmlPath=XmlUtilTest.class.getClassLoader().getResource("tables/User.xml").getPath();
		String xmlPath=XmlUtilTest.class.getClassLoader().getResource("tables/IpFeeTemplate.xml").getPath();
		Assert.assertTrue(XmlUtil.validateXmlByXSD(xmlPath));
	}
	
	@Test
	public void testParse(){
		String xmlPath=XmlUtilTest.class.getClassLoader().getResource("tables/IpFeeTemplate.xml").getPath();
		List<TableModel> tableList=null;
		try {
			tableList=XmlUtil.parseBySax(xmlPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.assertEquals(1, tableList.size());
	}
	
	

}
