/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;


/**
 * Container for layouts that are stacked on top of each other. The column is a
 * {@link ElementLayout} and contains other ElementLayouts. It can be nested
 * into other containers
 *
 * @author Alexander Lex
 */
public class Column extends ALayoutContainer {

	public enum VAlign {
		LEFT, RIGHT, CENTER
	}

	public Column() {
		super(new ColumnLayout());
	}

	public Column(String layoutName) {
		super(layoutName, new ColumnLayout());
	}

	@Override
	public ColumnLayout getLayout() {
		return (ColumnLayout) super.getLayout();
	}

	public void setVAlign(VAlign vAlign) {
		getLayout().setVAlign(vAlign);
	}

	/**
	 * Set flag signaling whether the content should be rendered from bottom to top (default, true) or from top to
	 * bottom (false)
	 *
	 * @param isBottomUp
	 */
	public void setBottomUp(boolean isBottomUp) {
		getLayout().setBottomUp(isBottomUp);
	}

	/**
	 * <p>
	 * Set flag signaling whether the x-size of the container should be set to
	 * the largest size in y of its sub-elements (true), or if some size
	 * indication (either scaled or not scaled) is given (false).
	 * </p>
	 * <p>
	 * Notice that for if this is set to true, sub-elements must not have a
	 * ratioSize of 1 (which is the default initialization). The reason for this
	 * is that it makes no sense, and catching it prevents errors.
	 */
	@Override
	public void setXDynamic(boolean isXDynamic) {
		super.setXDynamic(isXDynamic);
	}
}
