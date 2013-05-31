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
package org.caleydo.datadomain.genetic.internal;

import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.datadomain.genetic.Organism;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.kohsuke.args4j.Option;

/**
 * @author Samuel Gratzl
 *
 */
public class LoadGeneticStartupAddon implements IStartupAddon {
	private static final String HCC_SAMPLE_DATASET_PAPER_LINK = "http://www.ncbi.nlm.nih.gov/pubmed/17241883";

	private static final int WIDTH = 400;

	@Option(name = "-organism", aliases = { "--organism" }, usage = "the organism to use")
	private Organism organism;

	@Option(name = "-loadGeneData")
	private boolean loadGeneData;

	@Option(name = "-loadSampleGeneData")
	private boolean loadSampleGeneData;

	@Override
	public boolean init() {
		if (organism == null)
			organism = MyPreferences.getLastChosenOrganism();
		if (loadGeneData || loadSampleGeneData)
			return true;

		return false;
	}


	@Override
	public Composite create(Composite parent, final WizardPage page) {
		page.setPageComplete(true);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);

		Group groupOrganism = new Group(composite, SWT.NONE);
		groupOrganism.setText("Select organism");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		groupOrganism.setLayoutData(gridData);
		groupOrganism.setLayout(new GridLayout(1, false));

		Label label = new Label(groupOrganism, SWT.WRAP);
		label.setText("Please choose whether the data you want to load is for humans or mice. "
				+ "Other organisms are currently not supported.\n");
		label.setBackground(composite.getBackground());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = WIDTH;
		label.setLayoutData(gridData);

		SelectionListener onChange = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button b = (Button) e.widget;
				if (b.getSelection())
					organism = (Organism) b.getData();
			}
		};

		for (Organism o : Organism.values()) {
			Button b = new Button(groupOrganism, SWT.RADIO);
			b.setText(o.getLabel());
			b.setEnabled(true);
			b.setData(o);
			b.setSelection(organism == o);
			b.addSelectionListener(onChange);
		}

		Button buttonNewProject = new Button(composite, SWT.RADIO);
		buttonNewProject.setText("Load data from file (CSV, TXT)");
		buttonNewProject.setLayoutData(gridData);
		buttonNewProject.setSelection(true);

		Label desc = new Label(composite, SWT.WRAP);
		desc.setText("Load tabular data wich contains identifiers to one of the following types of IDs: "
				+ "DAVID IDs, gene names, RefSeq IDs, ENSEMBL IDs or ENTREZ IDs \n \n"
				+ "Other identifiers are currently not supported. Use the \"Load Other Data\" "
				+ "option if you have other identifiers.\n");
		desc.setBackground(composite.getBackground());
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = WIDTH;
		desc.setLayoutData(gridData);

		final Button btnSampleData = new Button(composite, SWT.RADIO);
		btnSampleData.setText("Start with sample gene expression data");
		// buttonSampleDataMode.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		btnSampleData.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Link link = new Link(composite, SWT.NULL);
		link.setText("This option loads a single sample mRNA expression dataset with samples for hepatocellular carcinoma (about 4000 genes and 39 experiments) through the standard loading dialog. The dataset is made available by the Institute of Pathology at the Medical University of Graz.\n\nDataset: <a href=\""
				+ HCC_SAMPLE_DATASET_PAPER_LINK + "\">" + HCC_SAMPLE_DATASET_PAPER_LINK + "</a>");
		link.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		link.addSelectionListener(BrowserUtils.LINK_LISTENER);

		btnSampleData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				page.setPageComplete(true);
				loadSampleGeneData = btnSampleData.getSelection();
			}
		});

		return composite;
	}

	@Override
    public IStartupProcedure create() {
		MyPreferences.setLastChosenOrganism(organism);
		return new GeneticGUIStartupProcedure(organism, loadSampleGeneData);
    }

}
