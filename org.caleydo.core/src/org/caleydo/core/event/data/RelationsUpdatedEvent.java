package org.caleydo.core.event.data;

import org.caleydo.core.data.virtualarray.similarity.RelationAnalyzer;
import org.caleydo.core.event.AEvent;

/**
 * Event that signals that the relations calculated in a {@link RelationAnalyzer} have been updated.
 * 
 * @author Alexander Lex
 */
public class RelationsUpdatedEvent
	extends AEvent {

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
