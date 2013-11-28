/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.entourage.model;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.IntegerRankColumnModel;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class SizeRankColumnModel extends IntegerRankColumnModel {
	public SizeRankColumnModel(String label, Function<IRow, Integer> data) {
		super(GLRenderers.drawText(label, VAlign.CENTER), data);
	}

	public SizeRankColumnModel(SizeRankColumnModel copy) {
		super(copy);
	}

	@Override
	public SizeRankColumnModel clone() {
		return new SizeRankColumnModel(this);
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		return -super.compare(o1, o2);
	}

}
