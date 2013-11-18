/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.t;

import java.util.Collection;

import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.InhomogenousPerspectiveRow;
import org.caleydo.view.tourguide.internal.mode.VariableDataMode;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.stratomex.event.UpdateNumericalPreviewEvent;

/**
 * facade / adapter to {@link GLStratomex} to hide the communication details
 *
 * @author Samuel Gratzl
 *
 */
public class VariableStratomexAdapter extends AStratomexAdapter {
	public VariableStratomexAdapter() {
		super(new VariableDataMode());
	}

	@Override
	public String getPartName() {
		return "Variables (Dependent)";
	}

	@Override
	public String getSecondaryID() {
		return "OTHER";
	}

	@Override
	protected void updatePreview(AScoreRow old, AScoreRow new_, Collection<IScore> visibleColumns, IScore sortedBy) {
		updateNumerical((InhomogenousPerspectiveRow) old, (InhomogenousPerspectiveRow) new_, visibleColumns, sortedBy);
	}

	private void updateNumerical(InhomogenousPerspectiveRow old, InhomogenousPerspectiveRow new_,
			Collection<IScore> visibleColumns, IScore sortedBy) {
		if (new_ != null && hasOne()) {
			UpdateNumericalPreviewEvent event = new UpdateNumericalPreviewEvent(new_.asTablePerspective());
			event.to(getAddin());
			triggerEvent(event);
		}
	}
}
