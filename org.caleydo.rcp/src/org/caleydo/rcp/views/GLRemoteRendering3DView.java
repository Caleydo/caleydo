package org.caleydo.rcp.views;

import org.caleydo.core.command.CommandType;
import org.caleydo.core.command.view.rcp.CmdExternalActionTrigger;
import org.caleydo.core.command.view.rcp.CmdExternalFlagSetter;
import org.caleydo.core.command.view.rcp.EExternalActionType;
import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.util.search.SearchBar;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

public class GLRemoteRendering3DView
	extends AGLViewPart
{

	public static final String ID = "org.caleydo.rcp.views.GLRemoteRendering3DView";

	public static final String ACTION_TOGGLE_LAYOUT_MODE_TEXT = "Toggle Jukebox/Bucket";
	public static final String ACTION_TOGGLE_LAYOUT_MODE_ICON = "resources/icons/toggle.png";

	public static final String ACTION_CLEAR_ALL_TEXT = "Clear all";
	public static final String ACTION_CLEAR_ALL_ICON = "resources/icons/eraser.png";

	public static final String ACTION_ENABLE_PATHWAY_TEXTURES_TEXT = "Turn on/off pathway textures";
	public static final String ACTION_ENABLE_PATHWAY_TEXTURES_ICON = "resources/icons/PathwayEditor/texture_on_off.png";
	public static final String ACTION_ENABLE_GENE_MAPPING_TEXT = "Turn on/off gene mapping";
	public static final String ACTION_ENABLE_GENE_MAPPING_ICON = "resources/icons/PathwayEditor/gene_mapping.png";
	public static final String ACTION_ENABLE_NEIGHBORHOOD_TEXT = "Turn on/off neighborhood highlighting";
	public static final String ACTION_ENABLE_NEIGHBORHOOD_ICON = "resources/icons/PathwayEditor/three_neighborhood.gif";

	protected int iGLCanvasDirectorId;

	private Action actToggleLayoutMode;
	private Action actClearAll;

	private Action actEnableGeneMapping;
	private boolean bEnableGeneMapping = true;

	private Action actEnablePathwayTextures;
	private boolean bEnablePathwayTextures = true;

	private Action actEnableNeighborhood;
	private boolean bEnableNeighborhood = false;

	/**
	 * Constructor.
	 */
	public GLRemoteRendering3DView()
	{

		super();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		super.createPartControlSWT(parent);

		createToggleLayoutStyleAction();
		createClearAllAction();
		createGeneMappingToggleAction();
		createPathwayTexturesToggleAction();
		createNeighborhoodToggleAction();

		contributeToActionBars();
	}

	protected void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalPullDown(IMenuManager manager)
	{
	}

	protected void fillLocalToolBar(IToolBarManager manager)
	{

		IContributionItem searchBar = new SearchBar("Quick search");

		manager.add(new Separator());
		manager.add(searchBar);
		manager.add(actToggleLayoutMode);
		manager.add(actClearAll);
		manager.add(actEnableGeneMapping);
		manager.add(actEnablePathwayTextures);
		manager.add(actEnableNeighborhood);
	}

	private void createToggleLayoutStyleAction()
	{

		actToggleLayoutMode = new Action()
		{
			public void run()
			{

				triggerCmdExternalAction(EExternalActionType.REMOTE_RENDERING_TOGGLE_LAYOUT_MODE);
			}
		};

		actToggleLayoutMode.setText(ACTION_TOGGLE_LAYOUT_MODE_TEXT);
		actToggleLayoutMode.setToolTipText(ACTION_TOGGLE_LAYOUT_MODE_TEXT);
		actToggleLayoutMode.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass()
				.getClassLoader().getResource(ACTION_TOGGLE_LAYOUT_MODE_ICON)));
	}

	private void createClearAllAction()
	{

		actClearAll = new Action()
		{
			public void run()
			{

				triggerCmdExternalAction(EExternalActionType.CLEAR_ALL);
			}
		};

		actClearAll.setText(ACTION_CLEAR_ALL_TEXT);
		actClearAll.setToolTipText(ACTION_CLEAR_ALL_TEXT);
		actClearAll.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass()
				.getClassLoader().getResource(ACTION_CLEAR_ALL_ICON)));
	}

	private void createGeneMappingToggleAction()
	{

		actEnableGeneMapping = new Action()
		{
			public void run()
			{

				bEnableGeneMapping = !bEnableGeneMapping;
				triggerCmdSExternalFlagSetter(bEnableGeneMapping,
						EExternalFlagSetterType.PATHWAY_ENABLE_GENE_MAPPING);
			}
		};

		actEnableGeneMapping.setText(ACTION_ENABLE_GENE_MAPPING_TEXT);
		actEnableGeneMapping.setToolTipText(ACTION_ENABLE_GENE_MAPPING_TEXT);
		actEnableGeneMapping.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass()
				.getClassLoader().getResource(ACTION_ENABLE_GENE_MAPPING_ICON)));
	}

	private void createPathwayTexturesToggleAction()
	{

		actEnablePathwayTextures = new Action()
		{
			public void run()
			{

				bEnablePathwayTextures = !bEnablePathwayTextures;
				triggerCmdSExternalFlagSetter(bEnablePathwayTextures,
						EExternalFlagSetterType.PATHWAY_ENABLE_TEXTURES);
			}
		};

		actEnablePathwayTextures.setText(ACTION_ENABLE_PATHWAY_TEXTURES_TEXT);
		actEnablePathwayTextures.setToolTipText(ACTION_ENABLE_PATHWAY_TEXTURES_TEXT);
		actEnablePathwayTextures
				.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass()
						.getClassLoader().getResource(ACTION_ENABLE_PATHWAY_TEXTURES_ICON)));
	}

	private void createNeighborhoodToggleAction()
	{

		actEnableNeighborhood = new Action()
		{
			public void run()
			{

				bEnableNeighborhood = !bEnableNeighborhood;
				triggerCmdSExternalFlagSetter(bEnableNeighborhood,
						EExternalFlagSetterType.PATHWAY_ENABLE_NEIGHBORHOOD);
			}
		};

		actEnableNeighborhood.setText(ACTION_ENABLE_NEIGHBORHOOD_TEXT);
		actEnableNeighborhood.setToolTipText(ACTION_ENABLE_NEIGHBORHOOD_TEXT);
		actEnableNeighborhood.setImageDescriptor(ImageDescriptor.createFromURL(this.getClass()
				.getClassLoader().getResource(ACTION_ENABLE_NEIGHBORHOOD_ICON)));
	}

	public void triggerCmdExternalAction(EExternalActionType type)
	{

		CmdExternalActionTrigger tmpCmd = (CmdExternalActionTrigger) Application.generalManager
				.getCommandManager().createCommandByType(
						CommandType.EXTERNAL_ACTION_TRIGGER);

		tmpCmd.setAttributes(iViewID, type);
		tmpCmd.doCommand();
	}

	public void triggerCmdSExternalFlagSetter(final boolean bFlag, EExternalFlagSetterType type)
	{

		CmdExternalFlagSetter tmpCmd = (CmdExternalFlagSetter) Application.generalManager
				.getCommandManager().createCommandByType(
						CommandType.EXTERNAL_FLAG_SETTER);

		tmpCmd.setAttributes(iViewID, bFlag, type);
		tmpCmd.doCommand();
	}

}