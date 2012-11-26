package org.caleydo.view.tourguide.vendingmachine.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.tourguide.data.score.CombinedScore;
import org.caleydo.view.tourguide.data.score.CombinedScore.ECombinedOperator;
import org.caleydo.view.tourguide.data.score.ICompositeScore;
import org.caleydo.view.tourguide.data.score.IScore;
import org.caleydo.view.tourguide.data.score.ProductScore;
import org.caleydo.view.tourguide.event.AddScoreColumnEvent;
import org.caleydo.view.tourguide.vendingmachine.ScoreQueryUI;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.swt.widgets.Text;

public class CreateCompositeScoreDialog extends TitleAreaDialog {
	// the root element to populate the viewer with
	private final Collection<IScore> scores;
	private final ScoreQueryUI sender;

	// the visual selection widget group
	private Text labelUI;
	private ControlDecoration labelDeco;
	private Combo operatorUI;
	private ControlDecoration operatorDeco;
	private CheckboxTableViewer scoresUI;
	private ControlDecoration scoresDeco;


	public CreateCompositeScoreDialog(Shell shell, Collection<IScore> scores, ScoreQueryUI sender) {
		super(shell);
		this.scores = scores;
		this.sender = sender;
	}

	@Override
	public void create() {
		super.create();
		this.setTitle("Create a new Combined Score");
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
		labelUI.setText("Combined");
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
		String[] items = new String[ECombinedOperator.values().length];
		for(int i = 0; i < items.length; ++i)
			items[i] = ECombinedOperator.values()[i].name();
		this.operatorUI.setItems(items);
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
		this.scoresUI = CheckboxTableViewer.newCheckList(c, SWT.BORDER);
		this.scoresUI.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scoresUI.setContentProvider(ArrayContentProvider.getInstance());
		scoresUI.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((org.caleydo.core.util.base.ILabelProvider)element).getLabel();
			}
		});
		scoresUI.setInput(scores);
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

	private boolean validateOperator() {
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
		int index = operatorUI.getSelectionIndex();
		ECombinedOperator op = ECombinedOperator.values()[index];
		Collection<IScore> children = new ArrayList<>();
		for (Object score : scoresUI.getCheckedElements()) {
			children.add((IScore) score);
		}
		ICompositeScore result = op == ECombinedOperator.PRODUCT ? new ProductScore(label, children) : new CombinedScore(label,
				op, children);
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
}