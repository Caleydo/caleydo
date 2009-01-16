package org.caleydo.core.manager.event;

public interface IMediatorEventSender
	extends IMediatorSender
{
	public void triggerEvent(int iID);
}
