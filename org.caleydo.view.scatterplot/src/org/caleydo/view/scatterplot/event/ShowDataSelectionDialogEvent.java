/**
 * 
 */
package org.caleydo.view.scatterplot.event;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;

/**
 * @author turkay
 *
 */
public class ShowDataSelectionDialogEvent extends AEvent{
	TablePerspective tablePerspective;
	

	/**
	 *
	 */
	public ShowDataSelectionDialogEvent(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	/**
	 * @return the perspective, see {@link #tablePerspective}
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	/**
	 * @param perspective
	 *            setter, see {@link perspective}
	 */
	public void setTablePerspective(TablePerspective tablePerspective) {
		this.tablePerspective = tablePerspective;
	}

	@Override
	public boolean checkIntegrity() {
		if (tablePerspective != null)
			return true;

		return false;
	}
}
