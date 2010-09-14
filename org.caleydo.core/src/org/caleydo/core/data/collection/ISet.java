package org.caleydo.core.data.collection;

import java.util.Iterator;
import java.util.Set;

import javax.naming.OperationNotSupportedException;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.set.ContentData;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.data.collection.set.StorageData;
import org.caleydo.core.data.collection.set.statistics.StatisticsResult;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.util.clusterer.ClusterState;

/**
 * <h2>General Information</h2>
 * <p>
 * A set is the main container for tabular data in Caleydo. A set is made up of {@link IStorage}s, where each
 * storage corresponds to a column in a tabular data set. Columns are therefore always refered to as
 * <b>Storages</b> and rows as <b>Content</b> The data should be accessed through {@link VirtualArray}s, which
 * are stored in {@link StorageData}s for Storages and {@link ContentData}s for Content.
 * </p>
 * <h2>Set Creation</h2>
 * <p>
 * A set relies heavily upon {@link SetUtils} for being created. Many creation related functions are provided
 * there, sometimes interfacing with package private methods in this class.
 * </p>
 * 
 * @author Alexander Lex
 */
public interface ISet
	extends IUniqueObject, ICollection {

	public static final String STORAGE = "STORAGE";
	public static final String CONTENT = "CONTENT";
	public static final String CONTENT_CONTEXT = "CONTENT_CONTEXT";

	/**
	 * Set the data domain that is responsible for the set
	 * 
	 * @param dataDomain
	 */
	public void setDataDomain(ASetBasedDataDomain dataDomain);

	/**
	 * Get the data domain that is responsible for the set
	 * 
	 * @param dataDomain
	 */
	public ASetBasedDataDomain getDataDomain();

	/**
	 * Get the storage associated with the ID provided. Returns null if no such storage is registered.
	 * 
	 * @param storageID
	 *            a unique storage ID
	 * @return
	 */
	public IStorage get(Integer storageID);

	/**
	 * Get the number of storages in a set
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Get the depth of the set, which is the length of the storages (i.e. the number of content elements)
	 * 
	 * @return the number of elements in the storages contained in the list
	 */
	public int depth();

	/**
	 * Get the minimum value in the set.
	 * 
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMin();

	/**
	 * Get the maximum value in the set.
	 * 
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return the absolute minimum value in the set
	 */
	public double getMax();

	/**
	 * Gets the minimum value in the set in the specified data representation.
	 * 
	 * @param dataRepresentation
	 *            Data representation the minimum value shall be returned in.
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return The absolute minimum value in the set in the specified data representation.
	 */
	public double getMinAs(EExternalDataRepresentation dataRepresentation);

	/**
	 * Gets the maximum value in the set in the specified data representation.
	 * 
	 * @param dataRepresentation
	 *            Data representation the maximum value shall be returned in.
	 * @throws OperationNotSupportedException
	 *             when executed on nominal data
	 * @return The absolute maximum value in the set in the specified data representation.
	 */
	public double getMaxAs(EExternalDataRepresentation dataRepresentation);

	/**
	 * Calculates a raw value based on min and max from a normalized value.
	 * 
	 * @param dNormalized
	 *            a value between 0 and 1
	 * @return a value between min and max
	 */
	public double getRawForNormalized(double dNormalized);

	/**
	 * Calculates a normalized value based on min and max.
	 * 
	 * @param dRaw
	 *            the raw value
	 * @return a value between 0 and 1
	 */
	public double getNormalizedForRaw(double dRaw);

	/**
	 * Returns the current external data rep.
	 * 
	 * @return
	 */
	public EExternalDataRepresentation getExternalDataRep();

	/**
	 * Returns true if the set contains homgeneous data (data of the same kind, with one global minimum and
	 * maximum), else false
	 * 
	 * @return
	 */
	public boolean isSetHomogeneous();

	/**
	 * Returns a {@link StorageData} object for the specified StorageVAType. The StorageData provides access
	 * to all data on a storage, e.g., virtualArryay, cluster tree, group list etc.
	 * 
	 * @param vaType
	 * @return
	 */
	public StorageData getStorageData(String vaType);

	/**
	 * Returns a {@link ContentData} object for the specified ContentVAType. The ContentData provides access
	 * to all data on a storage, e.g., virtualArryay, cluster tree, group list etc.
	 * 
	 * @param vaType
	 * @return
	 */
	public ContentData getContentData(String vaType);

	/**
	 * Iterate over the storages based on a virtual array
	 * 
	 * @param type
	 * @return
	 */
	public Iterator<IStorage> iterator(String type);

	/**
	 * Set a contentVA. The contentVA in the contentData object is replaced and the other elements in the
	 * contentData are reset.
	 * 
	 * @param vaType
	 * @param virtualArray
	 */
	public void setContentVA(String vaType, ContentVirtualArray virtualArray);

	/**
	 * Sets a storageVA. The storageVA in the storageData object is replaced and the other elements in the
	 * storageData are reset.
	 * 
	 * @param vaType
	 * @param virtualArray
	 */
	public void setStorageVA(String vaType, StorageVirtualArray virtualArray);

	/**
	 * Clusters a Storage
	 * 
	 * @param clusterState
	 * @return ArrayList<IVirtualArray> Virtual arrays holding cluster result
	 */
	public void cluster(ClusterState clusterState);

	/**
	 * Returns a histogram of the values of all storages in the set (not considering VAs). The number of the
	 * bins is sqrt(numberOfElements). This only works for homogeneous sets, if used on other sets an
	 * exception is thrown.
	 * 
	 * @return the Histogram of the values in the set
	 * @throws UnsupportedOperationException
	 *             when used on non-homogeneous sets
	 */
	public Histogram getHistogram() throws UnsupportedOperationException;

	/**
	 * Removes all data related to the set (Storages, Virtual Arrays and Sets) from the managers so that the
	 * garbage collector can handle it.
	 */
	public void destroy();

	/**
	 * Returns the statistics results. E.g. comparative t-test between sets.
	 * 
	 * @return the statistics result object containing all results.
	 */
	public StatisticsResult getStatisticsResult();

	/**
	 * Returns a storage containing the mean values of all the storages in the set. The mean storage contains
	 * raw and normalized values. The mean is calculated based on the raw data, that means for calculating the
	 * means possibly specified cut-off values are not considered, since cut-off values are meant for
	 * visualization only.
	 * 
	 * @return the storage containing means for all content elements
	 */
	public NumericalStorage getMeanStorage();

	/**
	 * Restores the original virtual array using the whole set data.
	 */
	public void restoreOriginalContentVA();

	/**
	 * Return a list of content VA types that have registered {@link ContentData}.
	 * 
	 * @return
	 */
	public Set<String> getRegisteredContentVATypes();

	/**
	 * Return a list of storage VA types that have registered {@link StorageData}
	 * 
	 * @return
	 */
	public Set<String> getRegisteredStorageVATypes();

}
