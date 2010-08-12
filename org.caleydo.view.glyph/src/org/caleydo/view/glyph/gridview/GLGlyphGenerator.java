package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import gleem.linalg.Vec4f;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.util.wavefrontobjectloader.ObjectDimensions;
import org.caleydo.core.view.opengl.util.wavefrontobjectloader.ObjectGroup;
import org.caleydo.view.glyph.gridview.GlyphObjectDefinition.ANCHOR;
import org.caleydo.view.glyph.gridview.GlyphObjectDefinition.DIRECTION;
import org.caleydo.view.glyph.gridview.data.GlyphAttributeType;
import org.caleydo.view.glyph.manager.EGlyphSettingIDs;
import org.caleydo.view.glyph.manager.GlyphManager;

/**
 * This class is the generator for all glyphs. Here the object file is linked with all the data parameters and
 * drawn.
 * 
 * @author Stefan Sauer
 */
public class GLGlyphGenerator {
	private GlyphManager gman;

	private static HashMap<Integer, GlyphObjectDefinition> objectModels =
		new HashMap<Integer, GlyphObjectDefinition>();

	private DETAILLEVEL iDetailLevel;

	private boolean bIsInit;

	static public enum DETAILLEVEL {
		LEVEL_MIN,
		LEVEL_MID,
		LEVEL_MAX,
	}

	/**
	 * Constructor
	 * 
	 * @param isLocal
	 *            is this a local (true) or remote (false) view?
	 */
	public GLGlyphGenerator(boolean isLocal) {
		gman = GeneralManager.get().getGlyphManager();

		bIsInit = false;
	}

	/**
	 * This sets the Glyph Object Model for a detail level. The level is defined in the GlyphObjectDefinition.
	 * 
	 * @param glyphDefinition
	 *            The full Model
	 */
	public static void setDetailLevelModel(GlyphObjectDefinition glyphDefinition) {
		int level = glyphDefinition.getDetailLevel();
		if (objectModels.containsKey(level)) {
			objectModels.remove(level);
			// TODO force refresh
		}
		objectModels.put(level, glyphDefinition);
	}

	/**
	 * This returns the Glyph Object Model for a detail level.
	 * 
	 * @param level
	 *            The Detail Level
	 * @return The Model
	 */
	public static GlyphObjectDefinition getDetailLevelModel(int level) {
		if (objectModels.containsKey(level))
			return objectModels.get(level);

		return null;
	}

	/**
	 * Sets the current detail level.
	 * 
	 * @param detail
	 *            The wanted detail level.
	 */
	public void setDetailLevel(DETAILLEVEL detail) {
		iDetailLevel = detail;
	}

	/**
	 * Returns the current detail level.
	 * 
	 * @return The current detail level
	 */
	public DETAILLEVEL getDetailLevel() {
		return iDetailLevel;
	}

	/**
	 * Generates the Display List for the given Glyph. It also initializes the Model parts of the glyph, if
	 * they are not initialized already.
	 * 
	 * @param gl
	 *            GL Context
	 * @param glyph
	 *            The glyph you want to render
	 * @param selected
	 *            Is the Glyph selected?
	 * @return The generated Display List
	 */
	public int generateGlyph(GL gl, GlyphEntry glyph, boolean selected) {
		if (!bIsInit) {
			for (GlyphObjectDefinition modelDefinition : objectModels.values()) {
				ArrayList<String> partnames = modelDefinition.getObjectPartNames();
				for (String partname : partnames) {
					modelDefinition.getObjectPart(partname).init(gl);
				}
			}
			bIsInit = true;
		}

		return generateSingleObject(gl, glyph, selected);
	}

	private int generateSingleObject(GL gl, GlyphEntry glyph, boolean selected) {
		if (iDetailLevel == DETAILLEVEL.LEVEL_MIN)
			return generateSingleObjectDetailLevel(gl, glyph, selected, 0);

		if (iDetailLevel == DETAILLEVEL.LEVEL_MID)
			return generateSingleObjectDetailLevel(gl, glyph, selected, 1);

		if (iDetailLevel == DETAILLEVEL.LEVEL_MAX)
			return generateSingleObjectDetailLevel(gl, glyph, selected, 2);

		// fallback
		return generateSingleObjectDetailLevel(gl, glyph, selected, 1);
	}

