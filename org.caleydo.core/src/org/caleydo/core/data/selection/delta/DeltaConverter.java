package org.caleydo.core.data.selection.delta;

import java.util.Set;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.mapping.IDMappingManager;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;

/**
 * <p>
 * Converter for deltas ({@link IDelta}).
 * </p>
 * <p>
 * Based on the internal type of a delta, which is the source type and a supplied target type a new delta is
 * created which contains the original id as its secondary ID and the new converted ID as the primary ID. Any
 * previous secondary ID is not preserved.
 * </p>
 * <p>
 * Due to the n:m relations of mappings the resulting delta can have a length different from the original.
 * </p>
 * 
 * @author Alexander Lex
 */
public class DeltaConverter {

	/**
	 * Convert method. TODO: only DAVID_2_EXPRESSION_INDEX is supported ATM
	 * 
	 * @param <T>
	 *            the type of the delta, an implementation of {@link IDelta}
	 * @param idMappingManager
	 *            the id mapping manager that should be used for the conversion
	 * @param targetType
	 *            the target type of the id conversion
	 * @param delta
	 *            the src delta containing the src type
	 * @return the new delta, which can be longer than the original
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IDelta> T convertDelta(IDMappingManager idMappingManager, IDType targetType,
		T delta) {
		T newDelta = null;
		if (delta instanceof SelectionDelta) {
			newDelta = (T) new SelectionDelta(targetType);
		}
		else if (delta instanceof VirtualArrayDelta) {

			VirtualArrayDelta vaDelta = (VirtualArrayDelta) delta;
			VirtualArrayDelta newVADelta = vaDelta.getInstance();
			newVADelta.tableIDType(targetType);
			newVADelta.setVAType(vaDelta.getVAType());
			newDelta = (T) newVADelta;

		}
		else
			throw new IllegalStateException(
				"This type of delta is not supported by the DeltaConverter, add appropriate implementation");

		for (Object tempItem : delta) {
			IDeltaItem item = (IDeltaItem) tempItem;
			Set<Integer> tableIDs = idMappingManager.getIDAsSet(delta.getIDType(), targetType, item.getID());
			if (tableIDs == null) {
				continue;
			}
			for (Integer id : tableIDs) {
				IDeltaItem clonedItem = (IDeltaItem) item.clone();
				clonedItem.setID(id);
				newDelta.add(clonedItem);
			}
		}

		return newDelta;
	}
}
