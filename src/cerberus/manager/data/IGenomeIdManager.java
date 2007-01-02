/**
 * 
 */
package cerberus.manager.data;

import java.util.ArrayList;
import java.util.Map;

import cerberus.manager.IGeneralManager;

import cerberus.data.mapping.GenomeIdType;
import cerberus.data.mapping.GenomeMappingType;

/**
 * @author Michael Kalkusch
 *
 */
public interface IGenomeIdManager extends IGeneralManager {

	public String getNameById( int iUniqueId );
	
	public int getIdByName( String name );
	
	public int getIdByType( final int iCerberusId, final GenomeIdType type );
	
	public ArrayList<Integer> getIdListByType( final int iCerberusId, 
			final GenomeIdType type );
	
	public String getIdListByTypeToString( final int iCerberusId, 
			final GenomeIdType type );
	
	public void setIdLUTByType( final int iCerberusId, final GenomeIdType type );
	
	public void buildLUT_startEditingSetTypes( final GenomeIdType typeFromId, 
			final GenomeIdType typeToId );
	
	//public void buildLUT( Object first, Object second );
	
	public void buildLUT_stopEditing();
	
	public boolean isBuildLUTfinished();
	
	public Map getMapByGenomeType( final GenomeMappingType type );
	
}
