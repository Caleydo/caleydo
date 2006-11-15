// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import bcedit.BCL.BCDevice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Referenced classes of package bcedit:
//            BCActionListener

class EditorGlobals
    implements ActionListener
{

    public EditorGlobals()
    {
        if(!bRegistered)
        {
            BCActionListener bca = new BCActionListener();
            bca.registerEditorGlobals(this);
            bRegistered = true;
        }
    }

    public synchronized void actionPerformed(ActionEvent actionevent)
    {
    }

    public static BCDevice BControl = null;
    public static int selectedPreset = -1;
    public static int selectedControl = -1;
    private static boolean bRegistered = false;

}
