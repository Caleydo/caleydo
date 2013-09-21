/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.external;

import static org.caleydo.core.util.collection.Pair.make;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.internal.event.ImportExternalScoreEvent;
import org.caleydo.view.tourguide.internal.score.ExternalGroupLabelScore;
import org.caleydo.view.tourguide.internal.score.ExternalIDTypeScore;
import org.caleydo.view.tourguide.internal.score.ExternalLabelScore;
/**
 * @author Samuel Gratzl
 *
 */
public class ExternalScoringDataDomainActionFactory {
	public static Collection<Pair<String, ? extends AEvent>> create(IDataDomain dataDomain, Object sender) {
		Collection<Pair<String,? extends AEvent>> r = new ArrayList<>(4);
		if (dataDomain instanceof ATableBasedDataDomain) {
			ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;
			r.add(make("Load Scoring for " + d.getDimensionIDCategory().getCategoryName(),
					new ImportExternalScoreEvent(d, true, ExternalIDTypeScore.class).from(sender)));
			r.add(make("Load Scoring for " + d.getRecordIDCategory().getCategoryName(), new ImportExternalScoreEvent(d,
					false, ExternalIDTypeScore.class).from(sender)));
			r.add(make("Load Grouping Scoring for " + d.getDimensionIDCategory().getCategoryName(),
					new ImportExternalScoreEvent(d, true, ExternalGroupLabelScore.class).from(sender)));
			r.add(make("Load Grouping Scoring for " + d.getRecordIDCategory().getCategoryName(),
					new ImportExternalScoreEvent(d, false, ExternalGroupLabelScore.class).from(sender)));
		} else {
			r.add(make("Load Row Scoring",
					new ImportExternalScoreEvent(dataDomain, false, ExternalLabelScore.class).from(sender)));
		}
		return r;
	}

}
