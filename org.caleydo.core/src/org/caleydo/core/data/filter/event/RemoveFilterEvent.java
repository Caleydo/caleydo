package org.caleydo.core.data.filter.event;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.manager.event.AEvent;

/**
 * @author Thomas Geymayer
 */
public abstract class RemoveFilterEvent<FilterType extends Filter<?>>
	extends FilterEvent<FilterType> {

}
