/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.table;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.view.table.action.SelectionOnlyAction;
import org.caleydo.view.table.action.TableSettingsAction;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.widgets.Composite;

/**
 * a simple eclipse view for presenting the raw values of a table using a {@link NatTable}
 * 
 * @author Samuel Gratzl and Marc Streit
 */
public class RcpTableView extends CaleydoRCPViewPart {

	/**
	 * Constructor.
	 */
	public RcpTableView() {
		super();
		try {
			viewContext = JAXBContext.newInstance(SerializedTableView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new TableView(parentComposite, this);

		initializeView();
		GeneralManager.get().getViewManager().registerRCPView(this, view);
		addToolBarContent();
	}

	@Override
	public void dispose() {
		super.dispose();
		((TableView) view).dispose();
		GeneralManager.get().getViewManager().unregisterRCPView(this, view);
		view = null;
	}

	@Override
	public void addToolBarContent() {
		toolBarManager.add(new SelectionOnlyAction((TableView) view));
		toolBarManager.add(new TableSettingsAction((TableView) view));
		super.addToolBarContent();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedTableView();
		determineDataConfiguration(serializedView);
	}
}
