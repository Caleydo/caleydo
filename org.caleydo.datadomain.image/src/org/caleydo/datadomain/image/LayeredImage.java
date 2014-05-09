/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.caleydo.core.serialize.INIParser;

import com.google.common.io.Files;

/**
 * @author Thomas Geymayer
 *
 */
public class LayeredImage {

	public static class Image {
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

		/**
		 * Copy all files of this image to the directory @a path
		 *
		 * @param path
		 * @throws IOException
		 */
		public void copy(File path, INIParser.Section cfgSection, String suffix) throws IOException {
			Files.copy(image, new File(path, image.getName()));
			cfgSection.setProperty("image" + suffix, image.getName());

			if (thumbnail != null) {
				Files.copy(thumbnail, new File(path, thumbnail.getName()));
				cfgSection.setProperty("image" + suffix + "_thumb", thumbnail.getName());
			}
		}
	}

	public static class Layer {

		protected LayeredImage parent;

		/** Border (just outline) */
		public Image border;

		/** Full region (filled, can be used for picking) */
		public Image area;

		/** Optional layer properties */
		public Properties props = new Properties();

		protected final String name;

		public Layer(String name, File borderImg, File borderThumb, File areaImg, File areaThumb) {
			if (borderImg != null || borderThumb != null)
				border = new Image(borderImg, borderThumb);

			if (areaImg != null || areaThumb != null)
				area = new Image(areaImg, areaThumb);

			this.name = name;
		}

		public String getName() {
			return name;
		}

		public LayeredImage getParent() {
			return parent;
		}

		/**
		 * Copy all files of this Layer to the directory @a path
		 *
		 * @param path
		 * @throws IOException
		 */
		public void copy(File path, INIParser.Section cfgSection) throws IOException {
			cfgSection.putAll(props);
			cfgSection.setProperty("name", name);

			if (border != null)
				border.copy(path, cfgSection, "_border");
			if (area != null)
				area.copy(path, cfgSection, "_area");
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

	public void addLayer(Layer layer) {
		layers.put(layer.getName(), layer);
		layer.parent = this;
	}

	public void addLayer(String name, File border, File borderThumb, File areaImg, File areaThumb) {
		addLayer(new Layer(name, border, borderThumb, areaImg, areaThumb));
	}

	public void addEmptyLayer(String name) {
		addLayer(new Layer(name, null, null, null, null));
	}

	public void setConfig(Properties cfg) {
		props = cfg;
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

	public void removeLayer(Layer layer) {
		layers.remove(layer.getName());
	}

	public Properties getConfig() {
		return props;
	}

	/**
	 * Copy all files of this image to the directory @a path and also write the
	 * config .ini
	 *
	 * @param path
	 * @throws IOException
	 */
	public void export(File path) throws IOException {
		path.mkdirs();

		INIParser ini = new INIParser();
		INIParser.Section baseSection = ini.getOrCreateSection("base");
		baseSection.setProperty("name", name);
		baseSection.putAll(props);

		if (base != null)
			base.copy(path, baseSection, "");

		int layer_index = 0;
		for (Layer layer: layers.values()) {
			layer.copy(path, ini.getOrCreateSection("layer" + ++layer_index));
		}

		FileWriter writer = new FileWriter(new File(path, name + ".ini"));
		ini.write(writer);
		writer.close();
	}

	public static LayeredImage fromINI(File cfg) {
		try {
			INIParser ini = new INIParser(cfg);
			LayeredImage img = new LayeredImage();

			// Base image
			INIParser.Section base = ini.getSections().get("base");
			if (base == null) {
				System.err.println("LayeredImage: missing [base] section");
				return null;
			}

			img.setName(base.extractString("name"));
			if (img.getName() == null || img.getName().isEmpty()) {
				System.err.println("LayeredImage: missing name");
				return null;
			}

			img.setBaseImage( base.extractFile("image"),
							  base.extractFile("image_thumb") );
			img.setConfig(base);

			// Parse layers
			for(Entry<String, INIParser.Section> section: ini.getSections().entrySet()) {
				if (section.getKey().equals("base"))
					continue;

				String name = section.getValue().extractString("name");
				if (name == null || name.isEmpty())
					name = section.getKey();

				INIParser.Section props = section.getValue();
				Layer layer = new Layer(
					name,
					props.extractFile("image_border"),
					props.extractFile("image_border_thumb"),
					props.extractFile("image_area"),
					props.extractFile("image_area_thumb")
				);
				layer.props = props;

				img.addLayer(layer);
			}

			return img;

		} catch (FileNotFoundException e) {
			System.err.println("Failed to open file: " + cfg.getPath());
		} catch (IOException e) {
			System.err.println("Failed to parse INI: " + e.getMessage());
		}

		return null;
	}
}
