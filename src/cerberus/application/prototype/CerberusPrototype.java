package cerberus.application.prototype;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cerberus.manager.GeneralManager;
import cerberus.manager.gui.SWTGUIManagerSimple;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.view.ViewManagerSimple;
import cerberus.command.system.CmdSystemLoadFileViaImporter;
import cerberus.data.loader.MicroArrayLoader;
import cerberus.manager.type.ManagerObjectType;
import cerberus.xml.parser.kgml.KgmlSaxHandler;

public class CerberusPrototype 
{
	public static void main(String[] args) 
	{
		String sRawDataFileName = "data/MicroarrayData/gpr_format/tests/test_10_values.gpr";
		
		/**
		 * In order to use SWT call setStateSWT( true ) to enabel SWT support!
		 */
		OneForAllManager oneForAllManager = new OneForAllManager(null);
		oneForAllManager.setStateSWT( true );
		oneForAllManager.initAll();
		
		GeneralManager generalManager = oneForAllManager.getGeneralManager();
		
		//loading the raw data
//		MicroArrayLoader microArrayLoader = 
//			new MicroArrayLoader(oneForAllManager.getGeneralManager(), sRawDataFileName);
//		microArrayLoader.loadData();
		
		//load the pathway data
//		KgmlSaxHandler kgmlParser = new KgmlSaxHandler();
//		SAXParserFactory factory = SAXParserFactory.newInstance();
//		try
//		{
//			// Parse the input
//			SAXParser saxParser = factory.newSAXParser();
//			saxParser.parse(new File("data/XML/pathways/map00271.xml"),
//					kgmlParser);
//
//		} catch (Throwable t)
//		{
//			t.printStackTrace();
//		}
		
//		MicroArrayLoader microArrayLoader = new MicroArrayLoader(generalManager);
//		microArrayLoader.setTargetSet(oneForAllManager.getSingelton().getSetManager().getItemSet(25101));
//		microArrayLoader.setFileName(sRawDataFileName);
//		microArrayLoader.setTokenPattern("FLOAT;FLOAT;SKIP;STRING;STRING;ABORT");
//		microArrayLoader.loadData();

		CmdSystemLoadFileViaImporter commandFileImporter = 
			new CmdSystemLoadFileViaImporter(
				generalManager, sRawDataFileName, "SKIP;INT;SKIP;STRING;ABORT", 15101 );
		commandFileImporter.doCommand();
		
		SWTGUIManagerSimple swtGuiManager = (SWTGUIManagerSimple) generalManager.getManagerByBaseType(ManagerObjectType.GUI_SWT);
		
		ViewManagerSimple viewManager = (ViewManagerSimple) generalManager.getManagerByBaseType(ManagerObjectType.VIEW);
		//viewManager.createView(ManagerObjectType.VIEW_PATHWAY);
		//viewManager.createView(ManagerObjectType.VIEW_TEST_TABLE);
		viewManager.createView(ManagerObjectType.VIEW_DATA_EXPLORER);
		//viewManager.createView(ManagerObjectType.VIEW_SET_TABLE);
		//viewManager.createView(ManagerObjectType.VIEW_STORAGE_TABLE);
		//viewManager.createView(ManagerObjectType.VIEW_GEARS);
		
		swtGuiManager.runApplication();
	}
}
