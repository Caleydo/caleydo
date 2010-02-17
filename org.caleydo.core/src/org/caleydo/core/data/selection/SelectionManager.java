package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * Manages selections generically by storing them in hash maps. The manager can handle an arbitrary number of
 * selection types, which have to be defined in {@link SelectionType} A normal type, by default NORMAL in the
 * aforementioned enum is considered to be the base type, where all elements are initially added to.
 * </p>
 * <p>
 * Use the Builder to create an instance and specify optional parameters.
 * </p>
 * <p>
 * The selection manager always keeps a {@link SelectionDelta}, which can be used to communicate changes in
 * the selection to other views. This is reset every time you call it's getter.
 * </p>
 * <p>
 * Consequently it can also merge external deltas into its own selection.
 * </p>
 * <p>
 * The manager can operate on a subset of the possible types in {@link SelectionType}, by specifying a list of
 * allowed types in the {@link Builder}. When a selection delta is merged into the manager that contains
 * values that are not specified in the list of allowed values, the selections are ignored
 * </p>
 * <p>
 * When using the manager on data that is also managed by a {@link IVirtualArray} set the currently active
 * virtual array so that the tow are kept synchronous.
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class SelectionManager
	implements IListenerOwner, Cloneable {

	private HashMap<SelectionType, HashMap<Integer, Integer>> hashSelectionTypes;

	private HashMap<Integer, ArrayList<Integer>> hashConnectionToElementID;

	private EIDType iDType;

	private ArrayList<SelectionType> selectionTypes;

	private SelectionDelta selectionDelta;

	private boolean bIsDeltaWritingEnabled = true;

	private IVirtualArray virtualArray;

	private SelectionTypeListener addSelectionTypeListener;

	/**
	 * Static Builder for GenericSelectionManager. Allows to handle various parameter configurations. Call new
	 * GenericSelectionManager.Builder(EIDType iDType).setOneVariabe().setOther().build()
	 * 
	 * @author Alexander Lex
	 */
	public static class Builder {
		private ArrayList<SelectionType> alSelectionTypes = null;
		private EIDType iDType = null;

		/**
		 * Constructor for Builder. Pass the ID type, of the type {@link EIDType}. The ID type is the type of
		 * ID the view is working with
		 * 
		 * @param iDType
		 *            the ID type
		 */
		public Builder(EIDType iDType) {
			this.iDType = iDType;
		}


		/**
		 * Set a list of selection types if you don't want to (or can) handle all selection types in your view
		 * 
		 * @param alSelectionTypes
		 *            the list of selection types
		 * @return the Builder, call another setter or build() when you're done
		 */
		public Builder selectionTypes(ArrayList<SelectionType> alSelectionTypes) {
			this.alSelectionTypes = alSelectionTypes;
			return this;
		}

		/**
		 * Call this method when you're done initializing, it will return the actual selection manager
		 * 
		 * @return the selection manager
		 */
		public SelectionManager build() {
			return new SelectionManager(this);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param builder
	 *            the builder
	 */
	private SelectionManager(Builder builder) {
		this.iDType = builder.iDType;
		if (builder.alSelectionTypes == null) {
			selectionTypes = new ArrayList<SelectionType>(SelectionType.getDefaultTypes());
		}

		hashSelectionTypes = new HashMap<SelectionType, HashMap<Integer, Integer>>();
		hashConnectionToElementID = new HashMap<Integer, ArrayList<Integer>>();

		selectionDelta = new SelectionDelta(iDType);

		for (SelectionType selectionType : selectionTypes) {
			if (selectionType != SelectionType.NORMAL)
				hashSelectionTypes.put(selectionType, new HashMap<Integer, Integer>());
		}
		registerEventListeners();

	}

	/**
	 * Returns the id type the storage is handling.
	 * 
	 * @return
	 */
	public EIDType getIDType() {
		return iDType;
	}

/**
	 * Initialize by adding the elements one by one. No delta writing. Do this only in the initialization phase.
	 * Use {@link #add(int) later.
	 * 
	 * @param iElementID
	 */
	// private void initialAdd(int iElementID) {
	// hashSelectionTypes.get(normalType).put(iElementID, 1);
	// }

	/**
	 * Initialize by adding the elements all at once. No delta writing.
	 * 
	 * @param iAlElementIDs
	 */
	// private void initialAdd(ArrayList<Integer> iAlElementIDs) {
	// for (Integer iElementID : iAlElementIDs) {
	// hashSelectionTypes.get(normalType).put(iElementID, 1);
	// }
	// }

	/**
	 * Use this to add elements at run-time
	 * 
	 * @param iElementID
	 */
	// private void add(int iElementID) {
	// for (SelectionType selectionType : alSelectionTypes) {
	// if (checkStatus(selectionType, iElementID)) {
	// int iNumTimesAdded = hashSelectionTypes.get(selectionType).get(iElementID);
	// hashSelectionTypes.get(selectionType).put(iElementID, ++iNumTimesAdded);
	// return;
	// }
	// }
	// initialAdd(iElementID);
	// }

	/**
	 * Removes a particular element from the selection manager, no matter what the type
	 * 
	 * @param iElementID
	 *            the element to be removed
	 */
	public void remove(int iElementID, boolean bWriteVA) {

		for (SelectionType selectionType : selectionTypes) {
			if (checkStatus(selectionType, iElementID)) {

				int iNumTimesAdded = hashSelectionTypes.get(selectionType).get(iElementID) - 1;
				if (iNumTimesAdded == 0) {
					hashSelectionTypes.get(selectionType).remove(iElementID);
					if (bWriteVA) {
						virtualArray.removeByElement(iElementID);
						// for (Integer removedIndex : removedIndices) {
						// vaDeltaItem = VADeltaItem.remove(removedIndex);
						// vaDelta.add(vaDeltaItem);
						// }
					}
				}
				else {
					hashSelectionTypes.get(selectionType).put(iElementID, iNumTimesAdded);
				}
				// since each element should exist only once we can break the loop here

			}
		}
	}

	/**
	 * Removes all elements of a particular type from the selection manager
	 * 
	 * @param type
	 *            the type of the selection which should be purged
	 * @return a VirtualArrayDelta containing all the indices which should be removed
	 */
	public void removeElements(SelectionType type) {
		HashMap<Integer, Integer> elementMap = hashSelectionTypes.get(type);
		Integer[] tempAr = new Integer[elementMap.size()];
		tempAr = elementMap.keySet().toArray(tempAr);

		for (Integer element : tempAr) {
			remove(element.intValue(), true);
		}

	}

	/**
	 * <p>
	 * Set a virtual array if the data you are managing with this selection manager is also managed by a
	 * virtual array.
	 * </p>
	 * <p>
	 * If you reset this virtual array at runtime the manager is completely resetted and reinitialized with
	 * the data of the virtual array
	 * </p>
	 * 
	 * @param virtualArray
	 *            the currently active virtual array
	 */
	public void setVA(IVirtualArray virtualArray) {
//		resetSelectionManager();
		// initialAdd(virtualArray.getIndexList());
		this.virtualArray = virtualArray;
	}

	/**
	 * Removes all elements and sets the element counter to 0 Removes all elements in selectionDelta. Clears
	 * the virtual array.
	 */
	public void resetSelectionManager() {
		hashSelectionTypes.clear();
		for (SelectionType eType : selectionTypes) {
			hashSelectionTypes.put(eType, new HashMap<Integer, Integer>());
		}
		if (virtualArray != null) {
			// null here?
			virtualArray.clear();
		}
		selectionDelta = new SelectionDelta(iDType);
	}

	/**
	 * All selections are written into the "normal" type. Delta is cleared.
	 */
	public void clearSelections() {
		bIsDeltaWritingEnabled = false;
		for (SelectionType type : selectionTypes) {
			if (type == SelectionType.NORMAL) {
				continue;
			}
			clearSelection(type);
		}
		bIsDeltaWritingEnabled = true;
		selectionDelta = new SelectionDelta(iDType);
	}

	/**
	 * Clear one specific selection type. The elements contained in that type are added to the "normal" type.
	 * This is also reflected in the delta
	 * 
	 * @param sSelectionType
	 *            the selection type to be cleared
	 */
	public void clearSelection(SelectionType selectionType) {
		if (selectionType == SelectionType.NORMAL)
			throw new IllegalArgumentException(
				"SelectionManager: cannot reset selections of normal selection");

		// TODO the first condition should not be necessary, investigate
		if (hashSelectionTypes.get(selectionType) == null || hashSelectionTypes.get(selectionType).isEmpty())
			return;

		for (int iSelectionID : hashSelectionTypes.get(selectionType).keySet()) {
			selectionDelta.removeSelection(iSelectionID, selectionType);
			removeConnectionForElementID(iSelectionID);
		}

		hashSelectionTypes.get(selectionType).clear();
	}

	/**
	 * Returns all elements that are in a specific selection type
	 * 
	 * @param sSelectionType
	 * @return the elements in the type. Null if the type does not exist yet.
	 * @throws IllegalArgumentException
	 *             when called with {@link SelectionType#REMOVE} or {@link SelectionType#ADD}
	 */
	public Set<Integer> getElements(SelectionType selectionType) {
		if (selectionType == SelectionType.NORMAL)
			throw new IllegalStateException("Normal types are not stored in the selection manager");
		if (hashSelectionTypes.containsKey(selectionType))
			return hashSelectionTypes.get(selectionType).keySet();

		return null;
	}

	/**
	 * Returns all elements contained in the selection manager. These are only the elements that are not of
	 * {@link SelectionType#NORMAL}.
	 * 
	 * @return
	 */
	@Deprecated
	public Set<Integer> getAllElements() {
		Set<Integer> allElements = new HashSet<Integer>();
		for (HashMap<Integer, Integer> elementMap : hashSelectionTypes.values()) {
			allElements.addAll(elementMap.keySet());
		}

		return allElements;
	}

	/**
	 * Add a element to a particular selection type. The element must exist in the selection manager. If the
	 * element is already in the target type nothing is done. If it is in another type the element is removed
	 * from the other type and moved to the target type
	 * 
	 * @param targetType
	 *            the selection type the element should be added to
	 * @param iElementID
	 *            the id of the element
	 */
	public void addToType(SelectionType targetType, int iElementID) {
		if (!hashSelectionTypes.containsKey(targetType))
			throw new IllegalArgumentException("The selection type " + targetType
				+ " is not registered with this selection manager.");
		if (targetType == SelectionType.NORMAL)
			return;
		// return if already in the target type
		if (hashSelectionTypes.get(targetType).containsKey(iElementID))
			return;

		// if (!isConnectedType(targetType))
		// {
		removeConnectionForElementID(iElementID);
		// }

		hashSelectionTypes.get(targetType).put(iElementID, 1);

		if (bIsDeltaWritingEnabled) {
			selectionDelta.addSelection(iElementID, targetType);
		}

		// for (SelectionType currentType : alSelectionTypes) {
		// // ignore if target == current, also MOUSE_OVEr does not override SELECTION
		// if (currentType == targetType || currentType == SelectionType.SELECTION
		// && targetType == SelectionType.MOUSE_OVER) {
		// continue;
		// }
		//
		// if (hashSelectionTypes.get(currentType).containsKey(iElementID)) {
		// Integer iNumTimesAdded = hashSelectionTypes.get(currentType).remove(iElementID);
		//
		// hashSelectionTypes.get(targetType).put(iElementID, iNumTimesAdded);
		//
		// // not sure whether we should add remove here if iNumTimesAdded
		// // is > 0
		// if (bIsDeltaWritingEnabled) {
		// selectionDelta.addSelection(iElementID, targetType);
		// }
		// return;
		// }
		// }
		// System.out.println("Pathways mishandle GenericSelectionManager");
		// // TODO: investigate
		// throw new IllegalArgumentException(
		// "SelectionManager: element to be removed does not exist");
	}

	/**
	 * Same as {@link #addToType(SelectionType, int)} but for a list
	 * 
	 * @param targetType
	 *            the selection type the element should be added to
	 * @param idCollection
	 *            collection of element ids
	 * @throws IllegalArgumentException
	 *             if the element is not in the selection manager
	 */
	public void addToType(SelectionType targetType, Collection<Integer> idCollection) {
		for (int value : idCollection) {
			addToType(targetType, value);
		}
	}

	/**
	 * Removes a element form a particular selection type and puts it into the normal type. Can not be called
	 * on the normal type. Nothing happens if the element is not contained in the type
	 * 
	 * @param selectionType
	 * @param elementID
	 * @throws IllegalArgumentException
	 *             if called with the normal type, REMOVE or ADD
	 */
	public void removeFromType(SelectionType selectionType, int elementID) {
		if (selectionType == SelectionType.NORMAL)
			throw new IllegalArgumentException(
				"SelectionManager: cannot remove from normal or remove selection");

		if (hashSelectionTypes.get(selectionType).containsKey(elementID)) {
			Integer iNumTimesAdded = hashSelectionTypes.get(selectionType).remove(elementID);
			// hashSelectionTypes.get(normalType).put(iElementID, iNumTimesAdded);
			selectionDelta.removeSelection(elementID, selectionType);
		}
	}

	/**
	 * Move all element from one type to another
	 * 
	 * @param srcType
	 *            the source type
	 * @param targetType
	 *            the target type
	 * @throws IllegalArgumentException
	 *             when called with {@link SelectionType#REMOVE}
	 */
	public void moveType(SelectionType srcType, SelectionType targetType) {
		HashMap<Integer, Integer> tempHash = hashSelectionTypes.remove(srcType);
		for (Integer value : tempHash.keySet()) {
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
	public int getNumberOfElements() {
		int iNumElements = 0;
		for (SelectionType selectionType : hashSelectionTypes.keySet()) {
			iNumElements += hashSelectionTypes.get(selectionType).size();
		}
		return iNumElements;
	}

	/**
	 * Get the number of elements in a particular selection
	 * 
	 * @param SelectionType
	 *            the selection type of interest
	 * @return the number of element in this selection
	 */
	public int getNumberOfElements(SelectionType SelectionType) {
		return hashSelectionTypes.get(SelectionType).size();
	}

	/**
	 * Check whether a element is in a particular selection
	 * 
	 * @param SelectionType
	 *            the suspected selection type
	 * @param iElementID
	 *            the id of the element
	 * @return true if the type contains the element, else false, also false when called with REMOVE
	 */
	public boolean checkStatus(SelectionType selectionType, int iElementID) {

		if (selectionType == SelectionType.NORMAL)
			return false;

		if (hashSelectionTypes.get(selectionType).containsKey(iElementID))
			return true;

		return false;
	}

	/**
	 * Check whether an element is in any selection
	 * 
	 * @param iElementID
	 *            the element id
	 * @return true if the element exists in the selection manager, else false
	 */
	public boolean checkStatus(int iElementID) {
		for (SelectionType type : selectionTypes) {
			if (checkStatus(type, iElementID))
				return true;
		}

		return false;
	}

	/**
	 * Returns the accumulated selection delta since the last getDelta and clears the internal selectionDelta.
	 * If there is a mapping specified, the returned selection id is of the external type and the internal id
	 * of the interal.
	 * 
	 * @return the SelectionDelta
	 */
	public SelectionDelta getDelta() {
		SelectionDelta returnDelta = selectionDelta;

		selectionDelta = new SelectionDelta(iDType);

		return returnDelta;
	}

	/**
	 * Provides a selection delta that contains all elements in the view, with the appropriate external and
	 * internal selection IDs. Clears the selection delta
	 * 
	 * @return the SelectionDelta containing all entries in the selection manager
	 */
	@Deprecated
	public SelectionDelta getCompleteDelta() {
		SelectionDelta tempDelta = new SelectionDelta(iDType);
		HashMap<Integer, Integer> tempHash;
		for (SelectionType selectionType : selectionTypes) {
			if(selectionType == SelectionType.NORMAL)
				continue;
			tempHash = hashSelectionTypes.get(selectionType);
			for (Integer iElement : tempHash.keySet()) {
				Integer iSelectionID = -1;

				iSelectionID = iElement;

				tempDelta.addSelection(iSelectionID, selectionType, iElement);
				// connection ids
				if (selectionType.isConnected()) {
					for (Integer iConnectionID : getConnectionForElementID(iElement)) {
						tempDelta.addConnectionID(iSelectionID, iConnectionID);
					}
				}

			}
		}

		selectionDelta = new SelectionDelta(iDType);
		return tempDelta;
	}

	/**
	 * Creates a delta for a virtual array containing all element of the manager intended for broadcasts. The
	 * type of the delta is {@link EVAOperation#APPEND_UNIQUE}
	 * 
	 * @return the delta containing all elements
	 */
	public VirtualArrayDelta getBroadcastVADelta() {
		EIDType idType = iDType;
		if (idType == null) {
			idType = iDType;
		}
		VirtualArrayDelta tempDelta = new VirtualArrayDelta(virtualArray.getVAType(), idType);
		HashMap<Integer, Integer> tempHash;
		for (SelectionType selectionType : selectionTypes) {
			if (!selectionType.isVisible()) {
				continue;
			}
			tempHash = hashSelectionTypes.get(selectionType);
			for (Integer iElement : tempHash.keySet()) {
				Integer iSelectionID = -1;

				iSelectionID = iElement;
				tempDelta.add(VADeltaItem.appendUnique(iSelectionID));

			}
		}

		return tempDelta;
	}

	/**
	 * <p>
	 * Merge an external selection delta into the local selection, and return a possibly converted selection
	 * </p>
	 * <p>
	 * This method takes into account data mapping, when mapping types are set
	 * </p>
	 * <p>
	 * When an element in the selectionDelta is not contained in the selection manager it is added and then
	 * moved to the appropriate type
	 * </p>
	 * <p>
	 * If a virtual array is set, the virtual array is also modified if necessary. This is the case when new
	 * element which are not contained in the virtual array are added or when elements are removed.
	 * </p>
	 * 
	 * @param selectionDelta
	 *            the selection delta
	 * @return a ISelectionDelta that contains the internal ID of the manager as its primary ID
	 */
	public void setDelta(ISelectionDelta selectionDelta) {
		bIsDeltaWritingEnabled = false;
		if (selectionDelta.getIDType() != iDType)
			selectionDelta = DeltaConverter.convertDelta(iDType, selectionDelta);
		for (SelectionDeltaItem item : selectionDelta) {

			// if (selectionDelta.getIDType() == internalIDType) {
			int selectionID = 0;
			selectionID = item.getPrimaryID();

			if (selectionID == -1) {
				GeneralManager.get().getLogger().log(
					new Status(IStatus.WARNING, IGeneralManager.PLUGIN_ID, "No internal id for "
						+ item.getPrimaryID()));

				continue;
			}

			if (!item.isRemove()) {
				addToType(item.getSelectionType(), selectionID);

				if (item.getSelectionType().isConnected()) {
					for (Integer iConnectionID : item.getConnectionIDs()) {
						addConnectionID(iConnectionID, selectionID);
					}
				}
			}
			else {
				removeFromType(item.getSelectionType(), selectionID);
			}
		}

		bIsDeltaWritingEnabled = true;

	}

	/**
	 * Set a virtual array delta to reflect changes to be made due to VA operations in the selection manager.
	 * When the virtual array is managed by the selection manager the delta is also applied to the virtual
	 * array.
	 * 
	 * @param delta
	 *            the delta containing the changes
	 */
	public void setVADelta(IVirtualArrayDelta delta) {
		if (virtualArray == null)
			return;
		if (delta.getIDType() == iDType) {

			for (VADeltaItem item : delta) {
				// TODO mapping stuff
				switch (item.getType()) {
					// case ADD:
					// add(item.getPrimaryID());
					// break;
					// case APPEND:
					// case APPEND_UNIQUE:
					// add(item.getPrimaryID());
					// break;
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

	/**
	 * Executes certain commands, as specified in a {@link SelectionCommand}. Typical examples are to clear a
	 * particular selection.
	 * 
	 * @param selectionCommand
	 *            a selection command
	 */
	public void executeSelectionCommand(SelectionCommand selectionCommand) {
		if (selectionCommand == null)
			return;

		ESelectionCommandType commandType = selectionCommand.getSelectionCommandType();
		switch (commandType) {
			case CLEAR:
				clearSelection(selectionCommand.getSelectionType());
				break;
			case CLEAR_ALL:
				clearSelections();
				break;
			case RESET:
				resetSelectionManager();
				break;
		}
	}

	/**
	 * Adds a connection id which is used for connection line bundling to a particular selection
	 * 
	 * @param iConnectionID
	 *            the connection id used by the connection line manager
	 * @param iSelectionID
	 *            the selection id which has to be already stored in the manager
	 */
	public void addConnectionID(int iConnectionID, int iSelectionID) {
		if (!hashConnectionToElementID.containsKey(iConnectionID)) {
			hashConnectionToElementID.put(iConnectionID, new ArrayList<Integer>());
		}

		hashConnectionToElementID.get(iConnectionID).add(iSelectionID);

		for (SelectionDeltaItem item : selectionDelta) {
			if (item.getPrimaryID() == iSelectionID) {
				item.addConnectionID(iConnectionID);
			}
		}
	}

	/**
	 * Remove a connection ID from the manager
	 * 
	 * @param iConnectionID
	 *            the connection ID
	 */
	public void clearConnectionID(int iConnectionID) {
		hashConnectionToElementID.remove(iConnectionID);
	}

	/**
	 * Returns a collection of connection ID for one element.
	 * 
	 * @param iElementID
	 * @return the collection ids. Collection is empty if no connection ids are found.
	 */
	public Collection<Integer> getConnectionForElementID(int iElementID) {
		Collection<Integer> colConnectionIDs = new ArrayList<Integer>();
		for (Integer iConnectionID : hashConnectionToElementID.keySet()) {
			ArrayList<Integer> alElementIDs = hashConnectionToElementID.get(iConnectionID);
			for (int iCurrentID : alElementIDs) {
				if (iCurrentID == iElementID) {
					colConnectionIDs.add(iConnectionID);
				}
			}
		}
		return colConnectionIDs;
	}

	private void removeConnectionForElementID(int iElementID) {
		for (int iConnectionID : getConnectionForElementID(iElementID)) {
			hashConnectionToElementID.remove(iConnectionID);
		}
	}

	/**
	 * Returns the {@link SelectionType} for a element ID or null, if element ID is not in the selection
	 * manager
	 * 
	 * @param elementID
	 * @return selection type or NULL
	 */
	public ArrayList<SelectionType> getSelectionTypes(int elementID) {
		ArrayList<SelectionType> selectedTypes = new ArrayList<SelectionType>(2);
		for (SelectionType type : selectionTypes) {
			if (checkStatus(type, elementID))
				selectedTypes.add(type);
		}
		if(selectedTypes.isEmpty())
			selectedTypes.add(SelectionType.NORMAL);
		return selectedTypes;
	}

	/** Returns a list of all currently registered selection types */
	public ArrayList<SelectionType> getSelectionTypes() {
		return selectionTypes;
	}

	/**
	 * TODO: check whether this is thread-save enough.
	 */
	@Override
	public synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		listener.handleEvent(event);
	}

	public void registerEventListeners() {

		addSelectionTypeListener = new SelectionTypeListener();

		addSelectionTypeListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(SelectionTypeEvent.class,
			addSelectionTypeListener);

	}

	public void unregisterEventListeners() {
		if (addSelectionTypeListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(addSelectionTypeListener);
			addSelectionTypeListener = null;
		}
	}

	public void addSelectionType(SelectionType selectionType) {
		hashSelectionTypes.put(selectionType, new HashMap<Integer, Integer>());
		selectionTypes.add(selectionType);
	}

	public void removeSelectionType(SelectionType selectionType) {
		hashSelectionTypes.remove(selectionType);
		selectionTypes.remove(selectionType);
	}

	@Override
	public String toString() {
		String result = "IDType: " + iDType + " ";
		for (SelectionType selectionType : hashSelectionTypes.keySet()) {
			result = result + "[" + selectionType + ": " + hashSelectionTypes.get(selectionType).size() + "]";
		}

		return result;
	}

	/**
	 * <p>
	 * Returns a semi-deep copy of the selection manager. That means that all containers have been cloned to
	 * be modifiable without affecting the source manager, however the elements themselves such as Integers or
	 * SelectionTypes are the same (this is necessary for SelectionTypes and should be fine for other values
	 * since they are not changed).
	 * </p>
	 * <p>
	 * Notice that the clone does not contain a virtual array! <b>It must be explicitly set by the
	 * receiver.</b>
	 * </p>
	 */
	@Override
	@SuppressWarnings("unchecked")
	public SelectionManager clone() {
		SelectionManager clone;
		try {
			clone = (SelectionManager) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Cloning error for SelectionManager: " + e.getMessage());
		}

		clone.registerEventListeners();
		// clone hashConnectionToElementID
		clone.hashConnectionToElementID =
			(HashMap<Integer, ArrayList<Integer>>) this.hashConnectionToElementID.clone();
		for (Integer id : clone.hashConnectionToElementID.keySet()) {
			clone.hashConnectionToElementID.put(id, (ArrayList<Integer>) this.hashConnectionToElementID.get(
				id).clone());
		}

		// clone hashSelectionTypes
		clone.hashSelectionTypes =
			(HashMap<SelectionType, HashMap<Integer, Integer>>) this.hashSelectionTypes.clone();
		for (SelectionType selectionType : clone.hashSelectionTypes.keySet()) {
			clone.hashSelectionTypes.put(selectionType, (HashMap<Integer, Integer>) this.hashSelectionTypes
				.get(selectionType).clone());
		}

		// clone selectionTypes
		clone.selectionTypes = (ArrayList<SelectionType>) this.selectionTypes.clone();

		// the selectionDelta is reset
		clone.selectionDelta = new SelectionDelta(iDType);
		// the virtual array needs to be set manually by the receiving instance
		clone.virtualArray = null;
		return clone;
	}
}
