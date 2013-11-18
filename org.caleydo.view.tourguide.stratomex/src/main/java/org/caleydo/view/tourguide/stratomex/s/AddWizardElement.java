/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.stratomex.s;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.DataDomainOracle;
import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLContextLocal;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.text.ETextStyle;
import org.caleydo.core.view.opengl.util.text.TextUtils;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.stratomex.brick.configurer.CategoricalDataConfigurer;
import org.caleydo.view.stratomex.brick.configurer.ClinicalDataConfigurer;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.sorting.NoSortingSortingStrategy;
import org.caleydo.view.tourguide.api.state.ABrowseState;
import org.caleydo.view.tourguide.api.state.IReactions;
import org.caleydo.view.tourguide.api.state.ISelectGroupState;
import org.caleydo.view.tourguide.api.state.ISelectStratificationState;
import org.caleydo.view.tourguide.api.state.IState;
import org.caleydo.view.tourguide.api.state.ITransition;
import org.caleydo.view.tourguide.api.state.SimpleTransition;
import org.caleydo.view.tourguide.api.vis.ITourGuideView;
import org.caleydo.view.tourguide.api.vis.TourGuideUtils;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.caleydo.view.tourguide.stratomex.StratomexRenderStyle;
import org.caleydo.view.tourguide.stratomex.event.UpdateNumericalPreviewEvent;
import org.caleydo.view.tourguide.stratomex.event.UpdatePathwayPreviewEvent;
import org.caleydo.view.tourguide.stratomex.event.UpdateStratificationPreviewEvent;
import org.caleydo.view.tourguide.stratomex.event.WizardEndedEvent;


/**
 * @author Samuel Gratzl
 *
 */
public class AddWizardElement extends ALayoutRenderer implements IReactions, IPickingLabelProvider {
	public static final String PICKING_TYPE = "templateWizard";

	@DeepScan
	private StateMachineImpl stateMachine;

	private final AGLView view;
	private GLContextLocal contextLocal;
	private int hovered = -1;

	private boolean initialized = false;

	private boolean canGoBack = true;

	private final TourGuideAddin adapter;

	public AddWizardElement(AGLView view, TourGuideAddin adapter, StateMachineImpl stateMachine) {
		this.adapter = adapter;
		contextLocal = new GLContextLocal(view.getTextRenderer(), view.getTextureManager(),
				ResourceLocators.classLoader(AddWizardElement.class.getClassLoader()));
		this.view = view;
		this.stateMachine = stateMachine;
		this.stateMachine.getCurrent().onEnter();
	}


	@Override
	public void prepare() {
		if (!initialized) {
			checkSelect();
			initialized = true;
		}
		super.prepare();
	}

	/**
	 * @param pick
	 */
	public void onPick(Pick pick) {
		switch (pick.getPickingMode()) {
		case CLICKED:
			IState current = stateMachine.getCurrent();
			List<ITransition> transitions = stateMachine.getTransitions(current);
			ITransition transition = transitions.get(pick.getObjectID());
			if (transition.isEnabled())
				transition.apply(this);
			repaint();
			break;
		case MOUSE_OVER:
			hovered = pick.getObjectID();
			repaint();
			break;
		case MOUSE_OUT:
			hovered = -1;
			repaint();
			break;
		default:
			break;
		}
	}

	@Override
	public String getLabel(Pick pick) {
		if (pick.getObjectID() < 0)
			return null;
		IState current = stateMachine.getCurrent();
		List<ITransition> transitions = stateMachine.getTransitions(current);
		ITransition transition = transitions.get(pick.getObjectID());
		return transition.getDisabledReason();
	}

	private void repaint() {
		setDisplayListDirty(true);
		layoutManager.setRenderingDirty();
	}

	private void checkSelect() {
		IState target = stateMachine.getCurrent();
		if (target instanceof ISelectStratificationState)
			adapter.selectStratification((ISelectStratificationState) target,
					((ISelectStratificationState) target).isAutoSelect());
		else if (target instanceof ISelectGroupState)
			adapter.selectGroup((ISelectGroupState) target, ((ISelectGroupState) target).isSelectAllSupported());
	}

