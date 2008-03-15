package org.geneview.core.view.opengl.util;

import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.util.HashMap;
import java.util.LinkedList;

import org.geneview.core.data.graph.core.PathwayGraph;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.view.opengl.canvas.pathway.GLPathwayTextureManager;

import com.sun.opengl.util.texture.Texture;


public class JukeboxHierarchyLayer {
	
	private IGeneralManager generalManager;
	
	private HashMap<Integer, Transform> hashElementPositionIndexToTransform;
	
	private GLPathwayTextureManager pathwayTextureManager;
	
	/**
	 * Index in list determines position in stack
	 */
	private LinkedList<Integer> llElementId;
	
	private LinkedList<Integer> llElementIdImportanceQueue;
	
	private LinkedList<Boolean> llElementIdVisibleState;
	
	private JukeboxHierarchyLayer parentLayer;
	private JukeboxHierarchyLayer childLayer;
	
	private int iCapacity = 0;
	
	private float fScalingFactor = 0;
	
	public JukeboxHierarchyLayer(
			final IGeneralManager generalManager,
			final int iCapacity, 
			final float fScalingFactor,
			final GLPathwayTextureManager pathwayTextureManager) {
		
		this.generalManager = generalManager;
		hashElementPositionIndexToTransform = new HashMap<Integer, Transform>();
		llElementId = new LinkedList<Integer>();
		llElementIdImportanceQueue = new LinkedList<Integer>();
		llElementIdVisibleState = new LinkedList<Boolean>();
		this.iCapacity = iCapacity;
		this.fScalingFactor = fScalingFactor;
		this.pathwayTextureManager = pathwayTextureManager;
		
		// Initialize elements with -1
		for (int iPositionIndex = 0; iPositionIndex < iCapacity; iPositionIndex++)
		{
			llElementId.add(-1);
			llElementIdVisibleState.add(false);
		}
	}
	
	public void setParentLayer(final JukeboxHierarchyLayer parentLayer) {
		
		this.parentLayer = parentLayer;
	}

	public final JukeboxHierarchyLayer getParentLayer() {
		
		return parentLayer;
	}
	
	public void setChildLayer(final JukeboxHierarchyLayer childLayer) {
		
		this.childLayer = childLayer;
	}

	public final JukeboxHierarchyLayer getChildLayer() {
		
		return childLayer;
	}
	
	public void replaceElement(int iElementId, int iDestinationPosIndex)
	{
		llElementId.set(iDestinationPosIndex, iElementId);
		llElementIdVisibleState.set(iDestinationPosIndex, false);
		llElementIdImportanceQueue.addFirst(iElementId);	
	}
	
	/**
	 * Add the element permanently to that layer.
	 * If all positions are occupied apply the FIFO 
	 * replacement strategy and return the replaced element.
	 * This is needed for cleanup stuff of the unloaded element.
	 * If a free spot is found 0 is returned because nothing
	 * needs to be replaced.
	 * 
	 * @param iElementId
	 * @return Replaced element
	 */
	public int addElement(int iElementId) {
		
		if (llElementId.contains(-1))
		{
			int iReplacePosition = llElementId.indexOf(-1);

			llElementId.set(iReplacePosition, iElementId);
			llElementIdVisibleState.set(iReplacePosition, false);
			llElementIdImportanceQueue.addFirst(iElementId);	
			
			return 0;
		}
		
		// Find and remove least important element
		int iLeastImportantElementId = llElementIdImportanceQueue.removeLast();

		int iReplacePosition = llElementId.indexOf(iLeastImportantElementId);

		// FIFO replace strategy
		llElementIdImportanceQueue.addFirst(iElementId);	

		llElementIdVisibleState.set(iReplacePosition, false);
		
		llElementId.set(iReplacePosition, iElementId);
		
		if (iCapacity < 5)
			calculatePathwayScaling(iElementId);
		
		return iLeastImportantElementId;	
	}

//	public void setElement(int iPosIndex, int iElementId) {
//		
//		if (llElementId.size() <= iPosIndex)
//			addElement(iElementId);
//		else
//			llElementId.set(iPosIndex, iElementId);
//	}
	
	public int getElementIdByPositionIndex(int iPosIndex) {
		
		return llElementId.get(iPosIndex);
	}
	
	public LinkedList<Integer> getElementList() {
		
		return llElementId;
	}
	
