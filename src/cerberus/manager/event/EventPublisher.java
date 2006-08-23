package cerberus.manager.event;

import cerberus.manager.IEventPublisher;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ISingelton;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.type.ManagerObjectType;

public class EventPublisher implements IEventPublisher
{
	IGeneralManager refGeneralManager;
	
	public EventPublisher(IGeneralManager refGeneralManager)
	{
		this.refGeneralManager = refGeneralManager;
	}

	public void registerSenderToReceiver(IMediatorSender sender, IMediatorReceiver receiver)
	{
		// TODO Auto-generated method stub
	}

	public void registerSenderToReceiver(int iMediatorSenderId, int iMediatorReceiverId)
	{
		IMediatorSender sender = (IMediatorSender)refGeneralManager.
			getItem(iMediatorSenderId);
		
		IMediatorReceiver receiver = (IMediatorReceiver)refGeneralManager.
			getItem(iMediatorReceiverId);
		
		registerSenderToReceiver(sender, receiver);
	}

	public void registerSenderToSender(IMediatorSender senderExisting, IMediatorSender senderNew)
	{
		// TODO Auto-generated method stub
		
	}

	public void unregister(IMediatorSender sender, IMediatorReceiver receiver)
	{
		// TODO Auto-generated method stub

	}

	public void unregister(IMediatorSender senderExisting,
			IMediatorSender senderRemove)
	{
		// TODO Auto-generated method stub

	}

	public void removeMediator(IMediatorSender sender)
	{
		// TODO Auto-generated method stub

	}

	public boolean hasRelation(IMediatorSender sender,
			IMediatorReceiver receiver)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasItem(int iItemId)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Object getItem(int iItemId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public ManagerObjectType getManagerType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public IGeneralManager getGeneralManager()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ISingelton getSingelton()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean registerItem(Object registerItem, int iItemId, ManagerObjectType type)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public int createNewId(ManagerObjectType setNewBaseType)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public IGeneralManager getManagerByBaseType(ManagerObjectType managerType)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
