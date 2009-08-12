package org.caleydo.core.data.selection.delta;

import java.util.Set;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.general.GeneralManager;

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
	 * @param targetType
	 *            the target type of the id conversion
	 * @param delta
	 *            the src delta containing the src type
	 * @return the new delta, which can be longer than the original
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IDelta> T convertDelta(EIDType targetType, T delta) {
		T newDelta = null;
		if (delta instanceof SelectionDelta) {
			newDelta = (T) new SelectionDelta(targetType, delta.getIDType());
		}
		else if (delta instanceof VirtualArrayDelta) {
			newDelta =
				(T) new VirtualArrayDelta(((VirtualArrayDelta) delta).getVAType(), targetType, delta
					.getIDType());
		}
		else
			throw new IllegalStateException(
				"This type of delta is not supported by the DeltaConverter, add appropriate implementation");

		// if (delta.getIDType() == EIDType.DAVID && targetType == EIDType.EXPRESSION_INDEX)
		// {
		// for (Object tempItem : delta)
		// {
		// IDeltaItem item = (IDeltaItem) tempItem;
		// Set<Integer> setExpressionIndices = GeneralManager.get().getIDMappingManager()
		// .<Integer, Integer> getMultiID(EMappingType.DAVID_2_EXPRESSION_INDEX,
		// item.getPrimaryID());
		// if (setExpressionIndices == null)
		// {
		// GeneralManager.get().getLogger().log(Level.WARNING,
		// "No mapping found for david to expression index");
		// continue;
		// }
		// for (int iExpressionIndex : setExpressionIndices)
		// {
		// IDeltaItem clonedItem = (IDeltaItem) item.clone();
		// clonedItem.setPrimaryID(iExpressionIndex);
		// clonedItem.setSecondaryID(item.getPrimaryID());
		// newDelta.add(clonedItem);
		// }
		// }
		// }
		// else
		if (delta.getIDType() == EIDType.REFSEQ_MRNA_INT && targetType == EIDType.EXPRESSION_INDEX) {
			for (Object tempItem : delta) {
				IDeltaItem item = (IDeltaItem) tempItem;
				Set<Integer> setExpressionIndices =
					GeneralManager.get().getIDMappingManager().getID(EIDType.REFSEQ_MRNA_INT,
						EIDType.EXPRESSION_INDEX, item.getPrimaryID());
				if (setExpressionIndices == null) {
					// GeneralManager.get().getLogger().log(new Status(Status.WARNING,
					// GeneralManager.PLUGIN_ID,
					// "No mapping found for david to expression index"));
					continue;
				}
				for (int iExpressionIndex : setExpressionIndices) {
					IDeltaItem clonedItem = (IDeltaItem) item.clone();
					clonedItem.setPrimaryID(iExpressionIndex);
					clonedItem.setSecondaryID(item.getPrimaryID());
					newDelta.add(clonedItem);
				}
			}
		}

		return newDelta;
	}
}
