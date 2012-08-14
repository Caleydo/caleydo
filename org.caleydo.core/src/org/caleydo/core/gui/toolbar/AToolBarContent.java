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

import org.caleydo.core.serialize.ASerializedView;

/**
 * Abstract super class for toolbar content classes to provide lists of toolbar-actions.
 * 
 * @author Werner Puff
 */
public abstract class AToolBarContent {

	/** number of toolbar-icons per row */
	public final static int TOOLBAR_WRAP_COUNT = 4;

	/**
	 * specifies that the toolbar should contain actions for standard rendering of the related view
	 */
	public static final int STANDARD_RENDERING = 1;

	/**
	 * FIXME view-id of the target view for the actions contained within this toolbar content
	 */
	protected ASerializedView targetViewData = null;

	/**
	 * specifies the type of content to render. sub classes may define their own content types
	 */
	protected int renderType = STANDARD_RENDERING;

	/**
	 * Returns the related view type for this toolbar content
	 * 
	 * @return class object of the view related to this toolbar content
	 */
	public abstract Class<?> getViewClass();

	/**
	 * Delivers the toolbar content. sub classes should return a list of toolbar-actions that are added to a
	 * toolbar.
	 * 
	 * @return list of actions for a toolbar
	 */
	protected abstract List<ToolBarContainer> getToolBarContent();

	/**
	 * Delivers the content for the view-inline toolbar for special behavior sub classes should override this
	 * method
	 * 
	 * @return list of actions for a toolbar
	 */
	public List<ToolBarContainer> getInlineToolBar() {
		return getToolBarContent();
	}

	/**
	 * Called when the object is not needed anymore to release resources that might be obtained during
	 * initialization. Inherited objects should override the default implementation if needed.
	 */
	public void dispose() {
		// nothing to do in this default implementation
	}

	/**
	 * Sets the id of the target view for the actions in this toolbar content. The id must be set before
	 * retrieving any toolbar content.
	 * 
	 * @param viewID
	 *            as used by ViewManager of the target view
	 */
	public void setTargetViewData(ASerializedView serializedView) {
		targetViewData = serializedView;
	}

	public ASerializedView getTargetViewData() {
		return targetViewData;
	}

	public int getRenderType() {
		return renderType;
	}

	public void setRenderType(int renderType) {
		this.renderType = renderType;
	}
}
