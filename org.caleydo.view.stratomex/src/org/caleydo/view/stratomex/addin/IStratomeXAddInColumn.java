/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.stratomex.addin;

import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.view.stratomex.column.IHasHeader;

/**
 * @author Samuel Gratzl
 *
 */
public interface IStratomeXAddInColumn {
	ElementLayout asElementLayout();

	IHasHeader asHasHeader();

}
