/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.data;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.id.IDType;

/**
 * An event that signals that something needs to be bookmarked. What is specified by the {@link EIDType} and a
 * list of ids.
 * 
 * @author Alexander Lex
 * @param <IDDataType>
 *            the data type of the ID, typically Integer or String
 */
public class BookmarkEvent<IDDataType>
	extends AEvent {

	private IDType idType;
	ArrayList<IDDataType> elements;

	public BookmarkEvent(IDType idType) {
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
