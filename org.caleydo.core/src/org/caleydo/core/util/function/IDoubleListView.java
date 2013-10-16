/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

/**
 * a view on a {@link IDoubleList}
 * 
 * @author Samuel Gratzl
 * 
 */
public interface IDoubleListView extends IDoubleList {
	/**
	 * persists this view and returns a new {@link IDoubleList} with the data
	 * 
	 * @return
	 */
	IDoubleList toList();
}
