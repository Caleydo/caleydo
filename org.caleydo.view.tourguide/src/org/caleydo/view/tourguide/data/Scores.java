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
package org.caleydo.view.tourguide.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.caleydo.view.tourguide.data.score.ExternalScore;
import org.caleydo.view.tourguide.data.score.ICompositeScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.serialize.ISerializeableScore;

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public final class Scores {
	private static final Scores instance = new Scores();

	public static Scores get() {
		return instance;
	}

	// use a weak list for caching but also removing if we no longer need it
	private final Set<IScore> scores = Collections.newSetFromMap(new WeakHashMap<IScore, Boolean>());
	// not weak for externals
	private final Set<ExternalScore> persistentScores = new HashSet<>();

	private Scores() {
		// Map<Integer, Float> v = new HashMap<>();
		// v.put(1, 2.f);
		// v.put(2, 2.f);
		// addPersistentScoreIfAbsent(new ExternalScore("test", v));
	}

	public synchronized ExternalScore addPersistentScoreIfAbsent(ExternalScore score) {
		this.persistentScores.add(score);
		return addIfAbsent(score);
	}

	public Iterable<ExternalScore> getPersistentScores() {
		return persistentScores;
	}

	@SuppressWarnings("unchecked")
	public synchronized <T extends IScore> T addIfAbsent(T score) {
		if (this.scores.add(score))
			return score;
		else {
			for (IScore s : scores) {
				if (s.equals(score))
					return (T) s;
			}
			return null;
		}
	}

	public synchronized void remove(IScore score) {
		scores.remove(score);
		if (score instanceof ISerializeableScore)
			persistentScores.remove(score);
	}

	public synchronized Collection<IScore> getScoreIDs() {
		return new ArrayList<>(scores);
	}

	public static Collection<IScore> flatten(IScore... scores) {
		return flatten(Arrays.asList(scores));
	}

	/**
	 * flattens the given scores, i.e. flat composites to a big flat set
	 *
	 * @param scores
	 * @return
	 */
	public static Set<IScore> flatten(Iterable<IScore> scores) {
		Set<IScore> result = new HashSet<>();
		Deque<IScore> queue = Lists.newLinkedList(scores);
		while (!queue.isEmpty()) {
			IScore s = queue.pollFirst();
			if (result.add(s) && s instanceof ICompositeScore)
				queue.addAll(((ICompositeScore) s).getChildren());
		}
		return result;

	}

}
