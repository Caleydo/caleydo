/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram.v2.internal;

import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;
import org.caleydo.view.histogram.v2.BoxAndWhiskersElement;


/**
 * Visualization info for {@link BoxAndWhiskersElement}.
 *
 * @author Christian Partl
 *
 */
public class BoxAndWhiskersVisInfo extends DefaultVisInfo {

	public BoxAndWhiskersVisInfo() {
	}

	@Override
	public String getLabel() {
		return "Box and Whiskers";
	}
}
