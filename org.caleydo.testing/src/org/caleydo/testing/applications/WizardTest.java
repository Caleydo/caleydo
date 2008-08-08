package org.caleydo.testing.applications;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class WizardTest
	extends ApplicationWindow
{
	public static WizardTest mainWindow;
	AddEntryAction addEntryAction;
	List entries;
	private TableViewer viewer;

	public WizardTest()
	{
		super(null);
		mainWindow = this;
		addEntryAction = new AddEntryAction();
		entries = new LinkedList();
		addToolBar(SWT.NONE);
	}

	public void run()
	{
		setBlockOnOpen(true);
		open();
		Display.getCurrent().dispose();
	}

	public void add(AddressEntry entry)
	{
		entries.add(entry);
		refresh();
	}

	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setSize(600, 400);
	}

	protected Control createContents(Composite parent)
	{
		viewer = new TableViewer(parent);
		viewer.setContentProvider(new AddressBookContentProvider());
		viewer.setLabelProvider(new AddressBookLabelProvider());
		viewer.setInput(entries);

		Table table = viewer.getTable();
		new TableColumn(table, SWT.LEFT).setText("First Name");
		new TableColumn(table, SWT.LEFT).setText("Last Name");
		new TableColumn(table, SWT.LEFT).setText("E-mail Address");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		refresh();

		return table;
	}

	protected ToolBarManager createToolBarManager(int style)
	{
		ToolBarManager tbm = new ToolBarManager(style);
		tbm.add(addEntryAction);
		return tbm;
	}

	private void refresh()
	{
		viewer.refresh();
		Table table = viewer.getTable();
		for (int i = 0, n = table.getColumnCount(); i < n; i++)
		{
			table.getColumn(i).pack();
		}
	}

	public static void main(String[] args)
	{
		new WizardTest().run();
	}
}

class AddEntryAction
	extends Action
{
	public AddEntryAction()
	{
		super("Add Entry");
		setToolTipText("Add Entry");
	}

	public void run()
	{
		WizardDialog dlg = new WizardDialog(WizardTest.mainWindow.getShell(),
				new AddEntryWizard());
		dlg.open();
	}
}

class AddEntryWizard
	extends Wizard
{
	private WelcomePage welcomePage = new WelcomePage();

	private NamePage namePage = new NamePage();

	private EmailPage emailPage = new EmailPage();

	public AddEntryWizard()
	{
		addPage(welcomePage);
		addPage(namePage);
		addPage(emailPage);

		setWindowTitle("Address Book Entry Wizard");
	}

	public boolean performFinish()
	{
		AddressEntry entry = new AddressEntry();
		entry.setFirstName(namePage.getFirstName());
		entry.setLastName(namePage.getLastName());
		entry.setEmail(emailPage.getEmail());

		WizardTest.mainWindow.add(entry);

		return true;
	}
}

class EmailPage
	extends WizardPage
{
	private String email = "";

	public EmailPage()
	{
		super("E-mail", "E-mail Address", ImageDescriptor.createFromFile(EmailPage.class,
				"email.gif"));
		setDescription("Enter the e-mail address");
		setPageComplete(false);
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		new Label(composite, SWT.LEFT).setText("E-mail Address:");
		final Text ea = new Text(composite, SWT.BORDER);
		ea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		ea.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent event)
			{
				email = ea.getText();
				setPageComplete(email.length() > 0);
			}
		});

		setControl(composite);
	}

	public String getEmail()
	{
		return email;
	}
}

class NamePage
	extends WizardPage
{
	private String firstName = "";

	private String lastName = "";

	public NamePage()
	{
		super("Name", "Name", ImageDescriptor.createFromFile(NamePage.class, "name.gif"));
		setDescription("Enter the first and last names");
		setPageComplete(false);
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		new Label(composite, SWT.LEFT).setText("First Name:");
		final Text first = new Text(composite, SWT.BORDER);
		first.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.LEFT).setText("Last Name:");
		final Text last = new Text(composite, SWT.BORDER);
		last.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		first.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent event)
			{
				firstName = first.getText();
				setPageComplete(firstName.length() > 0 && lastName.length() > 0);
			}
		});

		last.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent event)
			{
				lastName = last.getText();
				setPageComplete(firstName.length() > 0 && lastName.length() > 0);
			}
		});

		setControl(composite);
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}
}

class WelcomePage
	extends WizardPage
{
	protected WelcomePage()
	{
		super("Welcome", "Welcome", ImageDescriptor.createFromFile(WelcomePage.class,
				"welcome.gif"));
		setDescription("Welcome to the Address Book Entry Wizard");
	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		new Label(composite, SWT.CENTER).setText("Welcome to the Address Book Entry Wizard!");
		new Label(composite, SWT.LEFT)
				.setText("This wizard guides you through creating an Address Book entry.");
		new Label(composite, SWT.LEFT).setText("Click Next to continue.");
		setControl(composite);
	}
}

class AddressEntry
{
	private String lastName;

	private String firstName;

	private String email;

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
}

class AddressBookLabelProvider
	implements ITableLabelProvider
{
	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}

	public String getColumnText(Object element, int columnIndex)
	{
		AddressEntry ae = (AddressEntry) element;
		switch (columnIndex)
		{
			case 0:
				return ae.getFirstName();
			case 1:
				return ae.getLastName();
			case 2:
				return ae.getEmail();
		}
		return "";
	}

	public void addListener(ILabelProviderListener listener)
	{
	}

	public void dispose()
	{
	}

	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	public void removeListener(ILabelProviderListener listener)
	{
	}
}

class AddressBookContentProvider
	implements IStructuredContentProvider
{
	public Object[] getElements(Object inputElement)
	{
		return ((List) inputElement).toArray();
	}

	public void dispose()
	{
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}
}
