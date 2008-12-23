package org.caleydo.core.view.opengl.canvas.glyph.gridview.gridpositionmodels;

import gleem.linalg.open.Vec2i;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.media.opengl.GL;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphRenderStyle;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphGridPosition;

public class GlyphGridPositionModelPlus
	extends GlyphGridPositionModel
{
	private HashMap<Integer, HashMap<Integer, Vec2i>> pointmap = null;
	private GlyphManager gman = null;

	public GlyphGridPositionModelPlus(GlyphRenderStyle renderStyle)
	{
		super(renderStyle);
		gman = (GlyphManager) generalManager.getGlyphManager();
	}

	public void setGlyphPositions(Vector<Vector<GlyphGridPosition>> glyphMap,
			ArrayList<GlyphEntry> gg)
	{
		// setGlyphPositions(glyphMap, gg, (worldLimit.x() - 2) / 2,
		// (worldLimit.y() - 2) / 2);
	}

	public void buildGrid(Vector<Vector<GlyphGridPosition>> glyphMap, GL gl)
	{
		if (!gman.isActive())
			return;

		//		
		// int centerX = (worldLimit.x() - 2) / 2;
		// int centerY = (worldLimit.y() - 2) / 2;
		//		
		// glyphCenterGrid.setXY(centerX, centerY);
		// glyphCenterWorld.set(glyphMap.get(centerX).get(centerY).getGridPosition().toVec2f());
		//
		//		
		// // delete list if present (rebuild grid)
		// if (iDisplayListGrid >= 0)
		// gl.glDeleteLists(iDisplayListGrid, 1);
		//
		// TextRenderer textRenderer = renderStyle.getScatterplotTextRenderer();
		// Vec4f gridColor_ = renderStyle.getGridColor();
		//
		// int maxx = worldLimit.x();
		// int maxy = worldLimit.y();
		//
		// //int axisDefX =
		// Integer.parseInt(gman.getSetting(EGlyphSettingIDs.SCATTERPLOTX));
		// int axisDefX = 6; //male/female
		//		
		// int axisDefY =
		// Integer.parseInt(gman.getSetting(EGlyphSettingIDs.SCATTERPLOTY));
		// GlyphAttributeType xdata = gman
		// .getGlyphAttributeTypeWithExternalColumnNumber(axisDefX);
		// GlyphAttributeType ydata = gman
		// .getGlyphAttributeTypeWithExternalColumnNumber(axisDefY);
		//
		// if (xdata == null || ydata == null)
		// {
		// generalManager.getLogger().log(
		// Level.WARNING,
		// "Plus Model axix definition corrupt! (" + axisDefX + ", "
		// + axisDefY + ")");
		// return;
		// }
		//
		// ArrayList<String> xaxisdescription = xdata.getAttributeNames();
		// ArrayList<String> yaxisdescription = ydata.getAttributeNames();
		// xaxisdescription.remove(0); // remove NAV
		// yaxisdescription.remove(0); // remove NAV
		//
		// float incx = (float) maxx / (float) (xaxisdescription.size());
		// float incy = (float) maxy / (float) (yaxisdescription.size());
		//		
		//		
		// float linex = (yaxisdescription.size()) * incy; // we always get NAV
		// // first
		// float liney = (xaxisdescription.size()) * incx; // we always get NAV
		// // first
		//
		//
		// ArrayList<Float> pointsX = new ArrayList<Float>();
		// ArrayList<Float> pointsY = new ArrayList<Float>();
		//
		// iDisplayListGrid = gl.glGenLists(1);
		// gl.glNewList(iDisplayListGrid, GL.GL_COMPILE);
		//		
		// gl.glPushMatrix();
		// gl.glLineWidth(1);
		//
		// gl.glRotatef(-45f, 0, 0, 1);
		// gl.glTranslatef(linex/2, 0, 0f);
		//
		// gl.glBegin(GL.GL_LINES);
		// gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2),
		// gridColor_
		// .get(3));
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(0, liney, 0);
		// gl.glEnd();
		//		
		// gl.glTranslatef(-linex/2, liney/2, 0f);
		// gl.glRotatef(-90f, 0, 0, 1);
		//		
		// gl.glBegin(GL.GL_LINES);
		// gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2),
		// gridColor_
		// .get(3));
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(0, linex, 0);
		// gl.glEnd();
		// /*
		// for (int i = 0; i < xaxisdescription.size(); ++i)
		// {
		// pointsX.add(incx * i + incx / 2.0f);
		// gl.glTranslatef(incx, 0f, 0f);
		//
		// gl.glBegin(GL.GL_LINES);
		// gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2),
		// gridColor_
		// .get(3));
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(0, linex, 0);
		// gl.glEnd();
		//
		// if (i % drawLabelEveryLineX == 0)
		// {
		// gl.glTranslatef(-incx / 2.0f, -2.0f, 0f);
		// textRenderer.begin3DRendering();
		// textRenderer.draw3D(xaxisdescription.get(i), 0, 0, 0, 0.1f);
		// textRenderer.end3DRendering();
		// gl.glTranslatef(incx / 2.0f, +2.0f, 0f);
		// }
		//
		// }
		// // spare point for non valid data
		// pointsX.add(incx * (xaxisdescription.size() + 2));
		//
		// gl.glTranslatef(+0.0f, -4.0f, 0f);
		// textRenderer.begin3DRendering();
		// textRenderer.draw3D(xdata.getName(), 0, 0, 0, 0.1f);
		// textRenderer.end3DRendering();
		// gl.glTranslatef(-0.0f, +4.0f, 0f);
		//
		// gl.glTranslatef(-xaxisdescription.size() * incx, 0f, 0f);
		//
		// gl.glRotatef(-90f, 0, 0, 1);
		//
		// gl.glBegin(GL.GL_LINES);
		// gl.glColor4f(gridColor_.get(0), gridColor_.get(1), gridColor_.get(2),
		// gridColor_
		// .get(3));
		// gl.glVertex3f(0, 0, 0);
		// gl.glVertex3f(0, liney, 0);
		// gl.glEnd();
		// */
		// int yhalf = yaxisdescription.size()/2;
		// for (int i = -yhalf; i < yhalf; ++i)
		// {
		// pointsY.add(incy * i + incy / 2.0f);
		// }
		// int xhalf = xaxisdescription.size()/2;
		// for (int i = -xhalf; i < xhalf; ++i)
		// {
		// pointsX.add(incx * i + incx / 2.0f);
		// }
		//		
		// /*
		// gl.glTranslatef(-0.0f, -4.0f, 0f);
		// textRenderer.begin3DRendering();
		// textRenderer.draw3D(ydata.getName(), 0, 0, 0, 0.1f);
		// textRenderer.end3DRendering();
		// gl.glTranslatef(+0.0f, +4.0f, 0f);
		//
		// gl.glTranslatef(yaxisdescription.size() * incy, 0f, 0f);
		// // spare point for non valid data
		// pointsY.add(incy * (yaxisdescription.size() + 5));
		//
		// gl.glRotatef(135f, 0, 0, 1);
		//
		// scatterpointmap = new HashMap<Integer, HashMap<Integer, Vec2i>>();
		//
		// for (int i = 0; i < pointsX.size(); ++i)
		// {
		// HashMap<Integer, Vec2i> temp = new HashMap<Integer, Vec2i>();
		// scatterpointmap.put(i, temp);
		// for (int j = 0; j < pointsY.size(); ++j)
		// {
		// Vec2i temp2 = new Vec2i();
		//
		// // transform point
		// float y1 = pointsX.get(i);
		// float x1 = pointsY.get(j);
		//
		// double a1 = java.lang.Math.atan(y1 / x1);
		// double c = y1 / java.lang.Math.sin(a1);
		// double a2 = -(java.lang.Math.PI / 4.0 - a1);
		//
		// float x1t = (float) (java.lang.Math.cos(a2) * c);
		// float y1t = (float) (java.lang.Math.sin(a2) * c);
		//
		// double dist = 10000000000000.0;
		// Iterator<Vector<GlyphGridPosition>> it1 = glyphMap.iterator();
		// Iterator<GlyphGridPosition> it2;
		// while (it1.hasNext())
		// {
		// Vector<GlyphGridPosition> vggp = it1.next();
		// it2 = vggp.iterator();
		//
		// while (it2.hasNext())
		// {
		// GlyphGridPosition ggp = it2.next();
		// Vec2i pos = ggp.getGridPosition();
		//
		// int x2 = pos.x();
		// int y2 = pos.y();
		//
		// double dist2 = java.lang.Math.sqrt((x1t - x2) * (x1t - x2)
		// + (y1t - y2) * (y1t - y2));
		// if (dist2 < dist)
		// {
		// dist = dist2;
		// temp2 = ggp.getPosition();
		// }
		// }
		// }
		// temp.put(j, temp2);
		// }
		// }
		// */
		// gl.glPopMatrix();
		//		
		// gl.glEndList();
		//		

	}
	/*
	 * public void setGlyphPositions(Vector<Vector<GlyphGridPosition>>
	 * glyphMap_, ArrayList<GlyphEntry> gg, int centerX, int centerY) { if
	 * (centerX == 0 && centerY == 0) { int num = gg.size(); int x_max = (int)
	 * java.lang.Math.sqrt(num); centerX = x_max / 2 + 1; centerY = x_max / 2 +
	 * 1; } glyphCenterGrid.setXY(centerX, centerY);
	 * glyphCenterWorld.set(glyphMap_
	 * .get(centerX).get(centerY).getGridPosition().toVec2f()); // }
	 */

}
