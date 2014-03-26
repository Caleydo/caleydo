/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Christian
 *
 */
public abstract class AAverageBasedSummaryRenderer extends ADataRenderer {

	private static Integer rendererIDCounter = 0;

	protected Average average;
	protected int rendererID;

	/**
	 * @param contentRenderer
	 */
	public AAverageBasedSummaryRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
		if (contentRenderer.resolvedRowID == null)
			return;
		synchronized (rendererIDCounter) {
			rendererID = rendererIDCounter++;
		}
		average = TablePerspectiveStatistics.calculateAverage(contentRenderer.columnPerspective.getVirtualArray(),
				contentRenderer.dataDomain, contentRenderer.resolvedRowIDType, contentRenderer.resolvedRowID);

		registerPickingListeners();
	}

	protected void registerPickingListeners() {

		contentRenderer.parent.pickingListenerManager.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				contentRenderer.parent.sampleSelectionManager.clearSelection(SelectionType.SELECTION);

				contentRenderer.parent.sampleSelectionManager.addToType(SelectionType.SELECTION,
						contentRenderer.columnIDType, contentRenderer.columnPerspective.getVirtualArray().getIDs());
				contentRenderer.parent.sampleSelectionManager.triggerSelectionUpdateEvent();

				contentRenderer.parent.sampleGroupSelectionManager.clearSelection(SelectionType.SELECTION);

				contentRenderer.parent.sampleGroupSelectionManager.addToType(SelectionType.SELECTION,
						contentRenderer.group.getID());
				contentRenderer.parent.sampleGroupSelectionManager.triggerSelectionUpdateEvent();
				contentRenderer.parentView.setDisplayListDirty();
			}
		}, EPickingType.SAMPLE_GROUP_RENDERER.name(), rendererID);
	}


}
