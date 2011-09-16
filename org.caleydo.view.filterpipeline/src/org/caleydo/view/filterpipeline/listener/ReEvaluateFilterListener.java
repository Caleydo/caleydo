/**
 * 
 */
package org.caleydo.view.filterpipeline.listener;

import org.caleydo.core.data.filter.event.ReEvaluateDimensionFilterListEvent;
import org.caleydo.core.data.filter.event.ReEvaluateRecordFilterListEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.view.filterpipeline.SetFilterTypeEvent.FilterType;
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
			handler.handleReEvaluateFilter(FilterType.RECORD);
		else if( event instanceof ReEvaluateDimensionFilterListEvent )
			handler.handleReEvaluateFilter(FilterType.DIMENSION);
	}

}
