package org.caleydo.view.heatmap.uncertainty;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.data.virtualarray.group.ContentGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.LineSeparatorRenderer;
import org.caleydo.core.view.opengl.layout.util.SpacerRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.texture.BarplotTextureRenderer;
import org.caleydo.view.heatmap.heatmap.renderer.texture.HeatMapTextureRenderer;

/**
 * Uncertainty overview heat map view.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * @author Clemens Holzhüter
 */

public class ClusterRenderer extends LayoutRenderer {

	public HeatMapTextureRenderer textureRenderer;
	private BarplotTextureRenderer dataUncBarTextureRenderer;
	public BarplotTextureRenderer visUncBarTextureRenderer;

	private GLUncertaintyHeatMap uncertaintyHeatMap;

	private Column clusterHeatMapLayout;
	private Column clusterDataUncBarLayout;
	private Column clusterVisUncBarLayout;

	private ViewFrustum viewFrustum;
	private LayoutManager templateRenderer;

	private Object template;

	private Row clusterLayout;

	private ContentVirtualArray clusterVA;

	private int clusterIndex;
	private int height;
	private int width;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public ClusterRenderer(GLUncertaintyHeatMap uncertaintyHeatMap,
			Row clusterLayout, ContentVirtualArray clusterVA, int clusterIndex) {

		this.uncertaintyHeatMap = uncertaintyHeatMap;
		this.clusterLayout = clusterLayout;
		this.clusterVA = clusterVA;
		this.clusterIndex = clusterIndex;
	}

	public void init() {

		StorageVirtualArray storageVA = uncertaintyHeatMap.getStorageVA();
		ISet set = uncertaintyHeatMap.getDataDomain().getSet();

		clusterHeatMapLayout = new Column("heatmap");
		clusterHeatMapLayout.setRatioSizeX(1f);

		clusterDataUncBarLayout = new Column("bar");
		clusterDataUncBarLayout.setPixelGLConverter(uncertaintyHeatMap
				.getParentGLCanvas().getPixelGLConverter());
		clusterDataUncBarLayout.setPixelSizeX(14);

		clusterVisUncBarLayout = new Column("bar2");
		clusterVisUncBarLayout.setPixelGLConverter(uncertaintyHeatMap
				.getParentGLCanvas().getPixelGLConverter());
		clusterVisUncBarLayout.setPixelSizeX(14);

		textureRenderer = new HeatMapTextureRenderer(uncertaintyHeatMap,
				clusterHeatMapLayout);
		clusterHeatMapLayout.setRenderer(textureRenderer);

		dataUncBarTextureRenderer = new BarplotTextureRenderer();
		clusterDataUncBarLayout.setRenderer(dataUncBarTextureRenderer);
		dataUncBarTextureRenderer.setOrientationLeft(true);

		visUncBarTextureRenderer = new BarplotTextureRenderer();
		clusterVisUncBarLayout.setRenderer(visUncBarTextureRenderer);

		clusterLayout.append(clusterVisUncBarLayout);
		
		{
			ElementLayout lineSeparatorLayout = new ElementLayout(
					"lineSeparator");

			PixelGLConverter pixelGLConverter = uncertaintyHeatMap
					.getParentGLCanvas().getPixelGLConverter();
			lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
			lineSeparatorLayout.setPixelSizeX(1);
			lineSeparatorLayout.setRenderer(new SpacerRenderer(false));
			lineSeparatorLayout.setFrameColor(0.0f, 0.0f, 0.0f, 0.2f);
			clusterLayout.append(lineSeparatorLayout);

		}
		clusterLayout.append(clusterDataUncBarLayout);
		{
			ElementLayout lineSeparatorLayout = new ElementLayout(
					"lineSeparator");

			PixelGLConverter pixelGLConverter = uncertaintyHeatMap
					.getParentGLCanvas().getPixelGLConverter();
			lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
			lineSeparatorLayout.setPixelSizeX(1);
			lineSeparatorLayout.setRenderer(new SpacerRenderer(false));
			lineSeparatorLayout.setFrameColor(0.0f, 0.0f, 0.0f, 0.8f);
			clusterLayout.append(lineSeparatorLayout);

		}
		clusterLayout.append(clusterHeatMapLayout);
		{
			ElementLayout lineSeparatorLayout = new ElementLayout(
					"lineSeparator");

			PixelGLConverter pixelGLConverter = uncertaintyHeatMap
					.getParentGLCanvas().getPixelGLConverter();
			lineSeparatorLayout.setPixelGLConverter(pixelGLConverter);
			lineSeparatorLayout.setPixelSizeX(1);
			lineSeparatorLayout.setRenderer(new SpacerRenderer(false));
			lineSeparatorLayout.setFrameColor(0.0f, 0.0f, 0.0f, 0.3f);
			clusterLayout.append(lineSeparatorLayout);

		}
		

		textureRenderer.init(uncertaintyHeatMap, set, clusterVA, storageVA,
				clusterIndex);

		dataUncBarTextureRenderer.init(set, clusterVA, storageVA,
				uncertaintyHeatMap.getColorMapper());

		visUncBarTextureRenderer.init(set, clusterVA, storageVA,
				uncertaintyHeatMap.getColorMapper());

		visUncBarTextureRenderer.setLightCertainColor(uncertaintyHeatMap.VIS_UNC);
		visUncBarTextureRenderer.setLightUnCertainColor(uncertaintyHeatMap.VIS_UNC);
		//visUncBarTextureRenderer.setDarkColor(uncertaintyHeatMap.darkDark);

	}

	@Override
	public void render(GL2 gl) {

	}

}
