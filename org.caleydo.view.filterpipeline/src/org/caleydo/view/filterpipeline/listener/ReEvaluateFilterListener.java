/**
 * 
 */
package org.caleydo.view.filterpipeline.listener;

import org.caleydo.core.data.filter.event.ReEvaluateRecordFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateDimensionFilterListEvent;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.view.filterpipeline.SetFilterTypeEvent.FilterType;
import org.caleydo.view.filterpipeline.GLFilterPipeline;

/**
 * @author Thomas Geymayer
 *
 */
public class ReEvaluateFilterListener
	extends AEventListener<GLFilterPipeline>
{

	@Override
	public void handleEvent(AEvent event)
	{
		if( event instanceof ReEvaluateRecordFilterListEvent )
			handler.handleReEvaluateFilter(FilterType.CONTENT);
		else if( event instanceof ReEvaluateDimensionFilterListEvent )
			handler.handleReEvaluateFilter(FilterType.STORAGE);
	}

}
