package org.caleydo.core.view.opengl.canvas.glyph.gridview.gridpositionmodels;

import gleem.linalg.Vec4f;
import gleem.linalg.open.Vec2i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import javax.media.opengl.GL;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphRenderStyle;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphGridPosition;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.data.GlyphAttributeType;
import com.sun.opengl.util.j2d.TextRenderer;

public class GlyphGridPositionModelScatterplot
	extends GlyphGridPositionModel
{
	private HashMap<Integer, HashMap<Integer, Vec2i>> scatterpointmap = null;
	private GlyphManager gman = null;

	public GlyphGridPositionModelScatterplot(GlyphRenderStyle renderStyle)
	{
		super(renderStyle);
		gman = (GlyphManager) generalManager.getGlyphManager();
	}

	public void buildGrid(Vector<Vector<GlyphGridPosition>> glyphMap, GL gl)
	{
		if (!gman.isActive())
			return;

		// delete list if present (rebuild grid)
		if (iDisplayListGrid >= 0)
			gl.glDeleteLists(iDisplayListGrid, 1);

		TextRenderer textRenderer = renderStyle.getScatterplotTextRenderer();
		Vec4f gridColor_ = renderStyle.getGridColor();

		int maxx = worldLimit.x();// - (worldLimit.x() / 5);
		int maxy = maxx;// worldLimit.y() - (worldLimit.y() / 5);

		int scatterParamX = Integer.parseInt(gman.getSetting(EGlyphSettingIDs.SCATTERPLOTX));
		int scatterParamY = Integer.parseInt(gman.getSetting(EGlyphSettingIDs.SCATTERPLOTY));
		GlyphAttributeType xdata = gman
				.getGlyphAttributeTypeWithExternalColumnNumber(scatterParamX);
		GlyphAttributeType ydata = gman
				.getGlyphAttributeTypeWithExternalColumnNumber(scatterParamY);

		if (xdata == null || ydata == null)
		{
			generalManager.getLogger().log(
					Level.WARNING,
					"Scatterplot axix definition corrupt! (" + scatterParamX + ", "
							+ scatterParamY + ")");
			return;
		}

		ArrayList<String> xaxisdescription = xdata.getAttributeNames();
		ArrayList<String> yaxisdescription = ydata.getAttributeNames();
		xaxisdescription.remove(0); // remove NAV
		yaxisdescription.remove(0); // remove NAV

		float incx = (float) maxx / (float) (xaxisdescription.size());
		float incy = (float) maxy / (float) (yaxisdescription.size());
		float linex = (yaxisdescription.size()) * incy; // we always get NAV
		// first
		float liney = (xaxisdescription.size()) * incx; // we always get NAV
		// first

		// if(incx<1.0f) incx = 1.0f;
		// if(incy<1.0f) incy = 1.0f;

		int drawLabelEveryLineX = 1;
		int drawLabelEveryLineY = 1;

		if (incy < 2)
			drawLabelEveryLineY = 3;
		if (incy < 1)
			drawLabelEveryLineY = 5;
		if (incy < 0.1f)
			drawLabelEveryLineY = 50;

		ArrayList<Float> pointsX = new ArrayList<Float>();
		ArrayList<Float> pointsY = new ArrayList<Float>();

		iDisplayListGrid = gl.glGenLists(1);
		gl.glNewList(iDisplayListGrid, GL.GL_COMPILE);
		gl.glLineWidth(1);

		gl.glRotatef(-45f, 0, 0, 1);

		gl.glBegin(GL.GL_LINES);
		gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2), gridColor_
				.get(3));
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, linex, 0);
		gl.glEnd();

		for (int i = 0; i < xaxisdescription.size(); ++i)
		{
			pointsX.add(incx * i + incx / 2.0f);
			gl.glTranslatef(incx, 0f, 0f);

			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2), gridColor_
					.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, linex, 0);
			gl.glEnd();

			if (i % drawLabelEveryLineX == 0)
			{
				gl.glTranslatef(-incx / 2.0f, -2.0f, 0f);
				textRenderer.begin3DRendering();
				textRenderer.draw3D(xaxisdescription.get(i), 0, 0, 0, 0.1f);
				textRenderer.end3DRendering();
				gl.glTranslatef(incx / 2.0f, +2.0f, 0f);
			}

		}
		// spare point for non valid data
		pointsX.add(incx * (xaxisdescription.size() + 2));

		gl.glTranslatef(+0.0f, -4.0f, 0f);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(xdata.getName(), 0, 0, 0, 0.1f);
		textRenderer.end3DRendering();
		gl.glTranslatef(-0.0f, +4.0f, 0f);

		gl.glTranslatef(-xaxisdescription.size() * incx, 0f, 0f);

		gl.glRotatef(-90f, 0, 0, 1);

		gl.glBegin(GL.GL_LINES);
		gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2), gridColor_
				.get(3));
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, liney, 0);
		gl.glEnd();

		for (int i = 0; i < yaxisdescription.size(); ++i)
		{
			pointsY.add(incy * i + incy / 2.0f);
			gl.glTranslatef(-incy, 0f, 0f);

			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2), gridColor_
					.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, liney, 0);
			gl.glEnd();

			if (i % drawLabelEveryLineY == 0)
			{
				gl.glTranslatef(+incy / 2.0f, -2.0f, 0f);
				textRenderer.begin3DRendering();
				textRenderer.draw3D(yaxisdescription.get(i), 0, 0, 0, 0.1f);
				textRenderer.end3DRendering();
				gl.glTranslatef(-incy / 2.0f, +2.0f, 0f);
			}

		}
		gl.glTranslatef(-0.0f, -4.0f, 0f);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(ydata.getName(), 0, 0, 0, 0.1f);
		textRenderer.end3DRendering();
		gl.glTranslatef(+0.0f, +4.0f, 0f);

		gl.glTranslatef(yaxisdescription.size() * incy, 0f, 0f);
		// spare point for non valid data
		pointsY.add(incy * (yaxisdescription.size() + 5));

		gl.glRotatef(135f, 0, 0, 1);

		scatterpointmap = new HashMap<Integer, HashMap<Integer, Vec2i>>();

		for (int i = 0; i < pointsX.size(); ++i)
		{
			HashMap<Integer, Vec2i> temp = new HashMap<Integer, Vec2i>();
			scatterpointmap.put(i, temp);
			for (int j = 0; j < pointsY.size(); ++j)
			{
				Vec2i temp2 = new Vec2i();

				// transform point
				float y1 = pointsX.get(i);
				float x1 = pointsY.get(j);

				double a1 = java.lang.Math.atan(y1 / x1);
				double c = y1 / java.lang.Math.sin(a1);
				double a2 = -(java.lang.Math.PI / 4.0 - a1);

				float x1t = (float) (java.lang.Math.cos(a2) * c);
				float y1t = (float) (java.lang.Math.sin(a2) * c);

				double dist = 10000000000000.0;
				Iterator<Vector<GlyphGridPosition>> it1 = glyphMap.iterator();
				Iterator<GlyphGridPosition> it2;
				while (it1.hasNext())
				{
					Vector<GlyphGridPosition> vggp = it1.next();
					it2 = vggp.iterator();

					while (it2.hasNext())
					{
						GlyphGridPosition ggp = it2.next();
						Vec2i pos = ggp.getGridPosition();

						int x2 = pos.x();
						int y2 = pos.y();

						double dist2 = java.lang.Math.sqrt((x1t - x2) * (x1t - x2)
								+ (y1t - y2) * (y1t - y2));
						if (dist2 < dist)
						{
							dist = dist2;
							temp2 = ggp.getPosition();
						}
					}
				}
				temp.put(j, temp2);
			}
		}

		gl.glEndList();

		glyphCenterGrid.setXY(maxx / 2, maxx / 2);
		glyphCenterWorld.set(glyphMap.get(glyphCenterGrid.x()).get(glyphCenterGrid.y())
				.getGridPosition().toVec2f());

	}

	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap,
			ArrayList<GlyphEntry> gg)
	{
		GlyphGridPositionModelCircle posModel = new GlyphGridPositionModelCircle(renderStyle);
		posModel.setWorldLimit(worldLimit.x(), worldLimit.y());

		int scatterParamXe = Integer.parseInt(gman.getSetting(EGlyphSettingIDs.SCATTERPLOTX));
		int scatterParamYe = Integer.parseInt(gman.getSetting(EGlyphSettingIDs.SCATTERPLOTY));
		GlyphAttributeType tx = gman
				.getGlyphAttributeTypeWithExternalColumnNumber(scatterParamXe);
		GlyphAttributeType ty = gman
				.getGlyphAttributeTypeWithExternalColumnNumber(scatterParamYe);
		int scatterParamX = tx.getInternalColumnNumber();
		int scatterParamY = ty.getInternalColumnNumber();

		if (tx == null || ty == null)
		{
			generalManager.getLogger().log(Level.WARNING,
					"setGlyphPositionsScatterplot(); Scatterplot axix definition corrupt!");
			return;
		}

		int maxX = tx.getMaxIndex();
		int maxY = ty.getMaxIndex();

		for (GlyphEntry g : gg)
		{
			ArrayList<GlyphEntry> alge = new ArrayList<GlyphEntry>();
			int xp = g.getParameter(scatterParamX);
			int yp = g.getParameter(scatterParamY);

			if (xp < 0)
				xp = maxX + 1;
			if (yp < 0)
				yp = maxY + 1;

			alge.add(g);
			Vec2i pos = scatterpointmap.get(xp).get(yp);
			posModel.setGlyphPositions(glyphMap, alge, pos.x(), pos.y());
		}

	}

}
