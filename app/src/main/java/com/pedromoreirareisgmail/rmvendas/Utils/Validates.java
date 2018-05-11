package com.pedromoreirareisgmail.rmvendas.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.constant.ConstTag;

public class Validates {

    private static final String TAG = ConstTag.TAG_MAIN + Validates.class.getSimpleName();

    /* AddMoney CashMove validação */
    public static final void dataCashMove(Context context, Double valueDouble, String description,
                                          EditText editValue, EditText editDescription) {
        Log.v(TAG, "dataCashMove");

        // Valor não pode ser zero
        if (valueDouble == Const.NUMERO_ZERO) {

            editValue.setError(context.getString(R.string.error_valide_value));
            editValue.requestFocus();
            return;
        }

        // A descrição não pode fica vazia
        if (description.isEmpty()) {

            editDescription.setError(context.getString(R.string.error_empty_description));
            editDescription.requestFocus();
            return;
        }

        // A descrição deve ter pelo menos 10 caracteres
        if (description.length() < Const.MIN_CARACT_10) {

            editDescription.setError(context.getString(R.string.error_lenght_description_10));
            editDescription.requestFocus();
            return;
        }
    }
}
