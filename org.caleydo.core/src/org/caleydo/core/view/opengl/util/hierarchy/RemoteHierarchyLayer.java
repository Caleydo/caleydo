package org.caleydo.core.view.opengl.util.hierarchy;

import gleem.linalg.open.Transform;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Class represents a logical layer in a remote rendered view.
 * 
 * @author Marc Streit
 */
public class RemoteHierarchyLayer
{

	private EHierarchyLevel level;

	private HashMap<Integer, Transform> hashElementPositionIndexToTransform;

	/**
	 * Index in list determines position in stack
	 */
	private LinkedList<Integer> llElementId;

	private LinkedList<Integer> llElementIdImportanceQueue;

	private LinkedList<Boolean> llElementIdVisibleState;

	private RemoteHierarchyLayer parentLayer;

	private RemoteHierarchyLayer childLayer;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param iCapacity
	 */
	public RemoteHierarchyLayer(final EHierarchyLevel level)
	{

		this.level = level;
		hashElementPositionIndexToTransform = new HashMap<Integer, Transform>();
		llElementId = new LinkedList<Integer>();
		llElementIdImportanceQueue = new LinkedList<Integer>();
		llElementIdVisibleState = new LinkedList<Boolean>();

		// Initialize elements with -1
		for (int iPositionIndex = 0; iPositionIndex < level.getCapacity(); iPositionIndex++)
		{
			llElementId.add(-1);
			llElementIdVisibleState.add(false);
		}
	}

	public void setParentLayer(final RemoteHierarchyLayer parentLayer)
	{

		this.parentLayer = parentLayer;
	}

	public final RemoteHierarchyLayer getParentLayer()
	{

		return parentLayer;
	}

	public void setChildLayer(final RemoteHierarchyLayer childLayer)
	{

		this.childLayer = childLayer;
	}

	public final RemoteHierarchyLayer getChildLayer()
	{

		return childLayer;
	}

	public void replaceElement(int iElementId, int iDestinationPosIndex)
	{

		llElementId.set(iDestinationPosIndex, iElementId);
		llElementIdVisibleState.set(iDestinationPosIndex, false);
		llElementIdImportanceQueue.addFirst(iElementId);

		// if (iCapacity < 5)
		// calculatePathwayScaling(iElementId);
	}

	/**
	 * Add the element permanently to that layer. If all positions are occupied
	 * apply the FIFO replacement strategy and return the replaced element. This
	 * is needed for cleanup stuff of the unloaded element. If a free spot is
	 * found 0 is returned because nothing needs to be replaced.
	 * 
	 * @param iElementId
	 * @return Replaced element
	 */
	public int addElement(int iElementId)
	{

		if (llElementId.contains(-1))
		{
			int iReplacePosition = llElementId.indexOf(-1);

			llElementId.set(iReplacePosition, iElementId);
			llElementIdVisibleState.set(iReplacePosition, false);
			llElementIdImportanceQueue.addFirst(iElementId);

			// if (iCapacity < 5)
			// calculatePathwayScaling(iElementId);

			return 0;
		}

		// Find and remove least important element
		int iLeastImportantElementId = llElementIdImportanceQueue.removeLast();

		int iReplacePosition = llElementId.indexOf(iLeastImportantElementId);

		// FIFO replace strategy
		llElementIdImportanceQueue.addFirst(iElementId);

		llElementIdVisibleState.set(iReplacePosition, false);

		llElementId.set(iReplacePosition, iElementId);

		// if (iCapacity < 5)
		// calculatePathwayScaling(iElementId);

		return iLeastImportantElementId;
	}

	public int getElementIdByPositionIndex(int iPosIndex)
	{

		return llElementId.get(iPosIndex);
	}

	public LinkedList<Integer> getElementList()
	{

		return llElementId;
	}

	public void setTransformByPositionIndex(int iPosIndex, Transform transform)
	{

		hashElementPositionIndexToTransform.put(iPosIndex, transform);
	}

	public void removeElement(int iElementId)
	{

		if (llElementId.contains((Integer) iElementId))
		{
			int iReplacePosition = llElementId.indexOf((Integer) iElementId);

			llElementId.set(iReplacePosition, -1);
			llElementIdVisibleState.set(iReplacePosition, false);
			// llElementIdImportanceQueue.removeLastOccurrence(iElementId);
			llElementIdImportanceQueue.remove((Object) iElementId);
		}
	}

	public void removeAllElements()
	{

		llElementId.clear();
		llElementIdImportanceQueue.clear();
		llElementIdVisibleState.clear();
	}

	public boolean containsElement(int iElementId)
	{

		return llElementId.contains(iElementId);
	}

	public void setElementByPositionIndex(final int iPositionIndex, final int iElementId)
	{

		llElementId.set(iPositionIndex, iElementId);
		llElementIdImportanceQueue.set(iPositionIndex, iElementId);
	}

	public final Transform getTransformByElementId(int iElementId)
	{

		return hashElementPositionIndexToTransform
				.get(getPositionIndexByElementId(iElementId));
	}

	public final Transform getTransformByPositionIndex(int iPosIndex)
	{

		return hashElementPositionIndexToTransform.get(iPosIndex);
	}

	public final int getPositionIndexByElementId(int iElementId)
	{

		return llElementId.indexOf(iElementId);
	}

	public final int getNextPositionIndex()
	{

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

	public void setElementVisibilityById(final boolean bVisibility, final int iElementId)
	{

		if (!llElementId.contains(iElementId))
			return;

		llElementIdVisibleState.set(llElementId.indexOf(iElementId), bVisibility);
	}

	public final boolean getElementVisibilityById(final int iElementId)
	{

		return llElementIdVisibleState.get(llElementId.indexOf(iElementId));
	}

	public final int getCapacity()
	{

		return level.getCapacity();
	}

	public final EHierarchyLevel getLevel()
	{

		return level;
	}
}
