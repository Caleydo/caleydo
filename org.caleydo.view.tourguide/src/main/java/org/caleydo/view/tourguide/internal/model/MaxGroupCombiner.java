/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.model;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.api.score.GroupSelectors;
import org.caleydo.view.tourguide.internal.score.ExternalIDTypeScore;
import org.caleydo.view.tourguide.spi.score.IDecoratedScore;
import org.caleydo.view.tourguide.spi.score.IGroupBasedScore;
import org.caleydo.view.tourguide.spi.score.IGroupScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.spi.score.IStratificationScore;
import org.caleydo.vis.lineup.data.ADoubleFunction;
import org.caleydo.vis.lineup.model.IRow;


/**
 * the current strategy up to now is to show just a single stratification but here the maximal value for a group score,
 *
 * therefore if we have a group score
 *
 * @author Samuel Gratzl
 *
 */
public class MaxGroupCombiner extends ADoubleFunction<IRow> {

	private final IScore score;

	/**
	 * @param score
	 */
	public MaxGroupCombiner(IScore score) {
		this.score = score;
	}

	@Override
	public double applyPrimitive(IRow in) {
		AScoreRow row = (AScoreRow) in;
		if (score instanceof IStratificationScore && !(score instanceof IGroupScore)) {
			return score.apply(row, null); // as group independent
		}
		if (score instanceof ExternalIDTypeScore && !((ExternalIDTypeScore) score).isCompatible(row.getIdType())) {
			// working on dimension ids just once
			return score.apply(row, null);
		}
		Group group = selectImpl(row, score);
		return score.apply(row, group);
	}

	

	private static Group selectImpl(AScoreRow row, IScore score) {
		IGroupBasedScore sg = null;
		if (score instanceof IGroupBasedScore)
			sg = (IGroupBasedScore) score;
		if (score instanceof IDecoratedScore && ((IDecoratedScore) score).getUnderlying() instanceof IGroupBasedScore)
			sg = (IGroupBasedScore) ((IDecoratedScore) score).getUnderlying();
		if (sg != null)
			return sg.select(row, row.getGroups());
		return GroupSelectors.MAX.select(score, row, row.getGroups());
	}

	public static Group select(IRow in, IScore score) {
		if (score == null)
			return null;
		AScoreRow row = (AScoreRow) in;
		if (score instanceof IStratificationScore && !(score instanceof IGroupScore)) {
			return null;
		}

		return selectImpl(row, score);
	}

}
