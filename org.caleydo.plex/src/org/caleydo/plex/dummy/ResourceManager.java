package org.caleydo.plex.dummy;

import java.util.ArrayList;

import DKT._ResourceManagerIDisp;
import Ice.Communicator;
import Ice.Current;
import Ice.ObjectAdapter;

public class ResourceManager extends _ResourceManagerIDisp {

	private static final long serialVersionUID = 754237331087342973L;

	ObjectAdapter adapter;

	Communicator communicator;

	private ArrayList<String> clientIDList = new ArrayList<String>();
	
	@Override
	public String[] getAvailableGroupwareClients(String clientID, Current __current) {
		System.out.println("ResourceManager.getAvailableGroupwareClients() called");
		
		String[] clientIDs = new String[clientIDList.size()];
		clientIDs = clientIDList.toArray(clientIDs);
		return clientIDs;
	}

	@Override
	public String getHomeGroupwareClient(String clientID, Current __current) {
		System.out.println("ResourceManager.getHomeGroupwareClient() called");
		return clientIDList.get(clientIDList.size() - 1);
	}

	@Override
	public String getPublicGroupwareClient(String clientID, Current __current) {
		System.out.println("ResourceManager.getPublicGroupwareClient() called");
		return clientIDList.get(0);
	}

	@Override
	public void unregisterGroupwareClient(String clientID, Current __current) {
		System.out.println("ResourceManager.unregisterGroupwareClient() called");
		clientIDList.remove(clientID);
	}

	public String createClientID() {
		String id = "deskoXID-" + clientIDList.size();
		clientIDList.add(id);
		return id;
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
