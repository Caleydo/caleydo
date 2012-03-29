package org.caleydo.datadomain.pathway.graph;

import java.io.Serializable;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayDatabaseType;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;

/**
 * Overall graph that holds all pathways
 * 
 * @author Marc Streit
 */
public class PathwayGraph extends DirectedPseudograph<PathwayVertexRep, DefaultEdge> implements IUniqueObject, Serializable,
		Comparable<PathwayGraph> {
	
	private static final long serialVersionUID = 1L;

	private int id;
	
	private PathwayDatabaseType type;

	private String sName;

	private String sTitle;

	private String sImageLink;

	private String sExternalLink;

	private int iWidth = -1;

	private int iHeight = -1;

	public PathwayGraph(final PathwayDatabaseType type, final String sName,
			final String sTitle, final String sImageLink, final String sLink) {
		
		//super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.PATHWAY));

		super(DefaultEdge.class);

		id = GeneralManager.get().getIDCreator().createID(ManagedObjectType.PATHWAY);
		
		this.type = type;
		this.sName = sName;
		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sExternalLink = sLink;
	}
	
	@Override
	public int getID() {
		return id;
	}

	public final String getName() {

		return sName;
	}

	public final String getTitle() {

		return sTitle;
	}

	public final String getImageLink() {

		return sImageLink;
	}

	public final String getExternalLink() {

		return sExternalLink;
	}

	public final PathwayDatabaseType getType() {

		return type;
	}

	public final int getWidth() {

		return iWidth;
	}

	public final int getHeight() {

		return iHeight;
	}

	public void setWidth(final int iWidth) {

		this.iWidth = iWidth;
	}

	public void setHeight(final int iHeight) {

		this.iHeight = iHeight;
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
		return this.sTitle.compareToIgnoreCase(o.getTitle());
	}

}