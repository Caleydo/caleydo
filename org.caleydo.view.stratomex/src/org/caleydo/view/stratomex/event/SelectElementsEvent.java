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
package org.caleydo.view.stratomex.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.view.stratomex.GLStratomex;

/**
 * @author Samuel Gratzl
 *
 */
public class SelectElementsEvent extends AEvent {

	private GLStratomex receiver;
	private Iterable<Integer> ids;
	private IDType idType;



	public SelectElementsEvent() {

	}

	public SelectElementsEvent(Iterable<Integer> ids, IDType idType,
			GLStratomex receiver, Object sender) {
		this.setSender(sender);
		this.receiver = receiver;
		this.ids = ids;
		this.idType = idType;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.event.AEvent#checkIntegrity()
	 */
	@Override
	public boolean checkIntegrity() {
		return ids != null && idType != null;
	}

	/**
	 * @return the ids, see {@link #ids}
	 */
	public Iterable<Integer> getIds() {
		return ids;
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public IDType getIdType() {
		return idType;
	}
	/**
	 * @return the receiver, see {@link #receiver}
	 */
	public GLStratomex getReceiver() {
		return receiver;
	}

}
