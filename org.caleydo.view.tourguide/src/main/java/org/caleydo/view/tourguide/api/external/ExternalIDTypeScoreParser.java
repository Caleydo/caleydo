/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.external;

import java.util.Map;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.internal.score.ExternalIDTypeScore;

public class ExternalIDTypeScoreParser extends AExternalScoreParser<ScoreParseSpecification, Integer> {
	private static final Logger log = Logger.create(ExternalIDTypeScoreParser.class);

	private final IDType sourceIDType;
	private final IDType targetIDType;
	private final IIDTypeMapper<String, Integer> idMapper;
	private final IDTypeParsingRules parsingRules;


	public ExternalIDTypeScoreParser(ScoreParseSpecification spec, IDType targetIDType) {
		super(spec);
		this.targetIDType = targetIDType;
		this.sourceIDType = IDType.getIDType(spec.getRowIDSpecification().getIdType());
		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				this.sourceIDType.getIDCategory());
		idMapper = idMappingManager.getIDTypeMapper(sourceIDType, targetIDType);

		this.parsingRules = extractParsingRules(this.sourceIDType, spec.getRowIDSpecification());
	}

	private static IDTypeParsingRules extractParsingRules(IDType sourceIDType, IDSpecification idSpecification) {
		if (idSpecification.getIdTypeParsingRules() != null)
			return idSpecification.getIdTypeParsingRules();
		else if (sourceIDType.getIdTypeParsingRules() != null)
			return sourceIDType.getIdTypeParsingRules();
		return null;
	}

	@Override
	protected Integer extractID(String originalID) {
		originalID = convertID(originalID, parsingRules);

		Set<Integer> mappedID = idMapper.apply(originalID);
		if (mappedID == null || mappedID.isEmpty()) {
			System.err.println("Could not map id: " + originalID);
			idMapper.apply(originalID);
			return null;
		}
		if (mappedID.size() > 1) {
			log.warn("multi mapping: " + originalID + " to " + mappedID);
		}
		return mappedID.iterator().next();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.view.tourguide.data.load.AExternalScoreParser#addScore(java.lang.String, boolean, java.util.Map)
	 */
	@Override
	protected ExternalIDTypeScore createScore(String label, boolean isRank, Map<Integer, Double> scores) {
		return new ExternalIDTypeScore(label, spec, this.targetIDType, isRank, scores);
	}
}
