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
/**
 * 
 */
package org.caleydo.core.data.container;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.ADataPerspective;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;

/**
 * <p>
 * A DataContainer holds all the "rules" and properties on how the data in the
 * underlying {@link DataTable} should be accessed. It does so by holding
 * references to one {@link DimensionPerspective} and one
 * {@link RecordPerspective}, who define things like order, groups, and
 * hierarchical relationships for either the dimensions or the records of a
 * DataTable.
 * </p>
 * <p>
 * While the perspectives are only defined for either the records or the
 * dimensions, and thereby cannot reference specific cells (and consequently no
 * data), the DataContainer defines a concrete subset of the data.
 * </p>
 * <p>
 * This allows to calculate statistics (see {@link ContainerStatistics}) for a
 * DataContainer, thereby providing things like histograms or averages.
 * </p>
 * <p>
 * A DataContainer should be created/accessed by using
 * {@link ATableBasedDataDomain#getDataContainer(String, String)}, where the
 * Strings are the IDs of the perspectives that define the DataContainer. The
 * dataDomain registers the dataContainer for those perspective and provides
 * other instances which need a DataContainer for the same combination of
 * perspectives with the same instance of the DataContainer, thereby avoiding
 * double-calculation of derived meta-data (which can be both, computationally
 * and storage-wise expensive)
 * </p>
 * <p>
 * Data containers can be hierarchically created based on {@link GroupList}s of
 * one of the {@link ADataPerspective}s using the
 * {@link #getRecordSubDataContainers()} and
 * {@link #getDimensionSubDataContainers()}. The resulting
 * <code>DataContainer</code>s have the {@link #recordGroup}, resp. the
 * #dimensionGroup set, which are otherwise null.
 * </p>
 * 
 * @author Alexander Lex
 */
@XmlType
public class DataContainer {

	/** The static counter used to create unique ids */
	private static int idCounter;
	/** The unique id of the data container */
	private int id;

	protected ATableBasedDataDomain dataDomain;

	/**
	 * The recordPerspective defines the properties of the records (occurrence,
	 * order, groups, relationships)
	 */
	protected RecordPerspective recordPerspective;
	/** Same as {@link #recordPerspective} for dimensions */
	protected DimensionPerspective dimensionPerspective;

	/** A human-readable label */
	@XmlElement
	protected String label;

	/**
	 * Flag telling whether the set label is a default (true) and thereby should
	 * probably not be displayed or whether the label is worth displaying
	 */
	@XmlElement
	private boolean isDefaultLabel = true;

	/**
	 * Flag determining whether this data container is private to a certain
	 * view. That means that other views typically should not use this data
	 * container.
	 */
	@XmlElement
	protected boolean isPrivate;

	public static IDCategory DATA_CONTAINER = IDCategory
			.registerCategory("DATA_CONTAINER");
	public static IDType DATA_CONTAINER_IDTYPE = IDType.registerType("DataConatiners",
			DATA_CONTAINER, EColumnType.INT);

	/**
	 * A group containing all elements of the {@link #recordPerspective}'s
	 * virtual array of this data container. This is only set when the
	 * <code>DataContainer</code> is a sub-container of another
	 * <code>DataContainer</code> which
	 */
	protected Group recordGroup = null;

	/**
	 * Same as {@link #recordGroup} for dimensions
	 */
	protected Group dimensionGroup = null;

	/**
	 * Object holding respectively calculating all forms of (statistical)
	 * meta-data for this container
	 */
	@XmlTransient
	protected ContainerStatistics containerStatistics;

	/**
	 * Empty constructor, nothing initialized
	 */
	public DataContainer() {
	}

	/**
	 * @param dataDomain
	 * @param recordPerspective
	 * @param dimensionPerspective
	 */
	public DataContainer(ATableBasedDataDomain dataDomain,
			RecordPerspective recordPerspective, DimensionPerspective dimensionPerspective) {
		this.dataDomain = dataDomain;
		this.recordPerspective = recordPerspective;
		this.dimensionPerspective = dimensionPerspective;

	}

