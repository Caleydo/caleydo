package org.caleydo.core.view.opengl.canvas.storagebased;

import static org.caleydo.core.view.opengl.canvas.storagebased.HeatMapRenderStyle.SELECTION_Z;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_COLOR;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.data.selection.IGroupList;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.IVirtualArrayDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionCommandEventContainer;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.data.selection.VADeltaItem;
import org.caleydo.core.data.selection.VirtualArrayDelta;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IMediator;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.clusterer.HierarchyGraph;
import org.caleydo.core.util.clusterer.Node;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.AGLEventListener.EBusyModeState;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.PickingMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.GLIconTextureManager;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.IGraph;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

/**
 * Rendering the GLHierarchicalHeatMap with remote rendering support.
 * 
 * @author Bernhard Schlegl
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLHierarchicalHeatMap
	extends AStorageBasedView {
	private final static float GAP_LEVEL1_2 = 0.6f;
	private final static float GAP_LEVEL2_3 = 0.4f;

	// private final static float MAX_NUM_SAMPLES = 8f;

	private final static int MIN_SAMPLES_PER_HEATMAP = 14;
	private final static int MAX_SAMPLES_PER_HEATMAP = 100;

	private int iNumberOfElements = 0;

	private int iSamplesPerTexture = 0;

	private int iSamplesPerHeatmap = 0;

	// private HeatMapRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;
	private EIDType eExperimentDataType = EIDType.EXPERIMENT_INDEX;

	private GLIconTextureManager iconTextureManager;

	private ArrayList<Float> fAlXDistances;

	// for external selections of experiments
	private ArrayList<Integer> AlExpMouseOver = new ArrayList<Integer>();
	private ArrayList<Integer> AlExpSelected = new ArrayList<Integer>();

	// selector for texture in overviewBar
	private int iSelectorBar = 1;

	// number of partitions for selection in overViewBar
	private int iNrSelBar = 0;

	// array of textures for holding the data samples
	private ArrayList<Texture> AlTextures = new ArrayList<Texture>();
	private ArrayList<Integer> iAlNumberSamples = new ArrayList<Integer>();

	private Point PickingPoint = null;
	private int iPickedSample = 0;
	private int iFirstSample = 0;
	private int iLastSample = 0;
	private int iNrSamplesPerTexture = 0;

	private ArrayList<HeatMapSelection> AlSelection = new ArrayList<HeatMapSelection>();

	private boolean bRenderCaption;

	private float fAnimationScale = 1.0f;

	// embedded heat map
	private GLHeatMap glHeatMapView;
	private IMediator privateMediator;
	private boolean bIsHeatmapInFocus = false;

	private boolean bRedrawTextures = false;

	// dragging stuff
	private boolean bIsDraggingActive = false;
	private int iDraggedCursor = 0;
	private float fPosCursorFirstElement = 0;
	private float fPosCursorLastElement = 0;

	// clustering/grouping stuff
	private HierarchyGraph hierarchyGraph = null;
	private Node[] treeStructure = null;
	private DendrogramNode[] Nodes = null;
	private boolean bInitNodes = true;
	private EClustererAlgo eClustererAlgo = EClustererAlgo.COBWEB_CLUSTERER;
	private EClustererType eClustererType = EClustererType.GENE_CLUSTERING;

	private boolean bSplitGroupExp = false;
	private boolean bSplitGroupGene = false;
	private int iGroupToSplit = 0;
	private Point DraggingPoint = null;

	/**
	 * Constructor.
	 * 
	 * @param setType
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHierarchicalHeatMap(ESetType setType, final int iGLCanvasID, final String sLabel,
		final IViewFrustum viewFrustum) {
		super(setType, iGLCanvasID, sLabel, viewFrustum);
		viewType = EManagedObjectType.GL_HIER_HEAT_MAP;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager =
			new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX).externalIDType(
				EIDType.REFSEQ_MRNA_INT).mappingType(EMappingType.EXPRESSION_INDEX_2_REFSEQ_MRNA_INT,
				EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX).build();
		storageSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();

		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		fAlXDistances = new ArrayList<Float>();

		privateMediator = generalManager.getEventPublisher().getPrivateMediator();
		privateMediator.addSender(this);
		privateMediator.addReceiver(this);

		// activate clustering
		bUseClusteredVA = false;

	}

	@Override
	public void init(GL gl) {
		bRenderOnlyContext = false;
		// bUseRandomSampling = false;

		createHeatMap();

		glHeatMapView.initRemote(gl, getID(), pickingTriggerMouseAdapter, null);

		iconTextureManager = new GLIconTextureManager();
		initData();

		if (set == null)
			return;

		iNumberOfElements = set.getVA(iContentVAID).size();

		if (iNumberOfElements < 50) {
			throw new IllegalStateException("Number of elements not supported!!");
		}

		iSamplesPerTexture = (int) Math.floor(iNumberOfElements / 5);

		if (iSamplesPerTexture > 250)
			iSamplesPerTexture = 250;

		iSamplesPerHeatmap = (int) Math.floor(iSamplesPerTexture / 3);

		if (iSamplesPerHeatmap > MAX_SAMPLES_PER_HEATMAP)
			iSamplesPerTexture = 100;

		if (iSamplesPerHeatmap < MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = MIN_SAMPLES_PER_HEATMAP;

		initTextures(gl);
		initPosCursor();
	}

	@Override
	public void initLocal(GL gl) {

		generalManager.getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR, this);
		generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR, this);

		bRenderStorageHorizontally = false;

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);

	}

	@Override
	public void initRemote(GL gl, int remoteViewID, PickingMouseListener pickingTriggerMouseAdapter,
		IGLCanvasRemoteRendering remoteRenderingGLCanvas) {

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		bRenderStorageHorizontally = false;

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}

	private void setTexture() {
		boolean bSetCurrentTexture = true;

		if (AlSelection.size() > 0) {
			for (HeatMapSelection selection : AlSelection) {

				if (selection.getTexture() == iSelectorBar && selection.getPos() >= iFirstSample
					&& selection.getPos() <= iLastSample || selection.getTexture() == iSelectorBar - 1
					&& selection.getPos() >= iFirstSample && selection.getPos() <= iLastSample) {
					bSetCurrentTexture = false;
					break;
				}
			}
			if (bSetCurrentTexture) {
				iSelectorBar = AlSelection.get(0).getTexture() + 1;
				if (iSelectorBar == iNrSelBar) {
					iSelectorBar--;
				}
				initPosCursor();
			}
		}
	}

	/**
	 * Init (reset) the positions of cursors used for highlighting selected elements in stage 2 (texture)
	 * 
	 * @param
	 */
	private void initPosCursor() {
		if (AlSelection.size() > 0) {
			int iNumberSample = iNrSamplesPerTexture * 2;

			for (HeatMapSelection iter : AlSelection) {
				if (iter.getTexture() == iSelectorBar - 1) {
					iPickedSample = iter.getPos();

					if (iSamplesPerHeatmap % 2 == 0) {
						iFirstSample = iPickedSample - (int) Math.floor(iSamplesPerHeatmap / 2) + 1;
						iLastSample = iPickedSample + (int) Math.floor(iSamplesPerHeatmap / 2);
					}
					else {
						iFirstSample = iPickedSample - (int) Math.ceil(iSamplesPerHeatmap / 2);
						iLastSample = iPickedSample + (int) Math.floor(iSamplesPerHeatmap / 2);
					}

					if (iPickedSample < iSamplesPerHeatmap / 2) {
						iPickedSample = (int) Math.floor(iSamplesPerHeatmap / 2);
						iFirstSample = 0;
						iLastSample = iSamplesPerHeatmap - 1;
					}
					else if (iPickedSample > iNumberSample - 1 - iSamplesPerHeatmap / 2) {
						iPickedSample = (int) Math.ceil(iNumberSample - iSamplesPerHeatmap / 2);
						iLastSample = iNumberSample - 1;
						iFirstSample = iNumberSample - iSamplesPerHeatmap;
					}
					break;
				}
				else if (iter.getTexture() == iSelectorBar) {
					iPickedSample = iter.getPos() + iAlNumberSamples.get(iSelectorBar - 1);

					if (iSamplesPerHeatmap % 2 == 0) {
						iFirstSample = iPickedSample - (int) Math.floor(iSamplesPerHeatmap / 2) + 1;
						iLastSample = iPickedSample + (int) Math.floor(iSamplesPerHeatmap / 2);
					}
					else {
						iFirstSample = iPickedSample - (int) Math.ceil(iSamplesPerHeatmap / 2);
						iLastSample = iPickedSample + (int) Math.floor(iSamplesPerHeatmap / 2);
					}

					if (iPickedSample < iSamplesPerHeatmap / 2) {
						iPickedSample = (int) Math.floor(iSamplesPerHeatmap / 2);
						iFirstSample = 0;
						iLastSample = iSamplesPerHeatmap - 1;
					}
					else if (iPickedSample > iNumberSample - 1 - iSamplesPerHeatmap / 2) {
						iPickedSample = (int) Math.ceil(iNumberSample - iSamplesPerHeatmap / 2);
						iLastSample = iNumberSample - 1;
						iFirstSample = iNumberSample - iSamplesPerHeatmap;
					}
					break;
				}
				iPickedSample = (int) Math.floor(iSamplesPerHeatmap / 2);
				iFirstSample = 0;
				iLastSample = iSamplesPerHeatmap - 1;
			}
		}
		else {
			iPickedSample = (int) Math.floor(iSamplesPerHeatmap / 2);
			iFirstSample = 0;
			iLastSample = iSamplesPerHeatmap - 1;
		}
	}

	/**
	 * Init textures, build array of textures used for holding the whole examples from contentSelectionManager
	 * 
	 * @param
	 */
	private void initTextures(GL gl) {
		fAlXDistances.clear();
		// renderStyle.updateFieldSizes();

		iNrSelBar = (int) Math.ceil(set.getVA(iContentVAID).size() / iSamplesPerTexture);

		AlTextures.clear();
		iAlNumberSamples.clear();

		Texture tempTextur;

		int iTextureHeight = set.getVA(iContentVAID).size();
		int iTextureWidth = set.getVA(iStorageVAID).size();

		iNrSamplesPerTexture = (int) Math.floor(iTextureHeight / iNrSelBar);

		float fLookupValue = 0;
		float fOpacity = 0;

		FloatBuffer FbTemp = BufferUtil.newFloatBuffer(iTextureWidth * iTextureHeight * 4 / iNrSelBar);

		int iCount = 0;
		int iTextureCounter = 0;

		for (Integer iContentIndex : set.getVA(iContentVAID)) {
			iCount++;
			IVirtualArray storageVA = set.getVA(iStorageVAID);
			for (Integer iStorageIndex : storageVA) {
				if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iContentIndex)
					|| contentSelectionManager.checkStatus(ESelectionType.SELECTION, iContentIndex)
					|| detailLevel.compareTo(EDetailLevel.LOW) > 0) {
					fOpacity = 1.0f;
				}
				else {
					fOpacity = 0.3f;
				}

				fLookupValue = set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);

				float[] fArMappingColor = colorMapper.getColor(fLookupValue);

				float[] fArRgba = { fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity };

				FbTemp.put(fArRgba);
			}
			if (iCount >= iNrSamplesPerTexture) {
				FbTemp.rewind();

				TextureData texData =
					new TextureData(GL.GL_RGBA /* internalFormat */,
						set.getVA(iStorageVAID).size() /* height */, set.getVA(iContentVAID).size()
							/ iNrSelBar /* width */, 0 /* border */, GL.GL_RGBA /* pixelFormat */,
						GL.GL_FLOAT /* pixelType */, false /* mipmap */, false /* dataIsCompressed */,
						false /* mustFlipVertically */, FbTemp, null);

				tempTextur = TextureIO.newTexture(0);
				tempTextur.updateImage(texData);

				AlTextures.add(tempTextur);
				iAlNumberSamples.add(iCount);

				iTextureCounter++;
				iCount = 0;
			}
		}
	}

	/**
	 * Create embedded heatmap, register heatmap as a sender and receiver
	 * 
	 * @param
	 */
	private void createHeatMap() {
		CmdCreateGLEventListener cmdView =
			(CmdCreateGLEventListener) generalManager.getCommandManager().createCommandByType(
				ECommandType.CREATE_GL_HEAT_MAP_3D);

		ArrayList<Integer> alSetIDs = new ArrayList<Integer>();

		for (ISet set : alSets) {
			alSetIDs.add(set.getID());
		}
		float fHeatMapHeight = viewFrustum.getHeight();
		float fHeatMapWidth = viewFrustum.getWidth();

		cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, fHeatMapHeight, 0, fHeatMapWidth, -20, 20,
			alSetIDs, -1);

		cmdView.doCommand();

		glHeatMapView = (GLHeatMap) cmdView.getCreatedObject();

		// // Register heatmap as sender to event mediator
		// ArrayList<Integer> arMediatorIDs = new ArrayList<Integer>();
		// arMediatorIDs.add(glHeatMapView.getID());

		privateMediator.addSender(glHeatMapView);
		privateMediator.addReceiver(glHeatMapView);

		generalManager.getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR, glHeatMapView);
		// generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
		// this);
	}

	@Override
	public synchronized void setDetailLevel(EDetailLevel detailLevel) {
		super.setDetailLevel(detailLevel);
		// renderStyle.setDetailLevel(detailLevel);
		// renderStyle.updateFieldSizes();
	}

	@Override
	public synchronized void displayLocal(GL gl) {
		if (set == null)
			return;
		
		pickingManager.handlePicking(iUniqueID, gl);

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
	public synchronized void displayRemote(GL gl) {
		if (set == null)
			return;

		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
		// pickingTriggerMouseAdapter.resetEvents();
	}

	/**
	 * Function called any time a update is triggered external
	 * 
	 * @param
	 */
	@Override
	protected void reactOnExternalSelection(String trigger) {
		int iIndex = 0;
		int iTexture = 0;
		int iPos = 0;
		HeatMapSelection temp;

		AlSelection.clear();

		Set<Integer> setMouseOverElements = contentSelectionManager.getElements(ESelectionType.MOUSE_OVER);

		for (Integer iSelectedID : setMouseOverElements) {
			iIndex = set.getVA(iContentVAID).indexOf(iSelectedID.intValue()) + 1;

			iTexture = (int) Math.floor(iIndex / iAlNumberSamples.get(0));
			iPos = iIndex - iTexture * iAlNumberSamples.get(0);

			temp = new HeatMapSelection(iTexture, iPos, iSelectedID.intValue(), ESelectionType.MOUSE_OVER);
			AlSelection.add(temp);
		}

		Set<Integer> setSelectionElements = contentSelectionManager.getElements(ESelectionType.SELECTION);

		for (Integer iSelectedID : setSelectionElements) {
			iIndex = set.getVA(iContentVAID).indexOf(iSelectedID.intValue()) + 1;

			iTexture = (int) Math.floor(iIndex / iAlNumberSamples.get(0));
			iPos = iIndex - iTexture * iAlNumberSamples.get(0);

			temp = new HeatMapSelection(iTexture, iPos, iSelectedID.intValue(), ESelectionType.SELECTION);
			AlSelection.add(temp);
		}

		setMouseOverElements = storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);

		AlExpMouseOver.clear();
		if (setMouseOverElements.size() >= 0) {
			for (Integer iSelectedID : setMouseOverElements) {
				AlExpMouseOver.add(iSelectedID);
			}
		}

		setSelectionElements = storageSelectionManager.getElements(ESelectionType.SELECTION);

		AlExpSelected.clear();
		if (setSelectionElements.size() >= 0) {
			for (Integer iSelectedID : setSelectionElements) {
				AlExpSelected.add(iSelectedID);
			}
		}

		// if selected element is in another texture, switch to this texture
		if (!trigger.equals("GLHeatMap")) {
			setTexture();
		}
	}

	@Override
	protected void reactOnVAChanges(IVirtualArrayDelta delta) {
		privateMediator.triggerEvent(this, new DeltaEventContainer<IVirtualArrayDelta>(delta));
		bRedrawTextures = true;

		Set<Integer> setMouseOverElements = storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);

		AlExpMouseOver.clear();
		if (setMouseOverElements.size() >= 0) {
			for (Integer iSelectedID : setMouseOverElements) {
				AlExpMouseOver.add(iSelectedID);
			}
		}

		Set<Integer> setSelectionElements = storageSelectionManager.getElements(ESelectionType.SELECTION);

		AlExpSelected.clear();
		if (setSelectionElements.size() >= 0) {
			for (Integer iSelectedID : setSelectionElements) {
				AlExpSelected.add(iSelectedID);
			}
		}
		setDisplayListDirty();

	}

	/**
	 * Render caption, simplified version used in (original) heatmap
	 * 
	 * @param gl
	 * @param sLabel
	 * @param fXOrigin
	 * @param fYOrigin
	 * @param fFontScaling
	 */
	private void renderCaption(GL gl, String sLabel, float fXOrigin, float fYOrigin, float fFontScaling) {
		textRenderer.setColor(1, 1, 1, 1);
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glTranslatef(fXOrigin, fYOrigin, 0);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sLabel, 0, 0, 0, fFontScaling);
		textRenderer.end3DRendering();
		gl.glTranslatef(-fXOrigin, -fYOrigin, 0);
		gl.glPopAttrib();
	}

	/**
	 * Render a curved (nice looking) connection line from given start point to given end point
	 * 
	 * @param gl
	 * @param startpoint
	 * @param endpoint
	 */
	private void renderSelectedDomain(GL gl, Vec3f startpoint1, Vec3f endpoint1, Vec3f startpoint2,
		Vec3f endpoint2) {
		float fthickness = (endpoint1.x() - startpoint1.x()) / 4;
		float fScalFactor1, fScalFactor2;

		if (endpoint1.y() - startpoint1.y() < 0.2f) {
			fScalFactor1 = (endpoint1.y() - startpoint1.y()) * 5f;
		}
		else {
			fScalFactor1 = 1;
		}

		if (startpoint2.y() - endpoint2.y() < 0.2f) {
			fScalFactor2 = (startpoint2.y() - endpoint2.y()) * 5f;
		}
		else {
			fScalFactor2 = 1;
		}

		gl.glColor4f(0.5f, 0.5f, 0.5f, 1f);

		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(startpoint1.x(), startpoint1.y(), startpoint1.z());
		gl.glVertex3f(startpoint1.x() + 2 * fthickness, startpoint1.y(), startpoint1.z());
		gl.glVertex3f(startpoint2.x() + 2 * fthickness, startpoint2.y(), startpoint2.z());
		gl.glVertex3f(startpoint2.x(), startpoint2.y(), startpoint2.z());
		gl.glEnd();

		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(endpoint1.x(), endpoint1.y(), endpoint1.z());
		gl.glVertex3f(endpoint1.x() - 1 * fthickness, endpoint1.y(), endpoint1.z());
		gl.glVertex3f(endpoint2.x() - 1 * fthickness, endpoint2.y(), endpoint2.z());
		gl.glVertex3f(endpoint2.x(), endpoint2.y(), endpoint2.z());
		gl.glEnd();

		// fill gap
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(endpoint1.x() - 1 * fthickness, endpoint1.y() - 0.1f * fScalFactor1, endpoint1.z());
		gl.glVertex3f(endpoint1.x() - 2 * fthickness, endpoint1.y() - 0.1f * fScalFactor1, endpoint1.z());
		gl.glVertex3f(endpoint2.x() - 2 * fthickness, endpoint2.y() + 0.1f * fScalFactor2, endpoint2.z());
		gl.glVertex3f(endpoint2.x() - 1 * fthickness, endpoint2.y() + 0.1f * fScalFactor2, endpoint2.z());
		gl.glEnd();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1, 1, 1, 1);

		Texture TextureMask = iconTextureManager.getIconTexture(gl, EIconTextures.NAVIGATION_MASK_CURVE);
		TextureMask.enable();
		TextureMask.bind();

		TextureCoords texCoordsMask = TextureMask.getImageTexCoords();

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoordsMask.right(), texCoordsMask.top());
		gl.glVertex3f(startpoint1.x() + 2 * fthickness, startpoint1.y(), startpoint1.z());
		gl.glTexCoord2f(texCoordsMask.left(), texCoordsMask.top());
		gl.glVertex3f(startpoint1.x() + 1 * fthickness, startpoint1.y(), startpoint1.z());
		gl.glTexCoord2f(texCoordsMask.left(), texCoordsMask.bottom());
		gl.glVertex3f(startpoint1.x() + 1 * fthickness, startpoint1.y() + 0.1f * fScalFactor1, startpoint1
			.z());
		gl.glTexCoord2f(texCoordsMask.right(), texCoordsMask.bottom());
		gl.glVertex3f(startpoint1.x() + 2 * fthickness, startpoint1.y() + 0.1f * fScalFactor1, startpoint1
			.z());
		gl.glEnd();

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoordsMask.right(), texCoordsMask.top());
		gl.glVertex3f(startpoint2.x() + 2 * fthickness, startpoint2.y(), startpoint2.z());
		gl.glTexCoord2f(texCoordsMask.left(), texCoordsMask.top());
		gl.glVertex3f(startpoint2.x() + 1 * fthickness, startpoint2.y(), startpoint2.z());
		gl.glTexCoord2f(texCoordsMask.left(), texCoordsMask.bottom());
		gl.glVertex3f(startpoint2.x() + 1 * fthickness, startpoint2.y() - 0.1f * fScalFactor2, startpoint2
			.z());
		gl.glTexCoord2f(texCoordsMask.right(), texCoordsMask.bottom());
		gl.glVertex3f(startpoint2.x() + 2 * fthickness, startpoint2.y() - 0.1f * fScalFactor2, startpoint2
			.z());
		gl.glEnd();

		TextureMask.disable();

		Texture TextureMaskNeg =
			iconTextureManager.getIconTexture(gl, EIconTextures.NAVIGATION_MASK_CURVE_NEG);
		TextureMaskNeg.enable();
		TextureMaskNeg.bind();

		TextureCoords texCoordsMaskNeg = TextureMaskNeg.getImageTexCoords();

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.bottom());
		gl.glVertex3f(endpoint1.x() - 2 * fthickness, endpoint1.y() - 0.1f * fScalFactor1, endpoint1.z());
		gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.bottom());
		gl.glVertex3f(endpoint1.x() - 1 * fthickness, endpoint1.y() - 0.1f * fScalFactor1, endpoint1.z());
		gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.top());
		gl.glVertex3f(endpoint1.x() - 1 * fthickness, endpoint1.y(), endpoint1.z());
		gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.top());
		gl.glVertex3f(endpoint1.x() - 2 * fthickness, endpoint1.y(), endpoint1.z());
		gl.glEnd();

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.bottom());
		gl.glVertex3f(endpoint2.x() - 2 * fthickness, endpoint2.y() + 0.1f * fScalFactor2, endpoint2.z());
		gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.bottom());
		gl.glVertex3f(endpoint2.x() - 1 * fthickness, endpoint2.y() + 0.1f * fScalFactor2, endpoint2.z());
		gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.top());
		gl.glVertex3f(endpoint2.x() - 1 * fthickness, endpoint2.y(), endpoint2.z());
		gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.top());
		gl.glVertex3f(endpoint2.x() - 2 * fthickness, endpoint2.y(), endpoint2.z());
		gl.glEnd();

		TextureMaskNeg.disable();
		gl.glPopAttrib();
	}

	/**
	 * Render the first stage of the hierarchy (OverviewBar)
	 * 
	 * @param gl
	 */
	private void renderOverviewBar(GL gl) {
		float fHeight;
		float fWidth;
		float fyOffset = 0.0f;

		fHeight = viewFrustum.getHeight();
		fWidth = 0.1f;

		float fStep = fHeight / iNrSelBar;

		gl.glColor4f(1f, 1f, 0f, 1f);

		for (int i = 0; i < iNrSelBar; i++) {
			AlTextures.get(iNrSelBar - i - 1).enable();
			AlTextures.get(iNrSelBar - i - 1).bind();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			TextureCoords texCoords = AlTextures.get(iNrSelBar - i - 1).getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_TEXTURE_SELECTION, iNrSelBar - i));
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2d(texCoords.left(), texCoords.top());
			gl.glVertex3f(0, fyOffset, 0);
			gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0, fyOffset + fStep, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fWidth, fyOffset + fStep, 0);
			gl.glTexCoord2d(texCoords.right(), texCoords.top());
			gl.glVertex3f(fWidth, fyOffset, 0);
			gl.glEnd();
			gl.glPopName();

			fyOffset += fStep;
			AlTextures.get(iNrSelBar - i - 1).disable();
		}
	}

	private void renderClassAssignmentsExperiments(final GL gl) {

		float fWidth = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		int iNrElements = set.getVA(iStorageVAID).size();
		float fWidthSamples = fWidth / iNrElements;
		float fxpos = 0;
		float fHeight = viewFrustum.getHeight() + 0.1f;

		IGroupList groupList = set.getVA(iStorageVAID).getGroupList();

		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_EXPERIMENTS_GROUP, i));

			float classWidth = groupList.get(i).getNrElements() * fWidthSamples;

			if (groupList.get(i).getSelectionType() == ESelectionType.NORMAL)
				gl.glColor4f(0f, 0f, 1f, 0.5f);
			if (groupList.get(i).getSelectionType() == ESelectionType.SELECTION)
				gl.glColor4f(0f, 1f, 0f, 0.5f);

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(fxpos, fHeight, 0);
			gl.glVertex3f(fxpos, fHeight + 0.1f, 0);
			gl.glVertex3f(fxpos + classWidth, fHeight + 0.1f, 0);
			gl.glVertex3f(fxpos + classWidth, fHeight, 0);
			gl.glEnd();

			gl.glColor4f(0f, 0f, 0f, 1);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fxpos, fHeight, 0);
			gl.glVertex3f(fxpos, fHeight + 0.1f, 0);
			gl.glVertex3f(fxpos + classWidth, fHeight + 0.1f, 0);
			gl.glVertex3f(fxpos + classWidth, fHeight, 0);
			gl.glEnd();

			gl.glPopName();

			fxpos = fxpos + classWidth;
		}
	}

	private void renderClassAssignmentsGenes(final GL gl) {

		float fHeight = viewFrustum.getHeight();
		int iNrElements = iNumberOfElements;
		float fHeightSamples = fHeight / iNrElements;
		float fyPos = fHeight;

		IGroupList groupList = set.getVA(iContentVAID).getGroupList();

		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_GENES_GROUP, i));

			float classHeight = groupList.get(i).getNrElements() * fHeightSamples;

			if (groupList.get(i).getSelectionType() == ESelectionType.NORMAL)
				gl.glColor4f(0f, 0f, 1f, 0.5f);
			if (groupList.get(i).getSelectionType() == ESelectionType.SELECTION)
				gl.glColor4f(0f, 1f, 0f, 0.5f);
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(0, fyPos, 0);
			gl.glVertex3f(0.1f, fyPos, 0);
			gl.glVertex3f(0.1f, fyPos - classHeight, 0);
			gl.glVertex3f(0, fyPos - classHeight, 0);
			gl.glEnd();

			gl.glColor4f(0f, 0f, 0f, 1);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(0, fyPos, 0);
			gl.glVertex3f(0.1f, fyPos, 0);
			gl.glVertex3f(0.1f, fyPos - classHeight, 0);
			gl.glVertex3f(0, fyPos - classHeight, 0);
			gl.glEnd();

			gl.glPopName();

			fyPos = fyPos - classHeight;
		}
	}

	/**
	 * Render marker in OverviewBar for visualization of the currently (in stage 2) rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerOverviewBar(final GL gl) {
		float fHeight = viewFrustum.getHeight();
		float fStep = fHeight / iNrSelBar;
		float fFieldWith = 0.1f;
		Vec3f startpoint1, endpoint1, startpoint2, endpoint2;

		gl.glColor4f(1f, 1f, 0f, 1f);
		gl.glLineWidth(2f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, fStep * (iNrSelBar - iSelectorBar + 1), 0);
		gl.glVertex3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar + 1), 0);
		gl.glVertex3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar - 1), 0);
		gl.glVertex3f(0, fStep * (iNrSelBar - iSelectorBar - 1), 0);
		gl.glEnd();

		float foffsetPick =
			(fStep * (iNrSelBar - iSelectorBar + 1) - fStep * (iNrSelBar - iSelectorBar))
				/ iAlNumberSamples.get(iSelectorBar - 1);

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, fStep * (iNrSelBar - iSelectorBar + 1) - iFirstSample * foffsetPick, 0);
		gl.glVertex3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar + 1) - iFirstSample * foffsetPick, 0);
		gl
			.glVertex3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar + 1) - (iLastSample + 1) * foffsetPick,
				0);
		gl.glVertex3f(0, fStep * (iNrSelBar - iSelectorBar + 1) - (iLastSample + 1) * foffsetPick, 0);
		gl.glEnd();

		startpoint1 = new Vec3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar + 1), 0);
		endpoint1 = new Vec3f(GAP_LEVEL1_2, fHeight, 0);
		startpoint2 = new Vec3f(fFieldWith, fStep * (iNrSelBar - iSelectorBar - 1), 0);
		endpoint2 = new Vec3f(GAP_LEVEL1_2, 0, 0);

		renderSelectedDomain(gl, startpoint1, endpoint1, startpoint2, endpoint2);

	}

	/**
	 * Render marker next to OverviewBar for visualization of selected elements in the data set
	 * 
	 * @param gl
	 */
	private void renderSelectedElementsOverviewBar(GL gl) {
		float fHeight = viewFrustum.getHeight();
		float fStep = fHeight / iNrSelBar;
		float fBarWidth = 0.1f;

		for (HeatMapSelection selection : AlSelection) {
			if (selection.getSelectionType() == ESelectionType.MOUSE_OVER) {
				gl.glColor4fv(MOUSE_OVER_COLOR, 0);
			}
			else {
				gl.glColor4fv(SELECTED_COLOR, 0);
			}

			// elements in overview bar
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(fBarWidth, fStep * (iNrSelBar - (selection.getTexture() + 1) + 1), 0.001f);
			gl.glVertex3f(fBarWidth + 0.1f, fStep * (iNrSelBar - (selection.getTexture() + 1) + 1), 0.001f);
			gl.glVertex3f(fBarWidth + 0.1f, fStep * (iNrSelBar - (selection.getTexture() + 1)), 0.001f);
			gl.glVertex3f(fBarWidth, fStep * (iNrSelBar - (selection.getTexture() + 1)), 0.001f);
			gl.glEnd();
		}
		gl.glColor4f(1f, 1f, 0f, 1f);
	}

	/**
	 * Render the second stage of the hierarchy (Texture)
	 * 
	 * @param gl
	 */
	private void renderTextureHeatMap(GL gl) {
		float fHeight;
		float fWidth;

		Texture TexTemp1 = AlTextures.get(iSelectorBar - 1);
		TexTemp1.enable();
		TexTemp1.bind();

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);

		TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		fHeight = viewFrustum.getHeight();
		fWidth = viewFrustum.getWidth() / 4.0f * fAnimationScale;

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_FIELD_SELECTION, 1));

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2d(texCoords1.left(), texCoords1.top());
		gl.glVertex3f(0, fHeight / 2, 0);
		gl.glTexCoord2d(texCoords1.left(), texCoords1.bottom());
		gl.glVertex3f(0, fHeight, 0);
		gl.glTexCoord2d(texCoords1.right(), texCoords1.bottom());
		gl.glVertex3f(fWidth, fHeight, 0);
		gl.glTexCoord2d(texCoords1.right(), texCoords1.top());
		gl.glVertex3f(fWidth, fHeight / 2, 0);
		gl.glEnd();

		Texture TexTemp2 = AlTextures.get(iSelectorBar);
		TexTemp2.enable();
		TexTemp2.bind();

		TextureCoords texCoords2 = TexTemp2.getImageTexCoords();

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2d(texCoords2.left(), texCoords2.top());
		gl.glVertex3f(0, 0, 0);
		gl.glTexCoord2d(texCoords2.left(), texCoords2.bottom());
		gl.glVertex3f(0, fHeight / 2, 0);
		gl.glTexCoord2d(texCoords2.right(), texCoords2.bottom());
		gl.glVertex3f(fWidth, fHeight / 2, 0);
		gl.glTexCoord2d(texCoords2.right(), texCoords2.top());
		gl.glVertex3f(fWidth, 0, 0);
		gl.glEnd();

		gl.glPopName();

		gl.glPopAttrib();

		TexTemp1.disable();
		TexTemp2.disable();
	}

	/**
	 * Render marker in Texture for visualization of the currently (in stage 3) rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerTexture(final GL gl) {
		float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		float fHeightSample = viewFrustum.getHeight() / (iAlNumberSamples.get(iSelectorBar - 1) * 2);

		Vec3f startpoint1, endpoint1, startpoint2, endpoint2;

		gl.glColor4f(1, 1, 0, 1);
		gl.glLineWidth(2f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, viewFrustum.getHeight() - iFirstSample * fHeightSample, 0);
		gl.glVertex3f(fFieldWith, viewFrustum.getHeight() - iFirstSample * fHeightSample, 0);
		gl.glVertex3f(fFieldWith, viewFrustum.getHeight() - (iLastSample + 1) * fHeightSample, 0);
		gl.glVertex3f(0, viewFrustum.getHeight() - (iLastSample + 1) * fHeightSample, 0);
		gl.glEnd();

		if (bIsDraggingActive == false) {
			fPosCursorFirstElement = viewFrustum.getHeight() - iFirstSample * fHeightSample;
			fPosCursorLastElement = viewFrustum.getHeight() - (iLastSample + 1) * fHeightSample;
		}

		startpoint1 = new Vec3f(fFieldWith, viewFrustum.getHeight() - iFirstSample * fHeightSample, 0);
		endpoint1 = new Vec3f(fFieldWith + GAP_LEVEL2_3, viewFrustum.getHeight(), 0);
		startpoint2 = new Vec3f(fFieldWith, viewFrustum.getHeight() - (iLastSample + 1) * fHeightSample, 0);
		endpoint2 = new Vec3f(fFieldWith + GAP_LEVEL2_3, 0.0f, 0);

		renderSelectedDomain(gl, startpoint1, endpoint1, startpoint2, endpoint2);

		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE);
		tempTexture.enable();
		tempTexture.bind();

		float fYCoord = viewFrustum.getHeight() / 2;

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl
			.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_INFOCUS_SELECTION,
				1));
		if (bIsHeatmapInFocus) {
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fFieldWith + 0.2f, fYCoord - 0.3f, 0.1f);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fFieldWith + 0.2f, fYCoord + 0.3f, 0.1f);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fFieldWith + 0.3f, fYCoord + 0.3f, 0.1f);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fFieldWith + 0.3f, fYCoord - 0.3f, 0.1f);
			gl.glEnd();
		}
		else {
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fFieldWith + 0.2f, fYCoord - 0.3f, 0.1f);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fFieldWith + 0.2f, fYCoord + 0.3f, 0.1f);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fFieldWith + 0.3f, fYCoord + 0.3f, 0.1f);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fFieldWith + 0.3f, fYCoord - 0.3f, 0.1f);
			gl.glEnd();
		}
		gl.glPopName();

		tempTexture.disable();

		if (bRenderCaption == true) {
			renderCaption(gl, "Number Samples:" + iSamplesPerHeatmap, 0.0f, viewFrustum.getHeight()
				- iPickedSample * fHeightSample, 0.01f);
			bRenderCaption = false;
		}
	}

	/**
	 * Render marker in OverviewBar for visualization of selected elements in the data set
	 * 
	 * @param gl
	 */
	private void renderSelectedElementsTexture(GL gl) {
		float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		float fHeightSample = viewFrustum.getHeight() / (iNrSamplesPerTexture * 2);

		float fExpWidth = fFieldWith / set.getVA(iStorageVAID).size();

		gl.glEnable(GL.GL_LINE_STIPPLE);
		gl.glLineStipple(2, (short) 0xAAAA);

		gl.glColor4fv(MOUSE_OVER_COLOR, 0);
		Set<Integer> selectedSet = storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);
		int iColumnIndex = 0;
		for (int iTempLine : set.getVA(iStorageVAID)) {
			for (Integer iCurrentLine : selectedSet) {
				if (iTempLine == iCurrentLine) {
					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(iColumnIndex * fExpWidth, 0, SELECTION_Z);
					gl.glVertex3f((iColumnIndex + 1) * fExpWidth, 0, SELECTION_Z);
					gl.glVertex3f((iColumnIndex + 1) * fExpWidth, viewFrustum.getHeight(), SELECTION_Z);
					gl.glVertex3f(iColumnIndex * fExpWidth, viewFrustum.getHeight(), SELECTION_Z);
					gl.glEnd();
				}
			}
			iColumnIndex++;
		}

		gl.glColor4fv(SELECTED_COLOR, 0);
		selectedSet = storageSelectionManager.getElements(ESelectionType.SELECTION);
		int iLineIndex = 0;
		for (int iTempLine : set.getVA(iStorageVAID)) {
			for (Integer iCurrentLine : selectedSet) {
				if (iTempLine == iCurrentLine) {
					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(iLineIndex * fExpWidth, 0, SELECTION_Z);
					gl.glVertex3f((iLineIndex + 1) * fExpWidth, 0, SELECTION_Z);
					gl.glVertex3f((iLineIndex + 1) * fExpWidth, viewFrustum.getHeight(), SELECTION_Z);
					gl.glVertex3f(iLineIndex * fExpWidth, viewFrustum.getHeight(), SELECTION_Z);
					gl.glEnd();
				}
			}
			iLineIndex++;
		}

		gl.glDisable(GL.GL_LINE_STIPPLE);

		for (HeatMapSelection selection : AlSelection) {

			if (selection.getSelectionType() == ESelectionType.MOUSE_OVER) {
				gl.glColor4fv(MOUSE_OVER_COLOR, 0);
			}
			else {
				gl.glColor4fv(SELECTED_COLOR, 0);
			}

			// elements in texture
			if (iSelectorBar == selection.getTexture() + 1) {
				gl.glLineWidth(2f);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex3f(-0.1f, viewFrustum.getHeight() - (selection.getPos() - 1) * fHeightSample,
					SELECTION_Z);
				gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight() - (selection.getPos() - 1)
					* fHeightSample, SELECTION_Z);
				gl.glVertex3f(fFieldWith + 0.1f,
					viewFrustum.getHeight() - selection.getPos() * fHeightSample, SELECTION_Z);
				gl.glVertex3f(-0.1f, viewFrustum.getHeight() - selection.getPos() * fHeightSample,
					SELECTION_Z);
				gl.glEnd();
			}
			else if (iSelectorBar + 1 == selection.getTexture() + 1) {
				gl.glLineWidth(2f);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex3f(-0.1f, viewFrustum.getHeight()
					- (selection.getPos() - 1 + iAlNumberSamples.get(iSelectorBar - 1)) * fHeightSample,
					SELECTION_Z);
				gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight()
					- (selection.getPos() - 1 + iAlNumberSamples.get(iSelectorBar - 1)) * fHeightSample,
					SELECTION_Z);
				gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight()
					- (selection.getPos() + iAlNumberSamples.get(iSelectorBar - 1)) * fHeightSample,
					SELECTION_Z);
				gl.glVertex3f(-0.1f, viewFrustum.getHeight()
					- (selection.getPos() + iAlNumberSamples.get(iSelectorBar - 1)) * fHeightSample,
					SELECTION_Z);
				gl.glEnd();
			}
		}
	}

	/**
	 * Render cursor used for controlling hierarchical heatmap (e.g. next Texture, previous Texture, set
	 * heatmap in focus)
	 * 
	 * @param gl
	 */
	private void renderCursor(final GL gl) {
		float fHeight = viewFrustum.getHeight();
		float fWidth = viewFrustum.getWidth() / 4.0f;

		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_SIDE);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);

		if (iSelectorBar != 1) {
			// Polygon for selecting previous texture
			gl.glPushName(pickingManager
				.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_TEXTURE_CURSOR, 1));
			// left
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0.0f, fHeight, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0.1f, fHeight, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(0.1f, fHeight + 0.1f, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(0.0f, fHeight + 0.1f, 0);
			gl.glEnd();

			// right
			if (bIsHeatmapInFocus) {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidth / 5 - 0.1f, fHeight, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidth / 5, fHeight, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidth / 5, fHeight + 0.1f, 0);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidth / 5 - 0.1f, fHeight + 0.1f, 0);
				gl.glEnd();
			}
			else {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidth - 0.1f, fHeight, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidth, fHeight, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidth, fHeight + 0.1f, 0);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidth - 0.1f, fHeight + 0.1f, 0);
				gl.glEnd();
			}

			tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE);
			tempTexture.enable();
			tempTexture.bind();

			texCoords = tempTexture.getImageTexCoords();
			// middle
			if (bIsHeatmapInFocus) {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidth / 10 - 0.15f, fHeight, 0);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidth / 10 + 0.15f, fHeight, 0);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidth / 10 + 0.15f, fHeight + 0.1f, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidth / 10 - 0.15f, fHeight + 0.1f, 0);
				gl.glEnd();
			}
			else {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidth / 2 - 0.15f, fHeight, 0.001f);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidth / 2 + 0.15f, fHeight, 0.001f);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidth / 2 + 0.15f, fHeight + 0.1f, 0.001f);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidth / 2 - 0.15f, fHeight + 0.1f, 0.001f);
				gl.glEnd();

				// fill gap between middle and side
				gl.glBegin(GL.GL_QUADS);
				gl.glVertex3f(0.1f, fHeight, 0);
				gl.glVertex3f(fWidth / 2 - 0.15f, fHeight, 0);
				gl.glVertex3f(fWidth / 2 - 0.15f, fHeight + 0.1f, 0);
				gl.glVertex3f(0.1f, fHeight + 0.1f, 0);
				gl.glEnd();
				gl.glBegin(GL.GL_QUADS);
				gl.glVertex3f(fWidth - 0.1f, fHeight, 0);
				gl.glVertex3f(fWidth / 2 + 0.15f, fHeight, 0);
				gl.glVertex3f(fWidth / 2 + 0.15f, fHeight + 0.1f, 0);
				gl.glVertex3f(fWidth - 0.1f, fHeight + 0.1f, 0);
				gl.glEnd();
			}
			gl.glPopName();
		}

		tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_SIDE);
		tempTexture.enable();
		tempTexture.bind();

		texCoords = tempTexture.getImageTexCoords();

		if (iSelectorBar != iNrSelBar - 1) {
			// Polygon for selecting next texture
			gl.glPushName(pickingManager
				.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_TEXTURE_CURSOR, 2));
			// left
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0.0f, 0.0f, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0.1f, 0.0f, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(0.1f, -0.1f, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(0.0f, -0.1f, 0);
			gl.glEnd();

			// right
			if (bIsHeatmapInFocus) {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidth / 5 - 0.1f, 0, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidth / 5, 0, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidth / 5, -0.1f, 0);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidth / 5 - 0.1f, -0.1f, 0);
				gl.glEnd();
			}
			else {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidth - 0.1f, 0, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidth, 0, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidth, -0.1f, 0);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidth - 0.1f, -0.1f, 0);
				gl.glEnd();
			}

			tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE);
			tempTexture.enable();
			tempTexture.bind();

			texCoords = tempTexture.getImageTexCoords();
			// middle
			if (bIsHeatmapInFocus) {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidth / 10 - 0.15f, 0, 0);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidth / 10 + 0.15f, 0, 0);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidth / 10 + 0.15f, -0.1f, 0);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidth / 10 - 0.15f, -0.1f, 0);
				gl.glEnd();
			}
			else {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidth / 2 - 0.15f, 0, 0.001f);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidth / 2 + 0.15f, 0, 0.001f);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidth / 2 + 0.15f, -0.1f, 0.001f);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidth / 2 - 0.15f, -0.1f, 0.001f);
				gl.glEnd();

				// fill gap between middle and side
				gl.glBegin(GL.GL_QUADS);
				gl.glVertex3f(0.1f, 0, 0);
				gl.glVertex3f(fWidth / 2 - 0.15f, 0, 0);
				gl.glVertex3f(fWidth / 2 - 0.15f, -0.1f, 0);
				gl.glVertex3f(0.1f, -0.1f, 0);
				gl.glEnd();
				gl.glBegin(GL.GL_QUADS);
				gl.glVertex3f(fWidth - 0.1f, 0, 0);
				gl.glVertex3f(fWidth / 2 + 0.15f, 0, 0);
				gl.glVertex3f(fWidth / 2 + 0.15f, -0.1f, 0);
				gl.glVertex3f(fWidth - 0.1f, -0.1f, 0);
				gl.glEnd();
			}
			gl.glPopName();
		}

		tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_SMALL);
		tempTexture.enable();
		tempTexture.bind();

		texCoords = tempTexture.getImageTexCoords();

		// if (bIsHeatmapInFocus == false)
		{
			// Polygon for iFirstElement-Cursor
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_CURSOR, 1));
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(0.0f, fPosCursorFirstElement, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(-GAP_LEVEL1_2 / 4, fPosCursorFirstElement, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(-GAP_LEVEL1_2 / 4, fPosCursorFirstElement + 0.1f, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0.0f, fPosCursorFirstElement + 0.1f, 0);
			gl.glEnd();
			gl.glPopName();

			// Polygon for iLastElement-Cursor
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_CURSOR, 2));
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(0.0f, fPosCursorLastElement, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(-GAP_LEVEL1_2 / 4, fPosCursorLastElement, 0);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(-GAP_LEVEL1_2 / 4, fPosCursorLastElement - 0.1f, 0);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0.0f, fPosCursorLastElement - 0.1f, 0);
			gl.glEnd();
			gl.glPopName();

			// fill gap between cursor
			gl.glColor4f(0f, 0f, 0f, 0.45f);
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(-GAP_LEVEL1_2 / 4, fPosCursorLastElement, 0);
			gl.glVertex3f(0.0f, fPosCursorLastElement, 0);
			gl.glVertex3f(0.0f, fPosCursorFirstElement, 0);
			gl.glVertex3f(-GAP_LEVEL1_2 / 4, fPosCursorFirstElement, 0);
			gl.glEnd();
		}

		gl.glPopAttrib();
		tempTexture.disable();
	}

	@Override
	public synchronized void display(GL gl) {
		if (generalManager.isWiiModeActive()) {
			handleWiiInput();
		}

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		// GLHelperFunctions.drawAxis(gl);
		if (bIsDraggingActive) {
			handleCursorDragging(gl);
			if (pickingTriggerMouseAdapter.wasMouseReleased()) {
				bIsDraggingActive = false;
			}
		}

		if (bSplitGroupExp) {
			handleGroupSplitExperiments(gl);
			if (pickingTriggerMouseAdapter.wasMouseReleased()) {
				bSplitGroupExp = false;
			}
		}

		if (bSplitGroupGene) {
			handleGroupSplitGenes(gl);
			if (pickingTriggerMouseAdapter.wasMouseReleased()) {
				bSplitGroupExp = false;
			}
		}

		gl.glCallList(iGLDisplayListToCall);
		// buildDisplayList(gl, iGLDisplayListIndexRemote);

		float fright = 0.0f;
		float ftop = viewFrustum.getTop();

		float fleftOffset = 0;

		// render embedded heat map
		if (bIsHeatmapInFocus) {
			fright = viewFrustum.getWidth() - 1.2f;
			fleftOffset = 0.095f + // width level 1 + boarder
				GAP_LEVEL1_2 + // width gap between level 1 and 2
				viewFrustum.getWidth() / 4f * 0.2f + // width level 2
				GAP_LEVEL2_3; // width gap between level 2 and 3
		}
		else {
			fright = viewFrustum.getWidth() - 2.75f;
			fleftOffset = 0.075f + // width level 1
				GAP_LEVEL1_2 + // width gap between level 1 and 2
				viewFrustum.getWidth() / 4f + // width level 2
				GAP_LEVEL2_3;// width gap between level 2 and 3
		}

		if (glHeatMapView.isInDefaultOrientation()) {
			gl.glTranslatef(fleftOffset, +0.4f, 0);
		}
		else {
			gl.glTranslatef(fleftOffset, -0.2f, 0);
		}

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_VIEW_SELECTION,
			glHeatMapView.getID()));

		glHeatMapView.getViewFrustum().setTop(ftop);
		glHeatMapView.getViewFrustum().setRight(fright);
		glHeatMapView.displayRemote(gl);
		gl.glPopName();

		if (glHeatMapView.isInDefaultOrientation()) {
			gl.glTranslatef(-fleftOffset, -0.4f, 0);
		}
		else {
			gl.glTranslatef(-fleftOffset, +0.2f, 0);
		}
	}

	private int iNrNodes = 0, iNrLeafs = 0;

	/**
	 * Render dendrogram recursively
	 * 
	 * @param gl
	 * @param fymin
	 * @param fymax
	 * @param HierarchyGraph
	 * @param iDepth
	 * @param iNrSiblings
	 * @param iChildNr
	 */
	private void renderDendrogram(final GL gl, float fymin, float fymax, HierarchyGraph Node, int iDepth,
		int iNrSiblings, int iChildNr, boolean bleft, float fXMax) {

		iNrNodes++;

		int currentDepth = iDepth;

		float fxpos = 0.2f * currentDepth;
		float fypos = 0;
		float ymaxNew = 0, yminNew = 0;
		float fdiff = (fymax - fymin);
		float ywidth = fdiff / (iNrSiblings + 1);

		fypos = fymin + (ywidth * (iChildNr + 1));

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		int iNodeNr = Node.getClusterNr();

		// System.out.print(Node.getNodeName() + " ");

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_SELECTION, iNrNodes));

		if (bleft)
			gl.glColor4f(1f, 1f, 0f, 1f);
		else
			gl.glColor4f(1f, 0f, 1f, 1f);

		List<IGraph> listGraph = Node.getAllGraphByType(EGraphItemHierarchy.GRAPH_CHILDREN);

		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(fxpos, fypos, SELECTION_Z);
		if (listGraph.size() > 0) {
			gl.glVertex3f(fxpos + 0.2f, fypos, SELECTION_Z);
			gl.glVertex3f(fxpos + 0.2f, fypos + 0.01f, SELECTION_Z);
		}
		else {
			gl.glVertex3f(fXMax, fypos, SELECTION_Z);
			gl.glVertex3f(fXMax, fypos + 0.01f, SELECTION_Z);
			iNrLeafs++;
		}
		gl.glVertex3f(fxpos, fypos + 0.01f, SELECTION_Z);
		gl.glEnd();
		gl.glPopName();

		gl.glPopAttrib();

		if (listGraph.size() > 0) {
			currentDepth++;

			int iNrChildsNode = listGraph.size();

			if (bleft)
				gl.glColor4f(1f, 1f, 0f, 1f);
			else
				gl.glColor4f(1f, 0f, 1f, 1f);
			gl.glLineWidth(0.1f);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fxpos + 0.2f, fymin + (fdiff / (iNrChildsNode + 1) * 0.55f), SELECTION_Z);
			gl.glVertex3f(fxpos + 0.2f, fymin + (fdiff / (iNrChildsNode + 1) * (iNrChildsNode + 0.55f)),
				SELECTION_Z);
			gl.glEnd();

			for (int i = 0; i < iNrChildsNode; i++) {

				yminNew = fymin + (fdiff / (iNrChildsNode + 1) * (i + 0.55f));
				ymaxNew = fymin + (fdiff / (iNrChildsNode + 1) * (i + 1.5f));

				HierarchyGraph currentNode = (HierarchyGraph) listGraph.get(i);// .getChilds().elementAt(i);
				if (i == 0)
					renderDendrogram(gl, yminNew, ymaxNew, currentNode, currentDepth, iNrChildsNode, i, true,
						4);
				else
					renderDendrogram(gl, yminNew, ymaxNew, currentNode, currentDepth, iNrChildsNode, i,
						false, 4);
			}
		}
	}

	private class DendrogramNode {
		float xCoords;
		float yCoords;
		float correlation;
		boolean bSelected;
		boolean bCollapsed;
		String Nodename;

		public DendrogramNode() {
			this.xCoords = 0f;
			this.yCoords = 0f;
			this.correlation = 0f;
			this.bSelected = false;
			this.bCollapsed = false;
			this.Nodename = "";
		}
	}

	private void renderDendrogram(final GL gl, float fymin, float fymax, Node[] treeStructure) {

		float fxoffset = 0f;
		float fHeight = (fymax - fymin);

		float fHeightSample = fHeight / (treeStructure.length + 1);

		fymax = fymax - fHeightSample / 2f;

		// System.out.println(treeStructure.length);
		if (bInitNodes == true)
			Nodes = new DendrogramNode[treeStructure.length];

		int iCount = 0;
		int iClusterCount = 0;

		gl.glLineWidth(1f);
		gl.glColor4f(0f, 0f, 0f, 0.9f);

		for (int i = 0; i < treeStructure.length; i++) {

			float x_start_l = 0, x_start_r = 0, x_end = 0;
			float y_r = 0, y_l = 0;

			DendrogramNode temp = new DendrogramNode();

			if (treeStructure[i].getLeft() >= 0) {
				x_start_l = 0;
				y_l = fymax - (fHeightSample * iCount + fymin);
				iCount++;
			}
			else {
				x_start_l = Nodes[-(treeStructure[i].getLeft()) - 1].xCoords;
				y_l = Nodes[-(treeStructure[i].getLeft()) - 1].yCoords;
			}

			if (treeStructure[i].getRight() >= 0) {
				x_start_r = 0;
				y_r = fymax - (fHeightSample * iCount + fymin);
				iCount++;
			}
			else {
				x_start_r = Nodes[-(treeStructure[i].getRight()) - 1].xCoords;
				y_r = Nodes[-(treeStructure[i].getRight()) - 1].yCoords;
			}

			x_end = Math.max(x_start_r, x_start_l) - 0.05f + fxoffset;

			fxoffset -= 0.01f;

			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.DENDROGRAM_SELECTION,
				iClusterCount));
			gl.glBegin(GL.GL_LINES);
			// left
			gl.glVertex3f(x_start_l, y_l, SELECTION_Z);
			gl.glVertex3f(x_end, y_l, SELECTION_Z);
			// connect left and right
			gl.glVertex3f(x_end, y_l, SELECTION_Z);
			gl.glVertex3f(x_end, y_r, SELECTION_Z);
			// right
			gl.glVertex3f(x_start_r, y_r, SELECTION_Z);
			gl.glVertex3f(x_end, y_r, SELECTION_Z);
			gl.glEnd();
			gl.glPopName();

			if (bInitNodes == true) {

				temp.xCoords = x_end;
				temp.yCoords = y_l + (y_r - y_l) / 2f;
				temp.correlation = 1 - treeStructure[i].getCorrelation();
				temp.Nodename = "Node_" + i;

				Nodes[iClusterCount] = temp;
			}

			iClusterCount++;
		}
		bInitNodes = false;
		gl.glColor4f(1f, 1f, 0f, 1f);
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bRedrawTextures) {
			initTextures(gl);
			bRedrawTextures = false;
		}

		// Store clustered VA and number of samples per cluster to file
		// try
		// {
		// BufferedWriter out = new BufferedWriter(new
		// FileWriter("clusterer.txt"));
		// for (Integer iContentIndex : set.getVA(iContentVAID))
		// out.write(iContentIndex + " ");
		// out.write("\n");
		// for (Integer iter : set.getAlClusterSizes())
		// out.write(iter + " ");
		// out.write("\n");
		// out.close();
		// }
		// catch (IOException e)
		// {
		// }

		if (bHasFrustumChanged) {
			glHeatMapView.setDisplayListDirty();
			bHasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		// background color
		gl.glColor4f(0, 0, 0, 0.15f);
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(0, 0, -0.1f);
		gl.glVertex3f(viewFrustum.getRight(), 0, -0.1f);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getHeight(), -0.1f);
		gl.glVertex3f(0, viewFrustum.getHeight(), -0.1f);
		gl.glEnd();

		// padding along borders
		viewFrustum.setTop(viewFrustum.getTop() - 0.6f);
		viewFrustum.setLeft(viewFrustum.getLeft() + 0.1f);

		gl.glTranslatef(0.0f, 0.4f, 0);

		if (set.getVA(iContentVAID).getGroupList() != null)
			renderClassAssignmentsGenes(gl);

		gl.glTranslatef(0.1f, 0.0f, 0);

		handleTexturePicking(gl);

		// GLHelperFunctions.drawAxis(gl);

		if (hierarchyGraph == null) {
			// System.out.println("Problems during clustering!!");
		}
		else {
			// System.out.println("renderDendrogram(..)");
			iNrNodes = 0;
			iNrLeafs = 0;
			renderDendrogram(gl, 0.0f, viewFrustum.getHeight(), hierarchyGraph, 0, 1, 0, true, 4);
			// System.out.println("\n iNrNodes: " + iNrNodes);
			// System.out.println("\n iNrLeafs: " + iNrLeafs);
		}

		// all stuff for rendering level 1 (overview bar)
		renderOverviewBar(gl);
		renderMarkerOverviewBar(gl);
		renderSelectedElementsOverviewBar(gl);

		gl.glTranslatef(GAP_LEVEL1_2, 0, 0);

		if (bIsHeatmapInFocus) {
			fAnimationScale = 0.2f;
		}
		else {
			fAnimationScale = 1.0f;
		}

		// all stuff for rendering level 2 (textures)
		renderTextureHeatMap(gl);
		renderMarkerTexture(gl);
		renderSelectedElementsTexture(gl);
		renderCursor(gl);

		if (set.getVA(iStorageVAID).getGroupList() != null)
			renderClassAssignmentsExperiments(gl);

		// gl.glTranslatef(7.0f, -0.0f, 0);
		// if (treeStructure == null) {
		// // System.out.println("Problems during clustering!!");
		// }
		// else {
		// // System.out.println("renderDendrogram(..)");
		// renderDendrogram(gl, 0.0f, viewFrustum.getHeight(), treeStructure);
		// }
		// gl.glTranslatef(-7.0f, -0.0f, 0);

		viewFrustum.setTop(viewFrustum.getTop() + 0.6f);
		viewFrustum.setLeft(viewFrustum.getLeft() - 0.1f);
		gl.glTranslatef(-0.1f, -0.4f, 0);

		gl.glTranslatef(-GAP_LEVEL1_2, 0, 0);

		gl.glDisable(GL.GL_STENCIL_TEST);

		gl.glEndList();
	}

	/**
	 * Function responsible for handling SelectionDelta for embedded heatmap
	 * 
	 * @param
	 */
	private void triggerSelectionBlock() {
		int iCount = iNrSamplesPerTexture * (iSelectorBar - 1) + iFirstSample;

		privateMediator.triggerEvent(this, new SelectionCommandEventContainer(EIDType.REFSEQ_MRNA_INT,
			new SelectionCommand(ESelectionCommandType.RESET)));

		IVirtualArrayDelta delta = new VirtualArrayDelta(eFieldDataType);
		ISelectionDelta selectionDelta = new SelectionDelta(eFieldDataType);

		IVirtualArray currentVirtualArray = set.getVA(iContentVAID);
		int iIndex = 0;

		int iContentIndex = 0;

		for (int index = 0; index < iSamplesPerHeatmap; index++) {
			iIndex = iCount + index;
			if (iIndex < currentVirtualArray.size()) {
				iContentIndex = currentVirtualArray.get(iIndex);
			}

			delta.add(VADeltaItem.append(iContentIndex));
			// set elements selected in embedded heatMap
			for (HeatMapSelection selection : AlSelection) {
				if (selection.getContentIndex() == iContentIndex) {
					selectionDelta.addSelection(iContentIndex, selection.getSelectionType());
				}
			}
		}

		privateMediator.triggerEvent(this, new DeltaEventContainer<IVirtualArrayDelta>(delta));
		if (selectionDelta.size() > 0) {
			privateMediator.triggerEvent(this, new DeltaEventContainer<ISelectionDelta>(selectionDelta));
		}

		// selected experiments
		privateMediator.triggerEvent(this, new SelectionCommandEventContainer(eExperimentDataType,
			new SelectionCommand(ESelectionCommandType.RESET)));

		IVirtualArrayDelta deltaExp = new VirtualArrayDelta(eExperimentDataType);
		ISelectionDelta selectionDeltaEx = new SelectionDelta(eExperimentDataType);

		IVirtualArray currentVirtualArrayEx = set.getVA(iStorageVAID);

		for (int index = 0; index < currentVirtualArrayEx.size(); index++) {
			iContentIndex = currentVirtualArrayEx.get(index);

			deltaExp.add(VADeltaItem.append(iContentIndex));
			// set elements selected in embedded heatMap
			for (Integer selection : AlExpMouseOver) {
				if (selection == iContentIndex) {
					selectionDeltaEx.addSelection(iContentIndex, ESelectionType.MOUSE_OVER);
				}
			}
			for (Integer selection : AlExpSelected) {
				if (selection == iContentIndex) {
					selectionDeltaEx.addSelection(iContentIndex, ESelectionType.SELECTION);
				}
			}
		}

		privateMediator.triggerEvent(this, new DeltaEventContainer<IVirtualArrayDelta>(deltaExp));
		if (selectionDeltaEx.size() > 0) {
			privateMediator.triggerEvent(this, new DeltaEventContainer<ISelectionDelta>(selectionDeltaEx));
		}

	}

	public synchronized void renderHorizontally(boolean bRenderStorageHorizontally) {

		if (glHeatMapView.isInDefaultOrientation()) {
			glHeatMapView.changeOrientation(false);
		}
		else {
			glHeatMapView.changeOrientation(true);
		}

		// this.bRenderStorageHorizontally = bRenderStorageHorizontally;
		// renderStyle.setBRenderStorageHorizontally(bRenderStorageHorizontally);
		setDisplayListDirty();
	}

	@Override
	protected void initLists() {

		if (bRenderOnlyContext) {
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		}
		else {
			if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION)) {
				initCompleteList();
			}
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
		}
		iStorageVAID = mapVAIDs.get(EStorageBasedVAType.STORAGE_SELECTION);

		if (bUseClusteredVA) {
			if (eClustererType == EClustererType.GENE_CLUSTERING) {

				iContentVAID = set.cluster(iContentVAID, iStorageVAID, eClustererAlgo, eClustererType);

			}
			else if (eClustererType == EClustererType.EXPERIMENTS_CLUSTERING) {
				// System.out.println("iStorageVAID before clustering " + iStorageVAID + " size: "
				// + set.getVA(iStorageVAID).size());
				// for (int i = 0; i < 6; i++) {
				// System.out.print(set.getVA(iStorageVAID).get(i) + " ");
				// }
				// System.out.println(" ");

				iStorageVAID = set.cluster(iContentVAID, iStorageVAID, eClustererAlgo, eClustererType);

				// System.out.println("iStorageVAID after clustering  " + iStorageVAID + " size: "
				// + set.getVA(iStorageVAID).size());
				// for (int i = 0; i < 6; i++) {
				// System.out.print(set.getVA(iStorageVAID).get(i) + " ");
				// }
				// System.out.println(" ");
			}
			else {

				iStorageVAID =
					set.cluster(iContentVAID, iStorageVAID, eClustererAlgo,
						EClustererType.EXPERIMENTS_CLUSTERING);
				iContentVAID =
					set.cluster(iContentVAID, iStorageVAID, eClustererAlgo, EClustererType.GENE_CLUSTERING);

			}

			if (eClustererAlgo == EClustererAlgo.COBWEB_CLUSTERER
				|| eClustererAlgo == EClustererAlgo.TREE_CLUSTERER) {
				hierarchyGraph = set.getClusteredGraph();
			}
			else {

				// System.out.println("\n number of elements per cluster ... ");
				// for (Integer iter : set.getAlClusterSizes()) {
				// System.out.print(iter + " ");
				// }
				// System.out.println("\n index of example for cluster in data set ... ");
				// for (Integer iter : set.getAlExamples()) {
				// System.out.print(iter + " ");
				// }
				// System.out.println(" ");
			}
		}

		contentSelectionManager.resetSelectionManager();
		storageSelectionManager.resetSelectionManager();

		contentSelectionManager.setVA(set.getVA(iContentVAID));
		storageSelectionManager.setVA(set.getVA(iStorageVAID));

		int iNumberOfColumns = set.getVA(iContentVAID).size();
		int iNumberOfRows = set.getVA(iStorageVAID).size();

		for (int iRowCount = 0; iRowCount < iNumberOfRows; iRowCount++) {
			storageSelectionManager.initialAdd(set.getVA(iStorageVAID).get(iRowCount));

		}

		// this for loop executes one per axis
		for (int iColumnCount = 0; iColumnCount < iNumberOfColumns; iColumnCount++) {
			contentSelectionManager.initialAdd(set.getVA(iContentVAID).get(iColumnCount));
		}
	}

	@Override
	public String getShortInfo() {
		return "Hierarchical Heat Map (" + set.getVA(iContentVAID).size() + " genes / "
			+ set.getVA(iStorageVAID).size() + " experiments)";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Hierarchical Heat Map\n");

		if (bRenderStorageHorizontally) {
			sInfoText.append(set.getVA(iContentVAID).size() + "Genes in columns and "
				+ set.getVA(iStorageVAID).size() + " experiments in rows.\n");
		}
		else {
			sInfoText.append(set.getVA(iContentVAID).size() + " Genes in rows and "
				+ set.getVA(iStorageVAID).size() + " experiments in columns.\n");
		}

		if (bRenderOnlyContext) {
			sInfoText.append("Showing only genes which occur in one of the other views in focus\n");
		}
		else {
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

	/**
	 * Determine selected element in stage 2 (texture)
	 * 
	 * @param gl
	 */
	private void handleTexturePicking(GL gl) {
		int iNumberSample = iNrSamplesPerTexture * 2;
		float fOffsety;
		float fHeightSample = viewFrustum.getHeight() / iNumberSample;
		float[] fArPickingCoords = new float[3];

		if (PickingPoint != null) {
			fArPickingCoords =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, PickingPoint.x,
					PickingPoint.y);
			fOffsety = viewFrustum.getHeight() - fArPickingCoords[1] + 0.4f;
			iPickedSample = (int) Math.ceil(fOffsety / fHeightSample);
			PickingPoint = null;

			if (iSamplesPerHeatmap % 2 == 0) {
				iFirstSample = iPickedSample - (int) Math.floor(iSamplesPerHeatmap / 2) + 1;
				iLastSample = iPickedSample + (int) Math.floor(iSamplesPerHeatmap / 2);
			}
			else {
				iFirstSample = iPickedSample - (int) Math.ceil(iSamplesPerHeatmap / 2);
				iLastSample = iPickedSample + (int) Math.floor(iSamplesPerHeatmap / 2);
			}

			if (iPickedSample < iSamplesPerHeatmap / 2) {
				iPickedSample = (int) Math.floor(iSamplesPerHeatmap / 2);
				iFirstSample = 0;
				iLastSample = iSamplesPerHeatmap - 1;
			}
			else if (iPickedSample > iNumberSample - 1 - iSamplesPerHeatmap / 2) {
				iPickedSample = (int) Math.ceil(iNumberSample - iSamplesPerHeatmap / 2);
				iLastSample = iNumberSample - 1;
				iFirstSample = iNumberSample - iSamplesPerHeatmap;
			}
		}
		setDisplayListDirty();
		triggerSelectionBlock();
	}

	private void handleGroupSplitGenes(final GL gl) {
		Point currentPoint = pickingTriggerMouseAdapter.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		float[] fArDraggedPoint = new float[3];

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1], 0);
		gl.glVertex3f(fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1] + 0.1f, 0);
		gl.glVertex3f(fArTargetWorldCoordinates[0] + 0.1f, fArTargetWorldCoordinates[1] + 0.1f, 0);
		gl.glVertex3f(fArTargetWorldCoordinates[0] + 0.1f, fArTargetWorldCoordinates[1], 0);
		gl.glEnd();

		if (pickingTriggerMouseAdapter.wasMouseReleased()) {
			bSplitGroupGene = false;

			fArDraggedPoint =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, DraggingPoint.x,
					DraggingPoint.y);

			float fYPosDrag = fArDraggedPoint[1] - 0.4f;
			float fYPosRelease = fArTargetWorldCoordinates[1] - 0.4f;

			float fHeight = viewFrustum.getHeight() - 0.6f;
			int iNrSamples = set.getVA(iContentVAID).size();
			float fHeightSample = fHeight / iNrSamples;

			int iFirstSample = iNrSamples - (int) Math.floor(fYPosDrag / fHeightSample);
			int iLastSample = iNrSamples - (int) Math.ceil(fYPosRelease / fHeightSample);

			// System.out.println("von: " + fYPosDrag + " bis: " + fYPosRelease);
			// System.out.println("von: " + iFirstSample + " bis: " + iLastSample);

			if (set.getVA(iContentVAID).getGroupList().split(iGroupToSplit, iFirstSample, iLastSample) == false)
				System.out.println("Operation not allowed!!");
		}
	}

	private void handleGroupSplitExperiments(final GL gl) {
		Point currentPoint = pickingTriggerMouseAdapter.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		float[] fArDraggedPoint = new float[3];

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1], 0);
		gl.glVertex3f(fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1] + 0.1f, 0);
		gl.glVertex3f(fArTargetWorldCoordinates[0] + 0.1f, fArTargetWorldCoordinates[1] + 0.1f, 0);
		gl.glVertex3f(fArTargetWorldCoordinates[0] + 0.1f, fArTargetWorldCoordinates[1], 0);
		gl.glEnd();

		if (pickingTriggerMouseAdapter.wasMouseReleased()) {
			bSplitGroupExp = false;

			fArDraggedPoint =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, DraggingPoint.x,
					DraggingPoint.y);

			float fXPosDrag = fArDraggedPoint[0] - 0.7f;
			float fXPosRelease = fArTargetWorldCoordinates[0] - 0.7f;

			float fWidth = viewFrustum.getWidth() / 4.0f * fAnimationScale;
			int iNrSamples = set.getVA(iStorageVAID).size();
			float fWidthSample = fWidth / iNrSamples;

			int iFirstSample = (int) Math.floor(fXPosDrag / fWidthSample);
			int iLastSample = (int) Math.ceil(fXPosRelease / fWidthSample);

			if (set.getVA(iStorageVAID).getGroupList().split(iGroupToSplit, iLastSample, iFirstSample) == false)
				System.out.println("Operation not allowed!!");
		}
	}

	/**
	 * Function used for updating cursor position in case of dragging
	 * 
	 * @param gl
	 */
	private void handleCursorDragging(final GL gl) {
		Point currentPoint = pickingTriggerMouseAdapter.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;
		int iNrSamples;

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / (iAlNumberSamples.get(0) * 2);
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		// cursor for iFirstElement
		if (iDraggedCursor == 1) {
			if (fYPosMouse > fPosCursorLastElement && fYPosMouse <= viewFrustum.getHeight() - 0.6f) {
				iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
				iNrSamples = iLastSample - iselElement + 1;
				if (iNrSamples >= MIN_SAMPLES_PER_HEATMAP && iNrSamples < iAlNumberSamples.get(0) / 3) {
					fPosCursorFirstElement = fYPosMouse;
					iFirstSample = iselElement;
					iSamplesPerHeatmap = iLastSample - iFirstSample + 1;

					// update Preference store
					generalManager.getPreferenceStore().setValue(
						PreferenceConstants.HM_NUM_SAMPLES_PER_HEATMAP, iSamplesPerHeatmap);
				}
			}
		}
		// cursor for iLastElement
		if (iDraggedCursor == 2) {
			if (fYPosMouse < fPosCursorFirstElement && fYPosMouse >= 0.0f) {
				iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
				iNrSamples = iselElement - iFirstSample + 1;
				if (iNrSamples >= MIN_SAMPLES_PER_HEATMAP && iNrSamples < iAlNumberSamples.get(0) / 3) {
					fPosCursorLastElement = fYPosMouse;
					iLastSample = iselElement;
					iSamplesPerHeatmap = iLastSample - iFirstSample + 1;

					// update Preference store
					generalManager.getPreferenceStore().setValue(
						PreferenceConstants.HM_NUM_SAMPLES_PER_HEATMAP, iSamplesPerHeatmap);
				}
			}
		}

		setDisplayListDirty();
		triggerSelectionBlock();

		if (pickingTriggerMouseAdapter.wasMouseReleased()) {
			bIsDraggingActive = false;
		}
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			pickingManager.flushHits(iUniqueID, ePickingType);
			return;
		}

		switch (ePickingType) {

			case HIER_HEAT_MAP_GENES_GROUP:
				switch (pickingMode) {

					case CLICKED:
						set.getVA(iContentVAID).getGroupList().get(iExternalID).toggleSelectionType();
						setDisplayListDirty();
						break;

					case DRAGGED:
						if (bSplitGroupGene == false) {
							bSplitGroupGene = true;
							iGroupToSplit = iExternalID;
							DraggingPoint = pick.getPickedPoint();
						}
						setDisplayListDirty();
						break;

					case MOUSE_OVER:
						System.out.print("genes group " + iExternalID);
						System.out.print(" number elements in group: ");
						System.out.println(set.getVA(iContentVAID).getGroupList().get(iExternalID)
							.getNrElements());
						setDisplayListDirty();
						break;
				}

				pickingManager.flushHits(iUniqueID, ePickingType);
				break;

			case HIER_HEAT_MAP_EXPERIMENTS_GROUP:
				switch (pickingMode) {
					case CLICKED:
						set.getVA(iStorageVAID).getGroupList().get(iExternalID).toggleSelectionType();
						setDisplayListDirty();
						break;

					case DRAGGED:
						if (bSplitGroupExp == false) {
							bSplitGroupExp = true;
							iGroupToSplit = iExternalID;
							DraggingPoint = pick.getPickedPoint();
						}
						setDisplayListDirty();
						break;

					case MOUSE_OVER:
						System.out.print("patients group " + iExternalID);
						System.out.print(" number elements in group: ");
						System.out.println(set.getVA(iStorageVAID).getGroupList().get(iExternalID)
							.getNrElements());
						setDisplayListDirty();
						break;
				}
				pickingManager.flushHits(iUniqueID, ePickingType);
				break;

			case DENDROGRAM_SELECTION:
				switch (pickingMode) {

					case CLICKED:
						break;

					case MOUSE_OVER:
						System.out.println("node " + iExternalID);
						setDisplayListDirty();
						break;
				}

				pickingManager.flushHits(iUniqueID, ePickingType);
				break;

			case HIER_HEAT_MAP_INFOCUS_SELECTION:
				switch (pickingMode) {

					case CLICKED:

						bIsHeatmapInFocus = bIsHeatmapInFocus == true ? false : true;
						glHeatMapView.setDisplayListDirty();
						setDisplayListDirty();

						break;
				}

				pickingManager.flushHits(iUniqueID, ePickingType);
				break;

			case HIER_HEAT_MAP_TEXTURE_CURSOR:
				switch (pickingMode) {
					case CLICKED:

						if (iExternalID == 1) {
							iSelectorBar--;
							initPosCursor();
							triggerSelectionBlock();
							setDisplayListDirty();
						}
						if (iExternalID == 2) {
							iSelectorBar++;
							initPosCursor();
							triggerSelectionBlock();
							setDisplayListDirty();
						}

						setDisplayListDirty();
						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;
				}
				pickingManager.flushHits(iUniqueID, ePickingType);
				break;

			case HIER_HEAT_MAP_CURSOR:
				switch (pickingMode) {
					case CLICKED:

						// bRenderCaption = true;
						// setDisplayListDirty();
						break;

					case DRAGGED:

						// bRenderCaption = true;
						bIsDraggingActive = true;
						iDraggedCursor = iExternalID;
						setDisplayListDirty();
						break;

					case MOUSE_OVER:

						// bRenderCaption = true;
						// setDisplayListDirty();
						break;
				}
				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
			case HIER_HEAT_MAP_TEXTURE_SELECTION:
				switch (pickingMode) {
					case CLICKED:

						iSelectorBar = iExternalID;
						if (iSelectorBar == iNrSelBar) {
							iSelectorBar--;
						}
						initPosCursor();
						triggerSelectionBlock();
						setDisplayListDirty();
						break;

					case MOUSE_OVER:

						iSelectorBar = iExternalID;
						if (iSelectorBar == iNrSelBar) {
							iSelectorBar--;
						}
						initPosCursor();
						triggerSelectionBlock();
						setDisplayListDirty();
						break;
				}

				pickingManager.flushHits(iUniqueID, ePickingType);
				break;

			case HIER_HEAT_MAP_FIELD_SELECTION:
				switch (pickingMode) {
					case CLICKED:

						// bIsHeatmapInFocus = false;
						PickingPoint = pick.getPickedPoint();
						triggerSelectionBlock();
						setDisplayListDirty();
						break;

					case MOUSE_OVER:

						PickingPoint = pick.getPickedPoint();
						triggerSelectionBlock();
						setDisplayListDirty();
						break;
				}

				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
			case HIER_HEAT_MAP_VIEW_SELECTION:
				switch (pickingMode) {
					case MOUSE_OVER:
						break;

					case CLICKED:

						// bIsHeatmapInFocus = true;
						// glHeatMapView.setDisplayListDirty();
						// setDisplayListDirty();

						break;

					case DRAGGED:
						break;
				}

				pickingManager.flushHits(iUniqueID, EPickingType.HIER_HEAT_MAP_VIEW_SELECTION);

				break;
		}
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(EIDType idType, int iStorageIndex) {
		return null;
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext) {
		throw new IllegalStateException("Rendering only context not supported for the hierachical heat map");
	}

	@Override
	public void broadcastElements() {
		throw new IllegalStateException("broadcast elements of the contained heat map or all?");
	}

	@Override
	public synchronized void clearAllSelections() {

		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		AlSelection.clear();
		AlExpMouseOver.clear();
		AlExpSelected.clear();
		iSelectorBar = 1;
		initPosCursor();
		setDisplayListDirty();
		triggerSelectionBlock();
		glHeatMapView.setDisplayListDirty();
	}

	@Override
	public void changeOrientation(boolean defaultOrientation) {
		renderHorizontally(defaultOrientation);
	}

	@Override
	public boolean isInDefaultOrientation() {
		return bRenderStorageHorizontally;
	}

	public void changeFocus(boolean bInFocus) {
		bIsHeatmapInFocus = bIsHeatmapInFocus == true ? false : true;

		setDisplayListDirty();
	}

	public void startClustering(boolean bStartClustering) {
		bUseClusteredVA = true;
		initData();
		bUseClusteredVA = false;

		setDisplayListDirty();
	}

	public void activateGroupHandling() {

		if (set.getVA(iContentVAID).getGroupList() == null) {
			IGroupList groupList = new GroupList(0);
			Group group = new Group(set.getVA(iContentVAID).size(), false, 0, ESelectionType.NORMAL);
			groupList.append(group);
			set.getVA(iContentVAID).setGroupList(groupList);
		}

		if (set.getVA(iStorageVAID).getGroupList() == null) {
			IGroupList groupList = new GroupList(0);
			Group group = new Group(set.getVA(iStorageVAID).size(), false, 0, ESelectionType.NORMAL);
			groupList.append(group);
			set.getVA(iStorageVAID).setGroupList(groupList);
		}

		setDisplayListDirty();

	}

	public void mergeGroups() {

		IGroupList groupList = set.getVA(iStorageVAID).getGroupList();

		ArrayList<Integer> selGroups = new ArrayList<Integer>();

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		for (Group iter : groupList) {
			if (iter.getSelectionType() == ESelectionType.SELECTION)
				selGroups.add(groupList.indexOf(iter));
		}
		if (selGroups.size() != 2) {
			System.out.println("Number of selected elements has to be 2!!!");
			return;
		}

		// interchange
		// if (groupList.interchange(set.getVA(iContentVAID), selGroups.get(0), selGroups.get(1)) == false) {
		// System.out.println("Problem during interchange!!!");
		// return;
		// }

		// merge
		if (groupList.merge(set.getVA(iStorageVAID), selGroups.get(0), selGroups.get(1)) == false) {
			System.out.println("Problem during merge!!!");
			return;
		}

		bRedrawTextures = true;

		setDisplayListDirty();
	}

	public boolean isInFocus() {
		return bIsHeatmapInFocus;
	}

	private void handleWiiInput() {
		float fHeadPositionX = generalManager.getWiiRemote().getCurrentSmoothHeadPosition()[0];

		if (fHeadPositionX < -1.2f) {
			bIsHeatmapInFocus = false;
		}
		else {
			bIsHeatmapInFocus = true;
		}

		setDisplayListDirty();
	}

	@Override
	public synchronized void initData() {
		super.initData();

		bRedrawTextures = true;
	}
}
