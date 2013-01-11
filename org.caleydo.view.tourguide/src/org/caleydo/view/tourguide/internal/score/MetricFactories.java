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
package org.caleydo.view.tourguide.internal.score;

import java.util.Map;
import java.util.Set;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.spi.IMetricFactory;
import org.caleydo.view.tourguide.spi.score.IScore;

/**
 * @author Samuel Gratzl
 *
 */
public class MetricFactories {
	private static final String EXTENSION_ID = "org.caleydo.tourguide.metricfactory";

	private final static Map<String, IMetricFactory> factories = ExtensionUtils.findImplementation(EXTENSION_ID,
			"name", "class", IMetricFactory.class);

	public static void addCreateItems(ContextMenuCreator creator, Set<IScore> visible, Object receiver,
			EDataDomainQueryMode mode) {
		for (IMetricFactory f : factories.values()) {
			if (f.supports(mode))
				f.addCreateMetricItems(creator, visible, receiver);
		}
	}
}
