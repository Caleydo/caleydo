/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.eclipse.swt.widgets.Combo;

/**
 * @author Thomas Geymayer
 *
 */
public abstract class ALoadDataPageMediator {


	/**
	 * All registered id categories.
	 */
	protected List<IDCategory> registeredIDCategories;

	/**
	 * Initializes all widgets of the {@link #page}. This method should be
	 * called after all widgets of the dialog were created.
	 */
	public abstract void guiCreated();

	protected ALoadDataPageMediator() {
		refreshRegisteredCategories();
	}

	protected void refreshRegisteredCategories() {
		registeredIDCategories = new ArrayList<IDCategory>();
		for (IDCategory idCategory : IDCategory.getAllRegisteredIDCategories()) {
			if (!idCategory.isInternaltCategory()) {
				registeredIDCategories.add(idCategory);
			}
		}
	}

	protected void fillIDCategoryCombo(Combo idCategoryCombo) {

		String previousSelection = null;
		if (idCategoryCombo.getSelectionIndex() != -1) {
			previousSelection = idCategoryCombo.getItem(idCategoryCombo.getSelectionIndex());
		}

		idCategoryCombo.removeAll();
		idCategoryCombo.add("<Unmapped>");
		for (IDCategory idCategory : registeredIDCategories) {
			idCategoryCombo.add(idCategory.getCategoryName());
		}

		int selectionIndex = -1;
		if (previousSelection != null) {
			selectionIndex = idCategoryCombo.indexOf(previousSelection);
		}
		if (registeredIDCategories.size() == 1) {
			// idCategoryCombo.setText(idCategoryCombo.getItem(0));
			idCategoryCombo.select(1);
		} else if (selectionIndex == -1) {
			idCategoryCombo.deselectAll();
		} else {
			// idCategoryCombo.setText(idCategoryCombo.getItem(selectionIndex));
			idCategoryCombo.select(selectionIndex);
		}

	}

	protected void fillIDTypeCombo(IDCategory idCategory, List<IDType> idTypes, Combo idTypeCombo) {

		if (idCategory == null)
			return;
		ArrayList<IDType> allIDTypesOfCategory = new ArrayList<IDType>(idCategory.getIdTypes());

		String previousSelection = null;

		if (idTypeCombo.getSelectionIndex() != -1) {
			previousSelection = idTypeCombo.getItem(idTypeCombo.getSelectionIndex());
		}
		idTypeCombo.removeAll();
		idTypes.clear();

		for (IDType idType : allIDTypesOfCategory) {
			if (!idType.isInternalType()) {
				idTypes.add(idType);
				idTypeCombo.add(idType.getTypeName());
			}
		}

		int selectionIndex = -1;
		if (previousSelection != null) {
			selectionIndex = idTypeCombo.indexOf(previousSelection);
		}
		if (idTypes.size() == 1) {
			// idTypeCombo.setText(idTypeCombo.getItem(0));
			idTypeCombo.select(0);
		} else if (selectionIndex != -1) {
			// idTypeCombo.setText(idTypeCombo.getItem(selectionIndex));
			idTypeCombo.select(selectionIndex);
		} else {
			// idTypeCombo.setText("<Please Select>");
			idTypeCombo.deselectAll();
		}
	}

	protected void comboSelect(Combo combo, String item) {
		combo.select(combo.indexOf(item));
	}
}
