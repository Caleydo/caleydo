package org.caleydo.view.scatterplot;

//import static org.caleydo.view.parcoords.PCRenderStyle.GATE_Z;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.AXIS_Z;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.NR_TEXTURES;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.NR_TEXTURESX;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.NR_TEXTURESY;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.NUMBER_AXIS_MARKERS;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.POINTSIZE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.POINTSTYLE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.XLABELROTATIONNAGLE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.XYAXISDISTANCE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.X_AXIS_COLOR;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.X_AXIS_LINE_WIDTH;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.YLABELROTATIONNAGLE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.Y_AXIS_COLOR;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.Y_AXIS_LINE_WIDTH;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;

import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.selection.delta.ContentVADelta;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.StorageVADelta;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.SetPointSizeEvent;
import org.caleydo.core.manager.event.view.storagebased.SwitchMatrixViewEvent;
import org.caleydo.core.manager.event.view.storagebased.Toggle2AxisEvent;
import org.caleydo.core.manager.event.view.storagebased.ToggleColorModeEvent;
import org.caleydo.core.manager.event.view.storagebased.ToggleMainViewZoomEvent;
import org.caleydo.core.manager.event.view.storagebased.ToggleMatrixZoomEvent;
import org.caleydo.core.manager.event.view.storagebased.TogglePointTypeEvent;
import org.caleydo.core.manager.event.view.storagebased.UseRandomSamplingEvent;
import org.caleydo.core.manager.event.view.storagebased.XAxisSelectorEvent;
import org.caleydo.core.manager.event.view.storagebased.YAxisSelectorEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.manager.usecase.EDataFilterLevel;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.scatterplot.listener.GLScatterPlotKeyListener;
import org.caleydo.view.scatterplot.listener.SetPointSizeListener;
import org.caleydo.view.scatterplot.listener.Toggle2AxisModeListener;
import org.caleydo.view.scatterplot.listener.ToggleColorModeListener;
import org.caleydo.view.scatterplot.listener.ToggleMainViewZoomListener;
import org.caleydo.view.scatterplot.listener.ToggleMatrixViewListener;
import org.caleydo.view.scatterplot.listener.ToggleMatrixZoomListener;
import org.caleydo.view.scatterplot.listener.TogglePointTypeListener;
import org.caleydo.view.scatterplot.listener.UseRandomSamplingListener;
import org.caleydo.view.scatterplot.listener.XAxisSelectorListener;
import org.caleydo.view.scatterplot.listener.YAxisSelectorListener;
import org.caleydo.view.scatterplot.renderstyle.EScatterPointType;
import org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

/**
 * Rendering the GLScatterplott
 * 
 * @author Alexander Lex
 * @author Marc Streit
 * @author Juergen Pillhofer
 */
//@SuppressWarnings("unused")
/**
 * @author Aldi
 *
 */
public class GLScatterPlot extends AStorageBasedView {

	public final static String VIEW_ID = "org.caleydo.view.scatterplot";

	private ScatterPlotRenderStyle renderStyle;

	private ColorMapping colorMapper;

	// private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;
	// private EIDType eStorageDataType = EIDType.EXPERIMENT_INDEX;

	private Vec3f vecTranslation;

	private float fAnimationTranslation = 0;

	private ArrayList<Float> fAlXDistances;

	
	// toggleable feature flags
	
	boolean bUseDetailLevel = true;

	private boolean bUpdateMainView = false;
	private boolean bRender2Axis = false;
	private boolean bRenderMatrix = true;


	private boolean bUpdateSelection = false;
	private boolean bUpdateSelectionTexures =false;
	private boolean bUpdateFullTexures = false;

	private boolean bUseColor = true;
	private boolean bOnlyRenderHalfMatrix = true;
	private boolean bAllowMatrixZoom = false;
	private boolean bMainViewZoomDragged = false;
	private boolean bMainViewZoom = false;
	private boolean bRedrawTextures = false;
	private boolean bUseTextureOpacy = false;
	private boolean bClearSomeDisplayLists = false;

	


	//private int iCurrentMouseOverElement = -1;
	private int iCurrentDragZoom = -1;

	private float fTransformOldMinX = 0.3f;
	private float fTransformNewMinX = 0.3f;
	private float fTransformOldMaxX = 0.4f;
	private float fTransformNewMaxX = 0.4f;

	private float fTransformOldMinY = 0.3f;
	private float fTransformNewMinY = 0.3f;
	private float fTransformOldMaxY = 0.4f;
	private float fTransformNewMaxY = 0.4f;

	
				
	private int iSelectedAxisIndexX = 0;
	private int iSelectedAxisIndexY = 1;
	private int iSelectedAxisIndexX2 = 2;
	private int iSelectedAxisIndexY2 = 3;

	
	private int iMouseOverAxisIndexX = -1;
	private int iMouseOverAxisIndexY = -1;

	private int MAX_AXES=ScatterPlotRenderStyle.NUMBER_OF_INITIAL_AXES;

	// Listeners

	private TogglePointTypeListener togglePointTypeListener;
	private ToggleMatrixViewListener toggleMatrixViewListener;
	private Toggle2AxisModeListener toggle2AxisModeListener;
	private ToggleColorModeListener toggleColorModeListener;
	private ToggleMatrixZoomListener toggleMatrixZoomListener;
	private ToggleMainViewZoomListener toggleMainViewZoomListener;
	private UseRandomSamplingListener useRandomSamplingListener;

	private SetPointSizeListener setPointSizeListener;
	private XAxisSelectorListener xAxisSelectorListener;
	private YAxisSelectorListener yAxisSelectorListener;

	// Brushes

	private float[] fRectangleDragStartPoint = new float[3];
	private float[] fRectangleDragEndPoint = new float[3];
	private boolean bRectangleSelection = false;

	// Displaylists
	private int iGLDisplayListIndexMatrixFull;
	private int iGLDisplayListIndexMatrixSelection;
	private int iGLDisplayListIndexCoord;
	private int iGLDisplayListIndexMouseOver;
	private int iGLDisplayListIndexSelection;

	// Textures
	private int iTextureSize = 1000;
	private int iSamplefaktor=4;

	// array of textures for holding the data samples

	private ArrayList<Texture> AlFullTextures = new ArrayList<Texture>();
	private ArrayList<Texture> AlSelectionTextures = new ArrayList<Texture>();

	//private ArrayList<SelectionType> AlSelectionTypes = new ArrayList<SelectionType>();
	private SelectionType currentSelection = SelectionType.SELECTION;
	private final static String CUSTOM_SELECTION_NAME="SP Sel"; 
	private int iMaxSelections = 10;
	private int iCurrentSelectionNr=0;
	
	private int iDisplayEveryNthPoint = 1;
	
	

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLScatterPlot(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum);
		viewType = GLScatterPlot.VIEW_ID;

		// ArrayList<SelectionType> alSelectionTypes = new
		// ArrayList<SelectionType>();
		// alSelectionTypes.add(SelectionType.NORMAL);
		// alSelectionTypes.add(SelectionType.MOUSE_OVER);
		// alSelectionTypes.add(SelectionType.SELECTION);

		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		fAlXDistances = new ArrayList<Float>();

		glKeyListener = new GLScatterPlotKeyListener(this);
		
		iNumberOfRandomElements = generalManager.getPreferenceStore().getInt(
				PreferenceConstants.PC_NUM_RANDOM_SAMPLING_POINT);

	}

	
	@Override
	public void init(GL gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new ScatterPlotRenderStyle(this, viewFrustum);

		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;
		updateMaxAxis();
		renderStyle.setTextureNr(MAX_AXES, MAX_AXES);
		resetFullTextures();
		resetSelectionTextures();
		initTextures();
		initSelectionTextures();
		selectAxesfromExternal();		
		clearAllSelections();	
	}

	
	/**
	 * Aligns the dimension of the Scatter-Matrix With the Storage-Size  
	 */
	private void updateMaxAxis()
	{
		if (MAX_AXES > storageVA.size())
			MAX_AXES = storageVA.size();
		
	}
	
	@Override
	public void initLocal(GL gl) {

		
		// Register keyboard listener to GL canvas
		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						parentGLCanvas.getParentComposite().addKeyListener(
								glKeyListener);
					}
				});

		
		

