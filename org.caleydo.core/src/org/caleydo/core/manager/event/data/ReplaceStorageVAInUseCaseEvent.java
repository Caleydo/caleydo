package org.caleydo.core.manager.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;

/**
 * <p>
 * Event that can be called by views if their virtual array has changed so significantly that it is
 * inefficient (or even impossible) to communicate this via {@link VirtualArrayDelta}s. A possible example is
 * the saving of brushes in the parallel coordinates.
 * </p>
 * <p>
 * This event should not be received by the individual views, but by the UseCase
 * </p>
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ReplaceStorageVAInUseCaseEvent
	extends ReplaceStorageVAEvent {

	public ReplaceStorageVAInUseCaseEvent() {
		// nothing to initialize here
	}

	public ReplaceStorageVAInUseCaseEvent(DataTable set, String dataDomain, String vaType,
		StorageVirtualArray virtualArray) {
		super(set, dataDomain, vaType, virtualArray);
	}
}
