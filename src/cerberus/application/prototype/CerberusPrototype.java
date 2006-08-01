package cerberus.application.prototype;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cerberus.manager.gui.SWTGUIManagerSimple;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.view.ViewManagerSimple;
import cerberus.data.loader.MicroArrayLoader;
import cerberus.manager.type.ManagerObjectType;
import cerberus.xml.parser.kgml.KgmlSaxHandler;

public class CerberusPrototype 
{
	public static void main(String[] args) 
	{
		System.setProperty("java.library.path", "C:\\Compiler\\JAVA\\jre1.5.0_04\\lib\\ext");
		
		System.err.println(" java.lib.path: " + 
				System.getProperty("java.library.path") );
		
		String sRawDataFileName = "data/MicroarrayData/slide30.gpr";
		
		/**
		 * In order to use SWT call setStateSWT( true ) to enabel SWT support!
		 */
		OneForAllManager oneForAllManager = new OneForAllManager(null);
		oneForAllManager.setStateSWT( true );
		oneForAllManager.initAll();
		
		//loading the raw data
//		MicroArrayLoader microArrayLoader = 
//			new MicroArrayLoader(oneForAllManager.getGeneralManager(), sRawDataFileName);
//		microArrayLoader.loadData();
		
		//load the pathway data
		KgmlSaxHandler kgmlParser = new KgmlSaxHandler();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new File("data/XML/pathways/map00271.xml"),
					kgmlParser);

		} catch (Throwable t)
		{
			t.printStackTrace();
		}
		
		MicroArrayLoader microArrayLoader = new MicroArrayLoader(oneForAllManager.getGeneralManager());
		microArrayLoader.setTargetSet(oneForAllManager.getSingelton().getSetManager().getItemSet(25101));
		microArrayLoader.setFileName(sRawDataFileName);
		microArrayLoader.setTokenPattern("FLOAT;FLOAT;SKIP;STRING;STRING;ABORT");
		microArrayLoader.loadData();
			
		SWTGUIManagerSimple swtGuiManager = (SWTGUIManagerSimple) oneForAllManager.getManagerByBaseType(ManagerObjectType.GUI_SWT);
		
		ViewManagerSimple viewManager = (ViewManagerSimple) oneForAllManager.getManagerByBaseType(ManagerObjectType.VIEW);
		viewManager.createView(ManagerObjectType.PATHWAY_VIEW);
		//viewManager.createView(ManagerObjectType.TEST_TABLE_VIEW);
		viewManager.createView(ManagerObjectType.SET_TABLE_VIEW);
		//viewManager.createView(ManagerObjectType.STORAGE_TABLE_VIEW);
		viewManager.createView(ManagerObjectType.GEARS_VIEW);
		//viewManager.createView(ManagerObjectType.GEARS_VIEW);
		
		swtGuiManager.runApplication();
	}
}
