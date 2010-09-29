package org.caleydo.view.heatmap.hierarchical;

import static org.caleydo.view.heatmap.HeatMapRenderStyle.BACKGROUND_COLOR;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.BACKGROUND_Z;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.BUTTON_Z;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.CLUSTER_BORDERS_Z;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.DENDROGRAM_BACKROUND;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.DRAGGING_CURSOR_COLOR;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.FIELD_Z;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.SELECTION_Z;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Transform;

import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.group.ContentGroupList;
import org.caleydo.core.data.group.Group;
import org.caleydo.core.data.group.StorageGroupList;
import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
import org.caleydo.core.data.virtualarray.delta.StorageVADelta;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.EDataFilterLevel;
import org.caleydo.core.manager.event.view.ClusterNodeSelectionEvent;
import org.caleydo.core.manager.event.view.group.ExportContentGroupsEvent;
import org.caleydo.core.manager.event.view.group.ExportStorageGroupsEvent;
import org.caleydo.core.manager.event.view.group.InterchangeContentGroupsEvent;
import org.caleydo.core.manager.event.view.group.InterchangeStorageGroupsEvent;
import org.caleydo.core.manager.event.view.group.MergeContentGroupsEvent;
import org.caleydo.core.manager.event.view.group.MergeStorageGroupsEvent;
import org.caleydo.core.manager.event.view.storagebased.NewContentGroupInfoEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.RemoteRenderingTransformer;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ClusterNodeSelectionListener;
import org.caleydo.core.view.opengl.canvas.listener.IClusterNodeEventReceiver;
import org.caleydo.core.view.opengl.canvas.listener.UpdateViewListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IContentGroupsActionHandler;
import org.caleydo.core.view.opengl.canvas.remote.receiver.INewContentGroupInfoHandler;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IStorageGroupsActionHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.GroupContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.item.BookmarkItem;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.rcp.dialog.file.ExportDataDialog;
import org.caleydo.rcp.view.listener.ContentGroupExportingListener;
import org.caleydo.rcp.view.listener.ContentGroupInterChangingActionListener;
import org.caleydo.rcp.view.listener.ContentGroupMergingActionListener;
import org.caleydo.rcp.view.listener.NewContentGroupInfoActionListener;
import org.caleydo.rcp.view.listener.StorageGroupExportingListener;
import org.caleydo.rcp.view.listener.StorageGroupInterChangingActionListener;
import org.caleydo.rcp.view.listener.StorageGroupMergingActionListener;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.dendrogram.GLDendrogram;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.HierarchicalHeatMapTemplate;
import org.caleydo.view.heatmap.listener.GLHierarchicalHeatMapKeyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

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
public class GLHierarchicalHeatMap extends AStorageBasedView implements
		IContentGroupsActionHandler, IStorageGroupsActionHandler,
		IClusterNodeEventReceiver, INewContentGroupInfoHandler, IGLRemoteRenderingView {

	public final static String VIEW_ID = "org.caleydo.view.heatmap.hierarchical";

	private HeatMapRenderStyle renderStyle;

	private final static float GAP_BETWEEN_LEVELS = 0.6f;

	private int iSamplesPerTexture = 0;
	private final static int MAX_SAMPLES_PER_TEXTURE = 2000;

	private int iSamplesLevel2;
	private final static int MIN_SAMPLES_LEVEL_2 = 200;
	private final static int MAX_SAMPLES_LEVEL_2 = 400;

	private int iSamplesPerHeatmap = 0;
	private final static int MIN_SAMPLES_PER_HEATMAP = 14;
	private final static int MAX_SAMPLES_PER_HEATMAP = 50;

	private int iNumberOfElements = 0;
	/**
	 * if only a small number of genes is in the data set, level_1 (overViewBar)
	 * will not be rendered
	 */
	private boolean bSkipLevel1 = false;
	private final static int MIN_SAMPLES_SKIP_LEVEL_1 = 200;
	/**
	 * if only a small number of genes is in the data set, level_2 (textures)
	 * will not be rendered
	 */
	private boolean bSkipLevel2 = false;
	private final static int MIN_SAMPLES_SKIP_LEVEL_2 = 40;

	private ColorMapping colorMapper;

	private int iNrTextures = 0;
	/** array of textures for holding the data samples */
	private ArrayList<Texture> AlTextures = new ArrayList<Texture>();
	private ArrayList<Integer> iAlNumberSamples = new ArrayList<Integer>();

	private Point pickingPointLevel1 = null;
	private int iPickedSampleLevel1 = 0;
	/** the first content entry that is shown in level 2 */
	private int iFirstSampleLevel1 = 0;
	/** the last content entry that is shown in level 2 */
	private int iLastSampleLevel1 = 0;

	private Point pickingPointLevel2 = null;
	private int iPickedSampleLevel2 = 0;
	/**
	 * the first content entry shown in level 3 relative to level 2, i.e. n for
	 * this would be iFirstSampleLevel1 + n in absolutes
	 */
	private int iFirstSampleLevel2 = 0;

	/**
	 * the last content entry shown in level 3 relative to level 2, i.e. n for
	 * this would be iLastSampleLevel1 + n in absolutes
	 */
	private int iLastSampleLevel2 = 0;

	private boolean bRenderCaption = false;

	private float fScalingLevel2 = 1.0f;

	/** embedded heat map */
	private GLHeatMap glHeatMapView;
	private HierarchicalHeatMapTemplate renderTemplate;
	private boolean bIsHeatmapInFocus = false;

	/** embedded dendrogram */
	private GLDendrogram<ContentGroupList> glContentDendrogramView;
	private boolean bGeneDendrogramActive = false;
	private boolean bGeneDendrogramRenderCut = false;
	private boolean bFirstStartGeneDendrogram = true;
	private GLDendrogram<StorageGroupList> glExperimentDendrogramView;
	private boolean bExperimentDendrogramActive = false;
	private boolean bExperimentDendrogramRenderCut = false;
	private boolean bFirstStartExperimentDendrogram = true;

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
	@SuppressWarnings("unused")
	private boolean bSplitGroupExp = false;
	@SuppressWarnings("unused")
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

	private ContentGroupExportingListener contentGroupExportingListener;
	private StorageGroupExportingListener storageGroupExportingListener;

	private ContentGroupInterChangingActionListener contentGroupInterchangingListener;
	private StorageGroupInterChangingActionListener storageGroupInterchangingListener;

	private ContentGroupMergingActionListener contentGroupMergingListener;
	private StorageGroupMergingActionListener storageGroupMergingListener;
	private UpdateViewListener updateViewListener;
	private ClusterNodeSelectionListener clusterNodeMouseOverListener;
	private NewContentGroupInfoActionListener newGroupInfoActionListener;

	private boolean bRenderDendrogramBackgroundWhite = false;

	private boolean hasDataWindowChanged = true;

	// private org.eclipse.swt.graphics.Point upperLeftScreenPos = new
	// org.eclipse.swt.graphics.Point(0, 0);

	/**
	 * Transformation utility object to transform and project view related
	 * coordinates
	 */
	private RemoteRenderingTransformer selectionTransformer;

	private RemoteLevelElement heatMapRemoteElement;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param viewFrustum
	 */
	public GLHierarchicalHeatMap(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {

		super(glCanvas, viewFrustum);
		viewType = GLHierarchicalHeatMap.VIEW_ID;

		ArrayList<SelectionType> alSelectionTypes = new ArrayList<SelectionType>();
		alSelectionTypes.add(SelectionType.NORMAL);
		alSelectionTypes.add(SelectionType.MOUSE_OVER);
		alSelectionTypes.add(SelectionType.SELECTION);

		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		glKeyListener = new GLHierarchicalHeatMapKeyListener(this);

		renderStyle = new HeatMapRenderStyle(this, viewFrustum);
		super.renderStyle = renderStyle;

		heatMapRemoteElement = new RemoteLevelElement(null);
		Transform transform = new Transform();
		transform.setTranslation(new Vec3f(0, 0, 0));
		transform.setScale(new Vec3f(1, 1, 1));
		heatMapRemoteElement.setTransform(transform);

		ArrayList<RemoteLevelElement> remoteLevelElementWhiteList = new ArrayList<RemoteLevelElement>();
		remoteLevelElementWhiteList.add(heatMapRemoteElement);
		selectionTransformer = new RemoteRenderingTransformer(iUniqueID,
				remoteLevelElementWhiteList);

	}

	@Override
	public void init(GL gl) {

		// // FIXME: Alex, is it save to call this here?
		// initData();

		createHeatMap();
		createDendrogram();

		glHeatMapView.initRemote(gl, this, glMouseListener, null);
		glHeatMapView.useFishEye(false);
		glContentDendrogramView.initRemote(gl, this, glMouseListener, null);
		glExperimentDendrogramView.initRemote(gl, this, glMouseListener, null);

		initTextures(gl);
		// activateGroupHandling();
	}

	/**
	 * Function responsible for initialization of hierarchy levels. Depending on
	 * the amount of samples in the data set 1, 2, or 3 levels are used.
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

		} else if (iNumberOfElements < MIN_SAMPLES_SKIP_LEVEL_1) {
			bSkipLevel1 = true;
			bSkipLevel2 = false;

			iNrTextures = 1;

			iSamplesPerTexture = iNumberOfElements;
			iSamplesLevel2 = iNumberOfElements;
			iSamplesPerHeatmap = (int) Math.floor((double) iSamplesPerTexture / 3);

		} else {
			bSkipLevel1 = false;
			bSkipLevel2 = false;

			iNrTextures = (int) Math.ceil((double) iNumberOfElements
					/ MAX_SAMPLES_PER_TEXTURE);

			if (iNrTextures <= 1)
				iSamplesPerTexture = iNumberOfElements;
			else
				iSamplesPerTexture = MAX_SAMPLES_PER_TEXTURE;

			iSamplesLevel2 = 200;

			iSamplesPerHeatmap = (int) Math.floor((double) iSamplesLevel2 / 3);
		}

		if (iSamplesPerHeatmap > MAX_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = MAX_SAMPLES_PER_HEATMAP;

		if (iSamplesPerHeatmap < MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = MIN_SAMPLES_PER_HEATMAP;

		if (iNumberOfElements <= MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = iNumberOfElements;
	}

	@Override
	public void initLocal(GL gl) {

		// Register keyboard listener to GL canvas
		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentGLCanvas.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);

	}

	@Override
	public void initRemote(GL gl, final AGLView glParentView,
			GLMouseListener glMouseListener, GLInfoAreaManager infoAreaManager) {

		// Register keyboard listener to GL canvas
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
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

	/**
	 * Init (reset) the positions of cursors used for highlighting selected
	 * elements in stage 1 (overviewBar)
	 * 
	 * @param
	 */
	private void initPosCursorLevel1() {

		int iNumberSample = iNumberOfElements;

		if (bSkipLevel1)
			iSamplesLevel2 = iNumberSample;

		if (iSamplesLevel2 % 2 == 0) {
			iFirstSampleLevel1 = iPickedSampleLevel1
					- (int) Math.floor((double) iSamplesLevel2 / 2) + 1;
			iLastSampleLevel1 = iPickedSampleLevel1
					+ (int) Math.floor((double) iSamplesLevel2 / 2);
		} else {
			iFirstSampleLevel1 = iPickedSampleLevel1
					- (int) Math.ceil((double) iSamplesLevel2 / 2);
			iLastSampleLevel1 = iPickedSampleLevel1
					+ (int) Math.floor((double) iSamplesLevel2 / 2);
		}

		if (iPickedSampleLevel1 < iSamplesLevel2 / 2) {
			iPickedSampleLevel1 = (int) Math.floor((double) iSamplesLevel2 / 2);
			iFirstSampleLevel1 = 0;
			iLastSampleLevel1 = iSamplesLevel2 - 1;
		} else if (iPickedSampleLevel1 > iNumberSample - 1 - iSamplesLevel2 / 2) {
			iPickedSampleLevel1 = (int) Math.ceil((double) iNumberSample - iSamplesLevel2
					/ 2);
			iLastSampleLevel1 = iNumberSample - 1;
			iFirstSampleLevel1 = iNumberSample - iSamplesLevel2;
		}

	}

	/**
	 * Init (reset) the positions of cursors used for highlighting selected
	 * elements in stage 2 (texture)
	 * 
	 * @param
	 */
	private void initPosCursorLevel2() {

		if (bSkipLevel2) {
			iSamplesPerHeatmap = (int) Math.floor((double) iSamplesPerTexture / 3);
			if (iSamplesPerHeatmap % 2 == 0) {
				iFirstSampleLevel2 = iPickedSampleLevel2
						- (int) Math.floor((double) iSamplesPerHeatmap / 2) + 1;
				iLastSampleLevel2 = iPickedSampleLevel2
						+ (int) Math.floor((double) iSamplesPerHeatmap / 2);
			} else {
				iFirstSampleLevel2 = iPickedSampleLevel2
						- (int) Math.ceil((double) iSamplesPerHeatmap / 2);
				iLastSampleLevel2 = iPickedSampleLevel2
						+ (int) Math.floor((double) iSamplesPerHeatmap / 2);
			}
		}

		iSamplesPerHeatmap = iSamplesLevel2 / 3;

		if (iSamplesPerHeatmap >= 3 * MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = 2 * MIN_SAMPLES_PER_HEATMAP;
		else if (iSamplesPerHeatmap > MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = MIN_SAMPLES_PER_HEATMAP;

		if (iNumberOfElements <= MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = iNumberOfElements;

		iPickedSampleLevel2 = (int) Math.floor((double) iSamplesPerHeatmap / 2);
		iFirstSampleLevel2 = 0;
		iLastSampleLevel2 = iSamplesPerHeatmap - 1;
	}

	/**
	 * Function calculates number of textures and fills array lists to avoid
	 * NPEs after initialization.
	 * 
	 * @param
	 */
	private void calculateTextures() {

		if (bSkipLevel1 || bSkipLevel2) {

			AlTextures.clear();
			iAlNumberSamples.clear();

			Texture tempTextur = null;

			AlTextures.add(tempTextur);
			iAlNumberSamples.add(iSamplesPerTexture);

		} else {

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
	 * Init textures, build array of textures used for holding the whole
	 * examples
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

			FloatBuffer FbTemp = BufferUtil.newFloatBuffer(iTextureWidth * iTextureHeight
					* 4);

			for (Integer iContentIndex : contentVA) {
				for (Integer iStorageIndex : storageVA) {
					if (contentSelectionManager.checkStatus(SelectionType.DESELECTED,
							iContentIndex)) {
						fOpacity = 0.3f;
					} else {
						fOpacity = 1.0f;
					}

					fLookupValue = set.get(iStorageIndex).getFloat(
							EDataRepresentation.NORMALIZED, iContentIndex);

					float[] fArMappingColor = colorMapper.getColor(fLookupValue);

					float[] fArRgba = { fArMappingColor[0], fArMappingColor[1],
							fArMappingColor[2], fOpacity };

					FbTemp.put(fArRgba);
				}
			}
			FbTemp.rewind();

			TextureData texData = new TextureData(GL.GL_RGBA /* internalFormat */,
					iTextureWidth /* height */, iTextureHeight /* width */, 0 /* border */,
					GL.GL_RGBA /* pixelFormat */, GL.GL_FLOAT /* pixelType */,
					false /* mipmap */, false /* dataIsCompressed */,
					false /* mustFlipVertically */, FbTemp, null);

			tempTextur = TextureIO.newTexture(0);
			tempTextur.updateImage(texData);

			AlTextures.add(tempTextur);
			iAlNumberSamples.add(iSamplesPerTexture);

		} else {

			AlTextures.clear();
			iAlNumberSamples.clear();

			Texture tempTextur;

			int iTextureHeight = contentVA.size();
			int iTextureWidth = storageVA.size();

			iSamplesPerTexture = (int) Math.ceil((double) iTextureHeight / iNrTextures);

			float fLookupValue = 0;
			float fOpacity = 0;

			FloatBuffer[] FbTemp = new FloatBuffer[iNrTextures];

			for (int itextures = 0; itextures < iNrTextures; itextures++) {

				if (itextures == iNrTextures - 1) {
					iAlNumberSamples.add(iTextureHeight - iSamplesPerTexture * itextures);
					FbTemp[itextures] = BufferUtil
							.newFloatBuffer((iTextureHeight - iSamplesPerTexture
									* itextures)
									* iTextureWidth * 4);
				} else {
					iAlNumberSamples.add(iSamplesPerTexture);
					FbTemp[itextures] = BufferUtil.newFloatBuffer(iSamplesPerTexture
							* iTextureWidth * 4);
				}
			}

			int iCount = 0;
			int iTextureCounter = 0;

			for (Integer iContentIndex : contentVA) {
				iCount++;
				for (Integer iStorageIndex : storageVA) {
					if (contentSelectionManager.checkStatus(SelectionType.DESELECTED,
							iContentIndex)) {
						fOpacity = 0.3f;
					} else {
						fOpacity = 1.0f;
					}

					fLookupValue = set.get(iStorageIndex).getFloat(
							EDataRepresentation.NORMALIZED, iContentIndex);

					float[] fArMappingColor = colorMapper.getColor(fLookupValue);

					float[] fArRgba = { fArMappingColor[0], fArMappingColor[1],
							fArMappingColor[2], fOpacity };

					FbTemp[iTextureCounter].put(fArRgba);
				}
				if (iCount >= iAlNumberSamples.get(iTextureCounter)) {
					FbTemp[iTextureCounter].rewind();

					TextureData texData = new TextureData(GL.GL_RGBA /* internalFormat */,
							iTextureWidth /* height */,
							iAlNumberSamples.get(iTextureCounter) /* width */,
							0 /* border */, GL.GL_RGBA /* pixelFormat */,
							GL.GL_FLOAT /* pixelType */, false /* mipmap */,
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

		float fHeatMapHeight = viewFrustum.getHeight();
		float fHeatMapWidth = viewFrustum.getWidth();
		ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0,
				(int) fHeatMapHeight, 0, (int) fHeatMapWidth, -20, 20);

		glHeatMapView = new GLHeatMap(this.getParentGLCanvas(), viewFrustum);
		glHeatMapView.setDataDomain(dataDomain);

		glHeatMapView.setRemoteRenderingGLView(this);
		glHeatMapView.setRemoteLevelElement(heatMapRemoteElement);
		renderTemplate = new HierarchicalHeatMapTemplate();
		glHeatMapView.setRenderTemplate(renderTemplate);
		renderTemplate.setBottomSpacing(0.6f);
		heatMapRemoteElement.setGLView(glHeatMapView);
		glHeatMapView.setContentVAType(GLHeatMap.CONTENT_EMBEDDED_VA);
		glHeatMapView.initialize();
		glHeatMapView.initData();
	}

	/**
	 * Create embedded dendrogram
	 * 
	 * @param
	 */
	private void createDendrogram() {

		float fHeatMapHeight = viewFrustum.getHeight();
		float fHeatMapWidth = viewFrustum.getWidth();
		ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0,
				(int) fHeatMapHeight, 0, (int) fHeatMapWidth, -20, 20);

		glContentDendrogramView = new GLDendrogram<ContentGroupList>(
				this.getParentGLCanvas(), viewFrustum, true);
		glContentDendrogramView.setRemoteRenderingGLView(this);
		glContentDendrogramView.setDataDomain(dataDomain);

		glExperimentDendrogramView = new GLDendrogram<StorageGroupList>(
				this.getParentGLCanvas(), viewFrustum, false);
		glExperimentDendrogramView.setRemoteRenderingGLView(this);
		glExperimentDendrogramView.setDataDomain(dataDomain);
		glExperimentDendrogramView.setRemoteRenderingGLView(this);

		glContentDendrogramView.setContentVAType(ISet.CONTENT);
		glContentDendrogramView.initData();
		glContentDendrogramView.setRenderUntilCut(bGeneDendrogramRenderCut);

		glExperimentDendrogramView.setContentVAType(ISet.CONTENT);
		glExperimentDendrogramView.initData();
		glExperimentDendrogramView.setRenderUntilCut(bExperimentDendrogramRenderCut);
	}

	@Override
	public void setDetailLevel(DetailLevel detailLevel) {
		super.setDetailLevel(detailLevel);
	}

	@Override
	public void displayLocal(GL gl) {

		if (glExperimentDendrogramView != null)
			glExperimentDendrogramView.processEvents();
		if (glContentDendrogramView != null)
			glContentDendrogramView.processEvents();
		if (glHeatMapView != null)
			glHeatMapView.processEvents();

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

		ConnectedElementRepresentationManager cerm = GeneralManager.get()
				.getViewGLCanvasManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

	}

	/**
	 * Jump to other areas of a heat map
	 * 
	 * @param
	 */
	@Override
	protected void reactOnExternalSelection(ISelectionDelta delta,
			boolean scrollToSelection) {

		if (delta.getIDType() != contentIDType)
			return;

		if (scrollToSelection && bSkipLevel2 == false) {

			int mouseOverElement = 0;
			SelectionType type = SelectionType.NORMAL;

			for (SelectionDeltaItem item : delta) {
				if (item.isRemove())
					continue;
				if (item.getSelectionType().getPriority() > type.getPriority()) {
					mouseOverElement = item.getPrimaryID();
					type = item.getSelectionType();
				}

			}

			if (type == SelectionType.NORMAL)
				return;
			// for (Integer mouseOverElement : setMouseOverElements) {

			int index = contentVA.indexOf(mouseOverElement);

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
				iLastSampleLevel2 = index + iSamplesPerHeatmap / 2 - iFirstSampleLevel1
						- 1;
				if (iFirstSampleLevel2 < 0) {
					iFirstSampleLevel2 = 0;
					iLastSampleLevel2 = iSamplesPerHeatmap - 1;
				}
				if (iLastSampleLevel2 > iSamplesLevel2) {
					iFirstSampleLevel2 = iSamplesLevel2 - iSamplesPerHeatmap;
					iLastSampleLevel2 = iSamplesLevel2 - 1;
				}
			} else {
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
				} else if (iLastSampleLevel1 > iNumberOfElements) {
					iFirstSampleLevel1 = iNumberOfElements - iSamplesLevel2;
					iLastSampleLevel1 = iNumberOfElements;
					iFirstSampleLevel2 = index - iSamplesPerHeatmap / 2
							- iFirstSampleLevel1;
					iLastSampleLevel2 = index + iSamplesPerHeatmap / 2
							- iFirstSampleLevel1;
					if (iLastSampleLevel2 > iSamplesLevel2) {
						iFirstSampleLevel2 = iSamplesLevel2 - iSamplesPerHeatmap;
						iLastSampleLevel2 = iSamplesLevel2;
					}
				} else {
					iFirstSampleLevel2 = index - iSamplesPerHeatmap / 2
							- iFirstSampleLevel1;
					iLastSampleLevel2 = index + iSamplesPerHeatmap / 2
							- iFirstSampleLevel1;
				}
			}
			hasDataWindowChanged = true;
			setDisplayListDirty();
		}
		// }
	}

	@Override
	protected void reactOnContentVAChanges(ContentVADelta delta) {

		if (delta.getVAType().equals(contentVAType))
			contentVA.setGroupList(null);

		// glHeatMapView.handleVirtualArrayUpdate(delta, getShortInfo());
		bRedrawTextures = true;

		setDisplayListDirty();
	}

	@Override
	public void handleVAUpdate(StorageVADelta delta, String info) {
		super.handleVAUpdate(delta, info);
		bRedrawTextures = true;
	}

	@Override
	public void handleVAUpdate(ContentVADelta delta, String info) {
		if (!delta.getVAType().equals(contentVAType))
			return;
		super.handleVAUpdate(delta, info);
		bRedrawTextures = true;
		hasDataWindowChanged = true;
		iPickedSampleLevel1 = 0;
		bGeneDendrogramActive = false;
		setDisplayListDirty();

		initData();
	}

	@Override
	public void replaceContentVA(int setID, String dataDomain, String vaType) {
		if (!vaType.equals(contentVAType))
			return;
		super.replaceContentVA(setID, dataDomain, vaType);
		hasDataWindowChanged = true;
		iPickedSampleLevel1 = 0;
		setDisplayListDirty();
	}

	@Override
	public void replaceStorageVA(String dataDomain, String vaType) {
		super.replaceStorageVA(dataDomain, vaType);
		hasDataWindowChanged = true;
		iPickedSampleLevel1 = 0;
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
	private void renderCaption(GL gl, String sLabel, float fXOrigin, float fYOrigin,
			float fFontScaling) {
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
	private void renderSelectedDomain(GL gl, Vec3f startpoint1, Vec3f endpoint1,
			Vec3f startpoint2, Vec3f endpoint2) {
		float fthickness = (endpoint1.x() - startpoint1.x()) / 4;

		// Scaling factor for textures: endpoint1.y() > startpoint1.y()
		float fScalFactor1 = 0;
		// Scaling factor for textures: endpoint2.y() < startpoint2.y()
		float fScalFactor2 = 0;
		// Scaling factor for textures: endpoint1.y() < startpoint1.y()
		float fScalFactor3 = 0;

		boolean bHandleEndpoint1LowerStartpoint1 = false;

		if (endpoint1.y() < startpoint1.y()) {
			bHandleEndpoint1LowerStartpoint1 = true;
			fScalFactor1 = 0;
			fScalFactor3 = 1;
			if (startpoint1.y() - endpoint1.y() < fthickness)
				fScalFactor3 = (startpoint1.y() - endpoint1.y()) * 5f;
		} else if (endpoint1.y() - startpoint1.y() < fthickness) {
			fScalFactor1 = (endpoint1.y() - startpoint1.y()) * 5f;
		} else {
			fScalFactor1 = 1;
		}

		if (startpoint2.y() - endpoint2.y() < 0.2f) {
			fScalFactor2 = (startpoint2.y() - endpoint2.y()) * 5f;
		} else {
			fScalFactor2 = 1;
		}

		Texture TextureMask = null;
		Texture TextureMaskNeg = null;

		if (bRenderDendrogramBackgroundWhite) {
			TextureMask = textureManager.getIconTexture(gl,
					EIconTextures.NAVIGATION_MASK_CURVE_WHITE);
			TextureMaskNeg = textureManager.getIconTexture(gl,
					EIconTextures.NAVIGATION_MASK_CURVE_NEG_WHITE);
			gl.glColor4f(1, 1, 1, 1);
		} else {
			TextureMask = textureManager.getIconTexture(gl,
					EIconTextures.NAVIGATION_MASK_CURVE);
			TextureMaskNeg = textureManager.getIconTexture(gl,
					EIconTextures.NAVIGATION_MASK_CURVE_NEG);
			gl.glColor4fv(DENDROGRAM_BACKROUND, 0);
		}

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
		if (bHandleEndpoint1LowerStartpoint1) {
			gl.glVertex3f(endpoint1.x() - 1 * fthickness, startpoint1.y() - fthickness
					* fScalFactor3, endpoint1.z());
			gl.glVertex3f(endpoint1.x() - 2 * fthickness, startpoint1.y() - fthickness
					* fScalFactor3, endpoint1.z());
		} else {
			gl.glVertex3f(endpoint1.x() - 1 * fthickness, endpoint1.y() - fthickness
					* fScalFactor1, endpoint1.z());
			gl.glVertex3f(endpoint1.x() - 2 * fthickness, endpoint1.y() - fthickness
					* fScalFactor1, endpoint1.z());
		}

		gl.glVertex3f(endpoint2.x() - 2 * fthickness, endpoint2.y() + fthickness
				* fScalFactor2, endpoint2.z());
		gl.glVertex3f(endpoint2.x() - 1 * fthickness, endpoint2.y() + fthickness
				* fScalFactor2, endpoint2.z());
		gl.glEnd();

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1, 1, 1, 1);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

		TextureMask.enable();
		TextureMask.bind();

		TextureCoords texCoordsMask = TextureMask.getImageTexCoords();

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoordsMask.right(), texCoordsMask.top());
		gl.glVertex3f(startpoint1.x() + 2 * fthickness, startpoint1.y(), startpoint1.z());
		gl.glTexCoord2f(texCoordsMask.left(), texCoordsMask.top());
		gl.glVertex3f(startpoint1.x() + 1 * fthickness, startpoint1.y(), startpoint1.z());
		gl.glTexCoord2f(texCoordsMask.left(), texCoordsMask.bottom());
		gl.glVertex3f(startpoint1.x() + 1 * fthickness, startpoint1.y() + fthickness
				* fScalFactor1, startpoint1.z());
		gl.glTexCoord2f(texCoordsMask.right(), texCoordsMask.bottom());
		gl.glVertex3f(startpoint1.x() + 2 * fthickness, startpoint1.y() + fthickness
				* fScalFactor1, startpoint1.z());
		gl.glEnd();

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoordsMask.right(), texCoordsMask.top());
		gl.glVertex3f(startpoint2.x() + 2 * fthickness, startpoint2.y(), startpoint2.z());
		gl.glTexCoord2f(texCoordsMask.left(), texCoordsMask.top());
		gl.glVertex3f(startpoint2.x() + 1 * fthickness, startpoint2.y(), startpoint2.z());
		gl.glTexCoord2f(texCoordsMask.left(), texCoordsMask.bottom());
		gl.glVertex3f(startpoint2.x() + 1 * fthickness, startpoint2.y() - fthickness
				* fScalFactor2, startpoint2.z());
		gl.glTexCoord2f(texCoordsMask.right(), texCoordsMask.bottom());
		gl.glVertex3f(startpoint2.x() + 2 * fthickness, startpoint2.y() - fthickness
				* fScalFactor2, startpoint2.z());
		gl.glEnd();

		if (bHandleEndpoint1LowerStartpoint1) {
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoordsMask.right(), texCoordsMask.top());
			gl.glVertex3f(endpoint1.x() - 1 * fthickness, endpoint1.y(), endpoint1.z());
			gl.glTexCoord2f(texCoordsMask.left(), texCoordsMask.top());
			gl.glVertex3f(endpoint1.x(), endpoint1.y(), endpoint1.z());
			gl.glTexCoord2f(texCoordsMask.left(), texCoordsMask.bottom());
			gl.glVertex3f(endpoint1.x(), endpoint1.y() + fthickness * fScalFactor3,
					endpoint1.z());
			gl.glTexCoord2f(texCoordsMask.right(), texCoordsMask.bottom());
			gl.glVertex3f(endpoint1.x() - 1 * fthickness, endpoint1.y() + fthickness
					* fScalFactor3, endpoint1.z());
			gl.glEnd();
		}

		TextureMask.disable();

		TextureMaskNeg.enable();
		TextureMaskNeg.bind();

		TextureCoords texCoordsMaskNeg = TextureMaskNeg.getImageTexCoords();

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.bottom());
		gl.glVertex3f(endpoint1.x() - 2 * fthickness, endpoint1.y() - fthickness
				* fScalFactor1, endpoint1.z());
		gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.bottom());
		gl.glVertex3f(endpoint1.x() - 1 * fthickness, endpoint1.y() - fthickness
				* fScalFactor1, endpoint1.z());
		gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.top());
		gl.glVertex3f(endpoint1.x() - 1 * fthickness, endpoint1.y(), endpoint1.z());
		gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.top());
		gl.glVertex3f(endpoint1.x() - 2 * fthickness, endpoint1.y(), endpoint1.z());
		gl.glEnd();

		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.bottom());
		gl.glVertex3f(endpoint2.x() - 2 * fthickness, endpoint2.y() + fthickness
				* fScalFactor2, endpoint2.z());
		gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.bottom());
		gl.glVertex3f(endpoint2.x() - 1 * fthickness, endpoint2.y() + fthickness
				* fScalFactor2, endpoint2.z());
		gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.top());
		gl.glVertex3f(endpoint2.x() - 1 * fthickness, endpoint2.y(), endpoint2.z());
		gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.top());
		gl.glVertex3f(endpoint2.x() - 2 * fthickness, endpoint2.y(), endpoint2.z());
		gl.glEnd();

		if (bHandleEndpoint1LowerStartpoint1) {
			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(startpoint1.x() + 2 * fthickness, startpoint1.y() - fthickness
					* fScalFactor3, startpoint1.z());
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(startpoint1.x() + 3 * fthickness, startpoint1.y() - fthickness
					* fScalFactor3, startpoint1.z());
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.top());
			gl.glVertex3f(startpoint1.x() + 3 * fthickness, startpoint1.y(),
					startpoint1.z());
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.top());
			gl.glVertex3f(startpoint1.x() + 2 * fthickness, startpoint1.y(),
					startpoint1.z());
			gl.glEnd();
		}

		TextureMaskNeg.disable();
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPopAttrib();
	}

	/**
	 * Renders class assignments for experiments in level 2 (textures)
	 * 
	 * @param gl
	 */
	private void renderClassAssignmentsExperimentsLevel2(final GL gl) {

		if (storageVA.getGroupList() == null)
			return;

		if (fScalingLevel2 == 0.2f)
			return;

		float fWidthLevel2 = renderStyle.getWidthLevel2() * fScalingLevel2;
		int iNrElements = storageVA.size();
		float fWidthSamples = fWidthLevel2 / iNrElements;
		float fxpos = 0;
		float fHeight = viewFrustum.getHeight();

		float fHeightClusterVisualization = renderStyle.getWidthClusterVisualization();

		StorageGroupList groupList = storageVA.getGroupList();
		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			float classWidth = groupList.get(i).getNrElements() * fWidthSamples;

			gl.glColor4f(1, 1, 1, 1);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			Texture tempTexture = null;
			if (storageVA.getGroupList().get(i).getSelectionType() == SelectionType.SELECTION) {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_SELECTED);
			} else {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_NORMAL);
			}

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_EXPERIMENTS_GROUP, i));

			tempTexture.enable();
			tempTexture.bind();

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fxpos, 0, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fxpos, 0 - fHeightClusterVisualization, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fxpos + classWidth, 0 - fHeightClusterVisualization, FIELD_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fxpos + classWidth, 0, FIELD_Z);
			gl.glEnd();

			tempTexture.disable();
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glPopName();

			if (i == iNrClasses - 1)
				return;

			gl.glColor4fv(BACKGROUND_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fxpos + classWidth, 0 - fHeightClusterVisualization,
					CLUSTER_BORDERS_Z);
			gl.glVertex3f(fxpos + classWidth, fHeight, CLUSTER_BORDERS_Z);
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

		if (storageVA.getGroupList() == null)
			return;

		int iNrElements = storageVA.size();
		float fWidthSamples = renderStyle.getWidthLevel3() / iNrElements;
		float fxpos = 0;

		float fHeight = 0;
		float fHeightClusterVisualization = renderStyle.getWidthClusterVisualization();

		if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut)
			fHeight = viewFrustum.getHeight()
					- renderStyle.getHeightExperimentDendrogram();
		else
			fHeight = viewFrustum.getHeight();

		StorageGroupList groupList = storageVA.getGroupList();
		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			float classWidth = groupList.get(i).getNrElements() * fWidthSamples;

			gl.glColor4f(1, 1, 1, 1);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			Texture tempTexture = null;

			if (storageVA.getGroupList().get(i).getSelectionType() == SelectionType.SELECTION) {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_SELECTED);
			} else {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_NORMAL);
			}

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_EXPERIMENTS_GROUP, i));

			tempTexture.enable();
			tempTexture.bind();

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fxpos, 0, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fxpos, 0 - fHeightClusterVisualization, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fxpos + classWidth, 0 - fHeightClusterVisualization, FIELD_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fxpos + classWidth, 0, FIELD_Z);
			gl.glEnd();

			tempTexture.disable();
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glPopName();

			if (i == iNrClasses - 1)
				return;

			gl.glColor4fv(BACKGROUND_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fxpos + classWidth, 0 - fHeightClusterVisualization,
					CLUSTER_BORDERS_Z);
			gl.glVertex3f(fxpos + classWidth, fHeight, CLUSTER_BORDERS_Z);
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

		if (contentVA.getGroupList() == null)
			return;

		float fHeight = viewFrustum.getHeight();
		int iNrElements = iNumberOfElements;
		float fHeightSamples = fHeight / iNrElements;
		float fyPos = fHeight;

		float fWidthLevel1 = renderStyle.getWidthLevel1();
		float fWidthClusterVisualization = renderStyle.getWidthClusterVisualization();

		ContentGroupList groupList = contentVA.getGroupList();

		gl.glTranslatef(fWidthLevel1, 0, 0);

		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			float classHeight = groupList.get(i).getNrElements() * fHeightSamples;

			gl.glColor4f(1, 1, 1, 1);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			Texture tempTexture = null;

			if (contentVA.getGroupList().get(i).getSelectionType() == SelectionType.SELECTION) {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_SELECTED);
			}

			else {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_NORMAL);
			}

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_GENES_GROUP, i));

			tempTexture.enable();
			tempTexture.bind();

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0, fyPos, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0 + fWidthClusterVisualization, fyPos, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(0 + fWidthClusterVisualization, fyPos - classHeight, FIELD_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(0, fyPos - classHeight, FIELD_Z);
			gl.glEnd();

			tempTexture.disable();
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glPopName();

			if (i == iNrClasses - 1) {
				gl.glTranslatef(-fWidthLevel1, 0, 0);
				return;
			}

			gl.glColor4fv(BACKGROUND_COLOR, 0);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fWidthClusterVisualization, fyPos - classHeight,
					CLUSTER_BORDERS_Z);
			gl.glVertex3f(-fWidthLevel1, fyPos - classHeight, CLUSTER_BORDERS_Z);
			gl.glEnd();

			fyPos = fyPos - classHeight;
		}

		gl.glTranslatef(-fWidthLevel1, 0, 0);
	}

	/**
	 * Renders class assignments for genes in level 2 (textures)
	 * 
	 * @param gl
	 */
	private void renderClassAssignmentsGenesLevel2(final GL gl) {
		// FIXME: Class assignments could be rendered using HeatMapUtil
		if (contentVA.getGroupList() == null)
			return;

		float fHeight = viewFrustum.getHeight();
		float fHeightSamples = fHeight / iSamplesLevel2;
		float fWidthLevel2 = renderStyle.getWidthLevel2() * fScalingLevel2;
		float fWidthClusterVisualization = renderStyle.getWidthClusterVisualization();

		gl.glTranslatef(fWidthLevel2, 0, 0);

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

		gl.glLineWidth(1f);

		for (int i = 0; i < iSamplesLevel2; i++) {

			if (iCounter == contentVA.getGroupList().get(iIdxCluster).getNrElements()) {

				gl.glColor4f(1, 1, 1, 1);
				gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
				Texture tempTexture = null;

				if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == SelectionType.SELECTION) {
					tempTexture = textureManager.getIconTexture(gl,
							EIconTextures.HEAT_MAP_GROUP_SELECTED);
				} else {
					tempTexture = textureManager.getIconTexture(gl,
							EIconTextures.HEAT_MAP_GROUP_NORMAL);
				}
				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.HIER_HEAT_MAP_GENES_GROUP, iIdxCluster));

				tempTexture.enable();
				tempTexture.bind();

				TextureCoords texCoords = tempTexture.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(0, fHeight, FIELD_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(0 + fWidthClusterVisualization, fHeight, FIELD_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(0 + fWidthClusterVisualization, fHeight - fHeightSamples
						* iCnt, FIELD_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(0, fHeight - fHeightSamples * iCnt, FIELD_Z);
				gl.glEnd();

				tempTexture.disable();
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				gl.glPopName();

				gl.glColor4fv(BACKGROUND_COLOR, 0);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(fWidthClusterVisualization,
						fHeight - fHeightSamples * iCnt, CLUSTER_BORDERS_Z);
				gl.glVertex3f(-fWidthLevel2, fHeight - fHeightSamples * iCnt,
						CLUSTER_BORDERS_Z);
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

		gl.glColor4f(1, 1, 1, 1);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
		Texture tempTexture = null;

		if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == SelectionType.SELECTION) {
			tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_GROUP_SELECTED);
		} else {
			tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_GROUP_NORMAL);
		}

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_GENES_GROUP, iIdxCluster));

		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0, fHeight, FIELD_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0 + fWidthClusterVisualization, fHeight, FIELD_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(0 + fWidthClusterVisualization, 0, FIELD_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0, 0, FIELD_Z);
		gl.glEnd();

		tempTexture.disable();
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPopName();

		gl.glTranslatef(-fWidthLevel2, 0, 0);

	}

	/**
	 * Renders class assignments for genes in level 3 (embedded heat map)
	 * 
	 * @param gl
	 */
	private void renderClassAssignmentsGenesLevel3(final GL gl) {

		if (contentVA.getGroupList() == null)
			return;

		float fHeight = 0;

		if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut)
			fHeight = viewFrustum.getHeight()
					- renderStyle.getHeightExperimentDendrogram();
		else
			fHeight = viewFrustum.getHeight();

		float fHeightSamples = fHeight / iSamplesPerHeatmap;
		float fWidthClusterVisualization = renderStyle.getWidthClusterVisualization();
		float fWidthEHM = renderStyle.getWidthLevel3();

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

		gl.glLineWidth(1f);

		for (int i = 0; i < iSamplesPerHeatmap; i++) {

			if (iCounter == contentVA.getGroupList().get(iIdxCluster).getNrElements()) {

				gl.glColor4f(1, 1, 1, 1);
				gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
				Texture tempTexture = null;

				if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == SelectionType.SELECTION) {
					tempTexture = textureManager.getIconTexture(gl,
							EIconTextures.HEAT_MAP_GROUP_SELECTED);
				} else {
					tempTexture = textureManager.getIconTexture(gl,
							EIconTextures.HEAT_MAP_GROUP_NORMAL);
				}

				gl.glPushName(pickingManager.getPickingID(iUniqueID,
						EPickingType.HIER_HEAT_MAP_GENES_GROUP, iIdxCluster));

				tempTexture.enable();
				tempTexture.bind();

				TextureCoords texCoords = tempTexture.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidthEHM, fHeight, FIELD_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidthEHM + fWidthClusterVisualization, fHeight, FIELD_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidthEHM + fWidthClusterVisualization, fHeight
						- fHeightSamples * iCnt, FIELD_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidthEHM, fHeight - fHeightSamples * iCnt, FIELD_Z);
				gl.glEnd();

				tempTexture.disable();
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				gl.glPopName();

				gl.glColor4fv(BACKGROUND_COLOR, 0);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(fWidthEHM + fWidthClusterVisualization, fHeight
						- fHeightSamples * iCnt, CLUSTER_BORDERS_Z);
				gl.glVertex3f(0, fHeight - fHeightSamples * iCnt, CLUSTER_BORDERS_Z);
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

		gl.glColor4f(1, 1, 1, 1);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
		Texture tempTexture = null;

		if (contentVA.getGroupList().get(iIdxCluster).getSelectionType() == SelectionType.SELECTION) {
			tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_GROUP_SELECTED);
		} else {
			tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_GROUP_NORMAL);
		}

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_GENES_GROUP, iIdxCluster));

		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fWidthEHM, fHeight, FIELD_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fWidthEHM + fWidthClusterVisualization, fHeight, FIELD_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fWidthEHM + fWidthClusterVisualization, 0, FIELD_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fWidthEHM, 0, FIELD_Z);
		gl.glEnd();

		tempTexture.disable();
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPopName();
	}

	/**
	 * Render the first stage of the hierarchy (OverviewBar)
	 * 
	 * @param gl
	 */
	private void renderLevel1(GL gl) {
		float fHeight;
		float fWidth;
		float fyOffset = 0.0f;

		fHeight = viewFrustum.getHeight();
		fWidth = renderStyle.getWidthLevel1();

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
			TextureCoords texCoords = AlTextures.get(iNrTextures - i - 1)
					.getImageTexCoords();

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
	 * Render marker in OverviewBar for visualization of the currently (in stage
	 * 2) rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerLevel1(final GL gl) {
		float fHeight = viewFrustum.getHeight();
		float fWidthLevel1 = renderStyle.getWidthLevel1();
		Vec3f startpoint1, endpoint1, startpoint2, endpoint2;

		float fOffsetClusterVis = 0f;

		if (contentVA.getGroupList() != null)
			fOffsetClusterVis += 0.1f;

		float fHeightElem = fHeight / iNumberOfElements;

		if (bIsDraggingActiveLevel1 == false && bIsDraggingWholeBlockLevel1 == false) {
			fPosCursorFirstElementLevel1 = viewFrustum.getHeight() - iFirstSampleLevel1
					* fHeightElem;
			fPosCursorLastElementLevel1 = viewFrustum.getHeight()
					- (iLastSampleLevel1 + 1) * fHeightElem;
		}

		// selected domain level 1
		startpoint1 = new Vec3f(fWidthLevel1 + fOffsetClusterVis,
				fPosCursorFirstElementLevel1, 0);
		endpoint1 = new Vec3f(fWidthLevel1 + GAP_BETWEEN_LEVELS + fOffsetClusterVis,
				fHeight, 0);
		startpoint2 = new Vec3f(fWidthLevel1 + fOffsetClusterVis,
				fPosCursorLastElementLevel1, 0);
		endpoint2 = new Vec3f(fWidthLevel1 + GAP_BETWEEN_LEVELS + fOffsetClusterVis, 0, 0);
		renderSelectedDomain(gl, startpoint1, endpoint1, startpoint2, endpoint2);

		gl.glLineWidth(2f);

		gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, fPosCursorFirstElementLevel1, 0);
		gl.glVertex3f(fWidthLevel1, fPosCursorFirstElementLevel1, 0);
		gl.glVertex3f(fWidthLevel1, fPosCursorLastElementLevel1, 0);
		gl.glVertex3f(0, fPosCursorLastElementLevel1, 0);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, viewFrustum.getHeight()
				- (iFirstSampleLevel1 + iFirstSampleLevel2) * fHeightElem, 0);
		gl.glVertex3f(fWidthLevel1 + 0.1f, viewFrustum.getHeight()
				- (iFirstSampleLevel1 + iFirstSampleLevel2) * fHeightElem, 0);
		gl.glVertex3f(fWidthLevel1 + 0.1f, viewFrustum.getHeight()
				- ((iFirstSampleLevel1 + iLastSampleLevel2) + 1) * fHeightElem, 0);
		gl.glVertex3f(0, viewFrustum.getHeight()
				- ((iFirstSampleLevel1 + iLastSampleLevel2) + 1) * fHeightElem, 0);
		gl.glEnd();

		if (bRenderCaption == true) {
			renderCaption(gl, "nr:" + iSamplesLevel2, 0.0f, viewFrustum.getHeight()
					- iPickedSampleLevel1 * fHeightElem, 0.004f);
		}

		gl.glColor4f(1f, 1f, 1f, 1f);
	}

	/**
	 * Render marker next to OverviewBar for visualization of selected elements
	 * in the data set
	 * 
	 * @param gl
	 */
	private void renderSelectedElementsLevel1(GL gl) {
		float fHeight = viewFrustum.getHeight();
		float fWidthLevel1 = renderStyle.getWidthLevel1();

		float fHeightElem = fHeight / iNumberOfElements;

		java.util.Set<Integer> setMouseOverElements = contentSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		java.util.Set<Integer> setSelectedElements = contentSelectionManager
				.getElements(SelectionType.SELECTION);

		gl.glLineWidth(2f);

		for (Integer mouseOverElement : setMouseOverElements) {

			int index = contentVA.indexOf(mouseOverElement.intValue());

			if ((index >= iFirstSampleLevel1 && index <= iLastSampleLevel1) == false) {
				gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(fWidthLevel1, fHeight - fHeightElem * index, SELECTION_Z);
				gl.glVertex3f(fWidthLevel1 + 0.1f, fHeight - fHeightElem * index,
						SELECTION_Z);
				gl.glEnd();
			}
		}

		for (Integer selectedElement : setSelectedElements) {

			int index = contentVA.indexOf(selectedElement.intValue());

			if ((index >= iFirstSampleLevel1 && index <= iLastSampleLevel1) == false) {
				gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(fWidthLevel1, fHeight - fHeightElem * index, SELECTION_Z);
				gl.glVertex3f(fWidthLevel1 + 0.1f, fHeight - fHeightElem * index,
						SELECTION_Z);
				gl.glEnd();
			}
		}
	}

	/**
	 * Render the second stage of the hierarchy (Texture)
	 * 
	 * @param gl
	 */
	private void renderLevel2(GL gl) {
		float fHeight;

		fHeight = viewFrustum.getHeight();
		float fWidthLevel2 = renderStyle.getWidthLevel2() * fScalingLevel2;

		int iFirstTexture = 0;
		int iLastTexture = 0;
		int iFirstElementFirstTexture = iFirstSampleLevel1;
		int iLastElementLastTexture = iLastSampleLevel1;
		int iNrTexturesInUse = 0;

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_FIELD_SELECTION, 1));

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
			gl.glVertex3f(fWidthLevel2, fHeight, 0);
			gl.glTexCoord2d(texCoords1.right(), texCoords1.top());
			gl.glVertex3f(fWidthLevel2, 0, 0);
			gl.glEnd();

			TexTemp1.disable();
		} else {

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

			if (iNrTexturesInUse == 1) {

				float fScalingFirstElement = (float) iFirstElementFirstTexture
						/ iAlNumberSamples.get(iFirstTexture);
				float fScalingLastElement = (float) (iLastElementLastTexture + 1)
						/ iAlNumberSamples.get(iFirstTexture);

				Texture TexTemp1 = AlTextures.get(iFirstTexture);
				TexTemp1.enable();
				TexTemp1.bind();
				TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.top() * fScalingLastElement);
				gl.glVertex3f(0, 0, 0);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.bottom()
						+ fScalingFirstElement);
				gl.glVertex3f(0, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.bottom()
						+ fScalingFirstElement);
				gl.glVertex3f(fWidthLevel2, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.top()
						* fScalingLastElement);
				gl.glVertex3f(fWidthLevel2, 0, 0);
				gl.glEnd();

				TexTemp1.disable();

			} else if (iNrTexturesInUse == 2) {

				float fScalingLastTexture = (float) (iLastElementLastTexture + 1)
						/ iSamplesLevel2;

				float fRatioFirstTexture = (float) (iAlNumberSamples.get(iFirstTexture) - iFirstElementFirstTexture)
						/ iAlNumberSamples.get(iFirstTexture);
				float fRatioLastTexture = (float) (iLastElementLastTexture + 1)
						/ iAlNumberSamples.get(iLastTexture);

				Texture TexTemp1 = AlTextures.get(iFirstTexture);
				TexTemp1.enable();
				TexTemp1.bind();
				TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.top());
				gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.bottom()
						+ (1 - fRatioFirstTexture));
				gl.glVertex3f(0, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.bottom()
						+ (1 - fRatioFirstTexture));
				gl.glVertex3f(fWidthLevel2, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.top());
				gl.glVertex3f(fWidthLevel2, fHeight * fScalingLastTexture, 0);
				gl.glEnd();

				TexTemp1.disable();

				// gl.glBegin(GL.GL_LINES);
				// gl.glVertex3f(viewFrustum.getWidth(), fHeight *
				// fScalingLastTexture, 0);
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
				gl.glVertex3f(fWidthLevel2, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords2.right(), texCoords2.top() * fRatioLastTexture);
				gl.glVertex3f(fWidthLevel2, 0, 0);
				gl.glEnd();

				TexTemp2.disable();
			} else if (iNrTexturesInUse == 3) {

				float fScalingFirstTexture = (float) (iSamplesPerTexture - iFirstElementFirstTexture)
						/ iSamplesLevel2;
				float fScalingLastTexture = (float) (iLastElementLastTexture + 1)
						/ iSamplesLevel2;

				float fRatioFirstTexture = (float) (iAlNumberSamples.get(iLastTexture) - iFirstElementFirstTexture)
						/ iAlNumberSamples.get(iFirstTexture);
				float fRatioLastTexture = (float) (iLastElementLastTexture + 1)
						/ iAlNumberSamples.get(iLastTexture);

				Texture TexTemp1 = AlTextures.get(iFirstTexture);
				TexTemp1.enable();
				TexTemp1.bind();
				TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.top());
				gl.glVertex3f(0, fHeight * (1 - fScalingFirstTexture), 0);
				gl.glTexCoord2d(texCoords1.left(), texCoords1.bottom()
						+ (1 - fRatioFirstTexture));
				gl.glVertex3f(0, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.bottom()
						+ (1 - fRatioFirstTexture));
				gl.glVertex3f(fWidthLevel2, fHeight, 0);
				gl.glTexCoord2d(texCoords1.right(), texCoords1.top());
				gl.glVertex3f(fWidthLevel2, fHeight * (1 - fScalingFirstTexture), 0);
				gl.glEnd();

				TexTemp1.disable();

				// gl.glBegin(GL.GL_LINES);
				// gl.glVertex3f(viewFrustum.getWidth(), fHeight * (1 -
				// fScalingFirstTexture), 0);
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
				gl.glVertex3f(fWidthLevel2, fHeight * (1 - fScalingFirstTexture), 0);
				gl.glTexCoord2d(texCoords2.right(), texCoords2.top());
				gl.glVertex3f(fWidthLevel2, fHeight * fScalingLastTexture, 0);
				gl.glEnd();

				TexTemp2.disable();

				// gl.glBegin(GL.GL_LINES);
				// gl.glVertex3f(viewFrustum.getWidth(), fHeight *
				// fScalingLastTexture, 0);
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
				gl.glVertex3f(fWidthLevel2, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords3.right(), texCoords3.top() * fRatioLastTexture);
				gl.glVertex3f(fWidthLevel2, 0, 0);
				gl.glEnd();

				TexTemp3.disable();

			} else {
				throw new IllegalStateException(
						"Number of textures is bigger than 3 - something went wrong");
			}
		}

		gl.glPopName();
		gl.glPopAttrib();

	}

	private void renderSubTreeLevel2(GL gl) {
		if (set.getContentData(contentVAType).getContentTree() != null) {
			gl.glTranslatef(renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS / 2, 0, 0);

			if (contentVA.getGroupList() != null)
				gl.glTranslatef(renderStyle.getWidthClusterVisualization(), 0, 0);

			float fHeightSubTree = viewFrustum.getHeight();

			glContentDendrogramView.renderSubTreeFromIndexToIndex(gl, iFirstSampleLevel1,
					iLastSampleLevel1, iSamplesLevel2, GAP_BETWEEN_LEVELS / 2,
					fHeightSubTree);

			if (contentVA.getGroupList() != null)
				gl.glTranslatef(-renderStyle.getWidthClusterVisualization(), 0, 0);

			gl.glTranslatef(-(renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS / 2), 0,
					0);
		}
	}

	private void renderSubTreeLevel3(GL gl) {
		// render sub tree for level 3
		if (set.getContentData(contentVAType).getContentTree() != null) {

			if (contentVA.getGroupList() != null)
				gl.glTranslatef(2 * renderStyle.getWidthClusterVisualization(), 0, 0);

			float fHeightSubTree = 0;
			if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut)
				fHeightSubTree = viewFrustum.getHeight()
						- renderStyle.getHeightExperimentDendrogram();
			else
				fHeightSubTree = viewFrustum.getHeight();

			int from = iFirstSampleLevel1 + iFirstSampleLevel2;
			int to = iFirstSampleLevel1 + iLastSampleLevel2;

			glContentDendrogramView.renderSubTreeFromIndexToIndex(gl, from, to,
					iSamplesPerHeatmap, GAP_BETWEEN_LEVELS / 2, fHeightSubTree);

			if (contentVA.getGroupList() != null)
				gl.glTranslatef(-2 * renderStyle.getWidthClusterVisualization(), 0, 0);

		}
	}

	/**
	 * Render marker in Texture for visualization of the currently (in stage 3)
	 * rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerLevel2(final GL gl) {

		// float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		float fWidthLevel2 = renderStyle.getWidthLevel2() * fScalingLevel2;
		// float fHeight = viewFrustum.getHeight();
		float fHeightSampleLevel2 = viewFrustum.getHeight() / iSamplesLevel2;

		Vec3f startpoint1, endpoint1, startpoint2, endpoint2;

		float fOffsetClusterVis = 0f;

		if (contentVA.getGroupList() != null)
			fOffsetClusterVis += renderStyle.getWidthClusterVisualization();

		gl.glColor4f(1, 1, 0, 1);
		gl.glLineWidth(2f);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3f(0, viewFrustum.getHeight() - iFirstSampleLevel2
				* fHeightSampleLevel2, 0);
		gl.glVertex3f(fWidthLevel2, viewFrustum.getHeight() - iFirstSampleLevel2
				* fHeightSampleLevel2, 0);
		gl.glVertex3f(fWidthLevel2, viewFrustum.getHeight() - (iLastSampleLevel2 + 1)
				* fHeightSampleLevel2, 0);
		gl.glVertex3f(0, viewFrustum.getHeight() - (iLastSampleLevel2 + 1)
				* fHeightSampleLevel2, 0);
		gl.glEnd();

		if (bIsDraggingActiveLevel2 == false) {
			fPosCursorFirstElementLevel2 = viewFrustum.getHeight() - iFirstSampleLevel2
					* fHeightSampleLevel2;
			fPosCursorLastElementLevel2 = viewFrustum.getHeight()
					- (iLastSampleLevel2 + 1) * fHeightSampleLevel2;
		}

		startpoint1 = new Vec3f(fWidthLevel2 + fOffsetClusterVis, viewFrustum.getHeight()
				- iFirstSampleLevel2 * fHeightSampleLevel2, 0);

		if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut)
			endpoint1 = new Vec3f(
					fWidthLevel2 + GAP_BETWEEN_LEVELS + fOffsetClusterVis,
					viewFrustum.getHeight() - renderStyle.getHeightExperimentDendrogram(),
					0);
		else
			endpoint1 = new Vec3f(fWidthLevel2 + GAP_BETWEEN_LEVELS + fOffsetClusterVis,
					viewFrustum.getHeight(), 0);
		startpoint2 = new Vec3f(fWidthLevel2 + fOffsetClusterVis, viewFrustum.getHeight()
				- (iLastSampleLevel2 + 1) * fHeightSampleLevel2, 0);
		endpoint2 = new Vec3f(fWidthLevel2 + GAP_BETWEEN_LEVELS + fOffsetClusterVis,
				0.0f, 0);

		renderSelectedDomain(gl, startpoint1, endpoint1, startpoint2, endpoint2);

		// if (bGeneDendrogramRenderCut == false) {
		{
			gl.glLineWidth(1f);
			gl.glColor4f(1, 0, 0, 1);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(fWidthLevel2, 0.0f, BUTTON_Z);
			gl.glVertex3f(fWidthLevel2, -0.4f, BUTTON_Z);
			gl.glEnd();

			float fSizeHeatmapArrow = renderStyle.getSizeHeatmapArrow();

			Texture tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_ARROW);
			tempTexture.enable();
			tempTexture.bind();

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glColor4f(1, 1, 1, 1);

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_INFOCUS_SELECTION, 1));
			if (bIsHeatmapInFocus) {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidthLevel2, -0.4f, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidthLevel2, -0.4f + fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidthLevel2 - fSizeHeatmapArrow,
						-0.4f + fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidthLevel2 - fSizeHeatmapArrow, -0.4f, BUTTON_Z);
				gl.glEnd();
			} else {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidthLevel2, -0.4f, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidthLevel2, -0.4f + fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidthLevel2 - fSizeHeatmapArrow,
						-0.4f + fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidthLevel2 - fSizeHeatmapArrow, -0.4f, BUTTON_Z);
				gl.glEnd();
			}
			gl.glPopName();

			tempTexture.disable();
		}

		if (bRenderCaption == true) {
			renderCaption(gl, "Number Samples:" + iSamplesPerHeatmap, 0.0f,
					viewFrustum.getHeight() - iPickedSampleLevel2 * fHeightSampleLevel2,
					0.005f);
		}
	}

	/**
	 * Render marker in Texture (level 2) for visualization of selected elements
	 * in the data set
	 * 
	 * @param gl
	 */
	private void renderSelectedElementsLevel2(GL gl) {

		// float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		float fWidthLevel2 = renderStyle.getWidthLevel2() * fScalingLevel2;
		float fHeightSample = viewFrustum.getHeight() / iSamplesLevel2;
		float fExpWidth = fWidthLevel2 / storageVA.size();

		gl.glEnable(GL.GL_LINE_STIPPLE);
		gl.glLineStipple(2, (short) 0xAAAA);

		gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
		java.util.Set<Integer> selectedSet = storageSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		int iColumnIndex = 0;
		for (int iTempLine : storageVA) {
			for (Integer iCurrentLine : selectedSet) {
				if (iTempLine == iCurrentLine) {
					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(iColumnIndex * fExpWidth, 0, SELECTION_Z);
					gl.glVertex3f((iColumnIndex + 1) * fExpWidth, 0, SELECTION_Z);
					gl.glVertex3f((iColumnIndex + 1) * fExpWidth,
							viewFrustum.getHeight(), SELECTION_Z);
					gl.glVertex3f(iColumnIndex * fExpWidth, viewFrustum.getHeight(),
							SELECTION_Z);
					gl.glEnd();
				}
			}
			iColumnIndex++;
		}

		gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
		selectedSet = storageSelectionManager.getElements(SelectionType.SELECTION);
		int iLineIndex = 0;
		for (int iTempLine : storageVA) {
			for (Integer iCurrentLine : selectedSet) {
				if (iTempLine == iCurrentLine) {
					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(iLineIndex * fExpWidth, 0, SELECTION_Z);
					gl.glVertex3f((iLineIndex + 1) * fExpWidth, 0, SELECTION_Z);
					gl.glVertex3f((iLineIndex + 1) * fExpWidth, viewFrustum.getHeight(),
							SELECTION_Z);
					gl.glVertex3f(iLineIndex * fExpWidth, viewFrustum.getHeight(),
							SELECTION_Z);
					gl.glEnd();
				}
			}
			iLineIndex++;
		}

		gl.glDisable(GL.GL_LINE_STIPPLE);

		java.util.Set<Integer> setMouseOverElements = contentSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);

		for (Integer mouseOverElement : setMouseOverElements) {

			int selectedElement = contentVA.indexOf(mouseOverElement.intValue());

			if (selectedElement >= iFirstSampleLevel1
					&& selectedElement <= iLastSampleLevel1) {

				gl.glLineWidth(2f);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex3f(-0.0f, viewFrustum.getHeight()
						- (selectedElement - iFirstSampleLevel1) * fHeightSample,
						SELECTION_Z);
				gl.glVertex3f(fWidthLevel2 + 0.1f, viewFrustum.getHeight()
						- (selectedElement - iFirstSampleLevel1) * fHeightSample,
						SELECTION_Z);
				gl.glVertex3f(fWidthLevel2 + 0.1f, viewFrustum.getHeight()
						- (selectedElement + 1 - iFirstSampleLevel1) * fHeightSample,
						SELECTION_Z);
				gl.glVertex3f(-0.0f, viewFrustum.getHeight()
						- (selectedElement + 1 - iFirstSampleLevel1) * fHeightSample,
						SELECTION_Z);
				gl.glEnd();

			}
		}

		java.util.Set<Integer> setSelectedElements = contentSelectionManager
				.getElements(SelectionType.SELECTION);
		gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);

		for (Integer iSelectedElement : setSelectedElements) {

			int selectedElement = contentVA.indexOf(iSelectedElement.intValue());

			if (selectedElement >= iFirstSampleLevel1
					&& selectedElement <= iLastSampleLevel1) {

				gl.glLineWidth(2f);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex3f(-0.0f, viewFrustum.getHeight()
						- (selectedElement - iFirstSampleLevel1) * fHeightSample,
						SELECTION_Z);
				gl.glVertex3f(fWidthLevel2 + 0.1f, viewFrustum.getHeight()
						- (selectedElement - iFirstSampleLevel1) * fHeightSample,
						SELECTION_Z);
				gl.glVertex3f(fWidthLevel2 + 0.1f, viewFrustum.getHeight()
						- (selectedElement + 1 - iFirstSampleLevel1) * fHeightSample,
						SELECTION_Z);
				gl.glVertex3f(-0.0f, viewFrustum.getHeight()
						- (selectedElement + 1 - iFirstSampleLevel1) * fHeightSample,
						SELECTION_Z);
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

		float fWidthLevel1 = renderStyle.getWidthLevel1();
		float fSizeHeatmapArrow = renderStyle.getSizeHeatmapArrow();
		float fOffsetClusterVis = 0f;

		if (contentVA.getGroupList() != null)
			fOffsetClusterVis += 0.1f;

		gl.glTranslatef(fWidthLevel1 + fOffsetClusterVis, 0, 0);

		gl.glColor4f(1, 1, 1, 1);

		Texture tempTexture = textureManager.getIconTexture(gl,
				EIconTextures.HEAT_MAP_ARROW);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		// Polygon for iFirstElement-Cursor
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_CURSOR_LEVEL1, 1));
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel1, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorFirstElementLevel1, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fSizeHeatmapArrow,
				fPosCursorFirstElementLevel1 + fSizeHeatmapArrow, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel1 + fSizeHeatmapArrow, BUTTON_Z);
		gl.glEnd();
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPopName();

		// Polygon for iLastElement-Cursor
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_CURSOR_LEVEL1, 2));
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel1, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorLastElementLevel1, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorLastElementLevel1 - fSizeHeatmapArrow,
				BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel1 - fSizeHeatmapArrow, BUTTON_Z);
		gl.glEnd();
		gl.glPopName();

		tempTexture.disable();

		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		// fill gap between cursor
		gl.glColor4fv(DRAGGING_CURSOR_COLOR, 0);
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_BLOCK_CURSOR_LEVEL1, 1));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorLastElementLevel1, BUTTON_Z);
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel1, BUTTON_Z);
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel1, BUTTON_Z);
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorFirstElementLevel1, BUTTON_Z);
		gl.glEnd();
		gl.glPopName();

		gl.glTranslatef(-(fWidthLevel1 + fOffsetClusterVis), 0, 0);

	}

	/**
	 * Render cursor used for controlling hierarchical heat map (e.g. next
	 * Texture, previous Texture, set heatmap in focus)
	 * 
	 * @param gl
	 */
	private void renderCursorLevel2(final GL gl) {

		float fSizeHeatmapArrow = renderStyle.getSizeHeatmapArrow();

		gl.glColor4f(1f, 1, 1, 1f);

		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);

		gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
		Texture tempTexture = textureManager.getIconTexture(gl,
				EIconTextures.HEAT_MAP_ARROW);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		// Polygon for iFirstElement-Cursor
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_CURSOR_LEVEL2, 1));
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel2, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorFirstElementLevel2, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fSizeHeatmapArrow,
				fPosCursorFirstElementLevel2 + fSizeHeatmapArrow, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel2 + fSizeHeatmapArrow, BUTTON_Z);
		gl.glEnd();
		gl.glPopName();

		// Polygon for iLastElement-Cursor
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_CURSOR_LEVEL2, 2));
		gl.glBegin(GL.GL_POLYGON);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel2, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorLastElementLevel2, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorLastElementLevel2 - fSizeHeatmapArrow,
				BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel2 - fSizeHeatmapArrow, BUTTON_Z);
		gl.glEnd();
		gl.glPopName();

		tempTexture.disable();
		gl.glPopAttrib();
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		// fill gap between cursor
		gl.glColor4fv(DRAGGING_CURSOR_COLOR, 0);
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_BLOCK_CURSOR_LEVEL2, 1));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorLastElementLevel2, BUTTON_Z);
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel2, BUTTON_Z);
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel2, BUTTON_Z);
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorFirstElementLevel2, BUTTON_Z);
		gl.glEnd();
		gl.glPopName();

	}

	@Override
	public void display(GL gl) {
		// processEvents();
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

		// cluster dragging only allowed in case of partition based cluster
		// result
		if (bDragDropExpGroup
				&& set.getStorageData(storageVAType).getStorageTree() == null) {
			handleDragDropGroupExperiments(gl);
			if (glMouseListener.wasMouseReleased()) {
				bDragDropExpGroup = false;
			}
		}

		// cluster dragging only allowed in case of partition based cluster
		// result
		if (bDragDropGeneGroup
				&& set.getContentData(contentVAType).getContentTree() == null) {
			handleDragDropGroupGenes(gl);
			if (glMouseListener.wasMouseReleased()) {
				bDragDropGeneGroup = false;
			}
		}

		// if (bSplitGroupExp && set.getClusteredTreeExps() == null) {
		// handleGroupSplitExperiments(gl);
		// if (glMouseListener.wasMouseReleased()) {
		// bSplitGroupExp = false;
		// }
		// }

		// if (bSplitGroupGene && set.getClusteredTreeGenes() == null) {
		// handleGroupSplitGenes(gl);
		// if (glMouseListener.wasMouseReleased()) {
		// bSplitGroupGene = false;
		// }
		// }

		gl.glCallList(iGLDisplayListToCall);

		if (contentVA.getGroupList() != null)
			glHeatMapView.setClusterVisualizationGenesActiveFlag(true);
		else
			glHeatMapView.setClusterVisualizationGenesActiveFlag(false);

		if (storageVA.getGroupList() != null)
			glHeatMapView.setClusterVisualizationExperimentsActiveFlag(true);
		else
			glHeatMapView.setClusterVisualizationExperimentsActiveFlag(false);

		// all levels active (no one skipped)
		if (bSkipLevel1 == false && bSkipLevel2 == false) {
			renderRemoteViewsLevel_1_2_3_Active(gl);
		}
		// level 2 and 3 active (level 1 skipped)
		else if (bSkipLevel1 == true && bSkipLevel2 == false) {
			renderRemoteViewsLevel_2_3_Active(gl);
		}
		// level 3 active (level 1 and level 2 skipped)
		else if (bSkipLevel1 == true && bSkipLevel2 == true) {
			renderRemoteViewsLevel_3_Active(gl);
		} else {
			throw new IllegalStateException();
		}

		ConnectedElementRepresentationManager cerm = GeneralManager.get()
				.getViewGLCanvasManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

		contextMenu.render(gl, this);

	}

	private void renderRemoteViewsLevel_1_2_3_Active(GL gl) {
		float fright = 0.0f;
		float ftop = viewFrustum.getTop();

		float fleftOffset = 0.1f + renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS
				+ renderStyle.getWidthLevel2() * fScalingLevel2 + GAP_BETWEEN_LEVELS;

		if (bGeneDendrogramActive || bGeneDendrogramRenderCut)
			fleftOffset += renderStyle.getWidthGeneDendrogram();

		if (contentVA.getGroupList() != null)
			fleftOffset += 2 * renderStyle.getWidthClusterVisualization();

		if (!bIsHeatmapInFocus)
			fleftOffset -= 0.02f;

		fright = viewFrustum.getWidth() - fleftOffset;

		// gl.glTranslatef(fleftOffset, -0.2f, 0);

		// render embedded heat map
		if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut)
			ftop -= renderStyle.getHeightExperimentDendrogram();

		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION,
				glHeatMapView.getID()));

		heatMapRemoteElement.getTransform().getTranslation().set(fleftOffset, -0.2f, 0);
		heatMapRemoteElement.getTransform().getScale()
				.set(fright / 8, 1 * fAspectRatio, 1);
		// heatMapRemoteElement.getTransform().getTranslation().set(0, 0, 0);
		// heatMapRemoteElement.getTransform().getScale().set(1, 0.5f, 1);

		Transform transform = heatMapRemoteElement.getTransform();
		Vec3f translation = transform.getTranslation();
		Rotf rot = transform.getRotation();
		// Vec3f scale = transform.getScale();
		Vec3f axis = new Vec3f();
		float fAngle = rot.get(axis);

		gl.glTranslatef(translation.x(), translation.y(), translation.z());
		gl.glRotatef(Vec3f.convertRadiant2Grad(fAngle), axis.x(), axis.y(), axis.z());
		// gl.glScalef(scale.x(), scale.y(), scale.z());
		gl.glScalef(1, 1, 1);

		ViewFrustum embeddedHeatMapFrustum = glHeatMapView.getViewFrustum();
		embeddedHeatMapFrustum.setLeft(0);
		embeddedHeatMapFrustum.setRight(viewFrustum.getRight() - translation.x());
		embeddedHeatMapFrustum.setTop(ftop - translation.y());
		embeddedHeatMapFrustum.setBottom(-translation.y());

		glHeatMapView.setFrustum(embeddedHeatMapFrustum);
		glHeatMapView.displayRemote(gl);

		gl.glPopName();
		renderStyle.setWidthLevel3(glHeatMapView.getViewFrustum().getWidth() - 0.95f);

		// render embedded experiment dendrogram
		if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut) {

			float fOffsety = viewFrustum.getTop() - 1.45f;// renderStyle.getHeightExperimentDendrogram();

			gl.glTranslatef(0f, fOffsety, 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION,
					glExperimentDendrogramView.getID()));
			glExperimentDendrogramView.getViewFrustum().setTop(1.45f);
			glExperimentDendrogramView.getViewFrustum().setRight(
					renderStyle.getWidthLevel3());
			glExperimentDendrogramView.setRenderUntilCut(bExperimentDendrogramRenderCut);

			if (bFirstStartExperimentDendrogram) {
				if (glExperimentDendrogramView.setInitialPositionOfCut()) {
					bFirstStartExperimentDendrogram = false;
				}
			}

			glExperimentDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -fOffsety, 0f);
		}

		gl.glTranslatef(-fleftOffset, +0.2f, 0);

		// render embedded gene dendrogram
		if (bGeneDendrogramActive || bGeneDendrogramRenderCut) {

			gl.glTranslatef(0f, 0.4f, 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION,
					glContentDendrogramView.getID()));
			glContentDendrogramView.getViewFrustum().setTop(viewFrustum.getTop() - 0.6f);
			glContentDendrogramView.getViewFrustum().setRight(1.7f);
			glContentDendrogramView.setRenderUntilCut(bGeneDendrogramRenderCut);

			if (bFirstStartGeneDendrogram) {
				if (glContentDendrogramView.setInitialPositionOfCut()) {
					bFirstStartGeneDendrogram = false;
				}
			}

			glContentDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -0.4f, 0f);
		}

	}

	private void renderRemoteViewsLevel_2_3_Active(GL gl) {
		float fright = 0.0f;
		float ftop = viewFrustum.getTop();

		float fleftOffset = 0.1f + renderStyle.getWidthLevel2() * fScalingLevel2
				+ GAP_BETWEEN_LEVELS;

		if (contentVA.getGroupList() != null)
			fleftOffset += renderStyle.getWidthClusterVisualization();

		if (bGeneDendrogramActive || bGeneDendrogramRenderCut)
			fleftOffset += renderStyle.getWidthGeneDendrogram();

		if (!bIsHeatmapInFocus)
			fleftOffset -= 0.02;

		fright = viewFrustum.getWidth() - fleftOffset;

		gl.glTranslatef(fleftOffset, -0.2f, 0);

		// render embedded heat map
		if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut)
			ftop -= renderStyle.getHeightExperimentDendrogram();

		glHeatMapView.getViewFrustum().setTop(ftop);
		glHeatMapView.getViewFrustum().setRight(fright);
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION,
				glHeatMapView.getID()));
		glHeatMapView.displayRemote(gl);
		gl.glPopName();

		renderStyle.setWidthLevel3(glHeatMapView.getViewFrustum().getWidth() - 0.95f);

		// render embedded experiment dendrogram
		if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut) {
			float fOffsety = viewFrustum.getTop() - 1.45f;

			gl.glTranslatef(0f, fOffsety, 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION,
					glExperimentDendrogramView.getID()));
			glExperimentDendrogramView.getViewFrustum().setTop(1.45f);
			glExperimentDendrogramView.getViewFrustum().setRight(
					renderStyle.getWidthLevel3());
			glExperimentDendrogramView.setRenderUntilCut(bExperimentDendrogramRenderCut);

			if (bFirstStartExperimentDendrogram) {
				if (glExperimentDendrogramView.setInitialPositionOfCut()) {
					bFirstStartExperimentDendrogram = false;
				}
			}

			glExperimentDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -fOffsety, 0f);
		}

		gl.glTranslatef(-fleftOffset, +0.2f, 0);

		// render embedded gene dendrogram
		if (bGeneDendrogramActive || bGeneDendrogramRenderCut) {

			gl.glTranslatef(0f, 0.4f, 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION,
					glContentDendrogramView.getID()));
			glContentDendrogramView.getViewFrustum().setTop(viewFrustum.getTop() - 0.6f);
			glContentDendrogramView.getViewFrustum().setRight(1.7f);
			glContentDendrogramView.setRenderUntilCut(bGeneDendrogramRenderCut);

			if (bFirstStartGeneDendrogram) {
				if (glContentDendrogramView.setInitialPositionOfCut()) {
					bFirstStartGeneDendrogram = false;
				}
			}

			glContentDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -0.4f, 0f);
		}

	}

	private void renderRemoteViewsLevel_3_Active(GL gl) {
		float fright = 0;
		float ftop = viewFrustum.getTop();

		float fleftOffset = 0.1f;

		if (bGeneDendrogramActive || bGeneDendrogramRenderCut) {
			fleftOffset += renderStyle.getWidthGeneDendrogram();
		}

		fright = viewFrustum.getWidth() - fleftOffset;

		gl.glTranslatef(fleftOffset, -0.2f, 0);

		// render embedded heat map
		if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut)
			ftop -= renderStyle.getHeightExperimentDendrogram();

		glHeatMapView.getViewFrustum().setTop(ftop);
		glHeatMapView.getViewFrustum().setRight(fright);
		gl.glPushName(pickingManager.getPickingID(iUniqueID,
				EPickingType.HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION,
				glHeatMapView.getID()));
		glHeatMapView.displayRemote(gl);
		gl.glPopName();

		renderStyle.setWidthLevel3(glHeatMapView.getViewFrustum().getWidth() - 0.95f);

		// render embedded experiment dendrogram
		if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut) {
			float fOffsety = viewFrustum.getTop() - 1.45f;// -
			// renderStyle.getHeightExperimentDendrogram();

			gl.glTranslatef(0f, fOffsety, 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION,
					glExperimentDendrogramView.getID()));
			glExperimentDendrogramView.getViewFrustum().setTop(1.45f);
			glExperimentDendrogramView.getViewFrustum().setRight(
					renderStyle.getWidthLevel3());
			glExperimentDendrogramView.setRenderUntilCut(bExperimentDendrogramRenderCut);

			if (bFirstStartExperimentDendrogram) {
				if (glExperimentDendrogramView.setInitialPositionOfCut()) {
					bFirstStartExperimentDendrogram = false;
				}
			}

			glExperimentDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -fOffsety, 0f);
		}

		gl.glTranslatef(-fleftOffset, +0.2f, 0);

		// render embedded gene dendrogram
		if (bGeneDendrogramActive || bGeneDendrogramRenderCut) {

			gl.glTranslatef(0f, 0.4f, 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION,
					glContentDendrogramView.getID()));
			glContentDendrogramView.getViewFrustum().setTop(ftop - 0.6f);
			glContentDendrogramView.getViewFrustum().setRight(1.7f);
			glContentDendrogramView.setRenderUntilCut(bGeneDendrogramRenderCut);

			if (bFirstStartGeneDendrogram) {
				if (glContentDendrogramView.setInitialPositionOfCut()) {
					bFirstStartGeneDendrogram = false;
				}
			}

			glContentDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -0.4f, 0f);
		}

	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bRedrawTextures) {
			initTextures(gl);
			bRedrawTextures = false;
		}

		if (bHasFrustumChanged) {
			glHeatMapView.setDisplayListDirty();
			glContentDendrogramView.setRedrawDendrogram();
			glExperimentDendrogramView.setRedrawDendrogram();
			bHasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		// background color
		gl.glColor4fv(BACKGROUND_COLOR, 0);
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(0, 0, BACKGROUND_Z);
		gl.glVertex3f(viewFrustum.getRight(), 0, BACKGROUND_Z);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getHeight(), BACKGROUND_Z);
		gl.glVertex3f(0, viewFrustum.getHeight(), BACKGROUND_Z);
		gl.glEnd();

		// padding along borders
		viewFrustum.setTop(viewFrustum.getTop() - 0.6f);
		viewFrustum.setLeft(viewFrustum.getLeft() + 0.1f);
		gl.glTranslatef(0.1f, 0.4f, 0);

		if (set.getContentData(contentVAType).getContentTree() != null)
			bRenderDendrogramBackgroundWhite = true;
		else
			bRenderDendrogramBackgroundWhite = false;

		updateLevels();

		if (bGeneDendrogramActive || bGeneDendrogramRenderCut)
			gl.glTranslatef(+renderStyle.getWidthGeneDendrogram(), 0, 0);

		// all levels active (no one skipped)
		if (bSkipLevel1 == false && bSkipLevel2 == false) {
			renderViewsLevel_1_2_3_Active(gl);
		}
		// level 2 and 3 active (level 1 skipped)
		else if (bSkipLevel1 == true && bSkipLevel2 == false) {
			renderViewsLevel_2_3_Active(gl);
		}
		// level 3 active (level 1 and level 2 skipped)
		else if (bSkipLevel1 == true && bSkipLevel2 == true) {
			renderViewsLevel_3_Active(gl);
		} else {
			throw new IllegalStateException();
		}

		if (bGeneDendrogramActive || bGeneDendrogramRenderCut)
			gl.glTranslatef(-renderStyle.getWidthGeneDendrogram(), 0, 0);

		gl.glTranslatef(-0.1f, -0.4f, 0);
		viewFrustum.setTop(viewFrustum.getTop() + 0.6f);
		viewFrustum.setLeft(viewFrustum.getLeft() - 0.1f);

		if (hasDataWindowChanged)
			setEmbeddedHeatMapData();

		gl.glEndList();
	}

	private void updateLevels() {

		if (bIsHeatmapInFocus) {
			fScalingLevel2 = 0.2f;
		} else {
			fScalingLevel2 = 1.0f;
		}

		float fleftOffset = 0;

		if (bSkipLevel1 == false && bSkipLevel2 == false) {
			fleftOffset += renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS
					+ renderStyle.getWidthLevel2() * fScalingLevel2 + GAP_BETWEEN_LEVELS;
		}
		if (bSkipLevel1 == true && bSkipLevel2 == false) {
			fleftOffset += renderStyle.getWidthLevel2() * fScalingLevel2
					+ GAP_BETWEEN_LEVELS;
		}

		if (bGeneDendrogramActive || bGeneDendrogramRenderCut)
			fleftOffset += renderStyle.getWidthGeneDendrogram();

		if (contentVA.getGroupList() != null && bSkipLevel1 == false
				&& bSkipLevel2 == false)
			fleftOffset += 2 * renderStyle.getWidthClusterVisualization();

		if (contentVA.getGroupList() != null && bSkipLevel1 == true
				&& bSkipLevel2 == false)
			fleftOffset += renderStyle.getWidthClusterVisualization();

		float fWidthLevel3 = viewFrustum.getWidth() - fleftOffset - 0.95f;

		renderStyle.setWidthLevel3(fWidthLevel3);
	}

	private void renderViewsLevel_1_2_3_Active(GL gl) {

		renderClassAssignmentsGenesLevel1(gl);

		// all stuff for rendering level 1 (overview bar)
		if (pickingPointLevel1 != null)
			handleTexturePickingLevel1(gl);

		renderLevel1(gl);
		renderMarkerLevel1(gl);
		renderSelectedElementsLevel1(gl);
		renderCursorLevel1(gl);
		renderGeneDendrogramBackground(gl);

		// render sub tree for level 2
		renderSubTreeLevel2(gl);

		gl.glColor4f(1f, 1f, 1f, 1f);

		// all stuff for rendering level 2 (textures)

		gl.glTranslatef(renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS, 0, 0);
		if (contentVA.getGroupList() != null)
			gl.glTranslatef(renderStyle.getWidthClusterVisualization(), 0, 0);

		if (pickingPointLevel2 != null)
			handleTexturePickingLevel2(gl);

		renderLevel2(gl);
		renderMarkerLevel2(gl);
		renderSelectedElementsLevel2(gl);
		renderClassAssignmentsGenesLevel2(gl);
		renderClassAssignmentsExperimentsLevel2(gl);

		gl.glTranslatef(-(renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS), 0, 0);
		if (contentVA.getGroupList() != null)
			gl.glTranslatef(-renderStyle.getWidthClusterVisualization(), 0, 0);

		gl.glTranslatef(
				0.3f + renderStyle.getWidthLevel1() + renderStyle.getWidthLevel2()
						* fScalingLevel2 + GAP_BETWEEN_LEVELS, 0, 0);
		renderSubTreeLevel3(gl);
		gl.glTranslatef(
				-(0.3f + renderStyle.getWidthLevel1() + renderStyle.getWidthLevel2()
						* fScalingLevel2 + GAP_BETWEEN_LEVELS), 0, 0);

		if (contentVA.getGroupList() != null)
			gl.glTranslatef(2 * renderStyle.getWidthClusterVisualization(), 0, 0);
		gl.glTranslatef(renderStyle.getWidthLevel1() + renderStyle.getWidthLevel2()
				* fScalingLevel2, 0, 0);
		gl.glTranslatef(1 * GAP_BETWEEN_LEVELS, 0, 0);

		renderCursorLevel2(gl);

		gl.glTranslatef(1 * GAP_BETWEEN_LEVELS, 0, 0);

		renderClassAssignmentsGenesLevel3(gl);
		renderClassAssignmentsExperimentsLevel3(gl);
		renderExperimentDendrogramBackground(gl);
		gl.glTranslatef(-2 * GAP_BETWEEN_LEVELS, 0, 0);

		gl.glTranslatef(-(renderStyle.getWidthLevel1() + renderStyle.getWidthLevel2()
				* fScalingLevel2), 0, 0);

		if (contentVA.getGroupList() != null)
			gl.glTranslatef(-2 * renderStyle.getWidthClusterVisualization(), 0, 0);

	}

	private void renderViewsLevel_2_3_Active(GL gl) {

		if (pickingPointLevel2 != null)
			handleTexturePickingLevel2(gl);

		renderLevel2(gl);
		renderMarkerLevel2(gl);
		renderSelectedElementsLevel2(gl);
		renderClassAssignmentsGenesLevel2(gl);
		renderGeneDendrogramBackground(gl);

		renderClassAssignmentsExperimentsLevel2(gl);
		gl.glTranslatef(renderStyle.getWidthLevel2() * fScalingLevel2
				+ GAP_BETWEEN_LEVELS / 3, 0, 0);
		renderSubTreeLevel3(gl);
		gl.glTranslatef(
				-(renderStyle.getWidthLevel2() * fScalingLevel2 + GAP_BETWEEN_LEVELS / 3),
				0, 0);
	
		if (contentVA.getGroupList() != null)
			gl.glTranslatef(renderStyle.getWidthClusterVisualization(), 0, 0);
		gl.glTranslatef(renderStyle.getWidthLevel2() * fScalingLevel2, 0, 0);

		renderCursorLevel2(gl);

		gl.glTranslatef(GAP_BETWEEN_LEVELS, 0, 0);
		renderClassAssignmentsExperimentsLevel3(gl);
		renderClassAssignmentsGenesLevel3(gl);
		renderExperimentDendrogramBackground(gl);
		gl.glTranslatef(-GAP_BETWEEN_LEVELS, 0, 0);

		gl.glTranslatef(-renderStyle.getWidthLevel2() * fScalingLevel2, 0, 0);
		if (contentVA.getGroupList() != null)
			gl.glTranslatef(-renderStyle.getWidthClusterVisualization(), 0, 0);

	}

	private void renderViewsLevel_3_Active(GL gl) {

		renderClassAssignmentsGenesLevel3(gl);
		renderClassAssignmentsExperimentsLevel3(gl);
		renderGeneDendrogramBackground(gl);
		renderExperimentDendrogramBackground(gl);

	}

	private void renderGeneDendrogramBackground(GL gl) {

		float fHeight = viewFrustum.getHeight();
		float fSizeHeatmapArrow = renderStyle.getSizeHeatmapArrow();
		float fWidthGeneDendrogram = renderStyle.getWidthGeneDendrogram();

		float fWidthCurveTexture = GAP_BETWEEN_LEVELS / 4;

		if (bSkipLevel1 && bSkipLevel2
				&& (bExperimentDendrogramActive || bExperimentDendrogramRenderCut)) {
			fHeight -= renderStyle.getHeightExperimentDendrogram();
		}

		if (bGeneDendrogramActive || bGeneDendrogramRenderCut) {

			gl.glColor4f(1, 1, 1, 1);
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture, fHeight, 0);
			gl.glVertex3f(0, fHeight, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture, 0, 0);
			gl.glEnd();

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture, fHeight
					- fWidthCurveTexture, 0);
			gl.glVertex3f(-fWidthGeneDendrogram, fHeight - fWidthCurveTexture, 0);
			gl.glVertex3f(-fWidthGeneDendrogram, +fWidthCurveTexture, 0);
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture,
					+fWidthCurveTexture, 0);
			gl.glEnd();

			gl.glLineWidth(1f);
			gl.glColor4f(1, 0, 0, 1);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(-fWidthGeneDendrogram, fHeight, 0);
			gl.glVertex3f(-fWidthGeneDendrogram, -0.4f, 0);
			gl.glEnd();

			gl.glColor4f(1, 1, 1, 1);
			gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

			Texture textureMaskNeg = textureManager.getIconTexture(gl,
					EIconTextures.NAVIGATION_MASK_CURVE_NEG_WHITE);
			textureMaskNeg.enable();
			textureMaskNeg.bind();

			TextureCoords texCoordsMaskNeg = textureMaskNeg.getImageTexCoords();

			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(-fWidthGeneDendrogram, fHeight - fWidthCurveTexture, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture, fHeight
					- fWidthCurveTexture, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.top());
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture, fHeight, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.top());
			gl.glVertex3f(-fWidthGeneDendrogram, fHeight, 0);
			gl.glEnd();

			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture, fWidthCurveTexture,
					0);
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(-fWidthGeneDendrogram, fWidthCurveTexture, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.top());
			gl.glVertex3f(-fWidthGeneDendrogram, 0, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.top());
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture, 0, 0);
			gl.glEnd();
			textureMaskNeg.disable();
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glPopAttrib();
		}

		if (set.getContentData(contentVAType).getContentTree() != null) {

			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

			Texture tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_ARROW);
			tempTexture.enable();
			tempTexture.bind();

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_ACTIVATE_HORIZONTAL_DENDROGRAM, 1));
			if (bGeneDendrogramActive) {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(-fWidthGeneDendrogram, -0.4f, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(-fWidthGeneDendrogram, -0.4f + fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(-fWidthGeneDendrogram + fSizeHeatmapArrow, -0.4f
						+ fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(-fWidthGeneDendrogram + fSizeHeatmapArrow, -0.4f, BUTTON_Z);
				gl.glEnd();
			} else {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(-fWidthGeneDendrogram, -0.4f, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(-fWidthGeneDendrogram, -0.4f + fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(-fWidthGeneDendrogram + fSizeHeatmapArrow, -0.4f
						+ fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(-fWidthGeneDendrogram + fSizeHeatmapArrow, -0.4f, BUTTON_Z);
				gl.glEnd();
			}
			gl.glPopName();

			tempTexture.disable();
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	private void renderExperimentDendrogramBackground(GL gl) {

		float fHeight = viewFrustum.getHeight();
		float fWidthLevel3 = renderStyle.getWidthLevel3();
		float fSizeHeatmapArrow = renderStyle.getSizeHeatmapArrow();
		float fHeightExperimentDendrogram = renderStyle.getHeightExperimentDendrogram();

		float fWidthCurveTexture = GAP_BETWEEN_LEVELS / 4;

		if (bExperimentDendrogramActive || bExperimentDendrogramRenderCut) {

			gl.glColor4f(1, 1, 1, 1);
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(0, fHeight - fWidthCurveTexture, 0);
			gl.glVertex3f(fWidthLevel3, fHeight - fWidthCurveTexture, 0);
			gl.glVertex3f(fWidthLevel3, fHeight - fHeightExperimentDendrogram, 0);
			gl.glVertex3f(0, fHeight - fHeightExperimentDendrogram, 0);
			gl.glEnd();

			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(fWidthCurveTexture, fHeight, 0);
			gl.glVertex3f(fWidthLevel3 - fWidthCurveTexture, fHeight, 0);
			gl.glVertex3f(fWidthLevel3 - fWidthCurveTexture,
					fHeight - fWidthCurveTexture, 0);
			gl.glVertex3f(fWidthCurveTexture, fHeight - fWidthCurveTexture, 0);
			gl.glEnd();

			gl.glLineWidth(1f);
			gl.glColor4f(1, 0, 0, 1);
			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(0, fHeight, 0);
			gl.glVertex3f(fWidthLevel3 + 0.4f, fHeight, 0);
			gl.glEnd();

			gl.glColor4f(1, 1, 1, 1);
			gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

			Texture textureMaskNeg = textureManager.getIconTexture(gl,
					EIconTextures.NAVIGATION_MASK_CURVE_NEG_WHITE);
			textureMaskNeg.enable();
			textureMaskNeg.bind();

			TextureCoords texCoordsMaskNeg = textureMaskNeg.getImageTexCoords();

			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(0, fHeight - fWidthCurveTexture, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(fWidthCurveTexture, fHeight - fWidthCurveTexture, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.top());
			gl.glVertex3f(fWidthCurveTexture, fHeight, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.top());
			gl.glVertex3f(0, fHeight, 0);
			gl.glEnd();

			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(fWidthLevel3 - fWidthCurveTexture,
					fHeight - fWidthCurveTexture, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(fWidthLevel3, fHeight - fWidthCurveTexture, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.top());
			gl.glVertex3f(fWidthLevel3, fHeight, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.top());
			gl.glVertex3f(fWidthLevel3 - fWidthCurveTexture, fHeight, 0);
			gl.glEnd();
			textureMaskNeg.disable();
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glPopAttrib();
		}

		if (!set.getStorageData(Set.STORAGE).isDefaultTree()) {

			gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);

			Texture tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_ARROW);
			tempTexture.enable();
			tempTexture.bind();

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(iUniqueID,
					EPickingType.HIER_HEAT_MAP_ACTIVATE_STORAGE_DENDROGRAM, 1));
			if (bExperimentDendrogramActive) {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidthLevel3 + 0.4f - fSizeHeatmapArrow, fHeight, BUTTON_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidthLevel3 + 0.4f - fSizeHeatmapArrow, fHeight
						- fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidthLevel3 + 0.4f, fHeight - fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidthLevel3 + 0.4f, fHeight, BUTTON_Z);
				gl.glEnd();
			} else {
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(fWidthLevel3 + 0.4f - fSizeHeatmapArrow, fHeight, BUTTON_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(fWidthLevel3 + 0.4f - fSizeHeatmapArrow, fHeight
						- fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(fWidthLevel3 + 0.4f, fHeight - fSizeHeatmapArrow, BUTTON_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(fWidthLevel3 + 0.4f, fHeight, BUTTON_Z);
				gl.glEnd();
			}
			gl.glPopName();

			tempTexture.disable();
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	/**
	 * Sets the data shown in the embedded heat map based on the selected area
	 * in the first two levels
	 */
	private void setEmbeddedHeatMapData() {
		hasDataWindowChanged = false;
		int offset = iFirstSampleLevel1 + iFirstSampleLevel2;

		if (offset < 0) {
			throw new IllegalStateException("First Sample Level 1 (" + iFirstSampleLevel1
					+ ") was smaller than First Sample Level 2 (" + iFirstSampleLevel2
					+ ")");
		}

		ContentVirtualArray hmContentVa = new ContentVirtualArray();

		// every time we change the window of the embedded heat map we need to
		// remove the previously used ids
		connectedElementRepresentationManager.clearByView(glHeatMapView.getID());
		connectedElementRepresentationManager.clearByView(this.getID());

		int vaIndex = 0;
		int contentID = 0;

		for (int count = 0; count < iSamplesPerHeatmap; count++) {
			vaIndex = offset + count;
			if (vaIndex < contentVA.size()) {
				contentID = contentVA.get(vaIndex);
				hmContentVa.append(contentID);
			}

		}
		glHeatMapView.setContentVA(hmContentVa);

	}

	@Override
	protected void initLists() {

		if (bRenderOnlyContext)
			contentVAType = ISet.CONTENT_CONTEXT;
		else
			contentVAType = ISet.CONTENT;

		contentVA = dataDomain.getContentVA(contentVAType);
		storageVA = dataDomain.getStorageVA(storageVAType);

		// In case of importing group info
		// if (set.isGeneClusterInfo())
		// contentVA.setGroupList(set.getContentGroupList());
		// if (set.isExperimentClusterInfo())
		// storageVA.setGroupList(set.getStorageGroupList());

		contentSelectionManager.setVA(contentVA);
		storageSelectionManager.setVA(storageVA);

		setDisplayListDirty();

	}

	@Override
	public String toString() {
		return "Standalone hierarchical heat map, rendered remote: " + isRenderedRemote()
				+ ", contentSize: " + contentVA.size() + ", storageSize: "
				+ storageVA.size() + ", contentVAType: " + contentVAType
				+ ", remoteRenderer: " + getRemoteRenderingGLCanvas();
	}

	@Override
	public String getShortInfo() {
		return "Hierarchical Heat Map (" + contentVA.size() + " "
				+ dataDomain.getContentName(false, true) + " / " + storageVA.size()
				+ " experiments)";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Hierarchical Heat Map\n");

		sInfoText.append(contentVA.size() + " " + dataDomain.getContentName(true, true)
				+ " in rows and " + storageVA.size() + " experiments in columns.\n");

		if (bRenderOnlyContext) {
			sInfoText.append("Showing only " + " "
					+ dataDomain.getContentName(false, true)
					+ " which occur in one of the other views in focus\n");
		} else {
			if (dataFilterLevel == EDataFilterLevel.COMPLETE) {
				sInfoText.append("Showing all " + dataDomain.getContentName(false, true)
						+ " in the dataset\n");
			} else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING) {
				sInfoText.append("Showing all " + dataDomain.getContentName(false, true)
						+ " that have a known DAVID ID mapping\n");
			} else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT) {
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
			fArPickingCoords = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, pickingPointLevel1.x,
							pickingPointLevel1.y);
			fOffsety = viewFrustum.getHeight() - fArPickingCoords[1] + 0.4f;
			iPickedSampleLevel1 = (int) Math.ceil((double) fOffsety / fHeightSample);
			pickingPointLevel1 = null;

			if (iSamplesLevel2 % 2 == 0) {
				iFirstSampleLevel1 = iPickedSampleLevel1
						- (int) Math.floor((double) iSamplesLevel2 / 2) + 1;
				iLastSampleLevel1 = iPickedSampleLevel1
						+ (int) Math.floor((double) iSamplesLevel2 / 2);
			} else {
				iFirstSampleLevel1 = iPickedSampleLevel1
						- (int) Math.ceil((double) iSamplesLevel2 / 2);
				iLastSampleLevel1 = iPickedSampleLevel1
						+ (int) Math.floor((double) iSamplesLevel2 / 2);
			}

			if (iPickedSampleLevel1 < iSamplesLevel2 / 2) {
				iPickedSampleLevel1 = (int) Math.floor((double) iSamplesLevel2 / 2);
				iFirstSampleLevel1 = 0;
				iLastSampleLevel1 = iSamplesLevel2 - 1;
			} else if (iPickedSampleLevel1 > iNumberSample - 1 - iSamplesLevel2 / 2) {
				iPickedSampleLevel1 = (int) Math.ceil((double) iNumberSample
						- iSamplesLevel2 / 2);
				iLastSampleLevel1 = iNumberSample - 1;
				iFirstSampleLevel1 = iNumberSample - iSamplesLevel2;
			}
		}
		setDisplayListDirty();
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
			fArPickingCoords = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, pickingPointLevel2.x,
							pickingPointLevel2.y);
			fOffsety = viewFrustum.getHeight() - fArPickingCoords[1] + 0.4f;
			iPickedSampleLevel2 = (int) Math.ceil((double) fOffsety / fHeightSample);
			pickingPointLevel2 = null;

			if (iSamplesLevel2 % 2 == 0) {
				iFirstSampleLevel2 = iPickedSampleLevel2
						- (int) Math.floor((double) iSamplesPerHeatmap / 2) + 1;
				iLastSampleLevel2 = iPickedSampleLevel2
						+ (int) Math.floor((double) iSamplesPerHeatmap / 2);
			} else {
				iFirstSampleLevel2 = iPickedSampleLevel2
						- (int) Math.ceil((double) iSamplesPerHeatmap / 2);
				iLastSampleLevel2 = iPickedSampleLevel2
						+ (int) Math.floor((double) iSamplesPerHeatmap / 2) - 1;
			}

			if (iPickedSampleLevel2 < iSamplesPerHeatmap / 2) {
				iPickedSampleLevel2 = (int) Math.floor((double) iSamplesPerHeatmap / 2);
				iFirstSampleLevel2 = 0;
				iLastSampleLevel2 = iSamplesPerHeatmap - 1;
			} else if (iPickedSampleLevel2 > iNumberSample - 1 - iSamplesPerHeatmap / 2) {
				iPickedSampleLevel2 = (int) Math.ceil((double) iNumberSample
						- iSamplesPerHeatmap / 2);
				iLastSampleLevel2 = iNumberSample - 1;
				iFirstSampleLevel2 = iNumberSample - iSamplesPerHeatmap;
			}
		}
		// setDisplayListDirty();
		// hasDataWindowChanged = true;
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
			fgaps = GAP_BETWEEN_LEVELS + 0.2f;
		else
			fgaps = GAP_BETWEEN_LEVELS + GAP_BETWEEN_LEVELS;

		float fleftOffset = 0.075f + // width level 1
				fgaps + viewFrustum.getWidth() / 4f * fScalingLevel2;

		float fWidthSample = renderStyle.getWidthLevel3() / iNrSamples;

		int currentElement;

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		StorageGroupList groupList = storageVA.getGroupList();

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		int iNrElementsInGroup = groupList.get(iExpGroupToDrag).getNrElements();
		float currentWidth = fWidthSample * iNrElementsInGroup;
		float fHeight = viewFrustum.getHeight();

		if (fArTargetWorldCoordinates[0] > fleftOffset
				&& fArTargetWorldCoordinates[0] < fleftOffset
						+ renderStyle.getWidthLevel3()) {
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(fArTargetWorldCoordinates[0], fHeight, 0);
			gl.glVertex3f(fArTargetWorldCoordinates[0], fHeight - 0.1f, 0);
			gl.glVertex3f(fArTargetWorldCoordinates[0] + currentWidth, fHeight - 0.1f, 0);
			gl.glVertex3f(fArTargetWorldCoordinates[0] + currentWidth, fHeight, 0);
			gl.glEnd();
		}

		float fXPosRelease = fArTargetWorldCoordinates[0] - fleftOffset;

		currentElement = (int) Math.ceil((double) fXPosRelease / fWidthSample);

		int iElemOffset = 0;
		int cnt = 0;

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

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		ContentGroupList groupList = contentVA.getGroupList();

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		int iNrElementsInGroup = groupList.get(iGeneGroupToDrag).getNrElements();
		float currentHeight = fHeightSample * iNrElementsInGroup;

		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(0f, fArTargetWorldCoordinates[1], 0);
		gl.glVertex3f(0f, fArTargetWorldCoordinates[1] + currentHeight, 0);
		gl.glVertex3f(0.1f, fArTargetWorldCoordinates[1] + currentHeight, 0);
		gl.glVertex3f(0.1f, fArTargetWorldCoordinates[1], 0);
		gl.glEnd();

		fOffsety = viewFrustum.getHeight() - fArTargetWorldCoordinates[1] - 0.4f;
		currentElement = (int) Math.ceil((double) fOffsety / fHeightSample);

		int iElemOffset = 0;
		int cnt = 0;

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

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		if (glMouseListener.wasMouseReleased()) {
			// bSplitGroupGene = false;

			fArDraggedPoint = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, DraggingPoint.x,
							DraggingPoint.y);

			float fYPosDrag = fArDraggedPoint[1] - 0.4f;
			float fYPosRelease = fArTargetWorldCoordinates[1] - 0.4f;

			float fHeight = viewFrustum.getHeight() - 0.6f;
			int iNrSamples = contentVA.size();
			float fHeightSample = fHeight / iNrSamples;

			int iFirstSample = iNrSamples
					- (int) Math.floor((double) fYPosDrag / fHeightSample);
			int iLastSample = iNrSamples
					- (int) Math.ceil((double) fYPosRelease / fHeightSample);

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

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		if (glMouseListener.wasMouseReleased()) {
			// bSplitGroupExp = false;

			fArDraggedPoint = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, DraggingPoint.x,
							DraggingPoint.y);

			float fXPosDrag = fArDraggedPoint[0] - 0.7f;
			float fXPosRelease = fArTargetWorldCoordinates[0] - 0.7f;

			float fWidth = viewFrustum.getWidth() / 4.0f * fScalingLevel2;
			int iNrSamples = storageVA.size();
			float fWidthSample = fWidth / iNrSamples;

			int iFirstSample = (int) Math.floor((double) fXPosDrag / fWidthSample);
			int iLastSample = (int) Math.ceil((double) fXPosRelease / fWidthSample);

			if (storageVA.getGroupList().split(iGroupToSplit, iLastSample, iFirstSample) == false)
				System.out.println("Operation not allowed!!");
		}
	}

	/**
	 * Function used for updating position of block (block of elements rendered
	 * in level 2) in case of dragging
	 * 
	 * @param gl
	 */
	private void handleBlockDraggingLevel1(final GL gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / iNumberOfElements;
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		iselElement = (int) Math.floor((double) (fTextureHeight - fYPosMouse) / fStep);
		if (iSamplesLevel2 % 2 == 0) {
			if ((iselElement - (int) Math.floor((double) iSamplesLevel2 / 2) + 1) >= 0
					&& (iselElement + (int) Math.floor((double) iSamplesLevel2 / 2)) < iNumberOfElements) {
				iFirstSampleLevel1 = iselElement
						- (int) Math.floor((double) iSamplesLevel2 / 2) + 1;
				fPosCursorFirstElementLevel1 = fTextureHeight
						- (iFirstSampleLevel1 * fStep);
				iLastSampleLevel1 = iselElement
						+ (int) Math.floor((double) iSamplesLevel2 / 2);
				fPosCursorLastElementLevel1 = fTextureHeight
						- ((iLastSampleLevel1 + 1) * fStep);
			}
		} else {
			if ((iselElement - (int) Math.ceil((double) iSamplesLevel2 / 2)) >= 0
					&& (iselElement + (int) Math.floor((double) iSamplesLevel2 / 2)) < iNumberOfElements) {
				iFirstSampleLevel1 = iselElement
						- (int) Math.ceil((double) iSamplesLevel2 / 2);
				fPosCursorFirstElementLevel1 = fTextureHeight
						- (iFirstSampleLevel1 * fStep);
				iLastSampleLevel1 = iselElement
						+ (int) Math.floor((double) iSamplesLevel2 / 2);
				fPosCursorLastElementLevel1 = fTextureHeight
						- ((iLastSampleLevel1 + 1) * fStep);
			}
		}

		setDisplayListDirty();
		hasDataWindowChanged = true;

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingWholeBlockLevel1 = false;
			bDisableCursorDraggingLevel1 = false;
			bActivateDraggingLevel1 = false;
		}
	}

	/**
	 * Function used for updating position of block (block of elements rendered
	 * in level 3) in case of dragging
	 * 
	 * @param gl
	 */
	private void handleBlockDraggingLevel2(final GL gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / iSamplesLevel2;
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		iselElement = (int) Math.floor((double) (fTextureHeight - fYPosMouse) / fStep);
		if (iSamplesPerHeatmap % 2 == 0) {
			if ((iselElement - (int) Math.floor((double) iSamplesPerHeatmap / 2) + 1) >= 0
					&& (iselElement + (int) Math.floor((double) iSamplesPerHeatmap / 2)) < iSamplesLevel2) {
				iFirstSampleLevel2 = iselElement
						- (int) Math.floor((double) iSamplesPerHeatmap / 2) + 1;
				fPosCursorFirstElementLevel2 = fTextureHeight
						- (iFirstSampleLevel2 * fStep);
				iLastSampleLevel2 = iselElement
						+ (int) Math.floor((double) iSamplesPerHeatmap / 2);
				fPosCursorLastElementLevel2 = fTextureHeight
						- ((iLastSampleLevel2 + 1) * fStep);
			}
		} else {
			if ((iselElement - (int) Math.ceil((double) iSamplesPerHeatmap / 2)) >= 0
					&& (iselElement + (int) Math.floor((double) iSamplesPerHeatmap / 2)) < iSamplesLevel2) {
				iFirstSampleLevel2 = iselElement
						- (int) Math.ceil((double) iSamplesPerHeatmap / 2);
				fPosCursorFirstElementLevel2 = fTextureHeight
						- (iFirstSampleLevel2 * fStep);
				iLastSampleLevel2 = iselElement
						+ (int) Math.floor((double) iSamplesPerHeatmap / 2);
				fPosCursorLastElementLevel2 = fTextureHeight
						- ((iLastSampleLevel2 + 1) * fStep);
			}
		}

		setDisplayListDirty();
		hasDataWindowChanged = true;
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

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / iNumberOfElements;
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		// cursor for iFirstElement
		if (iDraggedCursorLevel1 == 1) {
			if (fYPosMouse > fPosCursorLastElementLevel1
					&& fYPosMouse <= viewFrustum.getHeight() - 0.6f) {
				iselElement = (int) Math.floor((double) (fTextureHeight - fYPosMouse)
						/ fStep);
				iNrSamples = iLastSampleLevel1 - iselElement + 1;
				if (iNrSamples >= MIN_SAMPLES_LEVEL_2 && iNrSamples < MAX_SAMPLES_LEVEL_2) {
					fPosCursorFirstElementLevel1 = fYPosMouse;
					iFirstSampleLevel1 = iselElement;
					iSamplesLevel2 = iLastSampleLevel1 - iFirstSampleLevel1 + 1;

					// // update Preference store
					// generalManager.getPreferenceStore().setValue(
					// PreferenceConstants.HM_NUM_SAMPLES_PER_TEXTURE,
					// iSamplesPerTexture);
				}
			}
		}
		// cursor for iLastElement
		if (iDraggedCursorLevel1 == 2) {
			if (fYPosMouse < fPosCursorFirstElementLevel1 && fYPosMouse >= 0.0f) {
				iselElement = (int) Math.floor((double) (fTextureHeight - fYPosMouse)
						/ fStep);
				iNrSamples = iselElement - iFirstSampleLevel1 + 1;
				if (iNrSamples >= MIN_SAMPLES_LEVEL_2 && iNrSamples < MAX_SAMPLES_LEVEL_2) {
					fPosCursorLastElementLevel1 = fYPosMouse;
					iLastSampleLevel1 = iselElement;
					iSamplesLevel2 = iLastSampleLevel1 - iFirstSampleLevel1 + 1;

					// // update Preference store
					// generalManager.getPreferenceStore().setValue(
					// PreferenceConstants.HM_NUM_SAMPLES_PER_TEXTURE,
					// iSamplesPerTexture);
				}
			}
		}

		setDisplayListDirty();
		hasDataWindowChanged = true;

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingActiveLevel1 = false;
			bDisableBlockDraggingLevel1 = false;
			bActivateDraggingLevel1 = false;
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

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / iSamplesLevel2;
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		// cursor for iFirstElement
		if (iDraggedCursorLevel2 == 1) {
			if (fYPosMouse > fPosCursorLastElementLevel2
					&& fYPosMouse <= viewFrustum.getHeight() - 0.6f) {
				iselElement = (int) Math.floor((double) (fTextureHeight - fYPosMouse)
						/ fStep);
				iNrSamples = iLastSampleLevel2 - iselElement + 1;
				if (iNrSamples >= MIN_SAMPLES_PER_HEATMAP
						&& iNrSamples < MAX_SAMPLES_PER_HEATMAP) {
					fPosCursorFirstElementLevel2 = fYPosMouse;
					iFirstSampleLevel2 = iselElement;
					iSamplesPerHeatmap = iLastSampleLevel2 - iFirstSampleLevel2 + 1;

					// update Preference store
					// generalManager.getPreferenceStore().setValue(
					// PreferenceConstants.HM_NUM_SAMPLES_PER_HEATMAP,
					// iSamplesPerHeatmap);
				}
			}
		}
		// cursor for iLastElement
		if (iDraggedCursorLevel2 == 2) {
			if (fYPosMouse < fPosCursorFirstElementLevel2 && fYPosMouse >= 0.0f) {
				iselElement = (int) Math.floor((double) (fTextureHeight - fYPosMouse)
						/ fStep);
				iNrSamples = iselElement - iFirstSampleLevel2 + 1;
				if (iNrSamples >= MIN_SAMPLES_PER_HEATMAP
						&& iNrSamples < MAX_SAMPLES_PER_HEATMAP) {
					fPosCursorLastElementLevel2 = fYPosMouse;
					iLastSampleLevel2 = iselElement;
					iSamplesPerHeatmap = iLastSampleLevel2 - iFirstSampleLevel2 + 1;

					// update Preference store
					// generalManager.getPreferenceStore().setValue(
					// PreferenceConstants.HM_NUM_SAMPLES_PER_HEATMAP,
					// iSamplesPerHeatmap);
				}
			}
		}

		setDisplayListDirty();
		hasDataWindowChanged = true;

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
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == DetailLevel.VERY_LOW) {
			return;
		}

		switch (pickingType) {

		// handling the groups/clusters of genes
		case HIER_HEAT_MAP_GENES_GROUP:
			switch (pickingMode) {
			case RIGHT_CLICKED:

				boolean bEnableInterchange = false;
				boolean bEnableMerge = false;
				boolean bEnableExport = true;
				int iNrSelectedGroups = 0;

				ContentGroupList tempGroupList = contentVA.getGroupList();

				for (Group group : tempGroupList) {
					if (group.getSelectionType() == SelectionType.SELECTION)
						iNrSelectedGroups++;
				}

				if (iNrSelectedGroups >= 1)
					bEnableExport = true;

				if (iNrSelectedGroups >= 2) {

					bEnableMerge = true;

					if (iNrSelectedGroups == 2)
						bEnableInterchange = true;
				}

				GroupContextMenuItemContainer groupContextMenuItemContainer = new GroupContextMenuItemContainer();
				groupContextMenuItemContainer.initContextMenu(true, bEnableMerge,
						bEnableInterchange, bEnableExport);
				groupContextMenuItemContainer.setDataDomain(dataDomain);
				groupContextMenuItemContainer.setContentIDs(contentIDType,
						contentVA.getGeneIdsOfGroup(iExternalID));

				BookmarkItem bookmarkItem = new BookmarkItem(contentIDType,
						contentVA.getGeneIdsOfGroup(iExternalID));
				groupContextMenuItemContainer.addContextMenuItem(bookmarkItem);

				contextMenu.addItemContanier(groupContextMenuItemContainer);
				contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
						.getWidth(), getParentGLCanvas().getHeight());
				contextMenu.setMasterGLView(this);

				if (contentVA.getGroupList().get(iExternalID).getSelectionType() == SelectionType.SELECTION)
					break;
				// else we want to go to clicked as well
			case CLICKED:
				contentVA.getGroupList().get(iExternalID).togglSelectionType();
				deactivateAllDraggingCursor();
				bActivateDraggingGenes = true;

				// ArrayList<Integer> temp =
				// contentVA.getGeneIdsOfGroup( iExternalID);
				// for (int i = 0; i < temp.size(); i++) {
				// System.out.println(idMappingManager.getID(EIDType.EXPRESSION_INDEX,
				// EIDType.GENE_SYMBOL, temp.get(i)));
				// }

				// ArrayList<Float> representatives =
				// contentVA.getGroupList().determineRepresentativeElement(set,
				// contentVA,
				// storageVA, iExternalID, true);

				// set node in tree selected
				// if
				// (contentVA.getGroupList().get(iExternalID).getClusterNode()
				// != null) {
				// contentVA.getGroupList().get(iExternalID).getClusterNode().togglSelectionType();
				// }

				// System.out.println(contentVA.getGroupList().get(iExternalID).getIdxExample());
				// System.out.println(idMappingManager.getID(EIDType.EXPRESSION_INDEX,
				// EIDType.GENE_SYMBOL,
				// contentVA.getGroupList().get(iExternalID).getIdxExample()));

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
			case RIGHT_CLICKED:

				boolean bEnableInterchange = false;
				boolean bEnableMerge = false;
				boolean bEnableExport = true;
				int iNrSelectedGroups = 0;

				StorageGroupList tempGroupList = storageVA.getGroupList();

				for (Group group : tempGroupList) {
					if (group.getSelectionType() == SelectionType.SELECTION)
						iNrSelectedGroups++;
				}

				if (iNrSelectedGroups >= 2) {

					bEnableMerge = true;

					if (iNrSelectedGroups == 2)
						bEnableInterchange = true;
				}
				GroupContextMenuItemContainer groupContextMenuItemContainer = new GroupContextMenuItemContainer();
				groupContextMenuItemContainer.initContextMenu(false, bEnableMerge,
						bEnableInterchange, bEnableExport);

				contextMenu.addItemContanier(groupContextMenuItemContainer);
				contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
						.getWidth(), getParentGLCanvas().getHeight());
				contextMenu.setMasterGLView(this);

				if (storageVA.getGroupList().get(iExternalID).getSelectionType() == SelectionType.SELECTION)
					break;
				// else we want to do clicked here as well
			case CLICKED:
				storageVA.getGroupList().get(iExternalID).togglSelectionType();
				deactivateAllDraggingCursor();
				bActivateDraggingExperiments = true;

				// ArrayList<Integer> temp =
				// storageVA.getGeneIdsOfGroup( iExternalID);
				// for (int i = 0; i < temp.size(); i++) {
				// System.out.println(set.get(temp.get(i)).getLabel());
				// }

				// ArrayList<Float> representatives =
				// contentVA.getGroupList().determineRepresentativeElement(set,
				// contentVA,
				// storageVA, iExternalID, false);

				// set node in tree selected
				// if
				// (storageVA.getGroupList().get(iExternalID).getClusterNode()
				// != null) {
				// storageVA.getGroupList().get(iExternalID).getClusterNode().togglSelectionType();
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
			}
			break;
		// handle click on button for setting EHM in focus
		case HIER_HEAT_MAP_INFOCUS_SELECTION:
			switch (pickingMode) {

			case CLICKED:

				bIsHeatmapInFocus = bIsHeatmapInFocus == true ? false : true;
				glHeatMapView.setDisplayListDirty();
				glContentDendrogramView.setDisplayListDirty();
				glExperimentDendrogramView.setRedrawDendrogram();
				setDisplayListDirty();
				break;
			}
			break;

		// handle click on button for setting experiment dendrogram active
		case HIER_HEAT_MAP_ACTIVATE_STORAGE_DENDROGRAM:
			switch (pickingMode) {

			case CLICKED:
				bExperimentDendrogramActive = bExperimentDendrogramActive == true ? false
						: true;

				if (bExperimentDendrogramActive == true) {
					float highDendro = glExperimentDendrogramView.getViewFrustum()
							.getHeight();

					if (highDendro > 0.5 && highDendro <= 1.5f)
						renderStyle.setHeightExperimentDendrogram(highDendro);

					bExperimentDendrogramRenderCut = false;

				} else {
					float fPosCut = glExperimentDendrogramView.getPositionOfCut();
					float highDendro = glExperimentDendrogramView.getViewFrustum()
							.getHeight();
					renderStyle
							.setHeightExperimentDendrogram(highDendro - fPosCut + 0.1f);
					bExperimentDendrogramRenderCut = true;
				}

				glExperimentDendrogramView.setRenderUntilCut(bGeneDendrogramRenderCut);
				glExperimentDendrogramView.setDisplayListDirty();
				glContentDendrogramView.setRedrawDendrogram();
				glHeatMapView.setDisplayListDirty();
				setDisplayListDirty();
				break;
			}
			break;

		// handle click on button for setting gene dendrogram active
		case HIER_HEAT_MAP_ACTIVATE_HORIZONTAL_DENDROGRAM:
			switch (pickingMode) {

			case CLICKED:
				bGeneDendrogramActive = bGeneDendrogramActive == true ? false : true;

				if (bGeneDendrogramActive == true) {
					float widthDendro = glContentDendrogramView.getViewFrustum()
							.getWidth();

					if (widthDendro > 0.5 && widthDendro <= 1.7f)
						renderStyle.setWidthGeneDendrogram(widthDendro - 0.1f);

					bGeneDendrogramRenderCut = false;
				} else {
					float temp = glContentDendrogramView.getPositionOfCut();
					renderStyle.setWidthGeneDendrogram(temp);
					bGeneDendrogramRenderCut = true;
				}

				glContentDendrogramView.setRenderUntilCut(bGeneDendrogramRenderCut);
				glContentDendrogramView.setDisplayListDirty();
				glExperimentDendrogramView.setRedrawDendrogram();
				glHeatMapView.setDisplayListDirty();
				setDisplayListDirty();
				break;
			}
			break;

		// handle dragging cursor for first and last element of block in
		// level 1
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
			}
			break;

		// handle dragging cursor for first and last element of block in
		// level 2
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
			}
			break;

		// handle click on level 1 (overview bar)
		case HIER_HEAT_MAP_TEXTURE_SELECTION:
			switch (pickingMode) {
			case CLICKED:

				pickingPointLevel1 = pick.getPickedPoint();
				hasDataWindowChanged = true;
				setDisplayListDirty();
				break;
			}
			break;

		// handle click on level 2
		case HIER_HEAT_MAP_FIELD_SELECTION:
			switch (pickingMode) {
			case CLICKED:
				pickingPointLevel2 = pick.getPickedPoint();
				hasDataWindowChanged = true;
				setDisplayListDirty();
				break;
			}
			break;

		// handle click on level 3 (EHM)
		case HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION:
			switch (pickingMode) {
			case RIGHT_CLICKED:
				contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
						.getWidth(), getParentGLCanvas().getHeight());
				contextMenu.setMasterGLView(this);
				break;
			}
			break;

		// handle click on gene dendrogram
		case HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION:
			switch (pickingMode) {
			case RIGHT_CLICKED:
				contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
						.getWidth(), getParentGLCanvas().getHeight());
				contextMenu.setMasterGLView(this);
				break;
			}
			break;

		// handle click on gene dendrogram
		case HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION:
			switch (pickingMode) {
			case RIGHT_CLICKED:
				contextMenu.setLocation(pick.getPickedPoint(), getParentGLCanvas()
						.getWidth(), getParentGLCanvas().getHeight());
				contextMenu.setMasterGLView(this);
				break;
			}
			break;
		}
		// setDisplayListDirty();
	}

	@Override
	protected ArrayList<SelectedElementRep> createElementRep(IDType idType,
			int iStorageIndex) {

		return null;
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext) {
		throw new IllegalStateException(
				"Rendering only context not supported for the hierachical heat map");
	}

	@Override
	public void clearAllSelections() {

		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		if (bSkipLevel1 == false)
			initPosCursorLevel1();
		bRedrawTextures = true;
		setDisplayListDirty();

		glHeatMapView.setDisplayListDirty();
		glContentDendrogramView.setDisplayListDirty();
		glExperimentDendrogramView.setDisplayListDirty();

		// group/cluster selections
		if (storageVA.getGroupList() != null) {
			StorageGroupList groupList = storageVA.getGroupList();

			for (Group group : groupList)
				group.setSelectionType(SelectionType.NORMAL);
		}
		if (contentVA.getGroupList() != null) {
			ContentGroupList groupList = contentVA.getGroupList();

			for (Group group : groupList)
				group.setSelectionType(SelectionType.NORMAL);
		}
	}

	@SuppressWarnings("unused")
	private void activateGroupHandling() {

		if (contentVA.getGroupList() == null) {
			ContentGroupList groupList = new ContentGroupList();
			Group group = new Group(contentVA.size(), false, 0, SelectionType.NORMAL);
			groupList.append(group);
			contentVA.setGroupList(groupList);
		}

		if (storageVA.getGroupList() == null) {
			StorageGroupList groupList = new StorageGroupList();
			Group group = new Group(storageVA.size(), false, 0, SelectionType.NORMAL);
			groupList.append(group);
			storageVA.setGroupList(groupList);
		}

		setDisplayListDirty();

	}

	private void handleWiiInput() {
		// float fHeadPositionX =
		// generalManager.getWiiRemote().getCurrentSmoothHeadPosition()[0];
		//
		// if (fHeadPositionX < -1.2f) {
		// bIsHeatmapInFocus = false;
		// }
		// else {
		// bIsHeatmapInFocus = true;
		// }

		setDisplayListDirty();
	}

	private void handleTrackInput(final GL gl) {

		// // TODO: very performance intensive - better solution needed (only in
		// reshape)!
		// getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new
		// Runnable() {
		// @Override
		// public void run() {
		// upperLeftScreenPos =
		// getParentGLCanvas().getParentComposite().toDisplay(1, 1);
		// }
		// });
		//
		// Rectangle screenRect = getParentGLCanvas().getBounds();
		// float[] fArTrackPos =
		// generalManager.getTrackDataProvider().getEyeTrackData();
		//
		// fArTrackPos[0] -= upperLeftScreenPos.x;
		// fArTrackPos[1] -= upperLeftScreenPos.y;
		//
		// GLHelperFunctions.drawPointAt(gl, new Vec3f(fArTrackPos[0] /
		// screenRect.width * 8f,
		// (1f - fArTrackPos[1] / screenRect.height) * 8f * fAspectRatio,
		// 0.01f));
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
		// int iNrPixelsPerSelectionBar = (int) (screenRect.getHeight() /
		// iNrSelBar);
		// iSelectorBar = (int) ((fArTrackPos[1] + 17) /
		// iNrPixelsPerSelectionBar * 1.1f); // TODO: play with
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
		// if (!bIsHeatmapInFocus && fArTrackPos[0] > 50 && fArTrackPos[0] <
		// 450) {
		// float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		// float fStep = fTextureHeight / (iAlNumberSamples.get(iSelectorBar -
		// 1));// * 2);
		// float fYPosMouse =
		// ((1f - fArTrackPos[1] / (float) screenRect.getHeight())) * 8f *
		// fAspectRatio - 0.4f;
		//
		// int iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) /
		// fStep);
		// if (iSamplesPerHeatmap % 2 == 0) {
		// if ((iselElement - (int) Math.floor(iSamplesPerHeatmap / 2) + 1) >= 0
		// && (iselElement + (int) Math.floor(iSamplesPerHeatmap / 2)) <
		// iAlNumberSamples
		// .get(iSelectorBar - 1)) {
		// iFirstSampleLevel2 = iselElement - (int)
		// Math.floor(iSamplesPerHeatmap / 2) + 1;
		// fPosCursorFirstElementLevel3 = fTextureHeight - (iFirstSampleLevel2 *
		// fStep);
		// iLastSampleLevel2 = iselElement + (int) Math.floor(iSamplesPerHeatmap
		// / 2);
		// fPosCursorLastElementLevel3 = fTextureHeight - ((iLastSampleLevel2 +
		// 1) * fStep);
		// }
		// }
		// else {
		// if ((iselElement - (int) Math.ceil(iSamplesPerHeatmap / 2)) >= 0
		// && (iselElement + (int) Math.floor(iSamplesPerHeatmap / 2)) <
		// iAlNumberSamples
		// .get(iSelectorBar - 1)) {
		// iFirstSampleLevel2 = iselElement - (int) Math.ceil(iSamplesPerHeatmap
		// / 2);
		// fPosCursorFirstElementLevel3 = fTextureHeight - (iFirstSampleLevel2 *
		// fStep);
		// iLastSampleLevel2 = iselElement + (int) Math.floor(iSamplesPerHeatmap
		// / 2);
		// fPosCursorLastElementLevel3 = fTextureHeight - ((iLastSampleLevel2 +
		// 1) * fStep);
		// }
		// }
		// }
		//
		// setDisplayListDirty();
	}

	@Override
	public void initData() {

		super.initData();

		initHierarchy();
		calculateTextures();

		initPosCursorLevel1();

		if (bSkipLevel2 == false) {
			initPosCursorLevel2();
		}

		if (set.getContentData(contentVAType).getContentTree() != null) {
			bGeneDendrogramActive = true;
			bGeneDendrogramRenderCut = false;
			bFirstStartGeneDendrogram = true;
			renderStyle.setWidthGeneDendrogram(1.6f);
		} else {
			bGeneDendrogramActive = false;
			bGeneDendrogramRenderCut = false;
		}

		if (!set.getStorageData(storageVAType).isDefaultTree()) {
			bExperimentDendrogramActive = true;
			bExperimentDendrogramRenderCut = false;
			bFirstStartExperimentDendrogram = true;
			renderStyle.setHeightExperimentDendrogram(1.45f);
		} else {
			bExperimentDendrogramActive = false;
			bExperimentDendrogramRenderCut = false;
		}

		if (bSkipLevel2 == false)
			bRedrawTextures = true;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHierarchicalHeatMapView serializedForm = new SerializedHierarchicalHeatMapView(
				dataDomain.getDataDomainType());
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleInterchangeContentGroups() {
		Tree<ClusterNode> tree = null;

		ContentVirtualArray va = contentVA;
		tree = set.getContentData(contentVAType).getContentTree();
		ContentGroupList groupList = va.getGroupList();

		ArrayList<Integer> selGroups = new ArrayList<Integer>();

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		for (Group iter : groupList) {
			if (iter.getSelectionType() == SelectionType.SELECTION)
				selGroups.add(groupList.indexOf(iter));
		}

		if (tree != null) {
			warn();
			bGeneDendrogramActive = false;
			bGeneDendrogramRenderCut = false;
			set.getContentData(contentVAType).setContentTree(null);

		}

		// interchange
		if (groupList.interchange(va, selGroups.get(0), selGroups.get(1)) == false) {
			System.out.println("Problem during interchange!!!");
			return;
		}

		bRedrawTextures = true;

		setDisplayListDirty();

	}

	private void warn() {
		GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = new Shell();
				MessageBox messageBox = new MessageBox(shell, SWT.CANCEL);
				messageBox.setText("Warning");
				messageBox
						.setMessage("Modifications break tree structure, therefore dendrogram will be closed!");
				messageBox.open();
			}
		});
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		contentGroupExportingListener = new ContentGroupExportingListener();
		contentGroupExportingListener.setHandler(this);
		eventPublisher.addListener(ExportContentGroupsEvent.class,
				contentGroupExportingListener);

		storageGroupExportingListener = new StorageGroupExportingListener();
		storageGroupExportingListener.setHandler(this);
		eventPublisher.addListener(ExportStorageGroupsEvent.class,
				storageGroupExportingListener);

		contentGroupInterchangingListener = new ContentGroupInterChangingActionListener();
		contentGroupInterchangingListener.setHandler(this);
		eventPublisher.addListener(InterchangeContentGroupsEvent.class,
				contentGroupInterchangingListener);

		storageGroupInterchangingListener = new StorageGroupInterChangingActionListener();
		storageGroupInterchangingListener.setHandler(this);
		eventPublisher.addListener(InterchangeStorageGroupsEvent.class,
				storageGroupInterchangingListener);

		contentGroupMergingListener = new ContentGroupMergingActionListener();
		contentGroupMergingListener.setHandler(this);
		eventPublisher.addListener(MergeContentGroupsEvent.class,
				contentGroupMergingListener);

		storageGroupMergingListener = new StorageGroupMergingActionListener();
		storageGroupMergingListener.setHandler(this);
		eventPublisher.addListener(MergeStorageGroupsEvent.class,
				storageGroupMergingListener);

		updateViewListener = new UpdateViewListener();
		updateViewListener.setHandler(this);
		eventPublisher.addListener(UpdateViewEvent.class, updateViewListener);

		clusterNodeMouseOverListener = new ClusterNodeSelectionListener();
		clusterNodeMouseOverListener.setHandler(this);
		eventPublisher.addListener(ClusterNodeSelectionEvent.class,
				clusterNodeMouseOverListener);

		newGroupInfoActionListener = new NewContentGroupInfoActionListener();
		newGroupInfoActionListener.setHandler(this);
		eventPublisher.addListener(NewContentGroupInfoEvent.class,
				newGroupInfoActionListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (contentGroupExportingListener != null) {
			eventPublisher.removeListener(contentGroupExportingListener);
			contentGroupExportingListener = null;
		}
		if (storageGroupExportingListener != null) {
			eventPublisher.removeListener(storageGroupExportingListener);
			storageGroupExportingListener = null;
		}
		if (contentGroupInterchangingListener != null) {
			eventPublisher.removeListener(contentGroupInterchangingListener);
			contentGroupInterchangingListener = null;
		}
		if (storageGroupInterchangingListener != null) {
			eventPublisher.removeListener(storageGroupInterchangingListener);
			storageGroupInterchangingListener = null;
		}
		if (contentGroupMergingListener != null) {
			eventPublisher.removeListener(contentGroupMergingListener);
			contentGroupMergingListener = null;
		}
		if (storageGroupMergingListener != null) {
			eventPublisher.removeListener(storageGroupMergingListener);
			storageGroupMergingListener = null;
		}
		if (updateViewListener != null) {
			eventPublisher.removeListener(updateViewListener);
			updateViewListener = null;
		}
		if (clusterNodeMouseOverListener != null) {
			eventPublisher.removeListener(clusterNodeMouseOverListener);
			clusterNodeMouseOverListener = null;
		}
		if (newGroupInfoActionListener != null) {
			eventPublisher.removeListener(newGroupInfoActionListener);
			newGroupInfoActionListener = null;
		}

	}

	@Override
	public void handleUpdateView() {
		bRedrawTextures = true;
		bFirstStartExperimentDendrogram = true;
		bFirstStartGeneDendrogram = true;

		// TODO: maybe causes side effects
		// initData();

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
				setDisplayListDirty();
			} else if (iFirstSampleLevel2 > 0) {
				iLastSampleLevel2--;
				iFirstSampleLevel2--;
				setDisplayListDirty();
			}
		} else {
			if (iLastSampleLevel2 < iSamplesLevel2 - 1 - iNrElementsToShift) {
				iLastSampleLevel2 += iNrElementsToShift;
				iFirstSampleLevel2 += iNrElementsToShift;
				setDisplayListDirty();
			} else if (iLastSampleLevel2 < iSamplesLevel2 - 1) {
				iLastSampleLevel2++;
				iFirstSampleLevel2++;
				setDisplayListDirty();
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
			} else if (iFirstSampleLevel1 > 0) {
				iFirstSampleLevel1--;
				iLastSampleLevel1--;
				setDisplayListDirty();
			}
		} else {
			if (iLastSampleLevel1 < iNumberOfElements - 1 - iNrElementsToShift) {
				iFirstSampleLevel1 += iNrElementsToShift;
				iLastSampleLevel1 += iNrElementsToShift;
				setDisplayListDirty();
			} else if (iLastSampleLevel1 < iNumberOfElements - 1) {
				iFirstSampleLevel1++;
				iLastSampleLevel1++;
				setDisplayListDirty();
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
			if (set.getContentData(contentVAType).getContentTree() == null)
				return;
			bGeneDendrogramActive = bGeneDendrogramActive == true ? false : true;

			if (bGeneDendrogramActive == true) {
				float widthDendro = glContentDendrogramView.getViewFrustum().getWidth();

				if (widthDendro > 0.5f && widthDendro <= 1.7f)
					renderStyle.setWidthGeneDendrogram(widthDendro - 0.1f);

				bGeneDendrogramRenderCut = false;
			} else {
				float temp = glContentDendrogramView.getPositionOfCut();
				renderStyle.setWidthGeneDendrogram(temp);
				bGeneDendrogramRenderCut = true;
			}

			glExperimentDendrogramView.setRedrawDendrogram();

			glContentDendrogramView.setRenderUntilCut(bGeneDendrogramRenderCut);
			glContentDendrogramView.setRedrawDendrogram();

			glHeatMapView.setDisplayListDirty();

			setDisplayListDirty();
		} else {
			if (set.getStorageData(storageVAType).getStorageTree() == null)
				return;
			bExperimentDendrogramActive = bExperimentDendrogramActive == true ? false
					: true;

			if (bExperimentDendrogramActive == true) {
				float highDendro = glExperimentDendrogramView.getViewFrustum()
						.getHeight();

				if (highDendro > 0.5 && highDendro <= 1.5f)
					renderStyle.setHeightExperimentDendrogram(highDendro);

				bExperimentDendrogramRenderCut = false;

			} else {
				float fPosCut = glExperimentDendrogramView.getPositionOfCut();
				float highDendro = glExperimentDendrogramView.getViewFrustum()
						.getHeight();
				renderStyle.setHeightExperimentDendrogram(highDendro - fPosCut + 0.1f);
				bExperimentDendrogramRenderCut = true;
			}

			glContentDendrogramView.setRedrawDendrogram();

			glExperimentDendrogramView.setRenderUntilCut(bGeneDendrogramRenderCut);
			glExperimentDendrogramView.setRedrawDendrogram();

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
		if (bArrowLeft == true && bIsHeatmapInFocus == false) {
			bIsHeatmapInFocus = true;

		} else if (bArrowLeft == false && bIsHeatmapInFocus == true) {
			bIsHeatmapInFocus = false;
		}

		glExperimentDendrogramView.setRedrawDendrogram();

		updateLevels();
		setDisplayListDirty();
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

	@Override
	public void handleClusterNodeSelection(ClusterNodeSelectionEvent event) {

		// TODO: visualize all elements in selected sub tree

		// SelectionDelta selectionDeltaTree = event.getSelectionDelta();
		//
		// // cluster mouse over events only used for gene trees
		// Tree<ClusterNode> tree = set.getClusteredTreeGenes();
		//
		// if (selectionDeltaTree.getIDType() == EIDType.CLUSTER_NUMBER && tree
		// != null) {
		//
		// Collection<SelectionDeltaItem> deltaItems =
		// selectionDeltaTree.getAllItems();
		//
		// for (SelectionDeltaItem item : deltaItems) {
		// int clusterNr = item.getPrimaryID();
		//
		// ClusterNode currentNode = tree.getNodeByNumber(clusterNr);
		//
		// if (currentNode != null) {
		// ArrayList<Integer> alGenes = new ArrayList<Integer>();
		// alGenes = ClusterHelper.getAl(tree, currentNode);
		//
		// for (Integer geneId : alGenes) {
		//
		// System.out.println(idMappingManager.getID(EIDType.EXPRESSION_INDEX,
		// EIDType.GENE_SYMBOL, geneId.intValue()));
		//
		// }
		// }
		// }
		// }
	}

	@Override
	public void handleNewContentGroupInfo(String vaType, ContentGroupList groupList,
			boolean bDeleteTree) {

		Tree<ClusterNode> tree = null;

		if (vaType.equals(contentVA.getVaType())) {
			contentVA.setGroupList(groupList);
			tree = set.getContentData(contentVAType).getContentTree();
		} else {
			return;
		}

		// if hierarchical clusterer result available, discard tree
		if (bDeleteTree && tree != null) {
			warn();

			bGeneDendrogramActive = false;
			bGeneDendrogramRenderCut = false;
			set.getContentData(contentVAType).setContentTree(null);

		}

		updateLevels();
		setDisplayListDirty();
	}

	@Override
	public List<AGLView> getRemoteRenderedViews() {
		List<AGLView> views = new ArrayList<AGLView>();
		views.add(glHeatMapView);
		return views;
	}

	@Override
	public void handleExportContentGroups() {

		ArrayList<Integer> genesToExport = new ArrayList<Integer>();
		ArrayList<Integer> experimentsToExport = new ArrayList<Integer>();

		ContentGroupList groupList = contentVA.getGroupList();

		int groupCnt = 0;

		for (Group iter : groupList) {
			if (iter.getSelectionType() == SelectionType.SELECTION)
				genesToExport.addAll(contentVA.getGeneIdsOfGroup(groupCnt));
			groupCnt++;
		}

		if (storageVA.getGroupList() != null) {
			StorageGroupList storageGroupList = storageVA.getGroupList();

			groupCnt = 0;
			for (Group iter : storageGroupList) {
				if (iter.getSelectionType() == SelectionType.SELECTION)
					experimentsToExport.addAll(storageVA.getGeneIdsOfGroup(groupCnt));
				groupCnt++;
			}
			if (experimentsToExport.size() == 0)
				experimentsToExport = storageVA.getIndexList();

		} else
			experimentsToExport = storageVA.getIndexList();

		openExportDialog(genesToExport, experimentsToExport);

	}

	@Override
	public void handleExportStorageGroups() {

		ArrayList<Integer> genesToExport = new ArrayList<Integer>();
		ArrayList<Integer> experimentsToExport = new ArrayList<Integer>();

		StorageGroupList groupList = storageVA.getGroupList();

		int groupCnt = 0;

		for (Group iter : groupList) {
			if (iter.getSelectionType() == SelectionType.SELECTION)
				experimentsToExport.addAll(storageVA.getGeneIdsOfGroup(groupCnt));
			groupCnt++;
		}

		if (contentVA.getGroupList() != null) {
			ContentGroupList contentGroupList = contentVA.getGroupList();

			groupCnt = 0;
			for (Group iter : contentGroupList) {
				if (iter.getSelectionType() == SelectionType.SELECTION)
					genesToExport.addAll(contentVA.getGeneIdsOfGroup(groupCnt));
				groupCnt++;
			}
			if (genesToExport.size() == 0)
				genesToExport = contentVA.getIndexList();

		} else
			genesToExport = contentVA.getIndexList();

		openExportDialog(genesToExport, experimentsToExport);

	}

	private void openExportDialog(final ArrayList<Integer> genesToExport,
			final ArrayList<Integer> experimentsToExport) {

		getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				ExportDataDialog exportDialog = new ExportDataDialog(new Shell());

				exportDialog.addGroupData(genesToExport, experimentsToExport);
				exportDialog.open();
				return;
			}
		});
	}

	@Override
	public void handleInterchangeStorageGroups() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMergeContentGroups() {
		ContentVirtualArray va;
		Tree<ClusterNode> tree = null;

		va = contentVA;
		tree = set.getContentData(contentVAType).getContentTree();

		ContentGroupList groupList = va.getGroupList();

		ArrayList<Integer> selGroups = new ArrayList<Integer>();

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		for (Group iter : groupList) {
			if (iter.getSelectionType() == SelectionType.SELECTION)
				selGroups.add(groupList.indexOf(iter));
		}

		if (tree != null) {

			warn();

			bGeneDendrogramActive = false;
			bGeneDendrogramRenderCut = false;
			set.getContentData(contentVAType).setContentTree(null);

		}

		// merge
		while (selGroups.size() >= 2) {

			int iLastSelected = selGroups.size() - 1;

			// merge last and the one before last
			if (groupList.merge(va, selGroups.get(iLastSelected - 1),
					selGroups.get(iLastSelected)) == false) {
				System.out.println("Problem during merge!!!");
				return;
			}
			selGroups.remove(iLastSelected);
		}

		bRedrawTextures = true;

		setDisplayListDirty();
	}

	@Override
	public void handleMergeStorageGroups() {
		StorageVirtualArray va;
		Tree<ClusterNode> tree = null;

		va = storageVA;
		tree = set.getStorageData(storageVAType).getStorageTree();

		StorageGroupList groupList = va.getGroupList();

		ArrayList<Integer> selGroups = new ArrayList<Integer>();

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		for (Group iter : groupList) {
			if (iter.getSelectionType() == SelectionType.SELECTION)
				selGroups.add(groupList.indexOf(iter));
		}

		if (tree != null) {

			warn();

			bExperimentDendrogramActive = false;
			bExperimentDendrogramRenderCut = false;
			set.getStorageData(storageVAType).setStorageTree(null);
		}

		// merge
		while (selGroups.size() >= 2) {

			int iLastSelected = selGroups.size() - 1;

			// merge last and the one before last
			if (groupList.merge(va, selGroups.get(iLastSelected - 1),
					selGroups.get(iLastSelected)) == false) {
				System.out.println("Problem during merge!!!");
				return;
			}
			selGroups.remove(iLastSelected);
		}

		bRedrawTextures = true;

		setDisplayListDirty();
	}

}
