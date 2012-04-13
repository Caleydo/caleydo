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
package org.caleydo.datadomain.pathway.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * Specialization of {@link DataContainer} for pathways. Adds a
 * {@link PathwayGraph} to the data container.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * 
 */
@XmlType
@XmlRootElement
public class PathwayDataContainer extends DataContainer {

	/** The datadomain giving access to the pathways themselves */

	private PathwayDataDomain pathwayDataDomain;
	/** The pathway associated with this data container */
	private PathwayGraph pathway;
	
	public PathwayDataContainer() {
		
	}

	/**
	 * 
	 * @param dataDomain
	 *            the data domain used for the mapping of the expression values
	 * @param pathwayDataDomain
	 *            the datadomain holding the actual pathways
	 * @param recordPerspective
	 * @param dimensionPerspective
	 * @param pathway
	 */
	public PathwayDataContainer(ATableBasedDataDomain dataDomain,
			PathwayDataDomain pathwayDataDomain, RecordPerspective recordPerspective,
			DimensionPerspective dimensionPerspective, PathwayGraph pathway) {
		super(dataDomain, recordPerspective, dimensionPerspective);
		this.pathwayDataDomain = pathwayDataDomain;
		this.pathway = pathway;
	}

	@Override
	public String getLabel() {
		if (pathway != null)
			return pathway.getTitle();
		return "";
	}

	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

	/**
	 * @return The data domain that is used for ID-mapping.
	 */
	public PathwayDataDomain getPathwayDataDomain() {
		return pathwayDataDomain;
	}
	
	@Override
	public List<DataContainer> getRecordSubDataContainers() {

		List<DataContainer> recordSubDataContainers = new ArrayList<DataContainer>();

		RecordVirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		for (Group group : groupList) {

			List<Integer> indices = recordVA.getIDsOfGroup(group.getGroupIndex());
			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(indices);
			recordPerspective.init(data);

			PathwayDataContainer subDataContainer = new PathwayDataContainer(dataDomain,
					pathwayDataDomain, recordPerspective, dimensionPerspective,
					pathway);
			subDataContainer.setRecordGroup(group);
			recordSubDataContainers.add(subDataContainer);
		}

		return recordSubDataContainers;
	}
}
