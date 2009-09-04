package org.caleydo.core.parser.ascii.tabular;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.caleydo.core.data.collection.EStorageType;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.parser.ascii.AbstractLoader;
import org.caleydo.core.parser.ascii.IParserObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Loader for tabular data.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public class TabularAsciiDataReader
	extends AbstractLoader
	implements IParserObject {

	/**
	 * Imports data from file to this set. uses first storage and overwrites first selection.
	 */
	protected ArrayList<IStorage> alTargetStorages;

	protected ArrayList<EStorageType> alColumnDataTypes;

	private ArrayList<int[]> alIntBuffers;

	private ArrayList<int[]> alGroupInfo;

	private ArrayList<float[]> alFloatBuffers;

	private ArrayList<ArrayList<String>> alStringBuffers;

	private boolean bUseExperimentClusterInfo;

	/**
	 * Constructor.
	 */
	public TabularAsciiDataReader(final String sFileName) {
		super(sFileName);

		alTargetStorages = new ArrayList<IStorage>();
		alColumnDataTypes = new ArrayList<EStorageType>();

		alIntBuffers = new ArrayList<int[]>();
		alFloatBuffers = new ArrayList<float[]>();
		alStringBuffers = new ArrayList<ArrayList<String>>();

		alGroupInfo = new ArrayList<int[]>();

		init();
	}

	/**
	 * Defines a pattern for parsing
	 */
	public final boolean setTokenPattern(final String tokenPattern) {

		boolean bAllTokensProper = true;

		StringTokenizer tokenizer = new StringTokenizer(tokenPattern);

		final String sTokenPatternParserSeperator = IGeneralManager.sDelimiter_Parser_DataType;

		while (tokenizer.hasMoreTokens()) {
			String sBuffer = tokenizer.nextToken(sTokenPatternParserSeperator);

			if (sBuffer.equalsIgnoreCase("abort")) {
				alColumnDataTypes.add(EStorageType.ABORT);

				return bAllTokensProper;
			}
			else if (sBuffer.equalsIgnoreCase("skip")) {
				alColumnDataTypes.add(EStorageType.SKIP);
			}
			else if (sBuffer.equalsIgnoreCase("int")) {
				alColumnDataTypes.add(EStorageType.INT);
			}
			else if (sBuffer.equalsIgnoreCase("float")) {
				alColumnDataTypes.add(EStorageType.FLOAT);
			}
			else if (sBuffer.equalsIgnoreCase("string")) {
				alColumnDataTypes.add(EStorageType.STRING);
			}
			else if (sBuffer.equalsIgnoreCase("group_number")) {
				alColumnDataTypes.add(EStorageType.GROUP_NUMBER);
			}
			else if (sBuffer.equalsIgnoreCase("group_representative")) {
				alColumnDataTypes.add(EStorageType.GROUP_REPRESENTATIVE);
			}
			else {
				bAllTokensProper = false;

				GeneralManager.get().getLogger().log(
					new Status(Status.WARNING, GeneralManager.PLUGIN_ID, "Unknown column data type: "
						+ tokenPattern));
			}
		}

		return bAllTokensProper;
	}

	public void enableExperimentClusterInfo() {
		bUseExperimentClusterInfo = true;
	}

	public void setTargetStorages(final ArrayList<Integer> iAlTargetStorageId) {
		for (int iStorageId : iAlTargetStorageId) {
			alTargetStorages.add(GeneralManager.get().getStorageManager().getItem(iStorageId));
		}
	}

	protected void allocateStorageBufferForTokenPattern() {

		int lineCount = 0;

		for (EStorageType storageType : alColumnDataTypes) {
			switch (storageType) {
				case GROUP_NUMBER:
				case GROUP_REPRESENTATIVE:
					if (bUseExperimentClusterInfo)
						alGroupInfo.add(new int[iStopParsingAtLine - iStartParsingAtLine + 1 - 2]);
					else
						alGroupInfo.add(new int[iStopParsingAtLine - iStartParsingAtLine + 1]);
					break;
				case INT:
					alIntBuffers.add(new int[iStopParsingAtLine - iStartParsingAtLine + 1]);
					break;
				case FLOAT:
					if (bUseExperimentClusterInfo)
						alFloatBuffers.add(new float[iStopParsingAtLine - iStartParsingAtLine + 1 - 2]);
					else
						alFloatBuffers.add(new float[iStopParsingAtLine - iStartParsingAtLine + 1]);
					lineCount++;
					break;
				case STRING:
					alStringBuffers.add(new ArrayList<String>(iStopParsingAtLine - iStartParsingAtLine + 1));
					break;
				case SKIP:
					break;
				case ABORT:
					if (bUseExperimentClusterInfo) {
						alGroupInfo.add(new int[lineCount]);
						alGroupInfo.add(new int[lineCount]);
					}
					return;

				default:
					throw new IllegalStateException("Unknown token pattern detected: "
						+ storageType.toString());
			}
		}
	}

	@Override
	protected void loadDataParseFile(BufferedReader brFile, final int iNumberOfLinesInFile)
		throws IOException {

		allocateStorageBufferForTokenPattern();

		// Init progress bar
		swtGuiManager.setProgressBarText("Load data file " + this.getFileName());

		String sLine;
		String sTmpToken;
		int iColumnIndex = 0;
		float fProgressBarFactor = 100f / iStopParsingAtLine;

		int max = iStopParsingAtLine - iStartParsingAtLine + 1;

		while ((sLine = brFile.readLine()) != null && iLineInFile <= iStopParsingAtLine) {
			// Check if line should be ignored
			if (iLineInFile < this.iStartParsingAtLine) {
				iLineInFile++;
				continue;
			}

			// Replace empty cells with NaN
			sLine = sLine.replace(sTokenSeperator + sTokenSeperator, sTokenSeperator + "NaN" + sTokenSeperator);
			// Take care of empty cells in first column because they are not embedded between two token separators
			if (sLine.substring(0, 1).equals(sTokenSeperator))
				sLine = "NaN" + sLine;
			
			StringTokenizer strTokenLine = new StringTokenizer(sLine, sTokenSeperator);

			iColumnIndex = 0;

			for (EStorageType columnDataType : alColumnDataTypes) {
				if (strTokenLine.hasMoreTokens()) {
					switch (columnDataType) {
						case INT:
							alIntBuffers.get(iColumnIndex)[iLineInFile - iStartParsingAtLine] =
								Integer.valueOf(strTokenLine.nextToken()).intValue();
							iColumnIndex++;
							break;
						case FLOAT:
							Float fValue;
							sTmpToken = strTokenLine.nextToken();
							try {
								fValue = Float.valueOf(sTmpToken).floatValue();
							}
							catch (NumberFormatException nfe) {

								String sErrorMessage =
									"Unable to parse the data file. \""
										+ sTmpToken
										+ "\" cannot be converted to a number. Please change the data selection and try again.";
								MessageDialog.openError(new Shell(), "Error during parsing", sErrorMessage);

								GeneralManager.get().getLogger().log(
									new Status(Status.ERROR, GeneralManager.PLUGIN_ID, sErrorMessage));
								throw nfe;
							}

							if (bUseExperimentClusterInfo) {
								if (iLineInFile < max - 1)
									alFloatBuffers.get(iColumnIndex)[iLineInFile - iStartParsingAtLine] =
										fValue;
								else if (iLineInFile == max - 1)
									alGroupInfo.get(2)[iColumnIndex] = Integer.valueOf(sTmpToken).intValue();
								else if (iLineInFile == max)
									alGroupInfo.get(3)[iColumnIndex] = Integer.valueOf(sTmpToken).intValue();
							}
							else
								alFloatBuffers.get(iColumnIndex)[iLineInFile - iStartParsingAtLine] = fValue;

							iColumnIndex++;
							break;
						case STRING:
							alStringBuffers.get(iColumnIndex).add(strTokenLine.nextToken());
							iColumnIndex++;
							break;
						case SKIP: // do nothing
							strTokenLine.nextToken();
							break;
						case ABORT:
							iColumnIndex = alColumnDataTypes.size();
							break;
						case GROUP_NUMBER:
							alGroupInfo.get(0)[iLineInFile - iStartParsingAtLine] =
								Integer.valueOf(strTokenLine.nextToken()).intValue();
							break;
						case GROUP_REPRESENTATIVE:
							alGroupInfo.get(1)[iLineInFile - iStartParsingAtLine] =
								Integer.valueOf(strTokenLine.nextToken()).intValue();
							break;
						default:
							throw new IllegalStateException("Unknown token pattern detected: "
								+ columnDataType.toString());
					}

					// Check if the line is finished or early aborted
					if (iColumnIndex == alColumnDataTypes.size()) {
						continue;
					}
				}
			}

			iLineInFile++;

			// Update progress bar only on each 100th line
			if (iLineInFile % 1000 == 0) {
				swtGuiManager.setProgressBarPercentage((int) (fProgressBarFactor * iLineInFile));
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void setArraysToStorages() {

		int iIntArrayIndex = 0;
		int iFloatArrayIndex = 0;
		int iStringArrayIndex = 0;
		int iStorageIndex = 0;

		ISet set = GeneralManager.get().getUseCase().getSet();

		for (EStorageType storageType : alColumnDataTypes) {
			// if(iStorageIndex + 1 == alTargetStorages.size())
			// break;
			switch (storageType) {
				case INT:
					alTargetStorages.get(iStorageIndex).setRawData(alIntBuffers.get(iIntArrayIndex));
					iIntArrayIndex++;
					iStorageIndex++;
					break;
				case FLOAT:
					alTargetStorages.get(iStorageIndex).setRawData(alFloatBuffers.get(iFloatArrayIndex));
					iFloatArrayIndex++;
					iStorageIndex++;
					break;
				case STRING:
					((INominalStorage<String>) alTargetStorages.get(iStorageIndex))
						.setRawNominalData(alStringBuffers.get(iStringArrayIndex));
					alStringBuffers.add(new ArrayList<String>(iStopParsingAtLine - iStartParsingAtLine));
					iStringArrayIndex++;
					iStorageIndex++;
					break;
				case SKIP: // do nothing
					break;
				case GROUP_NUMBER:

					int[] iArGroupInfo = alGroupInfo.get(0);
					set.setGroupNrInfo(iArGroupInfo, true);

					iIntArrayIndex++;
					break;
				case GROUP_REPRESENTATIVE:

					int[] iArGroupRepr = alGroupInfo.get(1);
					set.setGroupReprInfo(iArGroupRepr, true);

					iIntArrayIndex++;
					break;
				case ABORT:
					if (bUseExperimentClusterInfo) {
						iArGroupInfo = alGroupInfo.get(2);
						set.setGroupNrInfo(iArGroupInfo, false);
						iArGroupRepr = alGroupInfo.get(3);
						set.setGroupReprInfo(iArGroupRepr, false);
					}
					return;

				default:
					throw new IllegalStateException("Unknown token pattern detected: "
						+ storageType.toString());
			}
		}
	}

	/**
	 * Init data structures. Use this to reset the state also!
	 * 
	 * @see org.caleydo.core.parser.ascii.IParserObject#init()
	 */
	public void init() {

		iLineInFile = 0;
	}

	public ArrayList<EStorageType> getColumnDataTypes() {
		return alColumnDataTypes;
	}
}
