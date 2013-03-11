/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.SelectionTypeEvent;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.path.PathSizeConfiguration;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.ComplexNode;

/**
 * Renderer for mapped genomic data for linearized pathway view. Based on a list of input nodes genomic data is rendered
 * in a row-column layout. For every node 0-n rows are created, depending on the mapping of the node to the data.
 *
 * @author Alexander Lex
 *
 */
public class MappedDataRenderer {

	public static float[] FRAME_COLOR = { 1, 1, 1, 1 };

	public static float[] ODD_BACKGROUND_COLOR = { 220f / 255f, 220f / 255, 220f / 255, 1f };
	public static float[] EVEN_BACKGROUND_COLOR = { 180f / 255f, 180f / 255, 180f / 255, 1f };

	public static float[] CAPTION_BACKGROUND_COLOR = { 220f / 255f, 220f / 255, 220f / 255, 1f };

	public static float[] BAR_COLOR = { 43f / 255f, 140f / 255, 190f / 255, 1f };
	public static float[] CONTEXT_BAR_COLOR = { 0.3f, 0.3f, 0.3f, 1f };

	public static float[] SUMMARY_BAR_COLOR = { 49f / 255f, 163f / 255, 84f / 255, 1f };

	public static final SelectionType abstractGroupType = new SelectionType("AbstactGroup", new int[] { 0, 0, 0 }, 1,
			false, 0);

	public static final int ABSTRACT_GROUP_PIXEL_WIDTH = 100;

	public static final int MIN_SAMPLE_PIXEL_WIDTH = 2;

	public static final int CAPTION_COLUMN_PIXEL_WIDTH = 100;

	public static final int SPACING_PIXEL_WIDTH = 1;

	private GLEnRoutePathway parentView;

	private ArrayList<RelationshipRenderer> relationShipRenderers;

	/**
	 * Table perspectives rendered.
	 */
	private ArrayList<TablePerspective> geneTablePerspectives = new ArrayList<>();

	private ArrayList<TablePerspective> contextualTablePerspectives = new ArrayList<>();

	/**
	 * the distance from the left edge of this renderer to the left edge of the window
	 */
	private float xOffset = 0;

	/**
	 * The distance from the upper edge of this renderer to the upper edge of the window
	 */
	private float yOffset = 0;

	private float rowHeight;

	/**
	 * Specifies the minimum width required by this mapped data renderer for display.
	 */
	private int minWidthPixels = 0;

	/**
	 * Layout manager for the base representation of the data. This manager only updates when the layout is changed.
	 */
	private LayoutManager baseLayoutManger;
	/**
	 * Layout manager used for highlighted elements. This manager updates when the display list of enRoute is dirty.
	 */
	private LayoutManager highlightLayoutManger;
	private ViewFrustum viewFrustum;

	EventBasedSelectionManager rowSelectionManager;

	EventBasedSelectionManager sampleSelectionManager;
	EventBasedSelectionManager sampleGroupSelectionManager;

	EventBasedSelectionManager contextRowSelectionManager;

	/** The mapping Type all samples understand */
	IDType sampleIDType;

	// FIXME: This is pure hack
	int selectedBucketNumber = 0;
	int selectedBucketGeneID = 0;
	Perspective selectedBucketExperimentPerspective;

	/**
	 * Constructor with parent view as parameter.
	 */
	public MappedDataRenderer(GLEnRoutePathway parentView) {
		this.parentView = parentView;
		rowSelectionManager = parentView.getGeneSelectionManager();

		List<GeneticDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(GeneticDataDomain.class);
		if (dataDomains.size() != 0) {
			sampleIDType = dataDomains.get(0).getSampleIDType().getIDCategory().getPrimaryMappingType();
			sampleSelectionManager = parentView.getSampleSelectionManager();

			sampleGroupSelectionManager = new EventBasedSelectionManager(parentView, dataDomains.get(0)
					.getSampleGroupIDType());

			SelectionTypeEvent selectionTypeEvent = new SelectionTypeEvent(abstractGroupType);
			GeneralManager.get().getEventPublisher().triggerEvent(selectionTypeEvent);

		} else {
			throw new IllegalStateException("No Valid Datadomain");
		}

		registerPickingListeners();
	}

	public void init() {
		viewFrustum = new ViewFrustum();
		baseLayoutManger = new LayoutManager(viewFrustum, parentView.getPixelGLConverter());
		highlightLayoutManger = new LayoutManager(viewFrustum, parentView.getPixelGLConverter());
	}

