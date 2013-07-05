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

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.id.IDType;

/**
 * An event that signals that something needs to be removed from the bookmarks. What is specified by the
 * {@link EIDType} and a list of ids.
 * 
 * @author Alexander Lex
 * @param <IDDataType>
 *            the data type of the ID, typically Integer or String
 */
public class RemoveBookmarkEvent<IDDataType>
	extends AEvent {

	private IDType idType;
	ArrayList<IDDataType> elements;

	public RemoveBookmarkEvent(IDType idType) {
		this.idType = idType;
		elements = new ArrayList<IDDataType>();
	}

	public void tableIDType(IDType idType) {
		this.idType = idType;
	}

	public IDType getIDType() {
		return idType;
	}

	public void addBookmark(IDDataType element) {
		elements.add(element);
	}

	public void addBookmarks(Collection<IDDataType> elements) {
		elements.addAll(elements);
	}

	public ArrayList<IDDataType> getBookmarks() {
		return elements;
	}

	@Override
	public boolean checkIntegrity() {
		if (elements == null || elements.size() == 0 || idType == null)
			return false;
		return true;
	}

}
