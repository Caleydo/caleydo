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
package org.caleydo.view.stratomex.toolbar;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.stratomex.event.ConnectionsModeEvent;
import org.caleydo.view.stratomex.event.CreateBrickEvent;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Slider;

public class ConnectionsModeGUI extends ControlContribution {

	public ConnectionsModeGUI() {
		super("Trend Highlight Mode");
	}

	@Override
	protected Control createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		RowLayout layout = new RowLayout();
		// layout.marginHeight = layout.marginWidth = layout.horizontalSpacing =
		// 0;
		composite.setLayout(layout);
		
		Button button1 = new Button(composite, SWT.PUSH);
		button1.setText("Split Brick");
		
		Listener listenerButton = new Listener() {
			@Override
			public void handleEvent(Event event) {
				
				System.out.println("Split brick called!");

				GeneralManager
				.get()
				.getEventPublisher()
				.triggerEvent(new CreateBrickEvent());
				
			}

		};
		button1.addListener(SWT.Selection, listenerButton);

		final Button[] radios = new Button[1];
		radios[0] = new Button(composite, SWT.CHECK);
		radios[0].setSelection(true);
		radios[0].setText("Show Only Selected Connections");

		final Button dynamicTrendHighlightButton = new Button(composite, SWT.CHECK);
		dynamicTrendHighlightButton.setText("Dynamic Connection Highlight Focus");

		final Slider slider = new Slider(composite, SWT.HORIZONTAL);
		slider.setValues(0, 0, 100, 1, 1, 1);
		slider.setLayoutData(new RowData(130, 20));
		slider.setEnabled(false);

		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {

				slider.setEnabled(dynamicTrendHighlightButton.getSelection());

				GeneralManager
						.get()
						.getEventPublisher()
						.triggerEvent(
								new ConnectionsModeEvent(radios[0].getSelection(),
										dynamicTrendHighlightButton.getSelection(),
										slider.getSelection() / 100f));
			}

		};
		slider.addListener(SWT.Selection, listener);
		dynamicTrendHighlightButton.addListener(SWT.Selection, listener);
		radios[0].addListener(SWT.Selection, listener);

		return composite;
	}
}
