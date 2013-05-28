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

import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.util.system.BrowserUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.kohsuke.args4j.Option;

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
public class TryCaleydoStartupAddon implements IStartupAddon {
	private static final String TCGA_LINK = "http://cancergenome.nih.gov";

	private static final String BROAD_GDAC_LINK = "http://gdac.broadinstitute.org";

	private static final int WIDTH = 400;

	@Option(name="-loadSampleProject")
	private boolean loadSampleProject;

	@Override
	public Composite create(Composite parent, final WizardPage page) {
		SelectionAdapter linkSelectedAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String link = e.text;
				BrowserUtils.openURL(link);
			}

		};

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Button btnSampleProject = new Button(composite, SWT.RADIO);
		btnSampleProject.setText("Load sample project");
		btnSampleProject.setSelection(true);

		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
		btnSampleProject.setLayoutData(gd);

		btnSampleProject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				page.setPageComplete(true);
			}
		});

		Link sampleProjectDescription = new Link(composite, SWT.NULL);
		sampleProjectDescription
				.setText("This sample project loads five linked datasets from the <a href=\""
						+ TCGA_LINK
						+ "\">TCGA</a> GBM dataset made available by the Broad Institute's <a href=\""
						+ BROAD_GDAC_LINK
						+ "\">Genome Data Analysis Center (GDAC)</a>."
						+ "\n"
						+ "\n"
						+ "The datasets are mRNA expression data, microRNA expression, methylation and copy-number data. Additionally some clinical data is available. The project contains 300-550 samples for each dataset. The expression datasets contain about 1,500 pre-selected values, copy number status is availiabe for about 5,000 genes. "
						+ "\n" + "The ideal choice if you want to try out multi-dataset analysis in Calyedo.\n");

		sampleProjectDescription.setBackground(composite.getBackground());
		sampleProjectDescription.addSelectionListener(linkSelectedAdapter);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = WIDTH;
		sampleProjectDescription.setLayoutData(gd);

		return composite;
	}

	@Override
	public boolean init() {
		return loadSampleProject;
	}

	@Override
	public IStartupProcedure create() {
		return new LoadSampleProjectStartupProjecdure();
	}

}
