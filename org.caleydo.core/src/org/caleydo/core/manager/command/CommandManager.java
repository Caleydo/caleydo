package org.caleydo.core.manager.command;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.ICommand;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.command.factory.CommandFactory;
import org.caleydo.core.parser.parameter.IParameterHandler;

/**
 * Manager for creating and exporting commands.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class CommandManager
	extends AManager<ICommand>
	implements ICommandManager {

	private CommandFactory commandFactory;

	/**
	 * Constructor.
	 */
	public CommandManager() {
		commandFactory = new CommandFactory();
	}

	@Override
	public ICommand createCommandByType(final ECommandType cmdType) {

		ICommand createdCommand = commandFactory.createCommandByType(cmdType);

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

		createdCommand.doCommand();
		
		return createdCommand;
	}

//	@Override
//	public void writeSerializedObjects(final String sFileName) {
//		try {
//			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(sFileName));
//
//			// First write number of command objects
//			out.writeInt(vecUndo.size());
//
//			// Iterate over commands
//			for (ICommand tmpCmd : vecUndo) {
//				out.writeObject(tmpCmd);
//
//				GeneralManager.get().getLogger().log(
//					new Status(IStatus.INFO, IGeneralManager.PLUGIN_ID, "Serialize command: ["
//						+ tmpCmd.getInfoText() + "]"));
//			}
//
//			out.close();
//
//		}
//		catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void readSerializedObjects(final String sFileName) {
//
//		try {
//			ObjectInputStream in = new ObjectInputStream(new FileInputStream(sFileName));
//
//			int iCmdCount = in.readInt();
//			for (int iCmdIndex = 0; iCmdIndex < iCmdCount; iCmdIndex++) {
//				// vecCmdHandle.add((ICommand)in.readObject());
//				ACommand tmpCmd = (ACommand) in.readObject();
//				tmpCmd.doCommand();
//			}
//
//			in.close();
//
//		}
//		catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
