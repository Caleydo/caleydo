/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.datadomain.image.wizard;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.wizard.AImportDataPage;
import org.caleydo.datadomain.image.FilePrefixGrouper;
import org.caleydo.datadomain.image.ImageSet;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Page for selecting images and directories containting images for import.
 *
 * @author Thomas Geymayer
 *
 */
public class LoadImageSetPage
	extends AImportDataPage<ImageImportWizard>
	implements Listener {

	public static final String PAGE_NAME = "Load Images";

	public static final String PAGE_DESCRIPTION = "Specify the images you want to load.";

	/**
	 * Allowed image extensions.
	 */
	public static final List<String> EXTENSIONS_IMG = Arrays.asList("png", "jpg", "jpeg");

	/**
	 * Allowed config file extensions.
	 */
	public static final List<String> EXTENSIONS_CFG = Arrays.asList("url", "ini");

	/**
	 * Composite that is the parent of all gui elements of this dialog.
	 */
	protected Composite parentComposite;

	/**
	 * Combo box to specify the name for the imageset (or select an existing imageset).
	 */
	protected Text nameText;

	/**
	 * Combo box to specify the {@link IDCategory} for the columns of the dataset.
	 */
	protected Combo imageIDCategoryCombo;

	/**
	 * Combo box to specify the column ID Type.
	 */
	protected Combo imageIDTypeCombo;

	protected Combo layerIDCategoryCombo;

	protected Combo layerIDTypeCombo;

	/**
	 * TreeViewer for all files being imported
	 */
	protected TreeViewer fileTree;

	/**
	 * Label for loaded (and selected) image preview
	 */
	protected Label previewImage;

	/**
	 * Button to determine whether the columns are homogeneous.
	 */
	protected Button addDirButton;

	/**
	 * Button to determine whether the columns are inhomogeneous.
	 */
	protected Button addFilesButton;

	protected FilePrefixGrouper imageGroups;

	protected FileTree files = new FileTree();

	public LoadImageSetPage() {
		super(PAGE_NAME, null);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	protected void createGuiElements(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, true);
		parentComposite.setLayout(layout);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Group group = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		group.setText("Imageset Name");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

		nameText = new Text(group, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nameText.addListener(SWT.Modify, this);
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getWizard().getImageSet().setName(nameText.getText());
			}
		});

		Group datasetConfigGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		datasetConfigGroup.setText("Dataset Configuration");
		datasetConfigGroup.setLayout(new GridLayout(3, false));
		datasetConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		Label importExplanation = new Label(datasetConfigGroup, SWT.WRAP);
		importExplanation.setText("Import images and directories...");
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
		gd.widthHint = 400;
		importExplanation.setLayoutData(gd);

		fileTree = new TreeViewer(datasetConfigGroup);
		fileTree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		fileTree.setContentProvider(files);
		// fileTree.setLabelProvider(new FileTreeLabelProvider());
		fileTree.setInput("root"); // pass a non-null that will be ignored
		fileTree.getTree().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePreviewImage();
			}
		});
		fileTree.getTree().addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					for (TreeItem item : fileTree.getTree().getSelection())
						imageGroups.remove((String) item.getData());
					updateTree();
				}
			}
		});
		fileTree.getTree().addListener(SWT.Modify, this);

		previewImage = new Label(datasetConfigGroup, SWT.BORDER);
		previewImage.setBackground(new Color(Display.getDefault(), 64, 64, 64));
		gd = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd.widthHint = 256;
		gd.heightHint = 256;
		previewImage.setLayoutData(gd);
		previewImage.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				updatePreviewImage();
			}
		});

		addDirButton = new Button(datasetConfigGroup, SWT.PUSH);
		addDirButton.setText("Add Directory");
		addDirButton.setFocus();
		addDirButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(new Shell());
				dirDialog.setText("Add Directory");

				String dir = dirDialog.open();
				if (dir == null)
					return;

				imageGroups.add(new File(dir));
				updateTree();
			}
		});

		addFilesButton = new Button(datasetConfigGroup, SWT.PUSH);
		addFilesButton.setText("Add File(s)");
		addFilesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(new Shell(), SWT.MULTI);
				fileDialog.setText("Add File(s)");

				String[] exts = new String[EXTENSIONS_IMG.size()];
				for (int i = 0; i < exts.length; ++i)
					exts[i] = "*." + EXTENSIONS_IMG.get(i);
				fileDialog.setFilterExtensions(exts);

				if (fileDialog.open() == null)
					return;

				String basePath = fileDialog.getFilterPath();

				for (String file_name : fileDialog.getFileNames())
					imageGroups.add(new File(basePath, file_name.trim()));
				updateTree();
			}
		});

		createConfigPart(parentComposite, "Image", true);
		createConfigPart(parentComposite, "Marker", false);
	}

	/**
	 * Regroup all added images and rebuild the tree view.
	 */
	protected void updateTree() {
		imageGroups.refreshGroups();
		fileTree.refresh();
		handleEvent(null);
	}

	protected void updatePreviewImage() {
		Point s = previewImage.getSize();
		File imgFile = null;
		TreeItem[] selections = fileTree.getTree().getSelection();
		if (selections.length > 0)
			imgFile = imageGroups.getFiles().get(selections[0].getData());
		if (imgFile == null) {
			previewImage.setImage(null);
			previewImage.setText("Preview");
			previewImage.setToolTipText(null);
			return;
		}

		Image src = new Image(Display.getDefault(), imgFile.getAbsolutePath());
		Image dst = new Image(Display.getDefault(), s.x, s.y);
		GC gc = new GC(dst);

		double xScale = (double) s.x / src.getImageData().width;
		double yScale = (double) s.y / src.getImageData().height;

		// Scale to fit - image just fits in label.
		double scale = Math.min(xScale, yScale);
		int width = (int) (scale * src.getImageData().width);
		int height = (int) (scale * src.getImageData().height);
		int x = (s.x - width) / 2;
		int y = (s.y - height) / 2;

		gc.setBackground(new Color(Display.getDefault(), 64, 64, 64));
		gc.fillRectangle(0, 0, s.x, s.y);

		gc.setBackground(new Color(Display.getDefault(), 128, 128, 128));
		gc.fillRectangle(x, y, width, height);

		gc.drawImage(src, 0, 0, src.getImageData().width, src.getImageData().height, x, y, width, height);

		gc.dispose();

		previewImage.setImage(dst);
		previewImage.setToolTipText(imgFile.getAbsolutePath());
	}

	private Button createNewIDCategoryButton(Composite parent) {
		Button createIDCategoryButton = new Button(parent, SWT.PUSH);
		createIDCategoryButton.setText("New");
		createIDCategoryButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));

		return createIDCategoryButton;
	}

	private Button createNewIDTypeButton(Composite parent) {
		Button createIDTypeButton = new Button(parent, SWT.PUSH);
		createIDTypeButton.setText("New");

		return createIDTypeButton;
	}

	private void createConfigPart( Composite parent,
								   String title,
								   boolean isImage ) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(title + " Configuration");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite leftGroup = new Composite(group, SWT.NONE);
		leftGroup.setLayout(new GridLayout(2, false));
		leftGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		createIDCategoryGroup(leftGroup, isImage);
		createIDTypeGroup(leftGroup, isImage);

		Composite rightGroup = new Composite(group, SWT.NONE);
		rightGroup.setLayout(new GridLayout(2, false));
		rightGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

		createNewIDCategoryButton(rightGroup).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO
			}
		});
		createNewIDTypeButton(rightGroup).addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO
			}
		});
	}

	private void createIDTypeGroup(Composite parent, boolean isImage) {
		Label idTypeLabel = new Label(parent, SWT.SHADOW_ETCHED_IN);
		idTypeLabel.setText("Identifier");

		idTypeLabel.setLayoutData(new GridData(SWT.LEFT));
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setToolTipText("Identifiers are used to identify rows and columns and map them to other datasets or query public databases.");
		combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		combo.addListener(SWT.Modify, this);
		combo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
			// TODO
			}
		});

		if (isImage)
			imageIDTypeCombo = combo;
		else
			layerIDTypeCombo = combo;
	}

	private void createIDCategoryGroup(Composite parent, boolean isImage) {
		Label idCategoryLabel = new Label(parent, SWT.SHADOW_ETCHED_IN);
		idCategoryLabel.setText("Type");

		idCategoryLabel.setLayoutData(new GridData(SWT.LEFT));
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setToolTipText("ID classes define groups of ID types that can be mapped to each other. For example a 'gene' ID class could contain multiple ID types, such as 'ensemble ID' and 'gene short name' that can be mapped to each other.");
		combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		combo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				// TODO
			}
		});

		if (isImage)
			imageIDCategoryCombo = combo;
		else
			layerIDCategoryCombo = combo;
	}

	/**
	 *
	 */
	@Override
	public void fillDataSetDescription() {

	}

	public DataSetDescription getDataSetDescription() {
		return null;
	}

	@Override
	public boolean isPageComplete() {
		if (   nameText.getText().isEmpty()
			|| imageGroups == null
			|| imageGroups.getGroups().isEmpty() )
			return false;

		return super.isPageComplete();
	}

	@Override
	public IWizardPage getNextPage() {
		return null;
	}

	@Override
	public void handleEvent(Event event) {
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
	}

	@Override
	public void pageActivated() {
		ImageSet img = getWizard().getImageSet();
		nameText.setText(img.getName());
		imageGroups = img;
		files.fileGrouper = imageGroups;
		updateTree();

		resetCombo(imageIDCategoryCombo, img.getIdCategoryImage());
		resetCombo(imageIDTypeCombo, img.getIdTypeImage());
		resetCombo(layerIDCategoryCombo, img.getIDCategoryLayer());
		resetCombo(layerIDTypeCombo, img.getIDTypeLayer());

		getWizard().getContainer().updateButtons();
	}

	/**
	 * @return
	 */
	public String getDataSetName() {
		return nameText.getText();
	}

	protected void resetCombo(Combo combo, String entry) {
		combo.removeAll();
		combo.add(entry);
		combo.select(0);
	}

}

