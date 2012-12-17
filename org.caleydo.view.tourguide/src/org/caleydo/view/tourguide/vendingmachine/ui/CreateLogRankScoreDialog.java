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
package org.caleydo.view.tourguide.vendingmachine.ui;

import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.data.score.IGroupScore;
import org.caleydo.view.tourguide.data.score.LogRankScore;
import org.caleydo.view.tourguide.vendingmachine.ScoreQueryUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Samuel Gratzl
 *
 */
public class CreateLogRankScoreDialog extends ACreateGroupScoreDialog {

	private ComboViewer clinicalUI;

	public CreateLogRankScoreDialog(Shell shell, ScoreQueryUI sender) {
		super(shell, sender);
	}

	@Override
	protected String getLabel() {
		return "Log Rank Score";
	}

	@Override
	protected void addTypeSpecific(Composite c) {
		new Label(c, SWT.NONE).setText("Data Domain: ");
		this.clinicalUI = new ComboViewer(c, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		this.clinicalUI.setContentProvider(ArrayContentProvider.getInstance());
		this.clinicalUI.setLabelProvider(new LabelProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element) {
				return ((Pair<?, String>) element).getSecond();
			}
		});
		this.clinicalUI.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.clinicalUI.setInput(DataDomainOracle.getClinicalVariables());
	}

	@Override
	protected boolean validate() {
		boolean valid = super.validate();
		if (clinicalUI.getSelection() == null) {
			MessageDialog.openError(getParentShell(), "A clinical variable is required",
					"A clinical variable is required");
			valid = false;
		}
		return valid;

	}

	@Override
	protected IGroupScore createScore(String label, TablePerspective strat, Group g) {
		@SuppressWarnings("unchecked")
		Pair<Integer, String> clinical = (Pair<Integer, String>) ((IStructuredSelection) clinicalUI.getSelection())
				.getFirstElement();
		return new LogRankScore(label, clinical.getFirst(), strat, g);
	}
}
