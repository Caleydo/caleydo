package org.caleydo.util.r;

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
			int[] array = new int[]{5, 6, 7};
			int[] array_2 = new int[]{1, 2, 3};
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
	}
}
