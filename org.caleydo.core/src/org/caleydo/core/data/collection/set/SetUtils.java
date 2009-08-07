package org.caleydo.core.data.collection.set;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateSet;
import org.caleydo.core.command.data.CmdDataCreateStorage;
import org.caleydo.core.command.data.parser.CmdLoadFileLookupTable;
import org.caleydo.core.command.data.parser.CmdLoadFileNStorages;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.usecase.EUseCaseMode;
import org.caleydo.core.parser.ascii.tabular.TabularAsciiDataReader;

public class SetUtils {

	/** prefix for temporary set-files */
	public static final String DATA_FILE_PREFIX = "setfile"; 
	
	/**
	 * Loads the set-file as specified in the {@link IUseCase}'s {@link LoadDataParameters} and
	 * stores the raw-data in the useCase
	 * @param useCase
	 */
	public static byte[] loadSetFile(LoadDataParameters parameters) {
		String setFileName = parameters.getFileName();
		if (setFileName == null) {
			throw new RuntimeException("No set-file name specified in use case");
		}
		
		File setFile = new File(setFileName);
		byte[] buffer;
		try {
			FileInputStream is = new FileInputStream(setFile);
			if (setFile.length() > Integer.MAX_VALUE) {
				throw new RuntimeException("set-file is larger than maximum internal file-storage-size");
			}
			buffer = new byte[(int) setFile.length()];
			is.read(buffer, 0, buffer.length);
		} catch (IOException ex) {
			throw new RuntimeException("Could not read from specified set-file '" + setFileName + "'", ex);
		}
		return buffer;
	}

	/**
	 * Saves the set-data contained in the useCase in a new created temp-file.
	 * The {@link LoadDataParameters} of the useCase are set according to the created set-file  
	 * @param parameters set-load parameters to store the filename;
	 * @param data set-data to save
	 */
	public static void saveSetFile(LoadDataParameters parameters, byte[] data) {
		File homeDir = new File(IGeneralManager.CALEYDO_HOME_PATH);
		File setFile;
		try {
			setFile = File.createTempFile(DATA_FILE_PREFIX, "csv", homeDir);
			parameters.setFileName(setFile.getCanonicalPath());
		}
		catch (IOException ex) {
			throw new RuntimeException("Could not create temporary file to store the set file", ex);
		}
		saveSetFile(parameters, data, setFile);
	}
		
