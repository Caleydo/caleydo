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
package org.caleydo.core.event.view;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.serialize.ASerializedView;

/**
 * Event to create SWT-views from its serialized form. Especially used to transmit a view to a remote caleydo
 * application or load views from disk.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class CreateGUIViewEvent
	extends AEvent {

	/** serialized form of the view to create */
	private ASerializedView serializedView;

	/** application id of caleydo application where the id should be created */
	private String targetApplicationID;

	@Override
	public boolean checkIntegrity() {
		if (serializedView == null) {
			throw new IllegalStateException("the serialized-view has not been set");
		}
		if (targetApplicationID == null) {
			throw new IllegalStateException("the targetApplicationID has not been set");
		}

		return true;
	}

	public ASerializedView getSerializedView() {
		return serializedView;
	}

	public void setSerializedView(ASerializedView serializedView) {
		this.serializedView = serializedView;
	}

	public void setTargetApplicationID(String targetApplicationID) {
		this.targetApplicationID = targetApplicationID;
	}

	public String getTargetApplicationID() {
		return targetApplicationID;
	}

}
