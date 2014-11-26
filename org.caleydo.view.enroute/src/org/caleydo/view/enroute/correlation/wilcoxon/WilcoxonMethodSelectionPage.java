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
import org.eclipse.swt.widgets.Label;

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
		parentComposite.setLayout(new GridLayout(1, false));

		Group descriptionGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		descriptionGroup.setText("Description:");
		descriptionGroup.setLayout(new GridLayout(1, true));
		descriptionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label instructionsLabel = new Label(descriptionGroup, SWT.WRAP);
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gd.widthHint = 800;
		instructionsLabel.setLayoutData(gd);
		instructionsLabel
				.setText("The Wilcoxon rank-sum test (or Mann-Whitney U test) allows you to test the support of a split in one data block in another data block of the enRoute view. You will first select a data block and define how to split the data into two groups. Then you will select a second block which will be divided according to the split in the first one. Alternatively, you can just select two data blocks and different splits are automatically suggested based on statistical support.");

		Group methodGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		methodGroup.setText("Method:");
		methodGroup.setLayout(new GridLayout(1, true));
		methodGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		manualMethodButton = new Button(methodGroup, SWT.RADIO);
		manualMethodButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		manualMethodButton.setSelection(true);
		manualMethodButton.setText("Define Data Classification");

		Label manualLabel = new Label(methodGroup, SWT.WRAP);
		gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gd.horizontalIndent = 22;
		manualLabel.setLayoutData(gd);
		manualLabel.setText("You define both data blocks and how to split the data.");

		autoMethodButton = new Button(methodGroup, SWT.RADIO);
		autoMethodButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		autoMethodButton.setSelection(false);
		autoMethodButton.setText("Compute All Data Classifications");
		Label autoLabel = new Label(methodGroup, SWT.WRAP);
		gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gd.horizontalIndent = 22;
		autoLabel.setLayoutData(gd);
		autoLabel
				.setText("You only define both data blocks and Caleydo will list all possible splits and show their significance (p-values) and adjusted p-values taking the false discovery rate into account.");

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
