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

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.OpenViewHandler;
import org.caleydo.view.tourguide.internal.RcpGLTourGuideView;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class ASelectTransition implements ITransition {
	private final IState target;
	private ICallback<IState> onAutomaticSwitch;

	public ASelectTransition(IState target) {
		this.target = target;
	}

	@Override
	public final void onSourceEnter(ICallback<IState> onAutomaticSwitch) {
		this.onAutomaticSwitch = onAutomaticSwitch;
		this.onEnterImpl();
	}

	protected abstract void onEnterImpl();

	protected final void switchToTarget() {
		if (this.onAutomaticSwitch != null) {
			onAutomaticSwitch.on(target);
			onAutomaticSwitch = null;
		}
	}

	protected final static void addScoreToTourGuide(final EDataDomainQueryMode mode, final IScore... scores) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				RcpGLTourGuideView tourGuide = OpenViewHandler.showTourGuide(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow(), mode);
				Object receiver = tourGuide.getView();
				EventPublisher.trigger(new AddScoreColumnEvent(scores).to(receiver).from(this));
			}
		});
	}

}
