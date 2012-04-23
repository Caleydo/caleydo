/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout.util.IColorProvider;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.node.GeneNode;

/**
 * Base class for modes of a {@link GeneNode}.
 * 
 * @author Christian
 * 
 */
public abstract class AGeneNodeMode extends ALayoutBasedNodeMode implements
		IColorProvider {

	/**
	 * @param view
	 */
	public AGeneNodeMode(GLLinearizedPathway view) {
		super(view);
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		determineBackgroundColor(view.getGeneSelectionManager());
		super.render(gl, glu);

	}

	@Override
	protected void determineBackgroundColor(EventBasedSelectionManager selectionManager) {
		List<SelectionType> allSelectionTypes = new ArrayList<SelectionType>();
		for (Integer davidId : node.getPathwayVertexRep().getDavidIDs()) {
			ArrayList<SelectionType> selectionTypes = selectionManager
					.getSelectionTypes(davidId);
			for (SelectionType selectionType : selectionTypes) {
				if (!allSelectionTypes.contains(selectionType)) {
					allSelectionTypes.add(selectionType);
				}
			}
		}
		Collections.sort(allSelectionTypes);
		Collections.reverse(allSelectionTypes);
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		for (SelectionType selectionType : allSelectionTypes) {
			if (!selectionType.equals(SelectionType.NORMAL))
				backgroundColor = selectionType.getColor();
			break;
		}
	}

	@Override
	public float[] getColor() {
		return backgroundColor;
	}

}
