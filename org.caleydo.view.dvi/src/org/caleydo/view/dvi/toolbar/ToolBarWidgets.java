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
package org.caleydo.view.dvi.toolbar;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.event.ApplySpecificGraphLayoutEvent;
import org.caleydo.view.dvi.event.ShowDataConnectionsEvent;
import org.caleydo.view.dvi.layout.ForceDirectedGraphLayout;
import org.caleydo.view.dvi.layout.TwoLayeredGraphLayout;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * Class that specifies toolbar items that are not buttons for {@link GLDataViewIntegrator}.
 * 
 * @author Christian Partl
 * 
 */
public class ToolBarWidgets extends ControlContribution {

	public ToolBarWidgets(String id) {
		super(id);
	}

	@Override
	protected Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		RowLayout layout = new RowLayout();
		// layout.marginHeight = layout.marginWidth = layout.horizontalSpacing =
		// 0;
		composite.setLayout(layout);

		// final Combo graphLayoutCombo = new Combo(composite, SWT.READ_ONLY);
		// graphLayoutCombo.add("Bipartite");
		// graphLayoutCombo.add("Spring-Based");
		// graphLayoutCombo.setText("Bipartite");
		// graphLayoutCombo.setSize(100, 5);

		// final ToolItem item = new ToolItem((ToolBar)parent, SWT.DROP_DOWN);
		// item.setText("dd");

		final Label layoutLabel = new Label(composite, SWT.NONE);
		layoutLabel.setText("Layout: ");

		final Button bipartiteLayoutButton = new Button(composite, SWT.RADIO);
		bipartiteLayoutButton.setSelection(true);
		bipartiteLayoutButton.setText("Bipartite");
		// bipartiteLayoutButton.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT,
		// false, false));
		final Button springBasedLayoutButton = new Button(composite, SWT.RADIO);
		springBasedLayoutButton.setText("Spring-Based");
		// springBasedLayoutButton.setLayoutData(new GridData(SWT.LEFT,
		// SWT.LEFT, false, false));

		new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		// layoutLabel.setText("Layout: ");

		final Button showDataConnectionsCheckBox = new Button(composite, SWT.CHECK);
		showDataConnectionsCheckBox.setSelection(false);
		showDataConnectionsCheckBox.setText("Show Data Connections");

		Listener showDataConnectionsListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				GeneralManager
						.get()
						.getEventPublisher()
						.triggerEvent(
								new ShowDataConnectionsEvent(showDataConnectionsCheckBox
										.getSelection()));
			}

		};

		Listener graphLayoutListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				ApplySpecificGraphLayoutEvent e = new ApplySpecificGraphLayoutEvent();
				if (event.widget == bipartiteLayoutButton) {
					e.setGraphLayoutClass(TwoLayeredGraphLayout.class);
				} else {
					e.setGraphLayoutClass(ForceDirectedGraphLayout.class);
				}

				e.setSender(this);
				GeneralManager.get().getEventPublisher().triggerEvent(e);

			}
		};
		//
		// graphLayoutCombo.addListener(SWT.Selection, graphLayoutListener);

		showDataConnectionsCheckBox.addListener(SWT.Selection,
				showDataConnectionsListener);
		bipartiteLayoutButton.addListener(SWT.Selection, graphLayoutListener);
		springBasedLayoutButton.addListener(SWT.Selection, graphLayoutListener);
		// composite.pack();
		// parent.pack();

		return composite;
	}
}
