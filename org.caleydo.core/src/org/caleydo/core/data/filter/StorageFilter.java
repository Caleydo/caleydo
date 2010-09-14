package org.caleydo.core.data.filter;

import org.caleydo.core.data.filter.event.NewStorageFilterEvent;
import org.caleydo.core.data.filter.event.ReEvaluateStorageFilterListEvent;
import org.caleydo.core.data.virtualarray.StorageVAType;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.manager.GeneralManager;

/**
 * Static type for {@link Filter}s handling {@link StorageVirtualArray}s
 * @author Alexander Lex
 *
 */
public class StorageFilter
	extends Filter<StorageVAType, StorageVADelta> {

	public void updateFilterManager() {

		if (!isRegistered) {
			NewStorageFilterEvent filterEvent = new NewStorageFilterEvent();
			filterEvent.setFilter(this);
			filterEvent.setSender(this);
			filterEvent.setDataDomainType(dataDomain.getDataDomainType());

			GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
			isRegistered = true;
		}
		else {

			ReEvaluateStorageFilterListEvent reevaluateEvent = new ReEvaluateStorageFilterListEvent();
			// reevaluateEvent.addFilter(filter);
			reevaluateEvent.setSender(this);
			reevaluateEvent.setDataDomainType(dataDomain.getDataDomainType());

			GeneralManager.get().getEventPublisher().triggerEvent(reevaluateEvent);
		}
	}
}
