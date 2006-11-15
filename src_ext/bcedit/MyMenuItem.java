// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BcEditMenu.java

package bcedit;


class MyMenuItem
{

    public MyMenuItem(String menuText, String accelerationKey, String accelerationmaskm, String mnemonic_Key, String action, boolean bEnable, int setType)
    {
        stringText = null;
        stringAccelKey = null;
        stringAccelMaskModifier = null;
        stringMnemonicKey = null;
        stringAction = null;
        bIsEnabled = true;
        accelKey = 0;
        accelMaskModifier = 0;
        mnemonicKey = 0;
        stringText = menuText;
        stringAccelKey = accelerationKey;
        accelKey = accelKeyToInt(accelerationKey);
        stringAccelMaskModifier = accelerationmaskm;
        accelMaskModifier = maskKeyToInt(accelerationmaskm);
        stringMnemonicKey = mnemonic_Key;
        mnemonicKey = accelKeyToInt(mnemonic_Key);
        stringAction = action;
        bIsEnabled = bEnable;
        type = setType;
    }

    public MyMenuItem(String t, int acc_k, int acc_mm, int key)
    {
        stringText = null;
        stringAccelKey = null;
        stringAccelMaskModifier = null;
        stringMnemonicKey = null;
        stringAction = null;
        bIsEnabled = true;
        accelKey = 0;
        accelMaskModifier = 0;
        mnemonicKey = 0;
        stringText = t;
        accelKey = acc_k;
        stringAccelKey = accelKeyFromInt(accelKey);
        accelMaskModifier = acc_mm;
        stringAccelMaskModifier = maskKeyFromInt(accelMaskModifier);
        mnemonicKey = key;
        stringAccelKey = accelKeyFromInt(mnemonicKey);
    }

    private String maskKeyFromInt(int value)
    {
        for(int i = 0; i < maskValue.length; i++)
            if(maskValue[i] == value)
                return maskName[i];

        return null;
    }

    private int maskKeyToInt(String stringKey)
    {
        for(int i = 0; i < maskName.length; i++)
            if(maskName[i].equals(stringKey))
                return maskValue[i];

        return 0;
    }

    private String accelKeyFromInt(int value)
    {
        for(int i = 0; i < keyValue.length; i++)
            if(keyValue[i] == value)
                return keyName[i];

        return null;
    }

    private int accelKeyToInt(String stringKey)
    {
        if(stringKey == null)
            return 0;
        if(stringKey.length() == 0)
            return 0;
        for(int i = 0; i < keyName.length; i++)
            if(keyName[i].equals(stringKey))
                return keyValue[i];

        return 0;
    }

    public void setText(String text)
    {
        stringText = text;
    }

    public void setAccelKey(int ak)
    {
        accelKey = ak;
        stringAccelKey = accelKeyFromInt(accelKey);
    }

    public void setAccelMaskModifier(int mm)
    {
        accelMaskModifier = mm;
        stringAccelMaskModifier = maskKeyFromInt(accelMaskModifier);
    }

    public void setMnemonicKey(int key)
    {
        mnemonicKey = key;
        stringAccelKey = accelKeyFromInt(mnemonicKey);
    }

    public void setType(int t)
    {
        type = t;
    }

    public void setStringAction(String Action)
    {
        stringAction = Action;
    }

    public String getText()
    {
        return stringText;
    }

    public int getAccelKey()
    {
        return accelKey;
    }

    public int getAccelMaskModifier()
    {
        return accelMaskModifier;
    }

    public int getMnemonicKey()
    {
        return mnemonicKey;
    }

    public boolean getEnabled()
    {
        return bIsEnabled;
    }

    public int getType()
    {
        return type;
    }

    public String getStringAccelKey()
    {
        return stringAccelKey;
    }

    public String getStringAccelMaskModifier()
    {
        return stringAccelMaskModifier;
    }

