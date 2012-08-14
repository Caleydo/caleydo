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
package org.caleydo.datadomain.pathway.listener;

import java.util.Set;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class LoadPathwaysByGeneListener extends APathwayLoaderListener {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof LoadPathwaysByGeneEvent) {
			LoadPathwaysByGeneEvent loadEvent = (LoadPathwaysByGeneEvent) event;

			if (loadEvent.getIdType().getIDCategory() == IDCategory.getIDCategory("GENE")) {
				Set<PathwayGraph> pathwayGraphs = PathwayManager.get()
						.getPathwayGraphsByGeneID(loadEvent.getIdType(),
								loadEvent.getGeneID());
				if (pathwayGraphs == null) {
					Logger.log(new Status(IStatus.WARNING, this.toString(),
							"No mapping found for Gene ID to pathway graphs."));
					return;
				}
				handler.loadDependentPathways(pathwayGraphs);
			} else
				throw new IllegalStateException("Not implemented!");
		}
	}
}
