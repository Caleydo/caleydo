package org.caleydo.core.manager.event;

/**
 * Basic implementation of {@link IEventContainer}
 * 
 * @author Alexander Lex
 * 
 */
public abstract class AEventContainer
	implements IEventContainer
{
	EEventType eEventType;

	/**
	 * Constructor
	 * 
	 * @param eEventType the type of event for this container
	 */
	public AEventContainer(EEventType eEventType)
	{
		this.eEventType = eEventType;
	}

	@Override
	public EEventType getEventType()
	{
		return eEventType;
	}

}
