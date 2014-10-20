/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.fisher;

import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.view.enroute.correlation.ASelectDataCellPage;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.EndCorrelationCalculationEvent;
import org.caleydo.view.enroute.correlation.IDataClassifier;
import org.caleydo.view.enroute.correlation.StartCorrelationCalculationEvent;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.Lists;

/**
 * @author Christian
 *
 */
public class FishersExactTestWizard extends Wizard {

	protected ASelectDataCellPage firstDataCellPage;
	protected ASelectDataCellPage secondDataCellPage;

	protected DataCellInfo info1;
	protected DataCellInfo info2;
	protected IDataClassifier cell1Classifier;
	protected IDataClassifier cell2Classifier;

	/**
	 *
	 */
	public FishersExactTestWizard() {
		setWindowTitle("Test Significance of Association using Fisher's Exact Test");
	}

	@Override
	public void addPages() {
		// List<Color> allColors = ColorBrewer.Set3.get(5);
		List<Color> seq1 = ColorBrewer.Oranges.get(3);
		List<Color> seq2 = ColorBrewer.Blues.get(3);

		firstDataCellPage = new FishersSelectDataCellPage("FirstBlock", "Select First Data Block", null,
				Lists.newArrayList(
				seq1.get(0), seq1.get(2)));
		// firstDataCellPage
		// .setDescription("With Fisher's Exact Test you can evaluate the statistical significance of a correlation based on a contingency table. You will divide the data by selecting two data blocks in enRoute that you want to compare, and by setting thresholds in these blocks.\n\n"
		// + "To start, click on the first data block that you want to compare now.");
		secondDataCellPage = new FishersSelectDataCellPage("SecondBlock", "Select Second Data Block", null,
				Lists.newArrayList(seq2.get(0), seq2.get(2)));
		FishersExactTestResultPage resultPage = new FishersExactTestResultPage("Result",
				"Resulting Contingency Table and P-Values", null);

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
	public void createPageControls(Composite pageContainer) {
		EventPublisher.trigger(new StartCorrelationCalculationEvent());
		super.createPageControls(pageContainer);
	}

	@Override
	public boolean performFinish() {
		EventPublisher.trigger(new EndCorrelationCalculationEvent());
		return true;
	}

	@Override
	public boolean performCancel() {
		EventPublisher.trigger(new EndCorrelationCalculationEvent());
		return super.performCancel();
	}

	public void setPageInfo(ASelectDataCellPage page, DataCellInfo info, IDataClassifier classifier) {
		if (page == firstDataCellPage) {
			info1 = info;
			cell1Classifier = classifier;
		} else if (page == secondDataCellPage) {
			info2 = info;
			cell2Classifier = classifier;
		}
	}

	public boolean isFirstPage(ASelectDataCellPage page) {
		return page == firstDataCellPage;
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
