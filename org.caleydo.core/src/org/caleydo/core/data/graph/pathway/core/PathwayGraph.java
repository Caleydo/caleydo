package org.caleydo.core.data.graph.pathway.core;

import java.io.Serializable;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.util.graph.core.Graph;

/**
 * Overall graph that holds all pathways
 * 
 * @author Marc Streit
 */
public class PathwayGraph
	extends Graph
	implements IUniqueObject, Serializable {
	private static final long serialVersionUID = 1L;

	private EPathwayDatabaseType type;

	private String sName;

	private String sTitle;

	private String sImageLink;

	private String sExternalLink;

	private int iWidth = -1;

	private int iHeight = -1;

	public PathwayGraph(final EPathwayDatabaseType type, final String sName, final String sTitle,
		final String sImageLink, final String sLink) {
		super(GeneralManager.get().getIDManager().createID(EManagedObjectType.PATHWAY));

		this.type = type;
		this.sName = sName;
		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sExternalLink = sLink;
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

	public final EPathwayDatabaseType getType() {

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
		return getTitle();
	}

	@Override
	public int getID() {
		return super.getId();
	}
}