package org.caleydo.plex.dummy;

import DKT.GroupwareClientAppIPrx;
import DKT.GroupwareInformation;
import DKT.ResourceManagerIPrx;
import DKT.ResourceManagerIPrxHelper;
import DKT.ServerApplicationIPrx;
import DKT._MasterApplicationIDisp;
import Ice.Communicator;
import Ice.Current;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;

public class MasterApplication extends _MasterApplicationIDisp {

	private static final long serialVersionUID = 5144216540429096784L;

	ResourceManager resourceManager = null;
	ResourceManagerIPrx resourceManagerPrx = null;

	ObjectAdapter adapter;

	Communicator communicator;

	
	@Override
	public ResourceManagerIPrx getResourceManagerProxy(Current __current) {
		System.out.println("MasterApplication.getResourceManagerProxy() called");
		return resourceManagerPrx;
	}

	@Override
	public GroupwareInformation registerGroupwareClient(
			GroupwareClientAppIPrx client, String id,
			ServerApplicationIPrx serverApp, int x, int y, int w, int h,
			Current __current) {
		
		if (resourceManagerPrx == null) {
			resourceManager = new ResourceManager();
			resourceManager.setAdapter(adapter);
			resourceManager.setCommunicator(communicator);
			
			ObjectPrx objPrx = adapter.add(resourceManager, communicator.stringToIdentity("resourceManager"));
			resourceManagerPrx = ResourceManagerIPrxHelper.checkedCast(objPrx);
		}
		
		System.out.println("MasterApplication.registerGroupwareClient() called");
		client.dummy("hello, here is dummy_desko speaking");
		
		GroupwareInformation info = new GroupwareInformation();
		info.displayID = 0;
		info.isPrivate = true;
		info.deskoXID = resourceManager.createClientID();
		info.groupwareID = "groupwareID-123";

		return info;
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
