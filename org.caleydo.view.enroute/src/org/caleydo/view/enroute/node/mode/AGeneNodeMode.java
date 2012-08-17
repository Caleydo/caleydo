/**
 * 
 */
package org.caleydo.view.enroute.node.mode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.layout.util.IColorProvider;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.node.GeneNode;

/**
 * Base class for modes of a {@link GeneNode}.
 * 
 * @author Christian
 * 
 */
public abstract class AGeneNodeMode extends ALayoutBasedNodeMode implements
		IColorProvider, ILabelProvider {

	/**
	 * Second color that is used to show a color gradient made up of combined
	 * selection colors.
	 */
	protected float[] gradientColor = null;

	/**
	 * @param view
	 */
	public AGeneNodeMode(GLEnRoutePathway view) {
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

		if (allSelectionTypes.size() > 0) {
			SelectionType selectionType = allSelectionTypes.get(0);
			if (!selectionType.equals(SelectionType.NORMAL))
				backgroundColor = selectionType.getColor();
			gradientColor = null;
			if (allSelectionTypes.size() > 1) {
				selectionType = allSelectionTypes.get(1);
				if (!selectionType.equals(SelectionType.NORMAL))
					gradientColor = selectionType.getColor();
			}
		}

	}

	@Override
	public float[] getColor() {
		return (gradientColor != null) ? gradientColor : backgroundColor;
	}

	@Override
	public float[] getGradientColor() {
		return (gradientColor != null) ? backgroundColor : gradientColor;
	}

	@Override
	public boolean useGradient() {
		return gradientColor != null;
	}

	@Override
	public boolean isHorizontalGradient() {
		return false;
	}
	
	@Override
	public String getLabel() {
		return node.getCaption();
	}

	@Override
	public boolean isLabelDefault() {
		return false;
	}

}
