/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.enroute.correlation.EndCorrelationCalculationEvent;
import org.caleydo.view.enroute.correlation.StartCorrelationCalculationEvent;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author Christian
 *
 */
public class WilcoxonMethodSelectionPage extends WizardPage implements IPageChangedListener {

	private Button manualMethodButton;
	private Button autoMethodButton;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected WilcoxonMethodSelectionPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(2, false));

		Group methodGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		methodGroup.setText("Method:");
		methodGroup.setLayout(new GridLayout(1, true));
		methodGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		manualMethodButton = new Button(methodGroup, SWT.RADIO);
		manualMethodButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		manualMethodButton.setSelection(true);
		manualMethodButton.setText("Define Data Classification");

		autoMethodButton = new Button(methodGroup, SWT.RADIO);
		autoMethodButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		autoMethodButton.setSelection(false);
		autoMethodButton.setText("Detect Significant Data Classifications");

		setControl(parentComposite);
	}

	@Override
	public IWizardPage getNextPage() {

		WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
		if (manualMethodButton.getSelection())
			return wizard.getManualSourceDataCellPage();
		return wizard.getAutoSourceDataCellPage();
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == this) {
			EventPublisher.trigger(new EndCorrelationCalculationEvent());
		} else if (((IWizardPage) event.getSelectedPage()).getPreviousPage() == this) {
			EventPublisher.trigger(new StartCorrelationCalculationEvent());
		}
	}

}
