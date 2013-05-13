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
package org.caleydo.view.tourguide.api.state;

import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.OpenViewHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Samuel Gratzl
 *
 */
public class OpenTourGuideState implements IState {
	private final EDataDomainQueryMode mode;
	private final String label;

	public OpenTourGuideState(EDataDomainQueryMode mode, String label) {
		this.mode = mode;
		this.label = label;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Collection<ITransition> getTransitions() {
		return Collections.emptyList();
	}

	@Override
	public void onEnter(final ICallback<IState> onAutomaticSwitch) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				OpenViewHandler.showTourGuide(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), mode);
			}
		});
	}

	@Override
	public void onLeave() {

	}
}
