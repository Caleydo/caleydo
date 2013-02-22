/**
 *
 */
package org.caleydo.view.enroute.path.node.mode;

import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer.LabelAlignment;
import org.caleydo.core.view.opengl.layout.util.Renderers;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.pathway.VertexRepBasedContextMenuItem;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.mappeddataview.CategoricalContentPreviewRenderer;
import org.caleydo.view.enroute.mappeddataview.ContinuousContentPreviewRenderer;
import org.caleydo.view.enroute.path.EnRoutePathRenderer;
import org.caleydo.view.enroute.path.node.ALinearizableNode;
import org.caleydo.view.enroute.path.node.GeneNode;

/**
 * Preview mode for gene {@link GeneNode}s.
 *
 * @author Christian
 *
 */
public class GeneNodePreviewMode extends AGeneNodeMode {

	// protected static final int MIN_NODE_WIDTH_PIXELS = 70;
	protected static final int SPACING_PIXELS = 2;
	// protected static final int CAPTION_HEIGHT_PIXELS = 16;
	// protected static final int GENE_ROW_HEIGHT_PIXELS = 30;

	protected ColorRenderer colorRenderer;

	protected EnRoutePathRenderer enRoutePathRenderer;

	/**
	 * Specifies the pixel height of the node layout defined by this mode.
	 */
	protected int heightPixels = 0;

