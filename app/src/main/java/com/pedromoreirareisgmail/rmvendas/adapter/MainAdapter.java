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
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoVenda;

import static com.pedromoreirareisgmail.rmvendas.Utils.Constantes.VEZES_X;


public class MainAdapter extends CursorAdapter {

    /**
     * Construtor
     *
     * @param context Contexto da Activity
     */
    public MainAdapter(Context context) {
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
        return LayoutInflater.from(context).inflate(R.layout.item_main_list, viewGroup, false);
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

        MainViewHolder holder = new MainViewHolder(view);

        /* Nome do produto e hora de uma venda */
        String nomeProduto = cursor.getString(cursor.getColumnIndex(AcessoVenda.NOME_PRODUTO));
        String hora = cursor.getString(cursor.getColumnIndex(AcessoVenda.DATA));

        /* Valor de uma unidade do produto, valor da venda, valor da adicional e valor do desconto */
        double valorAdicional = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_ADICIONAL));
        double valorDesconto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_DESCONTO));
        double valorTotalVenda = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_TOTAL_VENDA));
        double valorUnidadeProduto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_UMA_UNIDADE_PRODUTO));

        /* Quantidade de produtos vendidos, sem tem adicional, se tem desconto e sem tem prazo */
        int quantidadeProduto = cursor.getInt(cursor.getColumnIndex(AcessoVenda.QUANTIDADE_VENDIDA));
        int temAdicional = cursor.getInt(cursor.getColumnIndex(AcessoVenda.TEM_ADICIONAL));
        int temDesconto = cursor.getInt(cursor.getColumnIndex(AcessoVenda.TEM_DESCONTO));
        int temPrazo = cursor.getInt(cursor.getColumnIndex(AcessoVenda.A_PRAZO));

        String quantidadeXValor = String.valueOf(quantidadeProduto) + VEZES_X + Formatar.formatarDoubleParaCurrency(valorUnidadeProduto);
        holder.tvQuantidade.setText(quantidadeXValor);
        holder.tvNomeProduto.setText(nomeProduto);
        holder.tvHoraMinuto.setText(DataHora.formatarHoraMinutoBr(hora));

        if (temAdicional == Constantes.ADICIONAL_SIM) {

            holder.tvValorAdicional.setText(Formatar.formatarDoubleParaCurrency(valorAdicional));

        } else {

            holder.tvValorAdicional.setText(Formatar.formatarDoubleParaCurrency(0));

        }

        if (temDesconto == Constantes.DESCONTO_SIM) {

            holder.tvValorDesconto.setText(Formatar.formatarDoubleParaCurrency(valorDesconto));

        } else {

            holder.tvValorDesconto.setText(Formatar.formatarDoubleParaCurrency(0));
        }

        if (temPrazo == Constantes.PRAZO_SIM) {

            holder.tvValorTotalLabel.setText(R.string.text_item_main_prazo);
            holder.tvValorTotalVenda.setText(Formatar.formatarDoubleParaCurrency(valorTotalVenda));

        } else {

            holder.tvValorTotalLabel.setText(R.string.text_item_main_total_venda_vista);
            holder.tvValorTotalVenda.setText(Formatar.formatarDoubleParaCurrency(valorTotalVenda));
        }
    }

    /*
     *   Cria o ViewHolder para gerenciar a criação de itens(views)
     */
    class MainViewHolder {

        final TextView tvNomeProduto;
        final TextView tvQuantidade;
        final TextView tvValorAdicional;
        final TextView tvValorDesconto;
        final TextView tvValorTotalVenda;
        final TextView tvValorTotalLabel;
        final TextView tvHoraMinuto;

        public MainViewHolder(View view) {

            tvNomeProduto = view.findViewById(R.id.tv_main_nome);
            tvQuantidade = view.findViewById(R.id.tv_main_valor_quantidade);
            tvValorAdicional = view.findViewById(R.id.tv_main_valor_adicional);
            tvValorDesconto = view.findViewById(R.id.tv_main_valor_desconto);
            tvValorTotalVenda = view.findViewById(R.id.tv_main_valor_venda);
            tvValorTotalLabel = view.findViewById(R.id.tv_main_valor_label_venda);
            tvHoraMinuto = view.findViewById(R.id.tv_main_data);
        }
    }
}
