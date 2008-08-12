package org.caleydo.core.view.opengl.util.infoarea;

import gleem.linalg.Vec3f;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.InfoAreaRenderStyle;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.parcoords.EInputDataType;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;

/**
 * Draw Info Areas Pass a point, and ID and a data type and this class manages
 * all the drawing of the info areas uses GLTextInfoAreaRenderer to draw the
 * actual rectangle, is responsible for creating the renderers (multiple in case
 * of star rendering) and draw the connections from the infoarea to the selected
 * element
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLInfoAreaManager
{

	private IGeneralManager generalManager;

	private Point pickedPoint;

	private GLTextInfoAreaRenderer infoArea;

	private float fXOrigin = 0;

	private float fYOrigin = 0;

	private float fXElementOrigin = 0;

	private float fYElementOrigin = 0;

	private Vec3f vecLowerLeft;

	private InformationContentCreator contentCreator;

	private HashMap<Integer, GLInfoOverlayRenderer> hashViewIDToInfoOverlay;

	private boolean bUpdateViewInfo = true;

	private boolean bEnableRendering = true;

	/**
	 * Constructor.
	 * 
	 */
	public GLInfoAreaManager()
	{
		this.generalManager = GeneralManager.get();
		hashViewIDToInfoOverlay = new HashMap<Integer, GLInfoOverlayRenderer>();
	}

	public void initInfoInPlace(final IViewFrustum viewFrustum)
	{

		infoArea = new GLTextInfoAreaRenderer(generalManager, viewFrustum);
	}

	public void initInfoOverlay(final int iViewID, final GLAutoDrawable drawable)
	{

		// Lazy creation to be sure that all managers are already initialized
		if (hashViewIDToInfoOverlay.isEmpty())
			contentCreator = new InformationContentCreator(generalManager);

		if (!hashViewIDToInfoOverlay.containsKey(iViewID))
		{
			GLInfoOverlayRenderer infoOverlayRenderer = new GLInfoOverlayRenderer(
					generalManager);

			hashViewIDToInfoOverlay.put(iViewID, infoOverlayRenderer);

			infoOverlayRenderer.init(drawable);
		}
	}

	/**
	 * Render the data previously set
	 * 
	 * @param gl
	 * @param bFirstTime this has to be true only the first time you render it
	 *            and can never be true after that
	 */
	public void renderInPlaceInfo(GL gl, boolean bFirstTime)
	{

		if (bFirstTime)
		{
			float[] fArWorldCoords = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x,
							pickedPoint.y);

			fXOrigin = fArWorldCoords[0];
			fYOrigin = fArWorldCoords[1];
			fXElementOrigin = fXOrigin + 0.2f;
			fYElementOrigin = fYOrigin + 0.2f;
			vecLowerLeft.set(fXElementOrigin, fYElementOrigin, 0);
		}

		gl.glColor3fv(InfoAreaRenderStyle.INFO_AREA_COLOR, 0);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXOrigin, fYOrigin, InfoAreaRenderStyle.INFO_AREA_CONNECTION_Z);
		gl.glVertex3f(fXElementOrigin, fYElementOrigin,
				InfoAreaRenderStyle.INFO_AREA_CONNECTION_Z);
		gl.glVertex3f(fXElementOrigin, fYElementOrigin + infoArea.getHeight(),
				InfoAreaRenderStyle.INFO_AREA_CONNECTION_Z);
		gl.glEnd();

		infoArea.renderInfoArea(gl, vecLowerLeft, bFirstTime);
	}

	public void renderInfoOverlay(final int iViewID, final GLAutoDrawable drawable)
	{

		if (!bEnableRendering)
			return;

		hashViewIDToInfoOverlay.get(iViewID).render(drawable);
	}

	public void setData(final int iViewID, final int iUniqueID,
			final EInputDataType eInputDataType, final ArrayList<String> sAlContent)
	{

		bUpdateViewInfo = false;

		sAlContent.add("---------------------------------------------------------");
		sAlContent.addAll(contentCreator.getStringContentForID(iUniqueID, eInputDataType));

		Iterator<GLInfoOverlayRenderer> iterInfoOverlay = hashViewIDToInfoOverlay.values()
				.iterator();

		while (iterInfoOverlay.hasNext())
		{
			iterInfoOverlay.next().setData(sAlContent);
		}
	}

	public void setDataAboutView(final int iViewID)
	{

		if (bUpdateViewInfo == false)
		{
			bUpdateViewInfo = true;
			return;
		}

		Iterator<GLInfoOverlayRenderer> iterInfoOverlay = hashViewIDToInfoOverlay.values()
				.iterator();

		while (iterInfoOverlay.hasNext())
		{
			iterInfoOverlay.next().setData(
					((AGLEventListener) generalManager.getViewGLCanvasManager().getEventListener(iViewID))
							.getInfo());
		}
	}

	/**
	 * Set the data to be rendered.
	 * 
	 * @param iCaleydoID
	 * @param eInputDataTypes
	 * @param pickedPoint
	 */
	public void setData(int iCaleydoID, EInputDataType eInputDataTypes, Point pickedPoint)
	{

		// this.sContent = contentCreator.getStringContentForID(iCaleydoID,
		// eInputDataTypes);
		this.pickedPoint = pickedPoint;
		vecLowerLeft = new Vec3f();

		infoArea.setData(iCaleydoID, eInputDataTypes);
		// miniView = new AGLParCoordsMiniView();
		// fXOrigin = 0;
		// fYOrigin = 0;
		// fHeight = 0;
		// fWidth = 0;
	}

	public void setMiniViewData(ArrayList<IStorage> alStorages)
	{

		infoArea.setMiniViewData(alStorages);
	}

	public void enable(final boolean bEnableRendering)
	{

		// this.bEnableRendering = bEnableRendering;
	}
}
