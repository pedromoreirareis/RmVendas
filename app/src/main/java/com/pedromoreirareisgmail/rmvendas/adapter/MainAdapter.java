package com.pedromoreirareisgmail.rmvendas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Calculos;
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.DataHora;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoVenda;
import com.pedromoreirareisgmail.rmvendas.db.PesquisasBD;


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

        /* Quantidade de produtos vendidos, sem tem adicional, se tem desconto e sem tem prazo */
        int quantidadeProduto = cursor.getInt(cursor.getColumnIndex(AcessoVenda.QUANTIDADE));
        double valorUnidadeProduto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_UNIDADE));

        int idCliente = cursor.getInt(cursor.getColumnIndex(AcessoVenda.ID_CLIENTE));

        String nomeCliente = "";
        if (idCliente > 0) {

            nomeCliente = PesquisasBD.Pesquisarcliente(context, idCliente);
        }

        /* Nome do produto e hora de uma venda */
        String nomeProduto = cursor.getString(cursor.getColumnIndex(AcessoVenda.NOME_PRODUTO));
        String horaMinuto = cursor.getString(cursor.getColumnIndex(AcessoVenda.DATA_HORA));

        double valorPrecoVenda = valorUnidadeProduto * quantidadeProduto;
        double valorAdicional = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_ADICIONAL));
        double valorDesconto = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_DESCONTO));
        double valorAPrazo = cursor.getDouble(cursor.getColumnIndex(AcessoVenda.VALOR_PRAZO));
        double valorTotal = Calculos.calcularValorTotalVendaDouble(
                quantidadeProduto,
                valorUnidadeProduto,
                valorAdicional,
                valorDesconto
        );
        double valorAVista = Calculos.CalcularValorAVistaDouble(
                quantidadeProduto,
                valorUnidadeProduto,
                valorAdicional,
                valorDesconto,
                valorAPrazo);

        String valorVendaString = String.format(context.getResources().getString(R.string.text_item_main_valor_preco), Formatar.formatarDoubleParaCurrency(valorPrecoVenda));
        String valorAdicionalString = String.format(context.getResources().getString(R.string.text_item_main_valor_adicional), Formatar.formatarDoubleParaCurrency(valorAdicional));
        String valorDescontoString = String.format(context.getResources().getString(R.string.text_item_main_valor_desconto), Formatar.formatarDoubleParaCurrency(valorDesconto));

        String valorTotalString = String.format(context.getResources().getString(R.string.text_item_main_valor_total), Formatar.formatarDoubleParaCurrency(valorTotal));
        String valorAPrazoString = String.format(context.getResources().getString(R.string.text_item_main_valor_prazo), Formatar.formatarDoubleParaCurrency(valorAPrazo));
        String valorAVistaString = String.format(context.getResources().getString(R.string.text_item_main_valor_a_vista), Formatar.formatarDoubleParaCurrency(valorAVista));

        holder.tvQuantidade.setText(String.valueOf(quantidadeProduto));
        holder.tvNomeProduto.setText(nomeProduto);
        holder.tvHoraMinuto.setText(DataHora.formatarHoraMinutoBr(horaMinuto));

        holder.tvValorVenda.setText(valorVendaString);

        if (valorAdicional != Constantes.NUMERO_ZERO) {

            holder.tvValorAdicional.setText(valorAdicionalString);

        } else {

            holder.tvValorAdicional.setText(valorAdicionalString);
        }

        if (valorDesconto != Constantes.NUMERO_ZERO) {

            holder.tvValorDesconto.setText(valorDescontoString);

        } else {

            holder.tvValorDesconto.setText(valorDescontoString);
        }

        holder.tvValorTotal.setText(valorTotalString);
        holder.tvValorAVIsta.setText(valorAVistaString);

        if (valorAPrazo != Constantes.NUMERO_ZERO) {

            holder.tvValorAPrazo.setText(valorAPrazoString);
            holder.tvValorAPrazoNomeCliente.setText(String.format(context.getResources().getString(R.string.text_item_main_valor_prazo_cliente), nomeCliente));
            holder.tvValorAPrazo.setVisibility(View.VISIBLE);
            holder.tvValorAPrazoNomeCliente.setVisibility(View.VISIBLE);

        } else {

            holder.tvValorAPrazo.setText(valorAPrazoString);
            holder.tvValorAPrazoNomeCliente.setText(String.format(context.getResources().getString(R.string.text_item_main_valor_prazo_cliente), nomeCliente));
            holder.tvValorAPrazo.setVisibility(View.GONE);
            holder.tvValorAPrazoNomeCliente.setVisibility(View.GONE);
        }
    }

    /*
     *   Cria o ViewHolder para gerenciar a criação de itens(views)
     */
    class MainViewHolder {

        final TextView tvQuantidade;
        final TextView tvNomeProduto;
        final TextView tvHoraMinuto;
        final TextView tvValorVenda;
        final TextView tvValorAdicional;
        final TextView tvValorDesconto;
        final TextView tvValorTotal;
        final TextView tvValorAVIsta;
        final TextView tvValorAPrazo;
        final TextView tvValorAPrazoNomeCliente;

        public MainViewHolder(View view) {

            tvQuantidade = view.findViewById(R.id.tv_main_valor_quantidade);
            tvNomeProduto = view.findViewById(R.id.tv_main_nome_produto);
            tvHoraMinuto = view.findViewById(R.id.tv_main_data_hora);
            tvValorVenda = view.findViewById(R.id.tv_main_valor_venda);
            tvValorAdicional = view.findViewById(R.id.tv_main_valor_adicional);
            tvValorDesconto = view.findViewById(R.id.tv_main_valor_desconto);
            tvValorTotal = view.findViewById(R.id.tv_main_valor_total);
            tvValorAVIsta = view.findViewById(R.id.tv_main_valor_a_vista);
            tvValorAPrazo = view.findViewById(R.id.tv_main_valor_a_prazo);
            tvValorAPrazoNomeCliente = view.findViewById(R.id.tv_main_valor_a_prazo_nome_cliente);
        }
    }
}
