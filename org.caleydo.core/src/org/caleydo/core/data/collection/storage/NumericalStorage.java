package org.caleydo.core.data.collection.storage;

import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ccontainer.INumericalCContainer;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;


/**
 * Storage for Numerical Values, Implementation of INumericalStorage
 * 
 * @author Alexander Lex
 * 
 */
public class NumericalStorage
	extends AStorage
	implements INumericalStorage
{

	/**
	 * Constructor
	 * 
	 */
	public NumericalStorage()
	{
		super(GeneralManager.get().getIDManager()
				.createID(EManagedObjectType.STORAGE_NUMERICAL));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.collection.INumericalSet#normalizeWithExternalExtrema
	 * (double, double)
	 */
	@Override
	public void normalizeWithExternalExtrema(double dMin, double dMax)
	{

		INumericalCContainer rawStorage = (INumericalCContainer) hashCContainers
				.get(EDataRepresentation.RAW);
		// TODO check if dMin < fMin
		INumericalCContainer normalizedStorage = (INumericalCContainer) rawStorage
				.normalizeWithExternalExtrema(dMin, dMax);

		hashCContainers.put(EDataRepresentation.NORMALIZED, normalizedStorage);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.storage.AStorage#getRawDataType()
	 */
	@Override
	public ERawDataType getRawDataType()
	{
		return rawDataType;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalStorage#getMin()
	 */
	@Override
	public double getMin()
	{
		EDataRepresentation dataKind = EDataRepresentation.RAW;
		if (hashCContainers.containsKey(EDataRepresentation.LOG10))
			dataKind = EDataRepresentation.LOG10;
		return ((INumericalCContainer) (hashCContainers.get(dataKind))).getMin();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalStorage#getMax()
	 */
	@Override
	public double getMax()
	{
		EDataRepresentation dataKind = EDataRepresentation.RAW;
		if (hashCContainers.containsKey(EDataRepresentation.LOG10))
			dataKind = EDataRepresentation.LOG10;
		return ((INumericalCContainer) (hashCContainers.get(dataKind))).getMax();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalStorage#getRawForNormalized(double)
	 */
	@Override
	public double getRawForNormalized(double dNormalized)
	{
		return dNormalized * (getMax() - getMin());
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalStorage#log10()
	 */
	@Override
	public void log10()
	{
		hashCContainers.put(EDataRepresentation.LOG10,
				((INumericalCContainer) (hashCContainers.get(EDataRepresentation.RAW)))
						.log10());
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.collection.INumericalStorage#reset()
	 */
	@Override
	public void reset()
	{
		hashCContainers.remove(EDataRepresentation.LOG10);
		hashCContainers.remove(EDataRepresentation.NORMALIZED);
	}
}
