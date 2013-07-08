/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.basic;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

public abstract class AScrollBar implements IScrollBar {
	private static final float MIN_WINDOW = 8;
	private static final float JUMP_BUTTON_SIZE = 8;
	private static final float JUMP_PERCENTAGE = 0.1f;
	protected final boolean isHorizontal;
	protected boolean hovered = false;
	private float offset;
	private float window;
	private float size;
	protected IScrollBarCallback callback;

	private float width;

	private boolean showJumpButtons = true;

	public AScrollBar(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	/**
	 * @param width
	 *            setter, see {@link width}
	 */
	@Override
	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 * @return the width, see {@link #width}
	 */
	@Override
	public float getWidth() {
		return width;
	}

	/**
	 * @param showJumpButtons
	 *            setter, see {@link showJumpButtons}
	 */
	public void setShowJumpButtons(boolean showJumpButtons) {
		if (this.showJumpButtons == showJumpButtons)
			return;
		this.showJumpButtons = showJumpButtons;
		if (callback != null)
			callback.repaint();
	}

	/**
	 * @return the showJumpButtons, see {@link #showJumpButtons}
	 */
	public boolean isShowJumpButtons() {
		return showJumpButtons;
	}

	@Override
	public void setCallback(IScrollBarCallback callback) {
		this.callback = callback;
	}

	@Override
	public float setBounds(float offset, float window, float size) {
		this.offset = Math.min(size - window, Math.max(0, offset));
		this.window = window;
		this.size = size;
		return this.offset;
	}

	private final float clamp(float newOffset) {
		return Math.min(size - window, Math.max(0, newOffset));
	}

	@Override
	public float getOffset() {
		return offset;
	}

	@Override
	public float getWindow() {
		return window;
	}

	@Override
	public float getSize() {
		return size;
	}

	private float[] map(float total) {
		if (showJumpButtons)
			total -= JUMP_BUTTON_SIZE * 4;

		float scale = total / this.size;
		float w = window * scale;
		if (w < MIN_WINDOW) { // need to scale
			float missing = MIN_WINDOW - w;
			total -= missing;
			scale = total / this.size;
			w = window * scale + missing;
		}
		return new float[] { offset * scale, w, scale };
	}

	private float unmap(float[] mapped, float v) {
		return v / mapped[2];
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		g.color(Color.LIGHT_GRAY).fillRect(0, 0, w, h);
		float total = isHorizontal ? w : h;
		float base = hovered ? 0.6f : 0.3f;
		g.color(0, 0, 0, base);
		float[] s = map(total);

		float jumpSize = (showJumpButtons ? JUMP_BUTTON_SIZE * 2 : 0);

		if (isHorizontal) {
			g.fillRect(jumpSize + s[0], 0, s[1], h);
			if (showJumpButtons) {
				g.color(0, 0, 0, base + 0.2f);
				g.drawLine(0, 0, 0, h - 1);
				fillTriangle(g, 0, 0, JUMP_BUTTON_SIZE - 1, h, true);
				fillTriangle(g, JUMP_BUTTON_SIZE, 0, JUMP_BUTTON_SIZE - 1, h, true);

				g.drawLine(w - 1, 0, w - 1, h - 1);
				fillTriangle(g, w, 0, -JUMP_BUTTON_SIZE + 1, h, true);
				fillTriangle(g, w - JUMP_BUTTON_SIZE, 0, -JUMP_BUTTON_SIZE + 1, h, true);
			}
		} else {
			g.fillRect(0, jumpSize + s[0], w, s[1]);
			if (showJumpButtons) {
				g.color(0, 0, 0, base + 0.2f);
				g.drawLine(0, 0, w - 1, 0);

				fillTriangle(g, 0, 0, w, JUMP_BUTTON_SIZE - 1, false);
				fillTriangle(g, 0, JUMP_BUTTON_SIZE, w, JUMP_BUTTON_SIZE - 1, false);

				g.drawLine(0, h - 1, w - 1, h - 1);
				fillTriangle(g, 0, h, w, -JUMP_BUTTON_SIZE + 1, false);
				fillTriangle(g, 0, h - JUMP_BUTTON_SIZE, w, -JUMP_BUTTON_SIZE + 1, false);
			}
		}


	}

	private static void fillTriangle(GLGraphics g, float x, float y, float w, float h, boolean hor) {
		if (hor) {
			g.drawPath(true, new Vec2f(x, y + h * 0.5f), new Vec2f(x + w, y + h - 1), new Vec2f(x + w, y));
		} else {
			g.drawPath(true, new Vec2f(x + w * 0.5f, y), new Vec2f(x + w - 1, y + h), new Vec2f(x, y + h));
		}
	}

	protected final boolean jump(float mousePos) {
		float total = callback.getHeight(this);
		float[] s = map(total);

		if (showJumpButtons && (mousePos < JUMP_BUTTON_SIZE * 2 || mousePos > (total - JUMP_BUTTON_SIZE * 2))) {
			//jump jump
			if (mousePos < JUMP_BUTTON_SIZE * 2)
				callback.onScrollBarMoved(this,
						clamp(mousePos < JUMP_BUTTON_SIZE ? 0 : offset - size * JUMP_PERCENTAGE));
			else
				callback.onScrollBarMoved(this, clamp(mousePos > (total - JUMP_BUTTON_SIZE) ? size : offset + size
						* JUMP_PERCENTAGE));
			return true;
		}
		// normal drag
		if (showJumpButtons)
			mousePos -= JUMP_BUTTON_SIZE * 2;

		if (mousePos >= s[0] && mousePos <= (s[0] + s[1]))
			return false;

		// jump
		float v = unmap(s, mousePos);
		callback.onScrollBarMoved(this, clamp(v));
		return true;
	}

	protected final void drag(float mouseDelta) {
		float[] s = map(callback.getHeight(this));
		float vd = unmap(s, mouseDelta);
		callback.onScrollBarMoved(this, clamp(offset + vd));
	}

	@Override
	public void renderPick(GLGraphics g, float w, float h, GLElement parent) {
		g.fillRect(0, 0, w, h);
	}
}
