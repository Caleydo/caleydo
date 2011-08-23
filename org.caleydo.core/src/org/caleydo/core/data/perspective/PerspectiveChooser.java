/**
 * 
 */
package org.caleydo.core.data.perspective;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.caleydo.core.data.collection.table.DataTable;
import org.eclipse.swt.widgets.Shell;

/**
 * Helper class that chooses the perspective based on parameters where possible, or opens a dialog when it's
 * unclear.
 * 
 * @author Alexander Lex
 */
public class PerspectiveChooser {

	/**
	 * <p>
	 * Choose a {@link RecordPerspective} from those registered with the {@link DataTable} specified. If
	 * multiple recordPerspectives are available a dialog to make the user choose is opened.
	 * </p>
	 * <p>
	 * As {@link DataPerspective}s can be either private or public (see
	 * {@link DataPerspective#setIsPrivate(boolean)}, it is possible to let the chooser only chose from those
	 * that are public.
	 * </p>
	 * 
	 * @param table
	 *            the data table from which to choose the available perspectives
	 * @param considerOnlyPublic
	 *            whether only public perspectives shall be used (true) or whether to also include private
	 *            perspectives
	 * @return the chosen perspectiveID
	 */
	public static String chooseRecordPerspectiveAndAskIfNecessary(DataTable table, boolean considerOnlyPublic) {
		return choosePerspective(table, considerOnlyPublic, true, true);
	}

	/**
	 * <p>
	 * Checks if there is only one {@link RecordPerspective} from those registered with the {@link DataTable}
	 * a possible match considering the parameters. If so, this one match is returned, else null is returned.
	 * </p>
	 * <p>
	 * As {@link DataPerspective}s can be either private or public (see
	 * {@link DataPerspective#setIsPrivate(boolean)}, it is possible to let the chooser only chose from those
	 * that are public.
	 * </p>
	 * 
	 * @param table
	 *            the data table from which to choose the available perspectives
	 * @param considerOnlyPublic
	 *            whether only public perspectives shall be used (true) or whether to also include private
	 *            perspectives
	 * @return the chosen perspectiveID if a unique ID could be identified, or null
	 */
	// public static String isUnambigousRecordPerspectiveAvailable(DataTable table, boolean
	// considerOnlyPublic) {
	// return choosePerspective(table, considerOnlyPublic, true, false);
	// }

	public static Collection<String> getRecordPerspectiveCandidates(DataTable table,
		boolean considerOnlyPublic) {
		return getPerspectiveCandidates(table, considerOnlyPublic, true);
	}

	/**
	 * Same as {@link #chooseRecordPerspectiveAndAskIfNecessary(DataTable, boolean)} but for dimensions.
	 * 
	 * @param table
	 * @param considerOnlyPublic
	 * @return
	 */
	public static String chooseDimensionPerspectiveAndAskIfNeccesary(DataTable table,
		boolean considerOnlyPublic) {
		return choosePerspective(table, considerOnlyPublic, false, true);
	}

	// ----------------- Private Implemetations ---------------------------

	/**
	 * @param table
	 * @param considerOnlyPublic
	 * @param isRecord
	 * @return
	 */
	private static ArrayList<String> getPerspectiveCandidates(DataTable table, boolean considerOnlyPublic,
		boolean isRecord) {
		Set<String> dataPerspectiveIDs;
		if (isRecord)
			dataPerspectiveIDs = table.getRecordPerspectiveIDs();
		else
			dataPerspectiveIDs = table.getDimensionPerspectiveIDs();

		ArrayList<String> candidates = new ArrayList<String>(dataPerspectiveIDs);

		if (!considerOnlyPublic)
			return candidates;
		else {
			Iterator<String> iterator = candidates.iterator();
			while (iterator.hasNext()) {
				String tempPerspectiveID = iterator.next();
				DataPerspective<?, ?, ?, ?> perspective;
				if (isRecord)
					perspective = table.getRecordPerspective(tempPerspectiveID);
				else
					perspective = table.getDimensionPerspective(tempPerspectiveID);

				if (!perspective.isPrivate()) {
					iterator.remove();
				}
			}
			return candidates;
		}
	}

	/**
	 * Implementation for both {@link #chooseRecordPerspectiveAndAskIfNecessary(DataTable, boolean)} and
	 * {@link #chooseDimensionPerspectiveAndAskIfNeccesary(DataTable, boolean)}.
	 * 
	 * @param table
	 * @param considerOnlyPublic
	 * @param isRecord
	 *            true for the {@link #chooseRecordPerspectiveAndAskIfNecessary(DataTable, boolean)} case.
	 * @param allowGUI
	 *            set to true if a dialog may be opened to let the user choose. If false, null is returned in
	 *            the case of multiple candidates.
	 * @return
	 */
	private static String choosePerspective(DataTable table, boolean considerOnlyPublic, boolean isRecord,
		boolean allowGUI) {

		ArrayList<String> dataPerspectiveIDs = getPerspectiveCandidates(table, considerOnlyPublic, isRecord);

		if (dataPerspectiveIDs.size() == 1)
			return dataPerspectiveIDs.iterator().next();
		else {
			String chosenPerspective = null;
			// check if there is only one "public" perspecive
			for (String tempPerspectiveID : dataPerspectiveIDs) {
				if (chosenPerspective != null) {
					// there is more than one candidate
					if (allowGUI)
						chosenPerspective = useDialog(dataPerspectiveIDs);
					else
						return null;
					break;
				}
				else {
					chosenPerspective = tempPerspectiveID;
				}

			}
			return chosenPerspective;
		}

	}

	private static String useDialog(ArrayList<String> perspectiveIDs) {
		ChooseDataPerspectiveDialog dialog = new ChooseDataPerspectiveDialog(new Shell());
		dialog.setPossiblePerspectiveIDs(perspectiveIDs);
		String chosenPerspective = dialog.open();
		return chosenPerspective;

	}

}
