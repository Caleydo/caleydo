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
package org.caleydo.core.startup.internal;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.PreferenceManager;
import org.caleydo.core.serialize.ProjectManager;
import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * {@link IStartupAddon} for loading a project or the recent project
 * 
 * @author Samuel Gratzl
 * 
 */
public class LoadProjectStartupAddon implements IStartupAddon {
	private static final String PROPERTY_CALEYDO_PROJECT_LOCATION = "caleydo.project.location";
	static final String PREFERENCE_LOAD_CALEYDO_PROJECT = "load.caleydo.project";

	private static final int WIDTH = 400;

	@Argument(metaVar = "PROJECT", usage = "the caledyo project to load")
	private File projectLocation;

	@Option(name = "-loadRecent")
	private boolean loadRecentProject;

	private Text projectLocationUI;

	private Button recentProject;

	@Override
	public boolean init() {
		{// check preference store -> used for open project switch workspace thing
			PreferenceStore store = PreferenceManager.get().getPreferenceStore();
			String loc = store.getString(PREFERENCE_LOAD_CALEYDO_PROJECT);
			if (loc != null && !loc.trim().isEmpty() && checkFileName(loc)) {
				store.setValue(PREFERENCE_LOAD_CALEYDO_PROJECT, "");
				try {
					// save right now the preferences to avoid that when crashing it will load my preferenced project
					// again
					store.save();
				} catch (IOException e) {
					// ignore
				}
				this.projectLocation = new File(loc);
				return true;
			}
		}

		{// check system property
			String loc = System.getProperty(PROPERTY_CALEYDO_PROJECT_LOCATION);
			if (loc != null && !loc.trim().isEmpty() && checkFileName(loc)) {
				this.projectLocation = new File(loc);
				return true;
			}
		}

		if (loadRecentProject)
			return true;
		if (projectLocation != null && !checkFileName(projectLocation.getAbsolutePath())) {
			projectLocation = null;
		} else if (projectLocation != null)
			return true;

		return false;
	}

	/** Check whether a string corresponds to a caleydo project file */
	private static boolean checkFileName(String file) {
		if (file.endsWith(".cal"))
			return true;

		System.err.println("The specified project " + file + " is not a *.cal file");
		return false;
	}

	@Override
	public Composite create(Composite parent, final WizardPage page) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		Label geneticDataDescription = new Label(composite, SWT.WRAP);
		geneticDataDescription
				.setText("Start Caleydo using an existing Caleydo project, or continue where you left-off last time. \n");
		geneticDataDescription.setBackground(composite.getBackground());
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		gridData.widthHint = WIDTH;
		geneticDataDescription.setLayoutData(gridData);

		this.recentProject = new Button(composite, SWT.RADIO);
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		gridData.widthHint = WIDTH;
		recentProject.setLayoutData(gridData);
		String text = "Continue where you stopped last time";
		recentProject.setText(text);

		final Date recentProjectChange = ProjectManager.getRecentProjectLastModified();

		if (recentProjectChange != null) {
			DateFormat dataformat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
			String lastModifiedDate = dataformat.format(recentProjectChange);
			text = text + ", on " + lastModifiedDate;
		}
		recentProject.setText(text);

		Button loadProject = new Button(composite, SWT.RADIO);
		loadProject.setText("Load a Caleydo project (*.cal file)");
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		gridData.widthHint = WIDTH;
		loadProject.setLayoutData(gridData);

		final Button chooseProjectFile = new Button(composite, SWT.CENTER);
		chooseProjectFile.setEnabled(false);
		chooseProjectFile.setText("Choose File");
		GridData singleCellGD = new GridData(SWT.LEFT, SWT.TOP, false, false);
		// singleCellGD.widthHint = 100;
		chooseProjectFile.setLayoutData(singleCellGD);

		projectLocationUI = new Text(composite, SWT.BORDER);
		projectLocationUI.setEnabled(false);
		singleCellGD = new GridData(SWT.FILL, SWT.TOP, true, false);
		singleCellGD.grabExcessHorizontalSpace = true;
		// singleCellGD.widthHint = 300;
		projectLocationUI.setLayoutData(singleCellGD);

		recentProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				chooseProjectFile.setEnabled(false);
				projectLocationUI.setEnabled(false);
				page.setPageComplete(true);
			}
		});

		loadProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectLocationUI.setEnabled(true);
				chooseProjectFile.setEnabled(true);
				if (projectLocationUI.getText() != null && !projectLocationUI.getText().isEmpty()) {
					page.setPageComplete(true);
				} else {
					page.setPageComplete(false);
				}
			}
		});

		String lastProjectFileName = GeneralManager.get().getPreferenceStore()
				.getString(PreferenceConstants.LAST_MANUALLY_CHOSEN_PROJECT);
		if ("recent".equalsIgnoreCase(lastProjectFileName)) {
			recentProject.setSelection(true);
		} else {
			loadProject.setSelection(true);
			projectLocationUI.setEnabled(true);
			projectLocationUI.setText(lastProjectFileName);
			chooseProjectFile.setEnabled(true);
			if (projectLocationUI.getText() != null && !projectLocationUI.getText().isEmpty()) {
				page.setPageComplete(true);
			} else {
				page.setPageComplete(false);
			}
		}

		chooseProjectFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(new Shell(), SWT.OPEN);
				fileDialog.setText("Load Project");
				String[] filterExt = { "*.cal" };
				fileDialog.setFilterExtensions(filterExt);

				String fileName = fileDialog.open();
				if (fileName != null) {
					projectLocationUI.setText(fileName);
					page.setPageComplete(true);
				}
			}
		});

		return composite;
	}

	@Override
	public IStartupProcedure create() {
		PreferenceStore store = PreferenceManager.get().getPreferenceStore();
		if (loadRecentProject || (recentProject != null && recentProject.getSelection())) {
			store.setValue(PreferenceConstants.LAST_MANUALLY_CHOSEN_PROJECT, "recent");
			return new LoadProjectStartupProcedure(ProjectManager.RECENT_PROJECT_FOLDER, true);
		}


		String path;
		if (projectLocation != null)
			path = projectLocation.getAbsolutePath();
		else
			path = projectLocationUI.getText();
		store.setValue(PreferenceConstants.LAST_MANUALLY_CHOSEN_PROJECT, path);
		return new LoadProjectStartupProcedure(path, false);
	}

}
