package org.caleydo.view.tourguide.vendingmachine.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.tourguide.data.score.CollapseScore;
import org.caleydo.view.tourguide.data.score.CombinedScore;
import org.caleydo.view.tourguide.data.score.ECollapseOperator;
import org.caleydo.view.tourguide.data.score.ECombinedOperator;
import org.caleydo.view.tourguide.data.score.ICompositeScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.util.EnumUtils;
import org.caleydo.view.tourguide.vendingmachine.ScoreQueryUI;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
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

public class CreateCompositeScoreDialog extends TitleAreaDialog {
	// the root element to populate the viewer with
	private final List<WeightedScore> scores;
	private final ScoreQueryUI sender;
	private final boolean createCollapseScore;

	// the visual selection widget group
	private Text labelUI;
	private ControlDecoration labelDeco;
	private Combo operatorUI;
	private ControlDecoration operatorDeco;
	private CheckboxTableViewer scoresUI;
	private ControlDecoration scoresDeco;


	public CreateCompositeScoreDialog(Shell shell, Collection<IScore> scores, ScoreQueryUI sender,
			boolean createCollapseScore) {
		super(shell);
		this.scores = Lists.newArrayList();
		for (IScore s : scores) {
			this.scores.add(new WeightedScore(1.0f, s));
		}
		for (IScore s : sender.getQuery().getSelection()) {
			if (!scores.contains(s))
				this.scores.add(0, new WeightedScore(1.0f, s));
		}
		this.createCollapseScore = createCollapseScore;
		this.sender = sender;
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

		new Label(c, SWT.NONE).setText("Operator: ");
		this.operatorUI = new Combo(c, SWT.DROP_DOWN | SWT.BORDER);
		this.operatorUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		String[] items;
		if (createCollapseScore) {
			items = EnumUtils.getLabels(ECollapseOperator.class);
			this.operatorUI.setItems(items);
			this.operatorUI.select(ECollapseOperator.NONE.ordinal());
		} else {
			items = EnumUtils.getNames(ECombinedOperator.class);
			this.operatorUI.setItems(items);
			this.operatorUI.select(ECombinedOperator.MEAN.ordinal());
		}
		this.operatorDeco = new ControlDecoration(this.operatorUI, SWT.TOP | SWT.RIGHT);
		operatorDeco.setDescriptionText("An operator is required");
		operatorDeco.setImage(image);
		operatorDeco.show(); // Hide deco if not in focus
		operatorUI.addListener(SWT.FocusOut, new Listener() {
			@Override
			public void handleEvent(Event event) {
				validateOperator();
			}
		});

		Label l = new Label(c, SWT.NONE);
		l.setText("Scores: ");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		this.scoresUI = CheckboxTableViewer.newCheckList(c, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = scoresUI.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		TableViewerColumn tableColumn;
		tableColumn = new TableViewerColumn(scoresUI, SWT.LEAD);
		tableColumn.getColumn().setText("Name");
		tableColumn.getColumn().setWidth(200);
		tableColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WeightedScore p = (WeightedScore) element;
				return p.score.getLabel();
			}
		});
		if (!createCollapseScore) {
			tableColumn = new TableViewerColumn(scoresUI, SWT.LEAD);
			tableColumn.getColumn().setText("Weight");
			tableColumn.getColumn().setWidth(50);
			tableColumn.setEditingSupport(new WeightEditingSupport(scoresUI));
			tableColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					WeightedScore p = (WeightedScore) element;
					return String.valueOf(p.weight);
				}
			});
		}
		scoresUI.setContentProvider(ArrayContentProvider.getInstance());
		scoresUI.setInput(scores);
		if (!createCollapseScore) {
			for (IScore s : sender.getQuery().getSelection()) {
				scoresUI.setChecked(s, true);
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

	public class WeightEditingSupport extends EditingSupport {
		private final TableViewer viewer;

		public WeightEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return new TextCellEditor(viewer.getTable());
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return String.valueOf(((WeightedScore) element).weight);
		}

		@Override
		protected void setValue(Object element, Object value) {
			((WeightedScore) element).weight = Float.parseFloat(String.valueOf(value));
			viewer.update(element, null);
		}
	}

	private boolean validateOperator() {
		if (createCollapseScore)
			return true;
		if (operatorUI.getSelectionIndex() == -1) {
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
		Collection<Pair<IScore, Float>> children = new ArrayList<>();
		for (Object score : scoresUI.getCheckedElements()) {
			children.add(((WeightedScore) score).asPair());
		}
		ICompositeScore result;
		if (createCollapseScore) {
			ECollapseOperator op = ECollapseOperator.values()[operatorUI.getSelectionIndex()];
			result = new CollapseScore(label, op, Collections2.transform(children, Pair.<IScore, Float> mapFirst()));
		} else {
			ECombinedOperator op = ECombinedOperator.values()[operatorUI.getSelectionIndex()];
			result = new CombinedScore(label, op, children);
		}
		GeneralManager.get().getEventPublisher().triggerEvent(new AddScoreColumnEvent(result, sender));
		super.okPressed();
	}

	private boolean validateScores() {
		if (scoresUI.getCheckedElements().length < 2) {
			scoresDeco.showHoverText("You have to select at least two elements");
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

	private static class WeightedScore {
		private float weight = 1.0f;
		private final IScore score;

		public WeightedScore(float weight, IScore score) {
			super();
			this.weight = weight;
			this.score = score;
		}

		public Pair<IScore, Float> asPair() {
			return Pair.make(score, weight);
		}
	}
}