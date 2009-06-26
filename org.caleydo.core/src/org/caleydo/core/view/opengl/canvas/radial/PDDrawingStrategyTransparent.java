package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;

/**
 * 
 * 
 * @author Christian Partl
 *
 */
public class PDDrawingStrategyTransparent
	extends APDDrawingStrategyChildIndicator {

	private float fTransparency;

	/**
	 * Constructor.
	 * 
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the drawn elements.
	 * @param iViewID
	 *            ID of the view where the elements will be displayed. Needed for picking.
	 */
	public PDDrawingStrategyTransparent(PickingManager pickingManager, int iViewID) {
		super(pickingManager, iViewID);
		fTransparency = RadialHierarchyRenderStyle.PARTIAL_DISC_TRANSPARENCY;
	}

	@Override
	public void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw) {

		if (pdDiscToDraw == null)
			return;

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			pdDiscToDraw.getElementID()));
		float fRadius = pdDiscToDraw.getCurrentWidth();
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		if ((pdDiscToDraw.getCurrentDepth() == 1) && (pdDiscToDraw.hasChildren())) {
			drawChildIndicator(gl, pdDiscToDraw.getCurrentInnerRadius(), fRadius, pdDiscToDraw
				.getCurrentStartAngle(), pdDiscToDraw.getCurrentAngle());
		}

		gl.glColor4f(1, 1, 1, fTransparency);
		GLPrimitives.renderCircle(gl, glu, fRadius, iNumSlicesPerFullDisc);

		gl.glColor4f(1, 1, 1, 1);
		GLPrimitives.renderCircleBorder(gl, glu, fRadius, iNumSlicesPerFullDisc, 2);

		gl.glPopAttrib();
		gl.glPopName();

	}

	@Override
	public void drawPartialDisc(GL gl, GLU glu, PartialDisc pdDiscToDraw) {

		if (pdDiscToDraw == null)
			return;

		float fStartAngle = pdDiscToDraw.getCurrentStartAngle();
		float fAngle = pdDiscToDraw.getCurrentAngle();
		float fInnerRadius = pdDiscToDraw.getCurrentInnerRadius();
		float fWidth = pdDiscToDraw.getCurrentWidth();

		float fMidAngle = fStartAngle + (fAngle / 2.0f);
		while (fMidAngle > 360) {
			fMidAngle -= 360;
		}

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			pdDiscToDraw.getElementID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		if ((pdDiscToDraw.getCurrentDepth() == 1) && (pdDiscToDraw.hasChildren())) {
			drawChildIndicator(gl, fInnerRadius, fWidth, fStartAngle, fAngle);
		}

		float fArRGB[];
		if (DrawingStrategyManager.get().getDefaultStrategyType() == DrawingStrategyManager.PD_DRAWING_STRATEGY_RAINBOW) {
			ColorMapping cmRainbow = ColorMappingManager.get().getColorMapping(EColorMappingType.RAINBOW);
			fArRGB = cmRainbow.getColor(fMidAngle / 360);
		}
		else {
			ColorMapping cmExpression =
				ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);
			fArRGB = cmExpression.getColor(pdDiscToDraw.getAverageExpressionValue());
		}

		gl.glPushMatrix();

		gl.glColor4f(fArRGB[0], fArRGB[1], fArRGB[2], fTransparency);
		GLPrimitives.renderPartialDisc(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle, fAngle,
			iNumSlicesPerFullDisc);

		gl.glColor4f(1, 1, 1, 1);
		GLPrimitives.renderPartialDiscBorder(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle,
			fAngle, iNumSlicesPerFullDisc, 2);

		gl.glPopMatrix();
		gl.glPopAttrib();
		gl.glPopName();
	}

	public void setTransparency(float fTransparency) {
		this.fTransparency = fTransparency;
	}

	public float getTransparency() {
		return fTransparency;
	}
}