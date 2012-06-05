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
package org.caleydo.view.stratomex;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized VisBricks view.
 * 
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SerializedStratomexView extends ASerializedView {

	private List<DataContainer> serializedDataContainers;

	/**
	 * Default constructor with default initialization
	 */
	/**
	 * 
	 */
	public SerializedStratomexView() {
	}

	public SerializedStratomexView(GLStratomex visBricks) {
		serializedDataContainers = visBricks.getDataContainers();
	}

	@Override
	public String getViewType() {
		return GLStratomex.VIEW_TYPE;
	}

	@Override
	public String getViewClassType() {
		return GLStratomex.class.getName();
	}

	/**
	 * @return the serializedDataContainers, see
	 *         {@link #serializedDataContainers}
	 */
	public List<DataContainer> getSerializedDataContainers() {
		return serializedDataContainers;
	}
	
	/**
	 * @param serializedDataContainers setter, see {@link #serializedDataContainers}
	 */
	public void setSerializedDataContainers(List<DataContainer> serializedDataContainers) {
		this.serializedDataContainers = serializedDataContainers;
	}

}
