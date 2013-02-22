/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.internal.external;

import java.util.Map;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.internal.score.ExternalIDTypeScore;

public class ExternalIDTypeScoreParser extends AExternalScoreParser<ScoreParseSpecification, Integer> {
	private static final Logger log = Logger.create(ExternalIDTypeScoreParser.class);

	private final IDType sourceIDType;
	private final IDType targetIDType;
	private final IDMappingManager idMapper;
	private final IDTypeParsingRules parsingRules;


	public ExternalIDTypeScoreParser(ScoreParseSpecification spec, IDType targetIDType) {
		super(spec);
		this.targetIDType = targetIDType;
		this.sourceIDType = IDType.getIDType(spec.getRowIDSpecification().getIdType());
		this.idMapper = IDMappingManagerRegistry.get().getIDMappingManager(this.sourceIDType.getIDCategory());

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

		Set<Integer> mappedID = idMapper.getIDAsSet(sourceIDType, targetIDType, originalID);
		if (mappedID == null || mappedID.isEmpty()) {
			System.err.println("Could not map id: " + originalID);
			idMapper.getIDAsSet(sourceIDType, targetIDType, originalID);
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
	protected ExternalIDTypeScore createScore(String label, boolean isRank, Map<Integer, Float> scores) {
		return new ExternalIDTypeScore(label, spec, this.targetIDType, isRank, scores);
	}
}
