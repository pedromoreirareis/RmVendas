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
import com.pedromoreirareisgmail.rmvendas.Utils.TimeDate;
import com.pedromoreirareisgmail.rmvendas.db.Contract.EntryCashMove;

public class RemoveMoneyAdapter extends CursorAdapter {

    public RemoveMoneyAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.item_list_cash_move, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        RemoveViewHolder holder = new RemoveViewHolder(view);

        Double value = cursor.getDouble(cursor.getColumnIndex(EntryCashMove.COLUMN_VALUE));
        String hour = cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_TIMESTAMP));
        String description = cursor.getString(cursor.getColumnIndex(EntryCashMove.COLUMN_DESCRIPTION));

        holder.tvValue.setText(Formatting.doubleToCurrency(value));
        holder.tvHour.setText(TimeDate.formatDateToHourAndMinute(hour));
        holder.tvDescription.setText(description);
    }

    class RemoveViewHolder {

        private final TextView tvValue;
        private final TextView tvHour;
        private final TextView tvDescription;

        private RemoveViewHolder(View view) {

            tvValue = view.findViewById(R.id.tv_add_remove_list_value);
            tvHour = view.findViewById(R.id.tv_add_remove_list_hour);
            tvDescription = view.findViewById(R.id.tv_add_remove_list_description);
        }
    }
}
