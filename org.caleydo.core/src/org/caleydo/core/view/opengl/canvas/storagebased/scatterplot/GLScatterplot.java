package org.caleydo.core.view.opengl.canvas.storagebased.scatterplot;

import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.AXIS_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.NUMBER_AXIS_MARKERS;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.POINTSIZE;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.POINTSTYLE;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.XLABELDISTANCE;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.XLABELROTATIONNAGLE;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.XYAXISDISTANCE;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.X_AXIS_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.X_AXIS_LINE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.YLABELDISTANCE;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.YLABELROTATIONNAGLE;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.Y_AXIS_COLOR;
import static org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.ScatterPlotRenderStyle.Y_AXIS_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.getDecimalFormat;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;

import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAType;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.event.view.radial.UpdateDepthSliderPositionEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.manager.usecase.EDataFilterLevel;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.AffinityClusterer;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.clusterer.EDistanceMeasure;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.SerializedHeatMapView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.ExperimentContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.GeneContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

import org.caleydo.core.manager.event.view.storagebased.TogglePointTypeEvent;
import org.caleydo.core.manager.event.view.storagebased.SetPointSizeEvent;
import org.caleydo.core.manager.event.view.storagebased.XAxisSelectorEvent;
import org.caleydo.core.manager.event.view.storagebased.YAxisSelectorEvent;
import org.caleydo.core.manager.event.view.storagebased.InitAxisComboEvent;
import org.caleydo.core.manager.general.GeneralManager;

import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.listener.TogglePointTypeListener;
import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.listener.SetPointSizeListener;
import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.listener.XAxisSelectorListener;
import org.caleydo.core.view.opengl.canvas.storagebased.scatterplot.listener.YAxisSelectorListener;



import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Rendering the GLSecatterplott
 * 
 * @author Alexander Lex
 * @author Marc Streit
 * @author Jürgen Pillhofer
 */
