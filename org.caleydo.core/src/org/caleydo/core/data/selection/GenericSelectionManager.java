package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import org.caleydo.core.data.mapping.EGenomeMappingType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * <p>
 * Manages selections generically by storing them in hash maps. The manager can
 * handle an arbitrary number of selection types, which have to be defined in
 * {@link EViewInternalSelectionType} A normal type, by default NORMAL in the
 * aforementioned enum is considered to be the base type, where all elements are
 * initially added to.
 * </p>
 * <p>
 * Use the Builder to create an instance and specify optional parameters.
 * </p>
 * <p>
 * The selection manager always keeps a {@link SelectionDelta}, which can be
 * used to communicate changes in the selection to other views. This is reset
 * every time you call it's getter.
 * </p>
 * <p>
 * Consequently it can also merge external deltas into its own selection.
 * </p>
 * <p>
 * The manager can also produce a {@link SelectionDelta} object which contains
 * different ID types, by mapping them. You therefore have to specify two
 * {@link EGenomeMappingType}, one for internal to external and vice versa.
 * </p>
 * <p>
 * The manager can operate on a subset of the possible types in
 * {@link EViewInternalSelectionType}, by specifying a list of allowed types in
 * the {@link Builder}. When a selection delta is merged into the manager that
 * contains values that are not specified in the list of allowed values, the
 * selections are ignored
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GenericSelectionManager
{

	// EnumMap<Enum, V>
	// HashMap<SelectionEnumeration, Integer> hash;
	private HashMap<EViewInternalSelectionType, HashMap<Integer, Boolean>> hashSelectionTypes;

	private EViewInternalSelectionType eNormalType;

	private int iNumberOfElements = 0;

	private ArrayList<EViewInternalSelectionType> alSelectionTypes;

	private SelectionDelta selectionDelta;

	private boolean bIsDeltaWritingEnabled = true;

	private EGenomeMappingType internalToExternalMapping;
	private EGenomeMappingType externalToInternalMapping;

	/**
	 * Static Builder for GenericSelectionManager. Allows to handle various
	 * parameter configurations. Call
	 * GenericSelectionManager.Builder().setOneVariabe ().setOther().build()
	 * 
	 * @author Alexander Lex
	 * 
	 */
	public static class Builder
	{
		private EGenomeMappingType internalToExternalMapping = null;
		private EGenomeMappingType externalToInternalMapping = null;
		private ArrayList<EViewInternalSelectionType> alSelectionTypes = null;
		private EViewInternalSelectionType normalType = EViewInternalSelectionType.NORMAL;

		/**
		 * Set the mapping type for the selection manager When (view) internal
		 * IDs differ from those that are recognized by the rest of the system
		 * it is possible to set up a mapping for the creation of the
		 * SelectionDelta here
		 * 
		 * @param iToEMapping internal to external mapping
		 * @param eToIMapping external to internal mapping
		 * @return the Builder, call another setter or build() when you're done
		 */
		public Builder mappingType(EGenomeMappingType iToEMapping,
				EGenomeMappingType eToIMapping)
		{
			internalToExternalMapping = iToEMapping;
			externalToInternalMapping = eToIMapping;
			return this;
		}

		/**
		 * Set a normal type if it should differ from
		 * EViewInternalSelectionType.NORMAL, which is the default
		 * 
		 * @param normalType the normal type
		 * @return the Builder, call another setter or build() when you're done
		 */
		public Builder normalType(EViewInternalSelectionType normalType)
		{
			this.normalType = normalType;
			return this;
		}

		/**
		 * Set a list of selection types if you don't want to (or can) handle
		 * all selection types in your view
		 * 
		 * @param alSelectionTypes the list of selection types
		 * @return the Builder, call another setter or build() when you're done
		 */
		public Builder selectionTypes(ArrayList<EViewInternalSelectionType> alSelectionTypes)
		{
			this.alSelectionTypes = alSelectionTypes;
			return this;
		}

		/**
		 * Call this method when you're done initializing, it will return the
		 * actual selection manager
		 * 
		 * @return the selection manager
		 */
		public GenericSelectionManager build()
		{
			return new GenericSelectionManager(this);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param builder the builder
	 */
	private GenericSelectionManager(Builder builder)
	{
		this.eNormalType = builder.normalType;
		this.externalToInternalMapping = builder.externalToInternalMapping;
		this.internalToExternalMapping = builder.internalToExternalMapping;
		if (builder.alSelectionTypes == null)
		{
			alSelectionTypes = new ArrayList<EViewInternalSelectionType>();
			for (EViewInternalSelectionType selectionType : EViewInternalSelectionType
					.values())
			{
				alSelectionTypes.add(selectionType);
			}
		}

		hashSelectionTypes = new HashMap<EViewInternalSelectionType, HashMap<Integer, Boolean>>();
		selectionDelta = new SelectionDelta();

		for (EViewInternalSelectionType eType : alSelectionTypes)
		{
			hashSelectionTypes.put(eType, new HashMap<Integer, Boolean>());
		}

	}

	/**
	 * Initialize by adding the elements one by one.
	 * 
	 * @param iElementID
	 */
	public void initialAdd(int iElementID)
	{
		iNumberOfElements++;
		hashSelectionTypes.get(eNormalType).put(iElementID, null);
	}

	/**
	 * Initialize by adding the elements all at once
	 * 
	 * @param iAlElementIDs
	 */
	public void initialAdd(ArrayList<Integer> iAlElementIDs)
	{
		for (Integer iElementID : iAlElementIDs)
		{
			hashSelectionTypes.get(eNormalType).put(iElementID, null);
		}

		iNumberOfElements += iAlElementIDs.size();
	}

	/**
	 * Clears all elements and sets the element counter to 0, clear selection
	 * delta
	 */
	public void resetSelectionManager()
	{
		hashSelectionTypes.clear();
		for (EViewInternalSelectionType eType : alSelectionTypes)
		{
			hashSelectionTypes.put(eType, new HashMap<Integer, Boolean>());
		}
		iNumberOfElements = 0;
		selectionDelta = new SelectionDelta();
	}

	/**
	 * Clears all selections. All selections are written into the "normal" type
	 */
	public void clearSelections()
	{
		for (EViewInternalSelectionType eType : alSelectionTypes)
		{
			if (eType == eNormalType)
				continue;
			clearSelection(eType);
		}
	}

	/**
	 * Clear one specific selection type. The elements contained in that type
	 * are added to the "normal" type
	 * 
	 * @param sSelectionType the selection type to be cleared
	 */
	public void clearSelection(EViewInternalSelectionType eSelectionType)
	{
		if (eSelectionType == eNormalType)
			throw new CaleydoRuntimeException(
					"SelectionManager: cannot reset selections of normal selection",
					CaleydoRuntimeExceptionType.VIEW);

		for (int iSelectionID : hashSelectionTypes.get(eSelectionType).keySet())
		{
			selectionDelta.addSelection(iSelectionID, eNormalType.intRep());
		}

		hashSelectionTypes.get(eNormalType).putAll(hashSelectionTypes.get(eSelectionType));
		hashSelectionTypes.get(eSelectionType).clear();
	}

	/**
	 * Returns all elements that are in a specific selection type
	 * 
	 * @param sSelectionType
	 * @return
	 */
	public Set<Integer> getElements(EViewInternalSelectionType eSelectionType)
	{
		return hashSelectionTypes.get(eSelectionType).keySet();
	}

	/**
	 * Add a element to a particular selection type. The element must exist in
	 * the selection manager. If the element is already in the target type
	 * nothing is done. If it is in another type the element is removed from the
	 * other type and moved to the target type
	 * 
	 * @param targetType the selection type the element should be added to
	 * @param iElementID the id of the element
	 * @throws CaleydoRuntimeException if the element is not in the selection
	 *             manager
	 */
	public void addToType(EViewInternalSelectionType targetType, int iElementID)
	{

		if (hashSelectionTypes.get(targetType).containsKey(iElementID))
			return;

		for (EViewInternalSelectionType currentType : alSelectionTypes)
		{
			if (currentType == targetType)
				continue;

			if (hashSelectionTypes.get(currentType).containsKey(iElementID))
			{
				hashSelectionTypes.get(currentType).remove(iElementID);
				hashSelectionTypes.get(targetType).put(iElementID, null);
				if (bIsDeltaWritingEnabled)
					selectionDelta.addSelection(iElementID, targetType.intRep());
				return;
			}
		}

		throw new CaleydoRuntimeException(
				"SelectionManager: element to be removed does not exist",
				CaleydoRuntimeExceptionType.VIEW);
	}

	/**
	 * Removes a element form a particular selection type and puts it into the
	 * normal type. Can not be called on the normal type. Nothing happens if the
	 * element is not contained in the type
	 * 
	 * @param eSelectionType
	 * @param iElementID
	 * @throws CaleydoRuntimeException if called with the normal type
	 */
	public void removeFromType(EViewInternalSelectionType eSelectionType, int iElementID)
	{
		if (eSelectionType == eNormalType)
			throw new CaleydoRuntimeException(
					"SelectionManager: cannot remove from normal selection",
					CaleydoRuntimeExceptionType.VIEW);

		if (hashSelectionTypes.get(eSelectionType).containsKey(iElementID))
		{
			hashSelectionTypes.get(eSelectionType).remove(iElementID);
			hashSelectionTypes.get(eNormalType).put(iElementID, null);
			selectionDelta.addSelection(iElementID, eNormalType.intRep());
		}
	}

	/**
	 * Get the number of elements in the selection manager
	 * 
	 * @return the number of elements
	 */
	public int getNumberOfElements()
	{
		return iNumberOfElements;
	}

	/**
	 * Check whether a element is in a particular selection
	 * 
	 * @param eSelectionType the suspected selection type
	 * @param iElementID the id of the element
	 * @return true if the type contains the element, else false
	 */
	public boolean checkStatus(EViewInternalSelectionType eSelectionType, int iElementID)
	{
		if (hashSelectionTypes.get(eSelectionType).containsKey(iElementID))
			return true;
		else
			return false;
	}

	/**
	 * Returns the accumulated selection delta since the last getDelta and
	 * clears the internal selectionDelta.
	 * 
	 * @return the SelectionDelta
	 */
	// TODO Virtual Arrays
	public ISelectionDelta getDelta()
	{
		SelectionDelta returnDelta;
		if (internalToExternalMapping == null)
			returnDelta = selectionDelta;
		else
		{
			returnDelta = new SelectionDelta();
			for (SelectionItem item : selectionDelta)
			{
				int iExternalID = getExternalFromInternalID(item.getSelectionID());
				if (iExternalID == -1)
					GeneralManager.get().getLogger().log(Level.WARNING,
							"No external id for " + item.getSelectionID());
				
				returnDelta.addSelection(iExternalID,
						item.getSelectionType());
			}
		}

		selectionDelta = new SelectionDelta();

		return returnDelta;
	}

	/**
	 * Merge an external selection delta into the local selection
	 * 
	 * @param selectionDelta the selection delta
	 */
	// TODO Virtual Arrays
	public void setDelta(ISelectionDelta selectionDelta)
	{
		if (externalToInternalMapping != null)
		{
			ISelectionDelta externalSelectionDelta = selectionDelta;
			selectionDelta = new SelectionDelta();
			for (SelectionItem item : externalSelectionDelta)
			{
				int iInternalID = getInternalFromExternalID(item.getSelectionID());
				if(iInternalID == -1)
					GeneralManager.get().getLogger().log(Level.WARNING,
							"No internal id for " + item.getSelectionID());
					
				selectionDelta.addSelection(iInternalID,
						item.getSelectionType());
			}
		}

		bIsDeltaWritingEnabled = false;
		for (SelectionItem selection : selectionDelta)
		{
			EViewInternalSelectionType type = EViewInternalSelectionType.valueOf(selection
					.getSelectionType());
			// TODO: what to do if types are not contained in the current list?
			// atm ignore them
			if (!alSelectionTypes.contains(type))
				continue;
			addToType(type, selection.getSelectionID());

		}
		bIsDeltaWritingEnabled = true;
	}

	private int getExternalFromInternalID(int index)
	{
		int iID = GeneralManager.get().getGenomeIdManager().getIdIntFromIntByMapping(index,
				internalToExternalMapping);
		return iID;
	}

	private int getInternalFromExternalID(int index)
	{
		int iID = GeneralManager.get().getGenomeIdManager().getIdIntFromIntByMapping(index,
				externalToInternalMapping);
		return iID;
	}
}
