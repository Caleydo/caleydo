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
			List<Color> categoryColors) {
		super(pageName, title, titleImage, categoryColors);
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == getNextPage()) {
			FishersExactTestWizard wizard = (FishersExactTestWizard) getWizard();
			wizard.setPageInfo(this, info, classificationWidget.getClassifier());
		} else if (event.getSelectedPage() == this) {
			UpdateDataCellSelectionValidatorEvent e = new UpdateDataCellSelectionValidatorEvent(
					CellSelectionValidators.nonEmptyCellValidator());
			EventPublisher.trigger(e);
		}

	}

	@Override
	protected void dataCellChanged(DataCellInfo info) {
		super.dataCellChanged(info);
		EventPublisher.trigger(new ShowOverlayEvent(info, classificationWidget.getClassifier().getOverlayProvider(),
				getWizard().getStartingPage() == this));
	}

	@Override
	public void on(IDataClassifier data) {
		EventPublisher.trigger(new ShowOverlayEvent(info, data.getOverlayProvider(),
				getWizard().getStartingPage() == this));
	}

}
