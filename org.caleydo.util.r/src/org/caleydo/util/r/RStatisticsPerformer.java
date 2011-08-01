package org.caleydo.util.r;

import java.util.ArrayList;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.HistogramCreator;
import org.caleydo.core.data.collection.dimension.DataRepresentation;
import org.caleydo.core.data.collection.dimension.NumericalDimension;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.filter.RecordFilter;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.StatisticsFoldChangeReductionEvent;
import org.caleydo.core.manager.event.data.StatisticsPValueReductionEvent;
import org.caleydo.core.manager.event.data.StatisticsTwoSidedTTestReductionEvent;
import org.caleydo.core.manager.event.view.OpenViewEvent;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.statistics.IStatisticsPerformer;
import org.caleydo.util.r.filter.FilterRepresentationFoldChange;
import org.caleydo.util.r.filter.FilterRepresentationPValue;
import org.caleydo.util.r.filter.FilterRepresentationTwoSidedTTest;
import org.caleydo.util.r.listener.StatisticsFoldChangeReductionListener;
import org.caleydo.util.r.listener.StatisticsPValueReductionListener;
import org.caleydo.util.r.listener.StatisticsTwoSidedTTestReductionListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class RStatisticsPerformer implements IStatisticsPerformer,
		IListenerOwner {

	private Rengine engine;

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

//			Map<String, String> env = System.getenv();
//			if(!env.containsKey("R_HOME")) {
//				throw new RuntimeException(
//				"Could not instantiate R Statistics Peformer");
//			}
//			
//			Properties properties = System.getProperties();
//			
//			String path = properties.getProperty("java.library.path");
			try {
				if (!Rengine.  versionCheck()) {
					System.err
							.println("** Version mismatch - Java files don't match library version.");
					System.exit(1);
				}	
			} catch (UnsatisfiedLinkError e) {
				System.out.println("R SUCKS");			}
			
			System.out.println("Creating Rengine (with arguments)");
			String[] args = new String[1];
			args[0] = "--no-save";
			engine = new Rengine(args, false, new RConsole());
			System.out.println("Rengine created, waiting for R");
			// the engine creates R is a new thread, so we should wait until
			// it's
			// ready
			if (!engine.waitForR()) {
				System.out.println("Cannot load R");
				return;
			}
	}

	@Override
	public void registerEventListeners() {

		statisticsPValueReductionListener = new StatisticsPValueReductionListener();
		statisticsPValueReductionListener.setHandler(this);
		GeneralManager
				.get()
				.getEventPublisher()
				.addListener(StatisticsPValueReductionEvent.class,
						statisticsPValueReductionListener);

		statisticsFoldChangeReductionListener = new StatisticsFoldChangeReductionListener();
		statisticsFoldChangeReductionListener.setHandler(this);
		GeneralManager
				.get()
				.getEventPublisher()
				.addListener(StatisticsFoldChangeReductionEvent.class,
						statisticsFoldChangeReductionListener);

		statisticsTwoSidedTTestReductionListener = new StatisticsTwoSidedTTestReductionListener();
		statisticsTwoSidedTTestReductionListener.setHandler(this);
		GeneralManager
				.get()
				.getEventPublisher()
				.addListener(StatisticsTwoSidedTTestReductionEvent.class,
						statisticsTwoSidedTTestReductionListener);
	}

	// TODO: never called!
	public void unregisterEventListeners() {

		if (statisticsPValueReductionListener != null) {
			GeneralManager.get().getEventPublisher()
					.removeListener(statisticsPValueReductionListener);
			statisticsPValueReductionListener = null;
		}

		if (statisticsFoldChangeReductionListener != null) {
			GeneralManager.get().getEventPublisher()
					.removeListener(statisticsFoldChangeReductionListener);
			statisticsFoldChangeReductionListener = null;
		}

		if (statisticsTwoSidedTTestReductionListener != null) {
			GeneralManager.get().getEventPublisher()
					.removeListener(statisticsTwoSidedTTestReductionListener);
			statisticsTwoSidedTTestReductionListener = null;
		}
	}

	@Override
	public void performTest() {

		try {
			REXP test;
			int[] array = new int[] { 223, 259, 248, 220, 287, 191, 229, 270,
					245, 201 };// 5,
			// 6,
			// 7};
			int[] array_2 = new int[] { 220, 244, 243, 211, 299, 170, 210, 276,
					252, 189 };// 1,
			// 2,
			// 3};
			engine.assign("my_array", array);
			engine.assign("my_array_2", array_2);

			System.out.println("Array: " + engine.eval("my_array"));
			System.out.println("Array 2: " + engine.eval("my_array_2"));
			test = engine.eval("t.test(my_array,my_array_2)");
			System.out.println("T-Test result: " + test);
		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, toString(),
					"Could not run R commands", e));
		}

		OpenViewEvent openViewEvent = new OpenViewEvent();
		openViewEvent.setViewType("org.caleydo.view.statistics");
		openViewEvent.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(openViewEvent);

	}

	public void foldChange(DataTable table1, DataTable table2) {

		// Do nothing if the operations was already performed earlier
		// if (set1.getStatisticsResult().getFoldChangeResult(set2) != null
		// && set2.getStatisticsResult().getFoldChangeResult(set1) != null)
		// return;

		NumericalDimension meanDimensionVec1 = table1.getMeanDimension();
		NumericalDimension meanDimensionVec2 = table2.getMeanDimension();

		double[] meanDimension1 = new double[meanDimensionVec1.size()];
		for (int recordIndex = 0; recordIndex < meanDimensionVec1.size(); recordIndex++) {
			meanDimension1[recordIndex] = meanDimensionVec1.getFloat(
					DataRepresentation.RAW, recordIndex);
		}

		double[] meanDimension2 = new double[meanDimensionVec2.size()];
		for (int recordIndex = 0; recordIndex < meanDimensionVec2.size(); recordIndex++) {
			meanDimension2[recordIndex] = meanDimensionVec2.getFloat(
					DataRepresentation.RAW, recordIndex);
		}

		engine.assign("set_1", meanDimension1);
		engine.assign("set_2", meanDimension2);
		engine.eval("library(\"gtools\")");
		REXP foldChangeResult = engine.eval("foldchange(set_1,set_2)");
		// System.out.println("Fold change result: " + foldChangeResult);

		double[] resultVec = foldChangeResult.asDoubleArray();

		table1.getStatisticsResult().setFoldChangeResult(table2, resultVec);
		table2.getStatisticsResult().setFoldChangeResult(table1, resultVec);

		// FIXME: just for uncertainty paper so that the uncertainty view can
		// access it via the main set
		DataTable table = table1.getDataDomain().getTable();
		table.getStatisticsResult().setFoldChangeResult(table1, resultVec);

		RecordFilter contentFilter = new RecordFilter();
		contentFilter.setDataDomain(table1.getDataDomain());
		contentFilter.setLabel("Fold change " + table1.getLabel() + " and "
				+ table2.getLabel());

		Histogram histogram = HistogramCreator.createLogHistogram(resultVec);
		// Histogram histogram = HistogramCreator.createHistogram(resultVec);

		FilterRepresentationFoldChange filterRep = new FilterRepresentationFoldChange();
		filterRep.setFilter(contentFilter);
		filterRep.setTable1(table1);
		filterRep.setTable2(table2);
		filterRep.setHistogram(histogram);
		contentFilter.setFilterRep(filterRep);
		contentFilter.openRepresentation();
	}

	public void oneSidedTTest(ArrayList<DataTable> sets) {

		// TODO: don't recalculate if pvalue array is already calculated
		// however, the evaluation must be done using the new pvalue
		// boolean allCalculated = true;
		// for (DataTable set : sets) {
		// if (table.getStatisticsResult().getOneSidedTTestResult() == null)
		// continue;
		//
		// allCalculated = false;
		// }
		//
		// if (!allCalculated)
		// return;

		// ContentMetaFilter metaFilter = null;
		// if (sets.size() > 1) {
		// metaFilter = new ContentMetaFilter();
		// metaFilter.setLabel("p-Value Reduction");
		// FilterRepresentationPValue filterRep = new
		// FilterRepresentationPValue();
		// filterRep.setFilter(metaFilter);
		// filterRep.create();
		// metaFilter.setFilterRep(filterRep);
		// }

		for (DataTable table : sets) {

			RecordVirtualArray recordVA = table.getBaseRecordVA();
			// getContentData(DataTable.CONTENT).getRecordVA();

			double[] pValueVector = new double[table.getBaseRecordVA().size()];
			// table.getContentData(DataTable.CONTENT).getRecordVA().size()];

			for (int recordIndex = 0; recordIndex < recordVA.size(); recordIndex++) {

				DimensionVirtualArray dimensionVA1 = table.getDimensionData(
						DataTable.DIMENSION).getDimensionVA();

				double[] compareVec1 = new double[dimensionVA1.size()];

				int dimensionCount = 0;
				for (Integer dimensionIndex : dimensionVA1) {
					compareVec1[dimensionCount++] = table.get(dimensionIndex)
							.getFloat(DataRepresentation.RAW, recordIndex);
				}

				engine.assign("set", compareVec1);

				REXP compareResult = engine.eval("t.test(set)");

				// System.out.println(compareVec1[0] + " " + compareVec1[1] +
				// " " +compareVec1[2]);

				// If all values in the vector are the same R returns null
				if (compareResult == null)
					pValueVector[recordIndex] = 0;
				else {
					REXP pValue = (REXP) compareResult.asVector().get(2);
					pValueVector[recordIndex] = pValue.asDouble();
					// System.out.println(pValue.asDouble());
				}
			}

			table.getStatisticsResult().setOneSiddedTTestResult(pValueVector);

			RecordFilter contentFilter = new RecordFilter();
			contentFilter.setDataDomain(table.getDataDomain());
			contentFilter.setLabel("p-Value Reduction of " + table.getLabel());

			Histogram histogram = HistogramCreator
					.createHistogram(pValueVector);

			// if (metaFilter != null) {
			// metaFilter.getFilterList().add(contentFilter);
			// metaFilter.setDataDomain(table.getDataDomain());
			// } else {
			FilterRepresentationPValue filterRep = new FilterRepresentationPValue();
			filterRep.setFilter(contentFilter);
			filterRep.setTable(table);
			filterRep.setHistogram(histogram);
			contentFilter.setFilterRep(filterRep);
			contentFilter.openRepresentation();
			// }
		}

		// if (metaFilter != null)
		// metaFilter.openRepresentation();
	}

	public void twoSidedTTest(ArrayList<DataTable> sets) {

		// Perform t-test between all neighboring sets (A<->B<->C)
		// for (int setIndex = 0; setIndex < sets.size(); setIndex++) {
		//
		// if (setIndex + 1 == sets.size())
		// break;

		DataTable set1 = sets.get(0);
		DataTable set2 = sets.get(1);

		ArrayList<Double> pValueVector = new ArrayList<Double>();

		for (int recordIndex = 0; recordIndex < set1.get(
				set1.getDimensionData(DataTable.DIMENSION).getDimensionVA()
						.get(0)).size(); recordIndex++) {

			DimensionVirtualArray dimensionVA1 = set1.getDimensionData(
					DataTable.DIMENSION).getDimensionVA();
			DimensionVirtualArray dimensionVA2 = set2.getDimensionData(
					DataTable.DIMENSION).getDimensionVA();

			double[] compareVec1 = new double[dimensionVA1.size()];
			double[] compareVec2 = new double[dimensionVA2.size()];

			int dimensionCount = 0;
			for (Integer dimensionIndex : dimensionVA1) {
				compareVec1[dimensionCount++] = set1.get(dimensionIndex)
						.getFloat(DataRepresentation.RAW, recordIndex);
			}

			dimensionCount = 0;
			for (Integer dimensionIndex : dimensionVA2) {
				compareVec2[dimensionCount++] = set2.get(dimensionIndex)
						.getFloat(DataRepresentation.RAW, recordIndex);
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

		RecordFilter contentFilter = new RecordFilter();
		contentFilter.setDataDomain(set1.getDataDomain());
		contentFilter.setLabel("Two sided t-test of " + set1.getLabel()
				+ " and " + set2.getLabel());

		FilterRepresentationTwoSidedTTest filterRep = new FilterRepresentationTwoSidedTTest();
		filterRep.setFilter(contentFilter);
		filterRep.setTable1(set1);
		filterRep.setTable2(set2);
		contentFilter.setFilterRep(filterRep);
		contentFilter.openRepresentation();
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
}
