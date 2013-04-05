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
package org.caleydo.core.data.selection;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;

import com.google.common.base.Preconditions;

/**
 * a mixin container class for handling table perspective their updates and their selection in a single class
 *
 * important: annotate the field of this element with the {@link DeepScan} annotation to ensure that the listener will
 * be created
 *
 * @author Samuel Gratzl
 *
 */
public final class TablePerspectiveSelectionMixin {
	private final TablePerspective tablePerspective;
	private final SelectionManager recordSelectionManager;
	private final SelectionManager dimensionSelectionManager;
	private final ITablePerspectiveMixinCallback callback;

	public TablePerspectiveSelectionMixin(TablePerspective tablePerspective, ITablePerspectiveMixinCallback callback) {
		Preconditions.checkNotNull(tablePerspective, "have a valid tablePerspective");
		this.tablePerspective = tablePerspective;
		this.recordSelectionManager = tablePerspective.getDataDomain().cloneRecordSelectionManager();
		this.dimensionSelectionManager = tablePerspective.getDataDomain().cloneDimensionSelectionManager();
		this.callback = callback;
	}

	/**
	 * @return the tablePerspective, see {@link #tablePerspective}
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	public final ATableBasedDataDomain getDataDomain() {
		return tablePerspective.getDataDomain();
	}

	@ListenTo
	private void onSelectionUpdate(SelectionUpdateEvent event) {
		if (event.getSender() == this) // ignore event sent by myself
			return;
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
			callback.onSelectionUpdate(recordSelectionManager);
		} else if (selectionDelta.getIDType() == dimensionIDType) {
			dimensionSelectionManager.setDelta(selectionDelta);
			callback.onSelectionUpdate(dimensionSelectionManager);
		}
	}

	@ListenTo
	private void onSelectionCommand(SelectionCommandEvent event) {
		if (event.getSender() == this) // ignore event sent by myself
			return;
		IDCategory idCategory = event.getIdCategory();
		ATableBasedDataDomain dataDomain = getDataDomain();
		if (idCategory == dataDomain.getRecordIDCategory()) {
			recordSelectionManager.executeSelectionCommand(event.getSelectionCommand());
			callback.onSelectionUpdate(recordSelectionManager);
		} else if (idCategory == dataDomain.getDimensionIDCategory()) {
			dimensionSelectionManager.executeSelectionCommand(event.getSelectionCommand());
			callback.onSelectionUpdate(dimensionSelectionManager);
		}
	}

	public void fireSelectionDelta(IDType type) {
		if (recordSelectionManager.getIDType().equals(type))
			fireRecordSelectionDelta();
		if (dimensionSelectionManager.getIDType().equals(type))
			fireDimensionSelectionDelta();
	}

	public void fireRecordSelectionDelta() {
		fireSelectionDelta(recordSelectionManager);
	}

	public void fireDimensionSelectionDelta() {
		fireSelectionDelta(dimensionSelectionManager);
	}

	/**
	 * @return the recordSelectionManager, see {@link #recordSelectionManager}
	 */
	public SelectionManager getRecordSelectionManager() {
		return recordSelectionManager;
	}

	/**
	 * @return the dimensionSelectionManager, see {@link #dimensionSelectionManager}
	 */
	public SelectionManager getDimensionSelectionManager() {
		return dimensionSelectionManager;
	}

	public SelectionManager getSelectionManager(IDType type) {
		if (recordSelectionManager.getIDType().equals(type))
			return recordSelectionManager;
		if (dimensionSelectionManager.getIDType().equals(type))
			return dimensionSelectionManager;
		return null;
	}

	private void fireSelectionDelta(SelectionManager manager) {
		SelectionDelta selectionDelta = manager.getDelta();
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setEventSpace(tablePerspective.getDataDomain().getDataDomainID());
		event.setSelectionDelta(selectionDelta);
		EventPublisher.trigger(event);
	}

	@ListenTo
	private void onRecordVAUpdate(RecordVAUpdateEvent event) {
		if (tablePerspective.hasRecordPerspective(event.getPerspectiveID())) {
			callback.onVAUpdate(tablePerspective);
		}
	}

	@ListenTo
	private void onDimensionVAUpdate(DimensionVAUpdateEvent event) {
		if (tablePerspective.hasDimensionPerspective(event.getPerspectiveID())) {
			callback.onVAUpdate(tablePerspective);
		}
	}

	public interface ITablePerspectiveMixinCallback {
		void onSelectionUpdate(SelectionManager manager);

		void onVAUpdate(TablePerspective tablePerspective);
	}
}
