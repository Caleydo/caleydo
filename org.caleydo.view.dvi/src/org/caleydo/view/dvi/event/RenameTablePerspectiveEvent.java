/**
 * 
 */
package org.caleydo.view.dvi.event;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;

/**
 * Event that triggers a dialog to rename a specified {@link TablePerspective}.
 * 
 * @author Christian Partl
 * 
 */
public class RenameTablePerspectiveEvent extends AEvent {

	/**
	 * The TablePerspective to be renamed.
	 */
	private TablePerspective tablePerspective;

	/**
	 * 
	 */
	public RenameTablePerspectiveEvent() {
		// TODO Auto-generated constructor stub
	}

	public RenameTablePerspectiveEvent(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	@Override
	public boolean checkIntegrity() {
		return tablePerspective != null;
	}

	/**
	 * @param tablePerspective
	 *            setter, see {@link #tablePerspective}
	 */
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	/**
	 * @return the tablePerspective, see {@link #tablePerspective}
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

}
