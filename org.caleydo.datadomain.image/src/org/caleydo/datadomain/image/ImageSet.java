/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.image;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;


/**
 * A set of images (base image + marker/highlight images/layers)
 *
 * @author Thomas Geymayer
 *
 */
public class ImageSet {

	protected String name;

	protected IDCategory idCategoryImage;
	protected IDType idTypeImage;

	protected IDCategory idCategoryLayer;
	protected IDType idTypeLayer;

	protected SortedMap<String, LayeredImage> images;

	/**
	 * Allowed config file extensions.
	 */
	public static final List<String> EXTENSIONS_CFG = Arrays.asList("ini");

	public ImageSet() {
		idCategoryImage = IDCategory.registerCategoryIfAbsent("Tissue Slice");
		idCategoryLayer = IDCategory.registerCategoryIfAbsent("Marker");
		// idCategoryImage.setDenominationPlural("Tissue Slices");
		// idCategoryLayer.setDenominationPlural("Markers");
		idTypeImage = IDType.registerType("Tissue Slice", idCategoryImage, EDataType.STRING);
		idTypeLayer = IDType.registerType("Marker", idCategoryLayer, EDataType.STRING);

		images = new TreeMap<>();
	}

	public ImageSet(ImageSet other) {
		name = new String(other.name);
		idCategoryImage = other.idCategoryImage;
		idTypeImage = other.idTypeImage;
		idCategoryLayer = other.idCategoryLayer;
		idTypeLayer = other.idTypeLayer;
		images = new TreeMap<>(other.images);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Add a new image (an image with the same name will be replaced)
	 * @param img
	 */
	public void addImage(LayeredImage img) {
		images.put(img.getName(), img);
	}

	public void removeImage(LayeredImage img) {
		images.remove(img.getName());
	}

	/**
	 * Import a single INI file or recursively all INI files from the given
	 * directory.
	 *
	 * @param path INI file or directory
	 */
	public void importFrom(File path) {
		if (path.isDirectory()) {
			for (File child: path.listFiles())
				importFrom(child);
		}

		int ext_sep = path.getName().lastIndexOf('.');
		if(ext_sep < 0)
			return;

		String ext = path.getName().substring(ext_sep + 1).toLowerCase();

		if (!ImageSet.EXTENSIONS_CFG.contains(ext))
			return;

		LayeredImage img = LayeredImage.fromINI(path);
		if (img != null) {
			addImage(img);
		} else {
			System.err.println("Failed to import: " + path);
		}
	}

	public List<LayeredImage> getImages() {
		return new ArrayList<>(images.values());
	}

	/**
	 * Get names of all images (usually the name of each base image)
	 *
	 * @return
	 */
	public List<String> getImageNames() {
		return new ArrayList<>(images.keySet());
	}

	/**
	 * Get the image with the given name
	 *
	 * @param name
	 * @return
	 */
	public LayeredImage getImage(String name) {
		return images.get(name);
	}

	/**
	 * Get the first image which contains a layer with the given name
	 *
	 * @param name
	 * @return First matching image (null, if no match)
	 */
	public LayeredImage getImageForLayer(String name) {
		for(Map.Entry<String, LayeredImage> img: images.entrySet()) {
			if( img.getValue().getLayer(name) != null )
				return img.getValue();
		}

		return null;
	}

	/**
	 * Get a list of all images which have a layer with the given name
	 *
	 * @param name
	 * @return List of matching images (empty, if no match)
	 */
	public List<LayeredImage> getAllImagesForLayer(String name) {
		List<LayeredImage> imageList = new ArrayList<>();

		for(Map.Entry<String, LayeredImage> img: images.entrySet()) {
			if( img.getValue().getLayer(name) != null )
				imageList.add(img.getValue());
		}

		return imageList;
	}

	/**
	 * @return the idCategoryImage, see {@link #idCategoryImage}
	 */
	public IDCategory getIDCategoryImage() {
		return idCategoryImage;
	}

	/**
	 * @param idCategoryImage setter, see {@link idCategoryImage}
	 */
	public void setIDCategoryImage(IDCategory idCategoryImage) {
		this.idCategoryImage = idCategoryImage;
	}

	/**
	 * @return the idTypeImage, see {@link #idTypeImage}
	 */
	public IDType getIDTypeImage() {
		return idTypeImage;
	}

	/**
	 * @param idTypeImage setter, see {@link idTypeImage}
	 */
	public void setIDTypeImage(IDType idTypeImage) {
		this.idTypeImage = idTypeImage;
	}

	/**
	 * @return the idCategoryLayer, see {@link #idCategoryLayer}
	 */
	public IDCategory getIDCategoryLayer() {
		return idCategoryLayer;
	}

	/**
	 * @param idCategoryLayer setter, see {@link idCategoryLayer}
	 */
	public void setIDCategoryLayer(IDCategory idCategoryLayer) {
		this.idCategoryLayer = idCategoryLayer;
	}

	/**
	 * @return the idTypeLayer, see {@link #idTypeLayer}
	 */
	public IDType getIDTypeLayer() {
		return idTypeLayer;
	}

	/**
	 * @param idTypeLayer setter, see {@link idTypeLayer}
	 */
	public void setIDTypeLayer(IDType idTypeLayer) {
		this.idTypeLayer = idTypeLayer;
	}

}
