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
import org.caleydo.core.event.view.tablebased.InitAxisComboEvent;
import org.caleydo.core.event.view.tablebased.YAxisSelectorEvent;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

/**
 * Toolbar item that contains a slider for TODO
 * 
 * @author Juergen Pillhofer
 */
public class YAxisSelector extends ControlContribution implements IToolBarItem,
		IListenerOwner {

	private InitYAxisComboListener initAxisComboListener;
	private Combo combo;
	private int iSelection;

	public YAxisSelector(String str, int iSliderSelection) {
		super(str);
		iSelection = iSliderSelection;
	}

	@Override
	protected Control createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = layout.marginWidth = layout.horizontalSpacing = 0;
		composite.setLayout(layout);

		combo = new Combo(composite, SWT.HORIZONTAL);
		combo.setLayoutData(new GridData(130, 20));
		// combo.add("Hansi");
		// combo.add("Ferdl");
		// combo.add("Seppi");
		// combo.add("Peter");
		// combo.add("Korl");
		// combo.select(1);
		//

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// sSelection = combo.getText();
				iSelection = combo.getSelectionIndex();
				YAxisSelectorEvent yAxisSelectorEvent = new YAxisSelectorEvent();
				yAxisSelectorEvent.setSender(this);
				yAxisSelectorEvent.setSelectedAxis(iSelection);
				GeneralManager.get().getEventPublisher().triggerEvent(yAxisSelectorEvent);
			}
		});

		initAxisComboListener = new InitYAxisComboListener();
		initAxisComboListener.setHandler(this);
		GeneralManager.get().getEventPublisher()
				.addListener(InitAxisComboEvent.class, initAxisComboListener);

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
	 * Inits the Combobox with values.
	 * 
	 * @param sItems
	 *            An Array Of Strings representing the items in the combobox.
	 */
	public void initComboString(String[] sItems) {

		// combo.setItems(sItems); Doesnt work ? strange;

		for (String tmp : sItems) {
			combo.add(tmp);

		}
		if (sItems != null && sItems.length > 1)
			combo.select(1);

	}

	@Override
	public void dispose() {
		// Unregister event listener
		if (initAxisComboListener != null) {
			GeneralManager.get().getEventPublisher()
					.removeListener(initAxisComboListener);
			initAxisComboListener = null;
		}
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
