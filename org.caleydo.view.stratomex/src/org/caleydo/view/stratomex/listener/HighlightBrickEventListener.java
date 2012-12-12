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
package org.caleydo.view.stratomex.listener;

import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnGlowRenderer;
import org.caleydo.view.stratomex.column.BrickColumnManager;
import org.caleydo.view.stratomex.event.HighlightBrickEvent;

/**
 * @author Samuel Gratzl
 *
 */
public class HighlightBrickEventListener extends AEventListener<GLStratomex> {
	public HighlightBrickEventListener(GLStratomex handler) {
		this.setHandler(handler);
	}

	@Override
	public void handleEvent(AEvent aevent) {
		HighlightBrickEvent event = (HighlightBrickEvent)aevent;
		if (event.getReceiver() != handler)
			return;
		BrickColumnManager manager = handler.getBrickColumnManager();
		BrickColumn brickColumn = manager.getBrickColumn(event.getStratification());
		if (brickColumn == null)
			return;

		ElementLayout layout = null;
		if (event.getGroup() == null) {
			layout = brickColumn.getLayout();
		} else {
			Group g = event.getGroup();
			for (GLBrick brick : brickColumn.getBricks()) {
				if (g.equals(brick.getTablePerspective().getRecordGroup())) {
					layout = brick.getLayout();
					break;
				}
			}
		}
		if (layout == null)
			return;

		if (!event.isHighlight()) {
			layout.clearBackgroundRenderers();
		} else {
			layout.addBackgroundRenderer(new BrickColumnGlowRenderer(event.getColor().getRGBA(), brickColumn, false));
		}
		if (layout.getLayoutManager() != null)
			layout.updateSubLayout();
	}

}
