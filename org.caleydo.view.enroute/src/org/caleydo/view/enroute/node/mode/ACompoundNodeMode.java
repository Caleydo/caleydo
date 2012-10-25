/**
 * 
 */
package org.caleydo.view.enroute.node.mode;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.Collections;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.GLPrimitives;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.SelectionColorCalculator;
import org.caleydo.view.enroute.node.ANode;
import org.caleydo.view.enroute.node.CompoundNode;

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
	public ACompoundNodeMode(GLEnRoutePathway view) {
		super(view);
		this.pixelGLConverter = view.getPixelGLConverter();
		colorCalculator = new SelectionColorCalculator(new Color(DEFAULT_BACKGROUND_COLOR));
	}

	protected void renderCircle(GL2 gl, GLU glu, Vec3f position, float radius) {

		gl.glPushName(pickingManager.getPickingID(view.getID(),
				EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId()));
		gl.glPushMatrix();
		gl.glTranslatef(position.x(), position.y(), position.z());
		gl.glColor4fv(backgroundColor, 0);
		GLPrimitives.renderCircle(glu, radius / 2.0f, 16);
		gl.glColor4f(0, 0, 0, 1);
		GLPrimitives.renderCircleBorder(gl, glu, radius / 2.0f, 16, 0.1f);
		gl.glPopMatrix();
		gl.glPopName();
	}
	
	@Override
	protected void determineBackgroundColor(EventBasedSelectionManager selectionManager) {
		ArrayList<SelectionType> selectionTypes = selectionManager.getSelectionTypes(node
				.getPathwayVertexRep().getName().hashCode());
		Collections.sort(selectionTypes);
		Collections.reverse(selectionTypes);
		colorCalculator.calculateColors(selectionTypes);
		backgroundColor = colorCalculator.getPrimaryColor().getRGBA();
//		for (SelectionType selectionType : selectionTypes) {
//			if (!selectionType.equals(SelectionType.NORMAL))
//				backgroundColor = selectionType.getColor();
//			break;
//		}
	}

	@Override
	public int getMinHeightPixels() {
		return ANode.DEFAULT_HEIGHT_PIXELS;
	}

}
