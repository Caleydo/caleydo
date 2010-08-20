package org.caleydo.view.datameta;

import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.EventPublisher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Data meta view that is located in the side-bar. It shows basic meta data about the data set.
 * 
 * @author Marc Streit
 */
public class DataMetaView implements IDataDomainBasedView<ASetBasedDataDomain> {

	ASetBasedDataDomain dataDomain;
	
	Set set; 

	GeneralManager generalManager = null;
	EventPublisher eventPublisher = null;

	ContentSelectionManager contentSelectionManager;
	StorageSelectionManager storageSelectionManager;

	/**
	 * Constructor.
	 */
	public DataMetaView() {
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();
//		registerEventListeners();
	}

	public Control createControl(final Composite parent) {

//		Button button = new Button(parent, SWT.PUSH);
//		button.setText("hallo");
		
		Label label = new Label(parent, SWT.NONE);
		label.setText("Number of genes: "+set.depth());
		
		label = new Label(parent, SWT.NONE);
		label.setText("Number of experiments: "+set.size());
		
		label = new Label(parent, SWT.NONE);
		label.setText("Loaded from file: "+dataDomain.getFileName());
		
		label = new Label(parent, SWT.NONE);
		label.setText("Human readable ID type: "+dataDomain.getHumanReadableContentIDType().getTypeName());

//		Tree<ClusterNode> storageTree = dataDomain.getSet().getStorageData(StorageVAType.STORAGE).getStorageTree();
		
//		label = new Label(parent, SWT.NONE);
//		label.setText("Experiments clustered: "+storageTree == null ? "false" : "true");
//
//		if (dataDomain.getSet().getStorageData(StorageVAType.STORAGE).getStorageClusterSizes() != null) {
//			label = new Label(parent, SWT.NONE);
//			label.setText("Number of clusters: "+dataDomain.getSet().getStorageData(StorageVAType.STORAGE).getStorageClusterSizes().size());			
//		}

//		label = new Label(parent, SWT.NONE);
//		label.setText(": "+dataDomain);


		return parent;
	}

	public void dispose() {
//		unregisterEventListeners();
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		
		this.set = dataDomain.getSet();
		
		ContentVAType contentVAType = ContentVAType.CONTENT;
		contentSelectionManager = dataDomain.getContentSelectionManager();

		ContentVirtualArray contentVA = dataDomain.getContentVA(contentVAType);
		contentSelectionManager.setVA(contentVA);
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}
}
