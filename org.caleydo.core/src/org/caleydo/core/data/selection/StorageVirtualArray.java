package org.caleydo.core.data.selection;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.delta.StorageVADelta;

@XmlType
@XmlRootElement
public class StorageVirtualArray
	extends VirtualArray<StorageVirtualArray, StorageVAType, StorageVADelta, StorageGroupList> {

	public StorageVirtualArray() {
		super(StorageVAType.getPrimaryVAType());
	}

	/**
	 * Constructor, creates an empty Virtual Array
	 */
	public StorageVirtualArray(StorageVAType vaType) {
		super(vaType);
	}

	/**
	 * Constructor. Pass the length of the managed collection and a predefined array list of indices on the
	 * collection. This will serve as the starting point for the virtual array.
	 * 
	 * @param initialList
	 */
	public StorageVirtualArray(StorageVAType vaType, List<Integer> initialList) {
		super(vaType, initialList);
	}

	@Override
	public StorageVirtualArray getNewInstance() {
		return new StorageVirtualArray();
	}
}