    public String getStringMnemonicKey()
    {
        return stringMnemonicKey;
    }

    public String getStringAction()
    {
        return stringAction;
    }

    private static String keyName[] = {
        "NONE", "KEY_LOCATION_LEFT", "KEY_LOCATION_NUMPAD", "KEY_LOCATION_RIGHT", "KEY_LOCATION_STANDARD", "KEY_LOCATION_UNKNOWN", "VK_0", "VK_1", "VK_2", "VK_3", 
        "VK_4", "VK_5", "VK_6", "VK_7", "VK_8", "VK_9", "VK_A", "VK_ACCEPT", "VK_ADD", "VK_AGAIN", 
        "VK_ALL_CANDIDATES", "VK_ALPHANUMERIC", "VK_ALT", "VK_ALT_GRAPH", "VK_AMPERSAND", "VK_ASTERISK", "VK_AT", "VK_B", "VK_BACK_QUOTE", "VK_BACK_SLASH", 
        "VK_BACK_SPACE", "VK_BRACELEFT", "VK_BRACERIGHT", "VK_C", "VK_CANCEL", "VK_CAPS_LOCK", "VK_CIRCUMFLEX", "VK_CLEAR", "VK_CLOSE_BRACKET", "VK_CODE_INPUT", 
        "VK_COLON", "VK_COMMA", "VK_COMPOSE", "VK_CONTROL", "VK_CONVERT", "VK_COPY", "VK_CUT", "VK_D", "VK_DEAD_ABOVEDOT", "VK_DEAD_ABOVERING", 
        "VK_DEAD_ACUTE", "VK_DEAD_BREVE", "VK_DEAD_CARON", "VK_DEAD_CEDILLA", "VK_DEAD_CIRCUMFLEX", "VK_DEAD_DIAERESIS", "VK_DEAD_DOUBLEACUTE", "VK_DEAD_GRAVE", "VK_DEAD_IOTA", "VK_DEAD_MACRON", 
        "VK_DEAD_OGONEK", "VK_DEAD_SEMIVOICED_SOUND", "VK_DEAD_TILDE", "VK_DEAD_VOICED_SOUND", "VK_DECIMAL", "VK_DELETE", "VK_DIVIDE", "VK_DOLLAR", "VK_DOWN", "VK_E", 
        "VK_END", "VK_ENTER", "VK_EQUALS", "VK_ESCAPE", "VK_EURO_SIGN", "VK_EXCLAMATION_MARK", "VK_F", "VK_F1", "VK_F10", "VK_F11", 
        "VK_F12", "VK_F13", "VK_F14", "VK_F15", "VK_F16", "VK_F17", "VK_F18", "VK_F19", "VK_F2", "VK_F20", 
        "VK_F21", "VK_F22", "VK_F23", "VK_F24", "VK_F3", "VK_F4", "VK_F5", "VK_F6", "VK_F7", "VK_F8", 
        "VK_F9", "VK_FINAL", "VK_FIND", "VK_FULL_WIDTH", "VK_G", "VK_GREATER", "VK_H", "VK_HALF_WIDTH", "VK_HELP", "VK_HIRAGANA", 
        "VK_HOME", "VK_I", "VK_INPUT_METHOD_ON_OFF", "VK_INSERT", "VK_INVERTED_EXCLAMATION_MARK", "VK_J", "VK_JAPANESE_HIRAGANA", "VK_JAPANESE_KATAKANA", "VK_JAPANESE_ROMAN", "VK_K", 
        "VK_KANA", "VK_KANA_LOCK", "VK_KANJI", "VK_KATAKANA", "VK_KP_DOWN", "VK_KP_LEFT", "VK_KP_RIGHT", "VK_KP_UP", "VK_L", "VK_LEFT", 
        "VK_LEFT_PARENTHESIS", "VK_LESS", "VK_M", "VK_META", "VK_MINUS", "VK_MODECHANGE", "VK_MULTIPLY", "VK_N", "VK_NONCONVERT", "VK_NUM_LOCK", 
        "VK_NUMBER_SIGN", "VK_NUMPAD0", "VK_NUMPAD1", "VK_NUMPAD2", "VK_NUMPAD3", "VK_NUMPAD4", "VK_NUMPAD5", "VK_NUMPAD6", "VK_NUMPAD7", "VK_NUMPAD8", 
        "VK_NUMPAD9", "VK_O", "VK_OPEN_BRACKET", "VK_P", "VK_PAGE_DOWN", "VK_PAGE_UP", "VK_PASTE", "VK_PAUSE", "VK_PERIOD", "VK_PLUS", 
        "VK_PREVIOUS_CANDIDATE", "VK_PRINTSCREEN", "VK_PROPS", "VK_Q", "VK_QUOTE", "VK_QUOTEDBL", "VK_R", "VK_RIGHT", "VK_RIGHT_PARENTHESIS", "VK_ROMAN_CHARACTERS", 
        "VK_S", "VK_SCROLL_LOCK", "VK_SEMICOLON", "VK_SEPARATER", "VK_SEPARATOR", "VK_SHIFT", "VK_SLASH", "VK_SPACE", "VK_STOP", "VK_SUBTRACT", 
        "VK_T", "VK_TAB", "VK_U", "VK_UNDEFINED", "VK_UNDERSCORE", "VK_UNDO", "VK_UP", "VK_V", "VK_W", "VK_X", 
        "VK_Y", "VK_Z"
    };
    private static int keyValue[] = {
        0, 2, 4, 3, 1, 0, 48, 49, 50, 51, 
        52, 53, 54, 55, 56, 57, 65, 30, 107, 65481, 
        256, 240, 18, 65406, 150, 151, 512, 66, 192, 92, 
        8, 161, 162, 67, 3, 20, 514, 12, 93, 258, 
        513, 44, 65312, 17, 28, 65485, 65489, 68, 134, 136, 
        129, 133, 138, 139, 130, 135, 137, 128, 141, 132, 
        140, 143, 131, 142, 110, 127, 111, 515, 40, 69, 
        35, 10, 61, 27, 516, 517, 70, 112, 121, 122, 
        123, 61440, 61441, 61442, 61443, 61444, 61445, 61446, 113, 61447, 
        61448, 61449, 61450, 61451, 114, 115, 116, 117, 118, 119, 
        120, 24, 65488, 243, 71, 160, 72, 244, 156, 242, 
        36, 73, 263, 155, 518, 74, 260, 259, 261, 75, 
        21, 262, 25, 241, 225, 226, 227, 224, 76, 37, 
        519, 153, 77, 157, 45, 31, 106, 78, 29, 144, 
        520, 96, 97, 98, 99, 100, 101, 102, 103, 104, 
        105, 79, 91, 80, 34, 33, 65487, 19, 46, 521, 
        257, 154, 65482, 81, 222, 152, 82, 39, 522, 245, 
        83, 145, 59, 108, 108, 16, 47, 32, 65480, 109, 
        84, 9, 85, 0, 523, 65483, 38, 86, 87, 88, 
        89, 90
    };
    private static String maskName[] = {
        "ALT_MASK", "CTRL_MASK", "META_MASK", "SHIFT_MASK", "CTRL_ALT_MASK", "CTRL_SHIFT_MASK", "ALT_SHIFT_MASK", "CTRL_ALT_SHIFT_MASK", "NO_MASK", "NONE"
    };
    private static int maskValue[] = {
        8, 2, 4, 1, 10, 3, 9, 11, 0, 0
    };
    private String stringText;
    private String stringAccelKey;
    private String stringAccelMaskModifier;
    private String stringMnemonicKey;
    private String stringAction;
    private boolean bIsEnabled;
    private int type;
    private int accelKey;
    private int accelMaskModifier;
    private int mnemonicKey;

}
