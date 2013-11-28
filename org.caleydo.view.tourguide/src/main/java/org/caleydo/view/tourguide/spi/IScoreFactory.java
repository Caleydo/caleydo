/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * extension point for creating new score implementations
 *
 * @author Samuel Gratzl
 *
 */
public interface IScoreFactory {
	/**
	 * returns entries to generate a context menu for the given stratification and group
	 *
	 * @param strat
	 * @param group
	 * @return an iterable with a pair label-score
	 */
	Iterable<ScoreEntry> createGroupEntries(TablePerspective strat, Group group);

	/**
	 * returns entries to generate a context menu for the given stratification
	 *
	 * @param strat
	 * @return an iterable with a pair label-score
	 */
	Iterable<ScoreEntry> createStratEntries(TablePerspective strat);

	/**
	 * determines, whether the current factory supports this {@link EDataDomainQueryMode} mode
	 *
	 * @param mode
	 * @return
	 */
	boolean supports(ITourGuideDataMode mode);

	public static final class ScoreEntry implements Iterable<IScore> {
		private final String label;
		private final Collection<IScore> scores;

		public ScoreEntry(String label, IScore... scores) {
			super();
			this.label = label;
			this.scores = Arrays.asList(scores);
		}

		@Override
		public Iterator<IScore> iterator() {
			return scores.iterator();
		}

		/**
		 * @return the label, see {@link #label}
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * @return the scores, see {@link #scores}
		 */
		public Collection<IScore> getScores() {
			return scores;
		}

	}
}

