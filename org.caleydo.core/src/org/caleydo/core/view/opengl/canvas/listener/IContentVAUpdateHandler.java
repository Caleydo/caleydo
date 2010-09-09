package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.virtualarray.ContentVAType;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ReplaceVAEvent;

public interface IContentVAUpdateHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a virtual array update event is catched by a related
	 * {@link ContentVAUpdateListener}.
	 * 
	 * @param delta
	 *            difference in the old and new virtual array
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to display in the info-box)
	 */
	public void handleVAUpdate(ContentVADelta vaDelta, String info);

	/**
	 * Handler method to be called by the {@link ReplaceContentVAListener} when a {@link ReplaceVAEvent} was
	 * received.
	 * 
	 * @param setID
	 *            TODO
	 * @param vaType
	 *            the type of the VA which is updated
	 */
	public void replaceVA(int setID, String dataDomainType, ContentVAType vaType);
}