	public void setTransformByPositionIndex(int iPosIndex, Transform transform) {
		
		hashElementPositionIndexToTransform.put(iPosIndex, transform);
	}
	
	public void removeElement(int iElementId) {

		if (llElementId.contains((Integer)iElementId))
		{
			int iReplacePosition = llElementId.indexOf((Integer)iElementId);
			
			llElementId.set(iReplacePosition, -1);
			llElementIdVisibleState.set(iReplacePosition, false);
			llElementIdImportanceQueue.removeLastOccurrence(iElementId);
		}
	}
	
	public void removeAllElements() {
		
		llElementId.clear();
		llElementIdImportanceQueue.clear();
		llElementIdVisibleState.clear();
	}
	
	public boolean containsElement(int iElementId) {
		
		return llElementId.contains(iElementId);
	}
	
//	public int replaceElement(int iElementToReplaceId, int iNewElementId) {
//		
//		if (llElementId.contains(iElementToReplaceId))
//			llElementId.set(llElementId.indexOf(iElementToReplaceId),iNewElementId);
//			
//		if (llElementIdImportanceQueue.contains(iElementToReplaceId))
//			llElementIdImportanceQueue.set(llElementIdImportanceQueue.indexOf(iElementToReplaceId),iNewElementId);
//		
////		if (llElementIdVisibleState.contains(iElementToReplaceId))
////			llElementIdVisibleState.set(llElementIdVisibleState.indexOf(iElementToReplaceId), true);
//		
//		return llElementId.indexOf(iNewElementId);
//	}
	
	public void setElementByPositionIndex(final int iPositionIndex,
			final int iElementId) {
	
		llElementId.set(iPositionIndex, iElementId);
		llElementIdImportanceQueue.set(iPositionIndex, iElementId);
	}
	
	public final Transform getTransformByElementId(int iElementId) {
		
		return hashElementPositionIndexToTransform.get(getPositionIndexByElementId(iElementId));
	}
	
	public final Transform getTransformByPositionIndex(int iPosIndex) {
		
		return hashElementPositionIndexToTransform.get(iPosIndex);
	}
	
	public final int getPositionIndexByElementId(int iElementId) {
		
		return llElementId.indexOf(iElementId);
	}
	
	public final int getNextPositionIndex() {
		
		if (llElementId.contains(-1))
		{
			return llElementId.indexOf(-1);
		}
		else 
		{	
			// Get index of least important element for replacement
			return llElementId.indexOf(llElementIdImportanceQueue.getLast());
		}
	}
	
	public void setElementVisibilityById(final boolean bVisibility,
			final int iElementId) {
		
		if (!llElementId.contains(iElementId))
			return;
			
		llElementIdVisibleState.set(llElementId.indexOf(iElementId), bVisibility);
	}
	
	public final boolean getElementVisibilityById(final int iElementId) {

		return llElementIdVisibleState.get(llElementId.indexOf(iElementId));
	}
	
	public final int getCapacity() {
		
		return iCapacity;
	}
	
	public final float getScalingFactor() {
		
		return fScalingFactor;
	}
	
	/**
	 * @deprecated
	 * @param iPathwayId
	 */
	private void calculatePathwayScaling(final int iPathwayId) {
		
		if (pathwayTextureManager == null)
			return;
		
		int iImageWidth = ((PathwayGraph)generalManager.getSingelton()
				.getPathwayManager().getItem(iPathwayId)).getWidth();
		int iImageHeight = ((PathwayGraph)generalManager.getSingelton()
				.getPathwayManager().getItem(iPathwayId)).getHeight();
	
		float fTmpScalingFactor = 1;
		if (iImageHeight > 570 && iImageHeight > iImageWidth)
		{
			fTmpScalingFactor = 570f / iImageHeight;
			fTmpScalingFactor *= getScalingFactor();
			
			getTransformByElementId(iPathwayId)
				.setScale(new Vec3f(fTmpScalingFactor, fTmpScalingFactor, 1f));
		}
		else if (iImageWidth > 750)
		{
			fTmpScalingFactor = 750f / iImageWidth;
			fTmpScalingFactor *= getScalingFactor();
			
			getTransformByElementId(iPathwayId)
				.setScale(new Vec3f(fTmpScalingFactor, fTmpScalingFactor, 1f));
		}
		else
		{
			getTransformByElementId(iPathwayId)
				.setScale(new Vec3f(fScalingFactor, fScalingFactor, fScalingFactor));
		}
	}
}
