/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;

/**
 * Class managing the entry point into the recursively specified layouts. Also this class is intended to be
 * sub-classed and the {@link #setStaticLayouts()} to be overridden, specifying static layouts if desired.
 * 
 * @author Alexander Lex
 */
public class LayoutConfiguration {

	protected ElementLayout baseElementLayout;

	/**
	 * <p>
	 * Sets the static layouts that may be specified in a sub-class.
	 * </p>
	 * <p>
	 * For static layouts (for example for a particular view) the layouting should be done in a sub-class of
	 * ATemplate in this method. If the layout is generated dynamically, this typically should be empty.
	 * </p>
	 */
	public void setStaticLayouts() {
	}

	/**
	 * @return the baseElementLayout, see {@link #baseElementLayout}
	 */
	public ElementLayout getBaseElementLayout() {
		return baseElementLayout;
	}

}
