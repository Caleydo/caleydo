package org.caleydo.core.data.collection;

import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.set.SetIterator;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.clusterer.ClusterState;

/**
 * Interface for Sets
 * 
 * @author Alexander Lex
 */
public interface ISet
	extends IUniqueObject, Iterable<IStorage>, ICollection {

	/**
	 * Set the set type. Possible set types in ESetType.
	 * 
	 * @see ESetType
	 * @param setType
	 *            the type
	 */
	public void setSetType(ESetType setType);

	/**
	 * Get the set type. Possible set types in ESetType.
	 * 
	 * @see ESetType
	 * @return the type
	 */
	public ESetType getSetType();

	/**
	 * Add a storage based on its id. The storage has to be fully initialized with data
	 * 
	 * @param iStorageID
	 */
	public void addStorage(int iStorageID);

	/**
	 * Add a storage by reference. The storage has to be fully initialized with data
	 * 
	 * @param storage
	 *            the storage
	 */
	public void addStorage(IStorage storage);

	/**
	 * Get the storage at the index iIndex
	 * 
	 * @param iIndex
	 * @return
	 */
	public IStorage get(int iIndex);

	/**
	 * Get the storage via the index in the virtual array
	 * 
	 * @param iUniqueID
	 *            the unique id associated with the virtual array
	 * @param iIndex
	 *            the index in the virtual array
	 * @return the storage
	 */
	public IStorage getStorageFromVA(int iUniqueID, int iIndex);

	/**
	 * Get an iterator that iterates over the storages considering the Virtual Array.
	 * 
	 * @param the
	 *            unique ID of the set virtula array
	 * @return the set iterator
	 */
	public SetIterator VAIterator(int iUniqueID);

	/**
	 * Get the number of storages in a set
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Return the size of the virtual array
	 * 
	 * @param iUniqueID
	 *            the unique id associated with the virtual array
	 * @return the number of sets in the virtual array
	 */
	public int sizeVA(int iUniqueID);

	/**
	 * Get the depth of the set, which is the length of the storages
	 * 
	 * @return the number of elements in the storages contained in the list
	 */
	public int depth();

	/**
	 * Normalize all storages in the set, based solely on the values within each storage. Operates with the
	 * raw data as basis by default, however when a logarithmized representation is in the storage this is
	 * used.
	 */
	// public void normalize();
	/**
	 * Normalize all storages in the set, based on values of all storages. For a numerical storage, this would
	 * mean, that global minima and maxima are retrieved instead of local ones (as is done with normalize())
	 * Operates with the raw data as basis by default, however when a logarithmized representation is in the
	 * storage this is used. Make sure that all storages are logarithmized.
	 */
	// public void normalizeGlobally();
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
	 * Set an artificial minimum for the dataset. All elements smaller than that are clipped to this value in
	 * the representation. This only affects the normalization, does not alter the raw data
	 */
	public void setMin(double dMin);

	/**
	 * Set an artificial maximum for the dataset. All elements smaller than that are clipped to this value in
	 * the representation. This only affects the normalization, does not alter the raw data
	 */
	public void setMax(double dMax);

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
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 * 
	 * @param externalDataRep
	 *            Determines how the data is visualized. For options see {@link EExternalDataRepresentation}
	 * @param bIsSetHomogeneous
	 *            Determines whether a set is homogeneous or not. Homogeneous means that the sat has a global
	 *            maximum and minimum, meaning that all storages in the set contain equal data. If false, each
	 *            storage is treated separately, has it's own min and max etc. Sets that contain nominal data
	 *            MUST be inhomogeneous.
	 */
	public void setExternalDataRepresentation(EExternalDataRepresentation externalDataRep,
		boolean bIsSetHomogeneous);

	/**
	 * Returns true if the set contains homgeneous data (data of the same kind, with one global minimum and
	 * maximum), else false
	 * 
	 * @return
	 */
	public boolean isSetHomogeneous();

	/**
	 * Calculates log10 on all storages in the set. Take care that the set contains only numerical storages,
	 * since nominal storages will cause a runtime exception. If you have mixed data you have to call log10 on
	 * all the storages that support it manually.
	 */
	public void log10();

	/**
	 * Calculates log2 on all storages in the set. Take care that the set contains only numerical storages,
	 * since nominal storages will cause a runtime exception. If you have mixed data you have to call log2 on
	 * all the storages that support it manually.
	 */
	public void log2();

	/**
	 * Creates a default virtual array for the storages in the set. This means that each storages can be
	 * referenced.
	 * 
	 * @return the unique id associated with the virtual array
	 */
	public int createStorageVA();

	/**
	 * Creates a virtual array based on the list of indices supplied for the storages in the set
	 * 
	 * @param iAlSelections
	 *            a list of indices
	 * @return the id of the newly created VA
	 */
	public int createStorageVA(List<Integer> iAlSelections);

	/**
	 * Creates a default virtual array for the set
	 * 
	 * @return the unique id associated with the virtual array
	 */
	public int createSetVA();

	/**
	 * Creates a virtual array based on the list of indices supplied for the set
	 * 
	 * @param iAlSelections
	 *            a list of indices
	 * @return the unique id associated with the virtual array
	 */
	public int createSetVA(ArrayList<Integer> iAlSelections);

	/**
	 * Returns the virtual array associated with the unique ID
	 * 
	 * @param iUniqueID
	 *            the unique id
	 * @return the virtual array associated with the unique ID
	 */
	public IVirtualArray getVA(int iUniqueID);

	/**
	 * Export a manipulated subset of the data to the destination specifiedn in sFileName. Determine whether
	 * 
	 * @param sFileName
	 * @param bExportBucketInternal
	 */
	public void export(String sFileName, boolean bExportBucketInternal);

	/**
	 * Clusters a Storage
	 * 
	 * @param iVAIdContent
	 * @param iVAIdStorage
	 * @param eClustererAlgo
	 * @param bClusterGenes
	 * @param iProgressBarOffset
	 *            Offset value used for overall progress bar. Used in case of bi clustering to avoid problems
	 *            with disposed shell
	 * @param iProgressBarMultiplier
	 *            Multiplier used for overall progress bar. Used in case of bi clustering to avoid problems
	 *            with disposed shell
	 * @return Integer Id of virtual arrays holding cluster result
	 */
	public Integer cluster(Integer iVAIdContent, Integer iVAIdStorage, ClusterState clusterState,
		int iProgressBarOffset, int iProgressBarMultiplier);

	/**
	 * Sets clustered Tree for genes
	 * 
	 * @param Tree
	 */
	public void setClusteredTreeGenes(Tree<ClusterNode> clusteredTree);

	/**
	 * Returns clustered Tree for genes
	 * 
	 * @return Tree
	 */
	public Tree<ClusterNode> getClusteredTreeGenes();

	/**
	 * Sets clustered Tree for experiments
	 * 
	 * @param Tree
	 */
	public void setClusteredTreeExps(Tree<ClusterNode> clusteredTree);

	/**
	 * Returns clustered Tree for experiments
	 * 
	 * @return Tree
	 */
	public Tree<ClusterNode> getClusteredTreeExps();

	/**
	 * Returns cluster sizes, determined by affinity clusterer
	 * 
	 * @param
	 * @return CNode
	 */
	public ArrayList<Integer> getAlClusterSizes();

	/**
	 * Sets cluster sizes, used by affinity clusterer
	 * 
	 * @param CNode
	 */
	public void setAlClusterSizes(ArrayList<Integer> alClusterSizes);

	/**
	 * Returns cluster examples, determined by affinity clusterer
	 * 
	 * @param
	 * @return CNode
	 */
	public ArrayList<Integer> getAlExamples();

	/**
	 * Sets cluster examples, used by affinity clusterer
	 * 
	 * @param CNode
	 */
	public void setAlExamples(ArrayList<Integer> alExamples);

	public void setGroupNrInfo(int[] arGroupInfo);

	public void setGroupReprInfo(int[] arGroupRepr);

	public GroupList getGroupList();

	public boolean isClusterInfo();

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

}
