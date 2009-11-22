package test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import daemon.BoundingBox;
import daemon.BoundingBoxList;

public class TestBoundingBoxes {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestBoundingBoxes.class);
	}
	
	@Test
	public void testXml() throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(BoundingBoxList.class);
		Marshaller m = jaxbContext.createMarshaller();
		
		BoundingBoxList bl = new BoundingBoxList();
		bl.add(new BoundingBox(20, 20, 200, 200));
		bl.add(new BoundingBox(30, 30, 300, 300));
		m.marshal(bl, System.out);
	}
	
}
