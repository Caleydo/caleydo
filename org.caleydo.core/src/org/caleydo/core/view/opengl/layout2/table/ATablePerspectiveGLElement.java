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
package org.caleydo.core.view.opengl.layout2.table;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ATablePerspectiveGLElement extends GLElement {
	protected final TablePerspective tablePerspective;
	protected final SelectionManager recordSelectionManager;
	protected final SelectionManager dimensionSelectionManager;

	public ATablePerspectiveGLElement(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
		this.recordSelectionManager = tablePerspective.getDataDomain().cloneRecordSelectionManager();
		this.dimensionSelectionManager = tablePerspective.getDataDomain().cloneDimensionSelectionManager();
	}

	protected final ATableBasedDataDomain getDataDomain() {
		return tablePerspective.getDataDomain();
	}

	@ListenTo
	private void onSelectionUpdate(SelectionUpdateEvent event) {
		SelectionDelta selectionDelta = event.getSelectionDelta();
		ATableBasedDataDomain dataDomain = getDataDomain();
		IDType recordIDType = dataDomain.getRecordIDType();
		IDType dimensionIDType = dataDomain.getDimensionIDType();

		if (selectionDelta.getIDType().getIDCategory().equals(recordIDType.getIDCategory())) {
			// Check for type that can be handled
			if (selectionDelta.getIDType() != recordIDType) {
				selectionDelta = DeltaConverter.convertDelta(getDataDomain().getRecordIDMappingManager(), recordIDType,
						selectionDelta);
			}
			recordSelectionManager.setDelta(selectionDelta);
			repaintAll();
		} else if (selectionDelta.getIDType() == dimensionIDType) {
			dimensionSelectionManager.setDelta(selectionDelta);
			repaintAll();
		}
	}

	@ListenTo
	private void onRecordVAUpdate(RecordVAUpdateEvent event) {
		if (tablePerspective.hasRecordPerspective(event.getPerspectiveID())) {
			onTablePerspectiveChanged();
		}
	}

	@ListenTo
	private void onDimensionVAUpdate(DimensionVAUpdateEvent event) {
		if (tablePerspective.hasDimensionPerspective(event.getPerspectiveID())) {
			onTablePerspectiveChanged();
		}
	}

	@ListenTo
	private void onSelectionCommand(SelectionCommandEvent event) {
		IDCategory idCategory = event.getIdCategory();
		ATableBasedDataDomain dataDomain = getDataDomain();
		if (idCategory == dataDomain.getRecordIDCategory())
			recordSelectionManager.executeSelectionCommand(event.getSelectionCommand());
		else if (idCategory == dataDomain.getDimensionIDCategory())
			dimensionSelectionManager.executeSelectionCommand(event.getSelectionCommand());
		else
			return;
		onTablePerspectiveChanged();
	}

	protected void onTablePerspectiveChanged() {
		repaintAll();
	}
}
