/**
 * 
 */
package cerberus.manager.data;

import java.util.ArrayList;

import cerberus.manager.IGeneralManager;

import cerberus.data.mapping.GenomeIdType;

/**
 * @author java
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
	
	public void buildLUT( int iFirst, int iSecond );
	
	public void buildLUT( String iFirst, String iSecond );
	
	public boolean buildLUT_stopEditing();
	
	public boolean isBuildLUTfinished();
	
}
