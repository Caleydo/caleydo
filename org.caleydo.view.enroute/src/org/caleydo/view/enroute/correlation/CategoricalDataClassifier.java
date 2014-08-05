/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.util.color.Color;

/**
 * @author Christian
 *
 */
public class CategoricalDataClassifier implements IDataClassifier {

	final Set<Object> categoryClass1;
	final Set<Object> categoryClass2;
	final List<SimpleCategory> dataClasses;

	public CategoricalDataClassifier(Set<Object> categoryClass1, Set<Object> categoryClass2) {
		this.categoryClass1 = categoryClass1;
		this.categoryClass2 = categoryClass2;
		dataClasses = new ArrayList<>();
		dataClasses.add(new SimpleCategory("categories: " + categoryClass1.toString(), Color.CYAN));
		dataClasses.add(new SimpleCategory("categories: " + categoryClass2.toString(), Color.MAGENTA));
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

}
