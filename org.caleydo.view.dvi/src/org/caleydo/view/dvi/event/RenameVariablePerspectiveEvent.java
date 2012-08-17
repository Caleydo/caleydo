/**
 * 
 */
package org.caleydo.view.dvi.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.event.AEvent;

/**
 * Event that triggers a dialog to rename a {@link AVariablePerspective}.
 * 
 * @author Christian Partl
 * 
 */
public class RenameVariablePerspectiveEvent extends AEvent {

	/**
	 * The perspective to be renamed.
	 */
	private String perspectiveID;

	/**
	 * The datadomain, the perspective specified by {@link #perspectiveID}
	 * belongs to.
	 */
	private ATableBasedDataDomain dataDomain;

	/**
	 * Determines, whether {@link #perspectiveID} specifies a
	 * {@link RecordPerspective} or a {@link DimensionPerspective}.
	 */
	private boolean isRecordPerspective;

	public RenameVariablePerspectiveEvent() {
	}

	public RenameVariablePerspectiveEvent(String perspectiveID,
			ATableBasedDataDomain dataDomain, boolean isRecordPerspective) {
		this.perspectiveID = perspectiveID;
		this.dataDomain = dataDomain;
		this.isRecordPerspective = isRecordPerspective;
	}

	@Override
	public boolean checkIntegrity() {
		return perspectiveID != null && dataDomain != null;
	}

	/**
	 * @param perspectiveID
	 *            setter, see {@link #perspectiveID}
	 */
	public void setPerspectiveID(String perspectiveID) {
		this.perspectiveID = perspectiveID;
	}

	/**
	 * @return the perspectiveID, see {@link #perspectiveID}
	 */
	public String getPerspectiveID() {
		return perspectiveID;
	}

	/**
	 * @param dataDomain
	 *            setter, see {@link #dataDomain}
	 */
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @param isRecordPerspective
	 *            setter, see {@link #isRecordPerspective}
	 */
	public void setRecordPerspective(boolean isRecordPerspective) {
		this.isRecordPerspective = isRecordPerspective;
	}

	/**
	 * @return the isRecordPerspective, see {@link #isRecordPerspective}
	 */
	public boolean isRecordPerspective() {
		return isRecordPerspective;
	}

}
