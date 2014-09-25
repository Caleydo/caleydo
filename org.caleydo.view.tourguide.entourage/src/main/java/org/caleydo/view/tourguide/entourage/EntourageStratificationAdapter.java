/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.datadomain.genetic.GeneActions;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.entourage.datamapping.DataMappingState;
import org.caleydo.view.tourguide.api.adapter.TourGuideDataModes;
import org.caleydo.view.tourguide.api.model.ADataDomainQuery;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.entourage.ui.DataDomainElements;
import org.caleydo.view.tourguide.entourage.ui.DataDomainHeader;
import org.caleydo.view.tourguide.entourage.ui.GroupElements;
import org.caleydo.view.tourguide.entourage.ui.GroupElements.IGroupCallback;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideDataMode;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;

import com.google.common.base.Predicate;

/**
 * adapter for tourguide to the entourage view
 *
 * @author Samuel Gratzl
 *
 */
public class EntourageStratificationAdapter extends AEntourageAdapter implements ISelectionCallback, IGroupCallback {
	private final DataDomainElements dataDomains = new DataDomainElements();
	private final GroupElements groups = new GroupElements(this);
	private final ISelectionCallback onNodeCallback;

	/**
	 * @param entourage
	 * @param vis
	 */
	public EntourageStratificationAdapter() {
		this.dataDomains.setCallback(this);

		for (GeneticDataDomain d : DataDomainManager.get().getDataDomainsByType(GeneticDataDomain.class)) {
			this.dataDomains.addDataDomain(d);
		}
		this.onNodeCallback = new ISelectionCallback() {
			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				setOnNodeElement(button == null ? null : button.getLayoutDataAs(ATableBasedDataDomain.class, null));
			}
		};
		this.dataDomains.setOnNodeCallback(onNodeCallback);
	}

	@Override
	public String getSecondaryID() {
		return EntourageStratificationAdapterFactory.SECONDARY_ID;
	}

	@Override
	public String getPartName() {
		return "Stratifications";
	}

	@Override
	public void setup(ITourGuideView vis, GLElementContainer lineUp) {
		super.setup(vis, lineUp);
		lineUp.add(
				0,
				wrap("Mapping Datasets", "Select which datasets to visualize", this.dataDomains, 200,
						new DataDomainHeader().setSize(-1, 38), dataDomains.getOnNoneNode()));

		GLElementContainer body = new GLElementContainer(GLLayouts.flowVertical(2));
		body.add(drawText("Stratification", "Select the stratification (grouping) of the data"));
		lineUp.add(1, body); // add already here such existing elements will be moved instead of takeDown/setup stuff
		// tree
		// body V
		// -text
		// -c H
		// --filterStratifications V
		// ---text
		// ---chooser
		// --table
		GLElementContainer c = new GLElementContainer(GLLayouts.flowHorizontal(10));
		body.add(c);
		GLElementContainer filterStratifications = new GLElementContainer(GLLayouts.flowVertical(2));
		filterStratifications.add(drawText("Filter Stratifications",
				"Select the datasets that are associated with statifications", 16));
		filterStratifications.setSize(130, -1);
		c.add(filterStratifications);
		filterStratifications.add(lineUp.get(2)); // move data domain selector
		c.add(lineUp.get(2)); // move table

		{
			GLElementContainer g = new GLElementContainer(GLLayouts.flowVertical(2));
			g.add(drawText("Groups", "Select the groups to show"));
			g.add(this.groups.createSelectAllNone());
			g.add(this.groups);
			g.setSize(250, -1);
			lineUp.add(lineUp.size() - 1, g);
		}

		if (isBound2View())
			loadViewState();
	}

	@Override
	public void addDefaultColumns(RankTableModel table) {
		asMode().addDefaultColumns(table);
		List<ARankColumnModel> c = table.getColumns();
		c.get(c.size() - 1).hide();
	}

	@Override
	public ITourGuideDataMode asMode() {
		return TourGuideDataModes.STRATIFICATIONS;
	}

	/**
	 *
	 */
	@Override
	protected void loadViewState() {
		loadDataDomainSelection();
		loadStratificationSelection();
	}

	private void loadStratificationSelection() {
		final Perspective active = getSourcePerspective();
		if (active == null)
			return;
		IDataDomain dataDomain = active.getDataDomain();
		for (ADataDomainQuery query : vis.getQueries()) {
			if (dataDomain.equals(query.getDataDomain())) {
				query.setActive(true);
				for (AScoreRow r : query.getOrCreate()) {
					if (r.is(active)) {
						vis.setSelection(r);
						break;
					}
				}
			} else
				query.setActive(false);
		}
		vis.updateBound2ViewState();
	}

	private void loadDataDomainSelection() {
		dataDomains.setCallback(null);
		dataDomains.setOnNodeCallback(null);
		for (GLButton b : dataDomains.getSelectionButtons()) {
			final ATableBasedDataDomain d = b.getLayoutDataAs(ATableBasedDataDomain.class, null);
			assert d != null;
			b.setSelected(isDataDomainVisible(d));
		}
		dataDomains.setActiveOnNodeDataDomain(getPathwayMappingDataDomain());
		dataDomains.setOnNodeCallback(onNodeCallback);
		dataDomains.setCallback(this);
	}

	@Override
	public void cleanup() {
		super.cleanup();
	}

	private GLElementContainer wrap(String label, String toolTip, GLElement content, int width, GLElement... postLabel) {
		GLElementContainer c = new GLElementContainer(GLLayouts.flowVertical(2));
		c.add(drawText(label, toolTip));
		for (GLElement p : postLabel)
			c.add(p);
		c.add(ScrollingDecorator.wrap(content, 8, EDimension.RECORD));
		c.setSize(width, -1);
		return c;
	}

	private GLElement drawText(String label, String toolTip) {
		return drawText(label, toolTip, 20);
	}

	private GLElement drawText(String label, String toolTip, int size) {
		PickableGLElement element = new PickableGLElement();
		element.setRenderer(GLRenderers.drawText(label, VAlign.LEFT, new GLPadding(4, 2), ETextStyle.BOLD));
		element.setSize(-1, size);
		element.setTooltip(toolTip);
		return element;
	}

	@Override
	public boolean isPreviewing(AScoreRow row) {
		return false; // just a single selection
	}

	@Override
	public boolean isVisible(AScoreRow row) {
		assert row instanceof ITablePerspectiveScoreRow;
		Perspective perspective = toPerspective((ITablePerspectiveScoreRow) row);
		return perspective.equals(getSourcePerspective());
	}

	private Perspective getSourcePerspective() {
		if (entourage == null)
			return null;
		return entourage.getDataMappingState().getSourcePerspective();
	}

	private boolean isDataDomainVisible(ATableBasedDataDomain dataDomain) {
		if (entourage == null)
			return false;
		DataMappingState dmState = entourage.getDataMappingState();
		return dmState.getDataDomains().contains(dataDomain);
	}

	private boolean isGroupVisible(Group group) {
		if (entourage == null)
			return false;
		DataMappingState dmState = entourage.getDataMappingState();
		GroupList groupList = dmState.getSelectedPerspective().getVirtualArray().getGroupList();
		for (Group g : groupList) {
			if (g.equals(group))
				return true;
		}
		return false;
	}

	private ATableBasedDataDomain getPathwayMappingDataDomain() {
		if (entourage == null)
			return null;
		TablePerspective tablePerspective = entourage.getDataMappingState().getPathwayMappedTablePerspective();
		if (tablePerspective == null)
			return null;
		return tablePerspective.getDataDomain();
	}

	@Override
	public void update(AScoreRow old, AScoreRow new_, Collection<IScore> visibleScores, IScore sortedByScore) {
		if (entourage == null)
			return;
		assert old == null || old instanceof ITablePerspectiveScoreRow;
		assert new_ == null || new_ instanceof ITablePerspectiveScoreRow;
		// TODO Auto-generated method stub

		Perspective perspective = toPerspective((ITablePerspectiveScoreRow) new_);
		DataMappingState dmState = entourage.getDataMappingState();
		// Prevents to loose group selections when gaining new focus
		if (dmState.getSourcePerspective() != perspective)
			dmState.setSelectedPerspective(perspective);
		dmState.setSourcePerspective(perspective);
		groups.set(perspective);

		loadGroupState();
	}

	/**
	 *
	 */
	private void loadGroupState() {
		groups.set(new Predicate<Group>() {
			@Override
			public boolean apply(Group input) {
				return input != null && isGroupVisible(input);
			}
		});
	}

	/**
	 * @param new_
	 * @return
	 */
	private Perspective toPerspective(ITablePerspectiveScoreRow new_) {
		if (new_ == null)
			return null;
		TablePerspective t = new_.asTablePerspective();
		if (areRecordsGenes(t.getDataDomain()))
			return t.getDimensionPerspective();

		// Take the record perspective if the datadomain has no genes.
		return t.getRecordPerspective();
	}

	private boolean areRecordsGenes(ATableBasedDataDomain dataDomain) {
		if (dataDomain instanceof GeneticDataDomain) {
			GeneticDataDomain dd = (GeneticDataDomain) dataDomain;
			IDCategory geneIDCategory = dd.getGeneIDType().getIDCategory();
			return dd.getRecordIDCategory().equals(geneIDCategory);
		}
		return false;
	}

	/**
	 * @param aTableBasedDataDomain
	 */
	protected void setOnNodeElement(ATableBasedDataDomain dataDomain) {
		if (entourage == null)
			return;
		entourage.getDataMappingState().setPathwayMappedDataDomain(dataDomain);
	}

	@Override
	public void onGroupSelectionChanged(Group group, boolean selected) {
		if (entourage == null)
			return;
		DataMappingState dmState = entourage.getDataMappingState();
		Perspective sourcePerspective = dmState.getSourcePerspective();
		Perspective currentPerspective = dmState.getSelectedPerspective();

		if (sourcePerspective != null && currentPerspective != null) {
			VirtualArray va = sourcePerspective.getVirtualArray();
			GroupList sourceGroupList = va.getGroupList();
			GroupList currentGroupList = currentPerspective.getVirtualArray().getGroupList();

			ATableBasedDataDomain dd = (ATableBasedDataDomain) sourcePerspective.getDataDomain();
			List<Integer> indices = new ArrayList<>(va.size());
			List<Integer> groupSizes = new ArrayList<>(currentGroupList.size() + 1);
			List<Integer> sampleElements = new ArrayList<>(currentGroupList.size() + 1);
			List<String> groupNames = new ArrayList<>(currentGroupList.size() + 1);

			int currentGroupIndex = 0;
			for (int i = 0; i < sourceGroupList.size(); i++) {
				Group sourceGroup = sourceGroupList.get(i);
				Group currentGroup = null;
				if (currentGroupIndex < currentGroupList.size())
					currentGroup = currentGroupList.get(currentGroupIndex);
				if (sourceGroup.equals(currentGroup)) {
					currentGroupIndex++;
					if (!(!selected && group.equals(sourceGroup))) {
						addGroupToLists(indices, groupSizes, sampleElements, groupNames, sourceGroup, va);
					}
				}
				if (selected && group.equals(sourceGroup)) {
					addGroupToLists(indices, groupSizes, sampleElements, groupNames, sourceGroup, va);
				}
			}
			Perspective perspective = new Perspective(dd, sourcePerspective.getIdType());
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(indices, groupSizes, sampleElements, groupNames);
			perspective.init(data);
			perspective.setLabel(sourcePerspective.getLabel(), true);
			perspective.setPrivate(true);

			dmState.setSelectedPerspective(perspective);
		}
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		if (entourage == null)
			return;
		final ATableBasedDataDomain dataDomain = button.getLayoutDataAs(ATableBasedDataDomain.class, null);
		DataMappingState dmState = entourage.getDataMappingState();
		if (dataDomain != null) {
			if (selected)
				dmState.addDataDomain(dataDomain);
			else
				dmState.removeDataDomain(dataDomain);
			return;
		}
	}

	private void addGroupToLists(List<Integer> indices, List<Integer> groupSizes, List<Integer> sampleElements,
			List<String> groupNames, Group group, VirtualArray va) {
		indices.addAll(va.getIDsOfGroup(group.getGroupIndex()));
		groupSizes.add(group.getSize());
		sampleElements.add(group.getRepresentativeElementIndex());
		groupNames.add(group.getLabel());
	}

	@Override
	public boolean canShowPreviews() {
		return true;
	}

	@Override
	public void onRowClick(RankTableModel table, PickingMode pickingMode, AScoreRow row, boolean isSelected,
			IGLElementContext context) {
		switch (pickingMode) {
		case RIGHT_CLICKED:
			ContextMenuCreator creator = new ContextMenuCreator();

			if (row instanceof ITablePerspectiveScoreRow) {
				TablePerspective tablePerspective = ((ITablePerspectiveScoreRow) row).asTablePerspective();
				Perspective perspective = null;
				if (!(tablePerspective.getDataDomain() instanceof GeneticDataDomain))
					return;
				if (areRecordsGenes(tablePerspective.getDataDomain())) {
					perspective = tablePerspective.getRecordPerspective();
				} else {
					perspective = tablePerspective.getDimensionPerspective();
				}
				VirtualArray va = perspective.getVirtualArray();
				if (va.size() == 1) {

					GeneActions.addToContextMenu(creator, va.get(0), perspective.getIdType(), this, true);
					context.getSWTLayer().showContextMenu(creator);

					// Set<PathwayGraph> pathways =
					// PathwayManager.get().getPathwayGraphsByGeneID(perspective.getIdType(),
					// va.get(0));
					// int numPathways = pathways == null ? 0 : pathways.size();
					// LoadPathwaysEvent event = new LoadPathwaysEvent(pathways == null ? new HashSet<PathwayGraph>()
					// : pathways);
					// creator.addContextMenuItem(new GenericContextMenuItem("Show Pathways with " + row.getLabel() +
					// " ("
					// + numPathways + " pathways available)", event));
					// context.getSWTLayer().showContextMenu(creator);
				}
			}

			break;
		default:
			break;
		}
	}
}
