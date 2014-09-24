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
import org.caleydo.view.enroute.correlation.SimpleCategory;
import org.caleydo.view.enroute.correlation.SimpleIDClassifier;
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

	protected WilcoxonMethodSelectionPage methodSelectionPage;

	protected ASelectDataCellPage manualSourceDataCellPage;
	protected WilcoxonManualTargetDataCellPage manualTargetDataCellPage;
	protected WilcoxonManualResultPage manualResultPage;

	protected WilcoxonAutoSourceDataCellPage autoSourceDataCellPage;
	protected WilcoxonAutoTargetDataCellPage autoTargetDataCellPage;
	protected WilcoxonAutoResultPage autoResultPage;

	protected DataCellInfo sourceInfo;
	protected DataCellInfo targetInfo;
	protected IDataClassifier cell1Classifier;
	protected SimpleIDClassifier derivedIDClassifier;

	/**
	 *
	 */
	public WilcoxonRankSumTestWizard() {
		setWindowTitle("Calculate Data Correlation using the Wilcoxon Rank-Sum Test");
	}

	@Override
	public void addPages() {
		// List<Color> allColors = ColorBrewer.Set3.get(5);
		List<Color> oranges = ColorBrewer.Oranges.get(3);
		List<Color> blues = ColorBrewer.Blues.get(3);

		methodSelectionPage = new WilcoxonMethodSelectionPage("Method", "Select the method for applying the test", null);
		manualSourceDataCellPage = new WilcoxonManualSourceDataCellPage("FirstBlock", "Select First Data Block", null,
				Lists.newArrayList(oranges.get(0), oranges.get(2)));
		manualTargetDataCellPage = new WilcoxonManualTargetDataCellPage("SecondBlock", "Select Second Data Block", null);
		manualResultPage = new WilcoxonManualResultPage("Results", "Summary and Results", null);

		autoSourceDataCellPage = new WilcoxonAutoSourceDataCellPage("FirstAutoBlock", "Select First Data Block", null,
				oranges.get(0));
		autoTargetDataCellPage = new WilcoxonAutoTargetDataCellPage("SecondAutoBlock", "Select Second Data Block",
				null, blues.get(0));
		autoResultPage = new WilcoxonAutoResultPage("AutoResults", "Summary and Results", null);

		IWizardContainer wizardContainer = getContainer();
		if (wizardContainer instanceof IPageChangeProvider) {
			IPageChangeProvider pageChangeProvider = (IPageChangeProvider) wizardContainer;
			pageChangeProvider.addPageChangedListener(methodSelectionPage);
			pageChangeProvider.addPageChangedListener(manualSourceDataCellPage);
			pageChangeProvider.addPageChangedListener(manualTargetDataCellPage);
			pageChangeProvider.addPageChangedListener(manualResultPage);
			pageChangeProvider.addPageChangedListener(autoSourceDataCellPage);
			pageChangeProvider.addPageChangedListener(autoTargetDataCellPage);
			pageChangeProvider.addPageChangedListener(autoResultPage);
		}

		addPage(methodSelectionPage);
		addPage(manualSourceDataCellPage);
		addPage(manualTargetDataCellPage);
		addPage(manualResultPage);
		addPage(autoSourceDataCellPage);
		addPage(autoTargetDataCellPage);
		addPage(autoResultPage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
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

	public void setSourceInfo(DataCellInfo info) {
		sourceInfo = info;
	}

	public void setSourceInfo(DataCellInfo info, IDataClassifier classifier) {
		sourceInfo = info;
		cell1Classifier = classifier;
		List<SimpleCategory> classes = classifier.getDataClasses();
		List<Set<Object>> idSets = new ArrayList<>(classes.size());
		// There will be no more than 2
		idSets.add(new HashSet<>());
		idSets.add(new HashSet<>());
		for (int columnID : sourceInfo.columnPerspective.getVirtualArray()) {
			Object value = sourceInfo.dataDomain.getRaw(sourceInfo.columnPerspective.getIdType(), columnID,
					sourceInfo.rowIDType, sourceInfo.rowID);
			SimpleCategory c = classifier.apply(value);
			if (c != null) {
				Set<Object> idSet = idSets.get(classes.indexOf(c));
				idSet.add(columnID);
			}
		}

		List<Color> blues = ColorBrewer.Blues.get(3);

		SimpleCategory class1 = new SimpleCategory(classes.get(0).name + " in first data block", blues.get(0));
		SimpleCategory class2 = new SimpleCategory(classes.get(1).name + " in first data block", blues.get(2));

		derivedIDClassifier = new SimpleIDClassifier(idSets.get(0), idSets.get(1),
				sourceInfo.columnPerspective.getIdType(), class1, class2);
	}

	/**
	 * @return the sourceInfo, see {@link #sourceInfo}
	 */
	public DataCellInfo getSourceInfo() {
		return sourceInfo;
	}

	/**
	 * @return the targetInfo, see {@link #targetInfo}
	 */
	public DataCellInfo getTargetInfo() {
		return targetInfo;
	}

	/**
	 * @param targetInfo
	 *            setter, see {@link targetInfo}
	 */
	public void setTargetInfo(DataCellInfo targetInfo) {
		this.targetInfo = targetInfo;
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
	public SimpleIDClassifier getDerivedIDClassifier() {
		return derivedIDClassifier;
	}

	/**
	 * @return the manualResultPage, see {@link #manualResultPage}
	 */
	public WilcoxonManualResultPage getManualResultPage() {
		return manualResultPage;
	}

	/**
	 * @return the manualSourceDataCellPage, see {@link #manualSourceDataCellPage}
	 */
	public ASelectDataCellPage getManualSourceDataCellPage() {
		return manualSourceDataCellPage;
	}

	/**
	 * @return the manualTargetDataCellPage, see {@link #manualTargetDataCellPage}
	 */
	public WilcoxonManualTargetDataCellPage getManualTargetDataCellPage() {
		return manualTargetDataCellPage;
	}

	/**
	 * @return the methodSelectionPage, see {@link #methodSelectionPage}
	 */
	public WilcoxonMethodSelectionPage getMethodSelectionPage() {
		return methodSelectionPage;
	}

	/**
	 * @return the autoResultPage, see {@link #autoResultPage}
	 */
	public WilcoxonAutoResultPage getAutoResultPage() {
		return autoResultPage;
	}

	/**
	 * @return the autoSourceDataCellPage, see {@link #autoSourceDataCellPage}
	 */
	public WilcoxonAutoSourceDataCellPage getAutoSourceDataCellPage() {
		return autoSourceDataCellPage;
	}

	/**
	 * @return the autoTargetDataCellPage, see {@link #autoTargetDataCellPage}
	 */
	public WilcoxonAutoTargetDataCellPage getAutoTargetDataCellPage() {
		return autoTargetDataCellPage;
	}

}
