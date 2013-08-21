/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.view.col;

import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;

/**
 * @author Samuel Gratzl
 *
 */
public interface IScoreMixin extends IRankableColumnMixin, IHideableColumnMixin {
	IScore getScore();

	void dirty();
}