public class GLScatterplot
	extends AStorageBasedView {

	private ScatterPlotRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;
	private EIDType eStorageDataType = EIDType.EXPERIMENT_INDEX;

	private Vec3f vecTranslation;

	private float fAnimationTranslation = 0;

	private ArrayList<Float> fAlXDistances;

	boolean bUseDetailLevel = true;

	int iCurrentMouseOverElement = -1;

	public static int SELECTED_X_AXIS = 0;
	public static int SELECTED_Y_AXIS = 1;
	
	// listeners
	
	private TogglePointTypeListener togglePointTypeListener;
	private SetPointSizeListener setPointSizeListener;
	private XAxisSelectorListener xAxisSelectorListener;
	private YAxisSelectorListener yAxisSelectorListener;
	
	private SelectionManager elementSelectionManager;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLScatterplot(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum);
		viewType = EManagedObjectType.GL_HEAT_MAP;

		// ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		// alSelectionTypes.add(ESelectionType.NORMAL);
		// alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		// alSelectionTypes.add(ESelectionType.SELECTION);

		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		fAlXDistances = new ArrayList<Float>();

		// glKeyListener = new GLHeatMapKeyListener(this);
	}

	@Override
	public void init(GL gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new ScatterPlotRenderStyle(this, viewFrustum);

		super.renderStyle = renderStyle;
		
		
		InitAxisComboEvent initAxisComboEvent = new InitAxisComboEvent();
		initAxisComboEvent.setSender(this);
		initAxisComboEvent.setAxisNames(this.getAxisString());
		GeneralManager.get().getEventPublisher().triggerEvent(initAxisComboEvent);
	}



	@Override
	public void initLocal(GL gl) {
		bRenderStorageHorizontally = false;

		// // Register keyboard listener to GL canvas
		// GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// parentGLCanvas.getParentComposite().addKeyListener(glKeyListener);
		// }
		// });

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLEventListener glParentView,
		final GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		if (glRemoteRenderingView instanceof GLRemoteRendering)
			renderStyle.disableFishEye();

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {
			public void run() {
				glParentView.getParentGLCanvas().getParentComposite().addKeyListener(glKeyListener);
			}
		});

		bRenderStorageHorizontally = false;

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		if (bUseDetailLevel) {
			super.setDetailLevel(detailLevel);
		}
		// renderStyle.setDetailLevel(detailLevel);

	}

	@Override
	public void displayLocal(GL gl) {

		// if (set == null)
		// return;
		//
		// if (bIsTranslationAnimationActive) {
		// doTranslation();
		// }

		pickingManager.handlePicking(this, gl);

		if (bIsDisplayListDirtyLocal) {
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL gl) {

		if (set == null)
			return;

		// if (bIsTranslationAnimationActive) {
		// bIsDisplayListDirtyRemote = true;
		// doTranslation();
		// }

		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
			// generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clearTransformedConnections();
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);

		// glMouseListener.resetEvents();
	}

	/**
	 * Render the coordinate system of the parallel coordinates, including the axis captions and axis-specific
	 * buttons
	 * 
	 * @param gl
	 *            the gl context
	 * @param iNumberAxis
	 */
	private void renderCoordinateSystem(GL gl) {

		textRenderer.setColor(0, 0, 0, 1);

		// axisVA = contentVA;
		// axisVAType = contentVAType;
		// polylineVA = storageVA;
		// polylineVAType = storageVAType;
		// int iNumberAxis = contentVA.size();

		// Markers On Axis
		float fXPosition = XYAXISDISTANCE;
		float fYPosition = XYAXISDISTANCE;
		float fMarkerSpacingY = renderStyle.getAxisHeight() / (NUMBER_AXIS_MARKERS + 1);
		float fMarkerSpacingX = renderStyle.getAxisWidth() / (NUMBER_AXIS_MARKERS + 1);
		for (int iInnerCount = 1; iInnerCount <= NUMBER_AXIS_MARKERS + 1; iInnerCount++) {
			float fCurrentHeight = fMarkerSpacingY * iInnerCount;
			float fCurrentWidth = fMarkerSpacingX * iInnerCount;

			if (set.isSetHomogeneous()) {
				float fNumber = (float) set.getRawForNormalized(fCurrentHeight / renderStyle.getAxisHeight());

				Rectangle2D bounds =
					textRenderer.getScaledBounds(gl, getDecimalFormat().format(fNumber), renderStyle
						.getSmallFontScalingFactor(), ScatterPlotRenderStyle.MIN_NUMBER_TEXT_SIZE);
				float fWidth = (float) bounds.getWidth();
				float fHeight = (float) bounds.getHeight();
				float fHeightHalf = fHeight / 2.0f;
				float fWidthHalf = fWidth / 2.0f;

				renderNumber(gl, getDecimalFormat().format(fNumber), fXPosition - fWidth - AXIS_MARKER_WIDTH,
					fCurrentHeight - fHeightHalf + XYAXISDISTANCE);

				renderNumber(gl, getDecimalFormat().format(fNumber), fCurrentWidth - fWidthHalf
					+ XYAXISDISTANCE, fYPosition - AXIS_MARKER_WIDTH - fHeight);

			}

			gl.glColor4fv(X_AXIS_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fCurrentWidth + XYAXISDISTANCE, fYPosition - AXIS_MARKER_WIDTH, AXIS_Z);
			gl.glVertex3f(fCurrentWidth + XYAXISDISTANCE, fYPosition + AXIS_MARKER_WIDTH, AXIS_Z);
			gl.glEnd();

			gl.glColor4fv(Y_AXIS_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fXPosition - AXIS_MARKER_WIDTH, fCurrentHeight + XYAXISDISTANCE, AXIS_Z);
			gl.glVertex3f(fXPosition + AXIS_MARKER_WIDTH, fCurrentHeight + XYAXISDISTANCE, AXIS_Z);
			gl.glEnd();
		}

		// draw X-Axis
		gl.glColor4fv(X_AXIS_COLOR, 0);
		gl.glLineWidth(X_AXIS_LINE_WIDTH);

		// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.X_AXIS_SELECTION, 1));
		gl.glBegin(GL.GL_LINES);

		gl.glVertex3f(XYAXISDISTANCE, XYAXISDISTANCE, 0.0f);
		gl.glVertex3f((renderStyle.getRenderWidth() - XYAXISDISTANCE), XYAXISDISTANCE, 0.0f);
		// gl.glVertex3f(5.0f, XYAXISDISTANCE, 0.0f);

		gl.glEnd();
		// gl.glPopName();

		// draw all Y-Axis

		gl.glColor4fv(Y_AXIS_COLOR, 0);
		gl.glLineWidth(Y_AXIS_LINE_WIDTH);

		// gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.X_AXIS_SELECTION, 1));
		gl.glBegin(GL.GL_LINES);

		// float fXAxisOverlap = 0.1f;

		gl.glVertex3f(XYAXISDISTANCE, XYAXISDISTANCE, AXIS_Z);
		gl.glVertex3f(XYAXISDISTANCE, renderStyle.getRenderHeight() - XYAXISDISTANCE, AXIS_Z);

		gl.glEnd();
		// gl.glPopName();

		// LABEL X

		// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glTranslatef(renderStyle.getLAbelWidth(), XLABELDISTANCE, 0);
		gl.glRotatef(XLABELROTATIONNAGLE, 0, 0, 1);
		textRenderer.begin3DRendering();
		float fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;

		String sAxisLabel = "X-Achse: " + set.get(SELECTED_X_AXIS).getLabel();
		textRenderer.draw3D(gl, sAxisLabel, 0, 0, 0, fScaling,
			ScatterPlotRenderStyle.MIN_AXIS_LABEL_TEXT_SIZE);
		textRenderer.end3DRendering();
		gl.glRotatef(-XLABELROTATIONNAGLE, 0, 0, 1);
		gl.glTranslatef(-renderStyle.getLAbelWidth(), -XLABELDISTANCE, 0);
		// gl.glPopAttrib();

		// LABEL Y

		// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glTranslatef(YLABELDISTANCE, renderStyle.getLabelHeight(), 0);
		gl.glRotatef(YLABELROTATIONNAGLE, 0, 0, 1);

		textRenderer.begin3DRendering();
		fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;

		sAxisLabel = "Y-Achse: " + set.get(SELECTED_Y_AXIS).getLabel();

		// sAxisLabel ="Y-Achse: "+set.get(2).getLabel()+" (O) / "+set.get(3).getLabel()+" (X)";
		textRenderer.draw3D(gl, sAxisLabel, 0, 0, 0, fScaling,
			ScatterPlotRenderStyle.MIN_AXIS_LABEL_TEXT_SIZE);
		textRenderer.end3DRendering();

		gl.glRotatef(-YLABELROTATIONNAGLE, 0, 0, 1);
		gl.glTranslatef(-YLABELDISTANCE, renderStyle.getLabelHeight(), 0);
		// gl.glPopAttrib();

	
	}

	private void renderNumber(GL gl, String sRawValue, float fXOrigin, float fYOrigin) {
		textRenderer.begin3DRendering();

		// String text = "";
		// if (Float.isNaN(fRawValue))
		// text = "NaN";
		// else
		// text = getDecimalFormat().format(fRawValue);

		float fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;

		textRenderer.draw3D(gl, sRawValue, fXOrigin, fYOrigin, ScatterPlotRenderStyle.TEXT_ON_LABEL_Z,
			fScaling, ScatterPlotRenderStyle.MIN_NUMBER_TEXT_SIZE);
		textRenderer.end3DRendering();
	}

	@Override
	public void display(GL gl) {
		processEvents();

		// GLHelperFunctions.drawAxis(gl);
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		// gl.glEnable(GL.GL_DEPTH_TEST);

		// clipToFrustum(gl);

		gl.glCallList(iGLDisplayListToCall);

		// buildDisplayList(gl, iGLDisplayListIndexRemote);

		// if (!isRenderedRemote())
		// contextMenu.render(gl, this);
	}
	
	private String[] getAxisString() {
		String[] tmpString = new String[storageVA.size()] ;
		for (Integer iStorageIndex : storageVA) {
			
			 tmpString[iStorageIndex] = set.get(iStorageIndex).getLabel();
			
		}
		return tmpString;
	}

	private void RenderScatterPoints(GL gl) {
		// int maxindex1 = set.size(); // contentVA
		// int maxindex2 = maxindex1;
		//		
		// maxindex1=6;
		// maxindex2=6;
		//		
		//int maxpoints = set.get(1).size();
		
		//contentVA.indicesOf(1);
		//int testcont= contentVA.size();
		//int testsize= storageVA.size();

		// boolean bSelectedPlot=false;
		// for (int iStorageIndex1=0;iStorageIndex1<maxindex1;iStorageIndex1++)
		// {
		// for (int iStorageIndex2=0;iStorageIndex2<maxindex2;iStorageIndex2++)
		// {
		// if (iStorageIndex2== iStorageIndex1) continue;

		// if (contentSelectionManager.checkStatus(ESelectionType.DESELECTED, iContentIndex))
		// if (iStorageIndex1==SELECTED_X_AXIS && iStorageIndex2==SELECTED_Y_AXIS)
		// bSelectedPlot=true;
		// else
		// bSelectedPlot=false;

		float XScale = renderStyle.getRenderWidth() - XYAXISDISTANCE * 2.0f;
		float YScale = renderStyle.getRenderHeight() - XYAXISDISTANCE * 2.0f;

		//for (int iContentIndex = 0; iContentIndex < maxpoints; iContentIndex++) {
		for (Integer iContentIndex : contentVA) {
		
			if (iContentIndex == -1) {
				// throw new
				// IllegalStateException("No such element in virtual array");
				// TODO this shouldn't happen here.
				continue;
			}
			// float xnormalized = set.get(iStorageIndex1).getFloat(EDataRepresentation.NORMALIZED,
			// iContentIndex);
			// float ynormalized = set.get(iStorageIndex2).getFloat(EDataRepresentation.NORMALIZED,
			// iContentIndex);
			float xnormalized =
				set.get(SELECTED_X_AXIS).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);
			float ynormalized =
				set.get(SELECTED_Y_AXIS).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);
			float x = xnormalized * XScale;
			float y = ynormalized * YScale;
			// //float[] fArMappingColor = colorMapper.getColor(xnormalized);
			// if (bSelectedPlot)
			// {
			float[] fArMappingColor = colorMapper.getColor(Math.max(xnormalized, ynormalized));
			DrawPointPrimitive(gl, x, y, 0.0f, // z
				fArMappingColor, 1.0f, iContentIndex); // fOpacity
			
			if (elementSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iContentIndex))
			{
				DrawLabel(gl, x, y, 0.0f, // z
					fArMappingColor, 1.0f, iContentIndex); // fOpacity
			}
			// }
		} // end iContentIndex
		// }
		// }
	}

	
	
	private void DrawLabel(GL gl, float x, float y, float z, float[] fArMappingColor, float fOpacity, int iContentIndex) {
		
		
		z= z+2.1f;
		gl.glTranslatef(x, y, z);		
		textRenderer.begin3DRendering();
		float fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;		
		String sLabel = "Point: " + 
						set.get(SELECTED_X_AXIS).getFloat(EDataRepresentation.RAW, iContentIndex)+
						" / " +
						set.get(SELECTED_Y_AXIS).getFloat(EDataRepresentation.RAW, iContentIndex);
		textRenderer.draw3D(gl, sLabel, 0, 0, 0, fScaling,
			ScatterPlotRenderStyle.MIN_AXIS_LABEL_TEXT_SIZE);
		textRenderer.end3DRendering();
		
		gl.glTranslatef(-x, -y, -z);
	}
	
	private void DrawPointPrimitive(GL gl, float x, float y, float z, float[] fArMappingColor, float fOpacity, int iContentIndex) {
		
		
		EScatterPointType type = POINTSTYLE;

		
		float halfPoint= POINTSIZE/2.0f;
		float fullPoint = POINTSIZE;
		int iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.SCATTER_POINT_SELECTION, iContentIndex);
		//gl.glColor4f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);
		gl.glColor3f(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2]);
		
		int test = elementSelectionManager.getNumberOfElements(ESelectionType.SELECTION);
		int test2 = elementSelectionManager.getNumberOfElements(ESelectionType.MOUSE_OVER);
		
		
		
		if (elementSelectionManager.checkStatus(ESelectionType.SELECTION, iContentIndex))
		{
			z=+1.0f;
			gl.glColor3f(1.0f, 0.1f, 0.5f);
		}
		
		if (elementSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iContentIndex))
		{
			z=+2.0f;
			fullPoint= 0.03f;
			gl.glColor3f(1.0f, 1.0f, 0.0f);			
			type = EScatterPointType.DISK;
		}
				
		gl.glPushName(iPickingID);
		switch (type) {
			case BOX: {
				gl.glBegin(GL.GL_POLYGON);			
				gl.glVertex3f(x-halfPoint, y-halfPoint, z);
				gl.glVertex3f(x-halfPoint, y + halfPoint, z);
				gl.glVertex3f(x + halfPoint, y + halfPoint, z);
				gl.glVertex3f(x + halfPoint, y-halfPoint, z);
				gl.glEnd();
				break;
			}
			case POINT: {
				gl.glPointSize(POINTSIZE * 20.0f);
				gl.glBegin(GL.GL_POINTS);
				gl.glVertex3f(x, y, z);
				gl.glEnd();
				break;
			}
			case CROSS: {
				gl.glLineWidth(1.0f);
				gl.glBegin(GL.GL_LINES);				
				gl.glVertex3f(x-halfPoint, y-halfPoint, z);
				gl.glVertex3f(x + halfPoint, y + halfPoint, z);
				gl.glVertex3f(x-halfPoint, y + halfPoint, z);
				gl.glVertex3f(x + halfPoint, y-halfPoint, z);
				gl.glEnd();
			}
				break;
			case CIRCLE: {
				float angle;
				float PI = (float) Math.PI;

				gl.glLineWidth(1.0f);				
				gl.glBegin(GL.GL_LINE_LOOP);			
				for (int i = 0; i < 10; i++) {
					angle = (i * 2 * PI) / 10;
					gl.glVertex3f(x + (float) (Math.cos(angle) * fullPoint), y
						+ (float) (Math.sin(angle) * fullPoint), z);
				}
				gl.glEnd();
			}
				break;
			case DISK: {
				float angle;
				float PI = (float) Math.PI;
								
				gl.glBegin(GL.GL_POLYGON);
				for (int i = 0; i < 10; i++) {
					angle = (i * 2 * PI) / 10;
					gl.glVertex3f(x + (float) (Math.cos(angle) * fullPoint), y
						+ (float) (Math.sin(angle) * fullPoint), z);
				}
				gl.glEnd();
			}
				break;
			default:
				
		}
		gl.glPopName();
	}
	

	
	public void togglePointType() {
		
		
		 switch (POINTSTYLE)
        {
        	case POINT:
          POINTSTYLE = EScatterPointType.BOX;
              	  break;
        	case BOX:
       	  POINTSTYLE = EScatterPointType.CIRCLE;
       	  		break;                    
          case CIRCLE:
       	  POINTSTYLE = EScatterPointType.DISK;
        	  break;
          case DISK:
           	  POINTSTYLE = EScatterPointType.CROSS;
            	  break;
          case CROSS:
           	  POINTSTYLE = EScatterPointType.POINT;
            	  break;         
          default:
             
        }
						
		setDisplayListDirty();
	}


	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		// if (contentSelectionManager.getNumberOfElements() == 0) {
		// renderSymbol(gl);
		// }
		// else {

		gl.glTranslatef(XYAXISDISTANCE, XYAXISDISTANCE, 0);
		

		RenderScatterPoints(gl);

		gl.glTranslatef(-XYAXISDISTANCE, -XYAXISDISTANCE, 0);
		renderCoordinateSystem(gl);
		// }
		gl.glEndList();
	}

	/**
	 * Render the symbol of the view instead of the view
	 * 
	 * @param gl
	 */
	private void renderSymbol(GL gl) {
		float fXButtonOrigin = 0.33f * renderStyle.getScaling();
		float fYButtonOrigin = 0.33f * renderStyle.getScaling();
		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.HEAT_MAP_SYMBOL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin * 2, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin * 2, fYButtonOrigin, 0.01f);
		gl.glEnd();
		gl.glPopAttrib();
		tempTexture.disable();
	}

	public void renderHorizontally(boolean bRenderStorageHorizontally) {

		this.bRenderStorageHorizontally = bRenderStorageHorizontally;
		// renderStyle.setBRenderStorageHorizontally(bRenderStorageHorizontally);
		setDisplayListDirty();
	}

	@Override
	protected void initLists() {
		if (contentVAType != EVAType.CONTENT_EMBEDDED_HM) {
			if (bRenderOnlyContext)
				contentVAType = EVAType.CONTENT_CONTEXT;
			else
				contentVAType = EVAType.CONTENT;
		}

		contentVA = useCase.getVA(contentVAType);
		storageVA = useCase.getVA(storageVAType);
		
		elementSelectionManager = storageSelectionManager;

	}

	@Override
	public String getShortInfo() {
		if (contentVA == null)
			return "Scatterplot - 0 " + useCase.getContentLabel(false, true) + " / 0 experiments";

		return "Scatterplot - " + contentVA.size() + " " + useCase.getContentLabel(false, true) + " / "
			+ storageVA.size() + " experiments";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Heat Map\n");

		if (bRenderStorageHorizontally) {
			sInfoText.append(contentVA.size() + " " + useCase.getContentLabel(false, true)
				+ " in columns and " + storageVA.size() + " experiments in rows.\n");
		}
		else {
			sInfoText.append(contentVA.size() + " " + useCase.getContentLabel(true, true) + " in rows and "
				+ storageVA.size() + " experiments in columns.\n");
		}

		if (bRenderOnlyContext) {
			sInfoText.append("Showing only " + " " + useCase.getContentLabel(false, true)
				+ " which occur in one of the other views in focus\n");
		}
		else {
			if (bUseRandomSampling) {
				sInfoText.append("Random sampling active, sample size: " + iNumberOfRandomElements + "\n");
			}
			else {
				sInfoText.append("Random sampling inactive\n");
			}

			if (dataFilterLevel == EDataFilterLevel.COMPLETE) {
				sInfoText.append("Showing all genes in the dataset\n");
			}
			else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING) {
				sInfoText.append("Showing all genes that have a known DAVID ID mapping\n");
			}
			else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
				sInfoText
					.append("Showing all genes that are contained in any of the KEGG or Biocarta pathways\n");
			}
		}

		return sInfoText.toString();
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		ESelectionType eSelectionType;
		switch (ePickingType) {
			case SCATTER_POINT_SELECTION:
				iCurrentMouseOverElement = iExternalID;
				switch (pickingMode) {

					case CLICKED:
						eSelectionType = ESelectionType.SELECTION;
						break;
					case MOUSE_OVER:

						eSelectionType = ESelectionType.MOUSE_OVER;
						elementSelectionManager.clearSelection(eSelectionType);

						break;
					case RIGHT_CLICKED:
						eSelectionType = ESelectionType.SELECTION;

					default:
						return;

				}

				createContentSelection(eSelectionType, iExternalID);

				break;

		
		
		}
	}

	private void createContentSelection(ESelectionType selectionType, int contentID) {
		if (elementSelectionManager.checkStatus(selectionType, contentID))
			return;

		// check if the mouse-overed element is already selected, and if it is, whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == ESelectionType.MOUSE_OVER
			&& elementSelectionManager.checkStatus(ESelectionType.SELECTION, contentID)
			&& elementSelectionManager.getElements(ESelectionType.MOUSE_OVER).size() == 0)
			return;

		connectedElementRepresentationManager.clear(EIDType.EXPRESSION_INDEX);

		if (elementSelectionManager.checkStatus(ESelectionType.SELECTION, contentID) && selectionType == ESelectionType.SELECTION)
			elementSelectionManager.removeFromType(selectionType, contentID);
		
		
		
