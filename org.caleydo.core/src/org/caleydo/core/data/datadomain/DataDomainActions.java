/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GroupContextMenuItem;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * common actions for a data domain, that can be performed
 *
 * @author Samuel Gratzl
 *
 */
public class DataDomainActions {
	private final static String EXTENSION_POINT = "org.caleydo.datadomain.DataDomainActions";
	private final static Collection<IDataDomainActionFactory> factories = ExtensionUtils.findImplementation(
			EXTENSION_POINT, "class", IDataDomainActionFactory.class);


	public static Collection<Pair<String, Runnable>> create(IDataDomain dataDomain, Object sender) {
		Collection<Pair<String, Runnable>> events = new ArrayList<>();
		for (IDataDomainActionFactory factory : factories) {
			events.addAll(factory.create(dataDomain, sender));
		}
		return events;
	}

	public static boolean add(ContextMenuCreator creator, IDataDomain dataDomain, Object sender, boolean separate) {
		return add(creator, map(dataDomain, sender), separate);
	}

	private static Iterable<Collection<Pair<String, Runnable>>> map(IDataDomain dataDomain, Object sender) {
		Collection<Collection<Pair<String, Runnable>>> r = new ArrayList<>(factories.size());
		for (IDataDomainActionFactory factory : factories) {
			r.add(factory.create(dataDomain, sender));
		}
		return r;
	}

	public interface IDataDomainActionFactory {
		public Collection<Pair<String, Runnable>> create(IDataDomain dataDomain, Object sender);
	}

	static boolean add(ContextMenuCreator creator, Iterable<Collection<Pair<String, Runnable>>> items, boolean separate) {
		boolean first = separate;
		boolean added = false;
		for (Collection<Pair<String, Runnable>> create : items) {
			if (create == null || create.isEmpty())
				continue;
			if (!first) {
				creator.addSeparator();
			}
			first = false;
			added = true;
			for (Pair<String, Runnable> p : create) {
				if (p.getSecond() instanceof ChooserRunnable) {
					GroupContextMenuItem group = new GroupContextMenuItem(p.getFirst());
					addAll(group, (ChooserRunnable) p.getSecond());
					creator.add(group);
				}
				creator.add(new ActionBasedContextMenuItem(p.getFirst(), p.getSecond()));
			}
		}
		return added;
	}

	private static void addAll(GroupContextMenuItem creator, ChooserRunnable items) {
		for (Pair<String, Runnable> p : items) {
			if (p.getSecond() instanceof ChooserRunnable) {
				GroupContextMenuItem group = new GroupContextMenuItem(p.getFirst());
				addAll(group, (ChooserRunnable) p.getSecond());
				creator.add(group);
			}
			creator.add(new ActionBasedContextMenuItem(p.getFirst(), p.getSecond()));
		}
	}

	/**
	 * a special runnable with included nesting
	 * 
	 * @author Samuel Gratzl
	 * 
	 */
	public final static class ChooserRunnable extends ArrayList<Pair<String, Runnable>> implements Runnable {
		private static final long serialVersionUID = -3564385524119059911L;

		@Override
		public void run() {
			Display d = Display.getDefault();
			// show a selection dialog
			if (d.getThread() == Thread.currentThread())
				showSelectionBox();
			else
				d.asyncExec(this);
		}

		private void showSelectionBox() {
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(null, new LabelProvider() {
				@Override
				public String getText(Object element) {
					return ((Pair<?, ?>) element).getFirst().toString();
				}
			});
			dialog.setMultipleSelection(false);
			dialog.setElements(toArray());
			dialog.setTitle("Select an action");
			// user pressed cancel
			if (dialog.open() != Window.OK) {
				return;
			}
			Object[] result = dialog.getResult();
			if (result.length == 0)
				return;
			for (Object r : result) {
				@SuppressWarnings("unchecked")
				Pair<String, Runnable> p = (Pair<String, Runnable>) r;
				p.getSecond().run();
			}
		}
	}

}
