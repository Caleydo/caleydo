/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.enroute.correlation.ShowOverlayEvent;
import org.caleydo.view.enroute.correlation.wilcoxon.WilcoxonUtil.WilcoxonResult;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

/**
 * @author Christian
 *
 */
public class WilcoxonAutoResultPage extends WizardPage implements IPageChangedListener {

	private Map<Integer, WilcoxonResult> resultsMap = new HashMap<>();
	private List resultsList;

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
		// Group summaryGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		// summaryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// summaryGroup.setLayout(new GridLayout(2, false));
		// summaryGroup.setText("Summary");

		Group resultsGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		resultsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		resultsGroup.setLayout(new GridLayout(1, false));
		resultsGroup.setText("Results");
		resultsList = new List(resultsGroup, SWT.BORDER | SWT.V_SCROLL);
		resultsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		resultsList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WilcoxonResult result = resultsMap.get(resultsList.getSelectionIndex());
				WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();

				EventPublisher.trigger(new ShowOverlayEvent(wizard.getSourceInfo(), result.classifier
						.getOverlayProvider(), true));
				EventPublisher.trigger(new ShowOverlayEvent(wizard.getTargetInfo(), result.derivedClassifier
						.getOverlayProvider(), false));
			}
		});

		setControl(parentComposite);

	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == this) {
			WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
			java.util.List<WilcoxonResult> results = WilcoxonUtil.calcAllWilcoxonCombinations(wizard.getSourceInfo(),
					wizard.getTargetInfo());

			resultsList.removeAll();
			resultsMap.clear();
			int index = 0;
			for (WilcoxonResult result : results) {
				resultsList.add(String.format(Locale.ENGLISH, "U: %.2f, P: %.5f", result.u, result.p));
				resultsMap.put(index, result);
				index++;
			}

		}

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