	private int generateSingleObjectDetailLevel(GL gl, GlyphEntry glyph, boolean selected, int level) {
		if (level < 0)
			return -1;
		if (objectModels.size() <= level)
			return -1;

		GlyphObjectDefinition modelDefinition = objectModels.get(level);

		int dltemp = gl.glGenLists(1);
		gl.glNewList(dltemp, GL.GL_COMPILE);

		gl.glPushMatrix();

		// enable light
		initEnviroment(gl);

		// rotate object (because the coordinate systems are not the same)
		gl.glRotatef(+90f, 1, 0, 0);

		HashMap<String, ObjectDimensions> objectDimensions = new HashMap<String, ObjectDimensions>();

		ArrayList<String> partnames = modelDefinition.getObjectPartNames();
		for (String partname : partnames) {
			GlyphObjectDefinitionPart partdef = modelDefinition.getObjectPartDefinition(partname);
			if (partdef == null) {
				continue;
			}

			gl.glPushMatrix();

			ObjectGroup group = modelDefinition.getObjectPart(partname);

			ObjectDimensions dim = group.getDimensions();

			// color
			{
				int index = partdef.getParameterIndexInternal(EGlyphSettingIDs.COLOR, null);
				if (index >= 0) {
					float nv = 0.0f;
					// if the box isn't selected color will be darker
					if (!selected) {
						nv = 0.5f;
					}

					int tc = glyph.getParameter(index);
					Vec4f color = partdef.getColor(tc);

					if (color != null && tc >= 0) {
						if (color.get(0) != -1.0f && color.get(1) != -1.0f && color.get(2) != -1.0f) {
							gl.glColor4f(color.get(0) - nv, color.get(1) - nv, color.get(2) - nv, color
								.get(3));
							// else, do nothing (keep old color)
						}

					}
					else {
						gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f); // Annoying gray
					}

				}
				else {
					// base color
					if (selected) {
						gl.glColor4f(0.1f, 0.6f, 0.1f, 1.0f);
					}
					else {
						gl.glColor4f(0.1f, 0.35f, 0.1f, 1.0f);
					}
				}
			}

			// ObjectDimensions og = group.getDimensions();

			// TODO scale x
			// TODO scale z
			// scale part
			if (partdef.canScale(DIRECTION.Z)) {
				int index = partdef.getParameterIndexInternal(EGlyphSettingIDs.SCALE, DIRECTION.Z);
				if (index >= 0) {
					GlyphAttributeType glyphAttributeType =
						gman.getGlyphAttributeTypeWithInternalColumnNumber(index);

					float scale = glyph.getParameter(index);

					if (scale < 0.1f) {
						scale = 1.0f;
					}

					scale = scale / glyphAttributeType.getMaxIndex();
					dim.scaleY(scale);
				}
			}
			// TODO anchor top
			// TODO anchor left
			// TODO anchor right
			// TODO anchor front
			// TODO anchor back
			// anchors
			float anchorOffsetX = 0;
			float anchorOffsetY = 0;
			float anchorOffsetZ = 0;
			{
				String place = null;
				place = partdef.getAnchorPlace(ANCHOR.BOTTOM);
				if (place != null) {
					ObjectDimensions pdim = objectDimensions.get(place);

					if (pdim != null) {
						float pyh = pdim.getHighestY(true);
						// float pyl = pdim.getLowestY(true);
						// float cyh = dim.getHighestY(false);
						float cyl = dim.getLowestY(false);

						anchorOffsetY = -pyh + cyl - pdim.getScaleOffsetY(false);

						// System.out.println("(" + pyl + ", " + pyh + "),(" +
						// cyl + ", " + cyh
						// + ") => " + anchorOffsetY);
					}
				}

				// place = partdef.getAnchorPlace(ANCHOR.TOP);
				// if (place != null)
				// {
				// ObjectDimensions pdim = objectDimensions.get(place);
				//
				// if (pdim != null)
				// {
				// float pyh = pdim.getHighestY(true);
				// float pyl = pdim.getLowestY(true);
				// float cyh = dim.getHighestY(true);
				// float cyl = dim.getLowestY(true);
				//
				// System.out.println("(" + pyl + ", " + pyh + "),(" + cyl +
				// ", " + cyh
				// + ")");
				//
				// anchorOffsetY = (-pyl + (cyh - dim.getScaleOffsetY(true)));
				// }
				// }
			}

			gl.glScalef(1.0f, dim.getScaleY(), 1.0f);
			gl.glTranslatef(0, dim.getScaleOffsetY(true), 0);
			gl.glTranslatef(anchorOffsetX, -anchorOffsetY, anchorOffsetZ);

			group.draw(gl);

			objectDimensions.put(partname, dim);

			gl.glPopMatrix();
		}

		gl.glRotatef(-90f, 1, 0, 0);

		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_LIGHT0);
		gl.glDisable(GL.GL_LIGHT1);
		gl.glDisable(GL.GL_LIGHT2);

		gl.glDisable(GL.GL_CULL_FACE);

		gl.glPopMatrix();

		gl.glEndList();

		return dltemp;
	}

	/**
	 * Initializes the "world" around the glyph.
	 * 
	 * @param gl
	 *            GL Context
	 */
	private void initEnviroment(GL gl) {
		// gl.glEnable(GL.GL_BLEND);
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE);

		float[] mat_ambient = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] mat_diffuse = { 1.0f, 1.0f, 1.0f, 1.0f };

		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient, 0);

		// gl.glEnable(GL.GL_AUTO_NORMAL);
		gl.glEnable(GL.GL_NORMALIZE);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_CULL_FACE);
		// gl.glEnable(GL.GL_LINE_SMOOTH);

		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);

		// light
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_LIGHT1);
		gl.glEnable(GL.GL_LIGHT2);

		float lc = 0.1f;

		float[] diffuse_light0 = { 5 * lc, 5 * lc, 5 * lc, 1.0f };
		float[] position_light0 = { 0.0f, 4.0f, 0.0f, 1.0f };
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse_light0, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position_light0, 0);

		float[] diffuse_light1 = { 3 * lc, 3 * lc, 3 * lc, 1.0f };
		float[] position_light1 = { -3.0f, 0.0f, 2.0f, 1.0f };
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, diffuse_light1, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position_light1, 0);

		float[] diffuse_light2 = { 2 * lc, 2 * lc, 2 * lc, 1.0f };
		float[] position_light2 = { 5.5f, 0.0f, 2.0f, 1.0f };
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, diffuse_light2, 0);
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, position_light2, 0);

		float[] ambient_lightModel = { 5 * lc, 5 * lc, 5 * lc, 1.0f };
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, ambient_lightModel, 0);
	}

}
