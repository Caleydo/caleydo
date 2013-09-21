/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.score;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

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

	public synchronized boolean removePersistentScore(ISerializeableScore score) {
		return this.persistentScores.remove(score);
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
