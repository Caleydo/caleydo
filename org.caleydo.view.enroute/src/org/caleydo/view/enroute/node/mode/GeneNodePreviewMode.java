/**
 *
 */
package org.caleydo.view.enroute.node.mode;

import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer.LabelAlignment;
import org.caleydo.core.view.opengl.layout.util.Renderers;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.mappeddataview.CategoricalContentPreviewRenderer;
import org.caleydo.view.enroute.mappeddataview.ContentRenderer;
import org.caleydo.view.enroute.mappeddataview.ContentRendererInitializor;
import org.caleydo.view.enroute.mappeddataview.ContinuousContentPreviewRenderer;
import org.caleydo.view.enroute.node.ALinearizableNode;
import org.caleydo.view.enroute.node.ComplexNode;
import org.caleydo.view.enroute.node.GeneNode;

/**
 * Preview mode for gene {@link GeneNode}s.
 *
 * @author Christian
 *
 */
public class GeneNodePreviewMode
	extends AGeneNodeMode {

	protected static final int MIN_NODE_WIDTH_PIXELS = 70;
	protected static final int SPACING_PIXELS = 2;
	protected static final int CAPTION_HEIGHT_PIXELS = 16;
	protected static final int GENE_ROW_HEIGHT_PIXELS = 30;

	protected ColorRenderer colorRenderer;

	/**
	 * Specifies the pixel height of the node layout defined by this mode.
	 */
	protected int heightPixels = 0;

	/**
	 * @param view
	 */
	public GeneNodePreviewMode(GLEnRoutePathway view) {
		super(view);
	}

	@Override
	public void apply(ALinearizableNode node) {
		this.node = node;
		unregisterPickingListeners();
		registerPickingListeners();
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
		colorRenderer.addPickingID(EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
		baseColumn.addBackgroundRenderer(colorRenderer);

		ElementLayout labelLayout = new ElementLayout("label");
		labelLayout.setRenderer(Renderers.createLabel(node, view).setAlignment(LabelAlignment.CENTER));
		labelLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);

		ElementLayout horizontalSpacing = new ElementLayout();
		horizontalSpacing.setPixelSizeX(SPACING_PIXELS);

		ElementLayout verticalSpacing = new ElementLayout();
		verticalSpacing.setPixelSizeY(SPACING_PIXELS);
		heightPixels = 0;
		Column previewRow = null;

		if (node.getNumAssociatedRows() > 0) {
			previewRow = createPreviewRow(horizontalSpacing, verticalSpacing);
		}

		// baseRow.append(horizontalSpacing);
		titleRow.append(labelLayout);
		// baseRow.append(horizontalSpacing);

		baseColumn.append(verticalSpacing);
		baseColumn.append(titleRow);
		baseColumn.append(verticalSpacing);
		heightPixels += 2 * SPACING_PIXELS + CAPTION_HEIGHT_PIXELS;
		if (previewRow != null) {
			baseColumn.append(previewRow);
		}

		layoutManager.setBaseElementLayout(baseColumn);
	}

	private Column createPreviewRow(ElementLayout horizontalSpacing, ElementLayout verticalSpacing) {

		List<TablePerspective> tablePerspectives = view.getResolvedTablePerspectives();
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
			geneRow.setPixelSizeY(GENE_ROW_HEIGHT_PIXELS);
			geneColumn.append(geneRow);
			// geneColumn.append(verticalSpacing);
			// geneColumn.append(verticalSpacing);
			heightPixels += GENE_ROW_HEIGHT_PIXELS;
			IDataDomain prevDataDomain = null;
			geneRow.append(horizontalSpacing);
			geneRow.append(columnSpacingLayout);

			for (TablePerspective tablePerspective : tablePerspectives) {
				IDataDomain currentDataDomain = tablePerspective.getDataDomain();
				if (currentDataDomain != prevDataDomain && prevDataDomain != null) {
					geneRow.append(datasetSpacing);
				}

				ContentRenderer tablePerspectivePreviewRenderer = null;
				ContentRendererInitializor initializor = new ContentRendererInitializor(tablePerspective, davidId,
						view.getMappedDataRenderer(), view);
				// FIXME: Bad hack to determine categorical data
				if (currentDataDomain.getLabel().toLowerCase().contains("copy")
						|| currentDataDomain.getLabel().toLowerCase().contains("mutation")) {
					tablePerspectivePreviewRenderer = new CategoricalContentPreviewRenderer(initializor);
				}
				else {
					tablePerspectivePreviewRenderer = new ContinuousContentPreviewRenderer(initializor);
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
		return MIN_NODE_WIDTH_PIXELS;
	}

	@Override
	protected void registerPickingListeners() {
		view.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				view.setExpandedBranchSummaryNode(null);
				ComplexNode parent = node.getParentNode();
				EventBasedSelectionManager selectionManager = view.getGeneSelectionManager();
				for (Integer davidId : node.getPathwayVertexRep().getDavidIDs()) {
					selectionManager.removeFromType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				if (parent != null) {
					view.selectBranch(parent);
				}
				else {
					view.selectBranch(node);
				}
			}

			@Override
			public void mouseOver(Pick pick) {
				EventBasedSelectionManager selectionManager = view.getGeneSelectionManager();
				EventBasedSelectionManager metaboliteSelectionManager = view.getMetaboliteSelectionManager();
				metaboliteSelectionManager.clearSelection(SelectionType.MOUSE_OVER);
				selectionManager.clearSelection(SelectionType.MOUSE_OVER);
				for (Integer davidId : node.getPathwayVertexRep().getDavidIDs()) {
					selectionManager.addToType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				view.setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {
				EventBasedSelectionManager selectionManager = view.getGeneSelectionManager();
				for (Integer davidId : node.getPathwayVertexRep().getDavidIDs()) {
					selectionManager.removeFromType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				view.setDisplayListDirty();
			}
		}, EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());

	}

	@Override
	public void unregisterPickingListeners() {
		super.unregisterPickingListeners();
		view.removeAllIDPickingListeners(EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
	}

}
