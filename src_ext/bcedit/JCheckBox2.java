// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JCheckBox2.java

package bcedit;

import javax.swing.JCheckBox;
import javax.swing.JToolTip;

// Referenced classes of package bcedit:
//            JMultiLineToolTip

public class JCheckBox2 extends JCheckBox
{

    public JCheckBox2()
    {
    }

    public JToolTip createToolTip()
    {
        return new JMultiLineToolTip();
    }
}
