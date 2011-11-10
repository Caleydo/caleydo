package org.caleydo.core.event.data;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.AEvent;

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
