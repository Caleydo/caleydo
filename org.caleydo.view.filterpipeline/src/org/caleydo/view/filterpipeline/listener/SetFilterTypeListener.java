/**
 * 
 */
package org.caleydo.view.filterpipeline.listener;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.filterpipeline.SetFilterTypeEvent;
import org.caleydo.view.filterpipeline.GLFilterPipeline;

/**
 * @author Thomas Geymayer
 *
 */
public class SetFilterTypeListener
	extends AEventListener<GLFilterPipeline>
{

	@Override
	public void handleEvent(AEvent event)
	{
		if( event instanceof SetFilterTypeEvent )
			handler.handleSetFilterTypeEvent
			(
				((SetFilterTypeEvent)event).getType()
			);
	}

}
