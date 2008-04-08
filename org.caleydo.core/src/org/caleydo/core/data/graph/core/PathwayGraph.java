package org.caleydo.core.data.graph.core;

import org.caleydo.core.manager.data.pathway.EPathwayDatabaseType;
import org.caleydo.util.graph.core.Graph;

/**
 * 
 * @author Marc Streit
 *
 */
public class PathwayGraph
extends Graph {
	
	private EPathwayDatabaseType type;
	
	private int iKeggId;

	private String sName;
	
	private String sTitle;

	private String sImageLink;

	private String sExternalLink;
	
	private int iWidth;
	
	private int iHeight;
	
	public PathwayGraph(
			final EPathwayDatabaseType type,
			final int iKeggId,
			final String sName,
			final String sTitle,
			final String sImageLink,
			final String sLink,
			final int iWidth,
			final int iHeight) {

		super(iKeggId);
		
		this.type = type;
		this.iKeggId = iKeggId;
		this.sName = sName;
		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sExternalLink = sLink;
		this.iWidth = iWidth;
		this.iHeight = iHeight;
	}
	
	public int getKeggId() {
		
		return iKeggId;
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
	
	@Override
	public String toString() {
	
		return getTitle();
	}
}