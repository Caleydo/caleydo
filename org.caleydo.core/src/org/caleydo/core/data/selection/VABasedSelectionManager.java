package org.caleydo.core.data.selection;

import java.util.Set;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.delta.DeltaConverter;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
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
public class VABasedSelectionManager<ConcreteType extends VABasedSelectionManager<ConcreteType, VA, VADelta>, VA extends VirtualArray<VA, VADelta, ?>, VADelta extends VirtualArrayDelta<VADelta>>
	extends SelectionManager
	implements IListenerOwner, Cloneable {

	private VA virtualArray;

	/**
	 * Constructor
	 */
	public VABasedSelectionManager(IDType idType) {
		super(idType);
	}

	/**
	 * <p>
	 * Set a virtual array if the data you are managing with this selection manager is also managed by a
	 * virtual array.
	 * </p>
	 * 
	 * @param virtualArray
	 *            the currently active virtual array
	 */
	public void setVA(VA virtualArray) {
		// clearSelections();
		// resetSelectionManager();
		// initialAdd(virtualArray.getIndexList());
		this.virtualArray = virtualArray;

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

		tempDelta.setVAType(virtualArray.getVaType());
		tempDelta.setIDType(iDType);

		for (Integer id : virtualArray) {
			tempDelta.add(VADeltaItem.appendUnique(id));
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
		VADelta localDelta;
		if (delta.getIDType().getIDCategory() != iDType.getIDCategory())
			throw new IllegalStateException("Not compatibel id types");

		if (delta.getIDType() != iDType)
			localDelta = DeltaConverter.convertDelta(iDType, delta);
		else
			localDelta = delta;

		for (VADeltaItem item : localDelta) {
			switch (item.getType()) {

				case REMOVE_ELEMENT:
					remove(item.getPrimaryID());
					break;
				case REMOVE:
					remove(virtualArray.get(item.getIndex()));
					break;

			}
		}
		virtualArray.setDelta(localDelta);

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
		VABasedSelectionManager<ConcreteType, VA, VADelta> clone;

		clone = (VABasedSelectionManager<ConcreteType, VA, VADelta>) super.clone();

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
			if (virtualArray.contains(element))
				size++;
		}
		return size;
	}

	/**
	 * Return only those elements that are actually in this selection manager. FIXME: this is slow, need a
	 * better solution
	 */
	@Override
	public Set<Integer> getElements(SelectionType selectionType) {

		Set<Integer> zoomedElements = super.getElements(selectionType);

		// Iterator<Integer> elementIterator = zoomedElements.iterator();
		// while (elementIterator.hasNext()) {
		// if (virtualArray.containsElement(elementIterator.next()) == 0)
		// elementIterator.remove();
		// }
		return zoomedElements;
	}
}
