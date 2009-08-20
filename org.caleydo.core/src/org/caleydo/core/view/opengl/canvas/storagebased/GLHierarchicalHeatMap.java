package org.caleydo.core.view.opengl.canvas.storagebased;

import static org.caleydo.core.view.opengl.canvas.storagebased.HeatMapRenderStyle.SELECTION_Z;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_COLOR;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.data.selection.IGroupList;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.event.view.group.InterchangeGroupsEvent;
import org.caleydo.core.manager.event.view.group.MergeGroupsEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterHelper;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.UpdateViewListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.canvas.remote.listener.GroupInterChangingActionListener;
import org.caleydo.core.view.opengl.canvas.remote.listener.GroupMergingActionListener;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IGroupsInterChangingActionReceiver;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IGroupsMergingActionReceiver;
import org.caleydo.core.view.opengl.canvas.storagebased.listener.GLHierarchicalHeatMapKeyListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.GroupContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

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
	extends AStorageBasedView
	implements IGroupsMergingActionReceiver, IGroupsInterChangingActionReceiver {

	private final static float GAP_LEVEL1_2 = 0.6f;
	private final static float GAP_LEVEL2_3 = 0.6f;

	private int iSamplesPerTexture = 0;
	private final static int MIN_SAMPLES_PER_TEXTURE = 200;
	private final static int MAX_SAMPLES_PER_TEXTURE = 1000;

	private int iSamplesLevel2;
	private final static int MIN_SAMPLES_LEVEL_2 = 200;
	private final static int MAX_SAMPLES_LEVEL_2 = 400;

	private int iSamplesPerHeatmap = 0;
	private final static int MIN_SAMPLES_PER_HEATMAP = 14;
	private final static int MAX_SAMPLES_PER_HEATMAP = 50;

	private int iNumberOfElements = 0;
	// if only a small number of genes is in the data set, level_1 (overViewBar) will not be
	// rendered
	private boolean bSkipLevel1 = false;
	private final static int MIN_SAMPLES_SKIP_LEVEL_1 = 200;
	// if only a small number of genes is in the data set, level_2 (textures) will not be rendered
	private boolean bSkipLevel2 = false;
	private final static int MIN_SAMPLES_SKIP_LEVEL_2 = 40;

	private ColorMapping colorMapper;

	private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;
	private EIDType eExperimentDataType = EIDType.EXPERIMENT_INDEX;

	// array of textures for holding the data samples
	private int iNrTextures = 0;
	private ArrayList<Texture> AlTextures = new ArrayList<Texture>();
	private ArrayList<Integer> iAlNumberSamples = new ArrayList<Integer>();

	private Point pickingPointLevel2 = null;
	private int iPickedSampleLevel2 = 0;
	private int iFirstSampleLevel2 = 0;
	private int iLastSampleLevel2 = 0;

	private Point pickingPointLevel1 = null;
	private int iPickedSampleLevel1 = 0;
	private int iFirstSampleLevel1 = 0;
	private int iLastSampleLevel1 = 0;

	private boolean bRenderCaption = false;

	private float fAnimationScale = 1.0f;

	// embedded heat map
	private GLHeatMap glHeatMapView;
	private boolean bIsHeatmapInFocus = false;
	private float fWidthEHM = 0;

	// embedded dendrogram
	private GLDendrogram glGeneDendrogramView;
	private boolean bGeneDendrogramActive = false;
	private GLDendrogram glExperimentDendrogramView;
	private boolean bExperimentDendrogramActive = false;

	private boolean bRedrawTextures = false;

	// dragging stuff level 2
	private boolean bIsDraggingActiveLevel2 = false;
	private boolean bIsDraggingWholeBlockLevel2 = false;
	private boolean bDisableCursorDraggingLevel2 = false;
	private boolean bDisableBlockDraggingLevel2 = false;
	private int iDraggedCursorLevel2 = 0;
	private float fPosCursorFirstElementLevel2 = 0;
	private float fPosCursorLastElementLevel2 = 0;
	private boolean bActivateDraggingLevel2 = false;

	// dragging stuff level 1
	private boolean bIsDraggingActiveLevel1 = false;
	private boolean bIsDraggingWholeBlockLevel1 = false;
	private boolean bDisableCursorDraggingLevel1 = false;
	private boolean bDisableBlockDraggingLevel1 = false;
	private int iDraggedCursorLevel1 = 0;
	private float fPosCursorFirstElementLevel1 = 0;
	private float fPosCursorLastElementLevel1 = 0;
	private boolean bActivateDraggingLevel1 = false;

	// clustering/grouping stuff
	private boolean bSplitGroupExp = false;
	private boolean bSplitGroupGene = false;
	private int iGroupToSplit = 0;
	private Point DraggingPoint = null;

	// drag&drop stuff for clusters/groups
	private boolean bDragDropExpGroup = false;
	private int iExpGroupToDrag = -1;
	private boolean bActivateDraggingExperiments = false;
	private boolean bDragDropGeneGroup = false;
	private int iGeneGroupToDrag = -1;
	private boolean bActivateDraggingGenes = false;

	private GroupMergingActionListener groupMergingActionListener;
	private GroupInterChangingActionListener groupInterChangingActionListener;
	private UpdateViewListener updateViewListener;

	private org.eclipse.swt.graphics.Point upperLeftScreenPos = new org.eclipse.swt.graphics.Point(0, 0);

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHierarchicalHeatMap(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum);
		viewType = EManagedObjectType.GL_HIER_HEAT_MAP;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager = new SelectionManager.Builder(EIDType.EXPRESSION_INDEX).build();
		storageSelectionManager = new SelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();

		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		glKeyListener = new GLHierarchicalHeatMapKeyListener(this);

		createHeatMap();
		createDendrogram();
	}

	@Override
	public void init(GL gl) {
		glHeatMapView.initRemote(gl, this, glMouseListener, null, null);
		glGeneDendrogramView.initRemote(gl, this, glMouseListener, null, null);
		glExperimentDendrogramView.initRemote(gl, this, glMouseListener, null, null);
		initTextures(gl);
		// activateGroupHandling();
	}

	/**
	 * Function responsible for initialization of hierarchy levels. Depending on the amount of samples in the
	 * data set 1, 2, or 3 levels are used.
	 */
	private void initHierarchy() {

		if (set == null)
			return;

		iNumberOfElements = contentVA.size();

		if (iNumberOfElements < MIN_SAMPLES_SKIP_LEVEL_2) {

			bSkipLevel1 = true;
			bSkipLevel2 = true;

			iSamplesPerHeatmap = iNumberOfElements;
			iAlNumberSamples.clear();
			iAlNumberSamples.add(iNumberOfElements);

		}
		else if (iNumberOfElements < MIN_SAMPLES_SKIP_LEVEL_1) {
			bSkipLevel1 = true;
			bSkipLevel2 = false;

			iSamplesPerTexture = iNumberOfElements;
			iSamplesLevel2 = iNumberOfElements;
			iSamplesPerHeatmap = (int) Math.floor(iSamplesPerTexture / 3);

		}
		else {
			bSkipLevel1 = false;
			bSkipLevel2 = false;

			iSamplesPerTexture = (int) Math.floor(iNumberOfElements / 5);

			if (iSamplesPerTexture > MAX_SAMPLES_PER_TEXTURE)
				iSamplesPerTexture = MAX_SAMPLES_PER_TEXTURE;

			if (iSamplesPerTexture < MIN_SAMPLES_PER_TEXTURE && iNumberOfElements > MIN_SAMPLES_PER_TEXTURE)
				iSamplesPerTexture = MIN_SAMPLES_PER_TEXTURE;

			iSamplesLevel2 = 200;

			iSamplesPerHeatmap = (int) Math.floor(iSamplesLevel2 / 3);
		}

		if (iSamplesPerHeatmap > MAX_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = MAX_SAMPLES_PER_HEATMAP;

		if (iSamplesPerHeatmap < MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = MIN_SAMPLES_PER_HEATMAP;
	}

	@Override
	public void initLocal(GL gl) {

		bRenderStorageHorizontally = false;

		// Register keyboard listener to GL canvas
		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
			public void run() {
				parentGLCanvas.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);

	}

	@Override
	public void initRemote(GL gl, final AGLEventListener glParentView, GLMouseListener glMouseListener,
		IGLCanvasRemoteRendering remoteRenderingGLCanvas, GLInfoAreaManager infoAreaManager) {

		this.remoteRenderingGLView = remoteRenderingGLCanvas;

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

	/**
	 * Init (reset) the positions of cursors used for highlighting selected elements in stage 1 (overviewBar)
	 * 
	 * @param
	 */
	private void initPosCursorLevel1() {

		int iNumberSample = iNumberOfElements;

		if (bSkipLevel1)
			iSamplesLevel2 = iNumberSample;

		if (iSamplesLevel2 % 2 == 0) {
			iFirstSampleLevel1 = iPickedSampleLevel1 - (int) Math.floor(iSamplesLevel2 / 2) + 1;
			iLastSampleLevel1 = iPickedSampleLevel1 + (int) Math.floor(iSamplesLevel2 / 2);
		}
		else {
			iFirstSampleLevel1 = iPickedSampleLevel1 - (int) Math.ceil(iSamplesLevel2 / 2);
			iLastSampleLevel1 = iPickedSampleLevel1 + (int) Math.floor(iSamplesLevel2 / 2);
		}

		if (iPickedSampleLevel1 < iSamplesLevel2 / 2) {
			iPickedSampleLevel1 = (int) Math.floor(iSamplesLevel2 / 2);
			iFirstSampleLevel1 = 0;
			iLastSampleLevel1 = iSamplesLevel2 - 1;
		}
		else if (iPickedSampleLevel1 > iNumberSample - 1 - iSamplesLevel2 / 2) {
			iPickedSampleLevel1 = (int) Math.ceil(iNumberSample - iSamplesLevel2 / 2);
			iLastSampleLevel1 = iNumberSample - 1;
			iFirstSampleLevel1 = iNumberSample - iSamplesLevel2;
		}

	}

	/**
	 * Init (reset) the positions of cursors used for highlighting selected elements in stage 2 (texture)
	 * 
	 * @param
	 */
	private void initPosCursorLevel2() {

		if (bSkipLevel2) {
			iSamplesPerHeatmap = (int) Math.floor(iSamplesPerTexture / 3);
			if (iSamplesPerHeatmap % 2 == 0) {
				iFirstSampleLevel2 = iPickedSampleLevel2 - (int) Math.floor(iSamplesPerHeatmap / 2) + 1;
				iLastSampleLevel2 = iPickedSampleLevel2 + (int) Math.floor(iSamplesPerHeatmap / 2);
			}
			else {
				iFirstSampleLevel2 = iPickedSampleLevel2 - (int) Math.ceil(iSamplesPerHeatmap / 2);
				iLastSampleLevel2 = iPickedSampleLevel2 + (int) Math.floor(iSamplesPerHeatmap / 2);
			}
		}

		iSamplesPerHeatmap = iSamplesLevel2 / 3;

		if (iSamplesPerHeatmap >= 3 * MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = 2 * MIN_SAMPLES_PER_HEATMAP;
		else if (iSamplesPerHeatmap > MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = MIN_SAMPLES_PER_HEATMAP;

		iPickedSampleLevel2 = (int) Math.floor(iSamplesPerHeatmap / 2);
		iFirstSampleLevel2 = 0;
		iLastSampleLevel2 = iSamplesPerHeatmap - 1;
	}

	/**
	 * Function calculates number of textures and fills array lists to avoid NPEs after initialization.
	 * 
	 * @param
	 */
	private void calculateTextures() {

		if (bSkipLevel1 || bSkipLevel2) {

			iNrTextures = 1;

			AlTextures.clear();
			iAlNumberSamples.clear();

			Texture tempTextur = null;

			AlTextures.add(tempTextur);
			iAlNumberSamples.add(iSamplesPerTexture);

		}
		else {

			iNrTextures = (int) Math.ceil(contentVA.size() / iSamplesPerTexture);

			AlTextures.clear();
			iAlNumberSamples.clear();

			Texture tempTextur = null;

			for (int i = 0; i < iNrTextures; i++) {

				AlTextures.add(tempTextur);
				iAlNumberSamples.add(iSamplesPerTexture);
			}
		}
	}

	/**
	 * Init textures, build array of textures used for holding the whole examples
	 * 
	 * @param gl
	 */
	private void initTextures(final GL gl) {

		if (bSkipLevel1 && bSkipLevel2)
			return;

		if (bSkipLevel1) {

			// only one texture is needed

			AlTextures.clear();
			iAlNumberSamples.clear();

			Texture tempTextur;

			int iTextureHeight = contentVA.size();
			int iTextureWidth = storageVA.size();

			float fLookupValue = 0;
			float fOpacity = 0;

			FloatBuffer FbTemp = BufferUtil.newFloatBuffer(iTextureWidth * iTextureHeight * 4);

			for (Integer iContentIndex : contentVA) {
				for (Integer iStorageIndex : storageVA) {
					if (contentSelectionManager.checkStatus(ESelectionType.DESELECTED, iContentIndex)) {
						fOpacity = 0.3f;
					}
					else {
						fOpacity = 1.0f;
					}

					fLookupValue =
						set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);

					float[] fArMappingColor = colorMapper.getColor(fLookupValue);

					float[] fArRgba =
						{ fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity };

					FbTemp.put(fArRgba);
				}
			}
			FbTemp.rewind();

			TextureData texData =
				new TextureData(GL.GL_RGBA /* internalFormat */, iTextureWidth /* height */,
					iTextureHeight /* width */, 0 /* border */, GL.GL_RGBA /* pixelFormat */,
					GL.GL_FLOAT /* pixelType */, false /* mipmap */, false /* dataIsCompressed */,
					false /* mustFlipVertically */, FbTemp, null);

			tempTextur = TextureIO.newTexture(0);
			tempTextur.updateImage(texData);

			AlTextures.add(tempTextur);
			iAlNumberSamples.add(iSamplesPerTexture);

		}
		else {

			iNrTextures = (int) Math.ceil((contentVA.size() + 0.0001) / iSamplesPerTexture);

			AlTextures.clear();
			iAlNumberSamples.clear();

			Texture tempTextur;

			int iTextureHeight = contentVA.size();
			int iTextureWidth = storageVA.size();

			iSamplesPerTexture = (int) Math.ceil((iTextureHeight + 0.0001) / iNrTextures);

			float fLookupValue = 0;
			float fOpacity = 0;

			FloatBuffer[] FbTemp = new FloatBuffer[iNrTextures];

			for (int itextures = 0; itextures < iNrTextures; itextures++) {

				if (itextures == iNrTextures - 1) {
					iAlNumberSamples.add(iTextureHeight - iSamplesPerTexture * itextures);
					FbTemp[itextures] =
						BufferUtil.newFloatBuffer((iTextureHeight - iSamplesPerTexture * itextures)
							* iTextureWidth * 4);
				}
				else {
					iAlNumberSamples.add(iSamplesPerTexture);
					FbTemp[itextures] = BufferUtil.newFloatBuffer(iSamplesPerTexture * iTextureWidth * 4);
				}
			}

			int iCount = 0;
			int iTextureCounter = 0;

			for (Integer iContentIndex : contentVA) {
				iCount++;
				for (Integer iStorageIndex : storageVA) {
					if (contentSelectionManager.checkStatus(ESelectionType.DESELECTED, iContentIndex)) {
						fOpacity = 0.3f;
					}
					else {
						fOpacity = 1.0f;
					}

					fLookupValue =
						set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED, iContentIndex);

					float[] fArMappingColor = colorMapper.getColor(fLookupValue);

					float[] fArRgba =
						{ fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity };

					FbTemp[iTextureCounter].put(fArRgba);
				}
				if (iCount >= iAlNumberSamples.get(iTextureCounter)) {
					FbTemp[iTextureCounter].rewind();

					TextureData texData =
						new TextureData(GL.GL_RGBA /* internalFormat */, iTextureWidth /* height */,
							iAlNumberSamples.get(iTextureCounter) /* width */, 0 /* border */,
							GL.GL_RGBA /* pixelFormat */, GL.GL_FLOAT /* pixelType */, false /* mipmap */,
							false /* dataIsCompressed */, false /* mustFlipVertically */,
							FbTemp[iTextureCounter], null);

					tempTextur = TextureIO.newTexture(0);
					tempTextur.updateImage(texData);

					AlTextures.add(tempTextur);

					iTextureCounter++;
					iCount = 0;
				}
			}
		}
	}

	/**
	 * Create embedded heat map
	 * 
	 * @param
	 */
	private void createHeatMap() {
		CmdCreateGLEventListener cmdView =
			(CmdCreateGLEventListener) generalManager.getCommandManager().createCommandByType(
				ECommandType.CREATE_GL_HEAT_MAP_3D);

		float fHeatMapHeight = viewFrustum.getHeight();
		float fHeatMapWidth = viewFrustum.getWidth();

		cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, fHeatMapHeight, 0, fHeatMapWidth, -20, 20,
			set, -1);

		cmdView.doCommand();

		glHeatMapView = (GLHeatMap) cmdView.getCreatedObject();
		glHeatMapView.setUseCase(GeneralManager.get().getUseCase());
		glHeatMapView.setRenderedRemote(true);

	}

	/**
	 * Create embedded dendrogram
	 * 
	 * @param
	 */
	private void createDendrogram() {
		CmdCreateGLEventListener cmdView =
			(CmdCreateGLEventListener) generalManager.getCommandManager().createCommandByType(
				ECommandType.CREATE_GL_DENDROGRAM_HORIZONTAL);

		float fHeatMapHeight = viewFrustum.getHeight();
		float fHeatMapWidth = viewFrustum.getWidth();

		cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, fHeatMapHeight, 0, fHeatMapWidth, -20, 20,
			set, -1);

		cmdView.doCommand();

		glGeneDendrogramView = (GLDendrogram) cmdView.getCreatedObject();
		glGeneDendrogramView.setUseCase(GeneralManager.get().getUseCase());
		glGeneDendrogramView.setRenderedRemote(true);

		cmdView =
			(CmdCreateGLEventListener) generalManager.getCommandManager().createCommandByType(
				ECommandType.CREATE_GL_DENDROGRAM_VERTICAL);

		cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, fHeatMapHeight, 0, fHeatMapWidth, -20, 20,
			set, -1);

		cmdView.doCommand();

		glExperimentDendrogramView = (GLDendrogram) cmdView.getCreatedObject();
		glExperimentDendrogramView.setUseCase(GeneralManager.get().getUseCase());
		glExperimentDendrogramView.setRenderedRemote(true);
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		super.setDetailLevel(detailLevel);
	}

	@Override
	public void displayLocal(GL gl) {

		if (set == null)
			return;

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

		if (bIsDisplayListDirtyRemote) {
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
	}

	/**
	 * Function called any time a update is triggered external
	 * 
	 * @param
	 */
	@Override
	protected void reactOnExternalSelection(boolean scrollToSelection) {

		if (scrollToSelection && bSkipLevel2 == false) {
			// Set<Integer> setSelectedElements =
			// contentSelectionManager.getElements(ESelectionType.SELECTION);
			//
			// for (Integer selectedElement : setSelectedElements) {
			//
			// int index = contentVA.indexOf(selectedElement.intValue());
			//
			// // selected element is in level 3
			// if (index >= (iFirstSampleLevel1 + iFirstSampleLevel2)
			// && index <= (iFirstSampleLevel1 + iLastSampleLevel2 + 1)) {
			// // System.out.println("in range of level 3 --> do nothing");
			// return;
			// }
			// // selected element is in level 2
			// else if (index >= iFirstSampleLevel1 && index < iLastSampleLevel1) {
			// // System.out.println("in range of level 2 --> move level 3");
			// iFirstSampleLevel2 = index - iSamplesPerHeatmap / 2 - iFirstSampleLevel1;
			// iLastSampleLevel2 = index + iSamplesPerHeatmap / 2 - iFirstSampleLevel1 - 1;
			// if (iFirstSampleLevel2 < 0) {
			// iFirstSampleLevel2 = 0;
			// iLastSampleLevel2 = iSamplesPerHeatmap - 1;
			// }
			// if (iLastSampleLevel2 > iSamplesLevel2) {
			// iFirstSampleLevel2 = iSamplesLevel2 - iSamplesPerHeatmap;
			// iLastSampleLevel2 = iSamplesLevel2 - 1;
			// }
			// }
			// else {
			// // System.out.println("in range of level 1 --> move level 2 and 3");
			// iFirstSampleLevel1 = index - iSamplesLevel2 / 2;
			// iLastSampleLevel1 = index + iSamplesLevel2 / 2;
			//
			// if (iFirstSampleLevel1 <= 0) {
			// iFirstSampleLevel1 = 0;
			// iLastSampleLevel1 = iSamplesLevel2;
			// iFirstSampleLevel2 = index - iSamplesPerHeatmap / 2;
			// iLastSampleLevel2 = index + iSamplesPerHeatmap / 2;
			// if (iFirstSampleLevel2 < 0) {
			// iFirstSampleLevel2 = 0;
			// iLastSampleLevel2 = iSamplesPerHeatmap;
			// }
			// }
			// else if (iLastSampleLevel1 > iNumberOfElements) {
			// iFirstSampleLevel1 = iNumberOfElements - iSamplesLevel2;
			// iLastSampleLevel1 = iNumberOfElements;
			// iFirstSampleLevel2 = index - iSamplesPerHeatmap / 2 - iFirstSampleLevel1;
			// iLastSampleLevel2 = index + iSamplesPerHeatmap / 2 - iFirstSampleLevel1;
			// if (iLastSampleLevel2 > iSamplesLevel2) {
			// iFirstSampleLevel2 = iSamplesLevel2 - iSamplesPerHeatmap;
			// iLastSampleLevel2 = iSamplesLevel2;
			// }
			// }
			// else {
			// iFirstSampleLevel2 = index - iSamplesPerHeatmap / 2 - iFirstSampleLevel1;
			// iLastSampleLevel2 = index + iSamplesPerHeatmap / 2 - iFirstSampleLevel1;
			// }
			// }
			// return;
			// }

			Set<Integer> setMouseOverElements =
				contentSelectionManager.getElements(ESelectionType.MOUSE_OVER);
			for (Integer mouseOverElement : setMouseOverElements) {

				int index = contentVA.indexOf(mouseOverElement.intValue());

				// selected element is in level 3
				if (index >= (iFirstSampleLevel1 + iFirstSampleLevel2)
					&& index <= (iFirstSampleLevel1 + iLastSampleLevel2 + 1)) {
					// System.out.println("in range of level 3 --> do nothing");
					return;
				}
				// selected element is in level 2
				else if (index >= iFirstSampleLevel1 && index < iLastSampleLevel1) {
					// System.out.println("in range of level 2 --> move level 3");
					iFirstSampleLevel2 = index - iSamplesPerHeatmap / 2 - iFirstSampleLevel1;
					iLastSampleLevel2 = index + iSamplesPerHeatmap / 2 - iFirstSampleLevel1 - 1;
					if (iFirstSampleLevel2 < 0) {
						iFirstSampleLevel2 = 0;
						iLastSampleLevel2 = iSamplesPerHeatmap - 1;
					}
					if (iLastSampleLevel2 > iSamplesLevel2) {
						iFirstSampleLevel2 = iSamplesLevel2 - iSamplesPerHeatmap;
						iLastSampleLevel2 = iSamplesLevel2 - 1;
					}
				}
				else {
					// System.out.println("in range of level 1 --> move level 2 and 3");
					iFirstSampleLevel1 = index - iSamplesLevel2 / 2;
					iLastSampleLevel1 = index + iSamplesLevel2 / 2;

					if (iFirstSampleLevel1 <= 0) {
						iFirstSampleLevel1 = 0;
						iLastSampleLevel1 = iSamplesLevel2;
						iFirstSampleLevel2 = index - iSamplesPerHeatmap / 2;
						iLastSampleLevel2 = index + iSamplesPerHeatmap / 2;
						if (iFirstSampleLevel2 < 0) {
							iFirstSampleLevel2 = 0;
							iLastSampleLevel2 = iSamplesPerHeatmap;
						}
					}
					else if (iLastSampleLevel1 > iNumberOfElements) {
						iFirstSampleLevel1 = iNumberOfElements - iSamplesLevel2;
						iLastSampleLevel1 = iNumberOfElements;
						iFirstSampleLevel2 = index - iSamplesPerHeatmap / 2 - iFirstSampleLevel1;
						iLastSampleLevel2 = index + iSamplesPerHeatmap / 2 - iFirstSampleLevel1;
						if (iLastSampleLevel2 > iSamplesLevel2) {
							iFirstSampleLevel2 = iSamplesLevel2 - iSamplesPerHeatmap;
							iLastSampleLevel2 = iSamplesLevel2;
						}
					}
					else {
						iFirstSampleLevel2 = index - iSamplesPerHeatmap / 2 - iFirstSampleLevel1;
						iLastSampleLevel2 = index + iSamplesPerHeatmap / 2 - iFirstSampleLevel1;
					}
				}
			}
		}
	}

	@Override
	protected void reactOnVAChanges(IVirtualArrayDelta delta) {
		glHeatMapView.handleVirtualArrayUpdate(delta, getShortInfo());
		bRedrawTextures = true;
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
	 * Render a curved (nice looking) grey area between two views
	 * 
	 * @param gl
	 * @param startpoint1
	 * @param endpoint1
	 * @param startpoint2
	 * @param endpoint2
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

		Texture TextureMask = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_MASK_CURVE);
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

		Texture TextureMaskNeg = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_MASK_CURVE_NEG);
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
	 * Renders class assignments for experiments in level 2 (textures)
	 * 
	 * @param gl
	 */
	private void renderClassAssignmentsExperimentsLevel2(final GL gl) {

		float fWidth = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		int iNrElements = storageVA.size();
		float fWidthSamples = fWidth / iNrElements;
		float fxpos = 0;
		float fHeight = viewFrustum.getHeight();

		IGroupList groupList = storageVA.getGroupList();

		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			float classWidth = groupList.get(i).getNrElements() * fWidthSamples;

			if (groupList.get(i).getSelectionType() == ESelectionType.NORMAL)
				gl.glColor4f(0f, 0f, 1f, 0.5f);
			if (groupList.get(i).getSelectionType() == ESelectionType.SELECTION)
				gl.glColor4f(0f, 1f, 0f, 0.5f);

			// gl.glPushName(pickingManager.getPickingID(iUniqueID,
			// EPickingType.HIER_HEAT_MAP_EXPERIMENTS_GROUP, i));
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(fxpos, fHeight, 0);
			gl.glVertex3f(fxpos, fHeight + 0.1f, 0);
			gl.glVertex3f(fxpos + classWidth, fHeight + 0.1f, 0);
			gl.glVertex3f(fxpos + classWidth, fHeight, 0);
			gl.glEnd();
			// gl.glPopName();

			if (i == iNrClasses - 1)
				return;

			gl.glColor4f(0f, 0f, 1f, 1);
			gl.glLineWidth(1f);
			gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f(fxpos, fHeight, 0);
			// gl.glVertex3f(fxpos, fHeight + 0.1f, 0);
			gl.glVertex3f(fxpos + classWidth, fHeight + 0.1f, 0);
			gl.glVertex3f(fxpos + classWidth, 0, 0);
			gl.glEnd();

			fxpos = fxpos + classWidth;
		}
	}

	/**
	 * Renders class assignments for experiments in level 3 (embedded heat map)
	 * 
	 * @param gl
	 */
	private void renderClassAssignmentsExperimentsLevel3(final GL gl) {

		float fWidth = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		int iNrElements = storageVA.size();
		float fWidthSamples = fWidthEHM / iNrElements;
		float fxpos = 0;

		if (bSkipLevel2 == false)
			fxpos = fWidth + GAP_LEVEL2_3;

		float fHeight = 0;

		if (bExperimentDendrogramActive)
			fHeight = viewFrustum.getHeight() - 1.45f;
		else
			fHeight = viewFrustum.getHeight();

		IGroupList groupList = storageVA.getGroupList();

		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			float classWidth = groupList.get(i).getNrElements() * fWidthSamples;

			if (groupList.get(i).getSelectionType() == ESelectionType.NORMAL)
				gl.glColor4f(0f, 0f, 1f, 0.5f);
			if (groupList.get(i).getSelectionType() == ESelectionType.SELECTION)
				gl.glColor4f(0f, 1f, 0f, 0.5f);

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_EXPERIMENTS_GROUP, i));
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(fxpos, fHeight, 0);
			gl.glVertex3f(fxpos, fHeight + 0.1f, 0);
			gl.glVertex3f(fxpos + classWidth, fHeight + 0.1f, 0);
			gl.glVertex3f(fxpos + classWidth, fHeight, 0);
			gl.glEnd();
			gl.glPopName();

			if (i == iNrClasses - 1)
				return;

			gl.glColor4f(0f, 0f, 1f, 1);
			gl.glLineWidth(1f);
			gl.glBegin(GL.GL_LINES);
			// gl.glVertex3f(fxpos, 0.1f, 0.1f);
			// gl.glVertex3f(fxpos, fHeight + 0.1f, 0.1f);
			gl.glVertex3f(fxpos + classWidth, fHeight + 0.1f, 0.1f);
			gl.glVertex3f(fxpos + classWidth, 0, 0.1f);
			gl.glEnd();

			fxpos = fxpos + classWidth;
		}

	}

	/**
	 * Renders class assignments for genes in level 1 (overview bar)
	 * 
	 * @param gl
	 */
	private void renderClassAssignmentsGenesLevel1(final GL gl) {

		float fHeight = viewFrustum.getHeight();
		int iNrElements = iNumberOfElements;
		float fHeightSamples = fHeight / iNrElements;
		float fyPos = fHeight;

		IGroupList groupList = contentVA.getGroupList();

		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			float classHeight = groupList.get(i).getNrElements() * fHeightSamples;

			if (groupList.get(i).getSelectionType() == ESelectionType.NORMAL)
				gl.glColor4f(0f, 0f, 1f, 0.5f);
			if (groupList.get(i).getSelectionType() == ESelectionType.SELECTION)
				gl.glColor4f(0f, 1f, 0f, 0.5f);

			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_GENES_GROUP, i));
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(-0.1f, fyPos, 0);
			gl.glVertex3f(0, fyPos, 0);
			gl.glVertex3f(0, fyPos - classHeight, 0);
			gl.glVertex3f(-0.1f, fyPos - classHeight, 0);
			gl.glEnd();
			gl.glPopName();

			if (i == iNrClasses - 1)
				return;

			gl.glColor4f(0f, 0f, 1f, 1);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(-0.1f, fyPos - classHeight, 0.1f);
			gl.glVertex3f(0.1f, fyPos - classHeight, 0.1f);
			gl.glEnd();

			fyPos = fyPos - classHeight;
		}
	}

	/**
	 * Renders class assignments for genes in level 2 (textures)
	 * 
	 * @param gl
	 */
	private void renderClassAssignmentsGenesLevel2(final GL gl) {

		float fHeight = viewFrustum.getHeight();
		float fHeightSamples = fHeight / iSamplesLevel2;
		float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;

		// cluster border stuff
		int iIdxCluster = 0;
		int iCounter = iFirstSampleLevel1;

		Group group = contentVA.getGroupList().get(iIdxCluster);
		while (group.getNrElements() < iCounter) {
			iIdxCluster++;
			iCounter -= group.getNrElements();
			group = contentVA.getGroupList().get(iIdxCluster);
		}

		int iCnt = 0;

		for (int i = 0; i < iSamplesLevel2; i++) {

			if (iCounter == contentVA.getGroupList().get(iIdxCluster).getNrElements()) {

				if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == ESelectionType.NORMAL)
					gl.glColor4f(0f, 0f, 1f, 0.5f);
				if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == ESelectionType.SELECTION)
					gl.glColor4f(0f, 1f, 0f, 0.5f);

				if (bSkipLevel1)
					gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.HIER_HEAT_MAP_GENES_GROUP, iIdxCluster));
				gl.glBegin(GL.GL_QUADS);
				gl.glVertex3f(-0.1f, fHeight, 0);
				gl.glVertex3f(0, fHeight, 0);
				gl.glVertex3f(0, fHeight - fHeightSamples * iCnt, 0);
				gl.glVertex3f(-0.1f, fHeight - fHeightSamples * iCnt, 0);
				gl.glEnd();
				if (bSkipLevel1)
					gl.glPopName();

				gl.glColor4f(0f, 0f, 1f, 1);
				gl.glLineWidth(1f);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(-0.1f, fHeight - fHeightSamples * iCnt, 0.1f);
				gl.glVertex3f(fFieldWith, fHeight - fHeightSamples * iCnt, 0.1f);
				gl.glEnd();

				fHeight -= fHeightSamples * iCnt;
				// FIXME
				if (iIdxCluster == contentVA.getGroupList().size() - 1)
					break;
				iIdxCluster++;
				iCounter = 0;
				iCnt = 0;
			}
			iCnt++;

			iCounter++;
		}

		if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == ESelectionType.NORMAL)
			gl.glColor4f(0f, 0f, 1f, 0.5f);
		if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == ESelectionType.SELECTION)
			gl.glColor4f(0f, 1f, 0f, 0.5f);

		if (bSkipLevel1)
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_GENES_GROUP,
				iIdxCluster));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(-0.1f, fHeight, 0);
		gl.glVertex3f(0, fHeight, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(-0.1f, 0, 0);
		gl.glEnd();
		if (bSkipLevel1)
			gl.glPopName();

	}

	/**
	 * Renders class assignments for genes in level 3 (embedded heat map)
	 * 
	 * @param gl
	 */
	private void renderClassAssignmentsGenesLevel3(final GL gl) {

		float fHeight = 0;

		if (bExperimentDendrogramActive)
			fHeight = viewFrustum.getHeight() - 1.45f;
		else
			fHeight = viewFrustum.getHeight();

		float fHeightSamples = fHeight / iSamplesPerHeatmap;

		float fOffsetX = 0;

		if (bSkipLevel2 == false)
			fOffsetX = GAP_LEVEL2_3;

		// cluster border stuff
		int iIdxCluster = 0;
		int iCounter = iFirstSampleLevel1 + iFirstSampleLevel2;

		Group group = contentVA.getGroupList().get(iIdxCluster);
		while (group.getNrElements() < iCounter) {
			iIdxCluster++;
			iCounter -= group.getNrElements();
			group = contentVA.getGroupList().get(iIdxCluster);
		}

		int iCnt = 0;

		for (int i = 0; i < iSamplesPerHeatmap; i++) {

			if (iCounter == contentVA.getGroupList().get(iIdxCluster).getNrElements()) {

				if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == ESelectionType.NORMAL)
					gl.glColor4f(0f, 0f, 1f, 0.5f);
				if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == ESelectionType.SELECTION)
					gl.glColor4f(0f, 1f, 0f, 0.5f);

				if (bSkipLevel2)
					gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.HIER_HEAT_MAP_GENES_GROUP, iIdxCluster));
				gl.glBegin(GL.GL_QUADS);
				gl.glVertex3f(fOffsetX - 0.1f, fHeight, 0);
				gl.glVertex3f(fOffsetX, fHeight, 0);
				gl.glVertex3f(fOffsetX, fHeight - fHeightSamples * iCnt, 0);
				gl.glVertex3f(fOffsetX - 0.1f, fHeight - fHeightSamples * iCnt, 0);
				gl.glEnd();
				if (bSkipLevel2)
					gl.glPopName();

				gl.glColor4f(0f, 0f, 1f, 1);
				gl.glLineWidth(1f);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(fOffsetX - 0.1f, fHeight - fHeightSamples * iCnt, 0.1f);
				gl.glVertex3f(fOffsetX + fWidthEHM, fHeight - fHeightSamples * iCnt, 0.1f);
				gl.glEnd();

				fHeight -= fHeightSamples * iCnt;
				// FIXME
				if (iIdxCluster == contentVA.getGroupList().size() - 1)
					break;
				iIdxCluster++;
				iCounter = 0;
				iCnt = 0;
			}
			iCnt++;

			iCounter++;
		}

		if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == ESelectionType.NORMAL)
			gl.glColor4f(0f, 0f, 1f, 0.5f);
		if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == ESelectionType.SELECTION)
			gl.glColor4f(0f, 1f, 0f, 0.5f);

		if (bSkipLevel2)
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_GENES_GROUP,
				iIdxCluster));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(fOffsetX - 0.1f, fHeight, 0);
		gl.glVertex3f(fOffsetX, fHeight, 0);
		gl.glVertex3f(fOffsetX, 0, 0);
		gl.glVertex3f(fOffsetX - 0.1f, 0, 0);
		gl.glEnd();
		if (bSkipLevel2)
			gl.glPopName();

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

		float fHeightElem = fHeight / iNumberOfElements;

		float fStep = 0;

		gl.glColor4f(1f, 1f, 0f, 1f);

		for (int i = 0; i < iNrTextures; i++) {

			fStep = fHeightElem * iAlNumberSamples.get(iNrTextures - i - 1);

			AlTextures.get(iNrTextures - i - 1).enable();
			AlTextures.get(iNrTextures - i - 1).bind();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
			TextureCoords texCoords = AlTextures.get(iNrTextures - i - 1).getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_TEXTURE_SELECTION, iNrTextures - i));
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
			AlTextures.get(iNrTextures - i - 1).disable();
		}
	}

	/**
	 * Render marker in OverviewBar for visualization of the currently (in stage 2) rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerOverviewBar(final GL gl) {
		float fHeight = viewFrustum.getHeight();
		float fFieldWith = 0.1f;
		Vec3f startpoint1, endpoint1, startpoint2, endpoint2;

		float fHeightElem = fHeight / iNumberOfElements;

		if (bIsDraggingActiveLevel1 == false && bIsDraggingWholeBlockLevel1 == false) {
			fPosCursorFirstElementLevel1 = viewFrustum.getHeight() - iFirstSampleLevel1 * fHeightElem;
			fPosCursorLastElementLevel1 = viewFrustum.getHeight() - (iLastSampleLevel1 + 1) * fHeightElem;
		}

		// int iStartElem = 0;
		// int iLastElem = 0;
		//
		// boolean colorToggle = true;
		//
		// gl.glLineWidth(2f);
		// for (int currentTextureIdx = 0; currentTextureIdx < iNrTextures; currentTextureIdx++) {
		//
		// iStartElem = iLastElem;
		// iLastElem += iAlNumberSamples.get(currentTextureIdx);
		//
		// if (colorToggle)
		// gl.glColor4f(0f, 0f, 0f, 1f);
		// else
		// gl.glColor4f(1f, 1f, 1f, 1f);
		//
		// colorToggle = (colorToggle == true) ? false : true;
		//
		// gl.glBegin(GL.GL_LINE_LOOP);
		// gl.glVertex3f(0, fHeight - fHeightElem * iStartElem, 0);
		// gl.glVertex3f(fFieldWith, fHeight - fHeightElem * iStartElem, 0);
		// // Different handling for last texture. To avoid problems with visualization.
		// if (currentTextureIdx == iNrTextures - 1) {
		// gl.glVertex3f(fFieldWith, (fHeight - fHeightElem * iLastElem), 0);
		// gl.glVertex3f(0, (fHeight - fHeightElem * iLastElem), 0);
		// }
		// else {
		// gl.glVertex3f(fFieldWith, (fHeight - fHeightElem * iLastElem) + 0.01f, 0);
		// gl.glVertex3f(0, (fHeight - fHeightElem * iLastElem) + 0.01f, 0);
		// }
		// gl.glEnd();
		// }

		// selected domain level 1
		startpoint1 = new Vec3f(fFieldWith, fPosCursorFirstElementLevel1, 0);
		endpoint1 = new Vec3f(GAP_LEVEL1_2, fHeight, 0);
		startpoint2 = new Vec3f(fFieldWith, fPosCursorLastElementLevel1, 0);
		endpoint2 = new Vec3f(GAP_LEVEL1_2, 0, 0);
		renderSelectedDomain(gl, startpoint1, endpoint1, startpoint2, endpoint2);

		gl.glLineWidth(2f);

		gl.glColor4fv(MOUSE_OVER_COLOR, 0);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, fPosCursorFirstElementLevel1, 0);
		gl.glVertex3f(fFieldWith, fPosCursorFirstElementLevel1, 0);
		gl.glVertex3f(fFieldWith, fPosCursorLastElementLevel1, 0);
		gl.glVertex3f(0, fPosCursorLastElementLevel1, 0);
		// gl.glVertex3f(0, viewFrustum.getHeight() - iFirstSampleLevel1 * fHeightElem, 0);
		// gl.glVertex3f(fFieldWith, viewFrustum.getHeight() - iFirstSampleLevel1 * fHeightElem, 0);
		// gl.glVertex3f(fFieldWith, viewFrustum.getHeight() - (iLastSampleLevel1 + 1) * fHeightElem, 0);
		// gl.glVertex3f(0, viewFrustum.getHeight() - (iLastSampleLevel1 + 1) * fHeightElem, 0);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		gl
			.glVertex3f(0, viewFrustum.getHeight() - (iFirstSampleLevel1 + iFirstSampleLevel2) * fHeightElem,
				0);
		gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight() - (iFirstSampleLevel1 + iFirstSampleLevel2)
			* fHeightElem, 0);
		gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight()
			- ((iFirstSampleLevel1 + iLastSampleLevel2) + 1) * fHeightElem, 0);
		gl.glVertex3f(0, viewFrustum.getHeight() - ((iFirstSampleLevel1 + iLastSampleLevel2) + 1)
			* fHeightElem, 0);
		gl.glEnd();

		if (bRenderCaption == true) {
			renderCaption(gl, "nr:" + iSamplesLevel2, 0.0f, viewFrustum.getHeight() - iPickedSampleLevel1
				* fHeightElem, 0.004f);
		}

		gl.glColor4f(1f, 1f, 1f, 1f);
	}

	/**
	 * Render marker next to OverviewBar for visualization of selected elements in the data set
	 * 
	 * @param gl
	 */
	private void renderSelectedElementsOverviewBar(GL gl) {
		float fHeight = viewFrustum.getHeight();
		float fBarWidth = 0.1f;

		float fHeightElem = fHeight / iNumberOfElements;

		Set<Integer> setMouseOverElements = contentSelectionManager.getElements(ESelectionType.MOUSE_OVER);
		Set<Integer> setSelectedElements = contentSelectionManager.getElements(ESelectionType.SELECTION);

		gl.glLineWidth(2f);

		for (Integer mouseOverElement : setMouseOverElements) {

			int index = contentVA.indexOf(mouseOverElement.intValue());

			if ((index >= iFirstSampleLevel1 && index <= iLastSampleLevel1) == false) {
				gl.glColor4fv(MOUSE_OVER_COLOR, 0);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(fBarWidth, fHeight - fHeightElem * index, 0.001f);
				gl.glVertex3f(fBarWidth + 0.1f, fHeight - fHeightElem * index, 0.001f);
				gl.glEnd();
			}
		}

		for (Integer selectedElement : setSelectedElements) {

			int index = contentVA.indexOf(selectedElement.intValue());

			if ((index >= iFirstSampleLevel1 && index <= iLastSampleLevel1) == false) {
				gl.glColor4fv(SELECTED_COLOR, 0);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(fBarWidth, fHeight - fHeightElem * index, 0.001f);
				gl.glVertex3f(fBarWidth + 0.1f, fHeight - fHeightElem * index, 0.001f);
				gl.glEnd();
			}
		}
	}

	/**
	 * Render the second stage of the hierarchy (Texture)
	 * 
	 * @param gl
	 */
	private void renderTextureHeatMap(GL gl) {
		float fHeight;
		float fWidth;

		fHeight = viewFrustum.getHeight();
		fWidth = viewFrustum.getWidth() / 4.0f * fAnimationScale;

		int iFirstTexture = 0;
		int iLastTexture = 0;
		int iFirstElementFirstTexture = iFirstSampleLevel1;
		int iLastElementLastTexture = iLastSampleLevel1;
		int iNrTexturesInUse = 0;

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_FIELD_SELECTION, 1));

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);

		if (bSkipLevel1) {
			Texture TexTemp1 = AlTextures.get(0);
			TexTemp1.enable();
			TexTemp1.bind();
			TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2d(texCoords1.left(), texCoords1.top());
			gl.glVertex3f(0, 0, 0);
			gl.glTexCoord2d(texCoords1.left(), texCoords1.bottom());
			gl.glVertex3f(0, fHeight, 0);
			gl.glTexCoord2d(texCoords1.right(), texCoords1.bottom());
			gl.glVertex3f(fWidth, fHeight, 0);
			gl.glTexCoord2d(texCoords1.right(), texCoords1.top());
			gl.glVertex3f(fWidth, 0, 0);
			gl.glEnd();

			TexTemp1.disable();
		}
		else {

			while (iAlNumberSamples.get(iFirstTexture) < iFirstElementFirstTexture) {
				iFirstElementFirstTexture -= iAlNumberSamples.get(iFirstTexture);
				if (iFirstTexture < iAlNumberSamples.size() - 1)
					iFirstTexture++;
			}

			while (iLastElementLastTexture > iAlNumberSamples.get(iLastTexture)) {
				iLastElementLastTexture -= iAlNumberSamples.get(iLastTexture);
				iLastTexture++;
				if (iLastTexture == iAlNumberSamples.size() - 1) {
					if (iLastElementLastTexture > iSamplesPerTexture)
						iLastElementLastTexture = iSamplesPerTexture;
					break;
				}
			}

			iNrTexturesInUse = iLastTexture - iFirstTexture + 1;

			// System.out.println("\niSamplesPerTexture: " + iSamplesPerTexture + "   isamplesLevel2: "
			// + iSamplesLevel2);
			// System.out.println("iFirstTexture: " + iFirstTexture);
			// System.out.println("iFirstElementFirstTexture: " + iFirstElementFirstTexture);
			// System.out.println("iLastTexture: " + iLastTexture);
			// System.out.println("iLastElementLastTexture: " + iLastElementLastTexture);
			// System.out.println("iNrTexturesInUse: " + iNrTexturesInUse);

			if (iNrTexturesInUse == 1) {

				float fScalingFirstElement =
					(float) iFirstElementFirstTexture / iAlNumberSamples.get(iFirstTexture);
				float fScalingLastElement =
					(float) (iLastElementLastTexture + 1) / iAlNumberSamples.get(iFirstTexture);

				// System.out.println("dScalingFirstElement: " + dScalingFirstElement);
				// System.out.println("dScalingLastElement: " + dScalingLastElement);

				Texture TexTemp1 = AlTextures.get(iFirstTexture);
				TexTemp1.enable();
				TexTemp1.bind();
				TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.top() * fScalingLastElement);
				gl.glVertex3f(0, 0, 0);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.bottom() + fScalingFirstElement);
				gl.glVertex3f(0, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.bottom() + fScalingFirstElement);
				gl.glVertex3f(fWidth, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.top() * fScalingLastElement);
				gl.glVertex3f(fWidth, 0, 0);
				gl.glEnd();

				TexTemp1.disable();

			}
			else if (iNrTexturesInUse == 2) {

				float fScalingLastTexture = (float) (iLastElementLastTexture + 1) / iSamplesLevel2;

				float fRatioFirstTexture =
					(float) (iAlNumberSamples.get(iFirstTexture) - iFirstElementFirstTexture)
						/ iAlNumberSamples.get(iFirstTexture);
				float fRatioLastTexture =
					(float) (iLastElementLastTexture + 1) / iAlNumberSamples.get(iLastTexture);

				// System.out.println("dScalingFirstElement: " + dScalingFirstElement);
				// System.out.println("dScalingLastElement: " + dScalingLastElement);
				// double sum = dScalingLastElement + dScalingFirstElement;
				// System.out.println("sum : " + sum);

				Texture TexTemp1 = AlTextures.get(iFirstTexture);
				TexTemp1.enable();
				TexTemp1.bind();
				TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.top());
				gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.bottom() + (1 - fRatioFirstTexture));
				gl.glVertex3f(0, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.bottom() + (1 - fRatioFirstTexture));
				gl.glVertex3f(fWidth, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.top());
				gl.glVertex3f(fWidth, fHeight * fScalingLastTexture, 0);
				gl.glEnd();

				TexTemp1.disable();

				// gl.glBegin(GL.GL_LINES);
				// gl.glVertex3f(viewFrustum.getWidth(), fHeight * fScalingLastTexture, 0);
				// gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				// gl.glEnd();

				Texture TexTemp2 = AlTextures.get(iLastTexture);
				TexTemp2.enable();
				TexTemp2.bind();
				TextureCoords texCoords2 = TexTemp2.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(texCoords2.left(), texCoords2.top() * fRatioLastTexture);
				gl.glVertex3f(0, 0, 0);
				gl.glTexCoord2d(texCoords2.left(), texCoords2.bottom());
				gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords2.right(), texCoords2.bottom());
				gl.glVertex3f(fWidth, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords2.right(), texCoords2.top() * fRatioLastTexture);
				gl.glVertex3f(fWidth, 0, 0);
				gl.glEnd();

				TexTemp2.disable();
			}
			else if (iNrTexturesInUse == 3) {

				float fScalingFirstTexture =
					(float) (iSamplesPerTexture - iFirstElementFirstTexture) / iSamplesLevel2;
				float fScalingLastTexture = (float) (iLastElementLastTexture + 1) / iSamplesLevel2;

				float fRatioFirstTexture =
					(float) (iAlNumberSamples.get(iLastTexture) - iFirstElementFirstTexture)
						/ iAlNumberSamples.get(iFirstTexture);
				float fRatioLastTexture =
					(float) (iLastElementLastTexture + 1) / iAlNumberSamples.get(iLastTexture);

				Texture TexTemp1 = AlTextures.get(iFirstTexture);
				TexTemp1.enable();
				TexTemp1.bind();
				TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.top());
				gl.glVertex3f(0, fHeight * (1 - fScalingFirstTexture), 0);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.bottom() + (1 - fRatioFirstTexture));
				gl.glVertex3f(0, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.bottom() + (1 - fRatioFirstTexture));
				gl.glVertex3f(fWidth, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.top());
				gl.glVertex3f(fWidth, fHeight * (1 - fScalingFirstTexture), 0);
				gl.glEnd();

				TexTemp1.disable();

				// gl.glBegin(GL.GL_LINES);
				// gl.glVertex3f(viewFrustum.getWidth(), fHeight * (1 - fScalingFirstTexture), 0);
				// gl.glVertex3f(0, fHeight * (1 - fScalingFirstTexture), 0);
				// gl.glEnd();

				Texture TexTemp2 = AlTextures.get(iFirstTexture + 1);
				TexTemp2.enable();
				TexTemp2.bind();
				TextureCoords texCoords2 = TexTemp2.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(texCoords2.left(), texCoords2.top());
				gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords2.left(), texCoords2.bottom());
				gl.glVertex3f(0, fHeight * (1 - fScalingFirstTexture), 0);
				gl.glTexCoord2d(texCoords2.right(), texCoords2.bottom());
				gl.glVertex3f(fWidth, fHeight * (1 - fScalingFirstTexture), 0);
				gl.glTexCoord2d(texCoords2.right(), texCoords2.top());
				gl.glVertex3f(fWidth, fHeight * fScalingLastTexture, 0);
				gl.glEnd();

				TexTemp2.disable();

				// gl.glBegin(GL.GL_LINES);
				// gl.glVertex3f(viewFrustum.getWidth(), fHeight * fScalingLastTexture, 0);
				// gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				// gl.glEnd();

				Texture TexTemp3 = AlTextures.get(iLastTexture);
				TexTemp3.enable();
				TexTemp3.bind();
				TextureCoords texCoords3 = TexTemp3.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(texCoords3.left(), texCoords3.top() * fRatioLastTexture);
				gl.glVertex3f(0, 0, 0);
				gl.glTexCoord2d(texCoords3.left(), texCoords3.bottom());
				gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords3.right(), texCoords3.bottom());
				gl.glVertex3f(fWidth, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords3.right(), texCoords3.top() * fRatioLastTexture);
				gl.glVertex3f(fWidth, 0, 0);
				gl.glEnd();

				TexTemp3.disable();

			}
			else {
				// FIXME: do something smart
				System.out.println("something went wrong !!");
			}
		}

		gl.glPopName();
		gl.glPopAttrib();

	}

	/**
	 * Render marker in Texture for visualization of the currently (in stage 3) rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerTexture(final GL gl) {

		float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		// float fHeight = viewFrustum.getHeight();
		float fHeightSampleLevel2 = viewFrustum.getHeight() / iSamplesLevel2;

		Vec3f startpoint1, endpoint1, startpoint2, endpoint2;

		gl.glColor4f(1, 1, 0, 1);
		gl.glLineWidth(2f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, viewFrustum.getHeight() - iFirstSampleLevel2 * fHeightSampleLevel2, 0);
		gl.glVertex3f(fFieldWith, viewFrustum.getHeight() - iFirstSampleLevel2 * fHeightSampleLevel2, 0);
		gl.glVertex3f(fFieldWith, viewFrustum.getHeight() - (iLastSampleLevel2 + 1) * fHeightSampleLevel2, 0);
		gl.glVertex3f(0, viewFrustum.getHeight() - (iLastSampleLevel2 + 1) * fHeightSampleLevel2, 0);
		gl.glEnd();

		if (bIsDraggingActiveLevel2 == false) {
			fPosCursorFirstElementLevel2 = viewFrustum.getHeight() - iFirstSampleLevel2 * fHeightSampleLevel2;
			fPosCursorLastElementLevel2 =
				viewFrustum.getHeight() - (iLastSampleLevel2 + 1) * fHeightSampleLevel2;
		}

		startpoint1 =
			new Vec3f(fFieldWith, viewFrustum.getHeight() - iFirstSampleLevel2 * fHeightSampleLevel2, 0);
		endpoint1 = new Vec3f(fFieldWith + GAP_LEVEL2_3, viewFrustum.getHeight(), 0);
		startpoint2 =
			new Vec3f(fFieldWith, viewFrustum.getHeight() - (iLastSampleLevel2 + 1) * fHeightSampleLevel2, 0);
		endpoint2 = new Vec3f(fFieldWith + GAP_LEVEL2_3, 0.0f, 0);

		renderSelectedDomain(gl, startpoint1, endpoint1, startpoint2, endpoint2);

		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE);
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
				- iPickedSampleLevel2 * fHeightSampleLevel2, 0.005f);
		}
	}

	/**
	 * Renders button for activating/deactivating embedded dendrogram views
	 * 
	 * @param gl
	 */
	private void renderDendrogramButton(GL gl) {

		float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		float fHeight = viewFrustum.getHeight();

		float fLeftOffset = GAP_LEVEL1_2 + fWidthEHM + fFieldWith;

		if (bSkipLevel1 == false)
			fLeftOffset += 0.58f;

		if (bSkipLevel2)
			fLeftOffset = fWidthEHM;

		gl.glColor4f(1, 1, 1, 1);

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);

		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		if (set.getClusteredTreeExps() != null) {
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_ACTIVATE_VERTICAL_DENDROGRAM, 1));
			if (bExperimentDendrogramActive) {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fLeftOffset, fHeight, 0.1f);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fLeftOffset, fHeight + 0.1f, 0.1f);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fLeftOffset + 0.4f, fHeight + 0.1f, 0.1f);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fLeftOffset + 0.4f, fHeight, 0.1f);
				gl.glEnd();
			}
			else {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fLeftOffset, fHeight, 0.1f);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fLeftOffset, fHeight + 0.1f, 0.1f);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fLeftOffset + 0.4f, fHeight + 0.1f, 0.1f);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fLeftOffset + 0.4f, fHeight, 0.1f);
				gl.glEnd();
			}

			gl.glPopName();

		}

		if (set.getClusteredTreeGenes() != null) {
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_ACTIVATE_HORIZONTAL_DENDROGRAM, 1));
			if (bGeneDendrogramActive) {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(0.0f, -0.4f, 0.1f);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(0.0f, +0.0f, 0.1f);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(0.1f, +0.0f, 0.1f);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(0.1f, -0.4f, 0.1f);
				gl.glEnd();
			}
			else {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(0.0f, -0.4f, 0.1f);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(0.0f, +0.0f, 0.1f);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(0.1f, +0.0f, 0.1f);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(0.1f, -0.4f, 0.1f);
				gl.glEnd();
			}
			gl.glPopName();
		}
		tempTexture.disable();
	}

	/**
	 * Render marker in Texture (level 2) for visualization of selected elements in the data set
	 * 
	 * @param gl
	 */
	private void renderSelectedElementsTexture(GL gl) {

		float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		float fHeightSample = viewFrustum.getHeight() / iSamplesLevel2;
		float fExpWidth = fFieldWith / storageVA.size();

		gl.glEnable(GL.GL_LINE_STIPPLE);
		gl.glLineStipple(2, (short) 0xAAAA);

		gl.glColor4fv(MOUSE_OVER_COLOR, 0);
		Set<Integer> selectedSet = storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);
		int iColumnIndex = 0;
		for (int iTempLine : storageVA) {
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
		for (int iTempLine : storageVA) {
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

		Set<Integer> setMouseOverElements = contentSelectionManager.getElements(ESelectionType.MOUSE_OVER);
		gl.glColor4fv(MOUSE_OVER_COLOR, 0);

		for (Integer mouseOverElement : setMouseOverElements) {

			int selectedElement = contentVA.indexOf(mouseOverElement.intValue());

			if (selectedElement >= iFirstSampleLevel1 && selectedElement <= iLastSampleLevel1) {

				gl.glLineWidth(2f);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex3f(-0.0f, viewFrustum.getHeight() - (selectedElement - iFirstSampleLevel1)
					* fHeightSample, SELECTION_Z);
				gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight()
					- (selectedElement - iFirstSampleLevel1) * fHeightSample, SELECTION_Z);
				gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight()
					- (selectedElement + 1 - iFirstSampleLevel1) * fHeightSample, SELECTION_Z);
				gl.glVertex3f(-0.0f, viewFrustum.getHeight() - (selectedElement + 1 - iFirstSampleLevel1)
					* fHeightSample, SELECTION_Z);
				gl.glEnd();

			}
		}

		Set<Integer> setSelectedElements = contentSelectionManager.getElements(ESelectionType.SELECTION);
		gl.glColor4fv(SELECTED_COLOR, 0);

		for (Integer iSelectedElement : setSelectedElements) {

			int selectedElement = contentVA.indexOf(iSelectedElement.intValue());

			if (selectedElement >= iFirstSampleLevel1 && selectedElement <= iLastSampleLevel1) {

				gl.glLineWidth(2f);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex3f(-0.0f, viewFrustum.getHeight() - (selectedElement - iFirstSampleLevel1)
					* fHeightSample, SELECTION_Z);
				gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight()
					- (selectedElement - iFirstSampleLevel1) * fHeightSample, SELECTION_Z);
				gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight()
					- (selectedElement + 1 - iFirstSampleLevel1) * fHeightSample, SELECTION_Z);
				gl.glVertex3f(-0.0f, viewFrustum.getHeight() - (selectedElement + 1 - iFirstSampleLevel1)
					* fHeightSample, SELECTION_Z);
				gl.glEnd();

			}
		}
	}

	/**
	 * Render cursor used for controlling level 1
	 * 
	 * @param gl
	 */
	private void renderCursorLevel1(final GL gl) {
		gl.glTranslatef(0.1f, 0, 0);

		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_SMALL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		// Polygon for iFirstElement-Cursor
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_CURSOR_LEVEL1, 1));
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel1, 0);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorFirstElementLevel1, 0);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorFirstElementLevel1 + 0.1f, 0);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel1 + 0.1f, 0);
		gl.glEnd();
		gl.glPopName();

		// Polygon for iLastElement-Cursor
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_CURSOR_LEVEL1, 2));
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel1, 0);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorLastElementLevel1, 0);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorLastElementLevel1 - 0.1f, 0);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel1 - 0.1f, 0);
		gl.glEnd();
		gl.glPopName();

		// fill gap between cursor
		gl.glColor4f(0f, 0f, 0f, 0.45f);
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_BLOCK_CURSOR_LEVEL1,
			1));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorLastElementLevel1, 0);
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel1, 0);
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel1, 0);
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorFirstElementLevel1, 0);
		gl.glEnd();
		gl.glPopName();

		gl.glPopAttrib();

		gl.glTranslatef(-0.1f, 0, 0);
		tempTexture.disable();
	}

	/**
	 * Render cursor used for controlling hierarchical heat map (e.g. next Texture, previous Texture, set
	 * heatmap in focus)
	 * 
	 * @param gl
	 */
	private void renderCursorLevel2(final GL gl) {
		// float fHeight = viewFrustum.getHeight();
		// float fWidth = viewFrustum.getWidth() / 4.0f;

		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_SIDE);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);

		gl.glTranslatef(-viewFrustum.getWidth() / 4.0f * fAnimationScale, 0, 0);

		// if (iSelectorBar != 1) {
		// // Polygon for selecting previous texture
		// gl.glPushName(pickingManager
		// .getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_TEXTURE_CURSOR, 1));
		// // left
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(0.0f, fHeight, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(0.1f, fHeight, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(0.1f, fHeight + 0.1f, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(0.0f, fHeight + 0.1f, 0);
		// gl.glEnd();
		//
		// // right
		// if (bIsHeatmapInFocus) {
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 5 - 0.1f, fHeight, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 5, fHeight, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(fWidth / 5, fHeight + 0.1f, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(fWidth / 5 - 0.1f, fHeight + 0.1f, 0);
		// gl.glEnd();
		// }
		// else {
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(fWidth - 0.1f, fHeight, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(fWidth, fHeight, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(fWidth, fHeight + 0.1f, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(fWidth - 0.1f, fHeight + 0.1f, 0);
		// gl.glEnd();
		// }
		//
		// tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE);
		// tempTexture.enable();
		// tempTexture.bind();
		//
		// texCoords = tempTexture.getImageTexCoords();
		// // middle
		// if (bIsHeatmapInFocus) {
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 10 - 0.15f, fHeight, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 10 + 0.15f, fHeight, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(fWidth / 10 + 0.15f, fHeight + 0.1f, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(fWidth / 10 - 0.15f, fHeight + 0.1f, 0);
		// gl.glEnd();
		// }
		// else {
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 2 - 0.15f, fHeight, 0.001f);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 2 + 0.15f, fHeight, 0.001f);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(fWidth / 2 + 0.15f, fHeight + 0.1f, 0.001f);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(fWidth / 2 - 0.15f, fHeight + 0.1f, 0.001f);
		// gl.glEnd();
		//
		// // fill gap between middle and side
		// gl.glBegin(GL.GL_QUADS);
		// gl.glVertex3f(0.1f, fHeight, 0);
		// gl.glVertex3f(fWidth / 2 - 0.15f, fHeight, 0);
		// gl.glVertex3f(fWidth / 2 - 0.15f, fHeight + 0.1f, 0);
		// gl.glVertex3f(0.1f, fHeight + 0.1f, 0);
		// gl.glEnd();
		// gl.glBegin(GL.GL_QUADS);
		// gl.glVertex3f(fWidth - 0.1f, fHeight, 0);
		// gl.glVertex3f(fWidth / 2 + 0.15f, fHeight, 0);
		// gl.glVertex3f(fWidth / 2 + 0.15f, fHeight + 0.1f, 0);
		// gl.glVertex3f(fWidth - 0.1f, fHeight + 0.1f, 0);
		// gl.glEnd();
		// }
		// gl.glPopName();
		// }
		//
		// tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_SIDE);
		// tempTexture.enable();
		// tempTexture.bind();
		//
		// texCoords = tempTexture.getImageTexCoords();
		//
		// // if (iSelectorBar != iNrSelBar - 1) {
		// if (iSelectorBar != iNrSelBar) {
		// // Polygon for selecting next texture
		// gl.glPushName(pickingManager
		// .getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_TEXTURE_CURSOR, 2));
		// // left
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(0.0f, 0.0f, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(0.1f, 0.0f, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(0.1f, -0.1f, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(0.0f, -0.1f, 0);
		// gl.glEnd();
		//
		// // right
		// if (bIsHeatmapInFocus) {
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 5 - 0.1f, 0, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 5, 0, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(fWidth / 5, -0.1f, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(fWidth / 5 - 0.1f, -0.1f, 0);
		// gl.glEnd();
		// }
		// else {
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(fWidth - 0.1f, 0, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(fWidth, 0, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(fWidth, -0.1f, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(fWidth - 0.1f, -0.1f, 0);
		// gl.glEnd();
		// }
		//
		// tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE);
		// tempTexture.enable();
		// tempTexture.bind();
		//
		// texCoords = tempTexture.getImageTexCoords();
		// // middle
		// if (bIsHeatmapInFocus) {
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 10 - 0.15f, 0, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 10 + 0.15f, 0, 0);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(fWidth / 10 + 0.15f, -0.1f, 0);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(fWidth / 10 - 0.15f, -0.1f, 0);
		// gl.glEnd();
		// }
		// else {
		// gl.glBegin(GL.GL_POLYGON);
		// gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 2 - 0.15f, 0, 0.001f);
		// gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		// gl.glVertex3f(fWidth / 2 + 0.15f, 0, 0.001f);
		// gl.glTexCoord2f(texCoords.right(), texCoords.top());
		// gl.glVertex3f(fWidth / 2 + 0.15f, -0.1f, 0.001f);
		// gl.glTexCoord2f(texCoords.left(), texCoords.top());
		// gl.glVertex3f(fWidth / 2 - 0.15f, -0.1f, 0.001f);
		// gl.glEnd();
		//
		// // fill gap between middle and side
		// gl.glBegin(GL.GL_QUADS);
		// gl.glVertex3f(0.1f, 0, 0);
		// gl.glVertex3f(fWidth / 2 - 0.15f, 0, 0);
		// gl.glVertex3f(fWidth / 2 - 0.15f, -0.1f, 0);
		// gl.glVertex3f(0.1f, -0.1f, 0);
		// gl.glEnd();
		// gl.glBegin(GL.GL_QUADS);
		// gl.glVertex3f(fWidth - 0.1f, 0, 0);
		// gl.glVertex3f(fWidth / 2 + 0.15f, 0, 0);
		// gl.glVertex3f(fWidth / 2 + 0.15f, -0.1f, 0);
		// gl.glVertex3f(fWidth - 0.1f, -0.1f, 0);
		// gl.glEnd();
		// }
		// gl.glPopName();
		// }
		//
		gl.glTranslatef(viewFrustum.getWidth() / 4.0f * fAnimationScale, 0, 0);

		tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_SMALL);
		tempTexture.enable();
		tempTexture.bind();

		texCoords = tempTexture.getImageTexCoords();

		// Polygon for iFirstElement-Cursor
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_CURSOR_LEVEL2, 1));
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel2, 0);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorFirstElementLevel2, 0);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorFirstElementLevel2 + 0.1f, 0);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel2 + 0.1f, 0);
		gl.glEnd();
		gl.glPopName();

		// Polygon for iLastElement-Cursor
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_CURSOR_LEVEL2, 2));
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel2, 0);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorLastElementLevel2, 0);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorLastElementLevel2 - 0.1f, 0);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel2 - 0.1f, 0);
		gl.glEnd();
		gl.glPopName();

		// fill gap between cursor
		gl.glColor4f(0f, 0f, 0f, 0.45f);
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_BLOCK_CURSOR_LEVEL2,
			1));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorLastElementLevel2, 0);
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel2, 0);
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel2, 0);
		gl.glVertex3f(GAP_LEVEL1_2 / 4, fPosCursorFirstElementLevel2, 0);
		gl.glEnd();
		gl.glPopName();

		gl.glPopAttrib();
		tempTexture.disable();
	}

	@Override
	public void display(GL gl) {
		processEvents();
		if (generalManager.isWiiModeActive()) {
			handleWiiInput();
		}

		if (generalManager.getTrackDataProvider().isTrackModeActive()) {
			handleTrackInput(gl);
		}

		if (bIsDraggingActiveLevel2) {
			handleCursorDraggingLevel2(gl);
			if (glMouseListener.wasMouseReleased()) {
				bIsDraggingActiveLevel2 = false;
			}
		}

		if (bIsDraggingWholeBlockLevel2) {
			handleBlockDraggingLevel2(gl);
			if (glMouseListener.wasMouseReleased()) {
				bIsDraggingWholeBlockLevel2 = false;
			}
		}

		if (bIsDraggingActiveLevel1) {
			handleCursorDraggingLevel1(gl);
			if (glMouseListener.wasMouseReleased()) {
				bIsDraggingActiveLevel1 = false;
			}
		}

		if (bIsDraggingWholeBlockLevel1) {
			handleBlockDraggingLevel1(gl);
			if (glMouseListener.wasMouseReleased()) {
				bIsDraggingWholeBlockLevel1 = false;
			}
		}

		if (bDragDropExpGroup) {
			handleDragDropGroupExperiments(gl);
			if (glMouseListener.wasMouseReleased()) {
				bDragDropExpGroup = false;
			}
		}

		if (bDragDropGeneGroup) {
			handleDragDropGroupGenes(gl);
			if (glMouseListener.wasMouseReleased()) {
				bDragDropGeneGroup = false;
			}
		}

		// if (bSplitGroupExp) {
		// handleGroupSplitExperiments(gl);
		// if (glMouseListener.wasMouseReleased()) {
		// bSplitGroupExp = false;
		// }
		// }

		// if (bSplitGroupGene) {
		// handleGroupSplitGenes(gl);
		// if (glMouseListener.wasMouseReleased()) {
		// bSplitGroupGene = false;
		// }
		// }

		gl.glCallList(iGLDisplayListToCall);

		float fright = 0.0f;
		float ftop = viewFrustum.getTop();

		float fleftOffset = 0;

		if (bSkipLevel1 == false) {
			gl.glTranslatef(GAP_LEVEL2_3, 0, 0);
		}

		if (bSkipLevel2 == false) {

			if (bIsHeatmapInFocus) {
				fright = viewFrustum.getWidth() - 1.2f;
				fleftOffset = 0.095f + // width level 1 + boarder
					GAP_LEVEL1_2 + // width gap between level 1 and 2
					viewFrustum.getWidth() / 4f * 0.2f;
			}
			else {
				fright = viewFrustum.getWidth() - 2.75f;
				fleftOffset = 0.075f + // width level 1
					GAP_LEVEL1_2 + // width gap between level 1 and 2
					viewFrustum.getWidth() / 4f;
			}

			if (glHeatMapView.isInDefaultOrientation()) {
				gl.glTranslatef(fleftOffset, +0.4f, 0);
			}
			else {
				gl.glTranslatef(fleftOffset, -0.2f, 0);
			}
		}
		else {
			ftop = viewFrustum.getTop();
			if (bGeneDendrogramActive) {
				fright = viewFrustum.getWidth() - 1.4f;
				gl.glTranslatef(1.7f, -0.2f, 0);
			}
			else {

				fright = viewFrustum.getWidth();
				gl.glTranslatef(0.1f, -0.2f, 0);
			}
		}

		// render embedded heat map
		if (bExperimentDendrogramActive) {
			glHeatMapView.getViewFrustum().setTop(ftop - 1.45f);
		}
		else {
			glHeatMapView.getViewFrustum().setTop(ftop);
		}
		glHeatMapView.getViewFrustum().setRight(fright);
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION,
			glHeatMapView.getID()));
		glHeatMapView.displayRemote(gl);
		gl.glPopName();
		fWidthEHM = glHeatMapView.getViewFrustum().getWidth() - 0.95f;

		// render embedded experiment dendrogram
		if (bExperimentDendrogramActive) {
			gl.glTranslatef(0f, 3.6f, 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION, glExperimentDendrogramView
					.getID()));
			glExperimentDendrogramView.getViewFrustum().setTop(ftop - 3.6f);
			glExperimentDendrogramView.getViewFrustum().setRight(fWidthEHM);
			glExperimentDendrogramView.setDisplayListDirty();
			glExperimentDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -3.6f, 0f);
		}

		if (bSkipLevel2 == false) {
			if (glHeatMapView.isInDefaultOrientation()) {
				gl.glTranslatef(-fleftOffset, -0.4f, 0);
			}
			else {
				gl.glTranslatef(-fleftOffset, +0.2f, 0);
			}
		}
		else {
			if (bGeneDendrogramActive) {
				gl.glTranslatef(-1.7f, +0.2f, 0);
			}
			else {

				gl.glTranslatef(-0.1f, +0.2f, 0);
			}
		}

		if (bSkipLevel1 == false) {
			gl.glTranslatef(-GAP_LEVEL2_3, 0, 0);
		}

		// render embedded gene dendrogram
		if (bGeneDendrogramActive) {
			gl.glTranslatef(0f, 0.4f, 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION, glGeneDendrogramView.getID()));
			glGeneDendrogramView.getViewFrustum().setTop(ftop - 0.6f);
			glGeneDendrogramView.getViewFrustum().setRight(1.7f);
			glGeneDendrogramView.setDisplayListDirty();
			glGeneDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -0.4f, 0f);
		}

		contextMenu.render(gl, this);

	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bRedrawTextures) {
			initTextures(gl);
			bRedrawTextures = false;
		}

		if (bHasFrustumChanged) {
			glHeatMapView.setDisplayListDirty();
			glGeneDendrogramView.setDisplayListDirty();
			glExperimentDendrogramView.setDisplayListDirty();
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
		gl.glTranslatef(0.1f, 0.4f, 0);

		if (bGeneDendrogramActive)
			gl.glTranslatef(1.58f, 0, 0);

		renderDendrogramButton(gl);

		if (contentVA.getGroupList() != null && bSkipLevel1 == false)
			renderClassAssignmentsGenesLevel1(gl);

		// all stuff for rendering level 1 (overview bar)
		if (bSkipLevel1 == false) {
			handleTexturePickingLevel1(gl);
			renderOverviewBar(gl);
			renderMarkerOverviewBar(gl);
			renderSelectedElementsOverviewBar(gl);
			renderCursorLevel1(gl);

			gl.glTranslatef(GAP_LEVEL1_2 / 2, 0, 0);

			// render sub tree for level 2
			float fHeightSubTree = viewFrustum.getHeight();

			int lastIndexOfSubTree = 0;

			// FIXME: bad hack!!!
			try {
				lastIndexOfSubTree = contentVA.get(iLastSampleLevel1 + 1);
			}
			catch (IndexOutOfBoundsException e) {
				try {
					lastIndexOfSubTree = contentVA.get(iLastSampleLevel1);
				}
				catch (IndexOutOfBoundsException e1) {
					lastIndexOfSubTree = contentVA.get(iLastSampleLevel1 - 1);
				}
			}

			glGeneDendrogramView.renderSubTreeFromIndexToIndex(gl, contentVA.get(iFirstSampleLevel1),
				lastIndexOfSubTree, iSamplesLevel2, GAP_LEVEL1_2 / 2, fHeightSubTree);
			gl.glTranslatef(GAP_LEVEL1_2 / 2, 0, 0);

			gl.glColor4f(1f, 1f, 1f, 1f);
		}
		else {
			gl.glColor4f(1f, 1f, 1f, 1f);
		}

		if (bIsHeatmapInFocus || bGeneDendrogramActive) {
			fAnimationScale = 0.2f;
		}
		else {
			fAnimationScale = 1.0f;
		}

		// all stuff for rendering level 2 (textures)
		if (bSkipLevel2 == false) {
			handleTexturePickingLevel2(gl);
			renderTextureHeatMap(gl);
			renderMarkerTexture(gl);
			renderSelectedElementsTexture(gl);
			if (contentVA.getGroupList() != null)
				renderClassAssignmentsGenesLevel2(gl);
			if (storageVA.getGroupList() != null)
				renderClassAssignmentsExperimentsLevel2(gl);

			// render sub tree for level 3
			gl.glTranslatef(viewFrustum.getWidth() / 4.0f * fAnimationScale + GAP_LEVEL2_3 / 2, 0, 0);

			float fHeightSubTree = 0;
			if (bExperimentDendrogramActive)
				fHeightSubTree = viewFrustum.getHeight() - 1.45f;
			else
				fHeightSubTree = viewFrustum.getHeight();

			int lastIndexOfSubTree = 0;
			try {
				lastIndexOfSubTree = contentVA.get(iFirstSampleLevel1 + iLastSampleLevel2 + 1);
			}
			catch (IndexOutOfBoundsException e) {
				lastIndexOfSubTree = contentVA.get(iFirstSampleLevel1 + iLastSampleLevel2);
			}

			glGeneDendrogramView.renderSubTreeFromIndexToIndex(gl, contentVA.get(iFirstSampleLevel1
				+ iFirstSampleLevel2), lastIndexOfSubTree, iSamplesPerHeatmap, GAP_LEVEL2_3 / 2,
				fHeightSubTree);
			gl.glTranslatef(-(viewFrustum.getWidth() / 4.0f * fAnimationScale + GAP_LEVEL2_3 / 2), 0, 0);

		}

		setEmbeddedHeatMapData();

		if (bSkipLevel2 == false) {
			gl.glTranslatef(viewFrustum.getWidth() / 4.0f * fAnimationScale, 0, 0);
			renderCursorLevel2(gl);
		}

		if (contentVA.getGroupList() != null)
			renderClassAssignmentsGenesLevel3(gl);

		if (bSkipLevel2 == false)
			gl.glTranslatef(-(viewFrustum.getWidth() / 4.0f * fAnimationScale), 0, 0);

		if (storageVA.getGroupList() != null) {
			renderClassAssignmentsExperimentsLevel3(gl);
		}

		if (bSkipLevel1 == false) {
			gl.glTranslatef(-GAP_LEVEL1_2, 0, 0);
		}

		if (bGeneDendrogramActive)
			gl.glTranslatef(-1.58f, 0, 0);

		gl.glTranslatef(-0.1f, -0.4f, 0);
		viewFrustum.setTop(viewFrustum.getTop() + 0.6f);
		viewFrustum.setLeft(viewFrustum.getLeft() - 0.1f);

		gl.glEndList();
	}

	/**
	 * Function responsible for handling SelectionDelta for embedded heatmap
	 */
	private void setEmbeddedHeatMapData() {
		int iCount = iFirstSampleLevel1 + iFirstSampleLevel2;

		if (iCount < 0) {
			System.out.println("iFirstSampleLevel1: " + iFirstSampleLevel1);
			System.out.println("iFirstSampleLevel2: " + iFirstSampleLevel2);
			throw new IllegalStateException();
		}

		glHeatMapView.resetView();
		IVirtualArrayDelta delta = new VirtualArrayDelta(EVAType.CONTENT_EMBEDDED_HM, eFieldDataType);
		ISelectionDelta selectionDelta = new SelectionDelta(eFieldDataType);

		IVirtualArray currentVirtualArray = contentVA;
		int iIndex = 0;

		int iContentIndex = 0;
		int iStorageIndex = 0;

		Set<Integer> setMouseOverElements = contentSelectionManager.getElements(ESelectionType.MOUSE_OVER);
		Set<Integer> setSelectedElements = contentSelectionManager.getElements(ESelectionType.SELECTION);
		Set<Integer> setDeselectedElements = contentSelectionManager.getElements(ESelectionType.DESELECTED);

		for (int index = 0; index < iSamplesPerHeatmap; index++) {
			iIndex = iCount + index;
			if (iIndex < currentVirtualArray.size()) {
				iContentIndex = currentVirtualArray.get(iIndex);
			}

			delta.add(VADeltaItem.append(iContentIndex));

			// set elements mouse over in embedded heat Map
			for (Integer iSelectedID : setMouseOverElements) {
				if (iSelectedID == iContentIndex)
					selectionDelta.addSelection(iContentIndex, ESelectionType.MOUSE_OVER);
			}

			// set elements selected in embedded heat Map
			for (Integer iSelectedID : setSelectedElements) {
				if (iSelectedID == iContentIndex)
					selectionDelta.addSelection(iContentIndex, ESelectionType.SELECTION);
			}

			// set elements deselected in embedded heat Map
			for (Integer iSelectedID : setDeselectedElements) {
				if (iSelectedID == iContentIndex)
					selectionDelta.addSelection(iContentIndex, ESelectionType.DESELECTED);
			}
		}

		glHeatMapView.handleVirtualArrayUpdate(delta, getShortInfo());
		if (selectionDelta.size() > 0) {
			glHeatMapView.handleSelectionUpdate(selectionDelta, true, null);
		}

		// selected experiments

		SelectionCommand command = new SelectionCommand(ESelectionCommandType.RESET);
		glHeatMapView.handleStorageTriggerSelectionCommand(eExperimentDataType.getCategory(), command);

		IVirtualArrayDelta deltaExp = new VirtualArrayDelta(storageVAType, eExperimentDataType);
		ISelectionDelta selectionDeltaEx = new SelectionDelta(eExperimentDataType);

		IVirtualArray currentVirtualArrayEx = storageVA;

		setMouseOverElements = storageSelectionManager.getElements(ESelectionType.MOUSE_OVER);
		setSelectedElements = storageSelectionManager.getElements(ESelectionType.SELECTION);

		for (int index = 0; index < currentVirtualArrayEx.size(); index++) {
			iStorageIndex = currentVirtualArrayEx.get(index);

			deltaExp.add(VADeltaItem.append(iStorageIndex));

			// set elements mouse over in embedded heat Map
			for (Integer iSelectedID : setMouseOverElements) {
				if (iSelectedID == iStorageIndex)
					selectionDeltaEx.addSelection(iStorageIndex, ESelectionType.MOUSE_OVER);
			}

			// set elements selected in embedded heat Map
			for (Integer iSelectedID : setSelectedElements) {
				if (iSelectedID == iStorageIndex)
					selectionDeltaEx.addSelection(iStorageIndex, ESelectionType.SELECTION);
			}
		}

		glHeatMapView.handleVirtualArrayUpdate(deltaExp, getShortInfo());
		if (selectionDeltaEx.size() > 0) {
			glHeatMapView.handleSelectionUpdate(selectionDeltaEx, true, null);
		}

	}

	public void renderHorizontally(boolean bRenderStorageHorizontally) {

		if (glHeatMapView.isInDefaultOrientation()) {
			glHeatMapView.changeOrientation(false);
		}
		else {
			glHeatMapView.changeOrientation(true);
		}

		setDisplayListDirty();
	}

	@Override
	protected void initLists() {

		if (bRenderOnlyContext)
			contentVAType = EVAType.CONTENT_CONTEXT;
		else
			contentVAType = EVAType.CONTENT;

		contentVA = useCase.getVA(contentVAType);
		storageVA = useCase.getVA(EVAType.STORAGE);

		// In case of importing group info
		if (set.isGeneClusterInfo())
			contentVA.setGroupList(set.getGroupListGenes());
		if (set.isExperimentClusterInfo())
			storageVA.setGroupList(set.getGroupListExperiments());

		contentSelectionManager.resetSelectionManager();
		storageSelectionManager.resetSelectionManager();

		contentSelectionManager.setVA(contentVA);
		storageSelectionManager.setVA(storageVA);

		int iNumberOfColumns = contentVA.size();
		int iNumberOfRows = storageVA.size();

		for (int iRowCount = 0; iRowCount < iNumberOfRows; iRowCount++) {
			storageSelectionManager.initialAdd(storageVA.get(iRowCount));

		}

		// this for loop executes one per axis
		for (int iColumnCount = 0; iColumnCount < iNumberOfColumns; iColumnCount++) {
			contentSelectionManager.initialAdd(contentVA.get(iColumnCount));
		}

		setDisplayListDirty();

	}

	@Override
	public String getShortInfo() {
		return "Hierarchical Heat Map (" + contentVA.size() + useCase.getContentLabel(false, true) + " / "
			+ storageVA.size() + " experiments)";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Hierarchical Heat Map\n");

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
			if (dataFilterLevel == EDataFilterLevel.COMPLETE) {
				sInfoText.append("Showing all " + useCase.getContentLabel(false, true) + " in the dataset\n");
			}
			else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING) {
				sInfoText.append("Showing all " + useCase.getContentLabel(false, true)
					+ " that have a known DAVID ID mapping\n");
			}
			else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
				sInfoText
					.append("Showing all genes that are contained in any of the KEGG or Biocarta pathways\n");
			}
		}

		return sInfoText.toString();
	}

	/**
	 * Determine selected element in stage 1 (overview bar)
	 * 
	 * @param gl
	 */
	private void handleTexturePickingLevel1(GL gl) {

		int iNumberSample = iNumberOfElements;
		float fOffsety;
		float fHeightSample = viewFrustum.getHeight() / iNumberSample;
		float[] fArPickingCoords = new float[3];

		if (pickingPointLevel1 != null) {
			fArPickingCoords =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, pickingPointLevel1.x,
					pickingPointLevel1.y);
			fOffsety = viewFrustum.getHeight() - fArPickingCoords[1] + 0.4f;
			iPickedSampleLevel1 = (int) Math.ceil(fOffsety / fHeightSample);
			pickingPointLevel1 = null;

			if (iSamplesLevel2 % 2 == 0) {
				iFirstSampleLevel1 = iPickedSampleLevel1 - (int) Math.floor(iSamplesLevel2 / 2) + 1;
				iLastSampleLevel1 = iPickedSampleLevel1 + (int) Math.floor(iSamplesLevel2 / 2);
			}
			else {
				iFirstSampleLevel1 = iPickedSampleLevel1 - (int) Math.ceil(iSamplesLevel2 / 2);
				iLastSampleLevel1 = iPickedSampleLevel1 + (int) Math.floor(iSamplesLevel2 / 2);
			}

			if (iPickedSampleLevel1 < iSamplesLevel2 / 2) {
				iPickedSampleLevel1 = (int) Math.floor(iSamplesLevel2 / 2);
				iFirstSampleLevel1 = 0;
				iLastSampleLevel1 = iSamplesLevel2 - 1;
			}
			else if (iPickedSampleLevel1 > iNumberSample - 1 - iSamplesLevel2 / 2) {
				iPickedSampleLevel1 = (int) Math.ceil(iNumberSample - iSamplesLevel2 / 2);
				iLastSampleLevel1 = iNumberSample - 1;
				iFirstSampleLevel1 = iNumberSample - iSamplesLevel2;
			}
		}
		// initPosCursorLevel2();
		setDisplayListDirty();
		setEmbeddedHeatMapData();
	}

	/**
	 * Determine selected element in stage 2 (texture)
	 * 
	 * @param gl
	 */
	private void handleTexturePickingLevel2(GL gl) {

		int iNumberSample = iSamplesLevel2;
		float fOffsety;
		float fHeightSample = viewFrustum.getHeight() / iNumberSample;
		float[] fArPickingCoords = new float[3];

		if (pickingPointLevel2 != null) {
			fArPickingCoords =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, pickingPointLevel2.x,
					pickingPointLevel2.y);
			fOffsety = viewFrustum.getHeight() - fArPickingCoords[1] + 0.4f;
			iPickedSampleLevel2 = (int) Math.ceil(fOffsety / fHeightSample);
			pickingPointLevel2 = null;

			if (iSamplesLevel2 % 2 == 0) {
				iFirstSampleLevel2 = iPickedSampleLevel2 - (int) Math.floor(iSamplesPerHeatmap / 2) + 1;
				iLastSampleLevel2 = iPickedSampleLevel2 + (int) Math.floor(iSamplesPerHeatmap / 2);
			}
			else {
				iFirstSampleLevel2 = iPickedSampleLevel2 - (int) Math.ceil(iSamplesPerHeatmap / 2);
				iLastSampleLevel2 = iPickedSampleLevel2 + (int) Math.floor(iSamplesPerHeatmap / 2) - 1;
			}

			if (iPickedSampleLevel2 < iSamplesPerHeatmap / 2) {
				iPickedSampleLevel2 = (int) Math.floor(iSamplesPerHeatmap / 2);
				iFirstSampleLevel2 = 0;
				iLastSampleLevel2 = iSamplesPerHeatmap - 1;
			}
			else if (iPickedSampleLevel2 > iNumberSample - 1 - iSamplesPerHeatmap / 2) {
				iPickedSampleLevel2 = (int) Math.ceil(iNumberSample - iSamplesPerHeatmap / 2);
				iLastSampleLevel2 = iNumberSample - 1;
				iFirstSampleLevel2 = iNumberSample - iSamplesPerHeatmap;
			}
		}
		setDisplayListDirty();
		setEmbeddedHeatMapData();
	}

	/**
	 * Handles drag&drop of groups in experiment dimension
	 * 
	 * @param gl
	 */
	private void handleDragDropGroupExperiments(final GL gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];

		int iTargetIdx = 0;

		int iNrSamples = storageVA.size();

		float fgaps = 0;
		if (bSkipLevel1)
			fgaps = GAP_LEVEL2_3 + 0.2f;
		else
			fgaps = GAP_LEVEL1_2 + GAP_LEVEL2_3;

		float fleftOffset = 0.075f + // width level 1
			fgaps + viewFrustum.getWidth() / 4f * fAnimationScale;

		float fWidthSample = fWidthEHM / iNrSamples;

		int currentElement;

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		IGroupList groupList = storageVA.getGroupList();

		int iNrElementsInGroup = groupList.get(iExpGroupToDrag).getNrElements();
		float currentWidth = fWidthSample * iNrElementsInGroup;
		float fHeight = viewFrustum.getHeight();

		if (fArTargetWorldCoordinates[0] > fleftOffset
			&& fArTargetWorldCoordinates[0] < fleftOffset + fWidthEHM) {
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(fArTargetWorldCoordinates[0], fHeight, 0);
			gl.glVertex3f(fArTargetWorldCoordinates[0], fHeight - 0.1f, 0);
			gl.glVertex3f(fArTargetWorldCoordinates[0] + currentWidth, fHeight - 0.1f, 0);
			gl.glVertex3f(fArTargetWorldCoordinates[0] + currentWidth, fHeight, 0);
			gl.glEnd();
		}

		float fXPosRelease = fArTargetWorldCoordinates[0] - fleftOffset;

		currentElement = (int) Math.ceil(fXPosRelease / fWidthSample);

		int iElemOffset = 0;
		int cnt = 0;

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		for (Group currentGroup : groupList) {
			if (currentElement < (iElemOffset + currentGroup.getNrElements())) {
				iTargetIdx = cnt;
				if (iExpGroupToDrag < iTargetIdx)
					iElemOffset += currentGroup.getNrElements();
				break;
			}
			cnt++;
			iElemOffset += currentGroup.getNrElements();
		}

		float fPosDropMarker = fleftOffset + fWidthSample * iElemOffset;

		gl.glLineWidth(6f);
		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(fPosDropMarker, fHeight, 0);
		gl.glVertex3f(fPosDropMarker, fHeight - 0.2f, 0);
		gl.glEnd();

		if (glMouseListener.wasMouseReleased()) {

			if (groupList.move(storageVA, iExpGroupToDrag, iTargetIdx) == false)
				System.out.println("Move operation not allowed!");

			bDragDropExpGroup = false;
			bActivateDraggingExperiments = false;

			bRedrawTextures = true;
			setDisplayListDirty();
		}
	}

	/**
	 * Handles drag&drop of groups in gene dimension
	 * 
	 * @param gl
	 */
	private void handleDragDropGroupGenes(final GL gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];

		int iTargetIdx = 0;

		int iNumberSample = iNumberOfElements;
		float fOffsety;
		int currentElement;
		float fHeight = viewFrustum.getHeight() - 0.2f;

		float fHeightSample = (fHeight - 0.4f) / iNumberSample;

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		IGroupList groupList = contentVA.getGroupList();

		int iNrElementsInGroup = groupList.get(iGeneGroupToDrag).getNrElements();
		float currentHeight = fHeightSample * iNrElementsInGroup;

		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(0f, fArTargetWorldCoordinates[1], 0);
		gl.glVertex3f(0f, fArTargetWorldCoordinates[1] + currentHeight, 0);
		gl.glVertex3f(0.1f, fArTargetWorldCoordinates[1] + currentHeight, 0);
		gl.glVertex3f(0.1f, fArTargetWorldCoordinates[1], 0);
		gl.glEnd();

		fOffsety = viewFrustum.getHeight() - fArTargetWorldCoordinates[1] - 0.4f;
		currentElement = (int) Math.ceil(fOffsety / fHeightSample);

		int iElemOffset = 0;
		int cnt = 0;

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		for (Group currentGroup : groupList) {
			if (currentElement < (iElemOffset + currentGroup.getNrElements())) {
				iTargetIdx = cnt;
				if (iGeneGroupToDrag < iTargetIdx)
					iElemOffset += currentGroup.getNrElements();
				break;
			}
			cnt++;
			iElemOffset += currentGroup.getNrElements();
		}

		float fPosDropMarker = fHeight - (fHeightSample * iElemOffset);

		gl.glLineWidth(6f);
		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(0, fPosDropMarker, 0);
		gl.glVertex3f(0.2f, fPosDropMarker, 0);
		gl.glEnd();

		if (glMouseListener.wasMouseReleased()) {

			if (groupList.move(contentVA, iGeneGroupToDrag, iTargetIdx) == false)
				System.out.println("Move operation not allowed!");

			bDragDropGeneGroup = false;
			bActivateDraggingGenes = false;

			bRedrawTextures = true;
			setDisplayListDirty();
		}
	}

	/**
	 * Handles the dragging cursor for gene groups
	 * 
	 * @param gl
	 */
	@SuppressWarnings("unused")
	private void handleGroupSplitGenes(final GL gl) {
		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		float[] fArDraggedPoint = new float[3];

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		if (glMouseListener.wasMouseReleased()) {
			bSplitGroupGene = false;

			fArDraggedPoint =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, DraggingPoint.x,
					DraggingPoint.y);

			float fYPosDrag = fArDraggedPoint[1] - 0.4f;
			float fYPosRelease = fArTargetWorldCoordinates[1] - 0.4f;

			float fHeight = viewFrustum.getHeight() - 0.6f;
			int iNrSamples = contentVA.size();
			float fHeightSample = fHeight / iNrSamples;

			int iFirstSample = iNrSamples - (int) Math.floor(fYPosDrag / fHeightSample);
			int iLastSample = iNrSamples - (int) Math.ceil(fYPosRelease / fHeightSample);

			// System.out.println("von: " + fYPosDrag + " bis: " + fYPosRelease);
			// System.out.println("von: " + iFirstSample + " bis: " + iLastSample);

			if (contentVA.getGroupList().split(iGroupToSplit, iFirstSample, iLastSample) == false)
				System.out.println("Operation not allowed!!");
		}
	}

	/**
	 * Handles the dragging cursor for experiments groups
	 * 
	 * @param gl
	 */
	@SuppressWarnings("unused")
	private void handleGroupSplitExperiments(final GL gl) {
		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		float[] fArDraggedPoint = new float[3];

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		if (glMouseListener.wasMouseReleased()) {
			bSplitGroupExp = false;

			fArDraggedPoint =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, DraggingPoint.x,
					DraggingPoint.y);

			float fXPosDrag = fArDraggedPoint[0] - 0.7f;
			float fXPosRelease = fArTargetWorldCoordinates[0] - 0.7f;

			float fWidth = viewFrustum.getWidth() / 4.0f * fAnimationScale;
			int iNrSamples = storageVA.size();
			float fWidthSample = fWidth / iNrSamples;

			int iFirstSample = (int) Math.floor(fXPosDrag / fWidthSample);
			int iLastSample = (int) Math.ceil(fXPosRelease / fWidthSample);

			if (storageVA.getGroupList().split(iGroupToSplit, iLastSample, iFirstSample) == false)
				System.out.println("Operation not allowed!!");
		}
	}

	/**
	 * Function used for updating position of block (block of elements rendered in level 2) in case of
	 * dragging
	 * 
	 * @param gl
	 */
	private void handleBlockDraggingLevel1(final GL gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / iNumberOfElements;
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
		if (iSamplesLevel2 % 2 == 0) {
			if ((iselElement - (int) Math.floor(iSamplesLevel2 / 2) + 1) >= 0
				&& (iselElement + (int) Math.floor(iSamplesLevel2 / 2)) < iNumberOfElements) {
				iFirstSampleLevel1 = iselElement - (int) Math.floor(iSamplesLevel2 / 2) + 1;
				fPosCursorFirstElementLevel1 = fTextureHeight - (iFirstSampleLevel1 * fStep);
				iLastSampleLevel1 = iselElement + (int) Math.floor(iSamplesLevel2 / 2);
				fPosCursorLastElementLevel1 = fTextureHeight - ((iLastSampleLevel1 + 1) * fStep);
			}
		}
		else {
			if ((iselElement - (int) Math.ceil(iSamplesLevel2 / 2)) >= 0
				&& (iselElement + (int) Math.floor(iSamplesLevel2 / 2)) < iNumberOfElements) {
				iFirstSampleLevel1 = iselElement - (int) Math.ceil(iSamplesLevel2 / 2);
				fPosCursorFirstElementLevel1 = fTextureHeight - (iFirstSampleLevel1 * fStep);
				iLastSampleLevel1 = iselElement + (int) Math.floor(iSamplesLevel2 / 2);
				fPosCursorLastElementLevel1 = fTextureHeight - ((iLastSampleLevel1 + 1) * fStep);
			}
		}

		setDisplayListDirty();
		setEmbeddedHeatMapData();

		// System.out.println(" iSamplesPerTexture: " + iSamplesPerTexture + "  iFirstSampleLevel1: "
		// + iFirstSampleLevel1 + "  iLastSampleLevel1: " + iLastSampleLevel1);
		// System.out.println("fPosCursorFirstElementLevel2: " + fPosCursorFirstElementLevel2
		// + "  fPosCursorLastElementLevel2: " + fPosCursorLastElementLevel2);

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingWholeBlockLevel1 = false;
			bDisableCursorDraggingLevel1 = false;
			bActivateDraggingLevel1 = false;
			// initPosCursorLevel2();
		}
	}

	/**
	 * Function used for updating position of block (block of elements rendered in level 3) in case of
	 * dragging
	 * 
	 * @param gl
	 */
	private void handleBlockDraggingLevel2(final GL gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / iSamplesLevel2;
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
		if (iSamplesPerHeatmap % 2 == 0) {
			if ((iselElement - (int) Math.floor(iSamplesPerHeatmap / 2) + 1) >= 0
				&& (iselElement + (int) Math.floor(iSamplesPerHeatmap / 2)) < iSamplesLevel2) {
				iFirstSampleLevel2 = iselElement - (int) Math.floor(iSamplesPerHeatmap / 2) + 1;
				fPosCursorFirstElementLevel2 = fTextureHeight - (iFirstSampleLevel2 * fStep);
				iLastSampleLevel2 = iselElement + (int) Math.floor(iSamplesPerHeatmap / 2);
				fPosCursorLastElementLevel2 = fTextureHeight - ((iLastSampleLevel2 + 1) * fStep);
			}
		}
		else {
			if ((iselElement - (int) Math.ceil(iSamplesPerHeatmap / 2)) >= 0
				&& (iselElement + (int) Math.floor(iSamplesPerHeatmap / 2)) < iSamplesLevel2) {
				iFirstSampleLevel2 = iselElement - (int) Math.ceil(iSamplesPerHeatmap / 2);
				fPosCursorFirstElementLevel2 = fTextureHeight - (iFirstSampleLevel2 * fStep);
				iLastSampleLevel2 = iselElement + (int) Math.floor(iSamplesPerHeatmap / 2);
				fPosCursorLastElementLevel2 = fTextureHeight - ((iLastSampleLevel2 + 1) * fStep);
			}
		}

		setDisplayListDirty();
		setEmbeddedHeatMapData();

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingWholeBlockLevel2 = false;
			bDisableCursorDraggingLevel2 = false;
			bActivateDraggingLevel2 = false;
		}
	}

	/**
	 * Function used for updating cursor position of level 1 in case of dragging
	 * 
	 * @param gl
	 */
	private void handleCursorDraggingLevel1(final GL gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;
		int iNrSamples;

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / iNumberOfElements;
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		// cursor for iFirstElement
		if (iDraggedCursorLevel1 == 1) {
			if (fYPosMouse > fPosCursorLastElementLevel1 && fYPosMouse <= viewFrustum.getHeight() - 0.6f) {
				iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
				iNrSamples = iLastSampleLevel1 - iselElement + 1;
				if (iNrSamples >= MIN_SAMPLES_LEVEL_2 && iNrSamples < MAX_SAMPLES_LEVEL_2) {
					fPosCursorFirstElementLevel1 = fYPosMouse;
					iFirstSampleLevel1 = iselElement;
					iSamplesLevel2 = iLastSampleLevel1 - iFirstSampleLevel1 + 1;

					// // update Preference store
					// generalManager.getPreferenceStore().setValue(
					// PreferenceConstants.HM_NUM_SAMPLES_PER_TEXTURE, iSamplesPerTexture);
				}
			}
		}
		// cursor for iLastElement
		if (iDraggedCursorLevel1 == 2) {
			if (fYPosMouse < fPosCursorFirstElementLevel1 && fYPosMouse >= 0.0f) {
				iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
				iNrSamples = iselElement - iFirstSampleLevel1 + 1;
				if (iNrSamples >= MIN_SAMPLES_LEVEL_2 && iNrSamples < MAX_SAMPLES_LEVEL_2) {
					fPosCursorLastElementLevel1 = fYPosMouse;
					iLastSampleLevel1 = iselElement;
					iSamplesLevel2 = iLastSampleLevel1 - iFirstSampleLevel1 + 1;

					// // update Preference store
					// generalManager.getPreferenceStore().setValue(
					// PreferenceConstants.HM_NUM_SAMPLES_PER_TEXTURE, iSamplesPerTexture);
				}
			}
		}

		setDisplayListDirty();
		setEmbeddedHeatMapData();

		// System.out.println(" iSamplesPerTexture: " + iSamplesPerTexture + "  iFirstSampleLevel1: "
		// + iFirstSampleLevel1 + "  iLastSampleLevel1: " + iLastSampleLevel1);

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingActiveLevel1 = false;
			bDisableBlockDraggingLevel1 = false;
			bActivateDraggingLevel1 = false;
			// initPosCursorLevel2();
		}
	}

	/**
	 * Function used for updating cursor position of level 2 in case of dragging
	 * 
	 * @param gl
	 */
	private void handleCursorDraggingLevel2(final GL gl) {
		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;
		int iNrSamples;

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / iSamplesLevel2;
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		// cursor for iFirstElement
		if (iDraggedCursorLevel2 == 1) {
			if (fYPosMouse > fPosCursorLastElementLevel2 && fYPosMouse <= viewFrustum.getHeight() - 0.6f) {
				iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
				iNrSamples = iLastSampleLevel2 - iselElement + 1;
				if (iNrSamples >= MIN_SAMPLES_PER_HEATMAP && iNrSamples < MAX_SAMPLES_PER_HEATMAP) {
					fPosCursorFirstElementLevel2 = fYPosMouse;
					iFirstSampleLevel2 = iselElement;
					iSamplesPerHeatmap = iLastSampleLevel2 - iFirstSampleLevel2 + 1;

					// update Preference store
					// generalManager.getPreferenceStore().setValue(
					// PreferenceConstants.HM_NUM_SAMPLES_PER_HEATMAP, iSamplesPerHeatmap);
				}
			}
		}
		// cursor for iLastElement
		if (iDraggedCursorLevel2 == 2) {
			if (fYPosMouse < fPosCursorFirstElementLevel2 && fYPosMouse >= 0.0f) {
				iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
				iNrSamples = iselElement - iFirstSampleLevel2 + 1;
				if (iNrSamples >= MIN_SAMPLES_PER_HEATMAP && iNrSamples < MAX_SAMPLES_PER_HEATMAP) {
					fPosCursorLastElementLevel2 = fYPosMouse;
					iLastSampleLevel2 = iselElement;
					iSamplesPerHeatmap = iLastSampleLevel2 - iFirstSampleLevel2 + 1;

					// update Preference store
					// generalManager.getPreferenceStore().setValue(
					// PreferenceConstants.HM_NUM_SAMPLES_PER_HEATMAP, iSamplesPerHeatmap);
				}
			}
		}

		setDisplayListDirty();
		setEmbeddedHeatMapData();

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingActiveLevel2 = false;
			bDisableBlockDraggingLevel2 = false;
			bActivateDraggingLevel2 = false;
		}
	}

	private void deactivateAllDraggingCursor() {
		bActivateDraggingExperiments = false;
		bActivateDraggingGenes = false;
		bActivateDraggingLevel1 = false;
		bActivateDraggingLevel2 = false;
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		switch (ePickingType) {

			// handling the groups/clusters of genes
			case HIER_HEAT_MAP_GENES_GROUP:
				switch (pickingMode) {

					case CLICKED:
						contentVA.getGroupList().get(iExternalID).toggleSelectionType();
						deactivateAllDraggingCursor();
						bActivateDraggingGenes = true;

						// ArrayList<Float> representatives =
						// contentVA.getGroupList().determineRepresentativeElement(set, contentVA,
						// storageVA, iExternalID, true);

						// set node in tree selected
						// if (contentVA.getGroupList().get(iExternalID).getClusterNode() != null) {
						// contentVA.getGroupList().get(iExternalID).getClusterNode().toggleSelectionType();
						// }

						// System.out.println(contentVA.getGroupList().get(iExternalID).getIdxExample());
						// System.out.println(idMappingManager.getID(EIDType.EXPRESSION_INDEX,
						// EIDType.GENE_SYMBOL, contentVA.getGroupList().get(iExternalID).getIdxExample()));

						setDisplayListDirty();
						break;

					case DRAGGED:
						if (bActivateDraggingGenes == false)
							return;
						// drag&drop for groups
						if (bDragDropGeneGroup == false) {
							bDragDropGeneGroup = true;
							bDragDropExpGroup = false;
							iGeneGroupToDrag = iExternalID;
						}

						// group splitting
						// if (bSplitGroupGene == false) {
						// bSplitGroupGene = true;
						// bSplitGroupExp = false;
						// iGroupToSplit = iExternalID;
						// DraggingPoint = pick.getPickedPoint();
						// }
						setDisplayListDirty();
						break;

					case RIGHT_CLICKED:

						boolean bEnableInterchange = false;
						boolean bEnableMerge = false;
						int iNrSelectedGroups = 0;

						IGroupList tempGroupList = contentVA.getGroupList();

						for (Group group : tempGroupList) {
							if (group.getSelectionType() == ESelectionType.SELECTION)
								iNrSelectedGroups++;
						}

						if (iNrSelectedGroups >= 2) {

							bEnableMerge = true;

							if (iNrSelectedGroups == 2)
								bEnableInterchange = true;

							GroupContextMenuItemContainer groupContextMenuItemContainer =
								new GroupContextMenuItemContainer();
							groupContextMenuItemContainer.setContextMenuFlags(true, bEnableMerge,
								bEnableInterchange);
							contextMenu.addItemContanier(groupContextMenuItemContainer);

							// if (!isRenderedRemote()) {
							contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
								getParentGLCanvas().getHeight());
							contextMenu.setMasterGLView(this);
							// }
						}
						break;

					case MOUSE_OVER:
						// System.out.print("genes group " + iExternalID);
						// System.out.print(" number elements in group: ");
						// System.out.println(contentVA.getGroupList().get(iExternalID)
						// .getNrElements());
						// setDisplayListDirty();
						break;
				}
				break;

			// handling the groups/clusters of experiments
			case HIER_HEAT_MAP_EXPERIMENTS_GROUP:
				switch (pickingMode) {
					case CLICKED:
						storageVA.getGroupList().get(iExternalID).toggleSelectionType();
						deactivateAllDraggingCursor();
						bActivateDraggingExperiments = true;

						// ArrayList<Float> representatives =
						// contentVA.getGroupList().determineRepresentativeElement(set, contentVA,
						// storageVA, iExternalID, false);

						// set node in tree selected
						// if (storageVA.getGroupList().get(iExternalID).getClusterNode() != null) {
						// storageVA.getGroupList().get(iExternalID).getClusterNode().toggleSelectionType();
						// }

						// System.out.println(storageVA.getGroupList().get(iExternalID).getIdxExample());
						// System.out.println(set.get(storageVA.getGroupList().get(iExternalID).getIdxExample())
						// .getLabel());

						setDisplayListDirty();
						break;

					case DRAGGED:
						if (bActivateDraggingExperiments == false)
							return;
						// drag&drop for groups
						if (bDragDropExpGroup == false) {
							bDragDropExpGroup = true;
							bDragDropGeneGroup = false;
							iExpGroupToDrag = iExternalID;
						}

						// group splitting
						// if (bSplitGroupExp == false) {
						// bSplitGroupExp = true;
						// bSplitGroupGene = false;
						// iGroupToSplit = iExternalID;
						// DraggingPoint = pick.getPickedPoint();
						// }
						setDisplayListDirty();
						break;

					case RIGHT_CLICKED:

						boolean bEnableInterchange = false;
						boolean bEnableMerge = false;
						int iNrSelectedGroups = 0;

						IGroupList tempGroupList = storageVA.getGroupList();

						for (Group group : tempGroupList) {
							if (group.getSelectionType() == ESelectionType.SELECTION)
								iNrSelectedGroups++;
						}

						if (iNrSelectedGroups >= 2) {

							bEnableMerge = true;

							if (iNrSelectedGroups == 2)
								bEnableInterchange = true;

							GroupContextMenuItemContainer groupContextMenuItemContainer =
								new GroupContextMenuItemContainer();
							groupContextMenuItemContainer.setContextMenuFlags(false, bEnableMerge,
								bEnableInterchange);
							contextMenu.addItemContanier(groupContextMenuItemContainer);

							// if (!isRenderedRemote()) {
							contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
								getParentGLCanvas().getHeight());
							contextMenu.setMasterGLView(this);
							// }
						}
						break;

					case MOUSE_OVER:
						// System.out.print("patients group " + iExternalID);
						// System.out.print(" number elements in group: ");
						// System.out.println(storageVA.getGroupList().get(iExternalID)
						// .getNrElements());
						// setDisplayListDirty();
						break;
				}
				break;

			// handle click on button for setting EHM in focus
			case HIER_HEAT_MAP_INFOCUS_SELECTION:
				switch (pickingMode) {

					case CLICKED:

						bIsHeatmapInFocus = bIsHeatmapInFocus == true ? false : true;
						bGeneDendrogramActive = false;
						glHeatMapView.setDisplayListDirty();
						glExperimentDendrogramView.setDisplayListDirty();
						setDisplayListDirty();

						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle click on button for setting vertical dendrogram active
			case HIER_HEAT_MAP_ACTIVATE_VERTICAL_DENDROGRAM:
				switch (pickingMode) {

					case CLICKED:

						bExperimentDendrogramActive = bExperimentDendrogramActive == true ? false : true;
						glHeatMapView.setDisplayListDirty();
						glGeneDendrogramView.setDisplayListDirty();
						setDisplayListDirty();

						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle click on button for setting horizontal dendrogram active
			case HIER_HEAT_MAP_ACTIVATE_HORIZONTAL_DENDROGRAM:
				switch (pickingMode) {

					case CLICKED:

						bGeneDendrogramActive = bGeneDendrogramActive == true ? false : true;
						bIsHeatmapInFocus = false;
						glHeatMapView.setDisplayListDirty();
						setDisplayListDirty();

						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle click on button for selecting next/previous texture in level 1 and 2
			// case HIER_HEAT_MAP_TEXTURE_CURSOR:
			// switch (pickingMode) {
			// case CLICKED:
			//
			// if (bSkipLevel1 == false) {
			//
			// if (iExternalID == 1) {
			// iSelectorBar--;
			// initPosCursorLevel2();
			// setEmbeddedHeatMapData();
			// setDisplayListDirty();
			// }
			// if (iExternalID == 2) {
			// iSelectorBar++;
			// initPosCursorLevel2();
			// setEmbeddedHeatMapData();
			// setDisplayListDirty();
			// }
			//
			// setDisplayListDirty();
			// }
			// break;
			//
			// case DRAGGED:
			// break;
			//
			// case MOUSE_OVER:
			// break;
			// }
			// break;

			// handle dragging cursor for first and last element of block in level 1
			case HIER_HEAT_MAP_CURSOR_LEVEL1:
				switch (pickingMode) {
					case CLICKED:
						deactivateAllDraggingCursor();
						bActivateDraggingLevel1 = true;
						break;

					case DRAGGED:
						if (bDisableCursorDraggingLevel1)
							return;
						if (bActivateDraggingLevel1 == false)
							return;
						bIsDraggingActiveLevel1 = true;
						bDisableBlockDraggingLevel1 = true;
						iDraggedCursorLevel1 = iExternalID;
						setDisplayListDirty();
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle dragging cursor for whole block in level 1
			case HIER_HEAT_MAP_BLOCK_CURSOR_LEVEL1:
				switch (pickingMode) {
					case CLICKED:
						deactivateAllDraggingCursor();
						bActivateDraggingLevel1 = true;
						break;

					case DRAGGED:
						if (bDisableBlockDraggingLevel1)
							return;
						if (bActivateDraggingLevel1 == false)
							return;
						bIsDraggingWholeBlockLevel1 = true;
						bDisableCursorDraggingLevel1 = true;
						iDraggedCursorLevel1 = iExternalID;
						setDisplayListDirty();
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle dragging cursor for first and last element of block in level 2
			case HIER_HEAT_MAP_CURSOR_LEVEL2:
				switch (pickingMode) {
					case CLICKED:
						deactivateAllDraggingCursor();
						bActivateDraggingLevel2 = true;
						break;

					case DRAGGED:
						if (bDisableCursorDraggingLevel2)
							return;
						if (bActivateDraggingLevel2 == false)
							return;
						bIsDraggingActiveLevel2 = true;
						bDisableBlockDraggingLevel2 = true;
						iDraggedCursorLevel2 = iExternalID;
						setDisplayListDirty();
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle dragging cursor for whole block in level 2
			case HIER_HEAT_MAP_BLOCK_CURSOR_LEVEL2:
				switch (pickingMode) {
					case CLICKED:
						deactivateAllDraggingCursor();
						bActivateDraggingLevel2 = true;
						break;

					case DRAGGED:
						if (bDisableBlockDraggingLevel2)
							return;
						if (bActivateDraggingLevel2 == false)
							return;
						bIsDraggingWholeBlockLevel2 = true;
						bDisableCursorDraggingLevel2 = true;
						iDraggedCursorLevel2 = iExternalID;
						setDisplayListDirty();
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle click on level 1 (overview bar)
			case HIER_HEAT_MAP_TEXTURE_SELECTION:
				switch (pickingMode) {
					case CLICKED:

						// if (bSkipLevel1 == false) {
						// iSelectorBar = iExternalID;
						// // if (iSelectorBar == iNrSelBar) {
						// // iSelectorBar--;
						// // }
						// initPosCursorLevel2();
						// setEmbeddedHeatMapData();
						// setDisplayListDirty();
						// }
						pickingPointLevel1 = pick.getPickedPoint();
						setEmbeddedHeatMapData();
						setDisplayListDirty();
						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle click on level 2
			case HIER_HEAT_MAP_FIELD_SELECTION:
				switch (pickingMode) {
					case CLICKED:
						pickingPointLevel2 = pick.getPickedPoint();
						setEmbeddedHeatMapData();
						setDisplayListDirty();
						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle click on level 3 (EHM)
			case HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION:
				switch (pickingMode) {
					case CLICKED:
						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;

					case RIGHT_CLICKED:
						contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
							getParentGLCanvas().getHeight());
						contextMenu.setMasterGLView(this);
						break;
				}
				break;

			// handle click on gene dendrogram
			case HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION:
				switch (pickingMode) {
					case CLICKED:
						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;

					case RIGHT_CLICKED:
						contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
							getParentGLCanvas().getHeight());
						contextMenu.setMasterGLView(this);
						break;
				}
				break;

			// handle click on gene dendrogram
			case HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION:
				switch (pickingMode) {
					case CLICKED:
						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;

					case RIGHT_CLICKED:
						contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas().getWidth(),
							getParentGLCanvas().getHeight());
						contextMenu.setMasterGLView(this);
						break;
				}
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
	public void clearAllSelections() {

		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		// iPickedSampleLevel1 = 0;
		if (bSkipLevel1 == false)
			initPosCursorLevel1();
		bRedrawTextures = true;
		setDisplayListDirty();
		setEmbeddedHeatMapData();
		glHeatMapView.setDisplayListDirty();
		glGeneDendrogramView.setDisplayListDirty();
		glExperimentDendrogramView.setDisplayListDirty();

		// group/cluster selections
		if (storageVA.getGroupList() != null) {
			IGroupList groupList = storageVA.getGroupList();

			for (Group group : groupList)
				group.setSelectionType(ESelectionType.NORMAL);
		}
		if (contentVA.getGroupList() != null) {
			IGroupList groupList = contentVA.getGroupList();

			for (Group group : groupList)
				group.setSelectionType(ESelectionType.NORMAL);
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

	@SuppressWarnings("unused")
	private void activateGroupHandling() {

		if (contentVA.getGroupList() == null) {
			GroupList groupList = new GroupList(0);
			Group group = new Group(contentVA.size(), false, 0, ESelectionType.NORMAL);
			groupList.append(group);
			contentVA.setGroupList(groupList);
		}

		if (storageVA.getGroupList() == null) {
			GroupList groupList = new GroupList(0);
			Group group = new Group(storageVA.size(), false, 0, ESelectionType.NORMAL);
			groupList.append(group);
			storageVA.setGroupList(groupList);
		}

		setDisplayListDirty();

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

	private void handleTrackInput(final GL gl) {

		// // TODO: very performance intensive - better solution needed (only in reshape)!
		// getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {
		// @Override
		// public void run() {
		// upperLeftScreenPos = getParentGLCanvas().getParentComposite().toDisplay(1, 1);
		// }
		// });
		//
		// Rectangle screenRect = getParentGLCanvas().getBounds();
		// float[] fArTrackPos = generalManager.getTrackDataProvider().getEyeTrackData();
		//
		// fArTrackPos[0] -= upperLeftScreenPos.x;
		// fArTrackPos[1] -= upperLeftScreenPos.y;
		//
		// GLHelperFunctions.drawPointAt(gl, new Vec3f(fArTrackPos[0] / screenRect.width * 8f,
		// (1f - fArTrackPos[1] / screenRect.height) * 8f * fAspectRatio, 0.01f));
		//
		// float fSwitchBorder = 200;
		//
		// if (!bIsHeatmapInFocus)
		// fSwitchBorder = 450;
		//
		// if (fArTrackPos[0] < fSwitchBorder) {
		// bIsHeatmapInFocus = false;
		// }
		// else {
		// bIsHeatmapInFocus = true;
		// }
		//
		// // Manipulate selected overview chunk (level 1)
		// if (fArTrackPos[0] < 50) {
		// int iNrPixelsPerSelectionBar = (int) (screenRect.getHeight() / iNrSelBar);
		// iSelectorBar = (int) ((fArTrackPos[1] + 17) / iNrPixelsPerSelectionBar * 1.1f); // TODO: play with
		// // these
		// // correction
		// // factors
		//
		// if (iSelectorBar <= 0)
		// iSelectorBar = 1;
		// else if (iSelectorBar > iNrSelBar)
		// iSelectorBar = iNrSelBar;
		// }
		//
		// // Manipulate selected level 2 chunk
		// if (!bIsHeatmapInFocus && fArTrackPos[0] > 50 && fArTrackPos[0] < 450) {
		// float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		// float fStep = fTextureHeight / (iAlNumberSamples.get(iSelectorBar - 1));// * 2);
		// float fYPosMouse =
		// ((1f - fArTrackPos[1] / (float) screenRect.getHeight())) * 8f * fAspectRatio - 0.4f;
		//
		// int iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
		// if (iSamplesPerHeatmap % 2 == 0) {
		// if ((iselElement - (int) Math.floor(iSamplesPerHeatmap / 2) + 1) >= 0
		// && (iselElement + (int) Math.floor(iSamplesPerHeatmap / 2)) < iAlNumberSamples
		// .get(iSelectorBar - 1)) {
		// iFirstSampleLevel2 = iselElement - (int) Math.floor(iSamplesPerHeatmap / 2) + 1;
		// fPosCursorFirstElementLevel3 = fTextureHeight - (iFirstSampleLevel2 * fStep);
		// iLastSampleLevel2 = iselElement + (int) Math.floor(iSamplesPerHeatmap / 2);
		// fPosCursorLastElementLevel3 = fTextureHeight - ((iLastSampleLevel2 + 1) * fStep);
		// }
		// }
		// else {
		// if ((iselElement - (int) Math.ceil(iSamplesPerHeatmap / 2)) >= 0
		// && (iselElement + (int) Math.floor(iSamplesPerHeatmap / 2)) < iAlNumberSamples
		// .get(iSelectorBar - 1)) {
		// iFirstSampleLevel2 = iselElement - (int) Math.ceil(iSamplesPerHeatmap / 2);
		// fPosCursorFirstElementLevel3 = fTextureHeight - (iFirstSampleLevel2 * fStep);
		// iLastSampleLevel2 = iselElement + (int) Math.floor(iSamplesPerHeatmap / 2);
		// fPosCursorLastElementLevel3 = fTextureHeight - ((iLastSampleLevel2 + 1) * fStep);
		// }
		// }
		// }
		//
		// setDisplayListDirty();
	}

	@Override
	public void initData() {

		super.initData();

		// FIXME: in case of loading a new data file the old cluster assignment must be deleted
		// contentVA.setGroupList(null);
		// storageVA.setGroupList(null);

		// FIXME: in case of loading a new data file the old dendrogram views should not be active
		// bGeneDendrogramActive = false;
		// bExperimentDendrogramActive = false;

		initHierarchy();
		calculateTextures();

		initPosCursorLevel1();

		if (bSkipLevel2 == false) {
			initPosCursorLevel2();
		}

		glHeatMapView.setSet(set);
		glHeatMapView.setContentVAType(EVAType.CONTENT_EMBEDDED_HM);
		glHeatMapView.initData();

		glGeneDendrogramView.setSet(set);
		glGeneDendrogramView.setContentVAType(EVAType.CONTENT);
		glGeneDendrogramView.initData();

		glExperimentDendrogramView.setSet(set);
		glExperimentDendrogramView.setContentVAType(EVAType.STORAGE);
		glExperimentDendrogramView.initData();

		if (bSkipLevel2 == false)
			bRedrawTextures = true;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHierarchicalHeatMapView serializedForm = new SerializedHierarchicalHeatMapView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleInterchangeGroups(boolean bGeneGroup) {
		IVirtualArray va;
		Tree<ClusterNode> tree = null;

		if (bGeneGroup) {
			va = contentVA;
			tree = set.getClusteredTreeGenes();
		}
		else {
			va = storageVA;
			tree = set.getClusteredTreeExps();
		}

		IGroupList groupList = va.getGroupList();

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

		// restructure tree
		if (groupList.get(selGroups.get(0)).getClusterNode() != null) {

			ClusterNode tempParent0 = tree.getParent(groupList.get(selGroups.get(0)).getClusterNode());
			ClusterNode tempParent1 = tree.getParent(groupList.get(selGroups.get(1)).getClusterNode());

			tree.getGraph().removeEdge(tempParent0, groupList.get(selGroups.get(0)).getClusterNode());
			tree.getGraph().removeEdge(tempParent1, groupList.get(selGroups.get(1)).getClusterNode());
			tree.addChild(tempParent0, groupList.get(selGroups.get(1)).getClusterNode());
			tree.addChild(tempParent1, groupList.get(selGroups.get(0)).getClusterNode());

			ClusterHelper.determineHierarchyDepth(tree);
			ClusterHelper.determineNrElements(tree);

			generalManager.getEventPublisher().triggerEvent(new UpdateViewEvent());

			ArrayList<Integer> newArraylist = ClusterHelper.getAl(tree);

			IVirtualArray virtualArray = null;
			if (bGeneGroup) {
				virtualArray = new VirtualArray(EVAType.CONTENT, set.depth(), newArraylist);
				useCase.replaceVirtualArray(EVAType.CONTENT, virtualArray);
			}
			else {
				virtualArray = new VirtualArray(EVAType.STORAGE, set.size(), newArraylist);
				useCase.replaceVirtualArray(EVAType.STORAGE, virtualArray);
			}

		}

		// interchange
		if (groupList.interchange(va, selGroups.get(0), selGroups.get(1)) == false) {
			System.out.println("Problem during interchange!!!");
			return;
		}

		bRedrawTextures = true;

		setDisplayListDirty();

	}

	@Override
	public void handleMergeGroups(boolean bGeneGroup) {
		IVirtualArray va;
		Tree<ClusterNode> tree = null;

		if (bGeneGroup) {
			va = contentVA;
			tree = set.getClusteredTreeGenes();
		}
		else {
			va = storageVA;
			tree = set.getClusteredTreeExps();
		}

		IGroupList groupList = va.getGroupList();

		ArrayList<Integer> selGroups = new ArrayList<Integer>();

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		for (Group iter : groupList) {
			if (iter.getSelectionType() == ESelectionType.SELECTION)
				selGroups.add(groupList.indexOf(iter));
		}

		if (selGroups.size() == 2) {
			// restructure tree
			if (groupList.get(selGroups.get(0)).getClusterNode() != null) {

				ClusterNode tempParent1 = tree.getParent(groupList.get(selGroups.get(1)).getClusterNode());

				tree.getGraph().removeEdge(tempParent1, groupList.get(selGroups.get(1)).getClusterNode());
				tree.addChild(groupList.get(selGroups.get(0)).getClusterNode(), groupList.get(
					selGroups.get(1)).getClusterNode());

				ClusterHelper.determineHierarchyDepth(tree);
				ClusterHelper.determineNrElements(tree);

				generalManager.getEventPublisher().triggerEvent(new UpdateViewEvent());

				ArrayList<Integer> newArraylist = ClusterHelper.getAl(tree);

				IVirtualArray virtualArray = null;
				if (bGeneGroup) {
					virtualArray = new VirtualArray(EVAType.CONTENT, set.depth(), newArraylist);
					useCase.replaceVirtualArray(EVAType.CONTENT, virtualArray);
				}
				else {
					virtualArray = new VirtualArray(EVAType.STORAGE, set.size(), newArraylist);
					useCase.replaceVirtualArray(EVAType.STORAGE, virtualArray);
				}
			}
		}

		// merge
		while (selGroups.size() >= 2) {

			int iLastSelected = selGroups.size() - 1;

			// merge last and the one before last
			if (groupList.merge(va, selGroups.get(iLastSelected - 1), selGroups.get(iLastSelected)) == false) {
				System.out.println("Problem during merge!!!");
				return;
			}
			selGroups.remove(iLastSelected);
		}

		bRedrawTextures = true;

		setDisplayListDirty();
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		groupMergingActionListener = new GroupMergingActionListener();
		groupMergingActionListener.setHandler(this);
		eventPublisher.addListener(MergeGroupsEvent.class, groupMergingActionListener);

		groupInterChangingActionListener = new GroupInterChangingActionListener();
		groupInterChangingActionListener.setHandler(this);
		eventPublisher.addListener(InterchangeGroupsEvent.class, groupInterChangingActionListener);

		updateViewListener = new UpdateViewListener();
		updateViewListener.setHandler(this);
		eventPublisher.addListener(UpdateViewEvent.class, updateViewListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (groupMergingActionListener != null) {
			eventPublisher.removeListener(groupMergingActionListener);
			groupMergingActionListener = null;
		}
		if (groupInterChangingActionListener != null) {
			eventPublisher.removeListener(groupInterChangingActionListener);
			groupInterChangingActionListener = null;
		}
		if (updateViewListener != null) {
			eventPublisher.removeListener(updateViewListener);
			updateViewListener = null;
		}
	}

	@Override
	public void handleUpdateView() {
		bRedrawTextures = true;
		setDisplayListDirty();
	}

	/**
	 * Handels keyboard events for controlling level 2
	 * 
	 * @param bArrowUp
	 *            true for arrow up, false for arrow down
	 */
	public void handleArrowAndAltPressed(boolean bArrowUp) {

		int iNrElementsToShift = (int) Math.floor(Math.sqrt(iSamplesLevel2));

		if (bArrowUp) {
			if (iFirstSampleLevel2 > iNrElementsToShift + 1) {
				iLastSampleLevel2 -= iNrElementsToShift;
				iFirstSampleLevel2 -= iNrElementsToShift;
				setEmbeddedHeatMapData();
				setDisplayListDirty();
				// glHeatMapView.upDownSelect(true);
			}
			else if (iFirstSampleLevel2 > 0) {
				iLastSampleLevel2--;
				iFirstSampleLevel2--;
				setEmbeddedHeatMapData();
				setDisplayListDirty();
				// glHeatMapView.upDownSelect(true);
			}
		}
		else {
			if (iLastSampleLevel2 < iSamplesLevel2 - 1 - iNrElementsToShift) {
				iLastSampleLevel2 += iNrElementsToShift;
				iFirstSampleLevel2 += iNrElementsToShift;
				setEmbeddedHeatMapData();
				setDisplayListDirty();
				// glHeatMapView.upDownSelect(false);
			}
			else if (iLastSampleLevel2 < iSamplesLevel2 - 1) {
				iLastSampleLevel2++;
				iFirstSampleLevel2++;
				setEmbeddedHeatMapData();
				setDisplayListDirty();
				// glHeatMapView.upDownSelect(false);
			}
		}
	}

	/**
	 * Handels keyboard events for controlling level 1
	 * 
	 * @param bArrowUp
	 *            true for arrow up, false for arrow down
	 */
	public void handleArrowAndCtrlPressed(boolean bArrowUp) {

		int iNrElementsToShift = (int) Math.floor(Math.sqrt(iNumberOfElements));

		if (bArrowUp) {
			if (iFirstSampleLevel1 > iNrElementsToShift + 1) {
				iFirstSampleLevel1 -= iNrElementsToShift;
				iLastSampleLevel1 -= iNrElementsToShift;
				setDisplayListDirty();
				// glHeatMapView.upDownSelect(true);
			}
			else if (iFirstSampleLevel1 > 0) {
				iFirstSampleLevel1--;
				iLastSampleLevel1--;
				setDisplayListDirty();
				// glHeatMapView.upDownSelect(true);
			}
		}
		else {
			if (iLastSampleLevel1 < iNumberOfElements - 1 - iNrElementsToShift) {
				iFirstSampleLevel1 += iNrElementsToShift;
				iLastSampleLevel1 += iNrElementsToShift;
				setDisplayListDirty();
				// glHeatMapView.upDownSelect(false);
			}
			else if (iLastSampleLevel1 < iNumberOfElements - 1) {
				iFirstSampleLevel1++;
				iLastSampleLevel1++;
				setDisplayListDirty();
				// glHeatMapView.upDownSelect(false);
			}
		}
	}

	/**
	 * Handle keyboard events for enabling embedded dendrograms.
	 * 
	 * @param bGeneDendrogram
	 *            true for gene dendrogram, false for experiment dendrogram
	 */
	public void handleDendrogramActivation(boolean bGeneDendrogram) {

		if (bGeneDendrogram) {
			if (set.getClusteredTreeGenes() == null)
				return;
			bGeneDendrogramActive = bGeneDendrogramActive == true ? false : true;
			if (bGeneDendrogramActive == true)
				bIsHeatmapInFocus = false;
			glHeatMapView.setDisplayListDirty();
			setDisplayListDirty();
		}
		else {
			if (set.getClusteredTreeExps() == null)
				return;
			bExperimentDendrogramActive = bExperimentDendrogramActive == true ? false : true;
			glHeatMapView.setDisplayListDirty();
			setDisplayListDirty();
		}
	}

	/**
	 * Handels arrow up/down keyboard events
	 * 
	 * @param bArrowUp
	 *            true for arrow up, false for arrow down
	 */
	public void handleArrowPressed(boolean bArrowUp) {
		// if(bArrowUp)
		// glHeatMapView.upDownSelect(true);
		// else
		// glHeatMapView.upDownSelect(false);
	}

	/**
	 * Handle keyboard events for setting embedded heat map in focus
	 * 
	 * @param bArrowLeft
	 *            true for arrow left, false for arrow right
	 */
	public void handleArrowAndShiftPressed(boolean bArrowLeft) {
		if (bArrowLeft) {
			if (bIsHeatmapInFocus == false) {
				bGeneDendrogramActive = false;
				bIsHeatmapInFocus = true;
				setDisplayListDirty();
				glExperimentDendrogramView.setRedrawDendrogram();
				glExperimentDendrogramView.setDisplayListDirty();
			}
		}
		else {
			if (bIsHeatmapInFocus) {
				bIsHeatmapInFocus = false;
				setDisplayListDirty();
				glExperimentDendrogramView.setRedrawDendrogram();
				glExperimentDendrogramView.setDisplayListDirty();
			}
		}
	}

	/**
	 * Set the number of samples which are shown in one texture
	 * 
	 * @param iNumberOfSamplesPerTexture
	 *            the number
	 */
	public final void setNumberOfSamplesPerTexture(int iNumberOfSamplesPerTexture) {
		this.iNumberOfSamplesPerTexture = iNumberOfSamplesPerTexture;
	}

	/**
	 * Set the number of samples which are shown in one heat map
	 * 
	 * @param iNumberOfSamplesPerHeatmap
	 *            the number
	 */
	public final void setNumberOfSamplesPerHeatmap(int iNumberOfSamplesPerHeatmap) {
		this.iNumberOfSamplesPerHeatmap = iNumberOfSamplesPerHeatmap;
	}

}