	@Override
	protected void renderContent(GL2 gl) {
		final GLGraphics g = new GLGraphics(gl, contextLocal, false, 0);
		final float w = x;
		final float h = y;

		final PixelGLConverter converter = view.getPixelGLConverter();

		final float h_header = converter.getGLHeightForPixelHeight(100);
		final float h_category = converter.getGLHeightForPixelHeight(32);
		final float _1px = converter.getGLWidthForPixelWidth(1);
		final float _1pxh = converter.getGLHeightForPixelHeight(1);
		final float gap = h_header * 0.1f;

		IState current = stateMachine.getCurrent();
		Collection<ITransition> transitions = stateMachine.getTransitions(current);

		if (transitions.isEmpty()) {
			drawMultiLineText(g, current, 0, 0, w, h, false);
		} else {
			boolean firstStep = stateMachine.getPrevious() == null;
			Pair<Collection<ITransition>, Collection<ITransition>> split = splitInDependent(transitions);
			if (split.getSecond().isEmpty() || split.getFirst().isEmpty())
				firstStep = false;

			drawMultiLineText(g, current, 0, h - h_header, w, h_header, false);

			if (firstStep) {
				float hi = (h - h_header - transitions.size() * gap - 2 * h_category - 2 * gap - _1pxh * 3)
						/ (transitions.size());
				float y = h_header + gap;

				g.color(0.92f).fillRect(_1px, h - y - h_category, w - _1px * 2, h_category);
				g.drawText("Independent", 0, h - y - h_category * .75f, w, h_category * .5f, VAlign.CENTER);
				y += h_category + gap;
				renderTransitions(g, w, h, gap, split.getSecond(), hi, y, 0);
				y += (hi + gap) * split.getSecond().size();

				g.color(0.92f).fillRect(_1px, h - y - h_category, w - _1px * 2, h_category);
				g.drawText("Dependent", 0, h - y - h_category * .75f, w, h_category * .5f, VAlign.CENTER);
				y += h_category + gap;
				renderTransitions(g, w, h, gap, split.getFirst(), hi, y, split.getSecond().size());
			} else {
				float hi = (h - h_header - transitions.size() * gap - _1pxh * 3) / (transitions.size());
				float y = h_header + gap;
				renderTransitions(g, w, h, gap, transitions, hi, y, 0);
			}
		}
	}

	private Pair<Collection<ITransition>, Collection<ITransition>> splitInDependent(Collection<ITransition> transitions) {
		Collection<ITransition> dependent = new ArrayList<>(transitions.size());
		Collection<ITransition> rest = new ArrayList<>(transitions.size());
		for (ITransition t : transitions)
			if (t instanceof SimpleTransition && (((SimpleTransition) t).getTarget() instanceof SelectStateState)
					&& ((SelectStateState) ((SimpleTransition) t).getTarget()).getMode().isDependent())
				dependent.add(t);
			else
				rest.add(t);
		return Pair.make(dependent, rest);
	}

	private void renderTransitions(final GLGraphics g, final float w, final float h, final float gap,
			Collection<ITransition> transitions, float hi, float y, int i) {
		g.incZ();
		// if in the first step split in dependent and independent data
		g.gl.glLineStipple(1, (short)0xAAAA);

		for (ITransition t : transitions) {
			g.pushName(getPickingID(i));
			if (!t.isEnabled())
				g.color(0.95f);
			else if(hovered == i)
				g.color(0.85f);
			else
				g.color(0.90f);

			g.fillRect(gap, h - y - hi, w - 2 * gap, hi);
			g.popName();
			drawMultiLineText(g, t, gap, h - y - hi, w - 2 * gap, hi, !t.isEnabled());
			if (!t.isEnabled())
				g.gl.glEnable(GL2.GL_LINE_STIPPLE);
			g.drawRect(gap, h - y - hi, w - 2 * gap, hi);
			if (!t.isEnabled())
				g.gl.glDisable(GL2.GL_LINE_STIPPLE);
			y += hi + gap;
			i++;
		}
		g.decZ();
	}

	private int getPickingID(int i) {
		return view.getPickingManager().getPickingID(view.getID(), PICKING_TYPE, i);
	}

	private void drawMultiLineText(GLGraphics g, ILabeled item, float x, float y, float w, float h, boolean grey) {
		if (item.getLabel().isEmpty())
			return;
		final float lineHeight = view.getPixelGLConverter().getGLHeightForPixelHeight(14);

		List<String> lines = TextUtils.wrap(g.text, item.getLabel(), w, lineHeight);

		if (grey)
			g.textColor(Color.GRAY);
		g.drawText(lines, x, y + (h - lineHeight * lines.size()) * 0.5f, w, lineHeight * lines.size(), 0,
				VAlign.CENTER, ETextStyle.PLAIN);
		g.textColor(Color.BLACK);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return true;
	}


	public void onUpdate(UpdateStratificationPreviewEvent event) {
		if (stateMachine.getCurrent() instanceof ABrowseState) {
			((ABrowseState) stateMachine.getCurrent()).onUpdateStratification(event.getTablePerspective(), this);
		}
	}

