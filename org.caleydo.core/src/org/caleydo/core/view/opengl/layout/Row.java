/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;


/**
 * Container for layouts that are rendered side by side. The row is a
 * {@link ElementLayout} and contains other ElementLayouts. It can be nested
 * into other containers
 *
 * @author Alexander Lex
 */
public class Row extends ALayoutContainer {

	public enum HAlign {
		TOP, BOTTOM, CENTER
	}

	public Row() {
		super(new RowLayout());
	}

	public Row(String layoutName) {
		super(layoutName, new RowLayout());
	}

	@Override
	public RowLayout getLayout() {
		return (RowLayout) super.getLayout();
	}

	/**
	 * Set the flag signaling whether the content should be rendered from left to right (true, default) or from right to
	 * left (false)
	 */
	public void setLeftToRight(boolean isLeftToRight) {
		getLayout().setLeftToRight(isLeftToRight);
	}

	public void sethAlign(HAlign hAlign) {
		this.getLayout().sethAlign(hAlign);
	}

	/**
	 * <p>
	 * Set flag signaling whether the y-size of the container should be set to
	 * the largest size in y of its sub-elements (true), or if some size
	 * indication (either scaled or not scaled) is given (false).
	 * </p>
	 * <p>
	 * Notice that for if this is set to true, sub-elements must not have a
	 * ratioSize of 1 (which is the default initialization). The reason for this
	 * is that it makes no sense, and catching it prevents errors.
	 */
	@Override
	public void setYDynamic(boolean isYDynamic) {
		super.setYDynamic(isYDynamic);
	}
}
