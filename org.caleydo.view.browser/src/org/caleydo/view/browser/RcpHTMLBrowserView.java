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
package org.caleydo.view.browser;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.core.view.swt.ASWTView;
import org.eclipse.swt.widgets.Composite;

public class RcpHTMLBrowserView extends CaleydoRCPViewPart {

	@Override
	@SuppressWarnings("unchecked")
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GenomeHTMLBrowser(parentComposite);

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
		// browserView.unregisterEventListeners();
		// GeneralManager.get().getViewGLCanvasManager().unregisterItem(browserView.getID());
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedHTMLBrowserView();
		determineDataConfiguration(serializedView);
	}
}
