package org.caleydo.core.view.opengl.miniview.slider;

import gleem.linalg.Vec4f;
import java.awt.Font;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.media.opengl.GL;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.opengl.miniview.AGLMiniView;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import com.sun.opengl.util.j2d.TextRenderer;

/**
 * OpenGL Slider
 * 
 * @author Stefan Sauer
 */
public class GLSliderMiniView
	extends AGLMiniView
{
	public enum BORDER
	{
		FULL,
		LEFT,
		RIGHT,
		TOP,
		BOTTOM;
	}

	protected IGeneralManager generalManager = null;
	protected PickingManager pickingManager = null;
	protected PickingJoglMouseListener pickingTriggerMouseAdapter;

	private int iSliderID = 0;

	private boolean bBorderLeft = true;
	private boolean bBorderTop = true;
	private boolean bBorderRight = true;
	private boolean bBorderBottom = true;
	private int iBorderWidth = 1;
	private int iSeperatorWidth = 3;
	private Vec4f vBorderColor = new Vec4f(0.0f, 0.0f, 0.0f, 1.0f);
	private Vec4f vSeperatorColor = new Vec4f(1.0f, 0.0f, 0.0f, 1.0f);
	private Vec4f vSeperatorBondColor = new Vec4f(0.75f, 0.75f, 0.75f, 0.33f);

	private int iUniqueId;
	private int iIdOffset = 1000;

	private ArrayList<SliderSeperator> alSeperator = null;
	private ArrayList<SliderSeperatorBond> alSeperatorBonds = null;
	private ArrayList<Float> alAxisScaleOrdinal = null;
	private ArrayList<String> alAxisScaleNominal = null;

	private boolean bDoDragging = false;
	private boolean bSelectionChanged = false;
	private int iDraggedSeperator = -1;
	float fLastSelectedBlock = -1f;

	TextRenderer textRenderer = null;

	public GLSliderMiniView(final IGeneralManager generalManager,
			PickingJoglMouseListener pickingTriggerMouseAdapter, final int iViewID,
			final int iSliderID)
	{
		this.generalManager = generalManager;
		this.pickingManager = generalManager.getViewGLCanvasManager().getPickingManager();
		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
		this.iUniqueId = iViewID;
		this.iSliderID = iSliderID;

		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 16), false);
		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.setSmoothing(false);

		fWidth = 1.0f;
		fHeight = 1.0f;

		alSeperator = new ArrayList<SliderSeperator>();
		alSeperator.add(new SliderSeperator(0, 0f));
		alSeperator.add(new SliderSeperator(1, 1f));

		alSeperatorBonds = new ArrayList<SliderSeperatorBond>();
		alSeperatorBonds
				.add(new SliderSeperatorBond(0, alSeperator.get(0), alSeperator.get(1)));
		alSeperator.get(0).setBond(alSeperatorBonds.get(0));
		alSeperator.get(1).setBond(alSeperatorBonds.get(0));
	}

	public int getID()
	{
		return iSliderID;
	}

	@Override
	public void setHeight(final float fHeight)
	{
		this.fHeight = fHeight;
		alSeperator.get(0).setPos(0f);
		alSeperator.get(1).setPos(fHeight);
	}

	public void setAxisScale(float min, float max, float increment)
	{
		alAxisScaleOrdinal = new ArrayList<Float>();

		float diff = max - min;
		float howmany = diff / increment;
		float realinc = 1000 / (howmany + 1.0f);

		float inccounter = 0;
		float fActual = min;
		for (int i = 0; i < 1000; ++i)
		{
			alAxisScaleOrdinal.add(fActual);

			if (inccounter >= realinc)
			{
				inccounter = inccounter - realinc;
				fActual += increment;
			}
			++inccounter;
		}
	}

	public void setAxisScale(final ArrayList<String> values)
	{
		alAxisScaleNominal = new ArrayList<String>();

		float realinc = 1000 / (values.size());

		float inccounter = 0;
		int counter = 0;
		for (int i = 0; i < 1000 && counter < values.size(); ++i)
		{
			alAxisScaleNominal.add(values.get(counter));

			if (inccounter >= realinc)
			{
				inccounter = 0;
				counter++;
			}
			++inccounter;
		}

	}

	public ArrayList<Float> getSelectionOrdinal()
	{
		if (alAxisScaleOrdinal == null)
		{
			generalManager.getLogger().log(Level.WARNING,
					"someone wanted ordinal data from GLSlider, but has not set any");

			throw (new CaleydoRuntimeException(
					"someone wanted odinal data from GLSlider, but has not set any"));
		}

		ArrayList<Float> selection = new ArrayList<Float>();

		for (SliderSeperatorBond bond : alSeperatorBonds)
		{
			float top = bond.getTop();
			float bottom = bond.getBottom();
			int lowerindex = (int) ((bottom / fHeight) * 1000);
			int upperindex = (int) ((top / fHeight) * 1000);
			if (upperindex >= 1000)
				upperindex = 999;

			float last = -2;
			for (int i = lowerindex; i <= upperindex; ++i)
			{
				float actual = alAxisScaleOrdinal.get(i);
				if (last != actual)
				{
					selection.add(actual);
					last = actual;
				}
			}
		}

		return selection;
	}

	public ArrayList<String> getSelectionNominal()
	{
		if (alAxisScaleNominal == null)
		{
			generalManager.getLogger().log(Level.WARNING,
					"someone wanted nominal data from GLSlider, but has not set any");

			throw (new CaleydoRuntimeException(
					"someone wanted nominal data from GLSlider, but has not set any"));
		}

		ArrayList<String> selection = new ArrayList<String>();

		for (SliderSeperatorBond bond : alSeperatorBonds)
		{
			float top = bond.getTop();
			float bottom = bond.getBottom();
			int lowerindex = (int) ((bottom / fHeight) * 1000);
			int upperindex = (int) ((top / fHeight) * 1000);

			String last = "";
			for (int i = lowerindex; i <= upperindex; ++i)
			{
				String actual = alAxisScaleNominal.get(i);
				if (!last.equals(actual))
				{
					selection.add(actual);
					last = actual;
				}
			}
		}

		return selection;
	}

	public void setBorderWidth(final int width)
	{
		iBorderWidth = width;
	}

	public void setBorder(BORDER borderpart, boolean onoff)
	{
		switch (borderpart)
		{
			case LEFT:
				bBorderLeft = onoff;
				break;
			case TOP:
				bBorderTop = onoff;
				break;
			case RIGHT:
				bBorderRight = onoff;
				break;
			case BOTTOM:
				bBorderBottom = onoff;
				break;
			default:
				bBorderLeft = onoff;
				bBorderTop = onoff;
				bBorderRight = onoff;
				bBorderBottom = onoff;
				break;
		}
	}

	public void setBorderColor(Vec4f color)
	{
		vBorderColor = color;
	}

	public void setSeperatorColor(Vec4f color)
	{
		vSeperatorColor = color;
	}

	public void setSeperatorBondColor(Vec4f color)
	{
		vSeperatorBondColor = color;
	}

	@Override
	public void render(GL gl, float fXOrigin, float fYOrigin, float fZOrigin)
	{
		gl.glPushMatrix();

		drawBorder(gl);

		drawSeperator(gl);

		drawSeperatorBond(gl);

		gl.glPopMatrix();
	}

	private void drawBorder(GL gl)
	{
		gl.glPushMatrix();
		gl.glLineWidth(iBorderWidth);

		if (bBorderLeft)
		{
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2),
					vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, fHeight, 0);
			gl.glEnd();
		}

		gl.glTranslatef(0f, fHeight, 0f);

		if (bBorderTop)
		{
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2),
					vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(fWidth, 0, 0);
			gl.glEnd();
		}

		gl.glTranslatef(fWidth, 0f, 0f);

		if (bBorderRight)
		{
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2),
					vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, -fHeight, 0);
			gl.glEnd();
		}

		gl.glTranslatef(0f, -fHeight, 0f);

		if (bBorderBottom)
		{
			gl.glBegin(GL.GL_LINES);
			gl.glColor4f(vBorderColor.get(0), vBorderColor.get(1), vBorderColor.get(2),
					vBorderColor.get(3));
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(-fWidth, 0, 0);
			gl.glEnd();
		}

		gl.glLineWidth(1);
		gl.glPopMatrix();
	}

	private void drawSeperator(GL gl)
	{
		for (SliderSeperator Seperator : alSeperator)
		{
			// recalculate position
			if (bDoDragging && iDraggedSeperator == Seperator.getID())
			{
				float[] fArTargetWorldCoordinates = GLCoordinateUtils
						.convertWindowCoordinatesToWorldCoordinates(gl, 0,
								pickingTriggerMouseAdapter.getPickedPoint().y);
				float canvasY = fArTargetWorldCoordinates[1];

				if (canvasY > fHeight)
					canvasY = fHeight;
				if (canvasY < 0)
					canvasY = 0;

				int actualindex = (int) ((canvasY / fHeight) * 1000);
				if (actualindex >= 1000)
					actualindex = 999;
				float actual = alAxisScaleOrdinal.get(actualindex);

				if (Seperator.getPos() != canvasY && fLastSelectedBlock != actual)
				{
					bSelectionChanged = true;
					fLastSelectedBlock = actual;
				}

				Seperator.setPos(canvasY);
			}

			gl.glPushMatrix();
			gl.glColor4f(vSeperatorColor.get(0), vSeperatorColor.get(1), vSeperatorColor
					.get(2), vSeperatorColor.get(3));

			gl.glTranslatef(0f, Seperator.getPos(), 0.1f);

			gl.glPushName(pickingManager
					.getPickingID(iUniqueId, EPickingType.Y_AXIS_SELECTION,
							(iSliderID * iIdOffset) + Seperator.getID()));

			float linewidthhalf = iSeperatorWidth / 200.0f;

			gl.glBegin(GL.GL_QUADS);
			gl.glNormal3i(0, 1, 0);
			gl.glVertex3f(0, linewidthhalf, 0);
			gl.glVertex3f(fWidth, linewidthhalf, 0);
			gl.glVertex3f(fWidth, -linewidthhalf, 0);
			gl.glVertex3f(0, -linewidthhalf, 0);
			gl.glEnd();

			// draw handle
			gl.glTranslatef(fWidth, 0, 0);

			gl.glBegin(GL.GL_QUADS);
			gl.glNormal3i(0, 1, 0);
			gl.glVertex3f(0, linewidthhalf, 0);
			gl.glVertex3f(0.2f, 0.2f, 0);
			gl.glVertex3f(0.2f, -0.2f, 0);
			gl.glVertex3f(0, -linewidthhalf, 0);
			gl.glEnd();

			gl.glPopName();

			gl.glLineWidth(1);

			// calculate relative position for text label
			float prozent = Seperator.getPos() / fHeight;
			int index = (int) (prozent * 1000);
			prozent = index / 1000f;

			String value = "";
			if (alAxisScaleOrdinal != null)
				if (alAxisScaleOrdinal.size() > index)
					value = Float.toString(alAxisScaleOrdinal.get(index));

			if (alAxisScaleNominal != null)
				if (alAxisScaleNominal.size() > index)
					value = alAxisScaleNominal.get(index);

			gl.glTranslatef(0.3f, -0.15f, 0);

			float scale = 0.25f;
			gl.glScalef(scale, scale, scale);
			textRenderer.begin3DRendering();
			textRenderer.draw3D(value, 0, 0, 0, 0.1f);
			// textRenderer.draw3D(Integer.toString( Seperator.getID()) + " " +
			// value, 0, 0, 0, 0.1f);
			textRenderer.end3DRendering();

			gl.glPopMatrix();
		}

		if (pickingTriggerMouseAdapter.wasMouseReleased())
		{
			if (bDoDragging)
				bSelectionChanged = true;

			bDoDragging = false;
		}

	}

	private void drawSeperatorBond(GL gl)
	{
		for (SliderSeperatorBond bond : alSeperatorBonds)
			bond.render(gl, fWidth, fHeight, vSeperatorBondColor);
	}

	public boolean handleEvents(EPickingType pickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{

		if (pickingMode == EPickingMode.DRAGGED)
		{
			int seperatorid = iExternalID % 1000;
			int sliderid = (iExternalID - seperatorid) / iIdOffset;

			if (iSliderID == sliderid && !bDoDragging)
			{
				bDoDragging = true;
				iDraggedSeperator = seperatorid;

				SliderSeperator seperator = alSeperator.get(seperatorid);

				if (!seperator.hasSeperatorBond())
				{ // this is a new seperator -> create bond + second seperator
					SliderSeperator newseperator = new SliderSeperator(alSeperator.size());
					alSeperator.add(newseperator);

					SliderSeperatorBond newbond = new SliderSeperatorBond(alSeperatorBonds
							.size(), seperator, newseperator);
					alSeperatorBonds.add(newbond);

					newseperator.setPos(seperator.getPos());
					newseperator.setBond(newbond);
					seperator.setBond(newbond);

				}
				else
				{
					// has bond, but we check if it is in the bounds (we need a
					// new seperator if it is)
					if (seperator.getPos() == 0 || seperator.getPos() == fHeight)
					{
						SliderSeperator newseperator = new SliderSeperator(alSeperator.size());
						alSeperator.add(newseperator);
						newseperator.setPos(seperator.getPos());
					}
				}
				return true;
			}
		}
		return false;
	}

	public boolean hasSelectionChanged()
	{
		if (bSelectionChanged)
		{
			bSelectionChanged = false;
			return true;
		}
		return bSelectionChanged;
	}

}
