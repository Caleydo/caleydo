/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.image;

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

	protected FilePrefixGrouper images;

	protected String idCategoryImage;
	protected String idTypeImage;

	protected String idCategoryLayer;
	protected String idTypeLayer;

	public ImageSet() {
		images = new FilePrefixGrouper();
		IDCategory idCategoryImage = IDCategory.registerCategoryIfAbsent("Tissue Slice");
		IDCategory idCategoryLayer = IDCategory.registerCategoryIfAbsent("Marker");
		IDType idTypeImage = IDType.registerType("Tissue Slice", idCategoryImage, EDataType.STRING);
		IDType idTypeLayer = IDType.registerType("Marker", idCategoryLayer, EDataType.STRING);

		this.idCategoryImage = idCategoryImage.getCategoryName();
		this.idCategoryLayer = idCategoryLayer.getCategoryName();
		this.idTypeImage = idTypeImage.getTypeName();
		this.idTypeLayer = idTypeLayer.getTypeName();
	}

	public ImageSet(ImageSet other) {
		name = new String(other.name);
		images = new FilePrefixGrouper(other.images);
		idCategoryImage = new String(other.idCategoryImage);
		idTypeImage = new String(other.idTypeImage);
		idCategoryLayer = new String(other.idCategoryLayer);
		idTypeLayer = new String(other.idTypeLayer);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FilePrefixGrouper getImages() {
		return images;
	}

	public void setImages(FilePrefixGrouper image_grouper) {
		images = image_grouper;
	}

	/**
	 * @return the idCategoryImage, see {@link #idCategoryImage}
	 */
	public String getIdCategoryImage() {
		return idCategoryImage;
	}

	/**
	 * @param idCategoryImage setter, see {@link idCategoryImage}
	 */
	public void setIdCategoryImage(String idCategoryImage) {
		this.idCategoryImage = idCategoryImage;
	}

	/**
	 * @return the idTypeImage, see {@link #idTypeImage}
	 */
	public String getIdTypeImage() {
		return idTypeImage;
	}

	/**
	 * @param idTypeImage setter, see {@link idTypeImage}
	 */
	public void setIdTypeImage(String idTypeImage) {
		this.idTypeImage = idTypeImage;
	}

	/**
	 * @return the idCategoryLayer, see {@link #idCategoryLayer}
	 */
	public String getIDCategoryLayer() {
		return idCategoryLayer;
	}

	/**
	 * @param idCategoryLayer setter, see {@link idCategoryLayer}
	 */
	public void setIDCategoryLayer(String idCategoryLayer) {
		this.idCategoryLayer = idCategoryLayer;
	}

	/**
	 * @return the idTypeLayer, see {@link #idTypeLayer}
	 */
	public String getIDTypeLayer() {
		return idTypeLayer;
	}

	/**
	 * @param idTypeLayer setter, see {@link idTypeLayer}
	 */
	public void setIDTypeLayer(String idTypeLayer) {
		this.idTypeLayer = idTypeLayer;
	}

}
