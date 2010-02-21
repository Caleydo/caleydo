package org.caleydo.core.data.selection;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.delta.ContentVADelta;

@XmlType
@XmlRootElement
public class ContentVirtualArray
	extends VirtualArray<ContentVirtualArray, ContentVAType, ContentVADelta, ContentGroupList> {

	public ContentVirtualArray() {
		super(ContentVAType.getPrimaryVAType());
	}

	/**
	 * Constructor, creates an empty Virtual Array
	 */
	public ContentVirtualArray(ContentVAType vaType) {
		super(vaType);

	}

	/**
	 * Constructor. Pass the length of the managed collection and a predefined array list of indices on the
	 * collection. This will serve as the starting point for the virtual array.
	 * 
	 * @param initialList
	 */
	public ContentVirtualArray(ContentVAType vaType, List<Integer> initialList) {
		super(vaType, initialList);
	}

	@Override
	public ContentVirtualArray getNewInstance() {
		return new ContentVirtualArray();
	}

}
