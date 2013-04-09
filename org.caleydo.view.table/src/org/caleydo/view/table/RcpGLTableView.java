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
package org.caleydo.view.table;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.view.table.menu.SelectDataRepresentationAction;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 *
 * @author <INSERT_YOUR_NAME>
 */
public class RcpGLTableView extends CaleydoRCPViewPart {
	public static final String VIEW_TYPE = "org.caleydo.view.table";
	private DataProvider data;
	private NatTable table;

	/**
	 * Constructor.
	 */
	public RcpGLTableView() {
		super();
		try {
			viewContext = JAXBContext.newInstance(SerializedTableView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		// super.createPartControl(parent);

		ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) GeneralManager.getDataDomainManagerInstance()
				.getDataDomainByID(((ASerializedSingleTablePerspectiveBasedView) serializedView).getDataDomainID());

		TablePerspective tablePerspective = dataDomain
				.getTablePerspective(((ASerializedSingleTablePerspectiveBasedView) serializedView)
						.getTablePerspectiveKey());

		Pair<NatTable, DataProvider> pair = NatTableBuilder.create(parent, tablePerspective);
		this.table = pair.getFirst();
		this.data = pair.getSecond();
	}

	/**
	 * @param raw
	 */
	public void setDataRepresentation(boolean raw) {
		this.data.setReturnRaw(raw);
		table.refresh();
	}

	@Override
	public void addToolBarContent() {
		toolBarManager.add(new SelectDataRepresentationAction(this, true));
		toolBarManager.add(new SelectDataRepresentationAction(this, false));
		super.addToolBarContent();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedTableView();
		determineDataConfiguration(serializedView);
	}

}