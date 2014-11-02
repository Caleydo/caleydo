/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.view.enroute.correlation.ASelectDataCellPage;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.EndCorrelationCalculationEvent;
import org.caleydo.view.enroute.correlation.IDataClassifier;
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

	public static final Pair<Color, Color> CLASSIFICATION_COLORS_1 = Pair.make(ColorBrewer.Oranges.get(3).get(0),
			ColorBrewer.Oranges.get(3).get(2));
	public static final Pair<Color, Color> CLASSIFICATION_COLORS_2 = Pair.make(ColorBrewer.Blues.get(3).get(0),
			ColorBrewer.Blues.get(3).get(2));

	protected WilcoxonMethodSelectionPage methodSelectionPage;

	protected ASelectDataCellPage manualSourceDataCellPage;
	protected WilcoxonManualTargetDataCellPage manualTargetDataCellPage;
	protected WilcoxonManualResultPage manualResultPage;

	protected WilcoxonAutoSourceDataCellPage autoSourceDataCellPage;
	protected WilcoxonAutoTargetDataCellPage autoTargetDataCellPage;
	protected WilcoxonAutoResultPage autoResultPage;

	protected DataCellInfo sourceInfo;
	protected DataCellInfo targetInfo;
	protected IDataClassifier sourceClassifier;
	protected SimpleIDClassifier derivedIDClassifier;

	/**
	 *
	 */
	public WilcoxonRankSumTestWizard() {
		setWindowTitle("Test Significance of Association using the Wilcoxon Rank-Sum Test");
	}

	@Override
	public void addPages() {
		// List<Color> allColors = ColorBrewer.Set3.get(5);

		methodSelectionPage = new WilcoxonMethodSelectionPage("Method", "Select the method for applying the test", null);
		manualSourceDataCellPage = new WilcoxonManualSourceDataCellPage("FirstBlock", "Select First Data Block", null,
				Lists.newArrayList(CLASSIFICATION_COLORS_1.getFirst(), CLASSIFICATION_COLORS_1.getSecond()));
		manualTargetDataCellPage = new WilcoxonManualTargetDataCellPage("SecondBlock", "Select Second Data Block", null);
		manualResultPage = new WilcoxonManualResultPage("Results", "Summary and Results", null);

		autoSourceDataCellPage = new WilcoxonAutoSourceDataCellPage("FirstAutoBlock", "Select First Data Block", null,
				CLASSIFICATION_COLORS_1.getFirst());
		autoTargetDataCellPage = new WilcoxonAutoTargetDataCellPage("SecondAutoBlock", "Select Second Data Block",
				null, CLASSIFICATION_COLORS_2.getFirst());
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
	public boolean canFinish() {
		return getContainer().getCurrentPage() == autoResultPage || getContainer().getCurrentPage() == manualResultPage;
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
	 * @return the cell1Classifier, see {@link #sourceClassifier}
	 */
	public IDataClassifier getSourceClassifier() {
		return sourceClassifier;
	}

	/**
	 * @param sourceClassifier
	 *            setter, see {@link sourceClassifier}
	 */
	public void setSourceClassifier(IDataClassifier sourceClassifier) {
		this.sourceClassifier = sourceClassifier;
	}

	/**
	 * @return the derivedIDClassifier, see {@link #derivedIDClassifier}
	 */
	public SimpleIDClassifier getDerivedIDClassifier() {
		return derivedIDClassifier;
	}

	/**
	 * @param derivedIDClassifier
	 *            setter, see {@link derivedIDClassifier}
	 */
	public void setDerivedIDClassifier(SimpleIDClassifier derivedIDClassifier) {
		this.derivedIDClassifier = derivedIDClassifier;
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
