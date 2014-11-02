/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.fisher;

import java.util.List;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.enroute.correlation.AManualDataClassificationPage;
import org.caleydo.view.enroute.correlation.CellSelectionValidators;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.IDataClassifier;
import org.caleydo.view.enroute.correlation.ShowOverlayEvent;
import org.caleydo.view.enroute.correlation.UpdateDataCellSelectionValidatorEvent;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;

import com.google.common.base.Predicates;

/**
 * @author Christian
 *
 */
public class FishersSelectDataCellPage extends AManualDataClassificationPage {

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 * @param categoryColors
	 */
	protected FishersSelectDataCellPage(String pageName, String title, ImageDescriptor titleImage,
			List<Color> categoryColors, String initialInstruction) {
		super(pageName, title, titleImage, categoryColors, initialInstruction);
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		FishersExactTestWizard wizard = (FishersExactTestWizard) getWizard();
		if (event.getSelectedPage() == getNextPage()) {

			wizard.setPageInfo(this, info, classificationWidget.getClassifier());
		} else if (event.getSelectedPage() == this) {
			if (wizard.isFirstPage(this)) {
				UpdateDataCellSelectionValidatorEvent e = new UpdateDataCellSelectionValidatorEvent(
						CellSelectionValidators.nonEmptyCellValidator());
				EventPublisher.trigger(e);
			} else {
				if (info != null) {
					if (!CellSelectionValidators.overlappingSamplesValidator(wizard.getInfo1()).apply(info)) {

						info = null;
						classificationGroup.setVisible(false);
						dataCellInfoWidget.updateInfo(null);
						instructionsLabel.setText(initialInstruction);
						getWizard().getContainer().updateButtons();

						EventPublisher.trigger(new ShowOverlayEvent(null, null, getWizard().getStartingPage() == this));
					}
				}

				UpdateDataCellSelectionValidatorEvent e = new UpdateDataCellSelectionValidatorEvent(Predicates.and(
						CellSelectionValidators.nonEmptyCellValidator(),
						CellSelectionValidators.overlappingSamplesValidator(wizard.getInfo1())));
				EventPublisher.trigger(e);
			}

		}

	}


	@Override
	protected void dataCellChanged(DataCellInfo info) {
		super.dataCellChanged(info);
		EventPublisher.trigger(new ShowOverlayEvent(info, classificationWidget.getClassifier().getOverlayProvider(),
				getWizard().getStartingPage() == this));
		getWizard().getContainer().updateButtons();
	}

	@Override
	public void on(IDataClassifier data) {
		EventPublisher.trigger(new ShowOverlayEvent(info, data.getOverlayProvider(),
				getWizard().getStartingPage() == this));
	}

}
