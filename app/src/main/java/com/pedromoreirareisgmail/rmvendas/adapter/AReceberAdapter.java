package com.pedromoreirareisgmail.rmvendas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;

import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoAReceber;

public class AReceberAdapter extends CursorAdapter {

    public AReceberAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_a_receber_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        AReceberViewHolder holder = new AReceberViewHolder(view);

        int tipoEntradaBD = cursor.getInt(cursor.getColumnIndex(AcessoAReceber.TIPO_ENTRADA));

        String dataBD = DataHora.formatarDataBr(cursor.getString(cursor.getColumnIndex(AcessoAReceber.DATA_HORA)));
        String descricaoBD = cursor.getString(cursor.getColumnIndex(AcessoAReceber.DESCRICAO));
        String valorBD = Formatar.formatarDoubleParaCurrency(cursor.getDouble(cursor.getColumnIndex(AcessoAReceber.VALOR)));

        if (tipoEntradaBD == Constantes.TIPO_A_RECEBER_RECEBIMENTO) {

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
