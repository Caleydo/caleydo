package org.caleydo.util.r;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.StatisticsFoldChangeReductionEvent;
import org.caleydo.core.manager.event.data.StatisticsPValueReductionEvent;
import org.caleydo.core.manager.event.data.StatisticsResultFinishedEvent;
import org.caleydo.core.manager.event.data.StatisticsTwoSidedTTestReductionEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.statistics.IStatisticsPerformer;
import org.caleydo.util.r.dialog.FoldChangeDialog;
import org.caleydo.util.r.listener.StatisticsFoldChangeReductionListener;
import org.caleydo.util.r.listener.StatisticsPValueReductionListener;
import org.caleydo.util.r.listener.StatisticsTwoSidedTTestReductionListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class RStatisticsPerformer implements IStatisticsPerformer, IListenerOwner {

	private Rengine engine;

	// private CompareGroupsEventListener compareGroupsEventListener = null;
	private StatisticsPValueReductionListener statisticsPValueReductionListener = null;
	private StatisticsFoldChangeReductionListener statisticsFoldChangeReductionListener = null;
	private StatisticsTwoSidedTTestReductionListener statisticsTwoSidedTTestReductionListener = null;

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

		// compareGroupsEventListener = new CompareGroupsEventListener();
		// compareGroupsEventListener.setHandler(this);
		// GeneralManager.get().getEventPublisher().addListener(
		// CompareGroupsEvent.class, compareGroupsEventListener);

		statisticsPValueReductionListener = new StatisticsPValueReductionListener();
		statisticsPValueReductionListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(
				StatisticsPValueReductionEvent.class, statisticsPValueReductionListener);

		statisticsFoldChangeReductionListener = new StatisticsFoldChangeReductionListener();
		statisticsFoldChangeReductionListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(
				StatisticsFoldChangeReductionEvent.class,
				statisticsFoldChangeReductionListener);
		
		statisticsTwoSidedTTestReductionListener = new StatisticsTwoSidedTTestReductionListener();
		statisticsTwoSidedTTestReductionListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(
				StatisticsTwoSidedTTestReductionEvent.class,
				statisticsTwoSidedTTestReductionListener);
		
	}

	// TODO: never called!
	public void unregisterEventListeners() {

		// if (compareGroupsEventListener != null) {
		// GeneralManager.get().getEventPublisher().removeListener(
		// compareGroupsEventListener);
		// compareGroupsEventListener = null;
		// }

		if (statisticsPValueReductionListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(
					statisticsPValueReductionListener);
			statisticsPValueReductionListener = null;
		}

		if (statisticsFoldChangeReductionListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(
					statisticsFoldChangeReductionListener);
			statisticsFoldChangeReductionListener = null;
		}
		
		if (statisticsTwoSidedTTestReductionListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(
					statisticsTwoSidedTTestReductionListener);
			statisticsTwoSidedTTestReductionListener = null;
		}
	}

	@Override
	public void performTest() {

		try {
			REXP test;
			int[] array = new int[] { 223, 259, 248, 220, 287, 191, 229, 270, 245, 201 };// 5,
			// 6,
			// 7};
			int[] array_2 = new int[] { 220, 244, 243, 211, 299, 170, 210, 276, 252, 189 };// 1,
			// 2,
			// 3};
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

		// Do nothing if the operations was already performed earlier
//		if (set1.getStatisticsResult().getFoldChangeResult(set2) != null
//				&& set2.getStatisticsResult().getFoldChangeResult(set1) != null)
//			return;

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

	public void oneSidedTTest(ArrayList<ISet> sets) {

//		OpenViewEvent openViewEvent  = new OpenViewEvent();
//		openViewEvent.setViewType("org.caleydo.view.statistics");
//		openViewEvent.setSender(this);
//		GeneralManager.get().getEventPublisher().triggerEvent(openViewEvent);
		
		boolean allCalculated = true;
		for (ISet set : sets) {
			if(set.getStatisticsResult().getOneSidedTTestResult() == null)
				continue;
			
			allCalculated = false;
		}
		
		if (!allCalculated)
			return;
		
		for (ISet set : sets) {
			double[] pValueVector = new double[set.getContentVA(ContentVAType.CONTENT)
					.size()];

			for (int contentIndex = 0; contentIndex < set.getContentVA(ContentVAType.CONTENT).size(); contentIndex++) {

				StorageVirtualArray storageVA1 = set.getStorageVA(StorageVAType.STORAGE);

				double[] compareVec1 = new double[storageVA1.size()];

				int storageCount = 0;
				for (Integer storageIndex : storageVA1) {
					compareVec1[storageCount++] = set.get(storageIndex).getFloat(
							EDataRepresentation.RAW, contentIndex);
				}

				engine.assign("set", compareVec1);

				REXP compareResult = engine.eval("t.test(set)");
				
				//System.out.println(compareVec1[0] + " " + compareVec1[1] + " " +compareVec1[2]);
				
				// If all values in the vector are the same R returns null
				if (compareResult == null)
					pValueVector[contentIndex] = 0;
				else {
					REXP pValue = (REXP) compareResult.asVector().get(2);
					pValueVector[contentIndex] = pValue.asDouble();
					// System.out.println(pValue.asDouble());					
				}
			}

			set.getStatisticsResult().setOneSiddedTTestResult(pValueVector);
		}
		
		StatisticsResultFinishedEvent event = new StatisticsResultFinishedEvent(sets);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
		
		System.out.println("One-sided t-test finished");
	}

	public void twoSidedTTest(ArrayList<ISet> sets) {
		
		// Perform t-test between all neighboring sets (A<->B<->C)
//		for (int setIndex = 0; setIndex < sets.size(); setIndex++) {
//
//			if (setIndex + 1 == sets.size())
//				break;

			ISet set1 = sets.get(0);
			ISet set2 = sets.get(1);

			ArrayList<Double> pValueVector = new ArrayList<Double>();

			for (int contentIndex = 0; contentIndex < set1.get(
					set1.getStorageVA(StorageVAType.STORAGE).get(0)).size(); contentIndex++) {

				StorageVirtualArray storageVA1 = set1.getStorageVA(StorageVAType.STORAGE);
				StorageVirtualArray storageVA2 = set2.getStorageVA(StorageVAType.STORAGE);

				double[] compareVec1 = new double[storageVA1.size()];
				double[] compareVec2 = new double[storageVA2.size()];

				int storageCount = 0;
				for (Integer storageIndex : storageVA1) {
					compareVec1[storageCount++] = set1.get(storageIndex).getFloat(
							EDataRepresentation.RAW, contentIndex);
				}

				storageCount = 0;
				for (Integer storageIndex : storageVA2) {
					compareVec2[storageCount++] = set2.get(storageIndex).getFloat(
							EDataRepresentation.RAW, contentIndex);
				}

				engine.assign("set_1", compareVec1);
				engine.assign("set_2", compareVec2);

				REXP compareResult = engine.eval("t.test(set_1,set_2)");

				// System.out.println("T-Test result: " + compareResult);

				REXP pValue = (REXP) compareResult.asVector().get(2);
				pValueVector.add(pValue.asDouble());
				// System.out.println(pValue.asDouble());
			}

			set1.getStatisticsResult().setTwoSiddedTTestResult(set2, pValueVector);
			set2.getStatisticsResult().setTwoSiddedTTestResult(set1, pValueVector);
//		}

		// setsToCompare.get(0).getStatisticsResult().getVABasedOnCompareResult(setsToCompare.get(1),
		// 0.9f);
		
		StatisticsResultFinishedEvent event = new StatisticsResultFinishedEvent(sets);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		System.out.println("Two-sided t-test finished");
	}

	@Override
	public synchronized void queueEvent(
			AEventListener<? extends IListenerOwner> listener, AEvent event) {

		if (event instanceof StatisticsPValueReductionEvent)
			statisticsPValueReductionListener.handleEvent(event);
		else if (event instanceof StatisticsFoldChangeReductionEvent)
			statisticsFoldChangeReductionListener.handleEvent(event);
		else if (event instanceof StatisticsTwoSidedTTestReductionEvent)
			statisticsTwoSidedTTestReductionListener.handleEvent(event);
	}

	public void handleFoldChangeEvent(final ISet set1, final ISet set2) {

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				new FoldChangeDialog(new Shell(),
						set1, set2).open();
			}
		});
	}
}
