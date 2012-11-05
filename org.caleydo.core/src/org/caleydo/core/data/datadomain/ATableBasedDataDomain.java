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
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.event.AggregateGroupEvent;
import org.caleydo.core.data.datadomain.event.AggregateGroupListener;
import org.caleydo.core.data.datadomain.event.StartClusteringListener;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.selection.DimensionSelectionManager;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ForeignSelectionCommandListener;
import org.caleydo.core.data.selection.events.ForeignSelectionUpdateListener;
import org.caleydo.core.data.selection.events.ISelectionCommandHandler;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionCommandListener;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.events.DimensionVADeltaEvent;
import org.caleydo.core.data.virtualarray.events.DimensionVADeltaListener;
import org.caleydo.core.data.virtualarray.events.DimensionVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.IDimensionChangeHandler;
import org.caleydo.core.data.virtualarray.events.IRecordVADeltaHandler;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaEvent;
import org.caleydo.core.data.virtualarray.events.RecordVADeltaListener;
import org.caleydo.core.data.virtualarray.events.RecordVAUpdateEvent;
import org.caleydo.core.data.virtualarray.events.ReplaceDimensionPerspectiveEvent;
import org.caleydo.core.data.virtualarray.events.ReplaceDimensionPerspectiveListener;
import org.caleydo.core.data.virtualarray.events.ReplaceRecordPerspectiveEvent;
import org.caleydo.core.data.virtualarray.events.ReplaceRecordPerspectiveListener;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
import org.caleydo.core.event.data.SelectionCommandEvent;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.event.data.StartClusteringEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterManager;
import org.caleydo.core.util.clusterer.ClusterResult;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.clusterer.initialization.EClustererTarget;
import org.caleydo.core.util.mapping.color.ColorMapper;

