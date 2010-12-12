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
	 * @param targetViewId 
	 * 
	 */
	public SetFilterTypeEvent(FilterType type, int targetViewId)
	{
		this.type = type;
		this.targetViewId = targetViewId;
	}
	
	public FilterType getType()
	{
		return type;
	}
	
	public int getTargetViewId()
	{
		return targetViewId;
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
	private int targetViewId;

}
