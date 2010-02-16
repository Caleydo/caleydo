package org.caleydo.util.r;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.ccontainer.FloatCContainerIterator;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.statistics.IStatisticsPerformer;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class RStatisticsPerformer implements IStatisticsPerformer {

	private Rengine engine;
	
	@Override
	public void init() {
		// just making sure we have the right version of everything
		if (!Rengine.versionCheck()) {
			System.err
					.println("** Version mismatch - Java files don't match library version.");
			System.exit(1);
		}
		System.out.println("Creating Rengine (with arguments)");
		String[] args = new String[1];
		args[0] = "--no-save";
		engine = new Rengine(args, false, new RConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's
		// ready
		if (!engine.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}
	}
	
	@Override
	public void performTest() {

		try {
			REXP test;
			int[] array = new int[]{223 ,259,248,220,287,191,229,270,245,201};//5, 6, 7};
			int[] array_2 = new int[]{220,244,243,211,299,170,210,276,252,189};//1, 2, 3};
			engine.assign("my_array", array);
			engine.assign("my_array_2", array_2);

			System.out.println("Array: " + engine.eval("my_array"));
			System.out.println("Array 2: " + engine.eval("my_array_2"));
			test = engine.eval("t.test(my_array,my_array_2)");
			System.out.println("T-Test result: " + test);
		} catch (Exception e) {
			System.out.println("EX:" + e);
			e.printStackTrace();
		}
		
		performStorageTest();
	}
	
	private void performStorageTest() {
		
		// ISet set = GeneralManager.get().getMasterUseCase().getSet();
		//		
		// double[] array1 = new double[set.size()];
		// double[] array2 = new double[set.size()];
		//		
		// for (IStorage storage : set) {
		// storage.get(EDataRepresentation.NORMALIZED, iIndex);
		// }
		//		
		// FloatCContainerIterator iter =
		// set.get(0).floatIterator(EDataRepresentation.NORMALIZED);
		// while(iter.hasNext()) {
		//			
		// }
	}
}
