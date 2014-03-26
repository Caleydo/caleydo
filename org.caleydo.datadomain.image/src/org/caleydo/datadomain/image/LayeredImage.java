/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.image;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Thomas Geymayer
 *
 */
public class LayeredImage {
	public class Image {
		public File image;
		public File thumbnail;

		public Image(File img, File thumb) {
			image = img;
			thumbnail = thumb;
		}
	}

	public class Layer {
		/** Highlight (usually just outline) */
		public Image highlight;

		/** Full region (filled, can be used for picking) */
		public Image mask;

		public Layer(File highlightImg, File highlightThumb, File maskImg, File maskThumb) {
			if (highlightImg != null && highlightThumb != null)
				highlight = new Image(highlightImg, highlightThumb);

			if (maskImg != null && maskThumb != null)
				mask = new Image(maskImg, maskThumb);
		}
	}

	protected SortedMap<String, Layer> layers = new TreeMap<>();
	protected Image base;
	protected Properties props = new Properties();

	public void setBaseImage(File img, File thumb) {
		base = new Image(img, thumb);
	}

	public void addLayer(String name, File img, File thumb, File maskImg, File maskThumb) {
		layers.put(name, new Layer(img, thumb, maskImg, maskThumb));
	}

	public void addLayer(String name, File img, File thumb) {
		addLayer(name, img, thumb, null, null);
	}

	public void addConfig(File cfg) throws IOException {
		FileReader reader = new FileReader(cfg);
		try {
			props.load(reader);
		} finally {
			reader.close();
		}
	}

	public Image getBaseImage() {
		return base;
	}

	public Layer getLayer(String name) {
		return layers.get(name);
	}

	public SortedMap<String, Layer> getLayers() {
		return layers;
	}

	public Properties getConfig() {
		return props;
	}
}
