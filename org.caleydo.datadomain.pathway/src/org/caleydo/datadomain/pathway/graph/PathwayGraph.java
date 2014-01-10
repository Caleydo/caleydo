/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.graph;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.media.opengl.GLException;

import org.caleydo.core.id.IDCreator;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.base.IUniqueObject;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Graph of a pathway
 * 
 * @author Marc Streit
 */
public class PathwayGraph extends DefaultDirectedGraph<PathwayVertexRep, DefaultEdge>
 implements IUniqueObject,
		Serializable, Comparable<PathwayGraph>, ILabeled {

	private static final long serialVersionUID = 1L;

	private int id;

	private EPathwayDatabaseType type;

	private String name;

	private String title;

	private File image;

	private String externalLink;

	private int width = -1;

	private int height = -1;

	public PathwayGraph(final EPathwayDatabaseType type, final String name,
 final String title, final File image,
			final String link) {

		super(DefaultEdge.class);

		id = IDCreator.createPersistentIntID(PathwayGraph.class);

		this.type = type;
		this.name = name;
		this.title = title;
		this.image = image;
		this.externalLink = link;
	}

	@Override
	public int getID() {
		return id;
	}

	public final String getName() {
		return name;
	}

	@Override
	public String getLabel() {
		return getTitle() + " (" + getType().getLabel() + ")";
	}

	public final String getTitle() {
		return title;
	}
	public final String getExternalLink() {
		return externalLink;
	}

	public final EPathwayDatabaseType getType() {
		return type;
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	public void setWidth(final int iWidth) {
		this.width = iWidth;
	}

	public void setHeight(final int iHeight) {
		this.height = iHeight;
	}

	@Override
	public String toString() {
		return type + ": " + getTitle();
	}

	/**
	 * Returns the internal unique-id as hashcode
	 *
	 * @return internal unique-id as hashcode
	 */
	@Override
	public int hashCode() {
		return getID();
	}

	/**
	 * Checks if the given object is equals to this one by comparing the
	 * internal unique-id
	 *
	 * @return <code>true</code> if the 2 objects are equal, <code>false</code>
	 *         otherwise
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof IUniqueObject) {
			return this.getID() == ((IUniqueObject) other).getID();
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(PathwayGraph o) {
		return this.title.compareToIgnoreCase(o.getTitle());
	}

	public Texture getTexture() {
		try {
			return TextureIO.newTexture(image, true);
		} catch (GLException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the image, see {@link #image}
	 */
	public File getImage() {
		return image;
	}

}
