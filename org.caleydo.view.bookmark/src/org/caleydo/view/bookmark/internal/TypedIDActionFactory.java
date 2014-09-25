/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.bookmark.internal;

import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.TypedIDActions.ITypedIDActionFactory;
import org.caleydo.core.event.data.BookmarkEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.core.util.collection.Pair;

/**
 * @author Samuel Gratzl
 *
 */
public class TypedIDActionFactory implements ITypedIDActionFactory {

	@Override
	public Collection<Pair<String, Runnable>> create(final Integer id, final IDType idType, ATableBasedDataDomain dataDomain,
			final Object sender) {
		String label = "Bookmark "+idType.getIDCategory().getHumanReadableIDType() + ": "
				+ dataDomain.getRecordLabel(idType, id);
		BookmarkEvent<Integer> event = new BookmarkEvent<Integer>(idType);
		event.addBookmark(id);
		event.setSender(sender);
		return Collections.singleton((Pair<String, Runnable>) Pair.make(label, Runnables.sendEvent(event)));
	}

}
