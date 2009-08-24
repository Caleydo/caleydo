package org.caleydo.plex.dummy;

import DKT.MasterApplicationIPrx;
import DKT.MasterApplicationIPrxHelper;
import DKT._ServerApplicationIDisp;
import Ice.Communicator;
import Ice.Current;
import Ice.ObjectAdapter;

public class ServerApplication extends _ServerApplicationIDisp{

	private static final long serialVersionUID = -213820085531228465L;

	MasterApplicationIPrx masterApplicationPrx = null;
	
	ObjectAdapter adapter;
	
	Communicator communicator;
	
	@Override
	public MasterApplicationIPrx getMasterProxy(Current __current) {
		System.out.println("ServerApplication.getMasterProxy() called");
		
		if (masterApplicationPrx == null) {
			MasterApplication masterApplication = new MasterApplication();
			masterApplication.setAdapter(adapter);
			masterApplication.setCommunicator(communicator);
	
			Ice.ObjectPrx objPrx = adapter.add(masterApplication, communicator.stringToIdentity("GroupwareClientAppI"));
			masterApplicationPrx = MasterApplicationIPrxHelper.checkedCast(objPrx);
		}

		return masterApplicationPrx;
	}

	public ObjectAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(ObjectAdapter adapter) {
		this.adapter = adapter;
	}

	public Communicator getCommunicator() {
		return communicator;
	}

	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}

}
