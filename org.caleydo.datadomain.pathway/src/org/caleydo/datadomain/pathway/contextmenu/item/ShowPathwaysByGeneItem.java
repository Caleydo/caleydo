/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.contextmenu.item;

import java.util.Arrays;
import java.util.Set;

import org.caleydo.core.data.datadomain.DataDomainActions.ChooserRunnable;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.listener.LoadPathwayEvent;
import org.caleydo.datadomain.pathway.listener.LoadPathwaysByGeneEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * <p>
 * Item for showing all pathways that contain a specific gene in a sub menu, where these pathways can be loaded
 * individually. The sub-pathways can either be specified manually or the convenience method
 * {@link ShowPathwaysByGeneItem#setRefSeqInt(int)} can be used, which creates the sub-menus automatically.
 * </p>
 * <p>
 * Text and icon have default values but can be overriden.
 * </p>
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ShowPathwaysByGeneItem extends AContextMenuItem {

	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ShowPathwaysByGeneItem(PathwayDataDomain dataDomain) {
		super();
		setLabel("Load pathways");
	}

	/**
	 * Convenience method that automatically creates a {@link LoadPathwaysByGeneEvent} based on a david ID
	 *
	 */
	public void setDavidID(IDType idType, int david) {

		Set<PathwayGraph> pathwayGraphs = PathwayManager.get().getPathwayGraphsByGeneID(idType, david);

		int pathwayCount = 0;

		if (pathwayGraphs != null && pathwayGraphs.size() > 0) {

			pathwayCount = pathwayGraphs.size();

			PathwayGraph[] pathways = new PathwayGraph[pathwayGraphs.size()];
			pathwayGraphs.toArray(pathways);
			Arrays.sort(pathways);

			for (PathwayGraph pathwayGraph : pathways) {
				addSubItem(new LoadPathwaysByPathwayItem(pathwayGraph));
			}
		}

		setLabel("Load Pathways (" + pathwayCount + ")");
	}

	public static Pair<String, Runnable> createChooser(PathwayDataDomain pathwayDataDomain, IDType idType,
			Integer david, Object sender) {
		Set<PathwayGraph> pathwayGraphs = PathwayManager.get().getPathwayGraphsByGeneID(idType, david);

		int pathwayCount = 0;

		ChooserRunnable r = new ChooserRunnable();
		if (pathwayGraphs != null && pathwayGraphs.size() > 0) {

			pathwayCount = pathwayGraphs.size();

			PathwayGraph[] pathways = new PathwayGraph[pathwayGraphs.size()];
			pathwayGraphs.toArray(pathways);
			Arrays.sort(pathways);

			for (PathwayGraph pathwayGraph : pathways) {
				LoadPathwayEvent loadPathwayEvent = new LoadPathwayEvent();
				loadPathwayEvent.setSender(sender);
				loadPathwayEvent.setPathwayID(pathwayGraph.getID());
				r.add(Pair.make(pathwayGraph.getTitle(), Runnables.sendEvent(loadPathwayEvent)));
			}
		}

		final String label = "Load Pathways (" + pathwayCount + ")";
		return Pair.make(label, (Runnable) r);
	}
}
