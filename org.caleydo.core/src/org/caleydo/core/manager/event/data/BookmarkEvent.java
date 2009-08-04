package org.caleydo.core.manager.event.data;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.AEvent;

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

	private EIDType idType;
	ArrayList<IDDataType> elements;

	public BookmarkEvent(EIDType idType) {
		this.idType = idType;
		elements = new ArrayList<IDDataType>();
	}

	public void setIDType(EIDType idType) {
		this.idType = idType;
	}

	public EIDType getIDType() {
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
