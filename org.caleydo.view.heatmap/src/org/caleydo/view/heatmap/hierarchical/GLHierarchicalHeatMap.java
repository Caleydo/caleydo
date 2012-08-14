/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
import java.util.HashSet;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.graph.tree.ClusterNode;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ClusterNodeSelectionListener;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.event.view.ClusterNodeSelectionEvent;
import org.caleydo.core.event.view.group.ExportContentGroupsEvent;
import org.caleydo.core.event.view.group.ExportDimensionGroupsEvent;
import org.caleydo.core.event.view.group.InterchangeContentGroupsEvent;
import org.caleydo.core.event.view.group.InterchangeDimensionGroupsEvent;
import org.caleydo.core.event.view.group.MergeContentGroupsEvent;
import org.caleydo.core.event.view.group.MergeDimensionGroupsEvent;
import org.caleydo.core.event.view.tablebased.NewRecordGroupInfoEvent;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.gui.ExportDataDialog;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.mapping.color.IColorMappingUpdateListener;
import org.caleydo.core.util.mapping.color.UpdateColorMappingEvent;
import org.caleydo.core.util.mapping.color.UpdateColorMappingListener;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.DimensionGroupExportingListener;
import org.caleydo.core.view.opengl.canvas.listener.DimensionGroupInterChangingActionListener;
import org.caleydo.core.view.opengl.canvas.listener.DimensionGroupMergingActionListener;
import org.caleydo.core.view.opengl.canvas.listener.IClusterNodeEventReceiver;
import org.caleydo.core.view.opengl.canvas.listener.NewContentGroupInfoActionListener;
import org.caleydo.core.view.opengl.canvas.listener.RecordGroupExportingListener;
import org.caleydo.core.view.opengl.canvas.listener.RecordGroupInterChangingActionListener;
import org.caleydo.core.view.opengl.canvas.listener.RecordGroupMergingActionListener;
import org.caleydo.core.view.opengl.canvas.remote.IGLRemoteRenderingView;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IContentGroupsActionHandler;
import org.caleydo.core.view.opengl.canvas.remote.receiver.IDimensionGroupsActionHandler;
import org.caleydo.core.view.opengl.canvas.remote.receiver.INewContentGroupInfoHandler;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.vislink.ConnectedElementRepresentationManager;
import org.caleydo.core.view.vislink.RemoteRenderingTransformer;
import org.caleydo.datadomain.pathway.contextmenu.container.GeneGroupContextMenuItemContainer;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.dendrogram.GLDendrogram;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.HierarchicalHeatMapTemplate;
import org.caleydo.view.heatmap.listener.GLHierarchicalHeatMapKeyListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Rendering the GLHierarchicalHeatMap with remote rendering support.
 * 
 * @author Bernhard Schlegl
 * @author Marc Streit
 * @author Alexander Lex
 */
