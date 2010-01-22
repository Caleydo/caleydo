package org.caleydo.core.view.swt.glyph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.specialized.clinical.glyph.EGlyphSettingIDs;
import org.caleydo.core.manager.specialized.clinical.glyph.GlyphManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedDummyView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyphGenerator;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphObjectDefinition;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphObjectDefinition.DIRECTION;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.data.GlyphAttributeType;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.ISWTView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @see org.caleydo.core.view.IView
 * @author Sauer Stefan
 */

// FIXME: bad hack, should implement IMediatorReciever
public class GlyphMappingConfigurationViewRep
	extends ASWTView
	implements ISWTView {

	private class DataPack {
		// public Composite composite;
		public CCombo comboBox;
		public GlyphObjectDefinition model;

		public String parameterName;
		public EGlyphSettingIDs parameterType;
		public DIRECTION parameterValue;
	}

	private static final long serialVersionUID = 6402651939169536561L;

	private GlyphManager gman = null;

	private ArrayList<String> columnNames = null;
	private HashMap<Integer, Integer> extColNumToArrayIndex = null;
	private HashMap<Integer, Integer> arrayIndexToExtColNum = null;

	SelectionListener listener = null;

	Color headerBackgroundColor = new Color(null, 153, 153, 153);
	Color bodyBackgroundColor = new Color(null, 255, 255, 255);

	private HashMap<Integer, Composite> composites = null;
	private HashMap<CCombo, DataPack> comboBoxes = null;

	/**
	 * Constructor.
	 */
	public GlyphMappingConfigurationViewRep(int iParentContainerId, String sLabel) {
		super(iParentContainerId, sLabel, GeneralManager.get().getIDManager().createID(
			EManagedObjectType.VIEW_SWT_GLYPH_MAPPINGCONFIGURATION));

		composites = new HashMap<Integer, Composite>();
		comboBoxes = new HashMap<CCombo, DataPack>();

		columnNames = new ArrayList<String>();
		extColNumToArrayIndex = new HashMap<Integer, Integer>();
		arrayIndexToExtColNum = new HashMap<Integer, Integer>();

		gman = generalManager.getGlyphManager();
	}

	@Override
	public void initViewSWTComposite(Composite parentComposite) {
		// get all combo box entrys
		Iterator<GlyphAttributeType> it = gman.getGlyphAttributes().iterator();
		int counter = 0;
		while (it.hasNext()) {
			GlyphAttributeType at = it.next();

			if (at.doesAutomaticAttribute()) {
				continue;
			}

			columnNames.add(at.getName());
			extColNumToArrayIndex.put(at.getExternalColumnNumber(), counter);
			arrayIndexToExtColNum.put(counter, at.getExternalColumnNumber());
			counter++;
		}

		initComponents();

	}

	@Override
	public void drawView() {

	}

	private void initComponents() {
		// create listener
		listener = new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			public void widgetSelected(SelectionEvent event) {

				DataPack box = comboBoxes.get(event.widget);

				if (box != null) {
					if (box.parameterType == EGlyphSettingIDs.SCALE
						|| box.parameterType == EGlyphSettingIDs.COLOR) {
						int selectedindex = box.comboBox.getSelectionIndex();
						int selectedCol = arrayIndexToExtColNum.get(selectedindex);

						box.model.setPartParameterIndex(box.parameterName, box.parameterType,
							box.parameterValue, selectedCol);

					}
				}

				for (AGLView agleventlistener : generalManager.getViewGLCanvasManager()
					.getAllGLViews())
					if (agleventlistener instanceof GLGlyph) {
						((GLGlyph) agleventlistener).forceRebuild();
					}

				// TODO request assistance for this triggerEvent
				// triggerEventHere

			}

		};

		GridLayout layout = new GridLayout();
		parentComposite.setBackground(new Color(null, 255, 255, 255));

		for (int i = 0; i < 30; ++i) {
			GlyphObjectDefinition model = GLGlyphGenerator.getDetailLevelModel(i);

			if (model == null) {
				continue;
			}

			addHeaderGlyphDefinition(parentComposite, model);
			addBodyGlyphDefinition(parentComposite, model);

		}

		parentComposite.setLayout(layout);
		layout.numColumns = 1;

		// useless in rcp
		// parent.getParent().setSize(600, 300);

	}

	/**
	 * Show/Hide a Body Component defined with the detail level. Uses "switchBody(Composite comp)".
	 * 
	 * @param level
	 */
	private void switchBody(int level) {
		Composite composite = composites.get(level);

		if (composite == null)
			return;

		switchBody(composite);
	}

	/**
	 * Show/Hide a Component (the given one)
	 * 
	 * @param comp
	 */
	private void switchBody(Composite comp) {
		if (comp.getVisible() == false) {
			GridData data = (GridData) comp.getLayoutData();
			data.exclude = false;

			comp.setVisible(true);
			parentComposite.layout(false);
		}
		else {
			GridData data = (GridData) comp.getLayoutData();
			data.exclude = true;

			comp.setVisible(false);
			parentComposite.layout(false);
		}
	}

	/**
	 * Creates a header line, defined with the Glyph model
	 * 
	 * @param parent
	 * @param model
	 */
	private void addHeaderGlyphDefinition(Composite parent, final GlyphObjectDefinition model) {
		Composite comp = getHeaderComposite(parent);

		Button button = new Button(comp, SWT.PUSH);
		button.setText("+");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switchBody(model.getDetailLevel());
			}
		});
		CLabel label = new CLabel(comp, SWT.LEFT);
		label.setText(model.getDescription());
		// label.setText(Integer.toString(model.getDetailLevel()));
		label.setBackground(headerBackgroundColor);
		label.computeSize(300, 30);
	}

	/**
	 * creates generic header line
	 * 
	 * @param parent
	 * @return
	 */
	private Composite getHeaderComposite(Composite parent) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		Composite comp = new Composite(parent, SWT.NO_FOCUS);
		comp.setLayout(layout);
		comp.setLayoutData(gd);
		comp.setBackground(headerBackgroundColor);

		return comp;
	}

	/**
	 * Creates the Body of a header component, depending on a Glyph model
	 * 
	 * @param parent
	 * @param model
	 */
	private void addBodyGlyphDefinition(Composite parent, final GlyphObjectDefinition model) {
		Composite comp = getBodyComposite(parent);

		GridLayout layout = new GridLayout();
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		for (String name : model.getObjectPartNames()) {
			// scale part
			for (DIRECTION dir : DIRECTION.values()) {
				if (model.canPartScale(name, dir)) {
					String description = model.getPartParameterDescription(name, EGlyphSettingIDs.SCALE, dir);

					int index = model.getPartParameterIndexExternal(name, EGlyphSettingIDs.SCALE, dir);
					if (extColNumToArrayIndex.containsKey(index)) {
						index = extColNumToArrayIndex.get(index);
					}
					CCombo box = makeLabelComboBoxLine(layout, gd, comp, index, description);

					DataPack pack = new DataPack();
					pack.comboBox = box;
					// pack.composite = comp;
					pack.model = model;
					pack.parameterName = name;
					pack.parameterType = EGlyphSettingIDs.SCALE;
					pack.parameterValue = dir;
					comboBoxes.put(box, pack);
				}
			}

			// color part
			if (model.canPartColorChange(name)) {
				String description = model.getPartParameterDescription(name, EGlyphSettingIDs.COLOR, null);

				int index = model.getPartParameterIndexExternal(name, EGlyphSettingIDs.COLOR, null);
				if (extColNumToArrayIndex.containsKey(index)) {
					index = extColNumToArrayIndex.get(index);
				}
				CCombo box = makeLabelComboBoxLine(layout, gd, comp, index, description);

				DataPack pack = new DataPack();
				pack.comboBox = box;
				// pack.composite = comp;
				pack.model = model;
				pack.parameterName = name;
				pack.parameterType = EGlyphSettingIDs.COLOR;
				pack.parameterValue = null;
				comboBoxes.put(box, pack);
			}

		}

		composites.put(model.getDetailLevel(), comp);
	}

	/**
	 * Creates a generic Body component
	 * 
	 * @param parent
	 * @return
	 */
	private Composite getBodyComposite(Composite parent) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		Composite comp = new Composite(parent, SWT.NO_FOCUS);
		comp.setLayout(layout);
		comp.setLayoutData(gd);
		comp.setBackground(bodyBackgroundColor);

		return comp;
	}

	/**
	 * Creates a Combo Box Line in a Base Component
	 * 
	 * @param layout
	 * @param gd
	 * @param block
	 * @param selectedid
	 * @param text
	 * @return
	 */
	private CCombo makeLabelComboBoxLine(GridLayout layout, GridData gd, Composite block, int selectedid,
		String text) {
		CLabel label = new CLabel(block, SWT.LEFT);
		label.setText(text);
		label.setBackground(bodyBackgroundColor);
		label.setLayout(layout);
		label.setLayoutData(gd);

		CCombo combo = new CCombo(block, SWT.NONE);
		combo.setEditable(false);
		combo.setLayout(layout);
		combo.setLayoutData(gd);

		combo.removeAll();
		for (int i = 0; i < columnNames.size(); ++i) {
			combo.add(columnNames.get(i));
		}
		if (selectedid >= 0) {
			combo.select(selectedid);
		}

		combo.addSelectionListener(listener);

		return combo;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView(dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		// this implementation does not initialize anything yet
	}
}
