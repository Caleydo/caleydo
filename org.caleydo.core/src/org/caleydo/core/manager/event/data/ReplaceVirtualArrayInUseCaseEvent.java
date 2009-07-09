package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;

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
public class ReplaceVirtualArrayInUseCaseEvent
	extends ReplaceVirtualArrayEvent {

	public ReplaceVirtualArrayInUseCaseEvent(EVAType vaType, IVirtualArray virtualArray) {
		super(vaType, virtualArray);
		
	}	
}
