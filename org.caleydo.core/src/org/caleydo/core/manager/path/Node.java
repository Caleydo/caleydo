package org.caleydo.core.manager.path;

import gleem.linalg.open.Transform;

import java.util.HashMap;

import org.caleydo.core.manager.datadomain.AssociationManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

public class Node {

	private static int INTERFACE_ID_COUNTER = 0;

	String dataDomainType;
	HashMap<Integer, String> hashInterfaceIDToInterfaceType;
	HashMap<String, Integer> hashInterfaceTypeToInterfaceID;
	HashMap<String, AGLView> hashViewTypeToGLView;
	HashMap<String, RemoteLevelElement> hashViewTypeToSpawnPos;
	
	public Node(String dataDomainType, AssociationManager dataDomainViewAssociationManager) {
		this.dataDomainType = dataDomainType;
		hashInterfaceIDToInterfaceType = new HashMap<Integer, String>();
		hashViewTypeToGLView = new HashMap<String, AGLView>();
		hashInterfaceTypeToInterfaceID = new HashMap<String, Integer>();
		hashViewTypeToSpawnPos = new HashMap<String, RemoteLevelElement>();

		for (String interfaceType : dataDomainViewAssociationManager.getViewTypesForDataDomain(dataDomainType)) {
			hashInterfaceIDToInterfaceType.put(++INTERFACE_ID_COUNTER, interfaceType);
			hashInterfaceTypeToInterfaceID.put(interfaceType, INTERFACE_ID_COUNTER);
			
			RemoteLevelElement remoteLevelElement = new RemoteLevelElement(null);
			remoteLevelElement.setTransform(new Transform());
			hashViewTypeToSpawnPos.put(interfaceType, remoteLevelElement);	
		}
	}

	@Override
	public String toString() {
		return "[" + dataDomainType + "]";
	}

	public String getDataDomainType() {
		return dataDomainType;
	}

	public void addInterface(String interfaceType, int interfaceID) {
		hashInterfaceIDToInterfaceType.put(interfaceID, interfaceType);
	}

	public void addGLView(AGLView view) {
		hashViewTypeToGLView.put(view.getViewType(), view);
	}

	public boolean containsViewForType(String viewType) {
		return hashViewTypeToGLView.containsKey(viewType);
	}

	public AGLView getGLView(String viewType) {
		return hashViewTypeToGLView.get(viewType);
	}
	
	public boolean containsView(AGLView view) {
		return hashViewTypeToGLView.get(view.getViewType()) == view;
	}
	
	public String getInterfaceType(int interfaceID) {
		return hashInterfaceIDToInterfaceType.get(interfaceID);
	}
	
	public int getInterfaceID(String interfaceType) {
		return hashInterfaceTypeToInterfaceID.get(interfaceType);
	}
	
	public Integer getFirstInterfaceID() {
		return (Integer)hashInterfaceIDToInterfaceType.keySet().toArray()[0];
	}
	
	public RemoteLevelElement getSpawnPos(String viewType) {
		return hashViewTypeToSpawnPos.get(viewType);
	}
	
	public String[] getAllInterfaces() {
		String[] tmp = new String[hashInterfaceIDToInterfaceType.size()];
		hashInterfaceTypeToInterfaceID.keySet().toArray(tmp);
		return tmp;
	}
}
