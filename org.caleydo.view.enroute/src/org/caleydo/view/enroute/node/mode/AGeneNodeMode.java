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
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.util.IColorProvider;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.SelectionColorCalculator;
import org.caleydo.view.enroute.node.GeneNode;

/**
 * Base class for modes of a {@link GeneNode}.
 * 
 * @author Christian
 * 
 */
public abstract class AGeneNodeMode extends ALayoutBasedNodeMode implements
		IColorProvider {

	/**
	 * Second color that is used to show a color gradient made up of combined
	 * selection colors.
	 */
	protected float[] gradientColor = null;
	
	SelectionColorCalculator colorCalculator;

	/**
	 * @param view
	 */
	public AGeneNodeMode(GLEnRoutePathway view) {
		super(view);
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		colorCalculator = new SelectionColorCalculator(new Color(DEFAULT_BACKGROUND_COLOR));
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
		colorCalculator.calculateColors(allSelectionTypes);
		backgroundColor = colorCalculator.getPrimaryColor().getRGBA();
		gradientColor = colorCalculator.getSecondaryColor().getRGBA();

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

}
