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
package org.caleydo.view.tourguide.internal.score;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.view.tourguide.api.score.ISerializeableScore;
import org.caleydo.view.tourguide.api.score.MultiScore;
import org.caleydo.view.tourguide.spi.score.IDecoratedScore;
import org.caleydo.view.tourguide.spi.score.IScore;

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

	// not weak for externals
	private final Set<ISerializeableScore> persistentScores = new HashSet<>();

	private Scores() {

	}

	/**
	 * adds a persistent score that will be serialized if it is not already there
	 *
	 * @param score
	 * @return the new added one or the existing one
	 */
	public synchronized ISerializeableScore addPersistentScoreIfAbsent(ISerializeableScore score) {
		this.persistentScores.add(score);
		return score;
	}

	public Iterable<ISerializeableScore> getPersistentScores() {
		return persistentScores;
	}
	/**
	 * flatten the given scores, {@link ICompositeScore} and {@link IDecoratedScore} will be flattened
	 *
	 * @param scores
	 * @return
	 */
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
			if (!result.add(s))
				continue;
			if (s instanceof MultiScore)
				queue.addAll(((MultiScore) s).getChildren());
			else if (s instanceof IDecoratedScore) {
				queue.add(((IDecoratedScore) s).getUnderlying());
			}
		}
		return result;

	}
}
