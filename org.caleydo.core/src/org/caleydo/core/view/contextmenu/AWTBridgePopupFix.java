package org.caleydo.core.view.contextmenu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

public class AWTBridgePopupFix {
	private static final int MAX_ATTEMPTS = 200;
	private static final int MAX_RETRIES = 5;

	public static Shell popupShell = null;

	public static void showMenu(final ContextMenuCreator menuCreator) {
		showMenu(menuCreator, MAX_RETRIES);
	}

	public static void showMenu(final ContextMenuCreator menuCreator, final int retriesLeft) {
		// System.out.println("Retries left: " + retriesLeft);

		if (retriesLeft == 0)
			return;

		final Display display = menuCreator.getParent().getDisplay();

		final Shell activeShell = menuCreator.getParent().getShell();
		popupShell = new Shell(activeShell, SWT.NO_TRIM | SWT.NO_FOCUS | SWT.ON_TOP);

		Point l = display.getCursorLocation();
		l.x -= 2;
		l.y -= 2;
		popupShell.setLocation(l);
		popupShell.setSize(4, 4);
		popupShell.open();
		final int[] count = new int[1];
		Runnable r = new Runnable() {
			public void run() {
				popupShell.setActive();

				final Menu menu = menuCreator.create(popupShell);
				menu.addListener(SWT.Hide, new Listener() {
					public void handleEvent(Event e) {
						// System.out.println("menu hidden");
						// popupShell.dispose();
						activeShell.setActive();

					}
				});
				menu.addListener(SWT.Show, new Listener() {
					public void handleEvent(Event e) {
						count[0]++;
						if (!menu.isVisible() && count[0] > MAX_ATTEMPTS) {
							Runnable r = new Runnable() {
								public void run() {
									System.err.println("menu not shown after " + MAX_ATTEMPTS + "attempts");
									menu.setVisible(false);
									menu.dispose();
									popupShell.dispose();
									System.err.println("disposing");
									showMenu(menuCreator, retriesLeft - 1);
								}
							};
							display.asyncExec(r);
							return;
						}

						Runnable runnable = new Runnable() {
							public void run() {
								if (!menu.isDisposed() && !menu.isVisible())
									menu.setVisible(true);
								else {
									// System.out.println("menu shown " + (count[0]) + " " +
									// menu.isVisible());
								}
							}
						};
						display.asyncExec(runnable);
					}
				});

				popupShell.addListener(SWT.Deactivate, new Listener() {

					@Override
					public void handleEvent(Event event) {

						if (!popupShell.isDisposed()) {
							popupShell.close();
							popupShell.dispose();
						}
						
						// Set lazy mode to false because in the case of an ignored context menu, the state is
						// erroneously set to true.
						menuCreator.getView().setLazyMode(false);
					}
				});

				menu.setVisible(true);
			}
		};
		display.asyncExec(r);
	}
}