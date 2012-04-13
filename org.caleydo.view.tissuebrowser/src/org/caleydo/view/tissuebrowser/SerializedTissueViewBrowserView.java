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
package org.caleydo.view.tissuebrowser;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.serialize.ASerializedView;

/**
 * Serialized form of the tissue browser view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedTissueViewBrowserView extends ASerializedTopLevelDataView {

	/** list of initially contained view-ids */
	private List<ASerializedView> initialContainedViews;

	/**
	 * No-Arg Constructor to create a serialized view browser view with default
	 * parameters.
	 */
	public SerializedTissueViewBrowserView() {
		init();
	}

	public SerializedTissueViewBrowserView(String dataDomainType) {
		super(dataDomainType);
		init();
	}

	public void init() {
		initialContainedViews = new ArrayList<ASerializedView>();

		// SerializedPathwayView pathway = new SerializedPathwayView();
		// pathway
		// .setPathwayID(((PathwayGraph)
		// GeneralManager.get().getPathwayManager().getAllItems().toArray()[0])
		// .getID());
		// pathway.setDataDomain(EDataDomain.PATHWAY_DATA);
		// initialContainedViews.add(pathway);
	}

	@XmlElementWrapper
	public List<ASerializedView> getInitialContainedViews() {
		return initialContainedViews;
	}

	@Override
	public String getViewType() {
		return GLTissueViewBrowser.VIEW_TYPE;
	}
}
