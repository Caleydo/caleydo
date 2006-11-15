// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MainFrame.java

package bcedit;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Referenced classes of package bcedit:
//            FPanel, GPanel, MyGlassPanel, BCActionListener, 
//            BcEditMenu, SizedPanel, ControllerPanel, ComputerPanel, 
//            EditorPanel, BCDefaults, BCSettings, BCInternalNotify, 
//            FPanel2, BCImages

public class MainFrame extends JFrame
    implements ActionListener
{

    public MainFrame()
    {
        maxX = 952;
        maxY = 720;
        MainMenuBar = null;
        actionListener = null;
        bReady = false;
        MyContentPane = null;
        HardwareFrame = new FPanel(240, 600, -1);
        EditorFrame = new FPanel(400, 600, 64);
        SoftwareFrame = new FPanel(240, 600, -1);
        dHardwareFrame = null;
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        MyContentPane = new GPanel(BCImages.getImage("BG1.png"));
        MyContentPane.setName("MainWindow-ContentPane");
        setTitle("BEHRINGER B-CONTROL SERIES EDITOR");
        setIconImage((new ImageIcon(BCImages.getImage("bcedit_icon.png"))).getImage());
        setDefaultLookAndFeelDecorated(true);
        MyContentPane.bAllowResize = true;
        MyContentPane.bKeepAspectRatio = false;
        setContentPane(MyContentPane);
        Container contentPane = getContentPane();
        getRootPane().setGlassPane(new MyGlassPanel());
        actionListener = new BCActionListener();
        MainMenuBar = new BcEditMenu();
        setJMenuBar(MainMenuBar);
        actionListener.setMainMenu(MainMenuBar);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        layout = new BorderLayout();
        contentPane.setLayout(layout);
        GPanel PanelNorth = new GPanel(BCImages.getImage("empty8x16.png"));
        GPanel PanelSouth = new GPanel(BCImages.getImage("empty8x16.png"));
        SizedPanel buttonPane[] = new SizedPanel[2];
        JPanel EditorPanelExtender = new JPanel();
        EditorPanelExtender.setOpaque(false);
        for(int i = 0; i < 2; i++)
        {
            buttonPane[i] = new SizedPanel(48, 40);
            buttonPane[i].setOpaque(false);
            buttonPane[i].setLayout(null);
            if(i == 0)
                buttonPane[i].setName("ButtonPanel-Left");
            else
                buttonPane[i].setName("ButtonPanel-Right");
        }

        actionListener.setButtonPanels(buttonPane);
        HardwareFrame.setName("HardwareFrame");
        EditorFrame.setName("EditorFrame");
        SoftwareFrame.setName("SoftwareFrame");
        HardwareFrame.setOpaque(false);
        EditorFrame.setOpaque(false);
        SoftwareFrame.setOpaque(false);
        HardwareFrame.setCenterObject(new ControllerPanel());
        SoftwareFrame.setCenterObject(new ComputerPanel());
        EditorFrame.setCenterObject(new EditorPanel());
        HardwareFrame.getCenterObject().setName("ControllerPanel");
        SoftwareFrame.getCenterObject().setName("ComputerPanel");
        EditorFrame.getCenterObject().setName("EditorFrame");
        JPanel jp = new JPanel();
        jp.setOpaque(false);
        jp.setLayout(new BorderLayout());
        SizedPanel sp = new SizedPanel(16, 16);
        sp.setOpaque(false);
        jp.add("West", sp);
        jp.add("Center", HardwareFrame);
        contentPane.add("West", jp);
        EditorPanelExtender.setLayout(new BorderLayout(2, 0));
        EditorPanelExtender.add("West", buttonPane[0]);
        EditorPanelExtender.add("East", buttonPane[1]);
        EditorPanelExtender.add("Center", EditorFrame);
        contentPane.add("Center", EditorPanelExtender);
        jp = new JPanel();
        jp.setOpaque(false);
        jp.setLayout(new BorderLayout());
        jp.add("Center", SoftwareFrame);
        sp = new SizedPanel(16, 16);
        sp.setOpaque(false);
        jp.add("East", sp);
        contentPane.add("East", jp);
        contentPane.add("North", PanelNorth);
        contentPane.add("South", PanelSouth);
        setDefaultCloseOperation(0);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e)
            {
                actionListener.RequestCloseApplication();
            }

        }
);
        layout.invalidateLayout(contentPane);
        layout.layoutContainer(contentPane);
        SwingUtilities.updateComponentTreeUI(this);
        pack();
    }

    public void run()
    {
        layout.layoutContainer(getContentPane());
        repaint();
        new BCDefaults(getToolkit());
        setCursor(BCDefaults.curDefault);
        MainMenuBar.setCursor(BCDefaults.curDefault);
        BCSettings settings = new BCSettings();
        if(settings.getBooleanValue("scan MIDI on startup", false))
            (new BCInternalNotify(HardwareFrame.getCenterObject(), "CONTROLER:ScanMidi", 500L)).start();
        if(settings.getBooleanValue("restore session", false))
            (new BCInternalNotify(EditorFrame.getCenterObject(), "EDITOR:RestoreSession", 500L)).start();
    }

    private FPanel2 makeFPanel2(Component o, Component c)
    {
        FPanel2 fp = new FPanel2(o);
        fp.setOpaque(false);
        fp.setCenterObject(c);
        return fp;
    }

    public BCActionListener getActionListener()
    {
        return actionListener;
    }

    public boolean requestScanHardware()
    {
        return false;
    }

    public boolean newHardwareSelected(int hwId)
    {
        return false;
    }

    public Dimension getMaximumSize()
    {
        return getPreferredSize();
    }

    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }

    public Dimension getPreferredSize()
    {
        Dimension d = new Dimension(maxX, maxY);
        return d;
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getActionCommand().equals("FoldHardwareFrame"))
            if(dHardwareFrame == null)
            {
                dHardwareFrame = HardwareFrame.getPreferredSize();
                HardwareFrame.setPreferredSize(new Dimension(0, 0));
                layout.layoutContainer(getContentPane());
                repaint();
            } else
            {
                dHardwareFrame = null;
                HardwareFrame.setPreferredSize(null);
                layout.layoutContainer(getContentPane());
                repaint();
            }
    }

    private int maxX;
    private int maxY;
    private BcEditMenu MainMenuBar;
    private BCActionListener actionListener;
    private boolean bReady;
    private GPanel MyContentPane;
    BorderLayout layout;
    FPanel HardwareFrame;
    FPanel EditorFrame;
    FPanel SoftwareFrame;
    Dimension dHardwareFrame;

}
