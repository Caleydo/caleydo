package org.caleydo.core.manager.event.data;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.manager.event.AEvent;

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

	public void setIDType(IDType idType) {
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
