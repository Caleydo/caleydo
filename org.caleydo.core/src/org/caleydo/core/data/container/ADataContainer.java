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
public abstract class ADataContainer
	implements IDataContainer {

	protected ATableBasedDataDomain dataDomain;

	protected String dimensionPerspectiveID;
	protected String recordPerspectiveID;

	protected RecordPerspective recordPerspective;
	protected DimensionPerspective dimensionPerspective;

	/**
	 * Empty constructor, nothing initialized
	 */
	public ADataContainer() {

	}

	/**
	 * Constructor using the actual objects, sets the ids as well based on the ids of the objects.
	 * 
	 * @param dataDomain
	 * @param recordPerspective
	 * @param dimensionPerspective
	 */
	public ADataContainer(ATableBasedDataDomain dataDomain, RecordPerspective recordPerspective,
		DimensionPerspective dimensionPerspective) {
		this.dataDomain = dataDomain;
		this.recordPerspective = recordPerspective;
		recordPerspectiveID = recordPerspective.getPerspectiveID();
		this.dimensionPerspective = dimensionPerspective;
		dimensionPerspectiveID = dimensionPerspective.getPerspectiveID();
	}

	/**
	 * Constructor using the IDs for the perspectives. Also sets the the perspective objects.
	 * 
	 * @param dataDomain
	 * @param recordPerspectiveID
	 * @param dimensionPerspectiveID
	 */
	public ADataContainer(ATableBasedDataDomain dataDomain, String recordPerspectiveID,
		String dimensionPerspectiveID) {
		this.dataDomain = dataDomain;
		this.recordPerspectiveID = recordPerspectiveID;
		this.recordPerspective = dataDomain.getTable().getRecordPerspective(recordPerspectiveID);
		this.dimensionPerspectiveID = dimensionPerspectiveID;
		this.dimensionPerspective = dataDomain.getTable().getDimensionPerspective(dimensionPerspectiveID);
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

}
