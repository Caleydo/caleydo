/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.table;

/**
 * different modi, which values should be shown
 *
 * TODO: add the log and log10 mode, but they aren't accessible at the moment
 *
 * @author Samuel Gratzl
 * 
 */
public enum EDataRepresentation {
	RAW, NORMALIZED;

	/**
	 * @return
	 */
	public String getLabel() {
		switch (this) {
		case RAW:
			return "Raw Values";
		case NORMALIZED:
			return "Normalized Values";
		}
		throw new IllegalStateException();
	}

	/**
	 * @return
	 */
	public String getTooltip() {
		switch (this) {
		case RAW:
			return "Show the raw values of the table";
		case NORMALIZED:
			return "Show the normalized values of the table";
		}
		throw new IllegalStateException();
	}
}
