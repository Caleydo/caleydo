package org.caleydo.core.view.util;
/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/


import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.internal.cmd.AOpenViewHandler;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.core.view.internal.ShowAndAddToViewAction;

/**
 * @author Samuel Gratzl
 *
 */
public class UIRunnables {
	public static Runnable show(String viewType, TablePerspective... tablePerspective) {
		return show(viewType, null, tablePerspective);
	}

	public static Runnable show(String viewType, String secondaryId, ASerializedView serializationData) {
		return Runnables.withinSWTThread(new ShowAndAddToViewAction(viewType, secondaryId, serializationData));
	}

	public static Runnable show(String viewType, String secondaryId, TablePerspective... tablePerspective) {
		return Runnables.withinSWTThread(new ShowAndAddToViewAction(viewType, secondaryId, tablePerspective));
	}

	public static Runnable showMultiple(String viewType, TablePerspective... tablePerspective) {
		return show(viewType, AOpenViewHandler.createSecondaryID(), tablePerspective);
	}

	public static Runnable showMultiple(String viewType, ASerializedView data) {
		return show(viewType, AOpenViewHandler.createSecondaryID(), data);
	}
}
