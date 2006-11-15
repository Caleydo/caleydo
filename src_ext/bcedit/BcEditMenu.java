// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BcEditMenu.java

package bcedit;

import java.io.PrintStream;
import java.util.Stack;
import java.util.Vector;
import javax.swing.*;

// Referenced classes of package bcedit:
//            BCActionListener, LoadMenu, MyMenuItem, bcedit, 
//            MainFrame

public class BcEditMenu extends JMenuBar
{

    public BcEditMenu()
    {
        Stack mstack = new Stack();
        JMenu akt_menu = null;
        JMenuItem mitem = null;
        BCActionListener aListener = new BCActionListener();
        menu = new LoadMenu("bcedit.mnu");
        for(int i = 0; i < menu.vmenu.size(); i++)
        {
            MyMenuItem m = (MyMenuItem)menu.vmenu.elementAt(i);
            if(m == null)
            {
                if(akt_menu == null)
                    continue;
                if(!mstack.empty())
                    akt_menu = (JMenu)mstack.pop();
                else
                    akt_menu = null;
                continue;
            }
            if(m.getType() == 0)
            {
                if(akt_menu != null)
                    mstack.push(akt_menu);
                akt_menu = new JMenu(m.getText());
                akt_menu.setEnabled(m.getEnabled());
                if(m.getMnemonicKey() != 0)
                    akt_menu.setMnemonic(m.getMnemonicKey());
                if(m.getAccelKey() != 0 && m.getAccelMaskModifier() != 0)
                    akt_menu.setAccelerator(KeyStroke.getKeyStroke(m.getAccelKey(), m.getAccelMaskModifier()));
                if(m.getStringAction() != null && m.getStringAction().length() > 0)
                {
                    akt_menu.setActionCommand(m.getStringAction());
                    akt_menu.addActionListener(bcedit.app.getActionListener());
                }
                if(!mstack.empty())
                {
                    JMenu jml = (JMenu)mstack.pop();
                    jml.add(akt_menu);
                    mstack.push(jml);
                } else
                {
                    add(akt_menu);
                }
                continue;
            }
            if(m.getText().equals("-"))
            {
                akt_menu.addSeparator();
                continue;
            }
            mitem = new JMenuItem(m.getText());
            mitem.setEnabled(m.getEnabled());
            if(m.getMnemonicKey() != 0)
                mitem.setMnemonic(m.getMnemonicKey());
            if(m.getAccelKey() != 0)
                mitem.setAccelerator(KeyStroke.getKeyStroke(m.getAccelKey(), m.getAccelMaskModifier()));
            if(m.getStringAction() != null && m.getStringAction().length() > 0)
            {
                mitem.setActionCommand(m.getStringAction());
                mitem.addActionListener(aListener);
            }
            akt_menu.add(mitem);
        }

    }

    public String[] getMenuAsText(boolean bDump)
    {
        String o[] = new String[menu.vmenu.size()];
        String oi = "";
        for(int i = 0; i < menu.vmenu.size(); i++)
        {
            MyMenuItem m = (MyMenuItem)menu.vmenu.elementAt(i);
            if(m == null)
            {
                oi = oi.substring(0, oi.length() - 4);
                o[i] = oi + "endm";
            } else
            {
                boolean bSkipset;
                if(m.getType() == 0)
                {
                    o[i] = oi + "menu=";
                    oi = oi + "    ";
                    bSkipset = false;
                } else
                if(m.getText().equals("-"))
                {
                    o[i] = oi + "item=seperator";
                    bSkipset = true;
                } else
                {
                    o[i] = oi + "item=";
                    bSkipset = false;
                }
                if(!bSkipset)
                    o[i] = o[i] + m.getText() + "," + m.getStringAccelKey() + "," + m.getStringAccelMaskModifier() + "," + m.getStringMnemonicKey() + "," + m.getStringAction();
            }
            for(; o[i].endsWith(",null"); o[i] = o[i].substring(0, o[i].length() - 5));
            for(; o[i].endsWith(",NONE"); o[i] = o[i].substring(0, o[i].length() - 5));
            if(bDump)
                System.out.println(o[i]);
        }

        return o;
    }

    static LoadMenu menu = null;

}
