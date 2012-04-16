/**
 * 
 */
package org.caleydo.view.linearizedpathway.node.mode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.layout.util.ILabelTextProvider;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.linearizedpathway.GLLinearizedPathway;
import org.caleydo.view.linearizedpathway.PickingType;
import org.caleydo.view.linearizedpathway.node.ALinearizableNode;
import org.caleydo.view.linearizedpathway.node.GeneNode;

/**
 * Preview mode for gene {@link GeneNode}s.
 * 
 * @author Christian
 * 
 */
public class GeneNodePreviewMode extends ALayoutBasedNodeMode implements
		ILabelTextProvider {

	protected static final int MIN_NODE_WIDTH_PIXELS = 150;
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
	public GeneNodePreviewMode(GLLinearizedPathway view) {
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
		colorRenderer = new ColorRenderer(new float[] { 1, 1, 1, 1 });
		colorRenderer.setView(view);
		colorRenderer.setBorderColor(new float[] { 0, 0, 0, 1 });
		colorRenderer
				.addPickingID(PickingType.LINEARIZABLE_NODE.name(), node.getNodeId());
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
		Row previewRow = null;

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
			baseColumn.append(verticalSpacing);
			heightPixels += SPACING_PIXELS;
		}

		layoutManager.setBaseElementLayout(baseColumn);
	}

	private Row createPreviewRow(ElementLayout horizontalSpacing,
			ElementLayout verticalSpacing) {
		PathwayVertexRep vertexRep = node.getPathwayVertexRep();

		List<IDataDomain> dataDomains = DataDomainManager.get().getDataDomainsByType(
				"org.caleydo.datadomain.genetic");
		List<GeneticDataDomain> geneticDataDomains = new ArrayList<GeneticDataDomain>();
		for (IDataDomain dataDomain : dataDomains) {
			geneticDataDomains.add((GeneticDataDomain) dataDomain);
		}
		PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get()
				.getDataDomainByType("org.caleydo.datadomain.pathway");

		Map<Integer, Set<GeneticDataDomain>> geneIDsToDataDomains = new HashMap<Integer, Set<GeneticDataDomain>>();
		Set<GeneticDataDomain> dataDomainsWithMappedGenes = new HashSet<GeneticDataDomain>();

		List<Integer> davidIDs = vertexRep.getDavidIDs();

		for (Integer davidID : davidIDs) {

			for (GeneticDataDomain dataDomain : geneticDataDomains) {
				Set<Integer> ids = dataDomain.getGeneIDMappingManager().getIDAsSet(
						pathwayDataDomain.getDavidIDType(), dataDomain.getGeneIDType(),
						davidID);

				// TODO: This is only true if the davidID maps to one id of
				// the
				// genetic
				// datadomain. However, matching multiple ids from different
				// genetic
				// datadomains is difficult.
				if (ids != null && !ids.isEmpty()) {
					// for (Integer id : ids) {
					Set<GeneticDataDomain> set = geneIDsToDataDomains.get(davidID);
					if (set == null) {
						set = new HashSet<GeneticDataDomain>();

						geneIDsToDataDomains.put(davidID, set);
					}
					set.add(dataDomain);
					dataDomainsWithMappedGenes.add(dataDomain);
					// }

				}
			}
		}

		Row previewRow = new Row("previewRow");
		previewRow.setFrameColor(1, 0, 0, 1);
		// previewRow.setDebug(true);

		List<GeneticDataDomain> mappedGeneticDataDomainList = new ArrayList<GeneticDataDomain>(
				dataDomainsWithMappedGenes.size());
		List<Column> dataDomainColumns = new ArrayList<Column>(
				dataDomainsWithMappedGenes.size());

		previewRow.append(horizontalSpacing);
		for (GeneticDataDomain dataDomain : geneticDataDomains) {
			if (dataDomainsWithMappedGenes.contains(dataDomain)) {
				mappedGeneticDataDomainList.add(dataDomain);
				Column column = new Column("dataDomainColumn");
				column.setBottomUp(false);
				dataDomainColumns.add(column);

				ElementLayout labelLayout = new ElementLayout("label");
				LabelRenderer labelRenderer = new LabelRenderer(view,
						dataDomain.getLabel());
				labelRenderer.setAlignment(LabelRenderer.ALIGN_CENTER);

				labelLayout.setRenderer(labelRenderer);
				labelLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
				column.append(labelLayout);
				column.append(verticalSpacing);
				column.append(verticalSpacing);

				previewRow.append(column);
				previewRow.append(horizontalSpacing);
			}
		}

		List<Integer> davidIdList = new ArrayList<Integer>(geneIDsToDataDomains.keySet());
		Collections.sort(davidIdList);

		for (int i = 0; i < mappedGeneticDataDomainList.size(); i++) {
			GeneticDataDomain currentDataDomain = mappedGeneticDataDomainList.get(i);
			Column column = dataDomainColumns.get(i);

			column.setRatioSizeX(1.0f / (float) dataDomainColumns.size());

			if (i == 0) {
				heightPixels += CAPTION_HEIGHT_PIXELS + SPACING_PIXELS + SPACING_PIXELS;
			}

			for (int j = 0; j < davidIdList.size(); j++) {
				Integer davidId = davidIdList.get(j);
				Set<GeneticDataDomain> mappedDataDomains = geneIDsToDataDomains
						.get(davidId);
				if (mappedDataDomains.contains(currentDataDomain)) {
					Row geneRow = new Row("dataDomain" + i + "geneRow");
					ColorRenderer geneRowColorRenderer = new ColorRenderer(new float[] {
							1, 1, 1, 1 }, new float[] { 0, 0, 0, 1 }, 1);
					geneRow.setRenderer(geneRowColorRenderer);
					geneRow.setPixelSizeY(GENE_ROW_HEIGHT_PIXELS);
					column.append(geneRow);
				} else {
					Row emptyRow = new Row("dataDomain" + i + "emptyRow");
					emptyRow.setPixelSizeY(GENE_ROW_HEIGHT_PIXELS);
					column.append(emptyRow);
				}

				if (i == 0)
					heightPixels += GENE_ROW_HEIGHT_PIXELS;

				if (j != davidIdList.size()) {
					column.append(verticalSpacing);
					if (i == 0)
						heightPixels += SPACING_PIXELS;
				}
			}
		}

		return previewRow;
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
				view.selectBranch(node);
			}

			@Override
			public void mouseOver(Pick pick) {
				node.setSelectionType(SelectionType.MOUSE_OVER);
				colorRenderer.setColor(SelectionType.MOUSE_OVER.getColor());
				view.setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {
				node.setSelectionType(SelectionType.NORMAL);
				colorRenderer.setColor(new float[] { 1, 1, 1, 1 });
				view.setDisplayListDirty();
			}
		}, PickingType.LINEARIZABLE_NODE.name(), node.getNodeId());

	}

	@Override
	public void unregisterPickingListeners() {
		super.unregisterPickingListeners();
		view.removeAllIDPickingListeners(PickingType.LINEARIZABLE_NODE.name(),
				node.getNodeId());
	}

	@Override
	public String getLabelText() {
		return node.getCaption();
	}

}
