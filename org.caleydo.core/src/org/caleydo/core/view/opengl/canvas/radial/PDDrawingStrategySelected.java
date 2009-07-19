package org.caleydo.core.view.opengl.canvas.radial;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;

/**
 * PDDrawingStrategySelected is responsible for drawing partial discs which have been selected in some
 * fashion. Therefore it is using a predefined fill color and a definable border color (for distinguishing
 * between e.g. Mouse Over and Click).
 * 
 * @author Christian Partl
 */
public class PDDrawingStrategySelected
	extends APDDrawingStrategyChildIndicator {

	private float[] fArBorderColor;

	/**
	 * Constructor.
	 * 
	 * @param pickingManager
	 *            The picking manager that should handle the picking of the drawn elements.
	 * @param iViewID
	 *            ID of the view where the elements will be displayed. Needed for picking.
	 */
	public PDDrawingStrategySelected(PickingManager pickingManager, int iViewID) {
		super(pickingManager, iViewID);
		fArBorderColor = RadialHierarchyRenderStyle.MOUSE_OVER_COLOR;
	}

	@Override
	public void drawFullCircle(GL gl, GLU glu, PartialDisc pdDiscToDraw) {

		if (pdDiscToDraw == null)
			return;

		float fRadius = pdDiscToDraw.getCurrentWidth();

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.RAD_HIERARCHY_PDISC_SELECTION,
			pdDiscToDraw.getElementID()));
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		if ((!pdDiscToDraw.isAChildDrawn()) && (pdDiscToDraw.hasChildren())) {
			drawChildIndicator(gl, pdDiscToDraw.getCurrentInnerRadius(), fRadius, pdDiscToDraw
				.getCurrentStartAngle(), pdDiscToDraw.getCurrentAngle());
		}

		gl.glColor4fv(RadialHierarchyRenderStyle.PARTIAL_DISC_MOUSE_OVER_COLOR, 0);
		GLPrimitives.renderCircle(glu, fRadius, iNumSlicesPerFullDisc);

		gl.glColor4fv(fArBorderColor, 0);
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

		if ((!pdDiscToDraw.isAChildDrawn()) && (pdDiscToDraw.hasChildren())) {
			drawChildIndicator(gl, fInnerRadius, fWidth, fStartAngle, fAngle);
		}

		gl.glColor4fv(RadialHierarchyRenderStyle.PARTIAL_DISC_MOUSE_OVER_COLOR, 0);
		GLPrimitives.renderPartialDisc(glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle, fAngle,
			iNumSlicesPerFullDisc);
		gl.glColor4fv(fArBorderColor, 0);
		GLPrimitives.renderPartialDiscBorder(gl, glu, fInnerRadius, fInnerRadius + fWidth, fStartAngle,
			fAngle, iNumSlicesPerFullDisc, RadialHierarchyRenderStyle.PARTIAL_DISC_BORDER_WIDTH);

		gl.glPopAttrib();
		gl.glPopName();

	}

	/**
	 * Gets the color which is used for drawing the partial disc's border.
	 * 
	 * @return RGB-Color which is used to draw the partial disc's border.
	 */
	public float[] getBorderColor() {
		return fArBorderColor;
	}

	/**
	 * Sets the color that shall be used for drawing the partial disc's border.
	 * 
	 * @param fArBorderColor
	 *            RGB-Color which shall be used to draw the partial disc's border. Only the first three values
	 *            of the array will be used.
	 */
	public void setBorderColor(float[] fArBorderColor) {
		if (fArBorderColor.length >= 3) {
			this.fArBorderColor = fArBorderColor;
		}
	}

	@Override
	public EPDDrawingStrategyType getDrawingStrategyType() {
		return EPDDrawingStrategyType.SELECTED;
	}

}