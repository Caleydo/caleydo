/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.selection;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.data.SelectionUpdateEvent;

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
public final class TablePerspectiveSelectionMixin extends MultiSelectionManagerMixin {
	private final TablePerspective tablePerspective;

	public TablePerspectiveSelectionMixin(TablePerspective tablePerspective, ITablePerspectiveMixinCallback callback) {
		super(callback);
		Preconditions.checkNotNull(tablePerspective, "have a valid tablePerspective");
		this.tablePerspective = tablePerspective;
		this.add(tablePerspective.getDataDomain().cloneRecordSelectionManager());
		this.add(tablePerspective.getDataDomain().cloneDimensionSelectionManager());
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
	public void fireRecordSelectionDelta() {
		fireSelectionDelta(getRecordSelectionManager());
	}

	public void fireDimensionSelectionDelta() {
		fireSelectionDelta(getDimensionSelectionManager());
	}

	/**
	 * @return the recordSelectionManager, see {@link #recordSelectionManager}
	 */
	public SelectionManager getRecordSelectionManager() {
		return get(0);
	}

	/**
	 * @return the dimensionSelectionManager, see {@link #dimensionSelectionManager}
	 */
	public SelectionManager getDimensionSelectionManager() {
		return get(1);
	}

	@Override
	protected SelectionUpdateEvent createEvent() {
		SelectionUpdateEvent event = super.createEvent();
		event.setEventSpace(getDataDomain().getDataDomainID());
		return event;
	}

	@ListenTo
	private void onRecordVAUpdate(RecordVAUpdateEvent event) {
		if (tablePerspective.hasRecordPerspective(event.getPerspectiveID())) {
			((ITablePerspectiveMixinCallback) callback).onVAUpdate(tablePerspective);
		}
	}

	@ListenTo
	private void onDimensionVAUpdate(DimensionVAUpdateEvent event) {
		if (tablePerspective.hasDimensionPerspective(event.getPerspectiveID())) {
			((ITablePerspectiveMixinCallback) callback).onVAUpdate(tablePerspective);
		}
	}

	public interface ITablePerspectiveMixinCallback extends ISelectionMixinCallback {
		void onVAUpdate(TablePerspective tablePerspective);
	}
}
