/**
 * 
 */
package org.caleydo.view.visbricks.event;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals that an object idtentified through the id should be
 * reneamed
 * 
 * @author Alexander Lex
 * 
 */
public class RenameEvent extends AEvent {

	private Integer id;

	/**
     * 
     */
	public RenameEvent() {
	}

	public RenameEvent(Integer id) {
		this.id = id;
	}

	/**
	 * @param id
	 *            setter, see {@link #id}
	 */
	public void setID(Integer id) {
		this.id = id;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public Integer getID() {
		return id;
	}

	@Override
	public boolean checkIntegrity() {
		if (id == null)
			return false;
		return true;
	}

}
