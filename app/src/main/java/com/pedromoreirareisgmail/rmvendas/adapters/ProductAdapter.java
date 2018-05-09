package com.pedromoreirareisgmail.rmvendas.adapters;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.pedromoreirareisgmail.rmvendas.R;
import com.pedromoreirareisgmail.rmvendas.Utils.Formatting;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryProduct;

public class ProductAdapter extends CursorAdapter {

    public ProductAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_list_product, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ProductViewHolder holder = new ProductViewHolder(view);

        String name = cursor.getString(cursor.getColumnIndex(EntryProduct.COLUMN_NAME));
        Double value = cursor.getDouble(cursor.getColumnIndex(EntryProduct.COLUMN_PRICE));

        holder.tvName.setText(name);
        holder.tvPrice.setText(Formatting.doubleToCurrency(value));
    }

    class ProductViewHolder {

        private TextView tvName;
        private TextView tvPrice;

        public ProductViewHolder(View view) {

            tvName = view.findViewById(R.id.tv_product_list_name);
            tvPrice = view.findViewById(R.id.tv_product_list_price);
        }
    }
}
