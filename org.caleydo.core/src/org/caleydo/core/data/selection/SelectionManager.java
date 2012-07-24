/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.RemoveManagedSelectionTypesEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * <p>
 * Manages selection-id associations. The ids are specified as Integers, while
 * the selection type must be of {@link SelectionType}. While
 * <code>SelectionType</code> objects can be arbitrarily created, there are some
 * specially pre-created types (for details see {@link SelectionType}). Of
 * importance to the <code>SelectionManager</code> is
 * {@link SelectionType#NORMAL}. The <code>SelectionManager</code> stores only
 * not-normal types, i.e. it assumes that every ID is of state "normal" unless
 * otherwise specified. So when you call {@link #getSelectionTypes(int)} for an
 * identifier that is unknown to the <code>SelectionManager</code> it will
 * return a List of selection types containing a single "normal" entry.
 * </p>
 * <p>
 * The manager can handle an <b>arbitrary number of selection types</b> for
 * every id.
 * </p>
 * 
 * <p>
 * The <code>SelectionManager</code> always keeps a {@link SelectionDelta},
 * which can be used to <b>synchronize multiple <code>SelectionManager</code>
 * s</b>. The <code>SelectionDelta</code> is reset every time you call it's
 * getter ( {@link #getDelta()}).
 * </p>
 * <p>
 * A <code>SelectionManager</code> can be synchronized by setting a
 * <code>SelectionDelta</code> using {@link #setDelta(SelectionDelta)}. These
 * deltas have to be of the same {@link IDCategory}, so that they can be
 * resolved to match the {@link IDType} of the receiving
 * <code>SelectionManager</code>. If the <code>IDCategory</code> matches, the
 * set <code>SelectionDelta</code> is automatically resolved to the
 * {@link IDType} of the <code>SelectionManager</code>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class SelectionManager implements IListenerOwner, Cloneable {

	protected HashMap<SelectionType, HashMap<Integer, Integer>> hashSelectionTypes;
	/**
	 * Selection types that should not be included in deltas have to be listed
	 * in this structure
	 */
	private HashMap<SelectionType, Boolean> deltaBlackList;

	private HashMap<Integer, ArrayList<Integer>> hashConnectionToElementID;

	protected IDType idType;

	protected ArrayList<SelectionType> selectionTypes;

	/**
	 * The current type that is used for selections. By default this is the
	 * standard SELECTION type, but this can be changed for multi-colored
	 * brushing for example.
	 */
	protected SelectionType selectionType = SelectionType.SELECTION;

	private SelectionDelta selectionDelta;

	private boolean bIsDeltaWritingEnabled = true;

	private SelectionTypeListener addSelectionTypeListener;
	private RemoveManagedSelectionTypesListener removeManagedSelectionTypesListener;

	private IDMappingManager idMappingManager;

	/**
	 * Constructor
	 */
	public SelectionManager(IDType idType) {
		this.idType = idType;
		idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				idType.getIDCategory());
		selectionTypes = new ArrayList<SelectionType>(SelectionType.getDefaultTypes());

		hashSelectionTypes = new HashMap<SelectionType, HashMap<Integer, Integer>>();
		hashConnectionToElementID = new HashMap<Integer, ArrayList<Integer>>();
		deltaBlackList = new HashMap<SelectionType, Boolean>(2);

		selectionDelta = new SelectionDelta(idType);

		for (SelectionType selectionType : selectionTypes) {
			if (selectionType != SelectionType.NORMAL)
				hashSelectionTypes.put(selectionType, new HashMap<Integer, Integer>());
		}
		this.registerEventListeners();

	}

	/**
	 * Returns the id type the dimension is handling.
	 * 
	 * @return
	 */
	public IDType getIDType() {
		return idType;
	}

	/**
	 * Removes a particular element from the selection manager, no matter what
	 * the type
	 * 
	 * @param elementID
	 *            the element to be removed
	 */
	public synchronized void remove(int elementID) {

		for (SelectionType selectionType : selectionTypes) {
			if (checkStatus(selectionType, elementID)) {

				int iNumTimesAdded = hashSelectionTypes.get(selectionType).get(elementID) - 1;
				if (iNumTimesAdded == 0) {
					hashSelectionTypes.get(selectionType).remove(elementID);

				} else {
					hashSelectionTypes.get(selectionType).put(elementID, iNumTimesAdded);
				}
			}
		}
	}

	/**
	 * Removes all elements of a particular type from the selection manager
	 * 
	 * @param type
	 *            the type of the selection which should be purged
	 */
	public synchronized void removeElements(SelectionType type) {
		HashMap<Integer, Integer> elementMap = hashSelectionTypes.get(type);
		Integer[] tempAr = new Integer[elementMap.size()];
		tempAr = elementMap.keySet().toArray(tempAr);

		for (Integer element : tempAr) {
			remove(element.intValue());
		}
	}

	/**
	 * Removes all elements and sets the element counter to 0 Removes all
	 * elements in selectionDelta. Clears the virtual array.
	 */
	public synchronized void resetSelectionManager() {
		hashSelectionTypes.clear();
		for (SelectionType eType : selectionTypes) {
			hashSelectionTypes.put(eType, new HashMap<Integer, Integer>());
		}

		selectionDelta = new SelectionDelta(idType);
	}

	/**
	 * All selections are written into the "normal" type. Delta is cleared.
	 */
	public synchronized void clearSelections() {
		bIsDeltaWritingEnabled = false;
		for (SelectionType type : selectionTypes) {
			if (type == SelectionType.NORMAL) {
				continue;
			}
			clearSelection(type);
		}
		bIsDeltaWritingEnabled = true;
		selectionDelta = new SelectionDelta(idType);
	}

	/**
	 * Clear one specific selection type. The elements contained in that type
	 * are added to the "normal" type. This is also reflected in the delta
	 * 
	 * @param sSelectionType
	 *            the selection type to be cleared
	 */
	public synchronized void clearSelection(SelectionType selectionType) {
		if (selectionType == SelectionType.NORMAL)
			throw new IllegalArgumentException(
					"SelectionManager: cannot reset selections of normal selection");

		// TODO the first condition should not be necessary, investigate
		if (hashSelectionTypes.get(selectionType) == null
				|| hashSelectionTypes.get(selectionType).isEmpty())
			return;

		for (int iSelectionID : hashSelectionTypes.get(selectionType).keySet()) {
			if (!deltaBlackList.containsKey(selectionType))
				selectionDelta.removeSelection(iSelectionID, selectionType);
			removeConnectionForElementID(iSelectionID);
		}

		hashSelectionTypes.get(selectionType).clear();
	}

	/**
	 * Returns all elements that are in a specific selection type
	 * 
	 * @param sSelectionType
	 * @return the elements in the type. Null if the type does not exist yet or
	 *         if no elements are contained for this type. Note that no normal
	 *         types are contained at any time.
	 */
	public synchronized Set<Integer> getElements(SelectionType selectionType) {
		if (hashSelectionTypes.containsKey(selectionType))
			return hashSelectionTypes.get(selectionType).keySet();

		return null;
	}

	/**
	 * Returns all elements contained in the selection manager. These are only
	 * the elements that are not of {@link SelectionType#NORMAL}.
	 * 
	 * @return
	 */
	@Deprecated
	public synchronized Set<Integer> getAllElements() {
		Set<Integer> allElements = new HashSet<Integer>();
		for (HashMap<Integer, Integer> elementMap : hashSelectionTypes.values()) {
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
	 * @param targetType
	 *            the selection type the element should be added to
	 * @param id
	 *            the id of the element
	 */
	public synchronized void addToType(SelectionType targetType, int id) {
		if (!hashSelectionTypes.containsKey(targetType)) {
			addSelectionType(targetType);
		}
		// throw new IllegalArgumentException("The selection type " + targetType
		// + " is not registered with this selection manager.");
		if (targetType == SelectionType.NORMAL)
			return;
		// return if already in the target type
		if (hashSelectionTypes.get(targetType).containsKey(id))
			return;

		// if (!isConnectedType(targetType))
		// {
		removeConnectionForElementID(id);
		// }

		hashSelectionTypes.get(targetType).put(id, 1);

		if (bIsDeltaWritingEnabled && !deltaBlackList.containsKey(targetType)) {
			selectionDelta.addSelection(id, targetType);
		}

	}

	/**
	 * Same as {@link #addToType(SelectionType, int)} with an additional
	 * parameter sourceIDType that is used for conversion of IDs if necessary
	 * 
	 * @param targetType
	 * @param sourceIDType
	 * @param elementID
	 */
	public synchronized void addToType(SelectionType targetType, IDType sourceIDType,
			int id) {
		if (sourceIDType.equals(idType)) {
			addToType(targetType, id);
		} else {
			Set<Integer> convertedIDs = idMappingManager.getIDAsSet(sourceIDType, idType,
					id);
			if (convertedIDs != null)
				addToType(targetType, convertedIDs);
		}

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
	public synchronized void addToType(SelectionType targetType,
			Collection<Integer> idCollection) {
		for (int value : idCollection) {
			addToType(targetType, value);
		}
	}

	/**
	 * Same as {@link #addToType(SelectionType, IDType, int)} but for a
	 * collection of ids)
	 * 
	 * @param targetType
	 * @param sourceIDType
	 * @param idCollection
	 */
	public synchronized void addToType(SelectionType targetType, IDType sourceIDType,
			Collection<Integer> idCollection) {
		for (int value : idCollection) {
			addToType(targetType, sourceIDType, value);
		}
	}

	/**
	 * Removes a element form a particular selection type and puts it into the
	 * normal type. Can not be called on the normal type. Nothing happens if the
	 * element is not contained in the type
	 * 
	 * @param selectionType
	 * @param elementID
	 * @throws IllegalArgumentException
	 *             if called with the normal type, REMOVE or ADD
	 */
	public synchronized void removeFromType(SelectionType selectionType, int elementID) {
		if (!hashSelectionTypes.containsKey(selectionType)) {
			addSelectionType(selectionType);
		}
		if (selectionType == SelectionType.NORMAL)
			throw new IllegalArgumentException(
					"SelectionManager: cannot remove from normal or remove selection");

		if (hashSelectionTypes.get(selectionType).containsKey(elementID)) {
			hashSelectionTypes.get(selectionType).remove(elementID);
			if (!deltaBlackList.containsKey(selectionType))
				selectionDelta.removeSelection(elementID, selectionType);
		}
	}

	/**
	 * <p>
	 * Move all element from one type to another
	 * </p>
	 * <p>
	 * FIXME: has not been used for a long time, check before using
	 * </p>
	 * 
	 * @param srcType
	 *            the source type
	 * @param targetType
	 *            the target type
	 * @throws IllegalArgumentException
	 *             when called with {@link SelectionType#REMOVE}
	 */
	@Deprecated
	public synchronized void moveType(SelectionType srcType, SelectionType targetType) {
		HashMap<Integer, Integer> tempHash = hashSelectionTypes.remove(srcType);
		for (Integer value : tempHash.keySet()) {
			if (!deltaBlackList.containsKey(targetType))
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
	public synchronized int getNumberOfElements() {
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
	public synchronized int getNumberOfElements(SelectionType SelectionType) {
		HashMap<Integer, Integer> hashElements = hashSelectionTypes.get(SelectionType);
		if (hashElements != null)
			return hashElements.size();
		else
			return 0;
	}

	/**
	 * Check whether a element is in a particular selection
	 * 
	 * @param SelectionType
	 *            the suspected selection type
	 * @param elementID
	 *            the id of the element
	 * @return true if the type contains the element, else false, also false
	 *         when called with REMOVE
	 */
	public synchronized boolean checkStatus(SelectionType selectionType, int elementID) {

		if (selectionType == SelectionType.NORMAL)
			return false;

		if (!hashSelectionTypes.containsKey(selectionType))
			return false;

		if (hashSelectionTypes.get(selectionType).containsKey(elementID))
			return true;

		return false;
	}

	/**
	 * Check whether an element is in any selection
	 * 
	 * @param elementID
	 *            the element id
	 * @return true if the element exists in the selection manager, else false
	 */
	public synchronized boolean checkStatus(int elementID) {
		for (SelectionType type : selectionTypes) {
			if (checkStatus(type, elementID))
				return true;
		}

		return false;
	}

	/**
	 * Returns the accumulated selection delta since the last getDelta and
	 * clears the internal selectionDelta. If there is a mapping specified, the
	 * returned selection id is of the external type and the internal id of the
	 * interal.
	 * 
	 * @return the SelectionDelta
	 */
	public synchronized SelectionDelta getDelta() {
		SelectionDelta returnDelta = selectionDelta;

		selectionDelta = new SelectionDelta(idType);

		return returnDelta;
	}

	/**
	 * Provides a selection delta that contains all elements in the view, with
	 * the appropriate external and internal selection IDs. Clears the selection
	 * delta
	 * 
	 * @return the SelectionDelta containing all entries in the selection
	 *         manager
	 */
	@Deprecated
	public synchronized SelectionDelta getCompleteDelta() {
		SelectionDelta tempDelta = new SelectionDelta(idType);
		HashMap<Integer, Integer> tempHash;
		for (SelectionType selectionType : selectionTypes) {
			if (selectionType == SelectionType.NORMAL
					|| deltaBlackList.containsKey(selectionType))
				continue;
			tempHash = hashSelectionTypes.get(selectionType);
			for (Integer selectionID : tempHash.keySet()) {

				tempDelta.addSelection(selectionID, selectionType);
				// connection ids
				if (selectionType.isConnected()) {
					for (Integer iConnectionID : getConnectionForElementID(selectionID)) {
						tempDelta.addConnectionID(selectionID, iConnectionID);
					}
				}

			}
		}

		selectionDelta = new SelectionDelta(idType);
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
	 * @param selectionDelta
	 *            the selection delta
	 * @return a SelectionDelta that contains the internal ID of the manager as
	 *         its primary ID
	 */
	public synchronized void setDelta(SelectionDelta selectionDelta) {
		bIsDeltaWritingEnabled = false;
		if (selectionDelta.getIDType() != idType)
			selectionDelta = DeltaConverter.convertDelta(idMappingManager, idType,
					selectionDelta);
		for (SelectionDeltaItem item : selectionDelta) {

			// if (selectionDelta.getIDType() == internalIDType) {
			int selectionID = 0;
			selectionID = item.getID();

			if (selectionID == -1) {
				Logger.log(new Status(IStatus.WARNING, this.toString(),
						"No internal id for " + item.getID()));

				continue;
			}

			if (!item.isRemove()) {
				addToType(item.getSelectionType(), selectionID);

				if (item.getSelectionType().isConnected()) {
					for (Integer iConnectionID : item.getConnectionIDs()) {
						addConnectionID(iConnectionID, selectionID);
					}
				}
			} else {
				removeFromType(item.getSelectionType(), selectionID);
			}
		}
		bIsDeltaWritingEnabled = true;
	}

	/**
	 * Executes certain commands, as specified in a {@link SelectionCommand}.
	 * Typical examples are to clear a particular selection.
	 * 
	 * @param selectionCommand
	 *            a selection command
	 */
	public synchronized void executeSelectionCommand(SelectionCommand selectionCommand) {
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
	 * Adds a connection id which is used for connection line bundling to a
	 * particular selection
	 * 
	 * @param iConnectionID
	 *            the connection id used by the connection line manager
	 * @param iSelectionID
	 *            the selection id which has to be already stored in the manager
	 */
	public synchronized void addConnectionID(int iConnectionID, int iSelectionID) {
		if (!hashConnectionToElementID.containsKey(iConnectionID)) {
			hashConnectionToElementID.put(iConnectionID, new ArrayList<Integer>());
		}

		hashConnectionToElementID.get(iConnectionID).add(iSelectionID);

		for (SelectionDeltaItem item : selectionDelta) {
			if (item.getID() == iSelectionID) {
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
	public synchronized void clearConnectionID(int iConnectionID) {
		hashConnectionToElementID.remove(iConnectionID);
	}

	/**
	 * Returns a collection of connection ID for one element.
	 * 
	 * @param iElementID
	 * @return the collection ids. Collection is empty if no connection ids are
	 *         found.
	 */
	public synchronized Collection<Integer> getConnectionForElementID(int iElementID) {
		Collection<Integer> colConnectionIDs = new ArrayList<Integer>();
		for (Integer iConnectionID : hashConnectionToElementID.keySet()) {
			ArrayList<Integer> alElementIDs = hashConnectionToElementID
					.get(iConnectionID);
			for (int iCurrentID : alElementIDs) {
				if (iCurrentID == iElementID) {
					colConnectionIDs.add(iConnectionID);
				}
			}
		}
		return colConnectionIDs;
	}

	private synchronized void removeConnectionForElementID(int iElementID) {
		for (int iConnectionID : getConnectionForElementID(iElementID)) {
			hashConnectionToElementID.remove(iConnectionID);
		}
	}

	/**
	 * Returns the {@link SelectionType}s for an elementID. As if element ID is
	 * not in the selection manager
	 * <p>
	 * The <code>SelectionType</code>s are sorted by their Priority (
	 * {@link SelectionType#getPriority()})
	 * </p>
	 * 
	 * @param elementID
	 * @return selection type or NULL
	 */
	public synchronized ArrayList<SelectionType> getSelectionTypes(int elementID) {

		ArrayList<SelectionType> selectedTypes = new ArrayList<SelectionType>(2);
		for (SelectionType type : selectionTypes) {
			if (checkStatus(type, elementID))
				selectedTypes.add(type);
		}
		if (selectedTypes.isEmpty())
			selectedTypes.add(SelectionType.NORMAL);
		Collections.sort(selectedTypes);
		Collections.reverse(selectedTypes);
		return selectedTypes;
	}

	/**
	 * Same as {@link #getSelectionTypes(int)} but including a sourceIDType
	 * which is used to first convert the ID. If no id can be resolved an empty
	 * ArrayList is returned
	 * 
	 * @param sourceIDType
	 * @param elementID
	 * @return the List of selection types or an empty list if ID could not be
	 *         resolved
	 */
	public synchronized ArrayList<SelectionType> getSelectionTypes(IDType sourceIDType,
			int elementID) {

		Integer resolvedID = idMappingManager.getID(sourceIDType, this.idType, elementID);
		if (resolvedID == null)
			return new ArrayList<SelectionType>();

		return getSelectionTypes(resolvedID);
	}

	/**
	 * Returns a list of all currently registered selection types, sorted by
	 * their Priority ({@link SelectionType#getPriority()})
	 */
	public synchronized ArrayList<SelectionType> getSelectionTypes() {
		return selectionTypes;
	}

	/**
	 * Adds a new, custom selection type to the selection manager. Should only
	 * be called via a {@link SelectionTypeEvent}
	 */
	public synchronized void addSelectionType(SelectionType selectionType) {
		if (!hashSelectionTypes.containsKey(selectionType)) {
			hashSelectionTypes.put(selectionType, new HashMap<Integer, Integer>());
			selectionTypes.add(selectionType);
			Collections.sort(selectionTypes);
		}
	}

	/**
	 * Removes a custom selection type from the selection manager. Should only
	 * be called via a {@link SelectionTypeEvent}
	 */
	synchronized void removeSelectionType(SelectionType selectionType) {
		hashSelectionTypes.remove(selectionType);
		selectionTypes.remove(selectionType);
	}

	/**
	 * Removes all managed selection types. These are for example newly created
	 * selection groups.
	 */
	public synchronized void removeMangagedSelectionTypes() {

		for (int selectionTypeIndex = 0; selectionTypeIndex < selectionTypes.size(); selectionTypeIndex++) {

			SelectionType selectionType = selectionTypes.get(selectionTypeIndex);
			if (selectionType.isManaged()) {
				selectionTypes.remove(selectionType);
				hashSelectionTypes.remove(selectionType);
			}
		}
	}

	/**
	 * Add a type to the delta black list, which excludes all operations on the
	 * type from being written to a delta
	 * 
	 * @param type
	 *            the selection type which should not be written to deltas
	 */
	public synchronized void addTypeToDeltaBlacklist(SelectionType type) {
		deltaBlackList.put(type, null);
	}

	/**
	 * TODO: check whether this is thread-save enough.
	 */
	@Override
	public synchronized void queueEvent(
			AEventListener<? extends IListenerOwner> listener, AEvent event) {
		listener.handleEvent(event);
	}

	@Override
	public synchronized void registerEventListeners() {

		addSelectionTypeListener = new SelectionTypeListener();

		addSelectionTypeListener.setHandler(this);
		GeneralManager.get().getEventPublisher()
				.addListener(SelectionTypeEvent.class, addSelectionTypeListener);

		removeManagedSelectionTypesListener = new RemoveManagedSelectionTypesListener();

		removeManagedSelectionTypesListener.setHandler(this);
		GeneralManager
				.get()
				.getEventPublisher()
				.addListener(RemoveManagedSelectionTypesEvent.class,
						removeManagedSelectionTypesListener);

	}

	@Override
	public synchronized void unregisterEventListeners() {
		if (addSelectionTypeListener != null) {
			GeneralManager.get().getEventPublisher()
					.removeListener(addSelectionTypeListener);
			addSelectionTypeListener = null;
		}

		if (removeManagedSelectionTypesListener != null) {
			GeneralManager.get().getEventPublisher()
					.removeListener(removeManagedSelectionTypesListener);
			removeManagedSelectionTypesListener = null;
		}
	}

	@Override
	public synchronized String toString() {
		String result = "IDType: " + idType + " ";
		for (SelectionType selectionType : hashSelectionTypes.keySet()) {
			result = result + "[" + selectionType + ": "
					+ hashSelectionTypes.get(selectionType).size() + "]";
		}

		return result;
	}

	/**
	 * <p>
	 * Returns a semi-deep copy of the selection manager. That means that all
	 * containers have been cloned to be modifiable without affecting the source
	 * manager, however the elements themselves such as Integers or
	 * SelectionTypes are the same (this is necessary for SelectionTypes and
	 * should be fine for other values since they are not changed).
	 * </p>
	 * <p>
	 * Notice that the clone does not contain a virtual array! <b>It must be
	 * explicitly set by the receiver.</b>
	 * </p>
	 */
	@Override
	@SuppressWarnings("unchecked")
	public synchronized SelectionManager clone() {
		SelectionManager clone;
		try {
			clone = (SelectionManager) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Cloning error for SelectionManager: "
					+ e.getMessage());
		}

		clone.registerEventListeners();
		// clone hashConnectionToElementID
		clone.hashConnectionToElementID = (HashMap<Integer, ArrayList<Integer>>) this.hashConnectionToElementID
				.clone();
		for (Integer id : clone.hashConnectionToElementID.keySet()) {
			clone.hashConnectionToElementID.put(id,
					(ArrayList<Integer>) this.hashConnectionToElementID.get(id).clone());
		}

		// clone hashSelectionTypes
		clone.hashSelectionTypes = (HashMap<SelectionType, HashMap<Integer, Integer>>) this.hashSelectionTypes
				.clone();
		for (SelectionType selectionType : clone.hashSelectionTypes.keySet()) {
			clone.hashSelectionTypes.put(selectionType,
					(HashMap<Integer, Integer>) this.hashSelectionTypes
							.get(selectionType).clone());
		}

		// clone deltaBlackList
		clone.deltaBlackList = (HashMap<SelectionType, Boolean>) this.deltaBlackList
				.clone();

		// clone selectionTypes
		clone.selectionTypes = (ArrayList<SelectionType>) this.selectionTypes.clone();

		// the selectionDelta is reset
		clone.selectionDelta = new SelectionDelta(idType);
		// the virtual array needs to be set manually by the receiving instance
		return clone;
	}

	/**
	 * Returns the current selection type. By default this is
	 * SelectionType.SELECTION.
	 * 
	 * @return
	 */
	public synchronized SelectionType getSelectionType() {
		return selectionType;
	}

	/**
	 * Changes the current selection type. By default the selection type is set
	 * to SelectionType.SELECTION. Should only be called via a
	 * {@link SelectionTypeEvent}.
	 * 
	 * @param selectionType
	 */
	public synchronized void setSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}
}
