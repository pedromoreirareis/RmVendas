package com.pedromoreirareisgmail.rmvendas.adapter;

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
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoEntRet;

public class EntAdapter extends CursorAdapter {

    /**
     * Construtor
     *
     * @param context Contexto da Activity
     */
    public EntAdapter(Context context) {
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

        EntViewHolder holder = new EntViewHolder(view);

        /* Valor, hora e descrição de uma entrada */
        double valor = cursor.getDouble(cursor.getColumnIndex(AcessoEntRet.VALOR));
        String hora = cursor.getString(cursor.getColumnIndex(AcessoEntRet.DATA_HORA));
        String descricao = cursor.getString(cursor.getColumnIndex(AcessoEntRet.DESCRICAO));

        holder.tvValor.setText(Formatar.formatarDoubleParaCurrency(valor));
        holder.tvHoraMinuto.setText(DataHora.formatarHoraMinutoBr(hora));
        holder.tvDescricao.setText(descricao);
    }

    /*
     *   Cria o ViewHolder para gerenciar a criação de itens(views)
     */
    class EntViewHolder {

        final TextView tvValor;
        final TextView tvHoraMinuto;
        final TextView tvDescricao;

        public EntViewHolder(View view) {

            tvValor = view.findViewById(R.id.tv_ent_ret_valor_list);
            tvHoraMinuto = view.findViewById(R.id.tv_ent_ret_hora_list);
            tvDescricao = view.findViewById(R.id.tv_ent_ret_desconto_list);
        }
    }
}
