package org.caleydo.core.manager.event;

/**
 * Types of events that can be send from a {@link IMediatorSender} to a
 * {@link IMediatorReceiver}
 * 
 * @author Alexander Lex
 * 
 */
public enum EEventType
{
	LOAD_PATHWAY_BY_GENE,
	LOAD_PATHWAY_BY_PATHWAY_ID,
	/**
	 * 
	 */
	TRIGGER_SELECTION_COMMAND
}
