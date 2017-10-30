package com.pedromoreirareisgmail.rmvendas.Utils;


import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Switch;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utilidades {

    /**
     * Utilizado para fechar o teclado quando o usuario fazer uma seleção negativa com o Switch
     * Tem um problema que ao fechar não abre mais o teclado
     *
     * @param context  indicar o contexto
     * @param switchCD indicar qual botão Switch foi alterado
     */
    public static void fecharTecladoSwitch(Context context, Switch switchCD) {

        InputMethodManager imm = (InputMethodManager) context.
                getSystemService(INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(switchCD.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void mostrarTeclado(Context context, EditText meuEdit) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(meuEdit, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Zera o valor do edit escolhido, retira o cursor, e retira a selação e foco
     *
     * @param editText recebe um edit
     */
    public static void semCursorFocoSelecaoZerado(EditText editText) {

        editText.setText("0");
        editText.setCursorVisible(false);
        editText.setSelectAllOnFocus(false);
    }


}