//		SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR, selectionType);
//		sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);
		
		int test = elementSelectionManager.getNumberOfElements();
		elementSelectionManager.add(contentID);
		elementSelectionManager.addToType(selectionType, contentID);
		test = elementSelectionManager.getNumberOfElements();

		
		
		
//		// TODO: Integrate multi spotting support again
//		// // Resolve multiple spotting on chip and add all to the
//		// // selection manager.
//		// Integer iRefSeqID =
//		// idMappingManager.getID(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT, iExternalID);
//		//
//		Integer iMappingID = generalManager.getIDManager().createID(EManagedObjectType.CONNECTION);
//		// for (Object iExpressionIndex : idMappingManager.getMultiID(
//		// EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX, iRefSeqID)) {
//		// contentSelectionManager.addToType(eSelectionType, (Integer) iExpressionIndex);
//		// contentSelectionManager.addConnectionID(iMappingID, (Integer) iExpressionIndex);
//		// }
//		contentSelectionManager.addToType(selectionType, contentID);
//		contentSelectionManager.addConnectionID(iMappingID, contentID);
//
//		if (eFieldDataType == EIDType.EXPRESSION_INDEX) {
//			SelectionDelta selectionDelta = contentSelectionManager.getDelta();
//
//			// SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR,
//			// eSelectionType);
//			// sendSelectionCommandEvent(EIDType.REFSEQ_MRNA_INT, command);
//
//			handleConnectedElementRep(selectionDelta);
//			SelectionUpdateEvent event = new SelectionUpdateEvent();
//			event.setSender(this);
//			event.setSelectionDelta(selectionDelta);
//			event.setInfo(getShortInfo());
//			eventPublisher.triggerEvent(event);
//		}

		setDisplayListDirty();
	}

	private void createStorageSelection(ESelectionType selectionType, int storageID) {
		if (storageSelectionManager.checkStatus(selectionType, storageID))
			return;

		// check if the mouse-overed element is already selected, and if it is, whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == ESelectionType.MOUSE_OVER
			&& storageSelectionManager.checkStatus(ESelectionType.SELECTION, storageID)
			&& storageSelectionManager.getElements(ESelectionType.MOUSE_OVER).size() == 0)
			return;

		storageSelectionManager.clearSelection(selectionType);
		storageSelectionManager.addToType(selectionType, storageID);

		if (eStorageDataType == EIDType.EXPERIMENT_INDEX) {

			// SelectionCommand command = new SelectionCommand(ESelectionCommandType.CLEAR,
			// eSelectionType);
			// sendSelectionCommandEvent(EIDType.EXPERIMENT_INDEX, command);

			SelectionDelta selectionDelta = storageSelectionManager.getDelta();
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta(selectionDelta);
			eventPublisher.triggerEvent(event);
		}
		setDisplayListDirty();
	}

	private int cursorSelect(IVirtualArray virtualArray, SelectionManager selectionManager, boolean isUp) {

		Set<Integer> elements = selectionManager.getElements(ESelectionType.MOUSE_OVER);
		if (elements.size() == 0) {
			elements = selectionManager.getElements(ESelectionType.SELECTION);
			if (elements.size() == 0)
				return -1;
		}

		if (elements.size() == 1) {
			Integer element = elements.iterator().next();
			int index = virtualArray.indexOf(element);
			int newIndex;
			if (isUp) {
				newIndex = index - 1;
				if (newIndex < 0)
					return -1;
			}
			else {
				newIndex = index + 1;
				if (newIndex == virtualArray.size())
					return -1;

			}
			return virtualArray.get(newIndex);

		}
		return -1;
	}

	@Override
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta) {
		// TODO
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(EIDType idType, int iStorageIndex)
		throws InvalidAttributeValueException {

		SelectedElementRep elementRep;
		ArrayList<SelectedElementRep> alElementReps = new ArrayList<SelectedElementRep>(4);

		for (int iContentIndex : contentVA.indicesOf(iStorageIndex)) {
			if (iContentIndex == -1) {
				// throw new
				// IllegalStateException("No such element in virtual array");
				// TODO this shouldn't happen here.
				continue;
			}

			float fXValue = fAlXDistances.get(iContentIndex); // + renderStyle.getSelectedFieldWidth() / 2;
			// float fYValue = 0;
			float fYValue = renderStyle.getYCenter();

			// Set<Integer> mouseOver = storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);
			// for (int iLineIndex : mouseOver)
			// {
			// fYValue = storageVA.indexOf(iLineIndex) * renderStyle.getFieldHeight() +
			// renderStyle.getFieldHeight()/2;
			// break;
			// }

			int iViewID = iUniqueID;
			// If rendered remote (hierarchical heat map) - use the remote view ID
			if (glRemoteRenderingView != null)
				iViewID = glRemoteRenderingView.getID();

			if (bRenderStorageHorizontally) {
				elementRep =
					new SelectedElementRep(EIDType.EXPRESSION_INDEX, iViewID,
						fXValue + fAnimationTranslation, fYValue, 0);

			}
			else {
				Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float) Math.PI / 2);
				Vec3f vecPoint = myRotf.rotateVector(new Vec3f(fXValue, fYValue, 0));
				vecPoint.setY(vecPoint.y() + vecTranslation.y());
				elementRep =
					new SelectedElementRep(EIDType.EXPRESSION_INDEX, iViewID, vecPoint.x(), vecPoint.y()
						- fAnimationTranslation, 0);

			}
			alElementReps.add(elementRep);
		}
		return alElementReps;
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext) {

		this.bRenderOnlyContext = bRenderOnlyContext;

		if (this.bRenderOnlyContext) {
			contentVA = useCase.getVA(EVAType.CONTENT_CONTEXT);
		}
		else {
			contentVA = useCase.getVA(EVAType.CONTENT);
		}

		contentSelectionManager.setVA(contentVA);
		// renderStyle.setActiveVirtualArray(iContentVAID);

		setDisplayListDirty();

	}

	@Override
	public void handleVirtualArrayUpdate(IVirtualArrayDelta delta, String info) {

		super.handleVirtualArrayUpdate(delta, info);

		if (delta.getVAType() == EVAType.CONTENT_CONTEXT && contentVAType == EVAType.CONTENT_CONTEXT) {
			if (contentVA.size() == 0)
				return;
			// FIXME: this is only proof of concept - use the cluster manager instead of affinity directly
			// long original = System.currentTimeMillis();
			// System.out.println("beginning clustering");
			AffinityClusterer clusterer = new AffinityClusterer(contentVA.size());
			ClusterState state =
				new ClusterState(EClustererAlgo.AFFINITY_PROPAGATION, EClustererType.GENE_CLUSTERING,
					EDistanceMeasure.EUCLIDEAN_DISTANCE);
			int contentVAID = contentVA.getID();
			state.setContentVaId(contentVA.getID());
			state.setStorageVaId(storageVA.getID());
			state.setAffinityPropClusterFactorGenes(4.0f);
			IVirtualArray tempVA = clusterer.getSortedVA(set, state, 0, 2);

			contentVA = tempVA;
			contentSelectionManager.setVA(contentVA);
			contentVA.setID(contentVAID);
			// long result = System.currentTimeMillis() - original;
			// System.out.println("Clustering took in ms: " + result);

		}
	}

	@Override
	public void changeOrientation(boolean defaultOrientation) {
		renderHorizontally(defaultOrientation);
	}

	@Override
	public boolean isInDefaultOrientation() {
		return bRenderStorageHorizontally;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHeatMapView serializedForm = new SerializedHeatMapView(dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}

	@Override
	public String toString() {
		return "Standalone heat map, rendered remote: " + isRenderedRemote() + ", contentSize: "
			+ contentVA.size() + ", storageSize: " + storageVA.size() + ", contentVAType: " + contentVAType
			+ ", remoteRenderer:" + getRemoteRenderingGLCanvas();
	}

	@Override
	public RemoteLevelElement getRemoteLevelElement() {

		// If the view is rendered remote - the remote level element from the parent is returned
		if (glRemoteRenderingView != null && glRemoteRenderingView instanceof AGLEventListener)
			return ((AGLEventListener) glRemoteRenderingView).getRemoteLevelElement();

		return super.getRemoteLevelElement();
	}



@Override
public void registerEventListeners() {
	super.registerEventListeners();

	togglePointTypeListener = new TogglePointTypeListener();
	togglePointTypeListener.setHandler(this);
	eventPublisher.addListener(TogglePointTypeEvent.class, togglePointTypeListener);
	
	setPointSizeListener = new SetPointSizeListener();
	setPointSizeListener.setHandler(this);
	eventPublisher.addListener(SetPointSizeEvent.class, setPointSizeListener);
	
	
	
	xAxisSelectorListener = new XAxisSelectorListener();
	xAxisSelectorListener.setHandler(this);
	eventPublisher.addListener(XAxisSelectorEvent.class, xAxisSelectorListener);
	
	yAxisSelectorListener = new YAxisSelectorListener();
	yAxisSelectorListener.setHandler(this);
	eventPublisher.addListener(YAxisSelectorEvent.class, yAxisSelectorListener);

}

@Override
public void unregisterEventListeners() {
	super.unregisterEventListeners();	

	if (togglePointTypeListener != null) {
		eventPublisher.removeListener(togglePointTypeListener);
		togglePointTypeListener = null;
	}
	
	if (setPointSizeListener != null) {
		eventPublisher.removeListener(setPointSizeListener);
		setPointSizeListener = null;
	}
	
	if (xAxisSelectorListener != null) {
		eventPublisher.removeListener(xAxisSelectorListener);
		xAxisSelectorListener = null;
	}
	
	if (yAxisSelectorListener != null) {
		eventPublisher.removeListener(yAxisSelectorListener);
		yAxisSelectorListener = null;
	}


}

public void setXAxis(int iAxisIndex)
{
	if (SELECTED_X_AXIS!=iAxisIndex)
	{
		SELECTED_X_AXIS = iAxisIndex;
		setDisplayListDirty();
	}
		
}

public void setYAxis(int iAxisIndex)
{
	if (SELECTED_Y_AXIS!=iAxisIndex)
	{
		SELECTED_Y_AXIS = iAxisIndex;
		setDisplayListDirty();
	}
		
}

public void setPointSize(int pointSize) {
	if (renderStyle.getPointSize() != pointSize) 
	{			
		renderStyle.setPointSize(pointSize);
		setDisplayListDirty();
	}
}
}

