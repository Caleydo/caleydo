package org.caleydo.core.data.filter;

/**
 * Factory for {@link ContentFilter}s. 
 * @author Alexander Lex
 *
 */
public class ContentFilterFactory
	implements IFilterFactory<ContentFilter> {

	@Override
	public ContentFilter create() {
		return new ContentFilter();
	}
}
