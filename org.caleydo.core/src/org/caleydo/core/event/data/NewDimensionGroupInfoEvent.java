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
package org.caleydo.core.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.event.AEvent;

/**
 * This event signals a view that a new groupList for a VA is available.
 * 
 * @author Bernhard Schlegl
 */
@XmlRootElement
@XmlType
public class NewDimensionGroupInfoEvent
	extends AEvent {

	private String vaType = null;
	private DimensionGroupList groupList = null;
	private boolean bDeleteTree = false;

	@Override
	public boolean checkIntegrity() {

		if (vaType != null && bDeleteTree == true)
			return true;
		else if (vaType != null && groupList != null && bDeleteTree == false)
			return true;
		else
			return false;
	}

	public void setVAType(String vaType) {
		this.vaType = vaType;
	}

	public String getVAType() {
		return vaType;
	}

	public void setGroupList(DimensionGroupList groupList) {
		this.groupList = groupList;
	}

	public DimensionGroupList getGroupList() {
		return groupList;
	}

	public void setDeleteTree(boolean bDeleteTree) {
		this.bDeleteTree = bDeleteTree;
	}

	public boolean isDeleteTree() {
		return bDeleteTree;
	}

}
