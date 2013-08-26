/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.external;

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
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.eclipse.core.runtime.SubMonitor;

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
		SubMonitor monitor = GeneralManager.get().createSubProgressMonitor();
		monitor.beginTask("Loading Ranking", this.calculateNumberOfLinesInFile() + 10);

		if (spec.getDataSourcePath() == null) {
			log.info("No path for ranking specified");
			monitor.done();
			return;
		}

		if (spec.isRankParsing())
			parseRank(reader, monitor);
		else
			parseScore(reader, monitor);
		monitor.done();

	}

	/**
	 * @param reader
	 * @param monitor
	 * @throws IOException
	 */
	private void parseScore(BufferedReader reader, SubMonitor monitor) throws IOException {
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
		monitor.worked(spec.getNumberOfHeaderLines());


		// parse data
		@SuppressWarnings("unchecked")
		HashMap<K, Double>[] scores = new HashMap[columnsToRead.size()];
		for (int i = 0; i < scores.length; ++i)
			scores[i] = new HashMap<>();

		String line;
		while ((line = reader.readLine()) != null) {
			// read ID
			String[] columns = line.split(spec.getDelimiter());
			monitor.worked(1);

			K mappedID = extractID(columns[spec.getColumnOfRowIds()]);
			if (mappedID == null)
				continue;
			// read data
			for (int i = 0; i < columnsToRead.size(); ++i) {
				String score = columns[columnsToRead.get(i)];
				Double s = parseDouble(score);
				if (s == null)
					continue;
				if (scores[i].containsKey(mappedID)) {
					System.err.println("double");
				}
				scores[i].put(mappedID, s);
			}
		}
		monitor.setWorkRemaining(10);
		for (int i = 0; i < scores.length; ++i) {
			Map<K, Double> s = scores[i];
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
	protected final Map<K, Double> normalizeScores(Map<K, Double> map) {
		if (map.size() <= 1)
			return map;

		// 1. find min/max
		Iterator<Double> it = map.values().iterator();
		double min, max;

		min = max = it.next();
		while (it.hasNext()) {
			double v = it.next();
			if (v < min)
				min = v;
			else if (v > max)
				max = v;
		}
		// 2. transform
		for (Map.Entry<K, Double> entry : map.entrySet()) {
			double v = entry.getValue();
			entry.setValue((v - min) / (max - min));
		}
		return map;
	}

	/**
	 * @param reader
	 * @param monitor
	 * @throws IOException
	 */
	private void parseRank(BufferedReader reader, SubMonitor monitor) throws IOException {
		skipLines(reader, spec.getNumberOfHeaderLines());
		monitor.worked(spec.getNumberOfHeaderLines());
		List<K> ranks = new ArrayList<>(this.numberOfLinesInFile < 0 ? 100 : this.numberOfLinesInFile);

		String line;
		while ((line = reader.readLine()) != null) {
			// read ID
			String[] columns = line.split(spec.getDelimiter());

			K mappedID = extractID(columns[spec.getColumnOfRowIds()]);
			monitor.worked(1);
			if (mappedID == null)
				continue;
			// read data
			ranks.add(mappedID);
		}

		if (ranks.isEmpty())
			return;

		monitor.setWorkRemaining(10);
		double delta;
		if (spec.isNormalizeScores()) { // convert to score
			delta = -1.f / (ranks.size() - 1);
		} else { // use rank
			delta = 1.f;
		}
		Map<K, Double> scores = new HashMap<>(ranks.size());

		double act = 1.f;
		for (K rank : ranks) {
			scores.put(rank, act);
			act += delta;
		}
		result.add(createScore(spec.getRankingName(), !spec.isNormalizeScores(), scores));
	}

	protected abstract K extractID(String originalID);

	protected abstract ISerializeableScore createScore(String label, boolean isRank, Map<K, Double> scores);

	protected final static Double parseDouble(String score) {
		if (score == null || score.trim().isEmpty())
			return null;
		try {
			return Double.parseDouble(score);
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