/**
 * <p>
 * Primary access point to a table data set. Holds the {@link DataTable}, the
 * {@link TablePerspective} which hold the rules on how to access the DataTable
 * and a lot of meta-information such as human-readable labels.
 * </p>
 * <p>
 * Holds the information on the {@link IDCategory} and the {@link IDType}s of
 * the rows and columns in the data set.
 * </p>
 * <p>
 * {@link ATableBasedDataDomain}s are initialized using a
 * {@link DataDomainConfiguration} object, which can be either specified or
 * taken from a default initialization of an implementing sub-class.
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public abstract class ATableBasedDataDomain extends ADataDomain implements
		IRecordVADeltaHandler, IDimensionChangeHandler, ISelectionUpdateHandler,
		ISelectionCommandHandler {

	/** The raw data for this data domain. */
	protected DataTable table;

	/**
	 * <p>
	 * The {@link TablePerspective} registered for this data domain. A
	 * {@link TablePerspective} is defined by its combination of
	 * {@link RecordPerspective} and {@link DimensionPerspective}.
	 * </p>
	 * <p>
	 * The key in this hasMap is created as a concatenation of the
	 * {@link AVariablePerspective#getPerspectiveID()} s using
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

	protected RecordSelectionManager recordSelectionManager;
	protected DimensionSelectionManager dimensionSelectionManager;
	protected SelectionManager recordGroupSelectionManager;

	/** central {@link EventPublisher} to receive and send events */
	protected EventPublisher eventPublisher = GeneralManager.get().getEventPublisher();

	/**
	 * All recordPerspectiveIDs registered with the DataTable. This variable is
	 * synchronous with the keys of the hashMap of the DataTable.
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

	private SelectionUpdateListener selectionUpdateListener;
	private SelectionCommandListener selectionCommandListener;
	private StartClusteringListener startClusteringListener;

	private ReplaceDimensionPerspectiveListener replaceDimensionPerspectiveListener;
	private DimensionVADeltaListener dimensionVADeltaListener;
	private ReplaceRecordPerspectiveListener replaceRecordPerspectiveListener;
	private RecordVADeltaListener recordVADeltaListener;

	private AggregateGroupListener aggregateGroupListener;

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
			recordIDCategory = IDCategory.getIDCategory(dataSetDescription
					.getColumnIDSpecification().getIdCategory());
			dimensionIDCategory = IDCategory.getIDCategory(dataSetDescription
					.getRowIDSpecification().getIdCategory());
		} else {
			recordIDCategory = IDCategory.getIDCategory(dataSetDescription
					.getRowIDSpecification().getIdCategory());
			dimensionIDCategory = IDCategory.getIDCategory(dataSetDescription
					.getColumnIDSpecification().getIdCategory());

		}

		recordIDType = IDType.registerType("record_" + dataDomainID + "_" + hashCode(),
				recordIDCategory, EDataType.INT);
		recordIDType.setInternalType(true);
		dimensionIDType = IDType.registerType("dimension_" + dataDomainID + "_"
				+ hashCode(), dimensionIDCategory, EDataType.INT);
		dimensionIDType.setInternalType(true);

		recordGroupIDCategory = IDCategory.registerCategory(recordIDCategory
				.getCategoryName() + "_GROUP");
		recordGroupIDCategory.setInternalCategory(true);
		recordGroupIDType = IDType.registerType("group_record_" + dataDomainID + "_"
				+ hashCode(), recordGroupIDCategory, EDataType.INT);
		recordGroupIDType.setInternalType(true);

		dimensionGroupIDCategory = IDCategory.registerCategory(dimensionIDCategory
				.getCategoryName() + "_GROUP");
		dimensionGroupIDCategory.setInternalCategory(true);
		dimensionGroupIDType = IDType.registerType("group_dimension_" + dataDomainID
				+ "_" + hashCode(), dimensionGroupIDCategory, EDataType.INT);
		dimensionGroupIDType.setInternalType(true);

		recordIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				recordIDCategory);
		dimensionIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				dimensionIDCategory);

		recordSelectionManager = new RecordSelectionManager(recordIDType);
		dimensionSelectionManager = new DimensionSelectionManager(dimensionIDType);
		recordGroupSelectionManager = new SelectionManager(recordGroupIDType);

		addIDCategory(dimensionIDCategory);
		addIDCategory(recordIDCategory);

		super.init();

	}

	/**
	 * Sets the {@link #table} of this dataDomain. The table may not be null.
	 * Initializes {@link #recordPerspectiveIDs} and
	 * {@link #dimensionPerspectiveIDs}.
	 * 
	 * @param table
	 *            The new set which replaced the currently loaded one.
	 */
	public void setTable(DataTable table) {
		if (table == null)
			throw new IllegalArgumentException("DataTable was null");
		this.table = table;

		recordPerspectiveIDs = table.getRecordPerspectiveIDs();
		dimensionPerspectiveIDs = table.getDimensionPerspectiveIDs();
	}

	/**
	 * @return the table, see {@link #table}
	 */
	@XmlTransient
	public DataTable getTable() {
		return table;
	}

	/**
	 * Returns the {@link TablePerspective} for the {@link RecordPerspective}
	 * and the {@link DimensionPerspective} specified. </p>
	 * <p>
	 * If such a container exists already, the existing container is returned.
	 * If not, a new container is created and the datadomain will be notified.
	 * 
	 * </p>
	 * 
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 * @return
	 */
	public TablePerspective getTablePerspective(String recordPerspectiveID,
			String dimensionPerspectiveID) {
		return getTablePerspective(recordPerspectiveID, dimensionPerspectiveID, true);
	}

	/**
	 * Returns the {@link TablePerspective} for the {@link RecordPerspective}
	 * and the {@link DimensionPerspective} specified. </p>
	 * <p>
	 * If such a container exists already, the existing container is returned.
	 * If not, a new container is created.
	 * </p>
	 * 
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 * @param flag
	 *            that determines whether a datadomain update event will be sent
	 *            or not
	 * @return
	 */
	public TablePerspective getTablePerspective(String recordPerspectiveID,
			String dimensionPerspectiveID, boolean notifyDataDomain) {

		TablePerspective container = tablePerspectives.get(TablePerspective.createKey(
				recordPerspectiveID, dimensionPerspectiveID));
		if (container == null) {
			RecordPerspective recordPerspective = table
					.getRecordPerspective(recordPerspectiveID);
			if (recordPerspective == null)
				throw new IllegalArgumentException(
						"No record perspective registered with this datadomain for "
								+ recordPerspectiveID);

			DimensionPerspective dimensionPerspective = table
					.getDimensionPerspective(dimensionPerspectiveID);
			if (dimensionPerspective == null)
				throw new IllegalArgumentException(
						"No dimension perspective registered with this datadomain for "
								+ dimensionPerspectiveID);

			container = new TablePerspective(this, recordPerspective,
					dimensionPerspective);

			tablePerspectives.put(TablePerspective.createKey(recordPerspectiveID,
					dimensionPerspectiveID), container);

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
		return getTablePerspective(
				table.getDefaultRecordPerspective().getPerspectiveID(), table
						.getDefaultDimensionPerspective().getPerspectiveID());
	}

	/**
	 * Returns whether a {@link TablePerspective} Object exists in this
	 * datadomain for the given perspectiveIDs.
	 * 
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 * @return
	 */
	public boolean hasTablePerspective(String recordPerspectiveID,
			String dimensionPerspectiveID) {
		return tablePerspectives.containsKey(TablePerspective.createKey(
				recordPerspectiveID, dimensionPerspectiveID));
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
	 * @return the dimensionIDMappingManager, see
	 *         {@link #dimensionIDMappingManager}
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
	 * Returns a clone of the record selection manager. This is the preferred
	 * way to initialize SelectionManagers.
	 * 
	 * @return a clone of the record selection manager
	 */
	public RecordSelectionManager getRecordSelectionManager() {
		return (RecordSelectionManager) recordSelectionManager.clone();
	}

	/**
	 * Returns a clone of the dimension selection manager. This is the preferred
	 * way to initialize SelectionManagers.
	 * 
	 * @return a clone of the dimension selection manager
	 */
	public DimensionSelectionManager getDimensionSelectionManager() {
		return (DimensionSelectionManager) dimensionSelectionManager.clone();
	}

	/**
	 * Returns a clone of the record group selection manager. This is the
	 * preferred way to initialize SelectionManagers. *
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
	public RecordVirtualArray getRecordVA(String recordPerspectiveID) {
		RecordVirtualArray va = table.getRecordPerspective(recordPerspectiveID)
				.getVirtualArray();
		return va;
	}

	/**
	 * Returns the virtual array for the type
	 * 
	 * @param dimensionPerspectiveID
	 *            the type of VA requested
	 * @return
	 */
	public DimensionVirtualArray getDimensionVA(String dimensionPerspectiveID) {
		DimensionVirtualArray va = table.getDimensionPerspective(dimensionPerspectiveID)
				.getVirtualArray();
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
		if (colorMapper == null)
			if (table.getMetaData().getDataCenter() == null) {
				colorMapper = ColorMapper.createDefaultTwoColorMapper();
			} else {
				colorMapper = ColorMapper.createDefaultThreeColorMapper();
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
	 * Initiates clustering based on the parameters passed. Sends out an event
	 * to all affected views upon positive completion to replace their VA.
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
			DimensionPerspective targetDimensionPerspective;
			boolean registerLater = false;
			if (clusterState.isModifyExistingPerspective()) {
				targetDimensionPerspective = clusterState.getSourceDimensionPerspective();
			} else {
				targetDimensionPerspective = clusterState
						.getOptionalTargetDimensionPerspective();
				if (targetDimensionPerspective == null) {
					registerLater = true;
					targetDimensionPerspective = new DimensionPerspective(this);

				}
			}
			targetDimensionPerspective.init(dimensionResult);
			targetDimensionPerspective.setLabel(clusterState
					.getClusterAlgorithmConfiguration().getClusterAlgorithmName()
					+ " "
					+ targetDimensionPerspective.getVirtualArray().getGroupList().size(),
					false);
			if (registerLater) {
				table.registerDimensionPerspective(targetDimensionPerspective);
			}
			eventPublisher.triggerEvent(new DimensionVAUpdateEvent(dataDomainID,
					targetDimensionPerspective.getPerspectiveID(), this));
		}

		if (clusterState.getClusterTarget() == EClustererTarget.RECORD_CLUSTERING) {
			PerspectiveInitializationData recordResult = result.getRecordResult();
			RecordPerspective targetRecordPerspective;
			boolean registerLater = false;
			if (clusterState.isModifyExistingPerspective()) {
				targetRecordPerspective = clusterState.getSourceRecordPerspective();
			} else {
				targetRecordPerspective = clusterState
						.getOptionalTargetRecordPerspective();
				if (targetRecordPerspective == null) {
					registerLater = true;
					targetRecordPerspective = new RecordPerspective(this);

				}
			}
			targetRecordPerspective.init(recordResult);
			targetRecordPerspective.setLabel(clusterState
					.getClusterAlgorithmConfiguration().getClusterAlgorithmName()
					+ " "
					+ targetRecordPerspective.getVirtualArray().getGroupList().size(),
					false);
			if (registerLater) {
				table.registerRecordPerspective(targetRecordPerspective);
			}
			eventPublisher.triggerEvent(new RecordVAUpdateEvent(dataDomainID,
					targetRecordPerspective.getPerspectiveID(), this));
		}

		eventPublisher.triggerEvent(new DataDomainUpdateEvent(this));
		return result;
	}

	/**
	 * Resets the context VA to it's initial state
	 */
	@Deprecated
	public void resetRecordVA(String recordPerspectiveID) {
		table.getRecordPerspective(recordPerspectiveID).reset();
	}

	@Override
	public void handleRecordVADelta(RecordVADelta vaDelta, String info) {
		IDCategory targetCategory = vaDelta.getIDType().getIDCategory();
		if (targetCategory != recordIDCategory)
			return;

		if (targetCategory == recordIDCategory && vaDelta.getIDType() != recordIDType)
			vaDelta = DeltaConverter.convertDelta(recordIDMappingManager, recordIDType,
					vaDelta);
		RecordPerspective recordData = table.getRecordPerspective(vaDelta.getVAType());
		recordData.setVADelta(vaDelta);

		RecordVAUpdateEvent event = new RecordVAUpdateEvent(dataDomainID,
				recordData.getPerspectiveID(), this);

		eventPublisher.triggerEvent(event);

	}

	@Override
	public void handleDimensionVADelta(DimensionVADelta vaDelta, String info) {
		// FIXME why is here nothing?
		System.out.println("What?");

	}

	@Override
	public void replaceRecordPerspective(String dataDomainID, String recordPerspectiveID,
			PerspectiveInitializationData data) {

		if (dataDomainID != this.dataDomainID) {
			handleForeignRecordVAUpdate(dataDomainID, recordPerspectiveID, data);
			return;
		}

		table.getRecordPerspective(recordPerspectiveID).init(data);

		RecordVAUpdateEvent event = new RecordVAUpdateEvent();
		event.setSender(this);
		event.setDataDomainID(dataDomainID);
		event.setPerspectiveID(recordPerspectiveID);
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void replaceDimensionPerspective(String dataDomainID,
			String dimensionPerspectiveID, PerspectiveInitializationData data) {

		table.getDimensionPerspective(dimensionPerspectiveID).init(data);

		DimensionVAUpdateEvent event = new DimensionVAUpdateEvent();
		event.setDataDomainID(dataDomainID);
		event.setSender(this);
		event.setPerspectiveID(dimensionPerspectiveID);
		eventPublisher.triggerEvent(event);
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {

		if (recordSelectionManager == null)
			return;

		if (recordIDMappingManager.hasMapping(selectionDelta.getIDType(),
				recordSelectionManager.getIDType())) {
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
	public void handleSelectionCommand(IDCategory idCategory,
			SelectionCommand selectionCommand) {
		// TODO Auto-generated method stub
	}

	/**
	 * This method is called by the {@link ForeignSelectionUpdateListener},
	 * signaling that a selection form another dataDomain is available. If
	 * possible, it is converted to be compatible with the local dataDomain and
	 * then sent out via a {@link SelectionUpdateEvent}.
	 * 
	 * @param dataDomainType
	 *            the type of the dataDomain for which this selectionUpdate is
	 *            intended
	 * @param delta
	 * @param scrollToSelection
	 * @param info
	 */
	public void handleForeignSelectionUpdate(String dataDomainType, SelectionDelta delta) {
		// may be interesting to implement in sub-class
	}

	/**
	 * Interface used by {@link ForeignSelectionCommandListener} to signal
	 * foreign selection commands. Can be implemented in concrete classes, has
	 * no functionality in base class.
	 */
	public void handleForeignSelectionCommand(String dataDomainType,
			IDCategory idCategory, SelectionCommand selectionCommand) {
		// may be interesting to implement in sub-class
	}

	/**
	 * This method is called if a record VA Update was requested, but the
	 * dataDomainType specified was not this dataDomains type. Concrete handling
	 * can only be done in concrete dataDomains.
	 * 
	 * @param tableID
	 * @param dataDomainType
	 * @param vaType
	 * @param data
	 */
	public void handleForeignRecordVAUpdate(String dataDomainType, String vaType,
			PerspectiveInitializationData data) {
		// may be interesting to implement in sub-class
	}

	/**
	 * Returns the denomination for the records. For genetic data for example
	 * this would be "Gene"
	 * 
	 * @param capitalized
	 *            if true, the label is returned capitalized, e.g., "Gene", if
	 *            false it would be "gene"
	 * @param plural
	 *            if true, the label is returned in the plural form of the word,
	 *            e.g., "genes" instead of the singular form, e.g., "gene"
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
	 * Get the human readable record label for the id, which is of the
	 * {@link #recordIDType}.
	 * 
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getRecordLabel(Object id) {
		return getRecordLabel(recordIDType, id);
	}

	/**
	 * Get the human readable dimension label for the id, which is of the
	 * {@link #dimensionIDType}.
	 * 
	 * @param id
	 *            the id to convert to a human readable label
	 * @return the readable label
	 */
	public String getDimensionLabel(Object id) {
		return getDimensionLabel(dimensionIDType, id);
	}

	/**
	 * Get the human readable record label for the id, which is of the type
	 * specified.
	 * 
	 * @param idType
	 *            the IDType of the id passed
	 * @param id
	 * @return the readable label
	 */
	public String getRecordLabel(IDType idType, Object id) {
		Set<String> ids = recordIDMappingManager.getIDAsSet(idType, idType
				.getIDCategory().getHumanReadableIDType(), id);
		String label = "No Mapping";
		if (ids != null && ids.size() > 0) {
			label = ids.iterator().next();
		}
		return label;
	}

	/** Same as {@link #getRecordLabel(IDType, Object)} for dimensions */
	public String getDimensionLabel(IDType idType, Object id) {
		Set<String> ids = dimensionIDMappingManager.getIDAsSet(idType, idType
				.getIDCategory().getHumanReadableIDType(), id);
		String label = "No Mapping";
		if (ids != null && ids.size() > 0) {
			label = ids.iterator().next();
		}
		return label;
	}

	public void aggregateGroups(java.util.Set<Integer> groups) {
		System.out
				.println("Received command to aggregate experiments, not implemented yet");
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
			return table.getMetaData().size() * table.getMetaData().depth();
		} catch (NoSuchElementException e) {
			return 0;
		}
	}

	@Override
	public void registerEventListeners() {

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		selectionUpdateListener.setExclusiveDataDomainID(dataDomainID);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		startClusteringListener = new StartClusteringListener();
		startClusteringListener.setHandler(this);
		startClusteringListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(StartClusteringEvent.class, startClusteringListener);

		recordVADeltaListener = new RecordVADeltaListener();
		recordVADeltaListener.setHandler(this);
		recordVADeltaListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(RecordVADeltaEvent.class, recordVADeltaListener);

		replaceRecordPerspectiveListener = new ReplaceRecordPerspectiveListener();
		replaceRecordPerspectiveListener.setHandler(this);
		replaceRecordPerspectiveListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(ReplaceRecordPerspectiveEvent.class,
				replaceRecordPerspectiveListener);

		dimensionVADeltaListener = new DimensionVADeltaListener();
		dimensionVADeltaListener.setHandler(this);
		dimensionVADeltaListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(DimensionVADeltaEvent.class, dimensionVADeltaListener);

		replaceDimensionPerspectiveListener = new ReplaceDimensionPerspectiveListener();
		replaceDimensionPerspectiveListener.setHandler(this);
		replaceDimensionPerspectiveListener.setDataDomainID(dataDomainID);
		eventPublisher.addListener(ReplaceDimensionPerspectiveEvent.class,
				replaceDimensionPerspectiveListener);

		aggregateGroupListener = new AggregateGroupListener();
		aggregateGroupListener.setHandler(this);
		eventPublisher.addListener(AggregateGroupEvent.class, aggregateGroupListener);
	}

	// TODO this is never called!
	@Override
	public void unregisterEventListeners() {

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}

		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}

		if (startClusteringListener != null) {
			eventPublisher.removeListener(startClusteringListener);
			startClusteringListener = null;
		}

		if (replaceRecordPerspectiveListener != null) {
			eventPublisher.removeListener(replaceRecordPerspectiveListener);
			replaceRecordPerspectiveListener = null;
		}

		if (replaceDimensionPerspectiveListener != null) {
			eventPublisher.removeListener(replaceDimensionPerspectiveListener);
			replaceDimensionPerspectiveListener = null;
		}

		if (recordVADeltaListener != null) {
			eventPublisher.removeListener(recordVADeltaListener);
			recordVADeltaListener = null;
		}

		if (dimensionVADeltaListener != null) {
			eventPublisher.removeListener(dimensionVADeltaListener);
			dimensionVADeltaListener = null;
		}

		if (aggregateGroupListener != null) {
			eventPublisher.removeListener(aggregateGroupListener);
			aggregateGroupListener = null;
		}
	}

	/**
	 * <p>
	 * Converts a {@link RecordPerspective} with an IDType that is not the
	 * {@link #recordIDType} to a new RecordPerspective with the recordIDType.
	 * </p>
	 * <p>
	 * Grouping, and naming is preserved, sample elements and trees are not.
	 * </p>
	 */
	public RecordPerspective convertForeignRecordPerspective(
			RecordPerspective foreignPerspective) {

		if (foreignPerspective.getIdType().getIDCategory() != recordIDCategory) {
			throw new IllegalArgumentException("Can not convert from "
					+ foreignPerspective.getIdType() + " to " + recordIDType);
		}
		if (foreignPerspective.getIdType() == recordIDType)
			return foreignPerspective;

		RecordVirtualArray foreignRecordVA = foreignPerspective.getVirtualArray();

		RecordGroupList recordGroupList = foreignRecordVA.getGroupList();

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
		for (Integer foreignVAID : foreignRecordVA) {
			Set<Integer> localVAIDS = recordIDMappingManager.getIDAsSet(
					foreignRecordVA.getIdType(), recordIDType, foreignVAID);
			if (localVAIDS == null)
				continue;
			for (Integer localVAID : localVAIDS) {
				if (localVAID == null)
					continue;
				indices.add(localVAID);
				int groupIndex = recordGroupList.getGroupOfVAIndex(
						foreignRecordVA.indexOf(foreignVAID)).getGroupIndex();
				groupSizes.set(groupIndex, groupSizes.get(groupIndex) + 1);
				sampleElements.set(groupIndex, count);
				count++;
			}

		}

		data.setData(indices, groupSizes, sampleElements, groupNames);

		RecordPerspective localRecordPerspective = new RecordPerspective(this);
		localRecordPerspective.setIDType(recordIDType);
		localRecordPerspective.init(data);
		localRecordPerspective.setLabel(foreignPerspective.getLabel(),
				foreignPerspective.isLabelDefault());
		return localRecordPerspective;

	}
}
