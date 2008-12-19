package org.caleydo.core.manager.event.mediator;

import org.caleydo.core.data.IUniqueObject;

public interface IMediatorEventReceiver
extends IMediatorReceiver
{
	public void handleExternalEvent(IUniqueObject eventTrigger, int iID);
	
}
