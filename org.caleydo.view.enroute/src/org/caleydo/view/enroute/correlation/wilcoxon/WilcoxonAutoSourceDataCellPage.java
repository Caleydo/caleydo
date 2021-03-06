/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.enroute.correlation.ASelectDataCellPage;
import org.caleydo.view.enroute.correlation.CellSelectionValidators;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.ShowOverlayEvent;
import org.caleydo.view.enroute.correlation.UpdateDataCellSelectionValidatorEvent;
import org.caleydo.view.enroute.mappeddataview.overlay.SimpleColorOverlayProvider;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @author Christian
 *
 */
public class WilcoxonAutoSourceDataCellPage extends ASelectDataCellPage {

	protected final Color overlayColor;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected WilcoxonAutoSourceDataCellPage(String pageName, String title, ImageDescriptor titleImage,
			Color overlayColor) {
		super(pageName, title, titleImage,
				"Select the first data block for which all possible splits should be calculated.",
				"Restriction: Make sure to select numerical (not categorical) data blocks.");
		this.overlayColor = overlayColor;
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == getNextPage()) {
			WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
			wizard.setSourceInfo(info);
		} else if (event.getSelectedPage() == this) {
			Predicate<DataCellInfo> validator = Predicates.and(CellSelectionValidators.nonEmptyCellValidator(),
					CellSelectionValidators.numericalValuesValidator());

			UpdateDataCellSelectionValidatorEvent e = new UpdateDataCellSelectionValidatorEvent(validator);
			EventPublisher.trigger(e);
		}
	}

	@Override
	protected void createWidgets(Composite parentComposite) {
		createInstructionsGroup(parentComposite);

	}

	@Override
	public boolean isPageComplete() {
		// WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
		return info != null;
	}

	@Override
	protected Layout getBaseLayout() {
		return new GridLayout(2, false);
	}

	@Override
	protected void dataCellChanged(DataCellInfo info) {
		EventPublisher.trigger(new ShowOverlayEvent(info, new SimpleColorOverlayProvider(overlayColor), true));
		getWizard().getContainer().updateButtons();
	}

	@Override
	public IWizardPage getNextPage() {
		return ((WilcoxonRankSumTestWizard) getWizard()).getAutoTargetDataCellPage();
	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((WilcoxonRankSumTestWizard) getWizard()).getMethodSelectionPage();
	}

}
