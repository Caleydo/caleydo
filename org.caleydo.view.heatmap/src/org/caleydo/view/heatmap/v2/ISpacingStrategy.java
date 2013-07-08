/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;

/**
 * @author Samuel Gratzl
 *
 */
public interface ISpacingStrategy {
	ISpacingLayout apply(Perspective perspective, SelectionManager manager, boolean hideHidden, float size);

	public interface ISpacingLayout {
		float getPosition(int index);

		float getSize(int index);
	}
}