	{
		id = ++idCounter;
		containerStatistics = new ContainerStatistics(this);
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public int getID() {
		return id;
	}

	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * @return the recordPerspective, see {@link #recordPerspective}
	 */
	public RecordPerspective getRecordPerspective() {
		return recordPerspective;
	}

	/**
	 * @param recordPerspective
	 *            setter, see {@link #recordPerspective}
	 */
	public void setRecordPerspective(RecordPerspective recordPerspective) {
		this.recordPerspective = recordPerspective;
	}

	/**
	 * @return the dimensionPerspective, see {@link #dimensionPerspective}
	 */
	public DimensionPerspective getDimensionPerspective() {
		return dimensionPerspective;
	}

	/**
	 * @param dimensionPerspective
	 *            setter, see {@link #dimensionPerspective}
	 */
	public void setDimensionPerspective(DimensionPerspective dimensionPerspective) {
		this.dimensionPerspective = dimensionPerspective;
	}

	/**
	 * Checks whether the specified container id matches to the record
	 * perspective in this {@link DataContainer}
	 * 
	 * @param recordPerspectiveID
	 * @return true if the specified id equals the id of the perspective in this
	 *         container
	 */
	public boolean hasRecordPerspective(String recordPerspectiveID) {
		return recordPerspective.getID().equals(recordPerspectiveID);
	}

	/**
	 * Same as {@link #hasRecordPerspective(String)} for dimensions
	 * 
	 * @param dimensionPerspectiveID
	 * @return true if the specified id equals the id of the perspective in this
	 *         container
	 */
	public boolean hasDimensionPerspective(String dimensionPerspectiveID) {
		return dimensionPerspective.getID().equals(dimensionPerspectiveID);
	}

	/**
	 * @return the statistics, see {@link #containerStatistics}
	 */
	public ContainerStatistics getContainerStatistics() {
		return containerStatistics;
	}

	/**
	 * Returns the size of the virtual array in the record perspective, i.e. the
	 * number of records
	 */
	public int getNrRecords() {
		return recordPerspective.getVirtualArray().size();
	}

	/** Same as {@link #getNrRecords()} for dimensions */
	public int getNrDimensions() {
		return dimensionPerspective.getVirtualArray().size();
	}

	/**
	 * Getter for {@link #label}, creates a default label if none was set
	 * 
	 * @return
	 */
	public String getLabel() {
		if (label == null)
			label = dimensionPerspective.getLabel() + "/" + recordPerspective.getLabel();
		return label;
	}

	/**
	 * @return the isDefaultLabel, see {@link #isDefaultLabel}
	 */
	public boolean isDefaultLabel() {
		return isDefaultLabel;
	}

	/**
	 * @param label
	 *            setter, see {@link #label}
	 */
	public void setLabel(String label, boolean isDefaultLabel) {
		this.label = label;
		this.isDefaultLabel = isDefaultLabel;
	}

	/**
	 * @param isPrivate
	 *            setter, see {@link #isPrivate}
	 */
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	/**
	 * @return the isPrivate, see {@link #isPrivate}
	 */
	public boolean isPrivate() {
		return isPrivate;
	}

	/**
	 * @return the recordGroup, see {@link #recordGroup}
	 */
	public Group getRecordGroup() {
		return recordGroup;
	}

	/**
	 * @param recordGroup
	 *            setter, see {@link #recordGroup}
	 */
	public void setRecordGroup(Group recordGroup) {
		this.recordGroup = recordGroup;
	}

	/**
	 * @return the dimensionGroup, see {@link #dimensionGroup}
	 */
	public Group getDimensionGroup() {
		return dimensionGroup;
	}

	/**
	 * @param dimensionGroup
	 *            setter, see {@link #dimensionGroup}
	 */
	public void setDimensionGroup(Group dimensionGroup) {
		this.dimensionGroup = dimensionGroup;
	}

	/**
	 * Creates and returns one new {@link DataContainer} for each group in the
	 * {@link RecordPerspective}, where the new {@link RecordPerspective}
	 * contains the elements of the group. The {@link DimensionPerspective} is
	 * the same as for this container.
	 * 
	 * @return a new list of new {@link DataContainer}s or null if no group list
	 *         is set.
	 */
	public List<DataContainer> getRecordSubDataContainers() {

		List<DataContainer> recordSubDataContainers = new ArrayList<DataContainer>();

		RecordVirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		for (Group group : groupList) {
			// fixme should check for default label
			if (groupList.size() == 1)
				group.setLabel(getLabel());

			List<Integer> indices = recordVA.getIDsOfGroup(group.getGroupIndex());

			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			recordPerspective.setLabel(group.getLabel(), group.isDefaultLabel());
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(indices);
			recordPerspective.init(data);

			DataContainer subDataContainer = new DataContainer(dataDomain,
					recordPerspective, dimensionPerspective);
			subDataContainer.setRecordGroup(group);
			subDataContainer.setLabel(recordPerspective.getLabel(),
					recordPerspective.isDefaultLabel());
			recordSubDataContainers.add(subDataContainer);

		}

		return recordSubDataContainers;
	}

	/**
	 * Creates and returns one new {@link DataContainer} for each group in the
	 * {@link RecordPerspective}, where the new {@link RecordPerspective}
	 * contains the elements of the group. The {@link DimensionPerspective} is
	 * the same as for this container.
	 * 
	 * @return a new list of new {@link DataContainer}s or null if no group list
	 *         is set.
	 */
	public List<DataContainer> getDimensionSubDataContainers() {

		List<DataContainer> dimensionSubDataContainers = new ArrayList<DataContainer>();

		DimensionVirtualArray dimensionVA = dimensionPerspective.getVirtualArray();

		if (dimensionVA.getGroupList() == null)
			return null;

		DimensionGroupList groupList = dimensionVA.getGroupList();
		groupList.updateGroupInfo();

		for (Group group : groupList) {
			if (groupList.size() == 1)
				group.setLabel(getLabel());
			List<Integer> indices = dimensionVA.getIDsOfGroup(group.getGroupIndex());

			DimensionPerspective dimensionPerspective = new DimensionPerspective(
					dataDomain);
			dimensionPerspective.setLabel(group.getLabel(), group.isDefaultLabel());
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(indices);
			dimensionPerspective.init(data);

			DataContainer subDataContainer = new DataContainer(dataDomain,
					recordPerspective, dimensionPerspective);
			subDataContainer.setDimensionGroup(group);
			subDataContainer.setLabel(group.getLabel(), group.isDefaultLabel());
			dimensionSubDataContainers.add(subDataContainer);

		}

		return dimensionSubDataContainers;
	}

}
