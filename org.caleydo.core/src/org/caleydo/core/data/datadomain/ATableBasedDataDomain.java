/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.collection.table.CategoricalTable;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.event.AggregateGroupEvent;
import org.caleydo.core.data.datadomain.event.AggregateGroupListener;
import org.caleydo.core.data.datadomain.event.CreateClusteringEvent;
import org.caleydo.core.data.datadomain.event.LoadGroupingEvent;
import org.caleydo.core.data.datadomain.event.StartClusteringListener;
import org.caleydo.core.data.datadomain.listener.CreateClusteringEventListener;
import org.caleydo.core.data.datadomain.listener.LoadGroupingEventListener;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ISelectionHandler;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.IVADeltaHandler;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.ReplacePerspectiveEvent;
import org.caleydo.core.data.virtualarray.events.ReplacePerspectiveListener;
import org.caleydo.core.data.virtualarray.events.SortByDataEvent;
import org.caleydo.core.data.virtualarray.events.VADeltaEvent;
import org.caleydo.core.data.virtualarray.events.VADeltaListener;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.data.StartClusteringEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterManager;
import org.caleydo.core.util.clusterer.ClusterResult;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.color.mapping.ColorMapper;

/**
 * <p>
 * Primary access point to a table data set. Holds the {@link Table}, the {@link TablePerspective} which hold the rules
 * on how to access the Table and a lot of meta-information such as human-readable labels.
 * </p>
 * <p>
 * Holds the information on the {@link IDCategory} and the {@link IDType}s of the rows and columns in the data set.
 * </p>
 * <p>
 * {@link ATableBasedDataDomain}s are initialized using a {@link DataDomainConfiguration} object, which can be either
 * specified or taken from a default initialization of an implementing sub-class.
 * </p>
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public abstract class ATableBasedDataDomain extends ADataDomain implements IVADeltaHandler, ISelectionHandler {

	/** The raw data for this data domain. */
	protected Table table;

	/**
	 * <p>
	 * The {@link TablePerspective} registered for this data domain. A {@link TablePerspective} is defined by its
	 * combination of {@link Perspective} and {@link DimensionPerspective}.
	 * </p>
	 * <p>
	 * The key in this hasMap is created as a concatenation of the {@link Perspective#getPerspectiveID()} s using
	 * {@link #createKey(String, String)},
	 * </p>
	 */
	protected HashMap<String, TablePerspective> tablePerspectives = new HashMap<String, TablePerspective>();

	protected IDCategory recordIDCategory;
	protected IDCategory dimensionIDCategory;

	protected IDCategory recordGroupIDCategory;
	protected IDCategory dimensionGroupIDCategory;

	protected IDType recordIDType;
	protected IDType dimensionIDType;

	/** IDType used for {@link Group}s or records in this dataDomain */
	protected IDType recordGroupIDType;

	/** same as {@link #recordGroupIDType} for dimensions */
	protected IDType dimensionGroupIDType;

	protected SelectionManager recordSelectionManager;
	protected SelectionManager dimensionSelectionManager;
	protected SelectionManager recordGroupSelectionManager;

	/** central {@link EventPublisher} to receive and send events */
	protected EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	/**
	 * All recordPerspectiveIDs registered with the Table. This variable is synchronous with the keys of the hashMap of
	 * the Table.
	 */
	@XmlElement
	private Set<String> recordPerspectiveIDs;

	/** Same as {@link #recordPerspectiveIDs} for dimensions */
	@XmlElement
	private Set<String> dimensionPerspectiveIDs;

	/** The color-mapper to be used by views of this data domain */
	private ColorMapper colorMapper;

	protected IDMappingManager recordIDMappingManager;
	protected IDMappingManager dimensionIDMappingManager;

	/**
	 * Constructor that should be used only for serialization
	 */
	public ATableBasedDataDomain() {
		super();
	}

	/**
	 * @return the tablePerspectives, see {@link #tablePerspectives}
	 */
	public HashMap<String, TablePerspective> getTablePerspectives() {
		return tablePerspectives;
	}

	/**
	 * @param tablePerspectives
	 *            setter, see {@link #tablePerspectives}
	 */
	public void setTablePerspectives(HashMap<String, TablePerspective> tablePerspectives) {
		this.tablePerspectives = tablePerspectives;
	}

	public ATableBasedDataDomain(String dataDomainType, String dataDomainID) {
		super(dataDomainType, dataDomainID);
	}

	{
		defaultStartViewType = "org.caleydo.view.heatmap.hierarchical";
	}

	/**
	 * @return the isColumnDimension, see {@link #isColumnDimension}
	 */
	public boolean isColumnDimension() {
		return !dataSetDescription.isTransposeMatrix();
	}

	@Override
	public void init() {

		if (dataSetDescription.isTransposeMatrix()) {
			recordIDCategory = IDCategory.getIDCategory(dataSetDescription.getColumnIDSpecification().getIdCategory());
			dimensionIDCategory = IDCategory.getIDCategory(dataSetDescription.getRowIDSpecification().getIdCategory());
		} else {
			recordIDCategory = IDCategory.getIDCategory(dataSetDescription.getRowIDSpecification().getIdCategory());
			dimensionIDCategory = IDCategory.getIDCategory(dataSetDescription.getColumnIDSpecification()
					.getIdCategory());

		}

		recordIDType = IDType.registerType("record_" + dataDomainID + "_" + hashCode(), recordIDCategory,
				EDataType.INTEGER);
		recordIDType.setInternalType(true);
		dimensionIDType = IDType.registerType("dimension_" + dataDomainID + "_" + hashCode(), dimensionIDCategory,
				EDataType.INTEGER);
		dimensionIDType.setInternalType(true);

		recordGroupIDCategory = IDCategory.registerCategory(recordIDCategory.getCategoryName() + "_GROUP");
		recordGroupIDCategory.setInternalCategory(true);
		recordGroupIDType = IDType.registerType("group_record_" + dataDomainID + "_" + hashCode(),
				recordGroupIDCategory, EDataType.INTEGER);
		recordGroupIDType.setInternalType(true);

		dimensionGroupIDCategory = IDCategory.registerCategory(dimensionIDCategory.getCategoryName() + "_GROUP");
		dimensionGroupIDCategory.setInternalCategory(true);
		dimensionGroupIDType = IDType.registerType("group_dimension_" + dataDomainID + "_" + hashCode(),
				dimensionGroupIDCategory, EDataType.INTEGER);
		dimensionGroupIDType.setInternalType(true);

		recordIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(recordIDCategory);
		dimensionIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(dimensionIDCategory);

		recordSelectionManager = new SelectionManager(recordIDType);
		dimensionSelectionManager = new SelectionManager(dimensionIDType);
		recordGroupSelectionManager = new SelectionManager(recordGroupIDType);

		addIDCategory(dimensionIDCategory);
		addIDCategory(recordIDCategory);

		super.init();

	}

	/**
	 * Sets the {@link #table} of this dataDomain. The table may not be null. Initializes {@link #recordPerspectiveIDs}
	 * and {@link #dimensionPerspectiveIDs}.
	 *
	 * @param table
	 *            The new set which replaced the currently loaded one.
	 */
	public void setTable(Table table) {
		if (table == null)
			throw new IllegalArgumentException("Table was null");
		this.table = table;

		recordPerspectiveIDs = table.getRecordPerspectiveIDs();
		dimensionPerspectiveIDs = table.getDimensionPerspectiveIDs();
	}

	/**
	 * @return the table, see {@link #table}
	 */
	@XmlTransient
	public Table getTable() {
		return table;
	}

	/**
	 * Returns the {@link TablePerspective} for the {@link Perspective} and the {@link DimensionPerspective} specified.
	 * </p>
	 * <p>
	 * If such a container exists already, the existing container is returned. If not, a new container is created and
	 * the datadomain will be notified.
	 *
	 * </p>
	 *
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 * @return
	 */
	public TablePerspective getTablePerspective(String recordPerspectiveID, String dimensionPerspectiveID) {
		return getTablePerspective(recordPerspectiveID, dimensionPerspectiveID, true);
	}

	/**
	 * Returns the {@link TablePerspective} for the {@link Perspective} and the {@link DimensionPerspective} specified.
	 * </p>
	 * <p>
	 * If such a container exists already, the existing container is returned. If not, a new container is created.
	 * </p>
	 *
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 * @param flag
	 *            that determines whether a datadomain update event will be sent or not
	 * @return
	 */
	public TablePerspective getTablePerspective(String recordPerspectiveID, String dimensionPerspectiveID,
			boolean notifyDataDomain) {

		TablePerspective container = tablePerspectives.get(TablePerspective.createKey(recordPerspectiveID,
				dimensionPerspectiveID));
		if (container == null) {
			Perspective recordPerspective = table.getRecordPerspective(recordPerspectiveID);
			if (recordPerspective == null)
				throw new IllegalArgumentException("No record perspective registered with this datadomain for "
						+ recordPerspectiveID);

			Perspective dimensionPerspective = table.getDimensionPerspective(dimensionPerspectiveID);
			if (dimensionPerspective == null)
				throw new IllegalArgumentException("No dimension perspective registered with this datadomain for "
						+ dimensionPerspectiveID);

			container = new TablePerspective(this, recordPerspective, dimensionPerspective);

			tablePerspectives.put(TablePerspective.createKey(recordPerspectiveID, dimensionPerspectiveID), container);

			if (notifyDataDomain) {
				DataDomainUpdateEvent event = new DataDomainUpdateEvent(this);
				event.setSender(this);
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}
		}

		return container;
	}

	/**
	 * Returns a data container based on its key
	 *
	 * @param tablePerspectiveKey
	 * @return
	 */
	public TablePerspective getTablePerspective(String tablePerspectiveKey) {
		return tablePerspectives.get(tablePerspectiveKey);
	}

	/** Returns the data container made up of the default perspectives */
	public TablePerspective getDefaultTablePerspective() {
		return getTablePerspective(table.getDefaultRecordPerspective().getPerspectiveID(), table
				.getDefaultDimensionPerspective().getPerspectiveID());
	}

	/**
	 * Returns whether a {@link TablePerspective} Object exists in this datadomain for the given perspectiveIDs.
	 *
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 * @return
	 */
	public boolean hasTablePerspective(String recordPerspectiveID, String dimensionPerspectiveID) {
		return tablePerspectives.containsKey(TablePerspective.createKey(recordPerspectiveID, dimensionPerspectiveID));
	}

	/**
	 * @return All {@link TablePerspective}s of this datadomain.
	 */
	public Collection<TablePerspective> getAllTablePerspectives() {
		return tablePerspectives.values();
	}

	/**
	 * @return the recordIDMappingManager, see {@link #recordIDMappingManager}
	 */
	public IDMappingManager getRecordIDMappingManager() {
		return recordIDMappingManager;
	}

	/**
	 * @return the dimensionIDMappingManager, see {@link #dimensionIDMappingManager}
	 */
	public IDMappingManager getDimensionIDMappingManager() {
		return dimensionIDMappingManager;
	}

	/**
	 * @return the recordIDType, see {@link #recordIDType}
	 */
	public IDType getRecordIDType() {
		return recordIDType;
	}

	/**
	 * @return the dimensionIDType, see {@link #dimensionIDType}
	 */
	public IDType getDimensionIDType() {
		return dimensionIDType;
	}

	/**
	 * @return the recordIDCategory, see {@link #recordIDCategory}
	 */
	public IDCategory getRecordIDCategory() {
		return recordIDCategory;
	}

	/**
	 * @return the dimensionIDCategory, see {@link #dimensionIDCategory}
	 */
	public IDCategory getDimensionIDCategory() {
		return dimensionIDCategory;
	}

	/**
	 * @return the recordGroupIDType, see {@link #recordGroupIDType}
	 */
	public IDType getRecordGroupIDType() {
		return recordGroupIDType;
	}

	/**
	 * @return the dimensionGroupIDType, see {@link #dimensionGroupIDType}
	 */
	public IDType getDimensionGroupIDType() {
		return dimensionGroupIDType;
	}

	/**
	 * Returns a clone of the record selection manager. This is the preferred way to initialize SelectionManagers.
	 *
	 * @return a clone of the record selection manager
	 */
	public SelectionManager getRecordSelectionManager() {
		return recordSelectionManager.clone();
	}

	/**
	 * Returns a clone of the dimension selection manager. This is the preferred way to initialize SelectionManagers.
	 *
	 * @return a clone of the dimension selection manager
	 */
	public SelectionManager getDimensionSelectionManager() {
		return dimensionSelectionManager.clone();
	}

	/**
	 * Returns a clone of the record group selection manager. This is the preferred way to initialize SelectionManagers.
	 * *
	 *
	 * @return a clone of the dimension selection manager
	 */
	public SelectionManager getRecordGroupSelectionManager() {
		return recordGroupSelectionManager.clone();
	}

	/**
	 * Returns the virtual array for the type
	 *
	 * @param recordPerspectiveID
	 *            the type of VA requested
	 * @return
	 */
	public VirtualArray getRecordVA(String recordPerspectiveID) {
		VirtualArray va = table.getRecordPerspective(recordPerspectiveID).getVirtualArray();
		return va;
	}

	/**
	 * Returns the virtual array for the type
	 *
	 * @param dimensionPerspectiveID
	 *            the type of VA requested
	 * @return
	 */
	public VirtualArray getDimensionVA(String dimensionPerspectiveID) {
		VirtualArray va = table.getDimensionPerspective(dimensionPerspectiveID).getVirtualArray();
		return va;
	}

	/**
	 * @return the recordPerspectiveIDs, see {@link #recordPerspectiveIDs}
	 */
	public Set<String> getRecordPerspectiveIDs() {
		return recordPerspectiveIDs;
	}

	/**
	 * @return the dimensionPerspectiveIDs, see {@link #dimensionPerspectiveIDs}
	 */
	public Set<String> getDimensionPerspectiveIDs() {
		return dimensionPerspectiveIDs;
	}

	/**
	 * @return the colorMapper, see {@link #colorMapper}
	 */
	public ColorMapper getColorMapper() {
		if (colorMapper == null) {
			if (table instanceof NumericalTable && ((NumericalTable) table).getDataCenter() != null) {
				colorMapper = ColorMapper.createDefaultThreeColorMapper();
			} else if (table instanceof CategoricalTable<?>) {
				colorMapper = ((CategoricalTable<?>) table).createColorMapper();
			} else {
				colorMapper = ColorMapper.createDefaultTwoColorMapper();
			}
		}
		return colorMapper;
	}

	/**
	 * @param colorMapper
	 *            setter, see {@link #colorMapper}
	 */
	public void setColorMapper(ColorMapper colorMapper) {
		this.colorMapper = colorMapper;
	}

	/**
	 * Initiates clustering based on the parameters passed. Sends out an event to all affected views upon positive
	 * completion to replace their VA.
	 *
	 * @param tableID
	 *            ID of the set to cluster
	 * @param clusterState
	 */
	public ClusterResult startClustering(ClusterConfiguration clusterState) {
		// FIXME this should be re-designed so that the clustering is a separate
		// thread and communicates via
		// events

		ClusterManager clusterManager = new ClusterManager(this);
		ClusterResult result = clusterManager.cluster(clusterState);

		// check if clustering failed. If so, we just ignore it.
		if (result == null)
			return null;

		if (clusterState.getClusterTarget() == EClustererTarget.DIMENSION_CLUSTERING) {
			PerspectiveInitializationData dimensionResult = result.getDimensionResult();
			Perspective targetDimensionPerspective;
			boolean registerLater = false;
			if (clusterState.isModifyExistingPerspective()) {
				targetDimensionPerspective = clusterState.getSourceDimensionPerspective();
			} else {
				targetDimensionPerspective = clusterState.getOptionalTargetDimensionPerspective();
				if (targetDimensionPerspective == null) {
					registerLater = true;
					targetDimensionPerspective = new Perspective(this, dimensionIDType);

				}
			}
			targetDimensionPerspective.init(dimensionResult);
			targetDimensionPerspective.setLabel(clusterState.getClusterAlgorithmConfiguration()
					.getClusterAlgorithmName()
					+ " "
					+ targetDimensionPerspective.getVirtualArray().getGroupList().size(), false);
			if (registerLater) {
				table.registerDimensionPerspective(targetDimensionPerspective);
			}
			eventPublisher.triggerEvent(new DimensionVAUpdateEvent(dataDomainID, targetDimensionPerspective
					.getPerspectiveID(), this));
		}

		if (clusterState.getClusterTarget() == EClustererTarget.RECORD_CLUSTERING) {
			PerspectiveInitializationData recordResult = result.getRecordResult();
			Perspective targetRecordPerspective;
			boolean registerLater = false;
			if (clusterState.isModifyExistingPerspective()) {
				targetRecordPerspective = clusterState.getSourceRecordPerspective();
			} else {
				targetRecordPerspective = clusterState.getOptionalTargetRecordPerspective();
				if (targetRecordPerspective == null) {
					registerLater = true;
					targetRecordPerspective = new Perspective(this, recordIDType);

				}
			}
			targetRecordPerspective.init(recordResult);
			targetRecordPerspective.setLabel(clusterState.getClusterAlgorithmConfiguration().getClusterAlgorithmName()
					+ " " + targetRecordPerspective.getVirtualArray().getGroupList().size(), false);
			if (registerLater) {
				table.registerRecordPerspective(targetRecordPerspective);
			}
			eventPublisher.triggerEvent(new RecordVAUpdateEvent(dataDomainID, targetRecordPerspective
					.getPerspectiveID(), this));
		}

		eventPublisher.triggerEvent(new DataDomainUpdateEvent(this));
		return result;
	}

	@Override
	public void handleVADelta(VirtualArrayDelta vaDelta, String info) {

		IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
		if (targetCategory == recordIDCategory) {
			if (vaDelta.getIDType() != recordIDType)
				vaDelta = DeltaConverter.convertDelta(recordIDMappingManager, recordIDType, vaDelta);
			Perspective recordPerspective = table.getRecordPerspective(vaDelta.getPerspectiveID());
			recordPerspective.setVADelta(vaDelta);

			RecordVAUpdateEvent event = new RecordVAUpdateEvent(dataDomainID, recordPerspective.getPerspectiveID(),
					this);
			eventPublisher.triggerEvent(event);
		} else if (targetCategory == dimensionIDCategory) {
			if (vaDelta.getIDType() != dimensionIDType)
				vaDelta = DeltaConverter.convertDelta(dimensionIDMappingManager, dimensionIDType, vaDelta);
			Perspective dimensionPerspective = table.getDimensionPerspective(vaDelta.getPerspectiveID());
			dimensionPerspective.setVADelta(vaDelta);

			RecordVAUpdateEvent event = new RecordVAUpdateEvent(dataDomainID, dimensionPerspective.getPerspectiveID(),
					this);
			eventPublisher.triggerEvent(event);
		}

	}

	@Override
	public void replacePerspective(String dataDomainID, String perspectiveID, PerspectiveInitializationData data) {

		if (dataDomainID != this.dataDomainID) {
			handleForeignRecordVAUpdate(dataDomainID, perspectiveID, data);
			return;
		}

		if (table.getRecordPerspective(perspectiveID) != null) {
			table.getRecordPerspective(perspectiveID).init(data);

			RecordVAUpdateEvent event = new RecordVAUpdateEvent();
			event.setSender(this);
			event.setEventSpace(dataDomainID);
			event.setPerspectiveID(perspectiveID);
			eventPublisher.triggerEvent(event);
		} else if (table.getDimensionPerspective(perspectiveID) != null) {
			table.getDimensionPerspective(perspectiveID).init(data);

			DimensionVAUpdateEvent event = new DimensionVAUpdateEvent();
			event.setEventSpace(dataDomainID);
			event.setSender(this);
			event.setPerspectiveID(perspectiveID);
			eventPublisher.triggerEvent(event);
		}
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {

		if (recordSelectionManager == null)
			return;

		if (recordIDMappingManager.hasMapping(selectionDelta.getIDType(), recordSelectionManager.getIDType())) {
			recordSelectionManager.setDelta(selectionDelta);
		} else if (dimensionIDMappingManager.hasMapping(selectionDelta.getIDType(),
				dimensionSelectionManager.getIDType())) {
			dimensionSelectionManager.setDelta(selectionDelta);
		}

		if (selectionDelta.getIDType() == recordGroupSelectionManager.getIDType()) {
			recordGroupSelectionManager.setDelta(selectionDelta);
		}
	}

	@Override
	public void handleSelectionCommand(IDCategory idCategory, SelectionCommand selectionCommand) {
		// TODO Auto-generated method stub
	}

	/**
	 * This method is called by the {@link ForeignSelectionUpdateListener}, signaling that a selection form another
	 * dataDomain is available. If possible, it is converted to be compatible with the local dataDomain and then sent
	 * out via a {@link SelectionUpdateEvent}.
	 *
	 * @param dataDomainType
	 *            the type of the dataDomain for which this selectionUpdate is intended
	 * @param delta
	 * @param scrollToSelection
	 * @param info
	 */
	public void handleForeignSelectionUpdate(String dataDomainType, SelectionDelta delta) {
		// may be interesting to implement in sub-class
	}

	/**
	 * Interface used by {@link ForeignSelectionCommandListener} to signal foreign selection commands. Can be
	 * implemented in concrete classes, has no functionality in base class.
	 */
	public void handleForeignSelectionCommand(String dataDomainType, IDCategory idCategory,
			SelectionCommand selectionCommand) {
		// may be interesting to implement in sub-class
	}

	/**
	 * This method is called if a record VA Update was requested, but the dataDomainType specified was not this
	 * dataDomains type. Concrete handling can only be done in concrete dataDomains.
	 *
	 * @param tableID
	 * @param dataDomainType
	 * @param vaType
	 * @param data
	 */
	public void handleForeignRecordVAUpdate(String dataDomainType, String vaType, PerspectiveInitializationData data) {
		// may be interesting to implement in sub-class
	}

	/**
	 * Returns the denomination for the records. For genetic data for example this would be "Gene"
	 *
	 * @param capitalized
	 *            if true, the label is returned capitalized, e.g., "Gene", if false it would be "gene"
	 * @param plural
	 *            if true, the label is returned in the plural form of the word, e.g., "genes" instead of the singular
	 *            form, e.g., "gene"
	 * @return the denomination formatted according to the parameters passed
	 */
	public String getRecordDenomination(boolean capitalized, boolean plural) {
		String recordDenomination;
		if (plural)
			recordDenomination = recordIDCategory.getDenominationPlural();
		else
			recordDenomination = recordIDCategory.getDenomination();

		if (capitalized) {
			// Make first char capitalized
			recordDenomination = recordDenomination.substring(0, 1).toUpperCase()
					+ recordDenomination.substring(1, recordDenomination.length());
		}
		return recordDenomination;
	}

	/** Same as {@link #getRecordDenomination(boolean, boolean)} for dimensions. */
	public String getDimensionDenomination(boolean capitalized, boolean plural) {
		String dimensionDenomination;

		if (plural)
			dimensionDenomination = dimensionIDCategory.getDenominationPlural();
		else
			dimensionDenomination = dimensionIDCategory.getDenomination();

		if (capitalized) {
			// Make first char capitalized
			dimensionDenomination = dimensionDenomination.substring(0, 1).toUpperCase()
					+ dimensionDenomination.substring(1, dimensionDenomination.length());
		}
		return dimensionDenomination;
	}

	/**
	 * Get the human readable record label for the id, which is of the {@link #recordIDType}.
	 *
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getRecordLabel(Object id) {
		return getRecordLabel(recordIDType, id);
	}

	/**
	 * Get the human readable dimension label for the id, which is of the {@link #dimensionIDType}.
	 *
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getDimensionLabel(Object id) {
		return getDimensionLabel(dimensionIDType, id);
	}

	/**
	 * Get the human readable record label for the id, which is of the type specified.
	 *
	 * @param idType
	 *            the IDType of the id passed
	 * @param id
	 * @return the readable label
	 */
	public String getRecordLabel(IDType idType, Object id) {
		Set<String> ids = recordIDMappingManager
				.getIDAsSet(idType, idType.getIDCategory().getHumanReadableIDType(), id);
		String label = "No Mapping";
		if (ids != null && ids.size() > 0) {
			label = ids.iterator().next();
		}
		return label;
	}

	/** Same as {@link #getRecordLabel(IDType, Object)} for dimensions */
	public String getDimensionLabel(IDType idType, Object id) {
		Set<String> ids = dimensionIDMappingManager.getIDAsSet(idType, idType.getIDCategory().getHumanReadableIDType(),
				id);
		String label = "No Mapping";
		if (ids != null && ids.size() > 0) {
			label = ids.iterator().next();
		}
		return label;
	}

	public void aggregateGroups(java.util.Set<Integer> groups) {
		System.out.println("Received command to aggregate experiments, not implemented yet");
	}

	// FIXME CONTEXT MENU
	// /**
	// * A dataDomain may contribute to the context menu. This function returns
	// the recordItemContainer of the
	// * context menu if one was specified. This should be overridden by
	// subclasses if needed.
	// *
	// * @return a context menu item container related to record items
	// */
	// public AItemContainer getRecordItemContainer(IDType idType, int id) {
	// return null;
	// }

	// FIXME CONTEXT MENU
	// /**
	// * A dataDomain may contribute to the context menu. This function returns
	// dataDomain specific
	// * implementations of a context menu for content groups. * @param idType
	// *
	// * @param ids
	// * @return
	// */
	// public AItemContainer getRecordGroupItemContainer(IDType idType,
	// ArrayList<Integer> ids) {
	// return null;
	// }

	@Override
	public int getDataAmount() {
		if (table == null)
			return 0;
		try {
			return table.size() * table.depth();
		} catch (NoSuchElementException e) {
			return 0;
		}
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		SelectionUpdateListener selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		selectionUpdateListener.setExclusiveEventSpace(dataDomainID);
		listeners.register(SelectionUpdateEvent.class, selectionUpdateListener);

		SelectionCommandListener selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setEventSpace(dataDomainID);
		listeners.register(SelectionCommandEvent.class, selectionCommandListener);

		StartClusteringListener startClusteringListener = new StartClusteringListener();
		startClusteringListener.setHandler(this);
		startClusteringListener.setEventSpace(dataDomainID);
		listeners.register(StartClusteringEvent.class, startClusteringListener);

		VADeltaListener vADeltaListener = new VADeltaListener();
		vADeltaListener.setHandler(this);
		vADeltaListener.setEventSpace(dataDomainID);
		listeners.register(VADeltaEvent.class, vADeltaListener);

		ReplacePerspectiveListener replacePerspectiveListener = new ReplacePerspectiveListener();
		replacePerspectiveListener.setHandler(this);
		replacePerspectiveListener.setEventSpace(dataDomainID);
		listeners.register(ReplacePerspectiveEvent.class, replacePerspectiveListener);

		AggregateGroupListener aggregateGroupListener = new AggregateGroupListener();
		aggregateGroupListener.setHandler(this);
		listeners.register(AggregateGroupEvent.class, aggregateGroupListener);

		listeners.register(CreateClusteringEvent.class, new CreateClusteringEventListener(this));
		listeners.register(LoadGroupingEvent.class, new LoadGroupingEventListener(this));

		listeners.register(this);
	}

	// TODO this is never called!
	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		listeners.unregisterAll();
	}

	/**
	 * <p>
	 * Converts a {@link Perspective} with an IDType that is not the {@link #recordIDType} to a new Perspective with the
	 * recordIDType.
	 * </p>
	 * <p>
	 * Grouping, and naming is preserved, sample elements and trees are not.
	 * </p>
	 */
	public Perspective convertForeignRecordPerspective(Perspective foreignPerspective) {

		if (foreignPerspective.getIdType().getIDCategory() != recordIDCategory) {
			throw new IllegalArgumentException("Can not convert from " + foreignPerspective.getIdType() + " to "
					+ recordIDType);
		}
		if (foreignPerspective.getIdType() == recordIDType)
			return foreignPerspective;

		VirtualArray foreignRecordVA = foreignPerspective.getVirtualArray();

		GroupList recordGroupList = foreignRecordVA.getGroupList();

		PerspectiveInitializationData data = new PerspectiveInitializationData();
		ArrayList<Integer> indices = new ArrayList<Integer>(foreignRecordVA.size());
		ArrayList<Integer> groupSizes = new ArrayList<Integer>(recordGroupList.size());
		ArrayList<Integer> sampleElements = new ArrayList<Integer>(recordGroupList.size());
		ArrayList<String> groupNames = new ArrayList<String>(recordGroupList.size());

		for (Group foreignGroup : recordGroupList) {
			// initialize number of groups with 0
			groupSizes.add(0);
			sampleElements.add(0);
			groupNames.add(foreignGroup.getLabel());

		}

		int count = 0;
		IIDTypeMapper<Integer, Integer> mapper = recordIDMappingManager.getIDTypeMapper(foreignRecordVA.getIdType(),
				recordIDType);
		for (Integer foreignVAID : foreignRecordVA) {
			Set<Integer> localVAIDS = mapper.apply(foreignVAID);
			if (localVAIDS == null)
				continue;
			for (Integer localVAID : localVAIDS) {
				if (localVAID == null)
					continue;
				indices.add(localVAID);
				int groupIndex = recordGroupList.getGroupOfVAIndex(foreignRecordVA.indexOf(foreignVAID))
						.getGroupIndex();
				groupSizes.set(groupIndex, groupSizes.get(groupIndex) + 1);
				sampleElements.set(groupIndex, count);
				count++;
			}

		}

		data.setData(indices, groupSizes, sampleElements, groupNames);

		Perspective localRecordPerspective = new Perspective(this, recordIDType);
		localRecordPerspective.setIDType(recordIDType);
		localRecordPerspective.init(data);
		localRecordPerspective.setLabel(foreignPerspective.getLabel(), foreignPerspective.isLabelDefault());
		return localRecordPerspective;

	}

	@ListenTo
	public void handleVASorting(SortByDataEvent event) {
		if (!event.getDataDomainID().equals(dataDomainID))
			return;

		TablePerspective tPerspective = getTablePerspective(event.getTablePerspectiveKey());

		Perspective perspective = null;

		assert (tPerspective != null) : "no tPerspective for " + event.getTablePerspectiveKey();

		VirtualArray virtualArray;
		ArrayList<Float> valueColumn = null;
		if (tPerspective.getRecordPerspective().getPerspectiveID().equals(event.getPerspectiveID())) {
			perspective = tPerspective.getRecordPerspective();
			virtualArray = perspective.getVirtualArray();

			valueColumn = new ArrayList<>(virtualArray.size());
			for (Integer recordIndex : virtualArray) {
				valueColumn.add(table.getNormalizedValue(event.getId(), recordIndex));
			}

		} else if (tPerspective.getDimensionPerspective().getPerspectiveID().equals(event.getPerspectiveID())) {
			perspective = tPerspective.getDimensionPerspective();
			virtualArray = perspective.getVirtualArray();

			valueColumn = new ArrayList<>(virtualArray.size());
			for (Integer dimensionIndex : virtualArray) {
				valueColumn.add(table.getNormalizedValue(dimensionIndex, event.getId()));
			}
		} else {
			throw new IllegalStateException("Table and value perspective key's don't sync up");
		}

		perspective.sort(valueColumn);

		System.out.println("Arrived" + valueColumn.toString());

	}
}
