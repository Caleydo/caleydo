/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.kaplanmeier;

import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;

public class KaplanMeierVisInfo extends DefaultVisInfo {

	public KaplanMeierVisInfo() {
	}

	@Override
	public String getLabel() {
		return GLKaplanMeier.VIEW_NAME;
	}

}
