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
package org.caleydo.view.selectionbrowser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.core.view.swt.ASWTView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * View showing selections.
 * 
 * @author Marc Streit
 */
public class RcpSelectionBrowserView extends CaleydoRCPViewPart {

	public static String VIEW_TYPE = "org.caleydo.view.selectionbrowser";

	public RcpSelectionBrowserView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedSelectionBrowserView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NULL);

		parentComposite.setLayout(new GridLayout(1, false));

		this.parentComposite = parentComposite;

		Composite infoComposite = new Composite(this.parentComposite, SWT.NULL);
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout;
		layout = new GridLayout(1, false);

		layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;

		infoComposite.setLayout(layout);

		view = new SelectionBrowserView(infoComposite);

		if (view instanceof IDataDomainBasedView<?>) {
			((IDataDomainBasedView<IDataDomain>) view).setDataDomain(DataDomainManager
					.get().getDataDomainByID(
							((ASerializedSingleTablePerspectiveBasedView) serializedView)
									.getDataDomainID()));
		}

		((ASWTView) view).draw();

	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		// selectionBrowser.dispose();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedSelectionBrowserView();
		determineDataConfiguration(serializedView);
	}
}
