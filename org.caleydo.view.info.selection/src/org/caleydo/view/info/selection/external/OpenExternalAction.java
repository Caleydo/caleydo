/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.selection.external;

import java.text.MessageFormat;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.view.info.selection.Activator;
import org.caleydo.view.info.selection.external.MyPreferences.OpenExternally;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * @author Samuel Gratzl
 *
 */
public class OpenExternalAction extends Action {
	private final String url;

	public OpenExternalAction(String label, String url) {
		super("Search in " + label, Activator.getImageDescriptor("resources/icons/external.png"));
		this.url = url;
	}

	@Override
	public void run() {
		BrowserUtils.openURL(url);
	}

	public static IAction create(IDType type, Object value) {
		OpenExternally pair = MyPreferences.getExternalIDCategory(type.getIDCategory());
		if (pair == null)
			return null;

		String pattern = pair.getPattern();
		IDType argumentType = pair.getIdType();
		String label = pair.getLabel();

		IDMappingManager manager = IDMappingManagerRegistry.get().getIDMappingManager(argumentType);
		Set<Object> result = manager.getIDAsSet(type, argumentType, value);
		if (result == null || result.isEmpty())
			return null;
		Object id = result.iterator().next();

		String url = MessageFormat.format(pattern, id);

		return new OpenExternalAction(label, url);
	}
}
