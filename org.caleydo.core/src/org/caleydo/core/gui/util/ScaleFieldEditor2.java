package org.caleydo.core.gui.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Objects;

public class ScaleFieldEditor2 extends ScaleFieldEditor {
	protected Text text;

	private final IFormatter formatter;
	/**
	 * Creates a scale field editor.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	public ScaleFieldEditor2(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		formatter = DEFAULT_FORMATTER;
	}

	/**
	 * Creates a scale field editor with particular scale values.
	 *
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 * @param min
	 *            the value used for Scale.setMinimum(int).
	 * @param max
	 *            the value used for Scale.setMaximum(int).
	 * @param increment
	 *            the value used for Scale.setIncrement(int).
	 * @param pageIncrement
	 *            the value used for Scale.setPageIncrement(int).
	 */
	public ScaleFieldEditor2(String name, String labelText, Composite parent, int min, int max, int increment,
			int pageIncrement, IFormatter formatter) {
		super(name, labelText, parent, min, max, increment, pageIncrement);
		this.formatter = Objects.firstNonNull(formatter, DEFAULT_FORMATTER);
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns - 1);
		text = getTextControl(parent);
		GridData gd = new GridData();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.CENTER;
		text.setLayoutData(gd);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		super.adjustForNumColumns(numColumns - 1);
	}

	@Override
	protected void doLoad() {
		super.doLoad();
		if (text != null) {
			text.setText(formatter.format(scale.getSelection()));
		}
	}

	@Override
	public int getNumberOfControls() {
		return 4;
	}

	/**
	 * @return the text, see {@link #text}
	 */
	public Text getTextControl() {
		return text;
	}

	/**
	 * Returns this field editor's scale control. The control is created if it does not yet exist.
	 *
	 * @param parent
	 *            the parent
	 * @return the scale control
	 */
	private Text getTextControl(Composite parent) {
		if (text == null) {
			text = new Text(parent, SWT.BORDER);
			text.setFont(parent.getFont());
			text.addListener(SWT.Traverse, new Listener() {
				@Override
				public void handleEvent(Event e) {
					if (e.detail == SWT.TRAVERSE_RETURN) {
						e.doit = false;
					}
				}
			});
			text.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					valueChanged2();
				}

				@Override
				public void focusGained(FocusEvent e) {

				}
			});
			text.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					valueChanged2();
				}
			});
			text.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent event) {
					text = null;
				}
			});
		} else {
			checkParent(text, parent);
		}
		return text;
	}

	/**
	 * Informs this field editor's listener, if it has one, about a change to the value (<code>VALUE</code> property)
	 * provided that the old and new values are different.
	 * <p>
	 * This hook is <em>not</em> called when the scale is initialized (or reset to the default value) from the
	 * preference store.
	 * </p>
	 */
	@Override
	protected void valueChanged() {
		super.valueChanged();
		text.setText(formatter.format(scale.getSelection()));
	}

	protected void valueChanged2() {
		Integer v = formatter.parse(text.getText());
		if (v != null) {
			scale.setSelection(v.intValue());
			valueChanged();
		}
	}

	private interface IFormatter {
		Integer parse(String in);

		String format(int in);
	}

	public static final IFormatter DEFAULT_FORMATTER = new IFormatter() {

		@Override
		public Integer parse(String in) {
			String v = in.trim();
			if (StringUtils.isNumeric(v)) {
				return new Integer(v);
			}
			return null;
		}

		@Override
		public String format(int in) {
			return String.valueOf(in);
		}
	};

	public static final IFormatter PERCENT_FORMATTER = scaled(100);

	public static IFormatter scaled(final float factor) {
		return new IFormatter() {

			@Override
			public Integer parse(String in) {
				String v = in.trim();
				if (!NumberUtils.isNumber(v))
					return null;
				int vi = (int) (Float.valueOf(v) * factor);
				return vi;
			}

			@Override
			public String format(int in) {
				return String.format("%.2f", in / factor);
			}
		};
	}
}
