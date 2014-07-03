/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.pathway.v2.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.util.PickingPool;
import org.caleydo.core.view.opengl.picking.IPickingLabelProvider;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingListenerComposite;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;

import com.google.common.base.Preconditions;

/**
 * Pathway representation that renders a single KEGG- or Wikipathway as texture. The locations {@link PathwayVertexRep}s
 * are masked to allow background augmentations.
 *
 *
 * @author Christian
 *
 */
public class PathwayTextureRepresentation extends APathwayElementRepresentation {

	private final PathwayGraph pathway;

	private Vec2f renderSize = new Vec2f();
	private Vec2f origin = new Vec2f();
	private Vec2f scaling = new Vec2f();

	private PickingPool pool;

	private List<IVertexRepSelectionListener> vertexListeners = new ArrayList<>();

	private float minWidth = -1;
	private float minHeight = -1;

	private GLPadding padding = GLPadding.ZERO;

	public PathwayTextureRepresentation(PathwayGraph pathway) {
		this.pathway = Preconditions.checkNotNull(pathway);
		setVisibility(EVisibility.PICKABLE);
	}

	@Override
	protected void init(IGLElementContext context) {

		IPickingListener pickingListener = PickingListenerComposite.concat(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onVertexPick(pick);
			}
		}, context.getSWTLayer().createTooltip(new IPickingLabelProvider() {
			@Override
			public String getLabel(Pick pick) {
				PathwayVertexRep vertexRep = PathwayItemManager.get().getPathwayVertexRep(pick.getObjectID());
				return vertexRep.getLabel();
			}
		}));

		pool = new PickingPool(context, pickingListener);

		super.init(context);
	}

	@Override
	protected void takeDown() {
		pool.clear();
		pool = null;
		super.takeDown();
	}

	private void onVertexPick(Pick pick) {
		PathwayVertexRep vertexRep = PathwayItemManager.get().getPathwayVertexRep(pick.getObjectID());
		for (IVertexRepSelectionListener listener : vertexListeners) {
			listener.onSelect(vertexRep, pick);
		}

		// if (pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {
		// ContextMenuCreator creator = new ContextMenuCreator();
		// for (VertexRepBasedContextMenuItem item : contextMenuItems) {
		// item.setVertexRep(vertexRep);
		// creator.add(item);
		// }
		// context.getSWTLayer().showContextMenu(creator);
		// }
	}

	@Override
	protected void layoutImpl(int deltaTimeMs) {
		super.layoutImpl(deltaTimeMs);
		Vec2f size = getSize();
		calculateTransforms(size.x(), size.y());
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.save();
		g.move(origin);
		g.gl.glScalef(scaling.x(), scaling.y(), 1);
		pathway.getType().render(g, pathway);
		g.restore();
		// repaint();
		// g.color(0f, 0f, 0f, 1f);
		// for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
		// g.fillRect(getVertexRepBounds(vertexRep));
		// }

	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
			g.pushName(pool.get(vertexRep.getID()));
			g.fillRect(getVertexRepBounds(vertexRep));
			g.popName();
		}

	}

	private void calculateTransforms(float w, float h) {

		float availableWidth = w - (padding.left + padding.right);
		float availableHeight = h - (padding.top + padding.bottom);

		float pathwayWidth = pathway.getWidth();
		float pathwayHeight = pathway.getHeight();

		float pathwayAspectRatio = pathwayWidth / pathwayHeight;
		float viewFrustumAspectRatio = availableWidth / availableHeight;

		if (pathwayWidth <= availableWidth && pathwayHeight <= h) {
			scaling.set(1f, 1f);
			renderSize.setX(pathwayWidth);
			renderSize.setY(pathwayHeight);
		} else {
			if (viewFrustumAspectRatio > pathwayAspectRatio) {
				renderSize.setX((availableHeight / pathwayHeight) * pathwayWidth);
				renderSize.setY(availableHeight);
			} else {
				renderSize.setX(availableWidth);
				renderSize.setY((availableWidth / pathwayWidth) * pathwayHeight);
			}
			scaling.set(renderSize.x() / pathwayWidth, renderSize.y() / pathwayHeight);
		}
		origin.set(padding.left + (availableWidth - renderSize.x()) / 2.0f,
				padding.top + (availableHeight - renderSize.y()) / 2.0f);
	}

	@Override
	public PathwayGraph getPathway() {
		return pathway;
	}

	@Override
	public List<PathwayGraph> getPathways() {
		if (pathway == null)
			return Collections.emptyList();
		return Collections.singletonList(pathway);
	}

	@Override
	public Rect getVertexRepBounds(PathwayVertexRep vertexRep) {
		if (pathway == null || !pathway.containsVertex(vertexRep))
			return null;
		int coordsX = vertexRep.getCoords().get(0).getFirst();
		int coordsY = vertexRep.getCoords().get(0).getSecond();

		float x = origin.x() + (scaling.x() * coordsX);
		float y = origin.y() + (scaling.y() * coordsY);

		float width = scaling.x() * vertexRep.getWidth();
		float height = scaling.y() * vertexRep.getHeight();

		return new Rect(x, y, width, height);
	}

	@Override
	public List<Rect> getVertexRepsBounds(PathwayVertexRep vertexRep) {
		Rect bounds = getVertexRepBounds(vertexRep);
		if (bounds == null)
			return new ArrayList<>();
		return Arrays.asList(bounds);
	}

	/**
	 * @param minHeight
	 *            setter, see {@link minHeight}
	 */
	public void setMinHeight(float minHeight) {
		this.minHeight = minHeight;
	}

	/**
	 * @param minWidth
	 *            setter, see {@link minWidth}
	 */
	public void setMinWidth(float minWidth) {
		this.minWidth = minWidth;
	}

	@Override
	public void addVertexRepSelectionListener(IVertexRepSelectionListener listener) {
		vertexListeners.add(listener);
	}

	@Override
	public Rect getPathwayBounds() {
		if (pathway == null)
			return null;
		return new Rect(origin.x(), origin.y(), renderSize.x(), renderSize.y());
	}

	@Override
	public float getMinWidth() {
		if (minWidth > 0)
			return minWidth;
		if (pathway == null)
			return 120;
		return pathway.getWidth() * 0.8f;
	}

	@Override
	public float getMinHeight() {
		if (minHeight > 0)
			return minHeight;
		if (pathway == null)
			return 120;
		return pathway.getHeight() * 0.8f;
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(getMinWidth(), getMinHeight());
	}

	/**
	 * @param padding
	 *            setter, see {@link padding}
	 */
	public void setPadding(GLPadding padding) {
		this.padding = padding;
		repaintAll();
	}

}
