package org.caleydo.core.data.datadomain.event;


import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.AEvent;

public class AskRemoveDataDomainEvent extends AEvent {

	public AskRemoveDataDomainEvent() {

	}

	public AskRemoveDataDomainEvent(IDataDomain dataDomain) {
		setDataDomainID(dataDomain.getDataDomainID());
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
