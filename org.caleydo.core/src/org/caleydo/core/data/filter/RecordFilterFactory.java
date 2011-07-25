package org.caleydo.core.data.filter;

/**
 * Factory for {@link RecordFilter}s. 
 * @author Alexander Lex
 *
 */
public class RecordFilterFactory
	implements IFilterFactory<RecordFilter> {

	@Override
	public RecordFilter create() {
		return new RecordFilter();
	}
}
