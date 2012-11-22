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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CreateCompositeScoreDialog extends TitleAreaDialog {
	// the root element to populate the viewer with
	private final Collection<IScore> scores;
	private final ScoreQueryUI sender;

	// the visual selection widget group
	private Text labelUI;
	private Combo operatorUI;
	private CheckboxTableViewer scoresUI;


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
		this.labelUI = new Text(c, SWT.BORDER);
		this.labelUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		labelUI.addVerifyListener(notEmpty);
		new Label(c, SWT.NONE).setText("Operator: ");
		this.operatorUI = new Combo(c, SWT.DROP_DOWN | SWT.BORDER);
		this.operatorUI.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		operatorUI.addVerifyListener(notEmpty);
		String[] items = new String[ECombinedOperator.values().length];
		for(int i = 0; i < items.length; ++i)
			items[i] = ECombinedOperator.values()[i].name();
		this.operatorUI.setItems(items);
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
		return c;
	}

	private VerifyListener notEmpty = new VerifyListener() {
		@Override
		public void verifyText(VerifyEvent e) {
			e.doit = e.text != null && e.text.trim().length() > 0;
		}
	};


	@Override
	protected void okPressed() {
		// TODO validation
		String label = labelUI.getText();
		ECombinedOperator op = ECombinedOperator.values()[operatorUI.getSelectionIndex()];
		Collection<IScore> children = new ArrayList<>();
		for (IScore score : scores) {
			if (scoresUI.getChecked(score))
				children.add(score);
		}
		ICompositeScore result = op == ECombinedOperator.PRODUCT ? new ProductScore(label, children) : new CombinedScore(label,
				op, children);
		GeneralManager.get().getEventPublisher().triggerEvent(new AddScoreColumnEvent(result, sender));
		super.okPressed();
	}
}