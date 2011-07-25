package org.caleydo.core.data.filter;

import org.caleydo.core.data.filter.event.NewDimensionFilterEvent;
import org.caleydo.core.data.filter.event.ReEvaluateDimensionFilterListEvent;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.manager.GeneralManager;

/**
 * Static type for {@link Filter}s handling {@link DimensionVirtualArray}s
 * 
 * @author Alexander Lex
 */
public class DimensionFilter
	extends Filter<DimensionVADelta> {

	public void updateFilterManager() {

		if (!isRegistered) {
			NewDimensionFilterEvent filterEvent = new NewDimensionFilterEvent();
			filterEvent.setFilter(this);
			filterEvent.setSender(this);
			filterEvent.setDataDomainID(dataDomain.getDataDomainID());

			GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
			isRegistered = true;
		}
		else {

			ReEvaluateDimensionFilterListEvent reevaluateEvent = new ReEvaluateDimensionFilterListEvent();
			// reevaluateEvent.addFilter(filter);
			reevaluateEvent.setSender(this);
			reevaluateEvent.setDataDomainID(dataDomain.getDataDomainID());

			GeneralManager.get().getEventPublisher().triggerEvent(reevaluateEvent);
		}
	}
}
