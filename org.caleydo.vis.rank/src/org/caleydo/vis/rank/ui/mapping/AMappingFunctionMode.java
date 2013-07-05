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

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AMappingFunctionMode<T extends IMappingFunction> extends GLElementContainer {
	protected final T model;

	public AMappingFunctionMode(T model) {
		this.model = model;
	}

	public abstract String getName();

	public abstract void reset();

	public abstract String getIcon();

	public abstract void doLayout(IGLLayoutElement rawHist, IGLLayoutElement normHist, IGLLayoutElement specific,
			float x, float y, float w, float h);

	protected final float normalizeRaw(float v) {
		return (v - model.getActMin()) / (model.getActMax() - model.getActMin());
	}

	protected final float inverseNormalize(float n) {
		return n * (model.getActMax() - model.getActMin()) + model.getActMin();
	}

	protected final void fireCallback() {
		((MappingFunctionUI) getParent()).fireCallback();
	}

	protected final void renderMapping(GLGraphics g, float w, float h, boolean cross, boolean isNormalLeftTop) {
		((MappingFunctionUI) getParent()).renderMapping(g, w, h, cross, isNormalLeftTop);
	}

	protected final void repaintMapping() {
		repaintAll();
		((MappingFunctionUI) getParent()).repaintMapping();
	}
}

