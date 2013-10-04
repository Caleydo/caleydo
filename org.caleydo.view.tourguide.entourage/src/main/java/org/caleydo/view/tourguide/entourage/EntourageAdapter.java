/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.entourage.GLEntourage;
import org.caleydo.view.entourage.RcpGLSubGraphView;
import org.caleydo.view.entourage.datamapping.DataMappingState;
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.entourage.ui.DataDomainElements;
import org.caleydo.view.tourguide.entourage.ui.DataDomainHeader;
import org.caleydo.view.tourguide.entourage.ui.GroupElements;
import org.caleydo.view.tourguide.entourage.ui.SelectAllNoneElement;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapter;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.ui.IWorkbenchPart;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class EntourageAdapter implements IViewAdapter, ISelectionCallback {

	private final GLEntourage entourage;
	private final ITourGuideView vis;

	private final DataDomainElements dataDomains = new DataDomainElements();
	private final GroupElements groups = new GroupElements();

	/**
	 * @param entourage
	 * @param vis
	 */
	public EntourageAdapter(GLEntourage entourage, ITourGuideView vis) {
		this.entourage = entourage;
		this.vis = vis;
		this.dataDomains.setCallback(this);
		this.groups.setCallback(this);

		for (GeneticDataDomain d : DataDomainManager.get().getDataDomainsByType(GeneticDataDomain.class)) {
			this.dataDomains.addDataDomain(d);
		}
		this.dataDomains.setOnNodeCallback(new ISelectionCallback() {
			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				setOnNodeElement(button == null ? null : button.getLayoutDataAs(ATableBasedDataDomain.class, null));
			}
		});
	}

	@Override
	public void attach() {
		// TODO Auto-generated method stub

	}

	@Override
	public void detach() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setup(GLElementContainer lineUp) {
		lineUp.add(
				0,
				wrap("Choose Datasets to Map", this.dataDomains, 200, new DataDomainHeader().setSize(-1, 38),
						dataDomains.getOnNodeNode()));
		GLElementContainer body = new GLElementContainer(GLLayouts.flowVertical(2));
		body.add(drawText("Choose Stratification"));
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
		filterStratifications.add(drawText("Filter Stratifications", 12));
		filterStratifications.setSize(130, -1);
		c.add(filterStratifications);
		filterStratifications.add(lineUp.get(2)); // move data domain selector
		c.add(lineUp.get(2)); // move table

		lineUp.add(
				lineUp.size() - 1,
				wrap("Choose Groups", this.groups, 160,
						new SelectAllNoneElement(Iterables.filter(this.groups, GLButton.class))));

		loadState();
	}

	/**
	 *
	 */
	private void loadState() {
		dataDomains.setCallback(null);
		for (GLButton b : dataDomains.getSelectionButtons()) {
			final ATableBasedDataDomain d = b.getLayoutDataAs(ATableBasedDataDomain.class, null);
			assert d != null;
			b.setSelected(isDataDomainVisible(d));
		}
		dataDomains.setCallback(this);
	}

	@Override
	public void cleanup(GLElementContainer lineUp) {
		// undo everything
		lineUp.remove(0); // remove choose datasets
		lineUp.remove(lineUp.size() - 2); // remove choose groupings

		final GLElementContainer body = (GLElementContainer) lineUp.get(0);
		GLElementContainer c = (GLElementContainer) body.get(1);
		lineUp.add(0, ((GLElementContainer) c.get(0)).get(1)); // move dataset again
		lineUp.add(1, c.get(1)); // move table
		lineUp.remove(2); // remove wrapper
	}

	private GLElementContainer wrap(String label, GLElement content, int width, GLElement... postLabel) {
		GLElementContainer c = new GLElementContainer(GLLayouts.flowVertical(2));
		c.add(drawText(label));
		for (GLElement p : postLabel)
			c.add(p);
		c.add(ScrollingDecorator.wrap(content, 8));
		c.setSize(width, -1);
		return c;
	}

	private GLElement drawText(String label) {
		return drawText(label, 20);
	}

	private GLElement drawText(String label, int size) {
		return new GLElement(GLRenderers.drawText(label, VAlign.LEFT, new GLPadding(4, 2), ETextStyle.BOLD)).setSize(
				-1, size);
	}

	@Override
	public boolean isPreviewing(AScoreRow row) {
		assert row instanceof ITablePerspectiveScoreRow;
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVisible(AScoreRow row) {
		assert row instanceof ITablePerspectiveScoreRow;
		Perspective perspective = toPerspective((ITablePerspectiveScoreRow) row);
		return perspective.equals(entourage.getDataMappingState().getSourcePerspective());
	}

	public boolean isDataDomainVisible(ATableBasedDataDomain dataDomain) {
		DataMappingState dmState = entourage.getDataMappingState();
		return dmState.getDataDomains().contains(dataDomain);
	}

	public boolean isGroupVisible(Group group) {
		DataMappingState dmState = entourage.getDataMappingState();
		GroupList groupList = dmState.getSelectedPerspective().getVirtualArray().getGroupList();
		for (Group g : groupList) {
			if (g.equals(group))
				return true;
		}
		return false;
	}

	public GroupList getSourceGroupList() {
		return entourage.getDataMappingState().getSourcePerspective().getVirtualArray().getGroupList();
	}

	public ATableBasedDataDomain getPathwayMappingDataDomain() {
		TablePerspective tablePerspective = entourage.getDataMappingState().getPathwayMappedTablePerspective();
		if (tablePerspective == null)
			return null;
		return tablePerspective.getDataDomain();
	}

	@Override
	public void update(AScoreRow old, AScoreRow new_, Collection<IScore> visibleScores, EDataDomainQueryMode mode,
			IScore sortedByScore) {
		if (mode != EDataDomainQueryMode.STRATIFICATIONS)
			return;
		assert old == null || old instanceof ITablePerspectiveScoreRow;
		assert new_ == null || new_ instanceof ITablePerspectiveScoreRow;
		// TODO Auto-generated method stub

		Perspective perspective = toPerspective((ITablePerspectiveScoreRow) new_);
		DataMappingState dmState = entourage.getDataMappingState();
		dmState.setSourcePerspective(perspective);
		dmState.setSelectedPerspective(perspective);
		groups.set(perspective);

		loadGroupState();
	}

	/**
	 *
	 */
	private void loadGroupState() {
		groups.setCallback(null);
		for (GLButton b : Iterables.filter(groups, GLButton.class)) {
			final Group g = b.getLayoutDataAs(Group.class, null);
			assert g != null;
			b.setSelected(isGroupVisible(g));
		}
		groups.setCallback(this);
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
		entourage.getDataMappingState().setPathwayMappedDataDomain(dataDomain);
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		final ATableBasedDataDomain dataDomain = button.getLayoutDataAs(ATableBasedDataDomain.class, null);
		DataMappingState dmState = entourage.getDataMappingState();
		if (dataDomain != null) {
			if (selected)
				dmState.addDataDomain(dataDomain);
			else
				dmState.removeDataDomain(dataDomain);
			return;
		}

		final Group group = button.getLayoutDataAs(Group.class, null);
		if (group != null) {
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
	public void preDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canShowPreviews() {
		return true;
	}

	@Override
	public boolean isRepresenting(IWorkbenchPart part) {
		if (part instanceof RcpGLSubGraphView)
			return ((RcpGLSubGraphView) part).getView() == entourage;
		return false;
	}
}
