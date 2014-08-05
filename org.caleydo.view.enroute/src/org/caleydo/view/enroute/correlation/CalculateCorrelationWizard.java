/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;

/**
 * @author Christian
 *
 */
public class CalculateCorrelationWizard extends Wizard {

	protected SelectDataCellPage firstDataCellPage;
	protected SelectDataCellPage secondDataCellPage;

	protected DataCellInfo info1;
	protected DataCellInfo info2;
	protected IDataClassifier cell1Classifier;
	protected IDataClassifier cell2Classifier;

	@Override
	public void addPages() {
		firstDataCellPage = new SelectDataCellPage("Select First Data Cell");
		secondDataCellPage = new SelectDataCellPage("Select Second Data Cell");
		CorrelationResultPage resultPage = new CorrelationResultPage("Result");

		IWizardContainer wizardContainer = getContainer();
		if (wizardContainer instanceof IPageChangeProvider) {
			IPageChangeProvider pageChangeProvider = (IPageChangeProvider) wizardContainer;
			pageChangeProvider.addPageChangedListener(firstDataCellPage);
			pageChangeProvider.addPageChangedListener(secondDataCellPage);
			pageChangeProvider.addPageChangedListener(resultPage);
		}

		addPage(firstDataCellPage);
		addPage(secondDataCellPage);
		addPage(resultPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public void setPageInfo(SelectDataCellPage page, DataCellInfo info, IDataClassifier classifier) {
		if (page == firstDataCellPage) {
			info1 = info;
			cell1Classifier = classifier;
		} else if (page == secondDataCellPage) {
			info2 = info;
			cell2Classifier = classifier;
		}
	}

	/**
	 * @param info1
	 *            setter, see {@link info1}
	 */
	public void setInfo1(DataCellInfo info1) {
		this.info1 = info1;
	}

	/**
	 * @return the info1, see {@link #info1}
	 */
	public DataCellInfo getInfo1() {
		return info1;
	}

	/**
	 * @param info2
	 *            setter, see {@link info2}
	 */
	public void setInfo2(DataCellInfo info2) {
		this.info2 = info2;
	}

	/**
	 * @return the info2, see {@link #info2}
	 */
	public DataCellInfo getInfo2() {
		return info2;
	}

	/**
	 * @return the cell1Classifier, see {@link #cell1Classifier}
	 */
	public IDataClassifier getCell1Classifier() {
		return cell1Classifier;
	}

	/**
	 * @return the cell2Classifier, see {@link #cell2Classifier}
	 */
	public IDataClassifier getCell2Classifier() {
		return cell2Classifier;
	}

}
