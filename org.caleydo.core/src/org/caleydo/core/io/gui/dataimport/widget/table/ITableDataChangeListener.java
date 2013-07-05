/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget.table;

/**
 * @author Christian
 *
 */
public interface ITableDataChangeListener {

	public static enum EChangeType {
		STRUCTURAL, VALUE;
	}

	public void dataChanged(EChangeType changeType);

}
