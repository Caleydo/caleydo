/**
 * 
 */
package org.caleydo.view.visbricks.brick.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.event.SplitBrickEvent;

/**
 * @author alexsb
 * 
 */
public class SplitBrickItem extends AContextMenuItem {

	public SplitBrickItem(Integer connectionBandID, Boolean splitLeftBrick) {
		if (splitLeftBrick)

			setLabel("Split Left Brick");
		else
			setLabel("Split Right Brick");

		SplitBrickEvent event = new SplitBrickEvent(connectionBandID, splitLeftBrick);
		event.setSender(this);
		registerEvent(event);
	}
}
