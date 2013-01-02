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
package org.caleydo.view.tourguide.api.score.ui;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.score.CollapseScore;
import org.caleydo.view.tourguide.api.util.ui.CaleydoLabelProvider;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;
import org.caleydo.view.tourguide.spi.score.IRegisteredScore;
import org.caleydo.view.tourguide.spi.score.IScore;
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

import com.google.common.collect.Lists;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ACreateGroupScoreDialog extends TitleAreaDialog {

	private static final Group ALL_GROUP = new Group();

	static {
		ALL_GROUP.setLabel("--ALL--");
	}

	private final ScoreQueryUI sender;

	private Text labelUI;
	private ComboViewer dataDomainUI;
	private ComboViewer stratificationUI;
	private ComboViewer groupUI;

	public ACreateGroupScoreDialog(Shell shell, ScoreQueryUI sender) {
		super(shell);
		this.sender = sender;
	}

	@Override
	public void create() {
		super.create();
		this.setTitle("Create a new " + getLabel());
		this.setBlockOnOpen(false);
	}

	protected abstract String getLabel();

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
		this.dataDomainUI.setInput(EDataDomainQueryMode.TABLE_BASED.getAllDataDomains());
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
		this.stratificationUI.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				TablePerspective dataDomain = (TablePerspective) selection.getFirstElement();
				updateGroups(dataDomain);
			}
		});
		this.stratificationUI.getCombo().setEnabled(false);

		new Label(c, SWT.NONE).setText("Group: ");
		this.groupUI = new ComboViewer(c, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		this.groupUI.setContentProvider(ArrayContentProvider.getInstance());
		this.groupUI.setLabelProvider(new CaleydoLabelProvider());
		this.groupUI.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.groupUI.getCombo().setEnabled(false);

		addTypeSpecific(c);

		return c;
	}

	protected abstract void addTypeSpecific(Composite c);

	protected void updateStratifications(ATableBasedDataDomain dataDomain) {
		if (dataDomain == null) {
			this.stratificationUI.setInput(null);
			this.stratificationUI.getCombo().setEnabled(false);
		} else {
			List<TablePerspective> data = new ArrayList<TablePerspective>(sender.getQuery().getQuery()
					.getPerspectives(dataDomain));
			this.stratificationUI.setInput(data);
			this.stratificationUI.getCombo().setEnabled(true);
		}
	}

	protected void updateGroups(TablePerspective perspective) {
		if (perspective == null) {
			this.groupUI.setInput(null);
			this.groupUI.getCombo().setEnabled(false);
		} else {
			List<Group> data = Lists.newArrayList(perspective.getRecordPerspective().getVirtualArray().getGroupList());
			data.add(0, ALL_GROUP);
			this.groupUI.setInput(data);
			this.groupUI.getCombo().setEnabled(true);
		}
	}

	@Override
	protected void okPressed() {
		if (!validate())
			return;
		save();
		super.okPressed();
	}

	protected boolean validate() {
		boolean valid = true;
		if (stratificationUI.getSelection() == null) {
			MessageDialog.openError(getParentShell(), "A stratification is required", "A stratification is required");
			valid = false;
		}
		return valid;
	}

	protected abstract IRegisteredScore createScore(String label, TablePerspective strat, Group g);

	private void save() {
		String label = labelUI.getText();
		TablePerspective strat = (TablePerspective) ((IStructuredSelection) stratificationUI.getSelection())
				.getFirstElement();
		Group group = groupUI.getSelection() == null ? null : (Group) ((IStructuredSelection) groupUI.getSelection())
				.getFirstElement();
		IScore s;
		if (group == null || group == ALL_GROUP) { // score all
			CollapseScore composite = new CollapseScore(label == null ? strat.getLabel() : label);
			for (Group g : strat.getRecordPerspective().getVirtualArray().getGroupList()) {
				composite.add(createScore(null, strat, g));
			}
			s = composite;
		} else { // score single
			s = createScore(label, strat, group);
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new AddScoreColumnEvent(s, sender));
	}
}
