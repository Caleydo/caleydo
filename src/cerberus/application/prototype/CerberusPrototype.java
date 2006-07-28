package cerberus.application.prototype;

import cerberus.manager.gui.SWTGUIManagerSimple;
import cerberus.manager.singelton.OneForAllManager;
import cerberus.manager.view.ViewManagerSimple;
import cerberus.data.loader.MicroArrayLoader;
import cerberus.manager.type.ManagerObjectType;

public class CerberusPrototype 
{
	public static void main(String[] args) 
	{
		String sRawDataFileName = "data/MicroarrayData/slides30.gpr";
		OneForAllManager oneForAllManager = new OneForAllManager(null);
		
		//loading the raw data
		MicroArrayLoader microArrayLoader = 
			new MicroArrayLoader(oneForAllManager.getGeneralManager(), sRawDataFileName);
		microArrayLoader.loadData();
		
		SWTGUIManagerSimple swtGuiManager = (SWTGUIManagerSimple) oneForAllManager.getManagerByBaseType(ManagerObjectType.GUI_SWT);
		
		ViewManagerSimple viewManager = (ViewManagerSimple) oneForAllManager.getManagerByBaseType(ManagerObjectType.VIEW);
		viewManager.createView(ManagerObjectType.PATHWAY_VIEW);
		viewManager.createView(ManagerObjectType.TABLE_VIEW);

		swtGuiManager.runApplication();
	
	}
}
