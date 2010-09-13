package org.caleydo.core.data.filter.event;

import java.lang.reflect.ParameterizedType;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.manager.event.AEvent;

/**
 * @author Alexander Lex
 */
public class RemoveFilterEvent<FilterType extends Filter<?, ?>>
	extends AEvent {

	private FilterType filter = null;

	public void setFilter(FilterType filter) {
		this.filter = filter;
	}

	public FilterType getFilter() {
		return filter;
	}

	public Class getFilterTypeClass() {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class) parameterizedType.getActualTypeArguments()[0];
	}

	@Override
	public boolean checkIntegrity() {
		if (filter == null)
			return false;

		return true;
	}

}
