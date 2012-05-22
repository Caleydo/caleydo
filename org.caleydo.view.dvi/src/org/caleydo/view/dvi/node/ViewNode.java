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
package org.caleydo.view.dvi.node;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.IDataContainerBasedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.BorderedAreaRenderer;
import org.caleydo.core.view.opengl.layout.util.TextureRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.ViewNodeBackGroundRenderer;
import org.caleydo.view.dvi.contextmenu.OpenViewItem;
import org.caleydo.view.dvi.datacontainer.ADataContainerRenderer;
import org.caleydo.view.dvi.layout.AGraphLayout;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class ViewNode extends ADefaultTemplateNode {

	// private DataContainerListRenderer overviewDataContainerRenderer;
	protected AGLView representedView;
	protected Set<IDataDomain> dataDomains;
	protected String viewName;
	protected String iconPath;

	public ViewNode(AGraphLayout graphLayout, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController, Integer id,
			AGLView representedView) {
		super(graphLayout, view, dragAndDropController, id);

		this.representedView = representedView;

		registerPickingListeners();
		setRepresentedViewInfo();
//		setupLayout();
	}

	protected void registerPickingListeners() {

		view.addIDPickingListener(new APickingListener() {

			@Override
			public void rightClicked(Pick pick) {
				view.getContextMenuCreator().addContextMenuItem(
						new OpenViewItem(representedView));
			}

			@Override
			public void doubleClicked(Pick pick) {
				view.openView(representedView);
			}

		}, DATA_GRAPH_NODE_PICKING_TYPE, id);

	}

	private void setRepresentedViewInfo() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint("org.eclipse.ui.views");
		IExtension[] extensions = point.getExtensions();
		String viewID = representedView.getViewType();
		viewName = viewID;
		iconPath = null;
		boolean viewNameObtained = false;

		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getAttribute("id").equals(viewID)) {
					viewName = representedView.getViewName();
					// element.getAttribute("name");
					iconPath = element.getAttribute("icon");
					viewNameObtained = true;
					break;

				}
			}
			if (viewNameObtained) {
				break;
			}
		}

		if (iconPath != null && iconPath.equals("")) {
			iconPath = null;
		}
		if (iconPath != null) {

			// FIXME: we need to find a solution that works in the export
			// version as well
			ClassLoader classLoader = representedView.getClass().getClassLoader();
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
	protected ElementLayout setupLayout() {
		Row baseRow = createDefaultBaseRow(BorderedAreaRenderer.DEFAULT_COLOR, id);

		ElementLayout spacingLayoutX = createDefaultSpacingX();

		baseColumn = new Column();

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		Row titleRow = new Row("titleRow");
		titleRow.setYDynamic(true);

		if (iconPath != null) {
			ElementLayout iconLayout = new ElementLayout("icon");
			iconLayout.setPixelSizeX(CAPTION_HEIGHT_PIXELS);
			iconLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
			iconLayout
					.setRenderer(new TextureRenderer(iconPath, view.getTextureManager()));
			titleRow.append(iconLayout);
			titleRow.append(spacingLayoutX);
		}

		ElementLayout captionLayout = createDefaultCaptionLayout(viewName, id);

		titleRow.append(captionLayout);

		ElementLayout lineSeparatorLayout = createDefaultLineSeparatorLayout();

		Row bodyRow = new Row("bodyRow");
		bodyRow.addBackgroundRenderer(new ViewNodeBackGroundRenderer(new float[] { 1, 1,
				1, 1 }, iconPath, view.getTextureManager()));

		bodyColumn = new Column("bodyColumn");

		ElementLayout bodySpacingLayoutY = new ElementLayout("compGroupOverview");
		// if (representedView instanceof ATableBasedView) {
		// overviewDataContainerRenderer = new DataContainerListRenderer(this,
		// view, dragAndDropController, new ArrayList<DataContainer>());
		// } else {
		// overviewDataContainerRenderer = new DataContainerListRenderer(this,
		// view, dragAndDropController, getDataContainers());
		// }

		bodySpacingLayoutY.setRatioSizeY(1);

		ElementLayout spacingLayoutY = createDefaultSpacingY();

		bodyColumn.append(bodySpacingLayoutY);
		// bodyColumn.append(spacingLayoutY);

		bodyRow.append(bodyColumn);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(bodyRow);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(titleRow);
		baseColumn.append(spacingLayoutY);

		setUpsideDown(isUpsideDown);

		return baseRow;
	}

	@Override
	public List<DataContainer> getDataContainers() {

		if (representedView instanceof IDataContainerBasedView) {
			return ((IDataContainerBasedView) representedView).getDataContainers();
			// DataContainer dataContainer = ((ATableBasedView) representedView)
			// .getDataContainers();
			// List<DataContainer> containers = new ArrayList<DataContainer>();
			// containers.add(dataContainer);
			// return containers;
		}

		// List<ADimensionGroupData> groups = representedView.get();
		// if (groups == null) {
		return new ArrayList<DataContainer>();
		// }
		// return new ArrayList<DataContainer>(groups);
	}

	public void setDataDomains(Set<IDataDomain> dataDomains) {
		this.dataDomains = dataDomains;
	}

	public Set<IDataDomain> getDataDomains() {
		return dataDomains;
	}

	public AGLView getRepresentedView() {
		return representedView;
	}

	@Override
	public void update() {
		// if (representedView instanceof ATableBasedView) {
		// overviewDataContainerRenderer
		// .setDataContainers(new ArrayList<DataContainer>());
		// } else {
		// overviewDataContainerRenderer
		// .setDataContainers(getDataContainers());
		// }
		recalculateNodeSize();
	}

	@Override
	protected ADataContainerRenderer getDataContainerRenderer() {
		return null;
	}

	@Override
	public void destroy() {
		super.destroy();
		// overviewDataContainerRenderer.destroy();
		view.removeAllIDPickingListeners(DATA_GRAPH_NODE_PICKING_TYPE, id);
	}

	@Override
	public boolean showsDataContainers() {
		return false;
	}

	@Override
	protected int getMinTitleBarWidthPixels() {
		float textWidth = view.getTextRenderer().getRequiredTextWidthWithMax(
				representedView.getViewName(),
				pixelGLConverter.getGLHeightForPixelHeight(CAPTION_HEIGHT_PIXELS),
				MIN_TITLE_BAR_WIDTH_PIXELS);

		return pixelGLConverter.getPixelWidthForGLWidth(textWidth)
				+ CAPTION_HEIGHT_PIXELS + SPACING_PIXELS;
	}

	@Override
	public String getCaption() {
		return viewName;
	}

}
