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
import com.pedromoreirareisgmail.rmvendas.constantes.ConstDB;

import static com.pedromoreirareisgmail.rmvendas.db.Contract.EntryReceive;

public class AReceberAdapter extends CursorAdapter {

    /**
     * Construtor
     *
     * @param context Contexto da Activity
     */
    public AReceberAdapter(Context context) {
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

        return LayoutInflater.from(context).inflate(R.layout.item_a_receber_list, viewGroup, false);
    }


    /**
     * Coloca informações nos itens(views) do layout a partir de dados de um Cursor
     * A fim da montagem de todas as views, fica como se fosse o extrato do cliente
     *
     * @param view    Um unico item do layout
     * @param context Contexto da Activity
     * @param cursor  Cursor com dados de uma pesquisa no BD
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        AReceberViewHolder holder = new AReceberViewHolder(view);

        // Venda ou Recebimento
        int tipoEntradaBD = cursor.getInt(cursor.getColumnIndex(EntryReceive.COLUMN_TYPE));

        /* Data, Descrição e Valor do Registro*/
        String dataBD = DataHora.formatarDataBr(cursor.getString(cursor.getColumnIndex(EntryReceive.COLUMN_TIMESTAMP)));
        String descricaoBD = cursor.getString(cursor.getColumnIndex(EntryReceive.COLUMN_DESCRIPTION));
        String valorBD = Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(EntryReceive.COLUMN_VALUE)));

        /*  Recebimento - fonte do registro fica Azul
         *   Venda - fonte do registro fica vermelha
         */
        if (tipoEntradaBD == ConstDB.TIPO_RECEBIMENTO) {

            holder.tvData.setTextColor(context.getResources().getColor(R.color.colorBlue));
            holder.tvDescricao.setTextColor(context.getResources().getColor(R.color.colorBlue));
            holder.tvValor.setTextColor(context.getResources().getColor(R.color.colorBlue));

            holder.tvData.setText(dataBD);
            holder.tvDescricao.setText(descricaoBD);
            holder.tvValor.setText(valorBD);

        } else {

            holder.tvData.setTextColor(context.getResources().getColor(R.color.colorRed));
            holder.tvDescricao.setTextColor(context.getResources().getColor(R.color.colorRed));
            holder.tvValor.setTextColor(context.getResources().getColor(R.color.colorRed));

            holder.tvData.setText(dataBD);
            holder.tvDescricao.setText(descricaoBD);
            holder.tvValor.setText(valorBD);
        }

    }

    /*
     *   Cria o ViewHolder para gerenciar a criação de itens(views)
     */
    class AReceberViewHolder {

        final TextView tvValor;
        final TextView tvData;
        final TextView tvDescricao;

        public AReceberViewHolder(View view) {

            tvValor = view.findViewById(R.id.tv_a_receber_list_valor);
            tvData = view.findViewById(R.id.tv_a_receber_list_data);
            tvDescricao = view.findViewById(R.id.tv_a_receber_list_descricao);
        }

    }


}
