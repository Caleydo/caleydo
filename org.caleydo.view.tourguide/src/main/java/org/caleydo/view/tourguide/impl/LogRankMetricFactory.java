/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.impl;

import static org.caleydo.view.tourguide.api.query.EDataDomainQueryMode.STRATIFICATIONS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.DataDomainOracle.ClinicalVariable;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.DefaultLabelProvider;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.DefaultComputedGroupScore;
import org.caleydo.view.tourguide.api.score.GroupSelectors;
import org.caleydo.view.tourguide.api.score.MultiScore;
import org.caleydo.view.tourguide.api.state.ABrowseState;
import org.caleydo.view.tourguide.api.state.BrowseOtherState;
import org.caleydo.view.tourguide.api.state.EWizardMode;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.PreviewRenderer;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.api.util.ui.CaleydoLabelProvider;
import org.caleydo.view.tourguide.impl.algorithm.LogRank;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.spi.score.IDecoratedScore;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.config.RankTableConfigBase;
import org.caleydo.vis.lineup.model.mapping.PiecewiseMapping;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Sets;

/**
 * @author Samuel Gratzl
 *
 */
public class LogRankMetricFactory implements IScoreFactory {
	@Override
	public void fillStateMachine(IStateMachine stateMachine, List<TablePerspective> existing, EWizardMode mode,
			TablePerspective source) {

		IState start = stateMachine.get(IStateMachine.ADD_STRATIFICATIONS);

		if (mode == EWizardMode.GLOBAL) {
			BothUpdateLogRankState browse = new BothUpdateLogRankState();
			stateMachine.addState("LogRankBrowse", browse);
			IState target = stateMachine.addState("LogRank", new CreateLogRankState(browse));
			stateMachine.addTransition(start, new SimpleTransition(target, "Based on log-rank test score (survival)",
					null));
		} else if (mode == EWizardMode.INDEPENDENT) {
			IState browseStratification = stateMachine.get(IStateMachine.BROWSE_STRATIFICATIONS);
			stateMachine.addTransition(start, new CreateLogRankTransition(browseStratification, source));
		}
	}

	@Override
	public Iterable<ScoreEntry> createGroupEntries(TablePerspective strat, Group group) {
		return Collections.emptyList();
	}

	@Override
	public Iterable<ScoreEntry> createStratEntries(TablePerspective strat) {
		return Collections.emptyList();
	}

	@Override
	public Dialog createCreateDialog(Shell shell, Object receiver) {
		return new CreateLogRankScoreDialog(shell, receiver);
	}

	@Override
	public boolean supports(EDataDomainQueryMode mode) {
		return mode == EDataDomainQueryMode.STRATIFICATIONS;
	}

	private static MultiScore createLogRankScore(TablePerspective numerical) {
		int dimId = numerical.getDimensionPerspective().getVirtualArray().get(0);
		String label = String.format("Sig. change of %s", numerical.getLabel());
		ATableBasedDataDomain clinical = numerical.getDataDomain();
		LogRankMetric metric = new LogRankMetric("LogRank", dimId, clinical);
		LogRankPValue pvalue = new LogRankPValue("-log(p-value)", metric);

		MultiScore multiScore = new MultiScore(label, wrap(clinical.getColor()),
 darker(clinical.getColor()),
				RankTableConfigBase.NESTED_MODE);
		multiScore.add(pvalue);
		multiScore.add(metric);
		return multiScore;
	}

	private class CreateLogRankState extends BrowseOtherState {
		private final BothUpdateLogRankState target;

		public CreateLogRankState(BothUpdateLogRankState target) {
			super("Select a numerical value in the LineUp as a starting point for finding a stratification.");
			this.target = target;
		}

		@Override
		public void onUpdateOther(TablePerspective tablePerspective, IReactions adapter) {
			TablePerspective numerical = tablePerspective;
			adapter.replaceTemplate(new PreviewRenderer(adapter.createPreview(numerical), adapter.getGLView(),
					"Browse for a stratification"));

			MultiScore multiScore = createLogRankScore(numerical);
			adapter.addScoreToTourGuide(STRATIFICATIONS, multiScore);
			target.numerical = numerical;
			adapter.switchTo(target);
		}
	}

	private class BothUpdateLogRankState extends ABrowseState {
		private TablePerspective numerical;

		public BothUpdateLogRankState() {
			super(EDataDomainQueryMode.STRATIFICATIONS, "From list");
		}

		@Override
		public void onUpdateStratification(TablePerspective tablePerspective, IReactions adapter) {
			TablePerspective tp = tablePerspective;
			if (DataDomainOracle.isCategoricalDataDomain(tp.getDataDomain()))
				adapter.replaceTemplate(tp, new CategoricalDataConfigurer(tp), true);
			else
				adapter.replaceTemplate(tp, null, true);
			adapter.replaceClinicalTemplate(tp.getRecordPerspective(), numerical, true, true);
		}
	}

	private class CreateLogRankTransition implements ITransition {
		private final IState target;
		private final TablePerspective numerical;

		public CreateLogRankTransition(IState target, TablePerspective numerical) {
			this.target = target;
			this.numerical = numerical;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public String getDisabledReason() {
			return null;
		}

		@Override
		public String getLabel() {
			return "Based on log-rank test score (survival)";
		}

		@Override
		public void apply(IReactions adapter) {
			adapter.addScoreToTourGuide(STRATIFICATIONS, createLogRankScore(numerical));
			adapter.switchTo(target);
		}
	}

