/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.RadioController;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactorySwitcher.IActiveChangedCallback;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;

/**
 * @author Samuel Gratzl
 *
 */
public class ButtonBarBuilder {
	private final static IGLRenderer BUTTON_RENDERER = new IGLRenderer() {
		@Override
		public void render(GLGraphics g, float w, float h, GLElement parent) {
			GLElementSupplier sub = parent.getLayoutDataAs(GLElementSupplier.class, null);
			g.fillImage(g.getTexture(sub.getIcon()), 0, 0, w, h);
			if (((GLButton) parent).isSelected()) {
				g.gl.glEnable(GL.GL_BLEND);
				g.gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
				g.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				g.gl.glEnable(GL.GL_LINE_SMOOTH);
				g.color(1, 1, 1, 0.5f).fillRoundedRect(0, 0, w, h, Math.min(w, h) * 0.25f);
				g.gl.glPopAttrib();
			}
		}
	};

	public final GLElementFactorySwitcher switcher;
	private int size = 16;
	private IGLRenderer renderer = BUTTON_RENDERER;
	private IGLRenderer hoverEffect = null;
	private IGLLayout layout = GLLayouts.flowHorizontal(2);

	/**
	 *
	 */
	public ButtonBarBuilder(GLElementFactorySwitcher switcher) {
		this.switcher = switcher;
	}

	public ButtonBarBuilder renderWith(IGLRenderer renderer) {
		this.renderer = renderer;
		return this;
	}

	public ButtonBarBuilder hoverEffect(IGLRenderer hoverEffect) {
		this.hoverEffect = hoverEffect;
		return this;
	}

	public ButtonBarBuilder size(int size) {
		this.size = size;
		return this;
	}

	public ButtonBarBuilder layoutUsing(IGLLayout layout) {
		this.layout = layout;
		return this;
	}

	public ButtonBarBuilder layoutAs(EButtonBarLayout layout) {
		return layoutUsing(layout);
	}

	public GLElementContainer build() {
		return new ButtonBar(this);
	}

	public enum EButtonBarLayout implements IGLLayout {
		HORIZONTAL, VERTICAL; // , SQUEEZE_BLOCK_3x3, SQUEEZE_HORIZONTAL, SQUEEZE_VERTICAL;

		@Override
		public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
			switch (this) {
			case HORIZONTAL:
				GLLayouts.flowHorizontal(2).doLayout(children, w, h);
				break;
			case VERTICAL:
				GLLayouts.flowVertical(2).doLayout(children, w, h);
				break;
			// case SQUEEZE_BLOCK_3x3:
			// block_3x3(children, w, h);
			// break;
			}
		}

		// private void block_3x3(List<? extends IGLLayoutElement> children, float w, float h) {
		// final int size = children.size();
		// assert size <= 8;
		// // set all to the same size
		// for (IGLLayoutElement child : children)
		// child.setSize(w, h);
		// switch (size) {
		// case 1: // just close center it
		// children.get(0).setLocation(0, 0);
		// break;
		// case 2: // just a single one, just show close
		// children.get(0).setLocation(0, 0);
		// children.get(1).hide();
		// break;
		// default:
		// if (hovered) { // show as a rect of 3x3
		// for (int i = 0; i < Math.min(3, size); ++i)
		// children.get(i).setLocation((i - 1) * w, -h);
		// if (size > 3)
		// children.get(3).setLocation(w, 0);
		// for (int i = 4; i < Math.min(4 + 3, size); ++i)
		// children.get(i).setLocation((5 - i) * w, h);
		// if (size > 7)
		// children.get(7).setLocation(-w, 0);
		// } else {
		// for (IGLLayoutElement child : children)
		// child.hide();
		// }
		// // center active
		// final IGLLayoutElement activeChild = children.get(this.active + 1);
		// activeChild.setLocation(0, 0);
		// activeChild.setSize(w, h);
		// }
		// }
	}

	private static class ButtonBar extends GLElementContainer implements ISelectionCallback, IActiveChangedCallback {
		private final RadioController controller = new RadioController(this);
		private final GLElementFactorySwitcher switcher;

		public ButtonBar(ButtonBarBuilder builder) {
			setLayout(builder.layout);
			this.switcher = builder.switcher;

			int i = 0;
			for (GLElementSupplier sup : builder.switcher) {
				GLButton b = new GLButton();
				b.setPickingObjectId(i++);
				b.setTooltip(sup.getLabel());
				b.setLayoutData(sup);
				b.setRenderer(builder.renderer);
				if (builder.hoverEffect != null)
					b.setHoverEffect(builder.hoverEffect);
				controller.add(b);
				b.setSize(builder.size, builder.size);
				this.add(b);
			}
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
			switcher.onActiveChanged(this);
			controller.setSelected(switcher.getActive());
		}

		@Override
		protected void takeDown() {
			switcher.removeOnActiveChanged(this);
			super.takeDown();
		}

		@Override
		public void onSelectionChanged(GLButton button, boolean selected) {
			switcher.setActive(button.getPickingObjectId());
		}

		@Override
		public void onActiveChanged(int active) {
			controller.setSelected(active);
		}
	}

	private interface SqueezingLayout extends IGLLayout {
		boolean needSqueezedArea(boolean hovered, GLElementContainer container);

		void renderSqueezedArea(GLGraphics g, float w, float h);
	}

	private static class SqueezedButtonBar extends ButtonBar implements IPickingListener {
		private boolean hovered;
		private final SqueezingLayout squeezer;

		public SqueezedButtonBar(ButtonBarBuilder builder) {
			super(builder);
			assert builder.layout instanceof SqueezingLayout;
			this.squeezer = (SqueezingLayout) builder.layout;
			setVisibility(EVisibility.PICKABLE);
			this.onPick(this);
		}

		@Override
		public void pick(Pick pick) {
			switch (pick.getPickingMode()) {
			case MOUSE_OVER:
				hovered = true;
				relayout();
				break;
			case MOUSE_OUT:
				hovered = false;
				relayout();
				break;
			default:
				break;
			}
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			boolean fat = squeezer.needSqueezedArea(hovered, this);
			if (fat) { // if we are rendering the 3x3 rect
				g.incZ(0.5f);
				squeezer.renderSqueezedArea(g, w, h);
			}
			super.renderImpl(g, w, h);
			if (fat)
				g.incZ(-0.5f);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			if (squeezer.needSqueezedArea(hovered, this))
				squeezer.renderSqueezedArea(g, w, h);
			super.renderPickImpl(g, w, h);
		}
	}
}
