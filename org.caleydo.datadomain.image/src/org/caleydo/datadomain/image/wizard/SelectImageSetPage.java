/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.datadomain.image.wizard;

import java.util.List;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.wizard.AImportDataPage;
import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.datadomain.image.ImageSet;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Page for creating a new or selecting an existing image set to edit.
 *
 * @author Thomas Geymayer
 *
 */
public class SelectImageSetPage
	extends AImportDataPage<ImageImportWizard>
	implements Listener {

	public static final String PAGE_NAME = "Load Images";

	public static final String PAGE_DESCRIPTION = "Create a new or load an existing image set.";

	/**
	 * Composite that is the parent of all gui elements of this dialog.
	 */
	protected Composite parentComposite;

	/**
	 * Radio button to create a new {@link ImageDataDomain} instance.
	 */
	protected Button createButton;

	/**
	 * Radio button to select an existing {@link ImageDataDomain} to modify.
	 */
	protected Button editButton;

	/**
	 * Combo box to select an existing imageset.
	 */
	protected Combo nameCombo;

	/**
	 * Text input to specify the name of the new imageset.
	 */
	protected Text nameText;

	public SelectImageSetPage() {
		super(PAGE_NAME, null);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	protected void createGuiElements(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		parentComposite.setLayout(layout);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createButton = new Button(parentComposite, SWT.RADIO);
		createButton.setText("Create new Image Set");
		createButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				nameText.setEnabled(true);
				nameCombo.setEnabled(false);
			}
		});
		createButton.setSelection(true);
		createButton.addListener(SWT.Selection, this);

		nameText = new Text(parentComposite, SWT.SINGLE | SWT.BORDER);
		nameText.setToolTipText("Enter name for new Image Set");
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nameText.addListener(SWT.Modify, this);

		editButton = new Button(parentComposite, SWT.RADIO);
		editButton.setText("Edit existing Image Set");
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				nameText.setEnabled(false);
				nameCombo.setEnabled(true);
			}
		});
		editButton.addListener(SWT.Selection, this);

		nameCombo = new Combo(parentComposite, SWT.BORDER | SWT.READ_ONLY);
		nameCombo.setToolTipText("Select existing Image Set");
		nameCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nameCombo.setEnabled(false);

		List<IDataDomain> imageSets = DataDomainManager.get()
				.getDataDomainsByType(ImageDataDomain.DATA_DOMAIN_TYPE);
		if (imageSets != null) {
			for (IDataDomain dataDomain : imageSets)
				nameCombo.add(dataDomain.getLabel());
			nameCombo.select(0);
		} else {
			editButton.setEnabled(false);
		}

		nameCombo.addListener(SWT.Modify, this);
	}

	/**
	 * On page complete...
	 */
	@Override
	public void fillDataSetDescription() {
		ImageSet imageSet = null;
		if (createButton.getSelection()) {
			imageSet = new ImageSet();
			imageSet.setName(nameText.getText());
		} else {
			ImageDataDomain imageDomain = (ImageDataDomain) DataDomainManager
					.get().getDataDomainByLabel(
							ImageDataDomain.DATA_DOMAIN_TYPE,
							nameCombo.getText());
			imageSet = new ImageSet(imageDomain.getImageSet());
			getWizard().setDataDomain(imageDomain);
		}
		getWizard().setImageSet(imageSet);
	}

	public DataSetDescription getDataSetDescription() {
		return null;
	}

	public String getImageSetName() {
		setErrorMessage(null);
		if (createButton.getSelection()) {
			String name = nameText.getText();
			for (String item : nameCombo.getItems())
				if (name.equalsIgnoreCase(item)) {
					setErrorMessage("Name already in use.");
				return "";
			}
			return nameText.getText();
		}
		else
			return nameCombo.getText();
	}

	@Override
	public boolean isPageComplete() {
		if (getImageSetName().isEmpty())
			return false;

		return super.isPageComplete();
	}

	@Override
	public IWizardPage getNextPage() {
		return getWizard().getNextPage(this);
	}

	@Override
	public void handleEvent(Event event) {
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
	}

	@Override
	public void pageActivated() {
		getWizard().getContainer().updateButtons();
	}
}