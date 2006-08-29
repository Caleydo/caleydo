package cerberus.manager.event;

import java.util.HashMap;

import cerberus.manager.IEventPublisher;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ISingelton;
import cerberus.manager.event.mediator.IMediator;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.event.mediator.LockableMediator;
import cerberus.manager.type.ManagerObjectType;

public class EventPublisher implements IEventPublisher
{
	protected IGeneralManager refGeneralManager;
	
	protected HashMap<IMediatorSender, IMediator> hashSender2Mediator;
	
	/**
	 * Constructor. 
	 * 
	 * @param refGeneralManager
	 */
	public EventPublisher(IGeneralManager refGeneralManager)
	{
		this.refGeneralManager = refGeneralManager;
		
		hashSender2Mediator = new HashMap<IMediatorSender, IMediator>();
	}

	/**
	 * Creates the mediator and registers sender and receiver.
	 * Additionally the sender mediator relation is stored in the hashmap.
	 * 
	 * @see cerberus.manager.IEventPublisher#registerSenderToReceiver(cerberus.manager.event.mediator.IMediatorSender, cerberus.manager.event.mediator.IMediatorReceiver)
	 */
	public void registerSenderToReceiver(IMediatorSender sender, IMediatorReceiver receiver)
	{
		IMediator newMediator = new LockableMediator(sender);

		newMediator.register(receiver);
		
		hashSender2Mediator.put(sender, newMediator);
	}

	/**
	 * Method takes ID from sender and receiver as parameters
	 * and requests the IMediatorSender and IMediatorReceiver
	 * objects from the general manager.
	 * 
	 * @param iMediatorSenderId 
	 * @param iMediatorReceiverId 
	 * @see cerberus.manager.IEventPublisher#registerSenderToReceiver(int, int)
	 */
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

	/**
	 * Casts the event trigger object to ISender and looks up 
	 * the Mediator for this sender.
	 * On the mediator object then the update method is called.
	 * 
	 * @param eventTrigger
	 */
	public void update(Object eventTrigger)
	{
		hashSender2Mediator.get((IMediatorSender)eventTrigger).updateReceiver(eventTrigger);
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
