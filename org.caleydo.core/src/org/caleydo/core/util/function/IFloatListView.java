/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

/**
 * a view on a {@link IFloatList}
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IFloatListView extends IFloatList {
	/**
	 * persists this view and returns a new {@link IFloatList} with the data
	 * 
	 * @return
	 */
	IFloatList toList();
}
