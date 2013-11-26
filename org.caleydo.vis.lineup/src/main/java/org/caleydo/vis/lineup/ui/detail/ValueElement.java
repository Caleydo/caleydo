/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.detail;

import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.ui.IColumnRenderInfo;

import com.google.common.base.Supplier;

/**
 * special element for the columns with extra information
 *
 * @author Samuel Gratzl
 *
 */
public class ValueElement extends PickableGLElement {
	private int animationFlag = 0;
	private IRow row;

	public ValueElement() {
		setVisibility(EVisibility.VISIBLE);
	}

	/**
	 * @param row
	 *            setter, see {@link row}
	 */
	public void setRow(IRow row) {
		this.row = row;
	}

	@Override
	public <T> T getLayoutDataAs(Class<T> clazz, Supplier<? extends T> default_) {
		if (clazz.isInstance(row))
			return clazz.cast(row);
		return super.getLayoutDataAs(clazz, default_);
	}

	protected final IColumnRenderInfo getRenderInfo() {
		return (IColumnRenderInfo) getParent();
	}

	/**
	 * returns the row represented by this element of this column
	 *
	 * @return
	 */
	protected final IRow getRow() {
		return row;
	}

	/**
	 * @return the animationFlag, see {@link #animationFlag}
	 */
	public int getAnimationFlag() {
		return animationFlag;
	}

	/**
	 * @param animationFlag
	 *            setter, see {@link animationFlag}
	 */
	public void setAnimationFlag(int animationFlag) {
		this.animationFlag = animationFlag;
	}
}
