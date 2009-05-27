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
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.GroupList;
import org.caleydo.core.data.selection.IGroupList;
import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.IVirtualArrayDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.VADeltaItem;
import org.caleydo.core.data.selection.delta.VirtualArrayDelta;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.event.view.group.InterchangeGroupsEvent;
import org.caleydo.core.manager.event.view.group.MergeGroupsEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.core.util.clusterer.EClustererType;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.util.preferences.PreferenceConstants;
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
import org.caleydo.core.view.opengl.canvas.storagebased.listener.StartClusteringListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.container.GroupContextMenuItemContainer;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedDummyView;

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
	private final static float GAP_LEVEL2_3 = 0.4f;

	// private final static float MAX_NUM_SAMPLES = 8f;

	private final static int MIN_SAMPLES_PER_HEATMAP = 14;
	private final static int MAX_SAMPLES_PER_HEATMAP = 100;

	private int iNumberOfElements = 0;

	private int iSamplesPerTexture = 0;

	private int iSamplesPerHeatmap = 0;

	private ColorMapping colorMapper;

	private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;
	private EIDType eExperimentDataType = EIDType.EXPERIMENT_INDEX;

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
	private boolean bIsHeatmapInFocus = false;
	private float fWidthEHM = 0;

	// embedded dendrogram
	// private GLDendrogram glDendrogram;

	private boolean bRedrawTextures = false;

	// if only a small number of genes is in the data set, level_1 (overViewBar) should not be rendered
	private boolean bSkipLevel1 = false;

	// dragging stuff
	private boolean bIsDraggingActive = false;
	private boolean bIsDraggingWholeBlock = false;
	private boolean bDisableCursorDragging = false;
	private boolean bDisableBlockDragging = false;
	private int iDraggedCursor = 0;
	private float fPosCursorFirstElement = 0;
	private float fPosCursorLastElement = 0;

	// clustering/grouping stuff
	private ClusterState clusterstate = new ClusterState();

	private boolean bSplitGroupExp = false;
	private boolean bSplitGroupGene = false;
	private int iGroupToSplit = 0;
	private Point DraggingPoint = null;

	private GroupMergingActionListener groupMergingActionListener;
	private GroupInterChangingActionListener groupInterChangingActionListener;
	private UpdateViewListener updateViewListener;
	private StartClusteringListener startClusteringListener;

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

		contentSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX).build();
		storageSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();

		colorMapper = ColorMappingManager.get().getColorMapping(EColorMappingType.GENE_EXPRESSION);

		fAlXDistances = new ArrayList<Float>();

		// activate clustering
		bUseClusteredVA = false;

		glKeyListener = new GLHierarchicalHeatMapKeyListener(this);

		createHeatMap();
	}

	/**
	 * Function used in keyListener to forward upDownselection to EHM
	 * 
	 * @return embedded heat map
	 */
	public GLHeatMap getEmbeddedHeatMap() {
		return glHeatMapView;
	}

	@Override
	public void init(GL gl) {
		glHeatMapView.initRemote(gl, this, glMouseListener, null, null);
		// glDendrogram.initRemote(gl, this, glMouseListener, null, null);

		initTextures(gl);
		// activateGroupHandling();
	}

	/**
	 * Function responsible for initialization of hierarchy levels. Depending on the amount of samples in the
	 * data set 2 or 3 levels are used.
	 */
	private void initHierarchy() {

		if (set == null)
			return;

		// createDendrogram();

		iNumberOfElements = set.getVA(iContentVAID).size();

		if (iNumberOfElements < MIN_SAMPLES_PER_HEATMAP) {
			System.out.println("Number of elements not supported!! Problems with visualization may occur!");
			// throw new IllegalStateException("Number of elements not supported!!");
		}

		if (iNumberOfElements < 100) {
			bSkipLevel1 = true;
			iSelectorBar = 1;

			iSamplesPerTexture = iNumberOfElements;
			iSamplesPerHeatmap = (int) Math.floor(iSamplesPerTexture / 3);

		}
		else {
			bSkipLevel1 = false;
			iSelectorBar = 1;
			iSamplesPerTexture = (int) Math.floor(iNumberOfElements / 5);

			if (iSamplesPerTexture > 250)
				iSamplesPerTexture = 250;

			iSamplesPerHeatmap = (int) Math.floor(iSamplesPerTexture / 3);
		}
		if (iSamplesPerHeatmap > MAX_SAMPLES_PER_HEATMAP)
			iSamplesPerTexture = 100;

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
	 * If no selected elements are in the current texture, the function switches the texture
	 */
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

		// if current textures contains little number of elements, set samples per textures to a small value
		if (iSamplesPerHeatmap > iAlNumberSamples.get(iSelectorBar - 1) / 2)
			iSamplesPerHeatmap = (int) Math.floor(iAlNumberSamples.get(iSelectorBar - 1) / 3);
		// if previous texture contained little number of elements, set samples per textures to
		// MIN_SAMPLES_PER_HEATMAP
		else if (iSamplesPerHeatmap < MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = MIN_SAMPLES_PER_HEATMAP;

		if (AlSelection.size() > 0) {
			int iNumberSample = iAlNumberSamples.get(iSelectorBar - 1);
			// int iNumberSample = iNrSamplesPerTexture; // * 2;

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
				// else if (iter.getTexture() == iSelectorBar) {
				// iPickedSample = iter.getPos() + iAlNumberSamples.get(iSelectorBar - 1);
				//
				// if (iSamplesPerHeatmap % 2 == 0) {
				// iFirstSample = iPickedSample - (int) Math.floor(iSamplesPerHeatmap / 2) + 1;
				// iLastSample = iPickedSample + (int) Math.floor(iSamplesPerHeatmap / 2);
				// }
				// else {
				// iFirstSample = iPickedSample - (int) Math.ceil(iSamplesPerHeatmap / 2);
				// iLastSample = iPickedSample + (int) Math.floor(iSamplesPerHeatmap / 2);
				// }
				//
				// if (iPickedSample < iSamplesPerHeatmap / 2) {
				// iPickedSample = (int) Math.floor(iSamplesPerHeatmap / 2);
				// iFirstSample = 0;
				// iLastSample = iSamplesPerHeatmap - 1;
				// }
				// else if (iPickedSample > iNumberSample - 1 - iSamplesPerHeatmap / 2) {
				// iPickedSample = (int) Math.ceil(iNumberSample - iSamplesPerHeatmap / 2);
				// iLastSample = iNumberSample - 1;
				// iFirstSample = iNumberSample - iSamplesPerHeatmap;
				// }
				// break;
				// }
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

	private void calculateTextures() {

		// less than 100 elements in VA, level 1 (overview bar) will not be rendered
		if (bSkipLevel1) {

			iNrSelBar = 1;

			AlTextures.clear();
			iAlNumberSamples.clear();

			Texture tempTextur = null;

			AlTextures.add(tempTextur);
			iAlNumberSamples.add(iSamplesPerTexture);

		}
		else {
			if (set.getVA(iContentVAID).getGroupList() != null) {

				IGroupList groupList = set.getVA(iContentVAID).getGroupList();
				iNrSelBar = groupList.size();

				AlTextures.clear();
				iAlNumberSamples.clear();

				Texture tempTextur = null;

				for (int i = 0; i < iNrSelBar; i++) {

					AlTextures.add(tempTextur);
					iAlNumberSamples.add(groupList.get(i).getNrElements());
				}
			}

			else {

				iNrSelBar = (int) Math.ceil(set.getVA(iContentVAID).size() / iSamplesPerTexture);

				AlTextures.clear();
				iAlNumberSamples.clear();

				Texture tempTextur = null;

				int iTextureHeight = set.getVA(iContentVAID).size();

				iNrSamplesPerTexture = (int) Math.floor(iTextureHeight / iNrSelBar);

				for (int i = 0; i < iNrSelBar; i++) {

					AlTextures.add(tempTextur);
					iAlNumberSamples.add(iNrSamplesPerTexture);
				}
			}
		}
	}

	/**
	 * Init textures, build array of textures used for holding the whole examples from contentSelectionManager
	 * 
	 * @param
	 */
	private void initTextures(final GL gl) {
		fAlXDistances.clear();

		if (bSkipLevel1) {

			AlTextures.clear();
			iAlNumberSamples.clear();

			Texture tempTextur;

			int iTextureHeight = set.getVA(iContentVAID).size();
			int iTextureWidth = set.getVA(iStorageVAID).size();

			float fLookupValue = 0;
			float fOpacity = 0;

			FloatBuffer FbTemp = BufferUtil.newFloatBuffer(iTextureWidth * iTextureHeight * 4);

			for (Integer iContentIndex : set.getVA(iContentVAID)) {
				IVirtualArray storageVA = set.getVA(iStorageVAID);
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

			if (set.getVA(iContentVAID).getGroupList() != null) {

				IGroupList groupList = set.getVA(iContentVAID).getGroupList();

				iNrSelBar = groupList.size();
				AlTextures.clear();
				iAlNumberSamples.clear();
				Texture tempTextur;
				// int iTextureHeight = set.getVA(iContentVAID).size();
				int iTextureWidth = set.getVA(iStorageVAID).size();

				float fLookupValue = 0;
				float fOpacity = 0;

				int iCount = 0;
				int iTextureCounter = 0;
				int iGroupNr = 0;

				FloatBuffer FbTemp =
					BufferUtil.newFloatBuffer(groupList.get(iGroupNr).getNrElements() * iTextureWidth * 4);

				for (Integer iContentIndex : set.getVA(iContentVAID)) {
					iCount++;
					IVirtualArray storageVA = set.getVA(iStorageVAID);
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
					if (iCount >= groupList.get(iGroupNr).getNrElements()) {
						FbTemp.rewind();

						TextureData texData =
							new TextureData(GL.GL_RGBA /* internalFormat */,
								set.getVA(iStorageVAID).size() /* height */,
								groupList.get(iGroupNr).getNrElements() /* width */,
								// set.getVA(iContentVAID).size()/ iNrSelBar /* width */,
								0 /* border */, GL.GL_RGBA /* pixelFormat */, GL.GL_FLOAT /* pixelType */,
								false /* mipmap */, false /* dataIsCompressed */, false /* mustFlipVertically */,
								FbTemp, null);

						tempTextur = TextureIO.newTexture(0);
						tempTextur.updateImage(texData);

						AlTextures.add(tempTextur);
						iAlNumberSamples.add(groupList.get(iGroupNr).getNrElements());
						if (iGroupNr < iNrSelBar - 1) {
							iGroupNr++;
							FbTemp =
								BufferUtil.newFloatBuffer(groupList.get(iGroupNr).getNrElements()
									* iTextureWidth * 4);
						}
						iTextureCounter++;
						iCount = 0;
					}
				}
			}
			else {
				iNrSelBar = (int) Math.ceil(set.getVA(iContentVAID).size() / iSamplesPerTexture);

				AlTextures.clear();
				iAlNumberSamples.clear();

				Texture tempTextur;

				int iTextureHeight = set.getVA(iContentVAID).size();
				int iTextureWidth = set.getVA(iStorageVAID).size();

				iNrSamplesPerTexture = (int) Math.floor(iTextureHeight / iNrSelBar);

				float fLookupValue = 0;
				float fOpacity = 0;

				FloatBuffer FbTemp =
					BufferUtil.newFloatBuffer(iTextureWidth * iTextureHeight * 4 / iNrSelBar);

				int iCount = 0;
				int iTextureCounter = 0;

				for (Integer iContentIndex : set.getVA(iContentVAID)) {
					iCount++;
					IVirtualArray storageVA = set.getVA(iStorageVAID);
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
					if (iCount >= iNrSamplesPerTexture) {
						FbTemp.rewind();

						TextureData texData =
							new TextureData(GL.GL_RGBA /* internalFormat */,
								set.getVA(iStorageVAID).size() /* height */, set.getVA(iContentVAID).size()
									/ iNrSelBar /* width */, 0 /* border */, GL.GL_RGBA /* pixelFormat */,
								GL.GL_FLOAT /* pixelType */, false /* mipmap */,
								false /* dataIsCompressed */, false /* mustFlipVertically */, FbTemp, null);

						tempTextur = TextureIO.newTexture(0);
						tempTextur.updateImage(texData);

						AlTextures.add(tempTextur);
						iAlNumberSamples.add(iCount);

						iTextureCounter++;
						iCount = 0;
					}
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
		GeneralManager.get().getUseCase().addView(glHeatMapView);
		glHeatMapView.setUseCase(useCase);
		glHeatMapView.setRenderedRemote(true);
	}

	// private void createDendrogram() {
	// CmdCreateGLEventListener cmdView =
	// (CmdCreateGLEventListener) generalManager.getCommandManager().createCommandByType(
	// ECommandType.CREATE_GL_DENDROGRAM_VERTICAL);
	//
	// float fHeatMapHeight = viewFrustum.getHeight();
	// float fHeatMapWidth = viewFrustum.getWidth();
	//
	// cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, fHeatMapHeight, 0, fHeatMapWidth, -20, 20,
	// set, -1);
	//
	// cmdView.doCommand();
	//
	// glDendrogram = (GLDendrogram) cmdView.getCreatedObject();
	// GeneralManager.get().getUseCase().addView(glDendrogram);
	// glDendrogram.setUseCase(useCase);
	// glDendrogram.setRenderedRemote(true);
	// glDendrogram.initData();
	// }

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
		int iIndex = 0;
		int iTemp = 0;
		int iTexture = 0;
		int iPos = 0;
		HeatMapSelection temp;

		AlSelection.clear();

		Set<Integer> setMouseOverElements = contentSelectionManager.getElements(ESelectionType.MOUSE_OVER);

		for (Integer iSelectedID : setMouseOverElements) {
			iIndex = set.getVA(iContentVAID).indexOf(iSelectedID.intValue()) + 1;

			iTemp = iIndex;

			if (iIndex - iAlNumberSamples.get(0) <= 0) {
				iTexture = 0;
				iPos = iIndex;
			}
			else {
				for (int i = 0; i < iNrSelBar; i++) {

					if (iTemp - iAlNumberSamples.get(i) <= 0) {
						iTexture = i;
						iPos = iTemp;
						break;
					}
					iTemp -= iAlNumberSamples.get(i);
				}
			}

			temp = new HeatMapSelection(iTexture, iPos, iSelectedID.intValue(), ESelectionType.MOUSE_OVER);
			AlSelection.add(temp);
		}

		Set<Integer> setSelectionElements = contentSelectionManager.getElements(ESelectionType.SELECTION);

		for (Integer iSelectedID : setSelectionElements) {
			iIndex = set.getVA(iContentVAID).indexOf(iSelectedID.intValue()) + 1;

			iTemp = iIndex;

			if (iIndex - iAlNumberSamples.get(0) <= 0) {
				iTexture = 0;
				iPos = iIndex;
			}
			else {
				for (int i = 0; i < iNrSelBar; i++) {

					if ((iTemp - iAlNumberSamples.get(i)) <= 0) {
						iTexture = i;
						iPos = iTemp;
						break;
					}
					iTemp -= iAlNumberSamples.get(i);
				}
			}

			temp = new HeatMapSelection(iTexture, iPos, iSelectedID.intValue(), ESelectionType.SELECTION);
			AlSelection.add(temp);
		}

		Set<Integer> setDeselctedElements = contentSelectionManager.getElements(ESelectionType.DESELECTED);
		for (Integer iSelectedID : setDeselctedElements) {
			iIndex = set.getVA(iContentVAID).indexOf(iSelectedID.intValue()) + 1;

			iTemp = iIndex;

			if (iIndex - iAlNumberSamples.get(0) <= 0) {
				iTexture = 0;
				iPos = iIndex;
			}
			else {
				for (int i = 0; i < iNrSelBar; i++) {

					if ((iTemp - iAlNumberSamples.get(i)) <= 0) {
						iTexture = i;
						iPos = iTemp;
						break;
					}
					iTemp -= iAlNumberSamples.get(i);
				}
			}

			temp = new HeatMapSelection(iTexture, iPos, iSelectedID.intValue(), ESelectionType.DESELECTED);
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

		if (bSkipLevel1 == false) {
			if (scrollToSelection) {
				setTexture();
			}
		}
	}

	@Override
	protected void reactOnVAChanges(IVirtualArrayDelta delta) {
		glHeatMapView.handleVirtualArrayUpdate(delta, getShortInfo());
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

		float fHeightElem = fHeight / set.getVA(iContentVAID).size();

		float fStep = 0;

		gl.glColor4f(1f, 1f, 0f, 1f);

		for (int i = 0; i < iNrSelBar; i++) {

			fStep = fHeightElem * iAlNumberSamples.get(iNrSelBar - i - 1);

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

	private void renderClassAssignmentsExperimentsLevel3(final GL gl) {

		float fWidth = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		int iNrElements = set.getVA(iStorageVAID).size();
		float fWidthSamples = fWidthEHM / iNrElements;
		float fxpos = fWidth + GAP_LEVEL2_3;
		float fHeight = viewFrustum.getHeight() + 0.1f;

		IGroupList groupList = set.getVA(iStorageVAID).getGroupList();

		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			// gl.glPushName(pickingManager.getPickingID(iUniqueID,
			// EPickingType.HIER_HEAT_MAP_EXPERIMENTS_GROUP, i));

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

			// gl.glPopName();

			fxpos = fxpos + classWidth;
		}

	}

	private void renderClassAssignmentsExperimentsLevel2(final GL gl) {

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
		float fFieldWith = 0.1f;
		Vec3f startpoint1, endpoint1, startpoint2, endpoint2;

		float fHeightElem = fHeight / set.getVA(iContentVAID).size();

		int iStartElem = 0;
		int iLastElem = 0;

		boolean colorToggle = true;

		gl.glLineWidth(2f);

		for (int currentGroup = 0; currentGroup < iNrSelBar; currentGroup++) {

			iStartElem = iLastElem;
			iLastElem += iAlNumberSamples.get(currentGroup);

			if (colorToggle)
				gl.glColor4f(0f, 0f, 0f, 1f);
			else
				gl.glColor4f(1f, 1f, 1f, 1f);

			colorToggle = (colorToggle == true) ? false : true;

			if (currentGroup == iSelectorBar - 1) {
				startpoint1 = new Vec3f(fFieldWith, fHeight - fHeightElem * iStartElem, 0);
				endpoint1 = new Vec3f(GAP_LEVEL1_2, fHeight, 0);
				startpoint2 = new Vec3f(fFieldWith, fHeight - fHeightElem * iLastElem, 0);
				endpoint2 = new Vec3f(GAP_LEVEL1_2, 0, 0);
				renderSelectedDomain(gl, startpoint1, endpoint1, startpoint2, endpoint2);

				gl.glColor4fv(MOUSE_OVER_COLOR, 0);
			}

			// TODO: find a better way to render cluster assignments (--> +0.01f is not a fine way)
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0, fHeight - fHeightElem * iStartElem, 0);
			gl.glVertex3f(fFieldWith, fHeight - fHeightElem * iStartElem, 0);
			gl.glVertex3f(fFieldWith, (fHeight - fHeightElem * iLastElem) + 0.01f, 0);
			gl.glVertex3f(0, (fHeight - fHeightElem * iLastElem) + 0.01f, 0);
			gl.glEnd();
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

		float fHeightElem = fHeight / set.getVA(iContentVAID).size();

		for (HeatMapSelection selection : AlSelection) {
			if (selection.getSelectionType() == ESelectionType.MOUSE_OVER) {
				gl.glColor4fv(MOUSE_OVER_COLOR, 0);
			}
			else if (selection.getSelectionType() == ESelectionType.SELECTION) {
				gl.glColor4fv(SELECTED_COLOR, 0);
			}
			// else if (selection.getSelectionType() == ESelectionType.DESELECTED) {
			// gl.glColor4f(1, 1, 1, 0.5f);
			// }
			else
				continue;

			float fStartElem = 0;

			for (int i = 0; i < selection.getTexture(); i++)
				fStartElem += iAlNumberSamples.get(i);

			// elements in overview bar
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(fBarWidth, fHeight - fHeightElem * fStartElem, 0.001f);
			gl.glVertex3f(fBarWidth + 0.1f, fHeight - fHeightElem * fStartElem, 0.001f);
			gl.glVertex3f(fBarWidth + 0.1f, fHeight - fHeightElem
				* (fStartElem + iAlNumberSamples.get(selection.getTexture())), 0.001f);
			gl.glVertex3f(fBarWidth, fHeight - fHeightElem
				* (fStartElem + iAlNumberSamples.get(selection.getTexture())), 0.001f);
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
		// gl.glVertex3f(0, fHeight / 2, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glTexCoord2d(texCoords1.left(), texCoords1.bottom());
		gl.glVertex3f(0, fHeight, 0);
		gl.glTexCoord2d(texCoords1.right(), texCoords1.bottom());
		gl.glVertex3f(fWidth, fHeight, 0);
		gl.glTexCoord2d(texCoords1.right(), texCoords1.top());
		// gl.glVertex3f(fWidth, fHeight / 2, 0);
		gl.glVertex3f(fWidth, 0, 0);
		gl.glEnd();

		// Texture TexTemp2 = AlTextures.get(iSelectorBar);
		// TexTemp2.enable();
		// TexTemp2.bind();
		//
		// TextureCoords texCoords2 = TexTemp2.getImageTexCoords();
		//
		// gl.glBegin(GL.GL_QUADS);
		// gl.glTexCoord2d(texCoords2.left(), texCoords2.top());
		// gl.glVertex3f(0, 0, 0);
		// gl.glTexCoord2d(texCoords2.left(), texCoords2.bottom());
		// gl.glVertex3f(0, fHeight / 2, 0);
		// gl.glTexCoord2d(texCoords2.right(), texCoords2.bottom());
		// gl.glVertex3f(fWidth, fHeight / 2, 0);
		// gl.glTexCoord2d(texCoords2.right(), texCoords2.top());
		// gl.glVertex3f(fWidth, 0, 0);
		// gl.glEnd();

		gl.glPopName();

		gl.glPopAttrib();

		TexTemp1.disable();
		// TexTemp2.disable();
	}

	/**
	 * Render marker in Texture for visualization of the currently (in stage 3) rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerTexture(final GL gl) {
		float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;

		float fHeightSample = viewFrustum.getHeight() / (iAlNumberSamples.get(iSelectorBar - 1));// * 2);

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
				- iPickedSample * fHeightSample, 0.01f);
			bRenderCaption = false;
		}
	}

	/**
	 * Render marker in Texture (level 2) for visualization of selected elements in the data set
	 * 
	 * @param gl
	 */
	private void renderSelectedElementsTexture(GL gl) {
		float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;

		float fHeightSample = viewFrustum.getHeight() / iAlNumberSamples.get(iSelectorBar - 1);
		// float fHeightSample = viewFrustum.getHeight() / (iNrSamplesPerTexture);// * 2);

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
			else if (selection.getSelectionType() == ESelectionType.SELECTION) {
				gl.glColor4fv(SELECTED_COLOR, 0);
			}
			// else if (selection.getSelectionType() == ESelectionType.DESELECTED) {
			// gl.glColor4f(1, 1, 1, 0.5f);
			// }
			else
				continue;

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
			// else if (iSelectorBar + 1 == selection.getTexture() + 1) {
			// gl.glLineWidth(2f);
			// gl.glBegin(GL.GL_LINE_LOOP);
			// gl.glVertex3f(-0.1f, viewFrustum.getHeight()
			// - (selection.getPos() - 1 + iAlNumberSamples.get(iSelectorBar - 1)) * fHeightSample,
			// SELECTION_Z);
			// gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight()
			// - (selection.getPos() - 1 + iAlNumberSamples.get(iSelectorBar - 1)) * fHeightSample,
			// SELECTION_Z);
			// gl.glVertex3f(fFieldWith + 0.1f, viewFrustum.getHeight()
			// - (selection.getPos() + iAlNumberSamples.get(iSelectorBar - 1)) * fHeightSample,
			// SELECTION_Z);
			// gl.glVertex3f(-0.1f, viewFrustum.getHeight()
			// - (selection.getPos() + iAlNumberSamples.get(iSelectorBar - 1)) * fHeightSample,
			// SELECTION_Z);
			// gl.glEnd();
			// }
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

		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_SIDE);
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

			tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE);
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

		tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_SIDE);
		tempTexture.enable();
		tempTexture.bind();

		texCoords = tempTexture.getImageTexCoords();

		// if (iSelectorBar != iNrSelBar - 1) {
		if (iSelectorBar != iNrSelBar) {
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

			tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_BIG_MIDDLE);
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

		tempTexture = textureManager.getIconTexture(gl, EIconTextures.NAVIGATION_NEXT_SMALL);
		tempTexture.enable();
		tempTexture.bind();

		texCoords = tempTexture.getImageTexCoords();

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
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_BLOCK_CURSOR, 1));
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(-GAP_LEVEL1_2 / 4, fPosCursorLastElement, 0);
		gl.glVertex3f(0.0f, fPosCursorLastElement, 0);
		gl.glVertex3f(0.0f, fPosCursorFirstElement, 0);
		gl.glVertex3f(-GAP_LEVEL1_2 / 4, fPosCursorFirstElement, 0);
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

		if (bIsDraggingActive) {
			handleCursorDragging(gl);
			if (glMouseListener.wasMouseReleased()) {
				bIsDraggingActive = false;
			}
		}

		if (bIsDraggingWholeBlock) {
			handleBlockDragging(gl);
			if (glMouseListener.wasMouseReleased()) {
				bIsDraggingWholeBlock = false;
			}
		}

		if (bSplitGroupExp) {
			handleGroupSplitExperiments(gl);
			if (glMouseListener.wasMouseReleased()) {
				bSplitGroupExp = false;
			}
		}

		if (bSplitGroupGene) {
			handleGroupSplitGenes(gl);
			if (glMouseListener.wasMouseReleased()) {
				bSplitGroupExp = false;
			}
		}

		gl.glCallList(iGLDisplayListToCall);

		float fright = 0.0f;
		float ftop = viewFrustum.getTop();

		float fleftOffset = 0;

		if (bSkipLevel1 == false) {
			gl.glTranslatef(GAP_LEVEL2_3, 0, 0);
		}

		// render embedded heat map
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

		glHeatMapView.getViewFrustum().setTop(ftop);
		glHeatMapView.getViewFrustum().setRight(fright);
		gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.HIER_HEAT_MAP_VIEW_SELECTION,
			glHeatMapView.getID()));
		glHeatMapView.displayRemote(gl);
		gl.glPopName();
		fWidthEHM = glHeatMapView.getViewFrustum().getWidth() - 0.95f;

		if (glHeatMapView.isInDefaultOrientation()) {
			gl.glTranslatef(-fleftOffset, -0.4f, 0);
		}
		else {
			gl.glTranslatef(-fleftOffset, +0.2f, 0);
		}

		if (bSkipLevel1 == false) {
			gl.glTranslatef(-GAP_LEVEL2_3, 0, 0);
		}

		// // render embedded dendrogram
		// if (bIsHeatmapInFocus) {
		// fright = viewFrustum.getWidth() - 1.2f;
		// fleftOffset = 0.095f + // width level 1 + boarder
		// GAP_LEVEL1_2 + // width gap between level 1 and 2
		// viewFrustum.getWidth() / 4f * 0.2f;
		// }
		// else {
		// fright = viewFrustum.getWidth() - 2.75f;
		// fleftOffset = 0.075f + // width level 1
		// GAP_LEVEL1_2 + // width gap between level 1 and 2
		// viewFrustum.getWidth() / 4f;
		// }
		// gl.glTranslatef(fleftOffset, 0, 1);
		//				
		// glDendrogram.getViewFrustum().setTop(ftop);
		// glDendrogram.getViewFrustum().setRight(fright);
		// glDendrogram.setDisplayListDirty();
		// glDendrogram.displayRemote(gl);
		//				
		// gl.glTranslatef(-fleftOffset, -0, -1);

		contextMenu.render(gl, this);

	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bRedrawTextures) {
			initTextures(gl);
			bRedrawTextures = false;
		}

		if (bHasFrustumChanged) {
			glHeatMapView.setDisplayListDirty();
			// glDendrogram.setDisplayListDirty();
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

		// all stuff for rendering level 1 (overview bar)
		if (bSkipLevel1 == false) {
			renderOverviewBar(gl);
			renderMarkerOverviewBar(gl);
			renderSelectedElementsOverviewBar(gl);

			gl.glTranslatef(GAP_LEVEL1_2, 0, 0);
		}
		else {
			gl.glColor4f(1f, 1f, 0f, 1f);
			// width of dragging cursor
			gl.glTranslatef(0.2f, 0.0f, 0);
		}

		if (bIsHeatmapInFocus) {
			fAnimationScale = 0.2f;
		}
		else {
			fAnimationScale = 1.0f;
		}

		// all stuff for rendering level 2 (textures)
		gl.glColor4f(1f, 1f, 1f, 1f);
		renderTextureHeatMap(gl);
		renderMarkerTexture(gl);
		renderSelectedElementsTexture(gl);
		renderCursor(gl);

		if (set.getVA(iStorageVAID).getGroupList() != null) {
			renderClassAssignmentsExperimentsLevel2(gl);
			renderClassAssignmentsExperimentsLevel3(gl);
		}

		viewFrustum.setTop(viewFrustum.getTop() + 0.6f);
		viewFrustum.setLeft(viewFrustum.getLeft() - 0.1f);
		gl.glTranslatef(-0.1f, -0.4f, 0);

		if (bSkipLevel1 == false) {
			gl.glTranslatef(-GAP_LEVEL1_2, 0, 0);
		}
		else {
			// width of dragging cursor
			gl.glTranslatef(-0.2f, 0.0f, 0);
		}

		// gl.glDisable(GL.GL_STENCIL_TEST);

		gl.glEndList();
	}

	/**
	 * Function responsible for handling SelectionDelta for embedded heatmap
	 */
	private void triggerSelectionBlock() {
		int iCount = iFirstSample;

		for (int i = 0; i < iSelectorBar - 1; i++)
			iCount += iAlNumberSamples.get(i);

		// SelectionCommand command = new SelectionCommand(ESelectionCommandType.RESET);
		// commands.add(command);
		// glHeatMapView.handleContentTriggerSelectionCommand(eFieldDataType, command);
		glHeatMapView.resetView();
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

		glHeatMapView.handleVirtualArrayUpdate(delta, getShortInfo());
		if (selectionDelta.size() > 0) {
			glHeatMapView.handleSelectionUpdate(selectionDelta, true, null);
		}

		// selected experiments

		SelectionCommand command = new SelectionCommand(ESelectionCommandType.RESET);
		glHeatMapView.handleStorageTriggerSelectionCommand(eExperimentDataType, command);

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

		// In case of importing group info
		if (set.isClusterInfo())
			set.getVA(iContentVAID).setGroupList(set.getGroupList());

		// clustering triggered by StartClusteringAction
		if (bUseClusteredVA) {

			int iContentVAIDtemp = 0, iStorageVAIDtemp = 0;

			if (bRenderOnlyContext) {
				iContentVAIDtemp = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
			}
			else {
				if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION)) {
					initCompleteList();
				}
				iContentVAIDtemp = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
			}
			iStorageVAIDtemp = mapVAIDs.get(EStorageBasedVAType.STORAGE_SELECTION);

			if (clusterstate.getClustererType() == EClustererType.GENE_CLUSTERING) {

				int iVAid = set.cluster(iContentVAIDtemp, iStorageVAIDtemp, clusterstate, 0, 2);
				if (iVAid < 0)
					iContentVAID = iContentVAIDtemp;
				else
					iContentVAID = iVAid;

				iStorageVAID = iStorageVAIDtemp;
			}
			else if (clusterstate.getClustererType() == EClustererType.EXPERIMENTS_CLUSTERING) {

				int iVAid = set.cluster(iContentVAIDtemp, iStorageVAIDtemp, clusterstate, 0, 2);
				if (iVAid < 0)
					iStorageVAID = iStorageVAIDtemp;
				else
					iStorageVAID = iVAid;

				iContentVAID = iContentVAIDtemp;
			}
			else {

				boolean bSkipGeneClustering = false;

				clusterstate.setClustererType(EClustererType.EXPERIMENTS_CLUSTERING);
				int iVAid = set.cluster(iContentVAIDtemp, iStorageVAIDtemp, clusterstate, 0, 1);
				if (iVAid < 0) {
					iStorageVAID = iStorageVAIDtemp;
					iContentVAID = iContentVAIDtemp;
					bSkipGeneClustering = true;
				}
				else
					iStorageVAID = iVAid;

				// in case of user requests abort during experiment clustering do not cluster genes
				if (bSkipGeneClustering == false) {
					clusterstate.setClustererType(EClustererType.GENE_CLUSTERING);
					iVAid = set.cluster(iContentVAIDtemp, iStorageVAID, clusterstate, 50, 1);
					if (iVAid < 0)
						iContentVAID = iContentVAIDtemp;
					else
						iContentVAID = iVAid;
				}
			}

			AlSelection.clear();

		}
		// // normal startup
		// else {
		// if (bRenderOnlyContext) {
		// iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		// }
		// else {
		// if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION)) {
		// initCompleteList();
		// }
		// iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
		// }
		// iStorageVAID = mapVAIDs.get(EStorageBasedVAType.STORAGE_SELECTION);
		//
		// // In case of importing group info
		// if (set.isClusterInfo())
		// set.getVA(iContentVAID).setGroupList(set.getGroupList());
		// }

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
		int iNumberSample = iAlNumberSamples.get(iSelectorBar - 1);
		// int iNumberSample = iNrSamplesPerTexture;// * 2;
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

	/**
	 * Handles the dragging cursor for gene groups
	 * 
	 * @param gl
	 */
	private void handleGroupSplitGenes(final GL gl) {
		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		float[] fArDraggedPoint = new float[3];

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		// gl.glBegin(GL.GL_QUADS);
		// gl.glVertex3f(fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1], 0);
		// gl.glVertex3f(fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1] + 0.1f, 0);
		// gl.glVertex3f(fArTargetWorldCoordinates[0] + 0.1f, fArTargetWorldCoordinates[1] + 0.1f, 0);
		// gl.glVertex3f(fArTargetWorldCoordinates[0] + 0.1f, fArTargetWorldCoordinates[1], 0);
		// gl.glEnd();

		if (glMouseListener.wasMouseReleased()) {
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

	/**
	 * Handles the dragging cursor for experiments groups
	 * 
	 * @param gl
	 */
	private void handleGroupSplitExperiments(final GL gl) {
		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		float[] fArDraggedPoint = new float[3];

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		// gl.glBegin(GL.GL_QUADS);
		// gl.glVertex3f(fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1], 0);
		// gl.glVertex3f(fArTargetWorldCoordinates[0], fArTargetWorldCoordinates[1] + 0.1f, 0);
		// gl.glVertex3f(fArTargetWorldCoordinates[0] + 0.1f, fArTargetWorldCoordinates[1] + 0.1f, 0);
		// gl.glVertex3f(fArTargetWorldCoordinates[0] + 0.1f, fArTargetWorldCoordinates[1], 0);
		// gl.glEnd();

		if (glMouseListener.wasMouseReleased()) {
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
	 * Function used for updating position of block (block of elements rendered in EHM) in case of dragging
	 * 
	 * @param gl
	 */
	private void handleBlockDragging(final GL gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / (iAlNumberSamples.get(iSelectorBar - 1));// * 2);
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
		if (iSamplesPerHeatmap % 2 == 0) {
			if ((iselElement - (int) Math.floor(iSamplesPerHeatmap / 2) + 1) >= 0
				&& (iselElement + (int) Math.floor(iSamplesPerHeatmap / 2)) < iAlNumberSamples
					.get(iSelectorBar - 1)) {
				iFirstSample = iselElement - (int) Math.floor(iSamplesPerHeatmap / 2) + 1;
				fPosCursorFirstElement = fTextureHeight - (iFirstSample * fStep);
				iLastSample = iselElement + (int) Math.floor(iSamplesPerHeatmap / 2);
				fPosCursorLastElement = fTextureHeight - ((iLastSample + 1) * fStep);
			}
		}
		else {
			if ((iselElement - (int) Math.ceil(iSamplesPerHeatmap / 2)) >= 0
				&& (iselElement + (int) Math.floor(iSamplesPerHeatmap / 2)) < iAlNumberSamples
					.get(iSelectorBar - 1)) {
				iFirstSample = iselElement - (int) Math.ceil(iSamplesPerHeatmap / 2);
				fPosCursorFirstElement = fTextureHeight - (iFirstSample * fStep);
				iLastSample = iselElement + (int) Math.floor(iSamplesPerHeatmap / 2);
				fPosCursorLastElement = fTextureHeight - ((iLastSample + 1) * fStep);
			}
		}

		setDisplayListDirty();
		triggerSelectionBlock();

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingWholeBlock = false;
			bDisableCursorDragging = false;
		}
	}

	/**
	 * Function used for updating cursor position in case of dragging
	 * 
	 * @param gl
	 */
	private void handleCursorDragging(final GL gl) {
		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;
		int iNrSamples;

		fArTargetWorldCoordinates =
			GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x, currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / (iAlNumberSamples.get(iSelectorBar - 1));// * 2);
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		// cursor for iFirstElement
		if (iDraggedCursor == 1) {
			if (fYPosMouse > fPosCursorLastElement && fYPosMouse <= viewFrustum.getHeight() - 0.6f) {
				iselElement = (int) Math.floor((fTextureHeight - fYPosMouse) / fStep);
				iNrSamples = iLastSample - iselElement + 1;
				if (iNrSamples >= MIN_SAMPLES_PER_HEATMAP
					&& iNrSamples < iAlNumberSamples.get(iSelectorBar - 1) / 3) {
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
				if (iNrSamples >= MIN_SAMPLES_PER_HEATMAP
					&& iNrSamples < iAlNumberSamples.get(iSelectorBar - 1) / 3) {
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

		if (glMouseListener.wasMouseReleased()) {
			bIsDraggingActive = false;
			bDisableBlockDragging = false;
		}
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
						set.getVA(iContentVAID).getGroupList().get(iExternalID).toggleSelectionType();
						setDisplayListDirty();
						break;

					case DRAGGED:
						if (bSplitGroupGene == false) {
							bSplitGroupGene = true;
							bSplitGroupExp = false;
							iGroupToSplit = iExternalID;
							DraggingPoint = pick.getPickedPoint();
						}
						setDisplayListDirty();
						break;

					case RIGHT_CLICKED:

						boolean bEnableInterchange = false;
						boolean bEnableMerge = false;
						int iNrSelectedGroups = 0;

						IGroupList tempGroupList = set.getVA(iContentVAID).getGroupList();

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
						// System.out.println(set.getVA(iContentVAID).getGroupList().get(iExternalID)
						// .getNrElements());
						// setDisplayListDirty();
						break;
				}
				break;

			// handling the groups/clusters of experiments
			case HIER_HEAT_MAP_EXPERIMENTS_GROUP:
				switch (pickingMode) {
					case CLICKED:
						set.getVA(iStorageVAID).getGroupList().get(iExternalID).toggleSelectionType();
						setDisplayListDirty();
						break;

					case DRAGGED:
						if (bSplitGroupExp == false) {
							bSplitGroupExp = true;
							bSplitGroupGene = false;
							iGroupToSplit = iExternalID;
							DraggingPoint = pick.getPickedPoint();
						}
						setDisplayListDirty();
						break;

					case RIGHT_CLICKED:

						boolean bEnableInterchange = false;
						boolean bEnableMerge = false;
						int iNrSelectedGroups = 0;

						IGroupList tempGroupList = set.getVA(iStorageVAID).getGroupList();

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
						// System.out.println(set.getVA(iStorageVAID).getGroupList().get(iExternalID)
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
			case HIER_HEAT_MAP_TEXTURE_CURSOR:
				switch (pickingMode) {
					case CLICKED:

						if (bSkipLevel1 == false) {

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
						}
						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle dragging cursor for first and last element of block
			case HIER_HEAT_MAP_CURSOR:
				switch (pickingMode) {
					case CLICKED:
						break;

					case DRAGGED:
						if (bDisableCursorDragging)
							return;
						bIsDraggingActive = true;
						bDisableBlockDragging = true;
						iDraggedCursor = iExternalID;
						setDisplayListDirty();
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle dragging cursor for whole block
			case HIER_HEAT_MAP_BLOCK_CURSOR:
				switch (pickingMode) {
					case CLICKED:
						break;

					case DRAGGED:
						if (bDisableBlockDragging)
							return;
						bIsDraggingWholeBlock = true;
						bDisableCursorDragging = true;
						iDraggedCursor = iExternalID;
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

						if (bSkipLevel1 == false) {
							iSelectorBar = iExternalID;
							// if (iSelectorBar == iNrSelBar) {
							// iSelectorBar--;
							// }
							initPosCursor();
							triggerSelectionBlock();
							setDisplayListDirty();
						}
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
						PickingPoint = pick.getPickedPoint();
						triggerSelectionBlock();
						setDisplayListDirty();
						break;

					case DRAGGED:
						break;

					case MOUSE_OVER:
						break;
				}
				break;

			// handle click on level 3 (EHM)
			case HIER_HEAT_MAP_VIEW_SELECTION:
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
	public void broadcastElements() {
		throw new IllegalStateException("broadcast elements of the contained heat map or all?");
	}

	@Override
	public void clearAllSelections() {

		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();

		AlSelection.clear();
		AlExpMouseOver.clear();
		AlExpSelected.clear();
		iSelectorBar = 1;
		initPosCursor();
		bRedrawTextures = true;
		setDisplayListDirty();
		triggerSelectionBlock();
		glHeatMapView.setDisplayListDirty();

		// group/cluster selections
		if (set.getVA(iStorageVAID).getGroupList() != null) {
			IGroupList groupList = set.getVA(iStorageVAID).getGroupList();

			for (Group group : groupList)
				group.setSelectionType(ESelectionType.NORMAL);
		}
		if (set.getVA(iContentVAID).getGroupList() != null) {

			IGroupList groupList = set.getVA(iContentVAID).getGroupList();

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

	public void changeFocus(boolean bInFocus) {
		bIsHeatmapInFocus = bIsHeatmapInFocus == true ? false : true;

		setDisplayListDirty();
	}

	public void startClustering(ClusterState clusterState) {

		this.clusterstate = clusterState;

		// int iNrElem = 0;
		//
		// if (clusterState.getClustererType() == EClustererType.GENE_CLUSTERING)
		// iNrElem = set.getVA(iContentVAID).size();
		// else
		// iNrElem = set.getVA(iStorageVAID).size();
		//
		// if (iNrElem > 1000) {
		//
		// GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// Shell shell = new Shell();
		// MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.CANCEL);
		// messageBox.setText("Start Clustering");
		// messageBox
		// .setMessage("Data set contains more than 1000 elements because of this the cluster process will take some time.");
		// if (messageBox.open() == SWT.CANCEL)
		// bSkipClustering = true;
		// }
		// });
		// }

		bUseClusteredVA = true;
		initData();
		bUseClusteredVA = false;

		setDisplayListDirty();
	}

	private void activateGroupHandling() {

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
	public void initData() {

		super.initData();

		initHierarchy();
		calculateTextures();
		initPosCursor();

		glHeatMapView.setSet(set);
		glHeatMapView.initData();

		bRedrawTextures = true;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleInterchangeGroups(boolean bGeneGroup) {
		int iVAId = 0;

		if (bGeneGroup)
			iVAId = iContentVAID;
		else
			iVAId = iStorageVAID;

		IGroupList groupList = set.getVA(iVAId).getGroupList();

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
		if (groupList.interchange(set.getVA(iVAId), selGroups.get(0), selGroups.get(1)) == false) {
			System.out.println("Problem during interchange!!!");
			return;
		}

		bRedrawTextures = true;

		setDisplayListDirty();

	}

	@Override
	public void handleMergeGroups(boolean bGeneGroup) {
		int iVAId = 0;

		if (bGeneGroup)
			iVAId = iContentVAID;
		else
			iVAId = iStorageVAID;

		IGroupList groupList = set.getVA(iVAId).getGroupList();

		ArrayList<Integer> selGroups = new ArrayList<Integer>();

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		for (Group iter : groupList) {
			if (iter.getSelectionType() == ESelectionType.SELECTION)
				selGroups.add(groupList.indexOf(iter));
		}

		// merge
		while (selGroups.size() >= 2) {

			int iLastSelected = selGroups.size() - 1;

			// merge last and the one before last
			if (groupList.merge(set.getVA(iVAId), selGroups.get(iLastSelected - 1), selGroups
				.get(iLastSelected)) == false) {
				System.out.println("Problem during merge!!!");
				return;
			}
			selGroups.remove(iLastSelected);
		}

		// set current texture
		if (bGeneGroup)
			iSelectorBar = selGroups.get(0) + 1;

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

		startClusteringListener = new StartClusteringListener();
		startClusteringListener.setHandler(this);
		eventPublisher.addListener(StartClusteringEvent.class, startClusteringListener);

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
		if (startClusteringListener != null) {
			eventPublisher.removeListener(startClusteringListener);
			startClusteringListener = null;
		}
	}

	@Override
	public void handleUpdateView() {
		bRedrawTextures = true;
		setDisplayListDirty();
	}

	public void handleArrowDownAltPressed() {
		iSamplesPerHeatmap--;
		initPosCursor();
		triggerSelectionBlock();
		setDisplayListDirty();
	}

	public void handleArrowUpAltPressed() {
		iSamplesPerHeatmap++;
		initPosCursor();
		triggerSelectionBlock();
		setDisplayListDirty();
	}

	public void handleArrowDownCtrlPressed() {
		if (iSelectorBar < iNrSelBar) {
			iSelectorBar++;
			initPosCursor();
			triggerSelectionBlock();
			setDisplayListDirty();
		}
	}

	public void handleArrowUpCtrlPressed() {
		if (iSelectorBar > 1) {
			iSelectorBar--;
			initPosCursor();
			triggerSelectionBlock();
			setDisplayListDirty();
		}
	}

	public void handleArrowUpPressed() {
		// glHeatMapView.upDownSelect(true);
	}

	public void handleArrowDownPressed() {
		// glHeatMapView.upDownSelect(false);
	}

	public void handleArrowLeftPressed() {
		// glHeatMapView.leftRightSelect(true);
	}

	public void handleArrowRightPressed() {
		// glHeatMapView.leftRightSelect(false);
	}

}
