package org.caleydo.core.data.filter;

/**
 * Factory for {@link DimensionFilter}s.
 * 
 * @author Alexander Lex
 */
public class DimensionFilterFactory
	implements IFilterFactory<DimensionFilter> {

	@Override
	public DimensionFilter create() {
		return new DimensionFilter();
	}

}
