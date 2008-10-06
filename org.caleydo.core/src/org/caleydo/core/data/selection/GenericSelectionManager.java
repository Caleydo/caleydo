package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * <p>
 * Manages selections generically by storing them in hash maps. The manager can
 * handle an arbitrary number of selection types, which have to be defined in
 * {@link ESelectionType} A normal type, by default NORMAL in the aforementioned
 * enum is considered to be the base type, where all elements are initially
 * added to.
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
 * {@link EMappingType}, one for internal to external and vice versa.
 * </p>
 * <p>
 * The manager can operate on a subset of the possible types in
 * {@link ESelectionType}, by specifying a list of allowed types in the
 * {@link Builder}. When a selection delta is merged into the manager that
 * contains values that are not specified in the list of allowed values, the
 * selections are ignored
 * </p>
 * <p>
 * When using the manager on data that is also managed by a
 * {@link IVirtualArray} set the currently active virtual array so that it can
 * be updated according to external as well as internal selection modifications.
 * The operations where a virtual array is updated are external and internal
 * remove operations and external add operations.
 * </p>
 * <p>
 * Do not forget to update the virtual array once another instance is active
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GenericSelectionManager
{

	// EnumMap<Enum, V>
	// HashMap<SelectionEnumeration, Integer> hash;
	private EnumMap<ESelectionType, HashMap<Integer, Boolean>> hashSelectionTypes;

	private ESelectionType eNormalType;

	private EIDType internalIDType;
	private EIDType externalIDType;

	// private int iNumberOfElements = 0;

	private ArrayList<ESelectionType> alSelectionTypes;

	private SelectionDelta selectionDelta;

	private boolean bIsDeltaWritingEnabled = true;

	private EMappingType internalToExternalMapping;
	private EMappingType externalToInternalMapping;

	private IVirtualArray virtualArray;

	/**
	 * Static Builder for GenericSelectionManager. Allows to handle various
	 * parameter configurations. Call new
	 * GenericSelectionManager.Builder(EIDType
	 * internalIDType).setOneVariabe().setOther().build()
	 * 
	 * @author Alexander Lex
	 * 
	 */
	public static class Builder
	{
		private EMappingType internalToExternalMapping = null;
		private EMappingType externalToInternalMapping = null;
		private ArrayList<ESelectionType> alSelectionTypes = null;
		private ESelectionType normalType = ESelectionType.NORMAL;
		private EIDType internalIDType = null;
		private EIDType externalIDType = null;

		/**
		 * Constructor for Builder. Pass the internal ID type, of the type
		 * {@link EIDType}. The internal ID type is the type of ID the view is
		 * working with internally, which can be of a different to a type that
		 * is used for external communication
		 * 
		 * @param internalIDType the internal ID type
		 */
		public Builder(EIDType internalIDType)
		{
			this.internalIDType = internalIDType;
		}

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
		public Builder mappingType(EMappingType iToEMapping, EMappingType eToIMapping)
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
		public Builder normalType(ESelectionType normalType)
		{
			this.normalType = normalType;
			return this;
		}

		/**
		 * Pass the external ID type, of the type {@link EIDType}. The external
		 * ID type is the type of ID the view is using for external
		 * communication
		 * 
		 * @param externalIDType the type of the external ID
		 */
		public Builder externalIDType(EIDType externalIDType)
		{
			this.externalIDType = externalIDType;
			return this;
		}

		/**
		 * Set a list of selection types if you don't want to (or can) handle
		 * all selection types in your view
		 * 
		 * @param alSelectionTypes the list of selection types
		 * @return the Builder, call another setter or build() when you're done
		 */
		public Builder selectionTypes(ArrayList<ESelectionType> alSelectionTypes)
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
		this.internalIDType = builder.internalIDType;
		this.externalIDType = builder.externalIDType;
		if (builder.alSelectionTypes == null)
		{
			alSelectionTypes = new ArrayList<ESelectionType>();
			for (ESelectionType selectionType : ESelectionType.values())
			{
				if (selectionType != ESelectionType.ADD)
					alSelectionTypes.add(selectionType);
			}
		}

		hashSelectionTypes = new EnumMap<ESelectionType, HashMap<Integer, Boolean>>(
				ESelectionType.class);
		selectionDelta = new SelectionDelta(internalIDType);

		for (ESelectionType eType : alSelectionTypes)
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
	}

	/**
	 * <p>
	 * Set a virtual array if the data you are managing with this selection
	 * manager is also managed by a virtual array.
	 * </p>
	 * <p>
	 * If you reset this virtual array at runtime the manager is completely
	 * reseted and reinitialized with the data of the virtual array
	 * </p>
	 * 
	 * @param virtualArray the currently active virtual array
	 */
	public void setVA(IVirtualArray virtualArray)
	{
		resetSelectionManager();
		initialAdd(virtualArray.getIndexList());
		this.virtualArray = virtualArray;
	}

	/**
	 * Clears all elements and sets the element counter to 0, clear selection
	 * delta
	 */
	public void resetSelectionManager()
	{
		hashSelectionTypes.clear();
		for (ESelectionType eType : alSelectionTypes)
		{
			hashSelectionTypes.put(eType, new HashMap<Integer, Boolean>());
		}
		virtualArray = null;
		selectionDelta = new SelectionDelta(internalIDType);
	}

	/**
	 * Clears all selections. All selections are written into the "normal" type.
	 * This is not included in the delta.
	 */
	public void clearSelections()
	{
		bIsDeltaWritingEnabled = false;
		for (ESelectionType eType : alSelectionTypes)
		{
			if (eType == eNormalType)
				continue;
			clearSelection(eType);
		}
		bIsDeltaWritingEnabled = true;
	}

	/**
	 * Clear one specific selection type. The elements contained in that type
	 * are added to the "normal" type
	 * 
	 * @param sSelectionType the selection type to be cleared
	 */
	public void clearSelection(ESelectionType eSelectionType)
	{
		if (eSelectionType == ESelectionType.REMOVE)
			return;

		if (eSelectionType == eNormalType)
			throw new IllegalArgumentException(
					"SelectionManager: cannot reset selections of normal selection");

		// TODO the first condition should not be necessary, investigate
		if (hashSelectionTypes.get(eSelectionType) == null
				|| hashSelectionTypes.get(eSelectionType).isEmpty())
			return;

		for (int iSelectionID : hashSelectionTypes.get(eSelectionType).keySet())
		{
			selectionDelta.addSelection(iSelectionID, eNormalType);
		}

		hashSelectionTypes.get(eNormalType).putAll(hashSelectionTypes.get(eSelectionType));
		hashSelectionTypes.get(eSelectionType).clear();
	}

	/**
	 * Returns all elements that are in a specific selection type
	 * 
	 * @param sSelectionType
	 * @return the elements in the type
	 * @throws IllegalArgumentException when called with
	 *             {@link ESelectionType#REMOVE}
	 */
	public Set<Integer> getElements(ESelectionType eSelectionType)
	{
		if (eSelectionType == ESelectionType.REMOVE)
			throw new IllegalArgumentException("Can not return removed values");
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
	public void addToType(ESelectionType targetType, int iElementID)
	{
		if (targetType != ESelectionType.REMOVE
				&& hashSelectionTypes.get(targetType).containsKey(iElementID))
			return;

		if (targetType == ESelectionType.ADD)
			throw new IllegalArgumentException(
					"ADD may not be stored in the selection manager");

		for (ESelectionType currentType : alSelectionTypes)
		{
			if (currentType == targetType)
				continue;

			if (hashSelectionTypes.get(currentType).containsKey(iElementID))
			{
				hashSelectionTypes.get(currentType).remove(iElementID);
				if (targetType == ESelectionType.REMOVE && virtualArray != null)
					virtualArray.removeByElement(iElementID);
				else
					hashSelectionTypes.get(targetType).put(iElementID, null);
				if (bIsDeltaWritingEnabled)
					selectionDelta.addSelection(iElementID, targetType);
				return;
			}
		}

		// // TODO: investigate
		// throw new CaleydoRuntimeException(
		// "SelectionManager: element to be removed does not exist",
		// CaleydoRuntimeExceptionType.VIEW);
	}

	/**
	 * Same as {@link #addToType(ESelectionType, int)} but for a list
	 * 
	 * @param targetType the selection type the element should be added to
	 * @param idCollection collection of element ids
	 * @throws CaleydoRuntimeException if the element is not in the selection
	 *             manager
	 */
	public void addToType(ESelectionType targetType, Collection<Integer> idCollection)
	{
		for (int value : idCollection)
		{
			addToType(targetType, value);
		}
	}

	/**
	 * Removes a element form a particular selection type and puts it into the
	 * normal type. Can not be called on the normal type. Nothing happens if the
	 * element is not contained in the type
	 * 
	 * @param eSelectionType
	 * @param iElementID
	 * @throws IllegalArgumentException if called with the normal type
	 */
	public void removeFromType(ESelectionType eSelectionType, int iElementID)
	{
		if (eSelectionType == eNormalType || eSelectionType == ESelectionType.REMOVE)
			throw new IllegalArgumentException(
					"SelectionManager: cannot remove from normal or remove selection");

		if (hashSelectionTypes.get(eSelectionType).containsKey(iElementID))
		{
			hashSelectionTypes.get(eSelectionType).remove(iElementID);
			hashSelectionTypes.get(eNormalType).put(iElementID, null);
			selectionDelta.addSelection(iElementID, eNormalType);
		}
	}

	/**
	 * Move all element from one type to another
	 * 
	 * @param srcType the source type
	 * @param targetType the target type
	 * @throws IllegalArgumentException when called with
	 *             {@link ESelectionType#REMOVE}
	 */
	public void moveType(ESelectionType srcType, ESelectionType targetType)
	{
		if (srcType == ESelectionType.REMOVE)
			throw new IllegalArgumentException("Can not move from REMOVE type");

		HashMap<Integer, Boolean> tempHash = hashSelectionTypes.remove(srcType);
		for (Integer value : tempHash.keySet())
		{
			selectionDelta.addSelection(value, targetType);
			virtualArray.removeByElement(value);
		}
		if (targetType != ESelectionType.REMOVE)
			hashSelectionTypes.get(targetType).putAll(tempHash);

		hashSelectionTypes.put(srcType, new HashMap<Integer, Boolean>());
	}

	/**
	 * Get the number of elements in the selection manager
	 * 
	 * @return the number of elements
	 */
	public int getNumberOfElements()
	{
		int iNumElements = 0;
		for (ESelectionType selectionType : hashSelectionTypes.keySet())
		{
			iNumElements += hashSelectionTypes.get(selectionType).size();
		}
		return iNumElements;
	}

	/**
	 * Get the number of elements in a particular selection
	 * 
	 * @param eSelectionType the selection type of interest
	 * @return the number of element in this selection
	 */
	public int getNumberOfElements(ESelectionType eSelectionType)
	{
		return hashSelectionTypes.get(eSelectionType).size();
	}

	/**
	 * Check whether a element is in a particular selection
	 * 
	 * @param eSelectionType the suspected selection type
	 * @param iElementID the id of the element
	 * @return true if the type contains the element, else false, also false
	 *         when called with REMOVE
	 */
	public boolean checkStatus(ESelectionType eSelectionType, int iElementID)
	{
		if (eSelectionType == ESelectionType.REMOVE)
			return false;

		if (hashSelectionTypes.get(eSelectionType).containsKey(iElementID))
			return true;

		return false;
	}

	/**
	 * Check whether an element is in any selection
	 * 
	 * @param iElementID the element id
	 * @return true if the element exists in the selection manager, else false
	 */
	public boolean checkStatus(int iElementID)
	{
		for (ESelectionType type : alSelectionTypes)
		{
			if (checkStatus(type, iElementID))
				return true;
		}

		return false;
	}

	/**
	 * Returns the accumulated selection delta since the last getDelta and
	 * clears the internal selectionDelta.
	 * 
	 * @return the SelectionDelta
	 */
	public ISelectionDelta getDelta()
	{
		ISelectionDelta returnDelta;
		if (internalToExternalMapping == null)
			returnDelta = selectionDelta.clone();
		else
		{
			returnDelta = new SelectionDelta(externalIDType, internalIDType);
			for (SelectionItem item : selectionDelta)
			{
				Integer iExternalID = GeneralManager.get().getGenomeIdManager().getID(
						internalToExternalMapping, item.getSelectionID());
				if (iExternalID == null || iExternalID == -1)
				{
					GeneralManager.get().getLogger().log(Level.WARNING,
							"No external ID for " + item.getSelectionID());
					continue;
				}

				returnDelta.addSelection(iExternalID, item.getSelectionType(), item
						.getSelectionID());
			}
		}

		selectionDelta = new SelectionDelta(internalIDType);

		return returnDelta;
	}

	/**
	 * Provides a selection delta that contains all elements in the view, with
	 * the appropriate external and internal selection IDs
	 * 
	 * @return the SelectionDelta containing all entries in the selection
	 *         manager
	 */
	public SelectionDelta getCompleteDelta()
	{
		SelectionDelta tempDelta = new SelectionDelta(externalIDType, internalIDType);
		HashMap<Integer, Boolean> tempHash;
		for (ESelectionType selectionType : alSelectionTypes)
		{
			tempHash = hashSelectionTypes.get(selectionType);
			for (Integer iElement : tempHash.keySet())
			{
				Integer iExternalID = GeneralManager.get().getGenomeIdManager().getID(
						internalToExternalMapping, iElement);
				if (iExternalID == null || iExternalID == -1)
				{
					GeneralManager.get().getLogger().log(Level.WARNING,
							"No external ID for " + iElement);
					continue;
				}
				tempDelta.addSelection(iExternalID, selectionType, iElement);
			}
		}
		return tempDelta;
	}

	/**
	 * <p>
	 * Merge an external selection delta into the local selection, and return a
	 * possibly converted selection
	 * </p>
	 * <p>
	 * This method takes into account data mapping, when mapping types are set
	 * </p>
	 * <p>
	 * When an element in the selectionDelta is not contained in the selection
	 * manager it is added and then moved to the appropriate type
	 * </p>
	 * <p>
	 * If a virtual array is set, the virtual array is also modified if
	 * necessary. This is the case when new element which are not contained in
	 * the virtual array are added or when elements are removed.
	 * </p>
	 * 
	 * @param selectionDelta the selection delta
	 * @return a ISelectionDelta that contains the internal ID of the manager as
	 *         its primary ID
	 */
	public ISelectionDelta setDelta(ISelectionDelta selectionDelta)
	{
		if (externalToInternalMapping != null)
		{
			ISelectionDelta externalSelectionDelta = selectionDelta;
			selectionDelta = new SelectionDelta(internalIDType);
			for (SelectionItem item : externalSelectionDelta)
			{
				Integer iInternalID = GeneralManager.get().getGenomeIdManager().getID(
						externalToInternalMapping, item.getSelectionID());
				if (iInternalID == null || iInternalID == -1)
				{
					GeneralManager.get().getLogger().log(Level.WARNING,
							"No internal id for " + item.getSelectionID());

					continue;
				}

				if (!checkStatus(iInternalID))
				{
					if (item.getSelectionType() == ESelectionType.ADD)
					{
						initialAdd(iInternalID);

						if (virtualArray != null)
							virtualArray.add(iInternalID);
					}
					continue;
				}
				else
				{
					if (item.getSelectionType() == ESelectionType.REMOVE
							&& virtualArray != null)
						virtualArray.removeByElement(iInternalID);
				}

				// caution, here we expect that the id that is used for
				// connections is stored in the internal id of the
				// externalSelectionDelta
				if (item.getInternalID() != -1 && item.getInternalID() != iInternalID)
					selectionDelta.addSelection(iInternalID, item.getSelectionType(), item
							.getInternalID());
				else
					selectionDelta.addSelection(iInternalID, item.getSelectionType(), item
							.getSelectionID());
			}
		}

		bIsDeltaWritingEnabled = false;
		for (SelectionItem selection : selectionDelta)
		{
			ESelectionType type = selection.getSelectionType();
			// if types are not contained in the current list we ignore them
			if (!alSelectionTypes.contains(type))
				continue;
			addToType(type, selection.getSelectionID());

		}
		bIsDeltaWritingEnabled = true;

		return selectionDelta;
	}
}
