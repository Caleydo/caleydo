/**
 * 
 */
package org.caleydo.core.data.collection.set.viewdata;

import java.util.Iterator;
import java.util.Vector;

import org.caleydo.core.data.collection.IMetaData;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.data.collection.set.ASetSimple;
import org.caleydo.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.caleydo.core.data.view.camera.IViewCamera;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;


/**
 * Class implements all unneeded methods from ISet.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class ASetViewData
extends ASetSimple 
implements ISetViewData {
	
	IViewCamera viewCamera;
	
	private final IStorage[] arStorageArray; 
	
	/**
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 * @param setType
	 */
	public ASetViewData(int iSetCollectionId,
			IGeneralManager setGeneralManager,
			SetType setType) {

		super(iSetCollectionId, setGeneralManager, setType);
		
		arStorageArray = new IStorage[0];
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getDimensionSize(int)
	 */
	public final int getDimensionSize(int iAtDimension) {

		assert false : "call dummy method";
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getDimensionSizeForAllVirtualArrays()
	 */
	public final int[] getDimensionSizeForAllVirtualArrays() {

		assert false : "call dummy method";
		return new int[0];
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getDimensions()
	 */
	public final int getDimensions() {

		assert false : "call dummy method";
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getStorageByDim(int)
	 */
	public final IStorage[] getStorageByDim(int iAtDimension) {

		assert false : "call dummy method";
		return arStorageArray;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getStorageByDimAndIndex(int, int)
	 */
	public final IStorage getStorageByDimAndIndex(int iAtDimension, int iAtIndex) {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getStorageVectorByDim(int)
	 */
	public final Vector<IStorage> getStorageVectorByDim(int iAtDimension) {

		assert false : "call dummy method";
		return new Vector<IStorage>();
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getVirtualArrayByDim(int)
	 */
	public final IVirtualArray[] getVirtualArrayByDim(int iAtDimension) {

		assert false : "call dummy method";
		return new IVirtualArray[0];
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getVirtualArrayByDimAndIndex(int, int)
	 */
	public final IVirtualArray getVirtualArrayByDimAndIndex(int iAtDimension,
			int iAtIndex) {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getVirtualArrayVectorByDim(int)
	 */
	public final Vector<IVirtualArray> getVirtualArrayVectorByDim(int iAtDimension) {

		assert false : "call dummy method";
		return new Vector<IVirtualArray>();
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#hasVirtualArray(org.caleydo.core.data.collection.IVirtualArray, int)
	 */
	public final boolean hasVirtualArray(IVirtualArray testVirtualArray,
			int iAtDimension) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#hasVirtualArrayInSet(org.caleydo.core.data.collection.IVirtualArray)
	 */
	public final boolean hasVirtualArrayInSet(IVirtualArray testVirtualArray) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#iteratorStorageByDim(int)
	 */
	public final Iterator<IStorage> iteratorStorageByDim(int iAtDimension) {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#iteratorVirtualArrayByDim(int)
	 */
	public final IVirtualArrayIterator iteratorVirtualArrayByDim(int iAtDimension) {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#removeVirtualArray(org.caleydo.core.data.collection.IVirtualArray[], int)
	 */
	public final boolean removeVirtualArray(IVirtualArray[] removeVirtualArray,
			int iFromDimension) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setStorageByDim(org.caleydo.core.data.collection.IStorage[], int)
	 */
	public final void setStorageByDim(IStorage[] setStorage, int iAtDimension) {

		assert false : "call dummy method";
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setStorageByDim(java.util.Vector, int)
	 */
	public final boolean setStorageByDim(Vector<IStorage> setStorage, int iAtDimension) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setStorageByDimAndIndex(org.caleydo.core.data.collection.IStorage, int, int)
	 */
	public final boolean setStorageByDimAndIndex(IStorage addStorage,
			int iAtDimension, int iAtIndex) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setVirtualArrayByDim(org.caleydo.core.data.collection.IVirtualArray[], int)
	 */
	public final boolean setVirtualArrayByDim(IVirtualArray[] addVirtualArray,
			int iAtDimension) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setVirtualArrayByDim(java.util.Vector, int)
	 */
	public final boolean setVirtualArrayByDim(Vector<IVirtualArray> addVirtualArray,
			int iAtDimension) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setVirtualArrayByDimAndIndex(org.caleydo.core.data.collection.IVirtualArray, int, int)
	 */
	public final boolean setVirtualArrayByDimAndIndex(IVirtualArray addVirtualArray,
			int iAtDimension, int iAtIndex) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IMetaDataHandler#getMetaData()
	 */
	public final IMetaData getMetaData() {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IMetaDataHandler#setMetaData(org.caleydo.core.data.collection.IMetaData)
	 */
	public final void setMetaData(IMetaData setMetaData) {

		assert false : "call dummy method";

	}



	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISubSet#swapSubSet(org.caleydo.core.data.collection.ISet, org.caleydo.core.data.collection.ISet)
	 */
	public final boolean swapSubSet(ISet fromSet, ISet toSet) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.xml.IMementoItemXML#createMementoXML()
	 */
	public final String createMementoXML() {

		assert false : "call dummy method";
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.xml.IMementoXML#setMementoXML_usingHandler(org.caleydo.core.xml.parser.ISaxParserHandler)
	 */
	public final boolean setMementoXML_usingHandler(ISaxParserHandler saxHandler) {

		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.IUniqueManagedObject#getBaseType()
	 */
	public final ManagerObjectType getBaseType() {

		return ManagerObjectType.SET_VIEWDATA;
	}

	/* --------------- */
	


}
