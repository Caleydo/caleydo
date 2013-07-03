/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.filter;

import java.util.ArrayList;

/**
 * A MetaFilter is a collection of filters that share for example a common set of settings. An example would
 * be a group of filters on dimensions, that should drop all records below a certain value. With the
 * MetaFilter this value can be set at once for all sub-filters.
 *
 * @author Alexander Lex
 * @param <VAType>
 * @param <DeltaType>
 */
public interface MetaFilter {

	public ArrayList<Filter> getFilterList();
}
