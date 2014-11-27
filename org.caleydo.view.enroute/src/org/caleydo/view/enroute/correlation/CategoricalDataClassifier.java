/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.enroute.mappeddataview.overlay.CategoricalClassifierOverlayProvider;
import org.caleydo.view.enroute.mappeddataview.overlay.IDataCellOverlayProvider;

/**
 * @author Christian
 *
 */
public class CategoricalDataClassifier implements IDataClassifier {

	private final Set<Object> categoryClass1;
	private final Set<Object> categoryClass2;
	private final List<SimpleCategory> dataClasses;
	private final CategoricalClassDescription<?> classDescription;

	public CategoricalDataClassifier(Set<Object> categoryClass1, Set<Object> categoryClass2, Color class1Color,
			Color class2Color, String class1Label, String class2Label, CategoricalClassDescription<?> classDescription) {
		this.categoryClass1 = categoryClass1;
		this.categoryClass2 = categoryClass2;
		this.classDescription = classDescription;
		dataClasses = new ArrayList<>();
		dataClasses.add(new SimpleCategory(class1Label, class1Color));
		dataClasses.add(new SimpleCategory(class2Label, class2Color));
	}



	@Override
	public SimpleCategory apply(Object input) {
		if (categoryClass1.contains(input))
			return dataClasses.get(0);
		if (categoryClass2.contains(input))
			return dataClasses.get(1);
		return null;
	}

	@Override
	public List<SimpleCategory> getDataClasses() {
		return dataClasses;
	}

	/**
	 * @return the classDescription, see {@link #classDescription}
	 */
	public CategoricalClassDescription<?> getClassDescription() {
		return classDescription;
	}

	@Override
	public IDataCellOverlayProvider getOverlayProvider() {
		return new CategoricalClassifierOverlayProvider(this);
	}

}
