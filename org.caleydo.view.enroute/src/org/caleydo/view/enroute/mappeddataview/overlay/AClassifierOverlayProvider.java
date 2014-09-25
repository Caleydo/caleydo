/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview.overlay;

import org.caleydo.view.enroute.correlation.IDataClassifier;
import org.caleydo.view.enroute.mappeddataview.AColumnBasedDataRenderer;
import org.caleydo.view.enroute.mappeddataview.AColumnBasedDataRenderer.IColumnBasedDataOverlay;

/**
 * @author Christian
 *
 */
public abstract class AClassifierOverlayProvider implements IDataCellOverlayProvider {

	private final IDataClassifier classifier;

	public AClassifierOverlayProvider(IDataClassifier classifier) {
		this.classifier = classifier;

	}

	@Override
	public IColumnBasedDataOverlay getOverlay(AColumnBasedDataRenderer dataRenderer) {
		return dataRenderer.new DataClassifierOverlay(classifier);
	}

}
