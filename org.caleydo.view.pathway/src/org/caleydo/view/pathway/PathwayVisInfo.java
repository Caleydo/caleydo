/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway;

import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;

/**
 * VisInfo for {@link GLPathway}
 *
 * @author Christian Partl
 *
 */
public class PathwayVisInfo extends DefaultVisInfo {

	public PathwayVisInfo() {
	}

	@Override
	public String getLabel() {
		return GLPathway.VIEW_NAME;
	}

}
