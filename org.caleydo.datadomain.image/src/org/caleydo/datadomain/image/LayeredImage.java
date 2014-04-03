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

			// Ensure an image is available
			if( image == null ) {
				image = thumbnail;
				thumbnail = null;
			}

			assert(image != null);
		}
	}

	public class Layer {
		/** Border (just outline) */
		public Image border;

		/** Full region (filled, can be used for picking) */
		public Image area;

		public Layer(File borderImg, File borderThumb, File areaImg, File areaThumb) {
			if (borderImg != null || borderThumb != null)
				border = new Image(borderImg, borderThumb);

			if (areaImg != null || areaThumb != null)
				area = new Image(areaImg, areaThumb);
		}
	}

	protected String name;
	protected SortedMap<String, Layer> layers = new TreeMap<>();
	protected Image base;
	protected Properties props = new Properties();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setBaseImage(File img, File thumb) {
		base = new Image(img, thumb);
	}

	public void addLayer(String name, File border, File borderThumb, File areaImg, File areaThumb) {
		layers.put(name, new Layer(border, borderThumb, areaImg, areaThumb));
	}

	public void addEmptyLayer(String name) {
		layers.put(name, null);
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
