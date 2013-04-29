/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.view.col;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.tourguide.internal.view.PerspectiveRow;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.IntegerRankColumnModel;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public class SizeRankColumnModel extends IntegerRankColumnModel {
	public SizeRankColumnModel() {
		super(GLRenderers.drawText("Group Size", VAlign.CENTER), new Function<IRow, Integer>() {
			@Override
			public Integer apply(IRow in) {
				return ((PerspectiveRow) in).size();
			}
		});
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