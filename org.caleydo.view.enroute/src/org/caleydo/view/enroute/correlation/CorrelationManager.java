/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.mappeddataview.ContentRenderer;
import org.caleydo.view.enroute.mappeddataview.overlay.IDataCellOverlayProvider;

/**
 * @author Christian
 *
 */
public class CorrelationManager {

	private boolean isCorrelationCalculationActive = false;
	private DataCellInfo dataCellInfo1;
	private DataCellInfo dataCellInfo2;
	private IDataCellOverlayProvider overlay1;
	private IDataCellOverlayProvider overlay2;

	private final GLEnRoutePathway enRoute;

	public CorrelationManager(GLEnRoutePathway enRoute) {
		this.enRoute = enRoute;
		enRoute.addEventListener(this);
	}

	/**
	 * @param isCorrelationCalculationActive
	 *            setter, see {@link isCorrelationCalculationActive}
	 */
	public void setCorrelationCalculationActive(boolean isCorrelationCalculationActive) {
		this.isCorrelationCalculationActive = isCorrelationCalculationActive;
	}

	/**
	 * @return the isCorrelationCalculationActive, see {@link #isCorrelationCalculationActive}
	 */
	public boolean isCorrelationCalculationActive() {
		return isCorrelationCalculationActive;
	}

	/**
	 * 
	 * 
	 * @param contentRenderer
	 * @return The overlay that was assigned to the data cell of the content renderer. Can be null.
	 */
	public IDataCellOverlayProvider getOverlayProvider(ContentRenderer contentRenderer) {

		if (dataCellInfo1 != null && isSelectedDataCell(contentRenderer, dataCellInfo1)) {
			return overlay1;
		} else if (dataCellInfo2 != null && isSelectedDataCell(contentRenderer, dataCellInfo2)) {
			return overlay2;
		}
		return null;
	}

	private boolean isSelectedDataCell(ContentRenderer contentRenderer, DataCellInfo info) {
		if (contentRenderer.getDataDomain() != info.dataDomain
				|| contentRenderer.getResolvedRowID() != info.rowID
				|| contentRenderer.getColumnPerspective().getVirtualArray().size() != info.columnPerspective
						.getVirtualArray().size()) {
			return false;
		}
		if (contentRenderer.getForeignColumnPerspective() != null) {
			if (info.foreignColumnPerspective != null) {
				if (contentRenderer.getForeignColumnPerspective().getDataDomain() != info.foreignColumnPerspective
						.getDataDomain()) {
					return false;
				}

			} else {
				return false;
			}
		}
		// Due to layout changes the column perspective object can change -> we have to test its content
		List<Integer> copyList = new ArrayList<>(contentRenderer.getColumnPerspective().getVirtualArray().getIDs());
		for (Integer id : info.columnPerspective.getVirtualArray()) {
			if (!copyList.contains(id))
				return false;
			// This way we make sure that duplicates are counted correctly
			copyList.remove(id);
		}

		return true;
	}

	@ListenTo
	public void onShowDataClassification(ShowOverlayEvent event) {
		if (event.isFirstCell) {
			this.dataCellInfo1 = event.getInfo();
			this.overlay1 = event.getOverlay();
		} else {
			this.dataCellInfo2 = event.getInfo();
			this.overlay2 = event.getOverlay();
		}
		enRoute.setDisplayListDirty();
	}

	public static void b(NumericalDataClassifier c) {

	}

	public static void b(CategoricalDataClassifier c) {

	}

	@ListenTo
	public void onStartCorrelationCalculation(StartCorrelationCalculationEvent event) {
		isCorrelationCalculationActive = true;
		enRoute.setDisplayListDirty();
	}

	@ListenTo
	public void onEndCorrelationCalculation(EndCorrelationCalculationEvent event) {
		isCorrelationCalculationActive = false;
		dataCellInfo1 = null;
		dataCellInfo2 = null;
		overlay1 = null;
		overlay2 = null;
		enRoute.setDisplayListDirty();
	}

}
