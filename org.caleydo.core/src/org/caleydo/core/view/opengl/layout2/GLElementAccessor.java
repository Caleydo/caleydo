/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * accessor helper for element container outside of this package, use with caution
 *
 * @author Samuel Gratzl
 *
 */
public class GLElementAccessor {
	/**
	 * just the container should access the layout data and here just for layouting purpose
	 *
	 * @param elem
	 * @return
	 */
	public static IGLLayoutElement asLayoutElement(GLElement elem) {
		return elem.layoutElement;
	}

	public static void setParent(GLElement elem, IGLElementParent parent) {
		elem.setParent(parent);
	}

	public static boolean isInitialized(GLElement elem) {
		return elem.context != null;
	}

	public static void init(GLElement elem, IGLElementContext context) {
		if (elem.context == null) // single initialization
			elem.init(context);
	}

	public static void takeDown(GLElement elem) {
		if (elem.context != null)
			elem.takeDown();
	}

	/**
	 * calls repaint of the given element but prevent that the parent element will be called
	 * 
	 * useful e.g. for repainting downwards
	 *
	 * @param elem
	 */
	public static void repaintDown(GLElement elem) {
		IGLElementParent parent = elem.getParent();
		elem.setParent(null);
		elem.repaint();
		elem.setParent(parent);
	}

	/**
	 * calls repaintPick of the given element but prevent that the parent element will be called
	 * 
	 * useful e.g. for repainting downwards
	 *
	 * @param elem
	 */
	public static void repaintPickDown(GLElement elem) {
		IGLElementParent parent = elem.getParent();
		elem.setParent(null);
		elem.repaintPick();
		elem.setParent(parent);
	}

	/**
	 * calls relayout of the given element but prevent that the parent element will be called
	 * 
	 * useful e.g. for repainting downwards
	 *
	 * @param elem
	 */
	public static void relayoutDown(GLElement elem) {
		IGLElementParent parent = elem.getParent();
		elem.setParent(null);
		elem.relayout();
		elem.setParent(parent);
	}
}
