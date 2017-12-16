package com.pedromoreirareisgmail.rmvendas.Utils;


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utilidades {

    /**
     * Utilizado para fechar o teclado
     *
     * @param context Indicar o contexto da Activity
     * @param view    Indica qual View esta solicitando o fechamento do teclado
     */
    public static void fecharTecladoView(Context context, View view) {

        InputMethodManager imm = (InputMethodManager) context.
                getSystemService(INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Quando o teclado esta fechado, solicita a abertura
     *
     * @param context Indicar o contexto da Activity
     * @param meuEdit Indica qual EditText esta solicitando a abertura do teclado
     */
    public static void mostrarTeclado(Context context, EditText meuEdit) {

        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(meuEdit, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Zera o valor do edit escolhido, retira o foco por completo
     *
     * @param editText recebe o EditText para zerar e tirar foco
     */
    public static void semFocoZerado(EditText editText) {

        editText.setText("0");
        editText.setSelectAllOnFocus(false);
    }


}
