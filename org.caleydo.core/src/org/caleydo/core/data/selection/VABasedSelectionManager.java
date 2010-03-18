package org.caleydo.core.data.selection;

import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.StorageVADelta;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.event.IListenerOwner;

/**
 * <p>
 * Manages selections generically by storing them in hash maps. The manager can handle an arbitrary number of
 * selection types, which have to be defined in {@link SelectionType} A normal type, by default NORMAL in the
 * aforementioned enum is considered to be the base type, where all elements are initially added to.
 * </p>
 * <p>
 * The selection manager always keeps a {@link SelectionDelta}, which can be used to communicate changes in
 * the selection to other views. This is reset every time you call it's getter.
 * </p>
 * <p>
 * Consequently it can also merge external deltas into its own selection.
 * </p>
 * <p>
 * When a selection delta is merged into the manager that contains values that are not specified in the list
 * of allowed values, the selections are ignored
 * </p>
 * <p>
 * When using the manager on data that is also managed by a {@link IVirtualArray} set the currently active
 * virtual array so that the tow are kept synchronous.
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class VABasedSelectionManager<ConcreteType extends VABasedSelectionManager<ConcreteType, VA, VAType, VADelta>, VA extends VirtualArray<VA, VAType, VADelta, ?>, VAType extends IVAType, VADelta extends VirtualArrayDelta<VADelta, VAType>>
	extends SelectionManager
	implements IListenerOwner, Cloneable {

	private VA virtualArray;

	/**
	 * Constructor
	 */
	public VABasedSelectionManager(EIDType idType) {
		super(idType);
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
	public void setVA(VA virtualArray) {
		// resetSelectionManager();
		// initialAdd(virtualArray.getIndexList());
		this.virtualArray = virtualArray;
	}

	/**
	 * Removes a particular element from the selection manager, no matter what the type
	 * 
	 * @param iElementID
	 *            the element to be removed
	 */
	@Override
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
			}
		}
	}

	/**
	 * Removes all elements and sets the element counter to 0 Removes all elements in selectionDelta. Clears
	 * the virtual array.
	 */
	@Override
	public void resetSelectionManager() {
		super.resetSelectionManager();
		if (virtualArray != null) {
			// null here?
			virtualArray.clear();
		}
	}

	/**
	 * Creates a delta for a virtual array containing all element of the manager intended for broadcasts. The
	 * type of the delta is {@link EVAOperation#APPEND_UNIQUE}
	 * 
	 * @return the delta containing all elements
	 */
	@SuppressWarnings("unchecked")
	public VADelta getBroadcastVADelta() {
		VADelta tempDelta;
		if (virtualArray instanceof ContentVirtualArray)
			tempDelta = (VADelta) new ContentVADelta();
		else
			tempDelta = (VADelta) new StorageVADelta();

		tempDelta.setVAType(virtualArray.getVAType());
		tempDelta.setIDType(iDType);
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
	 * Set a virtual array delta to reflect changes to be made due to VA operations in the selection manager.
	 * When the virtual array is managed by the selection manager the delta is also applied to the virtual
	 * array.
	 * 
	 * @param delta
	 *            the delta containing the changes
	 */
	public void setVADelta(VADelta delta) {
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

	@Override
	public String toString() {
		return ("VABased, " + super.toString());
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
	public ConcreteType clone() {
		VABasedSelectionManager clone;

		clone = (VABasedSelectionManager) super.clone();

		// the virtual array needs to be set manually by the receiving instance
		clone.virtualArray = null;
		return (ConcreteType) clone;
	}

	/**
	 * Adds a selection to the target type.
	 */
	@Override
	public void addToType(SelectionType targetType, int iElementID) {
		// if (virtualArray != null && virtualArray.containsElement(iElementID) != 0)
		super.addToType(targetType, iElementID);
	}

	@Override
	public int getNumberOfElements(SelectionType selectionType) {
		int size = 0;
		Set<Integer> elements = super.getElements(selectionType);
		if (elements == null)
			return 0;
		for (Integer element : elements) {
			if (virtualArray.containsElement(element) != 0)
				size++;
		}
		return size;
	}
}
