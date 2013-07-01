/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway;

import org.caleydo.core.data.perspective.table.TablePerspective;

/**
 * Defines which samples should be mapped on average in the pathway.
 * 
 * @author Alexander Lex
 * 
 */
public enum ESampleMappingMode {
	/** All samples of the current {@link TablePerspective} should be mapped */
	ALL,
	/**
	 * Only those samples that are both in the {@link TablePerspective} and in
	 * the selected set of samples should be mapped.
	 */
	SELECTED;

}
