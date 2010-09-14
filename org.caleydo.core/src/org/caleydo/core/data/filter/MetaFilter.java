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
public interface MetaFilter<FilterType extends Filter<?>> {

	public ArrayList<FilterType> getFilterList();
}
