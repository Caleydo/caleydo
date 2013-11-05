/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
import org.eclipse.swt.widgets.Listener;
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
	public Composite create(Composite parent, final WizardPage page, Listener listener) {
		page.setPageComplete(true);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);

		Group groupOrganism = new Group(composite, SWT.NONE);
		groupOrganism.setText("Select Organism");
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		groupOrganism.setLayoutData(gridData);
		groupOrganism.setLayout(new GridLayout(1, false));

		Label label = new Label(groupOrganism, SWT.WRAP);
		label.setText("Please choose whether the data you want to load is for humans or mice. "
				+ "Other organisms are currently not supported.\n");
		label.setBackground(composite.getBackground());
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
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
			if (organism != null) {
				b.setSelection(organism == o);
			} else {
				organism = o;
			}
			b.addSelectionListener(onChange);
			b.addListener(SWT.Selection, listener);
		}

		Group groupDataset = new Group(composite, SWT.NONE);
		groupDataset.setText("Select Dataset");
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		groupDataset.setLayoutData(gridData);
		groupDataset.setLayout(new GridLayout(1, false));

		Button buttonNewProject = new Button(groupDataset, SWT.RADIO);
		buttonNewProject.setText("Load data from file (CSV, TXT)");
		buttonNewProject.setLayoutData(gridData);
		buttonNewProject.setSelection(true);
		buttonNewProject.addListener(SWT.Selection, listener);

		Label desc = new Label(groupDataset, SWT.WRAP);
		desc.setText("Load tabular data wich contains identifiers to one of the following types of IDs: "
				+ "DAVID IDs, gene names, RefSeq IDs, ENSEMBL IDs or ENTREZ IDs \n \n"
				+ "Other identifiers are currently not supported. Use the \"Load Other Data\" "
				+ "option if you have other identifiers.\n");
		desc.setBackground(groupDataset.getBackground());
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.widthHint = WIDTH;
		desc.setLayoutData(gridData);

		final Button btnSampleData = new Button(groupDataset, SWT.RADIO);
		btnSampleData.setText("Start with sample gene expression data");
		// buttonSampleDataMode.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		btnSampleData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		btnSampleData.addListener(SWT.Selection, listener);

		Link link = new Link(groupDataset, SWT.NULL);
		link.setText("This option loads a single sample mRNA expression dataset with samples for hepatocellular carcinoma (about 4000 genes and 39 experiments) through the standard loading dialog. The dataset is made available by the Institute of Pathology at the Medical University of Graz.\n\nDataset: <a href=\""
				+ HCC_SAMPLE_DATASET_PAPER_LINK + "\">" + HCC_SAMPLE_DATASET_PAPER_LINK + "</a>");
		link.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
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
	public boolean validate() {
		return organism != null;
	}

	@Override
	public IStartupProcedure create() {
		MyPreferences.setLastChosenOrganism(organism);
		return new GeneticGUIStartupProcedure(organism, loadSampleGeneData);
	}

}
