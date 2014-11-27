/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview.overlay;

import org.caleydo.view.enroute.correlation.NumericalDataClassifier;
import org.caleydo.view.enroute.mappeddataview.HistogramRenderer;
import org.caleydo.view.enroute.mappeddataview.HistogramRenderer.IHistogramOverlay;
import org.caleydo.view.enroute.mappeddataview.SummaryBoxAndWhiskersRenderer;
import org.caleydo.view.enroute.mappeddataview.SummaryBoxAndWhiskersRenderer.IBoxAndWhiskersOverlay;

/**
 * @author Christian
 *
 */
public class NumericalClassifierOverlayProvider extends AClassifierOverlayProvider {

	private final NumericalDataClassifier classifier;

	public NumericalClassifierOverlayProvider(NumericalDataClassifier classifier) {
		super(classifier);
		this.classifier = classifier;
	}


	@Override
	public IBoxAndWhiskersOverlay getOverlay(SummaryBoxAndWhiskersRenderer dataRenderer) {
		return dataRenderer.new BoxAndWhiskersNumericalClassificationOverlay(classifier);
	}

	@Override
	public IHistogramOverlay getOverlay(HistogramRenderer dataRenderer) {
		return dataRenderer.new NumericalClassifierHistogramOverlay(classifier);
	}


}
