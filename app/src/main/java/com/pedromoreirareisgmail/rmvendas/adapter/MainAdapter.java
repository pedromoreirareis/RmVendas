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
import com.pedromoreirareisgmail.rmvendas.data.Contrato.AcessoVenda;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.VEZES_X;


public class MainAdapter extends CursorAdapter {

    public MainAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_main, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        MainViewHolder holder = new MainViewHolder(view);

        String nomeProduto = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_NOME_PROD));
        String dataHora = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_DATA));

        double valorCobertura = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA));
        double valorDesconto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO));
        double valorTotalVenda = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_PROD));
        double valorUnidadeProduto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_PRECO_UM_BOLO));

        int quantidadeProduto = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_QUANT));
        int temCobertura = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_COBERTURA));
        int temDesconto = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_DESCONTO));
        int temPrazo = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_PRAZO));

        holder.tvQuantidade.setText(String.valueOf(quantidadeProduto) + VEZES_X + Formatar.formatarDoubleParaCurrency(valorUnidadeProduto));
        holder.tvNomeProduto.setText(nomeProduto);
        holder.tvHoraMinuto.setText(DataHora.formatarHoraMinutoBr(dataHora));

        if (temCobertura == Constantes.COBERTURA_SIM) {

            holder.tvValorCobertura.setText(Formatar.formatarDoubleParaCurrency(valorCobertura));


        } else {

            holder.tvValorCobertura.setText(Formatar.formatarDoubleParaCurrency(0));

        }

        if (temDesconto == Constantes.DESCONTO_SIM) {

            holder.tvValorDesconto.setText(Formatar.formatarDoubleParaCurrency(valorDesconto));

        } else {

            holder.tvValorDesconto.setText(Formatar.formatarDoubleParaCurrency(0));
        }

        if (temPrazo == Constantes.PRAZO_SIM) {

            holder.tvValorTotalLabel.setText(R.string.prazo_item_main);
            holder.tvValorTotalVenda.setText(Formatar.formatarDoubleParaCurrency(valorTotalVenda));

        } else {
            holder.tvValorTotalVenda.setText(Formatar.formatarDoubleParaCurrency(valorTotalVenda));
        }
    }

    class MainViewHolder {

        final TextView tvNomeProduto;
        final TextView tvQuantidade;
        final TextView tvValorCobertura;
        final TextView tvValorDesconto;
        final TextView tvValorTotalVenda;
        final TextView tvValorTotalLabel;
        final TextView tvHoraMinuto;

        public MainViewHolder(View view) {

            tvNomeProduto = view.findViewById(R.id.tv_nome_main);
            tvQuantidade = view.findViewById(R.id.tv_valor_quant_main);
            tvValorCobertura = view.findViewById(R.id.tv_valor_cober_main);
            tvValorDesconto = view.findViewById(R.id.tv_valor_desc_main);
            tvValorTotalVenda = view.findViewById(R.id.tv_valor_venda_main);
            tvValorTotalLabel = view.findViewById(R.id.tv_valor_label_venda_main);
            tvHoraMinuto = view.findViewById(R.id.tv_data_main);
        }
    }
}
