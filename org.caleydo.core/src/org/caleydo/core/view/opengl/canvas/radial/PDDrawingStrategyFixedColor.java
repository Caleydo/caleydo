package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;

/**
 * PDDrawingStrategyFixedColor uses a definable color for drawing the partial disc. The color is not affected
 * by any of the partial disc's properties.
 * 
 * @author Christian Partl
 */
public class PDDrawingStrategyFixedColor
	extends APDDrawingStrategyChildIndicator {

	private float fFillColorR;
	private float fFillColorG;
	private float fFillColorB;
	private float fFillAlpha;

	private float fBorderColorR;
	private float fBorderColorG;
	private float fBorderColorB;
	private float fBorderAlpha;

	/**
	 * Constructor.
	 * 
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the drawn elements.
	 * @param iViewID
	 *            ID of the view where the elements will be displayed. Needed for picking.
	 */
	public PDDrawingStrategyFixedColor(PickingManager pickingManager, int iViewID) {
		super(pickingManager, iViewID);

		fFillColorR = 0.0f;
		fFillColorG = 0.0f;
		fFillColorB = 0.0f;
		fFillAlpha = 1.0f;
		fBorderColorR = 1.0f;
		fBorderColorG = 1.0f;
		fBorderColorB = 1.0f;
		fBorderAlpha = 1.0f;
	}

	@Override
	public void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw) {

		if (pdDiscToDraw == null)
			return;

		float fRadius = pdDiscToDraw.getCurrentWidth();

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			pdDiscToDraw.getElementID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		if ((pdDiscToDraw.getCurrentDepth() == 1) && (pdDiscToDraw.hasChildren())) {
			drawChildIndicator(gl, pdDiscToDraw.getCurrentInnerRadius(), fRadius, pdDiscToDraw
				.getCurrentStartAngle(), pdDiscToDraw.getCurrentAngle());
		}

		gl.glColor4f(fFillColorR, fFillColorG, fFillColorB, fFillAlpha);
		GLPrimitives.renderCircle(gl, glu, fRadius, iNumSlicesPerFullDisc);

		gl.glColor4f(fBorderColorR, fBorderColorG, fBorderColorB, fBorderAlpha);
		GLPrimitives.renderCircleBorder(gl, glu, fRadius, iNumSlicesPerFullDisc,
			RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_WIDTH);

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

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			pdDiscToDraw.getElementID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		if ((pdDiscToDraw.getCurrentDepth() == 1) && (pdDiscToDraw.hasChildren())) {
			drawChildIndicator(gl, fInnerRadius, fWidth, fStartAngle, fAngle);
		}

		gl.glColor4f(fFillColorR, fFillColorG, fFillColorB, fFillAlpha);
		GLPrimitives.renderPartialDisc(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle, fAngle,
			iNumSlicesPerFullDisc);

		gl.glColor4f(fBorderColorR, fBorderColorG, fBorderColorB, fBorderAlpha);
		GLPrimitives.renderPartialDiscBorder(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle,
			fAngle, iNumSlicesPerFullDisc, RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_WIDTH);

		gl.glPopAttrib();
		gl.glPopName();

	}

	/**
	 * Sets the color the partial disc shall be filled with.
	 * 
	 * @param fColorR Red portion of the fill color.
	 * @param fColorG Green portion of the fill color.
	 * @param fColorB Blue portion of the fill color.
	 * @param fAlpha Transparency value of the fill color.
	 */
	public void setFillColor(float fColorR, float fColorG, float fColorB, float fAlpha) {
		fFillColorR = fColorR;
		fFillColorG = fColorG;
		fFillColorB = fColorB;
		fFillAlpha = fAlpha;
	}

	/**
	 * Sets the color the partial disc's border shall be drawn with.
	 * 
	 * @param fColorR Red portion of the border color.
	 * @param fColorG Green portion of the border color.
	 * @param fColorB Blue portion of the border color.
	 * @param fAlpha Transparency value of the border color.
	 */
	public void setBorderColor(float fColorR, float fColorG, float fColorB, float fAlpha) {
		fBorderColorR = fColorR;
		fBorderColorG = fColorG;
		fBorderColorB = fColorB;
		fBorderAlpha = fAlpha;
	}

}
