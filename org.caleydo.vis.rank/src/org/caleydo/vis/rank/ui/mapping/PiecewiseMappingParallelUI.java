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
package org.caleydo.vis.rank.ui.mapping;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public class PiecewiseMappingParallelUI extends MappingParallelUI<PiecewiseMapping> implements IPickingListener,
		IGLLayout {
	public PiecewiseMappingParallelUI(PiecewiseMapping model, boolean isHorizontal) {
		super(model, isHorizontal);
		setPicker(GLRenderers.DUMMY);
		setLayout(this);
	}

	@Override
	public void reset() {
		this.clear();
		if (model.isDefinedMapping()) {
			// if (model.isMappingDefault()) {
			// if (Float.isNaN(model.getFromMin()))
			// this.add(new Point(model.getActMin(), 0, true));
			// if (Float.isNaN(model.getFromMax()))
			// this.add(new Point(model.getActMax(), 1, true));
			// }
			// for (Map.Entry<Float, Float> entry : model) {
			// this.add(new Point(entry.getKey(), entry.getValue(), false));
			// }
		}
	}

	@Override
	public void pick(Pick pick) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		// TODO Auto-generated method stub

	}

}

