package org.caleydo.core.view.opengl.canvas.storagebased.parcoords;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import javax.naming.OperationNotSupportedException;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.view.camera.IViewFrustum;
import org.caleydo.core.data.view.rep.renderstyle.ParCoordsRenderStyle;
import org.caleydo.core.data.view.rep.selection.SelectedElementRep;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.canvas.storagebased.EDataFilterLevel;
import org.caleydo.core.view.opengl.canvas.storagebased.EStorageBasedVAType;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.EIconTextures;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.GLIconTextureManager;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * This class is responsible for rendering the parallel coordinates
 * 
 * @author Alexander Lex (responsible for PC)
 * @author Marc Streit
 */
public class ParallelCoordinates
	extends AStorageBasedView
{

	private float fAxisSpacing = 0;

	/**
	 * Flag whether to take measures against occlusion or not
	 */
	private boolean bPreventOcclusion = true;

	// flag whether one array should be a polyline or an axis
	// protected boolean bRenderHorizontally = false;

	// Specify the current input data type for the axis and polylines
	// Is used for meta information, such as captions
	private EIDType eAxisDataType = EIDType.EXPRESSION_EXPERIMENT;

	private EIDType ePolylineDataType = EIDType.EXPRESSION_INDEX;

	private boolean bIsDraggingActive = false;

	private EPickingType draggedObject;

	// private int iNumberOfAxis = 0;

	private float[] fArGateTipHeight;

	private float[] fArGateBottomHeight;

	private ArrayList<ArrayList<Integer>> alIsGateBlocking;

	private ArrayList<ArrayList<Integer>> alIsAngleBlocking;

	private int iDraggedGateNumber = 0;

	private float fXDefaultTranslation = 0;

	private float fXTranslation = 0;

	private float fYTranslation = 0;

	private float fXTargetTranslation = 0;

	private boolean bIsTranslationActive = false;

	private ParCoordsRenderStyle renderStyle;

	// private boolean bRenderInfoArea = false;
	// private boolean bInfoAreaFirstTime = false;

	private boolean bAngularBrushingSelectPolyline = false;

	private boolean bIsAngularBrushingActive = false;

	private boolean bIsAngularBrushingFirstTime = false;

	private boolean bIsAngularDraggingActive = false;

	private Vec3f vecAngularBrusingPoint;

	private float fDefaultAngle = (float) Math.PI / 6;

	private float fCurrentAngle = 0;

	// private boolean bIsLineSelected = false;
	private int iSelectedLineID = -1;

	private Pick linePick;

	// private ArrayList<Integer> alPolylineSelection;
	//
	// private ArrayList<Integer> alAxisSelection;

	private DecimalFormat decimalFormat;

	private SelectedElementRep elementRep;

	// holds the textures for the icons
	private GLIconTextureManager iconTextureManager;

	private int iPolylineVAID = 0;
	private int iAxisVAID = 0;

	private GenericSelectionManager polylineSelectionManager;
	private GenericSelectionManager axisSelectionManager;

	/**
	 * Constructor.
	 */
	public ParallelCoordinates(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum);

		// alDataStorages = new ArrayList<IStorage>();
		renderStyle = new ParCoordsRenderStyle(viewFrustum);

		// TODO this is only valid for genes
		contentSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX)
				.mappingType(EMappingType.EXPRESSION_INDEX_2_DAVID,
						EMappingType.DAVID_2_EXPRESSION_STORAGE_ID).externalIDType(
						EIDType.DAVID).build();

		// TODO no mapping
		storageSelectionManager = new GenericSelectionManager.Builder(
				EIDType.EXPRESSION_EXPERIMENT).build();

		decimalFormat = new DecimalFormat("#####.##");

		alIsAngleBlocking = new ArrayList<ArrayList<Integer>>();
		alIsAngleBlocking.add(new ArrayList<Integer>());
	}

	@Override
	public void initLocal(final GL gl)
	{
		dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
		bRenderOnlyContext = false;

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);

		glToolboxRenderer = new GLParCoordsToolboxRenderer(gl, generalManager, iUniqueID,
				new Vec3f(0, 0, 0), true, renderStyle);

	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)
	{
//		dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;
		dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
		bRenderOnlyContext = true;

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		glToolboxRenderer = new GLParCoordsToolboxRenderer(gl, generalManager, iUniqueID,
				iRemoteViewID, new Vec3f(0, 0, 0), layer, true, renderStyle);

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void init(final GL gl)
	{
		iconTextureManager = new GLIconTextureManager(gl);

		initData();
		initLists();
		initGates();

		fXDefaultTranslation = renderStyle.getXSpacing();
		fYTranslation = renderStyle.getBottomSpacing();
	}
	
	/**
	 * Set the level of data filtering, according to the parameters defined in {@link EDataFilterLevel}
	 * 
	 * @param dataFilterLevel the level of filtering
	 */
	public void setDataFilterLevel(EDataFilterLevel dataFilterLevel)
	{
		this.dataFilterLevel = dataFilterLevel;
	}

	@Override
	public void displayLocal(final GL gl)
	{

		if (bIsTranslationActive)
		{
			doTranslation();
		}

		pickingManager.handlePicking(iUniqueID, gl, true);

		if (bIsDisplayListDirtyLocal)
		{
			buildPolyLineDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public void displayRemote(final GL gl)
	{

		if (bIsTranslationActive)
		{
			doTranslation();
		}

		if (bIsDisplayListDirtyRemote)
		{
			buildPolyLineDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}

		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		display(gl);

		checkForHits(gl);

	}

	@Override
	public void display(final GL gl)
	{

		// GLHelperFunctions.drawAxis(gl);
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
		gl.glColorMask(false, false, false, false);
		gl.glClearStencil(0); // Clear The Stencil Buffer To 0
		gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing To Do
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
		gl.glDisable(GL.GL_DEPTH_TEST);

		// Clip region that renders in stencil buffer (in this case the frustum)
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0, 0, -0.01f);
		gl.glVertex3f(0, 8, -0.01f);
		gl.glVertex3f(8, 8, -0.01f);
		gl.glVertex3f(8, 0, -0.01f);
		gl.glEnd();

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glColorMask(true, true, true, true);
		gl.glStencilFunc(GL.GL_EQUAL, 1, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

		gl.glTranslatef(fXDefaultTranslation + fXTranslation, fYTranslation, 0.0f);

		if (bIsDraggingActive)
			handleDragging(gl);

		// if(bRenderInfoArea)
		// infoAreaManager.renderInfoArea(gl, bInfoAreaFirstTime);
		// bInfoAreaFirstTime = false;

		if (bIsAngularBrushingActive && iSelectedLineID != -1)
		{
			handleAngularBrushing(gl);
		}

		checkUnselection();

		gl.glCallList(iGLDisplayListToCall);

		gl.glTranslatef(-fXDefaultTranslation - fXTranslation, -fYTranslation, 0.0f);

		gl.glTranslatef(fXDefaultTranslation - renderStyle.getXSpacing(), fYTranslation
				- renderStyle.getBottomSpacing(), 0.0f);
		glToolboxRenderer.render(gl);
		gl.glTranslatef(-fXDefaultTranslation + renderStyle.getXSpacing(), -fYTranslation
				+ renderStyle.getBottomSpacing(), 0.0f);

		gl.glDisable(GL.GL_STENCIL_TEST);
	}

	/**
	 * Choose whether to render one array as a polyline and every entry across
	 * arrays is an axis or whether the array corresponds to an axis and every
	 * entry across arrays is a polyline
	 */
	public void toggleAxisPolylineSwap()
	{
		bRenderStorageHorizontally = !bRenderStorageHorizontally;
		// bRenderInfoArea = false;
		EIDType eTempType = eAxisDataType;
		eAxisDataType = ePolylineDataType;
		ePolylineDataType = eTempType;
		fXTranslation = 0;
		connectedElementRepresentationManager.clear();
		initContentVariables();

		// TODO we might not need that here!
		// initLists();
		initGates();

	}

	@Override
	public void toggleRenderContext()
	{
		bRenderOnlyContext = !bRenderOnlyContext;

		if (bRenderOnlyContext)
			iContentVAID = mapSelections.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		else
		{
			if(!mapSelections.containsKey(EStorageBasedVAType.COMPLETE_SELECTION))
				initCompleteList();
			
			iContentVAID = mapSelections.get(EStorageBasedVAType.COMPLETE_SELECTION);
		}

		contentSelectionManager.setVA(set.getVA(iContentVAID));
		initContentVariables();
		initGates();
		resetSelections();

		setDisplayListDirty();
	}

	/**
	 * Choose whether to take measures against occlusion or not
	 * 
	 * @param bPreventOcclusion
	 */
	public void preventOcclusion(boolean bPreventOcclusion)
	{
		this.bPreventOcclusion = bPreventOcclusion;
	}

	/**
	 * Reset all selections and deselections
	 */
	public void resetSelections()
	{
		for (int iCount = 0; iCount < fArGateTipHeight.length; iCount++)
		{
			fArGateTipHeight[iCount] = 0;
			fArGateBottomHeight[iCount] = renderStyle.getGateYOffset()
					- renderStyle.getGateTipHeight();
		}
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		// bRenderInfoArea = false;
		bIsAngularBrushingActive = false;

		for (ArrayList<Integer> alCurrent : alIsAngleBlocking)
		{
			alCurrent.clear();
		}
		for (ArrayList<Integer> alCurrent : alIsGateBlocking)
		{
			alCurrent.clear();
		}
	}

	/**
	 * Set the display list to dirty
	 */
	public void setDisplayListDirty()
	{
		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;
	}

	/**
	 * Initializes the array lists that contain the data. Must be run at program
	 * start, every time you exchange axis and polylines and every time you
	 * change storages or selections *
	 */
	protected void initLists()
	{

		// TODO this needs only to be done if initLists has to be called during
		// runtime, not while initing
		// contentSelectionManager.resetSelectionManager();
		// storageSelectionManager.resetSelectionManager();

		// int iNumberOfEntriesToRender = 0;
		//		
		if (bRenderOnlyContext)
			iContentVAID = mapSelections.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		else
		{
			if(!mapSelections.containsKey(EStorageBasedVAType.COMPLETE_SELECTION))
				initCompleteList();
			iContentVAID = mapSelections.get(EStorageBasedVAType.COMPLETE_SELECTION);

		}
		iStorageVAID = mapSelections.get(EStorageBasedVAType.STORAGE_SELECTION);

		initContentVariables();

		contentSelectionManager.setVA(set.getVA(iContentVAID));
		storageSelectionManager.setVA(set.getVA(iStorageVAID));
		// iNumberOfEntriesToRender = alContentSelection.size();

		// int iNumberOfAxis = ;

		// // this for loop executes once per polyline
		// for (int iPolyLineCount = 0; iPolyLineCount <
		// iNumberOfPolyLinesToRender; iPolyLineCount++)
		// {
		// polylineSelectionManager.initialAdd(set.getVA(iPolylineVAID).get(
		// iPolyLineCount));
		// }
		//
		// // this for loop executes one per axis
		// for (int iAxisCount = 0; iAxisCount < iNumberOfAxis; iAxisCount++)
		// {
		//axisSelectionManager.initialAdd(set.getVA(iAxisVAID).get(iAxisCount));
		// }
		fAxisSpacing = renderStyle.getAxisSpacing(set.sizeVA(iAxisVAID));

	}

	/**
	 * Build mapping between polyline/axis and storage/content for virtual
	 * arrays and selection managers
	 */
	private void initContentVariables()
	{
		if (bRenderStorageHorizontally)
		{
			iPolylineVAID = iStorageVAID;
			iAxisVAID = iContentVAID;
			polylineSelectionManager = storageSelectionManager;
			axisSelectionManager = contentSelectionManager;
		}
		else
		{
			iPolylineVAID = iContentVAID;
			iAxisVAID = iStorageVAID;
			polylineSelectionManager = contentSelectionManager;
			axisSelectionManager = storageSelectionManager;
		}
	}

	/**
	 * Initialize the gates. The gate heights are saved in two lists, which
	 * contain the rendering height of the gate
	 */
	private void initGates()
	{

		fArGateTipHeight = new float[set.getVA(iAxisVAID).size()];
		fArGateBottomHeight = new float[set.getVA(iAxisVAID).size()];

		alIsGateBlocking = new ArrayList<ArrayList<Integer>>();
		for (int iCount = 0; iCount < fArGateTipHeight.length; iCount++)
		{
			fArGateTipHeight[iCount] = 0;
			fArGateBottomHeight[iCount] = renderStyle.getGateYOffset()
					- renderStyle.getGateTipHeight();
			alIsGateBlocking.add(new ArrayList<Integer>());
		}

	}

	/**
	 * Build polyline display list. Renderrs coordinate system, polylines and
	 * gates, by calling the render methods
	 * 
	 * @param gl GL context
	 * @param iGLDisplayListIndex the index of the display list
	 */
	private void buildPolyLineDisplayList(final GL gl, int iGLDisplayListIndex)
	{

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		// if(bIsDraggingActive)
		// handleDragging(gl);

		renderCoordinateSystem(gl, set.getVA(iAxisVAID).size());

		renderPolylines(gl, ESelectionType.DESELECTED);
		renderPolylines(gl, ESelectionType.NORMAL);
		renderPolylines(gl, ESelectionType.MOUSE_OVER);
		renderPolylines(gl, ESelectionType.SELECTION);

		renderGates(gl, set.getVA(iAxisVAID).size());

		gl.glEndList();
	}

	/**
	 * Polyline rendering method. All polylines that are contained in the
	 * polylineSelectionManager and are of the selection type specified in
	 * renderMode
	 * 
	 * @param gl the GL context
	 * @param renderMode the type of selection in the selection manager to
	 *            render
	 */
	private void renderPolylines(GL gl, ESelectionType renderMode)
	{

		Set<Integer> setDataToRender = null;
		float fZDepth = 0.0f;

		switch (renderMode)
		{
			case NORMAL:
				setDataToRender = polylineSelectionManager.getElements(renderMode);
				if (bPreventOcclusion)
					gl.glColor4fv(renderStyle.getPolylineOcclusionPrevColor(setDataToRender
							.size()), 0);
				else
					gl.glColor4fv(ParCoordsRenderStyle.POLYLINE_NO_OCCLUSION_PREV_COLOR, 0);

				gl.glLineWidth(ParCoordsRenderStyle.POLYLINE_LINE_WIDTH);
				break;
			case SELECTION:
				setDataToRender = polylineSelectionManager.getElements(renderMode);
				gl.glColor4fv(ParCoordsRenderStyle.POLYLINE_SELECTED_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.SELECTED_POLYLINE_LINE_WIDTH);
				break;
			case MOUSE_OVER:
				setDataToRender = polylineSelectionManager.getElements(renderMode);
				gl.glColor4fv(ParCoordsRenderStyle.POLYLINE_MOUSE_OVER_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.MOUSE_OVER_POLYLINE_LINE_WIDTH);
				break;
			case DESELECTED:
				setDataToRender = polylineSelectionManager.getElements(renderMode);
				gl.glColor4fv(renderStyle
						.getPolylineDeselectedOcclusionPrevColor(setDataToRender.size()), 0);
				gl.glLineWidth(ParCoordsRenderStyle.DESELECTED_POLYLINE_LINE_WIDTH);
				break;
			default:
				setDataToRender = polylineSelectionManager.getElements(ESelectionType.NORMAL);
		}

		Iterator<Integer> dataIterator = setDataToRender.iterator();
		// this loop executes once per polyline
		while (dataIterator.hasNext())
		{
			int iPolyLineID = dataIterator.next();
			if (renderMode != ESelectionType.DESELECTED)
				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.POLYLINE_SELECTION, iPolyLineID));

			IStorage currentStorage = null;

			// decide on which storage to use when array is polyline
			if (bRenderStorageHorizontally)
			{
				int iWhichStorage = iPolyLineID;
				currentStorage = set.getStorageFromVA(iStorageVAID, iWhichStorage);
			}

			float fPreviousXValue = 0;
			float fPreviousYValue = 0;
			float fCurrentXValue = 0;
			float fCurrentYValue = 0;

			// this loop executes once per axis
			for (int iVertexCount = 0; iVertexCount < set.getVA(iAxisVAID).size(); iVertexCount++)
			{
				int iStorageIndex = 0;

				// get the index if array as polyline
				if (bRenderStorageHorizontally)
				{
					iStorageIndex = set.getVA(iContentVAID).get(iVertexCount);
				}
				// get the storage and the storage index for the different cases
				else
				{
					currentStorage = set.getStorageFromVA(iStorageVAID, iVertexCount);
					iStorageIndex = iPolyLineID;
				}

				fCurrentXValue = iVertexCount * fAxisSpacing;
				fCurrentYValue = currentStorage.getFloat(EDataRepresentation.NORMALIZED,
						iStorageIndex);
				if (iVertexCount != 0)
				{
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3f(fPreviousXValue, fPreviousYValue
							* renderStyle.getAxisHeight(), fZDepth);
					gl.glVertex3f(fCurrentXValue,
							fCurrentYValue * renderStyle.getAxisHeight(), fZDepth);
					gl.glEnd();
				}

				if (renderMode == ESelectionType.SELECTION
						|| renderMode == ESelectionType.MOUSE_OVER)
				{
					float fYRawValue = currentStorage.getFloat(EDataRepresentation.RAW,
							iStorageIndex);
					renderYValues(gl, fCurrentXValue, fCurrentYValue
							* renderStyle.getAxisHeight(), fYRawValue, renderMode);
				}

				fPreviousXValue = fCurrentXValue;
				fPreviousYValue = fCurrentYValue;
			}

			if (renderMode != ESelectionType.DESELECTED)
				gl.glPopName();
		}
	}

	private void renderCoordinateSystem(GL gl, final int iNumberAxis)
	{
		textRenderer.setColor(0, 0, 0, 1);

		// draw X-Axis
		gl.glColor4fv(ParCoordsRenderStyle.X_AXIS_COLOR, 0);
		gl.glLineWidth(ParCoordsRenderStyle.X_AXIS_LINE_WIDTH);

		gl
				.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.X_AXIS_SELECTION, 1));
		gl.glBegin(GL.GL_LINES);

		gl.glVertex3f(-0.1f, 0.0f, 0.0f);
		gl.glVertex3f(((iNumberAxis - 1) * fAxisSpacing) + 0.1f, 0.0f, 0.0f);

		gl.glEnd();
		gl.glPopName();

		// draw all Y-Axis
		Set<Integer> selectedSet = axisSelectionManager.getElements(ESelectionType.SELECTION);
		Set<Integer> mouseOverSet = axisSelectionManager
				.getElements(ESelectionType.MOUSE_OVER);

		int iCount = 0;
		while (iCount < iNumberAxis)
		{
			if (selectedSet.contains(set.getVA(iAxisVAID).get(iCount)))
			{
				gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_SELECTED_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_SELECTED_LINE_WIDTH);
			}
			else if (mouseOverSet.contains(set.getVA(iAxisVAID).get(iCount)))
			{
				gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_MOUSE_OVER_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_MOUSE_OVER_LINE_WIDTH);
			}
			else
			{
				gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_COLOR, 0);
				gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH);
			}
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.Y_AXIS_SELECTION, set.getVA(iAxisVAID).get(iCount)));
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(iCount * fAxisSpacing, ParCoordsRenderStyle.Y_AXIS_LOW,
					ParCoordsRenderStyle.AXIS_Z);
			gl.glVertex3f(iCount * fAxisSpacing, renderStyle.getAxisHeight(),
					ParCoordsRenderStyle.AXIS_Z);
			gl.glVertex3f(iCount * fAxisSpacing - ParCoordsRenderStyle.AXIS_MARKER_WIDTH,
					renderStyle.getAxisHeight(), ParCoordsRenderStyle.AXIS_Z);
			gl.glVertex3f(iCount * fAxisSpacing + ParCoordsRenderStyle.AXIS_MARKER_WIDTH,
					renderStyle.getAxisHeight(), ParCoordsRenderStyle.AXIS_Z);
			gl.glEnd();

			String sAxisLabel = null;
			switch (eAxisDataType)
			{
				// TODO not very generic here
				case EXPRESSION_EXPERIMENT:
					// Labels
					// sAxisLabel = alDataStorages.get(iCount).getLabel();
					sAxisLabel = set.getStorageFromVA(iStorageVAID, iCount).getLabel();
					break;
				case EXPRESSION_INDEX:
					sAxisLabel = getRefSeqFromStorageIndex(set.getVA(iContentVAID).get(iCount));
					break;
				default:
					sAxisLabel = "No Label";
			}
			gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
			gl.glTranslatef(iCount * fAxisSpacing, renderStyle.getAxisHeight()
					+ renderStyle.getAxisCaptionSpacing(), 0);
			gl.glRotatef(25, 0, 0, 1);
			textRenderer.begin3DRendering();
			textRenderer.draw3D(sAxisLabel, 0, 0, 0, renderStyle.getSmallFontScalingFactor());
			textRenderer.end3DRendering();
			gl.glRotatef(-25, 0, 0, 1);
			gl.glTranslatef(-iCount * fAxisSpacing,
					-(renderStyle.getAxisHeight() + renderStyle.getAxisCaptionSpacing()), 0);

			textRenderer.begin3DRendering();

			// render values on top and bottom of axis
			try
			{
				// top
				textRenderer.draw3D(String.valueOf(set.getMax()), iCount * fAxisSpacing + 2
						* ParCoordsRenderStyle.AXIS_MARKER_WIDTH, renderStyle.getAxisHeight(),
						0, renderStyle.getSmallFontScalingFactor());

				// bottom
				textRenderer.draw3D(String.valueOf(set.getMin()), iCount * fAxisSpacing + 2
						* ParCoordsRenderStyle.AXIS_MARKER_WIDTH, 0, 0, renderStyle
						.getSmallFontScalingFactor());
				textRenderer.end3DRendering();
			}
			catch (OperationNotSupportedException e)
			{
				e.printStackTrace();
			}

			gl.glPopAttrib();
			gl.glPopName();
			// render Buttons

			int iNumberOfButtons = 0;
			if (iCount != 0 || iCount != iNumberAxis - 1)
				iNumberOfButtons = 4;
			else
				iNumberOfButtons = 3;

			float fXButtonOrigin = 0;
			float fYButtonOrigin = 0;
			int iPickingID = -1;

			fXButtonOrigin = iCount
					* fAxisSpacing
					- (iNumberOfButtons * renderStyle.getButtonWidht() + (iNumberOfButtons - 1)
							* renderStyle.getButtonSpacing()) / 2;
			fYButtonOrigin = -renderStyle.getAxisButtonYOffset();

			if (iCount != 0)
			{
				iPickingID = pickingManager.getPickingID(iUniqueID,
						EPickingType.MOVE_AXIS_LEFT, iCount);
				renderButton(gl, fXButtonOrigin, fYButtonOrigin, iPickingID,
						EIconTextures.ARROW_LEFT);
			}

			// remove button
			fXButtonOrigin = fXButtonOrigin + renderStyle.getButtonWidht()
					+ renderStyle.getButtonSpacing();

			iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.REMOVE_AXIS,
					iCount);
			renderButton(gl, fXButtonOrigin, fYButtonOrigin, iPickingID, EIconTextures.REMOVE);

			// duplicate axis button
			fXButtonOrigin = fXButtonOrigin + renderStyle.getButtonWidht()
					+ renderStyle.getButtonSpacing();
			iPickingID = pickingManager.getPickingID(iUniqueID, EPickingType.DUPLICATE_AXIS,
					iCount);
			renderButton(gl, fXButtonOrigin, fYButtonOrigin, iPickingID,
					EIconTextures.DUPLICATE);

			if (iCount != iNumberAxis - 1)
			{
				// right, move right button
				fXButtonOrigin = fXButtonOrigin + renderStyle.getButtonWidht()
						+ renderStyle.getButtonSpacing();
				iPickingID = pickingManager.getPickingID(iUniqueID,
						EPickingType.MOVE_AXIS_RIGHT, iCount);
				renderButton(gl, fXButtonOrigin, fYButtonOrigin, iPickingID,
						EIconTextures.ARROW_RIGHT);
			}
			iCount++;
		}
	}

	private void renderButton(GL gl, float fXButtonOrigin, float fYButtonOrigin,
			int iPickingID, EIconTextures eIconTextures)
	{

		Texture tempTexture = iconTextureManager.getIconTexture(eIconTextures);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1, 1, 1, 1);
		gl.glPushName(iPickingID);
		gl.glBegin(GL.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin, ParCoordsRenderStyle.AXIS_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin + renderStyle.getButtonWidht(),
				ParCoordsRenderStyle.AXIS_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin + renderStyle.getButtonWidht(), fYButtonOrigin
				+ renderStyle.getButtonWidht(), ParCoordsRenderStyle.AXIS_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin + renderStyle.getButtonWidht(), fYButtonOrigin,
				ParCoordsRenderStyle.AXIS_Z);
		gl.glEnd();
		gl.glPopName();
		gl.glPopAttrib();
		tempTexture.disable();
	}

	private void renderGates(GL gl, int iNumberAxis)
	{

		gl.glColor4fv(ParCoordsRenderStyle.GATE_COLOR, 0);

		final float fGateWidth = renderStyle.getGateWidth();
		final float fGateTipHeight = renderStyle.getGateTipHeight();
		int iCount = 0;
		while (iCount < iNumberAxis)
		{
			float fCurrentPosition = iCount * fAxisSpacing;

			// The tip of the gate (which is pickable)
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.LOWER_GATE_TIP_SELECTION, iCount));
			gl.glBegin(GL.GL_POLYGON);
			// variable
			gl.glVertex3f(fCurrentPosition + fGateWidth, fArGateTipHeight[iCount]
					- fGateTipHeight, 0.001f);
			// variable
			gl.glVertex3f(fCurrentPosition, fArGateTipHeight[iCount], 0.001f);
			// variable
			gl.glVertex3f(fCurrentPosition - fGateWidth, fArGateTipHeight[iCount]
					- fGateTipHeight, 0.001f);
			gl.glEnd();
			gl.glPopName();

			try
			{
				renderYValues(gl, fCurrentPosition, fArGateTipHeight[iCount], (float) set
						.getRawForNormalized(fArGateTipHeight[iCount]
								/ renderStyle.getAxisHeight()), ESelectionType.NORMAL);
			}
			catch (OperationNotSupportedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// The body of the gate
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.LOWER_GATE_BODY_SELECTION, iCount));
			gl.glBegin(GL.GL_POLYGON);
			// bottom
			gl.glVertex3f(fCurrentPosition - fGateWidth, fArGateBottomHeight[iCount]
					+ fGateTipHeight, 0.0001f);
			// constant
			gl.glVertex3f(fCurrentPosition + fGateWidth, fArGateBottomHeight[iCount]
					+ fGateTipHeight, 0.0001f);
			// top
			gl.glVertex3f(fCurrentPosition + fGateWidth, fArGateTipHeight[iCount]
					- fGateTipHeight, 0.0001f);
			// top
			gl.glVertex3f(fCurrentPosition - fGateWidth, fArGateTipHeight[iCount]
					- fGateTipHeight, 0.0001f);
			gl.glEnd();
			gl.glPopName();

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.LOWER_GATE_BOTTOM_SELECTION, iCount));
			// The bottom of the gate
			gl.glBegin(GL.GL_POLYGON);
			// variable
			gl.glVertex3f(fCurrentPosition + fGateWidth, fArGateBottomHeight[iCount]
					+ fGateTipHeight, 0.001f);
			// variable
			gl.glVertex3f(fCurrentPosition, fArGateBottomHeight[iCount], 0.001f);
			// variable
			gl.glVertex3f(fCurrentPosition - fGateWidth, fArGateBottomHeight[iCount]
					+ fGateTipHeight, 0.001f);
			gl.glEnd();
			gl.glPopName();

			try
			{
				renderYValues(gl, fCurrentPosition, fArGateBottomHeight[iCount], (float) set
						.getRawForNormalized(fArGateBottomHeight[iCount]
								/ renderStyle.getAxisHeight()), ESelectionType.NORMAL);
			}
			catch (OperationNotSupportedException e)
			{
				e.printStackTrace();
			}

			iCount++;
		}
	}

	/**
	 * Render the captions on the axis
	 * 
	 * @param gl
	 * @param fXOrigin
	 * @param fYOrigin
	 * @param renderMode
	 */
	private void renderYValues(GL gl, float fXOrigin, float fYOrigin, float fRawValue,
			ESelectionType renderMode)
	{

		// don't render values that are below the y axis
		if (fYOrigin < 0)
			return;

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glLineWidth(ParCoordsRenderStyle.Y_AXIS_LINE_WIDTH);
		gl.glColor4fv(ParCoordsRenderStyle.Y_AXIS_COLOR, 0);

		Rectangle2D tempRectangle = textRenderer.getBounds(decimalFormat.format(fYOrigin));
		float fBackPlaneWidth = (float) tempRectangle.getWidth()
				* renderStyle.getSmallFontScalingFactor();
		float fBackPlaneHeight = (float) tempRectangle.getHeight()
				* renderStyle.getSmallFontScalingFactor();
		float fXTextOrigin = fXOrigin + 2 * ParCoordsRenderStyle.AXIS_MARKER_WIDTH;
		float fYTextOrigin = fYOrigin;

		gl.glColor4f(0.8f, 0.8f, 0.8f, 0.5f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXTextOrigin, fYTextOrigin, 0.002f);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin, 0.002f);
		gl.glVertex3f(fXTextOrigin + fBackPlaneWidth, fYTextOrigin + fBackPlaneHeight, 0.002f);
		gl.glVertex3f(fXTextOrigin, fYTextOrigin + fBackPlaneHeight, 0.002f);
		gl.glEnd();

		textRenderer.begin3DRendering();

		textRenderer.draw3D(decimalFormat.format(fRawValue), fXTextOrigin, fYTextOrigin,
				0.0021f, renderStyle.getSmallFontScalingFactor());
		textRenderer.end3DRendering();
		gl.glPopAttrib();
	}

	private void handleDragging(GL gl)
	{

		// bIsDisplayListDirtyLocal = true;
		// bIsDisplayListDirtyRemote = true;
		Point currentPoint = pickingTriggerMouseAdapter.getPickedPoint();

		float[] fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float height = fArTargetWorldCoordinates[1];
		if (draggedObject == EPickingType.LOWER_GATE_TIP_SELECTION)
		{
			float fLowerLimit = fArGateBottomHeight[iDraggedGateNumber] + 2
					* renderStyle.getGateTipHeight();

			if (height > renderStyle.getAxisHeight())
			{
				height = renderStyle.getAxisHeight();
			}
			else if (height < 0)
			{
				height = 0;
			}
			else if (height < fLowerLimit)
			{
				height = fLowerLimit;
			}

			fArGateTipHeight[iDraggedGateNumber] = height;
		}
		else if (draggedObject == EPickingType.LOWER_GATE_BOTTOM_SELECTION)
		{
			float fLowerLimit = renderStyle.getGateYOffset() - renderStyle.getGateTipHeight();
			float fUpperLimit = fArGateTipHeight[iDraggedGateNumber] - 2
					* renderStyle.getGateTipHeight();

			if (height > renderStyle.getAxisHeight() - renderStyle.getGateTipHeight())
			{
				height = renderStyle.getAxisHeight() - renderStyle.getGateTipHeight();
			}
			else if (height < fLowerLimit)
			{
				height = renderStyle.getGateYOffset() - renderStyle.getGateTipHeight();
			}
			else if (height > fUpperLimit)
			{
				height = fUpperLimit;
			}

			fArGateBottomHeight[iDraggedGateNumber] = height;
		}
		else if (draggedObject == EPickingType.LOWER_GATE_BODY_SELECTION)
		{

		}

		bIsDisplayListDirtyLocal = true;
		bIsDisplayListDirtyRemote = true;

		if (pickingTriggerMouseAdapter.wasMouseReleased())
		{
			bIsDraggingActive = false;
		}
		handleUnselection(iDraggedGateNumber);
	}

	/**
	 * Unselect all lines that are deselected with the gates
	 * 
	 * @param iAxisNumber
	 */
	// TODO revise
	private void handleUnselection(int iAxisNumber)
	{

		ArrayList<Integer> alCurrentGateBlocks = alIsGateBlocking.get(iAxisNumber);
		alCurrentGateBlocks.clear();

		float fCurrentValue = -1;
		for (int iPolylineIndex : set.getVA(iPolylineVAID))
		{
			if (bRenderStorageHorizontally)
			{
				fCurrentValue = set.get(iPolylineIndex).getFloatVA(
						EDataRepresentation.NORMALIZED, iAxisNumber, iContentVAID);
			}
			else
			{
				fCurrentValue = set.getStorageFromVA(iStorageVAID, iAxisNumber).getFloat(
						EDataRepresentation.NORMALIZED, iPolylineIndex);
			}

			if (fCurrentValue <= fArGateTipHeight[iAxisNumber] / renderStyle.getAxisHeight()
					&& fCurrentValue >= fArGateBottomHeight[iAxisNumber]
							/ renderStyle.getAxisHeight())
			{
				alCurrentGateBlocks.add(iPolylineIndex);
			}
		}

	}

	protected void checkUnselection()
	{
		HashMap<Integer, Boolean> hashDeselectedPolylines = new HashMap<Integer, Boolean>();

		for (ArrayList<Integer> alCurrent : alIsGateBlocking)
		{
			for (Integer iCurrent : alCurrent)
			{
				hashDeselectedPolylines.put(iCurrent, true);
			}
		}

		for (ArrayList<Integer> alCurrent : alIsAngleBlocking)
		{
			for (Integer iCurrent : alCurrent)
			{
				hashDeselectedPolylines.put(iCurrent, true);
			}
		}

		for (Integer iCurrent : set.getVA(iPolylineVAID))
		{
			if (hashDeselectedPolylines.get(iCurrent) != null)
			{
				polylineSelectionManager.addToType(ESelectionType.DESELECTED, iCurrent);
			}
			else
			{
				polylineSelectionManager.removeFromType(ESelectionType.DESELECTED, iCurrent);
			}
		}
	}

	@Override
	protected void handleEvents(final EPickingType ePickingType,
			final EPickingMode ePickingMode, final int iExternalID, final Pick pick)
	{

		if (remoteRenderingGLCanvas != null)
		{
			// Check if selection occurs in the pool or memo layer of the remote
			// rendered view (i.e. bucket, jukebox)
			if (remoteRenderingGLCanvas.getHierarchyLayerByGLEventListenerId(iUniqueID)
					.getCapacity() > 5)
			{
				return;
			}
		}

		switch (ePickingType)
		{
			case POLYLINE_SELECTION:

				switch (ePickingMode)
				{

					case CLICKED:
						connectedElementRepresentationManager.clear();
						polylineSelectionManager.clearSelection(ESelectionType.SELECTION);
						polylineSelectionManager.addToType(ESelectionType.SELECTION,
								iExternalID);

						if (ePolylineDataType == EIDType.EXPRESSION_INDEX
								&& !bAngularBrushingSelectPolyline)
						{
							triggerUpdate(polylineSelectionManager.getDelta());
						}

						if (bAngularBrushingSelectPolyline)
						{
							bAngularBrushingSelectPolyline = false;
							bIsAngularBrushingActive = true;
							iSelectedLineID = iExternalID;
							linePick = pick;
							bIsAngularBrushingFirstTime = true;
						}
						setDisplayListDirty();
						break;
					case MOUSE_OVER:
						connectedElementRepresentationManager.clear();

						polylineSelectionManager.clearSelection(ESelectionType.MOUSE_OVER);
						polylineSelectionManager.addToType(ESelectionType.MOUSE_OVER,
								iExternalID);
						if (ePolylineDataType == EIDType.EXPRESSION_INDEX)
						{
							triggerUpdate(polylineSelectionManager.getDelta());
						}
						setDisplayListDirty();
						break;
				}
				pickingManager.flushHits(iUniqueID, ePickingType);
				break;

			case X_AXIS_SELECTION:
				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
			case Y_AXIS_SELECTION:
				switch (ePickingMode)
				{
					case CLICKED:

						axisSelectionManager.clearSelection(ESelectionType.SELECTION);
						axisSelectionManager.addToType(ESelectionType.SELECTION, iExternalID);

						if (eAxisDataType == EIDType.EXPRESSION_INDEX)
						{
							triggerUpdate(axisSelectionManager.getDelta());
						}
						rePosition(iExternalID);
						setDisplayListDirty();
						break;
					case MOUSE_OVER:
						axisSelectionManager.clearSelection(ESelectionType.MOUSE_OVER);
						axisSelectionManager.addToType(ESelectionType.MOUSE_OVER, iExternalID);

						if (eAxisDataType == EIDType.EXPRESSION_INDEX)
						{
							triggerUpdate(axisSelectionManager.getDelta());
						}
						setDisplayListDirty();
						break;
				}
				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
			case LOWER_GATE_TIP_SELECTION:
				switch (ePickingMode)
				{
					case CLICKED:
						break;
					case DRAGGED:
						bIsDraggingActive = true;
						draggedObject = EPickingType.LOWER_GATE_TIP_SELECTION;
						iDraggedGateNumber = iExternalID;
						break;
				}
				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
			case LOWER_GATE_BOTTOM_SELECTION:
				switch (ePickingMode)
				{
					case CLICKED:
						break;
					case DRAGGED:
						bIsDraggingActive = true;
						draggedObject = EPickingType.LOWER_GATE_BOTTOM_SELECTION;
						iDraggedGateNumber = iExternalID;
						break;
				}
				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
			case PC_ICON_SELECTION:
				switch (ePickingMode)
				{
					case CLICKED:
						if (iExternalID == EIconIDs.TOGGLE_RENDER_ARRAY_AS_POLYLINE.ordinal())
						{
							if (bRenderStorageHorizontally == true)
								toggleAxisPolylineSwap();
							else
								toggleAxisPolylineSwap();
						}
						else if (iExternalID == EIconIDs.TOGGLE_PREVENT_OCCLUSION.ordinal())
						{
							if (bPreventOcclusion == true)
								preventOcclusion(false);
							else
								preventOcclusion(true);
						}
						else if (iExternalID == EIconIDs.TOGGLE_RENDER_CONTEXT.ordinal())
						{
							toggleRenderContext();
						}
						else if (iExternalID == EIconIDs.RESET_SELECTIONS.ordinal())
						{
							resetSelections();
						}
						else if (iExternalID == EIconIDs.SAVE_SELECTIONS.ordinal())
						{
							contentSelectionManager.moveType(ESelectionType.DESELECTED,
									ESelectionType.REMOVE);
							ISelectionDelta delta = contentSelectionManager.getDelta();
							resetSelections();
							triggerUpdate(delta);

						}
						else if (iExternalID == EIconIDs.ANGULAR_BRUSHING.ordinal())
						{
							bAngularBrushingSelectPolyline = true;
						}

						bIsDisplayListDirtyLocal = true;
						bIsDisplayListDirtyRemote = true;
						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.PC_ICON_SELECTION);
				break;
			case REMOVE_AXIS:
				switch (ePickingMode)
				{
					case CLICKED:
						if (bRenderStorageHorizontally)
						{
							set.getVA(iContentVAID).remove(iExternalID);
						}
						else
						{
							set.getVA(iStorageVAID).remove(iExternalID);
						}
						setDisplayListDirty();
						break;
				}
				pickingManager.flushHits(iUniqueID, EPickingType.REMOVE_AXIS);
				break;
			case MOVE_AXIS_LEFT:
				switch (ePickingMode)
				{
					case CLICKED:
						if (iExternalID > 0)
						{
							set.getVA(iAxisVAID).moveLeft(iExternalID);
							setDisplayListDirty();
							resetSelections();
						}
						break;
				}
				pickingManager.flushHits(iUniqueID, EPickingType.MOVE_AXIS_LEFT);
				break;
			case MOVE_AXIS_RIGHT:

				switch (ePickingMode)
				{
					case CLICKED:
						if (iExternalID > 0)
						{
							set.getVA(iAxisVAID).moveRight(iExternalID);
							setDisplayListDirty();
							resetSelections();
						}
						break;
				}
				pickingManager.flushHits(iUniqueID, EPickingType.MOVE_AXIS_RIGHT);
				break;
			case DUPLICATE_AXIS:
				switch (ePickingMode)
				{
					case CLICKED:
						if (iExternalID > 0)
						{
							set.getVA(iAxisVAID).copy(iExternalID);
							setDisplayListDirty();
							resetSelections();
							break;
						}
				}
				pickingManager.flushHits(iUniqueID, EPickingType.DUPLICATE_AXIS);
				break;
			case ANGULAR_UPPER:
				switch (ePickingMode)
				{
					case DRAGGED:
						bIsAngularDraggingActive = true;
				}
				pickingManager.flushHits(iUniqueID, EPickingType.ANGULAR_UPPER);
				break;

			case ANGULAR_LOWER:
				switch (ePickingMode)
				{
					case DRAGGED:
						bIsAngularDraggingActive = true;
				}
				break;
		}
	}

	@Override
	protected SelectedElementRep createElementRep(int iStorageIndex)
			throws InvalidAttributeValueException
	{
		// TODO only for one element atm

		float fXValue = 0;
		float fYValue = 0;

		if (bRenderStorageHorizontally)
		{
			fXValue = set.getVA(iAxisVAID).indexOf(iStorageIndex);
			fXValue = fXValue + renderStyle.getXSpacing() + fXTranslation;
			fYValue = renderStyle.getBottomSpacing();
		}
		else
		{

			fXValue = renderStyle.getXSpacing() + fXTranslation;
			// get the value on the leftmost axis
			fYValue = set.getStorageFromVA(iStorageVAID, 0).getFloat(
					EDataRepresentation.NORMALIZED, iStorageIndex);

			fYValue = fYValue * renderStyle.getAxisHeight() + renderStyle.getBottomSpacing();
		}

		SelectedElementRep elementRep = new SelectedElementRep(iUniqueID, fXValue, fYValue,
				0.0f);
		return elementRep;
	}

	@Override
	public ArrayList<String> getInfo()
	{
		ArrayList<String> sAlInfo = new ArrayList<String>();
		sAlInfo.add("Type: Parallel Coordinates");
		if (!bRenderStorageHorizontally)
		{
			sAlInfo.add(set.getVA(iContentVAID).size() + " genes as polylines and "
					+ set.getVA(iAxisVAID).size() + " experiments as axis.");
		}
		else
		{
			sAlInfo.add(set.getVA(iStorageVAID).size() + " experiments as polylines and "
					+ set.getVA(iAxisVAID).size() + " genes as axis.");
		}
		return sAlInfo;
	}

	@Override
	protected void rePosition(int iElementID)
	{

		IVirtualArray virtualArray;
		if (bRenderStorageHorizontally)
			virtualArray = set.getVA(iContentVAID);
		else
			virtualArray = set.getVA(iStorageVAID);

		float fCurrentPosition = virtualArray.indexOf(iElementID) * fAxisSpacing
				+ renderStyle.getXSpacing();

		float fFrustumLength = viewFrustum.getRight() - viewFrustum.getLeft();
		float fLength = (virtualArray.size() - 1) * fAxisSpacing;

		fXTargetTranslation = -(fCurrentPosition - fFrustumLength / 2);

		if (-fXTargetTranslation > fLength - fFrustumLength)
			fXTargetTranslation = -(fLength - fFrustumLength + 2 * renderStyle.getXSpacing());
		else if (fXTargetTranslation > 0)
			fXTargetTranslation = 0;
		else if (-fXTargetTranslation < -fXTranslation + fFrustumLength / 2
				- renderStyle.getXSpacing()
				&& -fXTargetTranslation > -fXTranslation - fFrustumLength / 2
						+ renderStyle.getXSpacing())
		{
			fXTargetTranslation = fXTranslation;
			return;
		}

		bIsTranslationActive = true;
	}

	private void doTranslation()
	{

		float fDelta = 0;
		if (fXTargetTranslation < fXTranslation - 0.3)
		{

			fDelta = -0.3f;

		}
		else if (fXTargetTranslation > fXTranslation + 0.3)
		{
			fDelta = 0.3f;
		}
		else
		{
			fDelta = fXTargetTranslation - fXTranslation;
			bIsTranslationActive = false;
		}

		if (elementRep != null)
		{
			ArrayList<Vec3f> alPoints = elementRep.getPoints();
			for (Vec3f currentPoint : alPoints)
			{
				currentPoint.setX(currentPoint.x() + fDelta);
			}
		}

		fXTranslation += fDelta;
	}

	private void handleAngularBrushing(final GL gl)
	{

		if (bIsAngularBrushingFirstTime)
		{

			fCurrentAngle = fDefaultAngle;
			Point currentPoint = linePick.getPickedPoint();
			float[] fArPoint = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(
					gl, currentPoint.x, currentPoint.y);
			vecAngularBrusingPoint = new Vec3f(fArPoint[0], fArPoint[1], fArPoint[2]);
			bIsAngularBrushingFirstTime = false;

		}
		alIsAngleBlocking.get(0).clear();

		int iPosition = (int) (vecAngularBrusingPoint.x() / fAxisSpacing);
		int iAxisLeftIndex;
		int iAxisRightIndex;

		iAxisLeftIndex = set.getVA(iAxisVAID).get(iPosition);
		iAxisRightIndex = set.getVA(iAxisVAID).get(iPosition + 1);

		// int iPolylineIndex = alPolylineSelection.indexOf(iSelectedLineID);

		Vec3f vecLeftPoint = new Vec3f(0, 0, 0);
		Vec3f vecRightPoint = new Vec3f(0, 0, 0);

		if (bRenderStorageHorizontally)
		{
			// vecLeftPoint.setY(alDataStorages.get(iPolylineIndex).
			// getArrayFloat()[iAxisLeftIndex] * renderStyle.getAxisHeight());
			// vecRightPoint.setY(alDataStorages.get(iPolylineIndex).
			// getArrayFloat()[iAxisRightIndex] * renderStyle.getAxisHeight());
		}
		else
		{
			vecLeftPoint.setY(set.get(iAxisLeftIndex).getFloat(EDataRepresentation.NORMALIZED,
					iSelectedLineID)
					* renderStyle.getAxisHeight());
			vecRightPoint.setY(set.get(iAxisRightIndex).getFloat(
					EDataRepresentation.NORMALIZED, iSelectedLineID)
					* renderStyle.getAxisHeight());
		}

		vecLeftPoint.setX(iPosition * fAxisSpacing);
		vecRightPoint.setX((iPosition + 1) * fAxisSpacing);

		// GLHelperFunctions.drawPointAt(gl, vecLeftPoint);
		// GLHelperFunctions.drawPointAt(gl, vecRightPoint);

		Vec3f vecDirectional = vecRightPoint.minus(vecLeftPoint);
		float fLength = vecDirectional.length();
		vecDirectional.normalize();

		Vec3f vecTriangleOrigin = vecLeftPoint.addScaled(fLength / 4, vecDirectional);
		// GLHelperFunctions.drawPointAt(gl, vecTriangleOrigin);

		Vec3f vecTriangleLimit = vecLeftPoint.addScaled(fLength / 4 * 3, vecDirectional);

		Rotf rotf = new Rotf();

		Vec3f vecCenterLine = vecTriangleLimit.minus(vecTriangleOrigin);

		if (bIsAngularDraggingActive)
		{
			Point pickedPoint = pickingTriggerMouseAdapter.getPickedPoint();
			float fArPoint[] = GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(
					gl, pickedPoint.x, pickedPoint.y);
			Vec3f vecPickedPoint = new Vec3f(fArPoint[0], fArPoint[1], fArPoint[2]);
			Vec3f vecTempLine = vecPickedPoint.minus(vecTriangleOrigin);

			fCurrentAngle = getAngle(vecTempLine, vecCenterLine);

			bIsDisplayListDirtyLocal = true;
			bIsDisplayListDirtyRemote = true;
		}

		rotf.set(new Vec3f(0, 0, 1), fCurrentAngle);

		Vec3f vecUpperPoint = rotf.rotateVector(vecCenterLine);
		rotf.set(new Vec3f(0, 0, 1), -fCurrentAngle);
		Vec3f vecLowerPoint = rotf.rotateVector(vecCenterLine);

		vecUpperPoint.add(vecTriangleOrigin);
		vecLowerPoint.add(vecTriangleOrigin);

		gl.glColor4fv(ParCoordsRenderStyle.ANGULAR_COLOR, 0);
		gl.glLineWidth(ParCoordsRenderStyle.ANGLUAR_LINE_WIDTH);

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.ANGULAR_UPPER,
				iPosition));
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(),
				vecTriangleOrigin.z() + 0.02f);
		gl.glVertex3f(vecUpperPoint.x(), vecUpperPoint.y(), vecUpperPoint.z() + 0.02f);
		gl.glEnd();
		gl.glPopName();

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.ANGULAR_UPPER,
				iPosition));
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(),
				vecTriangleOrigin.z() + 0.02f);
		gl.glVertex3f(vecLowerPoint.x(), vecLowerPoint.y(), vecLowerPoint.z() + 0.02f);
		gl.glEnd();
		gl.glPopName();

		// gl.glLineStipple(1, GL.GL_LINE_STIPPLE_PATTERN);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(vecTriangleOrigin.x(), vecTriangleOrigin.y(),
				vecTriangleOrigin.z() + 0.02f);
		gl.glVertex3f(vecTriangleLimit.x(), vecTriangleLimit.y(), vecLowerPoint.z() + 0.02f);
		gl.glEnd();

		// check selection

		for (Integer iCurrent : set.getVA(iPolylineVAID))
		{
			if (bRenderStorageHorizontally)
			{
				vecLeftPoint.setY(set.get(iCurrent).getFloat(EDataRepresentation.NORMALIZED,
						iAxisLeftIndex)
						* renderStyle.getAxisHeight());
				vecRightPoint.setY(set.get(iCurrent).getFloat(EDataRepresentation.NORMALIZED,
						iAxisRightIndex)
						* renderStyle.getAxisHeight());
			}
			else
			{
				vecLeftPoint.setY(set.get(iAxisLeftIndex).getFloat(
						EDataRepresentation.NORMALIZED, iCurrent)
						* renderStyle.getAxisHeight());
				vecRightPoint.setY(set.get(iAxisRightIndex).getFloat(
						EDataRepresentation.NORMALIZED, iCurrent)
						* renderStyle.getAxisHeight());
			}

			vecLeftPoint.setX(iPosition * fAxisSpacing);
			vecRightPoint.setX((iPosition + 1) * fAxisSpacing);

			// Vec3f vecCompareLine = vecLeftPoint.minus(vecRightPoint);
			Vec3f vecCompareLine = vecRightPoint.minus(vecLeftPoint);
			float fCompareAngle = getAngle(vecCompareLine, vecCenterLine);

			if (fCompareAngle > fCurrentAngle || fCompareAngle < -fCurrentAngle)
			// !(fCompareAngle < fAngle && fCompareAngle < -fAngle))
			{
				// contentSelectionManager.addToType(EViewInternalSelectionType
				// .DESELECTED, iCurrent);
				alIsAngleBlocking.get(0).add(iCurrent);
			}
			// else
			// {
			// // TODO combinations
			// //contentSelectionManager.addToType(EViewInternalSelectionType.
			// NORMAL, iCurrent);
			// }

		}

		if (pickingTriggerMouseAdapter.wasMouseReleased())
		{
			bIsAngularDraggingActive = false;
			// bIsAngularBrushingActive = false;
		}

		// gl.glBegin(GL.GL_POLYGON);
		// double x;
		// double y;
		// for (double a=0; a<360; a+=1)
		// {
		// x = 0.2 * (Math.cos(a)) + vecTriangleLimit.x();
		// y = 0.2 * (Math.sin(a)) + vecTriangleLimit.y();
		// gl.glVertex2d(x, y);
		// } gl.glEnd();
		// GLHelperFunctions.drawPointAt(gl, vecUpperPoint);
		// GLHelperFunctions.drawPointAt(gl, vecLowerLine)

		// GLU glu = new GLU();
		// GLUnurbs theNurb = glu.gluNewNurbsRenderer();
		// glu.gluBeginCurve(theNurb);
		// //glu.gluNurbsCurve(theNurb, arg1, arg2, arg3, arg4, arg5, arg6)
		// glu.gluEndCurve(theNurb);

	}

	private float getAngle(final Vec3f vecOne, final Vec3f vecTwo)
	{

		Vec3f vecNewOne = vecOne.copy();
		Vec3f vecNewTwo = vecTwo.copy();

		vecNewOne.normalize();
		vecNewTwo.normalize();
		float fTmp = vecNewOne.dot(vecNewTwo);
		return (float) Math.acos(fTmp);
	}

}
