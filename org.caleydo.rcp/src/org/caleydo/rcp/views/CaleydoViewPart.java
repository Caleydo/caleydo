package org.caleydo.rcp.views;

import java.util.ArrayList;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.rcp.views.swt.ToolBarView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public abstract class CaleydoViewPart
extends ViewPart
{
	protected static ArrayList<IAction> alToolbar;
	protected static ArrayList<IContributionItem> alToolbarContributions;

	private final static int TOOLBAR_WRAP_COUNT = 4;
	
	protected int iViewID;
	
	protected Composite swtComposite;
	
	/**
	 * Method fills the toolbar in a given toolbar manager. Used in case of
	 * remote rendering. The array of toolbar managers is needed for simulating
	 * toolbar wrap which is not supported for linux. See bug:
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025
	 */
	public static void fillToolBar(ArrayList<IToolBarManager> alToolBarManager)
	{
		// Add ControlContribution items
		if (!GeneralManager.get().getPreferenceStore().getBoolean(
				PreferenceConstants.XP_CLASSIC_STYLE_MODE))
		{
			if (alToolbarContributions != null)
				for (IContributionItem item : alToolbarContributions)
					alToolBarManager.get(0).add(item);

			alToolbarContributions = null;
		}

		// add action items
		int iToolBarWrapCount = TOOLBAR_WRAP_COUNT;
		
		if (alToolbar.size() <= 4 && ToolBarView.bHorizontal)
			iToolBarWrapCount = 2;
		
		for (int iToolBarItemIndex = 0; iToolBarItemIndex < alToolbar.size(); iToolBarItemIndex++)
		{
			alToolBarManager.get((int) (iToolBarItemIndex / iToolBarWrapCount)).add(
					alToolbar.get(iToolBarItemIndex));
		}
		alToolbar = null;
	}
	
	/**
	 * Method fills the toolbar in a given toolbar manager. Used in case of a
	 * detached view that can put all its toolbar items into one single toolbar
	 * (no wrapping needed).
	 */
	public static void fillToolBar(final IToolBarManager toolBarManager)
	{
		// Add ControlContribution items
		if (!GeneralManager.get().getPreferenceStore().getBoolean(
				PreferenceConstants.XP_CLASSIC_STYLE_MODE))
		{
			if (alToolbarContributions != null)
				for (IContributionItem item : alToolbarContributions)
					toolBarManager.add(item);

			alToolbarContributions = null;
		}

		for (IAction toolBarAction : alToolbar)
		{
			toolBarManager.add(toolBarAction);
		}
	}
	
	public void fillToolBar()
	{
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		ArrayList<IToolBarManager> alToolBarManager = new ArrayList<IToolBarManager>();
		alToolBarManager.add(toolBarManager);
		fillToolBar(alToolBarManager);
	}
	
	public int getViewID()
	{
		return iViewID;
	}
	
	public Composite getSWTComposite()
	{
		return swtComposite;
	}
}
