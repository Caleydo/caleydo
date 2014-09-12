/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.view.enroute.mappeddataview.overlay.DerivedClassifierOverlayProvider;
import org.caleydo.view.enroute.mappeddataview.overlay.IDataCellOverlayProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class SimpleIDClassifier implements IIDClassifier {

	private final Set<Object> class1IDs;
	private final Set<Object> class2IDs;
	private final IDType myIDType;
	private final SimpleCategory class1;
	private final SimpleCategory class2;

	/**
	 * @param class1iDs
	 * @param class2iDs
	 * @param idType
	 */
	public SimpleIDClassifier(Set<Object> class1IDs, Set<Object> class2IDs, IDType idType, SimpleCategory class1,
			SimpleCategory class2) {
		this.class1IDs = new HashSet<>(class1IDs);
		this.class2IDs = new HashSet<>(class2IDs);
		this.myIDType = idType;
		this.class1 = class1;
		this.class2 = class2;
	}

	@Override
	public SimpleCategory apply(Object id, IDType idType) {
		if (idType.getIDCategory() != myIDType.getIDCategory())
			return null;

		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(myIDType);
		Set<Object> ids = mappingManager.getIDAsSet(idType, myIDType, id);
		if (ids == null || ids.isEmpty())
			return null;

		if (!Sets.intersection(ids, class1IDs).isEmpty()) {
			return class1;
		} else if (!Sets.intersection(ids, class2IDs).isEmpty()) {
			return class2;
		}
		return null;
	}

	@Override
	public List<SimpleCategory> getDataClasses() {
		return Lists.newArrayList(class1, class2);
	}

	@Override
	public IDataCellOverlayProvider getOverlayProvider() {
		return new DerivedClassifierOverlayProvider(this);
	}

}
