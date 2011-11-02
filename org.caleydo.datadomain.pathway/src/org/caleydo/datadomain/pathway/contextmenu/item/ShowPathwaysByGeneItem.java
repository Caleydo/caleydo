package org.caleydo.datadomain.pathway.contextmenu.item;

import java.util.Arrays;
import java.util.Set;

import org.caleydo.core.data.id.IDType;
import org.caleydo.core.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * <p>
 * Item for showing all pathways that contain a specific gene in a sub menu,
 * where these pathways can be loaded individually. The sub-pathways can either
 * be specified manually or the convenience method
 * {@link ShowPathwaysByGeneItem#setRefSeqInt(int)} can be used, which creates
 * the sub-menus automatically.
 * </p>
 * <p>
 * Text and icon have default values but can be overriden.
 * </p>
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ShowPathwaysByGeneItem extends AContextMenuItem {

	private PathwayDataDomain dataDomain;
	
	/**
	 * Constructor which sets the default values for icon and text
	 */
	public ShowPathwaysByGeneItem(PathwayDataDomain dataDomain) {
		super();
		this.dataDomain = dataDomain;
		//setIconTexture(EIconTextures.CM_DEPENDING_PATHWAYS);
		setLabel("Load Pathways");
	}

	/**
	 * Convenience method that automatically creates a
	 * {@link LoadPathwaysByGeneEvent} based on a david ID
	 * 
	 */
	public void setDavidID(IDType idType, int david, String dataDomainID) {

		Set<PathwayGraph> pathwayGraphs = dataDomain.getMappingHelper()
				.getPathwayGraphsByGeneID(idType, david);

		int pathwayCount = 0;

		if (pathwayGraphs != null) {

			pathwayCount = pathwayGraphs.size();

			PathwayGraph[] pathways = new PathwayGraph[pathwayGraphs.size()];
			pathwayGraphs.toArray(pathways);
			Arrays.sort(pathways);

			for (PathwayGraph pathwayGraph : pathways) {
				addSubItem(new LoadPathwaysByPathwayItem(pathwayGraph, dataDomainID));
			}
		}

		setLabel("Load Pathways (" + pathwayCount + ")");
	}
}
