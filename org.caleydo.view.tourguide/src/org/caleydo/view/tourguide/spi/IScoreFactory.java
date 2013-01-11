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
package org.caleydo.view.tourguide.spi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

/**
 * extension point for creating new score implementations
 *
 * @author Samuel Gratzl
 *
 */
public interface IScoreFactory {
	/**
	 * creates a dialog for creating a new score of this type
	 * 
	 * @param shell
	 * @param receiver
	 *            the receiver to use for the {@link AddScoreColumnEvent} event
	 * @return
	 */
	Dialog createCreateDialog(Shell shell, ScoreQueryUI receiver);

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

	boolean supports(EDataDomainQueryMode mode);

	public static final class ScoreEntry implements Iterable<IScore> {
		private final String label;
		private final Collection<IScore> scores;

		public ScoreEntry(String label, IScore... scores) {
			super();
			this.label = label;
			this.scores = Arrays.asList(scores);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Iterable#iterator()
		 */
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

