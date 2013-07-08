/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleTextAdapter;
import org.eclipse.swt.accessibility.AccessibleTextEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

/**
 * The SearchBox class represents a selectable user interface object that combines a text field and a list.
 * The values in the list are filtered according to the text in the text field (only values that starts with
 * the current text will be visible in the list).
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>, it does not make sense to add
 * children to it, or set a layout on it.
 * </p>
 */
public final class SearchBox
	extends Composite {

	private Text text;

	private List list;

	private int visibleItemCount = 10;

	private Shell popup;

	private Button arrow;

	private boolean hasFocus;

	private Listener listener, filter;

	private Color foreground, background;

	// private String listItems[];

	private ArrayList<String> listItems;

	private Font font;

	/**
	 * Constructs a new instance of this class given its parent and a style value describing its behavior and
	 * appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class <code>SWT</code> which is
	 * applicable to instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is,
	 * using the <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The
	 * class description lists the style constants that are applicable to the class. Style bits are also
	 * inherited from superclasses.
	 * </p>
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot be null)
	 * @param style
	 *            the style of widget to construct
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
	 *                </ul>
	 * @see SWT#BORDER
	 * @see SWT#FLAT
	 * @see Widget#getStyle()
	 */
	public SearchBox(Composite parent, int style) {
		super(parent, style = checkStyle(style));

		int textStyle = SWT.SINGLE;
		if ((style & SWT.FLAT) != 0) {
			textStyle |= SWT.FLAT;
		}
		text = new Text(this, textStyle);
		int arrowStyle = SWT.ARROW | SWT.DOWN;
		if ((style & SWT.FLAT) != 0) {
			arrowStyle |= SWT.FLAT;
		}
		arrow = new Button(this, arrowStyle);

		listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (popup == event.widget) {
					popupEvent(event);
					return;
				}
				if (text == event.widget) {
					textEvent(event);
					return;
				}
				if (list == event.widget) {
					listEvent(event);
					return;
				}
				if (arrow == event.widget) {
					arrowEvent(event);
					return;
				}
				if (SearchBox.this == event.widget) {
					comboEvent(event);
					return;
				}
				if (getShell() == event.widget) {
					handleFocus(SWT.FocusOut);
				}
			}
		};
		filter = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Shell shell = ((Control) event.widget).getShell();
				if (shell == SearchBox.this.getShell()) {
					handleFocus(SWT.FocusOut);
				}
			}
		};

		int[] comboEvents = { SWT.Dispose, SWT.Move, SWT.Resize };
		for (int comboEvent : comboEvents) {
			this.addListener(comboEvent, listener);
		}

		int[] textEvents = { SWT.KeyDown, SWT.KeyUp, SWT.FocusIn };
		for (int textEvent : textEvents) {
			text.addListener(textEvent, listener);
		}

		int[] arrowEvents = { SWT.Selection, SWT.FocusIn };
		for (int arrowEvent : arrowEvents) {
			arrow.addListener(arrowEvent, listener);
		}

		createPopup(null, -1);
		initAccessible();
	}

	static int checkStyle(int style) {
		int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
		return style & mask;
	}

	/**
	 * Adds the argument to the end of the receiver's list.
	 * 
	 * @param string
	 *            the new item
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
	 *                receiver</li>
	 *                </ul>
	 * @see #add(String,int)
	 */
	public void add(String string) {
		checkWidget();
		if (string == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		list.add(string);
		// call setItems() so new item is added to listItems and list is sorted
		setItems(list.getItems());
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when the receiver's text is
	 * modified, by sending it one of the messages defined in the <code>ModifyListener</code> interface.
	 * 
	 * @param listener
	 *            the listener which should be notified
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
	 *                receiver</li>
	 *                </ul>
	 * @see ModifyListener
	 * @see #removeModifyListener
	 */
	public void addModifyListener(ModifyListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Modify, typedListener);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when the receiver's selection
	 * changes, by sending it one of the messages defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the combo's list selection changes.
	 * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text area.
	 * </p>
	 * <ul>
	 * 
	 * @param listener
	 *            the listener which should be notified
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
	 *                receiver</li>
	 *                </ul>
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	void arrowEvent(Event event) {
		switch (event.type) {
			case SWT.FocusIn: {
				handleFocus(SWT.FocusIn);
				break;
			}
			case SWT.Selection: {
				dropDown(!isDropped());
				break;
			}
		}
	}

	/**
	 * Sets the selection in the receiver's text field to an empty selection starting just before the first
	 * character.
	 * <p>
	 * Note: To clear the selected items in the receiver's list, use <code>deselectAll()</code>.
	 * </p>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
	 *                receiver</li>
	 *                </ul>
	 * @see #deselectAll
	 */
	public void clearSelection() {
		checkWidget();
		text.clearSelection();
		list.deselectAll();
	}

	void comboEvent(Event event) {
		switch (event.type) {
			case SWT.Dispose:
				if (popup != null && !popup.isDisposed()) {
					list.removeListener(SWT.Dispose, listener);
					popup.dispose();
				}
				Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				popup = null;
				text = null;
				list = null;
				arrow = null;
				break;
			case SWT.Move:
				dropDown(false);
				break;
			case SWT.Resize:
				internalLayout(false);
				break;
		}
	}

	void shellEvent(Event event) {
		switch (event.type) {

			case SWT.Move:
				dropDown(false);
				break;
			case SWT.Resize:
				internalLayout(false);
				break;
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();
		int width = 0, height = 0;
		String[] items = list.getItems();
		int textWidth = 0;
		GC gc = new GC(text);
		int spacer = gc.stringExtent(" ").x; //$NON-NLS-1$
		for (String item : items) {
			textWidth = Math.max(gc.stringExtent(item).x, textWidth);
		}
		gc.dispose();
		Point textSize = text.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		Point arrowSize = arrow.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		Point listSize = list.computeSize(wHint, SWT.DEFAULT, changed);
		int borderWidth = getBorderWidth();

		height = Math.max(hHint, Math.max(textSize.y, arrowSize.y) + 2 * borderWidth);
		width = Math.max(wHint, Math.max(textWidth + 2 * spacer + arrowSize.x + 2 * borderWidth, listSize.x));

		return new Point(width, height);
	}

	void createPopup(String[] items, int selectionIndex) {
		// create shell and list
		popup = new Shell(getShell(), SWT.NO_TRIM | SWT.ON_TOP);
		int style = getStyle();
		int listStyle = SWT.SINGLE | SWT.V_SCROLL;
		if ((style & SWT.FLAT) != 0) {
			listStyle |= SWT.FLAT;
		}
		if ((style & SWT.RIGHT_TO_LEFT) != 0) {
			listStyle |= SWT.RIGHT_TO_LEFT;
		}
		if ((style & SWT.LEFT_TO_RIGHT) != 0) {
			listStyle |= SWT.LEFT_TO_RIGHT;
		}
		list = new List(popup, listStyle);
		if (font != null) {
			list.setFont(font);
		}
		if (foreground != null) {
			list.setForeground(foreground);
		}
		if (background != null) {
			list.setBackground(background);
		}

		int[] popupEvents = { SWT.Close, SWT.Paint, SWT.Deactivate };
		for (int popupEvent : popupEvents) {
			popup.addListener(popupEvent, listener);
		}
		int[] listEvents =
			{ SWT.MouseUp, SWT.Selection, SWT.Traverse, SWT.KeyDown, SWT.KeyUp, SWT.FocusIn, SWT.Dispose };
		for (int listEvent : listEvents) {
			list.addListener(listEvent, listener);
		}

		if (this.listItems != null) {
			list.setItems(getFilteredList());
		}
		if (selectionIndex != -1) {
			list.setSelection(selectionIndex);
		}
	}

	/**
	 * 
	 */
	public void deselect(int index) {
		checkWidget();
		list.deselect(index);
	}

	/**
	 * 
	 */
	public void deselectAll() {
		checkWidget();
		list.deselectAll();
	}

	void dropDown(boolean drop) {
		if (drop == isDropped())
			return;
		if (!drop) {
			popup.setVisible(false);
			if (!isDisposed()) {
				// text.setFocus();
			}
			return;
		}

		if (getShell() != popup.getParent()) {
			String[] items = list.getItems();
			int selectionIndex = list.getSelectionIndex();
			list.removeListener(SWT.Dispose, listener);
			popup.dispose();
			popup = null;
			list = null;
			createPopup(items, selectionIndex);

		}

		Point size = getSize();
		int itemCount = list.getItemCount();
		itemCount = itemCount == 0 ? visibleItemCount : Math.min(visibleItemCount, itemCount);
		int itemHeight = list.getItemHeight() * itemCount;
		Point listSize = list.computeSize(SWT.DEFAULT, itemHeight, false);

		// Restrict size of the drop down menu
		if (listSize.x > 500) {
			listSize.x = 500;
		}

		list.setBounds(1, 1, Math.max(size.x - 2, listSize.x), listSize.y);

		int index = list.getSelectionIndex();
		if (index != -1) {
			list.setTopIndex(index);
		}
		Display display = getDisplay();
		Rectangle listRect = list.getBounds();
		Rectangle parentRect = display.map(getParent(), null, getBounds());
		Point comboSize = getSize();
		Rectangle displayRect = getMonitor().getClientArea();
		int width = Math.max(comboSize.x, listRect.width + 2);
		int height = listRect.height + 2;
		int x = parentRect.x;
		int y = parentRect.y + comboSize.y;
		if (y + height > displayRect.y + displayRect.height) {
			y = parentRect.y - height;
		}
		popup.setBounds(x, y, width, height);
		popup.setVisible(true);
	}

	/*
	 * Return the Label immediately preceding the receiver in the z-order, or null if none.
	 */
	Label getAssociatedLabel() {
		Control[] siblings = getParent().getChildren();
		for (int i = 0; i < siblings.length; i++) {
			if (siblings[i] == SearchBox.this) {
				if (i > 0 && siblings[i - 1] instanceof Label)
					return (Label) siblings[i - 1];
			}
		}
		return null;
	}

	@Override
	public Control[] getChildren() {
		checkWidget();
		return new Control[0];
	}

	/**
	 * 
	 */
	public boolean getEditable() {
		checkWidget();
		return text.getEditable();
	}

	/**
	 * 
	 */
	public String getItem(int index) {
		checkWidget();
		return list.getItem(index);
	}

	/**
	 * 
	 */
	public int getItemCount() {
		checkWidget();
		return list.getItemCount();
	}

	/**
	 * 
	 */
	public int getItemHeight() {
		checkWidget();
		return list.getItemHeight();
	}

	/**
	 * 
	 */
	public String[] getItems() {
		checkWidget();
		return list.getItems();
	}

	char getMnemonic(String string) {
		int index = 0;
		int length = string.length();
		do {
			while (index < length && string.charAt(index) != '&') {
				index++;
			}
			if (++index >= length)
				return '\0';
			if (string.charAt(index) != '&')
				return string.charAt(index);
			index++;
		} while (index < length);
		return '\0';
	}

	/**
	 * 
	 */
	public Point getSelection() {
		checkWidget();
		return text.getSelection();
	}

	/**
	 * 
	 */
	public int getSelectionIndex() {
		checkWidget();
		return list.getSelectionIndex();
	}

	@Override
	public int getStyle() {
		int style = super.getStyle();
		return style;
	}

	/**
	 * 
	 */
	public String getText() {
		checkWidget();
		return text.getText();
	}

	/**
	 * 
	 */
	public int getTextHeight() {
		checkWidget();
		return text.getLineHeight();
	}

	/**
	 * 
	 */
	public int getTextLimit() {
		checkWidget();
		return text.getTextLimit();
	}

	/**
	 * 
	 */
	public int getVisibleItemCount() {
		checkWidget();
		return visibleItemCount;
	}

	void handleFocus(int type) {

		if (isDisposed())
			return;
		switch (type) {
			case SWT.FocusIn: {
				if (hasFocus)
					return;
				hasFocus = true;
				Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				shell.addListener(SWT.Deactivate, listener);
				Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				display.addFilter(SWT.FocusIn, filter);

				Event e = new Event();
				notifyListeners(SWT.FocusIn, e);

				break;

			}
			case SWT.FocusOut: {
				if (!hasFocus)
					return;

				Control focusControl = getDisplay().getFocusControl();

				if (focusControl == arrow || focusControl == list || focusControl == text)
					return;

				hasFocus = false;
				dropDown(false);
				Shell shell = getShell();
				shell.removeListener(SWT.Deactivate, listener);
				Display display = getDisplay();
				display.removeFilter(SWT.FocusIn, filter);
				// Event e = new Event();
				// notifyListeners(SWT.FocusOut, e);

				break;
			}
		}
	}

	/**
	 * 
	 */
	public int indexOf(String string) {
		checkWidget();
		if (string == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		return list.indexOf(string);
	}

	/**
	 * 
	 */
	public int indexOf(String string, int start) {
		checkWidget();
		if (string == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		return list.indexOf(string, start);
	}

	void initAccessible() {
		AccessibleAdapter accessibleAdapter = new AccessibleAdapter() {
			@Override
			public void getName(AccessibleEvent e) {
				String name = null;
				Label label = getAssociatedLabel();
				if (label != null) {
					name = stripMnemonic(label.getText());
				}
				e.result = name;
			}

			@Override
			public void getKeyboardShortcut(AccessibleEvent e) {
				String shortcut = null;
				Label label = getAssociatedLabel();
				if (label != null) {
					String text = label.getText();
					if (text != null) {
						char mnemonic = getMnemonic(text);
						if (mnemonic != '\0') {
							shortcut = "Alt+" + mnemonic; //$NON-NLS-1$
						}
					}
				}
				e.result = shortcut;
			}

			@Override
			public void getHelp(AccessibleEvent e) {
				e.result = getToolTipText();
			}
		};
		getAccessible().addAccessibleListener(accessibleAdapter);
		text.getAccessible().addAccessibleListener(accessibleAdapter);
		list.getAccessible().addAccessibleListener(accessibleAdapter);

		arrow.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getName(AccessibleEvent e) {
				e.result = isDropped() ? SWT.getMessage("SWT_Close") : SWT.getMessage("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			@Override
			public void getKeyboardShortcut(AccessibleEvent e) {
				e.result = "Alt+Down Arrow"; //$NON-NLS-1$
			}

			@Override
			public void getHelp(AccessibleEvent e) {
				e.result = getToolTipText();
			}
		});

		getAccessible().addAccessibleTextListener(new AccessibleTextAdapter() {
			@Override
			public void getCaretOffset(AccessibleTextEvent e) {
				e.offset = text.getCaretPosition();
			}
		});

		getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getChildAtPoint(AccessibleControlEvent e) {
				Point testPoint = toControl(e.x, e.y);
				if (getBounds().contains(testPoint)) {
					e.childID = ACC.CHILDID_SELF;
				}
			}

			@Override
			public void getLocation(AccessibleControlEvent e) {
				Rectangle location = getBounds();
				Point pt = toDisplay(location.x, location.y);
				e.x = pt.x;
				e.y = pt.y;
				e.width = location.width;
				e.height = location.height;
			}

			@Override
			public void getChildCount(AccessibleControlEvent e) {
				e.detail = 0;
			}

			@Override
			public void getRole(AccessibleControlEvent e) {
				e.detail = ACC.ROLE_COMBOBOX;
			}

			@Override
			public void getState(AccessibleControlEvent e) {
				e.detail = ACC.STATE_NORMAL;
			}

			@Override
			public void getValue(AccessibleControlEvent e) {
				e.result = getText();
			}
		});

		text.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getRole(AccessibleControlEvent e) {
				e.detail = text.getEditable() ? ACC.ROLE_TEXT : ACC.ROLE_LABEL;
			}
		});

		arrow.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getDefaultAction(AccessibleControlEvent e) {
				e.result = isDropped() ? SWT.getMessage("SWT_Close") : SWT.getMessage("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
	}

	boolean isDropped() {
		return popup.getVisible();
	}

	@Override
	public boolean isFocusControl() {
		checkWidget();
		if (text.isFocusControl() || arrow.isFocusControl() || list.isFocusControl()
			|| popup.isFocusControl())
			return true;
		return super.isFocusControl();
	}

	void internalLayout(boolean changed) {
		if (isDropped()) {
			dropDown(false);
		}
		Rectangle rect = getClientArea();
		int width = rect.width;
		int height = rect.height;
		Point arrowSize = arrow.computeSize(SWT.DEFAULT, height, changed);
		text.setBounds(0, 0, width - arrowSize.x, height);
		arrow.setBounds(width - arrowSize.x, 0, arrowSize.x, arrowSize.y);
	}

	void listEvent(Event event) {
		switch (event.type) {
			case SWT.Dispose:
				if (getShell() != popup.getParent()) {
					String[] items = list.getItems();
					int selectionIndex = list.getSelectionIndex();
					popup = null;
					list = null;
					createPopup(items, selectionIndex);
				}
				break;

			case SWT.MouseUp: {
				// if (event.button != 1)
				// return;
				text.setText(list.getItem(list.getSelectionIndex()));

				dropDown(false);
				text.selectAll();
				text.setFocus();

				// Added by Marc
				Event e = new Event();
				e.time = event.time;
				e.stateMask = event.stateMask;
				e.doit = event.doit;
				notifyListeners(SWT.Selection, e);
				event.doit = e.doit;
				break;

			}
			case SWT.Selection: {

				// Do nothing here - beause it would also trigger a selection
				// when the user just
				// browses through the list by using the arrows.
				// The selection is either trigger on mouse click or SWT.CR
				break;
			}

			case SWT.KeyUp: {
				if (list.getItemCount() > 0 && list.getSelectionIndex() != -1) {
					text.setText(list.getItem(list.getSelectionIndex()));
					text.selectAll();
				}
				break;
			}
			case SWT.KeyDown: {

				if (event.character == SWT.ESC) {
					// Escape key cancels popup list
					dropDown(false);
				}
				if (event.character == SWT.CR) {
					dropDown(false);

					// Added by Marc
					int index = list.getSelectionIndex();
					if (index == -1)
						return;
					text.setText(list.getItem(index));
					text.selectAll();
					list.setSelection(index);
					Event e = new Event();
					e.time = event.time;
					e.stateMask = event.stateMask;
					e.doit = event.doit;
					notifyListeners(SWT.Selection, e);
					event.doit = e.doit;
				}
				if (event.keyCode == SWT.ARROW_UP && list.getSelectionIndex() == 0) {
					// dropDown (false);
					text.setFocus();
				}

			}
		}
	}

	void popupEvent(Event event) {
		switch (event.type) {
			case SWT.Paint:
				// draw black rectangle around list
				Rectangle listRect = list.getBounds();
				Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
				event.gc.setForeground(black);
				event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height + 1);
				break;
			case SWT.Close:
				event.doit = false;
				dropDown(false);
				break;
			case SWT.Deactivate:
				// dropDown(false);
				break;
		}
	}

	@Override
	public void redraw() {
		super.redraw();
		text.redraw();
		arrow.redraw();
		if (popup.isVisible()) {
			list.redraw();
		}
	}

	@Override
	public void redraw(int x, int y, int width, int height, boolean all) {
		super.redraw(x, y, width, height, true);
	}

	/**
	 * 
	 */
	// public void remove(int index) {
	// checkWidget();
	// list.remove(index);
	// }
	//
	// /**
	// *
	// */
	// public void remove(int start, int end) {
	// checkWidget();
	// list.remove(start, end);
	// }
	/**
	 * 
	 */
	public void remove(String string) {
		checkWidget();
		if (string == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		list.remove(string);
		listItems.remove(string);
	}

	/**
	 * 
	 */
	public void removeAll() {
		checkWidget();
		text.setText(""); //$NON-NLS-1$
		list.removeAll();
		listItems.clear();
	}

	/**
	 * 
	 */
	public void removeModifyListener(ModifyListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		removeListener(SWT.Modify, listener);
	}

	/**
	 * 
	 */
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		removeListener(SWT.Selection, listener);
		removeListener(SWT.DefaultSelection, listener);
	}

	/**
	 * 
	 */
	public void select(int index) {
		checkWidget();
		if (index == -1) {
			list.deselectAll();
			text.setText(""); //$NON-NLS-1$
			return;
		}
		if (0 <= index && index < list.getItemCount()) {
			if (index != getSelectionIndex()) {
				text.setText(list.getItem(index));
				text.selectAll();
				list.select(index);
				list.showSelection();
			}
		}
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		background = color;
		if (text != null) {
			text.setBackground(color);
		}
		if (list != null) {
			list.setBackground(color);
		}
		if (arrow != null) {
			arrow.setBackground(color);
		}
	}

	/**
	 * 
	 */
	public void setEditable(boolean editable) {
		checkWidget();
		text.setEditable(editable);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (popup != null) {
			popup.setVisible(false);
		}
		if (text != null) {
			text.setEnabled(enabled);
		}
		if (arrow != null) {
			arrow.setEnabled(enabled);
		}
	}

	@Override
	public boolean setFocus() {
		checkWidget();
		return text.setFocus();
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		this.font = font;
		text.setFont(font);
		list.setFont(font);
		internalLayout(true);
	}

	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		foreground = color;
		if (text != null) {
			text.setForeground(color);
		}
		if (list != null) {
			list.setForeground(color);
		}
		if (arrow != null) {
			arrow.setForeground(color);
		}
	}

	/**
	 * 
	 */
	// public void setItem(int index, String string) {
	// checkWidget();
	// list.setItem(index, string);
	// }
	/**
	 * 
	 */
	public void setItems(String[] items) {
		checkWidget();
		text.setText("");
		if (listItems != null) {
			listItems.clear();
		}
		else {
			listItems = new ArrayList<String>(items.length);
		}

		for (String item : items) {
			listItems.add(item);
		}
		Arrays.sort(items);
		list.setItems(items);
	}

	/**
	 * 
	 */
	@Override
	public void setLayout(Layout layout) {
		checkWidget();
		return;
	}

	/**
	 * 
	 */
	public void setSelection(Point selection) {
		checkWidget();
		if (selection == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		text.setSelection(selection.x, selection.y);
	}

	/**
	 * 
	 */
	public void setText(String string) {
		checkWidget();
		if (string == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		int index = list.indexOf(string);
		if (index == -1) {
			text.setText(string);
			list.setItems(getFilteredList());
			return;
		}
		text.setText(string);
		text.selectAll();
		list.setItems(getFilteredList());
	}

	/**
	 * 
	 */
	public void setTextLimit(int limit) {
		checkWidget();
		text.setTextLimit(limit);
	}

	@Override
	public void setToolTipText(String string) {
		checkWidget();
		super.setToolTipText(string);
		arrow.setToolTipText(string);
		text.setToolTipText(string);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible) {
			popup.setVisible(false);
		}
	}

	/**
	 * 
	 */
	public void setVisibleItemCount(int count) {
		checkWidget();
		if (count < 0)
			return;
		visibleItemCount = count;
	}

	String stripMnemonic(String string) {
		int index = 0;
		int length = string.length();
		do {
			while (index < length && string.charAt(index) != '&') {
				index++;
			}
			if (++index >= length)
				return string;
			if (string.charAt(index) != '&')
				return string.substring(0, index - 1) + string.substring(index, length);
			index++;
		} while (index < length);
		return string;
	}

	void textEvent(Event event) {
		switch (event.type) {
			case SWT.Modify: {
				Event e = new Event();
				e.time = event.time;
				notifyListeners(SWT.Modify, e);
				break;
			}
			case SWT.FocusIn: {
				handleFocus(SWT.FocusIn);

				break;
			}
			case SWT.KeyDown: {
				break;
			}
			case SWT.KeyUp: {
				if (event.keyCode == SWT.CR) {
					text.selectAll();
					dropDown(false);
					return;
				}
				if (event.keyCode == SWT.ARROW_UP)
					return;
				hasFocus = false;
				list.setItems(getFilteredList());
				dropDown(false);
				dropDown(true);
				text.setFocus();
				handleFocus(SWT.FocusIn);
				if (event.keyCode == SWT.ARROW_DOWN) {
					list.setFocus();
					if (list.getItemCount() > 0 && text.getText() != "") {
						list.select(0);
						text.setText(list.getItem(list.getSelectionIndex()));
					}
					return;
				}

				break;
			}

		}
	}

	private String[] getFilteredList() {
		if (listItems == null)
			return null;
		ArrayList<Object> filteredList = new ArrayList<Object>();

		String txt = text.getText();
		Object listItemsArray[] = listItems.toArray();

		Arrays.sort(listItemsArray);
		String itemText;

		for (Object element : listItemsArray) {
			itemText = ((String) element).toLowerCase();
			if (itemText.contains(txt.toLowerCase())) {
				filteredList.add(element);
				// while (i < listItemsArray.length
				// &&
				// ((String)listItemsArray[i]).toLowerCase().contains(txt.
				// toLowerCase()))
				// {
				// filteredList.add(listItemsArray[i]);
				// i++;
				// }
				// break;
			}
		}
		String filtered[] = new String[filteredList.size()];
		filteredList.toArray(filtered);
		return filtered;
	}

}