	public void updateLayout() {
		baseLayoutManger.updateLayout();
		highlightLayoutManger.updateLayout();
	}

	public void renderBaseRepresentation(GL2 gl) {
		baseLayoutManger.render(gl);

		for (RelationshipRenderer relationshipRenderer : relationShipRenderers) {
			relationshipRenderer.render(gl);
		}
	}

	public void renderHighlightElements(GL2 gl) {
		highlightLayoutManger.render(gl);
	}

	/**
	 * Set the geometry information for this <code>MappedDataRenderer</code>.
	 *
	 * @param width
	 *            the width of the drawing space of this renderer
	 * @param height
	 *            the height of the drawing space of this renderer
	 * @param xOffset
	 *            how much this renderer is translated in x direction from the window origin
	 * @param yOffset
	 *            how much this renderer is translated in y direction from the window origin
	 * @param rowHeight
	 *            the height of a single row in the renderer rows
	 */
	public void setGeometry(float width, float height, float xOffset, float yOffset, float rowHeight) {
		viewFrustum.setRight(width);
		viewFrustum.setTop(height);
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.rowHeight = rowHeight;

		// layoutManger.updateLayout();

	}

	/**
	 * Sets the list of nodes that are used as the basis for rendering the mapped data. Triggers a complete re-build of
	 * the layout. Creates the layout used for the rendering.
	 *
	 * @param linearizedNodes
	 *            setter, see {@link #linearizedNodes}
	 */
	public void setLinearizedNodes(List<ALinearizableNode> linearizedNodes) {

		relationShipRenderers = new ArrayList<RelationshipRenderer>(linearizedNodes.size());
		createLayout(linearizedNodes, baseLayoutManger, false);
		createLayout(linearizedNodes, highlightLayoutManger, true);
	}

