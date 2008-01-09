package org.geneview.core.data.graph.core;

import org.geneview.core.manager.data.pathway.EPathwayDatabaseType;
import org.geneview.util.graph.core.Graph;

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
	
	public PathwayGraph(
			final EPathwayDatabaseType type,
			final int iKeggId,
			final String sName,
			final String sTitle,
			final String sImageLink,
			final String sLink) {

		super(iKeggId);
		
		this.type = type;
		this.iKeggId = iKeggId;
		this.sName = sName;
		this.sTitle = sTitle;
		this.sImageLink = sImageLink;
		this.sExternalLink = sLink;
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
	
	@Override
	public String toString() {
	
		return getTitle();
	}
}