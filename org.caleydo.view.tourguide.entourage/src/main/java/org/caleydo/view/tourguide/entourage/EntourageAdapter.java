/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.tourguide.entourage;

import java.util.Collection;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
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
import org.caleydo.view.tourguide.api.model.AScoreRow;
import org.caleydo.view.tourguide.api.model.ITablePerspectiveScoreRow;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.entourage.ui.DataDomainElements;
import org.caleydo.view.tourguide.entourage.ui.GroupElements;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapter;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.ui.IWorkbenchPart;

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
		lineUp.add(0, wrap("Choose Datasets to Map", this.dataDomains, 130));
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
		lineUp.add(lineUp.size() - 1, wrap("Choose Groupings", this.groups, 160));
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


	private GLElementContainer wrap(String label, GLElement content, int width) {
		GLElementContainer c = new GLElementContainer(GLLayouts.flowVertical(2));
		c.add(drawText(label));
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(AScoreRow old, AScoreRow new_, Collection<IScore> visibleScores, EDataDomainQueryMode mode,
			IScore sortedByScore) {
		if (mode != EDataDomainQueryMode.STRATIFICATIONS)
			return;
		assert old == null || old instanceof ITablePerspectiveScoreRow;
		assert new_ == null || new_ instanceof ITablePerspectiveScoreRow;
		// TODO Auto-generated method stub

		Perspective perspecitive = toPerspective((ITablePerspectiveScoreRow) new_);
		groups.set(perspecitive);
	}

	/**
	 * @param new_
	 * @return
	 */
	private Perspective toPerspective(ITablePerspectiveScoreRow new_) {
		if (new_ == null)
			return null;
		TablePerspective t = new_.asTablePerspective();
		// TODO correctly determine which dimension
		return t.getRecordPerspective();
	}


	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		final IDataDomain dataDomain = button.getLayoutDataAs(IDataDomain.class, null);
		if (dataDomain != null) {
			// TODO dataDomain selection update
			return;
		}

		final Group group = button.getLayoutDataAs(Group.class, null);
		if (group != null) {
			// TODO groupn selection update
			return;
		}
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
