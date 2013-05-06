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
package org.caleydo.core.util.base;

import java.util.Comparator;


/**
 * default implementation of an {@link ILabelProvider}
 *
 * @author Samuel Gratzl
 *
 */
public class DefaultLabelProvider implements ILabelHolder {
	private String label;

	public DefaultLabelProvider() {
		label = "";
	}

	public DefaultLabelProvider(String label) {
		this.label = label;
	}

	@Override
	public final String getLabel() {
		return label;
	}

	@Override
	public final void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getProviderName() {
		return null;
	}


	public static final Comparator<ILabelProvider> BY_LABEL = new Comparator<ILabelProvider>() {
		@Override
		public int compare(ILabelProvider dd1, ILabelProvider dd2) {
			return String.CASE_INSENSITIVE_ORDER.compare(dd1.getLabel(),dd2.getLabel());
		}
	};
}
