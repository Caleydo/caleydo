/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.external;

import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.internal.external.ui.ImportExternalGroupLabelScoreDialog;
import org.caleydo.view.tourguide.internal.external.ui.ImportExternalIDTypeScoreDialog;
import org.caleydo.view.tourguide.internal.external.ui.ImportExternalLabelScoreDialog;
import org.caleydo.view.tourguide.internal.score.ExternalGroupLabelScore;
import org.caleydo.view.tourguide.internal.score.ExternalIDTypeScore;
import org.caleydo.view.tourguide.internal.score.ExternalLabelScore;
import org.caleydo.view.tourguide.internal.score.Scores;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.swt.widgets.Shell;

public final class ImportExternalScoreCommand implements Runnable {
	private final IDataDomain dataDomain;

	private final Object receiver;
	private final boolean inDimensionDirection;
	private final Class<? extends ISerializeableScore> type;


	public ImportExternalScoreCommand(IDataDomain dataDomain, boolean dimensionDirection,
			Class<? extends ISerializeableScore> type, Object scoreQueryUI) {
		this.dataDomain = dataDomain;
		this.inDimensionDirection = dimensionDirection;
		this.type = type;
		this.receiver = scoreQueryUI;
	}

	@Override
	public void run() {
		Collection<ISerializeableScore> scores;
		if (type == ExternalIDTypeScore.class)
			scores = importExternalIDScore();
		else if (type == ExternalGroupLabelScore.class)
			scores = importExternalLabelGroup();
		else if (type == ExternalLabelScore.class)
			scores = importExternalElementLabelGroup();
		else
			return; // unknown type

		final Scores scoreManager = Scores.get();

		IScore last = null;
		for (ISerializeableScore score : scores) {
			last = scoreManager.addPersistentScoreIfAbsent(score);
		}
		if (last != null)
			EventPublisher.trigger(new AddScoreColumnEvent(last).to(receiver));
	}

	private Collection<ISerializeableScore> importExternalIDScore() {
		ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;
		IDCategory category = inDimensionDirection ? d.getDimensionIDCategory() : d
				.getRecordIDCategory();
		ScoreParseSpecification spec = new ImportExternalIDTypeScoreDialog(new Shell(), category).call();
		if (spec == null)
			return Collections.emptyList();
		IDType target = inDimensionDirection ? d.getDimensionIDType() : d.getRecordIDType();

		Collection<ISerializeableScore> scores = new ExternalIDTypeScoreParser(spec, target).call();
		return scores;
	}

	private Collection<ISerializeableScore> importExternalLabelGroup() {
		ATableBasedDataDomain d = (ATableBasedDataDomain) dataDomain;
		GroupLabelParseSpecification spec = new ImportExternalGroupLabelScoreDialog(new Shell(), d,
				inDimensionDirection).call();
		if (spec == null)
			return Collections.emptyList();
		Collection<ISerializeableScore> scores = new ExternalGroupLabelScoreParser(spec).call();
		return scores;
	}

	private Collection<ISerializeableScore> importExternalElementLabelGroup() {
		ExternalLabelParseSpecification spec = new ImportExternalLabelScoreDialog(new Shell(), dataDomain).call();
		if (spec == null)
			return Collections.emptyList();
		Collection<ISerializeableScore> scores = new ExternalLabelScoreParser(spec).call();
		return scores;
	}

}
