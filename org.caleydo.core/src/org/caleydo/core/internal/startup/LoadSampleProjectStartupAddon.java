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
package org.caleydo.core.internal.startup;

import java.net.MalformedURLException;
import java.net.URL;

import org.caleydo.core.internal.MyPreferences;
import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.util.system.BrowserUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

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
	public Composite create(Composite parent, final WizardPage page) {
		SelectionListener l = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedChoice = (URL) ((Button) e.getSource()).getData();
				page.setPageComplete(true);
			}
		};

		Composite g = new Composite(parent, SWT.NONE);
		g.setLayout(new GridLayout(1, false));

		for (IConfigurationElement elem : RegistryFactory.getRegistry().getConfigurationElementsFor(EXTENSION_POINT)) {
			String name = elem.getAttribute("name");
			String url = elem.getAttribute("url");
			String description = elem.getAttribute("description");
			createSample(url, name, description, g, l);
		}
		return g;
	}

	private Button createSample(String url, String name, String description, Composite g, SelectionListener l) {
		try {
			URL u = new URL(url);
			Button button = new Button(g, SWT.RADIO);
			button.setText(name);
			button.setData(u);
			button.addSelectionListener(l);
			GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			gd.horizontalSpan = 1;
			button.setLayoutData(gd);
			if (url.equals(MyPreferences.getLastChosenSampleProject())) {
				button.setSelection(true);
				selectedChoice = u;
			}

			Link desc = new Link(g, SWT.NONE);
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
	public boolean init() {
		return false;
	}

	@Override
	public IStartupProcedure create() {
		MyPreferences.setLastChosenSampleProject(selectedChoice.toString());
		return new LoadSampleProjectStartupProcedure(selectedChoice);
	}

}
