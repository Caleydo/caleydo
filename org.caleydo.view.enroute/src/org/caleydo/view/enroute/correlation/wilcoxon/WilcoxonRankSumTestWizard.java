/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.view.enroute.correlation.ASelectDataCellPage;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.EndCorrelationCalculationEvent;
import org.caleydo.view.enroute.correlation.IDataClassifier;
import org.caleydo.view.enroute.correlation.IIDClassifier;
import org.caleydo.view.enroute.correlation.SimpleCategory;
import org.caleydo.view.enroute.correlation.SimpleIDClassifier;
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
public class WilcoxonRankSumTestWizard extends Wizard {

	protected ASelectDataCellPage sourceDataCellPage;
	protected WilcoxonSelectTargetDataCellPage targetDataCellPage;

	protected DataCellInfo info1;
	protected DataCellInfo info2;
	protected IDataClassifier cell1Classifier;
	protected IIDClassifier derivedIDClassifier;

	/**
	 *
	 */
	public WilcoxonRankSumTestWizard() {
		setWindowTitle("Calculate Data Correlation using the Wilcoxon Rank-Sum Test");
	}

	@Override
	public void addPages() {
		// List<Color> allColors = ColorBrewer.Set3.get(5);
		List<Color> seq1 = ColorBrewer.Oranges.get(3);

		sourceDataCellPage = new WilcoxonSelectSourceDataCellPage("FirstBlock", "Select First Data Block", null,
				Lists.newArrayList(seq1.get(0), seq1.get(2)));
		targetDataCellPage = new WilcoxonSelectTargetDataCellPage("SecondBlock", "Select Second Data Block", null);
		WilcoxonResultPage resultPage = new WilcoxonResultPage("Results", "Results", null);

		IWizardContainer wizardContainer = getContainer();
		if (wizardContainer instanceof IPageChangeProvider) {
			IPageChangeProvider pageChangeProvider = (IPageChangeProvider) wizardContainer;
			pageChangeProvider.addPageChangedListener(sourceDataCellPage);
			pageChangeProvider.addPageChangedListener(targetDataCellPage);
			pageChangeProvider.addPageChangedListener(resultPage);
		}

		addPage(sourceDataCellPage);
		addPage(targetDataCellPage);
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

	public void setSourcePageInfo(DataCellInfo info, IDataClassifier classifier) {
		info1 = info;
		cell1Classifier = classifier;
		List<SimpleCategory> classes = classifier.getDataClasses();
		List<Set<Object>> idSets = new ArrayList<>(classes.size());
		// There will be no more than 2
		idSets.add(new HashSet<>());
		idSets.add(new HashSet<>());
		for (int columnID : info1.columnPerspective.getVirtualArray()) {
			Object value = info1.dataDomain.getRaw(info1.columnPerspective.getIdType(), columnID, info1.rowIDType,
					info1.rowID);
			SimpleCategory c = classifier.apply(value);
			if (c != null) {
				Set<Object> idSet = idSets.get(classes.indexOf(c));
				idSet.add(columnID);
			}
		}

		List<Color> blues = ColorBrewer.Blues.get(3);

		SimpleCategory class1 = new SimpleCategory(classes.get(0).name + " in first data block", blues.get(0));
		SimpleCategory class2 = new SimpleCategory(classes.get(1).name + " in first data block", blues.get(2));

		derivedIDClassifier = new SimpleIDClassifier(idSets.get(0), idSets.get(1), info1.columnPerspective.getIdType(),
				class1, class2);
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
	 * @return the derivedIDClassifier, see {@link #derivedIDClassifier}
	 */
	public IIDClassifier getDerivedIDClassifier() {
		return derivedIDClassifier;
	}

}
