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

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.tourguide.data.DataDomainQuery;
import org.caleydo.view.tourguide.data.Scores;
import org.caleydo.view.tourguide.data.score.AdjustedRankScore;
import org.caleydo.view.tourguide.data.score.CollapseScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.vendingmachine.ScoreQueryUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
public class CreateAdjustedRankScoreDialog extends TitleAreaDialog {

	private final ScoreQueryUI sender;

	private Text labelUI;
	private ComboViewer dataDomainUI;
	private ComboViewer stratificationUI;

	public CreateAdjustedRankScoreDialog(Shell shell, ScoreQueryUI sender) {
		super(shell);
		this.sender = sender;
	}

	@Override
	public void create() {
		super.create();
		this.setTitle("Create a new Adjusted Rank Index Score");
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
		this.dataDomainUI.setInput(DataDomainQuery.allDataDomains());
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
			List<TablePerspective> data = new ArrayList<TablePerspective>(sender.getQuery().getQuery()
					.getStratifications(dataDomain));
			this.stratificationUI.setInput(data);
			this.stratificationUI.getCombo().setEnabled(true);
		}
	}

	private static boolean isBlank(String t) {
		return t == null || t.trim().length() == 0;
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
		TablePerspective strat = (TablePerspective) ((IStructuredSelection) stratificationUI.getSelection())
				.getFirstElement();
		if (isBlank(label))
			label = strat.getLabel();

		IScore s;
		if (strat == null) { // score all
			Scores manager = Scores.get();
			CollapseScore composite = new CollapseScore(label);
			for (TablePerspective g : (Iterable<TablePerspective>) stratificationUI.getInput()) {
				composite.add(manager.addIfAbsent(new AdjustedRankScore(g)));
			}
			s = composite;
		} else { // score single
			s = Scores.get().addIfAbsent(new AdjustedRankScore(strat));
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new AddScoreColumnEvent(s, sender));
	}
}
