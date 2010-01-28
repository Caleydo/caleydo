package org.caleydo.view.scatterplot;

import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.getDecimalFormat;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.AXIS_MARKER_WIDTH;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.AXIS_Z;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.NR_TEXTURES;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.NR_TEXTURESX;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.NR_TEXTURESY;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.NUMBER_AXIS_MARKERS;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.POINTSIZE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.POINTSTYLE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.XLABELDISTANCE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.XLABELROTATIONNAGLE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.XYAXISDISTANCE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.X_AXIS_COLOR;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.X_AXIS_LINE_WIDTH;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.YLABELDISTANCE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.YLABELROTATIONNAGLE;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.Y_AXIS_COLOR;
import static org.caleydo.view.scatterplot.renderstyle.ScatterPlotRenderStyle.Y_AXIS_LINE_WIDTH;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.nio.FloatBuffer;
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
import org.caleydo.core.manager.event.view.storagebased.InitAxisComboEvent;
import org.caleydo.core.manager.event.view.storagebased.ResetScatterSelectionEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.SetPointSizeEvent;
import org.caleydo.core.manager.event.view.storagebased.TogglePointTypeEvent;
import org.caleydo.core.manager.event.view.storagebased.XAxisSelectorEvent;
import org.caleydo.core.manager.event.view.storagebased.YAxisSelectorEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
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
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.AGLView.EBusyModeState;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.view.scatterplot.listener.GLScatterPlotKeyListener;
import org.caleydo.view.scatterplot.listener.ResetSelectionListener;
import org.caleydo.view.scatterplot.listener.SetPointSizeListener;
import org.caleydo.view.scatterplot.listener.TogglePointTypeListener;
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
@SuppressWarnings("unused")
public class GLScatterplot extends AStorageBasedView {

	public final static String VIEW_ID = "org.caleydo.view.scatterplot";

	private ScatterPlotRenderStyle renderStyle;

	private ColorMapping colorMapper;

	// private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;
	// private EIDType eStorageDataType = EIDType.EXPERIMENT_INDEX;

	private Vec3f vecTranslation;

	private float fAnimationTranslation = 0;

	private ArrayList<Float> fAlXDistances;

	boolean bUseDetailLevel = true;

	private boolean bUpdateSelection = false;
	private boolean bUpdateAll = false;
	private boolean bRender2Axis = false;

	int iCurrentMouseOverElement = -1;

	public static int SELECTED_X_AXIS = 0;
	public static int SELECTED_Y_AXIS = 1;
	public static int SELECTED_X_AXIS_2 = 2;
	public static int SELECTED_Y_AXIS_2 = 3;

	// listeners

	private TogglePointTypeListener togglePointTypeListener;
	private ResetSelectionListener resetSelectionListener;

	private SetPointSizeListener setPointSizeListener;
	private XAxisSelectorListener xAxisSelectorListener;
	private YAxisSelectorListener yAxisSelectorListener;

	// Selections

	private SelectionManager elementSelectionManager;
	private SelectionManager mouseoverSelectionManager;
	private SelectionManager axisSelectionManager;

	// Brushes

	private float[] fRectangleDragStartPoint = new float[3];
	private float[] fRectangleDragEndPoint = new float[3];
	private boolean bRectangleSelection = false;

	// DIsplaylists
	private int iGLDisplayListIndexBrush;
	private int iGLDisplayListIndexCoord;
	private int iGLDisplayListIndexMouseOver;
	private int iGLDisplayListIndexSelection;

	// Textures
	private int iSamplesPerTexture = 0; // TODO not used yet, remove completly probably
	private final static int MAX_SAMPLES_PER_TEXTURE = 2000; // TODO not used yet, remove completly probably
	// array of textures for holding the data samples
	
	 
	private ArrayList<Texture> AlTextures = new ArrayList<Texture>();
	private ArrayList<Integer> iAlNumberSamples = new ArrayList<Integer>();
	private boolean bRedrawTextures = false;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLScatterplot(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum);
		viewType = GLScatterplot.VIEW_ID;

		// ArrayList<ESelectionType> alSelectionTypes = new
		// ArrayList<ESelectionType>();
		// alSelectionTypes.add(ESelectionType.NORMAL);
		// alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		// alSelectionTypes.add(ESelectionType.SELECTION);

		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		fAlXDistances = new ArrayList<Float>();

