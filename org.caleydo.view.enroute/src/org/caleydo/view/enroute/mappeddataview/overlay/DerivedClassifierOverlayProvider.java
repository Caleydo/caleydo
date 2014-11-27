/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview.overlay;

import org.caleydo.view.enroute.correlation.IIDClassifier;
import org.caleydo.view.enroute.mappeddataview.AColumnBasedDataRenderer;
import org.caleydo.view.enroute.mappeddataview.AColumnBasedDataRenderer.IColumnBasedDataOverlay;
import org.caleydo.view.enroute.mappeddataview.HistogramRenderer;
import org.caleydo.view.enroute.mappeddataview.HistogramRenderer.IHistogramOverlay;
import org.caleydo.view.enroute.mappeddataview.SummaryBoxAndWhiskersRenderer;
import org.caleydo.view.enroute.mappeddataview.SummaryBoxAndWhiskersRenderer.IBoxAndWhiskersOverlay;

/**
 * @author Christian
 *
 */
public class DerivedClassifierOverlayProvider implements IDataCellOverlayProvider {

	private final IIDClassifier classifier;

	public DerivedClassifierOverlayProvider(IIDClassifier classifier) {
		this.classifier = classifier;
	}

	@Override
	public IColumnBasedDataOverlay getOverlay(AColumnBasedDataRenderer dataRenderer) {
		return dataRenderer.new IDClassifierOverlay(classifier);
	}

	@Override
	public IBoxAndWhiskersOverlay getOverlay(SummaryBoxAndWhiskersRenderer dataRenderer) {
		return dataRenderer.new ColorOverlay(classifier.getDataClasses().get(0).color);
	}

	@Override
	public IHistogramOverlay getOverlay(HistogramRenderer dataRenderer) {
		return dataRenderer.new ColorOverlay(classifier.getDataClasses().get(0).color);
	}

}
