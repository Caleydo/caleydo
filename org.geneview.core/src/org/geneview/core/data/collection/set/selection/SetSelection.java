/**
 * 
 */
package org.geneview.core.data.collection.set.selection;

import java.util.Vector;

import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.IVirtualArray;
import org.geneview.core.data.collection.SetType;
import org.geneview.core.data.collection.set.SetPlanarSimple;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.event.EventPublisher;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Selection SET that gives access to
 * the selection storage arrays.
 * The access methods are thread safe.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class SetSelection 
extends SetPlanarSimple 
implements ISetSelection {

	/**
	 * Constructor.
	 * 
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 */
	public SetSelection(int iSetCollectionId, 
			IGeneralManager refGeneralManager) {

		super(iSetCollectionId, 
				refGeneralManager, 
				SetType.SET_SELECTION);
		
		/** add missing objects for optional data */
		vecRefSelection_Array.add(2, new Vector<IVirtualArray> (2));		
		vecRefStorage_Array.add(2, new Vector<IStorage> (2));
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.data.collection.selection.ISetSelection#setSelectionIdArray(int[])
	 */
	public void setSelectionIdArray(int[] iArSelectionId) {
		
		this.getWriteToken();
		this.getStorageByDimAndIndex(0, 0).setArrayInt(iArSelectionId);
		
		//TODO: fix this after CHI!
//		IVirtualArray bufferVA= this.getVirtualArrayByDimAndIndex(0, 0);
//		bufferVA.setOffset(0);
//		bufferVA.setLength(iArSelectionId.length-1);
		
		this.returnWriteToken();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.data.collection.selection.ISetSelection#setGroupArray(int[])
	 */
	public void setGroupArray(int[] iArSelectionGroup) {
		
		this.getWriteToken();
		this.getStorageByDimAndIndex(0, 1).setArrayInt(iArSelectionGroup);
		
		//TODO: fix this after CHI!
//		IVirtualArray bufferVA = this.getVirtualArrayByDimAndIndex(0, 1);
//		bufferVA.setOffset(0);
//		bufferVA.setLength(iArSelectionGroup.length-1);
		
		this.returnWriteToken();
	}

	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.data.collection.selection.ISetSelection#setOptionalDataArray(int[])
	 */
	public void setOptionalDataArray(int[] iArSelectionOptionalData) {
		
		this.getWriteToken();
		this.getStorageByDimAndIndex(0, 2).setArrayInt(iArSelectionOptionalData);
		
		//TODO: fix this after CHI!
//		IVirtualArray bufferVA = this.getVirtualArrayByDimAndIndex(0, 2);
//		bufferVA.setOffset(0);
//		bufferVA.setLength(iArSelectionOptionalData.length-1);
		
		this.returnWriteToken();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.data.collection.selection.ISetSelection#setAllSelectionDataArrays(int[], int[], int[])
	 */
	public void setAllSelectionDataArrays(int[] iArSelectionId, 
			int[] iArSelectionGroup, 
			int[] iArSelectionOptionalData) {
		
		if (( iArSelectionId.length != iArSelectionGroup.length )&&
				(iArSelectionGroup.length != iArSelectionOptionalData.length))
		{
			throw new GeneViewRuntimeException("Try to set a SetSelection wiht array of different length!");		
		}
		
		this.getWriteToken();
		this.getStorageByDimAndIndex(0, 0).setArrayInt(iArSelectionId);
		this.getStorageByDimAndIndex(0, 1).setArrayInt(iArSelectionGroup);
		this.getStorageByDimAndIndex(0, 2).setArrayInt(iArSelectionOptionalData);
		
		//TODO: fix this after CHI!
//		for ( int i=0; i<3; i++)
//		{
//			IVirtualArray bufferVA = this.getVirtualArrayByDimAndIndex(0, i);
//			bufferVA.setOffset(0);
//			bufferVA.setLength(iArSelectionId.length);
//		}
		
		this.returnWriteToken();
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.ISet#getDimensions()
	 */
	public final int getDimensions() {
		return 3;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.data.collection.selection.ISetSelection#getSelectionIdArray()
	 */
	public int[] getSelectionIdArray() {
		
		this.getReadToken();
		int[] tmp = this.getStorageByDimAndIndex(0, 0).getArrayInt();
		this.returnReadToken();
		
		return tmp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.data.collection.selection.ISetSelection#getGroupArray()
	 */
	public int[] getGroupArray() {	

		this.getReadToken();
		int[] tmp = this.getStorageByDimAndIndex(0, 1).getArrayInt();
		this.returnReadToken();
		
		return tmp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.data.collection.selection.ISetSelection#getOptionalDataArray()
	 */
	public int[] getOptionalDataArray() {

		this.getReadToken();
		int[] tmp = this.getStorageByDimAndIndex(0, 2).getArrayInt();
		this.returnReadToken();
		
		return tmp;	
	}
	
	
	/**
	 * The selection set will be filled 
	 * with arrays that are given as parameter.
	 * The selection event will be triggered
	 * with the unique ID parameter.
	 * 
	 * @param setSelection
	 * @param iUniqueTriggerID
	 * @param iArSelectionVertexId
	 * @param iArSelectionGroup
	 * @param iArNeighborVertices
	 */
	public void updateSelectionSet(int iUniqueTriggerID, 
			int[] iArSelectionVertexId,
			int[] iArSelectionGroup,
			int[] iArNeighborVertices) {
	
			// Update selection SET data.
			this.setAllSelectionDataArrays(
					iArSelectionVertexId, iArSelectionGroup, iArNeighborVertices);
			
			updateSelectionSet(iUniqueTriggerID);
	}
	
	public void updateSelectionSet(int iUniqueTriggerID) {
		
		try {	
			generalManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": updateSelectionSet(): Set selection data and trigger update.",
					LoggerType.VERBOSE );
			
	 		// Calls update with the ID of the view
	 		((EventPublisher)generalManager.getSingelton().
				getEventPublisher()).updateReceiver(generalManager.
						getSingelton().getViewGLCanvasManager().
							getItem(iUniqueTriggerID), this);
	 		
		} catch (Exception e)
		{
			generalManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": updateSelectionSet(): Problem during selection update triggering.",
					LoggerType.MINOR_ERROR );
	
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.geneview.core.data.collection.set.SetPlanarSimple#toString()
	 */
	public String toString() {
		return sLabel;
	}
}
