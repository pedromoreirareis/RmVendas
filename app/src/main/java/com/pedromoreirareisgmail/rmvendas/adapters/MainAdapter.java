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

        MainViewHolder holder = new MainViewHolder(view);

        /* Quantidade de produtos vendidos, sem tem adicional, se tem desconto e sem tem prazo */
        int quantity = cursor.getInt(cursor.getColumnIndex(EntrySeel.COLUMN_QUANTITY));
        double productValue = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_PRICE));

        long clientId = cursor.getLong(cursor.getColumnIndex(EntrySeel.COLUMN_CLIENT_ID));

        String clientName = "";
        if (clientId > 0) {

            clientName = SearchDB.searchClientName(context, clientId);
        }

        /* Nome do produto e hora de uma venda */
        String productName = cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_NAME));
        String hourMinute = cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_TIMESTAMP));

        double subTotalSaleValue = productValue * quantity;
        double addValue = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_ADD_VALUE));
        double discountValue = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_DISCOUNT_VALUE));
        double forwardValue = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_FORWARD_VALUE));

        double cardValue = -1;

        if (cursor.getString(cursor.getColumnIndex(EntrySeel.COLUMN_CARD_VALUE)).isEmpty()) {

            cardValue = Const.NUMBER_ZERO;

        } else {

            cardValue = cursor.getDouble(cursor.getColumnIndex(EntrySeel.COLUMN_CARD_VALUE));
        }

        double totalSaleValue = Calculus.calculateSaleValueDouble(
                quantity,
                productValue,
                addValue,
                discountValue
        );
        double inCashValue = Calculus.calculateInCashValueDouble(
                quantity,
                productValue,
                addValue,
                discountValue,
                forwardValue,
                cardValue);


        String inCashValueStr = String.format(context.getString(R.string.text_item_value_in_cash), Formatting.doubleToCurrency(inCashValue));
        String subTotalSaleValueStr = String.format(context.getString(R.string.text_item_price), Formatting.doubleToCurrency(subTotalSaleValue));

        String forwardValueStr = String.format(context.getString(R.string.text_item_value_forward), Formatting.doubleToCurrency(forwardValue));
        String addValueStr = String.format(context.getString(R.string.text_item_value_add), Formatting.doubleToCurrency(addValue));

        String cardValueStr = String.format(context.getString(R.string.text_item_value_card), Formatting.doubleToCurrency(cardValue));
        String discountValueStr = String.format(context.getString(R.string.text_item_value_discount), Formatting.doubleToCurrency(discountValue));

        String totalSaleValueStr = String.format(context.getString(R.string.text_item_value_total), Formatting.doubleToCurrency(totalSaleValue));


        holder.tvQuantity.setText(String.valueOf(quantity));
        holder.tvProductName.setText(productName);
        holder.tvHourMinute.setText(TimeDate.formatDateToHourAndMinute(hourMinute));

        holder.tvInCashValue.setText(inCashValueStr);
        holder.tvSaleSubTotalValue.setText(subTotalSaleValueStr);

        if (forwardValue != Const.NUMBER_ZERO) {

            holder.tvForwardValue.setText(forwardValueStr);
            holder.tvClientNameForwardValue.setText(String.format(context.getString(R.string.text_item_client_name_forward), clientName));
            holder.tvClientNameForwardValue.setVisibility(View.VISIBLE);

        } else {

            holder.tvForwardValue.setText(forwardValueStr);
            holder.tvClientNameForwardValue.setText(String.format(context.getString(R.string.text_item_client_name_forward), clientName));
            holder.tvClientNameForwardValue.setVisibility(View.GONE);
        }
        holder.tvAddValue.setText(addValueStr);


        holder.tvCardValue.setText(cardValueStr);
        holder.tvDiscountValue.setText(discountValueStr);

        holder.tvSaleTotalValue.setText(totalSaleValueStr);

    }

    class MainViewHolder {

        private final TextView tvQuantity;
        private final TextView tvProductName;
        private final TextView tvHourMinute;
        private final TextView tvInCashValue;
        private final TextView tvSaleSubTotalValue;
        private final TextView tvForwardValue;
        private final TextView tvAddValue;
        private final TextView tvCardValue;
        private final TextView tvDiscountValue;
        private final TextView tvSaleTotalValue;


        private final TextView tvClientNameForwardValue;

        public MainViewHolder(View view) {

            tvQuantity = view.findViewById(R.id.tv_main_quantity_value);
            tvProductName = view.findViewById(R.id.tv_main_product_name);
            tvHourMinute = view.findViewById(R.id.tv_main_hour_minute);
            tvInCashValue = view.findViewById(R.id.tv_main_in_cash_value);
            tvSaleSubTotalValue = view.findViewById(R.id.tv_main_sale_subtotal_value);
            tvForwardValue = view.findViewById(R.id.tv_main_forward_value);
            tvAddValue = view.findViewById(R.id.tv_main_add_value);
            tvCardValue = view.findViewById(R.id.tv_main_card_value);
            tvDiscountValue = view.findViewById(R.id.tv_main_discount_value);
            tvSaleTotalValue = view.findViewById(R.id.tv_main_sale_total_value);
            tvClientNameForwardValue = view.findViewById(R.id.tv_main_value_client_name_forward);
        }
    }
}