//		iGLDisplayListIndexLocal = gl.glGenLists(2);
//		iGLDisplayListToCall = iGLDisplayListIndexLocal;
//		iGLDisplayListIndexCoord = iGLDisplayListIndexLocal + 1;
//		iGLDisplayListIndexMouseOver = iGLDisplayListIndexLocal + 2;
//		iGLDisplayListIndexSelection = iGLDisplayListIndexLocal + 3;
//		iGLDisplayListIndexMatrixFull = iGLDisplayListIndexLocal + 4;
//		iGLDisplayListIndexMatrixSelection = iGLDisplayListIndexLocal + 5;

		
		iGLDisplayListIndexLocal = gl.glGenLists(1);
		//iGLDisplayListToCall = iGLDisplayListIndexLocal;
		iGLDisplayListIndexCoord = gl.glGenLists(1);
		iGLDisplayListIndexMouseOver = gl.glGenLists(1);
		iGLDisplayListIndexSelection = gl.glGenLists(1);
		iGLDisplayListIndexMatrixFull = gl.glGenLists(1);
		iGLDisplayListIndexMatrixSelection = gl.glGenLists(1);
		
		init(gl);
	

		
		
		
		
		
		gl.glNewList(iGLDisplayListIndexLocal, GL.GL_COMPILE);
		gl.glEndList();
		gl.glNewList(iGLDisplayListIndexCoord, GL.GL_COMPILE);
		gl.glEndList();
		gl.glNewList(iGLDisplayListIndexMouseOver, GL.GL_COMPILE);
		gl.glEndList();
		gl.glNewList(iGLDisplayListIndexSelection, GL.GL_COMPILE);
		gl.glEndList();
		gl.glNewList(iGLDisplayListIndexMatrixFull, GL.GL_COMPILE);
		gl.glEndList();
		gl.glNewList(iGLDisplayListIndexMatrixSelection, GL.GL_COMPILE);
		gl.glEndList();
		

		
		
		
		bRenderMatrix = true;
		bOnlyRenderHalfMatrix = true;
		renderStyle.setIsEmbedded(true);

		
		
		
		
		// ScatterPlotRenderStyle.setTextureNr(NR_TEXTURESX,NR_TEXTURESY);

	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
					}
				});

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
	}

	@Override
	public void displayLocal(GL gl) {
		processEvents();
					
		if (!isVisible())
			return;
		if (set == null)
			return;
	
		
		
//		textRenderer.dispose();
//		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, 24), false);
//		textRenderer.setColor(0, 0, 0, 1);
//		renderNumber(gl, "ScatterPlot View 1.0", 0, 0);
		
		if (detailLevel == EDetailLevel.HIGH) {
			GLMouseListener glMouseListener = getParentGLCanvas()
					.getGLMouseListener();
		

			if (bMainViewZoomDragged) {
				
				if (bClearSomeDisplayLists) 
				{
					gl.glNewList(iGLDisplayListIndexCoord, GL.GL_COMPILE);
					gl.glEndList();
					gl.glNewList(iGLDisplayListIndexLocal, GL.GL_COMPILE);
					gl.glEndList();
					gl.glNewList(iGLDisplayListIndexSelection, GL.GL_COMPILE);
					gl.glEndList();
					gl.glNewList(iGLDisplayListIndexMouseOver, GL.GL_COMPILE);
					gl.glEndList();
					bClearSomeDisplayLists =false;
				}
				
				Point pCurrentMousePoint = glMouseListener.getPickedPoint();

				float[] fCurrentMousePoint = GLCoordinateUtils
						.convertWindowCoordinatesToWorldCoordinates(gl,
								pCurrentMousePoint.x, pCurrentMousePoint.y);

				float x = (fCurrentMousePoint[0] - XYAXISDISTANCE);
				float y = (fCurrentMousePoint[1] - XYAXISDISTANCE);
						
				if (bRenderMatrix) 
				{
					x -= renderStyle.getCenterXOffset();
					y -= renderStyle.getCenterYOffset();
				}
				x=x / renderStyle.getAxisWidth();
				y=y / renderStyle.getAxisHeight();
				
				handleMainZoomAxes(x, y);
					
					
			
				//if (glMouseListener.wasLeftMouseButtonPressed()) {
				if (glMouseListener.wasMouseReleased()) {
					bMainViewZoomDragged = false;
					iCurrentDragZoom = -1;
					setDisplayListDirty();
					bUpdateMainView = true;
				}

			}

			if (glMouseListener.wasMouseDragged() && (!bRender2Axis) && !bMainViewZoomDragged) {

				bRectangleSelection = true;

				Point pDragEndPoint = glMouseListener.getPickedPoint();
				Point pDragStartPoint = glMouseListener
						.getPickedPointDragStart();

				fRectangleDragStartPoint = GLCoordinateUtils
						.convertWindowCoordinatesToWorldCoordinates(gl,
								pDragStartPoint.x, pDragStartPoint.y);
				fRectangleDragEndPoint = GLCoordinateUtils
						.convertWindowCoordinatesToWorldCoordinates(gl,
								pDragEndPoint.x, pDragEndPoint.y);

				float[] fArMappingColor = new float[] { 0.0f, 1.0f, 0.0f }; // green

				renderRectangularSelection(
						gl,
						fRectangleDragStartPoint[0],
						fRectangleDragStartPoint[1],
						ScatterPlotRenderStyle.SELECTION_RECTANGLE_Z, // Z-Value
						fRectangleDragEndPoint[0] - fRectangleDragStartPoint[0],
						fRectangleDragEndPoint[1] - fRectangleDragStartPoint[1],
						fArMappingColor);

			}
			if (glMouseListener.wasMouseReleased() && bRectangleSelection) {
				bRectangleSelection = false;
				setDisplayListDirty();
				if (bRenderMatrix)
					gl.glTranslatef(renderStyle.getCenterXOffset(), renderStyle
							.getCenterYOffset(), 0);
				updateSelection();
				if (bRenderMatrix)
					gl.glTranslatef(-renderStyle.getCenterXOffset(),
							-renderStyle.getCenterYOffset(), 0);
				// gl.glDeleteLists(iGLDisplayListIndexBrush, 1);
				bUpdateSelection = true;
				bUpdateSelectionTexures =true;
			}

			pickingManager.handlePicking(this, gl);
		}

		if (bIsDisplayListDirtyLocal) {

			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;

		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF)
			renderBusyMode(gl);

	}

	@Override
	public void displayRemote(GL gl) {

		// if (set == null)
		// return;
		//
		// // if (bIsTranslationAnimationActive) {
		// // bIsDisplayListDirtyRemote = true;
		// // doTranslation();
		// // }
		//
		// if (bIsDisplayListDirtyRemote) {
		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// bIsDisplayListDirtyRemote = false;
		// //
		// generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clearTransformedConnections();
		// }
		// iGLDisplayListToCall = iGLDisplayListIndexRemote;
		//
		// display(gl);
		// checkForHits(gl);

		// glMouseListener.resetEvents();
	}

	@Override
	public void display(GL gl) {

	
		//gl.glEnable(GL.GL_DEPTH_TEST);
		//clipToFrustum(gl);

		
		
		if (bRenderMatrix) {
			
			gl.glCallList(iGLDisplayListIndexMatrixFull);
			gl.glCallList(iGLDisplayListIndexMatrixSelection);

			renderMatrixSelection(gl, iSelectedAxisIndexX, iSelectedAxisIndexY, false);
			if (bRender2Axis)
				renderMatrixSelection(gl, iSelectedAxisIndexX2, iSelectedAxisIndexY2,
						true);
			
		}

		gl.glCallList(iGLDisplayListToCall);
		if (detailLevel == EDetailLevel.HIGH) {			
			gl.glCallList(iGLDisplayListIndexCoord);
			gl.glCallList(iGLDisplayListIndexMouseOver);
		}
		if (!bRender2Axis)
			gl.glCallList(iGLDisplayListIndexSelection);

		
		if (bMainViewZoom)
		{
			if (bRenderMatrix)

				gl.glTranslatef(renderStyle.getCenterXOffset(), renderStyle
						.getCenterYOffset(), 0);
		
			if(bMainViewZoomDragged)
			{
				renderCoordinateSystem(gl);		
				gl.glTranslatef(XYAXISDISTANCE, XYAXISDISTANCE, 0);
				renderScatterPoints(gl);
				renderSelectionPoints(gl);
				renderMouseOver(gl);
				gl.glTranslatef(-XYAXISDISTANCE, -XYAXISDISTANCE, 0);
			}
			renderMainViewZoomSelectionX(gl);
			renderMainViewZoomSelectionY(gl);
			renderMainViewZoomSelectionBoxes(gl);
			
			if (bRenderMatrix)
				gl.glTranslatef(-renderStyle.getCenterXOffset(),
						-renderStyle.getCenterYOffset(), 0);
		
		}

		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// if (!isRenderedRemote())
		// contextMenu.render(gl, this);
	}

	private void buildDisplayListSelection(final GL gl, int iGLDisplayListIndex) {

	
		if (bRenderMatrix) {
			gl.glNewList(iGLDisplayListIndexMatrixSelection, GL.GL_COMPILE);
			renderTextures(gl, true, ScatterPlotRenderStyle.MATRIX_SELECTIONTEXTURES_Z); // Selection textures
			gl.glEndList();

		}
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		gl.glTranslatef(XYAXISDISTANCE, XYAXISDISTANCE, 0);

		if (bRenderMatrix)
			// gl.glTranslatef(renderStyle.getXCenter(),
			// renderStyle.getYCenter(),0);
			gl.glTranslatef(renderStyle.getCenterXOffset(), renderStyle
					.getCenterYOffset(), 0);
		renderSelectionPoints(gl);
		if (bRenderMatrix)
			gl.glTranslatef(-renderStyle.getCenterXOffset(), -renderStyle
					.getCenterYOffset(), 0);
		gl.glTranslatef(-XYAXISDISTANCE, -XYAXISDISTANCE, 0);
		gl.glEndList();
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			// renderStyle.setCenterOffsets();
			bHasFrustumChanged = false;
			
			this.bRedrawTextures = true;
			this.bUpdateMainView = true;
			this.bUpdateSelection =true;
		}

	

		if (bUpdateFullTexures) {
			bUpdateFullTexures = false;
			initTextures();
		}
		
		if (this.bRedrawTextures)
	    {
	      this.bRedrawTextures = false;
	      gl.glNewList(this.iGLDisplayListIndexMatrixFull, GL.GL_COMPILE);
	      renderTextures(gl, false, ScatterPlotRenderStyle.MATRIX_FULLTEXTURES_Z);
	      gl.glEndList();
	      gl.glNewList(this.iGLDisplayListIndexMatrixSelection, GL.GL_COMPILE);
	      renderTextures(gl, true, ScatterPlotRenderStyle.MATRIX_SELECTIONTEXTURES_Z);
	      gl.glEndList();
	    }
		
		if ((bUpdateMainView || bUpdateSelection)) {

			if (bUpdateSelectionTexures)
			{
				initSelectionTextures();
				bUpdateSelectionTexures=false;
			}
			buildDisplayListSelection(gl, iGLDisplayListIndexSelection);
			bUpdateSelection = false;
		}

		if (bUpdateMainView) {

			gl.glNewList(iGLDisplayListIndexMatrixFull, GL.GL_COMPILE);
			renderTextures(gl, false, ScatterPlotRenderStyle.MATRIX_FULLTEXTURES_Z); // All textures
			gl.glEndList();

			
			gl.glNewList(iGLDisplayListIndexCoord, GL.GL_COMPILE);
			if (bRenderMatrix)

				gl.glTranslatef(renderStyle.getCenterXOffset(), renderStyle
						.getCenterYOffset(), 0);
			renderCoordinateSystem(gl);
			if (bRenderMatrix)
				gl.glTranslatef(-renderStyle.getCenterXOffset(),
						-renderStyle.getCenterYOffset(), 0);
			gl.glEndList();
			

			gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
			gl.glTranslatef(XYAXISDISTANCE, XYAXISDISTANCE, 0);
			if (bRenderMatrix)
				gl.glTranslatef(renderStyle.getCenterXOffset(), renderStyle
						.getCenterYOffset(), 0);
			renderScatterPoints(gl);
			if (bRenderMatrix)
				gl.glTranslatef(-renderStyle.getCenterXOffset(), -renderStyle
						.getCenterYOffset(), 0);
			gl.glTranslatef(-XYAXISDISTANCE, -XYAXISDISTANCE, 0);
			gl.glEndList();

			bUpdateMainView = false;
		}

		gl.glNewList(iGLDisplayListIndexMouseOver, GL.GL_COMPILE);
		gl.glTranslatef(XYAXISDISTANCE, XYAXISDISTANCE, 0);
		if (bRenderMatrix)
			// gl.glTranslatef(renderStyle.getXCenter(),
			// renderStyle.getYCenter(),0);
			gl.glTranslatef(renderStyle.getCenterXOffset(), renderStyle
					.getCenterYOffset(), 0);
		renderMouseOver(gl);
		if (bRenderMatrix)
			// gl.glTranslatef(-renderStyle.getXCenter(),
			// -renderStyle.getYCenter(), 0);
			gl.glTranslatef(-renderStyle.getCenterXOffset(), -renderStyle
					.getCenterYOffset(), 0);
		gl.glTranslatef(-XYAXISDISTANCE, -XYAXISDISTANCE, 0);
		gl.glEndList();

	}

	/**
	 * render the Selection Axes and Icons for the MainView Zoom Feature on the X-Axis
	 * 
	 * @param gl
	 */
	private void renderMainViewZoomSelectionX(GL gl) {
		
		gl.glLineWidth(Y_AXIS_LINE_WIDTH);
		//
		// Right Outer X
		//

		float x = renderStyle.transformNorm2GlobalX(fTransformNewMinX);
		float fIconwith = 0.15f;
		float y = XYAXISDISTANCE - fIconwith * 3f;
		float alpha=1f;
		gl.glColor4fv(Y_AXIS_COLOR, 0);
		gl.glBegin(GL.GL_LINES);

		gl.glVertex3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl
				.glVertex3f(x, renderStyle.getRenderHeight() - XYAXISDISTANCE,
						ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glEnd();

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.SCATTER_MAIN_ZOOM, 1));
		x = x - fIconwith;

		Vec3f lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		Vec3f lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		Vec3f upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		Vec3f upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);

		if (fTransformNewMinX > 0)
		{
			if (bMainViewZoomDragged && iCurrentDragZoom == 1)
				alpha =1f;	
			else alpha = 0.6f;
			textureManager.renderTexture(gl, EIconTextures.ARROW_LEFT,
					lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, 1, 1, 1, alpha);
		}
		x = x + fIconwith;
		lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);

		if (fTransformNewMinX < fTransformOldMinX)
		{
			if (bMainViewZoomDragged && iCurrentDragZoom == 1)
				alpha =1f;	
			else alpha = 0.6f;			
			textureManager.renderTexture(gl, EIconTextures.ARROW_RIGHT,
					lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, 1, 1, 1, alpha);
		}
		gl.glPopName();

		//
		// Left Outer X
		//
		x = renderStyle.transformNorm2GlobalX(fTransformNewMaxX);

		gl.glColor4fv(Y_AXIS_COLOR, 0);
		gl.glBegin(GL.GL_LINES);

		gl.glVertex3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl
				.glVertex3f(x, renderStyle.getRenderHeight() - XYAXISDISTANCE,
						ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glEnd();

		lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.SCATTER_MAIN_ZOOM, 2));

		if (fTransformNewMaxX < 1)
		{
			if (bMainViewZoomDragged && iCurrentDragZoom == 2)
				alpha =1f;	
			else alpha = 0.6f;
			textureManager.renderTexture(gl, EIconTextures.ARROW_RIGHT,
					lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, 1, 1, 1, alpha);
		}
		x = x - fIconwith;

		lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);

		if (fTransformNewMaxX > fTransformOldMaxX)
		{
			if (bMainViewZoomDragged && iCurrentDragZoom == 2)
				alpha =1f;	
			else alpha = 0.6f;
			textureManager.renderTexture(gl, EIconTextures.ARROW_LEFT,
						lowerLeftCorner, lowerRightCorner, upperRightCorner,
						upperLeftCorner, 1, 1, 1, alpha);
		}
		gl.glPopName();

		//
		// Right Inner X
		//
		if(fTransformOldMinX==fTransformNewMinX)
		{
			x = renderStyle.transformNorm2GlobalX(fTransformOldMinX);
			//fIconwith = 0.15f;
			y = XYAXISDISTANCE - fIconwith * 2.2f;
			gl.glColor4fv(Y_AXIS_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
	
			gl.glVertex3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			gl
					.glVertex3f(x, renderStyle.getRenderHeight() - XYAXISDISTANCE,
							ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			gl.glEnd();
	
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.SCATTER_MAIN_ZOOM, 3));
			x = x - fIconwith;
	
			lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
	
			if (fTransformOldMinX > 0)
			{
				if (bMainViewZoomDragged && iCurrentDragZoom == 3)
					alpha =1f;	
				else alpha = 0.6f;
				textureManager.renderTexture(gl, EIconTextures.ARROW_LEFT,
							lowerLeftCorner, lowerRightCorner, upperRightCorner,
							upperLeftCorner, 1, 0, 0, alpha);
			}
			x = x + fIconwith;
			lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
	
			if (fTransformOldMinX < fTransformOldMaxX)
			{
				if (bMainViewZoomDragged && iCurrentDragZoom == 3)
					alpha =1f;	
				else alpha = 0.6f;
				textureManager.renderTexture(gl, EIconTextures.ARROW_RIGHT,
							lowerLeftCorner, lowerRightCorner, upperRightCorner,
							upperLeftCorner, 1, 0, 0, alpha);
			}
			gl.glPopName();
		}
		//
		// Left Inner X
		//
		if(fTransformOldMaxX==fTransformNewMaxX)
		{
			x = renderStyle.transformNorm2GlobalX(fTransformOldMaxX);
			y = XYAXISDISTANCE - fIconwith * 2.2f;
	
			gl.glColor4fv(Y_AXIS_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
	
			gl.glVertex3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			gl
					.glVertex3f(x, renderStyle.getRenderHeight() - XYAXISDISTANCE,
							ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			gl.glEnd();
	
			lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
	
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.SCATTER_MAIN_ZOOM, 4));
	
			if (fTransformOldMaxX < 1)
			{
				if (bMainViewZoomDragged && iCurrentDragZoom == 4)
					alpha =1f;	
				else alpha = 0.6f;
				textureManager.renderTexture(gl, EIconTextures.ARROW_RIGHT,
							lowerLeftCorner, lowerRightCorner, upperRightCorner,
							upperLeftCorner, 1, 0, 0, alpha);
			}
			x = x - fIconwith;
	
			lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
	
			if (fTransformOldMaxX > fTransformOldMinX)
			{
				if (bMainViewZoomDragged && iCurrentDragZoom == 4)
					alpha =1f;	
				else alpha = 0.6f;
				textureManager.renderTexture(gl, EIconTextures.ARROW_LEFT,
							lowerLeftCorner, lowerRightCorner, upperRightCorner,
							upperLeftCorner, 1, 0, 0, alpha);
			}
			gl.glPopName();
		}
		
	}

	/**
	 * render the Selection Axes and Icons for the MainView Zoom Feature on the Y-Axis
	 * 
	 * @param gl
	 */
	private void renderMainViewZoomSelectionY(GL gl) {
		gl.glLineWidth(Y_AXIS_LINE_WIDTH);

		//
		// Lower Outer Y
		//
		
		float fIconwith = 0.15f;
		float x = XYAXISDISTANCE - fIconwith * 3f;
		float y = renderStyle.transformNorm2GlobalY(fTransformNewMinY);		
		float alpha=1f;
		
		gl.glColor4fv(Y_AXIS_COLOR, 0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(renderStyle.getRenderWidth() - XYAXISDISTANCE, y,ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glEnd();

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.SCATTER_MAIN_ZOOM, 5));
		y = y - fIconwith;

		Vec3f lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		Vec3f lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		Vec3f upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		Vec3f upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);

		if (fTransformNewMinY > 0)
		{
			if (bMainViewZoomDragged && iCurrentDragZoom == 5)
				alpha =1f;	
			else alpha = 0.6f;
			textureManager.renderTexture(gl, EIconTextures.ARROW_DOWN,
					lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, 1, 1, 1, alpha);
		}
		y = y + fIconwith;
		lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);

		if (fTransformNewMinY < fTransformOldMinY)
		{
			if (bMainViewZoomDragged && iCurrentDragZoom == 5)
				alpha =1f;	
			else alpha = 0.6f;			
			textureManager.renderTexture(gl, EIconTextures.ARROW_UP,
					lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, 1, 1, 1, alpha);
		}
		gl.glPopName();

		//
		// Upper Outer Y
		//
		y = renderStyle.transformNorm2GlobalY(fTransformNewMaxY);

		gl.glColor4fv(Y_AXIS_COLOR, 0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(renderStyle.getRenderWidth() - XYAXISDISTANCE, y,ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glEnd();

		lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.SCATTER_MAIN_ZOOM, 6));

		if (fTransformNewMaxY < 1)
		{
			if (bMainViewZoomDragged && iCurrentDragZoom == 6)
				alpha =1f;	
			else alpha = 0.6f;
			textureManager.renderTexture(gl, EIconTextures.ARROW_UP,
					lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner, 1, 1, 1, alpha);
		}
		y = y - fIconwith;

		lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);

		if (fTransformNewMaxY > fTransformOldMaxY)
		{
			if (bMainViewZoomDragged && iCurrentDragZoom == 6)
				alpha =1f;	
			else alpha = 0.6f;
			textureManager.renderTexture(gl, EIconTextures.ARROW_DOWN,
						lowerLeftCorner, lowerRightCorner, upperRightCorner,
						upperLeftCorner, 1, 1, 1, alpha);
		}
		gl.glPopName();

		//
		// Lower Inner Y
		//
		if(fTransformOldMinY==fTransformNewMinY)
		{
			
			//fIconwith = 0.15f;
			x = XYAXISDISTANCE - fIconwith * 2.2f;
			y = renderStyle.transformNorm2GlobalY(fTransformOldMinY);
					
			gl.glColor4fv(Y_AXIS_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			gl.glVertex3f(renderStyle.getRenderWidth() - XYAXISDISTANCE, y,ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			gl.glEnd();
	
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.SCATTER_MAIN_ZOOM, 7));
			y = y - fIconwith;
	
			lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
	
			if (fTransformOldMinY > 0)
			{
				if (bMainViewZoomDragged && iCurrentDragZoom == 7)
					alpha =1f;	
				else alpha = 0.6f;
				textureManager.renderTexture(gl, EIconTextures.ARROW_DOWN,
							lowerLeftCorner, lowerRightCorner, upperRightCorner,
							upperLeftCorner, 1, 0, 0, alpha);
			}
			y = y + fIconwith;
			lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
	
			if (fTransformOldMinY < fTransformOldMaxY)
			{
				if (bMainViewZoomDragged && iCurrentDragZoom == 7)
					alpha =1f;	
				else alpha = 0.6f;
				textureManager.renderTexture(gl, EIconTextures.ARROW_UP,
							lowerLeftCorner, lowerRightCorner, upperRightCorner,
							upperLeftCorner, 1, 0, 0, alpha);
			}
			gl.glPopName();
		}
		//
		// Upper Inner X
		//
		if(fTransformOldMaxY==fTransformNewMaxY)
		{
			
			
			x = XYAXISDISTANCE - fIconwith * 2.2f;
			y = renderStyle.transformNorm2GlobalY(fTransformOldMaxY);
			
			gl.glColor4fv(Y_AXIS_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			gl.glVertex3f(renderStyle.getRenderWidth() - XYAXISDISTANCE, y,ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			gl.glEnd();
	
	
			lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
	
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.SCATTER_MAIN_ZOOM, 8));
	
			if (fTransformOldMaxY < 1)
			{
				if (bMainViewZoomDragged && iCurrentDragZoom == 8)
					alpha =1f;	
				else alpha = 0.6f;
				textureManager.renderTexture(gl, EIconTextures.ARROW_UP,
							lowerLeftCorner, lowerRightCorner, upperRightCorner,
							upperLeftCorner, 1, 0, 0, alpha);
			}
			y = y - fIconwith;
	
			lowerLeftCorner = new Vec3f(x, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			lowerRightCorner = new Vec3f(x + fIconwith, y, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperRightCorner = new Vec3f(x + fIconwith, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
			upperLeftCorner = new Vec3f(x, y + fIconwith, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
	
			if (fTransformOldMaxY > fTransformOldMinY)
			{
				if (bMainViewZoomDragged && iCurrentDragZoom == 8)
					alpha =1f;	
				else alpha = 0.6f;
				textureManager.renderTexture(gl, EIconTextures.ARROW_DOWN,
							lowerLeftCorner, lowerRightCorner, upperRightCorner,
							upperLeftCorner, 1, 0, 0, alpha);
			}
			gl.glPopName();
		}
		
	}
	
	/**
	 * render the Selection Boxes for the MainView Zoom Feature 
	 * 
	 * @param gl
	 */
	private void renderMainViewZoomSelectionBoxes(GL gl)
	{
		// Show Selection Boxes

		if (!bMainViewZoomDragged)
			return;
		
		float x1 = renderStyle.transformNorm2GlobalX(fTransformNewMinX);
		float x2 = renderStyle.transformNorm2GlobalX(fTransformNewMaxX);
		float y1 = XYAXISDISTANCE;
		float y2 = renderStyle.getRenderHeight() - XYAXISDISTANCE;

		gl.glColor4f(0, 1, 0, 0.1f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(x1, y1, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x2, y1, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x2, y2, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x1, y2, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glEnd();

		x1 = renderStyle.transformNorm2GlobalX(fTransformOldMinX);
		x2 = renderStyle.transformNorm2GlobalX(fTransformOldMaxX);

		gl.glColor4f(1, 0, 0, 0.1f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(x1, y1, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x2, y1, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x2, y2, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x1, y2, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glEnd();

		x1 = XYAXISDISTANCE;
		x2 = renderStyle.getRenderWidth() - XYAXISDISTANCE;
		y1 = renderStyle.transformNorm2GlobalY(fTransformNewMinY);
		y2 = renderStyle.transformNorm2GlobalY(fTransformNewMaxY);
		
		gl.glColor4f(0, 1, 0, 0.1f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(x1, y1, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x2, y1, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x2, y2, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x1, y2, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glEnd();

		y1 = renderStyle.transformNorm2GlobalY(fTransformOldMinY);
		y2 = renderStyle.transformNorm2GlobalY(fTransformOldMaxY);
		
		gl.glColor4f(1, 0, 0, 0.1f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(x1, y1, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x2, y1, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x2, y2, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glVertex3f(x1, y2, ScatterPlotRenderStyle.MAINVIEW_ZOOM_Z);
		gl.glEnd();

		
		
		
	//	gl.glColor4fv(Y_AXIS_COLOR, 1);
		
	}
	
	private void resetFullTextures() {

		AlFullTextures.clear();
		Texture tempTextur = null;

		for (int i = 0; i < NR_TEXTURES; i++) {

			AlFullTextures.add(tempTextur);
		}
	}

	private void resetSelectionTextures() {

		AlSelectionTextures.clear();
		Texture tempTextur = null;

		for (int i = 0; i < NR_TEXTURES; i++) {

			AlSelectionTextures.add(tempTextur);
		}
	}

	/**
	 * Init textures, build array of base textures used for the Scatterplot Matrix
	 * 
	 * @param gl
	 */
	private void initTextures() {

		int ix = 0;
		int iy = 0;
		float xnormalized = 0.0f;
		float ynormalized = 0.0f;


		float[] fArRgbaWhite = { 1.0f, 1.0f, 1.0f, 1f }; // OPACY
		float fBaseOpacity = 0.5f;
		float fAddOpacity = 0.05f;
		
		float[] fBlackColor = { 0.0f, 0.0f, 0.0f }; // Black Color

		 //Collection<Integer> selectionSet = contentVA.getIndexList();
		

		//updateMaxAxis();
		int StartindexX = 0;
		int StartindexY = 0;
		int EndindexX = StartindexX + NR_TEXTURESX - 1;
		int EndindexY = StartindexY + NR_TEXTURESY - 1;

		if (EndindexX >= MAX_AXES) {
			EndindexX = MAX_AXES - 1;
			renderStyle.setTextureNr(EndindexX - StartindexX + 1, NR_TEXTURESY);
		}
		if (EndindexY >= MAX_AXES) {
			EndindexY = MAX_AXES - 1;
			renderStyle.setTextureNr(NR_TEXTURESX, EndindexY - StartindexY + 1);
		}

		AlFullTextures.clear();
		
		
		float fGlobalTexturePointsX = iTextureSize ;
		float fGlobalTexturePointsY = iTextureSize ;
	

		int iTextureWidth = (int) (fGlobalTexturePointsX / (double) NR_TEXTURESX);
		int iTextureHeight = (int) (fGlobalTexturePointsY / (double) NR_TEXTURESY);

		int TextureSize = iTextureWidth * iTextureHeight;
		int itmp = TextureSize * 4 - 4;

		FloatBuffer FbTemp = BufferUtil.newFloatBuffer(TextureSize * 4);

		Texture tempTextur;
		
		Collection<Integer> tmpSet = this.contentVA.getIndexList();
		 
		int iNumberOfActuelelements=iNumberOfRandomElements;
		int iActualPointsSkipped=1;
		
		 iDisplayEveryNthPoint = contentVA.size()
		  / iNumberOfRandomElements;
		  		 		 
		if (iDisplayEveryNthPoint == 0)
		{
			iDisplayEveryNthPoint = 1;
			iNumberOfActuelelements=contentVA.size();
		}
		
		if(iNumberOfActuelelements*iSamplefaktor>TextureSize)
			iActualPointsSkipped=(iNumberOfActuelelements*iSamplefaktor) / TextureSize;
		
		if(iDisplayEveryNthPoint>iActualPointsSkipped) iActualPointsSkipped=iDisplayEveryNthPoint;
		
		 
		 
		 Collection<Integer> selectionSet = new ArrayList<Integer>();
		    for (Integer iContentIndex : tmpSet)
		    {
		      if (iContentIndex.intValue() % iActualPointsSkipped != 0)
		        continue;
		      selectionSet.add(iContentIndex);
		    }


		for (Integer iAxisY = StartindexY; iAxisY <= EndindexY; iAxisY++) {
			for (Integer iAxisX = StartindexX; iAxisX <= EndindexX; iAxisX++) {

				for (Integer i = 0; i < TextureSize; i++) {
					FbTemp.put(fArRgbaWhite);
				}

				if (true) {
					for (Integer iContentIndex : selectionSet) {

						int current_SELECTED_X_AXIS = iAxisX;
						int current_SELECTED_Y_AXIS = iAxisY;


						xnormalized = set.get(
								storageVA.get(current_SELECTED_X_AXIS))
								.getFloat(EDataRepresentation.NORMALIZED,
										iContentIndex);
						ynormalized = set.get(
								storageVA.get(current_SELECTED_Y_AXIS))
								.getFloat(EDataRepresentation.NORMALIZED,
										iContentIndex);


						ix = (int) Math.floor(xnormalized
								* (double) (iTextureWidth - 1));
						iy = ix
								* (iTextureWidth)
								* 4
								+ (int) Math.floor(ynormalized
										* (double) (iTextureHeight - 1)) * 4;

						float[] fArMappingColor = null;

						if (bUseColor)
							fArMappingColor = colorMapper.getColor(Math.max(
									xnormalized, ynormalized));
						else
							fArMappingColor = fBlackColor;
						
						if (iy > itmp ) {							
							throw new IllegalStateException("Bug: Wrong Indices in Scatter-Matrix");
						}
						FbTemp.put(iy, fArMappingColor[0]);
						FbTemp.put(iy + 1, fArMappingColor[1]);
						FbTemp.put(iy + 2, fArMappingColor[2]);

						// Density Plot:
						if(bUseTextureOpacy)
						{
							float fcurrentOpacity = FbTemp.get(iy + 3);
	
							if (fcurrentOpacity < fBaseOpacity)
								fcurrentOpacity = fBaseOpacity;
							else
								fcurrentOpacity += fAddOpacity;
	
							if (fcurrentOpacity >= 1)
								fcurrentOpacity = 1;
	
							FbTemp.put(iy + 3, fcurrentOpacity);
						}
						else 
							FbTemp.put(iy + 3, 1);

					}

					FbTemp.rewind();
				}
				TextureData texData = new TextureData(
						GL.GL_RGBA /* internalFormat */,
						iTextureWidth /* height */, iTextureHeight /* width */,
						0 /* border */, GL.GL_RGBA /* pixelFormat */,
						GL.GL_FLOAT /* pixelType */, false /* mipmap */,
						false /* dataIsCompressed */,
						true /* mustFlipVertically */, FbTemp, null);

				tempTextur = TextureIO.newTexture(0);
				tempTextur.updateImage(texData);

				AlFullTextures.add(tempTextur);
			}
		}
	}

	/**
	 * Init Selection textures, build array of textures used for Scatterplot Matrix
	 * 
	 * @param gl
	 */
	private void initSelectionTextures() {

		int ix = 0;
		int iy = 0;
		float xnormalized = 0.0f;
		float ynormalized = 0.0f;
		float fSelectionFaktor = 1.0f;

		float[] fArRgbaWhite = { 1.0f, 1.0f, 1.0f, 1f }; // OPACY
		float fBaseOpacity = 0.5f;
		float fAddOpacity = 0.05f;

	//	float[] fBlackColor = { 0.0f, 0.0f, 0.0f }; // Black Color
		fSelectionFaktor = 2.0f;
		fArRgbaWhite = new float[] { 1.0f, 1.0f, 1.0f, 0f }; // OPACY

		updateMaxAxis();
		int StartindexX = 0;
		int StartindexY = 0;
		int EndindexX = StartindexX + NR_TEXTURESX - 1;
		int EndindexY = StartindexY + NR_TEXTURESY - 1;

		if (EndindexX >= MAX_AXES) {
			EndindexX = MAX_AXES - 1;
			renderStyle.setTextureNr(EndindexX - StartindexX + 1, NR_TEXTURESY);
		}
		if (EndindexY >= MAX_AXES) {
			EndindexY = MAX_AXES - 1;
			renderStyle.setTextureNr(NR_TEXTURESX, EndindexY - StartindexY + 1);
		}

		AlSelectionTextures.clear();

		float fGlobalTexturePointsX = iTextureSize / fSelectionFaktor;
		float fGlobalTexturePointsY = iTextureSize / fSelectionFaktor;

		int iTextureWidth = (int) (fGlobalTexturePointsX / (double) NR_TEXTURESX);
		int iTextureHeight = (int) (fGlobalTexturePointsY / (double) NR_TEXTURESY);

		int TextureSize = iTextureWidth * iTextureHeight;
		int itmp = TextureSize * 4 - 4;

		FloatBuffer FbTemp = BufferUtil.newFloatBuffer(TextureSize * 4);

		Texture tempTextur;
		
						

		for (Integer iAxisY = StartindexY; iAxisY <= EndindexY; iAxisY++) {
			for (Integer iAxisX = StartindexX; iAxisX <= EndindexX; iAxisX++) {

				for (Integer i = 0; i < TextureSize; i++) {
					FbTemp.put(fArRgbaWhite);
				}

				if (true) {

			//		for (SelectionType tmpSelectionType : AlSelectionTypes){
					ArrayList<SelectionType> sTypes = contentSelectionManager.getSelectionTypes();
					for (SelectionType tmpSelectionType : sTypes) {
											
						if (!tmpSelectionType.isVisible())
							continue;

						if(tmpSelectionType!=SelectionType.SELECTION)
							if (SelectionType.isDefaultType(tmpSelectionType))
								continue;
					
					
					
						Collection<Integer> selectionSet = contentSelectionManager
								.getElements(tmpSelectionType);

						for (Integer iContentIndex : selectionSet) {

							int current_SELECTED_X_AXIS = iAxisX;
							int current_SELECTED_Y_AXIS = iAxisY;
				
							xnormalized = set.get(
									storageVA.get(current_SELECTED_X_AXIS))
									.getFloat(EDataRepresentation.NORMALIZED,
											iContentIndex);
							ynormalized = set.get(
									storageVA.get(current_SELECTED_Y_AXIS))
									.getFloat(EDataRepresentation.NORMALIZED,
											iContentIndex);

							ix = (int) Math.floor(xnormalized
									* (double) (iTextureWidth - 1));
							iy = ix
									* (iTextureWidth)
									* 4
									+ (int) Math.floor(ynormalized
											* (double) (iTextureHeight - 1))
									* 4;

							float[] fArMappingColor = tmpSelectionType
									.getColor();

							
							if (iy > itmp ) {							
								throw new IllegalStateException("Bug: Wrong Indices in Scatter-Matrix");
							}
							FbTemp.put(iy, fArMappingColor[0]);
							FbTemp.put(iy + 1, fArMappingColor[1]);
							FbTemp.put(iy + 2, fArMappingColor[2]);

							// Density Plot:

							float fcurrentOpacity = FbTemp.get(iy + 3);

							if (fcurrentOpacity < fBaseOpacity)
								fcurrentOpacity = fBaseOpacity;
							else
								fcurrentOpacity += fAddOpacity;

							if (fcurrentOpacity >= 1)
								fcurrentOpacity = 1;

							FbTemp.put(iy + 3, fcurrentOpacity);

							// FbTemp.put(iy + 3, fBaseOpacity);

						}
					}
					FbTemp.rewind();
				}
				TextureData texData = new TextureData(
						GL.GL_RGBA /* internalFormat */,
						iTextureWidth /* height */, iTextureHeight /* width */,
						0 /* border */, GL.GL_RGBA /* pixelFormat */,
						GL.GL_FLOAT /* pixelType */, false /* mipmap */,
						false /* dataIsCompressed */,
						true /* mustFlipVertically */, FbTemp, null);

				tempTextur = TextureIO.newTexture(0);
				tempTextur.updateImage(texData);
				AlSelectionTextures.add(tempTextur);

			}
		}
	}

	/**
	 * render the Sletion Rectangles for the Scatter-Matrix
	 * 
	 * @param gl
	 * @param icurrent_X_AXIS
	 * @param icurrent_Y_AXIS
	 * @param bIsSecondAxis
	 */
	private void renderMatrixSelection(GL gl, int icurrent_X_AXIS, int icurrent_Y_AXIS, boolean bIsSecondAxis)
	  {
	    float fHeight = this.viewFrustum.getHeight();
	    float fWidth = this.viewFrustum.getWidth();
	    if (fWidth > fHeight) {
	      fWidth = fHeight;
	    }
	    int iAddTextures = 1;
	
	    int iCurrentAxisSelectionX = iMouseOverAxisIndexX;
	    int iCurrentAxisSelectionY = iMouseOverAxisIndexY;
	
	    if ((this.bAllowMatrixZoom) && 
	      (iCurrentAxisSelectionX >= 0) && (iCurrentAxisSelectionY > 0)) {
	      iAddTextures = 5;
	    }
	    float fStepY = fHeight / (ScatterPlotRenderStyle.NR_TEXTURESY + iAddTextures);
	    float fStepX = fWidth / (ScatterPlotRenderStyle.NR_TEXTURESX + iAddTextures);
	
	    float fSpacerX = fStepX / (ScatterPlotRenderStyle.NR_TEXTURESY + iAddTextures);
	    float fSpacerY = fStepY / (ScatterPlotRenderStyle.NR_TEXTURESX + iAddTextures);
	
	    float fyOffset = fHeight;
	    float fxOffset = fSpacerX;
	
	    int iZoomfactor = 1;
	    float iMOVERZOOMX = 0.0F;
	    float iMOVERZOOMY = 0.0F;
	    if ((iCurrentAxisSelectionX >= 0) && (iCurrentAxisSelectionY >= 0) && 
	      (this.bAllowMatrixZoom))
	    {
	      if (iCurrentAxisSelectionX == icurrent_X_AXIS + 1)
	        iMOVERZOOMX = 0.5F;
	      if (iCurrentAxisSelectionY == icurrent_Y_AXIS + 1) {
	        iMOVERZOOMY = 0.5F;
	      }
	      if (iCurrentAxisSelectionX == icurrent_X_AXIS)
	        iMOVERZOOMX = 2.0F;
	      if (iCurrentAxisSelectionY == icurrent_Y_AXIS) {
	        iMOVERZOOMY = 2.0F;
	      }
	      if (iCurrentAxisSelectionX == icurrent_X_AXIS - 1)
	        iMOVERZOOMX = 3.5F;
	      if (iCurrentAxisSelectionY == icurrent_Y_AXIS - 1) {
	        iMOVERZOOMY = 3.5F;
	      }
	      if (iCurrentAxisSelectionX < icurrent_X_AXIS - 1)
	        iMOVERZOOMX = 4.0F;
	      if (iCurrentAxisSelectionY < icurrent_Y_AXIS - 1) {
	        iMOVERZOOMY = 4.0F;
	      }
	      if ((iCurrentAxisSelectionX == icurrent_X_AXIS) && 
	        (iCurrentAxisSelectionY == icurrent_Y_AXIS + 1)) {
	        iZoomfactor = 2;
	        iMOVERZOOMX = 1.5F;
	        iMOVERZOOMY = 1.0F;
	      }
	      if ((iCurrentAxisSelectionY == icurrent_Y_AXIS) && 
	        (iCurrentAxisSelectionX == icurrent_X_AXIS + 1)) {
	        iZoomfactor = 2;
	        iMOVERZOOMX = 0.0F;
	        iMOVERZOOMY = 2.5F;
	      }
	      if ((iCurrentAxisSelectionX == icurrent_X_AXIS) && 
	        (iCurrentAxisSelectionY == icurrent_Y_AXIS - 1)) {
	        iZoomfactor = 2;
	        iMOVERZOOMX = 1.5F;
	        iMOVERZOOMY = 4.0F;
	      }
	      if ((iCurrentAxisSelectionY == icurrent_Y_AXIS) && 
	        (iCurrentAxisSelectionX == icurrent_X_AXIS - 1)) {
	        iZoomfactor = 2;
	        iMOVERZOOMX = 3.0F;
	        iMOVERZOOMY = 2.5F;
	      }
	
	      if (iCurrentAxisSelectionX == 0) {
	        iMOVERZOOMX -= 1.0F;
	      }
	      if ((iCurrentAxisSelectionX == icurrent_X_AXIS) && 
	        (iCurrentAxisSelectionY == icurrent_Y_AXIS)) {
	        iMOVERZOOMX = 1.0F;
	        iMOVERZOOMY = 3.0F;
	        if (iCurrentAxisSelectionX == 0)
	          iMOVERZOOMX = 0.0F;
	        iZoomfactor = 3;
	      }
	
	    }
	
	    fyOffset = fyOffset - ((fStepY + fSpacerY) * 
	      (icurrent_Y_AXIS + 1 + iMOVERZOOMY));
	
	    fxOffset = fxOffset + (fStepX + fSpacerX) * 
	      (icurrent_X_AXIS + iMOVERZOOMX);
	
	    float fEdge = 0.01F;
	
	    float z = ScatterPlotRenderStyle.MATRIX_SELECTIONRECTANGLE_Z;
	
	    float[] fArMappingColor = SelectionType.SELECTION.getColor();
	
	    if (bIsSecondAxis) {
	      fArMappingColor = new float[] { 0.1F, 0.6F, 0.1F };
	    }
	
	    renderRectangularSelection(gl, fxOffset - fEdge, fyOffset - fEdge, z, 
	      fStepX * iZoomfactor + 2.0F * fEdge, fStepY * iZoomfactor + 2.0F * 
	      fEdge, fArMappingColor);
	    if (((iCurrentAxisSelectionX < 0) && (iCurrentAxisSelectionY < 0)) || (bIsSecondAxis)) {
	      return;
	    }
	    fyOffset = fHeight;
	    fxOffset = fSpacerX;
	    if (this.bAllowMatrixZoom) {
	      iMOVERZOOMX = 1.0F;
	      iMOVERZOOMY = 3.0F;
	      if (iCurrentAxisSelectionX == 0)
	        iMOVERZOOMX = 0.0F;
	      iZoomfactor = 3;
	    }
	
	    fyOffset = fyOffset - ((fStepY + fSpacerY) * 
	      (iCurrentAxisSelectionY + 1 + iMOVERZOOMY));
	
	    fxOffset = fxOffset + (fStepX + fSpacerX) * 
	      (iCurrentAxisSelectionX + iMOVERZOOMX);
	    fArMappingColor = SelectionType.MOUSE_OVER.getColor();
	
	    renderRectangularSelection(gl, fxOffset - fEdge, fyOffset - fEdge, z, 
	      fStepX * iZoomfactor + 2.0F * fEdge, fStepY * iZoomfactor + 2.0F * 
	      fEdge, fArMappingColor);
  }

  /**
   * render the Textures build in initTextures; 
   * if  bIsSelection = true, render the SelectionTextures, else the base textures
   * 
 * @param gl
 * @param bIsSelection
 * @param z
 */
private void renderTextures(GL gl, boolean bIsSelection, float z)
  {
	    float fHeight = this.viewFrustum.getHeight();
	    float fWidth = this.viewFrustum.getWidth();
	    if (fWidth > fHeight) {
	      fWidth = fHeight;
	    }
	  
	
	    int iAddTextures = 1;
	    int iCurrentAxisSelectionX = iMouseOverAxisIndexX;
	    int iCurrentAxisSelectionY = iMouseOverAxisIndexY;
	
	    if ((this.bAllowMatrixZoom) && 
	      (iCurrentAxisSelectionX >= 0) && (iCurrentAxisSelectionY >= 0)) {
	      iAddTextures = 5;
	    }
	    float fMaxX = 0.0F;
	    float fMaxY = 0.0F;
	
	    float fStepY = fHeight / (ScatterPlotRenderStyle.NR_TEXTURESY + iAddTextures);
	    float fStepX = fWidth / (ScatterPlotRenderStyle.NR_TEXTURESX + iAddTextures);
	
	    float fSpacerX = fStepX / (ScatterPlotRenderStyle.NR_TEXTURESY + iAddTextures);
	    float fSpacerY = fStepY / (ScatterPlotRenderStyle.NR_TEXTURESX + iAddTextures);
	
	    float fyOffset = fHeight;
	    float fxOffset = fSpacerX;
	
	    int icounter = 0;
	
	    int iTextureMultiX = 1;
	    int iTextureMultiY = 1;
	    int iOffsetMultiX = 1;
	    int iOffsetMultiY = 1;
	    float fExtraOffsetX = 0.0F;
	    float fExtraOffsetY = 0.0F;
	
	    for (int i = 0; i < ScatterPlotRenderStyle.NR_TEXTURESX; ++i) {
	      for (int j = 0; j < ScatterPlotRenderStyle.NR_TEXTURESY; ++j)
	      {
	        if (this.bAllowMatrixZoom) {
	          iTextureMultiX = 1;
	          iOffsetMultiX = 1;
	          iTextureMultiY = 1;
	          iOffsetMultiY = 1;
	          fExtraOffsetX = 0.0F;
	          fExtraOffsetY = 0.0F;
	
	          if ((i == iCurrentAxisSelectionX - 1) || 
	            (i == iCurrentAxisSelectionX + 1)) {
	            iOffsetMultiX = iAddTextures - 3;
	            fExtraOffsetX = (fStepX + fSpacerX) / 2.0F;
	          }
	
	          if ((j == iCurrentAxisSelectionY - 1) || 
	            (j == iCurrentAxisSelectionY + 1)) {
	            iOffsetMultiY = iAddTextures - 3;
	            fExtraOffsetY = (fStepY + fSpacerY) / 2.0F;
	          }
	
	          if (i == iCurrentAxisSelectionX) {
	            fExtraOffsetX = fStepX + fSpacerX;
	            iOffsetMultiX = iAddTextures - 2;
	            if ((j == iCurrentAxisSelectionY - 1) || 
	              (j == iCurrentAxisSelectionY + 1)) {
	              iTextureMultiX = iAddTextures - 3;
	              iTextureMultiY = iAddTextures - 3;
	              fExtraOffsetX = (fStepX + fSpacerX) / 2.0F;
	              fExtraOffsetY = 0.0F;
	            } else {
	              iTextureMultiX = 1;
	            }
	          }
	          if (j == iCurrentAxisSelectionY) {
	            fExtraOffsetY = fStepY + fSpacerY;
	            iOffsetMultiY = iAddTextures - 2;
	            if ((i == iCurrentAxisSelectionX - 1) || 
	              (i == iCurrentAxisSelectionX + 1)) {
	              iTextureMultiY = iAddTextures - 3;
	              iTextureMultiX = iAddTextures - 3;
	              fExtraOffsetY = (fStepY + fSpacerY) / 2.0F;
	              fExtraOffsetX = 0.0F;
	            } else {
	              iTextureMultiY = 1;
	            }
	          }
	          if ((i == iCurrentAxisSelectionX) && (j == iCurrentAxisSelectionY)) {
	            iTextureMultiX = iAddTextures - 2;
	            iTextureMultiY = iAddTextures - 2;
	            fExtraOffsetX = 0.0F;
	            fExtraOffsetY = 0.0F;
	          }
	
	          if ((i == iCurrentAxisSelectionX - 1) || 
	            (i == iCurrentAxisSelectionX + 1)) {
	            iOffsetMultiX = iAddTextures - 3;
	          }
	          if ((j == iCurrentAxisSelectionY - 1) || 
	            (j == iCurrentAxisSelectionY + 1)) {
	            iOffsetMultiY = iAddTextures - 3;
	          }
	        }
	        fyOffset -= (fStepY + fSpacerY) * iOffsetMultiY;
	
	        if ((i > j) && (this.bOnlyRenderHalfMatrix)) {
	          ++icounter;
	        }
	        else
	        {
	          if (i != j) {
	            gl.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	            if (bIsSelection) {
	              ((Texture)this.AlSelectionTextures.get(icounter)).enable();
	              ((Texture)this.AlSelectionTextures.get(icounter)).bind();
	            } else {
	              ((Texture)this.AlFullTextures.get(icounter)).enable();
	              ((Texture)this.AlFullTextures.get(icounter)).bind();
	            }
	            gl.glTexParameteri(3553, 10242, 
	              10496);
	            gl.glTexParameteri(3553, 10243, 
	              10496);
	            gl.glTexParameteri(3553, 
	              10240, 9728);
	            gl.glTexParameteri(3553, 
	              10241, 9728);
	            TextureCoords texCoords = null;
	
	            if (bIsSelection)
	              texCoords = ((Texture)this.AlSelectionTextures.get(icounter))
	                .getImageTexCoords();
	            else {
	              texCoords = ((Texture)this.AlFullTextures.get(icounter))
	                .getImageTexCoords();
	            }
	
	            gl.glPushName(this.pickingManager.getPickingID(this.iUniqueID, 
	              EPickingType.SCATTER_MATRIX_SELECTION, icounter));
	
	            gl.glBegin(7);
	            gl.glTexCoord2d(texCoords.left(), texCoords.top());
	            gl.glVertex3f(fxOffset + fExtraOffsetX, fyOffset + 
	              fExtraOffsetY, z);
	            gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
	            gl.glVertex3f(fxOffset + fExtraOffsetX, fyOffset + fStepY * 
	              iTextureMultiY + fExtraOffsetY, z);
	            gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
	            gl.glVertex3f(fxOffset + fStepX * iTextureMultiX + 
	              fExtraOffsetX, fyOffset + fStepY * iTextureMultiY + 
	              fExtraOffsetY, z);
	            gl.glTexCoord2d(texCoords.right(), texCoords.top());
	            gl.glVertex3f(fxOffset + fStepX * iTextureMultiX + 
	              fExtraOffsetX, fyOffset + fExtraOffsetY, z);
	            gl.glEnd();
	            gl.glPopName();
	
	            if (!(bIsSelection)) {
	              gl.glLineWidth(0.05F);
	              gl.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
	              gl.glBegin(2);
	
	              gl.glVertex3f(fxOffset + fExtraOffsetX, fyOffset + 
	                fExtraOffsetY, z + 1.0F);
	              gl.glVertex3f(fxOffset + fExtraOffsetX, fyOffset + 
	                fStepY * iTextureMultiY + fExtraOffsetY, 
	                z + 1.0F);
	              gl.glVertex3f(fxOffset + fStepX * iTextureMultiX + 
	                fExtraOffsetX, fyOffset + fStepY * 
	                iTextureMultiY + fExtraOffsetY, z + 1.0F);
	              gl.glVertex3f(fxOffset + fStepX * iTextureMultiX + 
	                fExtraOffsetX, fyOffset + fExtraOffsetY, 
	                z + 1.0F);
	              gl.glEnd();
	            }
	
	            if (bIsSelection)
	              ((Texture)this.AlSelectionTextures.get(icounter)).disable();
	            else
	              ((Texture)this.AlFullTextures.get(icounter)).disable();
	          }
	          else if (!(bIsSelection)) {
	            renderHistogram(gl, fxOffset + fExtraOffsetX, fyOffset + 
	              fExtraOffsetY, fStepX, fStepY, i);
	
	            float tmpx = this.viewFrustum.getWidth() - (
	              fxOffset + fExtraOffsetX + fStepX);
	            float tmpy = this.viewFrustum.getHeight() - (
	              fyOffset + fExtraOffsetY + fStepY);
	
	            if ((ScatterPlotHelper.getSpace(tmpx, tmpy) > ScatterPlotHelper.getSpace(fMaxX, fMaxY)) && 
	              (ScatterPlotHelper.getCorrelation(tmpx, tmpy))) {
	              fMaxX = tmpx;
	              fMaxY = tmpy;
	            }
	
	          }
	
	          ++icounter;
	        }
	      }
	      fyOffset = fHeight;
	
	      fxOffset += (fStepX + fSpacerX) * iOffsetMultiX;
    }
    if (!(bIsSelection))
      this.renderStyle.setCenterOffsets(this.viewFrustum.getWidth() - fMaxX, 
        this.viewFrustum.getHeight() - fMaxY);
  }
	

  	
  
	/**
	 * Render the Histogram and the labels for the given Axis
	 * 
	 * @param gl
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param selected_Axis
	 */
	private void renderHistogram(GL gl, float x, float y, float width,
			float height, int selected_Axis) {

		float[] fArMappingColor = new float[] { 0.0f, 0.0f, 0.0f }; // black

		renderRectangularSelection(gl, x, y, ScatterPlotRenderStyle.MATRIX_HISTOGRAMM_Z, // Z-Value
				width, height, fArMappingColor);

		// TODO InsertHistogramm here

		String sLabel = set.get(storageVA.get(selected_Axis)).getLabel();

		float fScaling = renderStyle.getSmallFontScalingFactor() * 0.7f;
		if (isRenderedRemote())
			fScaling *= 1.5f;

		textRenderer.dispose();
		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, 24), false);
		Rectangle2D bounds = textRenderer.getScaledBounds(gl, sLabel, fScaling,
				ScatterPlotRenderStyle.MIN_NUMBER_TEXT_SIZE);

		float fRotation = 45;
		//gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		float tmpx = x + width + width /4;
		float tmpy = y + height / 6;
		gl.glTranslatef(tmpx, tmpy, 0);
		gl.glRotatef(fRotation, 0, 0, 1);
		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(gl, sLabel, 0,
				0,// + (1 * height / 3),
				ScatterPlotRenderStyle.MATRIX_HISTOGRAMM_Z, fScaling,
				ScatterPlotRenderStyle.MIN_AXIS_LABEL_TEXT_SIZE);
		textRenderer.end3DRendering();
		gl.glRotatef(-fRotation, 0, 0, 1);
		gl.glTranslatef(-tmpx, -tmpy, 0);
		//gl.glPopAttrib();
		
		
		gl.glLineWidth(0.1f);
		
		tmpx = x + width + width /6f;
		tmpy = y + (height /6);// +(float)bounds.getHeight()/2f;
		
		if (selected_Axis>0) 
		{
			gl.glBegin(GL.GL_LINES);			
			gl.glVertex3f(x+width, tmpy, ScatterPlotRenderStyle.MATRIX_HISTOGRAMM_Z);
			gl.glVertex3f(tmpx, tmpy, ScatterPlotRenderStyle.MATRIX_HISTOGRAMM_Z);
			gl.glEnd();
		}
		
		if (selected_Axis<MAX_AXES-1) 
		{
			gl.glBegin(GL.GL_LINES);			
			gl.glVertex3f(x+width, y-(height /10f), ScatterPlotRenderStyle.MATRIX_HISTOGRAMM_Z);
			gl.glVertex3f(tmpx, tmpy, ScatterPlotRenderStyle.MATRIX_HISTOGRAMM_Z);
			gl.glEnd();
		}
		
	
	}

	
	/**
	 * Render the coordinate system of the Scatterplot
	 * 
	 * 
	 * @param gl
	 *            the gl context
	 * 
	 */
	private void renderCoordinateSystem(GL gl) {
		
		textRenderer.dispose();
		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, 24), false);
		textRenderer.setColor(0, 0, 0, 1);
		// Markers On Axis
		
		float fXPosition = XYAXISDISTANCE;
		float fYPosition = XYAXISDISTANCE;
		float fMarkerSpacingY = renderStyle.getAxisHeight()
				/ (NUMBER_AXIS_MARKERS + 1);
		float fMarkerSpacingX = renderStyle.getAxisWidth()
				/ (NUMBER_AXIS_MARKERS + 1);
		for (int iInnerCount = 1; iInnerCount <= NUMBER_AXIS_MARKERS + 1; iInnerCount++) {
			float fCurrentHeight = fMarkerSpacingY * iInnerCount;
			float fCurrentWidth = fMarkerSpacingX * iInnerCount;

			if (set.isSetHomogeneous()) {
				float fNumber = (float) set.getRawForNormalized(fCurrentHeight
						/ renderStyle.getAxisHeight());
				// float max = (float) set.getMax();
				// float min = (float) set.getMin();

				//textRenderer.begin3DRendering();
				Rectangle2D bounds = textRenderer.getScaledBounds(gl, Formatter
						.formatNumber(fNumber), renderStyle
						.getSmallFontScalingFactor(),
						ScatterPlotRenderStyle.MIN_NUMBER_TEXT_SIZE);
				float fWidth = (float) bounds.getWidth();
				float fHeight = (float) bounds.getHeight();
				float fHeightHalf = fHeight / 2.0f;
				float fWidthHalf = fWidth / 2.0f;
				//textRenderer.endRendering();

				float y = fCurrentHeight + XYAXISDISTANCE;
				y = transformOnYZoom(y, renderStyle.getAxisHeight(),
						XYAXISDISTANCE)
						- fHeightHalf;

				renderNumber(gl, Formatter.formatNumber(fNumber), fXPosition
						- fWidth - AXIS_MARKER_WIDTH, y);

				
//				renderNumber(gl, Formatter.formatNumber(fNumber), fXPosition
//						- fWidth - AXIS_MARKER_WIDTH, fCurrentHeight
//						- fHeightHalf + XYAXISDISTANCE);

				gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
				float fRoationAngle = -45;
				// float x = fCurrentWidth- fWidthHalf + XYAXISDISTANCE;
				// x=transformOnZoom(x,renderStyle.getAxisWidth(),XYAXISDISTANCE);

				float x = fCurrentWidth + XYAXISDISTANCE;
				x = transformOnXZoom(x, renderStyle.getAxisWidth(),
						XYAXISDISTANCE)
						- fWidthHalf;

				y = fYPosition + AXIS_MARKER_WIDTH - fHeight;
				gl.glTranslatef(x, y, 0);
				gl.glRotatef(fRoationAngle, 0, 0, 1);
				renderNumber(gl, Formatter.formatNumber(fNumber), 0, 0);
				gl.glRotatef(-fRoationAngle, 0, 0, 1);
				gl.glTranslatef(-x, -y, 0);
				gl.glPopAttrib();

				// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
				// float fRoationAngle= -45;
				// gl.glRotatef(fRoationAngle, 0, 0, 1);
				// renderNumber(gl, Formatter.formatNumber(fNumber),
				// fCurrentWidth
				// - fWidthHalf + XYAXISDISTANCE, fYPosition
				// - AXIS_MARKER_WIDTH - fHeight);
				// gl.glRotatef(-fRoationAngle, 0, 0, 1);
				// gl.glPopAttrib();
			}

			gl.glColor4fv(X_AXIS_COLOR, 0);
			float tmpx = fCurrentWidth + XYAXISDISTANCE;
			tmpx = transformOnXZoom(tmpx, renderStyle.getAxisWidth(),
					XYAXISDISTANCE);
			gl.glBegin(GL.GL_LINES);			
			gl.glVertex3f(tmpx, fYPosition - AXIS_MARKER_WIDTH, AXIS_Z);
			gl.glVertex3f(tmpx, fYPosition + AXIS_MARKER_WIDTH, AXIS_Z);
			gl.glEnd();

			gl.glColor4fv(Y_AXIS_COLOR, 0);
			float tmpy = fCurrentHeight + XYAXISDISTANCE;
			tmpy = transformOnYZoom(tmpy, renderStyle.getAxisHeight(),
					XYAXISDISTANCE);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fXPosition - AXIS_MARKER_WIDTH, tmpy, AXIS_Z);
			gl.glVertex3f(fXPosition + AXIS_MARKER_WIDTH, tmpy, AXIS_Z);
			gl.glEnd();
		}

		// draw X-Axis
		gl.glColor4fv(X_AXIS_COLOR, 0);
		gl.glLineWidth(X_AXIS_LINE_WIDTH);

		// gl.glPushName(pickingManager.getPickingID(iUniqueID,
		// EPickingType.X_AXIS_SELECTION, 1));
		gl.glBegin(GL.GL_LINES);

		gl.glVertex3f(XYAXISDISTANCE, XYAXISDISTANCE, 0.0f);
		gl.glVertex3f((renderStyle.getRenderWidth() - XYAXISDISTANCE),
				XYAXISDISTANCE, 0.0f);
		// gl.glVertex3f(5.0f, XYAXISDISTANCE, 0.0f);

		gl.glEnd();
		// gl.glPopName();

		// draw all Y-Axis

		gl.glColor4fv(Y_AXIS_COLOR, 0);
		gl.glLineWidth(Y_AXIS_LINE_WIDTH);

		// gl.glPushName(pickingManager.getPickingID(iUniqueID,
		// EPickingType.X_AXIS_SELECTION, 1));
		gl.glBegin(GL.GL_LINES);

		// float fXAxisOverlap = 0.1f;

		gl.glVertex3f(XYAXISDISTANCE, XYAXISDISTANCE, AXIS_Z);
		gl.glVertex3f(XYAXISDISTANCE, renderStyle.getRenderHeight()
				- XYAXISDISTANCE, AXIS_Z);

		gl.glEnd();
		// gl.glPopName();

		// // LABEL X

		// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		// if(bRenderMatrix)
		// gl.glTranslatef(renderStyle.getLAbelWidth(), -5*XLABELDISTANCE, 0);
		// else
		gl.glTranslatef(renderStyle.getLAbelWidth(bRender2Axis), renderStyle
				.getAxisHeight()
				+ 1.3f * XYAXISDISTANCE, 0);

		gl.glRotatef(XLABELROTATIONNAGLE, 0, 0, 1);
		textRenderer.begin3DRendering();
		float fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;

		String sAxisLabel = "X-Axis: "
				+ set.get(storageVA.get(iSelectedAxisIndexX)).getLabel();
		if (bRender2Axis)
			sAxisLabel += " / "
					+ set.get(storageVA.get(iSelectedAxisIndexX2)).getLabel();

		textRenderer.draw3D(gl, sAxisLabel, 0, 0, 0, fScaling,
				ScatterPlotRenderStyle.MIN_AXIS_LABEL_TEXT_SIZE);
		textRenderer.end3DRendering();
		gl.glRotatef(-XLABELROTATIONNAGLE, 0, 0, 1);
		// if(bRenderMatrix)
		// gl.glTranslatef(-renderStyle.getLAbelWidth(), +5*XLABELDISTANCE, 0);
		// else
		gl.glTranslatef(-renderStyle.getLAbelWidth(bRender2Axis), -renderStyle
				.getAxisHeight()
				- 1.3f * XYAXISDISTANCE, 0);
		// gl.glPopAttrib();

		// LABEL Y

		// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		// if(bRenderMatrix)
		// gl.glTranslatef(-YLABELDISTANCE, renderStyle.getLabelHeight(), 0);
		// else
		gl.glTranslatef(renderStyle.getAxisWidth() + 1.7f * XYAXISDISTANCE,
				renderStyle.getLabelHeight(bRender2Axis), 0);
		gl.glRotatef(YLABELROTATIONNAGLE, 0, 0, 1);

		textRenderer.begin3DRendering();
		fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;

		sAxisLabel = "Y-Axis: "
				+ set.get(storageVA.get(iSelectedAxisIndexY)).getLabel();
		if (bRender2Axis)
			sAxisLabel += " / "
					+ set.get(storageVA.get(iSelectedAxisIndexY2)).getLabel();

		// sAxisLabel
		// ="Y-Achse: "+set.get(2).getLabel()+" (O) / "+set.get(3).getLabel()+" (X)";
		textRenderer.draw3D(gl, sAxisLabel, 0, 0, 0, fScaling,
				ScatterPlotRenderStyle.MIN_AXIS_LABEL_TEXT_SIZE);
		textRenderer.end3DRendering();

		// gl.glRotatef(-YLABELROTATIONNAGLE, 0, 0, 1);

		gl.glRotatef(-YLABELROTATIONNAGLE, 0, 0, 1);
		// if(bRenderMatrix)
		// gl.glTranslatef(YLABELDISTANCE, -renderStyle.getLabelHeight(), 0);
		// else
		gl.glTranslatef(-renderStyle.getAxisWidth() - 1.7f * XYAXISDISTANCE,
				-renderStyle.getLabelHeight(bRender2Axis), 0);
		// gl.glPopAttrib();

	}

	private void renderNumber(GL gl, String sRawValue, float fXOrigin,
			float fYOrigin) {

		
		textRenderer.begin3DRendering();

		float fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;
		textRenderer.draw3D(gl, sRawValue, fXOrigin, fYOrigin,
				ScatterPlotRenderStyle.TEXT_ON_LABEL_Z, fScaling,
				ScatterPlotRenderStyle.MIN_NUMBER_TEXT_SIZE);
		textRenderer.end3DRendering();

	}
		
	/**
	 * render the MainView
	 * 
	 * @param gl
	 */
	private void renderScatterPoints(GL gl) {
		float XScale = renderStyle.getRenderWidth() - XYAXISDISTANCE * 2.0f;
		float YScale = renderStyle.getRenderHeight() - XYAXISDISTANCE * 2.0f;
		float x = 0.0f;
		float y = 0.0f;
		float xnormalized = 0.0f;
		float ynormalized = 0.0f;

		float x_2 = 0.0f;
		float y_2 = 0.0f;
		EScatterPointType tmpPointStyle = POINTSTYLE;
		float[] fArMappingColor = { 0.0f, 0.0f, 0.0f }; // (black);
		
		 iDisplayEveryNthPoint = contentVA.size()
		  / iNumberOfRandomElements;
		  
		if (iDisplayEveryNthPoint == 0) 
			iDisplayEveryNthPoint = 1;

		if (detailLevel != EDetailLevel.HIGH) {
			bRender2Axis = false;
			POINTSTYLE = EScatterPointType.POINT;
		}

		// contentVA = useCase.getVA(EVAType.CONTENT);
		//Collection<Integer> selectionSet = contentVA.getIndexList();
		 Collection<Integer> tmpSet = this.contentVA.getIndexList();
		 Collection<Integer> selectionSet = new ArrayList<Integer>();
		    for (Integer iContentIndex : tmpSet)
		    {		      
		    	if (bUseRandomSampling)		    	  
		    		  if (iContentIndex % iDisplayEveryNthPoint != 0) 
							continue;		        
		      selectionSet.add(iContentIndex);
		    }

		if (bRender2Axis)
			if (contentSelectionManager
					.getNumberOfElements(currentSelection) > 0)
				selectionSet = contentSelectionManager
						.getElements(currentSelection);

		for (Integer iContentIndex : selectionSet) {

			if (iContentIndex == -1) {
				throw new IllegalStateException("No such element in virtual array");								
			}


			xnormalized = set.get(storageVA.get(iSelectedAxisIndexX)).getFloat(
					EDataRepresentation.NORMALIZED, iContentIndex);
			ynormalized = set.get(storageVA.get(iSelectedAxisIndexY)).getFloat(
					EDataRepresentation.NORMALIZED, iContentIndex);


			x = transformOnXZoom(xnormalized) * XScale;
			y = transformOnYZoom(ynormalized) * YScale;
			if (bUseColor)
				fArMappingColor = colorMapper.getColor(Math.max(xnormalized,
						ynormalized));
			EScatterPointType tmpPoint = POINTSTYLE;
			if (bRender2Axis) {
				fArMappingColor = new float[] { 1.0f, 0.0f, 0.0f };
				POINTSTYLE = EScatterPointType.POINT;
			}

			renderPointPrimitive(gl, x, y, ScatterPlotRenderStyle.SCATTERPOINT_Z, // z
					fArMappingColor, 1.0f,// fOpacity
					iContentIndex, 1.0f); // scale

			if (bRender2Axis) {			
				xnormalized = set
						.get(storageVA.get(iSelectedAxisIndexX2))
						.getFloat(EDataRepresentation.NORMALIZED, iContentIndex);
				ynormalized = set
						.get(storageVA.get(iSelectedAxisIndexY2))
						.getFloat(EDataRepresentation.NORMALIZED, iContentIndex);

				x_2 = transformOnXZoom(xnormalized) * XScale;
				y_2 = transformOnYZoom(ynormalized) * YScale;
				fArMappingColor = new float[] { 0.0f, 1.0f, 0.0f };

				renderPointPrimitive(gl, x_2, y_2, ScatterPlotRenderStyle.SCATTERPOINT_Z, // z
						fArMappingColor, 1.0f,// fOpacity
						iContentIndex, 1.0f); // scale

				POINTSTYLE = tmpPoint;
				
				int iPickingID = pickingManager.getPickingID(iUniqueID,
						EPickingType.SCATTER_POINT_SELECTION, iContentIndex);
				
				gl.glPushName(iPickingID);
				gl.glColor4f(0.0f, 0.0f, 1.0f,0.3f);
				gl.glLineWidth(0.5f);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(x, y, ScatterPlotRenderStyle.TWOAXISLINE_Z);
				gl.glVertex3f(x_2, y_2, ScatterPlotRenderStyle.TWOAXISLINE_Z);
				gl.glEnd();
				gl.glPopName();

			}
		}
		POINTSTYLE = tmpPointStyle;
	}

	/**
	 * render the Mouseover in the Mainview
	 * 
	 * @param gl
	 */
	private void renderMouseOver(GL gl) {

		if (contentSelectionManager
				.getNumberOfElements(SelectionType.MOUSE_OVER) == 0)
			return;

		Set<Integer> mouseOver = contentSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		int iContentIndex = 0;
		for (int i : mouseOver) {
			iContentIndex = i;
			break;
		}

		float XScale = renderStyle.getRenderWidth() - XYAXISDISTANCE * 2.0f;
		float YScale = renderStyle.getRenderHeight() - XYAXISDISTANCE * 2.0f;



		float xnormalized = set.get(storageVA.get(iSelectedAxisIndexX)).getFloat(
				EDataRepresentation.NORMALIZED, iContentIndex);
		float ynormalized = set.get(storageVA.get(iSelectedAxisIndexY)).getFloat(
				EDataRepresentation.NORMALIZED, iContentIndex);

		float x = transformOnXZoom(xnormalized) * XScale;
		float y = transformOnYZoom(ynormalized) * YScale;

		float[] fArMappingColor = colorMapper.getColor(Math.max(xnormalized,
				ynormalized));
		if (contentSelectionManager.checkStatus(SelectionType.SELECTION,
				iContentIndex))
			fArMappingColor = SelectionType.MOUSE_OVER.getColor();

		float z = ScatterPlotRenderStyle.LABEL_Z;
		float fullPoint = POINTSIZE * 1.5f;
		
		float[] fMoueseOverColor = SelectionType.MOUSE_OVER.getColor();
		
		gl.glColor3f(fMoueseOverColor[0], fMoueseOverColor[1],
				fMoueseOverColor[2]);

		

		
		float angle;
		float PI = (float) Math.PI;

		gl.glBegin(GL.GL_POLYGON);
		for (int i = 0; i < 20; i++) {
			angle = (i * 2 * PI) / 10;
			gl.glVertex3f(x + (float) (Math.cos(angle) * fullPoint), y
					+ (float) (Math.sin(angle) * fullPoint), z);
		}
		gl.glEnd();
		
		float fscale=1.4f;
		if(POINTSTYLE==EScatterPointType.CIRCLE || POINTSTYLE==EScatterPointType.DISK)
			fscale=0.7f;
		renderPointPrimitive(gl, x, y, ScatterPlotRenderStyle.HIGHLIGHTED_SCATTERPOINT_Z, // z
				fArMappingColor, 1.0f, iContentIndex, fscale); // fOpacity
		
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glPointSize(POINTSIZE * 50.0f);
		gl.glBegin(GL.GL_POINTS);
		gl.glVertex3f(x, y, ScatterPlotRenderStyle.HIGHLIGHTED_SCATTERPOINT_Z);
		gl.glEnd();
		
		renderMouseOverLabel(gl, x, y, // z
				fMoueseOverColor, 1.0f, iContentIndex); // fOpacity

	}

	/**
	 * render the Mouseover_Label in the Mainview, called by renderMouseOver(GL gl)
	 * 
	 * @param gl
	 * @param x
	 * @param y
	 * @param fArMappingColor
	 * @param fOpacity
	 * @param iContentIndex
	 */
	private void renderMouseOverLabel(GL gl, float x, float y,
			float[] fMoueseOverColor, float fOpacity, int iContentIndex) {

		textRenderer.dispose();
		textRenderer = new CaleydoTextRenderer(new Font("Arial", Font.PLAIN, 24), false);
		textRenderer.setColor(0, 0, 0, 1);


		float fXtranslation=0.2f;
		float fYtranslation=0.1f;
		x = x + fXtranslation;
		y = y + fYtranslation;
		gl.glTranslatef(x, y, 0);

		String sLabel = null;
		String genLabel = null;
		if (useCase.getDataDomain() == EDataDomain.GENETIC_DATA) 
		{
			genLabel = idMappingManager.getID(EIDType.EXPRESSION_INDEX,
					EIDType.GENE_SYMBOL, iContentIndex);

			
		} else if (useCase.getDataDomain() == EDataDomain.UNSPECIFIED) {
			genLabel = idMappingManager.getID(EIDType.EXPRESSION_INDEX,
					EIDType.UNSPECIFIED, iContentIndex);
		}

		if (genLabel== null || genLabel.equals(""))
			genLabel = "Unkonwn Gene";
		
		String tmpString =currentSelection.toString();
		if (contentSelectionManager.checkStatus(currentSelection,
				iContentIndex))												
			sLabel = tmpString+" ("
					+ genLabel
					+ "):"
					+ +set.get(storageVA.get(iSelectedAxisIndexX)).getFloat(
							EDataRepresentation.RAW, iContentIndex)
					+ " / "
					+ set.get(storageVA.get(iSelectedAxisIndexY)).getFloat(
							EDataRepresentation.RAW, iContentIndex);
		else
			if (contentSelectionManager.checkStatus(SelectionType.SELECTION,
					iContentIndex))
				sLabel = "Selected Point ("
					+ genLabel
					+ "):"
					+ +set.get(storageVA.get(iSelectedAxisIndexX)).getFloat(
							EDataRepresentation.RAW, iContentIndex)
					+ " / "
					+ set.get(storageVA.get(iSelectedAxisIndexY)).getFloat(
							EDataRepresentation.RAW, iContentIndex);
			else 						
			sLabel = "Point ("
					+ genLabel
					+ "):"
					+ +set.get(storageVA.get(iSelectedAxisIndexX)).getFloat(
							EDataRepresentation.RAW, iContentIndex)
					+ " / "
					+ set.get(storageVA.get(iSelectedAxisIndexY)).getFloat(
							EDataRepresentation.RAW, iContentIndex);
	
		float fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;

		Rectangle2D bounds = textRenderer.getScaledBounds(gl, sLabel, fScaling,
				ScatterPlotRenderStyle.MIN_NUMBER_TEXT_SIZE);

		float boxLengh = (float) bounds.getWidth() + 0.2f;
		float boxHight = (float) bounds.getHeight();

		gl.glColor3f(fMoueseOverColor[0], fMoueseOverColor[1],
				fMoueseOverColor[2]);
		gl.glBegin(GL.GL_POLYGON);
		
		gl.glVertex3f(0.0f, -0.02f, ScatterPlotRenderStyle.LABEL_Z);
		gl.glVertex3f(0.0f, boxHight, ScatterPlotRenderStyle.LABEL_Z);
		gl.glVertex3f(boxLengh, boxHight, ScatterPlotRenderStyle.LABEL_Z);
		gl.glVertex3f(boxLengh, -0.02f, ScatterPlotRenderStyle.LABEL_Z);
				
		gl.glEnd();
		
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3f(0.0f-fXtranslation, 0.0f-fYtranslation, ScatterPlotRenderStyle.LABEL_Z);
		gl.glVertex3f(0.0f, -0.02f, ScatterPlotRenderStyle.LABEL_Z);
		gl.glVertex3f(0.0f, boxHight, ScatterPlotRenderStyle.LABEL_Z);		
		gl.glEnd();
		
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(gl, sLabel, 0, 0,
				ScatterPlotRenderStyle.TEXT_ON_LABEL_Z, fScaling,
				ScatterPlotRenderStyle.MIN_AXIS_LABEL_TEXT_SIZE);
		textRenderer.end3DRendering();
		gl.glPopAttrib();

		gl.glTranslatef(-x, -y, 0);

	}

	
	/**
	 * Evaluates which Scatterpoint is in the Selection rectangle
	 * called by updateSelection()
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isInSelectionRectangle(float x, float y) {
		float XMin = Math.min(fRectangleDragStartPoint[0],
				fRectangleDragEndPoint[0]);
		float XMax = Math.max(fRectangleDragStartPoint[0],
				fRectangleDragEndPoint[0]);

		float YMin = Math.min(fRectangleDragStartPoint[1],
				fRectangleDragEndPoint[1]);
		float YMax = Math.max(fRectangleDragStartPoint[1],
				fRectangleDragEndPoint[1]);

		x = x + XYAXISDISTANCE;
		y = y + XYAXISDISTANCE;

		if (bRenderMatrix) {
			x += renderStyle.getCenterXOffset();
			y += renderStyle.getCenterYOffset();
		}

		if (x >= XMin && x <= XMax)
			if (y >= YMin && y <= YMax)
				return true;

		return false;
	}

	/**
	 * Evaluates which Scatterpoints are in the Selection rectangle
	 * 
	 */
	
	/**
	 * 
	 */
	private void updateSelection() {

		float XScale = renderStyle.getRenderWidth() - XYAXISDISTANCE * 2.0f;
		float YScale = renderStyle.getRenderHeight() - XYAXISDISTANCE * 2.0f;
		float x = 0.0f;
		float y = 0.0f;
		iDisplayEveryNthPoint = contentVA.size()
		  / iNumberOfRandomElements;
		  
		if (iDisplayEveryNthPoint == 0) 
			iDisplayEveryNthPoint = 1;

		boolean doNewSelection=true;
		for (Integer iContentIndex : contentVA) {

			if (iContentIndex == -1) {
				throw new IllegalStateException("No such element in virtual array");
			}
							
	    	if (bUseRandomSampling)		    	  
	    		  if (iContentIndex % iDisplayEveryNthPoint != 0) 
						continue;		        
		   				    											
			float xnormalized = set.get(storageVA.get(iSelectedAxisIndexX))
					.getFloat(EDataRepresentation.NORMALIZED, iContentIndex);
			float ynormalized = set.get(storageVA.get(iSelectedAxisIndexY))
					.getFloat(EDataRepresentation.NORMALIZED, iContentIndex);

			x = transformOnXZoom(xnormalized) * XScale;
			y = transformOnYZoom(ynormalized) * YScale;

			if (isInSelectionRectangle(x, y)) {		
				if(doNewSelection)
				{
					addSelectionType();
					doNewSelection=false;
				}
				
				contentSelectionManager.addToType(currentSelection,
						iContentIndex);
			}
		}
		ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
		handleConnectedElementRep(selectionDelta);
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		event.setInfo(getShortInfo());
		eventPublisher.triggerEvent(event);
	}

	/**
	 * Render The Selections in the MainView
	 * 
	 * @param gl
	 */
	private void renderSelectionPoints(GL gl) {

	//	for (SelectionType tmpSelectionType : AlSelectionTypes) {
		ArrayList<SelectionType> sTypes = contentSelectionManager.getSelectionTypes();
		for (SelectionType tmpSelectionType : sTypes) {
		
			
			if (!tmpSelectionType.isVisible())
				continue;

			if(tmpSelectionType!=SelectionType.SELECTION)		
				if (SelectionType.isDefaultType(tmpSelectionType))
					continue;
					

			if (contentSelectionManager.getNumberOfElements(tmpSelectionType) == 0)
				continue;

			float XScale = renderStyle.getRenderWidth() - XYAXISDISTANCE * 2.0f;
			float YScale = renderStyle.getRenderHeight() - XYAXISDISTANCE
					* 2.0f;

			// Set<Integer> selectionSet = elementSelectionManager
			// .getElements(SelectionType.SELECTION);

			Set<Integer> selectionSet = contentSelectionManager
					.getElements(tmpSelectionType);

			float x = 0.0f;
			float y = 0.0f;
			float z = ScatterPlotRenderStyle.SELECTION_Z+tmpSelectionType.getPriority();

			// float[] fArMappingColor = new float[]{1.0f, 0.1f, 0.5f};
			float[] fArMappingColor = tmpSelectionType.getColor();

			for (int iContentIndex : selectionSet) {

				float xnormalized = set
						.get(storageVA.get(iSelectedAxisIndexX))
						.getFloat(EDataRepresentation.NORMALIZED, iContentIndex);
				float ynormalized = set
						.get(storageVA.get(iSelectedAxisIndexY))
						.getFloat(EDataRepresentation.NORMALIZED, iContentIndex);


				x = transformOnXZoom(xnormalized) * XScale;
				y = transformOnYZoom(ynormalized) * YScale;

				renderPointPrimitive(gl, x, y, z, // z
						fArMappingColor, 1.0f,// fOpacity
						iContentIndex, 1.0f); // scale
			}
		}
	}

	private void renderPointPrimitive(GL gl, float x, float y, float z,
			float[] fArMappingColor, float fOpacity, int iContentIndex,
			float scale) {

		EScatterPointType type = POINTSTYLE;
		float fullPoint = POINTSIZE * scale;
		float halfPoint = (fullPoint / 2.0f);

		int iPickingID = pickingManager.getPickingID(iUniqueID,
				EPickingType.SCATTER_POINT_SELECTION, iContentIndex);
		gl
				.glColor3f(fArMappingColor[0], fArMappingColor[1],
						fArMappingColor[2]);

		gl.glPushName(iPickingID);
		switch (type) {
		case BOX: {
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(x - halfPoint, y - halfPoint, z);
			gl.glVertex3f(x - halfPoint, y + halfPoint, z);
			gl.glVertex3f(x + halfPoint, y + halfPoint, z);
			gl.glVertex3f(x + halfPoint, y - halfPoint, z);
			gl.glEnd();
			break;
		}
		case POINT: {
			gl.glPointSize(fullPoint * 50.0f);
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex3f(x, y, z);
			gl.glEnd();
			break;
		}
		case CROSS: {
			gl.glLineWidth(1.0f);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(x - halfPoint, y - halfPoint, z);
			gl.glVertex3f(x + halfPoint, y + halfPoint, z);
			gl.glVertex3f(x - halfPoint, y + halfPoint, z);
			gl.glVertex3f(x + halfPoint, y - halfPoint, z);
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

	private void renderRectangularSelection(GL gl, float x, float y, float z,
			float length, float height, float[] fArMappingColor) {

		gl
				.glColor3f(fArMappingColor[0], fArMappingColor[1],
						fArMappingColor[2]);
		gl.glLineWidth(2.0f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(x, y, z);
		gl.glVertex3f(x, y + height, z);
		gl.glVertex3f(x + length, y + height, z);
		gl.glVertex3f(x + length, y, z);
		gl.glEnd();
	}

	/**
	 * Updates the the MAinView Zoom Window borders, when a MainZoom-Selector-Axis is dragged;
	 * 
	 * @param x
	 * @param y
	 */
	private void handleMainZoomAxes(float x, float y)
	{
		switch (iCurrentDragZoom) {
		case 1:
			if (x >= 0 && x <= fTransformOldMinX)
				fTransformNewMinX = x;
			else if (x < 0)
				fTransformNewMinX = 0;
			else
				fTransformNewMinX = fTransformOldMinX;
			break;
		case 2:
			if (x >= fTransformOldMaxX && x <= 1)
				fTransformNewMaxX = x;
			else if (x > 1)
				fTransformNewMaxX = 1;
			else
				fTransformNewMaxX = fTransformOldMaxX;
			break;
		case 3:
			if (x >= 0 && x <= fTransformOldMaxX)
				fTransformOldMinX = x;
			else if (x < 0)
				fTransformOldMinX = 0;
			else
				fTransformOldMinX = fTransformOldMaxX;
			fTransformNewMinX=fTransformOldMinX;
			break;
		case 4:
			if (x >= fTransformOldMinX && x <= 1)
				fTransformOldMaxX = x;
			else if (fTransformNewMaxX > 1)
				fTransformOldMaxX = 1;
			else
				fTransformOldMaxX = fTransformOldMinX;
				fTransformNewMaxX=fTransformOldMaxX;
			break;	
		case 5:
			if (y >= 0 && y <= fTransformOldMinY)
				fTransformNewMinY = y;
			else if (y < 0)
				fTransformNewMinY = 0;
			else
				fTransformNewMinY = fTransformOldMinY;
			break;
		case 6:
			if (y >= fTransformOldMaxY && y <= 1)
				fTransformNewMaxY = y;
			else if (y > 1)
				fTransformNewMaxY = 1;
			else
				fTransformNewMaxY = fTransformOldMaxY;
			break;
		case 7:
			if (y >= 0 && y <= fTransformOldMaxY)
				fTransformOldMinY = y;
			else if (y < 0)
				fTransformOldMinY = 0;
			else
				fTransformOldMinY = fTransformOldMaxY;
			fTransformNewMinY=fTransformOldMinY;
			break;
		case 8:
			if (y >= fTransformOldMinY && y <= 1)
				fTransformOldMaxY = y;
			else if (fTransformNewMaxY > 1)
				fTransformOldMaxY = 1;
			else
				fTransformOldMaxY = fTransformOldMinY;
			fTransformNewMaxY=fTransformOldMaxY;
			break;								
		default:
		}
		
	}
		
	/**
	 * Transfer-function for Zoom on X-Axis
	 * 
	 * @param x
	 * @param fSize
	 * @param fOffset
	 * @return
	 */
	private float transformOnXZoom(float x, float fSize, float fOffset) {
		float tmp = (x - fOffset) / fSize;
		return transformOnXZoom(tmp) * fSize + fOffset;
	}

//	private float transformOnXZoom(float x, float fSize) {
//		float tmp = x / fSize;
//		return transformOnXZoom(tmp) * fSize;
//	}

	/**
	 * Transfer-function for Zoom  on X-Axis
	 * 
	 * @param x
	 * @return
	 */
	private float transformOnXZoom(float x) {
		if (!bMainViewZoom)
			return x;

		if (x < fTransformOldMinX) {
			float factor = fTransformOldMinX / fTransformNewMinX;
			return x / factor;
		}

		if (x > fTransformOldMaxX) {

			float factor = (1 - fTransformOldMaxX) / (1 - fTransformNewMaxX);
			return fTransformNewMaxX + (x - fTransformOldMaxX) / factor;
		}

		float factor = (fTransformNewMaxX - fTransformNewMinX)
				/ (fTransformOldMaxX - fTransformOldMinX);
		return (fTransformNewMinX) + (x - fTransformOldMinX) * factor;
	}
	
	/**
	 * Transfer-function for Zoom  on Y-Axis
	 * 
	 * @param y
	 * @param fSize
	 * @param fOffset
	 * @return
	 */
	private float transformOnYZoom(float y, float fSize, float fOffset) {
		float tmp = (y - fOffset) / fSize;
		return transformOnYZoom(tmp) * fSize + fOffset;
	}

//	private float transformOnYZoom(float y, float fSize) {
//		float tmp = y / fSize;
//		return transformOnXZoom(tmp) * fSize;
//	}

	/**
	 * Transfer-function for Zoom  on Y-Axis
	 * 
	 * @param y
	 * @return
	 */
	private float transformOnYZoom(float y) {
		if (!bMainViewZoom)
			return y;

		if (y < fTransformOldMinY) {
			float factor = fTransformOldMinY / fTransformNewMinY;
			return y / factor;
		}

		if (y > fTransformOldMaxY) {

			float factor = (1 - fTransformOldMaxY) / (1 - fTransformNewMaxY);
			return fTransformNewMaxY + (y - fTransformOldMaxY) / factor;
		}

		float factor = (fTransformNewMaxY - fTransformNewMinY)
				/ (fTransformOldMaxY - fTransformOldMinY);
		return (fTransformNewMinY) + (y - fTransformOldMinY) * factor;
	}
		
//	private String[] getAxisString() {
//		String[] tmpString = new String[storageVA.size()];
//		int axisCount = 0;
//		for (Integer iStorageIndex : storageVA) {
//
//			tmpString[axisCount++] = set.get(iStorageIndex).getLabel();
//
//		}
//		return tmpString;
//	}
	
	public void selectAxesfromExternal() {

		int iMouseOverSelections=storageSelectionManager
		.getNumberOfElements(SelectionType.MOUSE_OVER);
		
		int iSelectionSelections=storageSelectionManager
		.getNumberOfElements(SelectionType.SELECTION);
		
		if (iSelectionSelections>0) {

			Set<Integer> axisSet = storageSelectionManager
					.getElements(SelectionType.SELECTION);
			
			Iterator<Integer> axisIT = axisSet.iterator(); 									
			int itmpAxis=iSelectedAxisIndexX;
			
			int i = axisIT.next();
			iSelectedAxisIndexX = storageVA.indexOf(i);
			if(axisIT.hasNext()) 
			{
				i = axisIT.next();
				iSelectedAxisIndexY = storageVA.indexOf(i);
			}
								
			if(iSelectedAxisIndexX >iSelectedAxisIndexY)
			{
				itmpAxis=iSelectedAxisIndexX;
				iSelectedAxisIndexX=iSelectedAxisIndexY;
				iSelectedAxisIndexY=itmpAxis;
			}
			if(iSelectedAxisIndexX==iSelectedAxisIndexY)
				iSelectedAxisIndexX=itmpAxis;
						
			bUpdateMainView = true;

		}

		if (iMouseOverSelections==0)
			return;

		Set<Integer> axisSet = storageSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		Iterator<Integer> axisIT = axisSet.iterator(); 									
		int itmpAxis=iMouseOverAxisIndexX;
		
		int i = axisIT.next();
		iMouseOverAxisIndexX = storageVA.indexOf(i);
		if(axisIT.hasNext()) 
		{
			i = axisIT.next();
			iMouseOverAxisIndexY = storageVA.indexOf(i);
		}
							
		if(iMouseOverAxisIndexX >iMouseOverAxisIndexY)
		{
			itmpAxis=iMouseOverAxisIndexX;
			iMouseOverAxisIndexX=iMouseOverAxisIndexY;
			iMouseOverAxisIndexY=itmpAxis;
		}
		if(iMouseOverAxisIndexX==iMouseOverAxisIndexY)
			iMouseOverAxisIndexX=itmpAxis;

	}

	public void selectNewSelectionAxes() {
		storageSelectionManager.clearSelection(SelectionType.SELECTION);

		
		storageSelectionManager.addToType(SelectionType.SELECTION,
				storageVA.get(iSelectedAxisIndexX));
		storageSelectionManager.addToType(SelectionType.SELECTION,
				storageVA.get(iSelectedAxisIndexY));

		if (bRender2Axis) {
			storageSelectionManager.addToType(SelectionType.SELECTION,
					storageVA.get(iSelectedAxisIndexX2));
			storageSelectionManager.addToType(SelectionType.SELECTION,
					storageVA.get(iSelectedAxisIndexY2));
		}
		
		
		
		
//		storageSelectionManager.addToType(SelectionType.SELECTION,
//				SELECTED_X_AXIS);
//		storageSelectionManager.addToType(SelectionType.SELECTION,
//				SELECTED_Y_AXIS);
//
//		if (bRender2Axis) {
//			storageSelectionManager.addToType(SelectionType.SELECTION,
//					SELECTED_X_AXIS_2);
//			storageSelectionManager.addToType(SelectionType.SELECTION,
//					SELECTED_Y_AXIS_2);
//		}

		
		ISelectionDelta selectionDelta = storageSelectionManager.getDelta();
		handleConnectedElementRep(selectionDelta);
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		event.setInfo(getShortInfo());
		eventPublisher.triggerEvent(event);
	}

	public void selectNewMouseOverAxes() {
		storageSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
		
		storageSelectionManager.addToType(SelectionType.MOUSE_OVER,
				storageVA.get(iMouseOverAxisIndexX));
		storageSelectionManager.addToType(SelectionType.MOUSE_OVER,
				storageVA.get(iMouseOverAxisIndexY));
	
		ISelectionDelta selectionDelta = storageSelectionManager.getDelta();
		handleConnectedElementRep(selectionDelta);
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		event.setInfo(getShortInfo());
		eventPublisher.triggerEvent(event);
	}

	@Override
	protected void initLists() {
		if (contentVAType != ContentVAType.CONTENT_EMBEDDED_HM) {
			if (bRenderOnlyContext)
				contentVAType = ContentVAType.CONTENT_CONTEXT;
			else
				contentVAType = ContentVAType.CONTENT;
		}

		contentVA = useCase.getContentVA(contentVAType);
		storageVA = useCase.getStorageVA(storageVAType);

		contentSelectionManager.setVA(contentVA);

		storageSelectionManager.setVA(storageVA);
		//AlSelectionTypes.clear();
		//addSelectionType();
		// AlSelectionTypes.add(SelectionType.SELECTION);
		// currentSelection = SelectionType.SELECTION;

	}

	@Override
	public String getShortInfo() {
		if (contentVA == null)
			return "Scatterplot - 0 " + useCase.getContentLabel(false, true)
					+ " / 0 experiments";

//		return "Scatterplot - " + contentVA.size() + " "
//				+ useCase.getContentLabel(false, true) + " / "
//				+ storageVA.size() + " experiments";
		
		String tmpstring;
		int iPointNr = contentVA.size();
		
		if (iDisplayEveryNthPoint == 1) {
			tmpstring = "Scatterplot - " + iPointNr + " "
					+ useCase.getContentLabel(false, true) + " / "
					+ storageVA.size() + " experiments";
		} else {
			tmpstring = "Scatterplot - " + iPointNr
					/ iDisplayEveryNthPoint + " out of " + iPointNr + " "
					+ useCase.getContentLabel(false, true) + " / \n "
					+ storageVA.size() + " experiments";
		}
		return tmpstring;
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Scatter Plot\n");

		sInfoText.append(contentVA.size() + " "
				+ useCase.getContentLabel(true, true) + " in rows and "
				+ storageVA.size() + " experiments in columns.\n");

		if (bRenderOnlyContext) {
			sInfoText.append("Showing only " + " "
					+ useCase.getContentLabel(false, true)
					+ " which occur in one of the other views in focus\n");
		} else {
			if (bUseRandomSampling) {
				sInfoText.append("Random sampling active, sample size: "
						+ iNumberOfRandomElements + "\n");
			} else {
				sInfoText.append("Random sampling inactive\n");
			}

			if (dataFilterLevel == EDataFilterLevel.COMPLETE) {
				sInfoText.append("Showing all genes in the dataset\n");
			} else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING) {
				sInfoText
						.append("Showing all genes that have a known DAVID ID mapping\n");
			} else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
				sInfoText
						.append("Showing all genes that are contained in any of the KEGG or Biocarta pathways\n");
			}
		}

		// return sInfoText.toString();
		return "TODO: ScatterploT Deatil Info";
	}
	
	public void addSelectionType() {
		//int iSlectionNr = AlSelectionTypes.size();
		iCurrentSelectionNr++;
		if (iCurrentSelectionNr > iMaxSelections)
			return;
		SelectionTypeEvent event = new SelectionTypeEvent();
		currentSelection = new SelectionType();
		currentSelection.setType(CUSTOM_SELECTION_NAME+" " + iCurrentSelectionNr);
		currentSelection.setColor(ScatterPlotHelper.getSelectionColor(iCurrentSelectionNr));
		currentSelection.setPriority((float)iCurrentSelectionNr/10000.0f);
		event.addSelectionType(currentSelection);		
		eventPublisher.triggerEvent(event);
		
		

	//	AlSelectionTypes.add(currentSelection);

	}

	public void removeSelectionType() {
		return;
		//TODO:remove l8ter
//		iCurrentSelectionNr--;
//		if (iCurrentSelectionNr == 0)
//			return;
//		contentSelectionManager.clearSelection(currentSelection);
//		
//		SelectionTypeEvent event = new SelectionTypeEvent();			
//		event.addSelectionType(currentSelection);
//		event.setRemove(true);
//		eventPublisher.triggerEvent(event);
//		
//		
//		
//		AlSelectionTypes.remove(iSlectionNr - 1);
//		currentSelection = AlSelectionTypes.get(iSlectionNr - 2);
//		
//		ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
//		handleConnectedElementRep(selectionDelta);
//		SelectionUpdateEvent event2 = new SelectionUpdateEvent();
//		event2.setSender(this);
//		event2.setSelectionDelta((SelectionDelta) selectionDelta);
//		event2.setInfo(getShortInfo());
//		eventPublisher.triggerEvent(event2);
//		
//		
//		bUpdateSelection = true;
//		bUpdateSelectionTexures = true;
//		setDisplayListDirty();
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		if (bRectangleSelection)
			return;

		SelectionType selectionType;
		switch (ePickingType) {
		case SCATTER_POINT_SELECTION:
			if (bMainViewZoomDragged)
				return;
			//iCurrentMouseOverElement = iExternalID;
			switch (pickingMode) {

			case CLICKED:
				selectionType = SelectionType.SELECTION;
				// selectionType = currentSelection;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			case RIGHT_CLICKED:
				selectionType = SelectionType.DESELECTED;
				break;
			case DRAGGED:
				selectionType = SelectionType.SELECTION;
				break;
			default:
				return;

			}
			createContentSelection(selectionType, iExternalID);
			break;

		case SCATTER_MATRIX_SELECTION:
			//iCurrentMouseOverElement = iExternalID;
			switch (pickingMode) {
			case CLICKED:
				selectionType = SelectionType.SELECTION;
				break;
			case RIGHT_CLICKED:
				selectionType = SelectionType.DESELECTED;
				break;
			case MOUSE_OVER:
				selectionType = SelectionType.MOUSE_OVER;
				break;
			default:
				return;
			}
			createStorageSelection(selectionType, iExternalID);
			break;
		case SCATTER_MAIN_ZOOM:
			switch (pickingMode) {
			case CLICKED:
				if (!bMainViewZoomDragged) {
					bMainViewZoomDragged = true;					
					iCurrentDragZoom = iExternalID;		
					bClearSomeDisplayLists=true;
				}
				break;
			// case MOUSE_OVER :
			// if(bMainViewZoomDragged)
			// handleMainViewZoom(iExternalID);
			// break;

			default:
				return;
			}

			break;
		}

	}

	private void createStorageSelection(SelectionType selectionType,
			int contentID) {

		if (selectionType == SelectionType.SELECTION) {

			iSelectedAxisIndexX = contentID / NR_TEXTURESY;
			iSelectedAxisIndexY = contentID % NR_TEXTURESX;
			bUpdateMainView = true;
			this.bUpdateSelection =true;
			selectNewSelectionAxes();
			setDisplayListDirty();
			return;
		}

		if (selectionType == SelectionType.DESELECTED) {

			if (!bRender2Axis)
				return;
			iSelectedAxisIndexX2 = contentID / NR_TEXTURESY;
			iSelectedAxisIndexY2 = contentID % NR_TEXTURESX;
			bUpdateMainView = true;
			this.bUpdateSelection =true;
			selectNewSelectionAxes();
			setDisplayListDirty();
			return;
		}

		if (selectionType == SelectionType.MOUSE_OVER) {

			int itmpX_Axis = contentID / NR_TEXTURESY;
			int itmpY_Axis = contentID % NR_TEXTURESY;

			if ((itmpX_Axis == iMouseOverAxisIndexX)
					&& (itmpY_Axis == iMouseOverAxisIndexY))
				return;
			iMouseOverAxisIndexX = itmpX_Axis;
			iMouseOverAxisIndexY = itmpY_Axis;
			
			 selectNewMouseOverAxes();

			if (bRenderMatrix && bAllowMatrixZoom) {						
				 this.bRedrawTextures = true;
				 this.bUpdateMainView = true;
				 this.bUpdateSelection =true;
				 setDisplayListDirty();								
			}
		
		}

	}

	private void createContentSelection(SelectionType selectionType,
			int contentID) {

		
		SelectionCommand command = new SelectionCommand(
				ESelectionCommandType.CLEAR, selectionType);
		sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);

		contentSelectionManager.clearSelection(selectionType);

		
		if (contentSelectionManager.checkStatus(SelectionType.SELECTION,
				contentID)) {
			if (selectionType == SelectionType.DESELECTED) {
				contentSelectionManager.removeFromType(SelectionType.SELECTION,
						contentID);
				setDisplayListDirty();
				bUpdateSelection = true;
		//		return;
			}
		}


		if (selectionType == SelectionType.SELECTION) {
		//if (selectionType == currentSelection) {
			contentSelectionManager.addToType(selectionType, contentID);
			bUpdateSelection = true;
			// return;
		}

		if ((selectionType == SelectionType.MOUSE_OVER))
			contentSelectionManager.addToType(selectionType, contentID);


		ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
		handleConnectedElementRep(selectionDelta);
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		event.setInfo(getShortInfo());
		eventPublisher.triggerEvent(event);
		setDisplayListDirty();
	}

	// public void ResetSelection() {
	// elementSelectionManager.clearSelections();
	// fRectangleDragStartPoint = new float[3];
	// fRectangleDragEndPoint = new float[3];
	// bUpdateSelection = true;
	// setDisplayListDirty();
	// }

	
	
	
	@Override
	public void clearAllSelections() {
		
		storageSelectionManager.clearSelections();
		contentSelectionManager.clearSelections();
		
		ArrayList<SelectionType> sTypes = contentSelectionManager.getSelectionTypes(); 
		
		ArrayList<SelectionType> sTypestoRemove = new ArrayList<SelectionType>();
		for (SelectionType tmpSelectionType : sTypes) 
			{
				String tmpstring="";
				tmpstring=tmpSelectionType.toString();
				if (tmpstring==null)
					tmpstring="";
				
				if (!SelectionType.isDefaultType(tmpSelectionType) && tmpstring.startsWith(CUSTOM_SELECTION_NAME))
					sTypestoRemove.add(tmpSelectionType);
					
			}
		
		
		for (SelectionType tmpSelectionType : sTypestoRemove)
		{
			SelectionTypeEvent event = new SelectionTypeEvent();
			event.addSelectionType(tmpSelectionType);
			event.setRemove(true);
			eventPublisher.triggerEvent(event);
		}
		iCurrentSelectionNr=0;
	//	AlSelectionTypes.clear();		
	//	AlSelectionTypes.add(SelectionType.SELECTION);		
		//addSelectionType();
		ISelectionDelta selectionDelta = contentSelectionManager.getDelta();
		handleConnectedElementRep(selectionDelta);
		SelectionUpdateEvent event2 = new SelectionUpdateEvent();
		event2.setSender(this);
		event2.setSelectionDelta((SelectionDelta) selectionDelta);
		event2.setInfo(getShortInfo());
		eventPublisher.triggerEvent(event2);
		
		
		
		fRectangleDragStartPoint = new float[3];
		fRectangleDragEndPoint = new float[3];
		bUpdateSelection = true;
		bUpdateSelectionTexures = true;
		setDisplayListDirty();

	}

	@Override
	protected void reactOnExternalSelection(boolean scrollToSelection) {
		selectAxesfromExternal();
		bUpdateSelection = true;
		bUpdateSelectionTexures=true;
		setDisplayListDirty();
		// UpdateMouseOverfromExternal();
	}

	@Override
	protected void reactOnContentVAChanges(ContentVADelta delta) {

		contentSelectionManager.setVADelta(delta);
		bUpdateSelection = true;
		bUpdateSelectionTexures=true;
		setDisplayListDirty();

	}

	@Override
	protected void reactOnStorageVAChanges(StorageVADelta delta) {
		
		storageSelectionManager.setVADelta(delta);
		updateMaxAxis();
		renderStyle.setTextureNr(MAX_AXES, MAX_AXES);
		resetFullTextures();
		resetSelectionTextures();
		initTextures();
		initSelectionTextures();
		selectAxesfromExternal();
	}
	
	
	
	@Override
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta) {
		// Not Used in this View..
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext) {

		this.bRenderOnlyContext = bRenderOnlyContext;

		if (this.bRenderOnlyContext) {
			contentVA = useCase.getContentVA(ContentVAType.CONTENT_CONTEXT);
		} else {
			contentVA = useCase.getContentVA(ContentVAType.CONTENT);
		}

		contentSelectionManager.setVA(contentVA);
		// renderStyle.setActiveVirtualArray(iContentVAID);

		setDisplayListDirty();

	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedScatterplotView serializedForm = new SerializedScatterplotView(
				dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleUpdateView() {
		setDisplayListDirty();
	}
	
	@Override
	public void handleRedrawView() {
		if(bUseColor)
		{
		bUpdateMainView = true;
		bUpdateFullTexures = true;
		}
		setDisplayListDirty();
	}

	@Override
	public String toString() {
		return "Standalone Scatterplot, rendered remote: " + isRenderedRemote()
				+ ", contentSize: " + contentVA.size() + ", storageSize: "
				+ storageVA.size() + ", contentVAType: " + contentVAType
				+ ", remoteRenderer:" + getRemoteRenderingGLCanvas();
	}

	@Override
	public RemoteLevelElement getRemoteLevelElement() {

		// If the view is rendered remote - the remote level element from the
		// parent is returned
		if (glRemoteRenderingView != null
				&& glRemoteRenderingView instanceof AGLView)
			return ((AGLView) glRemoteRenderingView).getRemoteLevelElement();

		return super.getRemoteLevelElement();
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(EIDType idType,
			int iStorageIndex) throws InvalidAttributeValueException {

		SelectedElementRep elementRep;
		ArrayList<SelectedElementRep> alElementReps = new ArrayList<SelectedElementRep>(
				4);

		for (int iContentIndex : contentVA.indicesOf(iStorageIndex)) {
			if (iContentIndex == -1) {
				throw new IllegalStateException("No such element in virtual array");
				
			}

			float fXValue = fAlXDistances.get(iContentIndex); // +
			// renderStyle.getSelectedFieldWidth()
			// / 2;
			// float fYValue = 0;
			float fYValue = renderStyle.getYCenter();

			// Set<Integer> mouseOver =
			// storageSelectionManager.getElements(SelectionType.MOUSE_OVER);
			// for (int iLineIndex : mouseOver)
			// {
			// fYValue = storageVA.indexOf(iLineIndex) *
			// renderStyle.getFieldHeight() +
			// renderStyle.getFieldHeight()/2;
			// break;
			// }

			int iViewID = iUniqueID;
			// If rendered remote (hierarchical heat map) - use the remote view
			// ID
			if (glRemoteRenderingView != null)
				iViewID = glRemoteRenderingView.getID();

			Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float) Math.PI / 2);
			Vec3f vecPoint = myRotf
					.rotateVector(new Vec3f(fXValue, fYValue, 0));
			vecPoint.setY(vecPoint.y() + vecTranslation.y());
			elementRep = new SelectedElementRep(EIDType.EXPRESSION_INDEX,
					iViewID, vecPoint.x(),
					vecPoint.y() - fAnimationTranslation, 0);

			alElementReps.add(elementRep);
		}
		return alElementReps;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		togglePointTypeListener = new TogglePointTypeListener();
		togglePointTypeListener.setHandler(this);
		eventPublisher.addListener(TogglePointTypeEvent.class,
				togglePointTypeListener);

		toggleMatrixViewListener = new ToggleMatrixViewListener();
		toggleMatrixViewListener.setHandler(this);
		eventPublisher.addListener(SwitchMatrixViewEvent.class,
				toggleMatrixViewListener);

		toggle2AxisModeListener = new Toggle2AxisModeListener();
		toggle2AxisModeListener.setHandler(this);
		eventPublisher.addListener(Toggle2AxisEvent.class,
				toggle2AxisModeListener);

		toggleColorModeListener = new ToggleColorModeListener();
		toggleColorModeListener.setHandler(this);
		eventPublisher.addListener(ToggleColorModeEvent.class,
				toggleColorModeListener);

		toggleMatrixZoomListener = new ToggleMatrixZoomListener();
		toggleMatrixZoomListener.setHandler(this);
		eventPublisher.addListener(ToggleMatrixZoomEvent.class,
				toggleMatrixZoomListener);
		
		toggleMainViewZoomListener = new ToggleMainViewZoomListener();
		toggleMainViewZoomListener.setHandler(this);
		eventPublisher.addListener(ToggleMainViewZoomEvent.class,
				toggleMainViewZoomListener);

		setPointSizeListener = new SetPointSizeListener();
		setPointSizeListener.setHandler(this);
		eventPublisher.addListener(SetPointSizeEvent.class,
				setPointSizeListener);

		xAxisSelectorListener = new XAxisSelectorListener();
		xAxisSelectorListener.setHandler(this);
		eventPublisher.addListener(XAxisSelectorEvent.class,
				xAxisSelectorListener);

		yAxisSelectorListener = new YAxisSelectorListener();
		yAxisSelectorListener.setHandler(this);
		eventPublisher.addListener(YAxisSelectorEvent.class,
				yAxisSelectorListener);
		
		useRandomSamplingListener = new UseRandomSamplingListener();
		useRandomSamplingListener.setHandler(this);
		eventPublisher.addListener(UseRandomSamplingEvent.class,
				useRandomSamplingListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (togglePointTypeListener != null) {
			eventPublisher.removeListener(togglePointTypeListener);
			togglePointTypeListener = null;
		}

		if (toggleMatrixViewListener != null) {
			eventPublisher.removeListener(toggleMatrixViewListener);
			toggleMatrixViewListener = null;
		}

		if (toggleMainViewZoomListener != null) {
			eventPublisher.removeListener(toggleMainViewZoomListener);
			toggleMainViewZoomListener = null;
		}
						
		if (toggleMatrixZoomListener != null) {
			eventPublisher.removeListener(toggleMatrixZoomListener);
			toggleMatrixZoomListener = null;
		}

		if (toggle2AxisModeListener != null) {
			eventPublisher.removeListener(toggle2AxisModeListener);
			toggle2AxisModeListener = null;
		}

		if (toggleColorModeListener != null) {
			eventPublisher.removeListener(toggleColorModeListener);
			toggleColorModeListener = null;
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

	public void setXAxis(int iAxisIndex) {
		if (iSelectedAxisIndexX != iAxisIndex) {
			iSelectedAxisIndexX = iAxisIndex;
			bUpdateMainView = true;
			selectNewSelectionAxes();
			setDisplayListDirty();
		}

	}

	public void setYAxis(int iAxisIndex) {
		if (iSelectedAxisIndexY != iAxisIndex) {
			iSelectedAxisIndexY = iAxisIndex;
			bUpdateMainView = true;
			selectNewSelectionAxes();
			setDisplayListDirty();
		}

	}

	public void setPointSize(int pointSize) {
		if (renderStyle.getPointSize() != pointSize) {
			renderStyle.setPointSize(pointSize);
			bUpdateMainView = true;
			setDisplayListDirty();
		}
	}

	public void upDownMouse(boolean bDownIsTrue)
	  {
	    int tmpAxis = iMouseOverAxisIndexY;
	    if (bDownIsTrue)
	      ++tmpAxis;
	    else
	      --tmpAxis;
	    if ((tmpAxis == iMouseOverAxisIndexX) && (this.bOnlyRenderHalfMatrix))
	      return;
	    if (tmpAxis < 0)
	      tmpAxis = 0;
	    if (tmpAxis + 1 > this.MAX_AXES)
	      //tmpAxis = MOUSEOVER_Y_AXIS;
	      tmpAxis = this.MAX_AXES-1;
	    iMouseOverAxisIndexY = tmpAxis;
	    selectNewMouseOverAxes();
	    if (!(this.bAllowMatrixZoom))
	      return;
	    this.bRedrawTextures = true;
	    this.bUpdateMainView = true;
	    this.bUpdateSelection =true;
	    setDisplayListDirty();
	  }

	 public void upDownSelect(boolean bDownIsTrue)
	  {
		updateMaxAxis();
	    if (this.bRenderMatrix)
	    {
	      upDownMouse(bDownIsTrue);
	      return;
	    }
	    int tmpAxis = iSelectedAxisIndexY;
	    if (bDownIsTrue)
	      ++tmpAxis;
	    else
	      --tmpAxis;
	    if ((tmpAxis == iSelectedAxisIndexX) && (this.bOnlyRenderHalfMatrix))
	      return;
	    if (tmpAxis < 0)
	      tmpAxis = 0;
	    if (tmpAxis + 1 > this.MAX_AXES) {
	      //tmpAxis = SELECTED_Y_AXIS;
	    	tmpAxis = this.MAX_AXES-1;
	    }
	    iSelectedAxisIndexY = tmpAxis;
	    selectNewSelectionAxes();

	    this.bUpdateMainView = true;
	    setDisplayListDirty();
	  }

	 public void leftRightMouse(boolean bRightIsTrue)
	  {
	    int tmpAxis = iMouseOverAxisIndexX;
	    if (bRightIsTrue)
	      ++tmpAxis;
	    else
	      --tmpAxis;
	    if ((tmpAxis == iMouseOverAxisIndexY) && (this.bOnlyRenderHalfMatrix)) {
	      return;
	    }
	    if (tmpAxis < 0)
	      tmpAxis = 0;
	    if (tmpAxis + 1 > this.MAX_AXES)
	      //tmpAxis = MOUSEOVER_X_AXIS;
	    	tmpAxis = this.MAX_AXES-1;
	    iMouseOverAxisIndexX = tmpAxis;
	    selectNewMouseOverAxes();
	    if (!(this.bAllowMatrixZoom))
	      return;
	    this.bRedrawTextures = true;
	    this.bUpdateMainView = true;
	    this.bUpdateSelection =true;
	    setDisplayListDirty();
	  }

	 public void leftRightSelect(boolean bRightIsTrue)
	  {
		updateMaxAxis();
	    if (this.bRenderMatrix)
	    {
	      leftRightMouse(bRightIsTrue);
	      return;
	    }
	    int tmpAxis = iSelectedAxisIndexX;
	    if (bRightIsTrue)
	      ++tmpAxis;
	    else
	      --tmpAxis;
	    if ((tmpAxis == iSelectedAxisIndexY) && (this.bOnlyRenderHalfMatrix)) {
	      return;
	    }
	    if (tmpAxis < 0)
	      tmpAxis = 0;
	    if (tmpAxis + 1 > this.MAX_AXES)
	      //tmpAxis = SELECTED_X_AXIS;
	    	tmpAxis = this.MAX_AXES-1;
	    iSelectedAxisIndexX = tmpAxis;
	    selectNewSelectionAxes();
	    this.bUpdateMainView = true;	    
	    setDisplayListDirty();
	  }

	public void upDownSelect2Axis(boolean bDownIsTrue) {
		if (!bRender2Axis)
			return;
		updateMaxAxis();
		int tmpAxis = iSelectedAxisIndexY2;
		if (bDownIsTrue)
			tmpAxis++;
		else
			tmpAxis--;
		if (tmpAxis == iSelectedAxisIndexX2 && bOnlyRenderHalfMatrix)
			return;
		if (tmpAxis < 0)
			tmpAxis = 0;
		if ((tmpAxis + 1) > MAX_AXES)
			//tmpAxis = SELECTED_Y_AXIS_2;
			tmpAxis = this.MAX_AXES-1;
		iSelectedAxisIndexY2 = tmpAxis;
		bUpdateMainView = true;		
	    this.bUpdateSelection =true;
		selectNewSelectionAxes();
		setDisplayListDirty();
	}

	public void leftRightSelect2Axis(boolean bRightIsTrue) {
		if (!bRender2Axis)
			return;
		updateMaxAxis();
		int tmpAxis = iSelectedAxisIndexX2;
		if (bRightIsTrue)
			tmpAxis++;
		else
			tmpAxis--;
		if (tmpAxis == iSelectedAxisIndexY2 && bOnlyRenderHalfMatrix)
			return;

		if (tmpAxis < 0)
			tmpAxis = 0;
		if ((tmpAxis + 1) > MAX_AXES)
			//tmpAxis = SELECTED_X_AXIS_2;
			tmpAxis = this.MAX_AXES-1;
		iSelectedAxisIndexX2 = tmpAxis;
		bUpdateMainView = true;
		this.bUpdateSelection =true;
		selectNewSelectionAxes();
		setDisplayListDirty();
	}
	
	public void confirmCurrentSelection()
	  {
	    iSelectedAxisIndexX = iMouseOverAxisIndexX;
	    iSelectedAxisIndexY = iMouseOverAxisIndexY;
	    selectNewSelectionAxes();
	    this.bUpdateMainView = true;
	    this.bUpdateSelection =true;
	    setDisplayListDirty();
	  }
	
	public void toggleSpecialAxisMode() {
		if (bRender2Axis)
			bRender2Axis = false;
		else
			bRender2Axis = true;
		bUpdateMainView = true;
		setDisplayListDirty();
	}

	public void toggleDetailLevel() {
		if (detailLevel == EDetailLevel.HIGH) {
			detailLevel = EDetailLevel.LOW;
		} else
			detailLevel = EDetailLevel.HIGH;
		bUpdateMainView = true;
		setDisplayListDirty();
	}

	public void toggleMatrixMode() {

		if (bRenderMatrix) // embedded view->MainView
		{
			// bRenderMainView = true;
			bRenderMatrix = false;
			renderStyle.setIsEmbedded(false);
			bUpdateMainView = true;
			setDisplayListDirty();
			return;
		}

		// Not used Anymore
		// if (bRenderMainView && !bRenderMatrix) // MainView-> Matrix Mode
		// {
		// bRenderMainView = false;
		// bRenderMatrix = true;
		// bOnlyRenderHalfMatrix = false;
		// return;
		// }
		//
		// if (!bRenderMainView && bRenderMatrix) // Matrix View -> Embedded
		// View

		if (!bRenderMatrix) // MainView-> -> Embedded View
		{

			// bRenderMainView = true;
			bRenderMatrix = true;
			bOnlyRenderHalfMatrix = true;
			renderStyle.setIsEmbedded(true);
			bUpdateMainView = true;
			setDisplayListDirty();

			return;
		}
	}

	public void toggleMainViewZoom() {

		if (bMainViewZoom)
		{
			bMainViewZoom = false;
			bUpdateMainView = true;
			setDisplayListDirty();
		}
		else
		{
			fTransformOldMinX = 0.3f;
			fTransformNewMinX = 0.3f;
			fTransformOldMaxX = 0.4f;
			fTransformNewMaxX = 0.4f;
			
			fTransformOldMinY = 0.3f;
			fTransformNewMinY = 0.3f;
			fTransformOldMaxY = 0.4f;
			fTransformNewMaxY = 0.4f;
			
			bMainViewZoom = true;
			
		}

	}

	public void toggleColorMode() {
		if (bUseColor)
			bUseColor = false;
		else
			bUseColor = true;

		bUpdateMainView = true;
		bUpdateFullTexures = true;
		setDisplayListDirty();

	}

	public void toggleMatrixZoom() {
		if (bAllowMatrixZoom) {
			bAllowMatrixZoom = false;			
			bUpdateMainView = true;
			setDisplayListDirty();
		} else {
			bAllowMatrixZoom = true;			
			bUpdateMainView = true;
			setDisplayListDirty();
		}
	}
	
	public void togglePointType() {

		switch (POINTSTYLE) {
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
		bUpdateMainView = true;
		setDisplayListDirty();
	}

}