	private void createLayout(List<ALinearizableNode> linearizedNodes, LayoutManager layoutManager,
			boolean isHighlightLayout) {

		Row baseRow = new Row("baseRow");
		layoutManager.setBaseElementLayout(baseRow);

		ElementLayout xSpacing = new ElementLayout();
		xSpacing.setPixelSizeX(SPACING_PIXEL_WIDTH);

		float[] color;
		// baseRow.setDebug(true);
		Column dataSetColumn = new Column("dataSetColumn");
		dataSetColumn.setBottomUp(false);
		// dataSetColumn.setDebug(true);
		baseRow.append(dataSetColumn);
		Column captionColumn = new Column("captionColumn");
		captionColumn.setBottomUp(false);
		// captionColumn.setDebug(true);

		captionColumn.setPixelSizeX(CAPTION_COLUMN_PIXEL_WIDTH);
		ElementLayout columnCaptionSpacing = new ElementLayout();
		columnCaptionSpacing.setPixelSizeY(50);
		captionColumn.append(columnCaptionSpacing);
		baseRow.append(captionColumn);

		int nodeCount = 0;
		float previousNodePosition = viewFrustum.getHeight() + yOffset
				- parentView.getPixelGLConverter().getGLHeightForPixelHeight(50);
		int previousNrDavids = 0;

		/**
		 * A list of lists of element layouts where the outer list contains one nested list for every data container and
		 * the inner list one element layout for every gene in the linearized pathway
		 */
		ArrayList<ArrayList<ElementLayout>> rowListForContextTablePerspectives = new ArrayList<ArrayList<ElementLayout>>(
				contextualTablePerspectives.size());

		IDType contextRowIDType = null;
		List<Integer> contextIDs = new ArrayList<Integer>();
		if (contextualTablePerspectives != null && !contextualTablePerspectives.isEmpty()) {

			for (int count = 0; count < contextualTablePerspectives.size(); count++) {
				rowListForContextTablePerspectives.add(new ArrayList<ElementLayout>());
			}
			TablePerspective contextTPerspective = contextualTablePerspectives.get(0);
			contextRowIDType = contextTPerspective.getDataDomain().getOppositeIDType(sampleIDType);
			int i = 0;
			for (Integer rowID : contextTPerspective.getPerspective(contextRowIDType).getVirtualArray()) {
				contextIDs.add(rowID);
				if (i % 2 == 0)
					color = EVEN_BACKGROUND_COLOR;
				else
					color = ODD_BACKGROUND_COLOR;

				Row row = new Row("Row " + i);
				row.setAbsoluteSizeY(rowHeight);
				// row.setDebug(true);
				dataSetColumn.append(row);

				for (int tablePerspectiveCount = 0; tablePerspectiveCount < contextualTablePerspectives.size(); tablePerspectiveCount++) {

					ElementLayout tablePerspectiveLayout = new ElementLayout("TablePerspective "
							+ tablePerspectiveCount + " / " + i);
					// tablePerspectiveRow.setPixelSizeX(5);
					if (!isHighlightLayout) {
						tablePerspectiveLayout.addBackgroundRenderer(new RowBackgroundRenderer(color));
					}

					row.append(tablePerspectiveLayout);
					rowListForContextTablePerspectives.get(tablePerspectiveCount).add(tablePerspectiveLayout);
					if (tablePerspectiveCount != contextualTablePerspectives.size() - 1) {
						row.append(xSpacing);
					}
				}
				ElementLayout rowCaption = new ElementLayout();
				rowCaption.setAbsoluteSizeY(rowHeight);

				if (isHighlightLayout) {
					RowCaptionRenderer captionRenderer = new RowCaptionRenderer(contextRowIDType, rowID, parentView,
							this, color);
					rowCaption.setRenderer(captionRenderer);
				}

				captionColumn.append(rowCaption);
				i++;

				if (i > 2)
					break;

				PathSizeConfiguration oldConfig = parentView.getPathRenderer().getSizeConfig();

				PathSizeConfiguration newConfig = new PathSizeConfiguration.Builder(oldConfig).pathStartSpacing(
						PathSizeConfiguration.DEFAULT.getPathStartSpacing()
								+ parentView.getPixelGLConverter().getPixelHeightForGLHeight(rowHeight) * 3 + 10)
						.build();
				parentView.getPathRenderer().setSizeConfig(newConfig);
				previousNodePosition = 0;

			}

			ElementLayout spacing = new ElementLayout();
			spacing.setPixelSizeY(10);
			dataSetColumn.append(spacing);
			captionColumn.append(spacing);

		}

		/**
		 * A list of lists of element layouts where the outer list contains one nested list for every data container and
		 * the inner list one element layout for every gene in the linearized pathway
		 */
		ArrayList<ArrayList<ElementLayout>> rowListForTablePerspectives = new ArrayList<ArrayList<ElementLayout>>(
				geneTablePerspectives.size());

		for (int count = 0; count < geneTablePerspectives.size(); count++) {
			rowListForTablePerspectives.add(new ArrayList<ElementLayout>(linearizedNodes.size() * 2));
		}

		IDType davidIDType = IDType.getIDType("DAVID");

		ArrayList<Integer> davidIDs = new ArrayList<Integer>(linearizedNodes.size() * 2);

		ArrayList<ALinearizableNode> resolvedNodes = new ArrayList<ALinearizableNode>();

		// resolve complex nodes
		for (ALinearizableNode node : linearizedNodes) {
			if (node instanceof ComplexNode) {
				List<ALinearizableNode> embeddedNodes = ((ComplexNode) node).getNodes();
				resolvedNodes.addAll(embeddedNodes);
			} else
				resolvedNodes.add(node);
		}

		for (ALinearizableNode node : resolvedNodes) {

			if (node.getMappedDavidIDs().size() == 0)
				continue;

			List<Integer> subDavidIDs = node.getMappedDavidIDs();
			int currentNrDavids = subDavidIDs.size();
			davidIDs.addAll(subDavidIDs);

			float currentNodePositionY = node.getPosition().y();
			float deviation;

			if (node.getParentNode() != null) {
				currentNodePositionY = node.getParentNode().getPosition().y();
				currentNrDavids = node.getParentNode().getMappedDavidIDs().size();
			}
			float previousLowerHeight = previousNodePosition - rowHeight * (previousNrDavids) / 2;
			float currentUpperHeight = (currentNodePositionY + rowHeight * (currentNrDavids) / 2);

			deviation = previousLowerHeight - currentUpperHeight;

			if (previousNodePosition > 0 && deviation > 0) {
				ElementLayout spacing = new ElementLayout();
				spacing.setDebug(true);
				spacing.setAbsoluteSizeY(deviation);
				dataSetColumn.append(spacing);
				captionColumn.append(spacing);
			}

			previousNodePosition = currentNodePositionY;
			previousNrDavids = currentNrDavids;

			if (nodeCount % 2 == 0)
				color = EVEN_BACKGROUND_COLOR;
			else
				color = ODD_BACKGROUND_COLOR;

			RelationshipRenderer relationShipRenderer = null;

			if (!isHighlightLayout) {

				relationShipRenderer = new RelationshipRenderer(color, parentView);
				relationShipRenderers.add(relationShipRenderer);
				float x = node.getPosition().x()
						+ parentView.getPixelGLConverter().getGLWidthForPixelWidth(node.getWidthPixels()) / 2;
				float height = parentView.getPixelGLConverter().getGLHeightForPixelHeight(node.getHeightPixels());

				relationShipRenderer.topLeft[0] = x - xOffset;
				relationShipRenderer.topLeft[1] = node.getPosition().y() + height / 2 - yOffset;

				relationShipRenderer.bottomLeft[0] = x - xOffset;
				relationShipRenderer.bottomLeft[1] = node.getPosition().y() - height / 2 - yOffset;
			}

			nodeCount++;

			int idCount = 0;
			// for (int davidCounter = subDavidIDs.size()-1; davidCounter>=0;
			// davidCounter--) {

			for (Integer davidID : subDavidIDs) {

				Row row = new Row("Row " + davidID);
				row.setAbsoluteSizeY(rowHeight);
				// row.setDebug(true);
				dataSetColumn.append(row);

				for (int tablePerspectiveCount = 0; tablePerspectiveCount < geneTablePerspectives.size(); tablePerspectiveCount++) {

					ElementLayout tablePerspectiveLayout = new ElementLayout("TablePerspective "
							+ tablePerspectiveCount + " / " + idCount);
					// tablePerspectiveRow.setPixelSizeX(5);
					if (!isHighlightLayout) {
						tablePerspectiveLayout.addBackgroundRenderer(new RowBackgroundRenderer(color));
					}

					row.append(tablePerspectiveLayout);
					rowListForTablePerspectives.get(tablePerspectiveCount).add(tablePerspectiveLayout);
					if (tablePerspectiveCount != geneTablePerspectives.size() - 1) {
						row.append(xSpacing);
					}
				}
				ElementLayout rowCaption = new ElementLayout();
				rowCaption.setAbsoluteSizeY(rowHeight);

				if (isHighlightLayout) {
					RowCaptionRenderer captionRenderer = new RowCaptionRenderer(davidIDType, davidID, parentView, this,
							color);
					rowCaption.setRenderer(captionRenderer);
				}

				captionColumn.append(rowCaption);

				if (!isHighlightLayout) {
					if (relationShipRenderer == null) {
						throw new IllegalStateException("Relationshiprenderer was null");
					} else {

						if (idCount == 0)
							relationShipRenderer.topRightLayout = row;
						if (idCount == subDavidIDs.size() - 1)
							relationShipRenderer.bottomRightLayout = row;
					}
				}

				idCount++;
			}

		}
		ElementLayout ySpacing = new ElementLayout();
		ySpacing.setPixelSizeY(5);
		dataSetColumn.append(ySpacing);

		Row topCaptionRow = new Row("topCaptionRow");
		// captionRow.setDebug(true);
		topCaptionRow.setPixelSizeY(50);
		// topCaptionRow.setDebug(true);
		// dataSetColumn.add(0, captionRow);
		dataSetColumn.add(0, topCaptionRow);

		Row bottomCaptionRow = new Row("captionRow");
		// captionRow.setDebug(true);
		bottomCaptionRow.setPixelSizeY(50);
		// dataSetColumn.add(0, captionRow);
		dataSetColumn.append(bottomCaptionRow);

		// for (int contextPerspectiveCount = 0; contextPerspectiveCount < contextualTablePerspectives.size();
		// contextPerspectiveCount++)
		// {
		//
		//
		// }
		for (int tablePerspectiveCount = 0; tablePerspectiveCount < geneTablePerspectives.size(); tablePerspectiveCount++) {
			if (contextualTablePerspectives != null && contextualTablePerspectives.size() > 0
					&& contextRowIDType != null) {
				TablePerspective contextTablePerspective = contextualTablePerspectives.get(tablePerspectiveCount);

				prepareData(contextTablePerspective, rowListForContextTablePerspectives.get(tablePerspectiveCount),
						null, null, contextRowIDType, contextIDs, isHighlightLayout);
			}

			ColumnCaptionLayout topCaptionLayout = new ColumnCaptionLayout(parentView, this);
			topCaptionRow.append(topCaptionLayout);

			ColumnCaptionLayout bottomCaptionLayout = new ColumnCaptionLayout(parentView, this);
			bottomCaptionRow.append(bottomCaptionLayout);
			if (tablePerspectiveCount != geneTablePerspectives.size() - 1) {
				bottomCaptionRow.append(xSpacing);
				topCaptionRow.append(xSpacing);
			}

			prepareData(geneTablePerspectives.get(tablePerspectiveCount),
					rowListForTablePerspectives.get(tablePerspectiveCount), topCaptionLayout, bottomCaptionLayout,
					davidIDType, davidIDs, isHighlightLayout);
		}
		calcMinWidthPixels();

		PixelGLConverter pixelGLConverter = parentView.getPixelGLConverter();
		float minWidth = pixelGLConverter.getGLWidthForPixelWidth(minWidthPixels);
		if (!parentView.isFitWidthToScreen() && viewFrustum.getWidth() < minWidth) {
			viewFrustum.setRight(minWidth);
		}
	}

