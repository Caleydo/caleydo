/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout;

/**
 * Interface for elements that may want to make their layout available publicly. The contract for those
 * elements is, that they return exactly one layout and render only within this one layout.
 * 
 * @author Alexander Lex
 */
public interface ILayoutedElement {

	/**
	 * Get the single ElementLayout which is responsible for laying out this class.
	 * 
	 * @return
	 */
	public ElementLayout getLayout();

}
