package org.caleydo.core.view;

import java.util.ArrayList;
import java.util.Iterator;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.swt.widgets.Composite;

/**
 * Abstract class that is the base of all view representations. It holds the the
 * own view ID, the parent ID and the attributes that needs to be processed.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AView
	extends AUniqueObject
	implements IView
{
	protected IGeneralManager generalManager;

	protected transient ISetManager setManager;

	/**
	 * List for all ISet objects providing data for this ViewRep.
	 */
	protected ArrayList<ISet> alSets;

	protected int iParentContainerId;

	protected Composite parentComposite;

	protected String sLabel;

	/**
	 * Constructor.
	 */
	public AView(final int iParentContainerId, final String sLabel, final int iViewID)
	{
		super(iViewID);

		generalManager = GeneralManager.get();
		setManager = generalManager.getSetManager();

		this.iParentContainerId = iParentContainerId;
		this.sLabel = sLabel;

		alSets = new ArrayList<ISet>();
	}

	/**
	 * Sets the unique ID of the parent container. Normally it is already set in
	 * the constructor. Use this method only if you want to change the parent
	 * during runtime.
	 * 
	 * @param iParentContainerId
	 */
	public void setParentContainerId(int iParentContainerId)
	{
		this.iParentContainerId = iParentContainerId;
	}

	@Override
	public final String getLabel()
	{
		return sLabel;
	}

	@Override
	public final void setLabel(String label)
	{
		this.sLabel = label;

		if (parentComposite != null)
		{
			parentComposite.getShell().setText(label);
		}
	}
	
	public synchronized void addSet(ISet set)
	{
		alSets.add(set);
	}

	public synchronized void addSet(int iSetID)
	{
		alSets.add(generalManager.getSetManager().getItem(iSetID));		
	}

	public synchronized void removeSets(ESetType setType)
	{
		Iterator<ISet> iter = alSets.iterator();
		while (iter.hasNext())
		{
			if (iter.next().getSetType() == setType)
				iter.remove();
		}
	}

	public synchronized void clearSets()
	{
		alSets.clear();
	}
}
