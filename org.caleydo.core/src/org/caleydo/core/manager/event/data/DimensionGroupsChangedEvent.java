package org.caleydo.core.manager.event.data;

import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.event.AEvent;

/**
 * This Event should be triggered when the dimension groups of a data domain change.
 * 
 * @author Partl
 *
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
