/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.datadomain.image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.image.wizard.LoadImageSetPage;


/**
 * A set of images (base image + marker/highlight images/layers)
 *
 * @author Thomas Geymayer
 *
 */
public class ImageSet extends FilePrefixGrouper {

	protected String name;

	protected String idCategoryImage;
	protected String idTypeImage;

	protected String idCategoryLayer;
	protected String idTypeLayer;

	protected SortedMap<String, LayeredImage> images;

	public ImageSet() {
		IDCategory idCategoryImage = IDCategory.registerCategoryIfAbsent("Tissue Slice");
		idCategoryImage.initialize();
		IDCategory idCategoryLayer = IDCategory.registerCategoryIfAbsent("Marker");
		idCategoryLayer.initialize();
		IDType idTypeImage = IDType.registerType("Tissue Slice", idCategoryImage, EDataType.STRING);
		IDType idTypeLayer = IDType.registerType("Marker", idCategoryLayer, EDataType.STRING);

		this.idCategoryImage = idCategoryImage.getCategoryName();
		this.idCategoryLayer = idCategoryLayer.getCategoryName();
		this.idTypeImage = idTypeImage.getTypeName();
		this.idTypeLayer = idTypeLayer.getTypeName();
		images = new TreeMap<>();
	}

	public ImageSet(ImageSet other) {
		super(other);
		name = new String(other.name);
		idCategoryImage = new String(other.idCategoryImage);
		idTypeImage = new String(other.idTypeImage);
		idCategoryLayer = new String(other.idCategoryLayer);
		idTypeLayer = new String(other.idTypeLayer);
		images = new TreeMap<>(other.images);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void refreshGroups() {
		super.refreshGroups();

		images.clear();

		for (Entry<String, SortedSet<String>> group : groups.entrySet()) {
			String name = group.getKey();
			LayeredImage img = new LayeredImage();

			File baseImage = files.get(name);
			File baseThumb = files.get(name + "_thumb");
			img.setBaseImage(baseImage, baseThumb);

			String layerPrefix;
			int sepPos = name.indexOf('-');
			if( sepPos < 0 )
				layerPrefix = name;
			else
				layerPrefix = name.substring(0, sepPos + 1);

			for (String file : group.getValue()) {
				String suffix = stringSuffix(file, '_');

				if ( Arrays.asList("thumb", "border").contains(suffix) )
					continue;

				if ( LoadImageSetPage.EXTENSIONS_CFG.contains(suffix) ) {
					try {
						img.addConfig(files.get(file));
					} catch (IOException e) {
						e.printStackTrace();
					}
					continue;
				}

				String layerBaseName = file.substring(0, file.lastIndexOf("_highlight"));
				String borderName = layerBaseName + "_border";
				String layerName = layerBaseName.substring(layerBaseName.indexOf('_') + 1);

				File layerImage = files.get(borderName);
				File layerThumb = files.get(borderName + "_thumb");
				File maskImage = files.get(file);
				File maskThumb = files.get(file + "_thumb");

				img.addLayer(layerPrefix + layerName, layerImage, layerThumb, maskImage, maskThumb);
			}

			if( img.getLayers().isEmpty() ) {
				if( sepPos < 0 )
					sepPos = 0;
				int layerNamePos = name.indexOf('_', sepPos);

				if( layerNamePos >= 0 ) {
					String dummyLayerName = name.substring(layerNamePos + 1);
					name = name.substring(0, layerNamePos);

					img.addLayer(layerPrefix + dummyLayerName, null, null);
				}
			}

			img.setName(name);
			images.put(name, img);
		}
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
	 * @return
	 */
	public LayeredImage getImageForLayer(String name) {
		for(Map.Entry<String, LayeredImage> img: images.entrySet()) {
			if( img.getValue().getLayer(name) != null )
				return img.getValue();
		}

		return null;
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
