package com.pedromoreirareisgmail.rmvendas.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Constantes;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatar;
import com.pedromoreirareisgmail.rmvendas.db.DbHelper;

import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoAReceber;
import static com.pedromoreirareisgmail.rmvendas.db.Contrato.AcessoClientes;

public class ClientesAdapter extends CursorAdapter {

    /**
     * Construtor
     *
     * @param context Contexto da Activity
     */
    public ClientesAdapter(Context context) {
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

        return LayoutInflater.from(context).inflate(R.layout.item_clientes_list, viewGroup, false);
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

        ClientesViewHolder holder = new ClientesViewHolder(view);

        // Valor do registro
        double valorAReceber = calcularAReceber(context, cursor.getInt(cursor.getColumnIndex(AcessoClientes._ID)));

        // Nome do cliente e Telefone
        String nomeBD = cursor.getString(cursor.getColumnIndex(AcessoClientes.NOME));
        String foneBD = cursor.getString(cursor.getColumnIndex(AcessoClientes.TELEFONE));

        holder.tvNome.setText(nomeBD);
        holder.tvFone.setText(foneBD);

        /*  Cliente em debito - fonte do registro fica vermelha
         *  Cliente sem debito e sem credito - fonte do registro fica preta
         *  Cliente com credito - fonte do regitro fica azul
         */
        if (valorAReceber < 0) {

            holder.tvNome.setTextColor(context.getResources().getColor(R.color.colorRed));
            holder.tvFone.setTextColor(context.getResources().getColor(R.color.colorRed));
            holder.tvValorReceber.setTextColor(context.getResources().getColor(R.color.colorRed));
            holder.tvValorReceber.setText(Formatar.formatarDoubleParaCurrency(valorAReceber));


        } else if (valorAReceber == 0) {

            holder.tvNome.setTextColor(context.getResources().getColor(R.color.colorBlack));
            holder.tvFone.setTextColor(context.getResources().getColor(R.color.colorBlack));
            holder.tvValorReceber.setTextColor(context.getResources().getColor(R.color.colorBlack));
            holder.tvValorReceber.setText(Formatar.formatarDoubleParaCurrency(valorAReceber));

        } else if (valorAReceber > 0) {

            holder.tvNome.setTextColor(context.getResources().getColor(R.color.colorBlue));
            holder.tvFone.setTextColor(context.getResources().getColor(R.color.colorBlue));
            holder.tvValorReceber.setTextColor(context.getResources().getColor(R.color.colorBlue));
            holder.tvValorReceber.setText(Formatar.formatarDoubleParaCurrency(valorAReceber));
        }

    }

    /**
     * Faz uma pesquisa em todos os registro de um cliente especifico e verifica o valor de saldo
     * que o cliente tem. Cliente por esta em debito, ter credito, ou estar zerado
     *
     * @param context   Contexto da Activity
     * @param idCliente id do cliente (cada cliente tem um id unico que é usado em todos seus registros )
     * @return O valor do saldo do cliente
     */
    private double calcularAReceber(Context context, int idCliente) {

        double valorVendas = 0;
        double valorRecebimentos = 0;
        double valorTotal = 0;

        DbHelper mDbHelper = new DbHelper(context);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                AcessoAReceber._ID,
                AcessoAReceber.CLIENTE_ID,
                AcessoAReceber.CLIENTE_NOME,
                AcessoAReceber.TIPO_ENTRADA,
                AcessoAReceber.DATA_HORA,
                AcessoAReceber.VALOR,
                AcessoAReceber.DESCRICAO,
                AcessoAReceber.VALOR
        };

        String selection = AcessoAReceber.CLIENTE_ID + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(idCliente)};
        String sortOrder = AcessoAReceber.DATA_HORA;

        Cursor cursor = db.query(AcessoAReceber.TABELA_A_RECEBER, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {

            if (cursor.getInt(cursor.getColumnIndex(AcessoAReceber.TIPO_ENTRADA)) == Constantes.TIPO_A_RECEBER_VENDA) {

                valorVendas = valorVendas + cursor.getDouble(cursor.getColumnIndex(AcessoAReceber.VALOR));

            } else {

                valorRecebimentos = valorRecebimentos + cursor.getDouble(cursor.getColumnIndex(AcessoAReceber.VALOR));
            }

            valorTotal = valorRecebimentos - valorVendas;

            cursor.moveToNext();
        }

        db.close();

        return valorTotal;
    }

    /*
     *   Cria o ViewHolder para gerenciar a criação de itens(views)
     */
    class ClientesViewHolder {

        final TextView tvNome;
        final TextView tvFone;
        final TextView tvValorReceber;

        public ClientesViewHolder(View view) {

            tvNome = view.findViewById(R.id.tv_clientes_nome_list);
            tvFone = view.findViewById(R.id.tv_clientes_fone_list);
            tvValorReceber = view.findViewById(R.id.tv_clientes_valor_receber_list);
        }

    }
}
