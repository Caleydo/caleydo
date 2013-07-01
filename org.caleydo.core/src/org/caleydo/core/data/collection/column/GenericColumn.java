/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.collection.column;

import org.caleydo.core.data.collection.column.container.GenericContainer;
import org.caleydo.core.io.DataDescription;

/**
 *
 * Column for generic, unstructured data.
 *
 * @author Alexander Lex
 */

public class GenericColumn<T> extends AColumn<GenericContainer<T>, T> {

	/**
	 * @param uniqueID
	 */
	public GenericColumn(DataDescription dataDescription) {
		super(dataDescription);
	}


}
