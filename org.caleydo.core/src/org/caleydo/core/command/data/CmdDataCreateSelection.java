package org.caleydo.core.command.data;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.base.ACmdCreational;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Class creates a selection.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CmdDataCreateSelection
	extends ACmdCreational<ISelectionDelta>
{
	private EIDType idType;
	private EIDType internalIDType = null;

	/**
	 * Constructor.
	 */
	public CmdDataCreateSelection(final ECommandType cmdType)
	{
		super(cmdType);
	}

	@Override
	public void doCommand() throws CaleydoRuntimeException
	{
		
		createdObject = new SelectionDelta(idType, internalIDType);
		
		commandManager.runDoCommand(this);
	}

	@Override
	public void undoCommand() throws CaleydoRuntimeException
	{
		commandManager.runUndoCommand(this);
	}
	
	/**
	 * Set attributes for object to be constructed, see {@link SelectionDelta#SelectionDelta(EIDType)}
	 *
	 * @param idType see Constructor
	 */
	public void setAttributes(EIDType idType)
	{
		this.idType = idType;
	}
	
	/**
	 * Set attributes for object to be constructed, see {@link SelectionDelta#SelectionDelta(EIDType, EIDType)}
	 *
	 * @param idType see Constructor
	 * @param internalIDType see Constructor
	 */
	public void setAttributes(EIDType idType, EIDType internalIDType)
	{
		this.internalIDType = internalIDType;
	}
	
}
