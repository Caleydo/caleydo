/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.pathway.internal;

import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.TypedIDActions.ITypedIDActionFactory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.contextmenu.container.GeneMenuItemContainer;

/**
 * @author Samuel Gratzl
 *
 */
public class TypedIDActionFactory implements ITypedIDActionFactory {

	@Override
	public Collection<Pair<String, Runnable>> create(Integer id, IDType idType, ATableBasedDataDomain dataDomain,
			Object sender) {
		if (!(dataDomain instanceof GeneticDataDomain))
			return Collections.emptyList();

		if (dataDomain.getRecordIDType().equals(idType) && dataDomain.isColumnDimension())
			return GeneMenuItemContainer.create(id, idType, dataDomain, sender);
		if (dataDomain.getDimensionIDType().equals(idType) && !dataDomain.isColumnDimension())
			return GeneMenuItemContainer.create(id, idType, dataDomain, sender);
		return Collections.emptyList();
	}

}
