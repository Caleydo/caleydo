package org.caleydo.core.manager.event;

import org.caleydo.core.data.IUniqueObject;

/**
 * Object that shall receive an event.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public interface IMediatorReceiver
{
	/**
	 * Update called by Mediator triggered by IMediatorSender.
	 * 
	 * @param eventTrigger Calling object, that created the update
	 * @param selectionDelta the differences in the selections
	 * @param colSelectionCommand TODO
	 */
	// public void handleSelectionUpdate(IUniqueObject eventTrigger,
	// ISelectionDelta selectionDelta,
	// Collection<SelectionCommand> colSelectionCommand, EMediatorType
	// eMediatorType);
	//
	// /**
	// * Update concerning virtual arrays. The details about what to do with the
	// * update are specified in the delta.
	// *
	// * @param eMediatorType for which mediator
	// * @param eventTrigger the caller
	// * @param delta the delta containing all operations to be executed
	// * @param colSelectionCommand TODO
	// */
	// public void handleVAUpdate(EMediatorType eMediatorType, IUniqueObject
	// eventTrigger,
	// IVirtualArrayDelta delta, Collection<SelectionCommand>
	// colSelectionCommand);
	//	
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer);

}
