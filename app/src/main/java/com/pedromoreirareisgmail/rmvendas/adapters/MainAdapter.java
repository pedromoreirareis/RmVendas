package com.pedromoreirareisgmail.rmvendas.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Calculus;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.Utils.TimeDate;
import com.pedromoreirareisgmail.rmvendas.constant.Const;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntrySeel;
import com.pedromoreirareisgmail.rmvendas.db.SearchDB;


public class MainAdapter extends CursorAdapter {

    public MainAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_main, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //TODO: receber dados com vendas com cartão de credito e mostar ao usuario
        //TODO: fazer calculos para mostar corretamente os descontos, vendas a vista, vendas a prazo e vendas no cartão
        //TODO: criar novas strings e tavez campos para mostar que a venda foi no cartão - novo lugar do valor de venda a vista mostar a palavra "CARTAO"



        MainViewHolder holder = new MainViewHolder(view);

        /* Quantidade de produtos vendidos, sem tem adicional, se tem desconto e sem tem prazo */
        int quantity = cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY));
        double value = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE));

        long clientId = cursor.getLong(cursor.getColumnIndex(EntrySeel.COLUMN_CLIENT_ID));

        String clientName = "";
        if (clientId > 0) {

            clientName = SearchDB.searchClientName(context, clientId);
        }

        /* Nome do produto e hora de uma venda */
        String productName = cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_NAME));
        String hourMinute = cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_TIMESTAMP));

        double priceSale = value * quantity;
        double addValue = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE));
        double discountValue = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE));
        double forwardValue = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE));

        double totalValue = Calculus.calcularValorTotalVendaDouble(
                quantity,
                value,
                addValue,
                discountValue
        );
        double inCashValue = Calculus.CalcularValorAVistaDouble(
                quantity,
                value,
                addValue,
                discountValue,
                forwardValue);

        String saleValueStr = String.format(context.getString(R.string.text_item_price), Formatting.doubleToCurrency(priceSale));
        String addValueStr = String.format(context.getString(R.string.text_item_value_add), Formatting.doubleToCurrency(addValue));
        String discountValueStr = String.format(context.getString(R.string.text_item_value_discount), Formatting.doubleToCurrency(discountValue));

        String totalValueStr = String.format(context.getString(R.string.text_item_value_total), Formatting.doubleToCurrency(totalValue));
        String forwardValueStr = String.format(context.getString(R.string.text_item_value_forward), Formatting.doubleToCurrency(forwardValue));
        String inCashValueStr = String.format(context.getString(R.string.text_item_value_in_cash), Formatting.doubleToCurrency(inCashValue));

        holder.tvQuantity.setText(String.valueOf(quantity));
        holder.tvProductName.setText(productName);
        holder.tvHourMinute.setText(TimeDate.formatDateToHourAndMinute(hourMinute));

        holder.tvValueSale.setText(saleValueStr);

        if (addValue != Const.NUMBER_ZERO) {

            holder.tvValueAdd.setText(addValueStr);

        } else {

            holder.tvValueAdd.setText(addValueStr);
        }

        if (discountValue != Const.NUMBER_ZERO) {

            holder.tvValueDiscount.setText(discountValueStr);

        } else {

            holder.tvValueDiscount.setText(discountValueStr);
        }

        holder.tvValueTotal.setText(totalValueStr);
        holder.tvValueInCash.setText(inCashValueStr);

        if (forwardValue != Const.NUMBER_ZERO) {

            holder.tvValueForward.setText(forwardValueStr);
            holder.tvValueClientNameForward.setText(String.format(context.getString(R.string.text_item_client_name_forward), clientName));
            holder.tvValueClientNameForward.setVisibility(View.VISIBLE);

        } else {

            holder.tvValueForward.setText(forwardValueStr);
            holder.tvValueClientNameForward.setText(String.format(context.getString(R.string.text_item_client_name_forward), clientName));
            holder.tvValueClientNameForward.setVisibility(View.GONE);
        }
    }

    class MainViewHolder {

        private final TextView tvQuantity;
        private final TextView tvProductName;
        private final TextView tvHourMinute;
        private final TextView tvValueSale;
        private final TextView tvValueAdd;
        private final TextView tvValueDiscount;
        private final TextView tvValueTotal;
        private final TextView tvValueInCash;
        private final TextView tvValueForward;
        private final TextView tvValueClientNameForward;

        public MainViewHolder(View view) {

            tvQuantity = view.findViewById(R.id.tv_main_value_quantity);
            tvProductName = view.findViewById(R.id.tv_main_product_name);
            tvHourMinute = view.findViewById(R.id.tv_main_hour_minute);
            tvValueSale = view.findViewById(R.id.tv_main_value_sale);
            tvValueAdd = view.findViewById(R.id.tv_main_value_add);
            tvValueDiscount = view.findViewById(R.id.tv_main_value_discount);
            tvValueTotal = view.findViewById(R.id.tv_main_value_total);
            tvValueInCash = view.findViewById(R.id.tv_main_value_in_cash);
            tvValueForward = view.findViewById(R.id.tv_main_value_forward);
            tvValueClientNameForward = view.findViewById(R.id.tv_main_value_client_name_forward);
        }
    }
}
