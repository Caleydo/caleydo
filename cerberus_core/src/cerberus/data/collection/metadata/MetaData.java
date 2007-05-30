/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.metadata;

import cerberus.data.collection.IMetaData;

/**
 * Keep a state of an object linked to. 
 * 
 * Implementation of IMetaData.
 * 
 * @author Michael Kalkusch
 *
 */
public class MetaData implements IMetaData {

	/**
	 * Tells that this selection can not be selected.
	 * 
	 * Default is FALSE == unlocked.
	 */
	protected boolean bIsLocked;
	
	/**
	 * Tells that this selection shall not be rendered.
	 * 
	 * Default is FALSE == show.
	 */
	protected boolean bIsHidden;
	
	/**
	 * Indicates, that this is selected.
	 * 
	 * Default is FALSE == not selected.
	 */
	protected boolean bIsSelected;
	
	
	/**
	 * If bIsSelected == TRUE this defines, if the selection is 
	 * uses a fuzzy selection. 
	 * 
	 * Default is FLASE meaning not fuzzy.
	 */
	protected boolean bIsSelectedFuzzy;
	
	/**
	 * Label
	 */
	protected String sLabel = "";
	
	/**
	 * 
	 */
	public MetaData() {
		
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#isLocked()
	 */
	public boolean isLocked() {
		return this.bIsLocked;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#isHidden()
	 */
	public boolean isHidden() {
		return this.bIsHidden;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#isSelected()
	 */
	public boolean isSelected() {
		return this.bIsSelected;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#isSelectedFuzzy()
	 */
	public boolean isSelectedFuzzy() {
		if ( ! bIsSelected ) {
			return false;
		}		
		return this.bIsSelectedFuzzy;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#isSelectedFuzzyValue()
	 */
	public boolean isSelectedFuzzyValue() {
		return this.bIsSelectedFuzzy;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#setLocked(boolean)
	 */
	public void setLocked(boolean setLocked) {
		this.bIsLocked = setLocked;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#setHidden(boolean)
	 */
	public void setHidden(boolean setHidden) {
		this.bIsHidden = setHidden;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#setSelected(boolean)
	 */
	public void setSelected(boolean setSelected) {
		this.bIsSelected = setSelected;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#setSelectedFuzzy(boolean)
	 */
	public void setSelectedFuzzy(boolean setFuzzy) {
		this.bIsSelectedFuzzy = setFuzzy;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#equalState(prometheus.data.set.SelectionMetaData)
	 */
	public boolean equalState( final IMetaData testObject) {
		if ((testObject.isLocked() == bIsLocked)&&
				(testObject.isHidden() == bIsHidden)&&
				(testObject.isSelected() == bIsSelected)) 
		{
			if ( bIsSelected == false) {
				// Note: if no selection is set the value of bSelectedFuzzy is not relevant
				return true;
			}
			
			//Note: a selection is made, thus the fuzzy values has to be compared...
			if (testObject.isSelectedFuzzy() == bIsSelectedFuzzy) {
				return true;
			}
			return false;		
			
		} // end if 
		return false;
		
	} // end equalState()

	
	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#reset()
	 */
	public void reset() {
		this.bIsHidden = false;
		this.bIsLocked = false;
		this.bIsSelected = false;
		this.bIsSelectedFuzzy = false;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#setAll(boolean, boolean, boolean, boolean)
	 */
	public void setAll(boolean bSetLocked, boolean bSetHidden,
			boolean bSetSelect, boolean bSetSelectFuzzy) {
		this.bIsHidden = bSetHidden;
		this.bIsLocked = bSetLocked;
		this.bIsSelected = bSetSelect;
		this.bIsSelectedFuzzy = bSetSelectFuzzy;

	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#setAll(boolean, boolean, boolean, boolean, Stringt)
	 */
	public void setAll(boolean bSetLocked, boolean bSetHidden,
			boolean bSetSelect, boolean bSetSelectFuzzy, String label) {
		this.bIsHidden = bSetHidden;
		this.bIsLocked = bSetLocked;
		this.bIsSelected = bSetSelect;
		this.bIsSelectedFuzzy = bSetSelectFuzzy;
		
		this.setLabel( label );

	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#getLabel()
	 */
	public String getLabel() {
		return this.sLabel;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.CollectionMetaData#setLabel(Stringt)
	 */
	public void setLabel(String sSetLabel) {
		this.sLabel = sSetLabel;
	}

}
