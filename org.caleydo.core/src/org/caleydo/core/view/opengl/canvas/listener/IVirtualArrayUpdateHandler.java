package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayEvent;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;
import org.caleydo.core.view.opengl.canvas.storagebased.listener.ReplaceVirtualArrayListener;

/**
 * Interface for views, mediator or manager classes that needs to get virtual-array-update information.
 * Implementation of this interface are called by {@link VirtualArrayUpdateListener}s. FIXME link to virtual
 * arrays or explanation about virtual arrays
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public interface IVirtualArrayUpdateHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a virtual array update event is catched by a related
	 * {@link VirtualArrayUpdateListener}.
	 * 
	 * @param delta
	 *            difference in the old and new virtual array
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to display in the info-box)
	 */
	public void handleVirtualArrayUpdate(IVirtualArrayDelta vaDelta, String info);

	/**
	 * Handler method to be called by the {@link ReplaceVirtualArrayListener} when a
	 * {@link ReplaceVirtualArrayEvent} was received.
	 * 
	 * @param vaType
	 *            the type of the VA which is updated
	 */
	public void replaceVirtualArray(EVAType vaType);

}