	public void onUpdate(UpdatePathwayPreviewEvent event) {
		if (stateMachine.getCurrent() instanceof ABrowseState) {
			((ABrowseState) stateMachine.getCurrent()).onUpdatePathway(event.getPathway(), this);
		}
	}

	public void onUpdate(UpdateNumericalPreviewEvent event) {
		if (stateMachine.getCurrent() instanceof ABrowseState) {
			((ABrowseState) stateMachine.getCurrent()).onUpdateOther(event.getTablePerspective(), this);
		}
	}


	public boolean onSelected(TablePerspective tablePerspective) {
		if (stateMachine.getCurrent() instanceof ISelectStratificationState) {
			ISelectStratificationState s = ((ISelectStratificationState) stateMachine.getCurrent());
			if (!s.apply(tablePerspective))
				return false;
			s.select(tablePerspective, this);
			return true;
		}
		return false;
	}


	public boolean onSelected(TablePerspective tablePerspective, Group group) {
		if (stateMachine.getCurrent() instanceof ISelectGroupState) {
			ISelectGroupState s = ((ISelectGroupState) stateMachine.getCurrent());
			if ((group == null && !s.isSelectAllSupported()) || !s.apply(Pair.make(tablePerspective, group)))
				return false;
			s.select(tablePerspective, group, this);
			return true;
		}
		return false;
	}

	@Override
	public void switchTo(IState target) {
		stateMachine.move(target);
		Collection<ITransition> transitions = stateMachine.getTransitions(target);

		// automatically switch single transitions
		if (transitions.size() == 1) {
			transitions.iterator().next().apply(this);
			return;
		}
		checkSelect();
	}

	public boolean canGoBack() {
		return stateMachine.getPrevious() != null && canGoBack;
	}

	public void goBack() {
		if (!canGoBack())
			return;
		stateMachine.goBack();
		Collection<ITransition> transitions = stateMachine.getTransitions(stateMachine.getCurrent());
		// automatically switch back single transitions
		if (transitions.size() == 1) {
			stateMachine.goBack();
		}
		checkSelect();
	}

	@Override
	public void addScoreToTourGuide(IScore... scores) {
		ITourGuideView tourGuide = TourGuideUtils.showTourGuide(stateMachine.findNearestRoot().getAdapter()
				.getSecondaryID());
		tourGuide.removeLeadingScoreColumns();
		tourGuide.addColumns(scores);
	}

	public void done(boolean confirmed) {
		EventPublisher.trigger(new WizardEndedEvent());
	}

	@Override
	public void replaceTemplate(ALayoutRenderer renderer) {
		canGoBack = false;
		adapter.replaceTemplate(renderer);
	}

	@Override
	public void replaceTemplate(TablePerspective with, boolean highlight) {
		IBrickConfigurer configurer = null;
		if (DataDomainOracle.isCategoricalDataDomain(with.getDataDomain()))
			configurer = new CategoricalDataConfigurer(with);
		else if (!DataSupportDefinitions.homogenousTables.apply(with.getDataDomain()))
			configurer = new CategoricalDataConfigurer(with);

		replaceTemplate(with, configurer, highlight);
	}


	private void replaceTemplate(TablePerspective with, IBrickConfigurer configurer, boolean highlight) {
		canGoBack = false;
		adapter.replaceTemplate(with, configurer, false, highlight ? StratomexRenderStyle.stratomexHitGroup() : null);
	}

	@Override
	public void replaceOtherTemplate(Perspective underlying, TablePerspective numerical, boolean extra,
			boolean highlight) {
		if (numerical == null) {
			ClinicalDataConfigurer clinicalDataConfigurer = new ClinicalDataConfigurer();
			clinicalDataConfigurer.setSortingStrategy(new NoSortingSortingStrategy());
			replaceTemplate(numerical, clinicalDataConfigurer, true);
			return;
		}
		canGoBack = false;
		adapter.replaceOtherTemplate(underlying, numerical, extra,
				highlight ? StratomexRenderStyle.stratomexHitGroup() : null);
	}

	@Override
	public void replacePathwayTemplate(Perspective underlying, PathwayGraph pathway, boolean extra, boolean highlight) {
		canGoBack = false;
		adapter.replacePathwayTemplate(underlying, pathway, extra, highlight ? StratomexRenderStyle.stratomexHitGroup()
				: null);
	}

	@Override
	public ALayoutRenderer createPreview(TablePerspective tablePerspective) {
		return adapter.createPreviewRenderer(tablePerspective);
	}

	@Override
	public ALayoutRenderer createPreview(PathwayGraph pathway) {
		return adapter.createPreviewRenderer(pathway);
	}

	@Override
	public AGLView getGLView() {
		return view;
	}
}



