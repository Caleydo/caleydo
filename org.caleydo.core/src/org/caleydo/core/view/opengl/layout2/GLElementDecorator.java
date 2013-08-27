/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 *
 * @author Samuel Gratzl
 *
 */
public class GLElementDecorator extends GLElement implements IGLElementParent {
	protected GLElement content;

	public GLElementDecorator() {
	}

	public GLElementDecorator(GLElement content) {
		setContent(content);
	}

	/**
	 * @param content
	 *            setter, see {@link content}
	 */
	public void setContent(GLElement content) {
		if (this.content == content)
			return;
		if (this.content != null) {
			this.content.setParent(null);
			if (context != null)
				content.takeDown();
		}
		this.content = content;
		if (this.content != null) {
			this.content.setParent(this);
			if (context != null)
				this.content.init(context);
		}
		relayout();
	}

	/**
	 * @return the content, see {@link #content}
	 */
	public final GLElement getContent() {
		return content;
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		if (content != null)
			content.init(context);
	}

	@Override
	protected void takeDown() {
		if (content != null)
			content.takeDown();
		super.takeDown();
	}

	@Override
	public void repaint() {
		super.repaint();
		if (content != null)
			GLElementAccessor.repaintDown(content);
	}

	@Override
	public void repaintPick() {
		super.repaintPick();
		if (content != null)
			GLElementAccessor.repaintPickDown(content);
	}

	@Override
	protected boolean hasPickAbles() {
		return true;
	}

	@Override
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);
		if (content != null)
			content.layout(deltaTimeMs);
	}

	@Override
	protected void layoutImpl(int deltaTimeMs) {
		if (content != null) {
			Vec2f size = getSize();
			layoutContent(content.layoutElement, size.x(), size.y(), deltaTimeMs);
		}
		super.layoutImpl(deltaTimeMs);
	}

	protected void layoutContent(IGLLayoutElement content, float w, float h, int deltaTimeMs) {
		content.setBounds(0, 0, w, h);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (content != null)
			content.render(g);
		super.renderImpl(g, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		if (content != null)
			content.renderPick(g);
		super.renderPickImpl(g, w, h);
	}

	@Override
	public boolean moved(GLElement child) {
		return false;
	}

	@Override
	public final <P, R> R accept(IGLElementVisitor<P, R> visitor, P para) {
		return visitor.visit(this, para);
	}
}
