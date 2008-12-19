package org.caleydo.core.manager.event.mediator;

public interface IMediatorEventSender
	extends IMediatorSender
{
	public void triggerEvent(int iID);
}
