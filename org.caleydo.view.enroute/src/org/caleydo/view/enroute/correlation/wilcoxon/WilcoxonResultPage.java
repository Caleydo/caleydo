/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.IIDClassifier;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Christian
 *
 */
public class WilcoxonResultPage extends WizardPage implements IPageChangedListener {

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected WilcoxonResultPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(2, false));

		Label result = new Label(parentComposite, SWT.NONE);
		result.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		result.setText("Results...");

		setControl(parentComposite);

	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == this) {
			WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
			DataCellInfo info = wizard.getInfo2();
			IIDClassifier derivedClassifier = wizard.getDerivedIDClassifier();
			// TODO: implement rank sum test
		}

	}

}
