package org.caleydo.view.visbricks.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.event.OpenCreateSmallPathwayMultiplesGroupDialogEvent;

/**
 * Listener for the event
 * {@link OpenCreateSmallPathwayMultiplesGroupDialogEvent}.
 * 
 * @author Marc Streit
 * 
 */
public class OpenCreateSmallPathwayMultiplesGroupDialogListener extends
		AEventListener<GLBrick> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof OpenCreateSmallPathwayMultiplesGroupDialogEvent) {

			// Only the view on which the context menu was clicked should handle
			// the event
			if (((OpenCreateSmallPathwayMultiplesGroupDialogEvent) event)
					.getDimensionGroupDataContainer() != handler.getDataContainer())
				return;

			OpenCreateSmallPathwayMultiplesGroupDialogEvent openCreateSmallPathwayMultiplesGroupDialogevent = (OpenCreateSmallPathwayMultiplesGroupDialogEvent) event;
			handler.openCreateSmallPathwayMultiplesGroupDialog(
					openCreateSmallPathwayMultiplesGroupDialogevent
							.getDimensionGroupDataContainer(),
					openCreateSmallPathwayMultiplesGroupDialogevent
							.getDimensionPerspective());
		}
	}
}
