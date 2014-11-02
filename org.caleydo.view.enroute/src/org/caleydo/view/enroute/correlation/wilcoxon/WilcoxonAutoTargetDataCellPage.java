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
public class WilcoxonAutoTargetDataCellPage extends ASelectDataCellPage {

	protected final Color overlayColor;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected WilcoxonAutoTargetDataCellPage(String pageName, String title, ImageDescriptor titleImage,
			Color overlayColor) {
		super(
				pageName,
				title,
				titleImage,
				"Select the second data block. The data of this block will be divided according to the calculated splits from the first data block.");
		this.overlayColor = overlayColor;
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
		if (event.getSelectedPage() == getNextPage()) {

			wizard.setTargetInfo(info);
		} else if (event.getSelectedPage() == this) {
			if (info != null) {
				if (!CellSelectionValidators.overlappingSamplesValidator(wizard.getSourceInfo()).apply(info)) {
					info = null;
					dataCellInfoWidget.updateInfo(null);
					getWizard().getContainer().updateButtons();

					EventPublisher.trigger(new ShowOverlayEvent(null, null, getWizard().getStartingPage() == this));
				}
			}

			@SuppressWarnings("unchecked")
			Predicate<DataCellInfo> validator = Predicates.and(CellSelectionValidators.nonEmptyCellValidator(),
					CellSelectionValidators.numericalValuesValidator(),
					CellSelectionValidators.overlappingSamplesValidator(wizard.getSourceInfo()));
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
		EventPublisher.trigger(new ShowOverlayEvent(info, new SimpleColorOverlayProvider(overlayColor), false));
		getWizard().getContainer().updateButtons();
	}

	@Override
	public IWizardPage getNextPage() {
		return ((WilcoxonRankSumTestWizard) getWizard()).getAutoResultPage();
	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((WilcoxonRankSumTestWizard) getWizard()).getAutoSourceDataCellPage();
	}

}
