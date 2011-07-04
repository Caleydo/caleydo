package org.caleydo.core.data.collection.storage;

import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ccontainer.FloatCContainer;
import org.caleydo.core.data.collection.ccontainer.FloatCContainerIterator;
import org.caleydo.core.data.collection.ccontainer.INumericalCContainer;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;

/**
 * Storage for Numerical Values, Implementation of INumericalStorage
 * 
 * @author Alexander Lex
 */
public class NumericalStorage
	extends AStorage
	implements INumericalStorage {

	/**
	 * Constructor
	 */
	public NumericalStorage() {
		super(GeneralManager.get().getIDCreator().createID(EManagedObjectType.STORAGE_NUMERICAL));
	}

	@Override
	public void normalize() {

		INumericalCContainer iRawContainer = (INumericalCContainer) hashCContainers.get(dataRep);

		hashCContainers.put(EDataRepresentation.NORMALIZED, iRawContainer.normalize());

		if (isCertaintyDataSet) {
			normalizeCertainty();
		}
	}

	private void normalizeCertainty() {
		FloatCContainer certainties = (FloatCContainer) hashCContainers.get(EDataRepresentation.CERTAINTY);
		// TODO here is the manual cut-off for certainties
		FloatCContainer normalizedCertainties = certainties.normalizeWithExternalExtrema(1, 2);
		hashCContainers.put(EDataRepresentation.CERTAINTY_NORMALIZED, normalizedCertainties);
	}

	@Override
	public void normalizeWithExternalExtrema(double dMin, double dMax) {
		INumericalCContainer rawStorage = (INumericalCContainer) hashCContainers.get(dataRep);

		INumericalCContainer numericalContainer = rawStorage.normalizeWithExternalExtrema(dMin, dMax);

		hashCContainers.put(EDataRepresentation.NORMALIZED, numericalContainer);
		
		if (isCertaintyDataSet) {
			normalizeCertainty();
		}
	}

	@Override
	public ERawDataType getRawDataType() {
		return rawDataType;
	}

	@Override
	public double getMin() {
		if (!hashCContainers.containsKey(dataRep))
			throw new IllegalStateException("The requested data representation was not produced.");
		return ((INumericalCContainer) hashCContainers.get(dataRep)).getMin();
	}

	@Override
	public double getMax() {
		return ((INumericalCContainer) hashCContainers.get(dataRep)).getMax();
	}

	@Override
	public double getRawForNormalized(double dNormalized) {
		return dNormalized * (getMax() - getMin());
	}

	@Override
	public void log10() {
		hashCContainers.put(EDataRepresentation.LOG10,
			((INumericalCContainer) hashCContainers.get(EDataRepresentation.RAW)).log(10));
	}

	@Override
	public void log2() {
		hashCContainers.put(EDataRepresentation.LOG2,
			((INumericalCContainer) hashCContainers.get(EDataRepresentation.RAW)).log(2));
	}

	@Override
	public void reset() {
		hashCContainers.remove(EDataRepresentation.LOG2);
		hashCContainers.remove(EDataRepresentation.LOG10);
		hashCContainers.remove(EDataRepresentation.NORMALIZED);
	}

	@Override
	public void setExternalDataRepresentation(EExternalDataRepresentation externalDataRep) {
		switch (externalDataRep) {
			case NORMAL:
				dataRep = EDataRepresentation.RAW;
				break;
			case LOG10:
				dataRep = EDataRepresentation.LOG10;
				break;
			case LOG2:
				dataRep = EDataRepresentation.LOG2;
				break;
		}

	}

	@Override
	public Histogram getHistogram() {

		int iNumberOfBuckets = (int) Math.sqrt(size());
		Histogram histogram = new Histogram(iNumberOfBuckets);
		for (int iCount = 0; iCount < iNumberOfBuckets; iCount++) {
			histogram.add(0);
		}

		FloatCContainerIterator iterator =
			((FloatCContainer) hashCContainers.get(EDataRepresentation.NORMALIZED)).iterator();
		while (iterator.hasNext()) {
			// this works because the values in the container are already noramlized
			int iIndex = (int) (iterator.next() * iNumberOfBuckets);
			if (iIndex == iNumberOfBuckets)
				iIndex--;
			Integer iNumOccurences = histogram.get(iIndex);
			histogram.set(iIndex, ++iNumOccurences);
		}

		return histogram;
	}

	@Override
	public Histogram getHistogram(ContentVirtualArray contentVA) {

		int iNumberOfBuckets = (int) Math.sqrt(contentVA.size());
		Histogram histogram = new Histogram(iNumberOfBuckets);
		for (int iCount = 0; iCount < iNumberOfBuckets; iCount++) {
			histogram.add(0);
		}

		FloatCContainerIterator iterator =
			((FloatCContainer) hashCContainers.get(EDataRepresentation.NORMALIZED)).iterator(contentVA);
		while (iterator.hasNext()) {
			// this works because the values in the container are already noramlized
			int iIndex = (int) (iterator.next() * iNumberOfBuckets);
			if (iIndex == iNumberOfBuckets)
				iIndex--;
			Integer iNumOccurences = histogram.get(iIndex);
			histogram.set(iIndex, ++iNumOccurences);
		}

		return histogram;
	}

}
