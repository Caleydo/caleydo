/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.util.r;

import java.util.ArrayList;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.HistogramCreator;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.data.StatisticsFoldChangeReductionEvent;
import org.caleydo.core.event.data.StatisticsPValueReductionEvent;
import org.caleydo.core.event.data.StatisticsTwoSidedTTestReductionEvent;
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

public class RStatisticsPerformer implements IStatisticsPerformer, IListenerOwner {
	private static final Logger log = Logger.create(RStatisticsPerformer.class);

	private Rengine engine;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	public RStatisticsPerformer() {

		init();
		registerEventListeners();
	}

	@Override
	public void init() {
		// just making sure we have the right version of everything

		// Map<String, String> env = System.getenv();
		// if(!env.containsKey("R_HOME")) {
		// throw new RuntimeException(
		// "Could not instantiate R Statistics Peformer");
		// }
		//
		// Properties properties = System.getProperties();
		//
		// String path = properties.getProperty("java.library.path");
		if (!Rengine.versionCheck()) {
			log.error("** Version mismatch - Java files don't match library version.");
			return;
		}

		log.info("Creating Rengine (with arguments)");
		String[] args = new String[1];
		args[0] = "--no-save";
		engine = new Rengine(args, false, new RConsole());
		log.info("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until
		// it's
		// ready
		if (!engine.waitForR()) {
			log.error("Cannot load R");
			return;
		}
	}

	@Override
	public void registerEventListeners() {
		listeners.register(StatisticsPValueReductionEvent.class, new StatisticsPValueReductionListener(this));
		listeners.register(StatisticsFoldChangeReductionEvent.class, new StatisticsFoldChangeReductionListener(this));
		listeners.register(StatisticsTwoSidedTTestReductionEvent.class, new StatisticsTwoSidedTTestReductionListener(
				this));
	}

	// TODO: never called!
	@Override
	public void unregisterEventListeners() {
		listeners.unregisterAll();
	}

	@Override
	public void performTest() {

		try {
			REXP test;
			int[] array = new int[] { 223, 259, 248, 220, 287, 191, 229, 270, 245, 201 };// 5,
			int[] array_2 = new int[] { 220, 244, 243, 211, 299, 170, 210, 276, 252, 189 };// 1,

			engine.assign("my_array", array);
			engine.assign("my_array_2", array_2);

			System.out.println("Array: " + engine.eval("my_array"));
			System.out.println("Array 2: " + engine.eval("my_array_2"));

			test = engine.eval("t.test(my_array,my_array_2)");
			System.out.println("T-Test result: " + test);

		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, toString(), "Could not run R commands", e));
		}

