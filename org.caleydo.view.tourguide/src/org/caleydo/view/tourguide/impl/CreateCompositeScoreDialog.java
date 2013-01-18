package org.caleydo.view.tourguide.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.tourguide.api.score.CollapseScore;
import org.caleydo.view.tourguide.api.score.CombinedScore;
import org.caleydo.view.tourguide.api.score.CombinedScore.TransformedScore;
import org.caleydo.view.tourguide.api.score.ECombinedOperator;
import org.caleydo.view.tourguide.api.util.ui.CaleydoLabelProvider;
import org.caleydo.view.tourguide.api.util.ui.CellEditorValidators;
import org.caleydo.view.tourguide.internal.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.internal.view.ScoreQueryUI;
import org.caleydo.view.tourguide.spi.compute.ICompositeScore;
import org.caleydo.view.tourguide.spi.score.IScore;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

class CreateCompositeScoreDialog extends TitleAreaDialog {
	// the root element to populate the viewer with
	private final List<TransformedScore> scores;
	private final ScoreQueryUI receiver;
	private final boolean createCollapseScore;

	// the visual selection widget group
	private Text labelUI;
	private ControlDecoration labelDeco;
	private ComboViewer operatorUI;
	private ControlDecoration operatorDeco;
	private CheckboxTableViewer scoresUI;
	private ControlDecoration scoresDeco;


	public CreateCompositeScoreDialog(Shell shell, Collection<IScore> scores, ScoreQueryUI sender,
			boolean createCollapseScore) {
		super(shell);
		this.scores = Lists.newArrayList();
		for (IScore s : scores) {
			this.scores.add(new TransformedScore(s));
		}
		for (IScore s : sender.getQuery().getSelection()) {
			if (!scores.contains(s))
				this.scores.add(0, new TransformedScore(s));
		}
		this.createCollapseScore = createCollapseScore;
		this.receiver = sender;
	}

