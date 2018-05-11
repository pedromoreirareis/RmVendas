package com.pedromoreirareisgmail.rmvendas.Utils;


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ControlViews {

    /**
     * Esconde teclado se solicitado
     *
     * @param context Contexto da Activity
     * @param view    Qual View solicita esconder teclado
     */
    public static void hideKeyboard(Context context, View view) {

        if (context != null && view != null) {

            InputMethodManager imm = (InputMethodManager) context.
                    getSystemService(INPUT_METHOD_SERVICE);

            if (imm != null) {

                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * Quando o teclado esta fechado, solicita a abertura
     *
     * @param context Contexto da Activity
     * @param meuEdit Qual EditText solicita abertura do teclado
     */
    public static void showKeyboard(Context context, EditText meuEdit) {

        if (context != null && meuEdit != null) {

            InputMethodManager imm =
                    (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {

                imm.showSoftInput(meuEdit, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    /**
     * Não dar foco e coloca texto igual a "0"
     *
     * @param editText EditText que não tera foco e valor "0"
     */
    public static void noFocusAndZero(EditText editText) {

        editText.setSelectAllOnFocus(false);
        editText.setText("0");
    }

    public static Boolean noChangedValueDescription(EditText editValue, EditText editDescription) {

        String description = editDescription.getText().toString();
        Double valueDouble = Formatting.editsToDouble(editValue);

        return description.isEmpty() && valueDouble == 0;
    }
}
