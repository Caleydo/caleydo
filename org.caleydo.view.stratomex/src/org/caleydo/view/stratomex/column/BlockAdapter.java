/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.stratomex.column;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.view.stratomex.addin.IStratomeXAddInColumn;

/**
 * adapter for handling wizard and brick blocks
 *
 * @author Samuel Gratzl
 *
 */
public class BlockAdapter {
	private final BrickColumn brick;
	private final IStratomeXAddInColumn addin;

	public BlockAdapter(BrickColumn brick, IStratomeXAddInColumn addin) {
		this.brick = brick;
		this.addin = addin;
	}

	public BlockAdapter(BrickColumn elem) {
		this(elem, null);
	}

	public BlockAdapter(IStratomeXAddInColumn elem) {
		this(null, elem);
	}

	public BrickColumn asBrickColumn() {
		return brick;
	}

	public ElementLayout asElementLayout() {
		return brick != null ? brick.getLayout() : addin.asElementLayout();
	}

	public IStratomeXAddInColumn asAddin() {
		return addin;
	}

	public IHasHeader asHeader() {
		if (brick != null)
			return new BrickColumnHasHeader(brick);
		if (addin != null)
			return addin.asHasHeader();
		return null;
	}
}
