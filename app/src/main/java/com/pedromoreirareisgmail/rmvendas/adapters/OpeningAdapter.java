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
import com.pedromoreirareisgmail.rmvendas.Utils.TimeData;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryOpening;

public class OpeningAdapter extends CursorAdapter {

    public OpeningAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_list_opening, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        OpeningViewHolder holder = new OpeningViewHolder(view);

        Double value = cursor.getDouble(cursor.getColumnIndex(EntryOpening.COLUMN_VALUE));
        String hour = cursor.getString(cursor.getColumnIndex(EntryOpening.COLUMN_TIMESTAMP));

        holder.tvValue.setText(Formatting.doubleToCurrency(value));
        holder.tvHour.setText(TimeData.formatDateToHourAndMinute(hour));
    }

    class OpeningViewHolder {

        private TextView tvValue;
        private TextView tvHour;

        public OpeningViewHolder(View view) {

            tvValue = view.findViewById(R.id.tv_opening_list_value);
            tvHour = view.findViewById(R.id.tv_opening_list_hour);
        }
    }
}
