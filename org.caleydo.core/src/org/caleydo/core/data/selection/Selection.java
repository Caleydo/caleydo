package org.caleydo.core.data.selection;

import java.util.ArrayList;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Selection SET that gives access to the selection storage arrays.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class Selection
	extends AUniqueObject
	implements ISelection
{
	private ArrayList<Integer> iAlSelectionID = null;

	private ArrayList<Integer> iAlSelectionGroup = null;

	private ArrayList<Integer> iAlSelectionOptionalData = null;

	/**
	 * Constructor.
	 * 
	 */
	public Selection()
	{

		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.SELECTION));

	}

	@Override
	public void setSelectionIdArray(ArrayList<Integer> iAlSelectionId)
	{
		this.iAlSelectionID = iAlSelectionId;
	}

	@Override
	public void setGroupArray(ArrayList<Integer> iAlSelectionGroup)
	{
		this.iAlSelectionGroup = iAlSelectionGroup;
	}

	@Override
	public void setOptionalDataArray(ArrayList<Integer> iAlSelectionOptionalData)
	{
		this.iAlSelectionOptionalData = iAlSelectionOptionalData;
	}

	@Override
	public void setAllSelectionDataArrays(ArrayList<Integer> iAlSelectionId,
			ArrayList<Integer> iAlSelectionGroup, ArrayList<Integer> iAlSelectionOptionalData)
	{
		this.iAlSelectionID = iAlSelectionId;
		this.iAlSelectionGroup = iAlSelectionGroup;
		this.iAlSelectionOptionalData = iAlSelectionOptionalData;
	}

	@Override
	public ArrayList<Integer> getSelectionIdArray()
	{
		if (iAlSelectionID == null)
			return null;
//			throw new CaleydoRuntimeException(
//					"Tried to access group array, but group array was not set",
//					CaleydoRuntimeExceptionType.SELECTION);

		ArrayList<Integer> alTmp = new ArrayList<Integer>();
		for (Integer iCurrent : iAlSelectionID)
		{
			alTmp.add(iCurrent.intValue());
		}

		return alTmp;
	}

	@Override
	public ArrayList<Integer> getGroupArray()
	{
		if (iAlSelectionGroup == null)
			return null;
//			throw new CaleydoRuntimeException(
//					"Tried to access group array, but group array was not set",
//					CaleydoRuntimeExceptionType.SELECTION);
		ArrayList<Integer> alTmp = new ArrayList<Integer>();
		for (Integer iCurrent : iAlSelectionGroup)
		{
			alTmp.add(iCurrent.intValue());
		}
		return alTmp;
	}

	@Override
	public ArrayList<Integer> getOptionalDataArray()
	{
		return iAlSelectionOptionalData;
	}

	/**
	 * The selection set will be filled with arrays that are given as parameter.
	 * The selection event will be triggered with the unique ID parameter.
	 * 
	 * @param setSelection
	 * @param iUniqueTriggerID
	 * @param iArSelectionVertexId
	 * @param iArSelectionGroup
	 * @param iArNeighborVertices
	 */
	public void updateSelectionSet(int iUniqueTriggerID,
			ArrayList<Integer> iAlSelectionVertexId, ArrayList<Integer> iAlSelectionGroup,
			ArrayList<Integer> iAlNeighborVertices)
	{

		// Update selection SET data.
		this.setAllSelectionDataArrays(iAlSelectionVertexId, iAlSelectionGroup,
				iAlNeighborVertices);

		updateSelectionSet(iUniqueTriggerID);
	}

	public void updateSelectionSet(int iUniqueTriggerID)
	{
		IGeneralManager generalManager = GeneralManager.get();
		((EventPublisher) generalManager.getEventPublisher()).updateReceiver(generalManager
				.getViewGLCanvasManager().getEventListener(iUniqueTriggerID), this);

	}

	public void mergeSelection(ArrayList<Integer> iAlNewSelectionID,
			ArrayList<Integer> iAlNewSelectionGroup, ArrayList<Integer> iAlNewOptional)
	{

		if (iAlSelectionID == null)
		{
			iAlSelectionID = new ArrayList<Integer>();

			if (iAlNewSelectionGroup != null)
			{
				iAlSelectionGroup = new ArrayList<Integer>();
			}

			if (iAlNewOptional != null)
			{
				iAlSelectionGroup = new ArrayList<Integer>();
			}

			// setAllSelectionDataArrays(iAlNewSelectionID,
			// iAlNewSelectionGroup, iAlSelectionOptionalData);
		}

		int iCount = 0;
		for (Integer iCurrent : iAlNewSelectionID)
		{

			if (iAlSelectionID.contains(iCurrent))
			{
				int iIndex = iAlSelectionID.indexOf(iCurrent);
				if (iAlNewSelectionGroup.get(iCount) == -1)
				{
					iAlSelectionID.remove(iIndex);
					if (iAlSelectionGroup != null)
						iAlSelectionGroup.remove(iIndex);
					if (iAlSelectionOptionalData != null)
						iAlSelectionOptionalData.remove(iIndex);
				}
				else
				{
					if (iAlSelectionGroup != null)
					{
						int iBiggerSelection = Math.max(iAlNewSelectionGroup.get(iCount),
								iAlSelectionGroup.get(iIndex));

						iAlSelectionGroup.set(iIndex, iBiggerSelection);
					}
					if (iAlSelectionOptionalData != null)
						iAlSelectionOptionalData.set(iIndex, iAlNewOptional.get(iCount));
				}
			}
			else
			{
				iAlSelectionID.add(iCurrent);
				if (iAlSelectionGroup != null)
					iAlSelectionGroup.add(iAlNewSelectionGroup.get(iCount));
				if (iAlSelectionOptionalData != null)
					iAlSelectionOptionalData.add(iAlNewOptional.get(iCount));
			}
			iCount++;
		}
	}

	public void clearAllSelectionArrays()
	{
		iAlSelectionID = null;
		iAlSelectionGroup = null;
		iAlSelectionOptionalData = null;
	}
}
