package cerberus.manager.data.pathway;

import java.util.HashMap;

import org.geneview.graph.core.Graph;

import cerberus.data.graph.core.PathwayGraph;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.data.IPathwayManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;

/**
 * The pathway manager is in charge for 
 * creating and handling the pathways.
 * The class is implemented as a singleton.
 * 
 * @author Marc Streit
 *
 */
public class PathwayManager 
extends AAbstractManager
implements IPathwayManager {

	private HashMap<Integer, PathwayGraph> hashPathwayLUT;
	
	private String sPathwayXMLPath;	
	
	private String sPathwayImagePath;
	
	private String sPathwayImageMapPath;
	
	/**
	 * Root pathway contains all nodes that are loaded into the system.
	 * Therefore it represents the overall topological network.
	 * (The root pathway is independent from the representation of the nodes.)
	 */
	private Graph rootPathwayGraph;
	
	/**
	 * Constructor
	 */
	public PathwayManager(final IGeneralManager refGeneralManager) {

		super(refGeneralManager, 
			IGeneralManager.iUniqueId_TypeOffset_Pathways_Pathway,
			ManagerType.DATA_PATHWAY_ELEMENT );
		
		hashPathwayLUT = new HashMap<Integer, PathwayGraph>();
		
		rootPathwayGraph = new Graph(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.manager.data.IPathwayManagerNew#createPathway(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public PathwayGraph createPathway(
			final int iKEGGId,
			final String sName,
			final String sTitle, 
			final String sImageLink, 
			final String sExternalLink) {

		PathwayGraph pathway = new PathwayGraph(
				iKEGGId, sName, sTitle, sImageLink, sExternalLink);

		hashPathwayLUT.put(iKEGGId, pathway);
		
		return pathway;
	}
	
	public boolean loadPathwayById(int iPathwayID) {
		
		// Check if pathway was previously loaded
		if (hashPathwayLUT.containsKey(iPathwayID))
		{
			refGeneralManager.getSingelton().logMsg(
					this.getClass().getSimpleName() + 
					": loadPathwayById(): Pathway "+ iPathwayID + " is already loaded. SKIP.",
					LoggerType.VERBOSE);
			
			return true;
		}

		String sPathwayFilePath = "";
		boolean bLoadingOK = false;
		
		if (iPathwayID < 10)
		{
			sPathwayFilePath = "hsa0000" + Integer.toString(iPathwayID);
		}
		else if (iPathwayID < 100 && iPathwayID >= 10)
		{
			sPathwayFilePath = "hsa000" + Integer.toString(iPathwayID);
		}
		else if (iPathwayID < 1000 && iPathwayID >= 100)
		{
			sPathwayFilePath = "hsa00" + Integer.toString(iPathwayID);
		}
		else if (iPathwayID < 10000 && iPathwayID >= 1000)
		{
			sPathwayFilePath = "hsa0" + Integer.toString(iPathwayID);
		}
		
		sPathwayFilePath = sPathwayXMLPath + sPathwayFilePath +".xml";		
		
		bLoadingOK = refGeneralManager.getSingelton().getXmlParserManager().parseXmlFileByName(sPathwayFilePath);

		if (bLoadingOK)
			return true;
		
		refGeneralManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": loadPathwayById(): No HSA pathway available - " +
				"try to load reference pathway.",
				LoggerType.VERBOSE);
		
		// Replace HSA with MAP and therefore try to load reference pathway
		sPathwayFilePath = sPathwayFilePath.replace("hsa", "map");
		
		return refGeneralManager.getSingelton().getXmlParserManager().parseXmlFileByName(sPathwayFilePath);
	}
	
	public Graph getRootPathway() {
		
		return rootPathwayGraph;
	}
	
	public String getPathwayXMLPath() {
		
		assert !sPathwayXMLPath.isEmpty() : "Pathway XML path is not set!";
		
		return sPathwayXMLPath;
	}
	
	public final String getPathwayImagePath() {

		assert !sPathwayImagePath.isEmpty() : "Pathway image path is not set!";
		
		return sPathwayImagePath;
	}
	
	public void setPathwayImagePath(String sPathwayImagePath) {
		
		this.sPathwayImagePath = sPathwayImagePath;
	}

	public final String getPathwayImageMapPath() {
		
		assert !sPathwayImageMapPath.isEmpty() : "Pathway image map path is not set!";
		
		return sPathwayImageMapPath;
	}
	
	public void setPathwayImageMapPath(String sPathwayImageMapPath) {
		
		this.sPathwayImageMapPath = sPathwayImageMapPath;
	}
	
	public Object getItem(int iItemId) {

		return(hashPathwayLUT.get(iItemId));
	}


	public void setPathwayXMLPath(final String sPathwayXMLPath) {

		this.sPathwayXMLPath = sPathwayXMLPath;
	}
	
	@Override
	public boolean hasItem(int iItemId) {

		if (hashPathwayLUT.containsKey(iItemId))
			return true;
			
		return false;
	}

	@Override
	public boolean registerItem(Object registerItem, int itemId,
			ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean unregisterItem(int itemId, ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}
}
