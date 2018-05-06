package com.pedromoreirareisgmail.rmvendas.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;

public class RetAdapter extends CursorAdapter {

    /**
     * Construtor
     *
     * @param context Contexto da Activity
     */
    public RetAdapter(Context context) {
        super(context, null, 0);
    }

    /**
     * Infla os itens do layout, reaproveitando os itens
     *
     * @param context   Contexto da Activity que vem do construtor
     * @param cursor    Cursor com dados de uma pesquisa no BD
     * @param viewGroup Layout onde sera colocado os itens
     * @return View de um unico item do viewGroup
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_ent_ret_list, viewGroup, false);
    }

    /**
     * Coloca informações nos itens(views) do layout a partir de dados de um Cursor
     *
     * @param view    Um unico item do layout
     * @param context Contexto da Activity
     * @param cursor  Cursor com dados de uma pesquisa no BD
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        RetViewHolder holder = new RetViewHolder(view);

        /* valor, hora e descrição de uma retirada */
        double valor = cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE));
        String hora = cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_TIMESTAMP));
        String desccricao = cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_DESCRIPTION));

        holder.valor.setText(Formatar.formatarDoubleParaCurrency(valor));
        holder.tvHoraMinuto.setText(DataHora.formatarHoraMinutoBr(hora));
        holder.descricao.setText(desccricao);
    }

    /*
     *   Cria o ViewHolder para gerenciar a criação de itens(views)
     */
    class RetViewHolder {

        final TextView valor;
        final TextView tvHoraMinuto;
        final TextView descricao;

        public RetViewHolder(View view) {

            valor = view.findViewById(R.id.tv_ent_ret_valor_list);
            tvHoraMinuto = view.findViewById(R.id.tv_ent_ret_hora_list);
            descricao = view.findViewById(R.id.tv_ent_ret_desconto_list);
        }
    }
}
