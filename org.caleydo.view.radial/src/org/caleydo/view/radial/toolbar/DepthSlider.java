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
package org.caleydo.view.radial.toolbar;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.radial.event.SetMaxDisplayedHierarchyDepthEvent;
import org.caleydo.view.radial.event.UpdateDepthSliderPositionEvent;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.PlatformUI;

/**
 * Tool-bar item that contains a slider for specifying the maximum displayed
 * hierarchy depth of RadialHierarchy.
 *
 * @author Christian Partl
 */
public class DepthSlider extends ControlContribution implements
		IListenerOwner {

	private Listener listener;
	private UpdateDepthSliderPositionListener updateSliderPositionListener;
	private Slider slider;
	private int iSelection;

	public DepthSlider(int sliderSelection) {
		super("");
		iSelection = sliderSelection;
	}

	@Override
	protected Control createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		slider = new Slider(composite, SWT.HORIZONTAL);
		slider.setValues(iSelection, 2, 20, 1, 1, 1);
		slider.setLayoutData(new GridData(130, 20));

		listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				SetMaxDisplayedHierarchyDepthEvent setMaxDisplayedHierarchyDepthEvent = new SetMaxDisplayedHierarchyDepthEvent();
				setMaxDisplayedHierarchyDepthEvent.setSender(this);
				setMaxDisplayedHierarchyDepthEvent.setMaxDisplayedHierarchyDepth(slider
						.getSelection());
				GeneralManager.get().getEventPublisher()
						.triggerEvent(setMaxDisplayedHierarchyDepthEvent);
			}

		};
		iSelection = slider.getSelection();
		slider.addListener(SWT.Selection, listener);

		registerEventListeners();
		return composite;
	}

	@Override
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener,
			final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});

	}

	/**
	 * Sets the slider position (selection) to a cartain value.
	 *
	 * @param iPosition
	 *            The value the slider position (selection) is set to.
	 */
	public void setSliderPosition(int iPosition) {
		iSelection = iPosition;
		slider.setSelection(iPosition);
	}

	@Override
	public void dispose() {
		unregisterEventListeners();

	}

	@Override
	public void registerEventListeners() {
		updateSliderPositionListener = new UpdateDepthSliderPositionListener();
		updateSliderPositionListener.setHandler(this);
		GeneralManager
				.get()
				.getEventPublisher()
				.addListener(UpdateDepthSliderPositionEvent.class,
						updateSliderPositionListener);

	}

	@Override
	public void unregisterEventListeners() {
		if (updateSliderPositionListener != null) {
			GeneralManager.get().getEventPublisher()
					.removeListener(updateSliderPositionListener);
			updateSliderPositionListener = null;
		}

	}
}
