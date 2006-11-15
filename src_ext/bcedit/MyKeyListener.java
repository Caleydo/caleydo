// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ComboBoxNumberEdit.java

package bcedit;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class MyKeyListener
    implements KeyListener
{

    public MyKeyListener()
    {
    }

    public void keyTyped(KeyEvent e)
    {
        displayInfo(e, "KEY TYPED: ");
    }

    public void keyPressed(KeyEvent e)
    {
        displayInfo(e, "KEY PRESSED: ");
    }

    public void keyReleased(KeyEvent e)
    {
        displayInfo(e, "KEY RELEASED: ");
    }

    protected void displayInfo(KeyEvent e, String s)
    {
        int id = e.getID();
        if(id == 400)
        {
            char c = e.getKeyChar();
            String keyString = "key character = '" + c + "'";
        } else
        {
            int keyCode = e.getKeyCode();
            String keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")";
        }
        int modifiers = e.getModifiersEx();
        String modString = "modifiers = " + modifiers;
        String tmpString = KeyEvent.getModifiersExText(modifiers);
        if(tmpString.length() > 0)
            modString = modString + " (" + tmpString + ")";
        else
            modString = modString + " (no modifiers)";
        String actionString = "action key? ";
        if(e.isActionKey())
            actionString = actionString + "YES";
        else
            actionString = actionString + "NO";
        String locationString = "key location: ";
        int location = e.getKeyLocation();
        if(location == 1)
            locationString = locationString + "standard";
        else
        if(location == 2)
            locationString = locationString + "left";
        else
        if(location == 3)
            locationString = locationString + "right";
        else
        if(location == 4)
            locationString = locationString + "numpad";
        else
            locationString = locationString + "unknown";
    }
}