public class GLHierarchicalHeatMap extends ATableBasedView implements
		IContentGroupsActionHandler, IDimensionGroupsActionHandler,
		IClusterNodeEventReceiver, INewContentGroupInfoHandler, IGLRemoteRenderingView,
		IColorMappingUpdateListener {
	public static String VIEW_TYPE = "org.caleydo.view.heatmap.hierarchical";

	public static String VIEW_NAME = "Hierarchical Heat Map";

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

	protected int numberOfSamplesPerTexture = 100;

	protected int numberOfSamplesPerHeatmap = 100;

	private int numberOfRecords = 0;
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
	private final static int MIN_SAMPLES_SKIP_LEVEL_2 = 120;

	private int iNrTextures = 0;
	/** array of textures for holding the data samples */
	private ArrayList<Texture> textures = new ArrayList<Texture>();
	private ArrayList<Integer> numberSamples = new ArrayList<Integer>();

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
	private GLDendrogram<RecordGroupList> glRecordDendrogramView;
	private boolean recordDendrogramActive = false;
	private boolean recordDendrogramRenderCut = false;
	private boolean bFirstStartGeneDendrogram = true;
	private GLDendrogram<DimensionGroupList> glDimensionDendrogramView;
	private boolean dimensionDendrogramActive = false;
	private boolean dimensionDendrogramRenderCut = false;
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

	private RecordGroupExportingListener contentGroupExportingListener;
	private DimensionGroupExportingListener dimensionGroupExportingListener;

	private RecordGroupInterChangingActionListener contentGroupInterchangingListener;
	private DimensionGroupInterChangingActionListener dimensionGroupInterchangingListener;

	private RecordGroupMergingActionListener contentGroupMergingListener;
	private DimensionGroupMergingActionListener dimensionGroupMergingListener;
	private UpdateColorMappingListener updateViewListener;
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
	public GLHierarchicalHeatMap(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		ArrayList<SelectionType> alSelectionTypes = new ArrayList<SelectionType>();
		alSelectionTypes.add(SelectionType.NORMAL);
		alSelectionTypes.add(SelectionType.MOUSE_OVER);
		alSelectionTypes.add(SelectionType.SELECTION);

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
		selectionTransformer = new RemoteRenderingTransformer(uniqueID,
				remoteLevelElementWhiteList);

	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);
		textRenderer = new CaleydoTextRenderer(24);

		createHeatMap();
		createDendrogram();

		glHeatMapView.initRemote(gl, this, glMouseListener);
		// glHeatMapView.useFishEye(false);
		glRecordDendrogramView.initRemote(gl, this, glMouseListener);
		glDimensionDendrogramView.initRemote(gl, this, glMouseListener);

		initTextures(gl);
		// activateGroupHandling();
	}

	/**
	 * Function responsible for initialization of hierarchy levels. Depending on
	 * the amount of samples in the data set 1, 2, or 3 levels are used.
	 */
	private void initHierarchy() {

		numberOfRecords = tablePerspective.getRecordPerspective().getVirtualArray().size();

		if (numberOfRecords < MIN_SAMPLES_SKIP_LEVEL_2) {

			bSkipLevel1 = true;
			bSkipLevel2 = true;

			iSamplesPerHeatmap = numberOfRecords;
			numberSamples.clear();
			numberSamples.add(numberOfRecords);

		} else if (numberOfRecords < MIN_SAMPLES_SKIP_LEVEL_1) {
			bSkipLevel1 = true;
			bSkipLevel2 = false;

			iNrTextures = 1;

			iSamplesPerTexture = numberOfRecords;
			iSamplesLevel2 = numberOfRecords;
			iSamplesPerHeatmap = (int) Math.floor((double) iSamplesPerTexture / 3);

		} else {
			bSkipLevel1 = false;
			bSkipLevel2 = false;

			iNrTextures = (int) Math.ceil((double) numberOfRecords
					/ MAX_SAMPLES_PER_TEXTURE);

			if (iNrTextures <= 1)
				iSamplesPerTexture = numberOfRecords;
			else
				iSamplesPerTexture = MAX_SAMPLES_PER_TEXTURE;

			iSamplesLevel2 = 200;

			iSamplesPerHeatmap = (int) Math.floor((double) iSamplesLevel2 / 3);
		}

		if (iSamplesPerHeatmap > MAX_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = MAX_SAMPLES_PER_HEATMAP;

		if (iSamplesPerHeatmap < MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = MIN_SAMPLES_PER_HEATMAP;

		if (numberOfRecords <= MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = numberOfRecords;
	}

	@Override
	public void initLocal(GL2 gl) {

		// Register keyboard listener to GL2 canvas
		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				parentComposite.addKeyListener(glKeyListener);
			}
		});

		init(gl);

	}

	@Override
	public void initRemote(GL2 gl, final AGLView glParentView,
			GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);

	}

	/**
	 * Init (reset) the positions of cursors used for highlighting selected
	 * elements in stage 1 (overviewBar)
	 * 
	 * @param
	 */
	private void initPosCursorLevel1() {

		int iNumberSample = numberOfRecords;

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

		if (numberOfRecords <= MIN_SAMPLES_PER_HEATMAP)
			iSamplesPerHeatmap = numberOfRecords;

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

			textures.clear();
			numberSamples.clear();

			Texture tempTextur = null;

			textures.add(tempTextur);
			numberSamples.add(iSamplesPerTexture);

		} else {

			textures.clear();
			numberSamples.clear();

			Texture tempTextur = null;

			for (int i = 0; i < iNrTextures; i++) {

				textures.add(tempTextur);
				numberSamples.add(iSamplesPerTexture);
			}
		}
	}

	/**
	 * Init textures, build array of textures used for holding the whole samples
	 * 
	 * @param gl
	 */
	private void initTextures(final GL2 gl) {

		if (bSkipLevel1 && bSkipLevel2)
			return;

		if (bSkipLevel1) {

			// only one texture is needed

			textures.clear();
			numberSamples.clear();

			Texture tempTextur;

			RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
					.getVirtualArray();
			DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
					.getVirtualArray();

			int iTextureHeight = recordVA.size();
			int iTextureWidth = dimensionVA.size();

			float fLookupValue = 0;
			float fOpacity = 0;

			FloatBuffer FbTemp = FloatBuffer.allocate(iTextureWidth * iTextureHeight * 4);

			for (Integer recordIndex : recordVA) {
				for (Integer iDimensionIndex : dimensionVA) {
					if (recordSelectionManager.checkStatus(SelectionType.DESELECTED,
							recordIndex)) {
						fOpacity = 0.3f;
					} else {
						fOpacity = 1.0f;
					}

					fLookupValue = dataDomain.getTable().getFloat(
							dimensionDataRepresentation, recordIndex, iDimensionIndex);

					float[] fArMappingColor = dataDomain.getColorMapper().getColor(
							fLookupValue);

					float[] fArRgba = { fArMappingColor[0], fArMappingColor[1],
							fArMappingColor[2], fOpacity };

					FbTemp.put(fArRgba);
				}
			}
			FbTemp.rewind();

			TextureData texData = new TextureData(GLProfile.getDefault(),
					GL2.GL_RGBA /* internalFormat */, iTextureWidth /* height */,
					iTextureHeight /* width */, 0 /* border */,
					GL2.GL_RGBA /* pixelFormat */, GL2.GL_FLOAT /* pixelType */,
					false /* mipmap */, false /* dataIsCompressed */,
					false /* mustFlipVertically */, FbTemp, null);

			tempTextur = TextureIO.newTexture(0);
			tempTextur.updateImage(gl, texData);

			textures.add(tempTextur);
			numberSamples.add(iSamplesPerTexture);

		} else {

			textures.clear();
			numberSamples.clear();

			Texture tempTextur;

			RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
					.getVirtualArray();
			DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
					.getVirtualArray();
			int iTextureHeight = recordVA.size();
			int iTextureWidth = dimensionVA.size();

			iSamplesPerTexture = (int) Math.ceil((double) iTextureHeight / iNrTextures);

			float fLookupValue = 0;
			float fOpacity = 0;

			FloatBuffer[] FbTemp = new FloatBuffer[iNrTextures];

			for (int itextures = 0; itextures < iNrTextures; itextures++) {

				if (itextures == iNrTextures - 1) {
					numberSamples.add(iTextureHeight - iSamplesPerTexture * itextures);
					FbTemp[itextures] = FloatBuffer
							.allocate((iTextureHeight - iSamplesPerTexture * itextures)
									* iTextureWidth * 4);
				} else {
					numberSamples.add(iSamplesPerTexture);
					FbTemp[itextures] = FloatBuffer.allocate(iSamplesPerTexture
							* iTextureWidth * 4);
				}
			}

			int iCount = 0;
			int iTextureCounter = 0;

			for (Integer recordID : recordVA) {
				iCount++;
				for (Integer dimensionID : dimensionVA) {
					if (recordSelectionManager.checkStatus(SelectionType.DESELECTED,
							recordID)) {
						fOpacity = 0.3f;
					} else {
						fOpacity = 1.0f;
					}

					fLookupValue = dataDomain.getTable().getFloat(
							dimensionDataRepresentation, recordID, dimensionID);

					float[] fArMappingColor = dataDomain.getColorMapper().getColor(
							fLookupValue);

					float[] fArRgba = { fArMappingColor[0], fArMappingColor[1],
							fArMappingColor[2], fOpacity };

					FbTemp[iTextureCounter].put(fArRgba);
				}
				if (iCount >= numberSamples.get(iTextureCounter)) {
					FbTemp[iTextureCounter].rewind();

					TextureData texData = new TextureData(GLProfile.getDefault(),
							GL2.GL_RGBA /* internalFormat */, iTextureWidth /* height */,
							numberSamples.get(iTextureCounter) /* width */,
							0 /* border */, GL2.GL_RGBA /* pixelFormat */,
							GL2.GL_FLOAT /* pixelType */, false /* mipmap */,
							false /* dataIsCompressed */, false /* mustFlipVertically */,
							FbTemp[iTextureCounter], null);

					tempTextur = TextureIO.newTexture(0);
					tempTextur.updateImage(gl, texData);

					textures.add(tempTextur);

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

		glHeatMapView = new GLHeatMap(parentGLCanvas, parentComposite, viewFrustum);

		glHeatMapView.setDataDomain(dataDomain);

		RecordPerspective recordPerspective = new RecordPerspective(dataDomain);
		recordPerspective.init(null);

		TablePerspective embeddedHMContainer = new TablePerspective(dataDomain,
				recordPerspective, tablePerspective.getDimensionPerspective());

		glHeatMapView.setTablePerspective(embeddedHMContainer);

		glHeatMapView.setRemoteRenderingGLView(this);
		glHeatMapView.setRemoteLevelElement(heatMapRemoteElement);

		renderTemplate = new HierarchicalHeatMapTemplate(glHeatMapView);
		glHeatMapView.setRenderTemplate(renderTemplate);
		renderTemplate.setBottomSpacing(0.6f);
		heatMapRemoteElement.setGLView(glHeatMapView);
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

		glRecordDendrogramView = new GLDendrogram<RecordGroupList>(parentGLCanvas,
				parentComposite, viewFrustum, true);
		glRecordDendrogramView.setRemoteRenderingGLView(this);
		glRecordDendrogramView.setTablePerspective(tablePerspective);
		glRecordDendrogramView.setDataDomain(dataDomain);
		glRecordDendrogramView.setRemoteRenderingGLView(this);
		glRecordDendrogramView.initData();
		glRecordDendrogramView.setRenderUntilCut(recordDendrogramRenderCut);

		glDimensionDendrogramView = new GLDendrogram<DimensionGroupList>(parentGLCanvas,
				parentComposite, viewFrustum, false);
		glDimensionDendrogramView.setRemoteRenderingGLView(this);
		glDimensionDendrogramView.setTablePerspective(tablePerspective);
		glDimensionDendrogramView.setDataDomain(dataDomain);
		glDimensionDendrogramView.setRemoteRenderingGLView(this);
		glDimensionDendrogramView.initData();
		glDimensionDendrogramView.setRenderUntilCut(dimensionDendrogramRenderCut);
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
		super.setDetailLevel(detailLevel);
	}

	@Override
	public void displayLocal(GL2 gl) {

		if (glDimensionDendrogramView != null)
			glDimensionDendrogramView.processEvents();
		if (glRecordDendrogramView != null)
			glRecordDendrogramView.processEvents();
		if (glHeatMapView != null)
			glHeatMapView.processEvents();

		if (!lazyMode) {
			pickingManager.handlePicking(this, gl);
		}

		display(gl);

		if (busyState != EBusyState.OFF) {
			renderBusyMode(gl);
		}
	}

	@Override
	public void displayRemote(GL2 gl) {

		display(gl);

		ConnectedElementRepresentationManager cerm = GeneralManager.get()
				.getViewManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

	}

	/**
	 * Jump to other areas of a heat map
	 * 
	 * @param
	 */
	@Override
	protected void reactOnExternalSelection(SelectionDelta delta) {

		if (delta.getIDType() != recordIDType)
			return;

		if (bSkipLevel2 == false) {

			int mouseOverElement = 0;
			SelectionType type = SelectionType.NORMAL;

			for (SelectionDeltaItem item : delta) {
				if (item.isRemove())
					continue;
				if (item.getSelectionType().getPriority() > type.getPriority()) {
					mouseOverElement = item.getID();
					type = item.getSelectionType();
				}

			}

			if (type == SelectionType.NORMAL)
				return;
			// for (Integer mouseOverElement : setMouseOverElements) {

			int index = tablePerspective.getRecordPerspective().getVirtualArray()
					.indexOf(mouseOverElement);

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
				} else if (iLastSampleLevel1 > numberOfRecords) {
					iFirstSampleLevel1 = numberOfRecords - iSamplesLevel2;
					iLastSampleLevel1 = numberOfRecords;
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
	protected void reactOnRecordVAChanges() {

		// glHeatMapView.handleVirtualArrayUpdate(delta, getShortInfo());
		bRedrawTextures = true;

		setDisplayListDirty();
	}

	@Override
	public void handleDimensionVAUpdate(String dimensionPerspectiveID) {
		if (!(tablePerspective.getDimensionPerspective().getPerspectiveID()
				.equals(dimensionPerspectiveID)))
			return;
		super.handleDimensionVAUpdate(dimensionPerspectiveID);

		bRedrawTextures = true;
	}

	@Override
	public void handleRecordVAUpdate(String recordPerspectiveID) {
		if (!(tablePerspective.getRecordPerspective().getPerspectiveID()
				.equals(recordPerspectiveID)))
			return;
		super.handleRecordVAUpdate(recordPerspectiveID);

		bRedrawTextures = true;
		hasDataWindowChanged = true;
		iPickedSampleLevel1 = 0;
		recordDendrogramActive = false;
		setDisplayListDirty();

		initData();
	}

	/**
	 * Render caption, simplified version used in (original) heatmap
	 * 
	 * @param gl
	 * @param label
	 * @param fXOrigin
	 * @param fYOrigin
	 * @param fFontScaling
	 */
	private void renderCaption(GL2 gl, String sLabel, float fXOrigin, float fYOrigin,
			float fFontScaling) {
		textRenderer.setColor(1, 1, 1, 1);
		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
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
	private void renderSelectedDomain(GL2 gl, Vec3f startpoint1, Vec3f endpoint1,
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

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(startpoint1.x(), startpoint1.y(), startpoint1.z());
		gl.glVertex3f(startpoint1.x() + 2 * fthickness, startpoint1.y(), startpoint1.z());
		gl.glVertex3f(startpoint2.x() + 2 * fthickness, startpoint2.y(), startpoint2.z());
		gl.glVertex3f(startpoint2.x(), startpoint2.y(), startpoint2.z());
		gl.glEnd();

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(endpoint1.x(), endpoint1.y(), endpoint1.z());
		gl.glVertex3f(endpoint1.x() - 1 * fthickness, endpoint1.y(), endpoint1.z());
		gl.glVertex3f(endpoint2.x() - 1 * fthickness, endpoint2.y(), endpoint2.z());
		gl.glVertex3f(endpoint2.x(), endpoint2.y(), endpoint2.z());
		gl.glEnd();

		// fill gap
		gl.glBegin(GL2.GL_QUADS);
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

		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
		gl.glColor4f(1, 1, 1, 1);
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

		TextureMask.enable(gl);
		TextureMask.bind(gl);

		TextureCoords texCoordsMask = TextureMask.getImageTexCoords();

		gl.glBegin(GL2.GL_POLYGON);
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

		gl.glBegin(GL2.GL_POLYGON);
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
			gl.glBegin(GL2.GL_POLYGON);
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

		TextureMask.disable(gl);

		TextureMaskNeg.enable(gl);
		TextureMaskNeg.bind(gl);

		TextureCoords texCoordsMaskNeg = TextureMaskNeg.getImageTexCoords();

		gl.glBegin(GL2.GL_POLYGON);
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

		gl.glBegin(GL2.GL_POLYGON);
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
			gl.glBegin(GL2.GL_POLYGON);
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

		TextureMaskNeg.disable(gl);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPopAttrib();
	}

	/**
	 * Renders class assignments for experiments in level 2 (textures)
	 * 
	 * @param gl
	 */
	private void renderClassAssignmentsExperimentsLevel2(final GL2 gl) {
		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();
		if (dimensionVA.getGroupList() == null)
			return;

		if (fScalingLevel2 == 0.2f)
			return;

		float fWidthLevel2 = renderStyle.getWidthLevel2() * fScalingLevel2;
		int iNrElements = dimensionVA.size();
		float fWidthSamples = fWidthLevel2 / iNrElements;
		float fxpos = 0;
		float fHeight = viewFrustum.getHeight();

		float fHeightClusterVisualization = renderStyle.getWidthClusterVisualization();

		DimensionGroupList groupList = dimensionVA.getGroupList();
		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			float classWidth = groupList.get(i).getSize() * fWidthSamples;

			gl.glColor4f(1, 1, 1, 1);
			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
			Texture tempTexture = null;
			if (dimensionVA.getGroupList().get(i).getSelectionType() == SelectionType.SELECTION) {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_SELECTED);
			} else {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_NORMAL);
			}

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_DIMENSION_GROUP, i));

			tempTexture.enable(gl);
			tempTexture.bind(gl);

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fxpos, 0, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fxpos, 0 - fHeightClusterVisualization, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fxpos + classWidth, 0 - fHeightClusterVisualization, FIELD_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fxpos + classWidth, 0, FIELD_Z);
			gl.glEnd();

			tempTexture.disable(gl);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			gl.glPopName();

			if (i == iNrClasses - 1)
				return;

			gl.glColor4fv(BACKGROUND_COLOR, 0);
			gl.glBegin(GL2.GL_LINES);
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
	private void renderClassAssignmentsExperimentsLevel3(final GL2 gl) {

		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();
		if (dimensionVA.getGroupList() == null)
			return;

		int iNrElements = dimensionVA.size();
		float fWidthSamples = renderStyle.getWidthLevel3() / iNrElements;
		float fxpos = 0;

		float fHeight = 0;
		float fHeightClusterVisualization = renderStyle.getWidthClusterVisualization();

		if (dimensionDendrogramActive || dimensionDendrogramRenderCut)
			fHeight = viewFrustum.getHeight()
					- renderStyle.getHeightExperimentDendrogram();
		else
			fHeight = viewFrustum.getHeight();

		DimensionGroupList groupList = dimensionVA.getGroupList();
		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			float classWidth = groupList.get(i).getSize() * fWidthSamples;

			gl.glColor4f(1, 1, 1, 1);
			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
			Texture tempTexture = null;

			if (dimensionVA.getGroupList().get(i).getSelectionType() == SelectionType.SELECTION) {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_SELECTED);
			} else {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_NORMAL);
			}

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_DIMENSION_GROUP, i));

			tempTexture.enable(gl);
			tempTexture.bind(gl);

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(fxpos, 0, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(fxpos, 0 - fHeightClusterVisualization, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(fxpos + classWidth, 0 - fHeightClusterVisualization, FIELD_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(fxpos + classWidth, 0, FIELD_Z);
			gl.glEnd();

			tempTexture.disable(gl);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			gl.glPopName();

			if (i == iNrClasses - 1)
				return;

			gl.glColor4fv(BACKGROUND_COLOR, 0);
			gl.glBegin(GL2.GL_LINES);
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
	private void renderClassAssignmentsGenesLevel1(final GL2 gl) {
		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();
		if (recordVA.getGroupList() == null)
			return;

		float fHeight = viewFrustum.getHeight();
		int iNrElements = numberOfRecords;
		float fHeightSamples = fHeight / iNrElements;
		float fyPos = fHeight;

		float fWidthLevel1 = renderStyle.getWidthLevel1();
		float fWidthClusterVisualization = renderStyle.getWidthClusterVisualization();

		RecordGroupList groupList = recordVA.getGroupList();

		gl.glTranslatef(fWidthLevel1, 0, 0);

		int iNrClasses = groupList.size();

		gl.glLineWidth(1f);

		for (int i = 0; i < iNrClasses; i++) {

			float classHeight = groupList.get(i).getSize() * fHeightSamples;

			gl.glColor4f(1, 1, 1, 1);
			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
			Texture tempTexture = null;

			if (recordVA.getGroupList().get(i).getSelectionType() == SelectionType.SELECTION) {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_SELECTED);
			}

			else {
				tempTexture = textureManager.getIconTexture(gl,
						EIconTextures.HEAT_MAP_GROUP_NORMAL);
			}

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HEAT_MAP_RECORD_GROUP, i));

			tempTexture.enable(gl);
			tempTexture.bind(gl);

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(0, fyPos, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(0 + fWidthClusterVisualization, fyPos, FIELD_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(0 + fWidthClusterVisualization, fyPos - classHeight, FIELD_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(0, fyPos - classHeight, FIELD_Z);
			gl.glEnd();

			tempTexture.disable(gl);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			gl.glPopName();

			if (i == iNrClasses - 1) {
				gl.glTranslatef(-fWidthLevel1, 0, 0);
				return;
			}

			gl.glColor4fv(BACKGROUND_COLOR, 0);
			gl.glBegin(GL2.GL_LINES);
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
	private void renderClassAssignmentsGenesLevel2(final GL2 gl) {

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		// FIXME: Class assignments could be rendered using HeatMapUtil
		if (recordVA.getGroupList() == null)
			return;

		float fHeight = viewFrustum.getHeight();
		float fHeightSamples = fHeight / iSamplesLevel2;
		float fWidthLevel2 = renderStyle.getWidthLevel2() * fScalingLevel2;
		float fWidthClusterVisualization = renderStyle.getWidthClusterVisualization();

		gl.glTranslatef(fWidthLevel2, 0, 0);

		// cluster border stuff
		int iIdxCluster = 0;
		int iCounter = iFirstSampleLevel1;

		Group group = recordVA.getGroupList().get(iIdxCluster);
		while (group.getSize() < iCounter) {
			iIdxCluster++;
			iCounter -= group.getSize();
			group = recordVA.getGroupList().get(iIdxCluster);
		}

		int iCnt = 0;

		gl.glLineWidth(1f);

		for (int i = 0; i < iSamplesLevel2; i++) {

			if (iCounter == recordVA.getGroupList().get(iIdxCluster).getSize()) {

				gl.glColor4f(1, 1, 1, 1);
				gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
				Texture tempTexture = null;

				if (recordVA.getGroupList().get(iIdxCluster).getSelectionType() == SelectionType.SELECTION) {
					tempTexture = textureManager.getIconTexture(gl,
							EIconTextures.HEAT_MAP_GROUP_SELECTED);
				} else {
					tempTexture = textureManager.getIconTexture(gl,
							EIconTextures.HEAT_MAP_GROUP_NORMAL);
				}
				gl.glPushName(pickingManager.getPickingID(uniqueID,
						PickingType.HEAT_MAP_RECORD_GROUP, iIdxCluster));

				tempTexture.enable(gl);
				tempTexture.bind(gl);

				TextureCoords texCoords = tempTexture.getImageTexCoords();

				gl.glBegin(GL2.GL_QUADS);
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

				tempTexture.disable(gl);
				gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
				gl.glPopName();

				gl.glColor4fv(BACKGROUND_COLOR, 0);
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3f(fWidthClusterVisualization,
						fHeight - fHeightSamples * iCnt, CLUSTER_BORDERS_Z);
				gl.glVertex3f(-fWidthLevel2, fHeight - fHeightSamples * iCnt,
						CLUSTER_BORDERS_Z);
				gl.glEnd();

				fHeight -= fHeightSamples * iCnt;
				// FIXME
				if (iIdxCluster == recordVA.getGroupList().size() - 1)
					break;
				iIdxCluster++;
				iCounter = 0;
				iCnt = 0;
			}
			iCnt++;

			iCounter++;
		}

		gl.glColor4f(1, 1, 1, 1);
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		Texture tempTexture = null;

		if (recordVA.getGroupList().get(iIdxCluster).getSelectionType() == SelectionType.SELECTION) {
			tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_GROUP_SELECTED);
		} else {
			tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_GROUP_NORMAL);
		}

		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HEAT_MAP_RECORD_GROUP, iIdxCluster));

		tempTexture.enable(gl);
		tempTexture.bind(gl);

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0, fHeight, FIELD_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0 + fWidthClusterVisualization, fHeight, FIELD_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(0 + fWidthClusterVisualization, 0, FIELD_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(0, 0, FIELD_Z);
		gl.glEnd();

		tempTexture.disable(gl);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPopName();

		gl.glTranslatef(-fWidthLevel2, 0, 0);

	}

	/**
	 * Renders class assignments for genes in level 3 (embedded heat map)
	 * 
	 * @param gl
	 */
	private void renderClassAssignmentsGenesLevel3(final GL2 gl) {
		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		if (recordVA.getGroupList() == null)
			return;

		float fHeight = 0;

		if (dimensionDendrogramActive || dimensionDendrogramRenderCut)
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

		Group group = recordVA.getGroupList().get(iIdxCluster);
		while (group.getSize() < iCounter) {
			iIdxCluster++;
			iCounter -= group.getSize();
			group = recordVA.getGroupList().get(iIdxCluster);
		}

		int iCnt = 0;

		gl.glLineWidth(1f);

		for (int i = 0; i < iSamplesPerHeatmap; i++) {

			if (iCounter == recordVA.getGroupList().get(iIdxCluster).getSize()) {

				gl.glColor4f(1, 1, 1, 1);
				gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
				Texture tempTexture = null;

				if (recordVA.getGroupList().get(iIdxCluster).getSelectionType() == SelectionType.SELECTION) {
					tempTexture = textureManager.getIconTexture(gl,
							EIconTextures.HEAT_MAP_GROUP_SELECTED);
				} else {
					tempTexture = textureManager.getIconTexture(gl,
							EIconTextures.HEAT_MAP_GROUP_NORMAL);
				}

				gl.glPushName(pickingManager.getPickingID(uniqueID,
						PickingType.HEAT_MAP_RECORD_GROUP, iIdxCluster));

				tempTexture.enable(gl);
				tempTexture.bind(gl);

				TextureCoords texCoords = tempTexture.getImageTexCoords();

				gl.glBegin(GL2.GL_QUADS);
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

				tempTexture.disable(gl);
				gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
				gl.glPopName();

				gl.glColor4fv(BACKGROUND_COLOR, 0);
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3f(fWidthEHM + fWidthClusterVisualization, fHeight
						- fHeightSamples * iCnt, CLUSTER_BORDERS_Z);
				gl.glVertex3f(0, fHeight - fHeightSamples * iCnt, CLUSTER_BORDERS_Z);
				gl.glEnd();

				fHeight -= fHeightSamples * iCnt;
				// FIXME
				if (iIdxCluster == recordVA.getGroupList().size() - 1)
					break;
				iIdxCluster++;
				iCounter = 0;
				iCnt = 0;
			}
			iCnt++;
			iCounter++;
		}

		gl.glColor4f(1, 1, 1, 1);
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		Texture tempTexture = null;

		if (recordVA.getGroupList().get(iIdxCluster).getSelectionType() == SelectionType.SELECTION) {
			tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_GROUP_SELECTED);
		} else {
			tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_GROUP_NORMAL);
		}

		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HEAT_MAP_RECORD_GROUP, iIdxCluster));

		tempTexture.enable(gl);
		tempTexture.bind(gl);

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fWidthEHM, fHeight, FIELD_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fWidthEHM + fWidthClusterVisualization, fHeight, FIELD_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fWidthEHM + fWidthClusterVisualization, 0, FIELD_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fWidthEHM, 0, FIELD_Z);
		gl.glEnd();

		tempTexture.disable(gl);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPopName();
	}

	/**
	 * Render the first stage of the hierarchy (OverviewBar)
	 * 
	 * @param gl
	 */
	private void renderLevel1(GL2 gl) {
		float fHeight;
		float fWidth;
		float fyOffset = 0.0f;

		fHeight = viewFrustum.getHeight();
		fWidth = renderStyle.getWidthLevel1();

		float fHeightElem = fHeight / numberOfRecords;

		float fStep = 0;

		gl.glColor4f(1f, 1f, 0f, 1f);

		for (int i = 0; i < iNrTextures; i++) {

			fStep = fHeightElem * numberSamples.get(iNrTextures - i - 1);

			textures.get(iNrTextures - i - 1).enable(gl);
			textures.get(iNrTextures - i - 1).bind(gl);
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
					GL2.GL_NEAREST);
			gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
					GL2.GL_NEAREST);
			TextureCoords texCoords = textures.get(iNrTextures - i - 1)
					.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HEAT_MAP_TEXTURE_SELECTION, iNrTextures - i));
			gl.glBegin(GL2.GL_QUADS);
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
			textures.get(iNrTextures - i - 1).disable(gl);
		}
	}

	/**
	 * Render marker in OverviewBar for visualization of the currently (in stage
	 * 2) rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerLevel1(final GL2 gl) {
		float fHeight = viewFrustum.getHeight();
		float fWidthLevel1 = renderStyle.getWidthLevel1();
		Vec3f startpoint1, endpoint1, startpoint2, endpoint2;

		float fOffsetClusterVis = 0f;

		if (tablePerspective.getRecordPerspective().getVirtualArray().getGroupList() != null)
			fOffsetClusterVis += 0.1f;

		float fHeightElem = fHeight / numberOfRecords;

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
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(0, fPosCursorFirstElementLevel1, 0);
		gl.glVertex3f(fWidthLevel1, fPosCursorFirstElementLevel1, 0);
		gl.glVertex3f(fWidthLevel1, fPosCursorLastElementLevel1, 0);
		gl.glVertex3f(0, fPosCursorLastElementLevel1, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_LOOP);
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
	private void renderSelectedElementsLevel1(GL2 gl) {

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		float fHeight = viewFrustum.getHeight();
		float fWidthLevel1 = renderStyle.getWidthLevel1();

		float fHeightElem = fHeight / numberOfRecords;

		java.util.Set<Integer> setMouseOverElements = recordSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		java.util.Set<Integer> setSelectedElements = recordSelectionManager
				.getElements(SelectionType.SELECTION);

		gl.glLineWidth(2f);

		for (Integer mouseOverElement : setMouseOverElements) {

			int index = recordVA.indexOf(mouseOverElement.intValue());

			if ((index >= iFirstSampleLevel1 && index <= iLastSampleLevel1) == false) {
				gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3f(fWidthLevel1, fHeight - fHeightElem * index, SELECTION_Z);
				gl.glVertex3f(fWidthLevel1 + 0.1f, fHeight - fHeightElem * index,
						SELECTION_Z);
				gl.glEnd();
			}
		}

		for (Integer selectedElement : setSelectedElements) {

			int index = recordVA.indexOf(selectedElement.intValue());

			if ((index >= iFirstSampleLevel1 && index <= iLastSampleLevel1) == false) {
				gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
				gl.glBegin(GL2.GL_LINES);
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
	private void renderLevel2(GL2 gl) {
		float fHeight;

		fHeight = viewFrustum.getHeight();
		float fWidthLevel2 = renderStyle.getWidthLevel2() * fScalingLevel2;

		int iFirstTexture = 0;
		int iLastTexture = 0;
		int iFirstElementFirstTexture = iFirstSampleLevel1;
		int iLastElementLastTexture = iLastSampleLevel1;
		int iNrTexturesInUse = 0;

		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);

		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HIER_HEAT_MAP_FIELD_SELECTION, 1));
		// gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE,
		// GL2.GL_REPLACE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);

		if (bSkipLevel1) {
			Texture TexTemp1 = textures.get(0);
			TexTemp1.enable(gl);
			TexTemp1.bind(gl);
			TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d(texCoords1.left(), texCoords1.top());
			gl.glVertex3f(0, 0, 0);
			gl.glTexCoord2d(texCoords1.left(), texCoords1.bottom());
			gl.glVertex3f(0, fHeight, 0);
			gl.glTexCoord2d(texCoords1.right(), texCoords1.bottom());
			gl.glVertex3f(fWidthLevel2, fHeight, 0);
			gl.glTexCoord2d(texCoords1.right(), texCoords1.top());
			gl.glVertex3f(fWidthLevel2, 0, 0);
			gl.glEnd();

			TexTemp1.disable(gl);
		} else {

			while (numberSamples.get(iFirstTexture) < iFirstElementFirstTexture) {
				iFirstElementFirstTexture -= numberSamples.get(iFirstTexture);
				if (iFirstTexture < numberSamples.size() - 1)
					iFirstTexture++;
			}

			while (iLastElementLastTexture > numberSamples.get(iLastTexture)) {
				iLastElementLastTexture -= numberSamples.get(iLastTexture);
				iLastTexture++;
				if (iLastTexture == numberSamples.size() - 1) {
					if (iLastElementLastTexture > iSamplesPerTexture)
						iLastElementLastTexture = iSamplesPerTexture;
					break;
				}
			}

			iNrTexturesInUse = iLastTexture - iFirstTexture + 1;

			if (iNrTexturesInUse == 1) {

				float fScalingFirstElement = (float) iFirstElementFirstTexture
						/ numberSamples.get(iFirstTexture);
				float fScalingLastElement = (float) (iLastElementLastTexture + 1)
						/ numberSamples.get(iFirstTexture);

				Texture TexTemp1 = textures.get(iFirstTexture);
				TexTemp1.enable(gl);
				TexTemp1.bind(gl);
				TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

				gl.glBegin(GL2.GL_QUADS);
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

				TexTemp1.disable(gl);

			} else if (iNrTexturesInUse == 2) {

				float fScalingLastTexture = (float) (iLastElementLastTexture + 1)
						/ iSamplesLevel2;

				float fRatioFirstTexture = (float) (numberSamples.get(iFirstTexture) - iFirstElementFirstTexture)
						/ numberSamples.get(iFirstTexture);
				float fRatioLastTexture = (float) (iLastElementLastTexture + 1)
						/ numberSamples.get(iLastTexture);

				Texture TexTemp1 = textures.get(iFirstTexture);
				TexTemp1.enable(gl);
				TexTemp1.bind(gl);
				TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

				gl.glBegin(GL2.GL_QUADS);
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

				TexTemp1.disable(gl);

				// gl.glBegin(GL2.GL_LINES);
				// gl.glVertex3f(viewFrustum.getWidth(), fHeight *
				// fScalingLastTexture, 0);
				// gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				// gl.glEnd();

				Texture TexTemp2 = textures.get(iLastTexture);
				TexTemp2.enable(gl);
				TexTemp2.bind(gl);
				TextureCoords texCoords2 = TexTemp2.getImageTexCoords();

				gl.glBegin(GL2.GL_QUADS);
				gl.glTexCoord2d(texCoords2.left(), texCoords2.top() * fRatioLastTexture);
				gl.glVertex3f(0, 0, 0);
				gl.glTexCoord2d(texCoords2.left(), texCoords2.bottom());
				gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords2.right(), texCoords2.bottom());
				gl.glVertex3f(fWidthLevel2, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords2.right(), texCoords2.top() * fRatioLastTexture);
				gl.glVertex3f(fWidthLevel2, 0, 0);
				gl.glEnd();

				TexTemp2.disable(gl);
			} else if (iNrTexturesInUse == 3) {

				float fScalingFirstTexture = (float) (iSamplesPerTexture - iFirstElementFirstTexture)
						/ iSamplesLevel2;
				float fScalingLastTexture = (float) (iLastElementLastTexture + 1)
						/ iSamplesLevel2;

				float fRatioFirstTexture = (float) (numberSamples.get(iLastTexture) - iFirstElementFirstTexture)
						/ numberSamples.get(iFirstTexture);
				float fRatioLastTexture = (float) (iLastElementLastTexture + 1)
						/ numberSamples.get(iLastTexture);

				Texture TexTemp1 = textures.get(iFirstTexture);
				TexTemp1.enable(gl);
				TexTemp1.bind(gl);
				TextureCoords texCoords1 = TexTemp1.getImageTexCoords();

				gl.glBegin(GL2.GL_QUADS);
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

				TexTemp1.disable(gl);

				// gl.glBegin(GL2.GL_LINES);
				// gl.glVertex3f(viewFrustum.getWidth(), fHeight * (1 -
				// fScalingFirstTexture), 0);
				// gl.glVertex3f(0, fHeight * (1 - fScalingFirstTexture), 0);
				// gl.glEnd();

				Texture TexTemp2 = textures.get(iFirstTexture + 1);
				TexTemp2.enable(gl);
				TexTemp2.bind(gl);
				TextureCoords texCoords2 = TexTemp2.getImageTexCoords();

				gl.glBegin(GL2.GL_QUADS);
				gl.glTexCoord2d(texCoords2.left(), texCoords2.top());
				gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords2.left(), texCoords2.bottom());
				gl.glVertex3f(0, fHeight * (1 - fScalingFirstTexture), 0);
				gl.glTexCoord2d(texCoords2.right(), texCoords2.bottom());
				gl.glVertex3f(fWidthLevel2, fHeight * (1 - fScalingFirstTexture), 0);
				gl.glTexCoord2d(texCoords2.right(), texCoords2.top());
				gl.glVertex3f(fWidthLevel2, fHeight * fScalingLastTexture, 0);
				gl.glEnd();

				TexTemp2.disable(gl);

				// gl.glBegin(GL2.GL_LINES);
				// gl.glVertex3f(viewFrustum.getWidth(), fHeight *
				// fScalingLastTexture, 0);
				// gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				// gl.glEnd();

				Texture TexTemp3 = textures.get(iLastTexture);
				TexTemp3.enable(gl);
				TexTemp3.bind(gl);
				TextureCoords texCoords3 = TexTemp3.getImageTexCoords();

				gl.glBegin(GL2.GL_QUADS);
				gl.glTexCoord2d(texCoords3.left(), texCoords3.top() * fRatioLastTexture);
				gl.glVertex3f(0, 0, 0);
				gl.glTexCoord2d(texCoords3.left(), texCoords3.bottom());
				gl.glVertex3f(0, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords3.right(), texCoords3.bottom());
				gl.glVertex3f(fWidthLevel2, fHeight * fScalingLastTexture, 0);
				gl.glTexCoord2d(texCoords3.right(), texCoords3.top() * fRatioLastTexture);
				gl.glVertex3f(fWidthLevel2, 0, 0);
				gl.glEnd();

				TexTemp3.disable(gl);

			} else {
				throw new IllegalStateException(
						"Number of textures is bigger than 3 - something went wrong");
			}
		}

		gl.glPopName();
		gl.glPopAttrib();

	}

	private void renderSubTreeLevel2(GL2 gl) {
		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		if (tablePerspective.getRecordPerspective().getTree() != null) {
			gl.glTranslatef(renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS / 2, 0, 0);

			if (recordVA.getGroupList() != null)
				gl.glTranslatef(renderStyle.getWidthClusterVisualization(), 0, 0);

			float fHeightSubTree = viewFrustum.getHeight();

			glRecordDendrogramView.renderSubTreeFromIndexToIndex(gl, iFirstSampleLevel1,
					iLastSampleLevel1, iSamplesLevel2, GAP_BETWEEN_LEVELS / 2,
					fHeightSubTree);

			if (recordVA.getGroupList() != null)
				gl.glTranslatef(-renderStyle.getWidthClusterVisualization(), 0, 0);

			gl.glTranslatef(-(renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS / 2), 0,
					0);
		}
	}

	private void renderSubTreeLevel3(GL2 gl) {
		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();
		// render sub tree for level 3
		if (tablePerspective.getRecordPerspective().getTree() != null) {

			if (recordVA.getGroupList() != null)
				gl.glTranslatef(2 * renderStyle.getWidthClusterVisualization(), 0, 0);

			float fHeightSubTree = 0;
			if (dimensionDendrogramActive || dimensionDendrogramRenderCut)
				fHeightSubTree = viewFrustum.getHeight()
						- renderStyle.getHeightExperimentDendrogram();
			else
				fHeightSubTree = viewFrustum.getHeight();

			int from = iFirstSampleLevel1 + iFirstSampleLevel2;
			int to = iFirstSampleLevel1 + iLastSampleLevel2;

			glRecordDendrogramView.renderSubTreeFromIndexToIndex(gl, from, to,
					iSamplesPerHeatmap, GAP_BETWEEN_LEVELS / 2, fHeightSubTree);

			if (recordVA.getGroupList() != null)
				gl.glTranslatef(-2 * renderStyle.getWidthClusterVisualization(), 0, 0);

		}
	}

	/**
	 * Render marker in Texture for visualization of the currently (in stage 3)
	 * rendered part
	 * 
	 * @param gl
	 */
	private void renderMarkerLevel2(final GL2 gl) {

		// float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		float fWidthLevel2 = renderStyle.getWidthLevel2() * fScalingLevel2;
		// float fHeight = viewFrustum.getHeight();
		float fHeightSampleLevel2 = viewFrustum.getHeight() / iSamplesLevel2;

		Vec3f startpoint1, endpoint1, startpoint2, endpoint2;

		float fOffsetClusterVis = 0f;

		if (tablePerspective.getRecordPerspective().getVirtualArray().getGroupList() != null)
			fOffsetClusterVis += renderStyle.getWidthClusterVisualization();

		gl.glColor4f(1, 1, 0, 1);
		gl.glLineWidth(2f);
		gl.glBegin(GL2.GL_LINE_LOOP);
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

		if (dimensionDendrogramActive || dimensionDendrogramRenderCut)
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
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(fWidthLevel2, 0.0f, BUTTON_Z);
			gl.glVertex3f(fWidthLevel2, -0.4f, BUTTON_Z);
			gl.glEnd();

			float fSizeHeatmapArrow = renderStyle.getSizeHeatmapArrow();

			Texture tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_ARROW);
			tempTexture.enable(gl);
			tempTexture.bind(gl);

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glColor4f(1, 1, 1, 1);

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_INFOCUS_SELECTION, 1));
			if (bIsHeatmapInFocus) {
				gl.glBegin(GL2.GL_POLYGON);
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
				gl.glBegin(GL2.GL_POLYGON);
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

			tempTexture.disable(gl);
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
	private void renderSelectedElementsLevel2(GL2 gl) {

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();
		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();

		// float fFieldWith = viewFrustum.getWidth() / 4.0f * fAnimationScale;
		float fWidthLevel2 = renderStyle.getWidthLevel2() * fScalingLevel2;
		float fHeightSample = viewFrustum.getHeight() / iSamplesLevel2;
		float fExpWidth = fWidthLevel2 / dimensionVA.size();

		gl.glEnable(GL2.GL_LINE_STIPPLE);
		gl.glLineStipple(2, (short) 0xAAAA);

		gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
		java.util.Set<Integer> selectedSet = dimensionSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		int iColumnIndex = 0;
		for (int iTempLine : dimensionVA) {
			for (Integer iCurrentLine : selectedSet) {
				if (iTempLine == iCurrentLine) {
					gl.glBegin(GL2.GL_LINE_LOOP);
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
		selectedSet = dimensionSelectionManager.getElements(SelectionType.SELECTION);
		int iLineIndex = 0;
		for (int iTempLine : dimensionVA) {
			for (Integer iCurrentLine : selectedSet) {
				if (iTempLine == iCurrentLine) {
					gl.glBegin(GL2.GL_LINE_LOOP);
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

		gl.glDisable(GL2.GL_LINE_STIPPLE);

		java.util.Set<Integer> setMouseOverElements = recordSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);

		for (Integer mouseOverElement : setMouseOverElements) {

			int selectedElement = recordVA.indexOf(mouseOverElement.intValue());

			if (selectedElement >= iFirstSampleLevel1
					&& selectedElement <= iLastSampleLevel1) {

				gl.glLineWidth(2f);
				gl.glBegin(GL2.GL_LINE_LOOP);
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

		java.util.Set<Integer> setSelectedElements = recordSelectionManager
				.getElements(SelectionType.SELECTION);
		gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);

		for (Integer iSelectedElement : setSelectedElements) {

			int selectedElement = recordVA.indexOf(iSelectedElement.intValue());

			if (selectedElement >= iFirstSampleLevel1
					&& selectedElement <= iLastSampleLevel1) {

				gl.glLineWidth(2f);
				gl.glBegin(GL2.GL_LINE_LOOP);
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
	private void renderCursorLevel1(final GL2 gl) {

		float fWidthLevel1 = renderStyle.getWidthLevel1();
		float fSizeHeatmapArrow = renderStyle.getSizeHeatmapArrow();
		float fOffsetClusterVis = 0f;

		if (tablePerspective.getRecordPerspective().getVirtualArray().getGroupList() != null)
			fOffsetClusterVis += 0.1f;

		gl.glTranslatef(fWidthLevel1 + fOffsetClusterVis, 0, 0);

		gl.glColor4f(1, 1, 1, 1);

		Texture tempTexture = textureManager.getIconTexture(gl,
				EIconTextures.HEAT_MAP_ARROW);
		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		tempTexture.enable(gl);
		tempTexture.bind(gl);

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		// Polygon for iFirstElement-Cursor
		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HIER_HEAT_MAP_CURSOR_LEVEL1, 1));
		gl.glBegin(GL2.GL_POLYGON);
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
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPopName();

		// Polygon for iLastElement-Cursor
		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HIER_HEAT_MAP_CURSOR_LEVEL1, 2));
		gl.glBegin(GL2.GL_POLYGON);
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

		tempTexture.disable(gl);

		// gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// fill gap between cursor
		gl.glColor4fv(DRAGGING_CURSOR_COLOR, 0);
		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HIER_HEAT_MAP_BLOCK_CURSOR_LEVEL1, 1));
		gl.glBegin(GL2.GL_QUADS);
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
	private void renderCursorLevel2(final GL2 gl) {

		float fSizeHeatmapArrow = renderStyle.getSizeHeatmapArrow();

		gl.glColor4f(1f, 1, 1, 1f);

		gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);

		gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		Texture tempTexture = textureManager.getIconTexture(gl,
				EIconTextures.HEAT_MAP_ARROW);
		tempTexture.enable(gl);
		tempTexture.bind(gl);

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		// Polygon for iFirstElement-Cursor
		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HIER_HEAT_MAP_CURSOR_LEVEL2, 1));
		gl.glBegin(GL2.GL_POLYGON);
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
		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HIER_HEAT_MAP_CURSOR_LEVEL2, 2));
		gl.glBegin(GL2.GL_POLYGON);
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

		tempTexture.disable(gl);
		gl.glPopAttrib();
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// fill gap between cursor
		gl.glColor4fv(DRAGGING_CURSOR_COLOR, 0);
		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HIER_HEAT_MAP_BLOCK_CURSOR_LEVEL2, 1));
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorLastElementLevel2, BUTTON_Z);
		gl.glVertex3f(0.0f, fPosCursorLastElementLevel2, BUTTON_Z);
		gl.glVertex3f(0.0f, fPosCursorFirstElementLevel2, BUTTON_Z);
		gl.glVertex3f(fSizeHeatmapArrow, fPosCursorFirstElementLevel2, BUTTON_Z);
		gl.glEnd();
		gl.glPopName();

	}

	@Override
	public void display(GL2 gl) {
		// processEvents();

		if (isDisplayListDirty) {
			buildDisplayList(gl, displayListIndex);
			isDisplayListDirty = false;
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
				&& tablePerspective.getDimensionPerspective().getTree() == null) {
			handleDragDropGroupExperiments(gl);
			if (glMouseListener.wasMouseReleased()) {
				bDragDropExpGroup = false;
			}
		}

		// cluster dragging only allowed in case of partition based cluster
		// result
		if (bDragDropGeneGroup && tablePerspective.getRecordPerspective().getTree() == null) {
			handleDragDropGroupGenes(gl);
			if (glMouseListener.wasMouseReleased()) {
				bDragDropGeneGroup = false;
			}
		}

		// if (bSplitGroupExp && table.getClusteredTreeExps() == null) {
		// handleGroupSplitExperiments(gl);
		// if (glMouseListener.wasMouseReleased()) {
		// bSplitGroupExp = false;
		// }
		// }

		// if (bSplitGroupGene && table.getClusteredTreeGenes() == null) {
		// handleGroupSplitGenes(gl);
		// if (glMouseListener.wasMouseReleased()) {
		// bSplitGroupGene = false;
		// }
		// }

		gl.glCallList(displayListIndex);

		if (tablePerspective.getRecordPerspective().getVirtualArray().getGroupList() != null)
			glHeatMapView.setClusterVisualizationGenesActiveFlag(true);
		else
			glHeatMapView.setClusterVisualizationGenesActiveFlag(false);

		if (tablePerspective.getDimensionPerspective().getVirtualArray().getGroupList() != null)
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
				.getViewManager().getConnectedElementRepresentationManager();
		cerm.doViewRelatedTransformation(gl, selectionTransformer);

		if (!lazyMode)
			checkForHits(gl);

	}

	private void renderRemoteViewsLevel_1_2_3_Active(GL2 gl) {
		float fright = 0.0f;
		float top = viewFrustum.getTop();

		float fleftOffset = 0.1f + renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS
				+ renderStyle.getWidthLevel2() * fScalingLevel2 + GAP_BETWEEN_LEVELS;

		if (recordDendrogramActive || recordDendrogramRenderCut)
			fleftOffset += renderStyle.getWidthGeneDendrogram();

		if (tablePerspective.getRecordPerspective().getVirtualArray().getGroupList() != null)
			fleftOffset += 2 * renderStyle.getWidthClusterVisualization();

		if (!bIsHeatmapInFocus)
			fleftOffset -= 0.02f;

		fright = viewFrustum.getWidth() - fleftOffset;

		// gl.glTranslatef(fleftOffset, -0.2f, 0);

		// render embedded heat map
		if (dimensionDendrogramActive || dimensionDendrogramRenderCut)
			top -= renderStyle.getHeightExperimentDendrogram();

		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION,
				glHeatMapView.getID()));

		heatMapRemoteElement.getTransform().getTranslation().set(fleftOffset, 0, 0);
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

		// FIXME: the frustum should not be updated in every render step. a lot
		// of things are called then in the layout. all the spacings are
		// recalculated etc.
		ViewFrustum embeddedHeatMapFrustum = glHeatMapView.getViewFrustum();
		embeddedHeatMapFrustum.setLeft(0);
		embeddedHeatMapFrustum.setRight(viewFrustum.getRight() - translation.x());
		embeddedHeatMapFrustum.setTop(top - 0.2f);
		embeddedHeatMapFrustum.setBottom(0);
		glHeatMapView.setFrustum(embeddedHeatMapFrustum);

		glHeatMapView.displayRemote(gl);

		gl.glPopName();
		renderStyle.setWidthLevel3(glHeatMapView.getViewFrustum().getWidth() - 0.72f);

		// render embedded experiment dendrogram
		if (dimensionDendrogramActive || dimensionDendrogramRenderCut) {

			float fOffsety = viewFrustum.getTop() - 1.65f;// renderStyle.getHeightExperimentDendrogram();

			gl.glTranslatef(0f, fOffsety, 0f);
			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION,
					glDimensionDendrogramView.getID()));
			glDimensionDendrogramView.getViewFrustum().setTop(1.65f);
			glDimensionDendrogramView.getViewFrustum().setRight(
					renderStyle.getWidthLevel3());
			glDimensionDendrogramView.setRenderUntilCut(dimensionDendrogramRenderCut);

			if (bFirstStartExperimentDendrogram) {
				if (glDimensionDendrogramView.setInitialPositionOfCut()) {
					bFirstStartExperimentDendrogram = false;
				}
			}

			glDimensionDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -fOffsety, 0f);
		}

		gl.glTranslatef(-translation.x(), -translation.y(), 0);

		// render embedded gene dendrogram
		if (recordDendrogramActive || recordDendrogramRenderCut) {

			gl.glTranslatef(0f, 0.4f, 0f);
			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION,
					glRecordDendrogramView.getID()));
			glRecordDendrogramView.getViewFrustum().setTop(viewFrustum.getTop() - 0.6f);
			glRecordDendrogramView.getViewFrustum().setRight(1.7f);
			glRecordDendrogramView.setRenderUntilCut(recordDendrogramRenderCut);

			if (bFirstStartGeneDendrogram) {
				if (glRecordDendrogramView.setInitialPositionOfCut()) {
					bFirstStartGeneDendrogram = false;
				}
			}

			glRecordDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -0.4f, 0f);
		}

	}

	private void renderRemoteViewsLevel_2_3_Active(GL2 gl) {
		float fright = 0.0f;
		float ftop = viewFrustum.getTop() - 0.2f;

		float fleftOffset = 0.1f + renderStyle.getWidthLevel2() * fScalingLevel2
				+ GAP_BETWEEN_LEVELS;

		if (tablePerspective.getRecordPerspective().getVirtualArray().getGroupList() != null)
			fleftOffset += renderStyle.getWidthClusterVisualization();

		if (recordDendrogramActive || recordDendrogramRenderCut)
			fleftOffset += renderStyle.getWidthGeneDendrogram();

		if (!bIsHeatmapInFocus)
			fleftOffset -= 0.02;

		fright = viewFrustum.getWidth() - fleftOffset;

		gl.glTranslatef(fleftOffset, 0, 0);

		// render embedded heat map
		if (dimensionDendrogramActive || dimensionDendrogramRenderCut)
			ftop -= renderStyle.getHeightExperimentDendrogram();

		glHeatMapView.getViewFrustum().setTop(ftop);
		glHeatMapView.getViewFrustum().setRight(fright);
		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION,
				glHeatMapView.getID()));
		glHeatMapView.displayRemote(gl);
		gl.glPopName();

		renderStyle.setWidthLevel3(glHeatMapView.getViewFrustum().getWidth() - 0.72f);

		// render embedded experiment dendrogram
		if (dimensionDendrogramActive || dimensionDendrogramRenderCut) {
			float fOffsety = viewFrustum.getTop() - 1.65f;

			gl.glTranslatef(0f, fOffsety, 0f);
			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION,
					glDimensionDendrogramView.getID()));
			glDimensionDendrogramView.getViewFrustum().setTop(1.65f);
			glDimensionDendrogramView.getViewFrustum().setRight(
					renderStyle.getWidthLevel3());
			glDimensionDendrogramView.setRenderUntilCut(dimensionDendrogramRenderCut);

			if (bFirstStartExperimentDendrogram) {
				if (glDimensionDendrogramView.setInitialPositionOfCut()) {
					bFirstStartExperimentDendrogram = false;
				}
			}

			glDimensionDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -fOffsety, 0f);
		}

		gl.glTranslatef(-fleftOffset, 0, 0);

		// render embedded gene dendrogram
		if (recordDendrogramActive || recordDendrogramRenderCut) {

			gl.glTranslatef(0f, 0.4f, 0f);
			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION,
					glRecordDendrogramView.getID()));
			glRecordDendrogramView.getViewFrustum().setTop(viewFrustum.getTop() - 0.6f);
			glRecordDendrogramView.getViewFrustum().setRight(1.7f);
			glRecordDendrogramView.setRenderUntilCut(recordDendrogramRenderCut);

			if (bFirstStartGeneDendrogram) {
				if (glRecordDendrogramView.setInitialPositionOfCut()) {
					bFirstStartGeneDendrogram = false;
				}
			}

			glRecordDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -0.4f, 0f);
		}

	}

	private void renderRemoteViewsLevel_3_Active(GL2 gl) {
		float fright = 0;
		float ftop = viewFrustum.getTop() - 0.2f;

		float fleftOffset = 0.1f;

		if (recordDendrogramActive || recordDendrogramRenderCut) {
			fleftOffset += renderStyle.getWidthGeneDendrogram();
		}

		fright = viewFrustum.getWidth() - fleftOffset;

		gl.glTranslatef(fleftOffset, 0f, 0);

		// render embedded heat map
		if (dimensionDendrogramActive || dimensionDendrogramRenderCut)
			ftop -= renderStyle.getHeightExperimentDendrogram();

		glHeatMapView.getViewFrustum().setTop(ftop);
		glHeatMapView.getViewFrustum().setRight(fright);
		gl.glPushName(pickingManager.getPickingID(uniqueID,
				PickingType.HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION,
				glHeatMapView.getID()));
		glHeatMapView.displayRemote(gl);
		gl.glPopName();

		renderStyle.setWidthLevel3(glHeatMapView.getViewFrustum().getWidth() - 0.95f);

		// render embedded experiment dendrogram
		if (dimensionDendrogramActive || dimensionDendrogramRenderCut) {
			float fOffsety = viewFrustum.getTop() - 1.45f;// -
			// renderStyle.getHeightExperimentDendrogram();

			gl.glTranslatef(0f, fOffsety, 0f);
			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION,
					glDimensionDendrogramView.getID()));
			glDimensionDendrogramView.getViewFrustum().setTop(1.45f);
			glDimensionDendrogramView.getViewFrustum().setRight(
					renderStyle.getWidthLevel3());
			glDimensionDendrogramView.setRenderUntilCut(dimensionDendrogramRenderCut);

			if (bFirstStartExperimentDendrogram) {
				if (glDimensionDendrogramView.setInitialPositionOfCut()) {
					bFirstStartExperimentDendrogram = false;
				}
			}

			glDimensionDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -fOffsety, 0f);
		}

		gl.glTranslatef(-fleftOffset, 0, 0);

		// render embedded gene dendrogram
		if (recordDendrogramActive || recordDendrogramRenderCut) {

			gl.glTranslatef(0f, 0.4f, 0f);
			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION,
					glRecordDendrogramView.getID()));
			glRecordDendrogramView.getViewFrustum().setTop(ftop - 0.6f);
			glRecordDendrogramView.getViewFrustum().setRight(1.7f);
			glRecordDendrogramView.setRenderUntilCut(recordDendrogramRenderCut);

			if (bFirstStartGeneDendrogram) {
				if (glRecordDendrogramView.setInitialPositionOfCut()) {
					bFirstStartGeneDendrogram = false;
				}
			}

			glRecordDendrogramView.displayRemote(gl);
			gl.glPopName();
			gl.glTranslatef(0f, -0.4f, 0f);
		}

	}

	private void buildDisplayList(final GL2 gl, int iGLDisplayListIndex) {

		if (bRedrawTextures) {
			initTextures(gl);
			bRedrawTextures = false;
		}

		if (hasFrustumChanged) {
			glHeatMapView.setDisplayListDirty();
			glRecordDendrogramView.setRedrawDendrogram();
			glDimensionDendrogramView.setRedrawDendrogram();
			hasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL2.GL_COMPILE);

		// background color
		gl.glColor4fv(BACKGROUND_COLOR, 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, BACKGROUND_Z);
		gl.glVertex3f(viewFrustum.getRight(), 0, BACKGROUND_Z);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getHeight(), BACKGROUND_Z);
		gl.glVertex3f(0, viewFrustum.getHeight(), BACKGROUND_Z);
		gl.glEnd();

		// padding along borders
		viewFrustum.setTop(viewFrustum.getTop() - 0.6f);
		viewFrustum.setLeft(viewFrustum.getLeft() + 0.1f);
		gl.glTranslatef(0.1f, 0.4f, 0);

		if (tablePerspective.getRecordPerspective().getTree() != null)
			bRenderDendrogramBackgroundWhite = true;
		else
			bRenderDendrogramBackgroundWhite = false;

		updateLevels();

		if (recordDendrogramActive || recordDendrogramRenderCut)
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

		if (recordDendrogramActive || recordDendrogramRenderCut)
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

		// RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
		// .getVirtualArray();

		// float fleftOffset = 0;
		//
		// if (bSkipLevel1 == false && bSkipLevel2 == false) {
		// fleftOffset += renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS
		// + renderStyle.getWidthLevel2() * fScalingLevel2 + GAP_BETWEEN_LEVELS;
		// }
		// if (bSkipLevel1 == true && bSkipLevel2 == false) {
		// fleftOffset += renderStyle.getWidthLevel2() * fScalingLevel2
		// + GAP_BETWEEN_LEVELS;
		// }
		//
		// if (recordDendrogramActive || recordDendrogramRenderCut)
		// fleftOffset += renderStyle.getWidthGeneDendrogram();
		//
		// if (recordVA.getGroupList() != null && bSkipLevel1 == false
		// && bSkipLevel2 == false)
		// fleftOffset += 2 * renderStyle.getWidthClusterVisualization();
		//
		// if (recordVA.getGroupList() != null && bSkipLevel1 == true
		// && bSkipLevel2 == false)
		// fleftOffset += renderStyle.getWidthClusterVisualization();

		// float fWidthLevel3 = viewFrustum.getWidth() - fleftOffset - 0.95f;

		// renderStyle.setWidthLevel3(fWidthLevel3);
	}

	private void renderViewsLevel_1_2_3_Active(GL2 gl) {

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

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
		if (recordVA.getGroupList() != null)
			gl.glTranslatef(renderStyle.getWidthClusterVisualization(), 0, 0);

		if (pickingPointLevel2 != null)
			handleTexturePickingLevel2(gl);

		renderLevel2(gl);
		renderMarkerLevel2(gl);
		renderSelectedElementsLevel2(gl);
		renderClassAssignmentsGenesLevel2(gl);
		renderClassAssignmentsExperimentsLevel2(gl);

		gl.glTranslatef(-(renderStyle.getWidthLevel1() + GAP_BETWEEN_LEVELS), 0, 0);
		if (recordVA.getGroupList() != null)
			gl.glTranslatef(-renderStyle.getWidthClusterVisualization(), 0, 0);

		gl.glTranslatef(
				0.3f + renderStyle.getWidthLevel1() + renderStyle.getWidthLevel2()
						* fScalingLevel2 + GAP_BETWEEN_LEVELS, 0, 0);
		renderSubTreeLevel3(gl);
		gl.glTranslatef(
				-(0.3f + renderStyle.getWidthLevel1() + renderStyle.getWidthLevel2()
						* fScalingLevel2 + GAP_BETWEEN_LEVELS), 0, 0);

		if (recordVA.getGroupList() != null)
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

		if (recordVA.getGroupList() != null)
			gl.glTranslatef(-2 * renderStyle.getWidthClusterVisualization(), 0, 0);

	}

	private void renderViewsLevel_2_3_Active(GL2 gl) {

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

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

		if (recordVA.getGroupList() != null)
			gl.glTranslatef(renderStyle.getWidthClusterVisualization(), 0, 0);
		gl.glTranslatef(renderStyle.getWidthLevel2() * fScalingLevel2, 0, 0);

		renderCursorLevel2(gl);

		gl.glTranslatef(GAP_BETWEEN_LEVELS, 0, 0);
		renderClassAssignmentsExperimentsLevel3(gl);
		renderClassAssignmentsGenesLevel3(gl);
		renderExperimentDendrogramBackground(gl);
		gl.glTranslatef(-GAP_BETWEEN_LEVELS, 0, 0);

		gl.glTranslatef(-renderStyle.getWidthLevel2() * fScalingLevel2, 0, 0);
		if (recordVA.getGroupList() != null)
			gl.glTranslatef(-renderStyle.getWidthClusterVisualization(), 0, 0);

	}

	private void renderViewsLevel_3_Active(GL2 gl) {

		renderClassAssignmentsGenesLevel3(gl);
		renderClassAssignmentsExperimentsLevel3(gl);
		renderGeneDendrogramBackground(gl);
		renderExperimentDendrogramBackground(gl);

	}

	private void renderGeneDendrogramBackground(GL2 gl) {

		float fHeight = viewFrustum.getHeight();
		float fSizeHeatmapArrow = renderStyle.getSizeHeatmapArrow();
		float fWidthGeneDendrogram = renderStyle.getWidthGeneDendrogram();

		float fWidthCurveTexture = GAP_BETWEEN_LEVELS / 4;

		if (bSkipLevel1 && bSkipLevel2
				&& (dimensionDendrogramActive || dimensionDendrogramRenderCut)) {
			fHeight -= renderStyle.getHeightExperimentDendrogram();
		}

		if (recordDendrogramActive || recordDendrogramRenderCut) {

			gl.glColor4f(1, 1, 1, 1);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture, fHeight, 0);
			gl.glVertex3f(0, fHeight, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture, 0, 0);
			gl.glEnd();

			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture, fHeight
					- fWidthCurveTexture, 0);
			gl.glVertex3f(-fWidthGeneDendrogram, fHeight - fWidthCurveTexture, 0);
			gl.glVertex3f(-fWidthGeneDendrogram, +fWidthCurveTexture, 0);
			gl.glVertex3f(-fWidthGeneDendrogram + fWidthCurveTexture,
					+fWidthCurveTexture, 0);
			gl.glEnd();

			gl.glLineWidth(1f);
			gl.glColor4f(1, 0, 0, 1);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(-fWidthGeneDendrogram, fHeight, 0);
			gl.glVertex3f(-fWidthGeneDendrogram, -0.4f, 0);
			gl.glEnd();

			gl.glColor4f(1, 1, 1, 1);
			gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

			Texture textureMaskNeg = textureManager.getIconTexture(gl,
					EIconTextures.NAVIGATION_MASK_CURVE_NEG_WHITE);
			textureMaskNeg.enable(gl);
			textureMaskNeg.bind(gl);

			TextureCoords texCoordsMaskNeg = textureMaskNeg.getImageTexCoords();

			gl.glBegin(GL2.GL_POLYGON);
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

			gl.glBegin(GL2.GL_POLYGON);
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
			textureMaskNeg.disable(gl);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			gl.glPopAttrib();
		}

		if (tablePerspective.getRecordPerspective().getTree() != null) {

			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

			Texture tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_ARROW);
			tempTexture.enable(gl);
			tempTexture.bind(gl);

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_ACTIVATE_HORIZONTAL_DENDROGRAM, 1));
			if (recordDendrogramActive) {
				gl.glBegin(GL2.GL_POLYGON);
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
				gl.glBegin(GL2.GL_POLYGON);
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

			tempTexture.disable(gl);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	private void renderExperimentDendrogramBackground(GL2 gl) {

		float fHeight = viewFrustum.getHeight();
		float fWidthLevel3 = renderStyle.getWidthLevel3();
		float fSizeHeatmapArrow = renderStyle.getSizeHeatmapArrow();
		float fHeightExperimentDendrogram = renderStyle.getHeightExperimentDendrogram();

		float fWidthCurveTexture = GAP_BETWEEN_LEVELS / 4;

		if (dimensionDendrogramActive || dimensionDendrogramRenderCut) {

			gl.glColor4f(1, 1, 1, 1);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(0, fHeight - fWidthCurveTexture, 0);
			gl.glVertex3f(fWidthLevel3, fHeight - fWidthCurveTexture, 0);
			gl.glVertex3f(fWidthLevel3, fHeight - fHeightExperimentDendrogram, 0);
			gl.glVertex3f(0, fHeight - fHeightExperimentDendrogram, 0);
			gl.glEnd();

			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(fWidthCurveTexture, fHeight, 0);
			gl.glVertex3f(fWidthLevel3 - fWidthCurveTexture, fHeight, 0);
			gl.glVertex3f(fWidthLevel3 - fWidthCurveTexture,
					fHeight - fWidthCurveTexture, 0);
			gl.glVertex3f(fWidthCurveTexture, fHeight - fWidthCurveTexture, 0);
			gl.glEnd();

			gl.glLineWidth(1f);
			gl.glColor4f(1, 0, 0, 1);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(0, fHeight, 0);
			gl.glVertex3f(fWidthLevel3 + 0.4f, fHeight, 0);
			gl.glEnd();

			gl.glColor4f(1, 1, 1, 1);
			gl.glPushAttrib(GL2.GL_CURRENT_BIT | GL2.GL_LINE_BIT);
			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

			Texture textureMaskNeg = textureManager.getIconTexture(gl,
					EIconTextures.NAVIGATION_MASK_CURVE_NEG_WHITE);
			textureMaskNeg.enable(gl);
			textureMaskNeg.bind(gl);

			TextureCoords texCoordsMaskNeg = textureMaskNeg.getImageTexCoords();

			gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(0, fHeight - fWidthCurveTexture, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.bottom());
			gl.glVertex3f(fWidthCurveTexture, fHeight - fWidthCurveTexture, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.right(), texCoordsMaskNeg.top());
			gl.glVertex3f(fWidthCurveTexture, fHeight, 0);
			gl.glTexCoord2f(texCoordsMaskNeg.left(), texCoordsMaskNeg.top());
			gl.glVertex3f(0, fHeight, 0);
			gl.glEnd();

			gl.glBegin(GL2.GL_POLYGON);
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
			textureMaskNeg.disable(gl);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			gl.glPopAttrib();
		}

		if (!tablePerspective.getDimensionPerspective().isTreeDefaultTree()) {

			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);

			Texture tempTexture = textureManager.getIconTexture(gl,
					EIconTextures.HEAT_MAP_ARROW);
			tempTexture.enable(gl);
			tempTexture.bind(gl);

			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glPushName(pickingManager.getPickingID(uniqueID,
					PickingType.HIER_HEAT_MAP_ACTIVATE_STORAGE_DENDROGRAM, 1));
			if (dimensionDendrogramActive) {
				gl.glBegin(GL2.GL_POLYGON);
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
				gl.glBegin(GL2.GL_POLYGON);
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

			tempTexture.disable(gl);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
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

		// every time we change the window of the embedded heat map we need to
		// remove the previously used ids
		connectedElementRepresentationManager.clearByView(glHeatMapView.getID());
		connectedElementRepresentationManager.clearByView(this.getID());

		int vaIndex = 0;
		int recordID = 0;

		PerspectiveInitializationData data = new PerspectiveInitializationData();

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		ArrayList<Integer> embeddedRecords = new ArrayList<Integer>();

		for (int count = 0; count < iSamplesPerHeatmap; count++) {
			vaIndex = offset + count;
			if (vaIndex < recordVA.size()) {
				recordID = recordVA.get(vaIndex);
				embeddedRecords.add(recordID);
			}

		}
		data.setData(embeddedRecords);
		glHeatMapView.getTablePerspective().getRecordPerspective().init(data);
		glHeatMapView.handleRecordVAUpdate(glHeatMapView.getTablePerspective()
				.getRecordPerspective().getPerspectiveID());

	}

	@Override
	public String toString() {
		return "Heat map for " + dataDomain;
	}

	/**
	 * Determine selected element in stage 1 (overview bar)
	 * 
	 * @param gl
	 */
	private void handleTexturePickingLevel1(GL2 gl) {

		int iNumberSample = numberOfRecords;
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
	private void handleTexturePickingLevel2(GL2 gl) {

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
	private void handleDragDropGroupExperiments(final GL2 gl) {

		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];

		int iTargetIdx = 0;

		int iNrSamples = dimensionVA.size();

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

		DimensionGroupList groupList = dimensionVA.getGroupList();

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		int iNrElementsInGroup = groupList.get(iExpGroupToDrag).getSize();
		float currentWidth = fWidthSample * iNrElementsInGroup;
		float fHeight = viewFrustum.getHeight();

		if (fArTargetWorldCoordinates[0] > fleftOffset
				&& fArTargetWorldCoordinates[0] < fleftOffset
						+ renderStyle.getWidthLevel3()) {
			gl.glBegin(GL2.GL_QUADS);
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
			if (currentElement < (iElemOffset + currentGroup.getSize())) {
				iTargetIdx = cnt;
				if (iExpGroupToDrag < iTargetIdx)
					iElemOffset += currentGroup.getSize();
				break;
			}
			cnt++;
			iElemOffset += currentGroup.getSize();
		}

		float fPosDropMarker = fleftOffset + fWidthSample * iElemOffset;

		gl.glLineWidth(6f);
		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(fPosDropMarker, fHeight, 0);
		gl.glVertex3f(fPosDropMarker, fHeight - 0.2f, 0);
		gl.glEnd();

		if (glMouseListener.wasMouseReleased()) {

			if (groupList.move(dimensionVA, iExpGroupToDrag, iTargetIdx) == false)
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
	private void handleDragDropGroupGenes(final GL2 gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];

		int iTargetIdx = 0;

		int iNumberSample = numberOfRecords;
		float fOffsety;
		int currentElement;
		float fHeight = viewFrustum.getHeight() - 0.2f;

		float fHeightSample = (fHeight - 0.4f) / iNumberSample;

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		RecordGroupList groupList = recordVA.getGroupList();

		if (groupList == null) {
			System.out.println("No group assignment available!");
			return;
		}

		int iNrElementsInGroup = groupList.get(iGeneGroupToDrag).getSize();
		float currentHeight = fHeightSample * iNrElementsInGroup;

		gl.glBegin(GL2.GL_QUADS);
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
			if (currentElement < (iElemOffset + currentGroup.getSize())) {
				iTargetIdx = cnt;
				if (iGeneGroupToDrag < iTargetIdx)
					iElemOffset += currentGroup.getSize();
				break;
			}
			cnt++;
			iElemOffset += currentGroup.getSize();
		}

		float fPosDropMarker = fHeight - (fHeightSample * iElemOffset);

		gl.glLineWidth(6f);
		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex3f(0, fPosDropMarker, 0);
		gl.glVertex3f(0.2f, fPosDropMarker, 0);
		gl.glEnd();

		if (glMouseListener.wasMouseReleased()) {

			if (groupList.move(recordVA, iGeneGroupToDrag, iTargetIdx) == false)
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
	private void handleGroupSplitGenes(final GL2 gl) {
		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		float[] fArDraggedPoint = new float[3];

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		if (glMouseListener.wasMouseReleased()) {
			// bSplitGroupGene = false;

			fArDraggedPoint = GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, DraggingPoint.x,
							DraggingPoint.y);

			float fYPosDrag = fArDraggedPoint[1] - 0.4f;
			float fYPosRelease = fArTargetWorldCoordinates[1] - 0.4f;

			float fHeight = viewFrustum.getHeight() - 0.6f;
			int iNrSamples = recordVA.size();
			float fHeightSample = fHeight / iNrSamples;

			int iFirstSample = iNrSamples
					- (int) Math.floor((double) fYPosDrag / fHeightSample);
			int iLastSample = iNrSamples
					- (int) Math.ceil((double) fYPosRelease / fHeightSample);

			if (recordVA.getGroupList().split(iGroupToSplit, iFirstSample, iLastSample) == false)
				System.out.println("Operation not allowed!!");
		}
	}

	/**
	 * Handles the dragging cursor for experiments groups
	 * 
	 * @param gl
	 */
	@SuppressWarnings("unused")
	private void handleGroupSplitExperiments(final GL2 gl) {
		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		float[] fArDraggedPoint = new float[3];

		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();

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
			int iNrSamples = dimensionVA.size();
			float fWidthSample = fWidth / iNrSamples;

			int iFirstSample = (int) Math.floor((double) fXPosDrag / fWidthSample);
			int iLastSample = (int) Math.ceil((double) fXPosRelease / fWidthSample);

			if (dimensionVA.getGroupList()
					.split(iGroupToSplit, iLastSample, iFirstSample) == false)
				System.out.println("Operation not allowed!!");
		}
	}

	/**
	 * Function used for updating position of block (block of elements rendered
	 * in level 2) in case of dragging
	 * 
	 * @param gl
	 */
	private void handleBlockDraggingLevel1(final GL2 gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / numberOfRecords;
		float fYPosMouse = fArTargetWorldCoordinates[1] - 0.4f;

		iselElement = (int) Math.floor((double) (fTextureHeight - fYPosMouse) / fStep);
		if (iSamplesLevel2 % 2 == 0) {
			if ((iselElement - (int) Math.floor((double) iSamplesLevel2 / 2) + 1) >= 0
					&& (iselElement + (int) Math.floor((double) iSamplesLevel2 / 2)) < numberOfRecords) {
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
					&& (iselElement + (int) Math.floor((double) iSamplesLevel2 / 2)) < numberOfRecords) {
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
	private void handleBlockDraggingLevel2(final GL2 gl) {

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
	private void handleCursorDraggingLevel1(final GL2 gl) {

		Point currentPoint = glMouseListener.getPickedPoint();
		float[] fArTargetWorldCoordinates = new float[3];
		int iselElement;
		int iNrSamples;

		fArTargetWorldCoordinates = GLCoordinateUtils
				.convertWindowCoordinatesToWorldCoordinates(gl, currentPoint.x,
						currentPoint.y);

		float fTextureHeight = viewFrustum.getHeight() - 0.6f;
		float fStep = fTextureHeight / numberOfRecords;
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
	private void handleCursorDraggingLevel2(final GL2 gl) {
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
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int pickingID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}

		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();
		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		switch (pickingType) {

		// handling the groups/clusters of genes
		case HEAT_MAP_RECORD_GROUP:
			switch (pickingMode) {
			case RIGHT_CLICKED:

				boolean bEnableInterchange = false;
				boolean bEnableMerge = false;
				boolean bEnableExport = true;
				int iNrSelectedGroups = 0;

				RecordGroupList tempGroupList = recordVA.getGroupList();

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

				GeneGroupContextMenuItemContainer groupContextMenuItemContainer = new GeneGroupContextMenuItemContainer();
				// groupContextMenuItemContainer.initContextMenu(true,
				// bEnableMerge,
				// bEnableInterchange, bEnableExport);
				groupContextMenuItemContainer.setDataDomain(dataDomain);
				groupContextMenuItemContainer.setData(recordIDType,
						recordVA.getIDsOfGroup(pickingID));
				contextMenuCreator
						.addContextMenuItemContainer(groupContextMenuItemContainer);

				if (recordVA.getGroupList().get(pickingID).getSelectionType() == SelectionType.SELECTION)
					break;
				// else we want to go to clicked as well
			case CLICKED:

				// Reset group states before selecting the newly selected
				for (Group group : recordVA.getGroupList())
					group.setSelectionType(SelectionType.NORMAL);

				recordVA.getGroupList().get(pickingID).togglSelectionType();
				deactivateAllDraggingCursor();
				bActivateDraggingGenes = true;

				// ArrayList<Integer> temp =
				// recordVA.getGeneIdsOfGroup( externalID);
				// for (int i = 0; i < temp.size(); i++) {
				// System.out.println(idMappingManager.getID(EIDType.EXPRESSION_INDEX,
				// EIDType.GENE_SYMBOL, temp.get(i)));
				// }

				// ArrayList<Float> representatives =
				// recordVA.getGroupList().determineRepresentativeElement(set,
				// recordVA,
				// dimensionVA, externalID, true);

				// set node in tree selected
				// if
				// (recordVA.getGroupList().get(externalID).getClusterNode()
				// != null) {
				// recordVA.getGroupList().get(externalID).getClusterNode().togglSelectionType();
				// }

				// System.out.println(recordVA.getGroupList().get(externalID).getIdxExample());
				// System.out.println(idMappingManager.getID(EIDType.EXPRESSION_INDEX,
				// EIDType.GENE_SYMBOL,
				// recordVA.getGroupList().get(externalID).getIdxExample()));

				setDisplayListDirty();
				break;

			case DRAGGED:
				if (bActivateDraggingGenes == false)
					return;
				// drag&drop for groups
				if (bDragDropGeneGroup == false) {
					bDragDropGeneGroup = true;
					bDragDropExpGroup = false;
					iGeneGroupToDrag = pickingID;
				}

				// group splitting
				// if (bSplitGroupGene == false) {
				// bSplitGroupGene = true;
				// bSplitGroupExp = false;
				// iGroupToSplit = externalID;
				// DraggingPoint = pick.getPickedPoint();
				// }
				setDisplayListDirty();
				break;

			case MOUSE_OVER:
				// System.out.print("genes group " + externalID);
				// System.out.print(" number elements in group: ");
				// System.out.println(recordVA.getGroupList().get(externalID)
				// .getNrElements());
				// setDisplayListDirty();
				break;
			}
			break;

		// handling the groups/clusters of experiments
		case HIER_HEAT_MAP_DIMENSION_GROUP:
			switch (pickingMode) {
			case RIGHT_CLICKED:

				boolean bEnableInterchange = false;
				boolean bEnableMerge = false;
				boolean bEnableExport = true;
				int iNrSelectedGroups = 0;

				DimensionGroupList tempGroupList = dimensionVA.getGroupList();

				for (Group group : tempGroupList) {
					if (group.getSelectionType() == SelectionType.SELECTION)
						iNrSelectedGroups++;
				}

				if (iNrSelectedGroups >= 2) {

					bEnableMerge = true;

					if (iNrSelectedGroups == 2)
						bEnableInterchange = true;
				}

				// FIXME CONTEXT MENU
				// GroupContextMenuItemContainer groupContextMenuItemContainer =
				// new GroupContextMenuItemContainer();
				// groupContextMenuItemContainer.initContextMenu(false,
				// bEnableMerge,
				// bEnableInterchange, bEnableExport);
				//
				// contextMenu.addItemContanier(groupContextMenuItemContainer);

				if (dimensionVA.getGroupList().get(pickingID).getSelectionType() == SelectionType.SELECTION)
					break;
				// else we want to do clicked here as well
			case CLICKED:
				dimensionVA.getGroupList().get(pickingID).togglSelectionType();
				deactivateAllDraggingCursor();
				bActivateDraggingExperiments = true;

				// ArrayList<Integer> temp =
				// dimensionVA.getGeneIdsOfGroup( externalID);
				// for (int i = 0; i < temp.size(); i++) {
				// System.out.println(table.get(temp.get(i)).getLabel());
				// }

				// ArrayList<Float> representatives =
				// recordVA.getGroupList().determineRepresentativeElement(set,
				// recordVA,
				// dimensionVA, externalID, false);

				// set node in tree selected
				// if
				// (dimensionVA.getGroupList().get(externalID).getClusterNode()
				// != null) {
				// dimensionVA.getGroupList().get(externalID).getClusterNode().togglSelectionType();
				// }

				// System.out.println(dimensionVA.getGroupList().get(externalID).getIdxExample());
				// System.out.println(table.get(dimensionVA.getGroupList().get(externalID).getIdxExample())
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
					iExpGroupToDrag = pickingID;
				}

				// group splitting
				// if (bSplitGroupExp == false) {
				// bSplitGroupExp = true;
				// bSplitGroupGene = false;
				// iGroupToSplit = externalID;
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
				glRecordDendrogramView.setDisplayListDirty();
				glDimensionDendrogramView.setRedrawDendrogram();
				setDisplayListDirty();
				break;
			}
			break;

		// handle click on button for setting experiment dendrogram active
		case HIER_HEAT_MAP_ACTIVATE_STORAGE_DENDROGRAM:
			switch (pickingMode) {

			case CLICKED:
				dimensionDendrogramActive = dimensionDendrogramActive == true ? false
						: true;

				if (dimensionDendrogramActive == true) {
					float highDendro = glDimensionDendrogramView.getViewFrustum()
							.getHeight();

					if (highDendro > 0.5 && highDendro <= 1.5f)
						renderStyle.setHeightExperimentDendrogram(highDendro);

					dimensionDendrogramRenderCut = false;

				} else {
					float fPosCut = glDimensionDendrogramView.getPositionOfCut();
					float highDendro = glDimensionDendrogramView.getViewFrustum()
							.getHeight();
					renderStyle
							.setHeightExperimentDendrogram(highDendro - fPosCut + 0.1f);
					dimensionDendrogramRenderCut = true;
				}

				glDimensionDendrogramView.setRenderUntilCut(recordDendrogramRenderCut);
				glDimensionDendrogramView.setDisplayListDirty();
				glRecordDendrogramView.setRedrawDendrogram();
				glHeatMapView.setDisplayListDirty();
				setDisplayListDirty();
				break;
			}
			break;

		// handle click on button for setting gene dendrogram active
		case HIER_HEAT_MAP_ACTIVATE_HORIZONTAL_DENDROGRAM:
			switch (pickingMode) {

			case CLICKED:
				recordDendrogramActive = recordDendrogramActive == true ? false : true;

				if (recordDendrogramActive == true) {
					float widthDendro = glRecordDendrogramView.getViewFrustum()
							.getWidth();

					if (widthDendro > 0.5 && widthDendro <= 1.7f)
						renderStyle.setWidthGeneDendrogram(widthDendro - 0.1f);

					recordDendrogramRenderCut = false;
				} else {
					float temp = glRecordDendrogramView.getPositionOfCut();
					renderStyle.setWidthGeneDendrogram(temp);
					recordDendrogramRenderCut = true;
				}

				glRecordDendrogramView.setRenderUntilCut(recordDendrogramRenderCut);
				glRecordDendrogramView.setDisplayListDirty();
				glDimensionDendrogramView.setRedrawDendrogram();
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
				iDraggedCursorLevel1 = pickingID;
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
				iDraggedCursorLevel1 = pickingID;
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
				iDraggedCursorLevel2 = pickingID;
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
				iDraggedCursorLevel2 = pickingID;
				setDisplayListDirty();
				break;
			}
			break;

		// handle click on level 1 (overview bar)
		case HEAT_MAP_TEXTURE_SELECTION:
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

		// // handle click on level 3 (EHM)
		// case HIER_HEAT_MAP_EMBEDDED_HEATMAP_SELECTION:
		// switch (pickingMode) {
		// case RIGHT_CLICKED:
		//
		// break;
		// }
		// break;
		//
		// // handle click on gene dendrogram
		// case HIER_HEAT_MAP_GENE_DENDROGRAM_SELECTION:
		// switch (pickingMode) {
		// case RIGHT_CLICKED:
		//
		// break;
		// }
		// break;
		//
		// // handle click on gene dendrogram
		// case HIER_HEAT_MAP_EXPERIMENT_DENDROGRAM_SELECTION:
		// switch (pickingMode) {
		// case RIGHT_CLICKED:
		//
		// break;
		// }
		// break;
		}
		// setDisplayListDirty();
	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int iDimensionIndex) {

		return null;
	}

	// @Override
	// public void clearAllSelections() {
	//
	// recordSelectionManager.clearSelections();
	// dimensionSelectionManager.clearSelections();
	//
	// if (bSkipLevel1 == false)
	// initPosCursorLevel1();
	// bRedrawTextures = true;
	// setDisplayListDirty();
	//
	// glHeatMapView.setDisplayListDirty();
	// glRecordDendrogramView.setDisplayListDirty();
	// glDimensionDendrogramView.setDisplayListDirty();
	//
	// // group/cluster selections
	// if (dimensionVA.getGroupList() != null) {
	// DimensionGroupList groupList = dimensionVA.getGroupList();
	//
	// for (Group group : groupList)
	// group.setSelectionType(SelectionType.NORMAL);
	// }
	// if (recordVA.getGroupList() != null) {
	// RecordGroupList groupList = recordVA.getGroupList();
	//
	// for (Group group : groupList)
	// group.setSelectionType(SelectionType.NORMAL);
	// }
	// }

	@SuppressWarnings("unused")
	private void activateGroupHandling() {
		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();
		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();

		if (recordVA.getGroupList() == null) {
			RecordGroupList groupList = new RecordGroupList();
			Group group = new Group(recordVA.size());
			groupList.append(group);
			recordVA.setGroupList(groupList);
		}

		if (dimensionVA.getGroupList() == null) {
			DimensionGroupList groupList = new DimensionGroupList();
			Group group = new Group(dimensionVA.size());
			groupList.append(group);
			dimensionVA.setGroupList(groupList);
		}

		setDisplayListDirty();

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

		// Check if record tree is available and is deeper than three (in this
		// case we have a hierarchical clustering)
		if (tablePerspective.getRecordPerspective().getTree() != null
				&& tablePerspective.getRecordPerspective().getTree().getDepth() > 3) {
			recordDendrogramActive = true;
			recordDendrogramRenderCut = false;
			bFirstStartGeneDendrogram = true;
			renderStyle.setWidthGeneDendrogram(1.6f);
		} else {
			recordDendrogramActive = false;
			recordDendrogramRenderCut = false;
		}

		if (!tablePerspective.getDimensionPerspective().isTreeDefaultTree()) {
			dimensionDendrogramActive = true;
			dimensionDendrogramRenderCut = false;
			bFirstStartExperimentDendrogram = true;
			renderStyle.setHeightExperimentDendrogram(1.45f);
		} else {
			dimensionDendrogramActive = false;
			dimensionDendrogramRenderCut = false;
		}

		if (bSkipLevel2 == false)
			bRedrawTextures = true;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHierarchicalHeatMapView serializedForm = new SerializedHierarchicalHeatMapView(
				this);
		return serializedForm;
	}

	@Override
	public void handleInterchangeContentGroups() {
		Tree<ClusterNode> tree = null;

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();
		tree = tablePerspective.getRecordPerspective().getTree();
		RecordGroupList groupList = recordVA.getGroupList();

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
			recordDendrogramActive = false;
			recordDendrogramRenderCut = false;
			// table.getRecordPerspective(recordPerspectiveID).setTree(null);

		}

		// interchange
		if (groupList.interchange(recordVA, selGroups.get(0), selGroups.get(1)) == false) {
			System.out.println("Problem during interchange!!!");
			return;
		}

		bRedrawTextures = true;

		setDisplayListDirty();

	}

	private void warn() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
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

		contentGroupExportingListener = new RecordGroupExportingListener();
		contentGroupExportingListener.setHandler(this);
		eventPublisher.addListener(ExportContentGroupsEvent.class,
				contentGroupExportingListener);

		dimensionGroupExportingListener = new DimensionGroupExportingListener();
		dimensionGroupExportingListener.setHandler(this);
		eventPublisher.addListener(ExportDimensionGroupsEvent.class,
				dimensionGroupExportingListener);

		contentGroupInterchangingListener = new RecordGroupInterChangingActionListener();
		contentGroupInterchangingListener.setHandler(this);
		eventPublisher.addListener(InterchangeContentGroupsEvent.class,
				contentGroupInterchangingListener);

		dimensionGroupInterchangingListener = new DimensionGroupInterChangingActionListener();
		dimensionGroupInterchangingListener.setHandler(this);
		eventPublisher.addListener(InterchangeDimensionGroupsEvent.class,
				dimensionGroupInterchangingListener);

		contentGroupMergingListener = new RecordGroupMergingActionListener();
		contentGroupMergingListener.setHandler(this);
		eventPublisher.addListener(MergeContentGroupsEvent.class,
				contentGroupMergingListener);

		dimensionGroupMergingListener = new DimensionGroupMergingActionListener();
		dimensionGroupMergingListener.setHandler(this);
		eventPublisher.addListener(MergeDimensionGroupsEvent.class,
				dimensionGroupMergingListener);

		updateViewListener = new UpdateColorMappingListener();
		updateViewListener.setHandler(this);
		eventPublisher.addListener(UpdateColorMappingEvent.class, updateViewListener);

		clusterNodeMouseOverListener = new ClusterNodeSelectionListener();
		clusterNodeMouseOverListener.setHandler(this);
		eventPublisher.addListener(ClusterNodeSelectionEvent.class,
				clusterNodeMouseOverListener);

		newGroupInfoActionListener = new NewContentGroupInfoActionListener();
		newGroupInfoActionListener.setHandler(this);
		eventPublisher.addListener(NewRecordGroupInfoEvent.class,
				newGroupInfoActionListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (contentGroupExportingListener != null) {
			eventPublisher.removeListener(contentGroupExportingListener);
			contentGroupExportingListener = null;
		}
		if (dimensionGroupExportingListener != null) {
			eventPublisher.removeListener(dimensionGroupExportingListener);
			dimensionGroupExportingListener = null;
		}
		if (contentGroupInterchangingListener != null) {
			eventPublisher.removeListener(contentGroupInterchangingListener);
			contentGroupInterchangingListener = null;
		}
		if (dimensionGroupInterchangingListener != null) {
			eventPublisher.removeListener(dimensionGroupInterchangingListener);
			dimensionGroupInterchangingListener = null;
		}
		if (contentGroupMergingListener != null) {
			eventPublisher.removeListener(contentGroupMergingListener);
			contentGroupMergingListener = null;
		}
		if (dimensionGroupMergingListener != null) {
			eventPublisher.removeListener(dimensionGroupMergingListener);
			dimensionGroupMergingListener = null;
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
	public void updateColorMapping() {
		bRedrawTextures = true;
		bFirstStartExperimentDendrogram = true;
		bFirstStartGeneDendrogram = true;

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

		int iNrElementsToShift = (int) Math.floor(Math.sqrt(numberOfRecords));

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
			if (iLastSampleLevel1 < numberOfRecords - 1 - iNrElementsToShift) {
				iFirstSampleLevel1 += iNrElementsToShift;
				iLastSampleLevel1 += iNrElementsToShift;
				setDisplayListDirty();
			} else if (iLastSampleLevel1 < numberOfRecords - 1) {
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
			if (tablePerspective.getRecordPerspective().getTree() == null)
				return;
			recordDendrogramActive = recordDendrogramActive == true ? false : true;

			if (recordDendrogramActive == true) {
				float widthDendro = glRecordDendrogramView.getViewFrustum().getWidth();

				if (widthDendro > 0.5f && widthDendro <= 1.7f)
					renderStyle.setWidthGeneDendrogram(widthDendro - 0.1f);

				recordDendrogramRenderCut = false;
			} else {
				float temp = glRecordDendrogramView.getPositionOfCut();
				renderStyle.setWidthGeneDendrogram(temp);
				recordDendrogramRenderCut = true;
			}

			glDimensionDendrogramView.setRedrawDendrogram();

			glRecordDendrogramView.setRenderUntilCut(recordDendrogramRenderCut);
			glRecordDendrogramView.setRedrawDendrogram();

			glHeatMapView.setDisplayListDirty();

			setDisplayListDirty();
		} else {
			if (tablePerspective.getDimensionPerspective().getTree() == null)
				return;
			dimensionDendrogramActive = dimensionDendrogramActive == true ? false : true;

			if (dimensionDendrogramActive == true) {
				float highDendro = glDimensionDendrogramView.getViewFrustum().getHeight();

				if (highDendro > 0.5 && highDendro <= 1.5f)
					renderStyle.setHeightExperimentDendrogram(highDendro);

				dimensionDendrogramRenderCut = false;

			} else {
				float fPosCut = glDimensionDendrogramView.getPositionOfCut();
				float highDendro = glDimensionDendrogramView.getViewFrustum().getHeight();
				renderStyle.setHeightExperimentDendrogram(highDendro - fPosCut + 0.1f);
				dimensionDendrogramRenderCut = true;
			}

			glRecordDendrogramView.setRedrawDendrogram();

			glDimensionDendrogramView.setRenderUntilCut(recordDendrogramRenderCut);
			glDimensionDendrogramView.setRedrawDendrogram();

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

		glDimensionDendrogramView.setRedrawDendrogram();

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
		this.numberOfSamplesPerTexture = iNumberOfSamplesPerTexture;
	}

	/**
	 * Set the number of samples which are shown in one heat map
	 * 
	 * @param iNumberOfSamplesPerHeatmap
	 *            the number
	 */
	public final void setNumberOfSamplesPerHeatmap(int iNumberOfSamplesPerHeatmap) {
		this.numberOfSamplesPerHeatmap = iNumberOfSamplesPerHeatmap;
	}

	@Override
	public void handleClusterNodeSelection(ClusterNodeSelectionEvent event) {

		// TODO: visualize all elements in selected sub tree

		// SelectionDelta selectionDeltaTree = event.getSelectionDelta();
		//
		// // cluster mouse over events only used for gene trees
		// Tree<ClusterNode> tree = table.getClusteredTreeGenes();
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
	public void handleNewContentGroupInfo(String perspectiveID,
			RecordGroupList groupList, boolean bDeleteTree) {

		Tree<ClusterNode> tree = null;

		if (perspectiveID.equals(tablePerspective.getRecordPerspective().getPerspectiveID())) {
			tablePerspective.getRecordPerspective().getVirtualArray()
					.setGroupList(groupList);
			tree = tablePerspective.getRecordPerspective().getTree();
		} else {
			return;
		}

		// if hierarchical clusterer result available, discard tree
		if (bDeleteTree && tree != null) {
			warn();

			recordDendrogramActive = false;
			recordDendrogramRenderCut = false;
			// table.getRecordPerspective(recordPerspectiveID).setTree(null);

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

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();
		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();

		ArrayList<Integer> genesToExport = new ArrayList<Integer>();
		ArrayList<Integer> experimentsToExport = new ArrayList<Integer>();

		RecordGroupList groupList = recordVA.getGroupList();

		int groupCnt = 0;

		for (Group iter : groupList) {
			if (iter.getSelectionType() == SelectionType.SELECTION)
				genesToExport.addAll(recordVA.getIDsOfGroup(groupCnt));
			groupCnt++;
		}

		if (dimensionVA.getGroupList() != null) {
			DimensionGroupList dimensionGroupList = dimensionVA.getGroupList();

			groupCnt = 0;
			for (Group iter : dimensionGroupList) {
				if (iter.getSelectionType() == SelectionType.SELECTION)
					experimentsToExport.addAll(dimensionVA.getIDsOfGroup(groupCnt));
				groupCnt++;
			}
			if (experimentsToExport.size() == 0)
				experimentsToExport = dimensionVA.getIDs();

		} else
			experimentsToExport = dimensionVA.getIDs();

		openExportDialog(genesToExport, experimentsToExport);

	}

	@Override
	public void handleExportDimensionGroups() {
		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();
		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();

		ArrayList<Integer> genesToExport = new ArrayList<Integer>();
		ArrayList<Integer> experimentsToExport = new ArrayList<Integer>();

		DimensionGroupList groupList = dimensionVA.getGroupList();

		int groupCnt = 0;

		for (Group iter : groupList) {
			if (iter.getSelectionType() == SelectionType.SELECTION)
				experimentsToExport.addAll(dimensionVA.getIDsOfGroup(groupCnt));
			groupCnt++;
		}

		if (recordVA.getGroupList() != null) {
			RecordGroupList contentGroupList = recordVA.getGroupList();

			groupCnt = 0;
			for (Group iter : contentGroupList) {
				if (iter.getSelectionType() == SelectionType.SELECTION)
					genesToExport.addAll(recordVA.getIDsOfGroup(groupCnt));
				groupCnt++;
			}
			if (genesToExport.size() == 0)
				genesToExport = recordVA.getIDs();

		} else
			genesToExport = recordVA.getIDs();

		openExportDialog(genesToExport, experimentsToExport);

	}

	private void openExportDialog(final ArrayList<Integer> genesToExport,
			final ArrayList<Integer> experimentsToExport) {

		parentComposite.getDisplay().asyncExec(new Runnable() {

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
	public void handleInterchangeDimensionGroups() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMergeContentGroups() {
		RecordVirtualArray va = tablePerspective.getRecordPerspective().getVirtualArray();
		Tree<ClusterNode> tree = tablePerspective.getRecordPerspective().getTree();

		RecordGroupList groupList = va.getGroupList();

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

			recordDendrogramActive = false;
			recordDendrogramRenderCut = false;
			// table.getRecordPerspective(recordPerspectiveID).setTree(null);

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
	public void handleMergeDimensionGroups() {
		DimensionVirtualArray va = tablePerspective.getDimensionPerspective()
				.getVirtualArray();
		Tree<ClusterNode> tree = tablePerspective.getDimensionPerspective().getTree();

		DimensionGroupList groupList = va.getGroupList();

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

			dimensionDendrogramActive = false;
			dimensionDendrogramRenderCut = false;
			// table.getDimensionPerspective(dimensionPerspectiveID).setTree(null);
			// dataDomain.createDimensionGroupsFromDimensionTree(null);
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
	public java.util.Set<IDataDomain> getDataDomains() {
		java.util.Set<IDataDomain> dataDomains = new HashSet<IDataDomain>();
		dataDomains.add(dataDomain);
		return dataDomains;
	}

	@Override
	public void switchDataRepresentation() {
		bRedrawTextures = true;
		super.switchDataRepresentation();
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		gl.glDeleteLists(displayListIndex, 1);
	}

}
