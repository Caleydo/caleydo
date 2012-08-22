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
package org.caleydo.core.data.perspective.table;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.IDefaultLabelHolder;

/**
 * <p>
 * A TablePerspective holds all the "rules" and properties on how the data in
 * the underlying {@link DataTable} should be accessed. It does so by holding
 * references to one {@link DimensionPerspective} and one
 * {@link RecordPerspective}, who define things like order, groups, and
 * hierarchical relationships for either the dimensions or the records of a
 * DataTable.
 * </p>
 * <p>
 * While the perspectives are only defined for either the records or the
 * dimensions, and thereby cannot reference specific cells (and consequently no
 * data), the TablePerspective defines a concrete subset of the data.
 * </p>
 * <p>
 * This allows to calculate statistics (see {@link TablePerspectiveStatistics})
 * for a TablePerspective, thereby providing things like histograms or averages.
 * </p>
 * <p>
 * A TablePerspective should be created/accessed by using
 * {@link ATableBasedDataDomain#getTablePerspective(String, String)}, where the
 * Strings are the IDs of the perspectives that define the TablePerspective. The
 * dataDomain registers the tablePerspective for those perspective and provides
 * other instances which need a TablePerspective for the same combination of
 * perspectives with the same instance of the TablePerspective, thereby avoiding
 * double-calculation of derived meta-data (which can be both, computationally
 * and storage-wise expensive)
 * </p>
 * <p>
 * The tablePerspectives are identified by a the {@link #tablePerspectiveKey},
 * which is created as a function of the identifiers of the two perspectives.
 * </p>
 * <p>
 * Data containers can be hierarchically created based on {@link GroupList}s of
 * one of the {@link AVariablePerspective}s using the
 * {@link #getRecordSubTablePerspectives()} and
 * {@link #getDimensionSubTablePerspectives()}. The resulting
 * <code>TablePerspective</code>s have the {@link #recordGroup}, resp. the
 * #dimensionGroup set, which are otherwise null.
 * </p>
 * 
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public class TablePerspective implements IDefaultLabelHolder {

	/** The static counter used to create unique ids */
	private static int idCounter;
	/** The unique id of the data container */
	private int id;

	/** The key, which is created by using a function of the perspective IDs */
	private String tablePerspectiveKey;

	/** The data domain use in this data container */
	protected ATableBasedDataDomain dataDomain;

	@XmlElement
	private String dataDomainID;

	@XmlElement
	private String recordPerspectiveID;
	/**
	 * The recordPerspective defines the properties of the records (occurrence,
	 * order, groups, relationships)
	 */
	@XmlTransient
	protected RecordPerspective recordPerspective;

	@XmlElement
	private String dimensionPerspectiveID;

	/** Same as {@link #recordPerspective} for dimensions */
	@XmlTransient
	protected DimensionPerspective dimensionPerspective;

	/** A human-readable label */
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
			DATA_CONTAINER, EDataType.INT);

	/**
	 * A group containing all elements of the {@link #recordPerspective}'s
	 * virtual array of this data container. This is only set when the
	 * <code>TablePerspective</code> is a sub-container of another
	 * <code>TablePerspective</code> which
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
	protected TablePerspectiveStatistics tablePerspectiveStatistics;

	/**
	 * Empty constructor, nothing initialized
	 */
	public TablePerspective() {
		System.out.println("S");
	}

	/**
	 * @param dataDomain
	 * @param recordPerspective
	 * @param dimensionPerspective
	 */
	public TablePerspective(ATableBasedDataDomain dataDomain,
			RecordPerspective recordPerspective, DimensionPerspective dimensionPerspective) {
		this.dataDomain = dataDomain;
		this.dataDomainID = dataDomain.getDataDomainID();
		this.recordPerspective = recordPerspective;
		recordPerspectiveID = recordPerspective.getPerspectiveID();
		this.dimensionPerspective = dimensionPerspective;
		dimensionPerspectiveID = dimensionPerspective.getPerspectiveID();
		createKey();
	}

	{
		id = ++idCounter;
		tablePerspectiveStatistics = new TablePerspectiveStatistics(this);
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public int getID() {
		return id;
	}

	/**
	 * @return the tablePerspectiveKey, see {@link #tablePerspectiveKey}
	 */
	public String getTablePerspectiveKey() {
		return tablePerspectiveKey;
	}

	@XmlTransient
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomainID = dataDomain.getDataDomainID();
		this.dataDomain = dataDomain;
	}

	/**
	 * @return the recordPerspective, see {@link #recordPerspective}
	 */
	@XmlTransient
	public RecordPerspective getRecordPerspective() {
		return recordPerspective;
	}

	/**
	 * @param recordPerspective
	 *            setter, see {@link #recordPerspective}
	 */
	public void setRecordPerspective(RecordPerspective recordPerspective) {
		if (this.recordPerspective != null)
			throw new IllegalStateException(
					"Illegal to change perspectives of TablePerspectives.");
		this.recordPerspective = recordPerspective;
		this.recordPerspectiveID = recordPerspective.getPerspectiveID();
		createKey();
	}

	/**
	 * @return the dimensionPerspective, see {@link #dimensionPerspective}
	 */
	@XmlTransient
	public DimensionPerspective getDimensionPerspective() {
		return dimensionPerspective;
	}

	/**
	 * @param dimensionPerspective
	 *            setter, see {@link #dimensionPerspective}
	 */
	public void setDimensionPerspective(DimensionPerspective dimensionPerspective) {
		if (this.dimensionPerspective != null)
			throw new IllegalStateException(
					"Illegal to change perspectives of TablePerspectives.");
		this.dimensionPerspective = dimensionPerspective;
		dimensionPerspectiveID = dimensionPerspective.getPerspectiveID();
		createKey();
	}

	/**
	 * Checks whether the specified container id matches to the record
	 * perspective in this {@link TablePerspective}
	 * 
	 * @param recordPerspectiveID
	 * @return true if the specified id equals the id of the perspective in this
	 *         container
	 */
	public boolean hasRecordPerspective(String recordPerspectiveID) {
		return recordPerspective.getPerspectiveID().equals(recordPerspectiveID);
	}

	/**
	 * Same as {@link #hasRecordPerspective(String)} for dimensions
	 * 
	 * @param dimensionPerspectiveID
	 * @return true if the specified id equals the id of the perspective in this
	 *         container
	 */
	public boolean hasDimensionPerspective(String dimensionPerspectiveID) {
		return dimensionPerspective.getPerspectiveID().equals(dimensionPerspectiveID);
	}

	/**
	 * @return the statistics, see {@link #tablePerspectiveStatistics}
	 */
	public TablePerspectiveStatistics getContainerStatistics() {
		return tablePerspectiveStatistics;
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
	 * @param label
	 *            setter, see {@link #label}
	 */
	@Override
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
	 * Creates and returns one new {@link TablePerspective} for each group in
	 * the {@link RecordPerspective}, where the new {@link RecordPerspective}
	 * contains the elements of the group. The {@link DimensionPerspective} is
	 * the same as for this container.
	 * 
	 * @return a new list of new {@link TablePerspective}s or null if no group
	 *         list is set.
	 */
	public List<TablePerspective> getRecordSubTablePerspectives() {

		List<TablePerspective> recordSubTablePerspectives = new ArrayList<TablePerspective>();

		RecordVirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		for (Group group : groupList) {
			if (groupList.size() == 1 && group.isLabelDefault())
				group.setLabel(getLabel(), isLabelDefault());

			List<Integer> indices = recordVA.getIDsOfGroup(group.getGroupIndex());

			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			recordPerspective.setLabel(group.getLabel(), group.isLabelDefault());
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(indices);
			recordPerspective.init(data);

			TablePerspective subTablePerspective = new TablePerspective(dataDomain,
					recordPerspective, dimensionPerspective);
			subTablePerspective.setRecordGroup(group);
			subTablePerspective.setLabel(recordPerspective.getLabel(),
					recordPerspective.isLabelDefault());
			recordSubTablePerspectives.add(subTablePerspective);

		}

		return recordSubTablePerspectives;
	}

	/**
	 * Creates and returns one new {@link TablePerspective} for each group in
	 * the {@link RecordPerspective}, where the new {@link RecordPerspective}
	 * contains the elements of the group. The {@link DimensionPerspective} is
	 * the same as for this container.
	 * 
	 * @return a new list of new {@link TablePerspective}s or null if no group
	 *         list is set.
	 */
	public List<TablePerspective> getDimensionSubTablePerspectives() {

		List<TablePerspective> dimensionSubTablePerspectives = new ArrayList<TablePerspective>();

		DimensionVirtualArray dimensionVA = dimensionPerspective.getVirtualArray();

		if (dimensionVA.getGroupList() == null)
			return null;

		DimensionGroupList groupList = dimensionVA.getGroupList();
		groupList.updateGroupInfo();

		for (Group group : groupList) {
			if (groupList.size() == 1 && group.isLabelDefault())
				group.setLabel(getLabel(), isLabelDefault());
			List<Integer> indices = dimensionVA.getIDsOfGroup(group.getGroupIndex());

			DimensionPerspective dimensionPerspective = new DimensionPerspective(
					dataDomain);
			dimensionPerspective.setLabel(group.getLabel(), group.isLabelDefault());
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(indices);
			dimensionPerspective.init(data);

			TablePerspective subTablePerspective = new TablePerspective(dataDomain,
					recordPerspective, dimensionPerspective);
			subTablePerspective.setDimensionGroup(group);
			subTablePerspective.setLabel(group.getLabel(), group.isLabelDefault());
			dimensionSubTablePerspectives.add(subTablePerspective);

		}

		return dimensionSubTablePerspectives;
	}

	// public void afterUnmarshal(Unmarshaller u, Object parent) {
	// this.dataDomain = (ATableBasedDataDomain) parent;
	// }
	/**
	 * Creates the {@link #tablePerspectiveKey} if both
	 * {@link #recordPerspective} and {@link #dimensionPerspective} are already
	 * initialized.
	 */
	private void createKey() {
		if (recordPerspective != null && dimensionPerspective != null)
			tablePerspectiveKey = createKey(recordPerspective.getPerspectiveID(),
					dimensionPerspective.getPerspectiveID());
	}

	public static String createKey(String recordPerspectiveID,
			String dimensionPerspectiveID) {
		return recordPerspectiveID + "_" + dimensionPerspectiveID;
	}

	/**
	 * This should be called after the rest of the data, specifically the
	 * perspectives are sucessfully deserialized. Sets the perspectives based on
	 * the serialized perspective IDs.
	 */
	public void postDesirialize() {
		dataDomain = (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(
				dataDomainID);
		recordPerspective = dataDomain.getTable().getRecordPerspective(
				recordPerspectiveID);
		dimensionPerspective = dataDomain.getTable().getDimensionPerspective(
				dimensionPerspectiveID);
		createKey();

	}

	@Override
	public void setLabel(String label) {
		this.label = label;
		
	}

	@Override
	public String getProviderName() {
		return "Table Perspective";
	}


	@Override
	public boolean isLabelDefault() {
		return isDefaultLabel;
	}
}
