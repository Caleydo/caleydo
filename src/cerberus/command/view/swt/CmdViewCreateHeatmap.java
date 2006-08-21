package cerberus.command.view.swt;

import java.util.LinkedList;

import cerberus.command.ICommand;
import cerberus.command.view.CmdViewCreateAdapter;
import cerberus.manager.IGeneralManager;

/**
 * Class implementes the command for creating a heatmap view.
 * 
 * @author Marc Streit
 *
 */
public class CmdViewCreateHeatmap extends CmdViewCreateAdapter implements ICommand 
{
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateHeatmap( 
			IGeneralManager refGeneralManager,
			final LinkedList <String> listAttributes) 
	{
		super(refGeneralManager, listAttributes);
	}
}