	private void calcMinWidthPixels() {

		minWidthPixels = 0;

		// Calculate content specific width
		for (int i = 0; i < geneTablePerspectives.size(); i++) {

			TablePerspective tablePerspective = geneTablePerspectives.get(i);
			Perspective experimentPerspective;
			GeneticDataDomain dataDomain = (GeneticDataDomain) tablePerspective.getDataDomain();
			Group group = null;

			if (dataDomain.isGeneRecord()) {
				experimentPerspective = tablePerspective.getDimensionPerspective();
				group = tablePerspective.getDimensionGroup();
			} else {
				experimentPerspective = tablePerspective.getRecordPerspective();
				group = tablePerspective.getRecordGroup();
			}
			if (group == null)
				continue;

			if (sampleGroupSelectionManager.checkStatus(abstractGroupType, group.getID())) {
				minWidthPixels += ABSTRACT_GROUP_PIXEL_WIDTH;
			} else {
				minWidthPixels += experimentPerspective.getVirtualArray().size() * MIN_SAMPLE_PIXEL_WIDTH;
			}
		}

		minWidthPixels += (geneTablePerspectives.size() - 1) * SPACING_PIXEL_WIDTH;
		minWidthPixels += CAPTION_COLUMN_PIXEL_WIDTH;
	}

