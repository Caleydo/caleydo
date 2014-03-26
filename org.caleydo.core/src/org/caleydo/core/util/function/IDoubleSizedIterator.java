/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

import java.util.Iterator;

/**
 * an {@link Iterator} with a size
 *
 * @author Samuel Gratzl
 *
 */
public interface IDoubleSizedIterator extends IDoubleIterator {
	/**
	 * @return the number of items in this iterator
	 */
	int size();
}
