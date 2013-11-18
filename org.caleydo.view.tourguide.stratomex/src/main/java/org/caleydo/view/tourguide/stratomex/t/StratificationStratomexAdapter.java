/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.t;

import java.util.Collection;
import java.util.Objects;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.tourguide.api.adapter.DataDomainModes;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.api.model.MaxGroupCombiner;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.stratomex.event.UpdateStratificationPreviewEvent;

/**
 * facade / adapter to {@link GLStratomex} to hide the communication details
 *
 * @author Samuel Gratzl
 *
 */
public class StratificationStratomexAdapter extends AStratomexAdapter {
	public StratificationStratomexAdapter() {
		super(DataDomainModes.STRATIFICATIONS);
	}

	@Override
	public String getPartName() {
		return "Stratification";
	}

	@Override
	public String getSecondaryID() {
		return "STRATIFICATIONS";
	}

	@Override
	protected void updatePreview(AScoreRow old, AScoreRow new_, Collection<IScore> visibleColumns, IScore sortedBy) {
		updateTableBased((ITablePerspectiveScoreRow) old, (ITablePerspectiveScoreRow) new_, visibleColumns, sortedBy);
	}

	private void updateTableBased(ITablePerspectiveScoreRow old, ITablePerspectiveScoreRow new_,
			Collection<IScore> visibleColumns, IScore sortedBy) {
		TablePerspective strat = new_ == null ? null : new_.asTablePerspective();
		Group group = new_ == null ? null : MaxGroupCombiner.select(new_, sortedBy);

		// handle stratification changes
		if (currentPreview != null && strat != null) { // update
			if (currentPreview.equals(strat)) {
				if (!Objects.equals(currentPreviewGroup, group)) {
					unhighlightBrick(currentPreview, currentPreviewGroup);
					highlightBrick(currentPreview, group, true);
					currentPreviewGroup = group;
				}
			} else { // not same stratification
				unhighlightBrick(currentPreview, currentPreviewGroup);
				if (contains(strat)) { // part of stratomex
					highlightBrick(strat, group, true);
				} else {
					updatePreview(strat, group, true);
				}
			}
		} else if (currentPreview != null) { // last
			removePreview();
		} else if (strat != null) { // first
			updatePreview(strat, group, true);
		}

		// highlight connection band
		if (strat != null) {
			highlightRows(new_, visibleColumns, group);
			unhighlightBand(null, null);
			highlightBand(new_, visibleColumns, group);
		} else if (old != null) {
			clearHighlightRows(old.getIdType(), old.getDataDomain());
			unhighlightBand(null, null);
		}
	}

	private void updatePreview(TablePerspective strat, Group group, boolean highlightBrick) {
		if (!hasOne())
			return;
		this.currentPreview = strat;
		UpdateStratificationPreviewEvent event = new UpdateStratificationPreviewEvent(strat);
		event.to(getAddin());
		triggerEvent(event);

		if (group != null || highlightBrick)
			highlightBrick(strat, group, false);
		currentPreviewGroup = group;
	}
}
