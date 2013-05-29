/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.info.selection.external;

import java.text.MessageFormat;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.view.info.selection.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * @author Samuel Gratzl
 *
 */
public class OpenExternalAction extends Action {
	private final String url;

	public OpenExternalAction(String url) {
		super("Open Externally",Activator.getImageDescriptor("resources/icons/external.png"));
		this.url = url;
	}

	@Override
	public void run() {
		BrowserUtils.openURL(url);
	}

	public static IAction create(IDType type, Object value) {
		Pair<String,IDType> pair = MyPreferences.getExternalIDCategory(type.getIDCategory());
		if (pair == null)
			return null;

		String pattern = pair.getFirst();
		IDType argumentType = pair.getSecond();

		IDMappingManager manager = IDMappingManagerRegistry.get().getIDMappingManager(argumentType);
		Set<Object> result = manager.getIDAsSet(type, argumentType, value);
		if (result.isEmpty())
			return null;
		Object id = result.iterator().next();

		String url = MessageFormat.format(pattern, id);

		return new OpenExternalAction(url);
	}
}
