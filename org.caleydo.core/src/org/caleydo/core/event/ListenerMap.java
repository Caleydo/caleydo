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
package org.caleydo.core.event;

import java.util.Collection;
import java.util.HashMap;

/**
 * Map used within {@link EventPublisher}s that maps {@link AEvent}s to a hash map, that maps the
 * DataDomainType string to concrete {@link AEventListener}s registered to the event. For listeners not using
 * a dataDomainType string, the null key for the second hash map is used instead of a string.
 * 
 * @author Werner Puff
 * @author Alexander Lex
 */
public class ListenerMap
	extends HashMap<Class<? extends AEvent>, HashMap<String, Collection<AEventListener<?>>>> {

	/** version uid */
	public static final long serialVersionUID = 1L;

}
