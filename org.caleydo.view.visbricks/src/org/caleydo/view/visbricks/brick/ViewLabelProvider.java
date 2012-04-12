/**
 * 
 */
package org.caleydo.view.visbricks.brick;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.util.ILabelTextProvider;

/**
 * Adapter for views to provide their labels.
 * 
 * @author Christian
 * 
 */
public class ViewLabelProvider implements ILabelTextProvider {

	private AGLView view;

	public ViewLabelProvider(AGLView view) {
		this.view = view;
	}

	@Override
	public String getLabelText() {
		return view.getLabel();
	}

}
