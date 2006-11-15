// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ComboBoxNumberEdit.java

package bcedit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

// Referenced classes of package bcedit:
//            ComboBoxNumberEdit

class CBNumberAL
    implements ActionListener
{

    CBNumberAL()
    {
    }

    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        Object obj = e.getSource();
        if(obj instanceof ComboBoxNumberEdit)
        {
            ComboBoxNumberEdit cb = (ComboBoxNumberEdit)obj;
            try
            {
                String str = (String)cb.getSelectedItem();
                if(str != null)
                {
                    int i = Integer.decode(str).intValue();
                    if(i > 16383)
                        cb.setSelectedItem("");
                }
            }
            catch(NumberFormatException nexception)
            {
                cb.setSelectedItem("");
            }
            System.out.println("ComboBox: " + (String)cb.getSelectedItem());
        } else
        {
            System.out.println("Unknown Command:" + cmd);
        }
    }
}
