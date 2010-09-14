package org.caleydo.core.data.filter;

import java.util.ArrayList;

import org.caleydo.core.data.virtualarray.IVAType;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;

/**
 * A MetaFilter is a collection of filters that share for example a common set of settings. An example would
 * be a group of filters on dimensions, that should drop all records below a certain value. With the
 * MetaFilter this value can be set at once for all sub-filters.
 * 
 * @author Alexander Lex
 * @param <VAType>
 * @param <DeltaType>
 */
public class MetaFilter<VAType extends IVAType, DeltaType extends VirtualArrayDelta<?, VAType>>
	extends Filter<VAType, DeltaType> {

	ArrayList<Filter<VAType, DeltaType>> filterList = new ArrayList<Filter<VAType, DeltaType>>();

	public ArrayList<Filter<VAType, DeltaType>> getFilterList() {
		return filterList;
	}

}
