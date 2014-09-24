/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Christian
 *
 */
public class WilcoxonAutoResultPage extends WizardPage implements IPageChangedListener {

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected WilcoxonAutoResultPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, false));
		Group summaryGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		summaryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		summaryGroup.setLayout(new GridLayout(2, false));
		summaryGroup.setText("Summary");

		Group resultsGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		resultsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		resultsGroup.setLayout(new GridLayout(1, false));
		resultsGroup.setText("Results");

		setControl(parentComposite);

	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((WilcoxonRankSumTestWizard) getWizard()).getAutoTargetDataCellPage();
	}

	@Override
	public IWizardPage getNextPage() {
		return null;
	}

}
