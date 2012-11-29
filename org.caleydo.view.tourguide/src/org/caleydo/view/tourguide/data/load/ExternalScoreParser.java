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
package org.caleydo.view.tourguide.data.load;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.parser.ascii.ATextParser;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.data.score.ExternalScore;

public class ExternalScoreParser extends ATextParser implements SafeCallable<Collection<ExternalScore>> {
	private static final Logger log = Logger.create(ExternalScoreParser.class);

	private final ScoreParseSpecification spec;

	private final IDType sourceIDType;
	private final IDType targetIDType;
	private final IDMappingManager idMapper;
	private final IDTypeParsingRules parsingRules;

	private final Collection<ExternalScore> result = new ArrayList<>();

	public ExternalScoreParser(ScoreParseSpecification spec, IDType targetIDType) {
		super(spec.getDataSourcePath());
		this.spec = spec;
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
	public Collection<ExternalScore> call() {
		if (!this.loadData())
			return Collections.emptyList();
		return result;
	}

	private Integer extractID(String[] columns) {
		String originalID = columns[spec.getColumnOfRowIds()];
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

	@Override
	protected void parseFile(BufferedReader reader) throws IOException {
		if (!sourceIDType.getIDCategory().equals(targetIDType.getIDCategory())) {
			log.error("Can not map between specified IDTypes: " + sourceIDType + ", " + targetIDType);
			throw new IllegalArgumentException("Can not map between specified IDTypes: " + sourceIDType + ", "
					+ targetIDType);
		}

		swtGuiManager.setProgressBarText("Loading ranking for " + targetIDType);
		float progressBarFactor = 100f / numberOfLinesInFile;

		if (spec.getDataSourcePath() == null) {
			log.info("No path for ranking specified");
			return;
		}
		final List<Integer> columnsToRead = spec.getColumns();

		List<String> labels = new ArrayList<>();

		if (columnsToRead.size() <= 1) {
			labels.add(spec.getRankingName());// skip header lines
			skipLines(reader, spec.getNumberOfHeaderLines());
		} else if (spec.getRowOfColumnIDs() == null) { // no dedicated columns, generate one
			for(int i = 0; i < columnsToRead.size(); ++i)
				labels.add(spec.getRankingName() + "" + (i + 1));
			skipLines(reader, spec.getNumberOfHeaderLines());
		} else {
			skipLines(reader, spec.getRowOfColumnIDs()-1);
			String[] columns = reader.readLine().split(spec.getDelimiter());
			for(int col: columnsToRead)
				labels.add(columns[col]);
			skipLines(reader, spec.getNumberOfHeaderLines() - 1 - spec.getRowOfColumnIDs());
		}

		// parse data
		@SuppressWarnings("unchecked")
		HashMap<Integer, Float>[] scores = new HashMap[Math.max(1, columnsToRead.size())];
		for (int i = 0; i < scores.length; ++i)
			scores[i] = new HashMap<>();

		int lineCounter = 0;
		String line;
		while ((line = reader.readLine()) != null) {
			// read ID
			String[] columns = line.split(spec.getDelimiter());

			Integer mappedID = extractID(columns);
			if (mappedID == null)
				continue;
			// read data
			if (columnsToRead.isEmpty()) {
				scores[0].put(mappedID, lineCounter + 1.f);
			} else {
				for (int i = 0; i < columnsToRead.size(); ++i) {
					String score = columns[columnsToRead.get(i)];
					scores[i].put(mappedID, parseFloat(score));
				}
			}
			lineCounter++;

			if (lineCounter % 100 == 0) {
				swtGuiManager.setProgressBarPercentage((int) (progressBarFactor * lineCounter));
			}
		}

		// convert to external scores
		if (columnsToRead.size() == 0) { // ranking
			ExternalScore s = new ExternalScore(spec.getRankingName(), this.targetIDType, spec.getOperator(), true,
					normalizeScores(scores[0]));
			this.result.add(s);
		} else { // multiple scores
			for (int i = 0; i < columnsToRead.size(); ++i) {
				ExternalScore s = new ExternalScore(labels.get(i), this.targetIDType, spec.getOperator(), false,
						normalizeScores(scores[i]));
				this.result.add(s);
			}
		}

	}

	private Map<Integer, Float> normalizeScores(Map<Integer, Float> map) {
		if (!spec.isNormalizeScores() || map.size() <= 1)
			return map;
		Iterator<Float> it = map.values().iterator();
		// FIXME implement me
		return map;
	}

	private Float parseFloat(String score) {
		if (score == null || score.trim().isEmpty())
			return Float.NaN;
		try {
			return Float.parseFloat(score);
		} catch (NumberFormatException e) {
			log.warn("can't parse: " + score);
			return Float.NaN;
		}
	}

	private static void skipLines(BufferedReader reader, int count) throws IOException {
		for (int i = 0; i < count; i++) {
			reader.readLine();
		}
	}
}
