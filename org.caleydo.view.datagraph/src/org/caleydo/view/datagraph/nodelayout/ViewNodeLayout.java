package org.caleydo.view.datagraph.nodelayout;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.layout.util.TextureRenderer;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.OverviewDataContainerRenderer;
import org.caleydo.view.datagraph.ViewNodeBackGroundRenderer;
import org.caleydo.view.datagraph.node.ViewNode;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ViewNodeLayout extends ANodeLayout {

	protected ViewNode node;
	protected AGLView representedView;
	protected String iconPath;
	protected String viewName;

	public ViewNodeLayout(ViewNode node, GLDataGraph view,
			DragAndDropController dragAndDropController) {
		super(view, dragAndDropController);
		this.node = node;
		this.representedView = node.getRepresentedView();

		dataContainerRenderer = new OverviewDataContainerRenderer(node, view,
				dragAndDropController, node.getDimensionGroups());

		setRepresentedViewInfo();
	}

	private void setRepresentedViewInfo() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry
				.getExtensionPoint("org.eclipse.ui.views");
		IExtension[] extensions = point.getExtensions();
		String viewID = representedView.getViewType();
		viewName = viewID;
		iconPath = null;
		boolean viewNameObtained = false;

		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension
					.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getAttribute("id").equals(viewID)) {
					viewName = element.getAttribute("name");
					iconPath = element.getAttribute("icon");
					viewNameObtained = true;
					break;

				}
			}
			if (viewNameObtained) {
				break;
			}
		}

		if (iconPath.equals("")) {
			iconPath = null;
		}
		if (iconPath != null) {
			ClassLoader classLoader = representedView.getClass()
					.getClassLoader();
			URL url = classLoader.getResource(iconPath);
			try {
				url = FileLocator.resolve(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			iconPath = new File(url.getFile()).getAbsolutePath();
		}
	}

	@Override
	public ElementLayout setupLayout() {
		Row baseRow = new Row("baseRow");

		PixelGLConverter pixelGLConverter = view.getPixelGLConverter();

		baseRow.setFrameColor(0, 0, 1, 0);

		baseRow.setRenderer(new BorderedAreaRenderer(view,
				PickingType.DATA_GRAPH_NODE, node.getID()));

		ElementLayout spacingLayoutX = createDefaultSpacingX();

		Column baseColumn = new Column();
		baseColumn.setPixelGLConverter(pixelGLConverter);

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		Row titleRow = new Row("titleRow");
		titleRow.setYDynamic(true);

		if (iconPath != null) {
			ElementLayout iconLayout = new ElementLayout("icon");
			iconLayout.setPixelGLConverter(pixelGLConverter);
			iconLayout.setPixelSizeX(CAPTION_HEIGHT_PIXELS);
			iconLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
			iconLayout.setRenderer(new TextureRenderer(iconPath, view
					.getTextureManager(), true));
			titleRow.append(iconLayout);
			titleRow.append(spacingLayoutX);
		}

		ElementLayout captionLayout = new ElementLayout("caption");
		captionLayout.setPixelGLConverter(pixelGLConverter);
		captionLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		captionLayout.setRatioSizeX(1);
		captionLayout.setRenderer(new LabelRenderer(view, viewName,
				PickingType.DATA_GRAPH_NODE, node.getID()));

		titleRow.append(captionLayout);

		ElementLayout lineSeparatorLayout = new ElementLayout("lineSeparator");
		lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
		lineSeparatorLayout.setPixelSizeY(LINE_SEPARATOR_HEIGHT_PIXELS);
		lineSeparatorLayout.setRatioSizeX(1);
		lineSeparatorLayout.setRenderer(new LineSeparatorRenderer(false));

		Row bodyRow = new Row("bodyRow");
		bodyRow.addBackgroundRenderer(new ViewNodeBackGroundRenderer(
				new float[] { 1, 1, 1, 1 }, iconPath, view.getTextureManager(),
				true));

		Column bodyColumn = new Column("bodyColumn");

		ElementLayout dataContainerLayout = new ElementLayout("compGroupOverview");
		dataContainerLayout.setRatioSizeY(1);
		dataContainerLayout.setRenderer(dataContainerRenderer);

		ElementLayout spacingLayoutY = new ElementLayout("spacingY");
		spacingLayoutY.setPixelGLConverter(pixelGLConverter);
		spacingLayoutY.setPixelSizeY(SPACING_PIXELS);
		spacingLayoutY.setRatioSizeX(0);

		bodyColumn.append(dataContainerLayout);
		bodyColumn.append(spacingLayoutY);

		bodyRow.append(bodyColumn);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(bodyRow);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(titleRow);
		baseColumn.append(spacingLayoutY);
		return null;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<? extends IDataDomain> getDataDomainType() {
		// TODO Auto-generated method stub
		return null;
	}

}
