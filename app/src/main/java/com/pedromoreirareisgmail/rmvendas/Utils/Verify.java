package com.pedromoreirareisgmail.rmvendas.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class Verify {

    private static boolean isDatachanged = false;

    public static boolean dataChanged(EditText editText) {


        // Caso tenha alguma alteração no texto, informa que os dados foram alterados
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Dados alterados
                isDatachanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return isDatachanged;
    }
}
