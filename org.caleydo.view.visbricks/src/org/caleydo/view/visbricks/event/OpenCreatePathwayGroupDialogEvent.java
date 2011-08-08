package org.caleydo.view.visbricks.event;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.event.AEvent;

public class OpenCreatePathwayGroupDialogEvent extends AEvent {
	
	private IDataDomain sourceDataDomain;
	private RecordVirtualArray sourceRecordVA;

	public OpenCreatePathwayGroupDialogEvent(IDataDomain sourceDataDomain,
			RecordVirtualArray sourceRecordVA) {

		this.setSourceDataDomain(sourceDataDomain);
		this.setSourceRecordVA(sourceRecordVA);
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public void setSourceDataDomain(IDataDomain sourceDataDomain) {
		this.sourceDataDomain = sourceDataDomain;
	}

	public IDataDomain getSourceDataDomain() {
		return sourceDataDomain;
	}

	public void setSourceRecordVA(RecordVirtualArray sourceRecordVA) {
		this.sourceRecordVA = sourceRecordVA;
	}

	public RecordVirtualArray getSourceRecordVA() {
		return sourceRecordVA;
	}

}
