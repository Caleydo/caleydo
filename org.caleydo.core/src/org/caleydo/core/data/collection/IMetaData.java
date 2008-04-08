/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.data.collection;

/**
 * Meta data defines a state for each object it is bound to.
 */
public interface IMetaData {

	/**
	 * Tells that this selection can not be selected.
	 * 
	 * Default is FALSE == unlocked.
	 */
	public boolean isLocked();
	
	/**
	 * Tells that this selection shall not be rendered.
	 * 
	 * Default is FALSE == show.
	 */
	public boolean isHidden();
	
	/**
	 * Indicates, that this is selected.
	 * 
	 * Default is FALSE == not selected.
	 * 
	 *  Note the special case if SELECTED = FALSE than SELECTED_FUZZY is not relevant also.
	 */
	public boolean isSelected();
	
	
	/**
	 * If bIsSelected == TRUE this defines, if the selection is 
	 * uses a fuzzy selection. 
	 * 
	 * Note the special case if SELECTED = FALSE than SELECTED_FUZZY is not relevant
	 * and this call returens false always.
	 * 
	 * Default is FLASE meaning not fuzzy.
	 * 
	 * @return TRUE if object is selected as fuzzy.
	 */
	public boolean isSelectedFuzzy();
	
	/**
	 * Returns the current value of isSelectedFuzzy instead of checking if the object is selected fuzzy.
	 * @return 
	 */
	public boolean isSelectedFuzzyValue();
	
	
	/**
	 * Tells that this selection can not be selected.
	 * 
	 * Default is FALSE == unlocked.
	 */
	public void setLocked( final boolean setLocked );
	
	/**
	 * Tells that this selection shall not be rendered.
	 * 
	 * Default is FALSE == show.
	 */
	public void setHidden( final boolean setHidden );
	
	/**
	 * Indicates, that this is selected.
	 * 
	 * Default is FALSE == not selected.
	 */
	public void setSelected( final boolean setSelected );
	
	
	/**
	 * If bIsSelected == TRUE this defines, if the selection is 
	 * uses a fuzzy selection. 
	 * 
	 * Default is FLASE meaning not fuzzy.
	 */
	public void setSelectedFuzzy( final boolean setFuzzy );
	

	/**
	 * compares two IMetaData objects if their state is equal.
	 * 
	 * The state is equal if HIDE, LOCK, SELECTED and SELECTED_FUZZY are equal.
	 * Note the special case if SELECTED = FALSE than SELECTED_FUZZY is not relevant.
	 * 
	 * @param testObject to compare with
	 * @return TRUE is state is equal
	 */
	public boolean equalState( final IMetaData testObject);
	
	/**
	 * Sets all boolean parameters to false.
	 * 
	 * Does not alter the label.
	 */
	public void reset();
	
	/**
	 * Sets all boolean parameters. Does not set the label.
	 * 
	 * @param bSetLocked TRUE...locked  FALSE...unlocked
	 * @param bSetHidden TRUE...hidden  FALSE...show
	 * @param bSetSelect TRUE...select  FALSE...not select
	 * @param bSetSelectFuzzy  TRUE...selected fuzzy  FALSE...not selected fuzzy
	 * 
	 */
	public void setAll( final boolean bSetLocked,
			final boolean bSetHidden,
			final boolean bSetSelect,
			final boolean bSetSelectFuzzy );
	
	/**
	 * Sets all boolean parameters and the label.
	 * 
	 * @param bSetLocked TRUE...locked  FALSE...unlocked
	 * @param bSetHidden TRUE...hidden  FALSE...show
	 * @param bSetSelect TRUE...select  FALSE...not select
	 * @param bSetSelectFuzzy  TRUE...selected fuzzy  FALSE...not selected fuzzy
	 * @param label Label of the object
	 */
	public void setAll( final boolean bSetLocked,
			final boolean bSetHidden,
			final boolean bSetSelect,
			final boolean bSetSelectFuzzy,
			final String label );
	
	/**
	 * Get the label.
	 * 
	 * @return label
	 */
	public String getLabel();
	
	/**
	 * Sets the label.
	 * 
	 * @param sSetLabel text of the label
	 */
	public void setLabel( final String sSetLabel);
}
