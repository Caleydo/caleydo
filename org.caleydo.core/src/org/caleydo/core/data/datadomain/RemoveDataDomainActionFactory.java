/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.data.datadomain.DataDomainActions.IDataDomainActionFactory;
import org.caleydo.core.data.datadomain.event.AskRemoveDataDomainEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.collection.Pair;

/**
 * @author Samuel Gratzl
 *
 */
public class RemoveDataDomainActionFactory implements IDataDomainActionFactory {
	@Override
	public Collection<Pair<String, ? extends AEvent>> create(IDataDomain dataDomain, Object sender) {
		Collection<Pair<String, ? extends AEvent>> r = new ArrayList<>(1);
		if (dataDomain instanceof ATableBasedDataDomain)
			r.add(Pair.make("Remove " + dataDomain.getProviderName().toLowerCase(), new AskRemoveDataDomainEvent(
					dataDomain)));
		return r;
	}
}