	public static class LogRankMetric extends DefaultComputedGroupScore {
		private final Integer clinicalVariable;

		public LogRankMetric(String label, final Integer clinicalVariable, final ATableBasedDataDomain clinical) {
			super(label, new IGroupAlgorithm() {
				final IGroupAlgorithm underlying = LogRank.get(clinicalVariable, clinical);

				@Override
				public void init(IProgressMonitor monitor) {
					underlying.init(monitor);
				}

				@Override
				public IDType getTargetType(IComputeElement a, IComputeElement b) {
					return underlying.getTargetType(a, b);
				}

				@Override
				public String getAbbreviation() {
					return underlying.getAbbreviation();
				}

				@Override
				public String getDescription() {
					return "Log Rank of ";
				}

				@Override
				public double compute(Set<Integer> a, Group ag, Set<Integer> b, Group bg, IProgressMonitor monitor) {
					// me versus the rest
					return underlying.compute(a, ag, Sets.difference(b, a), bg, monitor);
				}
			}, null, GroupSelectors.MAX_ABS, wrap(clinical.getColor()), darker(clinical
					.getColor()));
			this.clinicalVariable = clinicalVariable;
		}

		public Integer getClinicalVariable() {
			return clinicalVariable;
		}

		@Override
		public PiecewiseMapping createMapping() {
			return new PiecewiseMapping(0, Float.NaN);
		}
	}

	public static class LogRankPValue extends DefaultLabelProvider implements IRegisteredScore, IDecoratedScore {
		private final LogRankMetric logRankScore;

		public LogRankPValue(String label, LogRankMetric logRankScore) {
			super(label);
			this.logRankScore = logRankScore;
		}

		@Override
		public void onRegistered() {

		}

		@Override
		public String getAbbreviation() {
			return "LR-P";
		}

		@Override
		public String getDescription() {
			return "Log Rank p-Value of " + getLabel();
		}

		@Override
		public Color getColor() {
			return logRankScore.getColor().darker();
		}

		@Override
		public Color getBGColor() {
			return logRankScore.getBGColor();
		}

		@Override
		public PiecewiseMapping createMapping() {
			return Utils.createPValueMapping();
		}

		@Override
		public boolean supports(EDataDomainQueryMode mode) {
			return mode == EDataDomainQueryMode.STRATIFICATIONS;
		}

		@Override
		public IScore getUnderlying() {
			return logRankScore;
		}

		@Override
		public final double apply(IComputeElement elem, Group g) {
			return LogRank.getPValue(logRankScore.apply(elem, g));
		}

		public Integer getClinicalVariable() {
			return logRankScore.getClinicalVariable();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((logRankScore == null) ? 0 : logRankScore.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LogRankPValue other = (LogRankPValue) obj;
			if (logRankScore == null) {
				if (other.logRankScore != null)
					return false;
			} else if (!logRankScore.equals(other.logRankScore))
				return false;
			return true;
		}
	}

	class CreateLogRankScoreDialog extends Dialog {

		private final Object receiver;

		private Text labelUI;
		private ComboViewer clinicialVariablesUI;

		public CreateLogRankScoreDialog(Shell shell, Object receiver) {
			super(shell);
			this.receiver = receiver;
		}

		@Override
		public void create() {
			super.create();
			this.getShell().setText("Add a log rank metric");
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

			new Label(c, SWT.NONE).setText("Clinical Variable: ");
			this.clinicialVariablesUI = new ComboViewer(c, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
			this.clinicialVariablesUI.setContentProvider(ArrayContentProvider.getInstance());
			this.clinicialVariablesUI.setLabelProvider(new CaleydoLabelProvider());
			this.clinicialVariablesUI.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

			List<ClinicalVariable> vars = new ArrayList<>(DataDomainOracle.getClinicalVariables());
			for(Iterator<ClinicalVariable> it = vars.iterator(); it.hasNext(); ) {
				if (it.next().getDataClass() != EDataClass.NATURAL_NUMBER)
					it.remove();
			}
			this.clinicialVariablesUI.setInput(vars);

			return c;
		}

		@Override
		protected void okPressed() {
			if (!validate())
				return;
			save();
			super.okPressed();
		}

		private boolean validate() {
			if (clinicialVariablesUI.getSelection() == null)
				MessageDialog.openError(getParentShell(), "A clinicial variable is required",
						"A clinicial variable is required");
			return true;
		}

		private void save() {
			String label = labelUI.getText();
			ClinicalVariable var = (ClinicalVariable) ((IStructuredSelection) clinicialVariablesUI.getSelection()).getFirstElement();
			if (label == null || label.trim().isEmpty())
				label = var.getLabel();

			LogRankMetric metric = new LogRankMetric(label, var.getDimId(), var.getDataDomain());
			LogRankPValue pvalue = new LogRankPValue(label + " (P-V)", metric);

			EventPublisher.trigger(new AddScoreColumnEvent(metric, pvalue).to(receiver));
		}
	}

	private static Color wrap(org.caleydo.core.util.color.Color color) {
		return new Color(color.r, color.g, color.b, color.a);
	}

	private static Color darker(org.caleydo.core.util.color.Color color) {
		Color c = new Color(color.r * 0.8f, color.g * 0.8f, color.b * 0.8f, color.a);
		return c;
	}

}

