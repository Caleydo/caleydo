/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.image.wizard;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.gui.dataimport.CreateIDTypeDialog;
import org.caleydo.core.io.gui.dataimport.wizard.ALoadDataPageMediator;
import org.caleydo.datadomain.image.ImageSet;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Thomas Geymayer
 *
 */
public class LoadImageSetPageMediator extends ALoadDataPageMediator {

	protected LoadImageSetPage page;

	/**
	 * The IDTypes available for {@link #imageIDCategory}.
	 */
	protected List<IDType> imageIDTypes = new ArrayList<IDType>();

	/**
	 * The IDTypes available for {@link #layerIDCategory}.
	 */
	protected List<IDType> layerIDTypes = new ArrayList<IDType>();

	/**
	 *
	 */
	public LoadImageSetPageMediator(LoadImageSetPage page) {
		this.page = page;
	}

	@Override
	public void guiCreated() {
		refreshRegisteredCategories();

		fillIDCategoryCombo(page.imageIDCategoryCombo);
		fillIDCategoryCombo(page.layerIDCategoryCombo);

		comboSelect(page.imageIDCategoryCombo, getIDCategoryImage());
		comboSelect(page.layerIDCategoryCombo, getIDCategoryLayer());

		fillIDTypeCombo(IDCategory.getIDCategory(getIDCategoryImage()), imageIDTypes, page.imageIDTypeCombo);
		page.imageIDTypeCombo.setEnabled(page.imageIDCategoryCombo.getSelectionIndex() != -1);

		fillIDTypeCombo(IDCategory.getIDCategory(getIDCategoryLayer()), layerIDTypes, page.layerIDTypeCombo);
		page.layerIDTypeCombo.setEnabled(page.layerIDCategoryCombo.getSelectionIndex() != -1);
	}

	/**
	 * Fills the idTypeCombos according to the IDCategory selected by the idCategoryCombo.
	 *
	 * @param isImage
	 *            Determines whether the image or layer combo is affected.
	 */
	public void idCategoryComboModified(boolean isImage) {

		Combo combo = isImage ? page.imageIDCategoryCombo : page.layerIDCategoryCombo;
		if( combo.getSelectionIndex() == -1 )
			return;

		Combo typeCombo = isImage ? page.imageIDTypeCombo : page.layerIDTypeCombo;
		Button createTypeButton = isImage ? page.imageCreateIDTypeButton : page.layerCreateIDTypeButton;
		IDCategory category = null;

		if( combo.getSelectionIndex() == 0 /* UNMAPPED_INDEX */ ) {
			typeCombo.clearSelection();
			typeCombo.removeAll();
			typeCombo.setText("<Unmapped>");
			typeCombo.setEnabled(false);
			createTypeButton.setEnabled(false);
		} else {
			category = IDCategory.getIDCategory(combo.getText());
			fillIDTypeCombo(category, isImage ? imageIDTypes : layerIDTypes, typeCombo);
			typeCombo.setEnabled(true);
			createTypeButton.setEnabled(true);
		}

		if( isImage )
			getImageSet().setIDCategoryImage(category);
		else
			getImageSet().setIDCategoryLayer(category);
	}

	/**
	 * @param isImage
	 */
	public void idTypeComboModified(boolean isImage) {
		Combo combo = isImage ? page.imageIDTypeCombo : page.layerIDTypeCombo;
		IDType type = IDType.getIDType(combo.getText());

		if( isImage )
			getImageSet().setIDTypeImage(type);
		else
			getImageSet().setIDTypeLayer(type);
	}

	/**
	 * Opens a dialog to create a new {@link IDCategory}. The value of
	 * columnIDCategoryCombo is set to the newly created category.
	 */
	public void createIDCategory(boolean isImage) {
		CreateIDTypeDialog dialog = new CreateIDTypeDialog(new Shell(), "sample-text");

		int status = dialog.open();
		if (status == Window.OK) {

			IDCategory newIDCategory = dialog.getIdCategory();
			registeredIDCategories.add(newIDCategory);

			fillIDCategoryCombo(page.imageIDCategoryCombo);
			fillIDCategoryCombo(page.layerIDCategoryCombo);
			if (isImage) {
				getImageSet().setIDCategoryImage(newIDCategory);
				comboSelect(page.imageIDCategoryCombo, getIDCategoryImage());
				fillIDTypeCombo(IDCategory.getIDCategory(getIDCategoryImage()), imageIDTypes, page.imageIDTypeCombo);
			} else {
				getImageSet().setIDCategoryLayer(newIDCategory);
				comboSelect(page.layerIDCategoryCombo, getIDCategoryLayer());
				fillIDTypeCombo(IDCategory.getIDCategory(getIDCategoryLayer()), layerIDTypes, page.layerIDTypeCombo);
			}
		}
	}

	/**
	 * Opens a dialog to create a new {@link IDType}. The value of columnIDCombo
	 * is set to the newly created category.
	 */
	public void createIDType(boolean isImage) {
		IDCategory categoryImage = IDCategory.getIDCategory(getIDCategoryImage());
		IDCategory categoryLayer = IDCategory.getIDCategory(getIDCategoryLayer());
		IDCategory category = isImage ? categoryImage : categoryLayer;

		CreateIDTypeDialog dialog =
				new CreateIDTypeDialog(new Shell(), category, "sample-text");

		int status = dialog.open();
		if (status == Window.OK) {

			fillIDTypeCombo(categoryImage, imageIDTypes, page.imageIDTypeCombo);
			fillIDTypeCombo(categoryLayer, layerIDTypes, page.layerIDTypeCombo);

			IDType newIDType = dialog.getIdType();

			if (isImage)
				comboSelect(page.imageIDTypeCombo, newIDType.getTypeName());
			else
				comboSelect(page.layerIDTypeCombo, newIDType.getTypeName());
		}
	}

	/**
	 *
	 */
	public ImageSet getImageSet() {
		return page.getWizard().getImageSet();
	}

	public String getIDCategoryImage() {
		return getImageSet().getIDCategoryImage().getCategoryName();
	}

	public String getIDTypeImage() {
		return getImageSet().getIDTypeImage().getTypeName();
	}

	public String getIDCategoryLayer() {
		return getImageSet().getIDCategoryLayer().getCategoryName();
	}

	public String getIDTypeLayer() {
		return getImageSet().getIDTypeLayer().getTypeName();
	}
}
