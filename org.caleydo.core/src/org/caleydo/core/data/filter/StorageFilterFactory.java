package org.caleydo.core.data.filter;

/**
 * Factory for {@link StorageFilter}s.
 * 
 * @author Alexander Lex
 */
public class StorageFilterFactory
	implements IFilterFactory<StorageFilter> {

	@Override
	public StorageFilter create() {
		return new StorageFilter();
	}

}
