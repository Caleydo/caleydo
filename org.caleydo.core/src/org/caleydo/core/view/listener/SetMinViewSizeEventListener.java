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
package org.caleydo.core.view.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.MinSizeAppliedEvent;
import org.caleydo.core.event.view.SetMinViewSizeEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.MinimumSizeComposite;
import org.caleydo.core.view.opengl.canvas.AGLView;

public class SetMinViewSizeEventListener
	extends AEventListener<MinimumSizeComposite> {

	private AGLView view;

	@Override
	public void handleEvent(AEvent event) {
		if (handler.isDisposed()) {
			GeneralManager.get().getEventPublisher().removeListener(this);
			return;
		}

		if (event instanceof SetMinViewSizeEvent) {
			SetMinViewSizeEvent setMinViewSizeEvent = (SetMinViewSizeEvent) event;
			if (setMinViewSizeEvent.getView() == view) {
				handler.setMinSize(setMinViewSizeEvent.getMinWidth(), setMinViewSizeEvent.getMinHeight());
				MinSizeAppliedEvent e = new MinSizeAppliedEvent();
				e.setView(view);
				e.setSender(this);
				GeneralManager.get().getEventPublisher().triggerEvent(e);
			}
		}
	}

	public AGLView getView() {
		return view;
	}

	public void setView(AGLView view) {
		this.view = view;
	}

}
