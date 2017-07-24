package com.pedromoreirareisgmail.rmvendas.Utils;

import android.widget.EditText;

public class UserInterface {

    public static void focoCursorSelect(EditText editText) {

        editText.setText("0");
        editText.setCursorVisible(false);
        editText.setSelectAllOnFocus(false);
    }
}
