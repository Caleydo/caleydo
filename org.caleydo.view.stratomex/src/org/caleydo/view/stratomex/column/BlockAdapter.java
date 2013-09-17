/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.stratomex.column;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.view.stratomex.tourguide.WizardElementLayout;

/**
 * adapter for handling wizard and brick blocks
 * 
 * @author Samuel Gratzl
 * 
 */
public class BlockAdapter {
	private final BrickColumn brick;
	private final WizardElementLayout wizard;

	public BlockAdapter(BrickColumn brick, WizardElementLayout wizard) {
		this.brick = brick;
		this.wizard = wizard;
	}

	public BlockAdapter(BrickColumn elem) {
		this(elem, null);
	}

	public BlockAdapter(WizardElementLayout elem) {
		this(null, elem);
	}

	public BrickColumn asBrickColumn() {
		return brick;
	}

	public ElementLayout asElementLayout() {
		return brick != null ? brick.getLayout() : wizard;
	}

	public WizardElementLayout asWizard() {
		return wizard;
	}

	public IHasHeader asHeader() {
		if (brick != null)
			return new BrickColumnHasHeader(brick);
		if (wizard != null)
			return new WizardHasHaeder(wizard);
		return null;
	}
}
