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
package org.caleydo.view.tourguide.impl;

import static org.caleydo.view.tourguide.api.query.EDataDomainQueryMode.STRATIFICATIONS;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.DefaultComputedReferenceStratificationScore;
import org.caleydo.view.tourguide.api.score.MultiScore;
import org.caleydo.view.tourguide.api.state.BrowseStratificationState;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.ISelectStratificationState;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.SimpleState;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.api.util.ui.CaleydoLabelProvider;
import org.caleydo.view.tourguide.impl.algorithm.AdjustedRandIndex;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Samuel Gratzl
 *
 */
public class AdjustedRandScoreFactory implements IScoreFactory {
	private final static Color color = Color.decode("#5fd3bc");
	private final static Color bgColor = Color.decode("#d5fff6");


	private IRegisteredScore create(String label, Perspective reference) {
		return new DefaultComputedReferenceStratificationScore(label, reference, AdjustedRandIndex.get(), null, color, bgColor) {
			@Override
			public PiecewiseMapping createMapping() {
				PiecewiseMapping m = new PiecewiseMapping(-1, 1);
				m.put(-1,1);
				m.put(0, 0);
				m.put(1, 1);
				return m;
			}
		};
	}

	@Override
	public void fillStateMachine(IStateMachine stateMachine, List<TablePerspective> existing, EWizardMode mode,
			TablePerspective source) {
		if (mode != EWizardMode.GLOBAL || existing.isEmpty()) // nothing to compare
			return;

		IState start = stateMachine.get(IStateMachine.ADD_STRATIFICATIONS);
		IState browse = stateMachine.addState("AdjustedRandBrowse", new UpdateAndBrowseAdjustedRand());
		IState target = stateMachine.addState("AdjustedRand", new CreateAdjustedRandState(browse));

		stateMachine.addTransition(start, new SimpleTransition(target,
				"Find similar to displayed stratification"));
	}

	@Override
	public Iterable<ScoreEntry> createGroupEntries(TablePerspective strat, Group group) {
		return Collections.emptyList();
	}

	@Override
	public Iterable<ScoreEntry> createStratEntries(TablePerspective strat) {
		return Collections
				.singleton(new ScoreEntry("Score column", (IScore) create(null, strat.getRecordPerspective())));
	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		return mode == STRATIFICATIONS;
	}

	@Override
	public Dialog createCreateDialog(Shell shell, Object receiver) {
		return new CreateAdjustedRandScoreDialog(shell, receiver);
	}

	private class CreateAdjustedRandState extends SimpleState implements ISelectStratificationState {
		private final IState target;

		public CreateAdjustedRandState(IState target) {
			super("Select query stratification by clicking on the header brick of one of the displayed columns\n"
					+ "Change query by clicking on other header brick at any time");
			this.target = target;
		}

		@Override
		public boolean apply(TablePerspective tablePerspective) {
			return true;
		}

		@Override
		public void select(TablePerspective tablePerspective, IReactions reactions) {
			reactions.addScoreToTourGuide(STRATIFICATIONS,
					create(null, tablePerspective.getRecordPerspective()));
			reactions.switchTo(target);
		}

		@Override
		public boolean isAutoSelect() {
			return false;
		}
	}

	private class UpdateAndBrowseAdjustedRand extends BrowseStratificationState implements ISelectStratificationState {
		public UpdateAndBrowseAdjustedRand() {
			super("Select a stratification in the Tour Guide to preview.\n" + "Then confirm or cancel your selection"
					+ "Change query by clicking on other brick at any time");
		}

		@Override
		public boolean apply(TablePerspective tablePerspective) {
			return true;
		}

		@Override
		public void select(TablePerspective tablePerspective, IReactions reactions) {
			reactions.addScoreToTourGuide(STRATIFICATIONS, create(null, tablePerspective.getRecordPerspective()));
		}

		@Override
		public boolean isAutoSelect() {
			return false;
		}
	}

	class CreateAdjustedRandScoreDialog extends Dialog {

		private final Object receiver;

		private Text labelUI;
		private ComboViewer dataDomainUI;
		private ComboViewer stratificationUI;

		public CreateAdjustedRandScoreDialog(Shell shell, Object sender) {
			super(shell);
			this.receiver = sender;
		}

		@Override
		public void create() {
			super.create();
			this.getShell().setText("Create a new Adjusted Rand Index Score");
			this.setBlockOnOpen(false);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite c = new Composite((Composite) super.createDialogArea(parent), SWT.NONE);
			c.setLayout(new GridLayout(2, false));
			c.setLayoutData(new GridData(GridData.FILL_BOTH));

			new Label(c, SWT.NONE).setText("Name: ");
			this.labelUI = new Text(c, SWT.BORDER);
			this.labelUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

			new Label(c, SWT.NONE).setText("Data Domain: ");
			this.dataDomainUI = new ComboViewer(c, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
			this.dataDomainUI.setContentProvider(ArrayContentProvider.getInstance());
			this.dataDomainUI.setLabelProvider(new CaleydoLabelProvider());
			this.dataDomainUI.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			this.dataDomainUI.setInput(EDataDomainQueryMode.STRATIFICATIONS.getAllDataDomains());
			this.dataDomainUI.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) selection.getFirstElement();
					updateStratifications(dataDomain);
					dataDomainUI.refresh();
				}
			});
			new Label(c, SWT.NONE).setText("Stratification: ");
			this.stratificationUI = new ComboViewer(c, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
			this.stratificationUI.setContentProvider(ArrayContentProvider.getInstance());
			this.stratificationUI.setLabelProvider(new CaleydoLabelProvider());
			this.stratificationUI.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			this.stratificationUI.getCombo().setEnabled(false);

			return c;
		}

		protected void updateStratifications(ATableBasedDataDomain dataDomain) {
			if (dataDomain == null) {
				this.stratificationUI.setInput(null);
				this.stratificationUI.getCombo().setEnabled(false);
			} else {
				ATableBasedDataDomain d = dataDomain;
				DataDomainOracle.initDataDomain(d);
				List<Perspective> data = new ArrayList<>(); // just stratifications

				for (String id : d.getTable().getRecordPerspectiveIDs()) {
					Perspective p = d.getTable().getRecordPerspective(id);
					if (p.isDefault())
						continue;
					data.add(p);
				}

				this.stratificationUI.setInput(data);
				this.stratificationUI.getCombo().setEnabled(true);
			}
		}

		@Override
		protected void okPressed() {
			if (!validate())
				return;
			save();
			super.okPressed();
		}

		private boolean validate() {
			if (dataDomainUI.getSelection() == null)
				MessageDialog.openError(getParentShell(), "A Data Domain is required", "A Data Domain is required");
			return true;
		}

		private void save() {
			String label = labelUI.getText();
			Perspective strat = (Perspective) ((IStructuredSelection) stratificationUI.getSelection())
					.getFirstElement();
			if (label == null || label.trim().isEmpty())
				label = strat.getLabel();

			IScore s;
			if (strat == null) { // score all
				MultiScore composite = new MultiScore(label, color, bgColor);
				@SuppressWarnings("unchecked")
				Iterable<Perspective> it = (Iterable<Perspective>) stratificationUI.getInput();
				for (Perspective g : it) {
					composite.add(AdjustedRandScoreFactory.this.create(null, g));
				}
				s = composite;
			} else { // score single
				s = AdjustedRandScoreFactory.this.create(null, strat);
			}
			EventPublisher.trigger(new AddScoreColumnEvent(s).to(receiver));
		}
	}
}
