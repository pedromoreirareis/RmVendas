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
import com.pedromoreirareisgmail.rmvendas.Utils.Datas;
import com.pedromoreirareisgmail.rmvendas.data.VendasContrato.AcessoVenda;

import java.text.NumberFormat;

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

        String nome = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_NOME_PROD));
        String data = cursor.getString(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_DATA));
        int quantInt = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_QUANT));
        double vlCoberDouble = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_COBERTURA));
        double vlDescDouble = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_DESCONTO));
        double vlTotalDouble = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_VALOR_PROD));
        double vlUmBolo = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_PRECO_UM_BOLO));
        int temCobert = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_COBERTURA));
        int temDesc = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_TEM_DESCONTO));
        int temPrazo = cursor.getInt(cursor.getColumnIndex(AcessoVenda.COLUNA_VENDA_PRAZO));

        String quant = String.valueOf(quantInt);
        data = Datas.formatTime(data);

        NumberFormat preco = NumberFormat.getCurrencyInstance();

        holder.tvQuant.setText(quant + VEZES_X + preco.format(vlUmBolo));
        holder.tvNome.setText(nome);
        holder.tvData.setText(data);

        if (temCobert == Constantes.COBERTURA_SIM) {

            holder.tvVlCober.setText(preco.format(vlCoberDouble));


        } else {

            holder.tvVlCober.setText(preco.format(0));

        }

        if (temDesc == Constantes.DESCONTO_SIM) {

            holder.tvVlDesc.setText(preco.format(vlDescDouble));

        } else {

            holder.tvVlDesc.setText(preco.format(0));
        }

        if (temPrazo == Constantes.PRAZO_SIM) {

            holder.tvVlTotalLabel.setText(R.string.prazo_item_main);
            holder.tvVlTotal.setText(preco.format(vlTotalDouble));

        } else {
            //holder.tvVlTotalLabel.setText(R.string.venda_item_main);
            holder.tvVlTotal.setText(preco.format(vlTotalDouble));
        }
    }

    class MainViewHolder {

        final TextView tvNome;
        final TextView tvQuant;
        final TextView tvVlCober;
        final TextView tvVlDesc;
        final TextView tvVlTotal;
        final TextView tvData;
        final TextView tvVlTotalLabel;

        public MainViewHolder(View view) {

            tvNome = view.findViewById(R.id.tv_nome_main);
            tvQuant = view.findViewById(R.id.tv_valor_quant_main);
            tvVlCober = view.findViewById(R.id.tv_valor_cober_main);
            tvVlDesc = view.findViewById(R.id.tv_valor_desc_main);
            tvVlTotal = view.findViewById(R.id.tv_valor_venda_main);
            tvVlTotalLabel = view.findViewById(R.id.tv_valor_label_venda_main);
            tvData = view.findViewById(R.id.tv_data_main);
        }
    }
}
