package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.general.GeneralManager;

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
	private EnumMap<ESelectionType, HashMap<Integer, Integer>> hashSelectionTypes;

	private HashMap<Integer, ArrayList<Integer>> hashConnectionToElementID;

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
		 * EViewInternalSelectionTypPancreatic cancer (KEGG)e.NORMAL, which is
		 * the default
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
				alSelectionTypes.add(selectionType);
			}
		}

		hashSelectionTypes = new EnumMap<ESelectionType, HashMap<Integer, Integer>>(
				ESelectionType.class);
		hashConnectionToElementID = new HashMap<Integer, ArrayList<Integer>>();

		selectionDelta = new SelectionDelta(internalIDType);

		for (ESelectionType eType : alSelectionTypes)
		{
			hashSelectionTypes.put(eType, new HashMap<Integer, Integer>());
		}

	}

/**
	 * Initialize by adding the elements one by one. No delta writing. Do this only in the initialization phase.
	 * Use {@link #add(int) later.
	 * 
	 * @param iElementID
	 */
	public void initialAdd(int iElementID)
	{
		hashSelectionTypes.get(eNormalType).put(iElementID, 1);
	}

	/**
	 * Initialize by adding the elements all at once. No delta writing.
	 * 
	 * @param iAlElementIDs
	 */
	public void initialAdd(ArrayList<Integer> iAlElementIDs)
	{
		for (Integer iElementID : iAlElementIDs)
		{
			hashSelectionTypes.get(eNormalType).put(iElementID, 1);
		}
	}

	/**
	 * Use this to add elements at run-time
	 * 
	 * @param iElementID
	 */
	public void add(int iElementID)
	{
		for (ESelectionType selectionType : alSelectionTypes)
		{
			if (checkStatus(selectionType, iElementID))
			{
				int iNumTimesAdded = hashSelectionTypes.get(selectionType).get(iElementID);
				hashSelectionTypes.get(selectionType).put(iElementID, ++iNumTimesAdded);
				return;
			}
		}
		initialAdd(iElementID);
	}

	/**
	 * Removes a particular element from the selection manager, no matter what
	 * the type
	 * 
	 * @param iElementID the element to be removed
	 */
	public void remove(int iElementID, boolean bWriteVA)
	{
		for (ESelectionType selectionType : alSelectionTypes)
		{
			if (checkStatus(selectionType, iElementID))
			{
				int iNumTimesAdded = hashSelectionTypes.get(selectionType).get(iElementID) - 1;
				if (iNumTimesAdded == 0)
				{
					hashSelectionTypes.get(selectionType).remove(iElementID);
					if(bWriteVA)
						virtualArray.removeByElement(iElementID);
				}
				else
					hashSelectionTypes.get(selectionType).put(iElementID, iNumTimesAdded);

				return;
			}
		}
	}

	/**
	 * Removes all elements of a particular type from the selection manager
	 * 
	 * @param type
	 */
	public void removeElements(ESelectionType type)
	{
		HashMap<Integer, Integer> elementMap = hashSelectionTypes.get(type);
		Integer[] tempAr = new Integer[elementMap.size()];
		tempAr = elementMap.keySet().toArray(tempAr);
		for (Integer element : tempAr )
		{
			remove(element, true);
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
	 * Removes all elements and sets the element counter to 0 Removes all
	 * elements in selectionDelta Sets virtual array to null
	 */
	public void resetSelectionManager()
	{
		hashSelectionTypes.clear();
		for (ESelectionType eType : alSelectionTypes)
		{
			hashSelectionTypes.put(eType, new HashMap<Integer, Integer>());
		}
		if (virtualArray != null)
			virtualArray.clear();
		selectionDelta = new SelectionDelta(internalIDType);
	}

	/**
	 * All selections are written into the "normal" type. Delta is cleared.
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
		selectionDelta = new SelectionDelta(internalIDType);
	}

	/**
	 * Clear one specific selection type. The elements contained in that type
	 * are added to the "normal" type.
	 * 
	 * This is also reflected in the delta
	 * 
	 * @param sSelectionType the selection type to be cleared
	 */
	public void clearSelection(ESelectionType eSelectionType)
	{
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
			removeConnectionForElementID(iSelectionID);
		}

		hashSelectionTypes.get(eNormalType).putAll(hashSelectionTypes.get(eSelectionType));
		hashSelectionTypes.get(eSelectionType).clear();
	}

	/**
	 * Returns all elements that are in a specific selection type
	 * 
	 * @param sSelectionType
	 * @return the elements in the type. Null if the type does not exist yet.
	 * @throws IllegalArgumentException when called with
	 *             {@link ESelectionType#REMOVE} or {@link ESelectionType#ADD}
	 */
	public Set<Integer> getElements(ESelectionType eSelectionType)
	{
		if (hashSelectionTypes.containsKey(eSelectionType))
			return hashSelectionTypes.get(eSelectionType).keySet();

		return null;
	}

	/**
	 * Returns all elements contained in the selection manager
	 * 
	 * @return
	 */
	public Set<Integer> getAllElements()
	{
		Set<Integer> allElements = new HashSet<Integer>();
		for (HashMap<Integer, Integer> elementMap : hashSelectionTypes.values())
		{
			allElements.addAll(elementMap.keySet());
		}

		return allElements;
	}

	/**
	 * Add a element to a particular selection type. The element must exist in
	 * the selection manager. If the element is already in the target type
	 * nothing is done. If it is in another type the element is removed from the
	 * other type and moved to the target type
	 * 
	 * @param targetType the selection type the element should be added to
	 * @param iElementID the id of the element
	 */
	public void addToType(ESelectionType targetType, int iElementID)
	{
		// check whether the type is storable
		if (!isStorableType(targetType))
		{
			throw new IllegalArgumentException("Illegal selection type: " + targetType);
		}
		// return if already in the target type
		if (isStorableType(targetType)
				&& hashSelectionTypes.get(targetType).containsKey(iElementID))
			return;

		// if (!isConnectedType(targetType))
		// {
		removeConnectionForElementID(iElementID);
		// }

		for (ESelectionType currentType : alSelectionTypes)
		{
			// TODO: Please Alex check if the the second if statement is valid in all cases
			if (currentType == targetType
					|| (currentType == ESelectionType.SELECTION && targetType == ESelectionType.MOUSE_OVER))
			{
				continue;
			}

			if (hashSelectionTypes.get(currentType).containsKey(iElementID))
			{
				Integer iNumTimesAdded = hashSelectionTypes.get(currentType)
						.remove(iElementID);

				hashSelectionTypes.get(targetType).put(iElementID, iNumTimesAdded);

				// not sure whether we should add remove here if iNumTimesAdded
				// is > 0
				if (bIsDeltaWritingEnabled)
					selectionDelta.addSelection(iElementID, targetType);
				return;
			}
		}
		// System.out.println("Pathways mishandle GenericSelectionManager");
		// // TODO: investigate
		// throw new IllegalArgumentException(
		// "SelectionManager: element to be removed does not exist");
	}

	/**
	 * Same as {@link #addToType(ESelectionType, int)} but for a list
	 * 
	 * @param targetType the selection type the element should be added to
	 * @param idCollection collection of element ids
	 * @throws IllegalArgumentException if the element is not in the selection
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
	 * @throws IllegalArgumentException if called with the normal type, REMOVE
	 *             or ADD
	 */
	public void removeFromType(ESelectionType eSelectionType, int iElementID)
	{
		if (!isStorableType(eSelectionType) || eSelectionType == eNormalType)
			throw new IllegalArgumentException(
					"SelectionManager: cannot remove from normal or remove selection");

		if (hashSelectionTypes.get(eSelectionType).containsKey(iElementID))
		{
			Integer iNumTimesAdded = hashSelectionTypes.get(eSelectionType).remove(iElementID);
			hashSelectionTypes.get(eNormalType).put(iElementID, iNumTimesAdded);
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
		// storable types and remove are allowed here
		if (!isStorableType(targetType) || !isStorableType(srcType))
		{
			throw new IllegalArgumentException("Illegal Type " + targetType + "for targetType");
		}

		HashMap<Integer, Integer> tempHash = hashSelectionTypes.remove(srcType);
		for (Integer value : tempHash.keySet())
		{
			selectionDelta.addSelection(value, targetType);
		}

		hashSelectionTypes.get(targetType).putAll(tempHash);

		hashSelectionTypes.put(srcType, new HashMap<Integer, Integer>());
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
		if (!isStorableType(eSelectionType))
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
	 * If there is a mapping specified, the returned selection id is of the
	 * external type and the internal id of the interal.
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
			for (SelectionDeltaItem item : selectionDelta)
			{
				Integer iExternalID = GeneralManager.get().getIDMappingManager().getID(
						internalToExternalMapping, item.getPrimaryID());
				if (iExternalID == null || iExternalID == -1)
				{
//					GeneralManager.get().getLogger().log(Level.WARNING,
//							"No external ID for " + item.getPrimaryID());
					continue;
				}

				SelectionDeltaItem newItem = returnDelta.addSelection(iExternalID, item
						.getSelectionType(), item.getPrimaryID());
				for (Integer iConnectionID : item.getConnectionID())
				{
					newItem.setConnectionID(iConnectionID);
				}
			}
		}

		selectionDelta = new SelectionDelta(internalIDType);

		// int iCount = 0;
		// for (SelectionDeltaItem item : returnDelta)
		// {
		// System.out.println("Number: " + iCount++ + " ID: " +
		// item.getSelectionID()
		// + " Type: " + item.getSelectionType() + " HashCode: " +
		// item.hashCode());
		// }
		return returnDelta;
	}

	/**
	 * Creates a delta, based on the original delta, which contains an addition
	 * ADD for every changed element which is not removed.
	 * 
	 * @return the selection delta with ADD for all not REMOVE types
	 */
	// public ISelectionDelta getBroadcastDelta()
	// {
	// ISelectionDelta originalDelta = getDelta();
	// ISelectionDelta newDelta = new SelectionDelta(originalDelta.getIDType());
	//
	// for (SelectionDeltaItem item : originalDelta)
	// {
	//
	// if (item.getSelectionType() == ESelectionType.REMOVE)
	// newDelta.addSelection(item.getSelectionID(), ESelectionType.REMOVE);
	// else
	// {
	// newDelta.addSelection(item.getSelectionID(), ESelectionType.ADD);
	// newDelta.addSelection(item.getSelectionID(), item.getSelectionType());
	// }
	// }
	//
	// return newDelta;
	// }
	/**
	 * Provides a selection delta that contains all elements in the view, with
	 * the appropriate external and internal selection IDs.
	 * 
	 * Clears the selection delta
	 * 
	 * @return the SelectionDelta containing all entries in the selection
	 *         manager
	 */
	@Deprecated
	public SelectionDelta getCompleteDelta()
	{
		SelectionDelta tempDelta = new SelectionDelta(externalIDType, internalIDType);
		HashMap<Integer, Integer> tempHash;
		for (ESelectionType selectionType : alSelectionTypes)
		{
			tempHash = hashSelectionTypes.get(selectionType);
			for (Integer iElement : tempHash.keySet())
			{
				Integer iSelectionID = -1;
				if (externalToInternalMapping != null)
				{
					iSelectionID = GeneralManager.get().getIDMappingManager().getID(
							internalToExternalMapping, iElement);
					if (iSelectionID == null || iSelectionID == -1)
					{
//						GeneralManager.get().getLogger().log(Level.WARNING,
//								"No external ID for " + iElement);
						continue;
					}
				}
				else
				{
					iSelectionID = iElement;
				}
				tempDelta.addSelection(iSelectionID, selectionType, iElement);
				// connection ids
				if (isConnectedType(selectionType))
				{
					for (Integer iConnectionID : getConnectionForElementID(iElement))
					{
						tempDelta.addConnectionID(iSelectionID, iConnectionID);
					}
				}

			}
		}

		selectionDelta = new SelectionDelta(internalIDType);
		return tempDelta;
	}

	/**
	 * Creates a delta for a virtual array containing all element of the manager
	 * intended for broadcasts. The type of the delta is
	 * {@link EVAOperation#APPEND_UNIQUE}
	 * 
	 * @return the delta containing all elements
	 */
	public VirtualArrayDelta getBroadcastVADelta()
	{
		EIDType idType = externalIDType;
		if (idType == null)
		{
			idType = internalIDType;
		}
		VirtualArrayDelta tempDelta = new VirtualArrayDelta(idType);
		HashMap<Integer, Integer> tempHash;
		for (ESelectionType selectionType : alSelectionTypes)
		{
			if(selectionType == ESelectionType.DESELECTED)
				continue;
			tempHash = hashSelectionTypes.get(selectionType);
			for (Integer iElement : tempHash.keySet())
			{
				Integer iSelectionID = -1;
				if (externalToInternalMapping != null)
				{
					iSelectionID = GeneralManager.get().getIDMappingManager().getID(
							internalToExternalMapping, iElement);
					if (iSelectionID == null || iSelectionID == -1)
					{
//						GeneralManager.get().getLogger().log(Level.WARNING,
//								"No external ID for " + iElement);
						continue;
					}
				}
				else
				{
					iSelectionID = iElement;
				}
				tempDelta.add(VADeltaItem.appendUnique(iSelectionID));

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
	public void setDelta(ISelectionDelta selectionDelta)
	{
		bIsDeltaWritingEnabled = false;
		for (SelectionDeltaItem item : selectionDelta)
		{

			if (selectionDelta.getIDType() == internalIDType)
			{
				int iSelectionID = 0;
				iSelectionID = item.getPrimaryID();

				if (iSelectionID == -1)
				{
					GeneralManager.get().getLogger().log(Level.WARNING,
							"No internal id for " + item.getPrimaryID());

					continue;
				}

				addToType(item.getSelectionType(), iSelectionID);

				if (isConnectedType(item.getSelectionType()))
				{
					for (Integer iConnectionID : item.getConnectionID())
					{
						addConnectionID(iConnectionID, iSelectionID);
					}
				}
			}
			else
			{
				// TODO check whether external id type is ok

				Set<Integer> iTmpSetID = convertExternalToInternal(item.getPrimaryID());

				if (iTmpSetID == null)
					continue;

				for (Integer iTmpSelectionID : iTmpSetID)
				{
					if (iTmpSelectionID == null || iTmpSelectionID == -1)
					{
						GeneralManager.get().getLogger().log(Level.WARNING,
								"No internal id for " + item.getPrimaryID());

						continue;
					}

					addToType(item.getSelectionType(), iTmpSelectionID);

					if (isConnectedType(item.getSelectionType()))
					{
						for (Integer iConnectionID : item.getConnectionID())
						{
							addConnectionID(iConnectionID, iTmpSelectionID);
						}
					}

				}
			}

		}

		bIsDeltaWritingEnabled = true;

	}

	/**
	 * Set a virtual array delta to reflect changes to be made due to VA
	 * operations in the selection manager. When the virtual array is managed by
	 * the selection manager the delta is also applied to the virtual array.
	 * 
	 * @param delta the delta containing the changes
	 */
	public void setVADelta(IVirtualArrayDelta delta)
	{
		if (virtualArray == null)
		{
			return;
		}
		if (delta.getIDType() == internalIDType)
		{
			
			for (VADeltaItem item : delta)
			{
				// TODO mapping stuff
				switch (item.getType())
				{
					case ADD:
						add(item.getPrimaryID());
						break;
					case APPEND:
					case APPEND_UNIQUE:
						add(item.getPrimaryID());
						break;
					case REMOVE_ELEMENT:
						remove(item.getPrimaryID(), false);
						break;
					case REMOVE:
						remove(virtualArray.get(item.getIndex()), false);
						break;

				}
			}
			virtualArray.setDelta(delta);

		}

	}

	private boolean isStorableType(ESelectionType selectionType)
	{
		for (ESelectionType validType : alSelectionTypes)
		{
			if (validType == selectionType)
				return true;
		}

		return false;
	}

	private boolean isConnectedType(ESelectionType selectionType)
	{
		if (selectionType == ESelectionType.MOUSE_OVER
				|| selectionType == ESelectionType.SELECTION)
			return true;

		return false;
	}

	private Set<Integer> convertExternalToInternal(Integer iSelectionID)
	{
		if (externalToInternalMapping.isMultiMap())
		{
			return GeneralManager.get().getIDMappingManager().getMultiID(
					externalToInternalMapping, iSelectionID);
		}

		Set<Integer> iSetID = new HashSet<Integer>();
		iSetID.add((Integer) GeneralManager.get().getIDMappingManager().getID(
				externalToInternalMapping, iSelectionID));
		return iSetID;

	}

	/**
	 * Execeutes certain commands, as specified in a {@link SelectionCommand}.
	 * Typical examples are to clear a particular selection.
	 * 
	 * @param colSelectionCommand a container with selection commands
	 */
	public void executeSelectionCommands(Collection<SelectionCommand> colSelectionCommand)
	{
		if (colSelectionCommand == null)
			return;

		for (SelectionCommand command : colSelectionCommand)
		{
			ESelectionCommandType commandType = command.getSelectionCommandType();
			switch (commandType)
			{
				case CLEAR:
					clearSelection(command.getSelectionType());
					break;
				case CLEAR_ALL:
					clearSelections();
					break;
				case RESET:
					resetSelectionManager();
					break;
			}
		}
	}

	/**
	 * Adds a connection id which is used for connection line bundling to a
	 * particular selection
	 * 
	 * @param iConnectionID the connection id used by the connection line
	 *            manager
	 * @param iSelectionID the selection id which has to be already stored in
	 *            the manager
	 */
	public void addConnectionID(int iConnectionID, int iSelectionID)
	{
		if (!hashConnectionToElementID.containsKey(iConnectionID))
		{
			hashConnectionToElementID.put(iConnectionID, new ArrayList<Integer>());
		}

		hashConnectionToElementID.get(iConnectionID).add(iSelectionID);

		for (SelectionDeltaItem item : selectionDelta)
		{
			if (item.getPrimaryID() == iSelectionID)
			{
				item.setConnectionID(iConnectionID);
			}
		}
	}

	/**
	 * Remove a connection ID from the manager
	 * 
	 * @param iConnectionID the connection ID
	 */
	public void clearConnectionID(int iConnectionID)
	{
		hashConnectionToElementID.remove(iConnectionID);
	}

	/**
	 * Returns a collection of connection ID for one element.
	 * 
	 * @param iElementID
	 * @return the collection ids. Collection is empty if no connection ids are
	 *         found.
	 */
	public Collection<Integer> getConnectionForElementID(int iElementID)
	{
		Collection<Integer> colConnectionIDs = new ArrayList<Integer>();
		for (Integer iConnectionID : hashConnectionToElementID.keySet())
		{
			ArrayList<Integer> alElementIDs = hashConnectionToElementID.get(iConnectionID);
			for (int iCurrentID : alElementIDs)
			{
				if (iCurrentID == iElementID)
					colConnectionIDs.add(iConnectionID);
			}
		}
		return colConnectionIDs;
	}

	private void removeConnectionForElementID(int iElementID)
	{
		for (int iConnectionID : getConnectionForElementID(iElementID))
		{
			hashConnectionToElementID.remove(iConnectionID);
		}
	}
}
