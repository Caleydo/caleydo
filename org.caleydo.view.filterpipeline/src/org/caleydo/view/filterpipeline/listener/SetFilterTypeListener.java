/**
 * 
 */
package org.caleydo.view.filterpipeline.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.filterpipeline.SetFilterTypeEvent;
import org.caleydo.view.filterpipeline.GLFilterPipeline;

/**
 * @author Thomas Geymayer
 *
 */
public class SetFilterTypeListener
	extends AEventListener<GLFilterPipeline>
{

	@Override
	public void handleEvent(AEvent rawEvent)
	{
		if( !(rawEvent instanceof SetFilterTypeEvent) )
			return;
		
		SetFilterTypeEvent event = ((SetFilterTypeEvent)rawEvent);
		
		if( handler.getID() != event.getTargetViewId() )
			return;

		handler.handleSetFilterTypeEvent( event.getType() );
	}

}
