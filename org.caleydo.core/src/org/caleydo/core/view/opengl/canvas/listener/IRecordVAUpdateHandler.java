package org.caleydo.core.view.opengl.canvas.listener;

import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ReplaceVAEvent;

public interface IRecordVAUpdateHandler
	extends IListenerOwner {

	/**
	 * Handler method to be called when a virtual array update event is catched by a related
	 * {@link RecordVAUpdateListener}.
	 * 
	 * @param delta
	 *            difference in the old and new virtual array
	 * @param info
	 *            info about the selection (e.g. the name of triggering view to display in the info-box)
	 */
	public void handleVAUpdate(RecordVADelta vaDelta, String info);

	/**
	 * Handler method to be called by the {@link ReplaceRecordVAListener} when a {@link ReplaceVAEvent} was
	 * received.
	 * 
	 * @param dataTableID
	 *            TODO
	 * @param vaType
	 *            the type of the VA which is updated
	 */
	public void replaceRecordVA(int dataTableID, String dataDomainType, String vaType);
}
