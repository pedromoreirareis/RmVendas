package com.pedromoreirareisgmail.rmvendas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;

import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoClientes;

public class ClientesAdapter extends CursorAdapter {

    public ClientesAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_clientes_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ClientesViewHolder holder = new ClientesViewHolder(view);

        String nomeBD = cursor.getString(cursor.getColumnIndex(AcessoClientes.NOME));
        int foneBD = cursor.getInt(cursor.getColumnIndex(AcessoClientes.TELEFONE));

        holder.tvNome.setText(nomeBD);
        holder.tvFone.setText(String.valueOf(foneBD));
    }

    class ClientesViewHolder {

        final TextView tvNome;
        final TextView tvFone;

        public ClientesViewHolder(View view) {

            tvNome = view.findViewById(R.id.tv_clientes_nome_list);
            tvFone = view.findViewById(R.id.tv_clientes_fone_list);
        }

    }
}
