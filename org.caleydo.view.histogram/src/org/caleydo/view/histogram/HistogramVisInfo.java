/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram;

import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;


/**
 * Visualization info for {@link GLHistogram}.
 *
 * @author Christian Partl
 *
 */
public class HistogramVisInfo extends DefaultVisInfo {

	public HistogramVisInfo() {
	}

	@Override
	public String getLabel() {
		return GLHistogram.VIEW_NAME;
	}

}
