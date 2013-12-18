/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;

/**
 * Base class for all GLElement based pathway representations.
 *
 * @author Christian
 *
 */
public abstract class APathwayElementRepresentation extends GLElement implements IPathwayRepresentation {

	private ALayoutRenderer wrappingLayoutRenderer;
	private PathwayElement wrappingElement;

	/**
	 * @param wrappingElement
	 *            setter, see {@link wrappingElement}
	 */
	public void setWrappingElement(PathwayElement wrappingElement) {
		this.wrappingElement = wrappingElement;
	}

	/**
	 * @param wrappingLayoutRenderer
	 *            setter, see {@link wrappingLayoutRenderer}
	 */
	public void setWrappingLayoutRenderer(ALayoutRenderer wrappingLayoutRenderer) {
		this.wrappingLayoutRenderer = wrappingLayoutRenderer;
	}

	@Override
	public AGLView asAGLView() {
		return null;
	}

	@Override
	public GLElement asGLElement() {
		return wrappingElement == null ? this : wrappingElement;
	}

	@Override
	public ALayoutRenderer asLayoutRenderer() {
		return wrappingLayoutRenderer;
	}
}
