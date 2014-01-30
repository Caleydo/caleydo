/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.datadomain.image.wizard;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.io.gui.dataimport.wizard.AWizard;
import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.datadomain.image.ImageSet;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.wizard.IWizardContainer;

/**
 * Wizard that guides the user through the different steps of importing an image set.
 *
 * @author Thomas Geymayer
 *
 */
public class ImageImportWizard extends AWizard<ImageImportWizard> {


	/**
	 * First page of the wizard that is used to specify the dataset.
	 */
	private SelectImageSetPage selectImageSetPage;

	private LoadImageSetPage loadImageSetPage;

	private ImageSet imageSet;

	private ImageDataDomain dataDomain;

	/**
	 *
	 */
	public ImageImportWizard() {
		setWindowTitle("Image Import Wizard");
	}

	@Override
	public void addPages() {
		selectImageSetPage = new SelectImageSetPage();
		loadImageSetPage = new LoadImageSetPage();

		IWizardContainer wizardContainer = getContainer();
		if (wizardContainer instanceof IPageChangeProvider) {
			IPageChangeProvider pageChangeProvider = (IPageChangeProvider) wizardContainer;
			pageChangeProvider.addPageChangedListener(selectImageSetPage);
			pageChangeProvider.addPageChangedListener(loadImageSetPage);
		}

		addPage(selectImageSetPage);
		addPage(loadImageSetPage);
	}

	@Override
	public boolean performFinish() {

		if (visitedPages.contains(loadImageSetPage)
				|| getContainer().getCurrentPage().equals(loadImageSetPage))
			loadImageSetPage.fillDataSetDescription();

		if (dataDomain == null)
			dataDomain = (ImageDataDomain) DataDomainManager.get()
					.createDataDomain(ImageDataDomain.DATA_DOMAIN_TYPE);

		dataDomain.setImageSet(imageSet);

		return true;
	}

	@Override
	public boolean canFinish() {
		return super.canFinish();
	}

	/**
	 * @return the loadImageSetPage, see {@link #loadImageSetPage}
	 */
	public LoadImageSetPage getLoadDataSetPage() {
		return loadImageSetPage;
	}

	public void setImageSet(ImageSet imageSet) {
		this.imageSet = imageSet;
	}

	public ImageSet getImageSet() {
		return imageSet;
	}

	public void setDataDomain(ImageDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public ImageDataDomain getDataDomain() {
		return dataDomain;
	}

}
