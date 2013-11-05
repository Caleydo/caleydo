/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal.gui;

import java.util.Map;
import java.util.Objects;

import org.caleydo.core.internal.Activator;
import org.caleydo.core.internal.MyPreferences;
import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 1st wizard page: The user has to choose the type of project, if she wants to create a new project or load an existing
 * one, or load sample data
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ChooseProjectTypePage extends WizardPage implements Listener {

	public static final String PAGE_NAME = "Project Wizard";

	private static final int WIDTH = 400;

	private final Map<String, IStartupAddon> addons;

	private TabFolder tabFolder;

	/**
	 * Constructor.
	 *
	 * @param addons2
	 */
	public ChooseProjectTypePage(Map<String, IStartupAddon> addons) {
		super(PAGE_NAME, PAGE_NAME, null);

		this.addons = addons;

		this.setImageDescriptor(Activator.getImageDescriptor("resources/wizard/wizard.png"));
		this.setDescription("What data do you want to load?");

		this.setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		this.tabFolder = new TabFolder(composite, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = WIDTH;
		tabFolder.setLayoutData(gridData);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// When changing the page should be set complete by default
				setPageComplete(true);
				updateButtons();
			}
		});

		String previous = MyPreferences.getLastChosenProjectMode();

		for (Map.Entry<String, IStartupAddon> addon : addons.entrySet()) {
			Composite tabContent = addon.getValue().create(tabFolder, this, this);
			if (tabContent == null)
				continue;
			tabContent.pack();
			TabItem item = new TabItem(tabFolder, SWT.NONE);
			item.setText(addon.getKey());
			item.setControl(tabContent);
			item.setData(Pair.make(addon.getKey(), addon.getValue()));
			if (Objects.equals(previous, addon.getKey()))
				tabFolder.setSelection(item);
		}
		tabFolder.pack();

		this.setControl(composite);
		composite.pack();
	}

	/**
	 * @return
	 */
	public Pair<String, IStartupAddon> getSelectedAddon() {
		int tab = tabFolder.getSelectionIndex();
		if (tab < 0)
			return null;
		Pair<String, IStartupAddon> r = (Pair<String, IStartupAddon>) tabFolder.getItem(tab).getData();
		return r;
	}

	@Override
	public boolean isPageComplete() {
		if (getSelectedAddon().getSecond().validate())
			return super.isPageComplete();
		return false;
	}

	@Override
	public void handleEvent(Event event) {
		updateButtons();
	}

	private void updateButtons() {
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
	}

}
