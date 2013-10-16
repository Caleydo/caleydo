/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * Specialization of {@link TablePerspective} for pathways. Adds a {@link PathwayGraph} to the data container.
 *
 * @author Christian Partl
 * @author Alexander Lex
 * @deprecated - this shouldn't be a table perspective bus something separate
 */
@XmlType
@XmlRootElement
@Deprecated
public class PathwayTablePerspective extends TablePerspective {

	/** The datadomain giving access to the pathways themselves */
	private PathwayDataDomain pathwayDataDomain;
	/** The pathway associated with this data container */
	@XmlTransient
	private PathwayGraph pathway;
	@XmlElement
	private String pathwayTitle;
	@XmlElement
	private EPathwayDatabaseType pathwayDataBaseType;

	public PathwayTablePerspective() {

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
	public PathwayTablePerspective(ATableBasedDataDomain dataDomain, PathwayDataDomain pathwayDataDomain,
			Perspective recordPerspective, Perspective dimensionPerspective, PathwayGraph pathway) {
		super(dataDomain, recordPerspective, dimensionPerspective);
		this.pathwayDataDomain = pathwayDataDomain;
		this.pathway = pathway;
		pathwayTitle = pathway.getTitle();
		pathwayDataBaseType = pathway.getType();

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
		if (pathway == null)
			resolvePathway();
		return pathway;
	}

	/**
	 * @return The data domain that is used for ID-mapping.
	 */
	public PathwayDataDomain getPathwayDataDomain() {
		return pathwayDataDomain;
	}

	@Override
	public List<TablePerspective> getRecordSubTablePerspectives() {

		List<TablePerspective> recordSubTablePerspectives = new ArrayList<TablePerspective>();

		VirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		GroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		for (Group group : groupList) {

			List<Integer> indices = recordVA.getIDsOfGroup(group.getGroupIndex());
			Perspective recordPerspective = new Perspective(dataDomain, dataDomain.getRecordIDType());
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(indices);
			recordPerspective.init(data);

			PathwayTablePerspective subTablePerspective = new PathwayTablePerspective(dataDomain, pathwayDataDomain,
					recordPerspective, dimensionPerspective, getPathway());
			subTablePerspective.setRecordGroup(group);
			subTablePerspective.parentTablePerspective = this;
			recordSubTablePerspectives.add(subTablePerspective);
		}

		return recordSubTablePerspectives;
	}

	private void resolvePathway() {
		// pathwayDataDomain.get
		pathway = PathwayManager.get().getPathwayByTitle(pathwayTitle, pathwayDataBaseType);
	}

}
