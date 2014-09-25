/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.dnd;


/**
 * @author Samuel Gratzl
 *
 */
public abstract class ADropGLTarget implements IDropGLTarget {
	@Override
	public void onItemChanged(IDnDItem input) {

	}

	@Override
	public EDnDType defaultSWTDnDType(IDnDItem item) {
		return EDnDType.MOVE;
	}

	@Override
	public void onDropLeave() {

	}
}
