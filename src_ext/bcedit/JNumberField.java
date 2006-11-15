// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JNumberField.java

package bcedit;

import java.awt.*;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class JNumberField extends JTextField
    implements FocusListener
{

    public JNumberField(int cols, int min, int max)
    {
        super(cols);
        bgColor = null;
        selColor = null;
        valueOnEnter = null;
        acceptedEntry = null;
        minValue = 0x80000000;
        maxValue = 0x7fffffff;
        minValue = min;
        maxValue = max;
        init();
    }

    public JNumberField(int cols)
    {
        super(cols);
        bgColor = null;
        selColor = null;
        valueOnEnter = null;
        acceptedEntry = null;
        minValue = 0x80000000;
        maxValue = 0x7fffffff;
        init();
    }

    private void init()
    {
        if(getColumns() < 3)
            setColumns(3);
        setBorder(BorderFactory.createEmptyBorder());
        addFocusListener(this);
    }

    public void processKeyEvent(KeyEvent e)
    {
        int id = e.getID();
        if(id == 401)
        {
            String newText = getText();
            int keyCode = e.getKeyCode();
            int cpos = getCaretPosition();
            int sstart = getSelectionStart();
            int send = getSelectionEnd();
            boolean bFire = false;
            if(!e.isShiftDown())
                switch(keyCode)
                {
                default:
                    break;

                case 110: // 'n'
                    if(acceptedEntry != null)
                    {
                        if(newText.equals(acceptedEntry[0]))
                            return;
                        setText(acceptedEntry[0]);
                        fireActionPerformed();
                    }
                    return;

                case 10: // '\n'
                case 30: // '\036'
                    if(checkValue())
                    {
                        if(selColor != null)
                        {
                            setSelectionColor(selColor);
                            selColor = null;
                        }
                        bFire = true;
                    } else
                    {
                        if(selColor == null)
                        {
                            selColor = getSelectionColor();
                            setSelectionColor(new Color(0xff0000));
                        }
                        getToolkit().beep();
                    }
                    setSelectionStart(0);
                    setSelectionEnd(newText.length());
                    repaint();
                    if(bFire)
                        fireActionPerformed();
                    return;

                case 27: // '\033'
                    newText = "";
                    setText(newText);
                    return;

                case 107: // 'k'
                case 521: 
                    newText = getSuccessor(newText);
                    setText(newText);
                    return;

                case 45: // '-'
                case 109: // 'm'
                    newText = getPredecessor(newText);
                    setText(newText);
                    return;

                case 48: // '0'
                case 49: // '1'
                case 50: // '2'
                case 51: // '3'
                case 52: // '4'
                case 53: // '5'
                case 54: // '6'
                case 55: // '7'
                case 56: // '8'
                case 57: // '9'
                case 96: // '`'
                case 97: // 'a'
                case 98: // 'b'
                case 99: // 'c'
                case 100: // 'd'
                case 101: // 'e'
                case 102: // 'f'
                case 103: // 'g'
                case 104: // 'h'
                case 105: // 'i'
                    if(sstart != send)
                    {
                        newText = newText.substring(0, sstart) + newText.substring(send);
                        select(sstart, sstart);
                        cpos = sstart;
                        setText(newText);
                    } else
                    if(newText.length() > 4)
                    {
                        getToolkit().beep();
                        return;
                    }
                    if(e.getKeyChar() != '\uFFFF')
                    {
                        if(cpos == newText.length())
                            newText = newText + e.getKeyChar();
                        else
                        if(cpos == 0)
                            newText = e.getKeyChar() + newText;
                        else
                            newText = newText.substring(0, cpos) + e.getKeyChar() + newText.substring(cpos);
                        setText(newText);
                        setCaretPosition(cpos + 1);
                        return;
                    }
                    break;

                case 8: // '\b'
                    if(sstart != send)
                    {
                        newText = newText.substring(0, sstart) + newText.substring(send);
                        setText(newText);
                        select(sstart, sstart);
                        setCaretPosition(sstart);
                        return;
                    }
                    if(cpos <= 0)
                        break;
                    if(cpos == 1)
                        newText = newText.substring(1);
                    else
                    if(cpos == newText.length())
                        newText = newText.substring(0, cpos - 1);
                    else
                        newText = newText.substring(0, cpos - 1) + newText.substring(cpos);
                    setText(newText);
                    setCaretPosition(cpos - 1);
                    return;
                }
            super.processKeyEvent(e);
        }
    }

    public void setEnabled(boolean flag)
    {
        if(flag)
        {
            if(bgColor != null)
            {
                setBackground(bgColor);
                bgColor = null;
            }
        } else
        if(bgColor == null)
        {
            bgColor = getBackground();
            setBackground(new Color(0xd6d3ce));
        }
        super.setEnabled(flag);
    }

    public void focusGained(FocusEvent e)
    {
        selectAll();
        valueOnEnter = getText();
    }

    public void focusLost(FocusEvent e)
    {
        if(getText().length() == 0)
        {
            setText(valueOnEnter);
            valueOnEnter = null;
        }
        if(checkValue())
        {
            if(selColor != null)
            {
                setSelectionColor(selColor);
                selColor = null;
            }
        } else
        {
            if(selColor == null)
            {
                selColor = getSelectionColor();
                setSelectionColor(new Color(0xff0000));
            }
            getToolkit().beep();
            selectAll();
            requestFocus();
        }
    }

    private String getSuccessor(String newText)
    {
    	try {
	        int i = 0;
	        if(newText.length() == 0)
	            if(acceptedEntry != null)
	                return acceptedEntry[i];
	            else
	                return Integer.toString(minValue);
	        if(acceptedEntry != null)
	            for(i = 0; i < acceptedEntry.length; i++)
	                if(newText.equals(acceptedEntry[i]) && i + 1 < acceptedEntry.length)
	                    return acceptedEntry[i + 1];
	
	        int testvalue = Integer.decode(newText).intValue() + 1;
	        if(testvalue > maxValue)
	            return Integer.toString(maxValue);
	        return Integer.toString(testvalue);
    	} catch (NumberFormatException nex) {
    		//nex;
    		return Integer.toString(minValue);
    	}
    }

    private String getPredecessor(String newText)
    {
    	int i = 0;
    	
    	try {
    		
	        if(newText.length() == 0)
	            if(acceptedEntry != null)
	                return acceptedEntry[i];
	            else
	                return Integer.toString(minValue);
	        if(acceptedEntry != null)
	            for(i = 0; i < acceptedEntry.length; i++)
	                if(newText.equals(acceptedEntry[i]))
	                    if(i > 0)
	                        return acceptedEntry[i - 1];
	                    else
	                        return newText;
	
	        int testvalue;
	        testvalue = Integer.decode(newText).intValue() - 1;
	        
	        if(testvalue >= minValue) {
//	            break MISSING_BLOCK_LABEL_129;
	        
		        if(acceptedEntry != null)
		            return acceptedEntry[acceptedEntry.length - 1];
		        
		        return Integer.toString(minValue);
	        }
	        
	        if(testvalue > maxValue)
	            return Integer.toString(maxValue);
	        return Integer.toString(testvalue);
	        
	    } catch(NumberFormatException nex) {
	        //nex;
	        if(acceptedEntry != null)
	            return acceptedEntry[i];
	        else
	            return Integer.toString(minValue);
	    }
    }

    public boolean checkValue()
    {
        boolean bReturn = true;
        String newText = getText();
        if(acceptedEntry != null)
        {
            for(int i = 0; i < acceptedEntry.length; i++)
                if(newText.equals(acceptedEntry[i]))
                    return true;

        }
        if(newText.length() == 0)
            return bReturn;
        try
        {
            int testvalue = Integer.decode(newText).intValue();
            if(testvalue > maxValue || testvalue < minValue)
                bReturn = false;
        }
        catch(NumberFormatException nex)
        {
            bReturn = false;
        }
        return bReturn;
    }

    public Dimension getPreferredSize()
    {
        String testString = " ";
        int i = getColumns();
        java.awt.Font myFont = getFont();
        if(myFont == null)
            return super.getPreferredSize();
        Dimension d = new Dimension(0, 0);
        FontMetrics f = getFontMetrics(myFont);
        for(; i > 0; i--)
            testString = testString + " ";

        int miv;
        if(minValue == 0x80000000)
            miv = 0;
        else
            miv = minValue;
        int mav;
        if(maxValue == 0x7fffffff)
            mav = 0;
        else
            mav = maxValue;
        int w1 = f.stringWidth(Integer.toString(miv)) + 2 * f.charWidth(' ');
        int w2 = f.stringWidth(Integer.toString(mav)) + 2 * f.charWidth(' ');
        int w3 = f.stringWidth(testString) + 2 * f.charWidth(' ');
        d.width = Math.max(w1, Math.max(w2, w3));
        d.height = f.getHeight();
        if(acceptedEntry != null)
            for(i = 0; i < acceptedEntry.length; i++)
                d.width = Math.max(d.width, f.stringWidth(acceptedEntry[i]) + 2 * f.charWidth(' '));

        return d;
    }

    private Color bgColor;
    private Color selColor;
    private String valueOnEnter;
    public String acceptedEntry[];
    public int minValue;
    public int maxValue;
}
