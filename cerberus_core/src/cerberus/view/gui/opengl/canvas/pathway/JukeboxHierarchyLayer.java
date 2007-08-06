package cerberus.view.gui.opengl.canvas.pathway;

import gleem.linalg.Transform;

import java.util.HashMap;
import java.util.LinkedList;


public class JukeboxHierarchyLayer {
	
	private HashMap<Integer, Transform> hashElementPositionIndexToTransform;
	
	/**
	 * Index in list determines position in stack
	 */
	private LinkedList<Integer> llElementId;
	
	/**
	 * Pathways that are currently slerped or need to be slerped.
	 */
	private LinkedList<Integer> llElementIdIncomingTransistionState;
	private LinkedList<Integer> llElementIdOutgoingTransistionState;
	
	private LinkedList<Integer> llElementIdImportanceQueue;
	
	private JukeboxHierarchyLayer parentLayer;
	private JukeboxHierarchyLayer childLayer;
	
	private int iCapacity = 0;
	
	public JukeboxHierarchyLayer(int iCapacity) {
		
		hashElementPositionIndexToTransform = new HashMap<Integer, Transform>();
		llElementId = new LinkedList<Integer>();
		llElementIdIncomingTransistionState = new LinkedList<Integer>();
		llElementIdOutgoingTransistionState = new LinkedList<Integer>();
		llElementIdImportanceQueue = new LinkedList<Integer>();
		this.iCapacity = iCapacity;
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
	
	/**
	 * Add the element permanently to that layer.
	 * 
	 * @param iElementId
	 */
	public void addElement(int iElementId) {
		
		llElementIdIncomingTransistionState.remove((Integer)iElementId);
		
		// Check if element limit is reached
		if (llElementId.size() >= iCapacity)
		{
			// Find and remove least important element
			int iLeastImportantElementId = llElementIdImportanceQueue.removeLast();

			int iReplacePosition = llElementId.indexOf(iLeastImportantElementId);
			llElementId.set(iReplacePosition, iElementId);
			
			// FIFO replace strategy
			llElementIdImportanceQueue.addFirst(iElementId);			
		}
		else
		{
			// Add to the end of the stack (because there is free space)
			llElementId.addLast(iElementId);
			llElementIdImportanceQueue.addFirst(iElementId);
		}
	}
	
	public void addElementIdIncomingTransitionState(int iElementId) {
		
		llElementIdIncomingTransistionState.add(iElementId);
	}
	
	public void setElement(int iPosIndex, int iElementId) {
		
		if (llElementId.size() <= iPosIndex)
			addElement(iElementId);
		else
			llElementId.set(iPosIndex, iElementId);
	}
	
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
		
		llElementId.remove((Integer)iElementId);
		llElementIdImportanceQueue.remove((Integer)iElementId);
	}
	
	public boolean containsElement(int iElementId) {
		
		return llElementId.contains(iElementId);
	}
	
	public boolean containsElementInTransitionState(int iElementId) {
		
		return llElementIdIncomingTransistionState.contains(iElementId)
			|| llElementIdOutgoingTransistionState.contains(iElementId);
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
		
		// Check if a spot is free
		if (llElementId.size() < iCapacity)
		{	
			// Append to the end of the list
			return llElementId.size(); 
		}
		else 
		{	
			// Get index of least important element for replacement
			return llElementId.indexOf(llElementIdImportanceQueue.getLast());
		}
	}
}