		// OpenViewEvent openViewEvent = new OpenViewEvent();
		// openViewEvent.setViewType("org.caleydo.view.statistics");
		// openViewEvent.setSender(this);
		// GeneralManager.get().getEventPublisher().triggerEvent(openViewEvent);
	}

	/**
	 * <p>
	 * Computation of fold change between two data containers. Implemented to imitate R behavior:
	 * </p>
	 * <p>
	 * Fold changes are commonly used in the biological sciences as a mechanism for comparing the relative size of two
	 * measurements. They are computed as: num/denom if num>denom, and as -denom/num otherwise.
	 * </p>
	 */
	@Override
	public synchronized void foldChange(TablePerspective container1, TablePerspective container2, boolean betweenRecords) {

		// Do nothing if the operations was already performed earlier
		// if (set1.getStatisticsResult().getFoldChangeResult(set2) != null
		// && set2.getStatisticsResult().getFoldChangeResult(set1) != null)
		// return;

		if (!container1.getRecordPerspective().equals(container2.getRecordPerspective())) {
			throw new IllegalArgumentException("The RecordPerspectives have to be the same");
		}

		ArrayList<Average> averageRecords1 = container1.getContainerStatistics().getAverageRecords();
		ArrayList<Average> averageRecords2 = container2.getContainerStatistics().getAverageRecords();
		double[] resultVec = new double[averageRecords1.size()];

		for (int count = 0; count < container1.getNrRecords(); count++) {
			double mean1 = averageRecords1.get(count).getArithmeticMean();
			double mean2 = averageRecords2.get(count).getArithmeticMean();
			if (mean1 > mean2)
				resultVec[count] = mean1 / mean2;
			else
				resultVec[count] = mean2 * -1 / mean1;
		}

		container1.getContainerStatistics().getFoldChange().setResult(container2, resultVec);
		container2.getContainerStatistics().getFoldChange().setResult(container1, resultVec);

		// double[] meanDimension1 = new double[meanDimensionVec1.size()];
		// for (int recordIndex = 0; recordIndex < meanDimensionVec1.size();
		// recordIndex++) {
		// meanDimension1[recordIndex] = meanDimensionVec1.getFloat(
		// DataRepresentation.RAW, recordIndex);
		// }
		//
		// double[] meanDimension2 = new double[meanDimensionVec2.size()];
		// for (int recordIndex = 0; recordIndex < meanDimensionVec2.size();
		// recordIndex++) {
		// meanDimension2[recordIndex] = meanDimensionVec2.getFloat(
		// DataRepresentation.RAW, recordIndex);
		// }

		// engine.assign("set_1", meanDimension1);
		// engine.assign("set_2", meanDimension2);
		// engine.eval("library(\"gtools\")");
		// REXP foldChangeResult = engine.eval("foldchange(set_1,set_2)");
		// // System.out.println("Fold change result: " + foldChangeResult);
		//
		// double[] resultVec = foldChangeResult.asDoubleArray();
		//
		// table1.getStatisticsResult().setResult(table2, resultVec);
		// table2.getStatisticsResult().setResult(table1, resultVec);
		//
		// // FIXME: just for uncertainty paper so that the uncertainty view can
		// // access it via the main set
		// Table table = table1.getDataDomain().getTable();
		// table.getStatisticsResult().setResult(table1, resultVec);

		Filter contentFilter = new Filter(container1.getRecordPerspective().getPerspectiveID());
		contentFilter.setDataDomain(container1.getDataDomain());
		contentFilter.setLabel("Fold change " + container1.getLabel() + " and " + container2.getLabel());

		Histogram histogram = HistogramCreator.createLogHistogram(resultVec);
		// Histogram histogram = HistogramCreator.createHistogram(resultVec);

		FilterRepresentationFoldChange filterRep = new FilterRepresentationFoldChange();
		filterRep.setFilter(contentFilter);
		filterRep.setDataDomain(container1.getDataDomain());
		filterRep.setTablePerspective1(container1);
		filterRep.setTablePerspective2(container2);
		filterRep.setHistogram(histogram);
		contentFilter.setFilterRep(filterRep);
		contentFilter.openRepresentation();
	}

	public void oneSidedTTest(ArrayList<TablePerspective> tablePerspectives) {

		// TODO: don't recalculate if pvalue array is already calculated
		// however, the evaluation must be done using the new pvalue
		// boolean allCalculated = true;
		// for (Table set : sets) {
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

		for (TablePerspective container : tablePerspectives) {

			VirtualArray recordVA = container.getRecordPerspective().getVirtualArray();
			VirtualArray dimensionVA1 = container.getDimensionPerspective().getVirtualArray();
			Table table = container.getDataDomain().getTable();
			// getContentData(Table.CONTENT).getRecordVA();

			double[] pValueVector = new double[container.getNrRecords()];
			// table.getContentData(Table.CONTENT).getRecordVA().size()];

			for (int recordIndex = 0; recordIndex < recordVA.size(); recordIndex++) {

				double[] compareVec1 = new double[dimensionVA1.size()];

				int dimensionCount = 0;
				for (Integer dimensionIndex : dimensionVA1) {
					compareVec1[dimensionCount++] = (double) table.getRaw(recordIndex, dimensionIndex);
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

			container.getContainerStatistics().getTTest().setOneSiddedTTestResult(pValueVector);

			Filter contentFilter = new Filter(container.getRecordPerspective().getPerspectiveID());
			contentFilter.setDataDomain(container.getDataDomain());
			contentFilter.setLabel("p-Value Reduction of " + container.getLabel());

			Histogram histogram = HistogramCreator.createHistogram(pValueVector);

			// if (metaFilter != null) {
			// metaFilter.getFilterList().add(contentFilter);
			// metaFilter.setDataDomain(table.getDataDomain());
			// } else {
			FilterRepresentationPValue filterRep = new FilterRepresentationPValue();
			filterRep.setFilter(contentFilter);
			filterRep.setTablePerspective1(container);
			filterRep.setHistogram(histogram);
			contentFilter.setFilterRep(filterRep);
			contentFilter.openRepresentation();
			// }
		}

		// if (metaFilter != null)
		// metaFilter.openRepresentation();
	}

	/**
	 * FIXME this uses only the first two!
	 */
	@Override
	public void twoSidedTTest(ArrayList<TablePerspective> tablePerspectives) {

		// Perform t-test between all neighboring sets (A<->B<->C)
		// for (int setIndex = 0; setIndex < sets.size(); setIndex++) {
		//
		// if (setIndex + 1 == sets.size())
		// break;

		TablePerspective tablePerspective1 = tablePerspectives.get(0);
		TablePerspective tablePerspective2 = tablePerspectives.get(1);

		if (!tablePerspective1.getRecordPerspective().equals(tablePerspective2.getRecordPerspective()))
			throw new IllegalStateException("data containers have to share record prespective");

		Table table = tablePerspective1.getDataDomain().getTable();

		ArrayList<Double> pValueVector = new ArrayList<Double>();

		for (int recordIndex = 0; recordIndex < tablePerspective1.getNrRecords(); recordIndex++) {

			VirtualArray dimensionVA1 = tablePerspective1.getDimensionPerspective().getVirtualArray();
			VirtualArray dimensionVA2 = tablePerspective2.getDimensionPerspective().getVirtualArray();

			double[] compareVec1 = new double[dimensionVA1.size()];
			double[] compareVec2 = new double[dimensionVA2.size()];

			int dimensionCount = 0;
			for (Integer dimensionIndex : dimensionVA1) {
				compareVec1[dimensionCount++] = table.getRaw(recordIndex, dimensionIndex);
			}

			dimensionCount = 0;
			for (Integer dimensionIndex : dimensionVA2) {
				compareVec2[dimensionCount++] = table.getRaw(recordIndex, dimensionIndex);
			}

			engine.assign("set_1", compareVec1);
			engine.assign("set_2", compareVec2);

			REXP compareResult = engine.eval("t.test(set_1,set_2)");

			// System.out.println("T-Test result: " + compareResult);

			REXP pValue = (REXP) compareResult.asVector().get(2);
			pValueVector.add(pValue.asDouble());
			// System.out.println(pValue.asDouble());
		}

		tablePerspective1.getContainerStatistics().getTTest().setTwoSiddedTTestResult(tablePerspective2, pValueVector);
		tablePerspective2.getContainerStatistics().getTTest().setTwoSiddedTTestResult(tablePerspective1, pValueVector);

		Filter contentFilter = new Filter(tablePerspective1.getRecordPerspective().getPerspectiveID());
		contentFilter.setDataDomain(tablePerspective1.getDataDomain());
		contentFilter.setLabel("Two sided t-test of " + tablePerspective1.getLabel() + " and "
				+ tablePerspective2.getLabel());

		FilterRepresentationTwoSidedTTest filterRep = new FilterRepresentationTwoSidedTTest();
		filterRep.setFilter(contentFilter);
		filterRep.setTablePerspective1(tablePerspective1);
		filterRep.setTablePerspective2(tablePerspective2);
		contentFilter.setFilterRep(filterRep);
		contentFilter.openRepresentation();
	}

	@Override
	public synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		listener.handleEvent(event);
	}
}
