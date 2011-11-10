package org.caleydo.core.event.data;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.AEvent;

/**
 * This Event should be triggered when the dimension groups of a data domain change.
 * 
 * @author Partl
 */
public class DimensionGroupsChangedEvent
	extends AEvent {

	private IDataDomain dataDomain;

	public DimensionGroupsChangedEvent(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public boolean checkIntegrity() {
		// TODO Auto-generated method stub
		return true;
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public IDataDomain getDataDomain() {
		return dataDomain;
	}

}
