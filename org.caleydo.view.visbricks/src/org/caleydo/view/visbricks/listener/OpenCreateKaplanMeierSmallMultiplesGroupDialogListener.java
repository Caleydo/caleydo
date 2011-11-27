package org.caleydo.view.visbricks.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.event.OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent;

/**
 * Listener for the event
 * {@link OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent}.
 * 
 * @author Marc Streit
 * 
 */
public class OpenCreateKaplanMeierSmallMultiplesGroupDialogListener extends
		AEventListener<GLBrick> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent) {

			// Only the view on which the context menu was clicked should handle
			// the event
			if (((OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent) event)
					.getDimensionGroupDataContainer() != handler.getDataContainer())
				return;

			OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent openCreateKaplanMeierSmallMultiplesGroupDialogevent = (OpenCreateKaplanMeierSmallMultiplesGroupDialogEvent) event;
			handler.openCreateKaplanMeierSmallMultiplesGroupDialog(
					openCreateKaplanMeierSmallMultiplesGroupDialogevent
							.getDimensionGroupDataContainer(),
					openCreateKaplanMeierSmallMultiplesGroupDialogevent
							.getDimensionPerspective());
		}
	}
}
