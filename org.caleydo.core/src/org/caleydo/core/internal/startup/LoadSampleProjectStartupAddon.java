/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal.startup;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.gui.util.FontUtil;
import org.caleydo.core.internal.MyPreferences;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.startup.LoadProjectStartupProcedure;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.core.util.system.RemoteFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * This tab lets you choose between a sample project, which has e.g. cluster data included and a sample dataset,
 * which is basically just a set csv file.
 *
 * @param tabFolder
 */
/**
 * @author Samuel Gratzl
 *
 */
public class LoadSampleProjectStartupAddon implements IStartupAddon {
	private static final String EXTENSION_POINT = "org.caleydo.core.SampleProject";
	private URL selectedChoice = null;

	@Override
	public Composite create(Composite parent, final WizardPage page, Listener listener) {
		SelectionListener l = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedChoice = (URL) ((Button) e.getSource()).getData();
				page.setPageComplete(true);
			}
		};

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);

		Composite g = new Composite(scrolledComposite, SWT.NONE);
		g.setLayout(new GridLayout(1, false));

		Button first = null;
		for (IConfigurationElement elem : RegistryFactory.getRegistry().getConfigurationElementsFor(EXTENSION_POINT)) {
			String name = elem.getAttribute("name");
			String url = elem.getAttribute("url").replace("DATA_URL_PREFIX",
					StringUtils.removeEnd(GeneralManager.DATA_URL_PREFIX, "/"));
			String description = elem.getAttribute("description");
			if (first == null)
				first = createSample(url, name, description, g, l, true, listener);
			else
				createSample(url, name, description, g, l, false, listener);
		}
		if (selectedChoice == null && first != null) {
			first.setSelection(true);
			selectedChoice = (URL) first.getData();
		}

		scrolledComposite.setContent(g);
		scrolledComposite.setMinSize(g.computeSize(500, SWT.DEFAULT));

		return scrolledComposite;
	}

	private Button createSample(String url, String name, String description, Composite g, SelectionListener l,
			boolean first, Listener listener) {
		try {
			URL u = new URL(url);
			Button button = new Button(g, SWT.RADIO);
			button.setText(name);
			FontUtil.makeBold(button);

			button.setData(u);
			button.addSelectionListener(l);
			button.addListener(SWT.Selection, listener);
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.verticalIndent = first ? 0 : 20;
			button.setLayoutData(gd);

			if (url.equals(MyPreferences.getLastChosenSampleProject())) {
				button.setSelection(true);
				selectedChoice = u;
			}

			Link desc = new Link(g, SWT.WRAP);
			desc.setText(description);
			desc.addSelectionListener(BrowserUtils.LINK_LISTENER);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			desc.setLayoutData(gd);

			return button;
		} catch (MalformedURLException e) {
			System.err.println("invalid url:" + url);
			return null;
		}

	}

	@Override
	public boolean validate() {
		return selectedChoice != null;
		// if (this.selectedChoice == null)
		// return false;
		// Try to download the file with interruption
		// RemoteFile file = RemoteFile.of(this.selectedChoice);
		// if (!file.inCache(true)) {
		// file.delete();
		// try {
		// new ProgressMonitorDialog(new Shell()).run(true, true, file);
		// } catch (InvocationTargetException | InterruptedException e) {
		// Status status = new Status(IStatus.ERROR, this.getClass().getSimpleName(), "Error during downloading: "
		// + selectedChoice, e);
		// ErrorDialog.openError(null, "Download Error", "Error during downloading: " + selectedChoice, status);
		// Logger.log(status);
		// }
		// }
		// return file.inCache(false);
	}

	@Override
	public boolean init() {
		return false;
	}

	@Override
	public IStartupProcedure create() {
		MyPreferences.setLastChosenSampleProject(selectedChoice.toString());
		RemoteFile file = RemoteFile.of(this.selectedChoice);
		if (!file.inCache(true)) {
			file.delete();
			try {
				new ProgressMonitorDialog(new Shell()).run(true, true, file);
			} catch (InvocationTargetException | InterruptedException e) {
				Status status = new Status(IStatus.ERROR, this.getClass().getSimpleName(), "Error during downloading: "
						+ selectedChoice, e);
				ErrorDialog.openError(null, "Download Error", "Error during downloading: " + selectedChoice, status);
				Logger.log(status);
			}
		}
		if (!file.inCache(false))
			return null;
		return new LoadProjectStartupProcedure(RemoteFile.of(selectedChoice).getFile().getAbsolutePath(), false);
	}

}
