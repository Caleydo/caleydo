/**
 * 
 */
package org.caleydo.core.manager.event.view.filterpipeline;

import org.caleydo.core.manager.event.AEvent;

/**
 * @author Thomas Geymayer
 *
 */
public class SetFilterTypeEvent
	extends AEvent {

	public enum FilterType
	{
		CONTENT,
		STORAGE
	}

	/**
	 * 
	 */
	public SetFilterTypeEvent(FilterType type)
	{
		this.type = type;
	}
	
	public FilterType getType()
	{
		return type;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.event.AEvent#checkIntegrity()
	 */
	@Override
	public boolean checkIntegrity()
	{
		return this.type != null;
	}
	
	private FilterType type;

}
