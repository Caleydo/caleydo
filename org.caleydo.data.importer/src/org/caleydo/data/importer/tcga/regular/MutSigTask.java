/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.data.importer.tcga.regular;

import java.util.concurrent.RecursiveTask;

import org.caleydo.core.util.color.Color;
import org.caleydo.data.importer.tcga.FirehoseProvider;
import org.caleydo.data.importer.tcga.TCGAFileInfo;
import org.caleydo.datadomain.genetic.TCGADefinitions;
import org.caleydo.view.tourguide.api.external.ScoreParseSpecification;
import org.caleydo.view.tourguide.api.score.ECombinedOperator;

/**
 * @author Samuel Gratzl
 *
 */
public class MutSigTask extends RecursiveTask<ScoreParseSpecification> {
	private static final long serialVersionUID = 193680768589791994L;
	private final FirehoseProvider fileProvider;

	public MutSigTask(FirehoseProvider fileProvider) {
		this.fileProvider = fileProvider;
	}

	@Override
	protected ScoreParseSpecification compute() {
		TCGAFileInfo mutsig = fileProvider.findMutSigReport();
		if (mutsig == null)
			return null;
		ScoreParseSpecification spec = new ScoreParseSpecification(mutsig.getFile().getAbsolutePath());


		spec.setDelimiter("\t");
		spec.setNumberOfHeaderLines(1);
		spec.setContainsColumnIDs(false);
		spec.setColumnOfRowIds(0);

		spec.setRowIDSpecification(TCGADefinitions.createGeneIDSpecificiation());

		// q-value
		spec.setRankingName("MutSig Q-Value");
		spec.addColum(14);
		spec.setNormalizeScores(false);
		spec.setOperator(ECombinedOperator.MEAN);
		spec.setColor(Color.GRAY);
		spec.setMappingMin(0);
		spec.setMappingMax(1);

		return spec;
	}

}
