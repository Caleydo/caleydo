/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.mapping;

import java.net.URL;

import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.vis.lineup.model.mapping.IMappingFunction;

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

	public abstract URL getIcon();

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