	/**
	 * Saves the set-data contained in the useCase in the given file.
	 * The {@link LoadDataParameters} of the useCase are set according to the created set-file  
	 * @param useCase useCase to get the set-data from 
	 */
	public static void saveSetFile(LoadDataParameters parameters, byte[] data, File setFile) {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(setFile);
			os.write(data);
		}
		catch (FileNotFoundException ex) {
			throw new RuntimeException("Could not create temporary file to store the set file", ex);
		} catch (IOException ex) {
			throw new RuntimeException("Could not write to temportary set file", ex);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ex) {
					// nothing to do here, assuming output stream is already closed
				}
			}
		}
	}

	/**
	 * Creates the storages from a previously prepared storage defintion.
	 * @param loadDataParameters definition how to create the storages
	 * @return <code>true</code>if the creation was successful, <code>false</code> otherwise 
	 */
	public static boolean createStorages(LoadDataParameters loadDataParameters) {
		ArrayList<Integer> storageIds = new ArrayList<Integer>();
		
		TabularAsciiDataReader reader = new TabularAsciiDataReader(null);
		reader.setTokenPattern(loadDataParameters.getInputPattern());
		ArrayList<EStorageType> dataTypes = reader.getColumnDataTypes();
		
		boolean abort = false;
		Iterator<String> storageLabelIterator = loadDataParameters.getStorageLabels().iterator();
		for (EStorageType dataType : dataTypes) {
			switch (dataType) {
				case FLOAT:
					CmdDataCreateStorage cmdCreateStorage =
						(CmdDataCreateStorage) GeneralManager.get().getCommandManager().createCommandByType(
							ECommandType.CREATE_STORAGE);
					cmdCreateStorage.setAttributes(EManagedObjectType.STORAGE_NUMERICAL);
					cmdCreateStorage.doCommand();

					String storageLabel = storageLabelIterator.next();
					INumericalStorage storage = (INumericalStorage) cmdCreateStorage.getCreatedObject();
					storage.setLabel(storageLabel);
					storageIds.add(storage.getID());
					break;
				case SKIP:
					// nothing to do, just skip
					break;
				case ABORT:
					abort = true;
					break;
				default: 
					// nothing to do
					break;
			}
			if (abort) {
				break;
			}
		}

		loadDataParameters.setStorageIds(storageIds);
		
		return true;
	}

	/**
	 * Creates the set from a previously prepared storage defintion.
	 * @param loadDataParameters definition how to load the set
	 */
	public static boolean createData(IUseCase useCase) {
		LoadDataParameters loadDataParameters = useCase.getLoadDataParameters();
		ArrayList<Integer> iAlStorageId = loadDataParameters.getStorageIds();

		// Create SET
		CmdDataCreateSet cmdCreateSet =
			(CmdDataCreateSet) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.CREATE_SET_DATA);

		if (useCase.getUseCaseMode() == EUseCaseMode.GENETIC_DATA) {
			cmdCreateSet.setAttributes(iAlStorageId, ESetType.GENE_EXPRESSION_DATA);
		}
		else if (useCase.getUseCaseMode() == EUseCaseMode.UNSPECIFIED_DATA) {
			cmdCreateSet.setAttributes(iAlStorageId, ESetType.UNSPECIFIED);
		}
		else {
			throw new IllegalStateException("Not implemented.");
		}

		cmdCreateSet.doCommand();

		// Trigger file loading command
		CmdLoadFileNStorages cmdLoadCsv =
			(CmdLoadFileNStorages) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.LOAD_DATA_FILE);

		cmdLoadCsv.setAttributes(iAlStorageId, loadDataParameters);
		cmdLoadCsv.doCommand();

		if (!cmdLoadCsv.isParsingOK()) {
			// TODO: Clear created set and storages which are empty
			return false;
		}

		CmdLoadFileLookupTable cmdLoadLookupTableFile =
			(CmdLoadFileLookupTable) GeneralManager.get().getCommandManager().createCommandByType(
				ECommandType.LOAD_LOOKUP_TABLE_FILE);

		if (useCase.getUseCaseMode() == EUseCaseMode.GENETIC_DATA) {
			cmdLoadLookupTableFile.setAttributes(loadDataParameters.getFileName(), loadDataParameters.getStartParseFileAtLine(), -1,
				"REFSEQ_MRNA_2_EXPRESSION_INDEX REVERSE LUT", loadDataParameters.getDelimiter(),
				"REFSEQ_MRNA_INT_2_EXPRESSION_INDEX");
		}
		else if (useCase.getUseCaseMode() == EUseCaseMode.UNSPECIFIED_DATA) {
			cmdLoadLookupTableFile.setAttributes(loadDataParameters.getFileName(), loadDataParameters.getStartParseFileAtLine(), -1,
				"UNSPECIFIED_2_EXPRESSION_INDEX REVERSE", loadDataParameters.getDelimiter(), "");
		}
		else {
			throw new IllegalStateException("Not implemented.");
		}

		cmdLoadLookupTableFile.doCommand();

		ISet set = useCase.getSet();

		if (loadDataParameters.isMinDefined()) {
			set.setMin(loadDataParameters.getMin());
		}
		if (loadDataParameters.isMaxDefined()) {
			set.setMax(loadDataParameters.getMax());
		}

		if (loadDataParameters.getMathFilterMode().equals("Normal")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.NORMAL, true);
		}
		else if (loadDataParameters.getMathFilterMode().equals("Log10")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.LOG10, true);
		}
		else if (loadDataParameters.getMathFilterMode().equals("Log2")) {
			set.setExternalDataRepresentation(EExternalDataRepresentation.LOG2, true);
		}
		else
			throw new IllegalStateException("Unknown data representation type");

		// Since the data is filled to the new set
		// the views of the current use case can be updated.
		useCase.updateSetInViews();

		return true;
	}

}
