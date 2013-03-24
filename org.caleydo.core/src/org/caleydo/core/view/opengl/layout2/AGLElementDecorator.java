/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout2;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AGLElementDecorator extends GLElement implements IGLElementParent {
	protected final GLElement content;

	public AGLElementDecorator(GLElement content) {
		this.content = content;
		content.setParent(this);
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
		content.init(context);
	}

	@Override
	protected void takeDown() {
		content.takeDown();
		super.takeDown();
	}

	@Override
	protected boolean hasPickAbles() {
		return true;
	}

	@Override
	public void layout(int deltaTimeMs) {
		super.layout(deltaTimeMs);
		content.layout(deltaTimeMs);
	}

	@Override
	protected void layoutImpl() {
		layoutContent(content.layoutElement);
		super.layoutImpl();
	}

	protected abstract void layoutContent(IGLLayoutElement content);

	@Override
	public boolean moved(GLElement child) {
		return false;
	}

	@Override
	public final <P, R> R accept(IGLElementVisitor<P, R> visitor, P para) {
		return visitor.visit(this, para);
	}
}
