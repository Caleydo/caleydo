/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview.overlay;

import org.caleydo.view.enroute.correlation.CategoricalDataClassifier;
import org.caleydo.view.enroute.mappeddataview.HistogramRenderer;
import org.caleydo.view.enroute.mappeddataview.HistogramRenderer.IHistogramOverlay;
import org.caleydo.view.enroute.mappeddataview.SummaryBoxAndWhiskersRenderer;
import org.caleydo.view.enroute.mappeddataview.SummaryBoxAndWhiskersRenderer.IBoxAndWhiskersOverlay;

/**
 * @author Christian
 *
 */
public class CategoricalClassifierOverlayProvider extends AClassifierOverlayProvider {

	private final CategoricalDataClassifier classifier;

	public CategoricalClassifierOverlayProvider(CategoricalDataClassifier classifier) {
		super(classifier);
		this.classifier = classifier;
	}

	@Override
	public IBoxAndWhiskersOverlay getOverlay(SummaryBoxAndWhiskersRenderer dataRenderer) {
		// not supported
		return null;
	}

	@Override
	public IHistogramOverlay getOverlay(HistogramRenderer dataRenderer) {
		return dataRenderer.new CategoricalClassifierHistogramOverlay(classifier);
	}

}
