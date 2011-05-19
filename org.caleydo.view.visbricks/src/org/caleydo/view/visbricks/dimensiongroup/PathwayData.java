package org.caleydo.view.visbricks.dimensiongroup;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexGraphItem;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;
import org.caleydo.view.visbricks.brick.layout.IBrickConfigurer;
import org.caleydo.view.visbricks.brick.layout.PathwayDataConfigurer;

public class PathwayData extends AUniqueObject implements IDimensionGroupData {

	private IDataDomain dataDomain;
	private ArrayList<PathwayGraph> pathways;
	private ArrayList<Group> groups;
	
	{
		uniqueID = GeneralManager.get().getIDCreator().createID(EManagedObjectType.SET);
	}

	public PathwayData(IDataDomain dataDomain, ArrayList<PathwayGraph> pathways) {
		this.dataDomain = dataDomain;
		this.pathways = pathways;
		createGroups();
	}

	private void createGroups() {
		groups = new ArrayList<Group>();

		int groupID = 0;
		int startIndex = 0;
		for (PathwayGraph pathway : pathways) {

			Group group = new Group();
			group.setGroupID(groupID);
			group.setStartIndex(startIndex);

			List<IGraphItem> vertexGraphItemReps = pathway
					.getAllItemsByKind(EGraphItemKind.NODE);

			int groupSize = 0;
			for (IGraphItem itemRep : vertexGraphItemReps) {

				List<IGraphItem> vertexGraphItems = itemRep
						.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT);

				for (IGraphItem item : vertexGraphItems) {
					int davidId = PathwayItemManager.get()
							.getDavidIdByPathwayVertexGraphItem(
									(PathwayVertexGraphItem) item);

					if (davidId != -1) {
						groupSize++;
					}
				}
			}

			group.setSize(groupSize);
			groups.add(group);
			startIndex += groupSize;
			groupID++;
		}
	}

	@Override
	public ContentVirtualArray getSummaryBrickVA() {
		// TODO: Is this a good way?
		ArrayList<ContentVirtualArray> contentVAs = getSegmentBrickVAs();
		ArrayList<Integer> summaryBrickIDs = new ArrayList<Integer>();
		for (ContentVirtualArray contentVA : contentVAs) {
			summaryBrickIDs.addAll(contentVA.getVirtualArray());
		}
		
		ContentGroupList groupList = new ContentGroupList();
		groupList.setGroups(groups);
		ContentVirtualArray summaryBrickVA = new ContentVirtualArray("CONTENT", summaryBrickIDs);
		summaryBrickVA.setGroupList(groupList);

		return summaryBrickVA;
	}

	@Override
	public ArrayList<ContentVirtualArray> getSegmentBrickVAs() {

		ArrayList<ContentVirtualArray> contentVAs = new ArrayList<ContentVirtualArray>();

		for (PathwayGraph pathway : pathways) {
			List<IGraphItem> vertexGraphItemReps = pathway
					.getAllItemsByKind(EGraphItemKind.NODE);

			List<Integer> ids = new ArrayList<Integer>();

			for (IGraphItem itemRep : vertexGraphItemReps) {

				List<IGraphItem> vertexGraphItems = itemRep
						.getAllItemsByProp(EGraphItemProperty.ALIAS_PARENT);

				for (IGraphItem item : vertexGraphItems) {
					int davidId = PathwayItemManager.get()
							.getDavidIdByPathwayVertexGraphItem(
									(PathwayVertexGraphItem) item);

					if (davidId != -1) {
						// TODO: Map to content id type (given as parameter)
						// GeneralManager.get().getIDMappingManager().getIDAsSet(IDType.getIDType("DAVID"),
						// destination, davidId);

						ids.add(davidId);
					}
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

	@Override
	public ArrayList<Group> getGroups() {
		return groups;
	}

}