class FileTree implements ITreeContentProvider {

	public FilePrefixGrouper fileGrouper;

	/**
	 * Gets the root element(s) of the tree
	 *
	 * @param arg0
	 *            the input data
	 * @return Object[]
	 */
	@Override
	public Object[] getElements(Object arg0) {
		if (fileGrouper == null)
			return new Object[0];

		// These are the root elements of the tree
		// We don't care what arg0 is, because we just want all
		// the root nodes in the file system
		return fileGrouper.getGroups().isEmpty()
				? fileGrouper.getFiles().keySet().toArray()
				: fileGrouper.getGroups().keySet().toArray();
	}

	/**
	 * Gets the children of the specified object
	 *
	 * @param arg0
	 *            the parent object
	 * @return Object[]
	 */
	@Override
	public Object[] getChildren(Object arg0) {
		if (fileGrouper == null)
			return new Object[0];

		SortedSet<String> group = fileGrouper.getGroups().get(arg0);
		if (group != null)
			return group.toArray();

		return new Object[0];
	}

	/**
	 * Returns whether the passed object has children
	 *
	 * @param arg0
	 *            the parent object
	 * @return boolean
	 */
	@Override
	public boolean hasChildren(Object arg0) {
		return getChildren(arg0).length > 0;
	}

	/**
	 * Gets the parent of the specified object
	 *
	 * @param arg0
	 *            the object
	 * @return Object
	 */
	@Override
	public Object getParent(Object arg0) {
		if (   fileGrouper == null
			|| fileGrouper.getGroups().isEmpty()
			|| fileGrouper.getGroups().keySet().contains(arg0) )
			return null;

		// TODO search groups for element
		return null;
	}

	/**
	 * Disposes any created resources
	 */
	@Override
	public void dispose() {
		// Nothing to dispose
	}

	/**
	 * Called when the input changes
	 *
	 * @param arg0
	 *            the viewer
	 * @param arg1
	 *            the old input
	 * @param arg2
	 *            the new input
	 */
	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		// Nothing to change
	}
}