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
package org.caleydo.view.scatterplot.toolbar;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.scatterplot.listener.SetPointSizeEvent;
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
 * Toolbar item that contains a slider for TODO
 * 
 * @author Juergen Pillhofer
 */
public class PointSizeSlider extends ControlContribution implements IToolBarItem,
		IListenerOwner {

	private Listener listener;
	// private UpdateDepthSliderPositionListener updateSliderPositionListener;
	private Slider slider;
	private int iSelection;

	public PointSizeSlider(String str, int iSliderSelection) {
		super(str);
		iSelection = iSliderSelection;
	}

	@Override
	protected Control createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		iSelection = 5;
		slider = new Slider(composite, SWT.HORIZONTAL);
		slider.setValues(iSelection, 1, 10, 1, 1, 1);
		slider.setLayoutData(new GridData(130, 20));

		listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				SetPointSizeEvent setPointSizeEvent = new SetPointSizeEvent();
				setPointSizeEvent.setSender(this);
				setPointSizeEvent.setPointSize(slider.getSelection());
				GeneralManager.get().getEventPublisher().triggerEvent(setPointSizeEvent);
			}

		};
		iSelection = slider.getSelection();
		slider.addListener(SWT.Selection, listener);

		// updateSliderPositionListener = new
		// UpdateDepthSliderPositionListener();
		// updateSliderPositionListener.setHandler(this);
		// GeneralManager.get().getEventPublisher().addListener(UpdateDepthSliderPositionEvent.class,
		// updateSliderPositionListener);

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
		// // Unregister event listener
		// if (updateSliderPositionListener != null) {
		// GeneralManager.get().getEventPublisher().removeListener(updateSliderPositionListener);
		// updateSliderPositionListener = null;
		// }
	}

	@Override
	public void registerEventListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterEventListeners() {
		// TODO Auto-generated method stub

	}
}
