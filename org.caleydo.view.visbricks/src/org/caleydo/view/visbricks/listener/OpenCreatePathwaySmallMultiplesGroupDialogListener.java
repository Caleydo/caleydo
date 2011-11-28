package org.caleydo.view.visbricks.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.view.visbricks.brick.GLBrick;
import org.caleydo.view.visbricks.event.OpenCreatePathwaySmallMultiplesGroupDialogEvent;

/**
 * Listener for the event
 * {@link OpenCreatePathwaySmallMultiplesGroupDialogEvent}.
 * 
 * @author Marc Streit
 * 
 */
public class OpenCreatePathwaySmallMultiplesGroupDialogListener
	extends AEventListener<GLBrick>
{

	@Override
	public void handleEvent(AEvent event)
	{
		if (event instanceof OpenCreatePathwaySmallMultiplesGroupDialogEvent)
		{
			// Only the view on which the context menu was clicked should handle
			// the event
			if (((OpenCreatePathwaySmallMultiplesGroupDialogEvent) event)
					.getDimensionGroupDataContainer() != handler.getDataContainer())
				return;

			OpenCreatePathwaySmallMultiplesGroupDialogEvent openCreatePathwaySmallMultiplesGroupDialogevent = (OpenCreatePathwaySmallMultiplesGroupDialogEvent) event;
			handler.openCreatePathwaySmallMultiplesGroupDialog(
					openCreatePathwaySmallMultiplesGroupDialogevent
							.getDimensionGroupDataContainer(),
					openCreatePathwaySmallMultiplesGroupDialogevent.getDimensionPerspective());
		}
	}
}
