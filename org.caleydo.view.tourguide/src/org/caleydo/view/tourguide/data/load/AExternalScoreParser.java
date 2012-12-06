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

import org.caleydo.core.io.parser.ascii.ATextParser;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.data.serialize.ISerializeableScore;

abstract class AExternalScoreParser<T extends AExternalScoreParseSpecification, K> extends ATextParser implements
		SafeCallable<Collection<? extends ISerializeableScore>> {
	private static final Logger log = Logger.create(AExternalScoreParser.class);

	protected final T spec;

	private final Collection<ISerializeableScore> result = new ArrayList<>();

	@Override
	public Collection<ISerializeableScore> call() {
		if (!this.loadData())
			return Collections.emptyList();
		return result;
	}

	public AExternalScoreParser(T spec) {
		super(spec.getDataSourcePath());
		this.spec = spec;
	}


	@Override
	protected void parseFile(BufferedReader reader) throws IOException {
		swtGuiManager.setProgressBarText("Loading ranking");

		if (spec.getDataSourcePath() == null) {
			log.info("No path for ranking specified");
			return;
		}

		if (spec.isRankParsing())
			parseRank(reader);
		else
			parseScore(reader);

	}

	/**
	 * @param reader
	 * @throws IOException
	 */
	private void parseScore(BufferedReader reader) throws IOException {
		final List<Integer> columnsToRead = spec.getColumns();

		List<String> labels = new ArrayList<>();

		if (columnsToRead.size() == 1) {
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
		HashMap<K, Float>[] scores = new HashMap[columnsToRead.size()];
		for (int i = 0; i < scores.length; ++i)
			scores[i] = new HashMap<>();

		String line;
		while ((line = reader.readLine()) != null) {
			// read ID
			String[] columns = line.split(spec.getDelimiter());

			K mappedID = extractID(columns[spec.getColumnOfRowIds()]);
			if (mappedID == null)
				continue;
			// read data
			for (int i = 0; i < columnsToRead.size(); ++i) {
				String score = columns[columnsToRead.get(i)];
				Float s = parseFloat(score);
				if (s == null)
					continue;
				if (scores[i].containsKey(mappedID)) {
					System.err.println("double");
				}
				scores[i].put(mappedID, s);
			}
		}

		for (int i = 0; i < scores.length; ++i) {
			Map<K, Float> s = scores[i];
			if (s.isEmpty())
				continue;
			if (spec.isNormalizeScores())
				s = normalizeScores(s);
			result.add(createScore(labels.get(i), false, s));
		}
	}
	/**
	 * @param hashMap
	 */
	protected final Map<K, Float> normalizeScores(Map<K, Float> map) {
		if (map.size() <= 1)
			return map;

		// 1. find min/max
		Iterator<Float> it = map.values().iterator();
		float min, max;

		min = max = it.next();
		while (it.hasNext()) {
			float v = it.next();
			if (v < min)
				min = v;
			else if (v > max)
				max = v;
		}
		// 2. transform
		for (Map.Entry<K, Float> entry : map.entrySet()) {
			float v = entry.getValue();
			entry.setValue((v - min) / (max - min));
		}
		return map;
	}

	/**
	 * @param reader
	 * @throws IOException
	 */
	private void parseRank(BufferedReader reader) throws IOException {
		skipLines(reader, spec.getNumberOfHeaderLines());

		List<K> ranks = new ArrayList<>(this.numberOfLinesInFile < 0 ? 100 : this.numberOfLinesInFile);

		String line;
		while ((line = reader.readLine()) != null) {
			// read ID
			String[] columns = line.split(spec.getDelimiter());

			K mappedID = extractID(columns[spec.getColumnOfRowIds()]);
			if (mappedID == null)
				continue;
			// read data
			ranks.add(mappedID);
		}

		if (ranks.isEmpty())
			return;


		float delta;
		if (spec.isNormalizeScores()) { // convert to score
			delta = -1.f / (ranks.size() - 1);
		} else { // use rank
			delta = 1.f;
		}
		Map<K, Float> scores = new HashMap<>(ranks.size());

		float act = 1.f;
		for (K rank : ranks) {
			scores.put(rank, act);
			act += delta;
		}
		result.add(createScore(spec.getRankingName(), !spec.isNormalizeScores(), scores));
	}

	protected abstract K extractID(String originalID);

	protected abstract ISerializeableScore createScore(String label, boolean isRank, Map<K, Float> scores);

	protected final static Float parseFloat(String score) {
		if (score == null || score.trim().isEmpty())
			return null;
		try {
			return Float.parseFloat(score);
		} catch (NumberFormatException e) {
			log.warn("can't parse: " + score);
			return null;
		}
	}

	private static void skipLines(BufferedReader reader, int count) throws IOException {
		for (int i = 0; i < count; i++) {
			reader.readLine();
		}
	}
}
