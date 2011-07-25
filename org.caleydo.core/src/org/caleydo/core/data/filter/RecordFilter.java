package org.caleydo.core.data.filter;

import org.caleydo.core.data.filter.event.NewRecordFilterEvent;
import org.caleydo.core.data.filter.event.ReEvaluateRecordFilterListEvent;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.manager.GeneralManager;

/**
 * Static type for {@link Filter}s handling {@link RecordVirtualArray}s.
 * 
 * @author Alexander Lex
 */
public class RecordFilter
	extends Filter<RecordVADelta> {

	public void updateFilterManager() {

		if (!isRegistered()) {
			NewRecordFilterEvent filterEvent = new NewRecordFilterEvent();
			filterEvent.setFilter(this);
			filterEvent.setSender(this);
			filterEvent.setDataDomainID(dataDomain.getDataDomainID());

			GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
			
			isRegistered = true;
		}
		else {

			ReEvaluateRecordFilterListEvent reevaluateEvent = new ReEvaluateRecordFilterListEvent();
			// reevaluateEvent.addFilter(filter);
			reevaluateEvent.setSender(this);
			reevaluateEvent.setDataDomainID(dataDomain.getDataDomainID());

			GeneralManager.get().getEventPublisher().triggerEvent(reevaluateEvent);
		}
	}
}