	/**
	 * Fills the layout with data specific for the data containers
	 *
	 * @param tablePerspective
	 *            the perspective containing the data
	 * @param rowLayouts
	 * @param topCaptionLayout
	 * @param bottomCaptionLayout
	 * @param rowIDType
	 * @param rowIDs
	 * @param isHighlightLayout
	 */
	private void prepareData(TablePerspective tablePerspective, List<ElementLayout> rowLayouts,
			ColumnCaptionLayout topCaptionLayout, ColumnCaptionLayout bottomCaptionLayout, IDType rowIDType,
			List<Integer> rowIDs, boolean isHighlightLayout) {
		ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

		Perspective columnPerspective;
		IDType columnIDType = dataDomain.getOppositeIDType(rowIDType);

		columnPerspective = tablePerspective.getPerspective(columnIDType);

		Group group = null;

		group = tablePerspective.getGroup(columnIDType);
		if (group == null) {
			group = tablePerspective.getPerspective(columnIDType).getVirtualArray().getGroupList().get(0);
			group.setLabel(tablePerspective.getLabel(), tablePerspective.isLabelDefault());
		}

		if (isHighlightLayout && topCaptionLayout != null && bottomCaptionLayout != null) {
			topCaptionLayout.init(group, columnPerspective, dataDomain);
			bottomCaptionLayout.init(group, columnPerspective, dataDomain);
		}

		IDType resolvedRowIDType;
		Integer resolvedRowID;

		// IDType geneIDTYpe = dataDomain.getGeneIDType();
		// ArrayList<Integer> geneIDs = new ArrayList<Integer>(davidIDs.size());
		for (int rowCount = 0; rowCount < rowIDs.size(); rowCount++) {
			Integer rowID = rowIDs.get(rowCount);
			if (!dataDomain.isPrimaryIDType(rowIDType)) {
				resolvedRowIDType = dataDomain.getPrimaryIDType(rowIDType);
				Set<Integer> convertedIDs = IDMappingManagerRegistry.get().getIDMappingManager(rowIDType)
						.getIDAsSet(rowIDType, resolvedRowIDType, rowID);

				if (convertedIDs == null) {
					resolvedRowID = null;
				} else {
					resolvedRowID = convertedIDs.iterator().next();
					if (convertedIDs.size() > 1) {

						System.out.println("Here's the problem: " + convertedIDs);
					}
				}
			} else {
				resolvedRowID = rowID;
				resolvedRowIDType = rowIDType;
			}

			// geneIDs.add(rowID);
			ElementLayout tablePerspectiveLayout = rowLayouts.get(rowCount);

			if (sampleGroupSelectionManager.checkStatus(abstractGroupType, group.getID())) {
				tablePerspectiveLayout.setPixelSizeX(ABSTRACT_GROUP_PIXEL_WIDTH);
				if (topCaptionLayout != null && bottomCaptionLayout != null) {
					bottomCaptionLayout.setPixelSizeX(ABSTRACT_GROUP_PIXEL_WIDTH);
					topCaptionLayout.setPixelSizeX(ABSTRACT_GROUP_PIXEL_WIDTH);
				}
			} else {
				// float width = 1.0f / usedTablePerspectives.size();
				// tablePerspectiveLayout.setRatioSizeX(0.2);

				tablePerspectiveLayout.setDynamicSizeUnitsX(columnPerspective.getVirtualArray().size());
				if (topCaptionLayout != null && bottomCaptionLayout != null) {
					bottomCaptionLayout.setDynamicSizeUnitsX(columnPerspective.getVirtualArray().size());
					topCaptionLayout.setDynamicSizeUnitsX(columnPerspective.getVirtualArray().size());
				}
			}

			// ALayoutRenderer columnCaptionRenderer = new
			// ColumnCaptionRenderer(parentView,
			// this, group);
			// captionLayout.setRenderer(columnCaptionRenderer);

			// FIXME: BAAAAD Hack to distinguish categorical data
			if (dataDomain.getLabel().toLowerCase().contains("copy")) {
				tablePerspectiveLayout.setRenderer(new CopyNumberRowContentRenderer(rowIDType, rowID,
						resolvedRowIDType, resolvedRowID, dataDomain, columnPerspective, parentView, this, group,
						isHighlightLayout));
			} else if (dataDomain.getLabel().toLowerCase().contains("mutation")) {
				tablePerspectiveLayout.setRenderer(new MutationStatusMatrixRowContentRenderer(rowIDType, rowID,
						resolvedRowIDType, resolvedRowID, dataDomain, columnPerspective, parentView, this, group,
						isHighlightLayout));

				tablePerspectiveLayout.setDynamicSizeUnitsX((int) Math.ceil(columnPerspective.getVirtualArray().size()
						* 2.0f / MutationStatusMatrixRowContentRenderer.NUM_ROWS));
				if (topCaptionLayout != null && bottomCaptionLayout != null) {
					bottomCaptionLayout.setDynamicSizeUnitsX((int) Math.ceil(columnPerspective.getVirtualArray().size()
							* 2.0f / MutationStatusMatrixRowContentRenderer.NUM_ROWS));
					topCaptionLayout.setDynamicSizeUnitsX((int) Math.ceil(columnPerspective.getVirtualArray().size()
							* 2.0f / MutationStatusMatrixRowContentRenderer.NUM_ROWS));
				}

				// tablePerspectiveLayout
				// .setRenderer(new MutationStatusRowContentRenderer(
				// rowID, rowID, dataDomain, tablePerspective,
				// columnPerspective, parentView, this, group,
				// isHighlightLayout));
			} else {
				tablePerspectiveLayout.setRenderer(new ContinuousContentRenderer(rowIDType, rowID, resolvedRowIDType,
						resolvedRowID, dataDomain, columnPerspective, parentView, this, group, isHighlightLayout));
			}

		}

	}