	/**
	 * @param view
	 */
	public GeneNodePreviewMode(AGLView view, EnRoutePathRenderer pathwayPathRenderer) {
		super(view, pathwayPathRenderer);
		this.enRoutePathRenderer = pathwayPathRenderer;
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		destroy();
		init();
		attributeRenderers.clear();

		Column baseColumn = new Column("baseColumn");
		// baseColumn.setDebug(true);
		baseColumn.setBottomUp(false);
		Row titleRow = new Row("baseRow");
		// titleRow.setDebug(true);
		titleRow.setFrameColor(0, 1, 0, 1);
		titleRow.setYDynamic(true);
		// titleRow.setPixelSizeY(20);
		colorRenderer = new ColorRenderer(this);
		colorRenderer.setView(view);
		colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
		colorRenderer.setDrawBorder(true);
		colorRenderer.addPickingID(EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
		baseColumn.addBackgroundRenderer(colorRenderer);

		ElementLayout labelLayout = new ElementLayout("label");
		labelLayout.setRenderer(Renderers.createLabel(node, view).setAlignment(LabelAlignment.CENTER));
		labelLayout.setPixelSizeY(pathwayPathRenderer.getSizeConfig().getNodeTextHeight());

		ElementLayout horizontalSpacing = new ElementLayout();
		horizontalSpacing.setPixelSizeX(SPACING_PIXELS);

		ElementLayout verticalSpacing = new ElementLayout();
		verticalSpacing.setPixelSizeY(SPACING_PIXELS);
		heightPixels = 0;
		Column previewRow = null;

		if (node.getMappedDavidIDs().size() > 0) {
			previewRow = createPreviewRow(horizontalSpacing, verticalSpacing);
		}

		// baseRow.append(horizontalSpacing);
		titleRow.append(labelLayout);
		// baseRow.append(horizontalSpacing);

		baseColumn.append(verticalSpacing);
		baseColumn.append(titleRow);
		baseColumn.append(verticalSpacing);
		heightPixels += 2 * SPACING_PIXELS + pathwayPathRenderer.getSizeConfig().getNodeTextHeight();
		if (previewRow != null) {
			baseColumn.append(previewRow);
		}

		layoutManager.setBaseElementLayout(baseColumn);
	}

	private Column createPreviewRow(ElementLayout horizontalSpacing, ElementLayout verticalSpacing) {

		List<TablePerspective> tablePerspectives = pathwayPathRenderer.getTablePerspectives();
		List<Integer> davidIds = node.getMappedDavidIDs();

		Column geneColumn = new Column("geneColumn");
		geneColumn.append(verticalSpacing);
		geneColumn.setYDynamic(true);
		geneColumn.setBottomUp(false);

		if (tablePerspectives == null || davidIds == null || davidIds.isEmpty())
			return geneColumn;

		// previewRow.append(geneColumn);
		heightPixels += SPACING_PIXELS;

		ElementLayout columnSpacingLayout = new ElementLayout("ColumnSpacing");
		columnSpacingLayout.setDynamicSizeUnitsX(1);
		ElementLayout datasetSpacing = new ElementLayout();
		datasetSpacing.setPixelSizeX(3);

		for (Integer davidId : davidIds) {
			Row geneRow = new Row("geneRow");
			ColorRenderer geneRowColorRenderer = new ColorRenderer(new float[] { 1, 1, 1, 1 },
					new float[] { 0, 0, 0, 1 }, 1);
			geneRow.setRenderer(geneRowColorRenderer);
			geneRow.setPixelSizeY(pathwayPathRenderer.getSizeConfig().getDataPreviewRowHeight());
			geneColumn.append(geneRow);
			// geneColumn.append(verticalSpacing);
			// geneColumn.append(verticalSpacing);
			heightPixels += pathwayPathRenderer.getSizeConfig().getDataPreviewRowHeight();
			IDataDomain prevDataDomain = null;
			geneRow.append(horizontalSpacing);
			geneRow.append(columnSpacingLayout);

			for (TablePerspective tablePerspective : tablePerspectives) {
				IDataDomain currentDataDomain = tablePerspective.getDataDomain();
				if (currentDataDomain != prevDataDomain && prevDataDomain != null) {
					geneRow.append(datasetSpacing);
				}

				ALayoutRenderer tablePerspectivePreviewRenderer = null;
				// PreviewContentRendererInitializor initializor = new
				// PreviewContentRendererInitializor(tablePerspective,
				// davidId, view, pathwayPathRenderer.getGeneSelectionManager(),
				// pathwayPathRenderer.getGeneSelectionManager());
				// FIXME: Bad hack to determine categorical data
				if (currentDataDomain.getLabel().toLowerCase().contains("copy")
						|| currentDataDomain.getLabel().toLowerCase().contains("mutation")) {
					tablePerspectivePreviewRenderer = new CategoricalContentPreviewRenderer(davidId, tablePerspective,
							pathwayPathRenderer.getGeneSelectionManager(),
							pathwayPathRenderer.getSampleSelectionManager());
				} else {
					tablePerspectivePreviewRenderer = new ContinuousContentPreviewRenderer(davidId, tablePerspective,
							pathwayPathRenderer.getGeneSelectionManager(),
							pathwayPathRenderer.getSampleSelectionManager());
				}

				ElementLayout previewRendererLayout = new ElementLayout("prev");
				// previewRendererLayout.setDebug(true);
				// previewRendererLayout.setFrameColor(1, 0, 0, 1);
				previewRendererLayout.setDynamicSizeUnitsX(3);

				previewRendererLayout.setRenderer(tablePerspectivePreviewRenderer);
				geneRow.append(previewRendererLayout);

				prevDataDomain = currentDataDomain;
			}
			geneRow.append(horizontalSpacing);
			geneRow.append(columnSpacingLayout);
		}

		return geneColumn;
	}

	@Override
	public int getMinHeightPixels() {
		return heightPixels;
	}

	@Override
	public int getMinWidthPixels() {
		return pathwayPathRenderer.getSizeConfig().getRectangleNodeWidth();
	}

	@Override
	protected void init() {
		view.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				enRoutePathRenderer.setExpandedBranchSummaryNode(null);
				ALinearizableNode branchNode = node;
				while (branchNode.getParentNode() != null) {
					branchNode = branchNode.getParentNode();
				}
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getGeneSelectionManager();
				for (Integer davidId : node.getPrimaryPathwayVertexRep().getDavidIDs()) {
					selectionManager.removeFromType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();
				enRoutePathRenderer.selectBranch(branchNode);

			}

			@Override
			public void mouseOver(Pick pick) {
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getGeneSelectionManager();
				EventBasedSelectionManager metaboliteSelectionManager = pathwayPathRenderer
						.getMetaboliteSelectionManager();
				metaboliteSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				for (Integer davidId : node.getPrimaryPathwayVertexRep().getDavidIDs()) {
					selectionManager.addToType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				pathwayPathRenderer.setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {
				EventBasedSelectionManager selectionManager = pathwayPathRenderer.getGeneSelectionManager();
				for (Integer davidId : node.getPrimaryPathwayVertexRep().getDavidIDs()) {
					selectionManager.removeFromType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				pathwayPathRenderer.setDisplayListDirty();
			}

			@Override
			protected void rightClicked(Pick pick) {
				for (VertexRepBasedContextMenuItem item : pathwayPathRenderer.getNodeContextMenuItems()) {
					// Only use primary vertex rep
					item.setVertexRep(node.getPrimaryPathwayVertexRep());
					view.getContextMenuCreator().addContextMenuItem(item);
				}
			}

		}, EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());

	}

	@Override
	public void destroy() {
		super.destroy();
		view.removeAllIDPickingListeners(EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
	}

}
