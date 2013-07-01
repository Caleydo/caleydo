/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
