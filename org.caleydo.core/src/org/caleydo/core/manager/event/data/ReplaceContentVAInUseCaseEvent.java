package org.caleydo.core.manager.event.data;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;

/**
 * <p>
 * Event that can be called by views if their virtual array has changed so significantly that it is
 * inefficient (or even impossible) to communicate this via {@link VirtualArrayDelta}s. A possible example is
 * the saving of brushes in the parallel coordinates.
 *</p>
 *<p>
 * This event should not be received by the individual views, but by the UseCase
 *</p>
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class ReplaceContentVAInUseCaseEvent
	extends ReplaceContentVAEvent {

	public ReplaceContentVAInUseCaseEvent() {
		// nothing to initialize here
	}

	public ReplaceContentVAInUseCaseEvent(ISet set, EIDCategory idCategory, ContentVAType vaType,
		ContentVirtualArray virtualArray) {
		super(set, idCategory, vaType, virtualArray);
	}
}
