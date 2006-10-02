package cerberus.manager.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

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
	
	protected HashMap<Integer, IMediator> hashMediatorId2Mediator;
	
	protected HashMap<IMediatorSender, Vector<IMediator>> hashSender2Mediators;
	
	protected HashMap<IMediatorReceiver, Vector<IMediator>> hashReceiver2Mediators;
	
	/**
	 * Constructor. 
	 * 
	 * @param refGeneralManager
	 */
	public EventPublisher(IGeneralManager refGeneralManager)
	{
		this.refGeneralManager = refGeneralManager;
		
		hashSender2Mediators = new HashMap<IMediatorSender, Vector<IMediator>>();
		hashReceiver2Mediators = new HashMap<IMediatorReceiver, Vector<IMediator>>();
	}
	
	public void createMediator(int iMediatorId, 
			Vector<Integer> vecSenderIDs, Vector<Integer> vecReceiverIDs)
	{
		IMediator newMediator = new LockableMediator(iMediatorId);
		
		Iterator<Integer> iterSenderIDs = vecSenderIDs.iterator();
		Iterator<Integer> iterReceiverIDs = vecReceiverIDs.iterator();
	
		// Register sender
		while (iterSenderIDs.hasNext())
		{
			IMediatorSender sender = (IMediatorSender)refGeneralManager.
				getItem(iterSenderIDs.next());
		
			newMediator.register(sender);
			
			if (!hashSender2Mediators.containsKey(sender))
			{
				hashSender2Mediators.put(sender, new Vector<IMediator>());
			}
			hashSender2Mediators.get(sender).add(newMediator);
		}
		
		// Register receiver
		while (iterReceiverIDs.hasNext())
		{
			IMediatorReceiver receiver = (IMediatorReceiver)refGeneralManager.
				getItem(iterReceiverIDs.next());
		
			newMediator.register(receiver);
			
			if (!hashReceiver2Mediators.containsKey(receiver))
			{
				hashReceiver2Mediators.put(receiver, new Vector<IMediator>());
			}
			hashReceiver2Mediators.get(receiver).add(newMediator);
		}
	}

	public void registerSenderToMediator(int iMediatorId, IMediatorSender sender)
	{
		// TODO Auto-generated method stub
		
	}

	public void registerSenderToMediator(int iMediatorId, int iMediatorSenderId)
	{
		// TODO Auto-generated method stub
		
	}

	public void registerReceiverToMediator(int iMediatorId, IMediatorReceiver receiver)
	{
		// TODO Auto-generated method stub
		
	}

	public void registerReceiverToMediator(int iMediatorId, int iMediatorReceiverId)
	{
		// TODO Auto-generated method stub
		
	}

	public void unregisterSenderToMediator(int iMediatorId, IMediatorSender sender)
	{
		// TODO Auto-generated method stub
		
	}

	public void unregisterSenderToMediator(int iMediatorId, int iMediatorSenderId)
	{
		// TODO Auto-generated method stub
		
	}

	public void unregisterReceiverToMediator(int iMediatorId, IMediatorReceiver receiver)
	{
		// TODO Auto-generated method stub
		
	}

	public void unregisterReceiverToMediator(int iMediatorId, int iMediatorReceiverId)
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
		// Prevent update during initialization of data.
		if (hashSender2Mediators.isEmpty())
		{
			return;
		}
		
		Vector<IMediator> vecMediators = hashSender2Mediators.get((IMediatorSender)eventTrigger);
		Iterator<IMediator> iterMediators = vecMediators.iterator();
		
		IMediator tmpMediator;
		
		while (iterMediators.hasNext())
		{
			tmpMediator = iterMediators.next();
			
			if (tmpMediator != null)
			{
				tmpMediator.updateReceiver(eventTrigger);
			}
			else
			{
				//TODO: print message
			}
		}
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


