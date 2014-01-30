/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.system.BrowserUtils;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Base class for pages that are used in {@link DataImportWizard}.
 *
 * @author Christian Partl
 *
 */
public abstract class AImportDataPage<WizardType extends AWizard<WizardType>>
	extends WizardPage
	implements IPageChangedListener {

	/**
	 * The {@link DataSetDescription} for which data is defined in subclasses.
	 */
	protected DataSetDescription dataSetDescription;

	/**
	 * Determines whether this page is currently shown.
	 */
	protected boolean isActive = false;

	private Composite parent;

	private ScrolledComposite scrolledComposite;

	/**
	 * @param pageName
	 */
	protected AImportDataPage(String pageName, DataSetDescription dataSetDescription) {
		super(pageName, pageName, null);
		setImageDescriptor(ImageDescriptor.createFromURL(this.getClass().getClassLoader()
				.getResource("resources/wizard/wizard.png")));
		this.dataSetDescription = dataSetDescription;
	}

	@Override
	public void createControl(Composite parent) {
		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		GridLayout l = new GridLayout(1, true);
		l.horizontalSpacing = 0;
		l.verticalSpacing = 0;
		l.marginHeight = 0;
		l.marginHeight = 0;
		scrolledComposite.setLayout(l);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		this.parent = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(this.parent);
		this.parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.parent.setLayout(l);

		createGuiElements(this.parent);

		this.parent.pack();
		// scrolledComposite.setMinSize(this.parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(scrolledComposite);
	}

	/**
	 * Subclasses are intended to create their widgets within this method instead of in
	 * {@link #createControl(Composite)}.
	 *
	 * @param parent
	 */
	protected abstract void createGuiElements(Composite parent);

	/**
	 * Fills {@link #dataSetDescription} with values specified by the user.
	 */
	public abstract void fillDataSetDescription();

	@Override
	public void pageChanged(PageChangedEvent event) {

		if (isActive && event.getSelectedPage() == getNextPage()) {
			// System.out.println("Fill desc: " + getTitle());
			fillDataSetDescription();
			getWizard().addVisitedPage(this);
		}

		if (event.getSelectedPage() == this) {
			isActive = true;

			pageActivated();
			// scrolledComposite.setMinSize(this.parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			Point currentSize = parent.getSize();
			Point computedSize = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			Point minSize = null;
			if (currentSize.x >= computedSize.x && currentSize.y >= computedSize.y) {
				minSize = currentSize;
			} else if (currentSize.x <= computedSize.x && currentSize.y <= computedSize.y) {
				minSize = computedSize;
			} else if (currentSize.x >= computedSize.x && currentSize.y <= computedSize.y) {
				minSize = parent.computeSize(currentSize.x, SWT.DEFAULT);
			} else {
				minSize = parent.computeSize(SWT.DEFAULT, currentSize.y);
			}
			scrolledComposite.setMinSize(minSize);

		} else {
			isActive = false;
		}
	}

	/**
	 * Called when the page is presented to the user.
	 */
	public abstract void pageActivated();

	@Override
	public void performHelp() {
		// super.performHelp();
		BrowserUtils.openURL(GeneralManager.HELP_URL + "data.md");

	}

	@SuppressWarnings("unchecked")
	@Override
	public WizardType getWizard() {
		return (WizardType) super.getWizard();
	}

}
