/**
 * 
 */
package cerberus.data.collection.set.viewdata;

import java.util.Iterator;
import java.util.Vector;

import cerberus.data.collection.IMetaData;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.SetType;
import cerberus.data.collection.set.ASetSimple;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import cerberus.data.view.camera.IViewCamera;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.ISaxParserHandler;


/**
 * Class implements all unneeded methods from ISet.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class ASetViewData
extends ASetSimple 
implements ISetViewData {
	
	IViewCamera refIViewCamera;
	
	private final IStorage[] refIStorageArray; 
	
	/**
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 * @param setType
	 */
	public ASetViewData(int iSetCollectionId,
			IGeneralManager setGeneralManager,
			SetType setType) {

		super(iSetCollectionId, setGeneralManager, setType);
		
		refIStorageArray = new IStorage[0];
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getDimensionSize(int)
	 */
	public final int getDimensionSize(int iAtDimension) {

		assert false : "call dummy method";
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getDimensionSizeForAllVirtualArrays()
	 */
	public final int[] getDimensionSizeForAllVirtualArrays() {

		assert false : "call dummy method";
		return new int[0];
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getDimensions()
	 */
	public final int getDimensions() {

		assert false : "call dummy method";
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getStorageByDim(int)
	 */
	public final IStorage[] getStorageByDim(int iAtDimension) {

		assert false : "call dummy method";
		return refIStorageArray;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getStorageByDimAndIndex(int, int)
	 */
	public final IStorage getStorageByDimAndIndex(int iAtDimension, int iAtIndex) {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getStorageVectorByDim(int)
	 */
	public final Vector<IStorage> getStorageVectorByDim(int iAtDimension) {

		assert false : "call dummy method";
		return new Vector<IStorage>();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getVirtualArrayByDim(int)
	 */
	public final IVirtualArray[] getVirtualArrayByDim(int iAtDimension) {

		assert false : "call dummy method";
		return new IVirtualArray[0];
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getVirtualArrayByDimAndIndex(int, int)
	 */
	public final IVirtualArray getVirtualArrayByDimAndIndex(int iAtDimension,
			int iAtIndex) {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getVirtualArrayVectorByDim(int)
	 */
	public final Vector<IVirtualArray> getVirtualArrayVectorByDim(int iAtDimension) {

		assert false : "call dummy method";
		return new Vector<IVirtualArray>();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#hasVirtualArray(cerberus.data.collection.IVirtualArray, int)
	 */
	public final boolean hasVirtualArray(IVirtualArray testVirtualArray,
			int iAtDimension) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#hasVirtualArrayInSet(cerberus.data.collection.IVirtualArray)
	 */
	public final boolean hasVirtualArrayInSet(IVirtualArray testVirtualArray) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#iteratorStorageByDim(int)
	 */
	public final Iterator<IStorage> iteratorStorageByDim(int iAtDimension) {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#iteratorVirtualArrayByDim(int)
	 */
	public final IVirtualArrayIterator iteratorVirtualArrayByDim(int iAtDimension) {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#removeVirtualArray(cerberus.data.collection.IVirtualArray[], int)
	 */
	public final boolean removeVirtualArray(IVirtualArray[] removeVirtualArray,
			int iFromDimension) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setStorageByDim(cerberus.data.collection.IStorage[], int)
	 */
	public final void setStorageByDim(IStorage[] setStorage, int iAtDimension) {

		assert false : "call dummy method";
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setStorageByDim(java.util.Vector, int)
	 */
	public final boolean setStorageByDim(Vector<IStorage> setStorage, int iAtDimension) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setStorageByDimAndIndex(cerberus.data.collection.IStorage, int, int)
	 */
	public final boolean setStorageByDimAndIndex(IStorage addStorage,
			int iAtDimension, int iAtIndex) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setVirtualArrayByDim(cerberus.data.collection.IVirtualArray[], int)
	 */
	public final boolean setVirtualArrayByDim(IVirtualArray[] addVirtualArray,
			int iAtDimension) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setVirtualArrayByDim(java.util.Vector, int)
	 */
	public final boolean setVirtualArrayByDim(Vector<IVirtualArray> addVirtualArray,
			int iAtDimension) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setVirtualArrayByDimAndIndex(cerberus.data.collection.IVirtualArray, int, int)
	 */
	public final boolean setVirtualArrayByDimAndIndex(IVirtualArray addVirtualArray,
			int iAtDimension, int iAtIndex) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IMetaDataHandler#getMetaData()
	 */
	public final IMetaData getMetaData() {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IMetaDataHandler#setMetaData(cerberus.data.collection.IMetaData)
	 */
	public final void setMetaData(IMetaData setMetaData) {

		assert false : "call dummy method";

	}



	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISubSet#swapSubSet(cerberus.data.collection.ISet, cerberus.data.collection.ISet)
	 */
	public final boolean swapSubSet(ISet fromSet, ISet toSet) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoItemXML#createMementoXML()
	 */
	public final String createMementoXML() {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.xml.IMementoXML#setMementoXML_usingHandler(cerberus.xml.parser.ISaxParserHandler)
	 */
	public final boolean setMementoXML_usingHandler(ISaxParserHandler refSaxHandler) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.IUniqueManagedObject#getBaseType()
	 */
	public final ManagerObjectType getBaseType() {

		return ManagerObjectType.SET_VIEWDATA;
	}

	/* --------------- */
	


}
