/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.base;

/**
 * Extends {@link ILabelProvider} by methods for writing labels.
 * 
 * @author Alexander Lex
 * 
 */
public interface ILabelHolder extends ILabelProvider {

	/**
	 * Sets the label of this labelHolder.
	 */
	public void setLabel(String label);

}
