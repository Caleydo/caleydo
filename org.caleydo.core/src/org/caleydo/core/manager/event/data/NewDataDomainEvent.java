package org.caleydo.core.manager.event.data;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.manager.event.AEvent;

public class NewDataDomainEvent
	extends AEvent {
	
	private IDataDomain dataDomain;
	
	public NewDataDomainEvent(IDataDomain dataDomain) {
		this.setDataDomain(dataDomain);
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public IDataDomain getDataDomain() {
		return dataDomain;
	}

}
