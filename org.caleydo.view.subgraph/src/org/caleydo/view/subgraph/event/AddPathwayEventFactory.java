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
package org.caleydo.view.subgraph.event;

import org.caleydo.datadomain.pathway.VertexRepBasedEventFactory;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.AVertexRepBasedEvent;

/**
 * @author Christian
 *
 */
public class AddPathwayEventFactory extends VertexRepBasedEventFactory {

	/**
	 * @param eventClass
	 * @param eventSpace
	 */
	public AddPathwayEventFactory(Class<? extends AVertexRepBasedEvent> eventClass, String eventSpace) {
		super(eventClass, eventSpace);
	}

	@Override
	public AVertexRepBasedEvent create(PathwayVertexRep vertexRep) {
		if (vertexRep != null && vertexRep.getType() == EPathwayVertexType.map) {
			AddPathwayEvent event = new AddPathwayEvent();
			event.setVertexRep(vertexRep);
			event.setEventSpace(eventSpace);
			return event;
		}
		return null;
	}

}
