/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.path;

import gleem.linalg.open.Transform;

import java.util.HashMap;

import org.caleydo.core.data.datadomain.AssociationManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

public class HistoryNode
	implements INode {

	private static int INTERFACE_ID_COUNTER = 0;

	String dataDomainType;

	AssociationManager dataDomainViewAssociationManager;
	HashMap<Integer, String> hashInterfaceIDToInterfaceType;
	HashMap<String, Integer> hashInterfaceTypeToInterfaceID;
	HashMap<String, AGLView> hashViewTypeToGLView;
	HashMap<String, RemoteLevelElement> hashViewTypeToSpawnPos;

	public HistoryNode(String dataDomainType, AssociationManager dataDomainViewAssociationManager) {
		this.dataDomainType = dataDomainType;
		this.dataDomainViewAssociationManager = dataDomainViewAssociationManager;
		hashInterfaceIDToInterfaceType = new HashMap<Integer, String>();
		hashViewTypeToGLView = new HashMap<String, AGLView>();
		hashInterfaceTypeToInterfaceID = new HashMap<String, Integer>();
		hashViewTypeToSpawnPos = new HashMap<String, RemoteLevelElement>();

		for (String interfaceType : dataDomainViewAssociationManager
			.getViewTypesForDataDomain(dataDomainType)) {
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

	@Override
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

		// FIXME: hack to ensure that the order of interfaces corresponds to the one needed in our workflow
		if (dataDomainType.equals("org.caleydo.datadomain.genetic")) {
			return hashInterfaceTypeToInterfaceID.get("org.caleydo.view.parcoords");
		}

		return hashInterfaceTypeToInterfaceID.get(dataDomainViewAssociationManager.getViewTypesForDataDomain(
			dataDomainType).toArray()[0]);
	}

	public RemoteLevelElement getSpawnPos(String viewType) {
		return hashViewTypeToSpawnPos.get(viewType);
	}

	public String[] getAllInterfaces() {

		String[] tmp = new String[hashInterfaceIDToInterfaceType.size()];

		// FIXME: hack to ensure that the order of interfaces corresponds to the one needed in our workflow
		if (dataDomainType.equals("org.caleydo.datadomain.genetic")) {
			tmp[0] = "org.caleydo.view.parcoords";
			tmp[1] = "org.caleydo.analytical.clustering";
			tmp[2] = "org.caleydo.view.heatmap.hierarchical";
			tmp[3] = "org.caleydo.view.texture"; // this is the fake browser
			return tmp;
		}

		hashInterfaceTypeToInterfaceID.keySet().toArray(tmp);
		return tmp;
	}
}
