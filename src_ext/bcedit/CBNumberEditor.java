// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ComboBoxNumberEdit.java

package bcedit;

import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;

// Referenced classes of package bcedit:
//            CBNumberAL, JNumberField

class CBNumberEditor extends BasicComboBoxEditor
{

    public CBNumberEditor()
    {
        addActionListener(new CBNumberAL());
        editor = new JNumberField(editor.getColumns());
    }

    public void setMinValue(int minValue)
    {
        if(editor instanceof JNumberField)
            ((JNumberField)editor).minValue = minValue;
    }

    public void setMaxValue(int maxValue)
    {
        if(editor instanceof JNumberField)
            ((JNumberField)editor).maxValue = maxValue;
    }

    public String getText()
    {
        return ((JNumberField)editor).getText();
    }

    public void setText(String text)
    {
        ((JNumberField)editor).setText(text);
    }

    public void focusGained(FocusEvent focusevent)
    {
    }

    public void selectAll()
    {
        int len = editor.getText().length();
        if(len > 0)
        {
            editor.setCaretPosition(len);
            editor.select(0, len);
        }
    }
}
