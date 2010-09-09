package org.caleydo.core.data.filter;

/**
 * Factory interface used to create objects when only a generic type is available.
 * 
 * @author Alexander Lex
 * @param <FilterType>
 */
public interface IFilterFactory<FilterType extends Filter<?, ?>> {

	/**
	 * Creates a new FilterType object.
	 * 
	 * @return
	 */
	FilterType create();

}
