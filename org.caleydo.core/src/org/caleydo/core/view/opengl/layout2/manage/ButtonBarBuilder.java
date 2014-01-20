/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2.manage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.animation.MoveTransitions;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.RadioController;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactorySwitcher.IActiveChangedCallback;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

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
	private IGLLayout2 layout = GLLayouts.flowHorizontal(2);

	private final Collection<GLElement> prepend = new ArrayList<>(0);
	private final Collection<GLElement> append = new ArrayList<>(0);

	private ISelectionCallback custom;

	/**
	 * filter of the possible items
	 */
	private Predicate<? super String> filter = Predicates.alwaysTrue();

	/**
	 *
	 */
	public ButtonBarBuilder(GLElementFactorySwitcher switcher) {
		this.switcher = switcher;
	}

	public ButtonBarBuilder customCallback(ISelectionCallback callback) {
		this.custom = callback;
		return this;
	}

	public ButtonBarBuilder filterBy(Predicate<? super String> filter) {
		this.filter = filter;
		return this;
	}

	public ButtonBarBuilder prepend(GLElement elem) {
		prepend.add(elem);
		return this;
	}

	public ButtonBarBuilder append(GLElement elem) {
		append.add(elem);
		return this;
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

	public ButtonBarBuilder layoutUsing(IGLLayout2 layout) {
		this.layout = layout;
		return this;
	}

	public ButtonBarBuilder layoutAs(EButtonBarLayout layout) {
		return layoutUsing(layout);
	}

	public GLElement build() {
		return new ButtonBar(this);
	}

	public static enum EButtonBarLayout implements IGLLayout2 {
		HORIZONTAL, VERTICAL, SLIDE_LEFT, SLIDE_RIGHT, SLIDE_DOWN, HOVER_BLOCK_3x3;

		@Override
		public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
				int deltaTimeMs) {
			if (children.isEmpty())
				return false;

			final boolean hovered = isHovered(parent);
			final int size = children.size();
			final int active = getActive(parent);
			switch (this) {
			case HORIZONTAL:
				return GLLayouts.flowHorizontal(2).doLayout(children, w, h, parent, deltaTimeMs);
			case VERTICAL:
				return GLLayouts.flowVertical(2).doLayout(children, w, h, parent, deltaTimeMs);
			case SLIDE_LEFT:
				if (hovered) {
					int j = 0;
					for (int i = 0; i < size; ++i)
						if (i != active)
							children.get(i).setBounds(-w * (++j), 0, w, h);
				} else {
					for (IGLLayoutElement child : children)
						child.setBounds(-w, 0, 0, h);
				}
				children.get(active).setBounds(0, 0, w, h);
				break;
			case SLIDE_RIGHT:
				if (hovered) {
					int j = 0;
					for (int i = 0; i < size; ++i)
						if (i != active)
							children.get(i).setBounds(w * (++j), 0, w, h);
				} else {
					for (IGLLayoutElement child : children)
						child.setBounds(w, 0, 0, h);
				}
				children.get(active).setBounds(0, 0, w, h);
				break;
			case SLIDE_DOWN:
				if (hovered) {
					int j = 0;
					for (int i = 0; i < size; ++i)
						if (i != active)
							children.get(i).setBounds(0, h * (++j), w, h);
				} else {
					for (IGLLayoutElement child : children)
						child.setBounds(0, h, 0, h);
				}
				children.get(active).setBounds(0, 0, w, h);
				break;
			case HOVER_BLOCK_3x3:
				switch (size) {
				case 1: // just close center it
					children.get(0).setBounds(0, 0, w, h);
					break;
				case 2: // just a single one, just the first one
					children.get(0).setBounds(0, 0, w, h);
					children.get(1).hide();
					break;
				default:
					if (hovered) { // show as a rect of 3x3
						for (int i = 0; i < Math.min(3, size); ++i)
							children.get(i).setBounds((i - 1) * w, -h, w, h);
						if (size > 3)
							children.get(3).setLocation(w, 0);
						for (int i = 4; i < Math.min(4 + 3, size); ++i)
							children.get(i).setBounds((5 - i) * w, h, w, h);
						if (size > 7)
							children.get(7).setBounds(-w, 0, w, h);
					} else {
						for (IGLLayoutElement child : children)
							child.hide();
					}
					// center active
					children.get(active).setBounds(0, 0, w, h);
				}
				break;
			}
			return false;
		}

		private int getActive(IGLLayoutElement parent) {
			assert parent.asElement() instanceof ButtonBar;
			return ((ButtonBar) parent.asElement()).getActive();
		}

		private boolean isHovered(IGLLayoutElement parent) {
			assert parent.asElement() instanceof ButtonBar;
			return ((ButtonBar) parent.asElement()).hovered;
		}

		public boolean isAnimated() {
			return this.ordinal() >= SLIDE_LEFT.ordinal();
		}

		public boolean needCustomArea(boolean hovered, ButtonBar buttonBar) {
			return hovered && buttonBar.size() > 2;
		}

		public void renderCustomArea(GLGraphics g, float w, float h, int size) {
			g.color(Color.WHITE);
			switch (this) {
			case SLIDE_LEFT:
				g.fillRect((-size + 1) * w, 0, size * w, h);
				break;
			case SLIDE_RIGHT:
				g.fillRect(0, 0, (size) * w, h);
				break;
			case SLIDE_DOWN:
				g.fillRect(0, 0, w, h * (size));
				break;
			case HOVER_BLOCK_3x3:
				g.fillRect(-w, -h, w * 3, h * 3);
				break;
			default:
				break;
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

	private static class ButtonBar extends AnimatedGLElementContainer implements ISelectionCallback,
			IActiveChangedCallback, IPickingListener {
		private final EButtonBarLayout layout;
		private final RadioController controller = new RadioController(this);
		final GLElementFactorySwitcher switcher;
		private final ISelectionCallback custom;
		final int prepended;

		boolean hovered;

		public ButtonBar(ButtonBarBuilder builder) {
			setLayout(builder.layout);
			this.switcher = builder.switcher;
			this.switcher.onActiveChanged(this);
			layout = toButtonBarLayout(builder.layout);

			if (layout != null) {
				setAnimateByDefault(true);
				setDefaultMoveTransition(MoveTransitions.MOVE_LINEAR);
				setVisibility(EVisibility.PICKABLE);
				onPick(this);
			} else {
				setAnimateByDefault(false);
			}

			prepended = builder.prepend.size();
			addAll(builder.prepend);
			addButtons(builder);
			addAll(builder.append);
			this.custom = builder.custom;
		}

		/**
		 * @return
		 */
		public int getActive() {
			return switcher.getActive() + prepended;
		}

		private static EButtonBarLayout toButtonBarLayout(IGLLayout2 layout) {
			if (!(layout instanceof EButtonBarLayout))
				return null;
			EButtonBarLayout l = (EButtonBarLayout) layout;
			return l.isAnimated() ? l : null;
		}

		private void addButtons(ButtonBarBuilder builder) {
			int i = 0;
			for (GLElementSupplier sup : builder.switcher) {
				if (!builder.filter.apply(sup.getId())) {
					i++;
					continue;
				}
				GLButton b = new GLButton();
				b.setPickingObjectId(i++);
				b.setTooltip(sup.getLabel());
				b.setLayoutData(sup);
				b.setRenderer(builder.renderer);
				if (builder.hoverEffect != null)
					b.setHoverEffect(builder.hoverEffect);
				controller.add(b);
				b.setSize(builder.size, builder.size);
				b.setzDelta(0.5f);
				this.add(b, 0);
			}
		}

		/**
		 * @param prepend
		 */
		private void addAll(Collection<GLElement> prepend) {
			for (GLElement elem : prepend)
				add(elem, 0);
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
			boolean fat = layout != null && layout.needCustomArea(hovered, this);
			if (fat) { // if we are rendering the 3x3 rect
				g.incZ(0.5f);
				assert layout != null;
				layout.renderCustomArea(g, w, h, size());
			}
			super.renderImpl(g, w, h);
			if (fat)
				g.incZ(-0.5f);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			if (layout != null && layout.needCustomArea(hovered, this))
				layout.renderCustomArea(g, w, h, size());
			super.renderPickImpl(g, w, h);
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
			switcher.onActiveChanged(this);
			onActiveChanged(switcher.getActive());
		}

		@Override
		protected void takeDown() {
			switcher.removeOnActiveChanged(this);
			super.takeDown();
		}

		@Override
		public void onSelectionChanged(GLButton button, boolean selected) {
			switcher.setActive(button.getPickingObjectId());
			if (custom != null)
				custom.onSelectionChanged(button, selected);
		}

		@Override
		public void onActiveChanged(int active) {
			int i = 0;
			for (GLButton b : controller) {
				if (b.getPickingObjectId() == active) {
					controller.setSelected(i);
					break;
				}
				i++;
			}
			relayout();
		}

	}
}
