/**
 * 
 */
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.RenameVariablePerspectiveEvent;

/**
 * Context menu item to rename an {@link AVariablePerspective} using a dialog.
 * 
 * @author Christian Partl
 * 
 */
public class RenameVariablePerspectiveItem extends AContextMenuItem {

	/**
	 * @param perspectiveID
	 *            ID of the perspective.
	 * @param dataDomain
	 *            DataDomain the perspective belongs to.
	 * @param isRecordPerspective
	 *            Determines whether the perspective is a
	 *            {@link RecordPerspective} or a {@link DimensionPerspective}.
	 */
	public RenameVariablePerspectiveItem(String perspectiveID,
			ATableBasedDataDomain dataDomain, boolean isRecordPerspective) {
		setLabel("Rename Grouping");

		RenameVariablePerspectiveEvent event = new RenameVariablePerspectiveEvent(
				perspectiveID, dataDomain, isRecordPerspective);
		event.setSender(this);
		registerEvent(event);
	}
}
