/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import org.caleydo.view.enroute.SelectionColorCalculator;


/**
 * @author Christian
 *
 */
public abstract class ADataRenderer implements IDataRenderer {

	protected float z = 0.05f;

	protected SelectionColorCalculator colorCalculator;

	protected final ContentRenderer contentRenderer;

	public ADataRenderer(ContentRenderer contentRenderer) {
		this.contentRenderer = contentRenderer;

		if (contentRenderer.foreignColumnPerspective == null) {
			colorCalculator = new SelectionColorCalculator(MappedDataRenderer.BAR_COLOR);
		} else {
			colorCalculator = new SelectionColorCalculator(MappedDataRenderer.CONTEXT_BAR_COLOR);
		}

	}

}
