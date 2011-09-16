/**
 * 
 */
package org.caleydo.core.event.view.filterpipeline;

import org.caleydo.core.event.AEvent;

/**
 * @author Thomas Geymayer
 *
 */
public class SetFilterTypeEvent
	extends AEvent {

	public enum FilterType
	{
		RECORD,
		DIMENSION
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
