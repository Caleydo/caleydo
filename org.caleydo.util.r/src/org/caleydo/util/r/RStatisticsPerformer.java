package org.caleydo.util.r;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.statistics.IStatisticsPerformer;
import org.caleydo.util.r.listener.CompareGroupsEventListener;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class RStatisticsPerformer
		implements
			IStatisticsPerformer,
			IListenerOwner {

	private Rengine engine;

	private CompareGroupsEventListener compareGroupsEventListener = null;

	public RStatisticsPerformer() {
		init();

		registerEventListeners();
	}

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

	private void registerEventListeners() {

		compareGroupsEventListener = new CompareGroupsEventListener();
		compareGroupsEventListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(
				CompareGroupsEvent.class, compareGroupsEventListener);
	}

	// TODO: never called!
	public void unregisterEventListeners() {

		if (compareGroupsEventListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(
					compareGroupsEventListener);
			compareGroupsEventListener = null;
		}
	}

	@Override
	public void performTest() {

		try {
			REXP test;
			int[] array = new int[]{223, 259, 248, 220, 287, 191, 229, 270,
					245, 201};// 5, 6, 7};
			int[] array_2 = new int[]{220, 244, 243, 211, 299, 170, 210, 276,
					252, 189};// 1, 2, 3};
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

	public void foldChange(ISet set1, ISet set2) {

		NumericalStorage meanStorageVec1 = set1.getMeanStorage();
		NumericalStorage meanStorageVec2 = set2.getMeanStorage();

		double[] meanStorage1 = new double[meanStorageVec1.size()];
		for (int contentIndex = 0; contentIndex < meanStorageVec1.size(); contentIndex++) {
			meanStorage1[contentIndex] = meanStorageVec1.getFloat(
					EDataRepresentation.RAW, contentIndex);
		}

		double[] meanStorage2 = new double[meanStorageVec2.size()];
		for (int contentIndex = 0; contentIndex < meanStorageVec2.size(); contentIndex++) {
			meanStorage2[contentIndex] = meanStorageVec2.getFloat(
					EDataRepresentation.RAW, contentIndex);
		}

		engine.assign("set_1", meanStorage1);
		engine.assign("set_2", meanStorage2);
		//
		engine.eval("library(\"gtools\")");
		REXP foldChangeResult = engine.eval("foldchange(set_1,set_2)");
		System.out.println("Fold change result: " + foldChangeResult);

		double[] resultVec = foldChangeResult.asDoubleArray();

		set1.getStatisticsResult().setFoldChangeResult(set2, resultVec);
		set2.getStatisticsResult().setFoldChangeResult(set1, resultVec);
	}

	public void twoSidedTTest(ArrayList<ISet> sets) {

		// Perform t-test between all neighboring sets (A<->B<->C)
		for (int setIndex = 0; setIndex < sets.size(); setIndex++) {

			if (setIndex + 1 == sets.size())
				break;

			ISet set1 = sets.get(setIndex);
			ISet set2 = sets.get(setIndex + 1);

			ArrayList<Double> pValueVector = new ArrayList<Double>();

			for (int contentIndex = 0; contentIndex < set1.get(
					set1.getStorageVA(StorageVAType.STORAGE).get(0)).size(); contentIndex++) {

				StorageVirtualArray storageVA1 = set1
						.getStorageVA(StorageVAType.STORAGE);
				StorageVirtualArray storageVA2 = set2
						.getStorageVA(StorageVAType.STORAGE);

				double[] compareVec1 = new double[storageVA1.size()];
				double[] compareVec2 = new double[storageVA2.size()];

				int storageCount = 0;
				for (Integer storageIndex : storageVA1) {
					compareVec1[storageCount++] = set1.get(storageIndex)
							.getFloat(EDataRepresentation.RAW, contentIndex);
				}

				storageCount = 0;
				for (Integer storageIndex : storageVA2) {
					compareVec2[storageCount++] = set2.get(storageIndex)
							.getFloat(EDataRepresentation.RAW, contentIndex);
				}

				engine.assign("set_1", compareVec1);
				engine.assign("set_2", compareVec2);

				REXP compareResult = engine.eval("t.test(set_1,set_2)");

				// System.out.println("T-Test result: " + compareResult);

				REXP pValue = (REXP) compareResult.asVector().get(2);
				pValueVector.add(pValue.asDouble());
				// System.out.println(pValue.asDouble());

				set1.getStatisticsResult().setTwoSiddedTTestResult(set2,
						pValueVector);
				set2.getStatisticsResult().setTwoSiddedTTestResult(set1,
						pValueVector);
			}
		}

		// setsToCompare.get(0).getStatisticsResult().getVABasedOnCompareResult(setsToCompare.get(1),
		// 0.9f);

		System.out.println("Finished");
	}
	@Override
	public synchronized void queueEvent(
			AEventListener<? extends IListenerOwner> listener, AEvent event) {

		// compareGroupsEventListener.handleEvent(event);
	}
}
