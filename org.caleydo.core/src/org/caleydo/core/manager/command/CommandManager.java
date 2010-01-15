package org.caleydo.core.manager.command;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Vector;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.command.ICommandListener;
import org.caleydo.core.command.base.ACommand;
import org.caleydo.core.command.queue.ICommandQueue;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.command.factory.CommandFactory;
import org.caleydo.core.manager.command.factory.ICommandFactory;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Manager for creating and exporting commands.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CommandManager
	extends AManager<ICommand>
	implements ICommandManager {
	private ICommandFactory commandFactory;

	/**
	 * List of all Commands to be executed as soon as possible
	 */
	private Vector<ICommand> vecCmdHandle;

	/**
	 * List of All Commands to be executed when sooner or later.
	 */
	private Vector<ICommand> vecCmdSchedule;

	protected Hashtable<Integer, ICommandQueue> hashCommandQueueId;
	// protected Hashtable<Integer, ICommand> hashCommandId;

	protected Vector<ICommand> vecUndo;
	protected Vector<ICommand> vecRedo;

	private int iCountRedoCommand = 0;

	/**
	 * Constructor.
	 */
	public CommandManager() {
		commandFactory = new CommandFactory();

		vecCmdHandle = new Vector<ICommand>();
		vecCmdSchedule = new Vector<ICommand>();

		hashCommandQueueId = new Hashtable<Integer, ICommandQueue>();
		// hashCommandId = new Hashtable<Integer, ICommand>();

		vecUndo = new Vector<ICommand>(100);
		vecRedo = new Vector<ICommand>(100);
	}

	@Override
	public void addCommandListener(ICommandListener addCommandListener) {

		// TODO Auto-generated method stub

	}

	@Override
	public boolean removeCommandListener(ICommandListener removeCommandListener) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCommandListener(ICommandListener hasCommandListener) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void handleCommand(ICommand addCommand) {
		addCommand.doCommand();
		vecCmdHandle.addElement(addCommand);
	}

	@Override
	public void scheduleCommand(ICommand addCommand) {
		vecCmdSchedule.addElement(addCommand);
		addCommand.doCommand();
	}

	@Override
	public void registerItem(ICommand command) {
		if (command instanceof ICommandQueue) {
			hashCommandQueueId.put(command.getID(), (ICommandQueue) command);
			return;
		}

		vecCmdHandle.addElement(command);
		hashItems.put(command.getID(), command);
	}

	@Override
	public void unregisterItem(int iItemId) {
		if (hashItems.containsKey(iItemId)) {

			ICommand unregisterCommand = hashItems.remove(iItemId);

			vecCmdHandle.remove(unregisterCommand);
		}
	}

	@Override
	public ICommand createCommandByType(final ECommandType cmdType) {

		ICommand createdCommand = commandFactory.createCommandByType(cmdType);

		// BUG! creating command is not executing command!
		// //FIXME: should iterate over all undo/redo views.
		// if ( ! arUndoRedoViews.isEmpty() )
		// {
		// arUndoRedoViews.get(0).addCommand(createdCommand);
		// }

		return createdCommand;
	}

	@Override
	public ICommand createCommand(final IParameterHandler phAttributes) {

		ECommandType cmdType =
			ECommandType.valueOf(phAttributes.getValueString(ECommandType.TAG_TYPE.getXmlKey()));

		ICommand createdCommand = createCommandByType(cmdType);

		if (phAttributes != null) {
			createdCommand.setParameterHandler(phAttributes);
		}

		// BUG! creating command is not executing command!
		// //FIXME: should iterate over all undo/redo views.
		// if (arUndoRedoViews.isEmpty() == false)
		// {
		// arUndoRedoViews.get(0).addCommand(createdCommand);
		// }

		return createdCommand;
	}

	@Override
	public boolean hasCommandQueueId(final int iCmdQueueId) {

		return hashCommandQueueId.containsKey(iCmdQueueId);
	}

	@Override
	public ICommandQueue getCommandQueueByCmdQueueId(final int iCmdQueueId) {

		return hashCommandQueueId.get(iCmdQueueId);
	}

	// @Override
	// public ICommand createCommandQueue(final String sCmdType, final String
	// sProcessType,
	// final int iCmdId, final int iCmdQueueId, final int sQueueThread,
	// final int sQueueThreadWait)
	// {
	// ICommand newCmd = commandFactory.createCommandQueue(sCmdType,
	// sProcessType, iCmdId,
	// iCmdQueueId, sQueueThread, sQueueThreadWait);
	//
	// // int iNewCmdId = createId(EManagerObjectType.COMMAND);
	// // newCmd.setId(iNewCmdId);
	// //
	// // registerItem(newCmd, iNewCmdId);
	//
	// return newCmd;
	// }

	@Override
	public void runDoCommand(ICommand runCmd) {

		vecUndo.addElement(runCmd);

		if (iCountRedoCommand > 0) {
			vecRedo.remove(runCmd);
			iCountRedoCommand--;
		}
	}

	@Override
	public void runUndoCommand(ICommand runCmd) {

		iCountRedoCommand++;
		vecUndo.remove(runCmd);

		vecRedo.addElement(runCmd);
	}

	@Override
	public void writeSerializedObjects(final String sFileName) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(sFileName));

			// First write number of command objects
			out.writeInt(vecUndo.size());

			// Iterate over commands
			for (ICommand tmpCmd : vecUndo) {
				out.writeObject(tmpCmd);

				GeneralManager.get().getLogger().log(
					new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Serialize command: ["
						+ tmpCmd.getInfoText() + "]"));
			}

			out.close();

		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void readSerializedObjects(final String sFileName) {

		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(sFileName));

			int iCmdCount = in.readInt();
			for (int iCmdIndex = 0; iCmdIndex < iCmdCount; iCmdIndex++) {
				// vecCmdHandle.add((ICommand)in.readObject());
				ACommand tmpCmd = (ACommand) in.readObject();
				tmpCmd.doCommand();
			}

			in.close();

		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
