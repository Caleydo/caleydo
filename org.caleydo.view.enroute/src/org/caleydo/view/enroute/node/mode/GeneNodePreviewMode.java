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
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
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
public class GeneNodePreviewMode extends AGeneNodeMode {

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
		colorRenderer
				.addPickingID(EPickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
		baseColumn.addBackgroundRenderer(colorRenderer);

		ElementLayout labelLayout = new ElementLayout("label");
		LabelRenderer labelRenderer = new LabelRenderer(view, this);
		labelRenderer.setAlignment(LabelRenderer.ALIGN_CENTER);

		labelLayout.setRenderer(labelRenderer);
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
			// baseColumn.append(verticalSpacing);
			// heightPixels += SPACING_PIXELS;
		}

		layoutManager.setBaseElementLayout(baseColumn);
	}

	private Column createPreviewRow(ElementLayout horizontalSpacing,
			ElementLayout verticalSpacing) {

		List<TablePerspective> tablePerspectives = view.getResolvedTablePerspectives();
		List<Integer> davidIds = node.getMappedDavidIDs();

		// Row previewRow = new Row("previewRow");
		// previewRow.setFrameColor(1, 0, 0, 1);
		// previewRow.setYDynamic(true);
		// previewRow.setPixelSizeY(100);

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
			ColorRenderer geneRowColorRenderer = new ColorRenderer(new float[] { 1, 1, 1,
					1 }, new float[] { 0, 0, 0, 1 }, 1);
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
				ContentRendererInitializor initializor = new ContentRendererInitializor(
						tablePerspective, davidId, view.getMappedDataRenderer(), view);
				// FIXME: Bad hack to determine categorical data
				if (currentDataDomain.getLabel().contains("Copy")) {
					tablePerspectivePreviewRenderer = new CategoricalContentPreviewRenderer(
							initializor);
				} else {
					tablePerspectivePreviewRenderer = new ContinuousContentPreviewRenderer(
							initializor);
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

		// geneColumn.append(verticalSpacing);

		// PathwayVertexRep vertexRep = node.getPathwayVertexRep();
		//
		// List<IDataDomain> dataDomains =
		// DataDomainManager.get().getDataDomainsByType(
		// "org.caleydo.datadomain.genetic");
		// List<GeneticDataDomain> geneticDataDomains = new
		// ArrayList<GeneticDataDomain>();
		// for (IDataDomain dataDomain : dataDomains) {
		// geneticDataDomains.add((GeneticDataDomain) dataDomain);
		// }
		// PathwayDataDomain pathwayDataDomain = (PathwayDataDomain)
		// DataDomainManager.get()
		// .getDataDomainByType("org.caleydo.datadomain.pathway");
		//
		// Map<Integer, Set<GeneticDataDomain>> geneIDsToDataDomains = new
		// HashMap<Integer, Set<GeneticDataDomain>>();
		// Set<GeneticDataDomain> dataDomainsWithMappedGenes = new
		// HashSet<GeneticDataDomain>();
		//
		// List<Integer> davidIDs = vertexRep.getDavidIDs();
		//
		// for (Integer davidID : davidIDs) {
		//
		// for (GeneticDataDomain dataDomain : geneticDataDomains) {
		// Set<Integer> ids = dataDomain.getGeneIDMappingManager().getIDAsSet(
		// pathwayDataDomain.getDavidIDType(), dataDomain.getGeneIDType(),
		// davidID);
		//
		// // TODO: This is only true if the davidID maps to one id of
		// // the
		// // genetic
		// // datadomain. However, matching multiple ids from different
		// // genetic
		// // datadomains is difficult.
		// if (ids != null && !ids.isEmpty()) {
		// // for (Integer id : ids) {
		// Set<GeneticDataDomain> set = geneIDsToDataDomains.get(davidID);
		// if (set == null) {
		// set = new HashSet<GeneticDataDomain>();
		//
		// geneIDsToDataDomains.put(davidID, set);
		// }
		// set.add(dataDomain);
		// dataDomainsWithMappedGenes.add(dataDomain);
		// // }
		//
		// }
		// }
		// }
		//

		// previewRow.setDebug(true);

		// List<GeneticDataDomain> mappedGeneticDataDomainList = new
		// ArrayList<GeneticDataDomain>(
		// dataDomainsWithMappedGenes.size());
		// List<Column> dataDomainColumns = new ArrayList<Column>(
		// dataDomainsWithMappedGenes.size());
		//
		// previewRow.append(horizontalSpacing);
		// for (GeneticDataDomain dataDomain : geneticDataDomains) {
		// if (dataDomainsWithMappedGenes.contains(dataDomain)) {
		// mappedGeneticDataDomainList.add(dataDomain);
		// Column column = new Column("dataDomainColumn");
		// column.setBottomUp(false);
		// dataDomainColumns.add(column);
		//
		// ElementLayout labelLayout = new ElementLayout("label");
		// LabelRenderer labelRenderer = new LabelRenderer(view,
		// dataDomain.getLabel());
		// labelRenderer.setAlignment(LabelRenderer.ALIGN_CENTER);
		//
		// labelLayout.setRenderer(labelRenderer);
		// labelLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		// column.append(labelLayout);
		// column.append(verticalSpacing);
		// column.append(verticalSpacing);
		//
		// previewRow.append(column);
		// previewRow.append(horizontalSpacing);
		// }
		// }
		//
		// List<Integer> davidIdList = new
		// ArrayList<Integer>(geneIDsToDataDomains.keySet());
		// Collections.sort(davidIdList);
		//
		// for (int i = 0; i < mappedGeneticDataDomainList.size(); i++) {
		// GeneticDataDomain currentDataDomain =
		// mappedGeneticDataDomainList.get(i);
		// Column column = dataDomainColumns.get(i);
		//
		// column.setRatioSizeX(1.0f / (float) dataDomainColumns.size());
		//
		// if (i == 0) {
		// heightPixels += CAPTION_HEIGHT_PIXELS + SPACING_PIXELS +
		// SPACING_PIXELS;
		// }
		//
		// for (int j = 0; j < davidIdList.size(); j++) {
		// Integer davidId = davidIdList.get(j);
		// Set<GeneticDataDomain> mappedDataDomains = geneIDsToDataDomains
		// .get(davidId);
		// if (mappedDataDomains.contains(currentDataDomain)) {
		// Row geneRow = new Row("dataDomain" + i + "geneRow");
		// ColorRenderer geneRowColorRenderer = new ColorRenderer(new float[] {
		// 1, 1, 1, 1 }, new float[] { 0, 0, 0, 1 }, 1);
		// geneRow.setRenderer(geneRowColorRenderer);
		// geneRow.setPixelSizeY(GENE_ROW_HEIGHT_PIXELS);
		// column.append(geneRow);
		// } else {
		// Row emptyRow = new Row("dataDomain" + i + "emptyRow");
		// emptyRow.setPixelSizeY(GENE_ROW_HEIGHT_PIXELS);
		// column.append(emptyRow);
		// }
		//
		// if (i == 0)
		// heightPixels += GENE_ROW_HEIGHT_PIXELS;
		//
		// if (j != davidIdList.size()) {
		// column.append(verticalSpacing);
		// if (i == 0)
		// heightPixels += SPACING_PIXELS;
		// }
		// }
		// }

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
				EventBasedSelectionManager selectionManager = view
						.getGeneSelectionManager();
				for (Integer davidId : node.getPathwayVertexRep().getDavidIDs()) {
					selectionManager.removeFromType(SelectionType.MOUSE_OVER, davidId);
				}
				selectionManager.triggerSelectionUpdateEvent();

				if (parent != null) {
					view.selectBranch(parent);
				} else {
					view.selectBranch(node);
				}
			}

			@Override
			public void mouseOver(Pick pick) {
				EventBasedSelectionManager selectionManager = view
						.getGeneSelectionManager();
				EventBasedSelectionManager metaboliteSelectionManager = view
						.getMetaboliteSelectionManager();
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
				EventBasedSelectionManager selectionManager = view
						.getGeneSelectionManager();
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
		view.removeAllIDPickingListeners(EPickingType.LINEARIZABLE_NODE.name(),
				node.getNodeId());
	}

}
