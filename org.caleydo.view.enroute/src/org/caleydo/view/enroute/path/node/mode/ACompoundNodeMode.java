/**
 *
 */
package org.caleydo.view.enroute.path.node.mode;

import gleem.linalg.Vec3f;

import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.SelectionColorCalculator;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.CompoundNode;

/**
 * Base class for modes of a {@link CompoundNode}.
 *
 * @author Christian
 *
 */
public abstract class ACompoundNodeMode extends ALinearizeableNodeMode {

	protected PixelGLConverter pixelGLConverter;

	SelectionColorCalculator colorCalculator;

	/**
	 * @param view
	 */
	public ACompoundNodeMode(AGLView view, APathwayPathRenderer pathwayPathRenderer) {
		super(view, pathwayPathRenderer);
		this.pixelGLConverter = view.getPixelGLConverter();
		colorCalculator = new SelectionColorCalculator(new Color(DEFAULT_BACKGROUND_COLOR));
	}

	protected void renderCircle(GL2 gl, GLU glu, Vec3f position, float radius) {

		gl.glPushName(pickingManager.getPickingID(view.getID(), EPickingType.LINEARIZABLE_NODE.name(), node.hashCode()));
		gl.glPushMatrix();
		gl.glTranslatef(position.x(), position.y(), position.z());
		// gl.glColor4f(backgroundColor[0], backgroundColor[1], backgroundColor[2], pathwayPathRenderer.getNodeAlpha());
		gl.glColor4fv(backgroundColor, 0);
		GLPrimitives.renderCircle(glu, radius / 2.0f, 16);
		// gl.glColor4f(0, 0, 0, pathwayPathRenderer.getNodeAlpha());
		gl.glColor4f(0, 0, 0, 1);
		GLPrimitives.renderCircleBorder(gl, glu, radius / 2.0f, 16, 0.1f);
		gl.glPopMatrix();
		gl.glPopName();
	}

	@Override
	protected boolean determineHighlightColor() {
		EventBasedSelectionManager selectionManager = pathwayPathRenderer.getMetaboliteSelectionManager();
		List<SelectionType> selectionTypes = selectionManager.getSelectionTypes(node.getPrimaryPathwayVertexRep()
				.getName().hashCode());
		if (selectionTypes.contains(SelectionType.SELECTION)) {
			highlightColor = SelectionType.SELECTION.getColor();
			return true;
		} else if (selectionTypes.contains(SelectionType.MOUSE_OVER)) {
			highlightColor = SelectionType.MOUSE_OVER.getColor();
			return true;
		}
		return false;
		// Collections.sort(selectionTypes);
		// Collections.reverse(selectionTypes);
		// colorCalculator.calculateColors(selectionTypes);
		// backgroundColor = colorCalculator.getPrimaryColor().getRGBA();
	}

	@Override
	public int getMinHeightPixels() {
		return pathwayPathRenderer.getSizeConfig().getCircleNodeRadius() * 2;
	}

}
