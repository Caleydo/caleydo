package org.caleydo.view.visbricks.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.event.AEvent;

/**
 * Event for opening the dialog for creating pathway groups.
 * 
 * @author Partl
 * 
 */
public class OpenCreatePathwayGroupDialogEvent extends AEvent {

	private ATableBasedDataDomain sourceDataDomain;
	private RecordVirtualArray sourceRecordVA;
	private DimensionPerspective dimensionPerspective;

	/**
	 * @param sourceDataDomain
	 *            DataDomain of the input data that can be used for
	 *            statistically specifying the pathways in the dialog.
	 * @param sourceRecordVA
	 *            RecordVA of the input data that can be used for statistically
	 *            specifying the pathways in the dialog.
	 * @param dimensionPerspective
	 */
	public OpenCreatePathwayGroupDialogEvent(
			ATableBasedDataDomain sourceDataDomain,
			RecordVirtualArray sourceRecordVA,
			DimensionPerspective dimensionPerspective) {

		this.setSourceDataDomain(sourceDataDomain);
		this.setSourceRecordVA(sourceRecordVA);
		this.dimensionPerspective = dimensionPerspective;

	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param sourceDataDomain
	 *            DataDomain of the input data that can be used for
	 *            statistically specifying the pathways in the dialog.
	 */
	public void setSourceDataDomain(ATableBasedDataDomain sourceDataDomain) {
		this.sourceDataDomain = sourceDataDomain;
	}

	/**
	 * @return DataDomain of the input data that can be used for statistically
	 *         specifying the pathways in the dialog.
	 */
	public ATableBasedDataDomain getSourceDataDomain() {
		return sourceDataDomain;
	}

	/**
	 * @param sourceRecordVA
	 *            RecordVA of the input data that can be used for statistically
	 *            specifying the pathways in the dialog.
	 */
	public void setSourceRecordVA(RecordVirtualArray sourceRecordVA) {
		this.sourceRecordVA = sourceRecordVA;
	}

	/**
	 * @return RecordVA of the input data that can be used for statistically
	 *         specifying the pathways in the dialog.
	 */
	public RecordVirtualArray getSourceRecordVA() {
		return sourceRecordVA;
	}

}
