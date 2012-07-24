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
package org.caleydo.view.treemap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.serialize.ASerializedSingleDataContainerBasedView;
import org.caleydo.core.view.ISingleDataContainerBasedView;

/**
 * Serialized form of a treemap view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedTreeMapView extends ASerializedSingleDataContainerBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedTreeMapView() {
	}

	public SerializedTreeMapView(ISingleDataContainerBasedView view) {
		super(view);
	}

	@Override
	public String getViewType() {
		return GLTreeMap.VIEW_TYPE;
	}
}
