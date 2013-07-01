/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 * 
 */
package org.caleydo.core.util.base;

/**
 * Interface that extends {@link ILabelHolder} with a default label
 * functionality. It basically tells whether the label provided is a default label.
 * 
 * @author Christian Partl
 * 
 */
public interface IDefaultLabelHolder extends ILabelHolder {

	/**
	 * @return True, if the label provided is a default label, false otherwise.
	 */
	public boolean isLabelDefault();
	
	/**
	 * Sets the label of this labelHolder and specifies whether it is a
	 * default-label (so that it can be hidden under certain circumstances) or
	 * not
	 */
	public void setLabel(String label, boolean isLabelDefault);
	
}
