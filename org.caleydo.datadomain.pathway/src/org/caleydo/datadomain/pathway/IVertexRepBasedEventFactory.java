/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * @author Christian Partl
 *
 */
public interface IVertexRepBasedEventFactory {
	/**
	 * Creates the event using the specified vertexRep.
	 *
	 * @param vertexRep
	 * @return The event, null if the event could not be created.
	 */
	public AEvent create(PathwayVertexRep vertexRep);

	/**
	 * Convenience method to trigger the event after creating.
	 *
	 * @param vertexRep
	 */
	public void triggerEvent(PathwayVertexRep vertexRep);
}
