package org.caleydo.core.manager.event;

import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectionCommandEventContainer;

/**
 * Types of events that can be send from a {@link IMediatorSender} to a
 * {@link IMediatorReceiver} Document every type, including a reference to the
 * implementing class
 * 
 * @author Alexander Lex
 */
public enum EEventType
{
	/**
	 * Type for loading pathways by gene IDs, uses {@link IDListEventContainer}
	 */
	LOAD_PATHWAY_BY_GENE,
	/**
	 * Type for loading pathways by pathway IDs, uses
	 * {@link IDListEventContainer}
	 */
	LOAD_PATHWAY_BY_PATHWAY_ID,
	/**
	 * Type for {@link SelectionCommandEventContainer}
	 */
	TRIGGER_SELECTION_COMMAND,
	/**
	 * Type for {@link DeltaEventContainer} with {@link ISelectionDelta} as type
	 */
	SELECTION_UPDATE,
	/**
	 * Type for {@link DeltaEventContainer} with {@link IVirtualArrayDelta} as
	 * type
	 */
	VA_UPDATE,
	/**
	 * Type for {@link ViewCommandEventContainer}. Used to signal views for
	 * example to redraw
	 */
	VIEW_COMMAND,
	/**
	 * 
	 */
	INFO_AREA_UPDATE
}
