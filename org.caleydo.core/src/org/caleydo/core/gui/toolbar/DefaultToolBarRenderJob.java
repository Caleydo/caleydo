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
package org.caleydo.core.gui.toolbar;

import java.util.List;

/**
 * Render job for toolbar contents, usually used with eclipse's Display.asyncRun()
 * 
 * @author Werner Puff
 */
public class DefaultToolBarRenderJob
	implements Runnable {

	/** list of toolbar contents to render */
	private List<AToolBarContent> toolBarContents;

	/** toolbar view to render the content into */
	private RcpToolBarView toolBarView;

	/** toolbar renderer */
	private IToolBarRenderer toolBarRenderer;

	@Override
	public void run() {
	}

	public List<AToolBarContent> getToolBarContents() {
		return toolBarContents;
	}

	public void setToolBarContents(List<AToolBarContent> toolBarContents) {
		this.toolBarContents = toolBarContents;
	}

	public RcpToolBarView getToolBarView() {
		return toolBarView;
	}

	public void setToolBarView(RcpToolBarView toolBarView) {
		this.toolBarView = toolBarView;
	}

	public IToolBarRenderer getToolBarRenderer() {
		return toolBarRenderer;
	}

	public void setToolBarRenderer(IToolBarRenderer toolBarRenderer) {
		this.toolBarRenderer = toolBarRenderer;
	}
}
