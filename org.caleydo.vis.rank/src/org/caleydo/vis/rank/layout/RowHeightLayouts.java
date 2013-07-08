/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.layout;

import org.caleydo.vis.rank.model.MaxRankColumnModel;

/**
 * row height layouts determine on the one hand the row heights of items and will also be used for layouting the rows of
 * a {@link MaxRankColumnModel}
 *
 * @author Samuel Gratzl
 *
 */
public class RowHeightLayouts {


	public static final IRowHeightLayout UNIFORM = new UniformRowHeightLayout();

	public static final IRowHeightLayout FISH_EYE = new FishEyeRowHeightLayout();
}