	protected void registerPickingListeners() {
		parentView.addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				rowSelectionManager.clearSelection(SelectionType.SELECTION);
				rowSelectionManager.addToType(SelectionType.SELECTION, pick.getObjectID());
				rowSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {

				rowSelectionManager.addToType(SelectionType.MOUSE_OVER, pick.getObjectID());
				rowSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOut(Pick pick) {
				rowSelectionManager.removeFromType(SelectionType.MOUSE_OVER, pick.getObjectID());
				rowSelectionManager.triggerSelectionUpdateEvent();

				parentView.setDisplayListDirty();

			}
		}, rowSelectionManager.getIDType().getTypeName());

		parentView.addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				sampleSelectionManager.clearSelection(SelectionType.SELECTION);
				sampleSelectionManager.addToType(SelectionType.SELECTION, pick.getObjectID());
				sampleSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {

				sampleSelectionManager.addToType(SelectionType.MOUSE_OVER, pick.getObjectID());
				sampleSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}

			@Override
			public void mouseOut(Pick pick) {
				sampleSelectionManager.removeFromType(SelectionType.MOUSE_OVER, pick.getObjectID());
				sampleSelectionManager.triggerSelectionUpdateEvent();
				parentView.setDisplayListDirty();

			}
		}, EPickingType.SAMPLE.name());

		parentView.addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				if (sampleGroupSelectionManager.checkStatus(abstractGroupType, pick.getObjectID()))
					sampleGroupSelectionManager.removeFromType(abstractGroupType, pick.getObjectID());
				else
					sampleGroupSelectionManager.addToType(abstractGroupType, pick.getObjectID());
				sampleGroupSelectionManager.triggerSelectionUpdateEvent();
				parentView.setLayoutDirty();

			}

		}, EPickingType.SAMPLE_GROUP_VIEW_MODE.name());
	}

	public void unregisterPickingListeners() {
	}

	/**
	 * @return the minWidthPixels, see {@link #minWidthPixels}
	 */
	public int getMinWidthPixels() {
		return minWidthPixels;
	}

	/**
	 * @param geneTablePerspectives
	 *            setter, see {@link geneTablePerspectives}
	 */
	public void setGeneTablePerspectives(ArrayList<TablePerspective> geneTablePerspectives) {
		this.geneTablePerspectives = geneTablePerspectives;
	}

	/**
	 * @param contextualTablePerspectives
	 *            setter, see {@link contextualTablePerspectives}
	 */
	public void setContextualTablePerspectives(ArrayList<TablePerspective> contextualTablePerspectives) {
		this.contextualTablePerspectives = contextualTablePerspectives;

		if (contextualTablePerspectives != null && !contextualTablePerspectives.isEmpty()) {
			IDType contextRowIDType = contextualTablePerspectives.get(0).getDataDomain()
					.getOppositeIDType(sampleIDType);

			if (contextRowSelectionManager == null) {
				contextRowSelectionManager = new EventBasedSelectionManager(parentView, contextRowIDType);

				parentView.addTypePickingListener(new APickingListener() {

					@Override
					public void clicked(Pick pick) {
						contextRowSelectionManager.clearSelection(SelectionType.SELECTION);
						contextRowSelectionManager.addToType(SelectionType.SELECTION, pick.getObjectID());
						contextRowSelectionManager.triggerSelectionUpdateEvent();
						parentView.setDisplayListDirty();

					}

					@Override
					public void mouseOver(Pick pick) {

						contextRowSelectionManager.addToType(SelectionType.MOUSE_OVER, pick.getObjectID());
						contextRowSelectionManager.triggerSelectionUpdateEvent();
						parentView.setDisplayListDirty();

					}

					@Override
					public void mouseOut(Pick pick) {
						contextRowSelectionManager.removeFromType(SelectionType.MOUSE_OVER, pick.getObjectID());
						contextRowSelectionManager.triggerSelectionUpdateEvent();

						parentView.setDisplayListDirty();

					}
				}, contextRowIDType.getTypeName());

			}
		}

	}

	public void destroy(GL2 gl) {
		if (highlightLayoutManger != null)
			highlightLayoutManger.destroy(gl);
		if (baseLayoutManger != null)
			baseLayoutManger.destroy(gl);
		sampleGroupSelectionManager.unregisterEventListeners();
	}

	SelectionManager getSelectionManager(IDType idType) {

		if (sampleSelectionManager.getIDType().resolvesTo(idType))
			return sampleSelectionManager;
		else if (rowSelectionManager.getIDType().resolvesTo(idType))
			return rowSelectionManager;
		else if (contextRowSelectionManager != null && contextRowSelectionManager.getIDType().resolvesTo(idType))
			return contextRowSelectionManager;

		return null;
	}

}
