/**
 * 
 */
package org.caleydo.core.data.container;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;

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
public class DataContainer
	implements IDataContainer {

	private static int idCounter;
	private int id;

	protected ATableBasedDataDomain dataDomain;

	protected RecordPerspective recordPerspective;
	protected DimensionPerspective dimensionPerspective;

	/**
	 * Object holding respectively calculating all forms of (statistical) meta-data for this container
	 */
	protected ContainerStatistics containerStatistics;

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

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public RecordPerspective getRecordPerspective() {
		return recordPerspective;
	}

	@Override
	public void setRecordPerspective(RecordPerspective recordPerspective) {
		this.recordPerspective = recordPerspective;
	}

	@Override
	public DimensionPerspective getDimensionPerspective() {
		return dimensionPerspective;
	}

	@Override
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

	/**
	 * @return the id, see {@link #id}
	 */
	public int getID() {
		return id;
	}

	/** Returns the size of the virtual array in the record perspective, i.e. the number of records */
	public int getNrRecords() {
		return recordPerspective.getVirtualArray().size();
	}

	/** Same as {@link #getNrRecords()} for dimensions */
	public int getNrDimensions() {
		return dimensionPerspective.getVirtualArray().size();
	}

}
