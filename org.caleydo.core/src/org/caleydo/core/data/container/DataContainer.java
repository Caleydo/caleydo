/**
 * 
 */
package org.caleydo.core.data.container;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.EColumnType;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;

/**
 * <p>
 * Base class for all classes that hold a {@link ATableBasedDataDomain}, a {@link DimensionPerspective} and a
 * {@link RecordPerspective}. Provides setters and getters to all these.
 * </p>
 * <p>
 * When your sub-class already has a superclass use {@link IDataContainer} instead, otherwise this class is
 * prefered.
 * </p>
 * 
 * @author Alexander Lex
 */
public class DataContainer {

	private static int idCounter;
	private int id;

	protected ATableBasedDataDomain dataDomain;

	protected RecordPerspective recordPerspective;
	protected DimensionPerspective dimensionPerspective;

	protected String label;
	
	public static IDCategory DATA_CONTAINER = IDCategory.registerCategory("DATA_CONTAINER");
	public static IDType DATA_CONTAINER_IDTYPE = IDType.registerType("DataConatiners", DATA_CONTAINER, EColumnType.INT);

	/**
	 * A Group describes a part of a virtual array, e.g. a cluster. The group for a data container is only set
	 * when the DataContainer is a sub-container of another recordVirtualArray.group which
	 */
	protected Group recordGroup;
	/**
	 * Object holding respectively calculating all forms of (statistical) meta-data for this container
	 */
	protected ContainerStatistics containerStatistics;

	// protected List<DataContainer> recordSubDataContainers;

	/**
	 * Empty constructor, nothing initialized
	 */
	public DataContainer() {
	}

	/**
	 * Constructor using the actual objects, sets the ids as well based on the ids of the objects.
	 * 
	 * @param dataDomain
	 * @param recordPerspective
	 * @param dimensionPerspective
	 */
	public DataContainer(ATableBasedDataDomain dataDomain, RecordPerspective recordPerspective,
		DimensionPerspective dimensionPerspective) {
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

	public RecordPerspective getRecordPerspective() {
		return recordPerspective;
	}

	public void setRecordPerspective(RecordPerspective recordPerspective) {
		this.recordPerspective = recordPerspective;
	}

	public DimensionPerspective getDimensionPerspective() {
		return dimensionPerspective;
	}

	public void setDimensionPerspective(DimensionPerspective dimensionPerspective) {
		this.dimensionPerspective = dimensionPerspective;
	}

	/**
	 * Checks whether the specified container id matches to the record perspective in this
	 * {@link DataContainer}
	 * 
	 * @param recordPerspectiveID
	 * @return true if the specified id equals the id of the perspective in this container
	 */
	public boolean hasRecordPerspective(String recordPerspectiveID) {
		return recordPerspective.getID().equals(recordPerspectiveID);
	}

	/**
	 * Same as {@link #hasRecordPerspective(String)} for dimensions
	 * 
	 * @param dimensionPerspectiveID
	 * @return true if the specified id equals the id of the perspective in this container
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

	/** Returns the size of the virtual array in the record perspective, i.e. the number of records */
	public int getNrRecords() {
		return recordPerspective.getVirtualArray().size();
	}

	/** Same as {@link #getNrRecords()} for dimensions */
	public int getNrDimensions() {
		return dimensionPerspective.getVirtualArray().size();
	}

	public String getLabel() {
		if (label == null)
			return dimensionPerspective.getLabel() + "/" + recordPerspective.getLabel();
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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
	 * Creates and returns one {@link DataContainer} for each group in the {@link RecordPerspective}, where
	 * the new {@link RecordPerspective} contains the elements of the group. The {@link DimensionPerspective}
	 * is the same as for this container.
	 * 
	 * @return a new list of new {@link DataContainer}s
	 */
	public List<DataContainer> createRecordSubDataContainers() {
		// if (recordSubDataContainers != null)
		// return recordSubDataContainers;

		List<DataContainer> recordSubDataContainers = new ArrayList<DataContainer>();

		// TODO implement
		RecordVirtualArray recordVA = recordPerspective.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return null;

		RecordGroupList groupList = recordVA.getGroupList();
		groupList.updateGroupInfo();

		for (Group group : groupList) {

			List<Integer> indices =
				recordVA.getVirtualArray().subList(group.getStartIndex(), group.getEndIndex() + 1);

			RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(indices);
			recordPerspective.init(data);

			DataContainer subDataContainer =
				new DataContainer(dataDomain, recordPerspective, dimensionPerspective);
			subDataContainer.setRecordGroup(group);
			recordSubDataContainers.add(subDataContainer);

		}

		return recordSubDataContainers;
	}

}
