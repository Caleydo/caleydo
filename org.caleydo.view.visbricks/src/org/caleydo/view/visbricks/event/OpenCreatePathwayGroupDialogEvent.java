package org.caleydo.view.visbricks.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.event.AEvent;

public class OpenCreatePathwayGroupDialogEvent extends AEvent {

	private ATableBasedDataDomain sourceDataDomain;
	private RecordVirtualArray sourceRecordVA;
	private DimensionPerspective dimensionPerspective;

	public OpenCreatePathwayGroupDialogEvent(ATableBasedDataDomain sourceDataDomain,
			RecordVirtualArray sourceRecordVA, DimensionPerspective dimensionPerspective) {

		this.setSourceDataDomain(sourceDataDomain);
		this.setSourceRecordVA(sourceRecordVA);
		this.dimensionPerspective = dimensionPerspective;

	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	public void setSourceDataDomain(ATableBasedDataDomain sourceDataDomain) {
		this.sourceDataDomain = sourceDataDomain;
	}

	public ATableBasedDataDomain getSourceDataDomain() {
		return sourceDataDomain;
	}

	public void setSourceRecordVA(RecordVirtualArray sourceRecordVA) {
		this.sourceRecordVA = sourceRecordVA;
	}

	public RecordVirtualArray getSourceRecordVA() {
		return sourceRecordVA;
	}

}
