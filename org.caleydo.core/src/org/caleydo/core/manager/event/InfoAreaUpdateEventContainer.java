package org.caleydo.core.manager.event;

/**
 * <p>
 * Event Container that signals an info area that it should go to the view and
 * get the info
 * </p>
 * <p>
 * TODO: possibly we will have to make this pull of information to a push in the
 * future. Especially if we want to serialize over a network.
 * </p>
 * 
 * @author Alexander Lex
 */
public class InfoAreaUpdateEventContainer
	implements IEventContainer
{
	EEventType eventType = EEventType.INFO_AREA_UPDATE;
	int iViewID = -1;

	public InfoAreaUpdateEventContainer(int iViewID)
	{
		this.iViewID = iViewID;
	}

	@Override
	public EEventType getEventType()
	{
		return eventType;
	}

	public int getViewID()
	{
		return iViewID;
	}

}
