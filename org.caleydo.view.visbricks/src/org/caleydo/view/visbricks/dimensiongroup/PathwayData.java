package org.caleydo.view.visbricks.dimensiongroup;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.IGraphItem;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.PathwayDataConfigurer;

public class PathwayData implements IDimensionGroupData {

	private IDataDomain dataDomain;
	private ArrayList<PathwayGraph> pathways;

	@Override
	public ContentVirtualArray getSummaryBrickVA() {
		// TODO: Is this a good way?
		ArrayList<ContentVirtualArray> contentVAs = getSegmentBrickVAs();
		ArrayList<Integer> summaryBrickIDs = new ArrayList<Integer>();
		for(ContentVirtualArray contentVA : contentVAs) {
			summaryBrickIDs.addAll(contentVA.getVirtualArray());
		}
		
		return new ContentVirtualArray("CONTENT", summaryBrickIDs);
	}

	@Override
	public ArrayList<ContentVirtualArray> getSegmentBrickVAs() {

		ArrayList<ContentVirtualArray> contentVAs = new ArrayList<ContentVirtualArray>();

		for (PathwayGraph pathway : pathways) {
			List<IGraphItem> items = pathway
					.getAllItemsByKind(EGraphItemKind.NODE);

			List<Integer> ids = new ArrayList<Integer>();

			for (IGraphItem item : items) {
				if (item instanceof PathwayVertexGraphItem) {
					int davidId = PathwayItemManager.get()
							.getDavidIdByPathwayVertexGraphItem(
									(PathwayVertexGraphItem) item);
					// TODO: map to contentVAType?
					// GeneralManager.get().getIDMappingManager().getID(IDType.getIDType("DAVID"),
					// destination, davidId);
					ids.add(davidId);
				}

			}

			contentVAs.add(new ContentVirtualArray("CONTENT", ids));
		}

		return contentVAs;
	}

	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public IBrickConfigurer getBrickConfigurer() {
		return new PathwayDataConfigurer();
	}

	public ArrayList<PathwayGraph> getPathways() {
		return pathways;
	}

	public void setPathways(ArrayList<PathwayGraph> pathways) {
		this.pathways = pathways;
	}

}
