/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.spi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
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
	 * add the states and transition for the create wizard to the given {@link IStateMachine}
	 * 
	 * @param stateMachine
	 * @param existing
	 *            the list of table perspective already existing
	 * @param mode
	 * @param source
	 *            the source {@link TablePerspective} in case of {@link EWizardMode#DEPENDENT} and
	 *            {@link EWizardMode#INDEPENDENT}
	 */
	void fillStateMachine(IStateMachine stateMachine, List<TablePerspective> existing, EWizardMode mode,
			TablePerspective source);

	/**
	 * creates a dialog for creating a new score of this type
	 *
	 * @param shell
	 * @param receiver
	 *            the receiver to use for the {@link AddScoreColumnEvent} event
	 * @return
	 */
	Dialog createCreateDialog(Shell shell, Object receiver);

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
	boolean supports(EDataDomainQueryMode mode);

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

