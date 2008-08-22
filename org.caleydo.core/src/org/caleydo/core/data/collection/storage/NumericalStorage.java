package org.caleydo.core.data.collection.storage;

import javax.management.InvalidAttributeValueException;
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
		super(GeneralManager.get().getIDManager().createID(
				EManagedObjectType.STORAGE_NUMERICAL));
	}

	@Override
	public void normalizeWithExternalExtrema(double dMin, double dMax)
			throws InvalidAttributeValueException
	{

		INumericalCContainer rawStorage = (INumericalCContainer) hashCContainers
				.get(EDataRepresentation.RAW);
		INumericalCContainer normalizedStorage = (INumericalCContainer) rawStorage
				.normalizeWithExternalExtrema(dMin, dMax);

		hashCContainers.put(EDataRepresentation.NORMALIZED, normalizedStorage);
	}

	@Override
	public ERawDataType getRawDataType()
	{
		return rawDataType;
	}

	@Override
	public double getMin()
	{
		EDataRepresentation dataKind = EDataRepresentation.RAW;
		if (hashCContainers.containsKey(EDataRepresentation.LOG10))
			dataKind = EDataRepresentation.LOG10;
		return ((INumericalCContainer) (hashCContainers.get(dataKind))).getMin();
	}

	@Override
	public double getMax()
	{
		EDataRepresentation dataKind = EDataRepresentation.RAW;
		if (hashCContainers.containsKey(EDataRepresentation.LOG10))
			dataKind = EDataRepresentation.LOG10;
		return ((INumericalCContainer) (hashCContainers.get(dataKind))).getMax();
	}

	@Override
	public double getRawForNormalized(double dNormalized)
	{
		return dNormalized * (getMax() - getMin());
	}

	@Override
	public void log10()
	{
		hashCContainers.put(EDataRepresentation.LOG10,
				((INumericalCContainer) (hashCContainers.get(EDataRepresentation.RAW)))
						.log10());
	}

	@Override
	public void reset()
	{
		hashCContainers.remove(EDataRepresentation.LOG10);
		hashCContainers.remove(EDataRepresentation.NORMALIZED);
	}
}
