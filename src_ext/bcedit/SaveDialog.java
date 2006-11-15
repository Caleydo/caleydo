// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SaveDialog.java

package bcedit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// Referenced classes of package bcedit:
//            BCSettings, WaitCursorAni, SizedPanel, JPanelMy, 
//            JLabel2, FPanel2, CbListCellRenderer, BCDefaults, 
//            FPanel

public class SaveDialog extends JDialog
    implements ActionListener, ListSelectionListener
{

    public SaveDialog(String startpath, String Extensions, int ModelType)
    {
        useStandardFrame = true;
        settings = new BCSettings();
        contentPane = null;
        panel = null;
        filelist = null;
        scrollpane = new JScrollPane();
        filenameinput = new JTextField();
        exitCode = -1;
        ext = null;
        bgColor = BCDefaults.bgColor;
        fgColor = BCDefaults.fgColor;
        init(startpath, Extensions, ModelType);
    }

    public SaveDialog(String startpath, String Extensions)
    {
        useStandardFrame = true;
        settings = new BCSettings();
        contentPane = null;
        panel = null;
        filelist = null;
        scrollpane = new JScrollPane();
        filenameinput = new JTextField();
        exitCode = -1;
        ext = null;
        bgColor = BCDefaults.bgColor;
        fgColor = BCDefaults.fgColor;
        init(startpath, Extensions, -1);
    }

    private void init(String startpath, String Extensions, int modelType)
    {
        WaitCursorAni wca = new WaitCursorAni();
        wca.start();
        this.startpath = startpath;
        if(Extensions != null && Extensions.trim().length() > 0)
        {
            StringTokenizer tok = new StringTokenizer(Extensions, ";");
            int cnt = tok.countTokens();
            if(cnt > 0)
            {
                ext = new String[cnt];
                cnt = 0;
                while(tok.hasMoreElements()) 
                    ext[cnt++] = tok.nextToken();
            }
        }
        contentPane = new JPanel();
        contentPane.setBackground(bgColor);
        contentPane.setForeground(fgColor);
        contentPane.setLayout(new BorderLayout());
        contentPane.add("North", new SizedPanel(8, 8));
        contentPane.add("East", new SizedPanel(8, 8));
        contentPane.add("West", new SizedPanel(8, 8));
        JPanel sizedPanel = new JPanel();
        JButton btnOk = new JButton("Ok");
        btnOk.setActionCommand("Accept");
        btnOk.addActionListener(this);
        btnOk.setDefaultCapable(true);
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setActionCommand("Cancel");
        btnCancel.addActionListener(this);
        sizedPanel.add(btnOk);
        sizedPanel.add(btnCancel);
        contentPane.add("South", sizedPanel);
        panel = new JPanelMy(true);
        panel.setLayout(new BorderLayout(0, 4));
        File f = new File(startpath);
        String dirlist[] = f.list();
        if(ext != null)
        {
            Vector v = new Vector();
            for(int i = 0; i < dirlist.length; i++)
            {
                for(int j = 0; j < ext.length; j++)
                    if(dirlist[i].endsWith(ext[j]))
                        v.add(dirlist[i].substring(0, dirlist[i].length() - ext[j].length()));

            }

            filelist = new JList(v);
        } else
        {
            filelist = new JList(dirlist);
        }
        setJListDefault(filelist);
        filelist.addListSelectionListener(this);
        scrollpane.getViewport().setView(filelist);
        scrollpane.setBorder(BorderFactory.createEmptyBorder());
        filenameinput.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel2 jlb = new JLabel2("Please name the device file");
        panel.add("North", jlb);
        panel.add("Center", scrollpane);
        panel.add("South", filenameinput);
        scrollpane.setBorder(BorderFactory.createEtchedBorder(0));
        panel.setBackground(BCDefaults.bgPanelColor);
        contentPane.add("Center", FPanel.createPanel(panel));
        if(useStandardFrame)
        {
            setContentPane(contentPane);
        } else
        {
            setContentPane(FPanel2.createPanel(this, contentPane));
            setUndecorated(true);
        }
        setModal(true);
        setResizable(true);
        reColor(this);
        jlb.use3D = false;
        filenameinput.setBackground(new Color(0xffffff));
        filenameinput.setForeground(new Color(0));
        pack();
        wca.stopit();
    }

    public void setTitle(String nt)
    {
        if(isUndecorated())
        {
            Container c = getContentPane();
            if(c instanceof FPanel2)
                ((FPanel2)c).setTitle(nt);
        } else
        {
            super.setTitle(nt);
        }
    }

    private void reColor(Object o)
    {
        if((o instanceof Component) && !(o instanceof JButton))
        {
            Component c = (Component)o;
            c.setBackground(bgColor);
            c.setForeground(fgColor);
        }
        if(o instanceof Container)
        {
            Container c = (Container)o;
            for(int i = 0; i < c.getComponentCount(); i++)
                reColor(c.getComponent(i));

        }
    }

    public void centerIn(Rectangle r)
    {
        Rectangle r2 = getBounds();
        r2.x = r.x + (r.width - r2.width) / 2;
        r2.y = r.y + (r.height - r2.height) / 2;
        setBounds(r2);
    }

    public void setParent(JComponent comp)
    {
        Rectangle r = comp.getBounds();
        Rectangle r2 = getBounds();
        r2.x = r.x + (r2.width - r.width) / 2;
        r2.y = r.y + (r2.height - r.height) / 2;
    }

    public void setJListDefault(JList l)
    {
        l.setSelectionMode(0);
        l.setDragEnabled(false);
        l.setVisibleRowCount(8);
        l.setBackground(new Color(0x4a7597));
        l.setForeground(new Color(0xffd800));
        l.setSelectionBackground(new Color(0xffd800));
        l.setSelectionForeground(new Color(0x4a7597));
        l.setCellRenderer(new CbListCellRenderer());
    }

    public int getExitCode()
    {
        return exitCode;
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() instanceof JButton)
        {
            if(e.getActionCommand().equals("Accept"))
            {
                if(filenameinput.getText() == null)
                    return;
                if(filenameinput.getText().trim().length() == 0)
                {
                    return;
                } else
                {
                    exitCode = 1;
                    hide();
                    return;
                }
            }
            if(e.getActionCommand().equals("Cancel"))
            {
                exitCode = 2;
                hide();
                return;
            }
        }
    }

    public void valueChanged(ListSelectionEvent e)
    {
        if(e.getSource() == filelist)
        {
            filenameinput.setText((String)filelist.getSelectedValue());
            filenameinput.repaint();
        }
    }

    boolean useStandardFrame;
    public static final int IDOK = 1;
    public static final int IDCANCEL = 2;
    public BCSettings settings;
    public String startpath;
    public JPanel contentPane;
    public JPanelMy panel;
    public JList filelist;
    public JScrollPane scrollpane;
    public JTextField filenameinput;
    private int exitCode;
    public String ext[];
    public Color bgColor;
    public Color fgColor;
    private static String file_seperator = System.getProperty("file.separator");

}
