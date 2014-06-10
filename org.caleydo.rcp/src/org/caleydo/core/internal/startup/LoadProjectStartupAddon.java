/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal.startup;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.caleydo.core.serialize.ProjectManager;
import org.caleydo.core.startup.IStartUpDocumentListener;
import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.startup.LoadProjectStartupProcedure;
import org.caleydo.core.view.internal.MyPreferences;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * {@link IStartupAddon} for loading a project or the recent project
 *
 * @author Samuel Gratzl
 *
 */
public class LoadProjectStartupAddon implements IStartupAddon, IStartUpDocumentListener {
	private static final String PROPERTY_CALEYDO_PROJECT_LOCATION = "caleydo.project.location";

	private static final int WIDTH = 400;

	@Argument(metaVar = "PROJECT", usage = "the caledyo project to load")
	private File projectLocation;

	@Option(name = "-loadRecent")
	private boolean loadRecentProject;

	private Combo projectLocationUI;

	private Button recentProject;

	@Override
	public boolean init() {
		{// check preference store -> used for open project switch workspace thing
			String loc = MyPreferences.getAutoLoadProject();
			if (loc != null && !loc.trim().isEmpty() && checkFileName(loc)) {
				MyPreferences.setAutoLoadProject(null);
				MyPreferences.flush();
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

	@Override
	public void handleEvent(Event event) {
		if (event.text != null && !event.text.isEmpty())
			this.projectLocation = new File(event.text);
	}

	/** Check whether a string corresponds to a caleydo project file */
	private static boolean checkFileName(String file) {
		if (file.endsWith(".cal"))
			return true;

		System.err.println("The specified project " + file + " is not a *.cal file");
		return false;
	}

	@Override
	public Composite create(Composite parent, final WizardPage page, Listener listener) {
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

		projectLocationUI = new Combo(composite, SWT.BORDER);
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
				// page.setPageComplete(true);
			}
		});
		recentProject.addListener(SWT.Selection, listener);

		loadProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectLocationUI.setEnabled(true);
				chooseProjectFile.setEnabled(true);
				// if (projectLocationUI.getText() != null && !projectLocationUI.getText().isEmpty()) {
				// page.setPageComplete(true);
				// } else {
				// page.setPageComplete(false);
				// }
			}
		});
		recentProject.addListener(SWT.Selection, listener);

		String lastProjectFileName = MyPreferences.getLastManuallyChosenProject();
		boolean recentProjectChosen = MyPreferences.wasRecentProjectChosenLastly();

		if (lastProjectFileName != null && !lastProjectFileName.trim().isEmpty() && validatePath(lastProjectFileName)) {
			projectLocationUI.setText(lastProjectFileName);
			List<String> lastChosenProjects = MyPreferences.getLastManuallyChosenProjects();
			projectLocationUI.setItems(lastChosenProjects.toArray(new String[lastChosenProjects.size()]));
			projectLocationUI.select(0);
		}

		if (recentProjectChosen) {
			recentProject.setSelection(true);
		} else {
			loadProject.setSelection(true);
			projectLocationUI.setEnabled(true);
			chooseProjectFile.setEnabled(true);
			// if (projectLocationUI.getText() != null && !projectLocationUI.getText().isEmpty()) {
			// page.setPageComplete(true);
			// } else {
			// page.setPageComplete(false);
			// }
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
					// page.setPageComplete(true);
				}
			}
		});
		chooseProjectFile.addListener(SWT.Selection, listener);

		projectLocationUI.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				// if (projectLocationUI.getText().length() > 0 && validatePath(projectLocationUI.getText()))
				// page.setPageComplete(true);
			}

			@Override
			public void focusGained(FocusEvent e) {

			}
		});
		projectLocationUI.addListener(SWT.Modify, listener);

		return composite;
	}

	@Override
	public boolean validate() {
		if (loadRecentProject || (recentProject != null && recentProject.getSelection()))
			return true;

		if (projectLocationUI != null) {
			return validatePath(projectLocationUI.getText());
		}

		return true;

	}

	private boolean validatePath(String path) {
		if (isURL(path))
			return true;
		File file = new File(path);
		if (!file.exists())
			return false;
		return true;
	}

	/**
	 * @param path
	 * @return
	 */
	private static boolean isURL(String path) {
		try {
			return new URL(path) != null;
		} catch (MalformedURLException e) {
			return false;
		}
	}

	@Override
	public IStartupProcedure create() {
		if (loadRecentProject || (recentProject != null && recentProject.getSelection())) {
			MyPreferences.setRecentProjectChosenLastly(true);
			return new LoadProjectStartupProcedure(ProjectManager.RECENT_PROJECT_FOLDER, true);
		}

		String path;
		if (projectLocation != null)
			path = projectLocation.getAbsolutePath();
		else
			path = projectLocationUI.getText();
		MyPreferences.setRecentProjectChosenLastly(false);
		MyPreferences.setLastManuallyChosenProject(path);
		return new LoadProjectStartupProcedure(path, false);
	}

}