		glKeyListener = new GLScatterPlotKeyListener(this);
	}

	private void calculateTextures() {

		AlTextures.clear();
		iAlNumberSamples.clear();
		

		Texture tempTextur = null;

		for (int i = 0; i < NR_TEXTURES; i++) {

			AlTextures.add(tempTextur);
			iAlNumberSamples.add(iSamplesPerTexture);

		}
	}
	
	
	private void renderTextures(GL gl) {
		float fHeight;
		float fWidth;
		float fyOffset = 0.0f;
		float fxOffset = 0.0f;

		
		int size = AlTextures.size();		
		initTextures();			
		size = AlTextures.size();
		
		fHeight = viewFrustum.getHeight();
		fWidth = viewFrustum.getWidth();

		float fHeightElem = fHeight; // / iNumberOfElements;

		float fStepY = fHeight / (float)(NR_TEXTURESY+1);
		float fStepX = fWidth / (float)(NR_TEXTURESX+1);
		
		float fSpacerX = fStepX / (float)(NR_TEXTURESX+1);
		float fSpacerY = fStepY /(float)(NR_TEXTURESX+1);
		
		//fyOffset=fWidth/2;

		gl.glColor4f(1f, 1f, 1f, 1f);
		int icounter=0;

		for (int i = 0; i < NR_TEXTURESX; i++) {
			for (int j = 0; j < NR_TEXTURESY; j++) {

			//fStep = fHeightElem * iAlNumberSamples.get(iNrTextures - i - 1);

			AlTextures.get(NR_TEXTURES - icounter - 1).enable();
			AlTextures.get(NR_TEXTURES - icounter - 1).bind();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
					GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
					GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
					GL.GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
					GL.GL_NEAREST);
			TextureCoords texCoords = AlTextures.get(NR_TEXTURES - i - 1)
					.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_TEXTURE_SELECTION, NR_TEXTURES
							- i));
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2d(texCoords.left(), texCoords.top());
			gl.glVertex3f(fxOffset, fyOffset, 0);
			gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fxOffset, fyOffset + fStepY, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fxOffset+ fStepX, fyOffset + fStepY, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.top());
			gl.glVertex3f(fxOffset+ fStepX, fyOffset, 0);
			gl.glEnd();
			gl.glPopName();

			fyOffset += fStepY+fSpacerY;
			
			AlTextures.get(NR_TEXTURES - icounter - 1).disable();
			icounter++;
			}
			fyOffset =0;
			fxOffset += fStepX+fSpacerX;
		}
	}
	

	/**
	 * Init textures, build array of textures used for holding the whole
	 * examples
	 * 
	 * @param gl
	 */
	private void initTextures() {
				
		int ix = 0;
		int iy = 0;
		float xnormalized = 0.0f;
		float ynormalized = 0.0f;
		float[] fArRgbaWhite = { 1.0f, 1.0f,1.0f, 
				1.0f }; //OPACY		
		float fOpacity = 1.0f;
		
		//iSamplesPerTexture = (int) Math.ceil((double) iTextureSize
		//		/ iNrTextures);
							
		
		//ScatterPlotRenderStyle.setTextureNr(NR_TEXTURESX,NR_TEXTURESY);
		ScatterPlotRenderStyle.setTextureNr(100,100);
		
		int StartindexX=0; //TODO Make this adjustable
		int StartindexY=0;
		int EndindexX=StartindexX+NR_TEXTURESX-1;
		int EndindexY=StartindexY+NR_TEXTURESY-1;
					
		if (EndindexX>=storageVA.size())
		{
			EndindexX=storageVA.size()-1;
			ScatterPlotRenderStyle.setTextureNr(EndindexX-StartindexX+1,NR_TEXTURESY);
		}
		if (EndindexY>=storageVA.size())
		{
			EndindexY=storageVA.size()-1;
			ScatterPlotRenderStyle.setTextureNr(NR_TEXTURESX,EndindexY-StartindexY+1);
		}	
		
		int iTextureHeight = (int)(1000.0f / (double)NR_TEXTURESY);
		int iTextureWidth = (int)(1000.0f / (double)NR_TEXTURESX);
		int TextureSize= iTextureWidth* iTextureHeight ;

		FloatBuffer FbTemp = BufferUtil.newFloatBuffer(TextureSize*4);		
		calculateTextures();
		AlTextures.clear();
		iAlNumberSamples.clear();

		Texture tempTextur;
											
		for (Integer iAxisX=StartindexX;iAxisX<=EndindexX;iAxisX++)
		{
			for (Integer iAxisY=StartindexY;iAxisY<=EndindexY;iAxisY++)
			{
						
					
				for (Integer i=0;i<TextureSize;i++)
				{
						FbTemp.put(fArRgbaWhite);
				}
				
				int maxX=0;
				int maxY=0;
				
				for (Integer iContentIndex : contentVA) {
					
					int current_SELECTED_X_AXIS=iAxisX;
					int current_SELECTED_Y_AXIS=iAxisY;
					
						xnormalized = set.get(current_SELECTED_X_AXIS).getFloat(
								EDataRepresentation.NORMALIZED, iContentIndex);
						ynormalized = set.get(current_SELECTED_Y_AXIS).getFloat(
								EDataRepresentation.NORMALIZED, iContentIndex);
			
						ix = (int) Math.floor(xnormalized * (double)(iTextureWidth-1));
						iy = ix* (iTextureWidth)*4+
							(int) Math.floor(ynormalized * (double)(iTextureHeight-1))*4;
						
					
														
						float[] fArMappingColor = colorMapper.getColor(Math.max(
								xnormalized, ynormalized));
			
			//			float[] fArRgba = { fArMappingColor[0], fArMappingColor[1],
			//					fArMappingColor[2], fOpacity };
			
						if(ix>maxX) maxX=ix;
						if(iy>maxY) maxY=iy;
						
						if(iy>=TextureSize*4-4)
						{
							iy=0; // TODO : DIRTY HACK CAUSE INIDICES ARE WRONG!
						}
						
			
						
						FbTemp.put(iy,fArMappingColor[0]);
						FbTemp.put(iy+1,fArMappingColor[1]);
						FbTemp.put(iy+2,fArMappingColor[2]);
						FbTemp.put(iy+3,fOpacity);
						
					
				}
				
				maxX=maxX;
				maxY=maxY;
				
				FbTemp.rewind();
			
				TextureData texData = new TextureData(
						GL.GL_RGBA /* internalFormat */,
						iTextureWidth /* height */, iTextureHeight /* width */,
						0 /* border */, GL.GL_RGBA /* pixelFormat */,
						GL.GL_FLOAT /* pixelType */, false /* mipmap */,
						false /* dataIsCompressed */, true /* mustFlipVertically */,
						FbTemp, null);
			
				tempTextur = TextureIO.newTexture(0);
				tempTextur.updateImage(texData);
			
				AlTextures.add(tempTextur);
		
			}
		}			
	}

	@Override
	public void init(GL gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new ScatterPlotRenderStyle(this, viewFrustum);

		super.renderStyle = renderStyle;

		InitAxisComboEvent initAxisComboEvent = new InitAxisComboEvent();
		initAxisComboEvent.setSender(this);
		initAxisComboEvent.setAxisNames(this.getAxisString());
		GeneralManager.get().getEventPublisher().triggerEvent(
				initAxisComboEvent);
		// detailLevel = EDetailLevel.LOW;
		detailLevel = EDetailLevel.HIGH;

	}

	@Override
	public void initLocal(GL gl) {
		bRenderStorageHorizontally = false;

		// // Register keyboard listener to GL canvas
		// GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new
		// Runnable() {
		// public void run() {
		// parentGLCanvas.getParentComposite().addKeyListener(glKeyListener);
		// }
		// });

		iGLDisplayListIndexLocal = gl.glGenLists(5);
		iGLDisplayListIndexBrush = 2;
		iGLDisplayListIndexCoord = 3;
		iGLDisplayListIndexMouseOver = 4;
		iGLDisplayListIndexSelection = 5;

		// Register keyboard listener to GL canvas
		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(
				new Runnable() {
					public void run() {
						parentGLCanvas.getParentComposite().addKeyListener(
								glKeyListener);
					}
				});

		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);
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

		if (set == null)
			return;
		//
		// if (bIsTranslationAnimationActive) {
		// doTranslation();
		// }

		// pickingManager.getHits(this, EPickingType.SCATTER_POINT_SELECTION);

		// ArrayList<Pick> alHits = null;
		//
		// alHits = pickingManager.getHits(iUniqueID,
		// EPickingType.SCATTER_POINT_SELECTION);

		// if (alHits == null && alHits.size() == 0) {

		if (detailLevel == EDetailLevel.HIGH) {
			GLMouseListener glMouseListener = getParentGLCanvas()
					.getGLMouseListener();

			if (glMouseListener.wasMouseDragged()) {
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

				gl.glNewList(iGLDisplayListIndexBrush, GL.GL_COMPILE);
				DrawRectangularSelection(gl);
				gl.glEndList();
			}
			if (glMouseListener.wasMouseReleased() && bRectangleSelection) {
				bRectangleSelection = false;
				setDisplayListDirty();
				UpdateSelection();
				gl.glDeleteLists(iGLDisplayListIndexBrush, 1);
				bUpdateSelection = true;
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
		processEvents();

		// GLHelperFunctions.drawAxis(gl);
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		// gl.glEnable(GL.GL_DEPTH_TEST);
		// clipToFrustum(gl);

		if (detailLevel == EDetailLevel.LOW) 
		{
			renderTextures(gl);			
			return;
		}
		
		gl.glCallList(iGLDisplayListToCall);
		if (detailLevel == EDetailLevel.HIGH) {
			gl.glCallList(iGLDisplayListIndexBrush);
			gl.glCallList(iGLDisplayListIndexCoord);
			gl.glCallList(iGLDisplayListIndexMouseOver);

		}
		gl.glCallList(iGLDisplayListIndexSelection);
		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// if (!isRenderedRemote())
		// contextMenu.render(gl, this);
	}

	private void buildDisplayListSelection(final GL gl, int iGLDisplayListIndex) {

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		gl.glTranslatef(XYAXISDISTANCE, XYAXISDISTANCE, 0);
		RenderSelection(gl);
		gl.glTranslatef(-XYAXISDISTANCE, -XYAXISDISTANCE, 0);
		gl.glEndList();
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
			bUpdateAll = true;
		}

		// TODO remove l8ter when Keylistener or Toolbar working again
		// bRender2Axis=false;
		//		
		// SELECTED_X_AXIS = 0;
		// SELECTED_Y_AXIS = 1;
		// SELECTED_X_AXIS_2 = 0;
		// SELECTED_Y_AXIS_2 = 2;

		if (bUpdateSelection || bUpdateAll) {
			bUpdateSelection = false;
			buildDisplayListSelection(gl, iGLDisplayListIndexSelection);
		}

		if (bUpdateAll) {
			if (detailLevel == EDetailLevel.HIGH) {
				gl.glNewList(iGLDisplayListIndexCoord, GL.GL_COMPILE);
				renderCoordinateSystem(gl);
				gl.glEndList();
			}

			gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
			gl.glTranslatef(XYAXISDISTANCE, XYAXISDISTANCE, 0);
			RenderScatterPoints(gl);
			gl.glTranslatef(-XYAXISDISTANCE, -XYAXISDISTANCE, 0);
			gl.glEndList();

			bUpdateAll = false;
		}

		gl.glNewList(iGLDisplayListIndexMouseOver, GL.GL_COMPILE);
		gl.glTranslatef(XYAXISDISTANCE, XYAXISDISTANCE, 0);
		RenderMouseOver(gl);
		gl.glTranslatef(-XYAXISDISTANCE, -XYAXISDISTANCE, 0);
		gl.glEndList();

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

				Rectangle2D bounds = textRenderer.getScaledBounds(gl,
						getDecimalFormat().format(fNumber), renderStyle
								.getSmallFontScalingFactor(),
						ScatterPlotRenderStyle.MIN_NUMBER_TEXT_SIZE);
				float fWidth = (float) bounds.getWidth();
				float fHeight = (float) bounds.getHeight();
				float fHeightHalf = fHeight / 2.0f;
				float fWidthHalf = fWidth / 2.0f;

				renderNumber(gl, getDecimalFormat().format(fNumber), fXPosition
						- fWidth - AXIS_MARKER_WIDTH, fCurrentHeight
						- fHeightHalf + XYAXISDISTANCE);

				renderNumber(gl, getDecimalFormat().format(fNumber),
						fCurrentWidth - fWidthHalf + XYAXISDISTANCE, fYPosition
								- AXIS_MARKER_WIDTH - fHeight);

			}

			gl.glColor4fv(X_AXIS_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fCurrentWidth + XYAXISDISTANCE, fYPosition
					- AXIS_MARKER_WIDTH, AXIS_Z);
			gl.glVertex3f(fCurrentWidth + XYAXISDISTANCE, fYPosition
					+ AXIS_MARKER_WIDTH, AXIS_Z);
			gl.glEnd();

			gl.glColor4fv(Y_AXIS_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fXPosition - AXIS_MARKER_WIDTH, fCurrentHeight
					+ XYAXISDISTANCE, AXIS_Z);
			gl.glVertex3f(fXPosition + AXIS_MARKER_WIDTH, fCurrentHeight
					+ XYAXISDISTANCE, AXIS_Z);
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

		// sAxisLabel
		// ="Y-Achse: "+set.get(2).getLabel()+" (O) / "+set.get(3).getLabel()+" (X)";
		textRenderer.draw3D(gl, sAxisLabel, 0, 0, 0, fScaling,
				ScatterPlotRenderStyle.MIN_AXIS_LABEL_TEXT_SIZE);
		textRenderer.end3DRendering();

		// gl.glRotatef(-YLABELROTATIONNAGLE, 0, 0, 1);

		gl.glRotatef(-YLABELROTATIONNAGLE, 0, 0, 1);
		gl.glTranslatef(-YLABELDISTANCE, -renderStyle.getLabelHeight(), 0);
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

	private String[] getAxisString() {
		String[] tmpString = new String[storageVA.size()];
		for (Integer iStorageIndex : storageVA) {

			tmpString[iStorageIndex] = set.get(iStorageIndex).getLabel();

		}
		return tmpString;
	}

	private void RenderScatterPoints(GL gl) {

		float XScale = renderStyle.getRenderWidth() - XYAXISDISTANCE * 2.0f;
		float YScale = renderStyle.getRenderHeight() - XYAXISDISTANCE * 2.0f;
		float x = 0.0f;
		float y = 0.0f;
		float xnormalized = 0.0f;
		float ynormalized = 0.0f;

		float x_2 = 0.0f;
		float y_2 = 0.0f;

		if (detailLevel != EDetailLevel.HIGH) {
			bRender2Axis = false;
			POINTSTYLE = EScatterPointType.POINT;
		}

		for (Integer iContentIndex : contentVA) {

			if (iContentIndex == -1) {
				// throw new
				// IllegalStateException("No such element in virtual array");
				// TODO this shouldn't happen here.
				continue;
			}

			xnormalized = set.get(SELECTED_X_AXIS).getFloat(
					EDataRepresentation.NORMALIZED, iContentIndex);
			ynormalized = set.get(SELECTED_Y_AXIS).getFloat(
					EDataRepresentation.NORMALIZED, iContentIndex);

			x = xnormalized * XScale;
			y = ynormalized * YScale;
			float[] fArMappingColor = colorMapper.getColor(Math.max(
					xnormalized, ynormalized));
			EScatterPointType tmpPoint = POINTSTYLE;
			if (bRender2Axis) {
				fArMappingColor = new float[]{1.0f, 0.0f, 0.0f};
				POINTSTYLE = EScatterPointType.POINT;
			}

			DrawPointPrimitive(gl, x, y, 0.0f, // z
					fArMappingColor, 1.0f,// fOpacity
					iContentIndex, 1.0f); // scale

			if (bRender2Axis) {
				xnormalized = set.get(SELECTED_X_AXIS_2).getFloat(
						EDataRepresentation.NORMALIZED, iContentIndex);
				ynormalized = set.get(SELECTED_Y_AXIS_2).getFloat(
						EDataRepresentation.NORMALIZED, iContentIndex);
				x_2 = xnormalized * XScale;
				y_2 = ynormalized * YScale;
				fArMappingColor = new float[]{0.0f, 1.0f, 0.0f};

				DrawPointPrimitive(gl, x_2, y_2, 0.0f, // z
						fArMappingColor, 1.0f,// fOpacity
						iContentIndex, 1.0f); // scale

				POINTSTYLE = tmpPoint;

				gl.glColor3f(0.0f, 0.0f, 1.0f);
				gl.glLineWidth(0.5f);
				gl.glBegin(GL.GL_LINES);
				// gl.glBegin(GL.GL_POLYGON);
				gl.glVertex3f(x, y, 1.0f);
				gl.glVertex3f(x_2, y_2, 1.0f);
				gl.glEnd();

			}
		}
	}

	private void RenderMouseOver(GL gl) {

		if (mouseoverSelectionManager
				.getNumberOfElements(ESelectionType.MOUSE_OVER) == 0)
			return;

		Set<Integer> mouseOver = mouseoverSelectionManager
				.getElements(ESelectionType.MOUSE_OVER);
		int iContentIndex = 0;
		for (int i : mouseOver) {
			iContentIndex = i;
			break;
		}

		float XScale = renderStyle.getRenderWidth() - XYAXISDISTANCE * 2.0f;
		float YScale = renderStyle.getRenderHeight() - XYAXISDISTANCE * 2.0f;

		float xnormalized = set.get(SELECTED_X_AXIS).getFloat(
				EDataRepresentation.NORMALIZED, iContentIndex);
		float ynormalized = set.get(SELECTED_Y_AXIS).getFloat(
				EDataRepresentation.NORMALIZED, iContentIndex);

		float x = xnormalized * XScale;
		float y = ynormalized * YScale;
		float[] fArMappingColor = colorMapper.getColor(Math.max(xnormalized,
				ynormalized));
		if (elementSelectionManager.checkStatus(ESelectionType.SELECTION,
				iContentIndex))
			fArMappingColor = GeneralRenderStyle.MOUSE_OVER_COLOR;

		float z = +1.5f;
		float fullPoint = POINTSIZE * 2f;
		gl.glColor3f(1.0f, 1.0f, 0.0f);

		float angle;
		float PI = (float) Math.PI;

		gl.glBegin(GL.GL_POLYGON);
		for (int i = 0; i < 20; i++) {
			angle = (i * 2 * PI) / 10;
			gl.glVertex3f(x + (float) (Math.cos(angle) * fullPoint), y
					+ (float) (Math.sin(angle) * fullPoint), z);
		}
		gl.glEnd();
		z = +2.0f;
		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glPointSize(POINTSIZE * 50.0f);
		gl.glBegin(GL.GL_POINTS);
		gl.glVertex3f(x, y, z);
		gl.glEnd();
		z = +2.5f;

		gl
				.glColor3f(fArMappingColor[0], fArMappingColor[1],
						fArMappingColor[2]);

		DrawPointPrimitive(gl, x, y, z, // z
				fArMappingColor, 1.0f, iContentIndex, 2.0f); // fOpacity

		DrawMouseOverLabel(gl, x, y, z, // z
				fArMappingColor, 1.0f, iContentIndex); // fOpacity

	}

	private void DrawMouseOverLabel(GL gl, float x, float y, float z,
			float[] fArMappingColor, float fOpacity, int iContentIndex) {

		textRenderer.setColor(0, 0, 0, 1);

		// z = z + 3.0f;
		x = x + 0.1f;
		gl.glTranslatef(x, y, z);

		String sLabel = "";

		String genLabel = idMappingManager.getID(EIDType.EXPRESSION_INDEX,
				EIDType.GENE_SYMBOL, iContentIndex);

		if (genLabel.equals(""))
			genLabel = "Unkonwn Gene";

		if (elementSelectionManager.checkStatus(ESelectionType.SELECTION,
				iContentIndex))
			sLabel = "Selected Point ("
					+ genLabel
					+ "):"
					+ +set.get(SELECTED_X_AXIS).getFloat(
							EDataRepresentation.RAW, iContentIndex)
					+ " / "
					+ set.get(SELECTED_Y_AXIS).getFloat(
							EDataRepresentation.RAW, iContentIndex);
		else
			sLabel = "Point ("
					+ genLabel
					+ "):"
					+ +set.get(SELECTED_X_AXIS).getFloat(
							EDataRepresentation.RAW, iContentIndex)
					+ " / "
					+ set.get(SELECTED_Y_AXIS).getFloat(
							EDataRepresentation.RAW, iContentIndex);

		// sLabel="Point :: ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 1234567890 //// ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 1234567890 //// ";

		float fScaling = renderStyle.getSmallFontScalingFactor();
		if (isRenderedRemote())
			fScaling *= 1.5f;

		Rectangle2D bounds = textRenderer.getScaledBounds(gl, sLabel, fScaling,
				ScatterPlotRenderStyle.MIN_NUMBER_TEXT_SIZE);

		float boxLengh = (float) bounds.getWidth() + 0.2f;
		float boxHight = (float) bounds.getHeight();

		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(0.0f, -0.02f, -0.1f);
		gl.glVertex3f(0.0f, boxHight, -0.1f);
		gl.glVertex3f(boxLengh, boxHight, -0.1f);
		gl.glVertex3f(boxLengh, -0.02f, -0.1f);
		gl.glEnd();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(gl, sLabel, 0, 0,
				ScatterPlotRenderStyle.TEXT_ON_LABEL_Z, fScaling,
				ScatterPlotRenderStyle.MIN_AXIS_LABEL_TEXT_SIZE);
		textRenderer.end3DRendering();
		gl.glPopAttrib();

		gl.glTranslatef(-x, -y, -z);

	}

	private boolean IsInSelectionRectangle(float x, float y) {
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

		if (x >= XMin && x <= XMax)
			if (y >= YMin && y <= YMax)
				return true;

		return false;
	}

	private void UpdateSelection() {

		float XScale = renderStyle.getRenderWidth() - XYAXISDISTANCE * 2.0f;
		float YScale = renderStyle.getRenderHeight() - XYAXISDISTANCE * 2.0f;
		float x = 0.0f;
		float y = 0.0f;

		for (Integer iContentIndex : contentVA) {

			if (iContentIndex == -1) {
				// throw new
				// IllegalStateException("No such element in virtual array");
				// TODO this shouldn't happen here.
				continue;
			}
			float xnormalized = set.get(SELECTED_X_AXIS).getFloat(
					EDataRepresentation.NORMALIZED, iContentIndex);
			float ynormalized = set.get(SELECTED_Y_AXIS).getFloat(
					EDataRepresentation.NORMALIZED, iContentIndex);

			x = xnormalized * XScale;
			y = ynormalized * YScale;

			if (IsInSelectionRectangle(x, y)) {
				if (!elementSelectionManager.checkStatus(iContentIndex))
					elementSelectionManager.add(iContentIndex);
				elementSelectionManager.addToType(ESelectionType.SELECTION,
						iContentIndex);
			}
		}
	}

	private void RenderSelection(GL gl) {

		if (elementSelectionManager
				.getNumberOfElements(ESelectionType.SELECTION) == 0)
			return;

		float XScale = renderStyle.getRenderWidth() - XYAXISDISTANCE * 2.0f;
		float YScale = renderStyle.getRenderHeight() - XYAXISDISTANCE * 2.0f;

		Set<Integer> selectionSet = elementSelectionManager
				.getElements(ESelectionType.SELECTION);

		float x = 0.0f;
		float y = 0.0f;
		float z = 1.0f;

		float[] fArMappingColor = new float[]{1.0f, 0.1f, 0.5f};

		for (int iContentIndex : selectionSet) {
			float xnormalized = set.get(SELECTED_X_AXIS).getFloat(
					EDataRepresentation.NORMALIZED, iContentIndex);
			float ynormalized = set.get(SELECTED_Y_AXIS).getFloat(
					EDataRepresentation.NORMALIZED, iContentIndex);

			x = xnormalized * XScale;
			y = ynormalized * YScale;

			DrawPointPrimitive(gl, x, y, z, // z
					fArMappingColor, 1.0f,// fOpacity
					iContentIndex, 1.0f); // scale
		}
	}

	private void DrawPointPrimitive(GL gl, float x, float y, float z,
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
			case BOX : {
				gl.glBegin(GL.GL_POLYGON);
				gl.glVertex3f(x - halfPoint, y - halfPoint, z);
				gl.glVertex3f(x - halfPoint, y + halfPoint, z);
				gl.glVertex3f(x + halfPoint, y + halfPoint, z);
				gl.glVertex3f(x + halfPoint, y - halfPoint, z);
				gl.glEnd();
				break;
			}
			case POINT : {
				gl.glPointSize(fullPoint * 50.0f);
				gl.glBegin(GL.GL_POINTS);
				gl.glVertex3f(x, y, z);
				gl.glEnd();
				break;
			}
			case CROSS : {
				gl.glLineWidth(1.0f);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(x - halfPoint, y - halfPoint, z);
				gl.glVertex3f(x + halfPoint, y + halfPoint, z);
				gl.glVertex3f(x - halfPoint, y + halfPoint, z);
				gl.glVertex3f(x + halfPoint, y - halfPoint, z);
				gl.glEnd();
			}
				break;
			case CIRCLE : {
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
			case DISK : {
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
			default :

		}
		gl.glPopName();
	}

	private void DrawRectangularSelection(GL gl) {

		float length = fRectangleDragEndPoint[0] - fRectangleDragStartPoint[0];
		float hight = fRectangleDragEndPoint[1] - fRectangleDragStartPoint[1];
		float x = fRectangleDragStartPoint[0];
		float y = fRectangleDragStartPoint[1];
		float z = 3.5f;

		gl.glColor3f(0.0f, 1.0f, 0.0f);
		gl.glLineWidth(2.0f);
		gl.glBegin(GL.GL_LINE_LOOP);
		// gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(x, y, z);
		gl.glVertex3f(x, y + hight, z);
		gl.glVertex3f(x + length, y + hight, z);
		gl.glVertex3f(x + length, y, z);
		gl.glEnd();
	}

	public void togglePointType() {

		switch (POINTSTYLE) {
			case POINT :
				POINTSTYLE = EScatterPointType.BOX;
				break;
			case BOX :
				POINTSTYLE = EScatterPointType.CIRCLE;
				break;
			case CIRCLE :
				POINTSTYLE = EScatterPointType.DISK;
				break;
			case DISK :
				POINTSTYLE = EScatterPointType.CROSS;
				break;
			case CROSS :
				POINTSTYLE = EScatterPointType.POINT;
				break;
			default :
		}
		bUpdateAll = true;
		setDisplayListDirty();
	}

	/**
	 * Render the symbol of the view instead of the view
	 * 
	 * @param gl
	 */
	// private void renderSymbol(GL gl) {
	// float fXButtonOrigin = 0.33f * renderStyle.getScaling();
	// float fYButtonOrigin = 0.33f * renderStyle.getScaling();
	// Texture tempTexture = textureManager.getIconTexture(gl,
	// EIconTextures.HEAT_MAP_SYMBOL);
	// tempTexture.enable();
	// tempTexture.bind();
	//
	// TextureCoords texCoords = tempTexture.getImageTexCoords();
	//
	// gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
	// gl.glColor4f(1f, 1, 1, 1f);
	// gl.glBegin(GL.GL_POLYGON);
	//
	// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
	// gl.glVertex3f(fXButtonOrigin, fYButtonOrigin, 0.01f);
	// gl.glTexCoord2f(texCoords.left(), texCoords.top());
	// gl.glVertex3f(fXButtonOrigin, 2 * fYButtonOrigin, 0.01f);
	// gl.glTexCoord2f(texCoords.right(), texCoords.top());
	// gl.glVertex3f(fXButtonOrigin * 2, 2 * fYButtonOrigin, 0.01f);
	// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
	// gl.glVertex3f(fXButtonOrigin * 2, fYButtonOrigin, 0.01f);
	// gl.glEnd();
	// gl.glPopAttrib();
	// tempTexture.disable();
	// }

	public void renderHorizontally(boolean bRenderStorageHorizontally) {

		this.bRenderStorageHorizontally = bRenderStorageHorizontally;
		// renderStyle.setBRenderStorageHorizontally(bRenderStorageHorizontally);
		setDisplayListDirty();
	}

	public void selectAxesfromExternal() {

		if (axisSelectionManager.getNumberOfElements(ESelectionType.SELECTION) == 0) {

			Set<Integer> axis = axisSelectionManager
					.getElements(ESelectionType.SELECTION);

			for (int i : axis) {
				// TODO : If Multiple Selections or Scatterplots are Available.
				// adjust this (take first 2 axis selections?)
				SELECTED_X_AXIS = i;
				bUpdateAll = true;
				break;
			}
		}

		// TODO Remove l8ter, Mousover shouldnd select an Axis
		if (axisSelectionManager.getNumberOfElements(ESelectionType.MOUSE_OVER) == 0)
			return;

		Set<Integer> axis = axisSelectionManager
				.getElements(ESelectionType.MOUSE_OVER);

		for (int i : axis) {
			// TODO : If Multiple Selections or Scatterplots are Available.
			// adjust this (take first 2 axis selections?)
			SELECTED_Y_AXIS = i;
			bUpdateAll = true;
			break;
		}

	}

	public void selectNewAxes() {
		axisSelectionManager.clearSelection(ESelectionType.SELECTION);

		axisSelectionManager.addToType(ESelectionType.SELECTION,
				SELECTED_X_AXIS);
		axisSelectionManager.addToType(ESelectionType.SELECTION,
				SELECTED_Y_AXIS);

		ISelectionDelta selectionDelta = axisSelectionManager.getDelta();
		handleConnectedElementRep(selectionDelta);
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		event.setInfo(getShortInfo());
		eventPublisher.triggerEvent(event);
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

		// mouseoverSelectionManager = storageSelectionManager;
		mouseoverSelectionManager = new SelectionManager.Builder(
				EIDType.EXPRESSION_INDEX).build();
		elementSelectionManager = contentSelectionManager;
		mouseoverSelectionManager.setVA(contentVA);
		elementSelectionManager.setVA(contentVA);

		axisSelectionManager = storageSelectionManager;
		axisSelectionManager.setVA(storageVA);

		// mouseoverSelectionManager.initialAdd(iAlElementIDs)
		// mouseoverSelectionManager = contentSelectionManager;
		// TODO: Thats just for testing!
	}

	@Override
	public String getShortInfo() {
		if (contentVA == null)
			return "Scatterplot - 0 " + useCase.getContentLabel(false, true)
					+ " / 0 experiments";

		return "Scatterplot - " + contentVA.size() + " "
				+ useCase.getContentLabel(false, true) + " / "
				+ storageVA.size() + " experiments";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Scatter Plot\n");
		// TODO Everything

		if (bRenderStorageHorizontally) {
			sInfoText.append(contentVA.size() + " "
					+ useCase.getContentLabel(false, true) + " in columns and "
					+ storageVA.size() + " experiments in rows.\n");
		} else {
			sInfoText.append(contentVA.size() + " "
					+ useCase.getContentLabel(true, true) + " in rows and "
					+ storageVA.size() + " experiments in columns.\n");
		}

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

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		if (bRectangleSelection)
			return;

		ESelectionType eSelectionType;
		switch (ePickingType) {
			case SCATTER_POINT_SELECTION :
				iCurrentMouseOverElement = iExternalID;
				switch (pickingMode) {

					case CLICKED :
						eSelectionType = ESelectionType.SELECTION;
						break;
					case MOUSE_OVER :
						eSelectionType = ESelectionType.MOUSE_OVER;
						break;
					case RIGHT_CLICKED :
						eSelectionType = ESelectionType.DESELECTED;
						break;
					case DRAGGED :
						eSelectionType = ESelectionType.SELECTION;
						// break;
					default :
						return;

				}

				createContentSelection(eSelectionType, iExternalID);

				break;

		}
	}

	private void createContentSelection(ESelectionType selectionType,
			int contentID) {

		if (elementSelectionManager.checkStatus(ESelectionType.SELECTION,
				contentID)) {
			if (selectionType == ESelectionType.DESELECTED) {
				elementSelectionManager.removeFromType(
						ESelectionType.SELECTION, contentID);
				setDisplayListDirty();
				bUpdateSelection = true;
				return;
			}
		}

		SelectionCommand command = new SelectionCommand(
				ESelectionCommandType.CLEAR, selectionType);
		sendSelectionCommandEvent(EIDType.EXPRESSION_INDEX, command);
		mouseoverSelectionManager.clearSelection(selectionType);
		elementSelectionManager.clearSelection(selectionType);

		if (selectionType == ESelectionType.SELECTION) {
			// fDragStartPoint = new float[3];
			// fDragEndPoint = new float[3];
			if (!elementSelectionManager.checkStatus(contentID))
				elementSelectionManager.add(contentID);

			elementSelectionManager.addToType(selectionType, contentID);

			bUpdateSelection = true;
			// return;
		}

		if ((selectionType == ESelectionType.MOUSE_OVER))
		// && (!mouseoverSelectionManager.checkStatus(contentID)))
		{
			// mouseoverSelectionManager.resetSelectionManager(); // This may be
			// not necessary;
			mouseoverSelectionManager.add(contentID);
			mouseoverSelectionManager.addToType(selectionType, contentID);
		}

		ISelectionDelta selectionDelta = mouseoverSelectionManager.getDelta();
		handleConnectedElementRep(selectionDelta);
		SelectionUpdateEvent event = new SelectionUpdateEvent();
		event.setSender(this);
		event.setSelectionDelta((SelectionDelta) selectionDelta);
		event.setInfo(getShortInfo());
		eventPublisher.triggerEvent(event);

		setDisplayListDirty();
	}

	public void ResetSelection() {
		elementSelectionManager.clearSelections();
		fRectangleDragStartPoint = new float[3];
		fRectangleDragEndPoint = new float[3];
		bUpdateAll = true;
		setDisplayListDirty();
	}

	@Override
	protected void reactOnExternalSelection(boolean scrollToSelection) {
		selectAxesfromExternal();
		bUpdateSelection = true;
		setDisplayListDirty();
		// UpdateMouseOverfromExternal();
	}

	@Override
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta) {
		// TODO
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext) {

		this.bRenderOnlyContext = bRenderOnlyContext;

		if (this.bRenderOnlyContext) {
			contentVA = useCase.getVA(EVAType.CONTENT_CONTEXT);
		} else {
			contentVA = useCase.getVA(EVAType.CONTENT);
		}

		contentSelectionManager.setVA(contentVA);
		// renderStyle.setActiveVirtualArray(iContentVAID);

		setDisplayListDirty();

	}

	@Override
	public void handleVirtualArrayUpdate(IVirtualArrayDelta delta, String info) {

		super.handleVirtualArrayUpdate(delta, info);

		if (delta.getVAType() == EVAType.CONTENT_CONTEXT
				&& contentVAType == EVAType.CONTENT_CONTEXT) {
			if (contentVA.size() == 0)
				return;
			// FIXME: this is only proof of concept - use the cluster manager
			// instead of affinity directly
			// long original = System.currentTimeMillis();
			// System.out.println("beginning clustering");
			AffinityClusterer clusterer = new AffinityClusterer(contentVA
					.size());
			ClusterState state = new ClusterState(
					EClustererAlgo.AFFINITY_PROPAGATION,
					EClustererType.GENE_CLUSTERING,
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
				// throw new
				// IllegalStateException("No such element in virtual array");
				// TODO this shouldn't happen here.
				continue;
			}

			float fXValue = fAlXDistances.get(iContentIndex); // +
																// renderStyle.getSelectedFieldWidth()
																// / 2;
			// float fYValue = 0;
			float fYValue = renderStyle.getYCenter();

			// Set<Integer> mouseOver =
			// storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);
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

			if (bRenderStorageHorizontally) {
				elementRep = new SelectedElementRep(EIDType.EXPRESSION_INDEX,
						iViewID, fXValue + fAnimationTranslation, fYValue, 0);

			} else {
				Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float) Math.PI / 2);
				Vec3f vecPoint = myRotf.rotateVector(new Vec3f(fXValue,
						fYValue, 0));
				vecPoint.setY(vecPoint.y() + vecTranslation.y());
				elementRep = new SelectedElementRep(EIDType.EXPRESSION_INDEX,
						iViewID, vecPoint.x(), vecPoint.y()
								- fAnimationTranslation, 0);

			}
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

		resetSelectionListener = new ResetSelectionListener();
		resetSelectionListener.setHandler(this);
		eventPublisher.addListener(ResetScatterSelectionEvent.class,
				resetSelectionListener);

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

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (togglePointTypeListener != null) {
			eventPublisher.removeListener(togglePointTypeListener);
			togglePointTypeListener = null;
		}

		if (resetSelectionListener != null) {
			eventPublisher.removeListener(resetSelectionListener);
			resetSelectionListener = null;
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
		if (SELECTED_X_AXIS != iAxisIndex) {
			SELECTED_X_AXIS = iAxisIndex;
			bUpdateAll = true;
			selectNewAxes();
			setDisplayListDirty();
		}

	}

	public void setYAxis(int iAxisIndex) {
		if (SELECTED_Y_AXIS != iAxisIndex) {
			SELECTED_Y_AXIS = iAxisIndex;
			bUpdateAll = true;
			selectNewAxes();
			setDisplayListDirty();
		}

	}

	public void setPointSize(int pointSize) {
		if (renderStyle.getPointSize() != pointSize) {
			renderStyle.setPointSize(pointSize);
			bUpdateAll = true;
			setDisplayListDirty();
		}
	}

	public void upDownSelect(boolean bDownIsTrue) {

		int tmpAxis = SELECTED_Y_AXIS;
		if (bDownIsTrue)
			tmpAxis++;
		else
			tmpAxis--;
		if (tmpAxis < 0)
			tmpAxis = 0;
		if ((tmpAxis + 1) >= storageVA.size())
			tmpAxis = SELECTED_Y_AXIS;
		SELECTED_Y_AXIS = tmpAxis;
		bUpdateAll = true;
		selectNewAxes();
		setDisplayListDirty();
	}

	public void leftRightSelect(boolean bRightIsTrue) {
		int tmpAxis = SELECTED_X_AXIS;
		if (bRightIsTrue)
			tmpAxis++;
		else
			tmpAxis--;
		if (tmpAxis < 0)
			tmpAxis = 0;
		if ((tmpAxis + 1) >= storageVA.size())
			tmpAxis = SELECTED_X_AXIS;
		SELECTED_X_AXIS = tmpAxis;
		bUpdateAll = true;
		selectNewAxes();
		setDisplayListDirty();
	}

	public void toggleSpecialAxisMode() {
		if (bRender2Axis)
			bRender2Axis = false;
		else
			bRender2Axis = true;
		bUpdateAll = true;
		setDisplayListDirty();
	}

	public void toggleDetailLevel() {
		if (detailLevel == EDetailLevel.HIGH)
		{			
			detailLevel = EDetailLevel.LOW;			
		}
		else
			detailLevel = EDetailLevel.HIGH;
		bUpdateAll = true;
		setDisplayListDirty();
	}

}
