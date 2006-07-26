package cerberus.application.prototype;

import cerberus.manager.singelton.OneForAllManager;
import cerberus.data.loader.MicroArrayLoader;

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
		
		//oneForAllManager.createNewId()
	}
}
