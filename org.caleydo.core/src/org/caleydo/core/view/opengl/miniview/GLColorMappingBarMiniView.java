package org.caleydo.core.view.opengl.miniview;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.ColorMarkerPoint;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Mini view that renders the current color bar.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLColorMappingBarMiniView
	extends AGLMiniView {
	private TextRenderer textRenderer;
	private GeneralRenderStyle renderStyle;

	/**
	 * Constructor.
	 */
	public GLColorMappingBarMiniView(IViewFrustum viewFrustum) {
		super();
		textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 18), false);
		renderStyle = new GeneralRenderStyle(viewFrustum);
	}

	@Override
	public synchronized void render(GL gl, float fXOrigin, float fYOrigin, float fZOrigin) {
		// TODO: generalize
		textRenderer.setColor(0, 0, 0, 1);
		ColorMapping colorMapper =
			ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		ISet geneExpressionSet = null;
		Collection<ISet> sets = GeneralManager.get().getSetManager().getAllItems();

		int iSetCount = 0;
		for (ISet set : sets) {
			if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA) {
				iSetCount++;
				geneExpressionSet = set;
			}
		}

		// FIXME: Bad hack: find a better way to ensure that not more
		// than one gene expression set is valid for this view.
		if (iSetCount > 1)
			return;

		if (geneExpressionSet == null)
			// this is the case when the application is in pathway viewer mode
			return;

		ArrayList<ColorMarkerPoint> alColorMarkerPoints = colorMapper.getMarkerPoints();

		gl.glBegin(GL.GL_QUAD_STRIP);
		int iCount = 0;
		for (ColorMarkerPoint markerPoint : alColorMarkerPoints) {
			gl.glColor3fv(markerPoint.getColor(), 0);
			float fYCurrent = fYOrigin + fHeight * markerPoint.getValue();
			gl.glVertex3f(fXOrigin, fYCurrent, fZOrigin);
			gl.glVertex3f(fXOrigin + fWidth / 3, fYCurrent, fZOrigin);
			iCount++;
		}
		gl.glEnd();
		for (ColorMarkerPoint markerPoint : alColorMarkerPoints) {
			gl.glColor3fv(markerPoint.getColor(), 0);
			float fYCurrent = fYOrigin + fHeight * markerPoint.getValue();
			// gl.glVertex3f(fXOrigin, fYCurrent, fZOrigin);

			Rectangle2D tempRectangle =
				textRenderer.getBounds(GeneralRenderStyle.getDecimalFormat().format(markerPoint.getValue()));
			// float fBackPlaneWidth = (float) tempRectangle.getWidth()
			// * renderStyle.getSmallFontScalingFactor();
			float fYOffset = (float) tempRectangle.getHeight() * renderStyle.getSmallFontScalingFactor() / 2;

			String infoToRender =
				GeneralRenderStyle.getDecimalFormat().format(
					geneExpressionSet.getRawForNormalized(markerPoint.getValue()));
			textRenderer.begin3DRendering();
			textRenderer.draw3D(infoToRender, fXOrigin + fWidth / 3 + renderStyle.getSmallSpacing(),
				fYCurrent - fYOffset, fZOrigin + 0.01f, renderStyle.getSmallFontScalingFactor());
			textRenderer.end3DRendering();
			iCount++;
		}
	}
}
