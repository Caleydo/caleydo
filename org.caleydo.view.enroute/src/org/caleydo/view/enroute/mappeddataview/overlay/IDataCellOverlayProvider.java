/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview.overlay;

import org.caleydo.view.enroute.mappeddataview.AColumnBasedDataRenderer;
import org.caleydo.view.enroute.mappeddataview.AColumnBasedDataRenderer.ColumnBasedDataOverlay;
import org.caleydo.view.enroute.mappeddataview.HistogramRenderer;
import org.caleydo.view.enroute.mappeddataview.HistogramRenderer.IHistogramOverlay;
import org.caleydo.view.enroute.mappeddataview.SummaryBoxAndWhiskersRenderer;
import org.caleydo.view.enroute.mappeddataview.SummaryBoxAndWhiskersRenderer.IBoxAndWhiskersOverlay;

/**
 *
 * Interface for overlays on data cells during correlation calculation.
 *
 * @author Christian
 *
 */
public interface IDataCellOverlayProvider {

	public ColumnBasedDataOverlay getOverlay(AColumnBasedDataRenderer dataRenderer);

	public IBoxAndWhiskersOverlay getOverlay(SummaryBoxAndWhiskersRenderer dataRenderer);

	public IHistogramOverlay getOverlay(HistogramRenderer dataRenderer);

}
