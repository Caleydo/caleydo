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

import java.awt.Color;
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
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.DefaultComputedGroupScore;
import org.caleydo.view.tourguide.api.state.IStateMachine;
import org.caleydo.view.tourguide.api.util.ui.CaleydoLabelProvider;
import org.caleydo.view.tourguide.impl.algorithm.LogRank;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.spi.IScoreFactory;
import org.caleydo.view.tourguide.spi.algorithm.IComputeElement;
import org.caleydo.view.tourguide.spi.algorithm.IGroupAlgorithm;
import org.caleydo.view.tourguide.spi.score.IDecoratedScore;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
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
	public void fillStateMachine(IStateMachine stateMachine, Object eventReceiver) {
		// TODO Auto-generated method stub

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

	public static class LogRankMetric extends DefaultComputedGroupScore {
		private final Integer clinicalVariable;

		public LogRankMetric(String label, final Integer clinicalVariable, final ATableBasedDataDomain clinical) {
			super(label, new IGroupAlgorithm() {
				final IGroupAlgorithm underlying = LogRank.get(clinicalVariable, clinical);

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
				public float compute(Set<Integer> a, Set<Integer> b) {
					// me versus the rest
					return underlying.compute(a, Sets.difference(b, a));
				}
			}, null, wrap(clinical.getColor()), darker(clinical.getColor()));
			this.clinicalVariable = clinicalVariable;
		}

		public Integer getClinicalVariable() {
			return clinicalVariable;
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
			PiecewiseMapping m = new PiecewiseMapping(0, 1);
			m.put(0, 1);
			m.put(1, 0);
			return m;
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
		public final float apply(IComputeElement elem, Group g) {
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

			ATableBasedDataDomain dataDomain = DataDomainOracle.getClinicalDataDomain();

			LogRankMetric metric = new LogRankMetric(label, var.getDimId(), dataDomain);
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

