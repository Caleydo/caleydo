package org.caleydo.core.view.opengl.miniview;

import gleem.linalg.Vec3f;
import java.util.ArrayList;
import javax.media.opengl.GL;
import org.caleydo.core.data.view.rep.renderstyle.GeneralRenderStyle;
import org.caleydo.core.util.mapping.ColorMapper;

/**
 * Color Mapping for Expression data TODO: not working yet
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */

public class GLColorMappingMiniView
	extends AGLMiniView
{

	private ColorMapper genomeMapper;

	private int iMappingRowCount = 1;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 */
	public GLColorMappingMiniView()
	{
		genomeMapper = new ColorMapper();
		// genomeMapper.setMappingData(alSets);
	}

	@Override
	public void render(GL gl, float fXOrigin, float fYOrigin, float fZOrigin)
	{

		// TODO Auto-generated method stub

	}

	private void drawMapping(final GL gl, final ArrayList<Vec3f> alMappingColor,
			final float fNodeWidth, final float fNodeHeight, final boolean bEnableGrid)
	{

		int iColumnCount = (int) Math.ceil((float) alMappingColor.size()
				/ (float) iMappingRowCount);

		Vec3f tmpNodeColor = null;

		gl.glPushMatrix();

		// If no mapping is available - render whole node in one color
		if (alMappingColor.size() == 1)
		{
			tmpNodeColor = alMappingColor.get(0);

			// Check if the mapping gave back a valid color
			// if (tmpNodeColor.x() == -1)
			// tmpNodeColor = renderStyle.getEnzymeNodeColor(true);

			gl.glColor3f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z());
			// gl.glCallList(iEnzymeNodeDisplayListId);
		}
		else
		{
			gl.glTranslatef(-fNodeWidth + fNodeWidth / iColumnCount, -fNodeHeight
					+ fNodeHeight / iMappingRowCount, 0.0f);

			for (int iRowIndex = 0; iRowIndex < iMappingRowCount; iRowIndex++)
			{
				for (int iColumnIndex = 0; iColumnIndex < iColumnCount; iColumnIndex++)
				{
					int iCurrentElement = iRowIndex * iMappingRowCount + iColumnIndex;

					if (iCurrentElement < alMappingColor.size())
						tmpNodeColor = alMappingColor.get((iRowIndex + 1) * iColumnIndex);
					else
						continue;

					// Check if the mapping gave back a valid color
					if (tmpNodeColor.x() != -1)
					{
						gl.glColor3f(tmpNodeColor.x(), tmpNodeColor.y(), tmpNodeColor.z());
						gl.glScalef(1.0f / iColumnCount, 1.0f / iMappingRowCount, 1.0f);
						// gl.glCallList(iEnzymeNodeDisplayListId);
						gl.glScalef(iColumnCount, iMappingRowCount, 1.0f);
					}

					gl.glTranslatef(fNodeWidth * 2.0f / iColumnCount, 0.0f, 0.0f);
				}

				gl.glTranslatef(-2.0f * fNodeWidth, 2.0f * fNodeHeight / iMappingRowCount,
						0.0f);
			}
		}

		gl.glPopMatrix();

		// Render grid
		if (bEnableGrid)
		{
			gl.glColor3f(1, 1, 1);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(-fNodeWidth, -fNodeHeight, GeneralRenderStyle.MINIVEW_Z);
			gl.glVertex3f(fNodeWidth, -fNodeHeight, GeneralRenderStyle.MINIVEW_Z);
			gl.glVertex3f(fNodeWidth, fNodeHeight, GeneralRenderStyle.MINIVEW_Z);
			gl.glVertex3f(-fNodeWidth, fNodeHeight, GeneralRenderStyle.MINIVEW_Z);
			gl.glEnd();

			gl.glBegin(GL.GL_LINES);
			for (int iRowIndex = 1; iRowIndex <= iMappingRowCount; iRowIndex++)
			{
				gl.glVertex3f(-fNodeWidth, -fNodeHeight + (2 * fNodeHeight / iMappingRowCount)
						* iRowIndex, GeneralRenderStyle.MINIVEW_Z);
				gl.glVertex3f(fNodeWidth, -fNodeHeight + (2 * fNodeHeight / iMappingRowCount)
						* iRowIndex, GeneralRenderStyle.MINIVEW_Z);
			}
			for (int iColumnIndex = 1; iColumnIndex <= iColumnCount; iColumnIndex++)
			{
				gl.glVertex3f(-fNodeWidth + (2 * fNodeWidth / iColumnCount) * iColumnIndex,
						fNodeHeight, GeneralRenderStyle.MINIVEW_Z);
				gl.glVertex3f(-fNodeWidth + (2 * fNodeWidth / iColumnCount) * iColumnIndex,
						-fNodeHeight, GeneralRenderStyle.MINIVEW_Z);
			}
			gl.glEnd();
		}
	}

}
