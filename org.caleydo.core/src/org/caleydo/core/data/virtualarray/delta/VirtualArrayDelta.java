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
package org.caleydo.core.data.virtualarray.delta;

import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.delta.IDelta;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.UniqueList;

/**
 * Implementation of {@link IVirtualArrayDelta}
 *
 * @author Alexander Lex
 */
@XmlType
public class VirtualArrayDelta
	implements IDelta<VADeltaItem> {

	@XmlElement
	private UniqueList<VADeltaItem> deltaItems;

	@XmlElement
	private IDType idType;

	@XmlElement
	private String perspectiveID;

	public VirtualArrayDelta() {
		deltaItems = new UniqueList<VADeltaItem>();
	}

	public VirtualArrayDelta(String perspectiveID, IDType idType) {
		this.perspectiveID = perspectiveID;
		this.idType = idType;
		deltaItems = new UniqueList<VADeltaItem>();
	}

	/**
	 * Returns the type of the virtual array as specified in {@link VAType}
	 *
	 * @return
	 */
	public String getPerspectiveID() {
		return perspectiveID;
	}

	/**
	 * Set the perspective ID associated
	 * 
	 * @param perspectiveID
	 */
	public void setPerspectiveID(String perspectiveID) {
		this.perspectiveID = perspectiveID;
	}

	@Override
	public void setIDType(IDType idType) {
		this.idType = idType;
	}

	@Override
	public IDType getIDType() {
		return idType;
	}

	@Override
	public void add(VADeltaItem item) {
		deltaItems.add(item);
	}

	@Override
	public Iterator<VADeltaItem> iterator() {
		return deltaItems.iterator();
	}

	@Override
	public Collection<VADeltaItem> getAllItems() {
		return deltaItems;
	}

	@Override
	public int size() {
		return deltaItems.size();
	}

	public void append(VirtualArrayDelta delta) {
		deltaItems.addAll(delta.getAllItems());
	}

	@Override
	public String toString() {
		String output = "";
		for (VADeltaItem deltaItem : deltaItems) {
			output = output + deltaItem;
		}

		return output;
	}
}
