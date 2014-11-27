/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.TiesStrategy;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.SimpleIDClassifier;
import org.caleydo.view.enroute.correlation.wilcoxon.WilcoxonUtil.WilcoxonResult;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Christian
 *
 */
public class WilcoxonManualResultPage extends WizardPage implements IPageChangedListener {

	private WilcoxonResultsWidget resultsWidget;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected WilcoxonManualResultPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {

		resultsWidget = new WilcoxonResultsWidget(parent, false);
		setControl(resultsWidget);

	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == this) {
			WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
			DataCellInfo targetInfo = wizard.getTargetInfo();

			// java.util.List<WilcoxonResult> resultList = WilcoxonUtil.applyWilcoxonToAllElements(
			// wizard.getSourceClassifier(), wizard.getSourceInfo(), targetInfo.columnPerspective);
			//
			// for (WilcoxonResult r : resultList) {
			// System.out.println(r.p);
			// }
			// System.out.println("NumElements: " + resultList.size());

			// WilcoxonUtil.calcWilcoxonRankSumTest(sourceInfo, classifier, targetInfo)

			SimpleIDClassifier derivedClassifier = wizard.getDerivedIDClassifier();
			MannWhitneyUTest test = new MannWhitneyUTest(NaNStrategy.REMOVED, TiesStrategy.AVERAGE);
			double[] values1 = WilcoxonUtil.getSampleValuesArray(targetInfo, derivedClassifier.getClass1IDs());
			double[] values2 = WilcoxonUtil.getSampleValuesArray(targetInfo, derivedClassifier.getClass2IDs());

			double u = test.mannWhitneyU(values1, values2);
			double p = test.mannWhitneyUTest(values1, values2);

			resultsWidget.update(wizard.getSourceInfo(), targetInfo,
					new WilcoxonResult(p, u, wizard.getSourceClassifier(), derivedClassifier));

			// resultsWidget.updateClassSummary(0, values1, derivedClassifier.getDataClasses().get(0));
			// resultsWidget.updateClassSummary(1, values2, derivedClassifier.getDataClasses().get(1));
			//
			// resultsWidget.updateStatistics(u, p);
		}

	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((WilcoxonRankSumTestWizard) getWizard()).getManualTargetDataCellPage();
	}

	@Override
	public IWizardPage getNextPage() {
		return null;
	}
}
