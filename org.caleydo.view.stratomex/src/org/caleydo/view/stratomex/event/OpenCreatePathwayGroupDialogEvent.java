/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.stratomex.event;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.AEvent;

/**
 * Event for opening the dialog for creating pathway groups.
 *
 * @author Partl
 *
 */
public class OpenCreatePathwayGroupDialogEvent
	extends AEvent {

	private ATableBasedDataDomain sourceDataDomain;
	private VirtualArray sourceRecordVA;

	/**
	 * @param sourceDataDomain DataDomain of the input data that can be used for
	 *            statistically specifying the pathways in the dialog.
	 * @param sourceRecordVA RecordVA of the input data that can be used for
	 *            statistically specifying the pathways in the dialog.
	 */
	public OpenCreatePathwayGroupDialogEvent(ATableBasedDataDomain sourceDataDomain,
			VirtualArray sourceRecordVA) {

		this.setSourceDataDomain(sourceDataDomain);
		this.setSourceRecordVA(sourceRecordVA);
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @param sourceDataDomain DataDomain of the input data that can be used for
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
	 * @param sourceRecordVA RecordVA of the input data that can be used for
	 *            statistically specifying the pathways in the dialog.
	 */
	public void setSourceRecordVA(VirtualArray sourceRecordVA) {
		this.sourceRecordVA = sourceRecordVA;
	}

	/**
	 * @return RecordVA of the input data that can be used for statistically
	 *         specifying the pathways in the dialog.
	 */
	public VirtualArray getSourceRecordVA() {
		return sourceRecordVA;
	}

}
