// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CPTab.java

package bcedit;

import java.awt.*;
import java.util.Vector;
import javax.swing.*;

// Referenced classes of package bcedit:
//            ListLabel, SizedPanel, CbListCellRenderer

public class CPTab extends JPanel
{

    public CPTab(Vector v, JPanel selector, boolean useButtons)
    {
        selectJPanel = null;
        bUseButtons = false;
        sInfo = "";
        bUseButtons = useButtons;
        slist = new String[v.size()];
        for(int i = 0; i < v.size(); i++)
            slist[i] = (String)v.elementAt(i);

        init(selector, slist.length);
    }

    CPTab(String s[], JPanel selector, boolean useButtons)
    {
        selectJPanel = null;
        bUseButtons = false;
        sInfo = "";
        bUseButtons = useButtons;
        slist = s;
        init(selector, s.length);
    }

    CPTab(int cnt)
    {
        selectJPanel = null;
        bUseButtons = false;
        sInfo = "";
        slist = new String[cnt];
        init(null, cnt);
    }

    void init(JPanel selector, int cnt)
    {
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setOpaque(false);
        selectJPanel = selector;
        scrollpane = new JScrollPane[cnt];
        list = new JList[cnt];
        listIndex = new int[cnt];
        lLabel = new ListLabel[cnt];
        if(bUseButtons)
            lbutton = new ListLabel[cnt * 2];
        setLayout(gb);
        c.fill = 1;
        c.anchor = 10;
        c.weightx = 0.0D;
        c.weighty = 0.0D;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridy = 1;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 0, 0);
        if(selectJPanel != null)
        {
            c.gridy++;
            gb.setConstraints(selectJPanel, c);
            add(selectJPanel);
            c.gridy++;
            c.weighty = 0.0D;
            SizedPanel sp = new SizedPanel(8, 8);
            gb.setConstraints(sp, c);
            sp.setOpaque(false);
            add(sp);
        }
        for(int i = 0; i < cnt; i++)
        {
            list[i] = new JList();
            lLabel[i] = new ListLabel(slist[i]);
            scrollpane[i] = new JScrollPane();
            listIndex[i] = -1;
            setJListDefault(list[i]);
            lLabel[i].setBControlDefault();
            lLabel[i].setName(sInfo + ":" + slist[i]);
            list[i].setName(slist[i]);
            c.weighty = 1.0D;
            c.gridy++;
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setName("List " + Integer.toString(i));
            p.setLayout(new BorderLayout(0, 0));
            scrollpane[i].getViewport().setView(list[i]);
            scrollpane[i].setBorder(BorderFactory.createEmptyBorder());
            scrollpane[i].setVerticalScrollBarPolicy(22);
            p.add("North", lLabel[i]);
            p.add("Center", scrollpane[i]);
            if(bUseButtons)
            {
                ListLabel pb = new ListLabel("");
                pb.setBControlDefault();
                pb.useGradient(false, true);
                lbutton[i * 2] = new ListLabel("load");
                lbutton[i * 2].setBControlButtonDefault2();
                pb.add(lbutton[i * 2]);
                lbutton[i * 2 + 1] = new ListLabel("delete");
                lbutton[i * 2 + 1].setBControlButtonDefault2();
                pb.add(lbutton[i * 2 + 1]);
                p.add("South", pb);
            }
            gb.setConstraints(p, c);
            add(p);
            if(i + 1 != cnt)
            {
                c.gridy++;
                c.weighty = 0.0D;
                SizedPanel sp = new SizedPanel(8, 8);
                gb.setConstraints(sp, c);
                sp.setOpaque(false);
                add(sp);
            }
        }

    }

    public void setJListDefault(JList l)
    {
        l.setPrototypeCellValue("00:Noname1234 Erfg Wert Yxcv");
        l.setDragEnabled(true);
        l.setVisibleRowCount(4);
        l.setBackground(new Color(0x4a7597));
        l.setForeground(new Color(0xffd800));
        l.setSelectionBackground(new Color(0xffd800));
        l.setSelectionForeground(new Color(0x4a7597));
        l.setCellRenderer(new CbListCellRenderer());
    }

    public JScrollPane scrollpane[];
    public JList list[];
    public int listIndex[];
    public ListLabel lLabel[];
    public String slist[];
    public JPanel selectJPanel;
    public boolean bUseButtons;
    public ListLabel lbutton[];
    public String sInfo;
}