	@Override
	public void create() {
		super.create();
		this.setTitle("Create a new " + (createCollapseScore ? "Collapsed" : "Combined") + " Score");
		this.setBlockOnOpen(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite((Composite) super.createDialogArea(parent), SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		new Label(c, SWT.NONE).setText("Name: ");

		final Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED)
				.getImage();

		this.labelUI = new Text(c, SWT.BORDER);
		labelUI.setText((createCollapseScore ? "Collapsed" : "Combined"));
		this.labelUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.labelDeco = new ControlDecoration(this.labelUI, SWT.TOP | SWT.RIGHT);
		labelDeco.setDescriptionText("A label is required");
		labelDeco.setImage(image);
		labelDeco.show(); // Hide deco if not in focus
		labelUI.addListener(SWT.FocusOut, new Listener() {
			@Override
			public void handleEvent(Event event) {
				validateLabel();
			}
		});

		if (!createCollapseScore) {
			new Label(c, SWT.NONE).setText("Operator: ");
			this.operatorUI = new ComboViewer(c, SWT.DROP_DOWN | SWT.BORDER);
			operatorUI.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			operatorUI.setLabelProvider(new CaleydoLabelProvider());
			operatorUI.setContentProvider(ArrayContentProvider.getInstance());
			operatorUI.setInput(ECombinedOperator.values());
			operatorUI.setSelection(new StructuredSelection(ECombinedOperator.MEAN));

			this.operatorDeco = new ControlDecoration(this.operatorUI.getCombo(), SWT.TOP | SWT.RIGHT);
			operatorDeco.setDescriptionText("An operator is required");
			operatorDeco.setImage(image);
			operatorDeco.show(); // Hide deco if not in focus
			operatorUI.getCombo().addListener(SWT.FocusOut, new Listener() {
				@Override
				public void handleEvent(Event event) {
					validateOperator();
				}
			});
		}


		Label l = new Label(c, SWT.NONE);
		l.setText("Scores: ");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		this.scoresUI = CheckboxTableViewer.newCheckList(c, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = scoresUI.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		TableViewerColumn tableColumn;
		tableColumn = createColumn(scoresUI, "Name", 200);
		tableColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TransformedScore p = (TransformedScore) element;
				return p.getScore().getLabel();
			}
		});
		if (!createCollapseScore) {
			l.setText("Score\nf * (x^p) + s");
			tableColumn = createColumn(scoresUI, "(f) Factor", 50);
			tableColumn.setEditingSupport(new FactorEditingSupport());
			tableColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					TransformedScore p = (TransformedScore) element;
					return String.valueOf(p.getFactor());
				}
			});
			tableColumn = createColumn(scoresUI, "(p) To the Power Of", 50);
			tableColumn.setEditingSupport(new PowerOfEditingSupport());
			tableColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					TransformedScore p = (TransformedScore) element;
					return String.valueOf(p.getPowerof());
				}
			});
			tableColumn = createColumn(scoresUI, "(s) Shift", 50);
			tableColumn.setEditingSupport(new ShiftEditingSupport());
			tableColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					TransformedScore p = (TransformedScore) element;
					return String.valueOf(p.getShift());
				}
			});
		}
		scoresUI.setContentProvider(ArrayContentProvider.getInstance());
		scoresUI.setInput(scores);
		if (!createCollapseScore) {
			for (IScore s : receiver.getQuery().getSelection()) {
				for (TransformedScore ws : this.scores)
					if (ws.getScore() == s)
						scoresUI.setChecked(ws, true);
			}
		}

		this.scoresDeco = new ControlDecoration(this.scoresUI.getControl(), SWT.TOP | SWT.RIGHT);
		scoresDeco.setDescriptionText("Select at least two elements");
		scoresDeco.setImage(image);
		scoresDeco.show(); // Hide deco if not in focus
		scoresDeco.getControl().addListener(SWT.FocusOut, new Listener() {
			@Override
			public void handleEvent(Event event) {
				validateScores();
			}
		});
		return c;
	}

	private TableViewerColumn createColumn(CheckboxTableViewer v, String label, int width) {
		TableViewerColumn c = new TableViewerColumn(v, SWT.LEAD);
		c.getColumn().setText(label);
		c.getColumn().setWidth(width);
		return c;
	}

	public class FactorEditingSupport extends EditingSupport {
		public FactorEditingSupport() {
			super(scoresUI);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			final TextCellEditor editor = new TextCellEditor(scoresUI.getTable());
			editor.setValidator(CellEditorValidators.isFloat);
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return String.valueOf(((TransformedScore) element).getFactor());
		}

		@Override
		protected void setValue(Object element, Object value) {
			((TransformedScore) element).setFactor(Float.parseFloat(String.valueOf(value)));
			scoresUI.update(element, null);
		}
	}

	public class PowerOfEditingSupport extends EditingSupport {
		public PowerOfEditingSupport() {
			super(scoresUI);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			final TextCellEditor editor = new TextCellEditor(scoresUI.getTable());
			editor.setValidator(CellEditorValidators.isFloat);
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return String.valueOf(((TransformedScore) element).getPowerof());
		}

		@Override
		protected void setValue(Object element, Object value) {
			((TransformedScore) element).setPowerof(Float.parseFloat(String.valueOf(value)));
			scoresUI.update(element, null);
		}
	}

	public class ShiftEditingSupport extends EditingSupport {
		public ShiftEditingSupport() {
			super(scoresUI);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			final TextCellEditor editor = new TextCellEditor(scoresUI.getTable());
			editor.setValidator(CellEditorValidators.isFloat);
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return String.valueOf(((TransformedScore) element).getShift());
		}

		@Override
		protected void setValue(Object element, Object value) {
			((TransformedScore) element).setShift(Float.parseFloat(String.valueOf(value)));
			scoresUI.update(element, null);
		}
	}

	private boolean validateOperator() {
		if (createCollapseScore)
			return true;
		if (operatorUI.getSelection() == null) {
			operatorDeco.showHoverText("An operator is required");
			return false;
		} else {
			operatorDeco.hide();
			return true;
		}
	}

	private static boolean isBlank(String t) {
		return t == null || t.trim().length() == 0;
	}

	@Override
	protected void okPressed() {
		if (!validateLabel() || !validateOperator() || !validateScores())
			return;
		String label = labelUI.getText();
		Collection<TransformedScore> children = new ArrayList<>();
		for (Object score : scoresUI.getCheckedElements()) {
			children.add(((TransformedScore) score));
		}
		ICompositeScore result;
		if (createCollapseScore) {
			result = new CollapseScore(label, Collections2.transform(children, CombinedScore.retrieveScore));
		} else {
			ECombinedOperator op = (ECombinedOperator) ((StructuredSelection) operatorUI.getSelection())
					.getFirstElement();
			result = new CombinedScore(label, op, children);
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new AddScoreColumnEvent(result).to(receiver));
		super.okPressed();
	}

	private boolean validateScores() {
		if (scoresUI.getCheckedElements().length < 1) {
			scoresDeco.showHoverText("You have to select at least one elements");
			return false;
		} else {
			scoresDeco.hide();
			return true;
		}
	}

	private boolean validateLabel() {
		if (isBlank(labelUI.getText())) {
			labelDeco.showHoverText("You have to enter a label");
			return false;
		} else {
			labelDeco.hide();
			return true;
		}
	}


}